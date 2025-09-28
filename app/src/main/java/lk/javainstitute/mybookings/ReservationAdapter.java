package lk.javainstitute.mybookings;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lk.javainstitute.mybookings.model.ReservationRoomItem;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {
    private final List<String> roomIds;
    private final List<Integer> roomCounts;

    private double totalPrice = 0.0;
    double nights;


    public ReservationAdapter(List<String> roomIds, List<Integer> roomCounts , Double nights) {
        this.roomIds = roomIds;
        this.roomCounts = roomCounts;
        this.nights = nights;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_room_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.roomIdTextView.setText("Room ID: " + roomIds.get(position));
        holder.roomCountTextView.setText(roomCounts.get(position)+" Rooms");


        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("room")
                .whereEqualTo("id", roomIds.get(position))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            QuerySnapshot querySnapshot = task.getResult();

                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String roomName = document.getString("name");
                                Double price_per_night = document.getDouble("price_per_night");


                               // double price_per_night_d = price_per_night;
                                double price_per_night_d = price_per_night != null ? price_per_night : 0.0;
                                double totalRoomPrice = price_per_night_d * roomCounts.get(position);
                                double totalRoomPriceTotal = totalRoomPrice * nights;


                                String price = String.valueOf(price_per_night_d);
                                String total_room_price = String.valueOf(totalRoomPriceTotal);

                                holder.roomNameTextView.setText(roomName);
                                holder.roomPriceTextView.setText(price);
                                holder.totalRoomPrice.setText(total_room_price);
                                holder.nightCount.setText("Total ( For "+(int) nights+" Nights )");

                                totalPrice += totalRoomPriceTotal;

                                if (position == roomIds.size() - 1) {
                                    // This is the last item, so update the total price TextView
//                                    TextView totalPriceTextView = ((SummaryActivity) holder.itemView.getContext()).findViewById(R.id.textView43);
//                                    totalPriceTextView.setText(totalPrice+"0");

                                    ((SummaryActivity) holder.itemView.getContext()).updateTotalPrice(totalPrice);
                                }


                            }
                        } else {

                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });





    }

    @Override
    public int getItemCount() {
        return roomIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomIdTextView, roomCountTextView , roomNameTextView , roomPriceTextView,totalRoomPrice,nightCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomIdTextView = itemView.findViewById(R.id.textView44);
            roomCountTextView = itemView.findViewById(R.id.textView361);
            roomNameTextView = itemView.findViewById(R.id.textView43);
            roomPriceTextView = itemView.findViewById(R.id.textView362);
            totalRoomPrice = itemView.findViewById(R.id.textView363);
            nightCount = itemView.findViewById(R.id.textView442);
        }
    }
}

