package gobgabllc.gobgab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;

/**
 * Created by David on 3/13/2016.
 */
public class MainMenuPagerActivity extends AppCompatActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_viewpager);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setCurrentItem(1);
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
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logout(){
        LoginManager.getInstance().logOut();
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
                    frag = new InboxFragment();
                    return frag;
                case 1:
                    frag = new PrimaryUIFragment();
                    return frag;
                case 2:
                    frag = new SocialFragment();
                    return frag;
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
}
