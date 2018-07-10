package com.creative.routetracker.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.routetracker.R;
import com.creative.routetracker.alertbanner.AlertDialogForAnything;
import com.creative.routetracker.appdata.GlobalAppAccess;
import com.creative.routetracker.appdata.MydApplication;
import com.creative.routetracker.model.Route;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyRouteAdapter extends RecyclerView.Adapter<MyRouteAdapter.MyViewHolder> {

    public static final String KEY_EVENT = "key_event";
    private List<Route> Displayedplaces;
    private List<Route> Originalplaces;
    private LayoutInflater inflater;
    @SuppressWarnings("unused")
    private Activity activity;
    private String call_from;

    private PopupWindow popupwindow_obj;

    private int lastPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_route_name, tv_activity_type;
        ImageView img_delete;

        public MyViewHolder(View view) {
            super(view);
            tv_route_name = view.findViewById(R.id.tv_route_name);
            tv_activity_type = view.findViewById(R.id.tv_activity_type);
            img_delete = view.findViewById(R.id.img_delete);
        }
    }


    public MyRouteAdapter(Activity activity, List<Route> attendees) {
        this.activity = activity;
        this.Displayedplaces = attendees;
        this.Originalplaces = attendees;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_route, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Route timeLocation = Displayedplaces.get(position);
        holder.tv_route_name.setText(timeLocation.getRouteName());
        holder.tv_activity_type.setText(timeLocation.getActivityType());


        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForDelete(timeLocation.getRouteId(),position);
            }
        });


    }

    private void showDialogForDelete(int routeId,int position){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        alertDialog.setTitle("Alert");

        alertDialog.setMessage("Do you want to delete this route permanently?");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sendRequestToGetRoutes(GlobalAppAccess.URL_DELETE_ROUTE,routeId,position);

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }


    @Override
    public int getItemCount() {
        return Displayedplaces.size();
    }

    private String getDummyDeleteResponse() {
        return "{\n" +
                "\t\"Result\": true\n" +
                "}";
    }


    public void sendRequestToGetRoutes(String url, final  int routeId, final int position) {

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
                                Displayedplaces.remove(position);
                                notifyDataSetChanged();
                            }else{
                                AlertDialogForAnything.showAlertDialogWhenComplte(activity, "Error", "Error while deleting the route information!", false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AlertDialogForAnything.showAlertDialogWhenComplte(activity, "Error", "Error while deleting the route information!", false);

                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissProgressDialog();

                AlertDialogForAnything.showAlertDialogWhenComplte(activity, "Error", "Network problem. please try again!", false);


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

    private ProgressDialog progressDialog;

    public void showProgressDialog(String message, boolean isIntermidiate, boolean isCancelable) {
        /**/
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
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