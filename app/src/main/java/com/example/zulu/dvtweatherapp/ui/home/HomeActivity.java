package com.example.zulu.dvtweatherapp.ui.home;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.zulu.dvtweatherapp.R;
import com.example.zulu.dvtweatherapp.adapters.FiveDayWeatherForecastAdapter;
import com.example.zulu.dvtweatherapp.adapters.TodayWeatherAdapter;
import com.example.zulu.dvtweatherapp.di.HomeModule;
import com.example.zulu.dvtweatherapp.di.WeatherModule;
import com.example.zulu.dvtweatherapp.di.components.DaggerHomeComponent;
import com.example.zulu.dvtweatherapp.models.CurrentWeatherResponse;
import com.example.zulu.dvtweatherapp.models.WeatherList;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.zulu.dvtweatherapp.App.mWeatherComponent;

public class HomeActivity extends AppCompatActivity implements HomeInterface.HomeView {

    @BindView(R.id.five_day_weather_forecast_recyclerview)
    RecyclerView mRecyclerViewFiveDayWeatherForecast;

    @BindView(R.id.today_weather_recyclerview)
    RecyclerView mRecyclerViewToday;

    @BindView(R.id.area_name_textview)
    TextView mTextViewAreaName;

    @BindView(R.id.current_weather_description_textview)
    TextView mTextViewCurrentWeatherDescription;

    @BindView(R.id.current_weather_current_temp_big_textview)
    TextView mTextViewCurrentWeatherTempBigFont;

    @BindView(R.id.current_weather_current_temp_textview)
    TextView mTextViewCurrentWeatherTemp;

    @BindView(R.id.current_weather_min_temp_textview)
    TextView mTextViewCurrentWeatherTempMin;

    @BindView(R.id.current_weather_max_temp_textview)
    TextView mTextViewCurrentWeatherTempMax;

    @BindView(R.id.top_container_relative_layout)
    RelativeLayout mRelativeLayoutTopContainer;

    @BindView(R.id.main_container_relative_layout)
    RelativeLayout mRalativeLayoutMainContainer;

    @BindView(R.id.current_weather_container_linearlayout)
    LinearLayout mLinearLayoutCurrentWeatherDetailsContainer;

    @BindView(R.id.current_weather_toggle_button)
    ToggleButton mToggleButtonCurrentWeatherDetailsSwitch;

    @BindView(R.id.line_view)
    View mViewLine;

    @Inject
    HomeInterface.HomePresenter mHomePresenter;

    private LinearLayoutManager mLinearLayoutManager;
    private FiveDayWeatherForecastAdapter mFiveDayWeatherForecastAdapter;

    private TodayWeatherAdapter mTodayWeatherAdapter;
    private LinearLayoutManager mLinearLayoutManagerTodayWeather;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private Double latitude;
    private Double longitude;
    private String cityName;

    private Handler mHandler;
    private Runnable mRunnableOnRequestLocation;

    private Date lastUpdate;

