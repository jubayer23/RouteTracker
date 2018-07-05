package com.creative.routetracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.routetracker.alertbanner.AlertDialogForAnything;
import com.creative.routetracker.appdata.DummyResponse;
import com.creative.routetracker.appdata.GlobalAppAccess;
import com.creative.routetracker.appdata.MydApplication;
import com.creative.routetracker.appdata.ViewPagerAdapter;
import com.creative.routetracker.fragment.FragmentRouteInfo;
import com.creative.routetracker.fragment.FragmentRouteReview;
import com.creative.routetracker.fragment.HomeFragment;
import com.creative.routetracker.model.Route;
import com.creative.routetracker.model.RouteLocation;
import com.creative.routetracker.model.Routes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biz.laenger.android.vpbs.BottomSheetUtils;
import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;


public class RouteTrackDetails extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG_FRAG_ROUTE_INFO = "Info";
    private static final String TAG_FRAG_ROUTE_REVIEW = "review";
    private GoogleMap mMap;
    private Route route;

    private Marker startMarker = null, stopMarker = null;

    LatLngBounds.Builder builder;

    LinearLayout ll_bottom_sheet;
    ViewPagerBottomSheetBehavior sheetBehavior;
    private static final int botomSheetPeekHeight = 240;
    private TabLayout bottom_sheet_tabs;
    private ViewPager bottom_sheet_viewpager;

    ArrayList<RouteLocation> routeLocations = new ArrayList<>();


    private TextView tv_area_type, tv_activity_type, tv_route_name, tv_duration, tv_fitness, tv_access, tv_safety_notes;
    LinearLayout ll_rating_container;


    private boolean isFav = false;
    private int favPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_track_details);


        String gson = getIntent().getStringExtra("routeTrack");
        route = MydApplication.gson.fromJson(gson, Route.class);


        initToolbar(route.getRouteName(), true);

        init();

        initBottomSheet();

        setUpMap();


        // updateBottomSheet();

        //toggleBottomSheet();


    }

    private void init() {


    }

    private void initBottomSheet() {
        ll_bottom_sheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        sheetBehavior = ViewPagerBottomSheetBehavior.from(ll_bottom_sheet);
        //sheetBehavior.setPeekHeight(botomSheetPeekHeight);
        //sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //sheetBehavior.setHideable(false);


        bottom_sheet_viewpager = (ViewPager) findViewById(R.id.bottom_sheet_viewpager);
        //setupViewPager(viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentRouteInfo(), TAG_FRAG_ROUTE_INFO);
        adapter.addFragment(new FragmentRouteReview(), TAG_FRAG_ROUTE_REVIEW);
        bottom_sheet_viewpager.setAdapter(adapter);

        bottom_sheet_tabs = (TabLayout) findViewById(R.id.bottom_sheet_tabs);
        bottom_sheet_tabs.setupWithViewPager(bottom_sheet_viewpager);
        BottomSheetUtils.setupViewPager(bottom_sheet_viewpager);

        bottom_sheet_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case 1:
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void setUpMap() {
        showProgressDialog("please wait..", true, false);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        dismissProgressDialog();
        if (mMap != null) {
            return;
        }
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
       // mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.setOnMapClickListener(this);

        sendRequestToGetRouteTrack(GlobalAppAccess.URL_GET_ROUTE_TRACK, route.getRouteId());

    }

    private void drawRoutePath(ArrayList<RouteLocation> routeLocations) {
        int i;
        builder = new LatLngBounds.Builder();
        RouteLocation previousRouteLocation = routeLocations.get(0);
        LatLng startLatLng = new LatLng(previousRouteLocation.getLatitude(), previousRouteLocation.getLongitude());
        startMarker = mMap.addMarker(getStartOrStopMarker(startLatLng, HomeFragment.MARKER_TYPE_START, "Start"));
        builder.include(startLatLng);

        for (i = 1; i < routeLocations.size(); i++) {
            RouteLocation currentLocation = routeLocations.get(i);
            LatLng middleLatlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            builder.include(middleLatlng);
            drawLineBetweenTwoPoint(previousRouteLocation, currentLocation);
            previousRouteLocation = currentLocation;
        }

        LatLng stopLatLng = new LatLng(previousRouteLocation.getLatitude(), previousRouteLocation/**/.getLongitude());
        builder.include(stopLatLng);
        stopMarker = mMap.addMarker(getStartOrStopMarker(stopLatLng, HomeFragment.MARKER_TYPE_STOP, "Stop"));

        LatLngBounds bounds = builder.build();
        int padding = 80; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    private void drawLineBetweenTwoPoint(RouteLocation prevLocation, RouteLocation currentLocation) {
        PolylineOptions rectLine = new PolylineOptions().width(15).color(getResources().getColor(R.color.colorPrimary));


        rectLine.add(new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude()));
        rectLine.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        Polyline polylin = mMap.addPolyline(rectLine);
        //polylines.add(polylin);
    }

    private MarkerOptions getStartOrStopMarker(LatLng position, int markerType, String title) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(title);
        if (markerType == HomeFragment.MARKER_TYPE_START) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_start));
        } else if (markerType == HomeFragment.MARKER_TYPE_STOP) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_stop));
        }

        return markerOptions;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_route_details, menu);

        MenuItem menuItem_fav = menu.findItem(R.id.action_fav);

        List<Route> favRoutes = MydApplication.getInstance().getPrefManger().getFavRoutes();

        isFav = false;
        int count = 0;
        for(Route favRoute: favRoutes){
            if(favRoute.getRouteId() == route.getRouteId()){
                isFav = true;
                favPosition = count;
                break;
            }
            count++;
        }

        if(isFav){
            menuItem_fav.setIcon(getResources().getDrawable(R.drawable.ic_favorite_fill_white));
        }else{
            menuItem_fav.setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_white));
        }
        //Do you custom menu work above this comment
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_fav) {
            ArrayList<Route> favRoutes = MydApplication.getInstance().getPrefManger().getFavRoutes();
            if(isFav){
                isFav = false;
                item.setIcon(getResources().getDrawable(R.drawable.ic_favorite_border_white));
                favRoutes.remove(favPosition);

            }else{
                item.setIcon(getResources().getDrawable(R.drawable.ic_favorite_fill_white));
                isFav = true;
                favPosition = favRoutes.size();
                favRoutes.add(route);

            }
            MydApplication.getInstance().getPrefManger().setFavRoutes(favRoutes);
            return true;
        }

        if (id == R.id.action_follow) {
            moveCameraToFollow();
            //MydApplication.getInstance().getPrefManger().setUserProfile("");
            //Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            //startActivity(intent);
            //finish();
            //processLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void moveCameraToFollow(){
        double lat1 = degToRad(routeLocations.get(0).getLatitude());
        double lon1 = degToRad(routeLocations.get(0).getLongitude());
        double lat2 = degToRad(routeLocations.get(1).getLatitude());
        double lon2 = degToRad(routeLocations.get(1).getLongitude());

        double a = Math.sin(lon2 - lon1) * Math.cos(lat2);
        double b = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1);
        float c = radToDeg((float) Math.atan2(a, b));

        RouteLocation previousRouteLocation = routeLocations.get(0);
        LatLng startLatLng = new LatLng(previousRouteLocation.getLatitude(), previousRouteLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(startLatLng, 20, 0, c)));
    }

    public static double degToRad(double deg){
        return deg * Math.PI / 180.0;
    }
    public static float radToDeg(float rad){
        rad = (float) (rad * (180.0 / Math.PI));
        if (rad < 0) rad = (float) (360.0 + rad);
        return rad;
    }


    private void updateBottomSheet() {

        tv_route_name.setText(route.getRouteName());
        tv_area_type.setText(route.getAreaType());
        tv_activity_type.setText(route.getActivityType());
        tv_duration.setText(route.getDuration());
        tv_fitness.setText(route.getFitness());
        tv_access.setText(route.getAccess());
        tv_safety_notes.setText(route.getSafetyNotes());

        int rating_int = (int) Math.floor(route.getAverageRating());
        float fraction = (float) (route.getAverageRating() - Math.floor(route.getAverageRating()));

        ll_rating_container.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < rating_int; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.ic_star_full);
            ll_rating_container.addView(imageView);
        }

        if (fraction >= 0.5) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.ic_star_half);
            ll_rating_container.addView(imageView);
        }


    }

    public void toggleBottomSheet() {

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.setPeekHeight(botomSheetPeekHeight);
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setPeekHeight(botomSheetPeekHeight);
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }


    public Route getRoute() {
        return route;
    }

    public void sendRequestToGetRouteTrack(String url, final int routeId) {

        // TODO Auto-generated method stub
        showProgressDialog("Loading..", true, false);

        final StringRequest req = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("DEBUG",response);


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int result = jsonObject.getInt("result");

                            if (result == 1) {
                                String routeTrack = jsonObject.getString("routeTrack");
                                String points[] = routeTrack.split(";");
                                routeLocations.clear();
                                for (String point : points) {
                                    String lat_lang[] = point.split(",");
                                    double lat = Double.parseDouble(lat_lang[0]);
                                    double lang = Double.parseDouble(lat_lang[1]);
                                    RouteLocation routeLocation = new RouteLocation(lat, lang);
                                    routeLocations.add(routeLocation);
                                }

                                drawRoutePath(routeLocations);
                            } else {
                                AlertDialogForAnything.showAlertDialogWhenComplte(RouteTrackDetails.this, "Error", "Error in fetching route info", false);
                                dismissProgressDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dismissProgressDialog();


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissProgressDialog();

                AlertDialogForAnything.showAlertDialogWhenComplte(RouteTrackDetails.this, "Error", "Network problem. please try again!", false);


               /* dismissProgressDialog();
                String dummyResponse = DummyResponse.getRouteTrack();

                try {
                    JSONObject jsonObject = new JSONObject(dummyResponse);

                    int result = jsonObject.getInt("result");

                    if (result == 1) {
                        String routeTrack = jsonObject.getString("routeTrack");
                        String points[] = routeTrack.split(";");
                        ArrayList<RouteLocation> routeLocations = new ArrayList<>();
                        for(String point : points){
                            String lat_lang[] = point.split(",");
                            double lat = Double.parseDouble(lat_lang[0]);
                            double lang = Double.parseDouble(lat_lang[1]);
                            RouteLocation routeLocation = new RouteLocation(lat,lang);
                            routeLocations.add(routeLocation);
                        }

                        drawRoutePath(routeLocations);
                    }else{
                        AlertDialogForAnything.showAlertDialogWhenComplte(RouteTrackDetails.this, "Error", "Error in fetching route info", false);
                        dismissProgressDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/


            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("routeId", String.valueOf(routeId));
                return params;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // TODO Auto-generated method stub
        MydApplication.getInstance().addToRequestQueue(req);
    }
}
