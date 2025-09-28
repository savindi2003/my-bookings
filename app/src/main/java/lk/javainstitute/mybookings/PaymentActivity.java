package lk.javainstitute.mybookings;

import java.math.BigDecimal;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class PaymentActivity extends Activity {
    private static final int PAYHERE_REQUEST = 11009; // Unique request code
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment); // Your XML layout

        Button payButton = findViewById(R.id.button9); // Button from XML
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSandboxPayment();
            }
        });
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

    private String generateOrderId() {
        return String.valueOf(System.currentTimeMillis()); // Millisecond timestamp is a common approach

    }



    private void startSandboxPayment() {
        showDimBackground();

        InitRequest req = new InitRequest();
        req.setMerchantId("1221148");       // Merchant ID
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(1000.00);             // Final Amount to be charged
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
                        // ...
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
}
