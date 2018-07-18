package com.example.zulu.dvtweatherapp.di.components;

import com.example.zulu.dvtweatherapp.di.HomeModule;
import com.example.zulu.dvtweatherapp.di.WeatherModule;
import com.example.zulu.dvtweatherapp.ui.home.HomeActivity;

import javax.inject.Inject;

import dagger.Component;

@Component(dependencies = WeatherComponent.class,
        modules = {HomeModule.class, WeatherModule.class})
public interface HomeComponent {
    void Inject (HomeActivity homeActivity);
}
