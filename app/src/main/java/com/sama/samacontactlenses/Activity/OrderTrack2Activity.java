package com.sama.samacontactlenses.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Adapter.CountryAdapter;
import com.sama.samacontactlenses.Adapter.OrderTrackingAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.TRACK_ACTIVITY;
import static com.sama.samacontactlenses.Common.Constant.TRACK_DATE;
import static com.sama.samacontactlenses.Common.Constant.TRACK_DETAILS;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class OrderTrack2Activity extends AppCompatActivity {
    private RecyclerView track_recy_view;
    private NestedScrollView scrollView;
    private TextView order_no, order_date, total_price, paybale_mode, item_count, status, tracking_tv;

    private EditText trac_order_et;
    private Button track_button;
    private LinearLayout track_lay;
    private TextView cariar_awb_no_tv, track_on_web_tv;
    private Spinner shi_spin_country;
    private ArrayList<MethodClass.CountryModelName> countryArrayList;
    private String country_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_order_track_2);
        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.traking_detils));
        track_recy_view = findViewById(R.id.track_recy_view);
        order_no = findViewById(R.id.order_no);
        order_date = findViewById(R.id.order_date);
        total_price = findViewById(R.id.total_price);
        paybale_mode = findViewById(R.id.paybale_mode);
        shi_spin_country = findViewById(R.id.shi_spin_country);
        item_count = findViewById(R.id.item_count);
        tracking_tv = findViewById(R.id.tracking_tv);
        status = findViewById(R.id.status);
        scrollView = findViewById(R.id.scrollView);
        track_button = findViewById(R.id.track_button);
        trac_order_et = findViewById(R.id.trac_order_et);
        track_lay = findViewById(R.id.track_lay);
        track_lay.setVisibility(View.GONE);
        cariar_awb_no_tv = findViewById(R.id.cariar_awb_no_tv);
        track_on_web_tv = findViewById(R.id.track_on_web_tv);

        track_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trac_order_et.length() == 0) {
                    trac_order_et.setError(getString(R.string.enter_awb_no));
                    trac_order_et.requestFocus();
                    return;
                }
                track_on_web_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://smsaexpress.com/trackingdetails?tracknumbers=" + trac_order_et.getText().toString().trim()));
                        startActivity(i);
                    }
                });
                cariar_awb_no_tv.setText(getString(R.string.awb_no) + "" + trac_order_et.getText().toString().trim());
                getTrackTData(trac_order_et.getText().toString().trim());
            }
        });
        getdata();

    }

    public void back(View view) {
        super.onBackPressed();
    }

    public void getdata() {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "get-tracking-country";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("jsonObject", jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(OrderTrack2Activity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(OrderTrack2Activity.this, response);
                    if (resultResponse != null) {

                        JSONArray countries = resultResponse.getJSONArray("countries");
                        countryArrayList = new ArrayList<>();
                        countryArrayList.add(new MethodClass.CountryModelName(getString(R.string.select_country), "0", "0", "0"));
                        for (int i = 0; i < countries.length(); i++) {
                            String id = countries.getJSONObject(i).getString("id");
                            String name = countries.getJSONObject(i).getString("name");
                            String sortname = countries.getJSONObject(i).getString("sortname");
                            String country_code = countries.getJSONObject(i).getString("country_code");
                            MethodClass.CountryModelName countryModelName = new MethodClass.CountryModelName(name, sortname, country_code, id);
                            countryArrayList.add(countryModelName);
                        }
                        CountryAdapter countryAdapter = new CountryAdapter(OrderTrack2Activity.this, countryArrayList);
                        shi_spin_country.setAdapter(countryAdapter);
                        shi_spin_country.setEnabled(false);
                        shi_spin_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                MethodClass.CountryModelName stringWithTag = countryArrayList.get(position);
                                if (!stringWithTag.tag.equals("0")) {
                                    country_id = String.valueOf(stringWithTag.tag);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(OrderTrack2Activity.this);
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
                MethodClass.hideProgressDialog(OrderTrack2Activity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(OrderTrack2Activity.this);
                } else {
                    MethodClass.error_alert(OrderTrack2Activity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(OrderTrack2Activity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(OrderTrack2Activity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(OrderTrack2Activity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(OrderTrack2Activity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void openSpinPopup(View view) {
        MethodClass.countryNamePopup(OrderTrack2Activity.this, countryArrayList, shi_spin_country);
    }

    /*public void getTrackTData(String awb_no) {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "track-order";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("order_no", getIntent().getStringExtra("order_id"));
        params.put("awb_no", awb_no);
        params.put("country_id", country_id );
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("jsonObject",jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(OrderTrack2Activity.this);
                Log.e("respUser", response.toString());
                try {
                    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                    //JSONObject soapBody = response.getJSONObject("soapBody").getJSONObject("getTrackingResponse").getJSONObject("getTrackingResult").getJSONObject("diffgrdiffgram").getJSONObject("NewDataSet");
                    if (response.has("error")){
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(OrderTrack2Activity.this, DialogTypes.TYPE_ERROR)
                                .setTitle(response.getJSONObject("error").getString("message"))
                                .setDescription(response.getJSONObject("error").getString("meaning"))
                                .setPositiveText(getString(R.string.ok))
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                    }
                                })
                                .build();
                        alertDialog.show();
                        return;
                    }
                    JSONObject soapBody1 = response.getJSONObject("xml").getJSONObject("soapBody");
                    if (soapBody1.has("getTrackingResponse")){
                        Object getTrackingResponse=new JSONTokener(soapBody1.getString("getTrackingResponse")).nextValue();
                        if (getTrackingResponse instanceof JSONObject) {
                            JSONObject NewDataSet = response.getJSONObject("soapBody").getJSONObject("getTrackingResponse").getJSONObject("getTrackingResult").getJSONObject("diffgrdiffgram").getJSONObject("NewDataSet");
                            String data = NewDataSet.getString("Tracking");
                            Object json = new JSONTokener(data).nextValue();
                            if (json instanceof JSONObject) {
                                JSONObject object = new JSONObject(data);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put(TRACK_DATE, object.getString("Date"));
                                hashMap.put(TRACK_ACTIVITY, object.getString("Activity"));
                                hashMap.put(TRACK_DETAILS, object.getString("Details"));
                                //hashMap.put(TRACK_LOCATION, object.getString("Location"));
                                arrayList.add(hashMap);
                            } else if (json instanceof JSONArray) {
                                JSONArray jsonArray = new JSONArray(data);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put(TRACK_DATE, object.getString("Date"));
                                    hashMap.put(TRACK_ACTIVITY, object.getString("Activity"));
                                    hashMap.put(TRACK_DETAILS, object.getString("Details"));
                                    // hashMap.put(TRACK_LOCATION, object.getString("Location"));
                                    arrayList.add(hashMap);
                                }
                            }
                        }
                        OrderTrackingAdapter trackingAdapter = new OrderTrackingAdapter(OrderTrack2Activity.this, arrayList);
                        track_recy_view.setAdapter(trackingAdapter);
                        track_lay.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    MethodClass.error_alert(OrderTrack2Activity.this);

                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(OrderTrack2Activity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(OrderTrack2Activity.this);
                } else {
                    MethodClass.error_alert(OrderTrack2Activity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(OrderTrack2Activity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(OrderTrack2Activity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(OrderTrack2Activity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(OrderTrack2Activity.this).addToRequestQueue(jsonObjectRequest);
    }*/


    public void getTrackTData(String awb_no) {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "track-order";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("order_no", awb_no);
        params.put("country_id", country_id);
        params.put("awb_no", awb_no);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(OrderTrack2Activity.this);
                Log.e("respUser", response.toString());
                try {
                    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                    //JSONObject soapBody = response.getJSONObject("soapBody").getJSONObject("getTrackingResponse").getJSONObject("getTrackingResult").getJSONObject("diffgrdiffgram").getJSONObject("NewDataSet");
                    if (response.has("error")) {
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(OrderTrack2Activity.this, DialogTypes.TYPE_ERROR)
                                .setTitle(response.getJSONObject("error").getString("message"))
                                .setDescription(response.getJSONObject("error").getString("meaning"))
                                .setPositiveText(getString(R.string.ok))
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                    }
                                })
                                .build();
                        alertDialog.show();
                        return;
                    }
                    if (response.getString("type").equals("O")) {
                        Intent intent = new Intent(OrderTrack2Activity.this, OrderDetailsActivity.class);
                        intent.putExtra("order_id", response.getString("order_no"));
                        startActivity(intent);
                        return;
                    } else {
                        Log.e("Date3", "Date3");

                        /*JSONObject soapBody1 = response.getJSONObject("").getJSONObject("soapBody");*/
                        JSONObject soapBody1 = response.getJSONObject("xml").getJSONObject("soapBody");
                        if (soapBody1.has("getTrackingwithRefResponse")) {
                            Log.e("Date4", "Date4");
                            Object getTrackingResponse = new JSONTokener(soapBody1.getString("getTrackingwithRefResponse")).nextValue();
                            if (getTrackingResponse instanceof JSONObject) {
                                Log.e("Date5", "Date5");
                                JSONObject NewDataSet = response.getJSONObject("xml").getJSONObject("soapBody").getJSONObject("getTrackingwithRefResponse").getJSONObject("getTrackingwithRefResult").getJSONObject("diffgrdiffgram").getJSONObject("NewDataSet");
                                String data = NewDataSet.getString("Tracking");
                                Object json = new JSONTokener(data).nextValue();
                                if (json instanceof JSONObject) {
                                    JSONObject object = new JSONObject(data);
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put(TRACK_DATE, object.getString("Date"));
                                    hashMap.put(TRACK_ACTIVITY, object.getString("Activity"));
                                    hashMap.put(TRACK_DETAILS, object.getString("Details"));
                                    //hashMap.put(TRACK_LOCATION, object.getString("Location"));
                                    arrayList.add(hashMap);
                                    Log.e("Date2", "Date2");

                                } else if (json instanceof JSONArray) {
                                    JSONArray jsonArray = new JSONArray(data);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put(TRACK_DATE, object.getString("Date"));
                                        hashMap.put(TRACK_ACTIVITY, object.getString("Activity"));
                                        hashMap.put(TRACK_DETAILS, object.getString("Details"));
                                        // hashMap.put(TRACK_LOCATION, object.getString("Location"));
                                        arrayList.add(hashMap);
                                        Log.e("Date1", "Date1");
                                    }
                                }
                            }
                            OrderTrackingAdapter trackingAdapter = new OrderTrackingAdapter(OrderTrack2Activity.this, arrayList);
                            track_recy_view.setAdapter(trackingAdapter);
                            track_lay.setVisibility(View.VISIBLE);
                        }
                    }


                } catch (JSONException e) {
                    MethodClass.error_alert(OrderTrack2Activity.this);

                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(OrderTrack2Activity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(OrderTrack2Activity.this);
                } else {
                    MethodClass.error_alert(OrderTrack2Activity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(OrderTrack2Activity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(OrderTrack2Activity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(OrderTrack2Activity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(OrderTrack2Activity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }

}