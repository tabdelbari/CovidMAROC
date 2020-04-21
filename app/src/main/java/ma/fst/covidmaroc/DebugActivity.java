package ma.fst.covidmaroc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ma.fst.covidmaroc.dao.Entry;
import ma.fst.covidmaroc.task.AsyncTaskLoad;
import ma.fst.covidmaroc.task.AsyncTaskUpdate;

public class DebugActivity extends AppCompatActivity {

    private static final String TAG = "DebugActivity";
    private ListView list_view;
    private ArrayList<Entry> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        data = new ArrayList<>();
        list_view = findViewById(R.id.list_entries);

        new AsyncTaskLoad(this).execute();
    }

    public void onLoadData(ArrayList<Entry> entries){
        this.data = entries;
        ArrayList<String> _data = new ArrayList<>();
        for (Entry e: entries) _data.add(e.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _data);
        list_view.setAdapter(adapter);
    }

    public void send(View v){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (final Entry e:data) {
            if(!e.isSaved()){
                Map<String, Object> entry = new HashMap<>();
                entry.put("cin", e.getCin());
                entry.put("cinNearby", e.getCinNearby());
                entry.put("date", e.getDate());
                db.collection("entries").add(entry)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                e.setSaved(true);
                                new AsyncTaskUpdate(DebugActivity.this).execute(e);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        }


    }


}
