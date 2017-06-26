package yuioyuoi.cleaning.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import yuioyuoi.cleaning.R;
import yuioyuoi.cleaning.data.RoomDbHelper;
import yuioyuoi.cleaning.notification.NotificationScheduler;

// TODO merge this with AddRoom
public class ScheduleRoom extends AppCompatActivity
{
    private static final String TAG = "ScheduleRoom";

    public final static String EXTRA_ROOM = "yuioyuio.cleaning.activity.ScheduleRoom.extra.room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_room);

        Intent intent = getIntent();
        String reminder = intent.getStringExtra(Dashboard.EXTRA_ROOM_REMINDER);
        String recurrence = intent.getStringExtra(Dashboard.EXTRA_ROOM_RECURRENCE);

        TextView reminderTextView = (TextView) findViewById(R.id.reminder);
        TextView recurrenceTextView = (TextView) findViewById(R.id.recurrence);

        reminderTextView.setText( reminder );
        recurrenceTextView.setText( recurrence );
    }

    public void saveData( View view)
    {
        EditText roomNameEditText = (EditText) findViewById(R.id.room_name);
        EditText subtype1EditText = (EditText) findViewById(R.id.subtype1);
        EditText subtype2EditText = (EditText) findViewById(R.id.subtype2);
        EditText actionEditText = (EditText) findViewById(R.id.action);
        EditText reminderEditText = (EditText) findViewById(R.id.reminder);
        EditText recurrenceEditText = (EditText) findViewById(R.id.recurrence);

        // TODO validate values, do this when wire these inputs properly with spinners

        Intent intent = getIntent();
        String _id = intent.getStringExtra(Dashboard.EXTRA_ROOM_ID);

        RoomDbHelper roomDbHelper = new RoomDbHelper(getApplicationContext());

        roomDbHelper.saveRoom( _id,
                roomNameEditText.getText().toString(),
                subtype1EditText.getText().toString(),
                subtype2EditText.getText().toString(),
                actionEditText.getText().toString(),
                reminderEditText.getText().toString(),
                recurrenceEditText.getText().toString());

        Date reminder = null;

        try
        {
            reminder = roomDbHelper.ISO_8601_FORMAT.parse( reminderEditText.getText().toString() );
        }
        catch( ParseException e )
        {
            // TODO failed to parse so fail to schedule, this should never happen if we enforce a datetimepicker
        }


        //Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis( calendar.getTimeInMillis() + 5000 );

        // TODO we need to schedule the next alarm on this!
        // TODO generate the text in the notification from somewhere common
        NotificationScheduler.getInstance().scheduleNotification( this, roomNameEditText.getText().toString(), reminder );//calendar.getTime() );

        intent.putExtra( EXTRA_ROOM, roomNameEditText.getText().toString() );
        setResult( RESULT_OK, intent );

        finish();
    }
}
