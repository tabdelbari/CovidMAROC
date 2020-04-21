package ma.fst.covidmaroc.task;

import android.os.AsyncTask;

import ma.fst.covidmaroc.MainActivity;
import ma.fst.covidmaroc.dao.AppDatabase;
import ma.fst.covidmaroc.dao.Entry;

public class AsyncTaskInsert extends AsyncTask<Entry, Void, Void> {

    private MainActivity context;

    public AsyncTaskInsert(MainActivity context){this.context = context;}

    @Override
    protected Void doInBackground(Entry... entries) {
        AppDatabase.getDatabase(this.context).entryDao().insertAll(entries);
        return null;
    }

}