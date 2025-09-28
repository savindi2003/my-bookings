package lk.javainstitute.mybookings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.widget.Button;
import android.widget.Toast;

import lk.javainstitute.mybookings.model.BookingModel;

public class BookingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<BookingModel> bookingList;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.booking_fragment, container, false);

        recyclerView = view.findViewById(R.id.rvBookingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(getContext(), bookingList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchBookings();

        Button completed = view.findViewById(R.id.button11);
        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchPastBookings();
            }
        });
        Button ongoing = view.findViewById(R.id.button10);
        ongoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchBookings();
            }
        });

        return view;
    }

    private void fetchPastBookings() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Timestamp todayTimestamp = new Timestamp(calendar.getTime());

        CollectionReference bookingRef = db.collection("bokking");

        Query query = bookingRef
                .whereLessThanOrEqualTo("check_out",todayTimestamp);

        query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("FirebaseData", "Error listening for hotel updates", e);
                return;
            }

            if (querySnapshot != null) {
                bookingList.clear();

                List<DocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
                Log.d("FirebaseData", "Total Reservation Retrieved: " + documentSnapshotList.size());

                for (DocumentSnapshot document : documentSnapshotList) {
                    try {

                        String hotel_id = document.getString("hotel_id");
                        Double room_count = document.getDouble("room_count");
                        String room_id = document.getString("room_id");
                        String reservation_id = document.getString("id");
                        String status = "Checked Out";

                        Timestamp check_in_t = document.getTimestamp("check_in");
                        Timestamp check_out_t = document.getTimestamp("check_out");
                        Timestamp date_t = document.getTimestamp("date");

                        String check_in_date = formatTimestamp(check_in_t);
                        String check_out_date = formatTimestamp(check_out_t);
                        String date = formatTimestamp(date_t);

                        String client_address = document.getString("client_address");
                        String client_country = document.getString("client_country");
                        String client_email = document.getString("client_email");
                        String client_mobile = document.getString("client_mobile");
                        String client_name = document.getString("client_name");

                        double roomCount = room_count; // Convert safely

                        searchHotelDetails(hotel_id,roomCount,room_id,reservation_id,status,check_in_date,check_out_date,date,client_email,client_address,client_country,client_mobile,client_name);


                    } catch (Exception ex) {
                        Log.e("FirebaseData", "Error processing hotel", ex);
                    }
                }

            } else {
                Log.d("FirebaseData", "No snapshot data available");
            }
        });

    }


    private void fetchBookings() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Timestamp todayTimestamp = new Timestamp(calendar.getTime());

        CollectionReference bookingRef = db.collection("bokking");

        Query query = bookingRef
                .whereGreaterThan("check_out",todayTimestamp);

        query.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("FirebaseData", "Error listening for hotel updates", e);
                return;
            }

            if (querySnapshot != null) {
                bookingList.clear();

                List<DocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
                Log.d("FirebaseData", "Total Reservation Retrieved: " + documentSnapshotList.size());

                for (DocumentSnapshot document : documentSnapshotList) {
                    try {

                        String hotel_id = document.getString("hotel_id");
                        Double room_count = document.getDouble("room_count");
                        String room_id = document.getString("room_id");
                        String reservation_id = document.getString("id");
                        String status = document.getString("status");

                        Timestamp check_in_t = document.getTimestamp("check_in");
                        Timestamp check_out_t = document.getTimestamp("check_out");
                        Timestamp date_t = document.getTimestamp("date");

                        String check_in_date = formatTimestamp(check_in_t);
                        String check_out_date = formatTimestamp(check_out_t);
                        String date = formatTimestamp(date_t);

                        String client_address = document.getString("client_address");
                        String client_country = document.getString("client_country");
                        String client_email = document.getString("client_email");
                        String client_mobile = document.getString("client_mobile");
                        String client_name = document.getString("client_name");

                        double roomCount = room_count; // Convert safely

                        searchHotelDetails(hotel_id,roomCount,room_id,reservation_id,status,check_in_date,check_out_date,date,client_email,client_address,client_country,client_mobile,client_name);


                    } catch (Exception ex) {
                        Log.e("FirebaseData", "Error processing hotel", ex);
                    }
                }

            } else {
                Log.d("FirebaseData", "No snapshot data available");
            }
        });

    }

    private void searchHotelDetails(String hotel_id,Double roomCount,String room_id,String reservation_id,String status,String check_in_date,String check_out_date,String date,String client_email,String client_address,String client_country,String client_mobile,String client_name){
        db = FirebaseFirestore.getInstance();
        CollectionReference bookingRef = db.collection("hotel");

        Query query = bookingRef
                .whereEqualTo("id",hotel_id);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot document : value) {
                    // Get the values from the document fields
                    String hotel_name = document.getString("name");
                    String location = document.getString("location");
                    List<String> images = (List<String>) document.get("image");

                    GeoPoint geoPoint = document.getGeoPoint("map_location");
                    double lat = geoPoint.getLatitude();
                    double lan = geoPoint.getLongitude();


                    Log.d("FirebaseData", "Hotel Name " + hotel_name);
                    Log.d("FirebaseData", "Hotel location " + location);


                    if (images != null && !images.isEmpty()) {
                        String firstImageUrl = images.get(0);  // Get the first image URL

                        BookingModel bookingModel = new BookingModel(hotel_name,location,firstImageUrl,hotel_id,roomCount,room_id,reservation_id,status,check_in_date,check_out_date,date,client_address,client_country,client_email,client_mobile,client_name,lat,lan);

                        bookingList.add(bookingModel);

                        new Handler(Looper.getMainLooper()).post(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d("FirebaseData", "RecyclerView Updated, Total Hotels: " + bookingList.size());
                        });
                    }

                }
            }
        });

    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return sdf.format(date);
        }
        return "N/A";
    }

}
