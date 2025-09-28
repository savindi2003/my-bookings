package lk.javainstitute.mybookings;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

public class ActivityDashboard extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private String hotel_id;

    private NetworkChangeReceiver networkChangeReceiver;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        networkChangeReceiver = new NetworkChangeReceiver();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        SharedPreferences sharedPref = ActivityDashboard.this.getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
        String hotel_email = sharedPref.getString("hotel", null);

        if (hotel_email != null) {
            firestore = FirebaseFirestore.getInstance();

            firestore.collection("hotel")
                    .whereEqualTo("email", hotel_email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                hotel_id = document.getString("id");

                                SharedPreferences sharedPref1 = getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref1.edit();
                                editor.putString("hotel_id", hotel_id);
                                editor.apply();


                                listenForNewDocuments(hotel_id);





                            }
                        }
                    });

        }else{
            Toast.makeText(ActivityDashboard.this,"No hotel email",Toast.LENGTH_LONG).show();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_dashboard,new Fragment_Admin1()).commit();

    }

    private void listenForNewDocuments(String hotel_id ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bookingsRef = db.collection("bokking");

        listenerRegistration = bookingsRef
                .whereEqualTo("hotel_id", hotel_id) // Filter by hotel_id
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Firestore", "Error fetching documents: ", e);
                            return;
                        }

                        if (snapshots != null) {
                            for (DocumentChange docChange : snapshots.getDocumentChanges()) {
                                if (docChange.getType() == DocumentChange.Type.ADDED) {
                                    Log.d("Firestore", "New document added: " + docChange.getDocument().getData());
                                    //Toast.makeText(ActivityDashboard.this,"New doc add",Toast.LENGTH_LONG).show();

                                    String bookingId = docChange.getDocument().getString("id");
                                    String roomId = docChange.getDocument().getString("room_id");

                                    viewNotofication(bookingId,roomId);

                                }
                            }
                        }
                    }
                });
    }

    private void viewNotofication(String id,String room_id){
        //
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "C1",
                    "Channel1",
                    NotificationManager.IMPORTANCE_HIGH  // Set importance to HIGH for heads-up notification
            );

            notificationManager.createNotificationChannel(notificationChannel);
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.large_b);
        Notification notification = new NotificationCompat.Builder(ActivityDashboard.this, "C1")
                .setContentTitle("Room no: " +room_id+ " Booking Received")
                .setContentText("New booking with ID: " + id + " has been made. Check the details now!")
                .setSmallIcon(R.drawable.b)
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);


        //
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.dashboard1:
                    selectedFragment = new Fragment_Admin1();
                    break;
                case R.id.dashboard2:
                    selectedFragment = new Fragment_Admin2();
                    break;
                case R.id.dashboard3:
                    selectedFragment = new Fragment_Admin3();
                    break;
                case R.id.dashboard4:
                    selectedFragment = new Fragment_Admin4();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_dashboard,selectedFragment).commit();
            return true;
        }
    };
}