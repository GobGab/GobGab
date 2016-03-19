package gobgabllc.gobgab;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.rolandl.carousel.Carousel;
import fr.rolandl.carousel.CarouselAdapter;
import fr.rolandl.carousel.CarouselBaseAdapter;

/**
 * Created by David on 3/13/2016.
 */
public class PrimaryUIFragment extends Fragment{

    //Spotify Endpoint for GET requests to retrieve album information
    public static String spotifyTrackWebRetrievalURL = "https://api.spotify.com/v1/tracks/";

    public static String spotifyPackageName = "com.spotify.music";
    public static String applePackageName = "com.apple.android.music";

    public static String trackId;
    public static String artistName;
    public static String albumName;
    public static String trackName;
    public static String typeOfMusic;
    public static String trackLengthSeconds;

    private Handler updateTrackInfoHandler = new Handler();

    Carousel musicIconCarousel;

    RelativeLayout primaryUIFragLayout;

    ImageLoader mImageLoader;
    NetworkImageView mNetworkImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true); //Add specific actions to app bar

        mImageLoader = new ImageLoader(MySingleton.getInstance(getActivity()).getRequestQueue(), new LruBitmapCache(LruBitmapCache.getCacheSize(getActivity())));
        Log.i("PrimaryUIFrag", "In onCreate");

        updateTrackInfoHandler.postDelayed(checkUpdateTrackRunnable, 200);

        updateTrackInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.primary_ui_fragment, container, false);

        Log.i("PrimaryUIFrag", "In onCreateView");

        mNetworkImageView = (NetworkImageView) rootView.findViewById(R.id.primaryUIFragmentBackground);

        primaryUIFragLayout = (RelativeLayout) rootView.findViewById(R.id.primaryUIFragmentContent);

        musicIconCarousel = (Carousel) rootView.findViewById(R.id.musicIconsCarousel);

        List<PrimaryUICarousel> musicIcons = new ArrayList<>();
        musicIcons.add(new PrimaryUICarousel("", "spotify_icon"));
        musicIcons.add(new PrimaryUICarousel("", "google_play_icon"));
        musicIcons.add(new PrimaryUICarousel("", "paper_clip_icon"));
        musicIcons.add(new PrimaryUICarousel("", "record_icon"));
        musicIcons.add(new PrimaryUICarousel("", "apple_music_icon"));

        final CarouselAdapter musicIconCarouselAdapter = new MyCarouselAdapter(getActivity(), musicIcons);
        musicIconCarousel.setAdapter(musicIconCarouselAdapter);
        musicIconCarouselAdapter.notifyDataSetChanged();

        musicIconCarousel.setOnItemClickListener(new CarouselBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CarouselBaseAdapter<?> carouselBaseAdapter, View view, int position, long id) {
                musicIconCarousel.scrollToChild(position);
                handleMusicIconClick(position);
            }
        });

        ImageButton inboxButton = (ImageButton) rootView.findViewById(R.id.inboxButton);
        inboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    MainMenuPagerActivity.mPager.setCurrentItem(0);
                }catch(Exception e){Log.e("PrimaryUI.InboxClick", e.toString());}
            }
        });

        ImageButton socialButton = (ImageButton) rootView.findViewById(R.id.socialButton);
        socialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    MainMenuPagerActivity.mPager.setCurrentItem(2);
                }catch(Exception e){Log.e("PrimaryUI.InboxClick", e.toString());}
            }
        });


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.primary_ui_menu, menu);
    }

    private Runnable checkUpdateTrackRunnable = new Runnable() {
        @Override
        public void run() {
            if (MainMenuPagerActivity.trackInfoNeedsUpdate) {
                updateTrackInfo();
            }
            updateTrackInfoHandler.postDelayed(this, 100);
        }
    };

    public void playMusic(){
        if(MainMenuPagerActivity.playerIsInitialized){
            MainMenuPagerActivity.mPlayer.play("spotify:track:63M8PK8yavNITSViKUB62p");
        }
    }

    public void updateTrackInfo(){
        Log.i("updateTrackInfo", "Updating info");
        trackId = MainMenuPagerActivity.settings.getString("trackId", "");
        artistName = MainMenuPagerActivity.settings.getString("artistName", "");
        albumName = MainMenuPagerActivity.settings.getString("albumName", "");
        trackName = MainMenuPagerActivity.settings.getString("trackName", "");
        typeOfMusic = MainMenuPagerActivity.settings.getString("typeOfMusic", "");
        trackLengthSeconds = MainMenuPagerActivity.settings.getInt("trackLengthSeconds", 0) + "";

        updateBackgroundPicture();

        MainMenuPagerActivity.trackInfoNeedsUpdate = false; //Has been updated
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

    //Called whenever an icon on the carousel is clicked
    public void handleMusicIconClick(int position){
        String message="";
        switch(position){
            case 0:
                spotifyIconClicked();
                return;
            case 1:
                message="Play Music";
                break;
            case 2:
                message="Attachment";
                break;
            case 3:
                message="Record";
                break;
            case 4:
                appleMusicClicked();
                return;
            default:
                message="Nothing";
        }
        Toast.makeText(getActivity(),  message + " has been clicked" + "\nTrack: " + trackName + "\nArtist: " + artistName + "\nAlbum: " + albumName, Toast.LENGTH_LONG).show();
    }

    public void spotifyIconClicked(){

        Intent intent=null;

        //Check if spotify is installed. Otherwise prompt to install
        if(isPackageInstalled(spotifyPackageName, getActivity())){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("spotify:track:5j3QqRGflS4o5jbsFSwKW1"));
        }else{
            intent = new Intent( Intent.ACTION_VIEW, Uri.parse("market://details?id=" + spotifyPackageName));
        }
        getActivity().startActivity(intent);
    }

    public void playMusicClicked(){

    }

    public void appleMusicClicked(){
        if(!isPackageInstalled(applePackageName, getActivity())){
           Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse("market://details?id=" + applePackageName));
           startActivity(intent);
        }
    }

    //Check if the given package is installed
    public boolean isPackageInstalled(String packagename, Context context){
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
