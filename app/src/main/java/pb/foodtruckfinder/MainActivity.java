package pb.foodtruckfinder;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.mopub.common.MoPub;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;

import pb.foodtruckfinder.Adapter.DividerItemDecoration;
import pb.foodtruckfinder.Adapter.MoPubAdapter;
import pb.foodtruckfinder.Adapter.SectionedRecyclerViewAdapter;
import pb.foodtruckfinder.Adapter.TruckAdapter;
import pb.foodtruckfinder.Fragment.MainFragment;
import pb.foodtruckfinder.Fragment.MapFragment;
import pb.foodtruckfinder.Fragment.PlaceholderFragment;
import pb.foodtruckfinder.Item.TruckItem;


import android.content.Intent;

public class MainActivity extends ActionBarActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "fF9wVi5SLqukKPscQemBmwbZr";
    private static final String TWITTER_SECRET = "FkRhJi5i4PTKnx3cXEtKTA1U4rx3NWA3dNmuwWMuN04kjsYhJy";


    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private PlaceholderFragment placeholderFragment;
    private MapFragment mMapFragment;



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
        super.onCreate(savedInstanceState);

        Log.d(TAG, "OnCreate");

        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);

                TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig), new MoPub());

        setContentView(R.layout.main_activity);

        if (savedInstanceState == null) {

            placeholderFragment = PlaceholderFragment.newInstance(2);
            mMapFragment = new MapFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, mMapFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();

        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
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
        placeholderFragment.onActivityResult(requestCode, resultCode,
                data);
    }
}