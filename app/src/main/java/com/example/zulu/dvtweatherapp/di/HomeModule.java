package com.example.zulu.dvtweatherapp.di;

import com.example.zulu.dvtweatherapp.interactors.interfaces.WeatherInteractor;
import com.example.zulu.dvtweatherapp.ui.home.HomeInterface;
import com.example.zulu.dvtweatherapp.ui.home.HomePresenterImpl;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class HomeModule {

    private HomeInterface.HomeView mHomeView;

    public HomeModule(HomeInterface.HomeView homeView){
        mHomeView = homeView;
    }

    @Provides
    HomeInterface.HomeView providesHomeView(){
        return mHomeView;
    }

    @Provides
    HomeInterface.HomePresenter providesHomePresenter(WeatherInteractor weatherInteractor){
        return  new HomePresenterImpl(new CompositeDisposable(), mHomeView,weatherInteractor );
    }

}
