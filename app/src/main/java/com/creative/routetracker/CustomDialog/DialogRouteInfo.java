package com.creative.routetracker.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.creative.routetracker.R;
import com.creative.routetracker.appdata.GlobalAppAccess;

public class DialogRouteInfo {

    private Context context;

    private RouteInfoListener routeInfoListener;

    public DialogRouteInfo(Context context, RouteInfoListener routeInfoListener) {
        this.context = context;
        this.routeInfoListener = routeInfoListener;
    }

    public void showDialog() {
        final Dialog dialog_start = new Dialog(context,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog_start.setCancelable(true);
        dialog_start.setContentView(R.layout.dialog_route_info);


        final Button btn_ok = (Button) dialog_start.findViewById(R.id.btn_ok);
        final Button btn_cancel = (Button) dialog_start.findViewById(R.id.btn_cancel);
        final Spinner sp_activity_type = (Spinner) dialog_start.findViewById(R.id.sp_activity_type);

        final EditText ed_route_name = dialog_start.findViewById(R.id.ed_route_name);
        final EditText ed_area_type = dialog_start.findViewById(R.id.ed_area_type);
        final EditText ed_duration = dialog_start.findViewById(R.id.ed_duration);
        final EditText ed_fitness = dialog_start.findViewById(R.id.ed_fitness);
        final EditText ed_access = dialog_start.findViewById(R.id.ed_access);
        final EditText ed_safety_notes = dialog_start.findViewById(R.id.ed_safety_notes);

        ArrayAdapter<String> adapter_activity_type = new ArrayAdapter<String>
                (context, R.layout.spinner_item, GlobalAppAccess.activity_type);

        sp_activity_type.setAdapter(adapter_activity_type);


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFormValid = true;

                if (ed_route_name.getText().toString().isEmpty()) {
                    ed_route_name.setError("Required");
                    return;
                } else if (ed_area_type.getText().toString().isEmpty()) {
                    ed_area_type.setError("Required");
                    return;
                } else if (ed_duration.getText().toString().isEmpty()) {
                    ed_duration.setError("Required");
                    return;
                } else if (ed_fitness.getText().toString().isEmpty()) {
                    ed_fitness.setError("Required");
                    return;
                } else if (ed_access.getText().toString().isEmpty()) {
                    ed_access.setError("Required");
                    return;
                } else if (ed_safety_notes.getText().toString().isEmpty()) {
                    ed_safety_notes.setError("Required");
                    return;
                }


                routeInfoListener.gotRouteInfo(ed_route_name.getText().toString(),
                        ed_area_type.getText().toString(),
                        sp_activity_type.getSelectedItem().toString(),
                        ed_duration.getText().toString(),
                        ed_fitness.getText().toString(),
                        ed_access.getText().toString(),
                        ed_safety_notes.getText().toString());
                dialog_start.dismiss();


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeInfoListener.onCancel();
                //sendRequestForGetTimes(GlobalAppAccess.URL_GET_TIMES);
                dialog_start.dismiss();
            }
        });


        dialog_start.show();
    }


    public static abstract class RouteInfoListener {
        public abstract void gotRouteInfo(String routeName, String areaType, String activityType, String duration,
                                          String fitness, String access, String safetyNotes);
        public abstract void onCancel();
    }

}
