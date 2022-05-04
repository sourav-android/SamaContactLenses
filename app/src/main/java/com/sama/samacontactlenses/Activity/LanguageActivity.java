package com.sama.samacontactlenses.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LanguageActivity extends AppCompatActivity {
    MaterialCardView engMat,arbMat;
    ImageView tickArb,tickEng;
    TextView arbTxt,engTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_language);
        engMat = findViewById(R.id.engMat);
        arbMat = findViewById(R.id.arbMat);
        tickArb = findViewById(R.id.tickArb);
        tickEng = findViewById(R.id.tickEng);
        arbTxt = findViewById(R.id.arbTxt);
        engTxt = findViewById(R.id.engTxt);
        MethodClass.setMenu(this);


        if(PreferenceManager.getDefaultSharedPreferences(LanguageActivity.this).getString("lang","en").equals("en")){
            engMat.setStrokeColor(getResources().getColor(R.color.colorPrimary));
            arbMat.setStrokeColor(getResources().getColor(R.color.border));
            tickEng.setVisibility(View.VISIBLE);
            tickArb.setVisibility(View.GONE);
            engTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
            arbTxt.setTextColor(getResources().getColor(R.color.txtColor));
        }else {

            engMat.setStrokeColor(getResources().getColor(R.color.border));
            arbMat.setStrokeColor(getResources().getColor(R.color.colorPrimary));
            tickEng.setVisibility(View.GONE);
            tickArb.setVisibility(View.VISIBLE);
            engTxt.setTextColor(getResources().getColor(R.color.txtColor));
            arbTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        engMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                engMat.setStrokeColor(getResources().getColor(R.color.colorPrimary));
                arbMat.setStrokeColor(getResources().getColor(R.color.border));
                tickEng.setVisibility(View.VISIBLE);
                tickArb.setVisibility(View.GONE);
                engTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
                arbTxt.setTextColor(getResources().getColor(R.color.txtColor));
                PreferenceManager.getDefaultSharedPreferences(LanguageActivity.this).edit().putString("lang","en").commit();
                MethodClass.set_locale(LanguageActivity.this);
            }
        });
        arbMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                engMat.setStrokeColor(getResources().getColor(R.color.border));
                arbMat.setStrokeColor(getResources().getColor(R.color.colorPrimary));
                tickEng.setVisibility(View.GONE);
                tickArb.setVisibility(View.VISIBLE);
                engTxt.setTextColor(getResources().getColor(R.color.txtColor));
                arbTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
                PreferenceManager.getDefaultSharedPreferences(LanguageActivity.this).edit().putString("lang","ar").commit();
                MethodClass.set_locale(LanguageActivity.this);
            }
        });

    }

    @SuppressLint("WrongConstant")
    public void home_menu(View view) {
        DrawerLayout drawer_layout = findViewById(R.id.drawer_layout);
        drawer_layout.openDrawer(Gravity.START);
    }
    public void start(View view){
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "change-lang";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lang",PreferenceManager.getDefaultSharedPreferences(this).getString("lang","en"));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    MethodClass.hideProgressDialog(LanguageActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(LanguageActivity.this, response);
                    if (resultResponse != null) {

                        String status = resultResponse.getString("status");
                        Intent I = new Intent(LanguageActivity.this,HomeActivity.class);
                        I.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(I);
                        finish();

                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(LanguageActivity.this);
                    MethodClass.error_alert(LanguageActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(LanguageActivity.this);
                MethodClass.error_alert(LanguageActivity.this);
            }
        }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(LanguageActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(LanguageActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(LanguageActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(LanguageActivity.this).addToRequestQueue(request);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}