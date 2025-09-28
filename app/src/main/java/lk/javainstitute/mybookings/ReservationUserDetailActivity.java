package lk.javainstitute.mybookings;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import lk.javainstitute.mybookings.model.Country;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class ReservationUserDetailActivity extends AppCompatActivity {

    private List<Country> allCountries;
    private List<Country> filteredCountries;
    private CountryAdapter adapter;
    double total;

    private static final int PAYHERE_REQUEST = 11009;
    private Dialog loadingDialog;

    String hotelId;
    String checkInDate;
    String checkOutDate;

    ArrayList<String> roomIds;
    ArrayList<Integer> roomCounts;
    String client_name,client_email,client_mobile,client_country,client_address ;

    private int roomCount;
    private String roomId;
    private NetworkChangeReceiver networkChangeReceiver;

    private Timestamp checkInTimestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservation_user_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        networkChangeReceiver = new NetworkChangeReceiver();

        Intent intent = getIntent();

        if (intent != null) {
            hotelId = intent.getStringExtra("hotel_id");
            checkInDate = intent.getStringExtra("check_in_date");
            checkOutDate = intent.getStringExtra("check_out_date");

            roomIds = intent.getStringArrayListExtra("roomIds");
            roomCounts = intent.getIntegerArrayListExtra("roomCounts");

            total = intent.getDoubleExtra("total", 0.0); // Default value 0.0 if not found

            // Log Data
            Log.d("ReservationDetails", "Hotel ID: " + hotelId);
            Log.d("ReservationDetails", "Check-in Date: " + checkInDate);
            Log.d("ReservationDetails", "Check-out Date: " + checkOutDate);
            Log.d("ReservationDetails", "Room IDs: " + roomIds);
            Log.d("ReservationDetails", "Room Counts: " + roomCounts);
            Log.d("ReservationDetails", "Total Price: " + total);
        }

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        allCountries = getAllCountries();
        filteredCountries = new ArrayList<>(allCountries);

        adapter = new CountryAdapter(ReservationUserDetailActivity.this,filteredCountries);


        ConstraintLayout constraintLayout = findViewById(R.id.layout_destination_country);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountryPickerDialog();

            }
        });

        Button btn = findViewById(R.id.button13);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView name_tv = findViewById(R.id.editTextText_name);
                TextView email_tv = findViewById(R.id.editTextTextEmail);
                TextView mobile_tv = findViewById(R.id.editTextTextMobile);
                TextView country_tv = findViewById(R.id.editTextTextCountry);
                TextView address_tv = findViewById(R.id.editTextTextAddress);

                client_name = name_tv.getText().toString();
                client_email = email_tv.getText().toString();
                client_mobile = mobile_tv.getText().toString();
                client_country = country_tv.getText().toString();
                client_address = address_tv.getText().toString();

                if (client_name.isEmpty()) {
                    name_tv.setError("Please enter client Full Name");
                } else if (client_email.isEmpty()) {
                    email_tv.setError("Please enter client's Email");
                } else if (!client_email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    email_tv.setError("Please enter a valid email address");
                } else if (client_mobile.isEmpty()) {
                    mobile_tv.setError("Please enter client's mobile number");
                }else if (client_country.isEmpty()) {
                    country_tv.setError("Please select the country");
                } else if (client_address.isEmpty()) {
                    address_tv.setError("Please enter client's address");
                } else {
                    // Proceed with your logic, e.g., submit the data
                    startSandboxPayment(total);
                }


            }
        });


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


    private void saveFirebase(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (checkInDate == null || checkOutDate == null || checkInDate.isEmpty() || checkOutDate.isEmpty()) {
            Log.e("Firestore", "Error: check-in or check-out date is null or empty");
            return;
        }

        if (!checkInDate.matches(".*\\d{4}$")) {
            checkInDate += " " + Calendar.getInstance().get(Calendar.YEAR);
        }
        if (!checkOutDate.matches(".*\\d{4}$")) {
            checkOutDate += " " + Calendar.getInstance().get(Calendar.YEAR);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM yyyy", Locale.ENGLISH);
        checkInTimestamp = null;
        Timestamp checkOutTimestamp = null;

        try {
            Date checkInDate1 = sdf.parse(checkInDate);
            Date checkOutDate1 = sdf.parse(checkOutDate);
            if (checkInDate1 != null && checkOutDate1 != null) {
                checkInTimestamp = new Timestamp(checkInDate1);
                checkOutTimestamp = new Timestamp(checkOutDate1);
            } else {
                Log.e("Firestore", "Error: Date parsing failed.");
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Firestore", "Error parsing date: " + e.getMessage());
            return;
        }

        for (int i = 0; i < roomIds.size(); i++) {
            roomId = roomIds.get(i);
            roomCount = roomCounts.get(i);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("check_in", checkInTimestamp);  // Example values
            hashMap.put("check_out", checkOutTimestamp);
            hashMap.put("client_address",client_address);
            hashMap.put("client_country", client_country);
            hashMap.put("client_email", client_email);
            hashMap.put("client_mobile", client_mobile);
            hashMap.put("client_name", client_name);
            hashMap.put("date", Timestamp.now());
            hashMap.put("hotel_id", hotelId);
            hashMap.put("id", generateReservationId());
            hashMap.put("room_count", roomCount);
            hashMap.put("room_id", roomId);
            hashMap.put("status", "Pending");

            // Save each room reservation as a separate document
            firestore.collection("bokking").add(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                           updateRoomCount(roomId,roomCount);


                             //
                            LayoutInflater inflater = LayoutInflater.from(ReservationUserDetailActivity.this);
                            View view = inflater.inflate(R.layout.custom_alert, null);

                            // Initialize views from layout
                            TextView tvTitle = view.findViewById(R.id.tvTitle);
                            TextView tvMessage = view.findViewById(R.id.tvMessage);
                            Button btnOk = view.findViewById(R.id.btnOk);

                            // Customize text dynamically (Optional)
                            tvTitle.setText("Reservation Completed");
                            tvMessage.setText("Reservation completed successfully! Your booking is now confirmed. You can view, manage, or modify your reservation details in the Booking tab. Thank you for choosing our service");

                            // Create and show dialog
                            AlertDialog dialog = new AlertDialog.Builder(ReservationUserDetailActivity.this)
                                    .setView(view)
                                    .setCancelable(false)  // Prevents dialog from closing when clicking outside
                                    .create();

                            if (dialog.getWindow() != null) {
                                dialog.getWindow().setBackgroundDrawableResource(R.drawable.search_border_rounded);
                            }

                            dialog.show();


                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();

                                    String bookingId = generateReservationId();
                                    viewNotofication(bookingId);

                                    Intent intent = new Intent(ReservationUserDetailActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                }
                            });


                            //
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //
                            LayoutInflater inflater = LayoutInflater.from(ReservationUserDetailActivity.this);
                            View view = inflater.inflate(R.layout.custom_error_alert, null);

                            // Initialize views from layout
                            TextView tvTitle = view.findViewById(R.id.tvTitle);
                            TextView tvMessage = view.findViewById(R.id.tvMessage);
                            Button btnOk = view.findViewById(R.id.btnOk);

                            // Customize text dynamically (Optional)
                            tvTitle.setText("Oops ! Something went wrong");
                            tvMessage.setText("Failed to complete the action. Please try again or contact support");

                            // Create and show dialog
                            AlertDialog dialog = new AlertDialog.Builder(ReservationUserDetailActivity.this)
                                    .setView(view)
                                    .setCancelable(false)  // Prevents dialog from closing when clicking outside
                                    .create();

                            if (dialog.getWindow() != null) {
                                dialog.getWindow().setBackgroundDrawableResource(R.drawable.search_border_rounded);
                            }

                            dialog.show();


                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();

                                    Intent intent = new Intent(ReservationUserDetailActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
        }
    }



    private void viewNotofication(String id){
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
        Notification notification = new NotificationCompat.Builder(ReservationUserDetailActivity.this, "C1")
                .setContentTitle("Booking Confirmed")
                .setContentText("Your booking with ID: " + id + " has been successfully confirmed. We look forward to your stay!")
                .setSmallIcon(R.drawable.b)
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);


        //
    }

    private void updateRoomCount(String room_id, int booked_count) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("room")
                .whereEqualTo("id", room_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {

                            DocumentSnapshot document = task.getResult().getDocuments().get(0);


                            Long count = document.getLong("count");
                            if (count == null) {
                                Log.e("Firestore", "Room count is null.");
                                return;
                            }

                            int available_count = count.intValue() - booked_count;

                            if (available_count < 0) {
                                Log.e("Firestore", "Not enough rooms available.");
                                return;
                            }

                            // Update the room count using document reference
                            document.getReference().update("count", available_count)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "Room count updated successfully!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Firestore", "Error updating room count", e);
                                        }
                                    });
                        } else {
                            Log.e("Firestore", "No matching room found.");
                        }
                    }
                });
    }



    private void startSandboxPayment(double total) {
        showDimBackground();

        InitRequest req = new InitRequest();
        req.setMerchantId("1221148");       // Merchant ID
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(total);             // Final Amount to be charged
        req.setOrderId(generateOrderId());        // Unique Reference ID
        req.setItemsDescription("Door bell wireless");  // Item description title
        req.setCustom1("This is the custom message 1");
        req.setCustom2("This is the custom message 2");
        req.getCustomer().setFirstName("Saman");
        req.getCustomer().setLastName("Perera");
        req.getCustomer().setEmail("samanp@gmail.com");
        req.getCustomer().setPhone("+94771234567");
        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");


        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        startActivityForResult(intent, PAYHERE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("PayHere", "onActivityResult called"); // Check if the method is even being called

        if (requestCode == PAYHERE_REQUEST) {
            Log.d("PayHere", "Request code matches");

            if (data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                Log.d("PayHere", "Data has extra result");

                PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

                if (response != null) {
                    Log.d("PayHere", "Response is not null");
                    Log.d("PayHere", "Response: " + response.toString()); // Log the entire response!

                    if (response.isSuccess()) {
                        Log.d("PayHere", "Payment Success");
                        hideDimBackground();
                        saveFirebase();


                    } else {
                        Log.e("PayHere", "Payment Failed: " + response.toString()); // Log the error message
                        // ...
                    }
                } else {
                    Log.d("PayHere", "Response is null");
                }
            } else {
                Log.d("PayHere", "Data does not have extra result");
            }
        }
    }

    private String generateOrderId() {
        return String.valueOf(System.currentTimeMillis()); // Millisecond timestamp is a common approach
    }
    private String generateReservationId() {
        //return String.valueOf(System.currentTimeMillis()); // Millisecond timestamp is a common approach
        Random random = new Random();
        int number = 10000 + random.nextInt(900000);
        return "R" + number;
    }

    private void showDimBackground() {
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_dim_background);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void hideDimBackground() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }



    AlertDialog dialog;

    // Method to show the country picker dialog
    private void showCountryPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the dialog view layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_country_picker, null);
        AutoCompleteTextView searchEditText = dialogView.findViewById(R.id.searchEditText);
        ListView countryListView = dialogView.findViewById(R.id.countryListView);

        countryListView.setAdapter(adapter);

        // Click listener for the country list items
        countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Country selectedCountry = filteredCountries.get(position);
                // Handle the selected country here (you can do whatever you want with it)
                // For example, you can display it in a TextView or save it to a variable.
