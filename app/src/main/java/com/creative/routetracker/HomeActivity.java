package com.creative.routetracker;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.creative.routetracker.CustomDialog.DialogFilter;
import com.creative.routetracker.Utility.GpsEnableTool;
import com.creative.routetracker.Utility.LastLocationOnly;
import com.creative.routetracker.appdata.MydApplication;
import com.creative.routetracker.fragment.HomeFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {

    private static final String TAG_HOME_FRAGMENT = "home_fragment";
    private HomeFragment homeFragment;

    PlaceAutocompleteFragment autocompleteFragment;
    View view;


    public boolean isSearchOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initToolbar();

        initAutoCompleteTextView();

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

    @Override
    protected void onResume() {
        super.onResume();

        if (isSearchOpen) {
            isSearchOpen = false;
            view.setVisibility(View.GONE);
            autocompleteFragment.setText("");
        }

    }

    private void initAutoCompleteTextView(){
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        view = autocompleteFragment.getView();
        view.setVisibility(View.GONE);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                view.setVisibility(View.GONE);
                autocompleteFragment.setText("");
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
                HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME_FRAGMENT);

                homeFragment.searchResult(place);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                view.setVisibility(View.GONE);
                autocompleteFragment.setText("");

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            isSearchOpen = true;
            view.setVisibility(View.VISIBLE);
            //view.findViewById(R.id.place_autocomplete_clear_button).setVisibility(View.GONE);
            view.setBackgroundColor(Color.WHITE);
            View view2 = view.findViewById(R.id.place_autocomplete_search_button);
            view2.performClick();

        } else if (item.getItemId() == R.id.action_logout) {
            MydApplication.getInstance().getPrefManger().setUserProfile("");
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }else if(item.getItemId() == R.id.action_filter){
            showDialogToFilterOption();
        }else if(item.getItemId() == R.id.action_setting){
            startActivity(new Intent(HomeActivity.this, MyRouteActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private void showDialogToFilterOption() {
        final DialogFilter dialogFilter = new DialogFilter(this);

        DialogFilter.DialogFilterListener routeInfoListener = new DialogFilter.DialogFilterListener() {


            @Override
            public void filterBy(boolean isFav, ArrayList<String> activityType) {

                HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME_FRAGMENT);

                homeFragment.changeMapAccordingToFilter(isFav, activityType);
            }

            @Override
            public void onCancel() {

            }
        };

        dialogFilter.showDialog(routeInfoListener);


    }

    @Override
    public void onBackPressed() {
        if (isSearchOpen) {
            isSearchOpen = false;
            view.setVisibility(View.GONE);
            autocompleteFragment.setText("");
        } else {
            super.onBackPressed();
        }
    }
}
