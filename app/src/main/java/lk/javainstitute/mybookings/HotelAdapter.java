package lk.javainstitute.mybookings;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {
    private Context context;
    private List<Hotel> hotelList;
    private OnHotelClickListener onHotelClickListener;
    private String checkInDate, checkOutDate;

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel);
    }

    public HotelAdapter(Context context, List<Hotel> hotelList,String checkInDate , String checkOutDate) {
        this.context = context;
        this.hotelList = hotelList;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hotel_item, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);


        Log.d("GlideImage", "Image URLs List for " + hotel.getName() + ": " + hotel.getImageUrls());

        if (hotel.getImageUrls() != null && !hotel.getImageUrls().isEmpty()) {
            String imageUrl = hotel.getImageUrls().get(0); // Get the first image URL
            Log.d("GlideImage", "Loading image URL: " + imageUrl); // Debugging
            

            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.loading_icon)  // Placeholder image while loading
                    .error(R.drawable.empty)         // Error image if loading fails
                    .into(holder.hotelImage);


        } else {
            // Set default error image if no image URLs
            Log.e("GlideImage", "No valid image URL for hotel: " + hotel.getName());
            holder.hotelImage.setImageResource(R.drawable.empty); // Default image
        }

        // Set other hotel details
        holder.hotelName.setText(hotel.getName());
        holder.hotelLocation.setText(hotel.getLocation());
        holder.hotelPrice.setText("Rs. " + hotel.getPrice());

        // Ensure consistent rating data type
        holder.hotelRating.setText(String.valueOf(hotel.getRating())); // Use float rating

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, hotel.getPrice(), Toast.LENGTH_SHORT).show();

                String priceString = String.valueOf(hotel.getPrice());

                Intent intent = new Intent(context,SingleViewActivity.class);
                intent.putExtra("hotel_name", hotel.getName());
                intent.putExtra("hotel_location", hotel.getLocation());
                intent.putExtra("hotel_description", hotel.getDescription());
                intent.putExtra("hotel_price", priceString);
                intent.putExtra("hotel_rating", hotel.getRating());
                intent.putExtra("hotel_id", hotel.getId());
                intent.putExtra("CHECK_IN_DATE", checkInDate);
                intent.putExtra("CHECK_OUT_DATE", checkOutDate);
                intent.putStringArrayListExtra("hotel_images",new ArrayList<>(hotel.getImageUrls()));
                intent.putStringArrayListExtra("hotel_amenities",new ArrayList<>(hotel.getAmenities()));
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView hotelImage;
        TextView hotelName, hotelLocation, hotelPrice, hotelRating;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelImage = itemView.findViewById(R.id.hotel_image);
            hotelName = itemView.findViewById(R.id.hotel_name);
            hotelLocation = itemView.findViewById(R.id.hotel_address); // Ensure correct ID
            hotelPrice = itemView.findViewById(R.id.hotel_price);
            hotelRating = itemView.findViewById(R.id.hotel_rating);
        }
    }
}
