package lk.javainstitute.mybookings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import lk.javainstitute.mybookings.model.Attraction;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {
    private List<Attraction> attractions;

    public AttractionAdapter(List<Attraction> attractions) {
        this.attractions = attractions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attraction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attraction attraction = attractions.get(position);
        holder.nameTextView.setText(attraction.getName());
        holder.distanceTextView.setText(attraction.getDistance());
    }

    @Override
    public int getItemCount() {
        return attractions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, distanceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.attraction_name);
            distanceTextView = itemView.findViewById(R.id.attraction_distance);
        }
    }
}
