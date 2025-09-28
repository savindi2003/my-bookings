package lk.javainstitute.mybookings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import lk.javainstitute.mybookings.model.ReservationRoomItem;

public class SummaryActivity extends AppCompatActivity {

    double nights = 0;
    private double totalPrice = 0.0;
    private TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color1));

        totalPriceTextView = findViewById(R.id.textView43);

        Intent i = getIntent();
        ArrayList<String> roomIds = i.getStringArrayListExtra("roomIds");
        ArrayList<Integer> roomCounts = i.getIntegerArrayListExtra("roomCounts");

        String hotel_name = i.getStringExtra("hotel_name");
        String hotel_location = i.getStringExtra("hotel_location");
        String hotel_price = i.getStringExtra("hotel_price");
        String hotel_ratings = i.getStringExtra("hotel_ratings");
        String hotel_id = i.getStringExtra("hotel_id");

        String check_in_date = i.getStringExtra("check_in_date");
        String check_out_date = i.getStringExtra("check_out_date");

        TextView hotelname_t = findViewById(R.id.hotel_name1);
        TextView hoteladddress_t = findViewById(R.id.hotel_address1);
        TextView hotelprice_t = findViewById(R.id.hotel_price1);
        TextView hotelratings_t = findViewById(R.id.hotel_rating1);

        TextView checkin_t = findViewById(R.id.textView39);
        TextView checkout_t = findViewById(R.id.textView41);

        hotelname_t.setText(hotel_name);
        hoteladddress_t.setText(hotel_location);
        hotelprice_t.setText("Rs. "+String.valueOf(hotel_price)+"0");
        hotelratings_t.setText(String.valueOf(hotel_ratings));
        checkin_t.setText(check_in_date);
        checkout_t.setText(check_out_date);

        //night count calculation

        String checkInDateStr = checkin_t.getText().toString();
        String checkOutDateStr = checkout_t.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM", Locale.ENGLISH);
        try {
            Date checkInDate = sdf.parse(checkInDateStr);
            Date checkOutDate = sdf.parse(checkOutDateStr);

            if (checkInDate != null && checkOutDate != null) {
                long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();
                nights = (double) diffInMillies / (1000 * 60 * 60 * 24);

                Toast.makeText(SummaryActivity.this, "Number of nights: " + (int) nights, Toast.LENGTH_SHORT).show();


            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //night count calculation


        Log.d("DEBUG", "Received Room IDs: " + roomIds);
        Log.d("DEBUG", "Received Room Counts: " + roomCounts);

        RecyclerView recyclerView = findViewById(R.id.summary_roomitem);

        if (recyclerView == null) {
            Log.e("ERROR", "RecyclerView not found!");
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (roomIds != null && roomCounts != null) {
            if (roomIds.size() == roomCounts.size()) {

                ReservationAdapter adapter = new ReservationAdapter(roomIds, roomCounts,nights);
                recyclerView.setAdapter(adapter);

            } else {
                Log.e("ERROR", "Room IDs and Counts size mismatch!");
                Toast.makeText(this, "Data mismatch error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("ERROR", "roomIds or roomCounts is NULL!");
            Toast.makeText(this, "No data received!", Toast.LENGTH_SHORT).show();
        }

        Button confirmBtn = findViewById(R.id.button8);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent in = new Intent(SummaryActivity.this, ReservationUserDetailActivity.class);
                in.putExtra("hotel_id",hotel_id);
                in.putExtra("check_in_date",check_in_date);
                in.putExtra("check_out_date",check_out_date);

                in.putStringArrayListExtra("roomIds", roomIds);
                in.putIntegerArrayListExtra("roomCounts", roomCounts);

                in.putExtra("total",totalPrice);

                startActivity(in);

            }
        });


    }
    public void updateTotalPrice(double price) {
        totalPrice += price;
        totalPriceTextView.setText(String.format("%.2f", totalPrice));  // Update total price in the TextView
    }
}
