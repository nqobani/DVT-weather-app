package com.example.zulu.dvtweatherapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zulu.dvtweatherapp.R;
import com.example.zulu.dvtweatherapp.adapters.viewholders.TodayWeatherViewHolder;
import com.example.zulu.dvtweatherapp.models.WeatherList;

import java.util.List;

import static com.example.zulu.dvtweatherapp.utils.DateConvertor.convertStringToDate;

public class TodayWeatherAdapter extends RecyclerView.Adapter<TodayWeatherViewHolder> {

    private List<WeatherList> mWeatherList;
    public TodayWeatherAdapter(List<WeatherList> weatherList){
        mWeatherList = weatherList;
    }

    @NonNull
    @Override
    public TodayWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_itrm_today_weather,parent,false);

        TodayWeatherViewHolder todayWeatherViewHolder = new TodayWeatherViewHolder(view, parent.getContext());

        return todayWeatherViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TodayWeatherViewHolder holder, int position) {
        WeatherList weatherList = mWeatherList.get(position);

        int maxTemp = weatherList.getMain().getTempMax();
        holder.setTemp(maxTemp+"\u00b0");

        int hours= convertStringToDate(weatherList.getDtTxt()).getHours();
        int minutes = convertStringToDate(weatherList.getDtTxt()).getMinutes();
        String time = null;
        if(hours<10)
            time = "0"+hours+":";
        else
            time = hours+":";

        if(minutes<10)
            time=time+minutes+"0";
        else
            time=time+minutes;

        holder.setTime(time);

        if(weatherList.getWeather().get(0).getMain().toLowerCase().contains("clear"))
            holder.setIcon(R.drawable.ic_clear_weather);
        else if(weatherList.getWeather().get(0).getMain().toLowerCase().contains("cloud"))
            holder.setIcon(R.drawable.ic_partlysunny_weather);
        else if(weatherList.getWeather().get(0).getMain().toLowerCase().contains("rain"))
            holder.setIcon(R.drawable.ic_rain_weather);
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
