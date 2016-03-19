package gobgabllc.gobgab;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by David on 3/13/2016.
 */
public class InboxFragment extends ListFragment {

    static final String[] Android =
            new String[] { "CupCake", "Donut", "Froyo", "GingerBread",
                    "HoneyComb","Ice-Cream Sandwich","Jelly-Bean"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new FriendsListAdapter(getActivity(), Android));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.inbox_fragment, container, false);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(getActivity(), selectedValue, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View v, int position, long id) {
                Toast.makeText(getActivity(), ""+position, Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }
}
