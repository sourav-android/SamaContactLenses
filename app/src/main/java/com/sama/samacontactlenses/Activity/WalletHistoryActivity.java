package com.sama.samacontactlenses.Activity;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Adapter.NotifcationAdapter;
import com.sama.samacontactlenses.Adapter.WalletHistoryAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.BALANCE;
import static com.sama.samacontactlenses.Common.Constant.BALANCE_TYPE;
import static com.sama.samacontactlenses.Common.Constant.CRAETED_AT;
import static com.sama.samacontactlenses.Common.Constant.CURR_CODE;
import static com.sama.samacontactlenses.Common.Constant.DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class WalletHistoryActivity extends AppCompatActivity {
    private  TextView wallet_bal_tv;
    private RecyclerView recy_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_wallet_history);
        TextView title=findViewById(R.id.title);
        title.setText(getString(R.string.wallet_history));
        wallet_bal_tv=findViewById(R.id.wallet_bal_tv);
        recy_view=findViewById(R.id.recy_view);
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
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "wallet-list";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("currency_code",getIntent().getStringExtra("currency_code"));
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(WalletHistoryActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(WalletHistoryActivity.this, response);
                    if (resultResponse != null) {
                        if (!resultResponse.getString("balance").equals("null") && !resultResponse.getString("balance").equals(null)){
                            JSONObject balanceObj=resultResponse.getJSONObject("balance");
                            String balance=balanceObj.getString("balance");
                            String currency_code=balanceObj.getJSONObject("currency").getString("currency_code");
                            wallet_bal_tv.setText(balance+" "+currency_code);
                        }


                        ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
                        JSONArray wallet=resultResponse.getJSONArray("wallet");
                        for (int i = 0; i < wallet.length(); i++) {
                            JSONObject object=wallet.getJSONObject(i);
                            String type=object.getString("type");
                            //String adjustment=object.getJSONObject("order_master").getString("adjustment");
                            String amount=object.getString("amount");
                            String currency_code=object.getJSONObject("currency").getString("currency_code");
                            String description=object.getString("description");
                            String created_at=object.getString("created_at");
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put(BALANCE_TYPE,type);
                            hashMap.put(BALANCE,amount);
                            hashMap.put(DESC,description);
                            hashMap.put(CURR_CODE,currency_code);
                            hashMap.put(CRAETED_AT,created_at);
                            arrayList.add(hashMap);
                        }
                        WalletHistoryAdapter adapter = new WalletHistoryAdapter(WalletHistoryActivity.this,arrayList);
                        recy_view.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(WalletHistoryActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(WalletHistoryActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(WalletHistoryActivity.this);
                } else {
                    MethodClass.error_alert(WalletHistoryActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(WalletHistoryActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(WalletHistoryActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(WalletHistoryActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(WalletHistoryActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}