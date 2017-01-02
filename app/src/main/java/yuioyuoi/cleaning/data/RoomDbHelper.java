package yuioyuoi.cleaning.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import yuioyuoi.cleaning.model.Room;
import yuioyuoi.cleaning.model.RoomContract;
import yuioyuoi.cleaning.model.RoomContract.RoomEntry;

/**
 * Created by Jean on 28/11/2016.
 */

// TODO only ever use this in an async thread
public class RoomDbHelper extends SQLiteOpenHelper
{
    private static final String TAG = "RoomDbHelper";

    private static final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RoomEntry.TABLE_NAME + " (" +
                    RoomEntry._ID + " INTEGER PRIMARY KEY," +
                    RoomEntry.COLUMN_NAME_ROOM + TEXT_TYPE + COMMA_SEP +
                    RoomEntry.COLUMN_NAME_SUBTYPE_1 + TEXT_TYPE + COMMA_SEP +
                    RoomEntry.COLUMN_NAME_SUBTYPE_2 + TEXT_TYPE + COMMA_SEP +
                    RoomEntry.COLUMN_NAME_RECURRENCE + TEXT_TYPE + COMMA_SEP +
                    RoomEntry.COLUMN_NAME_REMINDER + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RoomEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cleaning.db";

    public RoomDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO read the data, re-create the db and put the data back with default values
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO must know the schema of the old database? Is that the current schema when this is
        // is called? In that scenario you would need to read the data, re-create the DB as per the
        // old schema (or current if this is the "old" version calling this) and add the data where
        // possible
        onUpgrade(db, oldVersion, newVersion);
    }

    public List<Room> getAllRooms()
    {
        // TODO should do this in an async way and not on the main thread
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                RoomContract.RoomEntry._ID,
                RoomContract.RoomEntry.COLUMN_NAME_ROOM,
                RoomContract.RoomEntry.COLUMN_NAME_SUBTYPE_1,
                RoomContract.RoomEntry.COLUMN_NAME_SUBTYPE_2,
                RoomContract.RoomEntry.COLUMN_NAME_REMINDER,
                RoomContract.RoomEntry.COLUMN_NAME_RECURRENCE
        };

        String sortOrder =
                RoomContract.RoomEntry.COLUMN_NAME_REMINDER + " DESC";

        Cursor cursor = db.query(
                RoomContract.RoomEntry.TABLE_NAME,
                projection,
                null,
                null,
                RoomContract.RoomEntry.COLUMN_NAME_ROOM,
                null,
                sortOrder
        );

        List<Room> roomList = new LinkedList<Room>();

        while (cursor.moveToNext()) {
            Room room = new Room();
            room.name = cursor.getString(cursor.getColumnIndex(RoomContract.RoomEntry.COLUMN_NAME_ROOM));
            room.subtype1 = cursor.getString(cursor.getColumnIndex(RoomContract.RoomEntry.COLUMN_NAME_SUBTYPE_1));
            room.subtype2 = cursor.getString(cursor.getColumnIndex(RoomContract.RoomEntry.COLUMN_NAME_SUBTYPE_2));

            try
            {
                room.reminder = ISO_8601_FORMAT.parse( cursor.getString(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_REMINDER)) );
            }
            catch( ParseException e )
            {
                // TODO if we really get an exception here we should make the reminder null and
                // immediately ask the user to reset their reminder
                Log.e( TAG, "failed to parse reminder", e );
                room.reminder = Calendar.getInstance().getTime();
            }

            room.recurrence = cursor.getString(cursor.getColumnIndex(RoomContract.RoomEntry.COLUMN_NAME_RECURRENCE));
            roomList.add(room);
        }

        db.close();

        return roomList;
    }
}
