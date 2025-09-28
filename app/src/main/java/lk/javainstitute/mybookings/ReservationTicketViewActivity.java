package lk.javainstitute.mybookings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReservationTicketViewActivity extends AppCompatActivity {

    private  TextView reservation_id_tv , room_tv,check_in_tv,check_out_tv,guest_name_tv,guest_email_tv,guest_mobile_tv,guest_address_tv,guest_country_tv;
    private String reservation_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservation_ticket_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reservation_id_tv = findViewById(R.id.textView147);
        room_tv = findViewById(R.id.textView151);
        check_in_tv = findViewById(R.id.textView153);
        check_out_tv = findViewById(R.id.textView155);
        guest_name_tv = findViewById(R.id.textView157);
        guest_email_tv = findViewById(R.id.textView158);
        guest_mobile_tv = findViewById(R.id.textView160);
        guest_address_tv = findViewById(R.id.textView168);
        guest_country_tv = findViewById(R.id.textView161);

        Intent intent = getIntent();

        if (intent != null) {
            reservation_id = intent.getStringExtra("reservation_id");

            reservation_id_tv.setText(reservation_id);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("bokking")
                    .whereEqualTo("id", reservation_id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                String room_id = document.getString("room_id");
                                Double room_count = document.getDouble("room_count");

                                Timestamp date = document.getTimestamp("date");
                                Timestamp check_in_date = document.getTimestamp("check_in");
                                Timestamp check_out_date = document.getTimestamp("check_out");

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                                String date_s = (date != null) ? sdf.format(date.toDate()) : "N/A";
                                String check_in_s = (check_in_date != null) ? sdf.format(check_in_date.toDate()) : "N/A";
                                String check_out_s = (check_out_date != null) ? sdf.format(check_out_date.toDate()) : "N/A";

                                double count_d = room_count;

                                String client_name = document.getString("client_name");
                                String client_mobile = document.getString("client_mobile");
                                String client_email = document.getString("client_email");
                                String client_address = document.getString("client_address");
                                String client_country = document.getString("client_country");


                                int roomCountInt = (int) count_d;

                                room_tv.setText(room_id+" ( "+String.valueOf(roomCountInt)+" )");
                                check_in_tv.setText(check_in_s);
                                check_out_tv.setText(check_out_s);
                                guest_name_tv.setText(client_name);
                                guest_email_tv.setText(client_email);
                                guest_mobile_tv.setText(client_mobile);
                                guest_address_tv.setText(client_address);
                                guest_country_tv.setText(client_country);


                            }
                        }

                    });

        }
    }
}