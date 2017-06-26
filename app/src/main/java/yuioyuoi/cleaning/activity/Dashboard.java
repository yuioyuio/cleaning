package yuioyuoi.cleaning.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.Calendar;
import java.util.List;

import yuioyuoi.cleaning.R;
import yuioyuoi.cleaning.activity.adapter.ListAdapter;
import yuioyuoi.cleaning.data.RoomDbHelper;
import yuioyuoi.cleaning.model.Room;
import yuioyuoi.cleaning.notification.NotificationScheduler;
import yuioyuoi.cleaning.startup.BootReceiver;
import yuioyuoi.cleaning.startup.Wizard;

public class Dashboard extends AppCompatActivity implements ScheduleRoomFragment.OnSaveListener
{


    private static final String TAG = "Dashboard";

    // TODO remove this
    public final static String EXTRA_MESSAGE = "yuioyuio.cleaning.activities.extra.MESSAGE";
    public final static String KEY_PREFS_FIRST_LAUNCH = "first_launch";

    public final static String REFRESH_DASHBOARD = "yuioyuio.cleaning.dashboard.refresh";

    public final static String EXTRA_ROOM_ID = "yuioyuio.cleaning.activity.Dashboard.ROOM_ID";
    public final static String EXTRA_ROOM_NAME = "yuioyuio.cleaning.activity.Dashboard.ROOM_NAME";
    public final static String EXTRA_ROOM_SUBTYPE1 = "yuioyuio.cleaning.activity.Dashboard.ROOM_SUBTYPE1";
    public final static String EXTRA_ROOM_SUBTYPE2 = "yuioyuio.cleaning.activity.Dashboard.ROOM_SUBTYPE2";
    public final static String EXTRA_ROOM_ACTION = "yuioyuio.cleaning.activity.Dashboard.ROOM_ACTION";
    public final static String EXTRA_ROOM_REMINDER = "yuioyuio.cleaning.activity.Dashboard.ROOM_REMINDER";
    public final static String EXTRA_ROOM_RECURRENCE = "yuioyuio.cleaning.activity.Dashboard.ROOM_RECURRENCE";

