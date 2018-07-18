package com.example.zulu.dvtweatherapp.ui.home.listeners;

import com.example.zulu.dvtweatherapp.models.FiveDayWeatherResponse;

public interface FiveDayWeatherForecastListener {
    void onSuccess(FiveDayWeatherResponse fiveDayWeatherResponse);
    void onError(Throwable e);
}
