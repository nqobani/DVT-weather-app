package com.example.zulu.dvtweatherapp.network;

import android.content.Context;

import com.example.zulu.dvtweatherapp.R;
import com.example.zulu.dvtweatherapp.models.CurrentWeatherResponse;
import com.example.zulu.dvtweatherapp.models.FiveDayWeatherResponse;
import com.example.zulu.dvtweatherapp.network.interafaces.WeatherAPIGateway;

import io.reactivex.Observable;

public class WeatherGateway {

    Context mContext;

    WeatherAPIGateway mWeatherAPIGateway;

    public WeatherGateway(Context context, WeatherAPIGateway weatherAPIGateway){
        mContext = context;
        mWeatherAPIGateway = weatherAPIGateway;
    }

    public Observable<FiveDayWeatherResponse> getFiveDayWeatherForecast(int cityId){
        return mWeatherAPIGateway.getFiveDateWeatherForecast(cityId, mContext.getResources().getString(R.string.weather_api_app_id),"metric");
    }

    public Observable<CurrentWeatherResponse> getCurrentWeather(Double lat, Double lon){
        return mWeatherAPIGateway.getCurrentWeather(lat, lon, mContext.getResources().getString(R.string.weather_api_app_id),"metric");
    }
}

