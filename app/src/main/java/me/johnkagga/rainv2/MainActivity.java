package me.johnkagga.rainv2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiKey = "5a20df57556bd8bcb4924c5a083bc653";
        double latitude = 37.8267;
        double longitude = -122.423;

        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/ "
                + latitude + "," + longitude;

        if (isNetworkAvailable()) {
        /*
        calling the client and setting up the request
         */
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            //logging the whole response body
                            Log.d(TAG, response.body().string());
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.v(TAG, "Exception caught: ", e);
                    }
                }
            });
        }
        else {
            Toast.makeText(this,getString(R.string.network_toast),Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "Main thread is working");

    }

    /*
    This checks whether the network is available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (info != null && info.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");
    }
}



