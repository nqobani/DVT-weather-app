package com.example.zulu.dvtweatherapp.ui.home;

import com.example.zulu.dvtweatherapp.models.CurrentWeatherResponse;
import com.example.zulu.dvtweatherapp.models.WeatherList;

import java.util.List;

public interface HomeInterface {
    interface HomeView{

        void getCurrentLocation();

        void getWeather();

        void setCurrentWeather(CurrentWeatherResponse currentWeather);

        void setFiveDayWeatherForecasts(List<WeatherList> weatherList);

        void setTodayWeatherList(List<WeatherList> weatherList);

        void showError(String errorMessage);

    }

    interface HomePresenter{
        void getCurrentWeather(Double lat, Double lon);

        void getFiveDayWeatherForecast(int cityId);

        void disposeSubscriptions();
    }
}
