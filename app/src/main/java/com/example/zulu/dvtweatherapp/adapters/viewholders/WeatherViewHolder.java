package com.example.zulu.dvtweatherapp.adapters.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zulu.dvtweatherapp.R;
import com.example.zulu.dvtweatherapp.utils.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.zulu.dvtweatherapp.utils.ImageLoader.loadImage;

public class WeatherViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.day_name_textview)
    TextView mTExtViewDayName;

    @BindView(R.id.temperature_textview)
    TextView mTExtViewTemperature;

    @BindView(R.id.weather_icon_imageview)
    ImageView mImageViewWeatherIcon;

    Context mContext;

    public WeatherViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = context;
    }

    public void setDayName(String nameOfTheDay){
        mTExtViewDayName.setText(nameOfTheDay);
    }

    public void setTemperature(String temperature){
        mTExtViewTemperature.setText(temperature);
    }

    public void setIcon(int drawableId){
        loadImage(mContext, mImageViewWeatherIcon, drawableId,false);
    }
}
