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
import com.sama.samacontactlenses.Adapter.FAQAdapter;
import com.sama.samacontactlenses.Adapter.FAQHeadAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.ANSWER;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_TITLE;
import static com.sama.samacontactlenses.Common.Constant.DESC;
import static com.sama.samacontactlenses.Common.Constant.QUETION;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class FAQActivity extends AppCompatActivity {
    private RecyclerView recy_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.content_faq);
        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.faq));
        recy_view = findViewById(R.id.recy_view);
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
        String server_url = getString(R.string.SERVER_URL) + "show-faq";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(FAQActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(FAQActivity.this, response);
                    if (resultResponse != null) {
                        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                        JSONArray faq = resultResponse.getJSONArray("faq");
                        for (int i = 0; i < faq.length(); i++) {
                            JSONObject object = faq.getJSONObject(i);
                            JSONObject category_details_language=object.getJSONObject("category_details_language");
                            String faq_category = category_details_language.getString("faq_category");
                            String faq_master_t = object.getString("faq_master_t");
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(CATEGORY_TITLE, faq_category);
                            hashMap.put(DESC, faq_master_t);
                            arrayList.add(hashMap);
                        }
                        FAQHeadAdapter adapter = new FAQHeadAdapter(FAQActivity.this, arrayList);
                        recy_view.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(FAQActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(FAQActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(FAQActivity.this);
                } else {
                    MethodClass.error_alert(FAQActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(FAQActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(FAQActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(FAQActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(FAQActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}