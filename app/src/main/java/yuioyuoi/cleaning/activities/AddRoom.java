package yuioyuoi.cleaning.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Random;

import yuioyuoi.cleaning.R;

public class AddRoom extends Activity implements OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);


        Spinner spinner = (Spinner) findViewById(R.id.room_spinner);

        List<String> itemList = new ArrayList<String>();
        // TODO db helper to have a method to return all current known rooms
        // QUESTION what to do when you have too many rooms? item ... that rolls
        // to the next known rooms?
      	itemList.add("Bedroom");
        itemList.add("Bathroom");
        itemList.add("Kitchen");
      	itemList.add("Custom");
      	ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
      		android.R.layout.simple_spinner_item, itemList);
      	listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item
        // Apply the adapter to the spinner
        spinner.setAdapter(listAdapter);

        spinner = (Spinner) findViewById(R.id.subtype1_spinner);

        List<String> subtype1List = new ArrayList<String>();
        // TODO db helper to have a method to return all current known rooms
        // QUESTION what to do when you have too many rooms? item ... that rolls
        // to the next known rooms?
      	subtype1List.add("Bedroom");
        subtype1List.add("Bathroom");
        subtype1List.add("Kitchen");
      	subtype1List.add("Custom");
      	listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roomList);
      	listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item
        // Apply the adapter to the spinner
        roomSpinner.setAdapter(listAdapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
