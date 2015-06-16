package me.johnkagga.rainv2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;

    @InjectView(R.id.temperature_label)TextView mTemperatureLabel;
    @InjectView(R.id.time_label)TextView mTimeLabel;
    @InjectView(R.id.humidity_label)TextView mHumidityLabel;
    @InjectView(R.id.precip_label)TextView mPrecipLabel;
    @InjectView(R.id.summary_label)TextView mSummaryLabel;
    @InjectView(R.id.icon_imageView)ImageView mIconImage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


        String apiKey = "5a20df57556bd8bcb4924c5a083bc653";
        double latitude = 37.8267;
        double longitude = -122.423;

        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/"
                + latitude + "," + longitude;

        Log.v(TAG,forecastUrl);

        if (isNetworkAvailable()) {
        /*
        calling the client and setting up the request
         */
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();
            Call call =client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String jsonData = response.body().string();
                    //Log.v(TAG,jsonData);
                    if (response.isSuccessful()){
                        Log.v(TAG, jsonData);


                        try {
                            mCurrentWeather = getCurrentDetails(jsonData);
                             /*
                        Add runonUiThread method so that updateDisplay runs on the main UI
                         */
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    upDateDisplay();
                                }
                            });
                        } catch (JSONException e) {
                            Log.e(TAG,"Jsonobject: ", e);
                        }
                    }
                    else {
                        alertUserAboutError();
                    }

                }
            });
            Log.i(TAG,"main thread");

        }
        else {
            Toast.makeText(this, getString(R.string.network_toast), Toast.LENGTH_LONG).show();
        }
    }

    private void upDateDisplay() {
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() +"");
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException{
        /*
        throws helps us pass the exception to where the method is called
         */
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "the time zone is: " + timezone);
        JSONObject currently = forecast.getJSONObject("currently");
        Log.i(TAG, "Json object is: " + currently);

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipeChance(currently.getDouble("precipProbability"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.i(TAG,"time is: "+ currentWeather.getFormattedTime());
        return currentWeather;

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



