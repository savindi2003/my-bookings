package lk.javainstitute.mybookings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.javainstitute.mybookings.model.Amentiy;

public class AmenityAdapter extends RecyclerView.Adapter<AmenityAdapter.ViewHolder> {
    private List<Amentiy> amenityList;
    private Context context;

    public AmenityAdapter(Context context, List<Amentiy> amenityList) {
        this.context = context;
        this.amenityList = amenityList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_amenity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Amentiy amenity = amenityList.get(position);
        holder.iconImageView.setImageResource(amenity.getIcon());
        holder.nameTextView.setText(amenity.getName());
    }

    @Override
    public int getItemCount() {
        return amenityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView nameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.amenity_icon);
            nameTextView = itemView.findViewById(R.id.amenity_name);
        }
    }



}
