package com.sama.samacontactlenses.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Adapter.CelebDetailsAdapter;
import com.sama.samacontactlenses.Adapter.DetailsImageAdapter;
import com.sama.samacontactlenses.Adapter.HomeSliderAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.BANNER_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_ID;
import static com.sama.samacontactlenses.Common.Constant.CELEB_ID;
import static com.sama.samacontactlenses.Common.Constant.CELEB_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.CELEB_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.CELEB_TITLE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class CelebrityDetailsActivity extends AppCompatActivity {

    private TextView headerName,pname,cname1;
    private ImageView cimage1;
    private Button view;
    private RelativeLayout main_lay;
    public static ArrayList<HashMap<String, String>> tagArrayListId_1;
    private SliderView imageSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_celebrity_details);

        pname = findViewById(R.id.pname);
        headerName = findViewById(R.id.headerName);
        cimage1 = findViewById(R.id.cimage1);
        cname1 = findViewById(R.id.cname1);
        main_lay = findViewById(R.id.main_lay);
        view = findViewById(R.id.view);
        imageSlider = findViewById(R.id.imageSlider);
        getDetails();

    }
    public void getDetails(){
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "celebrity-details/"+getIntent().getStringExtra("celeb_id");
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("CATGOEY", new JSONObject(params).toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(CelebrityDetailsActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(CelebrityDetailsActivity.this, response);
                    if (resultResponse != null) {

                        JSONObject resultArr = resultResponse.getJSONObject("celebrity");
                        String celebName = resultArr.getJSONObject("celebrity_details_by_language").getString("celebrity_name");
                        JSONArray celebrity_to_product = resultArr.getJSONArray("celebrity_to_product");
                        tagArrayListId_1 = new ArrayList<HashMap<String, String>>();
                        for (int i = 0; i <celebrity_to_product.length() ; i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            String Image = celebrity_to_product.getJSONObject(i).getString("image");

                            String productName = celebrity_to_product.getJSONObject(i).getJSONObject("celebrity_product").getString("title");
                            String product_id = celebrity_to_product.getJSONObject(i).getString("product_id");

                            map.put(CELEB_ID,product_id);
                            map.put(CELEB_TITLE,celebName);
                            map.put(CELEB_IMAGE,Image);
                            map.put(PRODUCT_TITLE,productName);
                            tagArrayListId_1.add(map);
                        }

                        CelebDetailsAdapter imageAdapter = new CelebDetailsAdapter(CelebrityDetailsActivity.this, tagArrayListId_1);
                        imageSlider.setSliderAdapter(imageAdapter);
                        imageSlider.startAutoCycle();
                        setValue(celebrity_to_product.getJSONObject(0).getJSONObject("celebrity_product").getString("title"),celebrity_to_product.getJSONObject(0).getString("product_id"));
                        
                        imageSlider.setCurrentPageListener(new SliderView.OnSliderPageListener() {
                            @Override
                            public void onSliderPageChanged(int position) {
                                Log.e("position", String.valueOf(position));
                                try {
                                    imageSlider.stopAutoCycle();
                                    imageSlider.startAutoCycle();
                                    pname.setText(celebrity_to_product.getJSONObject(position).getJSONObject("celebrity_product").getString("title"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(CelebrityDetailsActivity.this, ProductDetailsActivity.class);
                                        try {
                                            i.putExtra("product_id",celebrity_to_product.getJSONObject(position).getString("product_id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        startActivity(i);
                                    }
                                });
                            }
                        });

                        headerName.setText(getString(R.string.celebrities));

                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(CelebrityDetailsActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(CelebrityDetailsActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(CelebrityDetailsActivity.this);
                } else {
                    MethodClass.error_alert(CelebrityDetailsActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(CelebrityDetailsActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(CelebrityDetailsActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(CelebrityDetailsActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(CelebrityDetailsActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void search(View view) {
        Intent I = new Intent(this, SearchActivity.class);
        startActivity(I);
    }

    public void back(View view){
        onBackPressed();
    }
    public void setValue(String name, String id){
        pname.setText(name);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CelebrityDetailsActivity.this, ProductDetailsActivity.class);
                i.putExtra("product_id",id);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setCartCount(this);
    }

    public void startCycle(){
        imageSlider.stopAutoCycle();
        imageSlider.startAutoCycle();
    }
    public void shopping_cart(View view) {
        Intent I = new Intent(this, ShoppingCartActivity.class);
        startActivity(I);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }

}