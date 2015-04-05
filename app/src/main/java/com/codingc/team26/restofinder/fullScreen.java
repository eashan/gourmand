package com.codingc.team26.restofinder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;


public class fullScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        int position = getIntent().getIntExtra("position", -1);
        String url = getIntent().getExtras().getString("url");

        setTitle("Full Image");

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setMaxHeight(1000);
        imageView.setMaxWidth(600);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(url,imageView);
    }

}
