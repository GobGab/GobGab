package gobgabllc.gobgab;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * Created by David on 3/13/2016.
 */
public class PrimaryUIFragment extends Fragment{

    TextView spotifyInfoTest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.primary_ui_fragment, container, false);

        (new UpdateTrackThread()).start(); //Start checking for new track updates

        final FloatingActionButton button = (FloatingActionButton) rootView.findViewById(R.id.testbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playMusic();
            }
        });

        final FloatingActionButton button2 = (FloatingActionButton) rootView.findViewById(R.id.testbutton2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateBackgroundPicture();
            }
        });

        spotifyInfoTest = (TextView) rootView.findViewById(R.id.spotifyInfoTest);

        return rootView;
    }

    public class UpdateTrackThread extends Thread {

        public void run() {
            while(true) {
                if (MainMenuPagerActivity.trackInfoNeedsUpdate) {
                    updateTrackInfo();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }

    }

    public void playMusic(){
        if(MainMenuPagerActivity.playerIsInitialized){
            Toast.makeText(getActivity(), "here2", Toast.LENGTH_SHORT).show();
            TextView primaryText = (TextView) getView().findViewById(R.id.primaryText);
            primaryText.setText("Schemin Up (feat. Drake)");

            MainMenuPagerActivity.mPlayer.play("spotify:track:63M8PK8yavNITSViKUB62p");
        }
    }

    public void updateTrackInfo(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String s1 = MainMenuPagerActivity.settings.getString("trackId", "");
                String s2 = MainMenuPagerActivity.settings.getString("artistName", "");
                String s3 = MainMenuPagerActivity.settings.getString("albumName", "");
                String s4 = MainMenuPagerActivity.settings.getString("trackName", "");
                String s5 = MainMenuPagerActivity.settings.getString("typeOfMusic", "");
                String s6 = MainMenuPagerActivity.settings.getInt("trackLengthSeconds", 0)+"";

                spotifyInfoTest.setText("Type: " + s5 + "\nTrackId: " + s1 + "\nArtist: " + s2 + "\nAlbum: " + s3 + "\nName: " + s4 + "\nLength: " + s6);

                MainMenuPagerActivity.trackInfoNeedsUpdate = false; //Has been updated
            }
        });

    }

    public void updateBackgroundPicture(){
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mTxtDisplay.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
    }
}
