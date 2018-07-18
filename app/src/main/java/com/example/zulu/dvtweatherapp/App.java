package com.example.zulu.dvtweatherapp;

import android.app.Application;

import com.example.zulu.dvtweatherapp.di.AppModule;
import com.example.zulu.dvtweatherapp.di.WeatherModule;
import com.example.zulu.dvtweatherapp.di.components.AppComponentInterface;
import com.example.zulu.dvtweatherapp.di.components.DaggerAppComponentInterface;
import com.example.zulu.dvtweatherapp.di.components.DaggerWeatherComponent;
import com.example.zulu.dvtweatherapp.di.components.WeatherComponent;

public class App extends Application {

    private AppComponentInterface mAppComponentInterface;
    public static WeatherComponent mWeatherComponent;

    @Override
    public void onCreate(){
        super.onCreate();

        mAppComponentInterface = DaggerAppComponentInterface.builder()
                .appModule(new AppModule(this))
                .build();

        mWeatherComponent = DaggerWeatherComponent.builder()
                .weatherModule(new WeatherModule(this))
                .appModule(new AppModule(this))
                .appComponentInterface(mAppComponentInterface).build();

        mAppComponentInterface.Inject(this);
    }

    public AppComponentInterface getAppComponentInterface(){
        return mAppComponentInterface;
    }
}
