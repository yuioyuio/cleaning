package yuioyuoi.cleaning.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import yuioyuoi.cleaning.R;
import yuioyuoi.cleaning.activity.Dashboard;
import yuioyuoi.cleaning.data.RoomDbHelper;
import yuioyuoi.cleaning.model.Room;
import yuioyuoi.cleaning.notification.NotificationScheduler;

public class ListAdapter extends ArrayAdapter<Room> {

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<Room> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.itemlistrow, null);
        }

        final Room room = getItem(position);

        if (room != null) {
            TextView roomTextView = (TextView) v.findViewById(R.id.room);
            TextView subtype1TextView = (TextView) v.findViewById(R.id.subtype1);
            TextView subtype2TextView = (TextView) v.findViewById(R.id.subtype2);
            TextView actionTextView = (TextView) v.findViewById(R.id.action);
            TextView reminderTextView = (TextView) v.findViewById(R.id.reminder);
            TextView recurrenceTextView = (TextView) v.findViewById(R.id.recurrence);
            CheckBox status = (CheckBox ) v.findViewById(R.id.checkbox);

            if (roomTextView != null) {
                roomTextView.setText(room.name);
            }

            if (subtype1TextView != null) {
                subtype1TextView.setText(room.subtype1);
            }

            if (subtype2TextView != null) {
                subtype2TextView.setText(room.subtype2);
            }

            if (actionTextView != null) {
                actionTextView.setText(room.action);
            }

            if (reminderTextView != null) {
                reminderTextView.setText(room.reminder.toString());
            }

            if (recurrenceTextView != null) {
                recurrenceTextView.setText(room.recurrence);
            }

            if( status != null )
            {
                Date utcNow = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) ).getTime();
                status.setChecked(  room.reminder.after( utcNow ) );
                // TODO does this register multiple listeners on the recycled view?
                status.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v )
                    {
                        if( ( ( CheckBox ) v ).isChecked() )
                        {
                            // TODO implement next reminder based on recurrence
                            // default for now

                            RoomDbHelper roomDbHelper = new RoomDbHelper( parent.getContext() );

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis( calendar.getTimeInMillis() + 5000 );

                            room.reminder = calendar.getTime();

                            roomDbHelper.saveRoom( room._id,
                                    room.name,
                                    room.subtype1,
                                    room.subtype2,
                                    room.action,
                                    RoomDbHelper.ISO_8601_FORMAT.format( room.reminder ),
                                    room.recurrence);

                            // schedule next reminder
                            NotificationScheduler.getInstance().scheduleNotification( parent.getContext(), room.name, room.reminder );//calendar.getTime() );

                            ( ( Dashboard ) parent.getContext() ).onSave( room.name );
                        } else {
                            // TODO find out how to unschedule a reminder, maybe we need an id
                            // unschedule reminder
                        }
                    }
                } );
            }
        }

        return v;
    }

}