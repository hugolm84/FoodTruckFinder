package pb.foodtruckfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.mopub.common.MoPub;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import pb.foodtruckfinder.Activity.SettingsPreferenceActivity;
import pb.foodtruckfinder.Adapter.DividerItemDecoration;
import pb.foodtruckfinder.Adapter.SectionedRecyclerViewAdapter;
import pb.foodtruckfinder.Adapter.TruckAdapter;
import pb.foodtruckfinder.Fragment.LoginFragment;
import pb.foodtruckfinder.Fragment.MapFragment;
import pb.foodtruckfinder.Item.TruckItem;
import pb.foodtruckfinder.Socket.IO.SocketIO;
import pb.foodtruckfinder.Socket.IO.SocketSession;

public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "fF9wVi5SLqukKPscQemBmwbZr";
    private static final String TWITTER_SECRET = "FkRhJi5i4PTKnx3cXEtKTA1U4rx3NWA3dNmuwWMuN04kjsYhJy";
    public final static String CRASHLYTICS_KEY_SESSION_ACTIVATED = "session_activated";


    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private LoginFragment loginFragment;
    private MapFragment mMapFragment;

    private SocketSession socketSession;
    private SocketIO socketIO = null;


    private ArrayList<TruckItem> adapterData = new ArrayList<TruckItem>() {
        {
            add(new TruckItem("Bun Bun Truck", R.drawable.bunbun_prof, 3));

            add(new TruckItem("El Taco", R.drawable.eltaco_prof,2));

            add(new TruckItem("CurbSide", R.drawable.curbside, 5));

            add(new TruckItem("Funky Chicken", R.drawable.funky_prof,1));

            add(new TruckItem("Chillibussen", R.drawable.chilibussen_card,2));
        }
    };

    private RecyclerView mRecyclerView;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    List<SectionedRecyclerViewAdapter.Section> mSections =
            new ArrayList<SectionedRecyclerViewAdapter.Section>();
    private TruckAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "OnCreate");

        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);

        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig), new MoPub());

        setContentView(R.layout.main_activity);

        if (savedInstanceState == null) {

            socketSession = new SocketSession(this);
            socketIO = new SocketIO(this);
            loginFragment = LoginFragment.newInstance(2);
            mMapFragment = new MapFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, mMapFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewCompat.setElevation(findViewById(R.id.toolbar), getResources().getDimension(R.dimen.toolbar_elevation));
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
        }


        if(socketSession != null && !socketSession.getAuthToken().isEmpty()) {
            onAuthSuccess();
        } else {
            onAuthFailure();
        }

        initDrawer();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                getApplicationContext()
        ));

        //Your RecyclerView.Adapter
        mAdapter = new TruckAdapter(this,adapterData);


        //Sections
        mSections.add(new SectionedRecyclerViewAdapter.Section(0,"Favoriter"));
        mSections.add(new SectionedRecyclerViewAdapter.Section(1,"Foodtrucks närmast dig"));

        //Add your adapter to the sectionAdapter
        SectionedRecyclerViewAdapter.Section[] dummy = new SectionedRecyclerViewAdapter.Section[mSections.size()];
        SectionedRecyclerViewAdapter mSectionedAdapter = new
                SectionedRecyclerViewAdapter(this,R.layout.section,R.id.section_text,mAdapter);
        mSectionedAdapter.setSections(mSections.toArray(dummy));

        mRecyclerView.setAdapter(mSectionedAdapter);

    }

    private void initDrawer() {

        Log.d(TAG, "InitDrawer");
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this, SettingsPreferenceActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        if(id == R.id.action_logout) {
            unloadSessions();
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginFragment.onActivityResult(requestCode, resultCode,
                data);
    }

    private void unloadSessions() {
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        TwitterCore.getInstance().getAppSessionManager().clearActiveSession();
        Digits.getSessionManager().clearActiveSession();
        socketSession.logout();
        socketIO.disconnect();
        onAuthSuccess(false);
    }

    private void onAuthToken(final String token) {
        try {
            socketIO.connect(token);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void onAuthSuccess() {
        onAuthSuccess(true);
        onAuthToken(socketSession.getAuthToken());
    }

    public void onAuthFailure() {
        onAuthSuccess(false);
    }

    private void onAuthSuccess(boolean success) {
        toolbar.setVisibility((success ? View.VISIBLE : View.GONE));
        mMapFragment.setCanInteract(success);

        if(success)
            getSupportFragmentManager().beginTransaction().hide(loginFragment).commit();
        else {
            loginFragment = LoginFragment.newInstance(2);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_overlay, loginFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "SharedPref changed" + key);
    }
}