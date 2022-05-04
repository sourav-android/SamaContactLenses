package com.sama.samacontactlenses.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Adapter.NotifcationAdapter;
import com.sama.samacontactlenses.Adapter.PastOrderHistoryAdapter;
import com.sama.samacontactlenses.Adapter.RecentOrderHistoryAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.CATEGORY_ID;
import static com.sama.samacontactlenses.Common.Constant.CRAETED_AT;
import static com.sama.samacontactlenses.Common.Constant.CREATE_DATE;
import static com.sama.samacontactlenses.Common.Constant.CURR_CODE;
import static com.sama.samacontactlenses.Common.Constant.NOTIFICATION_COUNT;
import static com.sama.samacontactlenses.Common.Constant.NOTIICATION_TYPE;
import static com.sama.samacontactlenses.Common.Constant.ORDER_ID;
import static com.sama.samacontactlenses.Common.Constant.ORDER_NO;
import static com.sama.samacontactlenses.Common.Constant.PAYMENT_MODE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Common.Constant.SHARED_PREF;
import static com.sama.samacontactlenses.Common.Constant.STATUS;
import static com.sama.samacontactlenses.Common.Constant.TOTAL_ITEM;
import static com.sama.samacontactlenses.Common.Constant.TOTAL_PRICE;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recy_view_trend_prod;
    private ArrayList<HashMap<String,String>> arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_notifcation);
        TextView title=findViewById(R.id.title);
        title.setText(getString(R.string.notification));
        MethodClass.setBottomFun(this);
        recy_view_trend_prod = findViewById(R.id.recy_view_trend_prod);
        list();
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
        SharedPreferences pref = getSharedPreferences(SHARED_PREF, 0);
        String firebase_reg_no = pref.getString("regId", null);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String server_url = getString(R.string.SERVER_URL) + "get-all-notification";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", android_id);
        params.put("device_type", "A");
        params.put("firebase_reg_no",firebase_reg_no);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(NotificationActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(NotificationActivity.this, response);
                    if (resultResponse != null) {
                        NOTIFICATION_COUNT = resultResponse.getString("notification_count");
                        MethodClass.setMenu(NotificationActivity.this);
                        ArrayList<HashMap<String,String>> recentArrayList=new ArrayList<>();
                        JSONArray notifications=resultResponse.getJSONArray("notifications");
                        for (int i = 0; i < notifications.length(); i++) {
                            JSONObject object=notifications.getJSONObject(i);
                            String id=object.getString("id");
                            String title_eng=object.getString("title_eng");
                            String title_arabic=object.getString("title_arabic");
                            String body_eng=object.getString("body_eng");
                            String body_arabic=object.getString("body_arabic");
                            String created_at=object.getString("created_at");
                            String image=object.getString("image");
                            String order_id=object.getString("order_id");
                            String type=object.getString("type");
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put(PRODUCT_ID,id);
                            String language = PreferenceManager.getDefaultSharedPreferences(NotificationActivity.this).getString("lang","en");
                            if (language.equals("en")) {
                                if (!body_eng.equalsIgnoreCase("null")) {
                                    hashMap.put(PRODUCT_DESC, body_eng);
                                    hashMap.put(PRODUCT_TITLE, title_eng);
                                } else {
                                    hashMap.put(PRODUCT_DESC, "");
                                    hashMap.put(PRODUCT_TITLE, "");
                                }
                            } else {
                                if (!body_arabic.equalsIgnoreCase("null")) {
                                    hashMap.put(PRODUCT_DESC, body_arabic);
                                    hashMap.put(PRODUCT_TITLE, title_arabic);
                                } else {
                                    hashMap.put(PRODUCT_DESC, "");
                                    hashMap.put(PRODUCT_TITLE, "");
                                }

                            }
                            hashMap.put(CRAETED_AT,created_at);
                            hashMap.put(PRODUCT_IMAGE,image);
                            hashMap.put(NOTIICATION_TYPE,type);
                            if (type.equals("O")){
                                hashMap.put(ORDER_ID,order_id);
                            }
                            if (type.equals("C")){
                                String category_id=object.getString("category_id");
                                hashMap.put(CATEGORY_ID,category_id);
                            }
                            recentArrayList.add(hashMap);
                        }
                        NotifcationAdapter adapter = new NotifcationAdapter(NotificationActivity.this,recentArrayList);
                        recy_view_trend_prod.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(NotificationActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(NotificationActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(NotificationActivity.this);
                } else {
                    MethodClass.error_alert(NotificationActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(NotificationActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(NotificationActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(NotificationActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(NotificationActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}