package lk.javainstitute.mybookings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lk.javainstitute.mybookings.model.Attraction;

public class TransportFragment extends Fragment {
    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<Attraction> attractionList;
    private FirebaseFirestore db;

    public TransportFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_fragment_single);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        attractionList = new ArrayList<>();
        adapter = new AttractionAdapter(attractionList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        SharedPreferences prefs = getActivity().getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
        String hotelId = prefs.getString("selected_hotel_id", null);
        if (!hotelId.isEmpty()) {

            loadAttractions(hotelId);

        }

        return view;
    }
    private void loadAttractions(String hotelId) {
        db.collection("hotel")
                .whereEqualTo("id",hotelId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.isEmpty()) {
                        // Assuming the attraction field is an array of maps
                        List<Map<String, Object>> attractions = (List<Map<String, Object>>) documentSnapshot.getDocuments().get(0).get("transport_p");

                        attractionList.clear();
                        for (Map<String, Object> attraction : attractions) {
                            String name = attraction.get("name").toString();
                            String distance = attraction.get("distance").toString();
                            attractionList.add(new Attraction(name, distance));
                        }

                        adapter.notifyDataSetChanged(); // Refresh RecyclerView
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching attractions", e);
                });
    }
}

