package com.example.zulu.dvtweatherapp.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.zulu.dvtweatherapp.R;
import com.example.zulu.dvtweatherapp.adapters.FiveDayWeatherForecastAdapter;
import com.example.zulu.dvtweatherapp.adapters.TodayWeatherAdapter;
import com.example.zulu.dvtweatherapp.di.HomeModule;
import com.example.zulu.dvtweatherapp.di.WeatherModule;
import com.example.zulu.dvtweatherapp.di.components.DaggerHomeComponent;
import com.example.zulu.dvtweatherapp.di.components.DaggerWeatherComponent;
import com.example.zulu.dvtweatherapp.models.CurrentWeatherResponse;
import com.example.zulu.dvtweatherapp.models.FiveDayWeatherResponse;
import com.example.zulu.dvtweatherapp.models.WeatherList;

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

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mSharedPreferencesEditor;

    private LinearLayoutManager mLinearLayoutManager;
    private FiveDayWeatherForecastAdapter mFiveDayWeatherForecastAdapter;

    private TodayWeatherAdapter mTodayWeatherAdapter;
    private LinearLayoutManager mLinearLayoutManagerTodayWeather;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private Double latitude;
    private Double longitude;
    private String cityName;

    private Handler mHandler;
    private Runnable mRunnableOnRequestLocation;

    private Date lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        daggerComponentsSetup();

        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.dvt_weather_app_preferences), Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewFiveDayWeatherForecast.setLayoutManager(mLinearLayoutManager);

        mLinearLayoutManagerTodayWeather= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewToday.setLayoutManager(mLinearLayoutManagerTodayWeather);

        mToggleButtonCurrentWeatherDetailsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mLinearLayoutCurrentWeatherDetailsContainer.setVisibility(View.GONE);
                    mRecyclerViewToday.setVisibility(View.VISIBLE);
                }else{
                    mLinearLayoutCurrentWeatherDetailsContainer.setVisibility(View.VISIBLE);
                    mRecyclerViewToday.setVisibility(View.GONE);
                }
            }
        });

        latitude = Double.valueOf(mSharedPreferences.getFloat(getResources().getString(R.string.latitude),0f));
        longitude = Double.valueOf(mSharedPreferences.getFloat(getResources().getString(R.string.longitude),0f));
        cityName = mSharedPreferences.getString(getResources().getString(R.string.city_name),null);
        mTextViewAreaName.setText(cityName);

        getWeather();

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
        super.onPause();
    }

    private void requestLocation(){
        mHandler = new Handler();
        mRunnableOnRequestLocation = new Runnable() {
            @Override
            public void run() {
                getCurrentLocation();
                mHandler.postDelayed(mRunnableOnRequestLocation, (DateUtils.MINUTE_IN_MILLIS * 10));
            }
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
    public void getCurrentLocation() {
        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                try {
                    cityName =  new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(latitude,longitude,1).get(0).getLocality();
                    mTextViewAreaName.setText(cityName);
                    if(cityName.equals(mSharedPreferences.getString(getResources().getString(R.string.city_name),null)) && !canMakeAPICall()){
                        mLocationManager.removeUpdates(mLocationListener);
                        return;
                    }

                    mSharedPreferencesEditor.putFloat(getResources().getString(R.string.latitude),latitude.floatValue());
                    mSharedPreferencesEditor.putFloat(getResources().getString(R.string.longitude),longitude.floatValue());
                    mSharedPreferencesEditor.putString(getResources().getString(R.string.city_name),cityName);
                    mSharedPreferencesEditor.commit();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                getWeather();
                mLocationManager.removeUpdates(mLocationListener);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (DateUtils.MINUTE_IN_MILLIS), 5000, mLocationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (DateUtils.MINUTE_IN_MILLIS), 5000, mLocationListener);
            }
        }
    }

    @Override
    public void getWeather() {
        if(latitude ==0 && longitude == 0)
            return;

        lastUpdate=new Date();
        mHomePresenter.getCurrebtWeather(latitude, longitude);
        mHomePresenter.getFiveDayWeatherForecast(latitude, longitude);
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

    //When the user doesn't kill an app for an hour, allow the app to make the API call and update the content
    public Boolean canMakeAPICall(){
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
}