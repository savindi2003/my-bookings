package lk.javainstitute.mybookings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.javainstitute.mybookings.model.Attraction;
import lk.javainstitute.mybookings.model.EatModel;
import lk.javainstitute.mybookings.model.EmailValidator;
import lk.javainstitute.mybookings.model.PlaceModel;
import lk.javainstitute.mybookings.model.Transport;

public class Fragment_Admin2 extends Fragment {

    private EditText hotel_name_tv , hotel_address_tv , hotel_description_tv , hotel_price_tv , email_tv , mob1_tv , mob2_tv , fb_tv ,insta_tv ,  latitude_tv ,  longitude_tv,password1_tv,password2_tv ;
    private ImageView imageView1, imageView2, imageView3;

    private Uri imagePath1, imagePath2, imagePath3;
    private int selectedImageIndex = 1;
    private static final int IMAGE_REQ = 1;

    private ArrayList<String> imageUrlList = new ArrayList<>();

    private int uploadedCount = 0;
    private int totalImages = 0;
    private RadioGroup radioGroup;

    private List<PlaceModel> placesList = new ArrayList<>();
    private List<EatModel> placesList2 = new ArrayList<>();
    private List<Transport> placesList3 = new ArrayList<>();

    private ImageView addButton , addButton2, addButton3;

    private ArrayList<String> selectedAmenities = new ArrayList<>();

    private PlaceAdapter adapter;
    private EatAndDrinkAdapter adapter2;
    private TransportAdapter adapter3;

    private List<String> imageUrls;

