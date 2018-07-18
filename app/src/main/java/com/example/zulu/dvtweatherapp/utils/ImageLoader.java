package com.example.zulu.dvtweatherapp.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.zulu.dvtweatherapp.R;

public class ImageLoader {

    public static void loadImage(Context context, ImageView imageView, String url, Boolean hasPlaceHolder){
        Glide.with(context)
                .load(url)
                .into(imageView);
    }
    public static void loadImage(Context context, ImageView imageView, int drawableId, Boolean hasPlaceHolder){
        Glide.with(context)
                .load(drawableId)
                .into(imageView);
    }
}
