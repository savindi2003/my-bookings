package lk.javainstitute.mybookings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.NavigationUiSaveStateControl;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BlankFragment1 extends Fragment {

    private RecyclerView hotelRecyclerView;
    private HotelAdapter hotelAdapter;
    private List<Hotel> hotelList;
    private FirebaseFirestore db;

    private String userEmail;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_blank1, container, false);

        View view = inflater.inflate(R.layout.fragment_blank1, container, false);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String checkInDate = formatDateDefault(calendar);

        calendar.add(Calendar.DAY_OF_YEAR, 2);
        String checkOutDate = formatDateDefault(calendar);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("lk.javainstitute.mybookings.data",Context.MODE_PRIVATE);
        userEmail = sharedPref.getString("userEmail",null);

        hotelRecyclerView = view.findViewById(R.id.fav_hotel_recyclerview);
        hotelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        hotelList = new ArrayList<>();
        hotelAdapter = new HotelAdapter(getContext(), hotelList , checkInDate,checkOutDate);
        hotelRecyclerView.setAdapter(hotelAdapter);

        db = FirebaseFirestore.getInstance();

        loadFavHotelList(userEmail);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hotelRecyclerView = view.findViewById(R.id.fav_hotel_recyclerview);
        hotelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                Hotel hotel = hotelList.get(position);
                String hotelId = hotel.getId();

                deleteFavorite(hotelId, userEmail, position);

            }
        });
        itemTouchHelper.attachToRecyclerView(hotelRecyclerView);


    }

    private void deleteFavorite(String hotelId, String userEmail, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference favRef = db.collection("favourite");

        favRef.whereEqualTo("hotel_id", hotelId)
                .whereEqualTo("user_email", userEmail)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        favRef.document(doc.getId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firebase", "Favorite deleted successfully!");
                                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();

                                    // Remove from list and notify adapter
                                    hotelList.remove(position);
                                    hotelAdapter.notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e -> Log.e("Firebase", "Error deleting favorite", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Error finding favorite", e));
    }

    private void loadFavHotelList(String userEmail) {
        CollectionReference favRef = db.collection("favourite"); // Reference to favourites collection

        favRef.whereEqualTo("user_email", userEmail) // Filter for logged-in user's favourites
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> favHotelIds = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            String hotelId = document.getString("hotel_id");
                            if (hotelId != null) {
                                favHotelIds.add(hotelId);
                            }
                        }

                        Log.d("FirebaseData", "Total Favourite Hotels Found: " + favHotelIds.size());

                        if (!favHotelIds.isEmpty()) {
                            fetchHotelsByIds(favHotelIds); // Fetch hotel details
                        } else {
                            hotelList.clear();
                            hotelAdapter.notifyDataSetChanged();
                            Log.d("FirebaseData", "No Favourite Hotels Found");
                        }
                    } else {
                        Log.e("FirebaseData", "Error fetching favourites", task.getException());
                    }
                });
    }

    private void fetchHotelsByIds(List<String> favHotelIds) {
        CollectionReference hotelsRef = db.collection("hotel");

        hotelsRef.whereIn("id", favHotelIds) // Fetch only favourite hotels
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        hotelList.clear(); // Clear previous list

                        for (DocumentSnapshot document : task.getResult()) {
                            try {
                                // Extract data
                                List<String> imageUrls = (List<String>) document.get("image");
                                String name = document.getString("name");
                                String description = document.getString("description");
                                String location = document.getString("location");
                                Double priceObj = document.getDouble("price");
                                String ratingStr = document.getString("ratings");
                                List<String> amenities = (List<String>) document.get("amenities");
                                String contactNo1 = document.getString("contactNo1");
                                String contactNo2 = document.getString("contactNo2");
                                String email = document.getString("email");
                                String id = document.getString("id");

                                if (name == null || location == null || priceObj == null) {
                                    Log.e("FirebaseData", "Skipping hotel due to missing data");
                                    continue;
                                }

                                double price = priceObj; // Convert safely
                                float rating = 0.0f;

                                if (ratingStr != null && !ratingStr.isEmpty()) {
                                    try {
                                        rating = Float.parseFloat(ratingStr);
                                    } catch (NumberFormatException ex) {
                                        Log.e("FirebaseData", "Invalid rating format: " + ratingStr);
                                    }
                                }

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

                        Log.d("FirebaseData", "Total Favourite Hotels in List Before UI Update: " + hotelList.size());

                        // Update UI on the main thread
                        new Handler(Looper.getMainLooper()).post(() -> {
                            hotelAdapter.notifyDataSetChanged();
                            Log.d("FirebaseData", "RecyclerView Updated, Total Favourite Hotels: " + hotelList.size());
                        });
                    } else {
                        Log.e("FirebaseData", "Error fetching favourite hotels", task.getException());
                    }
                });
    }


    private String formatDateDefault(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}