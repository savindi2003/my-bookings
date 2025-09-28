package lk.javainstitute.mybookings;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lk.javainstitute.mybookings.model.Amentiy;
import lk.javainstitute.mybookings.model.Review;

public class SingleViewActivity extends AppCompatActivity {
    private Runnable runnable;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int currentPage = 0;
    ArrayList<String> imageUrls;
    List<Integer> images;
    ViewPager2 viewPager;

    private RecyclerView amenityRecyclerView;
    private AmenityAdapter amenityAdapter;
    private List<Amentiy> amenityList;
    private TabLayout tabLayout;
    private ViewPager2 viewPagerF;
    private RecyclerView review_recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    TextView checkouttext;
    TextView checkintext;

    private String id,userEmail;

    String name;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color1));

        Intent intent = getIntent();
        name = intent.getStringExtra("hotel_name");
        String location = intent.getStringExtra("hotel_location");
        String description = intent.getStringExtra("hotel_description");
        String price = intent.getStringExtra("hotel_price");
        id = intent.getStringExtra("hotel_id");
        float rating = intent.getFloatExtra("hotel_rating", 0);
        String chechIn = intent.getStringExtra("CHECK_IN_DATE");
        String chechOut = intent.getStringExtra("CHECK_OUT_DATE");

        //Toast.makeText(SingleViewActivity.this,price+" "+rating,Toast.LENGTH_LONG).show();

        SharedPreferences prefs = SingleViewActivity.this.getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
        prefs.edit().putString("selected_hotel_id", id).apply();

        SharedPreferences sharedPref = getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
        userEmail = sharedPref.getString("userEmail",null);

        //load map
        loadMap();

        //add to fav
        searchFavourites();

        ImageView heart = findViewById(R.id.imageView4);
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavourite();
            }
        });

        TextView mob1 = findViewById(R.id.textView178);
        String mobile_no1 = mob1.getText().toString();

        mob1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent call_intent1 = new Intent(Intent.ACTION_DIAL);
                Uri uri = Uri.parse("tel:"+mobile_no1);
                call_intent1.setData(uri);
                startActivity(call_intent1);
            }
        });

        TextView mob2 = findViewById(R.id.textView179);
        String mobile_no2 = mob1.getText().toString();

        mob2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent call_intent2 = new Intent(Intent.ACTION_DIAL);
                Uri uri2 = Uri.parse("tel:"+mobile_no2);
                call_intent2.setData(uri2);
                startActivity(call_intent2);
            }
        });


        ArrayList<String> amenities = intent.getStringArrayListExtra("hotel_amenities");


        TextView hotel_name = findViewById(R.id.textView15);
        TextView hotel_address = findViewById(R.id.textView16);
        TextView hotel_rating = findViewById(R.id.textView18);
        TextView hotel_description = findViewById(R.id.textView17);

        hotel_name.setText(name);
        hotel_address.setText(location);
        hotel_rating.setText(String.valueOf(rating));
        hotel_description.setText(description);

        //date
        checkintext = findViewById(R.id.check_in_text_date);
        checkouttext = findViewById(R.id.check_out_text_date);

        checkintext.setText(chechIn);
        checkouttext.setText(chechOut);

        checkintext.setOnClickListener(view -> showStartDatePicker());
        checkouttext.setOnClickListener(view -> showEndOnlyDatePicker());
        //date


        ImageView rating_star1 = findViewById(R.id.imageView11);
        ImageView rating_star2 = findViewById(R.id.imageView12);
        ImageView rating_star3 = findViewById(R.id.imageView13);
        ImageView rating_star4 = findViewById(R.id.imageView16);
        ImageView rating_star5 = findViewById(R.id.imageView17);

        //rating start

        if(rating == 0.5){
            rating_star1.setImageResource(R.drawable.start_half);
            rating_star2.setImageResource(R.drawable.start_empty);
            rating_star3.setImageResource(R.drawable.start_empty);
            rating_star4.setImageResource(R.drawable.start_empty);
            rating_star5.setImageResource(R.drawable.start_empty);
        }else if(rating == 1){
            rating_star1.setImageResource(R.drawable.star);
            rating_star2.setImageResource(R.drawable.start_empty);
            rating_star3.setImageResource(R.drawable.start_empty);
            rating_star4.setImageResource(R.drawable.start_empty);
            rating_star5.setImageResource(R.drawable.start_empty);
        }else if(rating == 1.5){
            rating_star1.setImageResource(R.drawable.star);
            rating_star2.setImageResource(R.drawable.start_half);
            rating_star3.setImageResource(R.drawable.start_empty);
            rating_star4.setImageResource(R.drawable.start_empty);
            rating_star5.setImageResource(R.drawable.start_empty);
        }else if(rating == 2){
            rating_star1.setImageResource(R.drawable.star);
            rating_star2.setImageResource(R.drawable.star);
            rating_star3.setImageResource(R.drawable.start_empty);
            rating_star4.setImageResource(R.drawable.start_empty);
            rating_star5.setImageResource(R.drawable.start_empty);
        }else if(rating == 2.5){
            rating_star1.setImageResource(R.drawable.star);
            rating_star2.setImageResource(R.drawable.star);
            rating_star3.setImageResource(R.drawable.start_half);
            rating_star4.setImageResource(R.drawable.start_empty);
            rating_star5.setImageResource(R.drawable.start_empty);
        }else if(rating == 3){
            rating_star1.setImageResource(R.drawable.star);
            rating_star2.setImageResource(R.drawable.star);
            rating_star3.setImageResource(R.drawable.star);
            rating_star4.setImageResource(R.drawable.start_empty);
            rating_star5.setImageResource(R.drawable.start_empty);
        }else if(rating == 3.5){
            rating_star1.setImageResource(R.drawable.star);
            rating_star2.setImageResource(R.drawable.star);
            rating_star3.setImageResource(R.drawable.star);
            rating_star4.setImageResource(R.drawable.start_half);
            rating_star5.setImageResource(R.drawable.start_empty);
        }else if(rating == 4){
            rating_star1.setImageResource(R.drawable.star);
            rating_star2.setImageResource(R.drawable.star);
            rating_star3.setImageResource(R.drawable.star);
            rating_star4.setImageResource(R.drawable.star);
            rating_star5.setImageResource(R.drawable.start_empty);
        }else if(rating == 4.5){
            rating_star1.setImageResource(R.drawable.star);
            rating_star2.setImageResource(R.drawable.star);
            rating_star3.setImageResource(R.drawable.star);
            rating_star4.setImageResource(R.drawable.star);
            rating_star5.setImageResource(R.drawable.start_half);
        }else if(rating == 5){
            rating_star1.setImageResource(R.drawable.star);
            rating_star2.setImageResource(R.drawable.star);
            rating_star3.setImageResource(R.drawable.star);
            rating_star4.setImageResource(R.drawable.star);
            rating_star5.setImageResource(R.drawable.star);
        }

        //rating start



        viewPager = findViewById(R.id.imageViewPager);

        ///////////////////////////////////////////////////////////////////////////////////////////////////
        imageUrls = intent.getStringArrayListExtra("hotel_images");

        if (imageUrls != null && !imageUrls.isEmpty()) {
            HotelImageAdapter adapter = new HotelImageAdapter(this, imageUrls);
            viewPager.setAdapter(adapter);
            startAutoSlide();
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////


        //Amentiy

        amenityRecyclerView = findViewById(R.id.amenity_recycler_view);
        amenityRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));



        amenityList = new ArrayList<>();
        if (amenities != null) {
            for (String amenityName : amenities) {
                int iconResId = getAmenityIcon(amenityName);
                amenityList.add(new Amentiy(iconResId, amenityName));
            }
        }


        amenityAdapter = new AmenityAdapter(this, amenityList);
        amenityRecyclerView.setAdapter(amenityAdapter);

        //Amentiy


        //tab
        tabLayout = findViewById(R.id.tabLayout);
        viewPagerF = findViewById(R.id.viewPager2);
        viewPagerF.setAdapter(new ViewPagerAdapter(this));
        viewPagerF.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayout, viewPagerF, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Attractions"); break;
                case 1: tab.setText("Eat and Drink"); break;
                case 2: tab.setText("Transport"); break;
            }
        }).attach();
        //tab

        //review

        review_recyclerView = findViewById(R.id.review_recycler_view);
        review_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        review_recyclerView.setNestedScrollingEnabled(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reviewList = new ArrayList<>();

        db.collection("reviews").whereEqualTo("hotel_id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Task<QuerySnapshot>> userTasks = new ArrayList<>();

                        for (DocumentSnapshot doc : task.getResult()) {
                            String userEmail = doc.getString("user_email");
                            String reviewText = doc.getString("review");
                            Timestamp timestamp = doc.getTimestamp("date");
                            String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(timestamp.toDate());

                            // 🔥 Fetch user details from 'users' collection
                            Task<QuerySnapshot> userQuery = db.collection("users")
                                    .whereEqualTo("email", userEmail)
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener(userTask -> {
                                        if (!userTask.isEmpty()) {
                                            DocumentSnapshot userDoc = userTask.getDocuments().get(0);
                                            String firstName = userDoc.getString("first_name");
                                            String lastName = userDoc.getString("last_name");
                                            String profilePicUrl = userDoc.getString("image");

                                            String userName = firstName + " " + lastName;


                                            reviewList.add(new Review(userName, reviewText, formattedDate, profilePicUrl));
                                        }
                                    });

                            userTasks.add(userQuery);
                        }


                        Tasks.whenAllSuccess(userTasks).addOnSuccessListener(results -> {
                            reviewAdapter = new ReviewAdapter(SingleViewActivity.this,reviewList);
                            review_recyclerView.setAdapter(reviewAdapter);
                            reviewAdapter.notifyDataSetChanged(); // Update UI
                        });
                    } else {
                        Log.e("Firestore", "No reviews found or query failed", task.getException());
                    }
                });


        //review

        ToggleButton toggleButton = findViewById(R.id.toggleButton);
        RecyclerView review_recyclerView = findViewById(R.id.review_recycler_view);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ViewGroup.LayoutParams params = review_recyclerView.getLayoutParams();

            if (isChecked) {
                // Set height to WRAP_CONTENT
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else {
                // Set height to 450dp
                params.height = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 460, review_recyclerView.getResources().getDisplayMetrics());
            }

            review_recyclerView.setLayoutParams(params);
        });

        Button button_room = findViewById(R.id.room_select_button);
        button_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String rating_string = String.valueOf(rating);

                Intent intent = new Intent(SingleViewActivity.this, RoomActivity.class);
                intent.putExtra("hotel_name", name);
                intent.putExtra("hotel_location", location);
                intent.putExtra("hotel_id", id);
                intent.putExtra("hotel_price", price);
                intent.putExtra("hotel_rating", rating_string);
                intent.putExtra("check_in_date", chechIn);
                intent.putExtra("check_out_date", chechOut);
                startActivity(intent);
            }
        });



    }

    private void toggleFavourite() {
        String hotel_id = id; // Hotel ID
        SharedPreferences sharedPref = SingleViewActivity.this.getSharedPreferences("lk.javainstitute.mybookings.data",Context.MODE_PRIVATE);
        String user_email = sharedPref.getString("userEmail",null);// User Email

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ImageView heart = findViewById(R.id.imageView4); // ImageView reference

        // Reference to Firestore Collection
        CollectionReference favCollection = firestore.collection("favourite");

        // Query to check if the favorite document exists
        favCollection.whereEqualTo("hotel_id", hotel_id)
                .whereEqualTo("user_email", user_email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Document exists -> DELETE IT
                            for (DocumentSnapshot document : task.getResult()) {
                                favCollection.document(document.getId()).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            heart.setImageResource(R.drawable.heart_unfill); // Unfilled heart image

                                        })
                                        .addOnFailureListener(e -> Log.e("Firestore", "Error deleting document", e));
                            }
                        } else {
                            // Document does not exist -> CREATE IT
                            Map<String, Object> favData = new HashMap<>();
                            favData.put("hotel_id", hotel_id);
                            favData.put("user_email", user_email);

                            favCollection.add(favData)
                                    .addOnSuccessListener(documentReference -> {
                                        heart.setImageResource(R.drawable.heart_fill); // Filled heart image

                                    })
                                    .addOnFailureListener(e -> Log.e("Firestore", "Error adding document", e));
                        }
                    } else {
                        Log.e("Firestore", "Error checking document", task.getException());
                    }
                });
    }

    private void loadMap(){
        String hotel_id = id;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("hotel")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            GeoPoint geoPoint = document.getGeoPoint("map_location");

                            String mob1 = document.getString("contact_no1");
                            String mob2 = document.getString("contact_no2");
                            String email = document.getString("email");
                            String fb_link = document.getString("fb_link");
                            String insta_link = document.getString("insta_link");

                            if (geoPoint != null) {
                                double latitude = geoPoint.getLatitude();
                                double longitude = geoPoint.getLongitude();


                                //implement map

                                SupportMapFragment supportMapFragment = new SupportMapFragment();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.add(R.id.frameLayout_map_single,supportMapFragment);
                                fragmentTransaction.commit();

                                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(@NonNull GoogleMap googleMap) {
                                        LatLng latLng = new LatLng(latitude, longitude);

                                        googleMap.animateCamera(
                                                CameraUpdateFactory.newCameraPosition(
                                                        new CameraPosition.Builder()
                                                                .target(latLng)
                                                                .zoom(10)
                                                                .build()

                                                )
                                        );

                                        googleMap.addMarker(
                                                new MarkerOptions().position(latLng)
                                                        .title(name)
                                                        .icon(BitmapDescriptorFactory.defaultMarker())
                                        );
                                    }
                                });

                                //implement map


                            }

                            TextView mob1_t = findViewById(R.id.textView178);
                            TextView mob2_t = findViewById(R.id.textView179);
                            TextView email_t = findViewById(R.id.textView180);

                            mob1_t.setText("  "+mob1);
                            mob2_t.setText("  "+mob2);
                            email_t.setText("  "+email);

                            ImageView facebookButton = findViewById(R.id.imageView50);
                            ImageView instagramButton = findViewById(R.id.imageView51);

                            facebookButton.setOnClickListener(v -> openLink(fb_link));
                            instagramButton.setOnClickListener(v -> openLink(insta_link));



                        } else {
                            Log.e("Firestore", "Error checking document", task.getException());
                        }
                    }
                });

    }

    private void openLink(String url) {
        // Create an Intent to open the URL
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // Check if the app is installed (Facebook or Instagram)
        if (url.contains("facebook")) {
            if (isAppInstalled("com.facebook.katana")) {
                intent.setPackage("com.facebook.katana"); // Facebook app package
            }
        } else if (url.contains("instagram")) {
            if (isAppInstalled("com.instagram.android")) {
                intent.setPackage("com.instagram.android"); // Instagram app package
            }
        }

        // Start the activity to open the link
        startActivity(intent);
    }

    // Helper method to check if an app is installed
    private boolean isAppInstalled(String packageName) {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true; // App is installed
        } catch (PackageManager.NameNotFoundException e) {
            return false; // App is not installed
        }
    }



    private void searchFavourites() {
        String hotel_id = id;
        SharedPreferences sharedPref = SingleViewActivity.this.getSharedPreferences("lk.javainstitute.mybookings.data",Context.MODE_PRIVATE);
        String user_email = sharedPref.getString("userEmail",null);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ImageView heart = findViewById(R.id.imageView4);

        firestore.collection("favourite")
                .whereEqualTo("hotel_id", hotel_id)
                .whereEqualTo("user_email", user_email)
                .get() // Use get() for a one-time check
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Document exists -> Set filled heart image
                            heart.setImageResource(R.drawable.heart_fill);
                        } else {
                            // Document does not exist -> Set unfilled heart image
                            heart.setImageResource(R.drawable.heart_unfill);
                        }
                    } else {
                        Log.e("Firestore", "Error checking document", task.getException());
                    }
                });
    }



    private void showStartDatePicker() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now()); // Prevent past dates

        MaterialDatePicker<Long> startDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Check-in Date")
                .setTheme(R.style.GreenDatePickerTheme)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        startDatePicker.addOnPositiveButtonClickListener(selection -> {
            String startDate = formatDate(selection);
            showEndDatePicker(selection, startDate);
        });

        startDatePicker.show(getSupportFragmentManager(), "START_DATE_PICKER");
    }

    private void showEndDatePicker(long minDate, String startDate) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(minDate)); // Ensure checkout is after check-in

        MaterialDatePicker<Long> endDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Check-out Date")
                .setTheme(R.style.GreenDatePickerTheme)
                .setSelection(minDate + 86400000) // Default to next day
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        endDatePicker.addOnPositiveButtonClickListener(selection -> {
            String endDate = formatDate(selection);


            checkintext.setText(startDate);
            checkouttext.setText(endDate);;
        });

        endDatePicker.show(getSupportFragmentManager(), "END_DATE_PICKER");
    }

    private void showEndOnlyDatePicker() {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now()); // Prevent past dates

        MaterialDatePicker<Long> endDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Check-out Date")
                .setTheme(R.style.GreenDatePickerTheme)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        endDatePicker.addOnPositiveButtonClickListener(selection -> {

            String endDate = formatDate(selection);
            checkouttext.setText(endDate);;
        });

        endDatePicker.show(getSupportFragmentManager(), "END_DATE_PICKER");
    }

    private String formatDate(Long timeInMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return formatter.format(timeInMillis);
    }

    private int getAmenityIcon(String amenityName) {
        switch (amenityName.toLowerCase()) {
            case "wifi":
                return R.drawable.wifi;
            case "breakfast":
                return R.drawable.breakfast;
            case "spa":
                return R.drawable.spa;
            case "mini golf park":
                return R.drawable.golf;
            case "indoor pool":
                return R.drawable.swimingpool;
            case "restaurant":
                return R.drawable.dinner;
            case "parking":
                return R.drawable.parking;
            case "gym":
                return R.drawable.dumbbell;
            case "laundry":
                return R.drawable.laundry;
            case "ac":
                return R.drawable.freezing;
            default:
                return R.drawable.heart; // Use a default icon if no match
        }
    }


    private void startAutoSlide() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (imageUrls == null || imageUrls.isEmpty()) return; // Prevent crash if empty

                if (currentPage >= imageUrls.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000); // Change image every 3 seconds
            }
        };
        handler.postDelayed(runnable, 3000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private String formatDateDefault(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }


}