//                Toast.makeText(ReservationUserDetailActivity.this,
//                        "Selected Country: " + selectedCountry.getName()
//                                + "\nCountry Code: " + selectedCountry.getCode()
//                                + "\nPhone Code: " + selectedCountry.getPhoneCode(),
//                        Toast.LENGTH_SHORT).show();

                EditText countryEt = findViewById(R.id.editTextTextCountry);
                countryEt.setText(selectedCountry.getName());


                dialog.getWindow().setLayout(300, 400);

                // Close the dialog
                countryListView.clearFocus();
                searchEditText.clearFocus();
                dialog.dismiss();
            }
        });


// TextWatcher for filtering countries based on user input
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterCountries(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        builder.setView(dialogView);
        builder.setNegativeButton("Cancel", null);

        dialog = builder.create();
        dialog.show();

        // Clear the search bar before opening the dialog again
        searchEditText.setText("");
    }

    // Method to filter the countries based on the search query
    private void filterCountries(String searchText) {
        filteredCountries.clear();
        for (Country country : allCountries) {
            if (country.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredCountries.add(country);
            }
        }
        adapter.notifyDataSetChanged();
    }

    // Method to get the list of all countries from a JSON file
    private List<Country> getAllCountries() {
        List<Country> countries = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open("countries.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Country>>() {
            }.getType();
            countries = gson.fromJson(json, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countries;
    }

}

// Custom ArrayAdapter for displaying countries in the ListView
class CountryAdapter extends ArrayAdapter<Country> {

    CountryAdapter(Context context, List<Country> countries) {
        super(context, android.R.layout.simple_list_item_1, countries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.quicksand_regular);
        textView.setTypeface(typeface);

        Country country = getItem(position);
        if (country != null) {
            // Use EmojiCompat to display the flag Emoji

            String countryText = country.getName();
            textView.setText(countryText);
        }
        return convertView;
    }
}


