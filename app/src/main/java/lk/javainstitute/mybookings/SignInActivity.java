package lk.javainstitute.mybookings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

import lk.javainstitute.mybookings.model.EmailValidator;

public class SignInActivity extends AppCompatActivity {

    public TextView errorMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText email = findViewById(R.id.email_signin);
        TextInputEditText password = findViewById(R.id.password_signin);
        errorMessageTextView = findViewById(R.id.signInErrorText);

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                errorMessageTextView.setVisibility(View.GONE);
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                errorMessageTextView.setVisibility(View.GONE);
            }
        });



        Button signInButton = findViewById(R.id.button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailInput = email.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                if (emailInput.isEmpty()) {
                    errorMessageTextView.setText("Please Enter your Email Address");
                    showError();
                } else if (!EmailValidator.isValidEmail(emailInput)) {
                    errorMessageTextView.setText("Please Enter a Valid Email Address");
                    showError();
                } else if (passwordInput.isEmpty()) {
                    errorMessageTextView.setText("Please Enter your Password");
                    showError();
                } else {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    firestore.collection("users")
                            .whereEqualTo("email", emailInput)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        DocumentSnapshot userDoc = querySnapshot.getDocuments().get(0);
                                        String storedPassword = userDoc.getString("password");

                                        if (storedPassword != null && storedPassword.equals(passwordInput)) {
                                            SharedPreferences sharedPref = getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("userEmail", emailInput);
                                            editor.apply();

                                            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                                            finish();
                                        } else {
                                            errorMessageTextView.setText("Invalid Email or Password");
                                            showError();
                                        }
                                    } else {
                                        checkHotelLogin(emailInput, passwordInput);
                                    }
                                } else {
                                    errorMessageTextView.setText("Oops... Something went wrong");
                                    showError();
                                }
                            });
                }
            }

            private void checkHotelLogin(String emailInput, String passwordInput) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("hotel")
                        .whereEqualTo("email", emailInput)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                    DocumentSnapshot hotelDoc = querySnapshot.getDocuments().get(0);
                                    String storedPassword = hotelDoc.getString("password");

                                    Boolean verification = hotelDoc.getBoolean("valid");

                                    if (storedPassword != null && storedPassword.equals(passwordInput)) {
                                        SharedPreferences sharedPref = getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("hotel", emailInput);
                                        editor.apply();

                                        if(!verification){
                                            viewAlert(emailInput);
                                        }else{
                                            Intent intent = new Intent(SignInActivity.this, ActivityDashboard.class);
                                            intent.putExtra("hotel_email", emailInput);
                                            startActivity(intent);
                                            finish();
                                        }


                                    } else {
                                        errorMessageTextView.setText("Invalid Email or Password");
                                        showError();
                                    }
                                } else {
                                    errorMessageTextView.setText("Invalid Email or Password");
                                    showError();
                                }
                            }
                        });
            }



        });


        TextView sigiuptext = findViewById(R.id.textView6);
        sigiuptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        TextView admin = findViewById(R.id.textView71);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,RegisterHotel.class);
                startActivity(intent);
            }
        });
    }

    public void showError() {
        errorMessageTextView.setVisibility(View.VISIBLE);
        Animation shake = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.shake);
        errorMessageTextView.startAnimation(shake);
    }

    public void viewAlert(String email) {
        LayoutInflater inflater = LayoutInflater.from(SignInActivity.this);
        View view = inflater.inflate(R.layout.verification_code_layout, null);

        EditText code = view.findViewById(R.id.editTextCode); // Correctly reference EditText
        Button btnOk = view.findViewById(R.id.button33);

        // Create and show dialog
        AlertDialog dialog = new AlertDialog.Builder(SignInActivity.this)
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
                String v_code = code.getText().toString().trim(); // Get input

                if (v_code.isEmpty()) {
                    code.setError("Please Enter your Verification Code");
                    return; // Stop execution if empty
                }

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("hotel")
                        .where(
                                Filter.and(
                                        Filter.equalTo("email", email),
                                        Filter.equalTo("verification_code", v_code)
                                )
                        )
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();

                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                String documentId = document.getId();

                                if (querySnapshot != null && !querySnapshot.isEmpty()) {

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("valid",true);

                                    firestore.collection("hotel")
                                            .document(documentId)
                                            .update(hashMap)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(SignInActivity.this, "Verified now", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(SignInActivity.this, ActivityDashboard.class);
                                                intent.putExtra("hotel_email", email);
                                                startActivity(intent);
                                                finish();
                                                dialog.dismiss();
                                            })
                                            .addOnFailureListener(e -> {
                                                errorMessageTextView.setText("Verification Unsuccessfull");
                                                showError();
                                            });




                                } else {
                                    Toast.makeText(SignInActivity.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SignInActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }




}