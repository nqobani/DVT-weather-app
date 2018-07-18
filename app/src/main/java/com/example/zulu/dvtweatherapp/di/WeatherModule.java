package com.example.zulu.dvtweatherapp.di;

import android.content.Context;

import com.example.zulu.dvtweatherapp.R;
import com.example.zulu.dvtweatherapp.interactors.WeatherInteractorImpl;
import com.example.zulu.dvtweatherapp.interactors.interfaces.WeatherInteractor;
import com.example.zulu.dvtweatherapp.network.WeatherGateway;
import com.example.zulu.dvtweatherapp.network.interafaces.WeatherAPIGateway;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class WeatherModule {

    Context mContext;

    public WeatherModule(Context context){
        mContext = context;
    }

    @Provides
    WeatherInteractor providesWeatherInteractor(WeatherAPIGateway weatherAPIGateway){
        return new WeatherInteractorImpl( new WeatherGateway(mContext, weatherAPIGateway));
    }

    @Provides
    WeatherGateway providesWeatherGateway(WeatherAPIGateway weatherAPIGateway){
        return new WeatherGateway(mContext, weatherAPIGateway);
    }

    @Provides
    WeatherAPIGateway providesWeatherAPIGateway(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getResources().getString(R.string.base_url))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(WeatherAPIGateway.class);
    }
}