    private boolean isInitialCall = true;

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mSharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        daggerComponentsSetup();

        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.dvt_weather_app_preferences), Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewFiveDayWeatherForecast.setLayoutManager(mLinearLayoutManager);

        mLinearLayoutManagerTodayWeather= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewToday.setLayoutManager(mLinearLayoutManagerTodayWeather);

        mToggleButtonCurrentWeatherDetailsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                mLinearLayoutCurrentWeatherDetailsContainer.setVisibility(View.GONE);
                mRecyclerViewToday.setVisibility(View.VISIBLE);
            }else{
                mLinearLayoutCurrentWeatherDetailsContainer.setVisibility(View.VISIBLE);
                mRecyclerViewToday.setVisibility(View.GONE);
            }
        });

        latitude = (double) mSharedPreferences.getFloat(getResources().getString(R.string.latitude), 0f);
        longitude = (double) mSharedPreferences.getFloat(getResources().getString(R.string.longitude), 0f);
        cityName = mSharedPreferences.getString(getResources().getString(R.string.city_name),null);
        mTextViewAreaName.setText(cityName);
        getWeather();

        registerReceiver(broadcastReceiver,new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        createLocationRequest();
        requestLocation();
    }

    @Override
    protected void onResume() {
        mRunnableOnRequestLocation.run();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mRunnableOnRequestLocation);
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        mHomePresenter.disposeSubscriptions();
        super.onDestroy();
    }

    private void requestLocation(){
        mHandler = new Handler();
        mRunnableOnRequestLocation = () -> {
            getLocation();
            //Check for location updates every 10 minutes
            mHandler.postDelayed(mRunnableOnRequestLocation, (DateUtils.MINUTE_IN_MILLIS * 10));
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                if(locationResult.getLocations().size()>0){
                    Location location =  locationResult.getLocations().get(0);
                    userLocationData(location);
                }

                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            };
        };
    }

    private void daggerComponentsSetup() {
        mWeatherComponent.Inject(this);

        DaggerHomeComponent.builder()
                .homeModule(new HomeModule(this))
                .weatherModule(new WeatherModule(this))
                .weatherComponent(mWeatherComponent)
                .build().Inject(this);
    }

    @Override
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            //start location updates
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null);

            //Get the last known location of a device. The last know location of a device get cleared when the
            //user turn the location off on a device.
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, (location)-> {
                        userLocationData(location);
                    });
        }
    }

    private void userLocationData(Location location){
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            try {
                List<Address> addresses =  new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(latitude,longitude,1);

                if(addresses.size()>0){
                    if(cityName==null
                            ||!cityName.equals(addresses.get(0).getLocality())
                            ||isInitialCall
                            ||canMakeAPICall()){

                        cityName = addresses.get(0).getLocality();
                        mTextViewAreaName.setText(cityName);
                        cacheLocationData();
                        getWeather();
                    }
                } else {
                    if(mTextViewAreaName.getText().equals(getResources().getString(R.string.location_unkown)) && !canMakeAPICall())
                        return;

                    mTextViewAreaName.setText(getResources().getString(R.string.location_unkown));
                    cityName = getResources().getString(R.string.location_unkown);
                    cacheLocationData();
                    getWeather();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
        }
    }

    @Override
    public void getWeather() {
        if(latitude == null || longitude ==null)
            return;

        isInitialCall = false;
        lastUpdate = new Date();
        mHomePresenter.getCurrentWeather(latitude, longitude);
    }

    @Override
    public void setCurrentWeather(CurrentWeatherResponse currentWeather) {
        if(currentWeather!=null){
            mTextViewCurrentWeatherDescription.setText(currentWeather.getWeather().get(0).getDescription());
            mTextViewCurrentWeatherTempBigFont.setText(currentWeather.getMain().getTemp()+"\u00b0");
            mTextViewCurrentWeatherTemp.setText(currentWeather.getMain().getTemp()+"\u00b0");
            mTextViewCurrentWeatherTempMin.setText(currentWeather.getMain().getTempMin()+"\u00b0");
            mTextViewCurrentWeatherTempMax.setText(currentWeather.getMain().getTempMax()+"\u00b0");
            updateUI(currentWeather.getWeather().get(0).getDescription());
        }
    }

    @Override
    public void setFiveDayWeatherForecasts(List<WeatherList> weatherLists) {
        if(mFiveDayWeatherForecastAdapter==null){
            mFiveDayWeatherForecastAdapter = new FiveDayWeatherForecastAdapter(weatherLists);
            mRecyclerViewFiveDayWeatherForecast.setAdapter(mFiveDayWeatherForecastAdapter);
        } else{
            mFiveDayWeatherForecastAdapter.setWeatherList(weatherLists);
        }

    }

    @Override
    public void setTodayWeatherList(List<WeatherList> weatherList) {
        if(mTodayWeatherAdapter==null){
            mTodayWeatherAdapter = new TodayWeatherAdapter(weatherList);
            mRecyclerViewToday.setAdapter(mTodayWeatherAdapter);
        } else {
            mTodayWeatherAdapter.setWeatherList(weatherList);
        }

    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage,Toast.LENGTH_LONG).show();
    }

    private void cacheLocationData(){
        //This data is useful when the user has turned of the location off an device and it helps to speed up
        //the precess if the user is still in the same city.
        mSharedPreferencesEditor.putFloat(getResources().getString(R.string.latitude),latitude.floatValue());
        mSharedPreferencesEditor.putFloat(getResources().getString(R.string.longitude),longitude.floatValue());
        mSharedPreferencesEditor.putString(getResources().getString(R.string.city_name),cityName);
        mSharedPreferencesEditor.commit();
    }


    //When the user doesn't kill an app for an hour, allow the app to make the API call and update the content
    private Boolean canMakeAPICall(){
        if(lastUpdate==null)
            return true;

        Date now = new Date();
        long timeDif = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - lastUpdate.getTime());

        if(timeDif >= 60)
            return true;
        else
            return false;
    }

    public void updateUI(String weatherDescription){
        if(weatherDescription.toLowerCase().contains("clear")){
            mRelativeLayoutTopContainer.setBackgroundResource(R.drawable.forest_sunny);
            mRalativeLayoutMainContainer.setBackgroundColor(getResources().getColor(R.color.colorSunnyBackground));
        }
        else if(weatherDescription.toLowerCase().contains("cloud")){
            mRelativeLayoutTopContainer.setBackgroundResource(R.drawable.forest_cloudy);
            mRalativeLayoutMainContainer.setBackgroundColor(getResources().getColor(R.color.colorPartlySunnyBackground));
        }
        else if(weatherDescription.toLowerCase().contains("rain")){
            mRelativeLayoutTopContainer.setBackgroundResource(R.drawable.forest_rainy);
            mRalativeLayoutMainContainer.setBackgroundColor(getResources().getColor(R.color.colorRainyBackground));
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager =(ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if(networkInfo != null){
                if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE || networkInfo.getType() == ConnectivityManager.TYPE_WIFI ){
                    getWeather();
                }else{
                    Toast.makeText(getApplicationContext(),"No Internet connection",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"No Internet connection",Toast.LENGTH_LONG).show();
            }
        }
    };

}
