package yuioyuoi.cleaning.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

public class AddRoom extends Activity implements AdapterView.OnItemSelectedListener
{
    private static final String TAG = "AddRoom";

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
        EditText reminderEditText = (EditText) findViewById(R.id.reminder);
        EditText recurrenceEditText = (EditText) findViewById(R.id.recurrence);

        // TODO validate values, do this when wire these inputs properly with spinners

        RoomDbHelper roomDbHelper = new RoomDbHelper(getApplicationContext());
        SQLiteDatabase db = roomDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RoomContract.RoomEntry.COLUMN_NAME_ROOM, roomNameEditText.getText().toString());
        values.put(RoomContract.RoomEntry.COLUMN_NAME_SUBTYPE_1, subtype1EditText.getText().toString());
        values.put(RoomContract.RoomEntry.COLUMN_NAME_SUBTYPE_2, subtype2EditText.getText().toString());
        values.put(RoomContract.RoomEntry.COLUMN_NAME_REMINDER, reminderEditText.getText().toString());
        values.put(RoomContract.RoomEntry.COLUMN_NAME_RECURRENCE, recurrenceEditText.getText().toString());

        long newRowId = db.insert(RoomContract.RoomEntry.TABLE_NAME, null, values);
        Log.i(TAG, "inserted row id " + newRowId);

        db.close();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( calendar.getTimeInMillis() + 5000 );

        // TODO we need to schedule the next alarm on this!
        NotificationScheduler.getInstance().scheduleNotification( this, "notify!", calendar.getTime() );
    }
}
