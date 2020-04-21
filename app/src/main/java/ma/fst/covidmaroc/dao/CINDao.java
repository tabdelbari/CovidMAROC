package ma.fst.covidmaroc.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface CINDao {

    @Query("SELECT * FROM cin WHERE id = 1 LIMIT 1")
    CIN find();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CIN... entries);
}
