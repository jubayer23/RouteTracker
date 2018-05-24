
package com.creative.routetracker.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Route {

    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("routeId")
    @Expose
    private Integer routeId;
    @SerializedName("startingPoint")
    @Expose
    private String startingPoint;
    @SerializedName("routeName")
    @Expose
    private String routeName;
    @SerializedName("areaType")
    @Expose
    private String areaType;
    @SerializedName("activityType")
    @Expose
    private String activityType;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("fitness")
    @Expose
    private String fitness;
    @SerializedName("access")
    @Expose
    private String access;
    @SerializedName("safetyNotes")
    @Expose
    private String safetyNotes;
    @SerializedName("averageRating")
    @Expose
    private double averageRating;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFitness() {
        return fitness;
    }

    public void setFitness(String fitness) {
        this.fitness = fitness;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getSafetyNotes() {
        return safetyNotes;
    }

    public void setSafetyNotes(String safetyNotes) {
        this.safetyNotes = safetyNotes;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

}
