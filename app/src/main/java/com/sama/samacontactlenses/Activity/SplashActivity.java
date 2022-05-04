package com.sama.samacontactlenses.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    int k = 0;
    private MediaPlayer mMediaPlayer;
    //private ImageView image_anim;
    private GifImageView gifView;

    public static String enable_splash = "", image = "";
    public static Activity splashActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        setContentView(R.layout.activity_splash);
        splashActivity = this;
        //image_anim=findViewById(R.id.image_anim);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(this, R.raw.sound2);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }

        gifView = findViewById(R.id.gifView);
        Glide.with(this)
                .load(R.drawable.sama5)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource instanceof GifDrawable) {
                            ((GifDrawable) resource).setLoopCount(1);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((GifDrawable) resource).stop();
                                    if (enable_splash.equals("N")) {
                                        mMediaPlayer.stop();
                                        Intent I = new Intent(SplashActivity.this, MainActivity.class);
                                        //I.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(I);
                                        //finish();
                                    } else {
                                        mMediaPlayer.stop();
                                        Intent I = new Intent(SplashActivity.this, OpeningAcitvity.class);
                                        //I.putExtra("image",image);
                                        //I.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(I);
                                        //finish();
                                    }
                                }
                            }, 4400);

                        }
                        return false;
                    }
                })
                .into(gifView);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.e(TAG + "=token", token);
                        getSplash(token);
                    }
                });

    }

    public void getSplash(String firebase_reg_no) {
        String server_url = getString(R.string.SERVER_URL) + "after-splash";
        HashMap<String, String> params = new HashMap<String, String>();
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        params.put("device_id", android_id);
        params.put("device_type", "A");
        params.put("firebase_reg_no", firebase_reg_no);
        Log.e("params", MethodClass.Json_rpc_format(params).toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(SplashActivity.this, response);
                    if (resultResponse != null) {

                        String facebook_url = resultResponse.getString("facebook_url");
                        String twitter_url = resultResponse.getString("twitter_url");
                        String instagram_url = resultResponse.getString("instagram_url");
                        String whatsapp_no = resultResponse.getString("whatsapp_no");
                        String youtube_url = resultResponse.getString("youtube_url");
                        PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).edit().putString("facebook_url", facebook_url).apply();
                        PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).edit().putString("twitter_url", twitter_url).apply();
                        PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).edit().putString("instagram_url", instagram_url).apply();
                        PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).edit().putString("whatsapp_no", whatsapp_no).apply();
                        PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).edit().putString("youtube_url", youtube_url).apply();

                        JSONObject setting = resultResponse.getJSONObject("settings");
                        enable_splash = setting.getString("enable_splash");
                        image = setting.getString("image");

                    }
                } catch (JSONException e) {
                    MethodClass.custom_error(SplashActivity.this,e);
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.custom_error(SplashActivity.this,error);
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(SplashActivity.this).addToRequestQueue(request);
    }

    public void onBackPressed() {
        Log.e("My Tags", "onBackPressed");
        k++;
        if (k == 1) {
            Toast.makeText(SplashActivity.this, R.string.double_click_to_exit, Toast.LENGTH_SHORT).show();
        } else {
            mMediaPlayer.stop();
            finish();
        }
    }

    public class GifDrawableImageViewTarget extends ImageViewTarget<Drawable> {

        private int mLoopCount = GifDrawable.LOOP_FOREVER;

        public GifDrawableImageViewTarget(ImageView view, int loopCount) {
            super(view);
            mLoopCount = loopCount;
        }

        public GifDrawableImageViewTarget(ImageView view, int loopCount, boolean waitForLayout) {
            super(view, waitForLayout);
            mLoopCount = loopCount;
        }

        @Override
        protected void setResource(@Nullable Drawable resource) {
            if (resource instanceof GifDrawable) {
                ((GifDrawable) resource).setLoopCount(mLoopCount);
            }
            view.setImageDrawable(resource);
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}