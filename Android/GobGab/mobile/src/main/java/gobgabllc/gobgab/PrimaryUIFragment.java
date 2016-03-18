package gobgabllc.gobgab;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * Created by David on 3/13/2016.
 */
public class PrimaryUIFragment extends Fragment{

    //Spotify Endpoint for GET requests to retrieve album information
    public static String spotifyTrackWebRetrievalURL = "https://api.spotify.com/v1/tracks/";

    public static String trackId;
    public static String artistName;
    public static String albumName;
    public static String trackName;
    public static String typeOfMusic;
    public static String trackLengthSeconds;

    TextView spotifyInfoTest;

    RelativeLayout primaryUIFragLayout;

    ImageLoader mImageLoader;
    NetworkImageView mNetworkImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoader = new ImageLoader(MySingleton.getInstance(getActivity()).getRequestQueue(), new LruBitmapCache(LruBitmapCache.getCacheSize(getActivity())));

        MainMenuPagerActivity.trackInfoNeedsUpdate=true; //Update track info once when app loads using previously set values
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
            }
        });

        mNetworkImageView = (NetworkImageView) rootView.findViewById(R.id.primaryUIFragmentBackground);

        spotifyInfoTest = (TextView) rootView.findViewById(R.id.spotifyInfoTest);

        primaryUIFragLayout = (RelativeLayout) rootView.findViewById(R.id.primaryUIFragmentContent);

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
            MainMenuPagerActivity.mPlayer.play("spotify:track:63M8PK8yavNITSViKUB62p");
        }
    }

    public void updateTrackInfo(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                trackId = MainMenuPagerActivity.settings.getString("trackId", "");
                artistName = MainMenuPagerActivity.settings.getString("artistName", "");
                albumName = MainMenuPagerActivity.settings.getString("albumName", "");
                trackName = MainMenuPagerActivity.settings.getString("trackName", "");
                typeOfMusic = MainMenuPagerActivity.settings.getString("typeOfMusic", "");
                trackLengthSeconds = MainMenuPagerActivity.settings.getInt("trackLengthSeconds", 0) + "";

                spotifyInfoTest.setText("Type: " + typeOfMusic + "\nTrackId: " + trackId + "\nArtist: " + artistName + "\nAlbum: " + albumName + "\nName: " + trackName + "\nLength: " + trackLengthSeconds);

                updateBackgroundPicture();

                MainMenuPagerActivity.trackInfoNeedsUpdate = false; //Has been updated
            }
        });

    }

    public void updateBackgroundPicture(){
        String backgroundSourceUrl = spotifyTrackWebRetrievalURL + trackId.substring(trackId.indexOf(":", trackId.indexOf(":") + 1) + 1);
        Log.i("Background Source URL", backgroundSourceUrl);

        JsonObjectRequest request = new JsonObjectRequest(backgroundSourceUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String backgroundImgUrl = "";
                        try {
                            //Parse JSON returned from spotify to retrieve the URL of the album art
                            JSONObject album = response.getJSONObject("album");
                            JSONArray images  = album.getJSONArray("images");
                            JSONObject firstImage = images.getJSONObject(0);
                            backgroundImgUrl = firstImage.getString("url");
                            Log.i("Background IMG URL", backgroundImgUrl);
                        } catch (JSONException e) { e.printStackTrace(); }

                        // Set the URL of the image that should be loaded
                        mNetworkImageView.setImageUrl(backgroundImgUrl, mImageLoader);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("PrimaryUIFragment", error.toString());
                    }
                }
        );
        MySingleton.getInstance(getActivity()).getRequestQueue().add(request);
    }

    public String getSpotifyAlbumIdFromTrackURI(String trackUri){
        return "";
    }

}
