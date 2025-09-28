package lk.javainstitute.mybookings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.javainstitute.mybookings.model.Room;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private Context context;
    private List<Room> roomList;


    private Map<String, Integer> selectedRooms = new HashMap<>();


    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_item, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        Log.d("RoomAdapter", "Room list size: " + roomList.size());

        holder.textViewName.setText(room.getName());
        //holder.imageViewRoom.setImageResource(room.getImageUrl());

        String imageUrl = room.getImageUrl();

        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.loading_icon)
                .error(R.drawable.empty)
                .into(holder.imageViewRoom);

        holder.textViewPersonCount.setText(room.getMaxPerson());
        holder.textViewPricePerNight.setText("Rs. "+String.valueOf(room.getPricePerNight())+"0");

        holder.bedText.setText(room.getBed());
        holder.sizeText.setText(room.getSize());
        holder.bathroomText.setText(room.getBathroom());
        holder.soundText.setText(room.getSound());
        holder.wifiText.setText(room.getWifi());
        holder.balconyText.setText(room.getBalcony());
        holder.viewText.setText(room.getView());

        double roomCountDouble = room.getRoomCount();
        int roomCountInt = (int) Math.round(roomCountDouble);

        holder.roomCount.setText(String.valueOf(roomCountInt));

        String roomCountPass = String.valueOf(roomCountInt);

        holder.roomCountBtn.setOnClickListener(v -> showCustomDialog(position,roomCountPass,holder));

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(context, room.getId(), Toast.LENGTH_SHORT).show();

                    String buttonText = holder.roomCountBtn.getText().toString();
                    String numberOnly = buttonText.replaceAll("[^0-9]", "");

                    int count = Integer.parseInt(numberOnly);
                    selectedRooms.put(room.getId(), count);

                }else{
                    selectedRooms.remove(room.getId());
                }
            }
        });





    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public Map<String,Integer> getSelectedRooms() {
        return selectedRooms;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPersonCount, textViewCost, textViewCreditCard, textViewPricePerNight,bedText,sizeText,bathroomText,soundText,wifiText,balconyText,viewText,roomCount;
        ImageView imageViewRoom;
        CheckBox checkBox;

        Button roomCountBtn;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textView23);
            imageViewRoom = itemView.findViewById(R.id.imageView14);

            bedText = itemView.findViewById(R.id.textView_bed);
            sizeText = itemView.findViewById(R.id.textView_size);
            balconyText = itemView.findViewById(R.id.textView_balcony);
            bathroomText = itemView.findViewById(R.id.textView_bathroom);
            soundText = itemView.findViewById(R.id.textView_sound);
            wifiText = itemView.findViewById(R.id.textView_wify);
            viewText = itemView.findViewById(R.id.textView_view);

            textViewPersonCount = itemView.findViewById(R.id.textView_personcount);
            textViewCost = itemView.findViewById(R.id.textView_cost);
            textViewCreditCard = itemView.findViewById(R.id.textView_creditcard);
            textViewPricePerNight = itemView.findViewById(R.id.textView28);
            checkBox = itemView.findViewById(R.id.checkBox);

            roomCount = itemView.findViewById(R.id.textView31);

            roomCountBtn = itemView.findViewById(R.id.button5);
            CheckBox checkBox = itemView.findViewById(R.id.checkBox);



        }
    }

    private void showCustomDialog(int position,String roomCountPass,RoomViewHolder holder) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.room_count);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.search_border_rounded);

        dialog.show();

        Button btnDecrease = dialog.findViewById(R.id.button3);
        Button btnIncrease = dialog.findViewById(R.id.button4);
        Button apply_btn= dialog.findViewById(R.id.button7);

        TextView roomCount = dialog.findViewById(R.id.editTextText);
        roomCount.setText(roomCountPass);

        TextView t33 = dialog.findViewById(R.id.textView33);
        t33.setText(roomCountPass);

        int availabecountInt = Integer.parseInt(roomCountPass);

        int[] count = {1};
        roomCount.setText(String.valueOf(count[0]));

        // Increase Room Count
        btnIncrease.setOnClickListener(v -> {
            if (count[0] < availabecountInt) { // Check if count is within limit
                count[0]++;
                roomCount.setText(String.valueOf(count[0]));
            }
        });

        // Decrease Room Count (Prevent going below 1)
        btnDecrease.setOnClickListener(v -> {
            if (count[0] > 1) {
                count[0]--; // Decrease count
                roomCount.setText(String.valueOf(count[0])); // Update UI
            }
        });

        apply_btn.setOnClickListener(view -> {

            String updatedRoomCount = roomCount.getText().toString();
            holder.roomCountBtn.setText(updatedRoomCount+" Room");



            dialog.dismiss();

        });


    }
}

//balcony
//bathroom
//bed
//count - int
//hotel_id
//id
//image
//name
//person
//price_per_night - int
//room_type
//size
//sound
//view
//wifi