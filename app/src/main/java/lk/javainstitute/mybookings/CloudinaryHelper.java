package lk.javainstitute.mybookings;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.util.Map;

public class CloudinaryHelper {

    private Cloudinary cloudinary;

    public CloudinaryHelper() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dvijulv3l",
                "api_key", "712223112513167",
                "api_secret", "XD0JhYw-tisapOvTSdLWsyBtHbw"));
    }

    public void uploadImage(Context context, Uri imageUri, UploadCallback callback) {
        new UploadTask(context, callback).execute(imageUri);
    }

    private class UploadTask extends AsyncTask<Uri, Void, String> {
        private Context context;
        private UploadCallback callback;

        public UploadTask(Context context, UploadCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Uri... uris) {
            try {
                Uri imageUri = uris[0];
                File file = new File(FileUtils.getPath(context, imageUri));
                Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                return (String) uploadResult.get("secure_url");
            } catch (Exception e) {
                Log.e("Cloudinary Upload", "Error uploading image", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String imageUrl) {
            if (callback != null) {
                callback.onUploadComplete(imageUrl);
            }
        }
    }

    public interface UploadCallback {
        void onUploadComplete(String imageUrl);
    }
}

