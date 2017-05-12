package yuioyuoi.cleaning.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import yuioyuoi.cleaning.R;
import yuioyuoi.cleaning.model.Room;

public class ListAdapter extends ArrayAdapter<Room> {

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<Room> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.itemlistrow, null);
        }

        Room p = getItem(position);

        if (p != null) {
            TextView roomTextView = (TextView) v.findViewById(R.id.room);
            TextView subtype1TextView = (TextView) v.findViewById(R.id.subtype1);
            TextView subtype2TextView = (TextView) v.findViewById(R.id.subtype2);
            TextView actionTextView = (TextView) v.findViewById(R.id.action);
            TextView reminderTextView = (TextView) v.findViewById(R.id.reminder);
            TextView recurrenceTextView = (TextView) v.findViewById(R.id.recurrence);

            if (roomTextView != null) {
                roomTextView.setText(p.name);
            }

            if (subtype1TextView != null) {
                subtype1TextView.setText(p.subtype1);
            }

            if (subtype2TextView != null) {
                subtype2TextView.setText(p.subtype2);
            }

            if (actionTextView != null) {
                actionTextView.setText(p.action);
            }

            if (reminderTextView != null) {
                reminderTextView.setText(p.reminder.toString());
            }

            if (recurrenceTextView != null) {
                recurrenceTextView.setText(p.recurrence);
            }
        }

        return v;
    }

}