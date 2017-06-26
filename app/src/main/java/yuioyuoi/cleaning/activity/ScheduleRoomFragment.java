package yuioyuoi.cleaning.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.codetroopers.betterpickers.recurrencepicker.EventRecurrence;
import com.codetroopers.betterpickers.recurrencepicker.RecurrencePickerDialogFragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import yuioyuoi.cleaning.R;
import yuioyuoi.cleaning.data.RoomDbHelper;
import yuioyuoi.cleaning.notification.NotificationScheduler;

public class ScheduleRoomFragment extends DialogFragment implements RecurrencePickerDialogFragment.OnRecurrenceSetListener
{
    public interface OnSaveListener
    {
        void onSave( String roomName );
    }

    //private OnSaveListener saveListener;

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_schedule_room, null);

        String reminder = getArguments().getString(Dashboard.EXTRA_ROOM_REMINDER);
        String recurrence = getArguments().getString(Dashboard.EXTRA_ROOM_RECURRENCE);

        List< String > dateChoices = new ArrayList<>(  );
        dateChoices.add( reminder );
        dateChoices.add( "Select a date..." );

        SpinnerItemListener spinnerListener = new SpinnerItemListener();


        Spinner reminderSpinner = (Spinner) view.findViewById(R.id.reminder);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> reminderAdapter = new ArrayAdapter<String>( getContext(), android.R.layout.simple_spinner_item, dateChoices );
        // Specify the layout to use when the list of choices appears
        reminderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        reminderSpinner.setAdapter(reminderAdapter);
        reminderSpinner.setOnItemSelectedListener( spinnerListener );

        List< String > recurrenceChoices = new ArrayList<>(  );
        recurrenceChoices.add( "Every day" );
        recurrenceChoices.add( "Every week" );
        recurrenceChoices.add( "Every month" );
        recurrenceChoices.add( "Custom..." );

        Spinner recurrenceSpinner = (Spinner) view.findViewById(R.id.recurrence);
        ArrayAdapter<String> recurrenceAdapter = new ArrayAdapter<String>( getContext(), android.R.layout.simple_spinner_item, recurrenceChoices );
        recurrenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recurrenceSpinner.setAdapter(recurrenceAdapter);
        recurrenceSpinner.setOnItemSelectedListener( spinnerListener );

        builder.setView( view )
                .setPositiveButton("save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ScheduleRoomFragment.this.saveData(view);
                            }
                        }
                )
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick( DialogInterface dialog, int whichButton) {
                                ScheduleRoomFragment.this.dismiss();
                            }
                        }
                )
        ;
        return builder.create();
    }

    public class SpinnerItemListener implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            String selected = (String) parent.getItemAtPosition(pos);
            // TODO redo this properly
            if( selected.equals( "Select a date..." ) )
            {
                DialogFragment dateDialog = new DatePickerFragment();
                dateDialog.show(getFragmentManager(), "datePicker");
            }
            else if( selected.equals( "Custom..." ) )
            {
                FragmentManager fm = getFragmentManager();
                Bundle bundle = new Bundle();
                Time time = new Time();
                time.setToNow();
                bundle.putLong(RecurrencePickerDialogFragment.BUNDLE_START_TIME_MILLIS, time.toMillis(false));
                bundle.putString(RecurrencePickerDialogFragment.BUNDLE_TIME_ZONE, time.timezone);
                bundle.putString(RecurrencePickerDialogFragment.BUNDLE_RRULE, mRrule);
                bundle.putBoolean(RecurrencePickerDialogFragment.BUNDLE_HIDE_SWITCH_BUTTON, true);

                RecurrencePickerDialogFragment rpd = new RecurrencePickerDialogFragment();
                rpd.setArguments(bundle);
                rpd.setOnRecurrenceSetListener(ScheduleRoomFragment.this);
                rpd.show(fm, FRAG_TAG_RECUR_PICKER);
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";
    private EventRecurrence mEventRecurrence = new EventRecurrence();
    private String mRrule;

    @Override
    public void onRecurrenceSet(String rrule) {
        mRrule = rrule;
        if (rrule != null) {
            mEventRecurrence.parse(mRrule);
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet( DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }





    // this is not needed if we just get the Activity and cast it as the OnSaveListener interface
    /*@Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            this.saveListener = (OnSaveListener) context;
        }
        catch( final ClassCastException e )
        {
            throw new ClassCastException(context.toString() + " must implement " + OnSaveListener.class.toString());
        }
    }*/

    public void saveData( View view )
    {
        Bundle bundle = getArguments();
        String _id = bundle.getString(Dashboard.EXTRA_ROOM_ID);
        String roomName = bundle.getString(Dashboard.EXTRA_ROOM_NAME);
        String subtype1 = bundle.getString(Dashboard.EXTRA_ROOM_SUBTYPE1);
        String subtype2 = bundle.getString(Dashboard.EXTRA_ROOM_SUBTYPE2);
        String action = bundle.getString(Dashboard.EXTRA_ROOM_ACTION);
        EditText reminderEditText = (EditText) getDialog().findViewById(R.id.reminder);
        EditText recurrenceEditText = (EditText) getDialog().findViewById(R.id.recurrence);

        RoomDbHelper roomDbHelper = new RoomDbHelper(getActivity().getApplicationContext());

        roomDbHelper.saveRoom( _id,
                roomName,
                subtype1,
                subtype2,
                action,
                reminderEditText.getText().toString(),
                recurrenceEditText.getText().toString());

        Date reminder = null;

        try
        {
            reminder = RoomDbHelper.ISO_8601_FORMAT.parse( reminderEditText.getText().toString() );
        }
        catch( ParseException e )
        {
            // TODO failed to parse so fail to schedule, this should never happen if we enforce a datetimepicker
        }

        //Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis( calendar.getTimeInMillis() + 5000 );

        // TODO we need to schedule the next alarm on this!
        // TODO generate the text in the notification from somewhere common
        NotificationScheduler.getInstance().scheduleNotification( getActivity(), roomName, reminder );//calendar.getTime() );

        OnSaveListener onSaveListener = ( OnSaveListener ) getActivity();
        onSaveListener.onSave( roomName );

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                ScheduleRoomFragment.this.dismiss();
//            }
//        }, 250);

        this.dismiss();
    }
}
