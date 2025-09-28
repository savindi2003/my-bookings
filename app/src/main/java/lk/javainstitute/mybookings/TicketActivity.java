package lk.javainstitute.mybookings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TicketActivity extends AppCompatActivity {

    private ImageView qrCodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ticket);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent i = getIntent();
        String hotel_name = i.getStringExtra("hotel_name");
        String client_name = i.getStringExtra("client_name");
        String client_mobile = i.getStringExtra("client_mobile");
        String check_in = i.getStringExtra("check_in");
        String check_out = i.getStringExtra("check_out");

        TextView hotelname_t = findViewById(R.id.textView53);
        TextView clientname_t = findViewById(R.id.textView55);
        TextView clientmobile_t = findViewById(R.id.textView57);
        TextView checkin_t = findViewById(R.id.textView59);
        TextView checkout_t = findViewById(R.id.textView61);

        hotelname_t.setText(hotel_name);
        clientname_t.setText(client_name);
        clientmobile_t.setText(client_mobile);
        checkin_t.setText(check_in);
        checkout_t.setText(check_out);

        //qr code
        qrCodeImage = findViewById(R.id.imageView19);
        String reservationId = i.getStringExtra("res_id");

        generateQRCode(reservationId);


        //qr code

        Button pdfbtn1 = findViewById(R.id.button14);
        pdfbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPdf();
            }
        });

    }

    public void createPdf() {

        View view = findViewById(R.id.main); // Replace with your layout ID

        // Create a Bitmap from the view
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        // Create PDFDocument
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();


        // Draw the bitmap onto the PDF page
        canvas.drawBitmap(bitmap, 0, 0, null);
        pdfDocument.finishPage(page);

        // Save the PDF to storage
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MyBookings");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "ticket.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();
            Toast.makeText(this, "PDF Saved in Downloads/Invoices", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void generateQRCode(String reservationId) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(reservationId, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCodeImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}