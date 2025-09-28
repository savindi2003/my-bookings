package lk.javainstitute.mybookings;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.javainstitute.mybookings.model.EatModel;
import lk.javainstitute.mybookings.model.PlaceModel;

public class EatAndDrinkAdapter extends RecyclerView.Adapter<EatAndDrinkAdapter.ViewHolder>{
    private List<EatModel> placeList;

    public EatAndDrinkAdapter(List<EatModel> placeList) {
        this.placeList = placeList;
    }

    public void addEmptyItem() {
        placeList.add(0, new EatModel("", ""));
        notifyItemInserted(0);
    }

    public List<EatModel> getPlaceList() {
        return placeList;
    }


    @NonNull
    @Override
    public EatAndDrinkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.property_item, parent, false);
        return new EatAndDrinkAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EatAndDrinkAdapter.ViewHolder holder, int position) {
        EatModel place = placeList.get(position);
        holder.nameTextView.setText(place.getName());
        holder.distanceTextView.setText(place.getDistance());

        holder.nameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placeList.get(holder.getAdapterPosition()).setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.distanceTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placeList.get(holder.getAdapterPosition()).setDistance(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText nameTextView, distanceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.editText_propertyname);
            distanceTextView = itemView.findViewById(R.id.editText_property_km);
        }
    }

    public void saveToFirebase(String hotelId) {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, String>> propertiesArray = new ArrayList<>();

        for (EatModel place : placeList) {
            Map<String, String> property = new HashMap<>();
            property.put("name", place.getName());
            property.put("distance", place.getDistance());
            propertiesArray.add(property);
            Log.i("PlaceAdapter",place.getName()+" "+place.getDistance()+" "+hotelId);

        }



//        data.put("properties", propertiesArray);
//
//        db.collection("hotel").document(hotelId)
//                .set(data, SetOptions.merge())
//                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Data successfully stored"))
//                .addOnFailureListener(e -> Log.e("Firestore", "Error storing data", e));
    }
}
