package yuioyuoi.cleaning.activities.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

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
            TextView tt1 = (TextView) v.findViewById(R.id.room);
            TextView tt2 = (TextView) v.findViewById(R.id.subtype1);
            TextView tt3 = (TextView) v.findViewById(R.id.reminder);

            if (tt1 != null) {
                tt1.setText(p.room);
            }

            if (tt2 != null) {
                tt2.setText(p.subtype1);
            }

            if (tt3 != null) {
                tt3.setText(p.reminder);
            }
        }

        return v;
    }

}