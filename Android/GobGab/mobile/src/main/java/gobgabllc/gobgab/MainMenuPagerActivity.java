package gobgabllc.gobgab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONObject;

/**
 * Created by David on 3/13/2016.
 */
public class MainMenuPagerActivity extends AppCompatActivity  implements PlayerNotificationCallback, ConnectionStateCallback {

    public static Boolean trackInfoNeedsUpdate = false;

    public static final String PREFS_NAME = "MyPrefsFile";

    public static final String CLIENT_ID = "28206b88a44449ef9f101665f28ec1dd";
    public static final String REDIRECT_URI = "gobgabspotifylogin://callback";

    public static final int SPOTIFY_REQUEST_CODE = 1337; //Spotify request code

    public static Player mPlayer;
    public static Boolean playerIsInitialized = false;

    public static Context MainMenuContext;

    private static final int NUM_PAGES = 3;
    private ViewPager mPager;

    public static SharedPreferences settings;
    public static  SharedPreferences.Editor editor;

    InboxFragment inboxFrag;
    PrimaryUIFragment uiFrag;
    SocialFragment socialFrag;

    //Nav Drawer Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_viewpager);

        //Instantiate fragments
        inboxFrag = new InboxFragment();
        uiFrag = new PrimaryUIFragment();
        socialFrag = new SocialFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setCurrentItem(1);

        MainMenuContext = getApplicationContext();

        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();

        //If Spotify token exists, create player. Else, retrieve token
        if( settings.getString("SpotifyToken", "") != "" ){
            instantiateSpotifyPlayer();
        }else{
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(MainMenuPagerActivity.CLIENT_ID, AuthenticationResponse.Type.TOKEN, MainMenuPagerActivity.REDIRECT_URI);
            builder.setScopes(new String[]{"user-read-private", "streaming"});
            builder.setShowDialog(true);
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request);
        }

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);



        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_logout:
                        logout();
                        return true;
                    case R.id.nav_settings:
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        getFacebookInfo();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == SPOTIFY_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                loginSpotify(response);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 1) {
            // If the user is looking at the middle fragment, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the middle fragment
            mPager.setCurrentItem(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewpager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logout(){
        LoginManager.getInstance().logOut();
        AuthenticationClient.clearCookies(this);
        Intent startMainScreen = new Intent(MainMenuPagerActivity.this, LoginActivity.class);
        startActivity(startMainScreen);

        finish();
    }

    /**
     * Pager adapter manages which fragments to load, in which order
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag=null;

            switch(position){
                case 0:
                    return inboxFrag;
                case 1:
                    return uiFrag;
                case 2:
                    return socialFrag;
                default:
                    frag = new PrimaryUIFragment();
                    return frag;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainMenuPagerActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainMenuPagerActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainMenuPagerActivity", "Login failed: " + error);
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainMenuPagerActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainMenuPagerActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    public void loginSpotify(AuthenticationResponse response){
        editor.putString("SpotifyToken", response.getAccessToken());

        editor.commit(); //Save the spotify auth token
    }

    public void logoutSpotify(){

    }

    public void instantiateSpotifyPlayer(){
        String token = settings.getString("SpotifyToken", "");

        Config playerConfig = new Config(this, token, CLIENT_ID);
        mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mPlayer.addConnectionStateCallback(MainMenuPagerActivity.this);
                mPlayer.addPlayerNotificationCallback(MainMenuPagerActivity.this);
                playerIsInitialized = true;
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainMenuPagerActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    public void getFacebookInfo(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted( JSONObject object, GraphResponse response) {
                        // Application code
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
