package lk.javainstitute.mybookings;

import android.Manifest;
import android.app.Activity;
import android.app.ComponentCaller;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class HotelRegisterActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri imagePath;
    private CloudinaryHelper cloudinaryHelper;

    private static  int IMAGE_REQ=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_register);

        Button btnPickImage = findViewById(R.id.button18);
        Button btnUploadImage = findViewById(R.id.button19);
        imageView = findViewById(R.id.upload_cld);

        initCongif();


        imageView.setOnClickListener(view -> requestPermission());


        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MediaManager.get().upload(imagePath).callback(new UploadCallback() {
                   @Override
                   public void onStart(String requestId) {
                       Log.e("Cloudinary Upload", "OnStart");
                   }

                   @Override
                   public void onProgress(String requestId, long bytes, long totalBytes) {
                       Log.e("Cloudinary Upload", "On Prograss");
                   }

                   @Override
                   public void onSuccess(String requestId, Map resultData) {
                       Log.e("Cloudinary Upload", "On Sucess");
                   }

                   @Override
                   public void onError(String requestId, ErrorInfo error) {
                       Log.e("Cloudinary Upload", "On Error");
                   }

                   @Override
                   public void onReschedule(String requestId, ErrorInfo error) {
                       Log.e("Cloudinary Upload", "onReschedule");
                   }
               }).dispatch();
            }
        });




    }

    private void initCongif() {
        Map config = new HashMap();

        config.put("cloud_name", "dvijulv3l");
        config.put("api_key", "712223112513167");
        config.put("api_secret", "XD0JhYw-tisapOvTSdLWsyBtHbw");

        //config.put("secure", true);
        MediaManager.init(this, config);
    }

    private void requestPermission(){

        if(ContextCompat.checkSelfPermission(HotelRegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            selecetImage();
        }else{
            ActivityCompat.requestPermissions(HotelRegisterActivity.this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },IMAGE_REQ);
        }

    }

    private void selecetImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQ && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imagePath = data.getData();
            Picasso.get().load(imagePath).into(imageView);
        } else {
            Toast.makeText(this, "Wrong", Toast.LENGTH_SHORT).show();
        }
    }

}
