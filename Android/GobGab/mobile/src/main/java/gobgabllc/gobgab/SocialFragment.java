package gobgabllc.gobgab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;

/**
 * Created by David on 3/13/2016.
 */
public class SocialFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate( R.layout.social_fragment, container, false);

        setHasOptionsMenu(true); //Add specific actions to app bar

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.social_menu, menu);
    }
}
