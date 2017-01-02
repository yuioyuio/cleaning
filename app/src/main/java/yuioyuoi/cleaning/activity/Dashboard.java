package yuioyuoi.cleaning.activity;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import java.util.Calendar;
import java.util.List;

import yuioyuoi.cleaning.R;
import yuioyuoi.cleaning.activity.adapter.ListAdapter;
import yuioyuoi.cleaning.data.RoomDbHelper;
import yuioyuoi.cleaning.model.Room;
import yuioyuoi.cleaning.model.RoomContract;
import yuioyuoi.cleaning.notification.NotificationScheduler;
import yuioyuoi.cleaning.startup.BootReceiver;

public class Dashboard extends AppCompatActivity {


    private static final String TAG = "Dashboard";

    public final static String EXTRA_MESSAGE = "yuioyuio.cleaning.activities.extra.MESSAGE";
    public final static String KEY_PREFS_FIRST_LAUNCH = "first_launch";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO on the plus button go to the activity to add name
                Intent intent = new Intent( Dashboard.this, AddRoom.class );
                startActivity( intent );

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        SharedPreferences prefs = this.getPreferences( Context.MODE_PRIVATE );
        // we only do this the first time the application is launched
        if( prefs.getBoolean( KEY_PREFS_FIRST_LAUNCH, true ) )
        {
            prefs.edit().putBoolean( KEY_PREFS_FIRST_LAUNCH, false ).commit();

            // TODO if we have no alarms we should disable the boot receiver until the user opens
            // the app again an enables notifications so that we don't call the boot receiver
            // unnecessarily

            // we enable the boot receiver so that we can reset the notifications when the user
            // restarts their android
            ComponentName receiver = new ComponentName( this, BootReceiver.class );
            PackageManager pm = this.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }

        // TODO remove this once we're sure it works
        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName( this, BootReceiver.class );
        Log.i( TAG, "component should be enabled " + pm.getComponentEnabledSetting( receiver ) );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void openSearch()
    {
        System.out.println( "search" );
    }

    public void openSettings()
    {
        System.out.println( "settings" );
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        NotificationScheduler scheduler = NotificationScheduler.getInstance();

        Calendar calendar = Calendar.getInstance();

        switch( item.getItemId() )
        {
            case R.id.action_search:
                openSearch();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_5:
                calendar.setTimeInMillis( calendar.getTimeInMillis() + 5000 );
                scheduler.scheduleNotification( this, "5 second delay", calendar.getTime() );
                return true;
            case R.id.action_10:
                calendar.setTimeInMillis( calendar.getTimeInMillis() + 10000 );
                scheduler.scheduleNotification( this, "10 second delay", calendar.getTime() );
                return true;
            case R.id.action_30:
                calendar.setTimeInMillis( calendar.getTimeInMillis() + 30000 );
                scheduler.scheduleNotification( this, "30 second delay", calendar.getTime() );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    /**
     * transition to an activity
     */
    public void sendMessage( View view )
    {
        Intent intent = new Intent( this, DisplayMessageActivity.class );
        EditText editText = ( EditText ) findViewById( R.id.edit_message );
        String message = editText.getText().toString();
        intent.putExtra( EXTRA_MESSAGE, message );
        startActivity( intent );
    }

    /**
     * persistence
     * TODO move this somewhere else
     */

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    // These are the Contacts rows that we will retrieve
    static final String[] PROJECTION = new String[]{RoomContract.RoomEntry._ID,
            RoomContract.RoomEntry.COLUMN_NAME_ROOM};

    // This is the select criteria
    static final String SELECTION = "((" +
            RoomContract.RoomEntry.COLUMN_NAME_ROOM + " NOTNULL) AND (" +
            RoomContract.RoomEntry.COLUMN_NAME_ROOM + " != '' ))";


    public void storeData(View view) {
        RoomDbHelper mDbHelper = new RoomDbHelper(getApplicationContext());

        EditText editText = (EditText) findViewById(R.id.room_name);
        String roomName = editText.getText().toString();
        editText = (EditText) findViewById(R.id.reminder);
        String reminder = editText.getText().toString();

        // TODO this should be done in an async way
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(RoomContract.RoomEntry.COLUMN_NAME_ROOM, roomName);
        values.put(RoomContract.RoomEntry.COLUMN_NAME_REMINDER, reminder);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(RoomContract.RoomEntry.TABLE_NAME, null, values);
        Log.i(TAG, "inserted row id " + newRowId);

        db.close();
    }

    public void readData(View view)
    {
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);

        ListView listView = (ListView) findViewById(R.id.upcoming_cleaning);
        listView.setEmptyView(progressBar);

        // must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        RoomDbHelper roomDbHelper = new RoomDbHelper(getApplicationContext());
        List<Room> roomList = roomDbHelper.getAllRooms();
        ListAdapter listAdapter = new ListAdapter(this, R.layout.itemlistrow, roomList);
        listView.setAdapter(listAdapter);

        Log.i(TAG, "loaded all room data");
    }

    public void boot( View view )
    {
        sendBroadcast( new Intent( "boot" ) );
    }
}
