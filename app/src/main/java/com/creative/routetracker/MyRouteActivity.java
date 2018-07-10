package com.creative.routetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.routetracker.adapter.MyRouteAdapter;
import com.creative.routetracker.alertbanner.AlertDialogForAnything;
import com.creative.routetracker.appdata.GlobalAppAccess;
import com.creative.routetracker.appdata.MydApplication;
import com.creative.routetracker.eventListener.RecyclerItemClickListener;
import com.creative.routetracker.model.Route;
import com.creative.routetracker.model.Routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyRouteActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private MyRouteAdapter myRouteAdapter;
    private ArrayList<Route> routes = new ArrayList<>();
    private TextView tv_alert_no_route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_route);

        initToolbar(true);

        init();

        initAdapter();


        sendRequestToGetRoutes(GlobalAppAccess.URL_GET_ROUTES, "userSpecific", String.valueOf(MydApplication.getInstance().getPrefManger().getUserProfile().getId()));
    }

    private void init(){
        recyclerView = findViewById(R.id.recycler_view);
        tv_alert_no_route = findViewById(R.id.tv_alert_no_route);
        tv_alert_no_route.setVisibility(View.GONE);
    }


    private void initAdapter(){
        myRouteAdapter = new MyRouteAdapter(this, routes);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myRouteAdapter);


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        Route category = routes.get(position);
                        String json = MydApplication.gson.toJson(category);
                        Intent intent = new Intent(MyRouteActivity.this, RouteTrackDetails.class);
                        intent.putExtra("routeTrack",json);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }


    public void sendRequestToGetRoutes(String url, final String filterBy, final String userId) {

        // TODO Auto-generated method stub
        showProgressDialog("Loading..", true, false);

        final StringRequest req = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("DEBUG",response);

                        dismissProgressDialog();
                        Routes routeInfo = MydApplication.gson.fromJson(response, Routes.class);

                        if (routeInfo.getResult() == 1) {

                            routes.addAll(routeInfo.getRoutes());

                            myRouteAdapter.notifyDataSetChanged();

                            if(routes.size() <= 0){
                                tv_alert_no_route.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }else{
                                tv_alert_no_route.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                        } else {
                            AlertDialogForAnything.showAlertDialogWhenComplte(MyRouteActivity.this, "Error", "Error while loading the route information!", false);
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissProgressDialog();

                AlertDialogForAnything.showAlertDialogWhenComplte(MyRouteActivity.this, "Error", "Network problem. please try again!", false);


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
}
