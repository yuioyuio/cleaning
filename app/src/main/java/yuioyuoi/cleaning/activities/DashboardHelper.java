package yuioyuoi.cleaning.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jean on 19/12/2016.
 */

public class DashboardHelper {




    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    // These are the Contacts rows that we will retrieve
    static final String[] PROJECTION = new String[] {RoomContract.RoomEntry._ID,
            RoomEntry.COLUMN_NAME_ROOM};

    // This is the select criteria
    static final String SELECTION = "((" +
            RoomEntry.COLUMN_NAME_ROOM + " NOTNULL) AND (" +
            RoomEntry.COLUMN_NAME_ROOM + " != '' ))";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch( item.getItemId() )
        {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_5:
                scheduleNotification(getNotification("5 second delay"), 5000);
                return true;
            case R.id.action_10:
                scheduleNotification(getNotification("10 second delay"), 10000);
                return true;
            case R.id.action_30:
                scheduleNotification(getNotification("30 second delay"), 30000);
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    public void sendMessage( View view )
    {
        Intent intent = new Intent( this, DisplayMessageActivity.class );
        EditText editText = ( EditText ) findViewById( R.id.edit_message );
        String message = editText.getText().toString();
        intent.putExtra( EXTRA_MESSAGE, message );
        startActivity( intent );
    }


    public void openSearch()
    {
        System.out.println( "search" );
    }

    public void openSettings()
    {
        System.out.println( "settings" );
    }

    public void storeData( View view )
    {
        RoomDbHelper mDbHelper = new RoomDbHelper(getApplicationContext());

        EditText editText = ( EditText ) findViewById( R.id.room_name );
        String roomName = editText.getText().toString();
        editText = ( EditText ) findViewById( R.id.reminder );
        String reminder = editText.getText().toString();

        // TODO this should be done in an async way
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(RoomEntry.COLUMN_NAME_ROOM, roomName);
        values.put(RoomEntry.COLUMN_NAME_REMINDER, reminder);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(RoomEntry.TABLE_NAME, null, values);
        Log.i( TAG, "inserted row id " + newRowId );

        db.close();
    }

    public void readData( View view )
    {
        RoomDbHelper mDbHelper = new RoomDbHelper(getApplicationContext());

        // TODO this should be done in an async way
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                RoomEntry._ID,
                RoomEntry.COLUMN_NAME_ROOM,
                RoomEntry.COLUMN_NAME_REMINDER,
                RoomEntry.COLUMN_NAME_SUBTYPE_1
        };

        String selection = RoomEntry.COLUMN_NAME_ROOM + " = ?";
        String[] selectionArgs = { "Bedroom" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                RoomEntry.COLUMN_NAME_REMINDER + " DESC";

        Cursor cursor = db.query(
                RoomEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                RoomEntry.COLUMN_NAME_ROOM,               // group by room
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {RoomEntry.COLUMN_NAME_ROOM, RoomEntry.COLUMN_NAME_REMINDER };
        int[] toViews = {android.R.id.text1, android.R.id.text2}; // The TextView in simple_list_item_1

        // list view set up

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);

        // get the list view
        ListView listView = ( ListView ) findViewById( R.id.upcoming_cleaning );
        listView.setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);


        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);

        List< Room > roomList = new LinkedList< Room >();


        while( cursor.moveToNext() )
        {
            Room room = new Room();
            room.room = cursor.getString( cursor.getColumnIndex( RoomEntry.COLUMN_NAME_ROOM ) );
            room.subtype1 = cursor.getString( cursor.getColumnIndex( RoomEntry.COLUMN_NAME_SUBTYPE_1 ) );
            room.reminder = cursor.getString( cursor.getColumnIndex( RoomEntry.COLUMN_NAME_REMINDER ) );
            roomList.add( room );
        }

        ListAdapter listAdapter = new ListAdapter( this, R.layout.itemlistrow, roomList );

        //listView.setAdapter(mAdapter);
        listView.setAdapter( listAdapter );

        mAdapter.swapCursor(cursor);

        Log.i( TAG, "swapped cursor" );

        /*

        while( cursor.moveToNext() )
        {
            long itemId = cursor.getLong( cursor.getColumnIndexOrThrow( RoomEntry._ID ) );
            String roomName = cursor.getString( cursor.getColumnIndexOrThrow(
                    RoomEntry.COLUMN_NAME_ROOM ) );
            String reminder = cursor.getString( cursor.getColumnIndexOrThrow(
                    RoomEntry.COLUMN_NAME_REMINDER ) );
            Log.i( TAG, itemId + " " + roomName + " " + reminder );
        }

        cursor.close();
        db.close();*/
    }
}