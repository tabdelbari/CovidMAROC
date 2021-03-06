package ma.fst.covidmaroc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ma.fst.covidmaroc.dao.CIN;
import ma.fst.covidmaroc.dao.Entry;
import ma.fst.covidmaroc.model.Collision;
import ma.fst.covidmaroc.model.Path;
import ma.fst.covidmaroc.model.User;
import ma.fst.covidmaroc.task.AsyncTaskInsert;
import ma.fst.covidmaroc.task.AsyncTaskInsertCIN;
import ma.fst.covidmaroc.task.AsyncTaskLoadCIN;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MainActivity";

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private User user;
    private Path path;
    private List<Collision> collisions;
    private HashMap<String, Long> firstCollistions;

    private EditText text_cin;
    private Button btn_demarrer;

    private boolean nearby_started;

    public static final String SEVICE_ID = "ma.fst.covidmaroc.SERVICE_ID";

    private APIInterface apiInterface;

    private GoogleMap mMap;
    private ArrayList<LatLng> mPath;

    /**------------------------start PERMITIONS----------------------------*/
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (user != null){
                    user.setLat(locationResult.getLastLocation().getLatitude());
                    user.setLng(locationResult.getLastLocation().getLongitude());
                    LatLng posision = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    if (mMap != null && mPath != null) {
                        if ((mPath.size()>0 &&
                                !(mPath.get(mPath.size()-1).latitude == posision.latitude &&
                                mPath.get(mPath.size()-1).longitude == posision.longitude)) || mPath.size()==0)
                            mPath.add(posision);
                        mMap.clear();
                        mMap.addPolyline(new PolylineOptions().color(Color.BLUE).addAll(mPath));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posision, 20));
                    }
                    apiInterface.putUser(user.getCin(), user).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            Log.i(TAG, "coords saved");
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.e(TAG, " couldn't save coords", t);
                        }
                    });
                }

            }
        };
        text_cin = findViewById(R.id.text_cin);
        text_cin.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                refresh(view);
                return false;
            }
        });
        btn_demarrer = findViewById(R.id.btn_demarrer);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        refresh(null);
        new AsyncTaskLoadCIN(this).execute();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void requestLocation() {
        LocationRequest request = new LocationRequest();
        request.setInterval(3000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, Looper.myLooper());
    }

    public void refresh(View v){
        btn_demarrer.setEnabled(text_cin.getText().length()>4);
        btn_demarrer.setText(nearby_started?R.string.stop:R.string.start);
        // btn_arreter.setEnabled(nearby_started);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_REQUIRED_PERMISSIONS:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    requestLocation();
                }else{
                    Toast.makeText(this, "Merci d'activer GPS", Toast.LENGTH_LONG).show();
                }
        }
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
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            Nearby.getConnectionsClient(MainActivity.this).acceptConnection(endpointId, new PayloadCallback() {
                @Override
                public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                    byte[] receivedBytes = payload.asBytes();
                    Log.i(TAG, "onPayloadReceived: " + new String(receivedBytes));
                }

                @Override
                public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) { }
            });
        }

        @Override
        public void onConnectionResult(@NonNull String toEndpointId, @NonNull ConnectionResolution connectionResolution) {
            Payload bytesPayload = Payload.fromBytes(new byte[] {0xa, 0xb, 0xc, 0xd});
            Nearby.getConnectionsClient(MainActivity.this).sendPayload(toEndpointId, bytesPayload);
        }

        @Override
        public void onDisconnected(@NonNull String s) {}
    };
    private void startAdvertising() {
        user = new User();
        user.setCin(text_cin.getText().toString());
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(this).startAdvertising(text_cin.getText().toString(), SEVICE_ID, connectionLifecycleCallback, advertisingOptions);
    }
    // ==================================================================================================================

    // =============================== DISCOVERING ==================================================================
    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
            Nearby.getConnectionsClient(MainActivity.this).requestConnection(text_cin.getText().toString(), endpointId, connectionLifecycleCallback);
            Toast.makeText(MainActivity.this, "Found:"+info.getEndpointName(), Toast.LENGTH_SHORT).show();
            new AsyncTaskInsert(MainActivity.this).execute(
                    new Entry(text_cin.getText().toString(), info.getEndpointName(), new Date().getTime()));
            firstCollistions.put(text_cin.getText().toString(), System.currentTimeMillis());
            Collision c = new Collision();
            c.setCin(text_cin.getText().toString());
            c.setPathId(path.getPathId());
            c.setCinNear(info.getEndpointName());
            c.setLat(user.getLat());
            c.setLng(user.getLng());
            // yyyy-MM-dd'T'HH:mm:ss.SSS
            String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            c.setDate(date);
            collisions.add(c);
        }

        @Override
        public void onEndpointLost(String endpointId) {
            if(firstCollistions.keySet().contains(endpointId)){
                Long duration = (firstCollistions.keySet().contains((endpointId)))?(System.currentTimeMillis() - firstCollistions.get(endpointId)):0l; // Time difference
                for(Collision c:collisions){
                    if(c.getCinNear().equals(endpointId) && c.getDuration() == null){
                        c.setDuration(duration);
                    }
                }
            }
        }
    };
    private void startDiscovery() {
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(this).startDiscovery(SEVICE_ID, endpointDiscoveryCallback, discoveryOptions);
    }
    // ===================================================================================================================

    public void demarrer(View v){
        if (!nearby_started){
            new AsyncTaskInsertCIN(this).execute(new CIN(text_cin.getText().toString()));
            // =========== USER
            user = new User();
            user.setCin(text_cin.getText().toString());

            // =========== PATH
            path = new Path();
            path.setCin(text_cin.getText().toString());
            path.setPathId(System.currentTimeMillis()+"");
            mPath = new ArrayList<>();
            // yyyy-MM-dd'T'HH:mm:ss.SSS
            String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            path.setDate(date);

            // ============ Collisions
            collisions = new ArrayList<>();
            firstCollistions = new HashMap<>();
            // ============ Start
            startAdvertising();
            startDiscovery();
            requestLocation();
            nearby_started = true;
            btn_demarrer.setText(R.string.stop);
            text_cin.setEnabled(false);
            refresh(v);
        } else {
            arreter(v);
        }
    }

    public void arreter(View v){
        apiInterface.saveUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(MainActivity.this, "saved user", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
        apiInterface.savePath(path).enqueue(new Callback<Path>() {
            @Override
            public void onResponse(Call<Path> call, Response<Path> response) {
                Toast.makeText(MainActivity.this, "saved path", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Path> call, Throwable t) {

            }
        });
        for(Collision c:collisions){
            Long duration = (firstCollistions.keySet().contains((c.getCinNear())))?(System.currentTimeMillis() - firstCollistions.get(c.getCinNear())):0l; // Time difference
            if(c.getDuration() == null){
                c.setDuration(duration);
            }
        }
        apiInterface.saveCollisions(collisions).enqueue(new Callback<List<Collision>>() {
            @Override
            public void onResponse(Call<List<Collision>> call, Response<List<Collision>> response) {
                Toast.makeText(MainActivity.this, "saved Collisions", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Collision>> call, Throwable t) {

            }
        });
        Nearby.getConnectionsClient(this).stopAdvertising();
        Nearby.getConnectionsClient(this).stopDiscovery();
        Nearby.getConnectionsClient(this).stopAllEndpoints();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        text_cin.setEnabled(true);
        nearby_started = false;
        mPath = null;
        refresh(v);
    }

    public void showPaths(View v){
        Intent intent = new Intent(MainActivity.this, PathsActivity.class);
        intent.putExtra("cin", text_cin.getText().toString());
        startActivity(intent);
    }

    public void showQR(View v){
        Intent intent = new Intent(MainActivity.this, QRActivity.class);
        intent.putExtra("cin", text_cin.getText().toString());
        startActivity(intent);
    }

    public void onLoadCIN(String cin){
        if(cin!=null){
            text_cin.setText(cin);
            apiInterface.getPaths(cin).enqueue(new Callback<List<Path>>() {
                @Override
                public void onResponse(Call<List<Path>> call, Response<List<Path>> response) {
                    boolean notified = false;
                    for (Path p:response.body()) {
                        if (p.getColor()!=-1 && !notified){
                            createNotificationChannel();
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "covidmaroc")
                                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                                    .setContentTitle("Covid19: ATTENTION !")
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText("Déplacez-vous rapidement au service de dépistage le plus proche"))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                            // notificationId is a unique int for each notification that you must define
                            notificationManager.notify(1, builder.build());
                            notified = true;
                        }
                    }
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
        refresh(null);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
