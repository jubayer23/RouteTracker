package com.creative.routetracker.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.routetracker.R;
import com.creative.routetracker.RouteTrackDetails;
import com.creative.routetracker.adapter.FeedBackAdapter;
import com.creative.routetracker.alertbanner.AlertDialogForAnything;
import com.creative.routetracker.appdata.GlobalAppAccess;
import com.creative.routetracker.appdata.MydApplication;
import com.creative.routetracker.model.FeedBackInfo;
import com.creative.routetracker.model.Feedback;
import com.creative.routetracker.model.Route;
import com.creative.routetracker.model.RouteLocation;
import com.creative.routetracker.model.Routes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentRouteReview extends Fragment implements View.OnClickListener{

    private TextView tv_area_type, tv_activity_type, tv_route_name, tv_duration, tv_fitness, tv_access, tv_safety_notes;


    private RecyclerView recyclerView;
    private FeedBackAdapter feedBackAdapter;
    private ArrayList<Feedback> feedbacks = new ArrayList<>();

    private ProgressBar progressBar;


    private TextView tv_no_feedback;


    private Route route;


    ImageView img_rat_1,img_rat_2,img_rat_3,img_rat_4,img_rat_5;
    Button btn_add_feedback;
    EditText ed_comment;
    private int rating = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_review, container, false);


        init(view);

        initAddRatingUi(view);

        initAdapter();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        route = ((RouteTrackDetails)getActivity()).getRoute();
        sentRequestToGetFeedBack(GlobalAppAccess.URL_GET_RATING,route.getRouteId());

        //sendRequestToGetPlaceList(ApiUrl.getUrlForMovieList("50", null, null, null, null, null, "seeds", null,null),true);
    }


    private void init(View view) {

        recyclerView = view.findViewById(R.id.recycler_view);
        //recyclerView.setNestedScrollingEnabled(false);

        progressBar = view.findViewById(R.id.progressBar_cyclic);
        progressBar.setVisibility(View.GONE);

        tv_no_feedback = view.findViewById(R.id.tv_no_feedback);
        tv_no_feedback.setVisibility(View.GONE);

    }

    private void initAddRatingUi(View view){

        img_rat_1 = view.findViewById(R.id.img_rat_1);
        img_rat_1.setOnClickListener(this);
        img_rat_2 = view.findViewById(R.id.img_rat_2);
        img_rat_2.setOnClickListener(this);
        img_rat_3 = view.findViewById(R.id.img_rat_3);
        img_rat_3.setOnClickListener(this);
        img_rat_4 = view.findViewById(R.id.img_rat_4);
        img_rat_4.setOnClickListener(this);
        img_rat_5 = view.findViewById(R.id.img_rat_5);
        img_rat_5.setOnClickListener(this);

        btn_add_feedback = view.findViewById(R.id.btn_add_rating);
        btn_add_feedback.setOnClickListener(this);
        ed_comment = view.findViewById(R.id.ed_comment);
    }


    private void initAdapter(){
        feedBackAdapter = new FeedBackAdapter(getActivity(), feedbacks);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(feedBackAdapter);
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.img_rat_1){
            rating = 1;
            img_rat_1.setImageResource(R.drawable.ic_star_full);
            img_rat_2.setImageResource(R.drawable.ic_star_blankl);
            img_rat_3.setImageResource(R.drawable.ic_star_blankl);
            img_rat_4.setImageResource(R.drawable.ic_star_blankl);
            img_rat_5.setImageResource(R.drawable.ic_star_blankl);

        }
        if(id == R.id.img_rat_2){
            rating = 2;
            img_rat_1.setImageResource(R.drawable.ic_star_full);
            img_rat_2.setImageResource(R.drawable.ic_star_full);
            img_rat_3.setImageResource(R.drawable.ic_star_blankl);
            img_rat_4.setImageResource(R.drawable.ic_star_blankl);
            img_rat_5.setImageResource(R.drawable.ic_star_blankl);


        }
        if(id == R.id.img_rat_3){
            rating = 3;
            img_rat_1.setImageResource(R.drawable.ic_star_full);
            img_rat_2.setImageResource(R.drawable.ic_star_full);
            img_rat_3.setImageResource(R.drawable.ic_star_full);
            img_rat_4.setImageResource(R.drawable.ic_star_blankl);
            img_rat_5.setImageResource(R.drawable.ic_star_blankl);

        }if(id == R.id.img_rat_4){
            rating = 4;
            img_rat_1.setImageResource(R.drawable.ic_star_full);
            img_rat_2.setImageResource(R.drawable.ic_star_full);
            img_rat_3.setImageResource(R.drawable.ic_star_full);
            img_rat_4.setImageResource(R.drawable.ic_star_full);
            img_rat_5.setImageResource(R.drawable.ic_star_blankl);

        }
        if(id == R.id.img_rat_5){
            rating = 5;
            img_rat_1.setImageResource(R.drawable.ic_star_full);
            img_rat_2.setImageResource(R.drawable.ic_star_full);
            img_rat_3.setImageResource(R.drawable.ic_star_full);
            img_rat_4.setImageResource(R.drawable.ic_star_full);
            img_rat_5.setImageResource(R.drawable.ic_star_full);

        }

        if (id == R.id.btn_add_rating){
            if(rating == 0){
                Toast.makeText(getActivity(),"Please give some rating!",Toast.LENGTH_SHORT).show();
                return;
            }
            if(ed_comment.getText().toString().isEmpty()){
                ed_comment.setError("Required");
                return;
            }

            sendRequestToAddFeedBack(GlobalAppAccess.URL_ADD_RATING,MydApplication.getInstance().getPrefManger().getUserProfile().getId(),rating,ed_comment.getText().toString(),route.getRouteId());

        }


    }

    private void updateNoFeedBackUi(){
        if(feedbacks.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            tv_no_feedback.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            tv_no_feedback.setVisibility(View.GONE);
        }
    }



    public void sentRequestToGetFeedBack(String url, final int routeId) {

        // TODO Auto-generated method stub
        progressBar.setVisibility(View.VISIBLE);

        final StringRequest req = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("DEBUG",response);


                        FeedBackInfo feedBackInfo = MydApplication.gson.fromJson(response,FeedBackInfo.class);
                        feedbacks.addAll(feedBackInfo.getFeedbacks());
                        feedBackAdapter.notifyDataSetChanged();

                        updateNoFeedBackUi();

                        //dismissProgressDialog();
                        progressBar.setVisibility(View.GONE);



                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //dismissProgressDialog();
                progressBar.setVisibility(View.GONE);

                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Network problem. please try again!", false);

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

    public void sendRequestToAddFeedBack(String url, final int userId, final int rating, final String comment, final int routeId) {

        // TODO Auto-generated method stub
        showProgressDialog("Loading..", true, false);

        final StringRequest req = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("DEBUG",response);

                        dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int result = jsonObject.getInt("result");

                            if(result == 1){

                                Feedback feedback = new Feedback();

                                feedback.setComment(comment);
                                feedback.setUserId(userId);
                                feedback.setRating(String.valueOf(rating));
                                feedback.setUserName(MydApplication.getInstance().getPrefManger().getUserProfile().getName());

                                feedbacks.add(feedback);

                                feedBackAdapter.notifyDataSetChanged();

                                updateNoFeedBackUi();

                            }else{
                                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Error while add rating. Server problem!", false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Error while add rating. Server problem!", false);

                        }




                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissProgressDialog();

                AlertDialogForAnything.showAlertDialogWhenComplte(getActivity(), "Error", "Network problem. please try again!", false);


            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", String.valueOf(userId));
                params.put("routeId", String.valueOf(routeId));
                params.put("rating", String.valueOf(rating));
                params.put("comment", comment);
                return params;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // TODO Auto-generated method stub
        MydApplication.getInstance().addToRequestQueue(req);
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
