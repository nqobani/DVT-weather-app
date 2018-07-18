package com.example.zulu.dvtweatherapp.interactors.interfaces;

import com.example.zulu.dvtweatherapp.ui.home.listeners.CurrentWeatherListener;
import com.example.zulu.dvtweatherapp.ui.home.listeners.FiveDayWeatherForecastListener;

import io.reactivex.disposables.CompositeDisposable;

public interface WeatherInteractor {

    void getCurrentWeatherForecast(CompositeDisposable compositeDisposable, Double lat, Double lon, CurrentWeatherListener currentWeatherListener);

    void getFiveDayWeatherForecast(CompositeDisposable compositeDisposable, Double lat, Double lon, FiveDayWeatherForecastListener fiveDayWeatherForecastListener);

}
