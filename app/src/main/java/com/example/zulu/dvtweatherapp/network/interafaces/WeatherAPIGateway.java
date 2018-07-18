package com.example.zulu.dvtweatherapp.network.interafaces;

import com.example.zulu.dvtweatherapp.models.CurrentWeatherResponse;
import com.example.zulu.dvtweatherapp.models.FiveDayWeatherResponse;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPIGateway {
//https://samples.openweathermap.org/data/2.5/forecast?lat=-29.861014&lon=31.016362&appid=b6907d289e10d714a6e88b30761fae22
    //api.openweathermap.org/data/2.5/weather?
    //?lat=-29.861014&lon=31.016362&appid=10e02294d2626d5619b5a315d362e0c7
    //&units=metric
@GET("forecast")
Observable<FiveDayWeatherResponse> getFiveDateWeatherForecast(@Query("lat") Double lat,
                                                              @Query("lon") Double lon,
                                                              @Query("appid") String appId,
                                                              @Query("units") String unit);

    @GET("weather")
    Observable<CurrentWeatherResponse> getCurrentWeather(@Query("lat") Double lat,
                                                         @Query("lon") Double lon,
                                                         @Query("appid") String appId,
                                                         @Query("units") String unit);
}
