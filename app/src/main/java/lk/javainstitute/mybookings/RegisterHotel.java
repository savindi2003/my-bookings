package lk.javainstitute.mybookings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import lk.javainstitute.mybookings.model.EatModel;
import lk.javainstitute.mybookings.model.EmailValidator;
import lk.javainstitute.mybookings.model.PlaceModel;
import lk.javainstitute.mybookings.model.Transport;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterHotel extends AppCompatActivity {

    private RecyclerView recyclerView1 , recyclerView2 , recyclerView3;
    private PlaceAdapter adapter;
    private EatAndDrinkAdapter adapter2;
    private TransportAdapter adapter3;
    private List<PlaceModel> placesList = new ArrayList<>();
    private List<EatModel> placesList2 = new ArrayList<>();
    private List<Transport> placesList3 = new ArrayList<>();
    private EditText editTextName, editTextDistance;
    private ImageView addButton , addButton2, addButton3;

    private EditText hotel_name_tv , hotel_address_tv , hotel_description_tv , hotel_price_tv , email_tv , mob1_tv , mob2_tv , fb_tv ,insta_tv ,  latitude_tv ,  longitude_tv,password1_tv,password2_tv ;

    private ArrayList<String> selectedAmenities = new ArrayList<>();
    private RadioGroup radioGroup;

    private ImageView imageView1, imageView2, imageView3;
    private Uri imagePath1, imagePath2, imagePath3;
    private int selectedImageIndex = 1;
    private static final int IMAGE_REQ = 1;

    private ArrayList<String> imageUrlList = new ArrayList<>();

    private int uploadedCount = 0;
    private int totalImages = 0;

    private List<String> allNames = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_hotel);

        //change the color of radio button
        RadioButton radio1 = findViewById(R.id.radioButton);
        radio1.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        RadioButton radio2 = findViewById(R.id.radioButton4);
        radio2.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        RadioButton radio3 = findViewById(R.id.radioButton8);
        radio3.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        RadioButton radio4 = findViewById(R.id.radioButton6);
        radio4.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        RadioButton radio5 = findViewById(R.id.radioButton7);
        radio5.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

        //add attraction place
        recyclerView1 = findViewById(R.id.r1);
        addButton = findViewById(R.id.imageView24);

        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceAdapter(placesList);
        recyclerView1.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            adapter.addEmptyItem();
        });

        //add eat and drink
        recyclerView2 = findViewById(R.id.r2);
        addButton2 = findViewById(R.id.imageView29); //23

        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new EatAndDrinkAdapter(placesList2);
        recyclerView2.setAdapter(adapter2);

        addButton2.setOnClickListener(v -> {
            adapter2.addEmptyItem();
        });

        //add transport
        recyclerView3 = findViewById(R.id.r3);
        addButton3 = findViewById(R.id.imageView23); //23

        recyclerView3.setLayoutManager(new LinearLayoutManager(this));
        adapter3 = new TransportAdapter(placesList3);
        recyclerView3.setAdapter(adapter3);

        addButton3.setOnClickListener(v -> {
            adapter3.addEmptyItem();
        });

        //image view

        imageView1 = findViewById(R.id.upload_image1);
        imageView2 = findViewById(R.id.upload_image2);
        imageView3 = findViewById(R.id.upload_image3);

        initConfig();

        imageView1.setOnClickListener(view -> {
            selectedImageIndex = 1;
            requestPermission();
        });

        imageView2.setOnClickListener(view -> {
            selectedImageIndex = 2;
            requestPermission();
        });

        imageView3.setOnClickListener(view -> {
            selectedImageIndex = 3;
            requestPermission();
        });

        //image view

        setupSwipeToDelete(recyclerView1, placesList, adapter);
        setupSwipeToDelete(recyclerView2, placesList2, adapter2);
        setupSwipeToDelete(recyclerView3, placesList3, adapter3);




        hotel_name_tv = findViewById(R.id.editTextText_name);
        hotel_address_tv = findViewById(R.id.editTextText_location);
        hotel_description_tv = findViewById(R.id.editTextText_description);
        hotel_price_tv = findViewById(R.id.editTextText_price);

        email_tv = findViewById(R.id.editTextText_email);
        mob1_tv = findViewById(R.id.editTextText_mob1);
        mob2_tv = findViewById(R.id.editTextText_mob2);

        fb_tv = findViewById(R.id.editTextText_fblink);
        insta_tv = findViewById(R.id.editTextText_insterlink);

        latitude_tv = findViewById(R.id.location_lat);
        longitude_tv = findViewById(R.id.location_long);

        password1_tv = findViewById(R.id.password1);
        password2_tv = findViewById(R.id.password2);

        TextView emailt = findViewById(R.id.textView76);
        emailt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        Button imgUploadBtn = findViewById(R.id.button21);
        imgUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImages();
            }
        });

        Button saveButton = findViewById(R.id.button20);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                CheckBox wifi = findViewById(R.id.checkBox3);
                CheckBox breakfast = findViewById(R.id.checkBox4);
                CheckBox spa = findViewById(R.id.checkBox5);
                CheckBox golf = findViewById(R.id.checkBox6);
                CheckBox pool = findViewById(R.id.checkBox7);
                CheckBox resturent = findViewById(R.id.checkBox8);
                CheckBox parking = findViewById(R.id.checkBox9);
                CheckBox gym = findViewById(R.id.checkBox10);
                CheckBox laundary = findViewById(R.id.checkBox11);
                CheckBox ac = findViewById(R.id.checkBox12);

                if (wifi.isChecked()) selectedAmenities.add("WiFi");
                if (breakfast.isChecked()) selectedAmenities.add("Breakfast");
                if (spa.isChecked()) selectedAmenities.add("Spa");
                if (golf.isChecked()) selectedAmenities.add("Mini golf park");
                if (pool.isChecked()) selectedAmenities.add("Indoor pool");
                if (resturent.isChecked()) selectedAmenities.add("Resturent");
                if (parking.isChecked()) selectedAmenities.add("Parking");
                if (gym.isChecked()) selectedAmenities.add("Gym");
                if (laundary.isChecked()) selectedAmenities.add("Laundry");
                if (ac.isChecked()) selectedAmenities.add("AC");

                Button imgUploadBtn = findViewById(R.id.button21);


                List<PlaceModel> attraction_places = adapter.getPlaceList(); // Get list from adapter

                List<EatModel> eat_places = adapter2.getPlaceList(); // Get list from adapter

                List<Transport> transport_places = adapter3.getPlaceList(); // Get list from adapter

                for (PlaceModel place : attraction_places) {
                    allNames.add(place.getName());  // Assuming getName() returns the name
                }

