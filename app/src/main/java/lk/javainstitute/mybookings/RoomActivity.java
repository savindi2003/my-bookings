package lk.javainstitute.mybookings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lk.javainstitute.mybookings.model.Room;

public class RoomActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RoomAdapter adapter;
    private List<Room> roomList;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color1));

        Intent intent = getIntent();
        String name = intent.getStringExtra("hotel_name");
        String location = intent.getStringExtra("hotel_location");
        String id = intent.getStringExtra("hotel_id");

        String price = intent.getStringExtra("hotel_price");
        String ratings = intent.getStringExtra("hotel_rating");
        String check_in_date = intent.getStringExtra("check_in_date");
        String check_out_date = intent.getStringExtra("check_out_date");

        TextView hotelname = findViewById(R.id.textView21);
        TextView hoteladdress = findViewById(R.id.textView22);

        hotelname.setText(name);
        hoteladdress.setText(location);

        //Toast.makeText(RoomActivity.this,price+" "+ratings,Toast.LENGTH_LONG).show();

        recyclerView = findViewById(R.id.room_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomList = new ArrayList<>();

        adapter = new RoomAdapter(RoomActivity.this, roomList);
        recyclerView.setAdapter(adapter);

        //
        db = FirebaseFirestore.getInstance();
        loadRoomsFromFirebase(id);
        //

        Button reserveButton = findViewById(R.id.button6);
        reserveButton.setOnClickListener(v -> {
            Map<String, Integer> selectedRooms = adapter.getSelectedRooms();
            if (selectedRooms.isEmpty()) {
                Toast.makeText(RoomActivity.this, "No rooms selected!", Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<String> roomIds = new ArrayList<>();
                ArrayList<Integer> roomCounts = new ArrayList<>();

                for (Map.Entry<String, Integer> entry : selectedRooms.entrySet()) {
                    roomIds.add(entry.getKey());
                    roomCounts.add(entry.getValue());
                }

                Log.d("DEBUG", "Selected Rooms: " + roomIds + " Counts: " + roomCounts);

                Intent in = new Intent(RoomActivity.this, SummaryActivity.class);
                in.putStringArrayListExtra("roomIds", roomIds);
                in.putIntegerArrayListExtra("roomCounts", roomCounts);

                in.putExtra("hotel_name",name);
                in.putExtra("hotel_location",location);
                in.putExtra("hotel_price",price);
                in.putExtra("hotel_ratings",ratings);
                in.putExtra("hotel_id",id);

                in.putExtra("check_in_date",check_in_date);
                in.putExtra("check_out_date",check_out_date);

                startActivity(in);
                Log.d("DEBUG", "Navigating to ActivitySummary");
            }
        });


    }

    private void loadRoomsFromFirebase(String id){
        CollectionReference roomRef = db.collection("room");

        // Adding a SnapshotListener to listen for real-time changes
        roomRef.whereEqualTo("hotel_id", id)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("FirebaseData", "Error listening for room updates", e);
                        return;
                    }

                    if (querySnapshot != null) {
                        roomList.clear(); // Clear previous list

                        List<DocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
                        Log.d("FirebaseData", "Total room Retrieved: " + documentSnapshotList.size());

                        for (DocumentSnapshot document : documentSnapshotList) {
                            try {
                                // Extract data

                                String balcony = document.getString("name");
                                String bathroom = document.getString("bathroom");
                                String bed = document.getString("bed");
                                Double count = document.getDouble("count"); // Handle null case
                                String hotel_id = document.getString("hotel_id");
                                String room_id = document.getString("id");
                                String imageUrl = document.getString("image");
                                String room_name = document.getString("name");
                                String person_count = document.getString("person");
                                Double price_per_night = document.getDouble("price_per_night");
                                String room_type = document.getString("room_type");
                                String size = document.getString("size");
                                String sound = document.getString("sound");
                                String view = document.getString("view");
                                String wifi = document.getString("wifi");

                                // Check for null values
                                if (room_name == null || hotel_id == null || price_per_night == null) {
                                    Log.e("FirebaseData", "Skipping room due to missing data");
                                    continue; // Skip this hotel
                                }

                                double price_per_night_d = price_per_night;
                                double count_d = count;

                                Room room = new Room(room_name, room_id, imageUrl, hotel_id, room_type, price_per_night_d, person_count, count_d, bed, size, bathroom, sound, wifi, view, balcony);

                                // Create Hotel object and add to list

                                roomList.add(room);
                                Log.d("FirebaseData", "Added room: " + room_name);

                            } catch (Exception ex) {
                                Log.e("FirebaseData", "Error processing room", ex);
                            }
                        }

                        Log.d("FirebaseData", "Total room in List Before UI Update: " + roomList.size());

                        // Update UI on the main thread
                        new Handler(Looper.getMainLooper()).post(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d("FirebaseData", "RecyclerView Updated, Total rooms: " + roomList.size());
                        });
                    } else {
                        Log.d("FirebaseData", "No snapshot data available");
                    }
                });
    }

}