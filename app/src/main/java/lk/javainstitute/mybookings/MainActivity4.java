package lk.javainstitute.mybookings;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;

public class MainActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SupportMapFragment supportMapFragment = new SupportMapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout2,supportMapFragment);
        fragmentTransaction.commit();


//        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull GoogleMap googleMap) {
//
//                LatLng latLng = new LatLng(7.170132359357029, 79.94526076783906);
//
//                googleMap.animateCamera(
//                        CameraUpdateFactory.newCameraPosition(
//                                new CameraPosition.Builder()
//                                        .target(latLng)
//                                        .zoom(18)
//                                        .build()
//
//                        )
//                );

//                LatLng latLng1 = new LatLng(7.170132359357029, 79.94526076783906);
//                LatLng latLng2 = new LatLng(7.1693640734380555, 79.94611957539301);
//                LatLng latLng3 = new LatLng(7.169154539446937, 79.94647154605217);
//                LatLng latLng4 = new LatLng(7.169154524715285, 79.94803430374002);
//                LatLng latLng5 = new LatLng(7.169755179354865, 79.94835812420897);
//                LatLng latLng6 = new LatLng(7.169992611534289, 79.95094867531584);
//
//                //add polyline
//                googleMap.addPolyline(
//                        new PolylineOptions()
//                                .add(latLng1)
//                                .add(latLng2)
//                                .add(latLng3)
//                                .add(latLng4)
//                                .add(latLng5)
//                                .add(latLng6)
//                                .width(20)
//                                .color(getColor(R.color.color1))
//                );

//                ArrayList<LatLng> lines = new ArrayList<>();
//                lines.add(new LatLng(7.170132359357029, 79.94526076783906));
//                lines.add(new LatLng(7.1693640734380555, 79.94611957539301));
//                lines.add(new LatLng(7.169154539446937, 79.94647154605217));
//                lines.add(new LatLng(7.169154524715285, 79.94803430374002));
//                lines.add(new LatLng(7.169755179354865, 79.94835812420897));
//                lines.add(new LatLng(7.169992611534289, 79.95094867531584));
//
//                //add polyline
//                googleMap.addPolyline(
//                        new PolylineOptions()
//                                .addAll(lines)
//                                .width(20)
//                                .color(getColor(R.color.color1))
//                                .startCap(new RoundCap())
//                                .endCap(new RoundCap())
//                                .jointType(JointType.ROUND)
//                );
//
//
//            }
//        });


        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
//              googleMap.setMyLocationEnabled(true);
//              googleMap.getUiSettings().setCompassEnabled(ture);
//              googleMap.getUiSettings().setZoomControlsEnabled(ture);
            }
        });




    }
}