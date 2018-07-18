package com.example.zulu.dvtweatherapp.interactors;

import com.example.zulu.dvtweatherapp.interactors.interfaces.CurrentWeatherListener;
import com.example.zulu.dvtweatherapp.interactors.interfaces.WeatherInteractor;
import com.example.zulu.dvtweatherapp.network.WeatherGateway;
import com.example.zulu.dvtweatherapp.ui.home.listeners.FiveDayWeatherForecastListener;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WeatherInteractorImpl implements WeatherInteractor {

    WeatherGateway mWeatherGateway;
    public WeatherInteractorImpl(WeatherGateway weatherGateway){
        mWeatherGateway = weatherGateway;
    }

    @Override
    public void getCurrentWeatherForecast(CompositeDisposable compositeDisposable, Double lat, Double lon, CurrentWeatherListener currentWeatherListener) {
        compositeDisposable.add(
                mWeatherGateway.getCurrentWeather(lat,lon).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe((currentWeatherResponse)->{
                            currentWeatherListener.onSuccess(currentWeatherResponse);
                        },
                        throwable -> {
                            currentWeatherListener.onError(throwable);
                        },
                        ()->{

                        })
        );
    }

    @Override
    public void getFiveDayWeatherForecast(CompositeDisposable compositeDisposable, Double lat, Double lon, FiveDayWeatherForecastListener fiveDayWeatherForecastListener) {
        compositeDisposable.add(
                mWeatherGateway.getFiveDayWeatherForecast(lat,lon).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe((fiveDayWeatherResponse) -> {
                                    fiveDayWeatherForecastListener.onSuccess(fiveDayWeatherResponse);
                                },
                                throwable -> {
                                    fiveDayWeatherForecastListener.onError(throwable);
                                },
                                ()->{})
        );
    }
}
