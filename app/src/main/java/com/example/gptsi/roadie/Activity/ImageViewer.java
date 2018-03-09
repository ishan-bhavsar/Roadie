package com.example.gptsi.roadie.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.gptsi.roadie.R;
import com.example.gptsi.roadie.Util.touchCallback;
import com.example.gptsi.roadie.Util.touch_util;

import java.util.ArrayList;

public class ImageViewer extends AppCompatActivity {

    ImageView image;
    ArrayList<String> mList;
    int mIndex = 0;
    touchCallback callback;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        image = findViewById(R.id.image);
        toolbar=(Toolbar)findViewById(R.id.appbar);

        toolbar.setTitle("Items");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        Intent i = getIntent();
        mList = i.getStringArrayListExtra("images");
        Log.d("ImageViewer",mList.toString());

        Glide.with(ImageViewer.this).load(mList.get(mIndex)).into(image);

        callback = new touchCallback() {
            @Override
            public void onSwipeRight() {
                mIndex = (mIndex+1) % mList.size();
                Log.d("ImageViewer","swipe Index"+mIndex);
                Glide.with(ImageViewer.this).load(mList.get(mIndex)).into(image);
            }

            @Override
            public void onSwipeLeft() {
                mIndex = mIndex==0? (mList.size()-1):(mIndex-1);
                Log.d("ImageViewer","swipe Index"+mIndex);
                Glide.with(ImageViewer.this).load(mList.get(mIndex)).into(image);
            }

            @Override
            public void onSwipeTop() {

            }

            @Override
            public void onSwipeBottom() {

            }
        };

        image.setOnTouchListener(new touch_util(ImageViewer.this,callback));
    }
}
