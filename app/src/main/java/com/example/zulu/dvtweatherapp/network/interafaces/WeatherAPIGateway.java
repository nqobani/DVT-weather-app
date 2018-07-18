package com.example.zulu.dvtweatherapp.network.interafaces;

import com.example.zulu.dvtweatherapp.models.CurrentWeatherResponse;
import com.example.zulu.dvtweatherapp.models.FiveDayWeatherResponse;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPIGateway {

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
