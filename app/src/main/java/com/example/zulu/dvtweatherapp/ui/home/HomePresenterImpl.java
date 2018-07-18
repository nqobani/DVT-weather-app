package com.example.zulu.dvtweatherapp.ui.home;

import com.example.zulu.dvtweatherapp.interactors.interfaces.CurrentWeatherListener;
import com.example.zulu.dvtweatherapp.interactors.interfaces.WeatherInteractor;
import com.example.zulu.dvtweatherapp.models.CurrentWeatherResponse;
import com.example.zulu.dvtweatherapp.models.FiveDayWeatherResponse;
import com.example.zulu.dvtweatherapp.models.WeatherList;
import com.example.zulu.dvtweatherapp.ui.home.listeners.FiveDayWeatherForecastListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class HomePresenterImpl implements HomeInterface.HomePresenter {


    CompositeDisposable mCompositeDisposable;

    HomeInterface.HomeView mHomeView;

    WeatherInteractor mWeatherInteractor;

    @Inject
    public HomePresenterImpl(CompositeDisposable compositeDisposable, HomeInterface.HomeView homeView,WeatherInteractor weatherInteractor){
        mCompositeDisposable = compositeDisposable;
        mWeatherInteractor = weatherInteractor;
        mHomeView = homeView;
    }

    @Override
    public void getCurrebtWeather(Double lat, Double lon) {
        mWeatherInteractor.getCurrentWeatherForecast(mCompositeDisposable, lat, lon, new CurrentWeatherListener() {
            @Override
            public void onSuccess(CurrentWeatherResponse currentWeatherResponse) {
                mHomeView.setCurrentWeather(currentWeatherResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                String nnn = "";
            }
        });
    }

    @Override
    public void getFiveDayWeatherForecast(Double lat, Double lon) {
        mWeatherInteractor.getFiveDayWeatherForecast(mCompositeDisposable, lat, lon, new FiveDayWeatherForecastListener() {
            @Override
            public void onSuccess(FiveDayWeatherResponse fiveDayWeatherResponse) {
                List<WeatherList> weatherLists = new ArrayList<>();
                List<WeatherList> todayWeatherLists = new ArrayList<>();
                Date today = new Date();
                for (WeatherList item :fiveDayWeatherResponse.getList()) {
                    Date date =convertStringToDate(item.getDtTxt());

                    if((today.getYear()!=date.getYear()
                            || today.getMonth()!=date.getMonth()
                            || today.getDate()!=date.getDate())
                            && date.getHours() == 12){

                        weatherLists.add(item);
                    } else if(today.getYear()==date.getYear()
                            && today.getMonth()==date.getMonth()
                            && today.getDate()==date.getDate()){

                        todayWeatherLists.add(item);
                    }
                }

                WeatherList lastItem = fiveDayWeatherResponse.getList().get(fiveDayWeatherResponse.getList().size()-1);
                if(convertStringToDate(lastItem.getDtTxt()).getHours()<12){
                    weatherLists.add(lastItem);
                }

                if(todayWeatherLists.size()>0)
                    mHomeView.setTodayWeatherList(todayWeatherLists);

                mHomeView.setFiveDayWeatherForecasts(weatherLists);
            }

            @Override
            public void onError(Throwable e) {
                String xx="";
            }
        });
    }

    @Override
    public void disposeSubscriptions() {
        if(mCompositeDisposable !=null && !mCompositeDisposable.isDisposed())
            mCompositeDisposable.dispose();
    }

    private Date convertStringToDate(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  date;
    }
}