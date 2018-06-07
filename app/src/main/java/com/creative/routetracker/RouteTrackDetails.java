package com.creative.routetracker;

import android.content.pm.PackageManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class RouteTrackDetails extends BaseActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Route route;

    private Marker startMarker = null, stopMarker = null;

    LatLngBounds.Builder builder;

    LinearLayout ll_bottom_sheet;
    BottomSheetBehavior sheetBehavior;
    private static final int botomSheetPeekHeight = 240;


    private TextView tv_area_type, tv_activity_type, tv_route_name,tv_duration, tv_fitness, tv_access, tv_safety_notes;
    LinearLayout ll_rating_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_track_details);

        initToolbar(true);

        init();

        setUpMap();

        String gson = getIntent().getStringExtra("routeTrack");
        route = MydApplication.gson.fromJson(gson,Route.class);

        updateBottomSheet();

        //toggleBottomSheet();


    }

    private void init() {

        ll_bottom_sheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(ll_bottom_sheet);
        sheetBehavior.setPeekHeight(botomSheetPeekHeight);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        BottomSheetBehavior.from(ll_bottom_sheet).setHideable(false);

        tv_route_name = findViewById(R.id.tv_route_name);
        tv_area_type = findViewById(R.id.tv_area_type);
        tv_activity_type = findViewById(R.id.tv_activity_type);
        tv_duration = findViewById(R.id.tv_duration);
        tv_fitness = findViewById(R.id.tv_fitness);
        tv_access = findViewById(R.id.tv_access);
        tv_safety_notes = findViewById(R.id.tv_safety_notes);
        ll_rating_container = findViewById(R.id.ll_rating_container);
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.setOnMapClickListener(this);

        sendRequestToGetRouteTrack(GlobalAppAccess.URL_GET_ROUTE_TRACK,route.getRouteId());

    }

    private void drawRoutePath(ArrayList<RouteLocation> routeLocations){
        int i ;
        builder = new LatLngBounds.Builder();
        RouteLocation previousRouteLocation = routeLocations.get(0);
        LatLng startLatLng = new LatLng(previousRouteLocation.getLatitude(), previousRouteLocation.getLongitude());
        startMarker = mMap.addMarker(getStartOrStopMarker(startLatLng, HomeFragment.MARKER_TYPE_START, "Start"));
        builder.include(startLatLng);

        for ( i = 1; i < routeLocations.size(); i++) {
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


    private void updateBottomSheet(){

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

    public void sendRequestToGetRouteTrack(String url, final int routeId) {

        // TODO Auto-generated method stub
        showProgressDialog("Loading..", true, false);

        final StringRequest req = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("DEBUG",response);





                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                /*dismissProgressDialog();

                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Network problem. please try again!", false);*/


                dismissProgressDialog();
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
                }


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
