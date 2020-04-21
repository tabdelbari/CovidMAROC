package ma.fst.covidmaroc.task;

import android.os.AsyncTask;

import ma.fst.covidmaroc.DebugActivity;
import ma.fst.covidmaroc.dao.AppDatabase;
import ma.fst.covidmaroc.dao.Entry;

public class AsyncTaskUpdate extends AsyncTask<Entry, Void, Void> {

    private DebugActivity context;

    public AsyncTaskUpdate(DebugActivity context){this.context = context;}

    @Override
    protected Void doInBackground(Entry... entries) {
        AppDatabase.getDatabase(this.context).entryDao().updateAll(entries);
        return null;
    }

}