package com.creative.routetracker;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.creative.routetracker.Utility.GpsEnableTool;
import com.creative.routetracker.Utility.LastLocationOnly;
import com.creative.routetracker.fragment.HomeFragment;

public class HomeActivity extends BaseActivity {

    private static final String TAG_HOME_FRAGMENT = "home_fragment";
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initToolbar();

        if (savedInstanceState == null) {

            /**
             * This is marshmallow runtime Permissions
             * It will ask user for grand permission in queue order[FIFO]
             * If user gave all permission then check whether user device has google play service or not!
             * NB : before adding runtime request for permission Must add manifest permission for that
             * specific request
             * */


            //getSupportFragmentManager()
            //        .beginTransaction()
            //        .add(R.id.content_layout, new HouseListFragment(), TAG_HOME_FRAGMENT)
            //        .commit();


            homeFragment = new HomeFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.content_layout, homeFragment, TAG_HOME_FRAGMENT)
                    .commit();


            LastLocationOnly lastLocationOnly = new LastLocationOnly(this);
            if (!lastLocationOnly.canGetLocation()) {
                GpsEnableTool gpsEnableTool = new GpsEnableTool(this);
                gpsEnableTool.enableGPs();
                return;
            }
        }


    }
}
