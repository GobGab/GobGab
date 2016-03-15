package gobgabllc.gobgab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class LoginActivity extends Activity  {
    private Button signInButton;
    private LoginButton fbSignInButton;
    private  TextView fbInfo;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        checkSavedCredentials();

        callbackManager = CallbackManager.Factory.create();

        fbSignInButton = (LoginButton) findViewById(R.id.fbSignInButton);
        fbInfo = (TextView)findViewById(R.id.fbInfo);

        //Handle what to do with information returned from FB login
        fbSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loadMainActivity();
            }

            @Override
            public void onCancel() {
                fbInfo.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                fbInfo.setText("Login attempt failed.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    //Called on activity being loaded. Checks if the user is already logged in
    public void checkSavedCredentials(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        fbInfo = (TextView)findViewById(R.id.fbInfo);
        if(accessToken!=null){
            loadMainActivity();
        }
    }

    //Load the main screen
    public void loadMainActivity(){
        Intent startMainScreen = new Intent(LoginActivity.this, MainMenuPagerActivity.class);
        startActivity(startMainScreen);
        finish();
    }
}