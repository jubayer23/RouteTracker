
package com.creative.routetracker.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedBackInfo {

    @SerializedName("Result")
    @Expose
    private Integer result;
    @SerializedName("Feedbacks")
    @Expose
    private List<Feedback> feedbacks = null;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

}
