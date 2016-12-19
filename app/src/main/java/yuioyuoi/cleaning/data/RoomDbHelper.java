package yuioyuoi.cleaning.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import yuioyuoi.cleaning.model.RoomContract.RoomEntry;

/**
 * Created by Jean on 28/11/2016.
 */

// TODO only ever use this in an async thread
public class RoomDbHelper extends SQLiteOpenHelper
{
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
}
