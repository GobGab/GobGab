package gobgabllc.gobgab;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by David on 3/13/2016.
 */
public class PrimaryUIFragment extends Fragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.primary_ui_fragment, container, false);

        final FloatingActionButton button = (FloatingActionButton) rootView.findViewById(R.id.testbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playMusic();
            }
        });

        return rootView;
    }



    public void playMusic(){
        if(MainMenuPagerActivity.playerIsInitialized){
            Toast.makeText(getActivity(), "here2", Toast.LENGTH_SHORT).show();
            TextView primaryText = (TextView) getView().findViewById(R.id.primaryText);
            primaryText.setText("Schemin Up (feat. Drake)");

            MainMenuPagerActivity.mPlayer.play("spotify:track:63M8PK8yavNITSViKUB62p");
        }
    }
}