// Extract names from eat_places
                for (EatModel eat : eat_places) {
                    allNames.add(eat.getName());  // Assuming getName() returns the name
                }

// Extract names from transport_places
                for (Transport transport : transport_places) {
                    allNames.add(transport.getName());  // Assuming getName() returns the name
                }

                radioGroup = findViewById(R.id.radioGroup1);

                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId != -1) {

                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedType = selectedRadioButton.getText().toString();

                    String hotelId = generateUniqueID();
                    String verification = generateUniqueCode();

                    String hotelName = hotel_name_tv.getText().toString().trim();
                    String hotelAddress = hotel_address_tv.getText().toString().trim();
                    String hotelDescription = hotel_description_tv.getText().toString().trim();
                    String hotelPrice = hotel_price_tv.getText().toString().trim();

                    String email = email_tv.getText().toString().trim();
                    String mobile1 = mob1_tv.getText().toString().trim();
                    String mobile2 = mob2_tv.getText().toString().trim();

                    String facebookLink = fb_tv.getText().toString().trim();
                    String instagramLink = insta_tv.getText().toString().trim();

                    String latitude_sring = latitude_tv.getText().toString().trim();
                    String longitude_sring = longitude_tv.getText().toString().trim();

                    String password = password1_tv.getText().toString().trim();
                    String re_password = password2_tv.getText().toString().trim();

                    double latitude = Double.parseDouble(latitude_sring);
                    double longitude = Double.parseDouble(longitude_sring);

                    GeoPoint hotelLocation = new GeoPoint(latitude, longitude);

                    if(hotelName.isEmpty()){
                        hotel_name_tv.setError("Please enter hotel name");
                    }else if(hotelAddress.isEmpty()){
                        hotel_address_tv.setError("Please enter hotel address");
                    }else if(hotelDescription.isEmpty()){
                        hotel_description_tv.setError("Please enter small description about your hotel");
                    }else if(hotelPrice.isEmpty()){
                        hotel_price_tv.setError("Please enter avarage hotel price per night");
                    }else if(selectedAmenities.isEmpty()){
                        Toast.makeText(RegisterHotel.this, "amenities no", Toast.LENGTH_SHORT).show();
                    }else if(email.isEmpty()){
                        email_tv.setError("Please enter email");
                    }else if(!EmailValidator.isValidEmail(email)){
                        email_tv.setError("Please enter valid email address");
                    }else if(mobile1.isEmpty()){
                        mob1_tv.setError("Please enter mobile number");
                    }else if(mobile2.isEmpty()){
                        mob2_tv.setError("Please enter mobile number");
                    }else if(imageUrlList.isEmpty()){
                        Toast.makeText(RegisterHotel.this, "image no", Toast.LENGTH_SHORT).show();
                    }else if(attraction_places.isEmpty()) {
                        Toast.makeText(RegisterHotel.this, "attraction_places no", Toast.LENGTH_SHORT).show();
                    }else if(eat_places.isEmpty()) {
                        Toast.makeText(RegisterHotel.this, "eat_places no", Toast.LENGTH_SHORT).show();
                    }else if(transport_places.isEmpty()) {
                        Toast.makeText(RegisterHotel.this, "transport_places no", Toast.LENGTH_SHORT).show();
                    }else if(latitude_sring.isEmpty()) {
                        latitude_tv.setError("Please set latitude for your location");
                    }else if(longitude_sring.isEmpty()) {
                        longitude_tv.setError("Please set longitude  for your location");
                    }else if(password.isEmpty()) {
                        password1_tv.setError("Please enter your password");
                    }else if(re_password.isEmpty()) {
                        password2_tv.setError("Please re enter your password");
                    }else if(!password.equals(re_password)){
                        password1_tv.setError("password not match");
                        password2_tv.setError("password not match");
                    }else {

                        firestore.collection("users")
                                .whereEqualTo("email", email)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        Toast.makeText(RegisterHotel.this, "Email already registered!", Toast.LENGTH_SHORT).show();
                                    } else {

                                        //

                                        String priceString = hotelPrice;
                                        double price = 0;
                                        try {
                                            price = Double.parseDouble(priceString);

                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        }

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("id", hotelId);
                                        hashMap.put("type", selectedType);
                                        hashMap.put("name", hotelName);
                                        hashMap.put("location", hotelAddress);
                                        hashMap.put("description", hotelDescription);
                                        hashMap.put("price", price);
                                        hashMap.put("amenities", selectedAmenities);
                                        hashMap.put("email", email);
                                        hashMap.put("contact_no1", mobile1);
                                        hashMap.put("contact_no2", mobile2);
                                        hashMap.put("contact_no2", mobile2);
                                        hashMap.put("fb_link", facebookLink);
                                        hashMap.put("insta_link", instagramLink);
                                        hashMap.put("attraction_p", attraction_places);
                                        hashMap.put("eat_drink_p", eat_places);
                                        hashMap.put("transport_p", transport_places);
                                        hashMap.put("image", imageUrlList);
                                        hashMap.put("ratings", "0");
                                        hashMap.put("registeredDate", FieldValue.serverTimestamp());
                                        hashMap.put("map_location", hotelLocation);
                                        hashMap.put("password", password);
                                        hashMap.put("verification_code",verification);
                                        hashMap.put("valid",false);
                                        hashMap.put("landmarks",allNames);

                                        firestore.collection("hotel").add(hashMap)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {

                                                        alertView(email,verification);


                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RegisterHotel.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                        //

                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegisterHotel.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });


                    }

                } else {

                    Toast.makeText(RegisterHotel.this, "Selecte the hotel type", Toast.LENGTH_SHORT).show();
                }





            }
        });



    }

    public void saveLocation(String name , double latitute,double longitude){
        SQLiteHelper sqLiteHelper = new SQLiteHelper(RegisterHotel.this,"mybookings.db",null,1);

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                sqLiteDatabase.execSQL("INSERT INTO `location` (name, latitude, longitude) VALUES (name,latitude,longitude) ");

            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQ && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            switch (selectedImageIndex) {
                case 1:
                    imagePath1 = selectedImageUri;
                    Picasso.get().load(imagePath1).into(imageView1);
                    break;
                case 2:
                    imagePath2 = selectedImageUri;
                    Picasso.get().load(imagePath2).into(imageView2);
                    break;
                case 3:
                    imagePath3 = selectedImageUri;
                    Picasso.get().load(imagePath3).into(imageView3);
                    break;
            }
        } else {
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void emailVerification(String email , String code){

        new Thread(new Runnable() {
            @Override
            public void run() {

                Gson gson = new Gson();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("email",email);
                jsonObject.addProperty("code",code);

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(gson.toJson(jsonObject), MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url("https://24a7-2402-4000-2100-b01f-406c-319-7712-da77.ngrok-free.app/MyBookingWeb/SendEmail")
                        .post(requestBody)
                        .build();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    String responseText = response.body().string();


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();


    }

    public void alertView(String email , String code){
        LayoutInflater inflater = LayoutInflater.from(RegisterHotel.this);
        View view2 = inflater.inflate(R.layout.custom_alert, null);

        // Initialize views from layout
        TextView tvTitle = view2.findViewById(R.id.tvTitle);
        TextView tvMessage = view2.findViewById(R.id.tvMessage);
        Button btnOk = view2.findViewById(R.id.btnOk);

        btnOk.setText("Log in");
        tvTitle.setText("Partner Account Create Successfully");
        tvMessage.setText("Your hotel has been added successfully. We send verification code to your email. Please check and Log in to your account now");

        // Create and show dialog
        AlertDialog dialog1 = new AlertDialog.Builder(RegisterHotel.this)
                .setView(view2)
                .setCancelable(false)  // Prevents dialog from closing when clicking outside
                .create();

        if (dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawableResource(R.drawable.search_border_rounded);
        }

        dialog1.show();


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailVerification(email,code);
                dialog1.dismiss();
                Intent intent = new Intent(RegisterHotel.this,SignInActivity.class);
                startActivity(intent);

            }
        });
    }

    private void uploadImages() {
        uploadedCount = 0;
        totalImages = 0;

        if (imagePath1 != null) totalImages++;
        if (imagePath2 != null) totalImages++;
        if (imagePath3 != null) totalImages++;

        if (imagePath1 != null) {
            uploadToCloudinary(imagePath1, "Image 1");
        }
        if (imagePath2 != null) {
            uploadToCloudinary(imagePath2, "Image 2");
        }
        if (imagePath3 != null) {
            uploadToCloudinary(imagePath3, "Image 3");
        }
    }

    private void uploadToCloudinary(Uri imageUri, String imageName) {
        MediaManager.get().upload(imageUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.e("Cloudinary Upload", imageName + " - OnStart");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.e("Cloudinary Upload", imageName + " - On Progress");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {

                        Log.e("Cloudinary Upload", imageName + " - On Success: " +resultData.get("secure_url"));
                        Toast.makeText(RegisterHotel.this, imageName + " Uploaded!", Toast.LENGTH_SHORT).show();

                        imageUrlList.add(String.valueOf(resultData.get("secure_url")));

                        uploadedCount++;

                        if (uploadedCount == totalImages) {
                            Log.i("ImageArray", imageUrlList.toString());
                        }
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("Cloudinary Upload", imageName + " - On Error: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.e("Cloudinary Upload", imageName + " - On Reschedule");
                    }
                }).dispatch();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQ);
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQ);
    }

    private void initConfig() {
        Map config = new HashMap();

        config.put("cloud_name", "dvijulv3l");
        config.put("api_key", "712223112513167");
        config.put("api_secret", "XD0JhYw-tisapOvTSdLWsyBtHbw");

        //config.put("secure", true);
        MediaManager.init(this, config);
    }

    public void getAttractionPlaces() {
        List<PlaceModel> places = adapter.getPlaceList(); // Get list from adapter

        for (PlaceModel place : places) {
            Log.i("MainActivity99", "Place: " + place.getName() + " - " + place.getDistance());
        }


    }

    public void getEatAndDrinkPlaces() {
        List<EatModel> places = adapter2.getPlaceList(); // Get list from adapter

        for (EatModel place : places) {
            Log.i("MainActivity99", "Place: " + place.getName() + " - " + place.getDistance());
        }


    }

    public void getTransportService() {
        List<Transport> places = adapter3.getPlaceList(); // Get list from adapter

        for (Transport place : places) {
            Log.i("MainActivity99", "Place: " + place.getName() + " - " + place.getDistance());
        }


    }

    private void setupSwipeToDelete(RecyclerView recyclerView, List<?> list, RecyclerView.Adapter adapter) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                list.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public static String generateUniqueID() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000); // Ensures 5-digit number
        return "H" + number;
    }

    public static String generateUniqueCode() {
        Random random = new Random();
        int number = 10000 + random.nextInt(900000); // Ensures 6-digit number
        return "MB" + number;
    }

}