package com.creative.routetracker.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.creative.routetracker.R;
import com.creative.routetracker.model.Feedback;

import java.util.List;


public class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.MyViewHolder> {

    public static final String KEY_EVENT = "key_event";
    private List<Feedback> Displayedplaces;
    private List<Feedback> Originalplaces;
    private LayoutInflater inflater;
    @SuppressWarnings("unused")
    private Activity activity;
    private String call_from;

    private PopupWindow popupwindow_obj;

    private int lastPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_feedback, tv_username;
        LinearLayout ll_rating_container;

        public MyViewHolder(View view) {
            super(view);
            tv_feedback = view.findViewById(R.id.tv_feedback);
            tv_username = view.findViewById(R.id.tv_username);
            ll_rating_container = view.findViewById(R.id.ll_rating_container);
        }
    }


    public FeedBackAdapter(Activity activity, List<Feedback> attendees) {
        this.activity = activity;
        this.Displayedplaces = attendees;
        this.Originalplaces = attendees;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_feedback, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Feedback timeLocation = Displayedplaces.get(position);
        holder.tv_feedback.setText(timeLocation.getComment());
        holder.tv_username.setText(timeLocation.getUserName());


        double rating = Double.parseDouble(timeLocation.getRating());
        int rating_int = (int) Math.floor(rating);
        float fraction = (float) (rating - Math.floor(rating));

        holder.ll_rating_container.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < rating_int; i++) {
            ImageView imageView = new ImageView(activity);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.ic_star_full);
            holder.ll_rating_container.addView(imageView);
        }

        if (fraction >= 0.5) {
            ImageView imageView = new ImageView(activity);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.ic_star_half);
            holder.ll_rating_container.addView(imageView);
        }


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