package com.sama.samacontactlenses.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Adapter.CategoryAdapter;
import com.sama.samacontactlenses.Adapter.CelebAdapter;
import com.sama.samacontactlenses.Adapter.HomeSliderAdapter;
import com.sama.samacontactlenses.Adapter.NewAdpater;
import com.sama.samacontactlenses.Adapter.NotifcationAdapter;
import com.sama.samacontactlenses.Adapter.OfferAdapter;
import com.sama.samacontactlenses.Adapter.PastOrderHistoryAdapter;
import com.sama.samacontactlenses.Adapter.RecentOrderHistoryAdapter;
import com.sama.samacontactlenses.Adapter.TrendingAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.AWB_CODE;
import static com.sama.samacontactlenses.Common.Constant.BANNER_BUTTON;
import static com.sama.samacontactlenses.Common.Constant.BANNER_HEADING;
import static com.sama.samacontactlenses.Common.Constant.BANNER_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.BANNER_URL;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_PARENT_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_TITLE;
import static com.sama.samacontactlenses.Common.Constant.CELEB_ID;
import static com.sama.samacontactlenses.Common.Constant.CELEB_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.CELEB_TITLE;
import static com.sama.samacontactlenses.Common.Constant.CREATE_DATE;
import static com.sama.samacontactlenses.Common.Constant.CURR_CODE;
import static com.sama.samacontactlenses.Common.Constant.INTERNAL_STATUS;
import static com.sama.samacontactlenses.Common.Constant.ORDER_ID;
import static com.sama.samacontactlenses.Common.Constant.ORDER_NO;
import static com.sama.samacontactlenses.Common.Constant.PAYMENT_MODE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_FAV;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_OFF;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Common.Constant.RESCHEDULE_DATE;
import static com.sama.samacontactlenses.Common.Constant.RESCHEDULE_TIME;
import static com.sama.samacontactlenses.Common.Constant.SHIPPING_PRICE;
import static com.sama.samacontactlenses.Common.Constant.STATUS;
import static com.sama.samacontactlenses.Common.Constant.TOTAL_ITEM;
import static com.sama.samacontactlenses.Common.Constant.TOTAL_PRICE;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView recy_view_trend_prod,recy_view_trend_prod2;
    private ArrayList<HashMap<String,String>> arrayList;
    private NestedScrollView scrollView;
    private TextView recent_order_text,past_order_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_order_history);
        TextView title=findViewById(R.id.title);
        title.setText(getString(R.string.order_history));
        MethodClass.setBottomFun(this);
        recy_view_trend_prod = findViewById(R.id.recy_view_trend_prod);
        recy_view_trend_prod2 = findViewById(R.id.recy_view_trend_prod2);
        scrollView = findViewById(R.id.scrollView);
        recent_order_text = findViewById(R.id.recent_order_text);
        past_order_text = findViewById(R.id.past_order_text);
        scrollView.setVisibility(View.GONE);
        list();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setMenu(this);
    }

    public void back(View view) {
        onBackPressed();
    }

    public void list() {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "order-history";

        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(OrderHistoryActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(OrderHistoryActivity.this, response);
                    if (resultResponse != null) {
                        ArrayList<HashMap<String,String>> recentArrayList=new ArrayList<>();
                        JSONArray recentorders=resultResponse.getJSONArray("recentorders");
                        for (int i = 0; i < recentorders.length(); i++) {
                            JSONObject recent_obj=recentorders.getJSONObject(i);
                            String id=recent_obj.getString("id");
                            String order_no=recent_obj.getString("order_no");
                            //String adjustment=recent_obj.getString("adjustment");
                            String order_total=recent_obj.getString("order_total");
                            String created_at=recent_obj.getString("created_at");
                            String total_items=recent_obj.getString("total_items");
                            String payment_method=recent_obj.getString("payment_method");
                            String status=recent_obj.getString("status");
                            JSONObject currency = recent_obj.getJSONObject("currency");
                            String currency_code = currency.getString("currency_code");
                            String awb_number = recent_obj.getString("awb_number");
                            String shipping_price = recent_obj.getString("shipping_price");

                            String rescdule_date = recent_obj.getString("rescdule_date");
                            String reschedule_time = recent_obj.getString("reschedule_time");
                            String internal_status = recent_obj.getString("internal_status");

                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put(ORDER_ID,id);
                            hashMap.put(ORDER_NO,order_no);
                            hashMap.put(TOTAL_PRICE,order_total);
                            hashMap.put(CREATE_DATE,created_at);
                            hashMap.put(TOTAL_ITEM,total_items);
                            hashMap.put(PAYMENT_MODE,payment_method);
                            hashMap.put(STATUS,status);
                            hashMap.put(RESCHEDULE_DATE,rescdule_date);
                            hashMap.put(RESCHEDULE_TIME,reschedule_time);
                            hashMap.put(INTERNAL_STATUS,internal_status);
                            hashMap.put(CURR_CODE,currency_code);
                            hashMap.put(AWB_CODE,awb_number);
                            hashMap.put(SHIPPING_PRICE,shipping_price);
                            recentArrayList.add(hashMap);
                        }
                        RecentOrderHistoryAdapter adapter = new RecentOrderHistoryAdapter(OrderHistoryActivity.this,recentArrayList);
                        recy_view_trend_prod.setAdapter(adapter);
                        if (recentorders.length()==0){
                            recent_order_text.setVisibility(View.GONE);
                        }

                        ArrayList<HashMap<String,String>> pastArrayList=new ArrayList<>();
                        JSONArray pastorders=resultResponse.getJSONArray("pastorders");
                        for (int i = 0; i < pastorders.length(); i++) {
                            JSONObject past_obj=pastorders.getJSONObject(i);
                            String id=past_obj.getString("id");
                            String order_no=past_obj.getString("order_no");
                            String order_total=past_obj.getString("order_total");
                            String created_at=past_obj.getString("created_at");
                            String total_items=past_obj.getString("total_items");
                            String payment_method=past_obj.getString("payment_method");
                            String status=past_obj.getString("status");
                            JSONObject currency = past_obj.getJSONObject("currency");
                            String currency_code = currency.getString("currency_code");
                            String awb_number = past_obj.getString("awb_number");
                            String shipping_price = past_obj.getString("shipping_price");

                            String rescdule_date = past_obj.getString("rescdule_date");
                            String reschedule_time = past_obj.getString("reschedule_time");
                            String internal_status = past_obj.getString("internal_status");

                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put(ORDER_ID,id);
                            hashMap.put(ORDER_NO,order_no);
                            hashMap.put(TOTAL_PRICE,order_total);
                            hashMap.put(CREATE_DATE,created_at);
                            hashMap.put(TOTAL_ITEM,total_items);
                            hashMap.put(PAYMENT_MODE,payment_method);
                            hashMap.put(STATUS,status);
                            hashMap.put(RESCHEDULE_DATE,rescdule_date);
                            hashMap.put(RESCHEDULE_TIME,reschedule_time);
                            hashMap.put(INTERNAL_STATUS,internal_status);
                            hashMap.put(CURR_CODE,currency_code);
                            hashMap.put(AWB_CODE,awb_number);
                            hashMap.put(SHIPPING_PRICE,shipping_price);
                            pastArrayList.add(hashMap);
                        }
                        PastOrderHistoryAdapter adapter2 = new PastOrderHistoryAdapter(OrderHistoryActivity.this,pastArrayList);
                        recy_view_trend_prod2.setAdapter(adapter2);
                        if (pastorders.length()==0){
                            past_order_text.setVisibility(View.GONE);
                        }
                        scrollView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(OrderHistoryActivity.this);
                    scrollView.setVisibility(View.GONE);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                scrollView.setVisibility(View.GONE);
                MethodClass.hideProgressDialog(OrderHistoryActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(OrderHistoryActivity.this);
                } else {
                    MethodClass.error_alert(OrderHistoryActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(OrderHistoryActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(OrderHistoryActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(OrderHistoryActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(OrderHistoryActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}