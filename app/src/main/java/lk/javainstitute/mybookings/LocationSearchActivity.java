package lk.javainstitute.mybookings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lk.javainstitute.mybookings.model.Location;

public class LocationSearchActivity extends AppCompatActivity {
    private GoogleMap googleMap;
    private SearchView searchView;
    private List<Location> locations = new ArrayList<>();
    private List<Location> hotel_locations = new ArrayList<>();
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCZQdzReFFYZyqxjso9iS8b5_jnLKUXy2Y");
        }

        placesClient = Places.createClient(this);


        searchView = findViewById(R.id.searchView);

        fetchAndSaveHotelsToSQLite();

        SupportMapFragment supportMapFragment = new SupportMapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layoutmap, supportMapFragment);
        fragmentTransaction.commit();

            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap map) {
                    googleMap = map;
                    hotel_locations = getLocationsFromDatabase();

                    for (Location location : hotel_locations) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(location.getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon)) // Use your custom image
                        );

                    }

                    if (!hotel_locations.isEmpty()) {
                        LatLng firstLocation = new LatLng(hotel_locations.get(0).getLatitude(), hotel_locations.get(0).getLongitude());
                        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(firstLocation)
                                .zoom(10)
                                .build()));
                    }

                    setupSearchFunctionality();

                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            showHotelDetails(marker.getTitle());
                            return true;
                        }
                    });
                }
            });




    }

    private void setupSearchFunctionality() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               // searchHotel(query);
                searchLandmarks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //searchHotel(newText);
                searchLandmarks(newText);
                return true;
            }
        });
    }

    private void searchLandmarks(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setTypeFilter(TypeFilter.ESTABLISHMENT) // You can use other filters like TypeFilter.GEOCODE for address
                .setSessionToken(token)
                .build();

        placesClient.findAutocompletePredictions(predictionsRequest)
                .addOnSuccessListener((response) -> {
                    googleMap.clear();
                    addHotelMarkers();

                    if (!response.getAutocompletePredictions().isEmpty()) {
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            String placeId = prediction.getPlaceId();
                            getPlaceDetails(placeId);
                        }
                    } else {
                        Toast.makeText(LocationSearchActivity.this, "No landmarks found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener((exception) -> {
                    Toast.makeText(LocationSearchActivity.this, "Failed to search landmarks", Toast.LENGTH_SHORT).show();
                });
    }

    private void addHotelMarkers() {
        for (Location location : hotel_locations) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(location.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon)) // Use your custom image
            );
        }
    }

    private void getPlaceDetails(String placeId) {
        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME))
                .build();

        placesClient.fetchPlace(placeRequest)
                .addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    }
                })
                .addOnFailureListener((exception) -> {
                    Log.e("PlacesAPI", "Failed to fetch place details: " + exception.getMessage());
                });
    }

    private void searchHotel(String hotelName) {
        googleMap.clear();
        boolean found = false;

        for (Location location : locations) {
            if (location.getName().toLowerCase().contains(hotelName.toLowerCase())) {
                found = true;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLng).title(location.getName()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            }
        }

        if (!found) {
            Toast.makeText(this, "Hotel not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showHotelDetails(String hotelName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("hotel")
                .whereEqualTo("name", hotelName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String name = document.getString("name");
                        String description = document.getString("description");
                        String location = document.getString("location");
                        String imageUrl = ((List<String>) document.get("image")).get(0);
                        double price = document.getDouble("price") != null ? document.getDouble("price") : 0.0;
                        float rating = Float.parseFloat(document.getString("ratings"));

                        showPopup(name, imageUrl, price, rating, location, description);
                    } else {
                        Log.e("Firestore", "No hotel found with this name.");
                    }
                });
    }

    private void showPopup(String hotelName, String imageUrl, double price, float rating, String address, String description) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.hotel_map_item, null);
        ((TextView) popupView.findViewById(R.id.hotel_name)).setText(hotelName);
        ((TextView) popupView.findViewById(R.id.hotel_price)).setText("Rs. " + price);
        ((TextView) popupView.findViewById(R.id.hotel_rating)).setText(String.valueOf(rating));
        ((TextView) popupView.findViewById(R.id.hotel_address)).setText(address);
        ((TextView) popupView.findViewById(R.id.textView177)).setText(description);

        ImageView imageView = popupView.findViewById(R.id.hotel_image);
        Glide.with(this).load(imageUrl).into(imageView);

        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private List<Location> getLocationsFromDatabase() {
        SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "mybookings.db", null, 1);
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        List<Location> locations = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT name, latitude, longitude FROM location", null);
        while (cursor.moveToNext()) {
            locations.add(new Location(cursor.getString(0), cursor.getDouble(1), cursor.getDouble(2)));
        }
        cursor.close();
        db.close();
        return locations;
    }

    private void fetchAndSaveHotelsToSQLite() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("hotel").addSnapshotListener((snapshot, e) -> {
            if (snapshot != null) {
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    String name = document.getString("name");
                    GeoPoint geoPoint = document.getGeoPoint("map_location");
                    if (geoPoint != null) {
                        saveLocation(name, geoPoint.getLatitude(), geoPoint.getLongitude());
                    }
                }
            }
        });
    }

    private void saveLocation(String name, double latitude, double longitude) {
        SQLiteHelper sqLiteHelper = new SQLiteHelper(this, "mybookings.db", null, 1);
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
        db.execSQL("INSERT OR IGNORE INTO location (name, latitude, longitude) VALUES (?, ?, ?)",
                new Object[]{name, latitude, longitude});
        db.close();
    }


}