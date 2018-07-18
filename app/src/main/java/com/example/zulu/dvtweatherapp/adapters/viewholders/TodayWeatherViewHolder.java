package com.example.zulu.dvtweatherapp.adapters.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zulu.dvtweatherapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.zulu.dvtweatherapp.utils.ImageLoader.loadImage;

public class TodayWeatherViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.today_temp_textview)
    TextView mTextViewTodayTemp;

    @BindView(R.id.time_textview)
    TextView mTextViewTime;

    @BindView(R.id.today_weather_icon_imageview)
    ImageView mImageViewTodayWeatherIcon;

    Context mContext;
    public TodayWeatherViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        mContext = context;
    }

    public void setTemp(String temp){
        mTextViewTodayTemp.setText(temp);
    }

    public void setTime(String time){
        mTextViewTime.setText(time);
    }

    public void setIcon(int drawableId){
        loadImage(mContext, mImageViewTodayWeatherIcon, drawableId,false);
    }


}
