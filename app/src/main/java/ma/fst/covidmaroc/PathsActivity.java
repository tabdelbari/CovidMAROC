package ma.fst.covidmaroc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.List;

import ma.fst.covidmaroc.model.Path;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PathsActivity extends AppCompatActivity {
    static String TAG = "PathsActivity";

    private APIInterface apiInterface;
    private ListView listView;
    private String cin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paths);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        cin = getIntent().getStringExtra("cin");
        listView = (ListView)findViewById(R.id.listView);
        apiInterface.getPaths(cin).enqueue(new Callback<List<Path>>() {
            @Override
            public void onResponse(Call<List<Path>> call, Response<List<Path>> response) {
                boolean notified = false;
                for (Path p:response.body()) {
                    if (p.getColor()!=-1 && !notified){
                        createNotificationChannel();
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(PathsActivity.this, "covidmaroc")
                                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                                .setContentTitle("Covid19: ATTENTION !")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("Déplacez-vous rapidement au service de dépistage le plus proche"))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(PathsActivity.this);
                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(1, builder.build());
                        notified = true;
                    }
                }
                listView.setAdapter(new CustomListAdapter(PathsActivity.this, response.body()));
            }

            private void createNotificationChannel() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "covidmaroc";
                    String description = "alerts for infections";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("covidmaroc", name, importance);
                    channel.setDescription(description);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
            }

            @Override
            public void onFailure(Call<List<Path>> call, Throwable t) {
                Log.e(TAG, "onFailure: CANT GET PATHS", t);
            }
        });
    }
}