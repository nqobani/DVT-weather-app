package com.example.zulu.dvtweatherapp.ui.home.listeners;

import com.example.zulu.dvtweatherapp.models.CurrentWeatherResponse;

public interface CurrentWeatherListener {
    void onSuccess(CurrentWeatherResponse currentWeatherResponse);
    void onError(Throwable throwable);
}
