package lk.javainstitute.mybookings;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

import lk.javainstitute.mybookings.model.BookingModel;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<BookingModel> bookingList;

    String hotel_id;
    private FirebaseFirestore db;

    public BookingAdapter(Context context, List<BookingModel> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            // Inflate layout for passed
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_past, parent, false);
        } else {
            // Inflate layout for other bookings
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        }

        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingModel booking = bookingList.get(position);

        if (getItemViewType(position) == 0) {

            String imageUrl = booking.getImageResource();
            hotel_id = booking.getHotel_id();

            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.loading_icon)
                    .error(R.drawable.empty)
                    .into(holder.ivHotelImage);

            holder.tvHotelName.setText(booking.getHotelName());
            holder.tvHotelLocation.setText("Check in : "+booking.getCheck_in_date());
            holder.tvResId.setText("#"+booking.getReservation_id());

            if ("Pending".equals(booking.getStatus())) {
                holder.tvPaidStatus.setText("Pending");
                holder.tvPaidStatus.setBackgroundResource(R.drawable.bg_paid_status);
            } else if ("Checked Out".equals(booking.getStatus())) {
                holder.tvPaidStatus.setText("Checked Out");
                holder.tvPaidStatus.setBackgroundResource(R.drawable.bg_unpaid_status);
            } else {
                holder.tvPaidStatus.setText("Paid");
                holder.tvPaidStatus.setBackgroundResource(R.drawable.bg_unpaid_status);
            }


            //add feedback
            holder.btnViewTicket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveReview(hotel_id);
                }
            });

            // add ratings
            holder.btnCancelBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveRatings(hotel_id);
                }
            });

        } else {

            String imageUrl = booking.getImageResource();

            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.loading_icon)
                    .error(R.drawable.empty)
                    .into(holder.ivHotelImage);

            holder.tvHotelName.setText(booking.getHotelName());
            holder.tvHotelLocation.setText("Check in : "+booking.getCheck_in_date());
            holder.tvResId.setText("#"+booking.getReservation_id());


            if ("Pending".equals(booking.getStatus())) {
                holder.tvPaidStatus.setText("Pending");
                holder.tvPaidStatus.setBackgroundResource(R.drawable.bg_paid_status);
            } else if ("Checked Out".equals(booking.getStatus())) {
                holder.tvPaidStatus.setText("Checked Out");
                holder.tvPaidStatus.setBackgroundResource(R.drawable.bg_unpaid_status);
            } else {
                holder.tvPaidStatus.setText("Paid");
                holder.tvPaidStatus.setBackgroundResource(R.drawable.bg_unpaid_status);
            }


            // Handle button clicks
            holder.btnCancelBooking.setOnClickListener(v -> {
                // Handle cancellation
            });

            holder.btnViewTicket.setOnClickListener(v -> {
                Intent intent = new Intent(context, TicketActivity.class);
                intent.putExtra("hotel_name",booking.getHotelName());
                intent.putExtra("client_name",booking.getClient_name());
                intent.putExtra("client_mobile",booking.getClient_mobile());
                intent.putExtra("check_in",booking.getCheck_in_date());
                intent.putExtra("check_out",booking.getCheck_out_date());
                intent.putExtra("res_id",booking.getReservation_id());
                context.startActivity(intent);
            });

            holder.btnOpenMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BookingMapView.class);
                    intent.putExtra("hotel_name",booking.getHotelName());
                    intent.putExtra("hotel_address",booking.getLocation());
                    intent.putExtra("latitude",booking.isLat());
                    intent.putExtra("longitude",booking.isLang());
                    context.startActivity(intent);
                }
            });

        }


    }

    private void saveRatings(String hotel_id){

        LayoutInflater inflater = LayoutInflater.from(context);
        View view1 = inflater.inflate(R.layout.rating_alert, null);

        RatingBar ratingBar = view1.findViewById(R.id.ratingBar);
        Button btnOk = view1.findViewById(R.id.button30);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                SharedPreferences sharedPref = context.getSharedPreferences("lk.javainstitute.mybookings.data",Context.MODE_PRIVATE);
                String user = sharedPref.getString("userEmail",null);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("hotel_id", hotel_id);
                hashMap.put("user_email", user);
                hashMap.put("ratings", v);

                db = FirebaseFirestore.getInstance();
                db.collection("ratings").add(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                //Toast.makeText(context,"Rating submitted",Toast.LENGTH_LONG).show();
                                updateHotelRating(hotel_id);
                            }
                        });


            }
        });


        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view1)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.search_border_rounded);
        }

        dialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void updateHotelRating(String hotelId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get all ratings for this hotel
        db.collection("ratings").whereEqualTo("hotel_id", hotelId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float totalRating = 0;
                        int count = 0;

                        for (DocumentSnapshot doc : task.getResult()) {
                            if (doc.contains("ratings")) {
                                totalRating += doc.getDouble("ratings");
                                count++;
                            }
                        }

                        if (count > 0) {
                            float avgRating = totalRating / count;
                            float roundedRating = (float) (Math.round(avgRating * 2) / 2.0);

                            String rating_s = String.valueOf(roundedRating);

                            // Update hotel document with new average rating
                            db.collection("hotel")
                                    .whereEqualTo("id", hotelId)  // If hotelId is stored as a field
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                            for (DocumentSnapshot doc : task1.getResult()) {
                                                doc.getReference().update("ratings", rating_s)  // Update the field
                                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Hotel rating updated"))
                                                        .addOnFailureListener(e -> Log.e("Firestore", "Failed to update rating", e));
                                            }
                                        }
                                    });

                        }
                    }
                });
    }


    public void saveReview(String hotel_id){

        db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPref = context.getSharedPreferences("lk.javainstitute.mybookings.data",Context.MODE_PRIVATE);
        String user = sharedPref.getString("userEmail",null);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view1 = inflater.inflate(R.layout.new_feedback_dialog, null);


        EditText review_t = view1.findViewById(R.id.editTextText_review);
        Button btnOk = view1.findViewById(R.id.button29);


        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view1)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.search_border_rounded);
        }

        dialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String review = review_t.getText().toString();

                if(review.isEmpty()){

                }else{

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("hotel_id", hotel_id);
                    hashMap.put("user_email", user);
                    hashMap.put("review", review);
                    hashMap.put("date", Timestamp.now());

                    db.collection("reviews").add(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(context,"Review saved successfully",Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });

                }


            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        BookingModel bookingModel = bookingList.get(position);
        if (bookingModel.getStatus() != null && bookingModel.getStatus().equals("Checked Out")) {
            return 0;  // Type 0 for active bookings
        } else {
            return 1;  // Type 1 for other bookings
        }

    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvHotelName, tvHotelLocation, tvPaidStatus,tvResId;
        ImageView ivHotelImage;
        Button btnCancelBooking, btnViewTicket,btnOpenMap;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHotelName = itemView.findViewById(R.id.tvHotelName);
            tvHotelLocation = itemView.findViewById(R.id.tvHotelLocation);
            tvPaidStatus = itemView.findViewById(R.id.tvPaidStatus);
            ivHotelImage = itemView.findViewById(R.id.ivHotelImage);
            btnCancelBooking = itemView.findViewById(R.id.btnCancelBooking);
            btnViewTicket = itemView.findViewById(R.id.btnViewTicket);
            tvResId = itemView.findViewById(R.id.textView51);
            btnOpenMap = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}

