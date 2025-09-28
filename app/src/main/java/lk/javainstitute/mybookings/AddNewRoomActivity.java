package lk.javainstitute.mybookings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddNewRoomActivity extends AppCompatActivity {

    private EditText room_name_tv , count_tv,price_tv,guest_tv,balcony_tv,bed_tv,size_tv,bathroom_tv,sound_tv,view_tv,wifi_tv;
    private RadioGroup radioGroup;

    private ImageView imageView;
    private Uri imagePath;
    private static  int IMAGE_REQ=1;

    private String image_url;

    private String hotelId;
    private boolean isMediaManagerInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_add_new_room);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color1));

        Intent intent = getIntent();
        hotelId = intent.getStringExtra("hotelId");

        //Toast.makeText(AddNewRoomActivity.this,hotelId,Toast.LENGTH_LONG).show();

        RadioButton radio1 = findViewById(R.id.radioButton2);
        radio1.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        RadioButton radio2 = findViewById(R.id.radioButton3);
        radio2.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        RadioButton radio3 = findViewById(R.id.radioButton5);
        radio3.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

        room_name_tv=findViewById(R.id.editTextText_name);
        count_tv=findViewById(R.id.editTextText_count);
        guest_tv=findViewById(R.id.editTextText_guest);
        price_tv=findViewById(R.id.editTextText_price);

        bed_tv=findViewById(R.id.editTextText_bed);
        size_tv=findViewById(R.id.editTextText_size);
        balcony_tv=findViewById(R.id.editTextText_balcony);
        bathroom_tv=findViewById(R.id.editTextText_bathroom);
        view_tv=findViewById(R.id.editTextText_view);
        sound_tv=findViewById(R.id.editTextText_sound);
        wifi_tv=findViewById(R.id.editTextText_wifi);

        imageView = findViewById(R.id.imageView27);



        imageView.setOnClickListener(view -> requestPermission());

        Button btnUploadImage = findViewById(R.id.button24);
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
                        Toast.makeText(AddNewRoomActivity.this,"Image Uploaded",Toast.LENGTH_LONG).show();
                        image_url = resultData.get("secure_url").toString();
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


        Button createBtn = findViewById(R.id.button25);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                radioGroup = findViewById(R.id.radioGroup2);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {

                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedType = selectedRadioButton.getText().toString();

                    String room_name = room_name_tv.getText().toString();
                    String count = count_tv.getText().toString();
                    String guests = guest_tv.getText().toString();
                    String price = price_tv.getText().toString();

                    String bed = bed_tv.getText().toString();
                    String size = size_tv.getText().toString();
                    String balcony = balcony_tv.getText().toString();
                    String bathroom = bathroom_tv.getText().toString();
                    String views = view_tv.getText().toString();
                    String sound = sound_tv.getText().toString();
                    String wifi = wifi_tv.getText().toString();

                    if(room_name.isEmpty()){
                        room_name_tv.setError("Please enter room name");
                    }else if(count.isEmpty()){
                        count_tv.setError("Please enter room count");
                    }else if(guests.isEmpty()){
                        guest_tv.setError("Please enter maximum guest count");
                    }else if(price.isEmpty()){
                        price_tv.setError("Please enter room price per night");
                    }else if(image_url.isEmpty()){
                        Toast.makeText(AddNewRoomActivity.this,"Please upload image",Toast.LENGTH_LONG).show();
                    }else if(bed.isEmpty()){
                        bed_tv.setError("Fill this for continue room adding");
                    }else if(size.isEmpty()){
                        size_tv.setError("Fill this for continue room adding");
                    }else if(balcony.isEmpty()){
                        balcony_tv.setError("Fill this for continue room adding");
                    }else if(bathroom.isEmpty()){
                        bathroom_tv.setError("Fill this for continue room adding");
                    }else if(views.isEmpty()){
                        view_tv.setError("Fill this for continue room adding");
                    }else if(sound.isEmpty()){
                        sound_tv.setError("Fill this for continue room adding");
                    }else if(wifi.isEmpty()){
                        wifi_tv.setError("Fill this for continue room adding");
                    }else{

                        String roomId = generateUniqueID();

                        double count_int = 0;
                        double price_int = 0;
                        try {
                            count_int = Double.parseDouble(count);
                            price_int = Double.parseDouble(price);
                        } catch (NumberFormatException e) {
                            // Handle invalid number format
                            Toast.makeText(AddNewRoomActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                            return;  // Exit the method if invalid
                        }

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("balcony", balcony);
                        hashMap.put("bathroom", bathroom);
                        hashMap.put("bed", bed);
                        hashMap.put("count", count_int);
                        hashMap.put("max_count", count_int);
                        hashMap.put("hotel_id", hotelId);
                        hashMap.put("id", roomId);
                        hashMap.put("image", image_url);
                        hashMap.put("name", room_name);
                        hashMap.put("person", guests);
                        hashMap.put("price_per_night", price_int);
                        hashMap.put("room_type", selectedType);
                        hashMap.put("size", size);
                        hashMap.put("sound", sound);
                        hashMap.put("view", views);
                        hashMap.put("wifi", wifi);

                        try {
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection("room").add(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            alertView();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddNewRoomActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AddNewRoomActivity.this, "Error while adding room data", Toast.LENGTH_SHORT).show();
                        }


                    }



                }else{
                    Toast.makeText(AddNewRoomActivity.this,"Select room type",Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    public void alertView(){
        LayoutInflater inflater = LayoutInflater.from(AddNewRoomActivity.this);
        View view2 = inflater.inflate(R.layout.custom_alert, null);

        // Initialize views from layout
        TextView tvTitle = view2.findViewById(R.id.tvTitle);
        TextView tvMessage = view2.findViewById(R.id.tvMessage);
        Button btnOk = view2.findViewById(R.id.btnOk);

        // Ensure views are not null before setting text
        if (tvTitle != null) {
            tvTitle.setText("New Room Added Successfully");
        }
        if (tvMessage != null) {
            tvMessage.setText("Room has been added successfully. You can now view it in the listings");
        }
        if (btnOk != null) {
            btnOk.setText("Ok");
        }

        // Create and show dialog
        AlertDialog dialog1 = new AlertDialog.Builder(AddNewRoomActivity.this)
                .setView(view2)
                .setCancelable(false)  // Prevents dialog from closing when clicking outside
                .create();

        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawableResource(R.drawable.search_border_rounded);
        }

        dialog1.show();

        if (btnOk != null) {
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog1.dismiss();
                    Intent intent = new Intent(AddNewRoomActivity.this,ActivityDashboard.class);
                    startActivity(intent);

                }
            });
        }
    }


    public static String generateUniqueID() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000); // Ensures 5-digit number
        return "R" + number;
    }

    private void initCongif() {
        if (isMediaManagerInitialized) {
            return; // Avoid reinitializing if already initialized
        }

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dvijulv3l");
        config.put("api_key", "712223112513167");
        config.put("api_secret", "XD0JhYw-tisapOvTSdLWsyBtHbw");

        MediaManager.init(this, config);
        isMediaManagerInitialized = true; // Mark as initialized
    }


    private void requestPermission(){

        if(ContextCompat.checkSelfPermission(AddNewRoomActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            selecetImage();
        }else{
            ActivityCompat.requestPermissions(AddNewRoomActivity.this,new String[]{
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