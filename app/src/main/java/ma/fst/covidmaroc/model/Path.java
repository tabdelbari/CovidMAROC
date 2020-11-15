package ma.fst.covidmaroc.model;

import com.google.gson.annotations.SerializedName;

public class Path {
    @SerializedName("id")
    private String id;
    @SerializedName("pathId")
    private String pathId;
    @SerializedName("cin")
    private String cin;

    public Path() { }

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
}
