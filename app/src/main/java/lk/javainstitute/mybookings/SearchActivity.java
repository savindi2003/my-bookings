package lk.javainstitute.mybookings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements SensorEventListener {
    ConstraintLayout datePickerLayout;
    ConstraintLayout layout_guests;
    TextView dateRangeTextView1;
    TextView dateRangeTextView2;
    TextView dateRangeTextView3;
    TextView dateRangeTextView4;
    private FirebaseFirestore db;

    String checkInDatePass;
    String checkOutDatePass;

    private int rooms = 1;
    private int adults = 2;
    private int children = 0;

    private RecyclerView hotelRecyclerView;
    private SearchHotelAdapter hotelAdapter;
    private List<Hotel> hotelList;

    EditText searchLocation;

    private SeekBar seekBar;
    private  TextView seekbarText;

    private Query query;
    private CollectionReference hotelsRef;

    private int minValue;
    private int maxValue;
    CheckBox checkBox1, checkBox2, checkBox3,checkBox4, checkBox5,checkBox6,checkBox7,checkBox8,checkBox9;

    private String selectedPropertyType;

    private boolean sortByPrice;
    private boolean sortOptionRating;

    private NetworkChangeReceiver networkChangeReceiver;

    private SensorManager sensorManager;
    private float lastX, lastY, lastZ;
    private static final int SHAKE_THRESHOLD = 800; // Adjust sensitivity
    private long lastUpdate;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView searchLocationBtn = findViewById(R.id.imageView49);
        searchLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, LocationSearchActivity.class);
                startActivity(intent);
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(SearchActivity.this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        lastUpdate = System.currentTimeMillis();

        networkChangeReceiver = new NetworkChangeReceiver();

        dateRangeTextView1 = findViewById(R.id.tvDateRange);
        dateRangeTextView2 = findViewById(R.id.tvDateRangeEnd);
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String checkInDate = formatDateDefault(calendar);

        calendar.add(Calendar.DAY_OF_YEAR, 2);
        String checkOutDate = formatDateDefault(calendar);

        dateRangeTextView1.setText(String.format(checkInDate));
        dateRangeTextView2.setText(String.format(checkOutDate));


        datePickerLayout = findViewById(R.id.layout_date);
        datePickerLayout.setOnClickListener(view -> showStartDatePicker());

        //check box


        //check box

        //room adult child

        layout_guests = findViewById(R.id.layout_guests);
        layout_guests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGuestInfoDialog();


            }
        });

        ImageView viewSort = findViewById(R.id.imageView48);
        viewSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout searchLayout = findViewById(R.id.constraint_layout_search);
                searchLayout.setVisibility(View.VISIBLE);

                viewSort.setVisibility(View.GONE);
            }
        });

        //seek bar
        seekBar = findViewById(R.id.seekBar);
        seekbarText = findViewById(R.id.textView170);

        minValue = 500;


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int actualValue = progress + minValue;
                seekbarText.setText(String.valueOf(progress));
                maxValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // This is called when the user first touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // This is called when the user releases the SeekBar
            }
        });

        //seekbar


        Button search_btn = findViewById(R.id.btnSearch);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConstraintLayout searchLayout = findViewById(R.id.constraint_layout_search);
                searchLayout.setVisibility(View.GONE);

                ImageView viewSort = findViewById(R.id.imageView48);
                viewSort.setVisibility(View.VISIBLE);

                dateRangeTextView3 = findViewById(R.id.tvDateRange);
                dateRangeTextView4 = findViewById(R.id.tvDateRangeEnd);

                checkInDatePass = dateRangeTextView3.getText().toString();
                checkOutDatePass = dateRangeTextView4.getText().toString();

                hotelRecyclerView = findViewById(R.id.hotel_search_recycler_view);
                hotelRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

                hotelList = new ArrayList<>();
                db = FirebaseFirestore.getInstance();

                //search

                String priceRange = seekbarText.getText().toString();
                int price_int = Integer.parseInt(priceRange);
                //search

                loadHotelsFromFirebase();

                hotelAdapter = new SearchHotelAdapter(SearchActivity.this, hotelList,checkInDatePass, checkOutDatePass);
                hotelRecyclerView.setAdapter(hotelAdapter);

            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastUpdate) > 100) { // Time gap between shakes
                long diffTime = (currentTime - lastUpdate);
                lastUpdate = currentTime;

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    onShakeDetected();
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed
    }



    private void onShakeDetected() {
        // Vibrate when shake is detected (optional)
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }

        // Show a Toast message
        Toast.makeText(this, "Shaking detected! Refreshing hotel sorting...", Toast.LENGTH_SHORT).show();

        // Call method to refresh hotel sorting
        refreshHotelSorting();
    }

    private void refreshHotelSorting() {
        Toast.makeText(this, "Hotels sorted!", Toast.LENGTH_SHORT).show();

        //date
        dateRangeTextView1 = findViewById(R.id.tvDateRange);
        dateRangeTextView2 = findViewById(R.id.tvDateRangeEnd);
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String checkInDate = formatDateDefault(calendar);

        calendar.add(Calendar.DAY_OF_YEAR, 2);
        String checkOutDate = formatDateDefault(calendar);

        dateRangeTextView1.setText(String.format(checkInDate));
        dateRangeTextView2.setText(String.format(checkOutDate));

        //check box
        checkBox1 = findViewById(R.id.checkBox13);
        checkBox2 = findViewById(R.id.checkBox14);
        checkBox3 = findViewById(R.id.checkBox15);
        checkBox4 = findViewById(R.id.checkBox16);
        checkBox5 = findViewById(R.id.checkBox17);
        checkBox6 = findViewById(R.id.checkBox18);
        checkBox7 = findViewById(R.id.checkBox19);
        checkBox8 = findViewById(R.id.checkBox20);
        checkBox9 = findViewById(R.id.checkBox21);

        checkBox1.setChecked(false);
        checkBox2.setChecked(false);
        checkBox3.setChecked(false);
        checkBox4.setChecked(false);
        checkBox5.setChecked(false);
        checkBox6.setChecked(false);
        checkBox7.setChecked(false);
        checkBox8.setChecked(false);
        checkBox9.setChecked(false);

        //edit text
        searchLocation = findViewById(R.id.editTextText4);
        searchLocation.setText("");

        EditText landMark = findViewById(R.id.editTextTextLandmark);
        landMark.setText("");

        //seek bar
        seekBar = findViewById(R.id.seekBar);
        seekbarText = findViewById(R.id.textView170);
        seekBar.setProgress(500);
        seekbarText.setText(String.valueOf(500));






    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        sensorManager.registerListener(SearchActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
        sensorManager.unregisterListener(this);
    }

    private void loadHotelsFromFirebase() {

        searchLocation = findViewById(R.id.editTextText4);

        checkBox1 = findViewById(R.id.checkBox13);
        checkBox2 = findViewById(R.id.checkBox14);
        checkBox3 = findViewById(R.id.checkBox15);
        checkBox4 = findViewById(R.id.checkBox16);
        checkBox5 = findViewById(R.id.checkBox17);

        checkBox6 = findViewById(R.id.checkBox18);
        checkBox7 = findViewById(R.id.checkBox19);
        checkBox8 = findViewById(R.id.checkBox20);
        checkBox9 = findViewById(R.id.checkBox21);

        checkBox1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBox2.setChecked(false);
                checkBox3.setChecked(false);
                checkBox4.setChecked(false);
                checkBox5.setChecked(false);
            }
        });
        checkBox2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBox1.setChecked(false);
                checkBox3.setChecked(false);
                checkBox4.setChecked(false);
                checkBox5.setChecked(false);
            }
        });
        checkBox3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBox2.setChecked(false);
                checkBox1.setChecked(false);
                checkBox4.setChecked(false);
                checkBox5.setChecked(false);
            }
        });
        checkBox4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBox2.setChecked(false);
                checkBox3.setChecked(false);
                checkBox1.setChecked(false);
                checkBox5.setChecked(false);
            }
        });
        checkBox5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBox2.setChecked(false);
                checkBox3.setChecked(false);
                checkBox4.setChecked(false);
                checkBox1.setChecked(false);
            }
        });

        checkBox6.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBox7.setChecked(false);

            }
        });
        checkBox7.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBox6.setChecked(false);

            }
        });
        checkBox8.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBox9.setChecked(false);

            }
        });
        checkBox9.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBox8.setChecked(false);

            }
        });

        if (checkBox1.isChecked()) {
            selectedPropertyType = "Hotels";
        }
        else if (checkBox2.isChecked()) {
            selectedPropertyType = "Apartments";
        }
        else if (checkBox3.isChecked()) {
            selectedPropertyType = "Resort";
        }
        else if (checkBox4.isChecked()) {
            selectedPropertyType = "Villas";
        }
        else if (checkBox5.isChecked()) {
            selectedPropertyType = "Cabana";
        }

        if(checkBox6.isChecked()){
            sortByPrice = true;
        }else if(checkBox7.isChecked()){
            sortByPrice = false;
        }if(checkBox8.isChecked()){
            sortOptionRating = true;
        }else if(checkBox9.isChecked()){
            sortOptionRating = false;
        }

        String search = searchLocation.getText().toString().trim();
        EditText landMark = findViewById(R.id.editTextTextLandmark);
        String selectedNearbyLandmarks = landMark.getText().toString().trim();

        hotelsRef = db.collection("hotel");

        // Get Filters
        int minBudget = minValue;
        int maxBudget = maxValue;
        String selectedType = selectedPropertyType;
        String selectedLandmarks = selectedNearbyLandmarks; // List of selected landmarks
        boolean sortByPriceHigh = sortByPrice; // high-true , low-false
        boolean sortByRatingHigh = sortOptionRating; //high-true , low-false

        query = hotelsRef;


        // Filter by Budget
        if (minBudget > 0 && maxBudget > 0) {
            query = query.whereGreaterThanOrEqualTo("price", minBudget)
                    .whereLessThanOrEqualTo("price", maxBudget);
        }



        // Filter by Property Type
        if (selectedPropertyType != null && !selectedPropertyType.isEmpty()) {
            query = query.whereEqualTo("type", selectedPropertyType);
        }


        // Filter by Nearby Landmarks
        if (selectedLandmarks != null && !selectedLandmarks.isEmpty()) {
            query = query.whereArrayContains("landmarks", selectedLandmarks.trim());
        }


        if (search != null && !search.isEmpty()) {
            query = query.orderBy("location")
                    .startAt(search)
                    .endAt(search + "\uf8ff");
        }else{

                    // Sorting
        if (sortByPriceHigh) {
            query = query.orderBy("price", Query.Direction.DESCENDING);
        }else if (!sortByPriceHigh) {
            query = query.orderBy("price", Query.Direction.ASCENDING);
        } else {
            query = query.orderBy("price", Query.Direction.ASCENDING); // Default sort by price low to high
        }

        }






        // Adding a SnapshotListener to listen for real-time changes
        query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("FirebaseData", "Error listening for hotel updates", e);
                return;
            }

            if (querySnapshot != null) {
                hotelList.clear(); // Clear previous list

                List<DocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
                Log.d("FirebaseData", "Total Hotels Retrieved: " + documentSnapshotList.size());

                for (DocumentSnapshot document : documentSnapshotList) {
                    try {
                        // Extract data
                        List<String> imageUrls = (List<String>) document.get("image");
                        String name = document.getString("name");
                        String description = document.getString("description");
                        String location = document.getString("location");
                        Double priceObj = document.getDouble("price"); // Handle null case
                        String ratingStr = document.getString("ratings");
                        List<String> amenities = (List<String>) document.get("amenities");
                        String contactNo1 = document.getString("contactNo1");
                        String contactNo2 = document.getString("contactNo2");
                        String email = document.getString("email");
                        String id = document.getString("id");

                        // Check for null values
                        if (name == null || location == null || priceObj == null) {
                            Log.e("FirebaseData", "Skipping hotel due to missing data");
                            continue; // Skip this hotel
                        }

                        double price = priceObj; // Convert safely

                        // Convert rating safely
                        float rating = 0.0f;
                        if (ratingStr != null && !ratingStr.isEmpty()) {
                            try {
                                rating = Float.parseFloat(ratingStr);
                            } catch (NumberFormatException ex) {
                                Log.e("FirebaseData", "Invalid rating format: " + ratingStr);
                            }
                        }

                        // Ensure imageUrls is not null
                        if (imageUrls == null) {
                            imageUrls = new ArrayList<>();
                        }

                        // Create Hotel object and add to list
                        Hotel hotel = new Hotel(imageUrls, name, description, location, price, rating, amenities, contactNo1, contactNo2, email, id);

                        hotelList.add(hotel);
                        Log.d("FirebaseData", "Added Hotel: " + name);

                    } catch (Exception ex) {
                        Log.e("FirebaseData", "Error processing hotel", ex);
                    }
                }

                if (sortOptionRating) {
                    Collections.sort(hotelList, (hotel1, hotel2) -> Float.compare(hotel2.getRating(), hotel1.getRating()));

                } else if (!sortOptionRating) {
                    Collections.sort(hotelList, (hotel1, hotel2) -> Float.compare(hotel1.getRating(), hotel2.getRating()));
                }

                Log.d("FirebaseData", "Total Hotels in List Before UI Update: " + hotelList.size());

                // Update UI on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    hotelAdapter.notifyDataSetChanged();
                    Log.d("FirebaseData", "RecyclerView Updated, Total Hotels: " + hotelList.size());
                });
            } else {
                Log.d("FirebaseData", "No snapshot data available");
            }
        });
    }

    private void showGuestInfoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.room_guest_dialogbox);
        dialog.setCancelable(true);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85); // 85% of screen width
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.search_border_rounded);

        // Get references to buttons and text views
        Button btnDecreaseRooms = dialog.findViewById(R.id.btnDecreaseRooms);
        Button btnIncreaseRooms = dialog.findViewById(R.id.btnIncreaseRooms);
        TextView txtRooms = dialog.findViewById(R.id.txtRooms);

        Button btnDecreaseAdults = dialog.findViewById(R.id.btnDecreaseAdults);
        Button btnIncreaseAdults = dialog.findViewById(R.id.btnIncreaseAdults);
        TextView txtAdults = dialog.findViewById(R.id.txtAdults);

        Button btnDecreaseChildren = dialog.findViewById(R.id.btnDecreaseChildren);
        Button btnIncreaseChildren = dialog.findViewById(R.id.btnIncreaseChildren);
        TextView txtChildren = dialog.findViewById(R.id.txtChildren);

        Button btnApply = dialog.findViewById(R.id.btnApply);

        // Set initial values
        txtRooms.setText(String.valueOf(rooms));
        txtAdults.setText(String.valueOf(adults));
        txtChildren.setText(String.valueOf(children));

        // Button Click Listeners for Rooms
        btnDecreaseRooms.setOnClickListener(v -> {
            if (rooms > 1) {
                rooms--;
                txtRooms.setText(String.valueOf(rooms));
            }
        });

        btnIncreaseRooms.setOnClickListener(v -> {
            rooms++;
            txtRooms.setText(String.valueOf(rooms));
        });

        // Button Click Listeners for Adults
        btnDecreaseAdults.setOnClickListener(v -> {
            if (adults > 1) {
                adults--;
                txtAdults.setText(String.valueOf(adults));
            }
        });

        btnIncreaseAdults.setOnClickListener(v -> {
            adults++;
            txtAdults.setText(String.valueOf(adults));
        });

        // Button Click Listeners for Children
        btnDecreaseChildren.setOnClickListener(v -> {
            if (children > 0) {
                children--;
                txtChildren.setText(String.valueOf(children));
            }
        });

        btnIncreaseChildren.setOnClickListener(v -> {
            children++;
            txtChildren.setText(String.valueOf(children));
        });

        // Apply Button Click Listener
        btnApply.setOnClickListener(v -> {


            TextView roomCount = dialog.findViewById(R.id.txtRooms);
            TextView adultsCount = dialog.findViewById(R.id.txtAdults);
            TextView childCount = dialog.findViewById(R.id.txtChildren);

            String roomCountText = roomCount.getText().toString();
            String adultCountText = adultsCount.getText().toString();
            String childrenCountText = childCount.getText().toString();

            TextView roomTextView = findViewById(R.id.textView14);
            TextView aultTextView = findViewById(R.id.textView9);
            TextView chirlderTextView = findViewById(R.id.textView10);

            roomTextView.setText(roomCountText + " rooms");
            aultTextView.setText(adultCountText + " adults");
            chirlderTextView.setText(childrenCountText + " childern");

            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();


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

            dateRangeTextView1 = findViewById(R.id.tvDateRange);
            dateRangeTextView1.setText(startDate);

            dateRangeTextView2 = findViewById(R.id.tvDateRangeEnd);
            dateRangeTextView2.setText(endDate);


        });

        endDatePicker.show(getSupportFragmentManager(), "END_DATE_PICKER");
    }

    private String formatDate(Long timeInMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return formatter.format(timeInMillis);
    }

    private String formatDateDefault(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }




}
