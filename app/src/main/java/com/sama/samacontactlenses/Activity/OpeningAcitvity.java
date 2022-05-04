package com.sama.samacontactlenses.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifImageView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.R;

import static com.sama.samacontactlenses.Common.Constant.SPLASH_IMAGE_URL;

public class OpeningAcitvity extends AppCompatActivity {

    private GifImageView splash;
    private ImageView loader_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_opening_acitvity);
        splash = findViewById(R.id.splash);
        loader_img = findViewById(R.id.loader_img);

        if(!SplashActivity.image.equals(null) && !SplashActivity.image.equals("")){
            String Image = SplashActivity.image;
            String currentString = Image;
            String[] separated = currentString.split("\\.");
            Log.e("LOG", separated[1].toString());
            if(separated[1].equals("gif") || separated[1].equals("Gif")){
                Glide.with(this).asGif()
                        .load(SPLASH_IMAGE_URL+Image)
                        .listener(new RequestListener<GifDrawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                loader_img.setVisibility(View.GONE);
                                splash.setImageResource(R.drawable.splash573);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                loader_img.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(splash);
            }else {
                Glide.with(this)
                        .load(SPLASH_IMAGE_URL+Image)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                loader_img.setVisibility(View.GONE);
                                splash.setImageResource(R.drawable.splash573);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                loader_img.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(splash);
            }

        }
        if (!SplashActivity.image.equals("")){
            SplashActivity.splashActivity.finish();
        }

    }


    public void choose(View view){
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_logged_in",false)){
            Intent I = new Intent(OpeningAcitvity.this,HomeActivity.class);
            I.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(I);
        }else {
            Intent I = new Intent(OpeningAcitvity.this,MainActivity.class);
            I.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(I);
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}