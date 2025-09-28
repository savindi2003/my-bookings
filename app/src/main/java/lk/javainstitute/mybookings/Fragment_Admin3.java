package lk.javainstitute.mybookings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.mybookings.model.Room;

public class Fragment_Admin3 extends Fragment {

    private RecyclerView recyclerView;
    private RoomAdapter2 roomAdapter;
    private List<Room> roomList;
    private FirebaseFirestore firestore;
    private CollectionReference roomsRef;

    private String hotel_id;

    public Fragment_Admin3() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment3, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_a1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize room list and adapter
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter2(getContext(), roomList);
        recyclerView.setAdapter(roomAdapter);

        // Get hotel email from SharedPreferences
        SharedPreferences sharedPref = requireContext().getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
        String hotel_email = sharedPref.getString("hotel", null);

        if (hotel_email != null) {
            firestore = FirebaseFirestore.getInstance();

            // Query to get hotel_id based on hotel email
            firestore.collection("hotel")
                    .whereEqualTo("email", hotel_email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                hotel_id = document.getString("id");

                                // Fetch rooms based on hotel_id
                                fetchRoomsByHotelId(hotel_id);

                                // Add new room button click listener
                                Button addNewRoomButton = view.findViewById(R.id.button23);
                                addNewRoomButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (hotel_id != null) {
                                            Intent intent = new Intent(getContext(), AddNewRoomActivity.class);
                                            intent.putExtra("hotelId", hotel_id);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getContext(), "Hotel ID not found. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                Log.e("Firestore", "No hotel found or error occurred", task.getException());
                            }
                        }
                    });
        } else {
            Log.e("SharedPreferences", "No hotel email found");
        }

        return view;
    }

    // Fetch rooms based on hotel_id
    private void fetchRoomsByHotelId(String hotel_id) {
        firestore.collection("room")
                .whereEqualTo("hotel_id", hotel_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // Clear previous room data
                            roomList.clear();

                            for (DocumentSnapshot document : task.getResult()) {
                                // Retrieve fields for each room
                                String roomId = document.getString("id");
                                String roomName = document.getString("name");
                                Double count = document.getDouble("count");
                                Double max_count = document.getDouble("max_count");
                                String imageUrl = document.getString("image");
                                String personCount = document.getString("person");
                                Double pricePerNight = document.getDouble("price_per_night");

                                double count_d = count;
                                double max_count_d = max_count;

                                int count_int = (int) count_d;
                                int max_count_int = (int) max_count_d;

                                // Create Room object
                                Room room = new Room(roomId, roomName, count_int,max_count_int, pricePerNight != null ? pricePerNight : 0, personCount, imageUrl);

                                // Add room to list
                                roomList.add(room);
                            }

                            // Notify adapter that the data has changed
                            roomAdapter.notifyDataSetChanged();
                            Log.d("FirebaseData", "RecyclerView Updated, Total rooms: " + roomList.size());

                        } else {
                            Log.e("Firestore", "Error getting rooms: ", task.getException());
                        }
                    }
                });
    }
}

