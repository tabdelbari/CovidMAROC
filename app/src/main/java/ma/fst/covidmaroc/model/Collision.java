package ma.fst.covidmaroc.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Collision {

    @SerializedName("id")
    private String id;
    @SerializedName("pathId")
    private String pathId;
    @SerializedName("cin")
    private String cin;
    @SerializedName("cinNear")
    private String cinNear;
    @SerializedName("date")
    private String date;
    @SerializedName("duration")
    private Long duration;
    private double lat;
    @SerializedName("lng")
    private double lng;

    public Collision() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getCinNear() {
        return cinNear;
    }

    public void setCinNear(String cinNear) {
        this.cinNear = cinNear;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
