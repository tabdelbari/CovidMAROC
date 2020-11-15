package ma.fst.covidmaroc.model;

import com.google.gson.annotations.SerializedName;

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
    private Long date;
    @SerializedName("duration")
    private Long duration;

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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
