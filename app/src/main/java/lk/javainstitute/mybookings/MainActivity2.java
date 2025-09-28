package lk.javainstitute.mybookings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {
    private static final int QR_SCAN_REQUEST = 100;
    private TextView resultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        Button scanButton = findViewById(R.id.scan_button1);
        resultText = findViewById(R.id.result_text1);

        scanButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, QRScannerActivity.class);
            startActivityForResult(intent, QR_SCAN_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == QR_SCAN_REQUEST && resultCode == RESULT_OK && data != null) {
            String qrData = data.getStringExtra("qr_data");
            resultText.setText("Scanned Data: " + qrData);
        }
    }
}