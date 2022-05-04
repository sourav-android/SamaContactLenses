package com.sama.samacontactlenses.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Adapter.AddressAdapter;
import com.sama.samacontactlenses.Adapter.BillingAddressAdapter;
import com.sama.samacontactlenses.Adapter.ReviewAdapter;
import com.sama.samacontactlenses.Adapter.ShippingAddressAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.ADDRESS_ID;
import static com.sama.samacontactlenses.Common.Constant.ADDRESS_LINE_1;
import static com.sama.samacontactlenses.Common.Constant.ADDRESS_LINE_2;
import static com.sama.samacontactlenses.Common.Constant.CITY;
import static com.sama.samacontactlenses.Common.Constant.COUNTRY_NAME;
import static com.sama.samacontactlenses.Common.Constant.CRAETED_AT;
import static com.sama.samacontactlenses.Common.Constant.EMAIL_ADDRESS;
import static com.sama.samacontactlenses.Common.Constant.FULL_NAME;
import static com.sama.samacontactlenses.Common.Constant.HEADING;
import static com.sama.samacontactlenses.Common.Constant.IS_DEFAULT;
import static com.sama.samacontactlenses.Common.Constant.PHONE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.RATING_START;
import static com.sama.samacontactlenses.Common.Constant.STATE;
import static com.sama.samacontactlenses.Common.Constant.USER_NAME;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class AddressActivity extends AppCompatActivity {
    private RecyclerView recy_view;
    private ArrayList<HashMap<String,String>> adress_arrayList;
    Button add_adres_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_adress);
        TextView title=findViewById(R.id.title);
        title.setText(getString(R.string.address));
        MethodClass.setBottomFun(this);
        recy_view = findViewById(R.id.recy_view);
        add_adres_btn = findViewById(R.id.add_adres_btn);
        add_adres_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddressActivity.this,AddAddressActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setMenu(this);
        getList();
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
        String server_url = getString(R.string.SERVER_URL) + "user-address-book";
        Log.e("server_url", server_url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, server_url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(AddressActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(AddressActivity.this, response);
                    if (resultResponse != null) {
                        JSONArray user_address_book = resultResponse.getJSONArray("user_address_book");
                        adress_arrayList = new ArrayList<>();
                        for (int i = 0; i < user_address_book.length(); i++) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            JSONObject object = user_address_book.getJSONObject(i);
                            hashMap.put(ADDRESS_ID, object.getString("id"));
                            hashMap.put(EMAIL_ADDRESS, object.getString("email"));
                            hashMap.put(FULL_NAME, object.getString("full_name"));
                            hashMap.put(PHONE, object.getString("phone"));
                            hashMap.put(ADDRESS_LINE_1, object.getString("address_line_1"));
                            hashMap.put(ADDRESS_LINE_2, object.getString("address_line_2"));
                            hashMap.put(CITY, object.getString("city"));
                            hashMap.put(STATE, object.getString("state"));
                            hashMap.put(HEADING, object.getString("address_heading"));
                            hashMap.put(IS_DEFAULT, object.getString("is_default"));
                            hashMap.put(COUNTRY_NAME, object.getJSONObject("get_country").getString("name"));
                            adress_arrayList.add(hashMap);
                        }
                        AddressAdapter addressActivity = new AddressAdapter(AddressActivity.this, adress_arrayList);
                        recy_view.setAdapter(addressActivity);
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(AddressActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(AddressActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(AddressActivity.this);
                } else {
                    MethodClass.error_alert(AddressActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(AddressActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(AddressActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(AddressActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(AddressActivity.this).addToRequestQueue(jsonObjectRequest);
    }



    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }

}