package ma.fst.covidmaroc.task;

import android.os.AsyncTask;

import ma.fst.covidmaroc.MainActivity;
import ma.fst.covidmaroc.dao.AppDatabase;
import ma.fst.covidmaroc.dao.CIN;

public class AsyncTaskLoadCIN extends AsyncTask<Void, Void, CIN> {

    private MainActivity context;

    public AsyncTaskLoadCIN(MainActivity context){this.context = context;}

    @Override
    protected CIN doInBackground(Void... voids) {
        return AppDatabase.getDatabase(this.context).cinDao().find();
    }

    @Override
    protected void onPostExecute(CIN cin) {
        context.onLoadCIN(cin!=null?cin.getCin():null);
        super.onPostExecute(cin);
    }
}