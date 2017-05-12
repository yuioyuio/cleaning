package yuioyuoi.cleaning.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import yuioyuoi.cleaning.R;
import yuioyuoi.cleaning.data.RoomDbHelper;
import yuioyuoi.cleaning.notification.NotificationScheduler;

// TODO merge this with AddRoom
public class EditRoom extends AppCompatActivity
{
    private static final String TAG = "EditRoom";

    public final static String EXTRA_ROOM = "yuioyuio.cleaning.activity.EditRoom.extra.room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        Intent intent = getIntent();
        String room = intent.getStringExtra(Dashboard.EXTRA_ROOM_NAME);
        String subtype1 = intent.getStringExtra(Dashboard.EXTRA_ROOM_SUBTYPE1);
        String subtype2 = intent.getStringExtra(Dashboard.EXTRA_ROOM_SUBTYPE2);
        String action = intent.getStringExtra(Dashboard.EXTRA_ROOM_ACTION);
        String reminder = intent.getStringExtra(Dashboard.EXTRA_ROOM_REMINDER);
        String recurrence = intent.getStringExtra(Dashboard.EXTRA_ROOM_RECURRENCE);

        TextView roomTextView = (TextView) findViewById(R.id.room_name);
        TextView subtype1TextView = (TextView) findViewById(R.id.subtype1);
        TextView subtype2TextView = (TextView) findViewById(R.id.subtype2);
        TextView actionTextView = (TextView) findViewById(R.id.action);
        TextView reminderTextView = (TextView) findViewById(R.id.reminder);
        TextView recurrenceTextView = (TextView) findViewById(R.id.recurrence);

        roomTextView.setText( room );
        subtype1TextView.setText( subtype1 );
        subtype2TextView.setText( subtype2 );
        actionTextView.setText( action );
        reminderTextView.setText( reminder );
        recurrenceTextView.setText( recurrence );
    }

    public void storeData(View view)
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

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( calendar.getTimeInMillis() + 5000 );

        // TODO we need to schedule the next alarm on this!
        // TODO generate the text in the notification from somewhere common
        NotificationScheduler.getInstance().scheduleNotification( this, roomNameEditText.getText().toString(), calendar.getTime() );

        intent.putExtra( EXTRA_ROOM, roomNameEditText.getText().toString() );
        setResult( RESULT_OK, intent );

        finish();
    }
}
