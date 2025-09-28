package lk.javainstitute.mybookings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import lk.javainstitute.mybookings.model.Room;

public class RoomAdapter2 extends RecyclerView.Adapter<RoomAdapter2.RoomViewHolder> {
    private List<Room> roomList;
    private Context context;

    public RoomAdapter2(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_room_item, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        // Set data to views
        holder.roomName.setText(room.getName());
        holder.roomId.setText(room.getId());
        holder.roomMaxCount.setText(String.valueOf(room.getAvailabe_count()));
        holder.roomsCount.setText(String.valueOf(room.getCount()));
        holder.pricePerNight.setText("Rs. " + room.getPricePerNight());
        holder.maxGuests.setText(room.getMaxPerson());

        // Load image from Firebase storage
        Picasso.get().load(room.getImageUrl()).into(holder.roomImage);
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomName, roomsCount,roomMaxCount, pricePerNight, maxGuests,roomId;
        ImageView roomImage;

        public RoomViewHolder(View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.textView111);
            roomId = itemView.findViewById(R.id.textView23);
            roomsCount = itemView.findViewById(R.id.textView116);
            roomMaxCount = itemView.findViewById(R.id.textView118);
            pricePerNight = itemView.findViewById(R.id.textView120);
            maxGuests = itemView.findViewById(R.id.textView122);
            roomImage = itemView.findViewById(R.id.imageView31);
        }
    }
}

