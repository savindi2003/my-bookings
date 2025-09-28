package lk.javainstitute.mybookings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.chip.Chip;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.MyViewHolder> {

    private List<String> dataList; // Data source for the RecyclerView

    private OnChipClickListener listener;

    public interface OnChipClickListener {
        void onChipClick(String chipText);
    }


    public ChipAdapter(List<String> chipList, OnChipClickListener listener) {
        this.dataList = chipList;
        this.listener = listener;
    }

    // Called when a new view is created
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout for RecyclerView (chip_item.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chip_item, parent, false);
        return new MyViewHolder(view);
    }

    // Binds the data to the views in the item
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String data = dataList.get(position);
        holder.chip.setText(data); // Set the data to the Chip

        holder.chip.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChipClick(data);  // Pass chip text to listener
            }
        });
    }

    // Return the total count of items in the data list
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder class that holds the item views
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public MyViewHolder(View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip); // Reference to the Chip in chip_item.xml
        }
    }
}
