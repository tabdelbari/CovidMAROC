package ma.fst.covidmaroc.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Entry.class, CIN.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract EntryDao entryDao();
    public abstract CINDao cinDao();

    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "covid_local").build();
                }
            }
        }
        return INSTANCE;
    }
}
