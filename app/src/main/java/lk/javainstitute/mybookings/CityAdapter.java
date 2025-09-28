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

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private List<String> cityList;
    private List<Integer> cityImageList;
    private Context context;

    public CityAdapter(List<String> cityList,List<Integer> cityImageList) {
        this.cityList = cityList;
        this.cityImageList = cityImageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String cityName = cityList.get(position);
        holder.cityName.setText(cityName);

        // Placeholder image - Replace with actual image logic
        int imageResourceId = cityImageList.get(position);
        holder.cityImage.setImageResource(imageResourceId);


    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cityImage;
        TextView cityName, cityPrice, cityRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityImage = itemView.findViewById(R.id.hotelImage);
            cityName = itemView.findViewById(R.id.hotelName);

        }
    }
}

