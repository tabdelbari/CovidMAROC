package ma.fst.covidmaroc;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRActivity extends AppCompatActivity {
    static String TAG = "QRActivity";

    private ImageView imageView;
    private Bitmap bitmap;
    private String cin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r);

        cin = getIntent().getStringExtra("cin");
        imageView = findViewById(R.id.imageView);

        QRGEncoder qrgEncoder = new QRGEncoder(cin, null, QRGContents.Type.TEXT, 500);
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.e(TAG, "=================== COULDN'T GENERATE QR CODE ==============", e);
        }
    }
}