    static final int ADD_ROOM_REQUEST = 1;
    static final int EDIT_ROOM_REQUEST = 2;
    static final int SCHEDULE_ROOM_REQUEST = 3;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            refreshListView();
        }
    };

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
                startActivityForResult( intent, ADD_ROOM_REQUEST );
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

            // on first time setup we generate some default rooms
            Wizard wizard = new Wizard();
            wizard.create( getApplicationContext() );
        }
        createListView();

        Log.i(TAG, "loaded all room data");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.registerReceiver( messageReceiver, new IntentFilter( REFRESH_DASHBOARD ) );
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        this.unregisterReceiver( messageReceiver );
    }

    // TODO should do this in an asynch task / using a loader seems overkill at this point
    // guess the idea of a loader might be that this only gets called when the activity is created and not resumed from paused etc
    public void createListView()
    {
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);

        // must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(R.id.layout_dashboard);
        root.addView(progressBar);

        ListView listView = (ListView) findViewById(R.id.upcoming_cleaning);
        listView.setEmptyView(progressBar);

        RoomDbHelper roomDbHelper = new RoomDbHelper(getApplicationContext());
        List<Room> roomList = roomDbHelper.getAllRooms();
        ListAdapter listAdapter = new ListAdapter(this, R.layout.itemlistrow, roomList);
        listView.setAdapter(listAdapter);
        listView.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick( AdapterView< ? > parent, View view, int position, long id )
            {
                System.out.println("position " + position);
                System.out.println("id " + id);

                Room room = (Room) parent.getAdapter().getItem(position);

                Intent intent = new Intent( Dashboard.this, EditRoom.class );
                intent.putExtra( EXTRA_ROOM_ID, room._id );
                intent.putExtra( EXTRA_ROOM_NAME, room.name );
                intent.putExtra( EXTRA_ROOM_SUBTYPE1, room.subtype1 );
                intent.putExtra( EXTRA_ROOM_SUBTYPE2, room.subtype2 );
                intent.putExtra( EXTRA_ROOM_ACTION, room.action );
                intent.putExtra( EXTRA_ROOM_REMINDER, RoomDbHelper.ISO_8601_FORMAT.format( room.reminder ) );
                intent.putExtra( EXTRA_ROOM_RECURRENCE, room.recurrence );

                startActivityForResult( intent, EDIT_ROOM_REQUEST );

                return true;
            }
        } );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
            {
                System.out.println("position " + position);
                System.out.println("id " + id);

                Room room = (Room) parent.getAdapter().getItem(position);

                // TODO this is for the fragment
                ScheduleRoomFragment popup = new ScheduleRoomFragment();

                Bundle bundle = new Bundle();
                bundle.putString( EXTRA_ROOM_ID, room._id );
                bundle.putString( EXTRA_ROOM_NAME, room.name );
                bundle.putString( EXTRA_ROOM_SUBTYPE1, room.subtype1 );
                bundle.putString( EXTRA_ROOM_SUBTYPE2, room.subtype2 );
                bundle.putString( EXTRA_ROOM_ACTION, room.action );
                bundle.putString( EXTRA_ROOM_REMINDER, RoomDbHelper.ISO_8601_FORMAT.format( room.reminder ) );
                bundle.putString( EXTRA_ROOM_RECURRENCE, room.recurrence );
                popup.setArguments( bundle );
                popup.show( getSupportFragmentManager(), "schedulepopup" );

                /*Intent intent = new Intent( Dashboard.this, ScheduleRoom.class );
                intent.putExtra( EXTRA_ROOM_ID, room._id );
                intent.putExtra( EXTRA_ROOM_NAME, room.name );
                intent.putExtra( EXTRA_ROOM_SUBTYPE1, room.subtype1 );
                intent.putExtra( EXTRA_ROOM_SUBTYPE2, room.subtype2 );
                intent.putExtra( EXTRA_ROOM_ACTION, room.action );
                intent.putExtra( EXTRA_ROOM_REMINDER, RoomDbHelper.ISO_8601_FORMAT.format( room.reminder ) );
                intent.putExtra( EXTRA_ROOM_RECURRENCE, room.recurrence );

                startActivityForResult( intent, SCHEDULE_ROOM_REQUEST );*/
            }
        } );
    }

    public void refreshListView()
    {
        ListView listView = ( ListView ) findViewById( R.id.upcoming_cleaning );
        ListAdapter listAdapter = ( ( ListAdapter ) listView.getAdapter() );
        listAdapter.clear();

        RoomDbHelper roomDbHelper = new RoomDbHelper( getApplicationContext() );
        listAdapter.addAll( roomDbHelper.getAllRooms() );
        ( ( ListAdapter ) listView.getAdapter() ).notifyDataSetChanged();
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
            case R.id.boot:
                boot( null );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    public void boot( View view )
    {
        sendBroadcast( new Intent( "boot" ) );
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        String room = null;

        if( resultCode == RESULT_OK )
        {
            switch( requestCode )
            {
                case ADD_ROOM_REQUEST:
                    refreshListView();
                    room = data.getStringExtra(AddRoom.EXTRA_ROOM);
                    Snackbar.make( findViewById(R.id.layout_dashboard), room + " added and reminder scheduled", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                case EDIT_ROOM_REQUEST:
                    refreshListView();
                    room = data.getStringExtra(AddRoom.EXTRA_ROOM);
                    Snackbar.make( findViewById(R.id.layout_dashboard), room + " saved", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSave( String roomName )
    {
        // TODO generalise these snackbars, this is a duplicate of onActivityResult for edit
        refreshListView();
        Snackbar.make( findViewById(R.id.layout_dashboard), roomName + " saved", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
