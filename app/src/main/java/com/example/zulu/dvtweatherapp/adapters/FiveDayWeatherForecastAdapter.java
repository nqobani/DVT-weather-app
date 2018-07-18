package com.example.zulu.dvtweatherapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zulu.dvtweatherapp.R;
import com.example.zulu.dvtweatherapp.adapters.viewholders.WeatherViewHolder;
import com.example.zulu.dvtweatherapp.models.WeatherList;

import java.util.List;

import static com.example.zulu.dvtweatherapp.utils.DateConvertor.convertStringToDate;

public class FiveDayWeatherForecastAdapter extends RecyclerView.Adapter<WeatherViewHolder> {

    private String [] namesOfDays= {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    private List<WeatherList> mWeatherList;

    public FiveDayWeatherForecastAdapter(List<WeatherList> weatherList){
        mWeatherList = weatherList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_weather,parent,false);

        return new WeatherViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherList weatherList = mWeatherList.get(position);

        String nameOfDay=namesOfDays[convertStringToDate(weatherList.getDtTxt()).getDay()];
        holder.setDayName(nameOfDay);

        if(weatherList.getWeather().get(0).getMain().toLowerCase().contains("clear"))
            holder.setIcon(R.drawable.ic_clear_weather);
        else if(weatherList.getWeather().get(0).getMain().toLowerCase().contains("cloud"))
            holder.setIcon(R.drawable.ic_partlysunny_weather);
        else if(weatherList.getWeather().get(0).getMain().toLowerCase().contains("rain"))
            holder.setIcon(R.drawable.ic_rain_weather);

        int maxTemp = weatherList.getMain().getTempMax();

        holder.setTemperature(maxTemp+"\u00b0");
    }


    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    public void setWeatherList(List<WeatherList> weatherList){
        mWeatherList.clear();
        mWeatherList = weatherList;
        notifyDataSetChanged();
    }

}
