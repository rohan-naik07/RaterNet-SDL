package com.example.raternet_isp_app.models;

public class ReviewDetails {
    private String ISP_Name = null;
    private String MAP_Latitude = null;
    private String MAP_Longitude = null;

    private String UserEmail = null;

    private String type = null;

    private String priceRating = null;
    private String speedRating = null;
    private String serviceRating = null;
    private String overallRating = null;

    private String feedback = null;

    public ReviewDetails() {}

    public ReviewDetails(String ISP_Name, String MAP_Latitude, String MAP_Longitude, String UserEmail, String type, String speedRating, String priceRating, String serviceRating, String overallRating, String feedback) {
        this.ISP_Name = ISP_Name;
        this.MAP_Latitude = MAP_Latitude;
        this.MAP_Longitude = MAP_Longitude;
        this.UserEmail = UserEmail;
        this.type = type;
        this.priceRating = priceRating;
        this.speedRating = speedRating;
        this.serviceRating = serviceRating;
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getISP_Name() {
        return ISP_Name;
    }

    public String getMAP_Latitude() {
        return MAP_Latitude;
    }

    public String getMAP_Longitude() {
        return MAP_Longitude;
    }

    public String getOverallRating() {
        return overallRating;
    }

    public String getPriceRating() {
        return priceRating;
    }

    public String getServiceRating() {
        return serviceRating;
    }

    public String getSpeedRating() {
        return speedRating;
    }

    public String getType() {
        return type;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setISP_Name(String ISP_Name) {
        this.ISP_Name = ISP_Name;
    }

    public void setMAP_Latitude(String MAP_Latitude) {
        this.MAP_Latitude = MAP_Latitude;
    }

    public void setMAP_Longitude(String MAP_Longitude) {
        this.MAP_Longitude = MAP_Longitude;
    }

    public void setOverallRating(String overallRating) {
        this.overallRating = overallRating;
    }

    public void setPriceRating(String priceRating) {
        this.priceRating = priceRating;
    }

    public void setServiceRating(String serviceRating) {
        this.serviceRating = serviceRating;
    }

    public void setSpeedRating(String speedRating) {
        this.speedRating = speedRating;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }
}
