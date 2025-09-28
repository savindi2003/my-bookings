package lk.javainstitute.mybookings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isInternetAvailable(context)) {
            showNoInternetDialog(context);
        }
    }

    private boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void showNoInternetDialog(Context context) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("No Internet Connection")
//                .setMessage("Please check your internet connection and try again.")
//                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                .setCancelable(false);
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.no_internet_layout, null);

        // Initialize views from layout
        Button btnOk = view.findViewById(R.id.btnOk);

        // Create and show dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
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

            }
        });


    }
}
