package yuioyuoi.cleaning.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.List;

import yuioyuoi.cleaning.R;
import yuioyuoi.cleaning.data.RoomDbHelper;
import yuioyuoi.cleaning.model.RoomContract;
import yuioyuoi.cleaning.notification.NotificationScheduler;

public class AddRoom extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private static final String TAG = "AddRoom";

    public final static String EXTRA_ROOM = "yuioyuio.cleaning.activity.AddRoom.extra.room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);


        /*Spinner spinner = (Spinner) findViewById(R.id.room_spinner);
        List<String> itemList = new ArrayList<String>();
        // TODO db helper to have a method to return all current known rooms
        // QUESTION what to do when you have too many rooms? item ... that rolls
        // to the next known rooms?
      	itemList.add("Bedroom");
        itemList.add("Bathroom");
        itemList.add("Kitchen");
      	itemList.add("Custom");
        initialiseSpinner( spinner, itemList );


        spinner = (Spinner) findViewById(R.id.subtype1_spinner);
        itemList = new ArrayList<String>();
        // TODO db helper to have a method to return all current known rooms
        // QUESTION what to do when you have too many rooms? item ... that rolls
        // to the next known rooms?
        itemList.add("Shower");
        itemList.add("Toilet");
        itemList.add("Sink");
        itemList.add("Desk");
        itemList.add("Custom");
        initialiseSpinner( spinner, itemList );*/
    }

    private void initialiseSpinner( Spinner spinner, List< String > itemList )
    {
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>( this,
                android.R.layout.simple_spinner_item, itemList );
        listAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinner.setAdapter( listAdapter );
        spinner.setOnItemSelectedListener( this );
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        System.out.println( pos );
        System.out.println( id );
        // if we are the last item we are the custom selection
        if( pos == ((Spinner)parent).getCount() - 1 )
        {
            final EditText customLabel = new EditText(this);

            // Set the default text to a link of the Queen
            customLabel.setHint( "Label name" );

            new AlertDialog.Builder(this)
                    .setTitle("Custom label name")
                    //.setMessage("Paste in the link of an image to moustachify!")
                    .setView(customLabel)
                    .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String url = customLabel.getText().toString();
                            // add name
                        }
                    })
                    .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        System.out.println( "nothing selected " + parent.toString() );
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

        RoomDbHelper roomDbHelper = new RoomDbHelper(getApplicationContext());

        roomDbHelper.newRoom( roomNameEditText.getText().toString(),
                subtype1EditText.getText().toString(),
                subtype2EditText.getText().toString(),
                actionEditText.getText().toString(),
                reminderEditText.getText().toString(),
                recurrenceEditText.getText().toString());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( calendar.getTimeInMillis() + 5000 );

        // TODO we need to schedule the next alarm on this!
        // TODO generate the text in the notification from somewhere common
        NotificationScheduler.getInstance().scheduleNotification( this, "notify!", calendar.getTime() );

        Intent intent = getIntent();
        intent.putExtra( EXTRA_ROOM, roomNameEditText.getText().toString() );
        setResult( RESULT_OK, intent );

        finish();
    }
}
