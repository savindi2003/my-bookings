package lk.javainstitute.mybookings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class BlankFragment2 extends Fragment {

    TextView full_name, email_tv, mobile_tv;
    EditText first_name_et, last_name_et, mobile_et;

    ImageView profile_pic;
    private Uri imagePath;
    private static  int IMAGE_REQ=1;

    private Button uploadImg;

    private String uploaed_image_uri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank2, container, false);

        initCongif();

        // Initialize views
        full_name = view.findViewById(R.id.tvUserName);
        email_tv = view.findViewById(R.id.tvUserEmail);
        mobile_tv = view.findViewById(R.id.textView63);

        first_name_et = view.findViewById(R.id.editTextText_name11);
        last_name_et = view.findViewById(R.id.editTextText_name1);
        mobile_et = view.findViewById(R.id.editTextText_name);

        profile_pic = view.findViewById(R.id.ivProfile);
        uploadImg = view.findViewById(R.id.button31);

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaManager.get().upload(imagePath).callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.e("Cloudinary Upload", "OnStart");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.e("Cloudinary Upload", "On Prograss");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.e("Cloudinary Upload", "On Sucess");
                        Toast.makeText(getContext(),"Image Saved",Toast.LENGTH_LONG).show();
                        uploadImg.setVisibility(View.GONE);
                        uploaed_image_uri = resultData.get("secure_url").toString();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("Cloudinary Upload", "On Error");

                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.e("Cloudinary Upload", "onReschedule");
                    }
                }).dispatch();
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });

        // Get user email from SharedPreferences
        SharedPreferences sharedPref = requireContext().getSharedPreferences("lk.javainstitute.mybookings.data", Context.MODE_PRIVATE);
        String email = sharedPref.getString("userEmail", null);

        if (email != null) {
            // Firestore instance
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Fetch user data from Firestore
            firestore.collection("users")
                    .whereEqualTo("email", email)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.e("Firestore", "Error getting documents: ", error);
                                return;
                            }

                            if (value != null && !value.isEmpty()) {
                                DocumentSnapshot document = value.getDocuments().get(0);

                                // Fetch user details from the document
                                String first_name = document.getString("first_name");
                                String last_name = document.getString("last_name");
                                String mobile = document.getString("mobile");
                                String image = document.getString("image");



                                // Set the values to TextViews
                                if (first_name != null && last_name != null && mobile != null) {
                                    full_name.setText(first_name + " " + last_name);
                                    email_tv.setText(email);
                                    mobile_tv.setText(String.valueOf(mobile));

                                    first_name_et.setText(first_name);
                                    last_name_et.setText(last_name);
                                    mobile_et.setText(String.valueOf(mobile));

                                    Picasso.get()
                                            .load(image)  // Load the image URL
                                            .placeholder(R.drawable.loading_icon)  // Placeholder while loading
                                            .error(R.drawable.empty)  // Error image if loading fails
                                            .into(profile_pic);


                                } else {
                                    Log.d("Firestore", "User data is incomplete.");
                                }
                            } else {
                                Log.d("Firestore", "No document found for email: " + email);
                            }
                        }
                    });
        } else {
            Log.d("BlankFragment2", "Email not found in SharedPreferences.");
        }

        Button update = view.findViewById(R.id.button17);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                HashMap<String,Object> updatedUserDetails = new HashMap<>();
                updatedUserDetails.put("first_name",first_name_et.getText().toString());
                updatedUserDetails.put("last_name",last_name_et.getText().toString());
                updatedUserDetails.put("mobile",mobile_et.getText().toString());
                updatedUserDetails.put("image",uploaed_image_uri);

                if (email != null) {
                    firestore.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        // Get the first document from the query results
                                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                        DocumentReference documentReference = documentSnapshot.getReference();

                                        // Update the document with new details
                                        documentReference.update(updatedUserDetails)
                                                .addOnSuccessListener(aVoid -> {


                                                    LayoutInflater inflater = LayoutInflater.from(getContext());
                                                    View view2 = inflater.inflate(R.layout.custom_alert, null);

                                                    // Initialize views from layout
                                                    TextView tvTitle = view2.findViewById(R.id.tvTitle);
                                                    TextView tvMessage = view2.findViewById(R.id.tvMessage);
                                                    Button btnOk = view2.findViewById(R.id.btnOk);

                                                    // Customize text dynamically (Optional)
                                                    tvTitle.setText("Update Successful");
                                                    tvMessage.setText("Your details have been successfully updated. All changes are now saved");

                                                    // Create and show dialog
                                                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                                                            .setView(view2)
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

                                                        }
                                                    });


                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("Firestore", "Error updating user details", e);
                                                });
                                    } else {
                                        Log.d("Firestore", "No user found with the email: " + email);
                                    }
                                } else {
                                    Log.e("Firestore", "Error getting documents", task.getException());
                                }
                            });
                } else {
                    Log.e("Firestore", "Email not found in SharedPreferences.");
                }

            }
        });

        Button passwordChangeBtn = view.findViewById(R.id.button16);
        passwordChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view2 = inflater.inflate(R.layout.change_password_alert, null);

                // Initialize views from layout
                TextView tvTitle = view2.findViewById(R.id.textView52);
                EditText password1 = view2.findViewById(R.id.editTextText_password1);
                EditText password2 = view2.findViewById(R.id.editTextText_password2);

                Button btnUpdate = view2.findViewById(R.id.button12);


                // Create and show dialog
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setView(view2)
                        .setCancelable(false)  // Prevents dialog from closing when clicking outside
                        .create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.search_border_rounded);
                }

                dialog.show();


                btnUpdate.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {

                                                     String password1_s = password1.getText().toString();
                                                     String password2_s = password2.getText().toString();

                                                     if (password1_s.isEmpty()) {
                                                         password1.setError("Please enter your new password");
                                                     } else if (password2_s.isEmpty()) {
                                                         password2.setError("Please re enter your new password");
                                                     } else if (!password1_s.equals(password2_s)) {
                                                         password2.setError("This one not equal the first password");
                                                     } else {

                                                         FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                                                         HashMap<String, Object> updatedUserDetails = new HashMap<>();
                                                         updatedUserDetails.put("password", password1_s);

                                                         if (email != null) {
                                                             firestore.collection("users")
                                                                     .whereEqualTo("email", email)
                                                                     .get()
                                                                     .addOnCompleteListener(task -> {
                                                                         if (task.isSuccessful()) {
                                                                             QuerySnapshot querySnapshot = task.getResult();
                                                                             if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                                                 // Get the first document from the query results
                                                                                 DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                                                                 DocumentReference documentReference = documentSnapshot.getReference();

                                                                                 // Update the document with new details
                                                                                 documentReference.update(updatedUserDetails)
                                                                                         .addOnSuccessListener(aVoid -> {


                                                                                             LayoutInflater inflater = LayoutInflater.from(getContext());
                                                                                             View view2 = inflater.inflate(R.layout.custom_alert, null);

                                                                                             // Initialize views from layout
                                                                                             TextView tvTitle = view2.findViewById(R.id.tvTitle);
                                                                                             TextView tvMessage = view2.findViewById(R.id.tvMessage);
                                                                                             Button btnOk = view2.findViewById(R.id.btnOk);

                                                                                             // Customize text dynamically (Optional)
                                                                                             tvTitle.setText("Update Successful");
                                                                                             tvMessage.setText("Your password have been successfully updated. All changes are now saved");

                                                                                             // Create and show dialog
                                                                                             AlertDialog dialog1 = new AlertDialog.Builder(getContext())
                                                                                                     .setView(view2)
                                                                                                     .setCancelable(false)  // Prevents dialog from closing when clicking outside
                                                                                                     .create();

                                                                                             if (dialog1.getWindow() != null) {
                                                                                                 dialog1.getWindow().setBackgroundDrawableResource(R.drawable.search_border_rounded);
                                                                                             }

                                                                                             dialog1.show();


                                                                                             btnOk.setOnClickListener(new View.OnClickListener() {
                                                                                                 @Override
                                                                                                 public void onClick(View view) {
                                                                                                     dialog1.dismiss();
                                                                                                     dialog.dismiss();

                                                                                                 }
                                                                                             });


                                                                                         })
                                                                                         .addOnFailureListener(e -> {
                                                                                             Log.e("Firestore", "Error updating user details", e);
                                                                                         });
                                                                             } else {
                                                                                 Log.d("Firestore", "No user found with the email: " + email);
                                                                             }
                                                                         } else {
                                                                             Log.e("Firestore", "Error getting documents", task.getException());
                                                                         }
                                                                     });
                                                         } else {
                                                             Log.e("Firestore", "Email not found in SharedPreferences.");
                                                         }

                                                     }



                    }
                });



            }
        });

        TextView logout = view.findViewById(R.id.textView68);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("userEmail");
                editor.apply();

                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }

    private void initCongif() {
        Map config = new HashMap();

        config.put("cloud_name", "dvijulv3l");
        config.put("api_key", "712223112513167");
        config.put("api_secret", "XD0JhYw-tisapOvTSdLWsyBtHbw");

        //config.put("secure", true);
        MediaManager.init(getContext(), config);
    }

    private void requestPermission(){

        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            selecetImage();
        }else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },IMAGE_REQ);
        }

    }

    private void selecetImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQ && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imagePath = data.getData();
            Picasso.get().load(imagePath).into(profile_pic);

            uploadImg.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(getContext(), "Wrong", Toast.LENGTH_SHORT).show();
        }
    }


}
