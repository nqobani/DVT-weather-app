package com.example.zulu.dvtweatherapp.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

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
