package com.example.zulu.dvtweatherapp.di.components;

import android.content.Context;

import com.example.zulu.dvtweatherapp.di.AppModule;
import com.example.zulu.dvtweatherapp.di.WeatherModule;
import com.example.zulu.dvtweatherapp.ui.home.HomeActivity;

import dagger.Component;

@Component(dependencies = AppComponentInterface.class,
        modules = {WeatherModule.class, AppModule.class})
public interface WeatherComponent {

    void Inject (Context context);
}
