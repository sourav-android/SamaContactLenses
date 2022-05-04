package com.sama.samacontactlenses.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FilterActivity extends AppCompatActivity {
    AutoCompleteTextView key_autotext;
    private RadioButton name_rbtn, price_H_t_L_rbtn, price_L_t_H_rbtn;
    private Button apply_btn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_filter);
        TextView title = findViewById(R.id.title);
        title.setText(R.string.Filter);
        MethodClass.setBottomFun(this);
        key_autotext = findViewById(R.id.key_autotext);
        name_rbtn = findViewById(R.id.name_rbtn);
        price_H_t_L_rbtn = findViewById(R.id.price_H_t_L_rbtn);
        price_L_t_H_rbtn = findViewById(R.id.price_L_t_H_rbtn);
        apply_btn = findViewById(R.id.apply_btn);

        if (!Objects.equals(getIntent().getExtras(), null)) {
            if (getIntent().getExtras().containsKey("key")) {
                if (!getIntent().getStringExtra("key").equals("")) {
                    key_autotext.setText(getIntent().getStringExtra("key"));
                }
            }
            if (getIntent().getExtras().containsKey("sort")) {
                if (!getIntent().getStringExtra("sort").equals("")) {
                    if (getIntent().getStringExtra("sort").equals("N")) {
                        name_rbtn.setChecked(true);
                    } else if (getIntent().getStringExtra("sort").equals("H")) {
                        price_H_t_L_rbtn.setChecked(true);
                    } else if (getIntent().getStringExtra("sort").equals("L")) {
                        price_L_t_H_rbtn.setChecked(true);
                    }
                }
            }


        }
        apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.search_activity.finish();
                String key = "" + key_autotext.getText().toString().trim();
                String sort = "";
                if (name_rbtn.isChecked()) {
                    sort = "N";
                } else if (price_H_t_L_rbtn.isChecked()) {
                    sort = "H";
                } else if (price_L_t_H_rbtn.isChecked()) {
                    sort = "L";
                }

                Intent intent = new Intent(FilterActivity.this, SearchActivity.class);
                intent.putExtra("category_id", "");
                intent.putExtra("key", "" + key);
                intent.putExtra("sort", "" + sort);
                startActivity(intent);
                finish();
            }
        });

        getProdName();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setMenu(this);
    }

    public void back(View view) {
        onBackPressed();
    }

    public void getProdName() {
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "filter";
        HashMap<String, String> params = new HashMap<String, String>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    MethodClass.hideProgressDialog(FilterActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(FilterActivity.this, response);
                    if (resultResponse != null) {

                        JSONArray product_arr = resultResponse.getJSONArray("product");
                        ArrayList<MethodClass.StringWithTag> productArrayList = new ArrayList<>();
                        for (int i = 0; i < product_arr.length(); i++) {
                            String id = product_arr.getJSONObject(i).getJSONObject("product_by_language").getString("product_id");
                            String name = product_arr.getJSONObject(i).getJSONObject("product_by_language").getString("title");
                            productArrayList.add(new MethodClass.StringWithTag(name, id));
                        }
                        ArrayAdapter<MethodClass.StringWithTag> adapter = new ArrayAdapter<MethodClass.StringWithTag>(FilterActivity.this, android.R.layout.simple_dropdown_item_1line, productArrayList);
                        key_autotext.setAdapter(adapter);

                        key_autotext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                MethodClass.StringWithTag stringWithTag = (MethodClass.StringWithTag) parent.getItemAtPosition(position);
                                String product_id = (String) stringWithTag.tag;
                                Intent intent = new Intent(FilterActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("product_id", product_id);
                                startActivity(intent);
                                finish();
                            }
                        });

                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(FilterActivity.this);
                    MethodClass.error_alert(FilterActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(FilterActivity.this);
                MethodClass.error_alert(FilterActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(FilterActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(FilterActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(FilterActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(FilterActivity.this).addToRequestQueue(request);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}