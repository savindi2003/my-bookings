package lk.javainstitute.mybookings;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lk.javainstitute.mybookings.model.Traveller;

public class Fragment_Admin1 extends Fragment {

    private BarChart barChart;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private Set<Traveller> uniqueClients;
    private TextView rating_tv , favourite_tv;

    private TextView textViewTodayCheckIn;
    private TextView textViewTodayCheckOut;
    private TextView textViewUpcomingBookings;
    private TextView textViewTotalBookings;

    private ImageView rating_star1 , rating_star2,rating_star3,rating_star4,rating_star5;
    private static final int QR_SCAN_REQUEST = 100;
    private TextView resultText;

    private ImageView hotelImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_admindashboard,container,false);
        View view = inflater.inflate(R.layout.fragment_admindashboard, container, false);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
        String hotel_email = sharedPref.getString("hotel_id",null);
        String hotel_id = sharedPref.getString("hotel",null);

        TextView logout = view.findViewById(R.id.textView164);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("hotel");
                editor.remove("hotel_id");
                editor.apply();

                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

            }
        });


        if(hotel_email != null){
            TextView t1 = view.findViewById(R.id.textView95);
            t1.setText(hotel_email);
            TextView t2 = view.findViewById(R.id.textView124);
            t2.setText(hotel_id);

            barChart = view.findViewById(R.id.barChart);
            setupBarChart(hotel_email);

            textViewTodayCheckIn = view.findViewById(R.id.textView138);
            textViewTodayCheckOut = view.findViewById(R.id.textView142);
            textViewUpcomingBookings = view.findViewById(R.id.textView140);
            textViewTotalBookings = view.findViewById(R.id.textView144);
            showCounts(hotel_email);

            hotelImage = view.findViewById(R.id.imageView38);
            loadHotelImage(hotel_email);

        }else{
            Log.i("SharedPreferences","Email not found");
        }

        ImageView qrButton = view.findViewById(R.id.imageView39);
        resultText = view.findViewById(R.id.textView145);

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), QRScannerActivity.class);
                startActivityForResult(intent, QR_SCAN_REQUEST);
            }
        });



        //traveller
        recyclerView = view.findViewById(R.id.ad_rv1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        uniqueClients = new HashSet<>();

        db.collection("bokking")
                .whereEqualTo("hotel_id", hotel_email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String clientName = document.getString("client_name");
                        if (clientName != null) {
                            String firstName = clientName.split(" ")[0];
                            char firstLetter = firstName.charAt(0);

                            // Create Traveller object
                            Traveller traveller = new Traveller(firstName, firstLetter);
                            uniqueClients.add(traveller);
                        }
                    }

                    int circleColor = ContextCompat.getColor(getContext(), R.color.bottom_navigation);

                    List<Traveller> travellerList = new ArrayList<>(uniqueClients);

                    // Update RecyclerView
                    TravellerAdapter adapter = new TravellerAdapter(getContext(), travellerList, circleColor);
                    recyclerView.setAdapter(adapter);


                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching client names", e));

        //traveller

        //reviews
        getHotelRating(hotel_email);
        rating_tv = view.findViewById(R.id.textView149);

        rating_star1 = view.findViewById(R.id.imageView40);
        rating_star2 = view.findViewById(R.id.imageView41);
        rating_star3 = view.findViewById(R.id.imageView42);
        rating_star4 = view.findViewById(R.id.imageView43);
        rating_star5 = view.findViewById(R.id.imageView44);
        //reviews

        //favourite count
        favourite_tv = view.findViewById(R.id.textView159);
        getFavoriteCount(hotel_email);

        //map

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_dashboard);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.location_layout, supportMapFragment);
            transaction.commit();
        }

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                LatLng latLng = new LatLng(7.163848726761394, 79.930656458272);

                googleMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(latLng)
                                        .zoom(10)
                                        .build()

                        )
                );

                //marker ekk add krnva
                googleMap.addMarker(
                        new MarkerOptions().position(latLng)
                                .title("Your Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                );

            }
        });

        //map



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == QR_SCAN_REQUEST && resultCode == RESULT_OK && data != null) {
            String qrData = data.getStringExtra("qr_data");

            Intent intent = new Intent(getContext(), ReservationTicketViewActivity.class);
            intent.putExtra("reservation_id", qrData);
            startActivity(intent);

        }
    }

    private void loadHotelImage(String hotel_id){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("hotel")
                .whereEqualTo("id", hotel_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            List<String> imageUrls = (List<String>) document.get("image");

                            if (imageUrls != null && !imageUrls.isEmpty()) {
                                String firstImageUrl = imageUrls.get(0);

                                Picasso.get()
                                        .load(firstImageUrl)
                                        .into(hotelImage);
                            }

                        }
                    }
                });


    }

    private void showCounts(String hotel_id){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Date today = new Date();
        Timestamp todayTimestamp = new Timestamp(today);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();

        db.collection("bokking")
                .whereEqualTo("hotel_id", hotel_id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalBookings = queryDocumentSnapshots.size();
                    textViewTotalBookings.setText(String.valueOf(totalBookings));
                });

        db.collection("bokking")
                .where(Filter.and(
                        Filter.equalTo("hotel_id",hotel_id),
                        Filter.equalTo("check_in",startOfDay)
                ))

                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int todayCheckInCount = queryDocumentSnapshots.size();
                    textViewTodayCheckIn.setText(String.valueOf(todayCheckInCount));
                });

        db.collection("bokking")
                .where(Filter.and(
                        Filter.equalTo("hotel_id",hotel_id),
                        Filter.equalTo("check_out",startOfDay)
                ))

                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int todayCheckOutCount = queryDocumentSnapshots.size();
                    textViewTodayCheckOut.setText(String.valueOf(todayCheckOutCount));
                });


        db.collection("bokking")
                .where(Filter.and(
                        Filter.equalTo("hotel_id",hotel_id),
                        Filter.equalTo("date",startOfDay)
                ))

                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int upcommingCount = queryDocumentSnapshots.size();
                    textViewUpcomingBookings.setText(String.valueOf(upcommingCount));
                });
    }


    private void getFavoriteCount(String hotelId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("favourite")
                .whereEqualTo("hotel_id", hotelId)  
                .get()  // Get the query results
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Get the count of documents matching the query
                    int favoriteCount = queryDocumentSnapshots.size();

                    // Log the count (optional)
                    Log.d("FavoriteCount", "Favorite count: " + favoriteCount);

                    // Display the count in the TextView
                    favourite_tv.setText(String.valueOf(favoriteCount));
                })
                .addOnFailureListener(e -> Log.e("FavoriteCount", "Error fetching favorite count", e));
    }

    private void getHotelRating(String hotelId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("hotel")
                .whereEqualTo("id", hotelId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            String  ratings = document.getString("ratings");
                            rating_tv.setText(ratings);


                            if(ratings.equals("0.5")){
                                rating_star1.setImageResource(R.drawable.start_half);
                                rating_star2.setImageResource(R.drawable.start_empty);
                                rating_star3.setImageResource(R.drawable.start_empty);
                                rating_star4.setImageResource(R.drawable.start_empty);
                                rating_star5.setImageResource(R.drawable.start_empty);
                            }else if(ratings.equals("1")){
                                rating_star1.setImageResource(R.drawable.star);
                                rating_star2.setImageResource(R.drawable.start_empty);
                                rating_star3.setImageResource(R.drawable.start_empty);
                                rating_star4.setImageResource(R.drawable.start_empty);
                                rating_star5.setImageResource(R.drawable.start_empty);
                            }else if(ratings.equals("1.5")){
                                rating_star1.setImageResource(R.drawable.star);
                                rating_star2.setImageResource(R.drawable.start_half);
                                rating_star3.setImageResource(R.drawable.start_empty);
                                rating_star4.setImageResource(R.drawable.start_empty);
                                rating_star5.setImageResource(R.drawable.start_empty);
                            }else if(ratings.equals("2")){
                                rating_star1.setImageResource(R.drawable.star);
                                rating_star2.setImageResource(R.drawable.star);
                                rating_star3.setImageResource(R.drawable.start_empty);
                                rating_star4.setImageResource(R.drawable.start_empty);
                                rating_star5.setImageResource(R.drawable.start_empty);
                            }else if(ratings.equals("2.5")){
                                rating_star1.setImageResource(R.drawable.star);
                                rating_star2.setImageResource(R.drawable.star);
                                rating_star3.setImageResource(R.drawable.start_half);
                                rating_star4.setImageResource(R.drawable.start_empty);
                                rating_star5.setImageResource(R.drawable.start_empty);
                            }else if(ratings.equals("3")){
                                rating_star1.setImageResource(R.drawable.star);
                                rating_star2.setImageResource(R.drawable.star);
                                rating_star3.setImageResource(R.drawable.star);
                                rating_star4.setImageResource(R.drawable.start_empty);
                                rating_star5.setImageResource(R.drawable.start_empty);
                            }else if(ratings.equals("3.5")){
                                rating_star1.setImageResource(R.drawable.star);
                                rating_star2.setImageResource(R.drawable.star);
                                rating_star3.setImageResource(R.drawable.star);
                                rating_star4.setImageResource(R.drawable.start_half);
                                rating_star5.setImageResource(R.drawable.start_empty);
                            }else if(ratings.equals("4")){
                                rating_star1.setImageResource(R.drawable.star);
                                rating_star2.setImageResource(R.drawable.star);
                                rating_star3.setImageResource(R.drawable.star);
                                rating_star4.setImageResource(R.drawable.star);
                                rating_star5.setImageResource(R.drawable.start_empty);
                            }else if(ratings.equals("4.5")){
                                rating_star1.setImageResource(R.drawable.star);
                                rating_star2.setImageResource(R.drawable.star);
                                rating_star3.setImageResource(R.drawable.star);
                                rating_star4.setImageResource(R.drawable.star);
                                rating_star5.setImageResource(R.drawable.start_half);
                            }else if(ratings.equals("5")){
                                rating_star1.setImageResource(R.drawable.star);
                                rating_star2.setImageResource(R.drawable.star);
                                rating_star3.setImageResource(R.drawable.star);
                                rating_star4.setImageResource(R.drawable.star);
                                rating_star5.setImageResource(R.drawable.star);
                            }



                        }
                    }
                });
    }

    private void setupBarChart(String hotel_id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final int[] pendingCount = {0};
        final int[] paidCount = {0};
        final int[] checkedOutCount = {0};

        db.collection("bokking")
                .whereEqualTo("hotel_id", hotel_id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String status = document.getString("status");

                        if (status != null) {
                            switch (status) {
                                case "Pending":
                                    pendingCount[0]++;
                                    break;
                                case "Paid":
                                    paidCount[0]++;
                                    break;
                                case "Checked Out":
                                    checkedOutCount[0]++;
                                    break;
                            }
                        }
                    }

                    updateBarChart(pendingCount[0], paidCount[0], checkedOutCount[0]);

                })
                .addOnFailureListener(e -> Log.e("Firestore Error", "Error fetching booking data", e));

    }

    private void updateBarChart(int pending, int paid, int checkedOut) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, pending)); // Pending
        entries.add(new BarEntry(1, paid));    // Paid
        entries.add(new BarEntry(2, checkedOut)); // Checked Out

        BarDataSet barDataSet = new BarDataSet(entries, "Booking Status");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        String[] labels = {"Pending", "Paid", "Checked Out"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);

        YAxis leftYAxis = barChart.getAxisLeft();
        leftYAxis.setDrawGridLines(false);
        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);

        barChart.animateY(1000);
        barChart.invalidate();
    }
}
