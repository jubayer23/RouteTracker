package com.creative.routetracker.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.routetracker.R;
import com.creative.routetracker.Utility.CommonMethods;
import com.creative.routetracker.Utility.GpsEnableTool;
import com.creative.routetracker.Utility.LastLocationOnly;
import com.creative.routetracker.alertbanner.AlertDialogForAnything;
import com.creative.routetracker.appdata.DummyResponse;
import com.creative.routetracker.appdata.GlobalAppAccess;
import com.creative.routetracker.appdata.MydApplication;
import com.creative.routetracker.model.Route;
import com.creative.routetracker.model.RouteLocation;
import com.creative.routetracker.model.Routes;
import com.creative.routetracker.service.GpsServiceUpdate;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jubayer on 5/21/2018.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    public static final String TAG_INTENT_FILTER_NAME = "location_receiver";
    private MapView mapView;

    private GoogleMap mMap;

    private List<Route> routes = new ArrayList<>();

    private HashMap<Marker, Route> hashMapMarker = new HashMap<>();
    LatLngBounds.Builder builder;


    private FusedLocationProviderClient mFusedLocationClient;


    private FloatingActionButton btn_add_route;


    LinearLayout ll_recording;
    ImageView img_recording_icon;


    private RouteLocation previousRouteLocation = null;


    private static final int MARKER_TYPE_START = 1;
    private static final int MARKER_TYPE_STOP = 2;

    private Marker startMarker = null, stopMarker = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container,
                false);

        initMapView(view, savedInstanceState);

        init(view);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // init(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registerLocationReceiverBroadCast();

        sendRequestToGetRoutes(GlobalAppAccess.URL_GET_ROUTE, "none", "null");
    }

    private void init(View view) {
        btn_add_route = view.findViewById(R.id.btn_add_route);
        btn_add_route.setOnClickListener(this);


        ll_recording = view.findViewById(R.id.ll_recording);
        ll_recording.setVisibility(View.GONE);
        img_recording_icon = view.findViewById(R.id.img_recording_icon);

    }

    private void initMapView(View view, Bundle savedInstanceState) {
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.btn_add_route) {
            LastLocationOnly lastLocationOnly = new LastLocationOnly(getActivity());
            if (lastLocationOnly.canGetLocation()) {

                if(MydApplication.getInstance().getPrefManger().getRouteRecordingStatus()){

                    showAlertDialogToStopRecordingRoute();

                }else{
                    showAlertDialogToStartRecordingRoute();
                }



            } else {
                GpsEnableTool gpsEnableTool = new GpsEnableTool(getActivity());
                gpsEnableTool.enableGPs();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        //mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    protected void zoomToSpecificLocation(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to location user
                .zoom(20)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    protected void placeRoutesMarkerOnMap() {
        builder = new LatLngBounds.Builder();
        mMap.clear();
        hashMapMarker.clear();


        for (Route route : routes) {
            String starting_point[] = route.getStartingPoint().split(",");
            double lat = Double.parseDouble(starting_point[0]);
            double lang = Double.parseDouble(starting_point[1]);
            LatLng latLng = new LatLng(lat, lang);
            Marker marker = mMap.addMarker(getRouteMarker(latLng, route.getRouteName(), route.getActivityType()));
            hashMapMarker.put(marker, route);
            builder.include(latLng);
        }

        if (MydApplication.getInstance().getPrefManger().getRouteRecordingStatus()) {
            List<RouteLocation> routeLocations = MydApplication.getInstance().getPrefManger().getRouteLocations();
            int count = 0;
            previousRouteLocation = routeLocations.get(0);
            LatLng latLng = new LatLng(previousRouteLocation.getLatitude(), previousRouteLocation.getLongitude());
            startMarker = mMap.addMarker(getStartOrStopMarker(latLng, MARKER_TYPE_START, "Start"));

            for (int i = 1; i < routeLocations.size(); i++) {
                RouteLocation currentLocation = routeLocations.get(i);
                drawLineBetweenTwoPoint(previousRouteLocation,currentLocation);
                previousRouteLocation = currentLocation;
            }

            zoomToCurrentLocation();

            startRecordingAnimation();

        } else if (routes.size() > 0) {
            LatLngBounds bounds = builder.build();
            int padding = 80; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        } else {
            zoomToCurrentLocation();
        }


        dismissProgressDialog();

    }

    @SuppressLint("MissingPermission")
    private void zoomToCurrentLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            zoomToSpecificLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                    }
                });
    }

    private MarkerOptions getRouteMarker(LatLng position, String routeName, String activityType) {
        return new MarkerOptions()
                .position(position)
                .title(routeName)
                .icon(CommonMethods.bitmapDescriptorFromVector(getActivity(), GlobalAppAccess.getMarkerOfActivity(activityType)));
    }

    private MarkerOptions getStartOrStopMarker(LatLng position, int markerType, String title) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(title);
        if (markerType == MARKER_TYPE_START) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_start));
        } else if (markerType == MARKER_TYPE_STOP) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_stop));
        }

        return markerOptions;

    }

    private void showAlertDialogToStartRecordingRoute() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle("Add route");

        alertDialog.setMessage("Do you want to start recording route from your current location?");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @SuppressLint("MissingPermission")
            public void onClick(DialogInterface dialog, int which) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    RouteLocation routeLocation = new RouteLocation(location.getLatitude(),location.getLongitude());

                                    previousRouteLocation = routeLocation;
                                    LatLng latLng = new LatLng(previousRouteLocation.getLatitude(), previousRouteLocation.getLongitude());
                                    startMarker = mMap.addMarker(getStartOrStopMarker(latLng, MARKER_TYPE_START, "Start"));

                                    List<RouteLocation> routeLocations = MydApplication.getInstance().getPrefManger().getRouteLocations();
                                    routeLocations.clear();
                                    routeLocations.add(routeLocation);
                                    MydApplication.getInstance().getPrefManger().setRouteLocations(routeLocations);
                                    // Logic to handle location object

                                    zoomToSpecificLocation(new LatLng(location.getLatitude(), location.getLongitude()));

                                    getActivity().stopService(new Intent(getActivity(), GpsServiceUpdate.class));
                                    getActivity().startService(new Intent(getActivity(), GpsServiceUpdate.class));
                                    MydApplication.getInstance().getPrefManger().setRouteRecordingStatus(true);

                                    startRecordingAnimation();
                                }
                            }
                        });

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alertDialog.show();
    }
    private void showAlertDialogToStopRecordingRoute() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle("Add route");

        alertDialog.setMessage("Do you want to stop recording route?");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @SuppressLint("MissingPermission")
            public void onClick(DialogInterface dialog, int which) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    getActivity().stopService(new Intent(getActivity(), GpsServiceUpdate.class));
                                    MydApplication.getInstance().getPrefManger().setRouteRecordingStatus(false);

                                    RouteLocation routeLocation = new RouteLocation(location.getLatitude(),location.getLongitude());

                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    stopMarker = mMap.addMarker(getStartOrStopMarker(latLng, MARKER_TYPE_STOP, "Stop"));
                                    drawLineBetweenTwoPoint(previousRouteLocation,routeLocation);

                                    List<RouteLocation> routeLocations = MydApplication.getInstance().getPrefManger().getRouteLocations();
                                    routeLocations.add(routeLocation);
                                    MydApplication.getInstance().getPrefManger().setRouteLocations(routeLocations);
                                    // Logic to handle location object

                                    zoomToSpecificLocation(new LatLng(location.getLatitude(), location.getLongitude()));

                                    stopRecodingAnimation();
                                }
                            }
                        });

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alertDialog.show();
    }


    /**
     * Demonstrates customizing the info window and/or its contents.
     */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        // private final View mWindow;

        private final View mContents;

        CustomInfoWindowAdapter() {
            //mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {

            TextView title = view.findViewById(R.id.tv_title);
            LinearLayout ll_rating_container = view.findViewById(R.id.ll_rating_container);

            if ((startMarker != null && marker.equals(startMarker) ) || (stopMarker != null && marker.equals(stopMarker))) {
                title.setText(marker.getTitle());
                ll_rating_container.setVisibility(View.GONE);
            } else {
                Route route = hashMapMarker.get(marker);


                title.setText(route.getRouteName());

                int rating_int = (int) Math.floor(route.getAverageRating());
                float fraction = (float) (route.getAverageRating() - Math.floor(route.getAverageRating()));

                ll_rating_container.removeAllViews();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                for (int i = 0; i < rating_int; i++) {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams(params);
                    imageView.setImageResource(R.drawable.ic_star_full);
                    ll_rating_container.addView(imageView);
                }

                if (fraction >= 0.5) {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams(params);
                    imageView.setImageResource(R.drawable.ic_star_half);
                    ll_rating_container.addView(imageView);
                }
            }


        }
    }


    private void registerLocationReceiverBroadCast() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mLocationUpdateReceiver,
                new IntentFilter(TAG_INTENT_FILTER_NAME));
    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mLocationUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("DEBUG", "onReceive: called");
            if (mMap == null) return;
            // Get extra data included in the Intent
            String message = intent.getStringExtra(GpsServiceUpdate.KEY_CURRENT_LOCATION);
            RouteLocation userCurrentLocation = MydApplication.gson.fromJson(message, RouteLocation.class);
            drawLineBetweenTwoPoint(previousRouteLocation, userCurrentLocation);
            Log.d("receiver", "Got message: " + message);
        }
    };


    private void drawLineBetweenTwoPoint(RouteLocation prevLocation, RouteLocation currentLocation) {
        PolylineOptions rectLine = new PolylineOptions().width(15).color(getActivity().getResources().getColor(R.color.colorPrimary));


        rectLine.add(new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude()));
        rectLine.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        Polyline polylin = mMap.addPolyline(rectLine);
    }

    public void sendRequestToGetRoutes(String url, final String filterBy, final String userId) {

        // TODO Auto-generated method stub
        showProgressDialog("Loading..", true, false);

        final StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("DEBUG",response);


                        Routes routeInfo = MydApplication.gson.fromJson(response, Routes.class);

                        if (routeInfo.getResult() == 1) {

                            routes.addAll(routeInfo.getRoutes());

                            placeRoutesMarkerOnMap();

                        } else {
                            AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Wrong login information!", false);
                            dismissProgressDialog();
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                /*dismissProgressDialog();

                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Network problem. please try again!", false);*/

                String dummyResponse = DummyResponse.getRoutes();
                Routes routeInfo = MydApplication.gson.fromJson(dummyResponse, Routes.class);

                if (routeInfo.getResult() == 1) {

                    routes.addAll(routeInfo.getRoutes());

                    placeRoutesMarkerOnMap();

                } else {
                    AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Wrong login information!", false);
                    dismissProgressDialog();
                }

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("filterBy", filterBy);
                params.put("userId", userId);
                return params;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // TODO Auto-generated method stub
        MydApplication.getInstance().addToRequestQueue(req);
    }


    private void startRecordingAnimation() {


        btn_add_route.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        btn_add_route.setImageResource(R.drawable.ic_stop_white);

        ll_recording.setVisibility(View.VISIBLE);


        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(600); //You can manage the blinking time with this parameter
        anim.setStartOffset(400);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        img_recording_icon.startAnimation(anim);

    }

    private void stopRecodingAnimation() {

        btn_add_route.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
        btn_add_route.setImageResource(R.drawable.ic_add_white);


        ll_recording.setVisibility(View.GONE);

        img_recording_icon.clearAnimation();
    }

    @Override
    public void onResume() {
        Log.d("DEBUG","onResume called");
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        Log.d("DEBUG","onPause called");
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mLocationUpdateReceiver);
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private ProgressDialog progressDialog;

    public void showProgressDialog(String message, boolean isIntermidiate, boolean isCancelable) {
        /**/
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog.setIndeterminate(isIntermidiate);
        progressDialog.setCancelable(isCancelable);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog == null) {
            return;
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
