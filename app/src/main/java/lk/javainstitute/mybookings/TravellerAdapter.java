package lk.javainstitute.mybookings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import lk.javainstitute.mybookings.model.Traveller;

public class TravellerAdapter extends RecyclerView.Adapter<TravellerAdapter.ViewHolder> {
    private Context context;
    private List<Traveller> travellerList;
    private int circleColor;

    public TravellerAdapter(Context context, List<Traveller> travellerList, int color) {
        this.context = context;
        this.travellerList = travellerList;
        this.circleColor = color;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_traveller, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Traveller traveller = travellerList.get(position);
        holder.nameTextView.setText(traveller.getName());
        holder.letterTextView.setText(String.valueOf(traveller.getFirstLetter()));

        holder.circleView.setCardBackgroundColor(circleColor);
    }

    @Override
    public int getItemCount() {
        return travellerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView letterTextView, nameTextView;
        CardView circleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            letterTextView = itemView.findViewById(R.id.letterTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            circleView = itemView.findViewById(R.id.circleView);
        }
    }
}

