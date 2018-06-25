package com.creative.routetracker.appdata;


import com.creative.routetracker.R;

public class GlobalAppAccess {


    public static final String APP_NAME = "TimeSetter";
    public static String BaseUrl = "http://5.135.67.228/RouteTracker/";
    //public static String BaseUrl = "none";
    public static final String URL_LOGIN =  BaseUrl + "login.jsp";
    public static final String URL_REGISTRATION = BaseUrl +  "register.jsp";
    public static final String URL_ADD_ROUTE = BaseUrl +  "addRoute.jsp";
    public static final String URL_ADD_RATING = BaseUrl +  "addRating.jsp";
    public static final String URL_GET_RATING = BaseUrl +  "getRating.jsp";
    public static final String URL_GET_ROUTES = BaseUrl +  "getRoutes.jsp";
    public static final String URL_GET_ROUTE_TRACK = BaseUrl +  "getRouteTrack.jsp";
    public static final String URL_SET_FAV = BaseUrl +  "setFavourite.jsp";
    public static final String URL_DELETE_ROUTE = BaseUrl +  "deleteRoute.jsp";


    public static final  int SUCCESS = 1;
    public static  final  int ERROR = 0;



    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;


    public static final String KEY_CALL_FROM = "call_from";
    public static final String KEY_NOTIFICATION_ID = "notification_id";

    public static final String TAG_ALARM_RECEIVER = "alarm_receiver";

    public static final String[] reminder_time_options = {"Select a time", "Before time expires", "15 mins before",
            "30 mins before","1 hour before"};

    public static final String[] rideshares_options = {"Uber", "Lyft", "Uber or Lyft"};

    public static final String[] activity_type = {"walk", "run", "hike", "cycle", "mountain bike", "exercise park", "other"};


    public static int getMarkerOfActivity(String type){
        if(type.equals(activity_type[0])){
            return R.drawable.marker_red;
        }else if(type.equals(activity_type[1])){
            return R.drawable.marker_orange;
        }else if(type.equals(activity_type[2])){
            return R.drawable.marker_green;
        }else if(type.equals(activity_type[3])){
            return R.drawable.marker_yellow;
        }else if(type.equals(activity_type[4])){
            return R.drawable.marker_blue;
        }else if(type.equals(activity_type[5])){
            return R.drawable.marker_blue_light;
        }else{
            return R.drawable.marker_black;
        }

    }

}