    private boolean isMediaManagerInitialized = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_admin2,container,false);
        View view = inflater.inflate(R.layout.fragment_admin2, container, false);

        hotel_name_tv = view.findViewById(R.id.editTextText_name);
        hotel_address_tv = view.findViewById(R.id.editTextText_location);
        hotel_description_tv = view.findViewById(R.id.editTextText_description);
        hotel_price_tv = view.findViewById(R.id.editTextText_price);

        email_tv = view.findViewById(R.id.editTextText_email);
        mob1_tv = view.findViewById(R.id.editTextText_mob1);
        mob2_tv = view.findViewById(R.id.editTextText_mob2);

        fb_tv = view.findViewById(R.id.editTextText_fblink);
        insta_tv = view.findViewById(R.id.editTextText_insterlink);

        latitude_tv = view.findViewById(R.id.location_lat);
        longitude_tv = view.findViewById(R.id.location_long);

        imageView1 = view.findViewById(R.id.imageView22);
        imageView2 = view.findViewById(R.id.imageView25);
        imageView3 = view.findViewById(R.id.imageView26);

        //initCongif();

        CheckBox wifi = view.findViewById(R.id.checkBox3);
        CheckBox breakfast = view.findViewById(R.id.checkBox4);
        CheckBox spa = view.findViewById(R.id.checkBox5);
        CheckBox golf = view.findViewById(R.id.checkBox6);
        CheckBox pool = view.findViewById(R.id.checkBox7);
        CheckBox resturent = view.findViewById(R.id.checkBox8);
        CheckBox parking = view.findViewById(R.id.checkBox9);
        CheckBox gym = view.findViewById(R.id.checkBox10);
        CheckBox laundary = view.findViewById(R.id.checkBox11);
        CheckBox ac = view.findViewById(R.id.checkBox12);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
        String email_h = sharedPref.getString("hotel", null);

        if(email_h != null){

            RecyclerView recyclerView = view.findViewById(R.id.r1);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            List<PlaceModel> attractionList = new ArrayList<>();
            adapter = new PlaceAdapter(attractionList);

            recyclerView.setAdapter(adapter);

            addButton = view.findViewById(R.id.imageView24);
            addButton.setOnClickListener(v -> {
                adapter.addEmptyItem();
            });

            RecyclerView recyclerView2 = view.findViewById(R.id.r2);
            recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
            List<EatModel> attractionList2 = new ArrayList<>();
            adapter2 = new EatAndDrinkAdapter(attractionList2);
            recyclerView2.setAdapter(adapter2);

            addButton2 = view.findViewById(R.id.imageView29);
            addButton2.setOnClickListener(v -> {
                adapter2.addEmptyItem();
            });

            RecyclerView recyclerView3 = view.findViewById(R.id.r3);
            recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
            List<Transport> attractionList3 = new ArrayList<>();
            adapter3 = new TransportAdapter(attractionList3);
            recyclerView3.setAdapter(adapter3);

            addButton3 = view.findViewById(R.id.imageView23);
            addButton3.setOnClickListener(v -> {
                adapter3.addEmptyItem();
            });


            setupSwipeToDelete(recyclerView, attractionList, adapter);
            setupSwipeToDelete(recyclerView2, attractionList2, adapter2);
            setupSwipeToDelete(recyclerView3, attractionList3, adapter3);



            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Fetch user data from Firestore
            firestore.collection("hotel")
                    .whereEqualTo("email", email_h)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.e("Firestore", "Error getting documents: ", error);
                                return;
                            }

                            if (value != null && !value.isEmpty()) {
                                DocumentSnapshot document = value.getDocuments().get(0);

                                // Fetch user details from the document
                                String hotel_type = document.getString("type");

                                String hotel_name = document.getString("name");
                                String hotel_address = document.getString("location");
                                String hotel_description = document.getString("description");
                                Double priceObj = document.getDouble("price");

                                String email = document.getString("email");
                                String mob1 = document.getString("contact_no1");
                                String mob2 = document.getString("contact_no2");

                                String fb = document.getString("fb_link");
                                String insta = document.getString("insta_link");

                                List<String> amenities = (List<String>) document.get("amenities");
                                imageUrls = (List<String>) document.get("image");

                                List<Map<String, Object>> attractions = (List<Map<String, Object>>) document.get("attraction_p");
                                List<Map<String, Object>> eat_drink = (List<Map<String, Object>>) document.get("eat_drink_p");
                                List<Map<String, Object>> transport = (List<Map<String, Object>>) document.get("transport_p");

                                GeoPoint mapLocation = document.getGeoPoint("map_location");
                                double latitude = mapLocation.getLatitude();
                                double longitude = mapLocation.getLongitude();


                                double price = priceObj;

                                //set text fields
                                hotel_name_tv.setText(hotel_name);
                                hotel_address_tv.setText(hotel_address);
                                hotel_description_tv.setText(hotel_description);
                                hotel_price_tv.setText(String.valueOf(price));
                                email_tv.setText(email);
                                mob1_tv.setText(mob1);
                                mob2_tv.setText(mob2);
                                fb_tv.setText(fb);
                                insta_tv.setText(insta);
                                latitude_tv.setText(String.valueOf(latitude));
                                longitude_tv.setText((String.valueOf(longitude)));

                                RadioButton radio1 = view.findViewById(R.id.radioButton);
                                radio1.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                                radio1.setText(hotel_type);
                                radio1.setChecked(true);

                                for (String amenity : amenities) {
                                    if(amenity.equals("WiFi")){
                                        wifi.setChecked(true);
                                    }
                                    if(amenity.equals("Breakfast")){
                                        breakfast.setChecked(true);
                                    }
                                    if(amenity.equals("Spa")){
                                        spa.setChecked(true);
                                    }
                                    if(amenity.equals("Mini golf park")){
                                        golf.setChecked(true);
                                    }
                                    if(amenity.equals("Indoor pool")){
                                        pool.setChecked(true);
                                    }
                                    if(amenity.equals("Resturent")){
                                        resturent.setChecked(true);
                                    }
                                    if(amenity.equals("Parking")){
                                        parking.setChecked(true);
                                    }
                                    if(amenity.equals("Gym")){
                                        gym.setChecked(true);
                                    }
                                    if(amenity.equals("Laundry")){
                                        laundary.setChecked(true);
                                    }
                                    if(amenity.equals("AC")){
                                        ac.setChecked(true);
                                    }

                                }

                                imageView1.setOnClickListener(v -> openImagePicker(1));
                                imageView2.setOnClickListener(v -> openImagePicker(2));
                                imageView3.setOnClickListener(v -> openImagePicker(3));

                                if (imageUrls != null && imageUrls.size() == 3) {
                                    Picasso.get().load(imageUrls.get(0)).into(imageView1);
                                    Picasso.get().load(imageUrls.get(1)).into(imageView2);
                                    Picasso.get().load(imageUrls.get(2)).into(imageView3);
                                }




                                if (attractions != null) {
                                    for (Map<String, Object> map : attractions) {
                                        String name = map.get("name").toString();
                                        String distance = map.get("distance").toString();
                                        attractionList.add(new PlaceModel(name, distance));
                                    }
                                    adapter.notifyDataSetChanged();
                                }

                                if (eat_drink != null) {
                                    for (Map<String, Object> map : eat_drink) {
                                        String name = map.get("name").toString();
                                        String distance = map.get("distance").toString();
                                        attractionList2.add(new EatModel(name, distance));
                                    }
                                    adapter2.notifyDataSetChanged();
                                }

                                if (transport != null) {
                                    for (Map<String, Object> map : transport) {
                                        String name = map.get("name").toString();
                                        String distance = map.get("distance").toString();
                                        attractionList3.add(new Transport(name, distance));
                                    }
                                    adapter3.notifyDataSetChanged();
                                }


                                Button imgUploadBtn = view.findViewById(R.id.button21);
                                imgUploadBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        uploadImages();
                                    }
                                });



                            } else {
                                Log.d("Firestore", "No document found for email: " + email_h);
                            }
                        }
                    });

        }else{
            Toast.makeText(getContext(),"email not found",Toast.LENGTH_LONG).show();
        }


        Button updateButton = view.findViewById(R.id.button20);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                List<PlaceModel> attraction_places = adapter.getPlaceList();

                List<EatModel> eat_places = adapter2.getPlaceList();

                List<Transport> transport_places = adapter3.getPlaceList();

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
                    Toast.makeText(getContext(), "amenities no", Toast.LENGTH_SHORT).show();
                }else if(email.isEmpty()){
                    email_tv.setError("Please enter email");
                }else if(!EmailValidator.isValidEmail(email)){
                    email_tv.setError("Please enter valid email address");
                }else if(mobile1.isEmpty()){
                    mob1_tv.setError("Please enter mobile number");
                }else if(mobile2.isEmpty()){
                    mob2_tv.setError("Please enter mobile number");
                }else if(attraction_places.isEmpty()) {
                    Toast.makeText(getContext(), "attraction_places no", Toast.LENGTH_SHORT).show();
                }else if(eat_places.isEmpty()) {
                    Toast.makeText(getContext(), "eat_places no", Toast.LENGTH_SHORT).show();
                }else if(transport_places.isEmpty()) {
                    Toast.makeText(getContext(), "transport_places no", Toast.LENGTH_SHORT).show();
                }else if(latitude_sring.isEmpty()) {
                    latitude_tv.setError("Please set latitude for your location");
                }else if(longitude_sring.isEmpty()) {
                    longitude_tv.setError("Please set longitude  for your location");
                }else {

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    firestore.collection("hotel")
                            .whereEqualTo("email", email_h) // Search by email
                            .get()
                            .addOnCompleteListener(task -> {

                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    // Get the document ID of the existing hotel
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                    String documentId = document.getId();

                                    // Convert price string to double safely
                                    double price = 0;
                                    try {
                                        price = Double.parseDouble(hotelPrice);
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

                                    // Create update data
                                    HashMap<String, Object> updateMap = new HashMap<>();
                                    updateMap.put("name", hotelName);
                                    updateMap.put("location", hotelAddress);
                                    updateMap.put("description", hotelDescription);
                                    updateMap.put("price", price);
                                    updateMap.put("amenities", selectedAmenities);
                                    updateMap.put("contact_no1", mobile1);
                                    updateMap.put("contact_no2", mobile2);
                                    updateMap.put("fb_link", facebookLink);
                                    updateMap.put("insta_link", instagramLink);
                                    updateMap.put("attraction_p", attraction_places);
                                    updateMap.put("eat_drink_p", eat_places);
                                    updateMap.put("transport_p", transport_places);
                                    updateMap.put("map_location", hotelLocation);

                                    if(imageUrlList!=null){
                                        updateMap.put("image", imageUrlList);
                                    }else{
                                        updateMap.put("image", imageUrls);
                                    }

                                    // Ensure this is a valid GeoPoint

                                    // Perform the update
                                    firestore.collection("hotel")
                                            .document(documentId)
                                            .update(updateMap)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(getContext(), "Hotel details updated!", Toast.LENGTH_SHORT).show();
                                                alertView();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });

                                }

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });



                }

                }
        });


        return view;
    }

    public void alertView(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view2 = inflater.inflate(R.layout.custom_alert, null);

        // Initialize views from layout
        TextView tvTitle = view2.findViewById(R.id.tvTitle);
        TextView tvMessage = view2.findViewById(R.id.tvMessage);
        Button btnOk = view2.findViewById(R.id.btnOk);

        btnOk.setText("Ok");
        tvTitle.setText("Partner Account Updated Successfully");
        tvMessage.setText("Your hotel info has been updated successfully. You can now view it in the listings.");

        // Create and show dialog
        AlertDialog dialog1 = new AlertDialog.Builder(getContext())
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
                dialog1.dismiss();

            }
        });
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        handleImageSelection(selectedImageUri);
                    }
                } else {
                    Toast.makeText(getContext(), "Image selection failed", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void handleImageSelection(Uri selectedImageUri) {
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
    }

    private void openImagePicker(int index) {
        selectedImageIndex = index; // Store which image is being selected
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
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
                        Toast.makeText(getContext(), imageName + " Uploaded!", Toast.LENGTH_SHORT).show();

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
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQ);
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQ);
    }

    private void initCongif() {
        if (isMediaManagerInitialized) {
            return; // Avoid reinitializing if already initialized
        }

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dvijulv3l");
        config.put("api_key", "712223112513167");
        config.put("api_secret", "XD0JhYw-tisapOvTSdLWsyBtHbw");

        MediaManager.init(getContext(), config);
        isMediaManagerInitialized = true; // Mark as initialized
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
}
