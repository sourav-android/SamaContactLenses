package com.sama.samacontactlenses.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.R;


public class ImageShowZoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_image_show_zoom);
        ImageView close_iv =(ImageView)findViewById(R.id.close_iv);
        final ImageView photo_iv =(ImageView)findViewById(R.id.photo_iv);
        String image_url=getIntent().getStringExtra("image_url");
        Glide.with(this).load(image_url).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(photo_iv);
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}