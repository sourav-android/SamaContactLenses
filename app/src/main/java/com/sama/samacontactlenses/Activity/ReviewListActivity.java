package com.sama.samacontactlenses.Activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Adapter.NotifcationAdapter;
import com.sama.samacontactlenses.Adapter.ReviewAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.CRAETED_AT;
import static com.sama.samacontactlenses.Common.Constant.IS_POSTED;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.RATING_START;
import static com.sama.samacontactlenses.Common.Constant.USER_NAME;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class ReviewListActivity extends AppCompatActivity {
    private RecyclerView recy_view_review;
    private ArrayList<HashMap<String,String>> arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_review_list);
        TextView title=findViewById(R.id.title);
        title.setText(getString(R.string.review));
        MethodClass.setBottomFun(this);
        recy_view_review = findViewById(R.id.recy_view_trend_prod);
        getList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setMenu(this);
    }

    public void back(View view) {
        onBackPressed();
    }

    public void getList() {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "get-all-review";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("product_id", getIntent().getStringExtra("product_id"));
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("CATGOEY", new JSONObject(params).toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(ReviewListActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(ReviewListActivity.this, response);
                    if (resultResponse != null) {
                        JSONArray review=resultResponse.getJSONArray("productReviews");
                        ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
                        for (int i = 0; i < review.length(); i++) {
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put(PRODUCT_ID,review.getJSONObject(i).getString("product_id"));
                            hashMap.put(PRODUCT_IMAGE,review.getJSONObject(i).getString("image"));
                            hashMap.put(USER_NAME,review.getJSONObject(i).getJSONObject("customer_details").getString("name"));
                            hashMap.put(RATING_START,review.getJSONObject(i).getString("review_stars"));
                            hashMap.put(PRODUCT_DESC,review.getJSONObject(i).getString("review_text"));
                            hashMap.put(CRAETED_AT,review.getJSONObject(i).getString("created_at"));
                            arrayList.add(hashMap);
                        }
                        ReviewAdapter reviewAdapter=new ReviewAdapter(ReviewListActivity.this,arrayList);
                        recy_view_review.setAdapter(reviewAdapter);
                        recy_view_review.setFocusable(false);
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(ReviewListActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(ReviewListActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(ReviewListActivity.this);
                } else {
                    MethodClass.error_alert(ReviewListActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ReviewListActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ReviewListActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ReviewListActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ReviewListActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}