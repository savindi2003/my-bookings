package lk.javainstitute.mybookings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import lk.javainstitute.mybookings.model.BookingModel;
import lk.javainstitute.mybookings.model.Room;

public class Fragment_Admin4 extends Fragment {

    private RecyclerView recyclerView;
    private BookingAdminAdapter adapter;
    private List<BookingModel> bookingList;
    private FirebaseFirestore firestore;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment4, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_ad_bookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookingList = new ArrayList<>();
        adapter = new BookingAdminAdapter(getContext(), bookingList);
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
        String hotel_id1 = sharedPref.getString("hotel_id", null);

        firestore = FirebaseFirestore.getInstance();

        loadBookings(hotel_id1);

        Spinner spinner = view.findViewById(R.id.spinner2);

        HashSet<String> uniqueRooms = new HashSet<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Select Room");

        ArrayAdapter<String> ad = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_selected_item,
                arrayList

        );
        ad.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(ad);

        firestore.collection("room")
                .whereEqualTo("hotel_id", hotel_id1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String roomName = document.getString("id"); // Get room name
                            if (roomName != null && uniqueRooms.add(roomName)) {
                                arrayList.add(roomName);
                            }
                        }
                        ad.notifyDataSetChanged(); // Notify adapter of changes
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching rooms", e));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedRoom = adapterView.getItemAtPosition(i).toString();
                if (!selectedRoom.equals("Select Room")) {
                    searchByRoom(selectedRoom,hotel_id1);

                }else{
                    loadBookings(hotel_id1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        //spinner status
        Spinner spinner_status = view.findViewById(R.id.spinner3);

        ArrayList<String> status_arrayList = new ArrayList<>();
        status_arrayList.add("Select Status");
        status_arrayList.add("Pending");
        status_arrayList.add("Paid");
        status_arrayList.add("Checked Out");

        ArrayAdapter<String> ad1 = new ArrayAdapter<>(
                getContext(),
                R.layout.spinner_selected_item,
                status_arrayList

        );
        ad1.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner_status.setAdapter(ad1);

        spinner_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedStatus = adapterView.getItemAtPosition(i).toString();
                if (!selectedStatus.equals("Select Status")) {
                    searchByStatus(selectedStatus,hotel_id1);

                }else{
                    loadBookings(hotel_id1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //spinner status



        return view;
    }

    private void searchByStatus(String status1,String hotel_id) {

        CollectionReference hotelsRef = firestore.collection("bokking");

        Query query;


        query = hotelsRef.where(
                Filter.and(
                        Filter.equalTo("status",status1),
                        Filter.equalTo("hotel_id",hotel_id)
                )
        );


        query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("FirebaseData", "Error listening for hotel updates", e);
                return;
            }

            if (querySnapshot != null) {
                bookingList.clear(); // Clear previous list

                List<DocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
                Log.d("FirebaseData", "Total Hotels Retrieved: " + documentSnapshotList.size());

                for (DocumentSnapshot document : documentSnapshotList) {
                    try {
                        String reservation_id = document.getString("id");
                        // String roomName = document.getString("name");
                        String roomName = "Room Id :"; // You can replace with actual field if available
                        String room_id = document.getString("room_id");
                        Double room_count = document.getDouble("room_count");
                        String client_name = document.getString("client_name");
                        String client_mobile = document.getString("client_mobile");
                        String status = document.getString("status");

                        Timestamp date = document.getTimestamp("date");
                        Timestamp check_in_date = document.getTimestamp("check_in");
                        Timestamp check_out_date = document.getTimestamp("check_out");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        String date_s = (date != null) ? sdf.format(date.toDate()) : "N/A";
                        String check_in_s = (check_in_date != null) ? sdf.format(check_in_date.toDate()) : "N/A";
                        String check_out_s = (check_out_date != null) ? sdf.format(check_out_date.toDate()) : "N/A";

                        double count_d = room_count != null ? room_count : 0;


                        // Create BookingModel and add to list
                        BookingModel bookingModel = new BookingModel(count_d, room_id, reservation_id, status, check_in_s, check_out_s, date_s, client_mobile, client_name, roomName);
                        bookingList.add(bookingModel);

                    } catch (Exception ex) {
                        Log.e("FirebaseData", "Error processing hotel", ex);
                    }
                }

                Log.d("FirebaseData", "Total Hotels in List Before UI Update: " + bookingList.size());

                // Update UI on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    adapter.notifyDataSetChanged();
                    Log.d("FirebaseData", "RecyclerView Updated, Total Hotels: " + bookingList.size());
                });
            } else {
                Log.d("FirebaseData", "No snapshot data available");
            }
        });


    }

    private void searchByRoom(String room_id1,String hotel_id) {

        CollectionReference hotelsRef = firestore.collection("bokking");

        Query query;


        query = hotelsRef.where(
                Filter.and(
                        Filter.equalTo("room_id",room_id1),
                        Filter.equalTo("hotel_id",hotel_id)
                )
        );


        query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("FirebaseData", "Error listening for hotel updates", e);
                return;
            }

            if (querySnapshot != null) {
                bookingList.clear(); // Clear previous list

                List<DocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
                Log.d("FirebaseData", "Total Hotels Retrieved: " + documentSnapshotList.size());

                for (DocumentSnapshot document : documentSnapshotList) {
                    try {
                        String reservation_id = document.getString("id");
                        // String roomName = document.getString("name");
                        String roomName = "Room Id :"; // You can replace with actual field if available
                        String room_id = document.getString("room_id");
                        Double room_count = document.getDouble("room_count");
                        String client_name = document.getString("client_name");
                        String client_mobile = document.getString("client_mobile");
                        String status = document.getString("status");

                        Timestamp date = document.getTimestamp("date");
                        Timestamp check_in_date = document.getTimestamp("check_in");
                        Timestamp check_out_date = document.getTimestamp("check_out");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        String date_s = (date != null) ? sdf.format(date.toDate()) : "N/A";
                        String check_in_s = (check_in_date != null) ? sdf.format(check_in_date.toDate()) : "N/A";
                        String check_out_s = (check_out_date != null) ? sdf.format(check_out_date.toDate()) : "N/A";

                        double count_d = room_count != null ? room_count : 0;


                        // Create BookingModel and add to list
                        BookingModel bookingModel = new BookingModel(count_d, room_id, reservation_id, status, check_in_s, check_out_s, date_s, client_mobile, client_name, roomName);
                        bookingList.add(bookingModel);

                    } catch (Exception ex) {
                        Log.e("FirebaseData", "Error processing hotel", ex);
                    }
                }

                Log.d("FirebaseData", "Total Hotels in List Before UI Update: " + bookingList.size());

                // Update UI on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    adapter.notifyDataSetChanged();
                    Log.d("FirebaseData", "RecyclerView Updated, Total Hotels: " + bookingList.size());
                });
            } else {
                Log.d("FirebaseData", "No snapshot data available");
            }
        });


    }


    private void loadBookings(String hotel_id) {

        CollectionReference hotelsRef = firestore.collection("bokking");

        Query query;
        query = hotelsRef.whereEqualTo("hotel_id", hotel_id);


        query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("FirebaseData", "Error listening for hotel updates", e);
                return;
            }

            if (querySnapshot != null) {
                bookingList.clear(); // Clear previous list

                List<DocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
                Log.d("FirebaseData", "Total Hotels Retrieved: " + documentSnapshotList.size());

                for (DocumentSnapshot document : documentSnapshotList) {
                    try {
                        String reservation_id = document.getString("id");
                        // String roomName = document.getString("name");
                        String roomName = "Room Id :"; // You can replace with actual field if available
                        String room_id = document.getString("room_id");
                        Double room_count = document.getDouble("room_count");
                        String client_name = document.getString("client_name");
                        String client_mobile = document.getString("client_mobile");
                        String status = document.getString("status");

                        Timestamp date = document.getTimestamp("date");
                        Timestamp check_in_date = document.getTimestamp("check_in");
                        Timestamp check_out_date = document.getTimestamp("check_out");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        String date_s = (date != null) ? sdf.format(date.toDate()) : "N/A";
                        String check_in_s = (check_in_date != null) ? sdf.format(check_in_date.toDate()) : "N/A";
                        String check_out_s = (check_out_date != null) ? sdf.format(check_out_date.toDate()) : "N/A";

                        double count_d = room_count != null ? room_count : 0;


                        // Create BookingModel and add to list
                        BookingModel bookingModel = new BookingModel(count_d, room_id, reservation_id, status, check_in_s, check_out_s, date_s, client_mobile, client_name, roomName);
                        bookingList.add(bookingModel);

                    } catch (Exception ex) {
                        Log.e("FirebaseData", "Error processing hotel", ex);
                    }
                }

                Log.d("FirebaseData", "Total Hotels in List Before UI Update: " + bookingList.size());

                // Update UI on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    adapter.notifyDataSetChanged();
                    Log.d("FirebaseData", "RecyclerView Updated, Total Hotels: " + bookingList.size());
                });
            } else {
                Log.d("FirebaseData", "No snapshot data available");
            }
        });


    }


}
