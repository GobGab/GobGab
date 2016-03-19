package gobgabllc.gobgab;

import android.widget.ArrayAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public FriendsListAdapter(Context context, String[] values) {
        super(context, R.layout.inbox_item_layout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.inbox_item_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.nameLabel);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.messageStatus);
        textView.setText(values[position]);

        // Change icon based on name
        String s = values[position];

        System.out.println(s);

        return rowView;
    }
}