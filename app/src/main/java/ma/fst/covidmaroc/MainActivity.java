package ma.fst.covidmaroc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import ma.fst.covidmaroc.dao.CIN;
import ma.fst.covidmaroc.dao.Entry;
import ma.fst.covidmaroc.model.Collision;
import ma.fst.covidmaroc.task.AsyncTaskInsert;
import ma.fst.covidmaroc.task.AsyncTaskInsertCIN;
import ma.fst.covidmaroc.task.AsyncTaskLoadCIN;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";

    private EditText text_cin;
    private Button btn_demarrer, btn_arreter;

    private boolean nearby_started;

    public static final String SEVICE_ID = "ma.fst.covidmaroc.SERVICE_ID";

    private FirebaseFirestore db;

    private APIInterface apiInterface;

    /**------------------------start PERMITIONS----------------------------*/
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    /**------------------------END PERMITIONS----------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_cin = findViewById(R.id.text_cin);
        text_cin.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) { refresh(view); return false; }
        });
        btn_demarrer = findViewById(R.id.btn_demarrer);
        btn_arreter = findViewById(R.id.btn_arreter);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        refresh(null);

        new AsyncTaskLoadCIN(this).execute();

        db = FirebaseFirestore.getInstance();
    }

    public void refresh(View v){
        btn_demarrer.setEnabled(text_cin.getText().length()>4);
        btn_arreter.setEnabled(nearby_started);
    }

    @Override
    protected void onStart() {
        /**chack for permitions*/
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
            }
        }
        super.onStart();
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // ============================== ADVERTISING ===================================================================
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {}

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution connectionResolution) {}

        @Override
        public void onDisconnected(@NonNull String s) {}
    };
    private void startAdvertising() {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(this).startAdvertising(text_cin.getText().toString(), SEVICE_ID, connectionLifecycleCallback, advertisingOptions);
    }
    // ==================================================================================================================

    // =============================== DISCOVERING ==================================================================
    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
            Toast.makeText(MainActivity.this, "Found:"+info.getEndpointName(), Toast.LENGTH_SHORT).show();
            new AsyncTaskInsert(MainActivity.this).execute(
                    new Entry(text_cin.getText().toString(), info.getEndpointName(), new Date().getTime()));
            // TODO: start recording time
            Collision c = new Collision();
            c.setCin(text_cin.getText().toString());
            c.setPathId("1");
            c.setCinNear(info.getEndpointName());
            c.setDate(System.currentTimeMillis());
            c.setDuration(6000l);
            apiInterface.createCollision(c).enqueue(new Callback<Collision>() {
                @Override
                public void onResponse(Call<Collision> call, Response<Collision> response) {
                    Log.d(TAG, "onResponse: saved new collision to mongodb");
                }

                @Override
                public void onFailure(Call<Collision> call, Throwable t) {
                    Log.e(TAG, "onFailure: failed to save collision to mongodb", t);
                }
            });
        }
        @Override
        public void onEndpointLost(String endpointId) {
            // TODO: stop recording time
        }
    };
    private void startDiscovery() {
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(this).startDiscovery(SEVICE_ID, endpointDiscoveryCallback, discoveryOptions);
    }
    // ===================================================================================================================

    public void demarrer(View v){
        new AsyncTaskInsertCIN(this).execute(new CIN(text_cin.getText().toString()));
        startAdvertising();
        startDiscovery();
        nearby_started = true;
        text_cin.setEnabled(false);
        refresh(v);
        //TODO ajouter job pour firebase
    }

    public void arreter(View v){
        Nearby.getConnectionsClient(this).stopAdvertising();
        Nearby.getConnectionsClient(this).stopDiscovery();
        Nearby.getConnectionsClient(this).stopAllEndpoints();
        text_cin.setEnabled(true);
        nearby_started = false;
        refresh(v);
    }

    public void onLoadCIN(String cin){
        if(cin!=null){
            text_cin.setText(cin);
        }
        refresh(null);
    }


    /**
     * for debugage
     */
    public void startDebugage(View v){
        Intent intent = new Intent(this, DebugActivity.class);
        startActivity(intent);
    }

    public void startMap(View v){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


}
