package com.creative.routetracker.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.creative.routetracker.R;
import com.creative.routetracker.appdata.GlobalAppAccess;
import com.creative.routetracker.appdata.MydApplication;

import java.util.ArrayList;

public class DialogFilter {

    private Context context;

    private DialogFilterListener filterListener;
    final Dialog dialog_start;

    public DialogFilter(Context context) {
        this.context = context;

        this.dialog_start = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
    }

    public void showDialog(final DialogFilterListener filterListener) {
        this.filterListener = filterListener;

        dialog_start.setCancelable(true);
        dialog_start.setContentView(R.layout.dialog_filter);

        CheckBox cb_fav = dialog_start.findViewById(R.id.cb_fav);


        RelativeLayout rl_container_dropdown = dialog_start.findViewById(R.id.rl_container_dropdown);
        ImageView img_dropdown_arrow = dialog_start.findViewById(R.id.img_dropdown_arrow);

        LinearLayout ll_container_activity_type = dialog_start.findViewById(R.id.ll_container_activity_type);
        ll_container_activity_type.removeAllViews();
        ll_container_activity_type.setVisibility(View.GONE);


        Button btn_filter = dialog_start.findViewById(R.id.btn_filter);


        ArrayList<CheckBox> activityTypeCheckBoxes = new ArrayList<>();
        for(String activityType: GlobalAppAccess.activity_type){
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(activityType);
            ll_container_activity_type.addView(checkBox);
            activityTypeCheckBoxes.add(checkBox);
        }

        rl_container_dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ll_container_activity_type.getVisibility() == View.GONE){
                    ll_container_activity_type.setVisibility(View.VISIBLE);
                    img_dropdown_arrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                }else{
                    ll_container_activity_type.setVisibility(View.GONE);
                    img_dropdown_arrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                }
            }
        });


        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> activityTypeFilter = new ArrayList<>();
                int count = 0;
                for(CheckBox checkBox: activityTypeCheckBoxes){
                    if (checkBox.isChecked()){
                        Log.d("DEbug",GlobalAppAccess.activity_type[count]);
                        activityTypeFilter.add(GlobalAppAccess.activity_type[count]);
                    }
                    count++;
                }


                filterListener.filterBy(cb_fav.isChecked(), activityTypeFilter);
                dialog_start.dismiss();
            }
        });

        dialog_start.show();
    }



    public void dismissDialog(){
        if(dialog_start != null){
            dialog_start.dismiss();
        }
    }


    public static abstract class DialogFilterListener {
        public abstract void filterBy(boolean isFav, ArrayList<String> activityType);
        public abstract void onCancel();
    }
}
