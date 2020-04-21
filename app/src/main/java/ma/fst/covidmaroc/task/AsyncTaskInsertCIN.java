package ma.fst.covidmaroc.task;

import android.os.AsyncTask;

import ma.fst.covidmaroc.MainActivity;
import ma.fst.covidmaroc.dao.AppDatabase;
import ma.fst.covidmaroc.dao.CIN;

public class AsyncTaskInsertCIN extends AsyncTask<CIN, Void, Void> {

    private MainActivity context;

    public AsyncTaskInsertCIN(MainActivity context){this.context = context;}

    @Override
    protected Void doInBackground(CIN... cins) {
        AppDatabase.getDatabase(this.context).cinDao().insert(cins);
        return null;
    }

}