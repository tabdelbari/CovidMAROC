package ma.fst.covidmaroc.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "entry")
public class Entry {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "cin")
    private String cin;

    @ColumnInfo(name = "cinNearby")
    private String cinNearby;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "saved")
    private boolean saved;

    public Entry(){}

    public Entry(String cin, String cinNearby, long date) {
        this.cin = cin;
        this.cinNearby = cinNearby;
        this.date = date;
        this.saved = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getCinNearby() {
        return cinNearby;
    }

    public void setCinNearby(String cinNearby) {
        this.cinNearby = cinNearby;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    @Override
    public String toString() {
        return id + " | " +
                cin + " | " +
                cinNearby + " | " +
                new Date(date).toString() + " | " +
                (saved?"Y":"N");
    }
}
