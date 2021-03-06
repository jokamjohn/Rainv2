package me.johnkagga.rainv2.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import me.johnkagga.rainv2.R;
import me.johnkagga.rainv2.weather.Current;
import me.johnkagga.rainv2.weather.Day;
import me.johnkagga.rainv2.weather.Forecast;
import me.johnkagga.rainv2.weather.Hour;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private Forecast mForecast;

    @InjectView(R.id.temperature_label)TextView mTemperatureLabel;
    @InjectView(R.id.time_label)TextView mTimeLabel;
    @InjectView(R.id.humidity_value)TextView mHumidityValue;
    @InjectView(R.id.precip_value)TextView mPrecipValue;
    @InjectView(R.id.summary_label)TextView mSummaryLabel;
    @InjectView(R.id.icon_imageView)ImageView mIconImage;
    @InjectView(R.id.refresh_imageview)ImageView mRefreshImage;
    @InjectView(R.id.progressBar)ProgressBar mProgressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE);
        final double latitude = 0.3136;//37.8267;
        final double longitude = 32.5811;//-122.423;
        mRefreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude,longitude);
            }
        });

        getForecast(latitude,longitude);
        Log.i(TAG, "main thread");
    }

    private void getForecast(double latitude,double longitude) {
        String apiKey = "5a20df57556bd8bcb4924c5a083bc653";


        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/"
                + latitude + "," + longitude;

        Log.v(TAG, forecastUrl);

        if (isNetworkAvailable()) {
            toogleRefresh();
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toogleRefresh();
                        }
                    });

                    alertUserAboutError();

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toogleRefresh();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
//                        Log.v(TAG,jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parsingForecastDetails(jsonData);
                             /*
                        Add run onUiThread method so that updateDisplay runs on the main UI
                         */
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    upDateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Jsonobject: ", e);
                    }


                }
            });

        }
        else {
            Toast.makeText(this, getString(R.string.network_toast), Toast.LENGTH_LONG).show();
        }
    }

    private void toogleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE){
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImage.setVisibility(View.INVISIBLE);
        }
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImage.setVisibility(View.VISIBLE);
        }
    }

    private void upDateDisplay() {
        Current current = mForecast.getCurrent();
        mTemperatureLabel.setText(current.getTemperature() +"");
        mTimeLabel.setText("At " + current.getFormattedTime() + " it will be");
        mHumidityValue.setText(current.getHumidity() + "");
        mPrecipValue.setText(current.getPrecipeChance() + "%");
        mSummaryLabel.setText(current.getSummary());
        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImage.setImageDrawable(drawable);
    }

    private Forecast parsingForecastDetails(String jsonData) throws JSONException{
        Forecast forecast = new Forecast();
        //setting current through forecast object
        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHours(getHourlyForecast(jsonData));
        forecast.setDays(getDailyForecast(jsonData));
        return forecast;
    }

    private Hour[] getHourlyForecast(String jsonData) {
        return new Hour[0];
    }

    private Day[] getDailyForecast(String jsonData) {
        return new Day[0];
    }



    private Current getCurrentDetails(String jsonData) throws JSONException{
        /*
        throws helps us pass the exception to where the method is called
         */
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "the time zone is: " + timezone);
        JSONObject currently = forecast.getJSONObject("currently");
        //Log.i(TAG, "Json object is: " + currently);

        Current current = new Current();
        current.setTime(currently.getLong("time"));
        current.setSummary(currently.getString("summary"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipeChance(currently.getDouble("precipProbability"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimeZone(timezone);

        Log.i(TAG,"time is: "+ current.getFormattedTime());
        return current;

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



