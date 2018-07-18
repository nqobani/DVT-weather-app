package com.example.zulu.dvtweatherapp.di.components;

import android.content.Context;

import com.example.zulu.dvtweatherapp.App;
import com.example.zulu.dvtweatherapp.di.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = AppModule.class)
public interface AppComponentInterface {
    void Inject (Context context);
}
