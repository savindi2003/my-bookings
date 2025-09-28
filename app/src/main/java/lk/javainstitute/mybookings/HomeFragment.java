package lk.javainstitute.mybookings;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView chipRecyclerView, cityRecyclerView, hotelRecyclerView;
    private ChipAdapter chipAdapter;
    private CityAdapter cityAdapter;
    private List<String> chipDataList;
    private List<String> cityDataList;
    private List<Integer> cityImageList;
    private HotelAdapter hotelAdapter;
    private List<Hotel> hotelList;

    private ImageView searchSpace;
    private ImageView searchName;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout containing both RecyclerViews
        View view = inflater.inflate(R.layout.fragment_home_fragmennt, container, false);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("lk.javainstitute.mybookings.data",Context.MODE_PRIVATE);
        String x = sharedPref.getString("userEmail",null);

        //TextView textView = view.findViewById(R.id.textView25);
        //textView.setText(x);


        // Initialize RecyclerViews
        chipRecyclerView = view.findViewById(R.id.recycler_view1);
        cityRecyclerView = view.findViewById(R.id.recycler_view2);
        hotelRecyclerView = view.findViewById(R.id.recycler_view3);

        // Setup first RecyclerView (Horizontal Chips)
        chipRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        initializeChipData();

        chipAdapter = new ChipAdapter(chipDataList,new ChipAdapter.OnChipClickListener() {
            @Override
            public void onChipClick(String chipText) {
                db = FirebaseFirestore.getInstance();
                chipSearch(chipText);
            }
        });

        chipRecyclerView.setAdapter(chipAdapter);

        // Setup second RecyclerView (City Cards)
        cityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        initializeCityData();
        initializeCityImage();
        cityAdapter = new CityAdapter(cityDataList,cityImageList);
        cityRecyclerView.setAdapter(cityAdapter);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String checkInDate = formatDateDefault(calendar);

        calendar.add(Calendar.DAY_OF_YEAR, 2);
        String checkOutDate = formatDateDefault(calendar);


//        checkintext.setText(String.format("%s",checkInDate));
//        checkouttext.setText(String.format("%s",checkOutDate));

        hotelRecyclerView = view.findViewById(R.id.recycler_view3);
        hotelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        hotelList = new ArrayList<>();
        hotelAdapter = new HotelAdapter(getContext(), hotelList , checkInDate,checkOutDate);
        hotelRecyclerView.setAdapter(hotelAdapter);

        db = FirebaseFirestore.getInstance();

        loadHotelsFromFirebase();





        //search activity
        searchSpace = view.findViewById(R.id.imageView2);

        searchSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        searchName = view.findViewById(R.id.imageView5);
        EditText searchEditText = view.findViewById(R.id.editTextSearch);


        searchName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = searchEditText.getText().toString().trim();


                if (searchText.isEmpty()) {
                    loadHotelsFromFirebase();
                    return; // Prevent empty search
                }

                CollectionReference hotelsRef = db.collection("hotel");

                Query query = hotelsRef
                        .orderBy("name")
                        .startAt(searchText)
                        .endAt(searchText + "\uf8ff");

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
                                String contactNo1 = document.getString("contact_no1");
                                String contactNo2 = document.getString("contact_no2");
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
                                Hotel hotel = new Hotel(imageUrls, name, description, location, price, rating, amenities, contactNo1, contactNo2, email,id);

                                hotelList.add(hotel);
                                Log.d("FirebaseData", "Added Hotel: " + name);

                            } catch (Exception ex) {
                                Log.e("FirebaseData", "Error processing hotel", ex);
                            }
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
        });

        //search activity

        LinearLayout l1 = view.findViewById(R.id.linearLayout3);
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loadHotelsFromFirebase();

            }
        });






        return view;




    }

    private void loadHotelsFromFirebase() {
        if (db == null) {
            Log.e("FirebaseError", "Firestore instance is null!");
            return;
        }

        CollectionReference hotelsRef = db.collection("hotel");

        // Adding a SnapshotListener to listen for real-time changes
        hotelsRef.addSnapshotListener((querySnapshot, e) -> {
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
                        String contactNo1 = document.getString("contact_no1");
                        String contactNo2 = document.getString("contact_no2");
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
                        Hotel hotel = new Hotel(imageUrls, name, description, location, price, rating, amenities, contactNo1, contactNo2, email,id);

                        hotelList.add(hotel);
                        Log.d("FirebaseData", "Added Hotel: " + name);

                    } catch (Exception ex) {
                        Log.e("FirebaseData", "Error processing hotel", ex);
                    }
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


    private void chipSearch(String type){

        CollectionReference hotelsRef = db.collection("hotel");

        Query query;
        query = hotelsRef.whereEqualTo("type", type);

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
                        String contactNo1 = document.getString("contact_no1");
                        String contactNo2 = document.getString("contact_no2");
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
                        Hotel hotel = new Hotel(imageUrls, name, description, location, price, rating, amenities, contactNo1, contactNo2, email,id);

                        hotelList.add(hotel);
                        Log.d("FirebaseData", "Added Hotel: " + name);

                    } catch (Exception ex) {
                        Log.e("FirebaseData", "Error processing hotel", ex);
                    }
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
    

    // Initialize dummy chip data
    private void initializeChipData() {
        chipDataList = new ArrayList<>();
        chipDataList.add("Hotels");
        chipDataList.add("Apartments");
        chipDataList.add("Resort");
        chipDataList.add("Villas");
        chipDataList.add("Cabana");
    }

    // Initialize dummy city data
    private void initializeCityData() {
        cityDataList = new ArrayList<>();
        cityDataList.add("Kandy");
        cityDataList.add("Colombo");
        cityDataList.add("Galle");
        cityDataList.add("Ella");
        cityDataList.add("Jafna");
    }

    // Initialize city images
    private void initializeCityImage() {
        cityImageList = new ArrayList<Integer>();
        cityImageList.add(R.drawable.kandy);
        cityImageList.add(R.drawable.colombo);
        cityImageList.add(R.drawable.galle);
        cityImageList.add(R.drawable.ella);
        cityImageList.add(R.drawable.jaffna);

    }

    private void loadSampleHotels() {
        hotelList.add(new Hotel(R.drawable.kandy, "Shangrilla Hotel", "211B Baker Street", 300.00, 4.8f));
        hotelList.add(new Hotel(R.drawable.kandy, "Grand Palace", "Downtown Street", 250.00, 4.5f));
        hotelList.add(new Hotel(R.drawable.kandy, "Royal Orchid", "Beach Road", 400.00, 4.9f));

    }

    private String formatDateDefault(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }





}



