package com.creative.routetracker.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creative.routetracker.R;

public class FragmentRouteReview extends Fragment {

    private TextView tv_area_type, tv_activity_type, tv_route_name, tv_duration, tv_fitness, tv_access, tv_safety_notes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_info, container, false);


        init(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //sendRequestToGetPlaceList(ApiUrl.getUrlForMovieList("50", null, null, null, null, null, "seeds", null,null),true);
    }


    private void init(View view) {

        tv_area_type = view.findViewById(R.id.tv_area_type);
        tv_activity_type = view.findViewById(R.id.tv_activity_type);
        tv_duration = view.findViewById(R.id.tv_duration);
        tv_fitness = view.findViewById(R.id.tv_fitness);
        tv_access = view.findViewById(R.id.tv_access);
        tv_safety_notes = view.findViewById(R.id.tv_safety_notes);
    }
}
