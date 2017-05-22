package com.uwrf.service.data;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("email")
    private String email;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("dateTime")
    private String dateTime;

    public Message() {

    }

    public Message(String email, String latitude, String longitude, String dateTime) {

        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateTime = dateTime;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}
