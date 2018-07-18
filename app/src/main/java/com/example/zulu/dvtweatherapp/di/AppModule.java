package com.example.zulu.dvtweatherapp.di;

import android.content.Context;

import com.example.zulu.dvtweatherapp.R;
import com.example.zulu.dvtweatherapp.network.interafaces.WeatherAPIGateway;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    private Context mContext;

    public AppModule(Context context){
        mContext = context;
    }

    @Singleton
    @Provides
    public Context providesContext(){
        return mContext;
    }


    @Provides
    CompositeDisposable providesCompositeDisposable(){
        return  new CompositeDisposable();
    }
}
