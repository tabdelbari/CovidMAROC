package ma.fst.covidmaroc.task;

import android.os.AsyncTask;

import java.util.ArrayList;

import ma.fst.covidmaroc.DebugActivity;
import ma.fst.covidmaroc.dao.AppDatabase;
import ma.fst.covidmaroc.dao.Entry;

public class AsyncTaskLoad extends AsyncTask<Void, Void, ArrayList<Entry>> {

    private DebugActivity context;

    public AsyncTaskLoad(DebugActivity context){this.context = context;}

    @Override
    protected ArrayList<Entry> doInBackground(Void... voids) {
        ArrayList<Entry> res = new ArrayList<>();
        for (Entry e :
                AppDatabase.getDatabase(this.context).entryDao().getAll()) res.add(e);
        return res;
    }

    @Override
    protected void onPostExecute(ArrayList<Entry> entries) {
        context.onLoadData(entries);
        super.onPostExecute(entries);
    }
}