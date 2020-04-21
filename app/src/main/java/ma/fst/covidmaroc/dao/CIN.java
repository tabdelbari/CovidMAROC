package ma.fst.covidmaroc.dao;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cin")
public class CIN {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "cin")
    private String cin;

    public CIN() { this.id = 1; }

    public CIN(String cin) {
        this.id = 1;
        this.cin = cin;
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

    @Override
    public String toString() {
        return "CIN{" + "id=" + id + ", cin='" + cin + '\'' + '}';
    }
}
