package lk.javainstitute.mybookings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import lk.javainstitute.mybookings.model.BookingModel;

public class BookingAdminAdapter extends RecyclerView.Adapter<BookingAdminAdapter.ViewHolder>{

    private Context context;
    private List<BookingModel> bookingList;

    public BookingAdminAdapter(Context context, List<BookingModel> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingModel booking = bookingList.get(position);

        holder.reservationId.setText(booking.getReservation_id());
        holder.reservationDate.setText(booking.getDate());
        holder.roomNameId.setText(booking.getRoomName()+" "+booking.getRoom_id());
        holder.client_name.setText(booking.getClient_name());
        holder.client_mobile.setText(booking.getClient_mobile());
        holder.check_in.setText(booking.getCheck_in_date());
        holder.check_out.setText(booking.getCheck_out_date());
        holder.status.setText(booking.getStatus());

        double roomCount = booking.getRoom_count();
        int roomCountInt = (int) roomCount;
        holder.room_count.setText(String.valueOf(roomCountInt)+" Room");

        if(booking.getStatus().equals("Paid")){
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.color1));
        }else if(booking.getStatus().equals("Checked Out")){
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.red));
        }else if(booking.getStatus().equals("Pending")){
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.yellow));

            holder.status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String reservationId = booking.getReservation_id();

                    FirebaseFirestore.getInstance()
                            .collection("bokking")
                            .whereEqualTo("id", reservationId)  // Assuming "id" is the field name for reservationId
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                    // Document found, get the first document from the result
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                    // Get the document reference
                                    DocumentReference bookingRef = FirebaseFirestore.getInstance().collection("bokking").document(document.getId());

                                    // Update the status to "Pending"
                                    bookingRef.update("status", "Paid")
                                            .addOnSuccessListener(aVoid -> {
                                                // Status updated successfully
                                                Toast.makeText(view.getContext(), "Status updated to Pending", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure
                                                Toast.makeText(view.getContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    // Handle case where no matching document is found
                                    Toast.makeText(view.getContext(), "Reservation not found", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reservationId, reservationDate, roomNameId,room_count,client_name,client_mobile,check_in,check_out;

        Button status;

        public ViewHolder(View itemView) {
            super(itemView);
            reservationId = itemView.findViewById(R.id.textView126);
            reservationDate = itemView.findViewById(R.id.textView125);
            roomNameId = itemView.findViewById(R.id.textView127);
            room_count = itemView.findViewById(R.id.textView128);
            client_name = itemView.findViewById(R.id.textView135);
            client_mobile = itemView.findViewById(R.id.textView136);
            check_in = itemView.findViewById(R.id.textView130);
            check_out = itemView.findViewById(R.id.textView132);
            status = itemView.findViewById(R.id.button28);
        }
    }

}
