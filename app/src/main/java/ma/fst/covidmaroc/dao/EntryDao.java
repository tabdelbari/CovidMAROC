package ma.fst.covidmaroc.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntryDao {
    @Query("SELECT * FROM entry")
    List<Entry> getAll();

    @Query("SELECT * FROM entry WHERE id IN (:userIds)")
    List<Entry> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM entry WHERE cin LIKE :first LIMIT 1")
    Entry findByCin(String first);

    @Insert
    void insertAll(Entry... entries);

    @Update
    void updateAll(Entry... entries);

    @Delete
    void delete(Entry entry);
}
