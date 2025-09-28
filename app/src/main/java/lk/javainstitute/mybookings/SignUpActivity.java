package lk.javainstitute.mybookings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import lk.javainstitute.mybookings.model.EmailValidator;
import lk.javainstitute.mybookings.model.MobileNumberValidator;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText fname = findViewById(R.id.first_name);
        TextInputEditText lname = findViewById(R.id.last_name);
        TextInputEditText mobile = findViewById(R.id.mobile_signup);
        TextInputEditText email = findViewById(R.id.email_signup);
        TextInputEditText password = findViewById(R.id.signup_password);
        TextView errorMessageTextView = findViewById(R.id.textView24);

        Button signUpButton = findViewById(R.id.button_signup);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fname.getText().toString().isEmpty()){
                    errorMessageTextView.setText("Please Enter your First Name");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake);
                    errorMessageTextView.startAnimation(shake);

                }else if(lname.getText().toString().isEmpty()){
                    errorMessageTextView.setText("Please Enter your Last Name");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake);
                    errorMessageTextView.startAnimation(shake);

                }else if(email.getText().toString().isEmpty()){
                    errorMessageTextView.setText("Please Enter your Email Address");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake);
                    errorMessageTextView.startAnimation(shake);

                }else if(EmailValidator.isValidEmail(String.valueOf(email))){
                    errorMessageTextView.setText("Please Enter valid Email Address");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake);
                    errorMessageTextView.startAnimation(shake);

                }else if(mobile.getText().toString().isEmpty()){
                    errorMessageTextView.setText("Please Enter your Mobile number");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake);
                    errorMessageTextView.startAnimation(shake);

                }else if(MobileNumberValidator.isValidMobileNumber(String.valueOf(mobile))){
                    errorMessageTextView.setText("Please Enter valid Mobile Number");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake);
                    errorMessageTextView.startAnimation(shake);

                }else if(password.getText().toString().isEmpty()){
                    errorMessageTextView.setText("Please Enter your Password");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake);
                    errorMessageTextView.startAnimation(shake);

                }else if(password.length()>=8){
                    errorMessageTextView.setText("Password should be less that 8 charactors ");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    Animation shake = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake);
                    errorMessageTextView.startAnimation(shake);

                }else{
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("email", email.getText().toString());
                    hashMap.put("first_name", fname.getText().toString());
                    hashMap.put("last_name", lname.getText().toString());
                    hashMap.put("mobile", mobile.getText().toString());
                    hashMap.put("password", password.getText().toString());

                    firestore.collection("users").add(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    SharedPreferences sharedPref = getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("x","Hello");
                                    editor.apply();

                                    Intent intent = new Intent(SignUpActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    errorMessageTextView.setText("Oppps.. Something went wrong");
                                    errorMessageTextView.setVisibility(View.VISIBLE);
                                    Animation shake = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake);
                                    errorMessageTextView.startAnimation(shake);
                                }
                            });



                }


            }
        });

        TextView signinxt = findViewById(R.id.textView6);
        signinxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });

    }
}