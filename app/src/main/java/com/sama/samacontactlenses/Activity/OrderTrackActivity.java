package com.sama.samacontactlenses.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.sama.samacontactlenses.Adapter.OrderTrackingAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.TRACK_ACTIVITY;
import static com.sama.samacontactlenses.Common.Constant.TRACK_DATE;
import static com.sama.samacontactlenses.Common.Constant.TRACK_DETAILS;
import static com.sama.samacontactlenses.Common.Constant.TRACK_LOCATION;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class OrderTrackActivity extends AppCompatActivity {
    private RecyclerView track_recy_view;
    private NestedScrollView scrollView;
    private TextView order_no, order_date, total_price, paybale_mode, item_count, status,tracking_tv;
    private TextView stimated_date_tv,cariar_title_tv,cariar_awb_no_tv,track_on_web_tv;
    private ImageView cariar_img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_order_track);
        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.traking_detils));
        track_recy_view = findViewById(R.id.track_recy_view);
        order_no = findViewById(R.id.order_no);
        order_date = findViewById(R.id.order_date);
        total_price = findViewById(R.id.total_price);
        paybale_mode = findViewById(R.id.paybale_mode);
        item_count = findViewById(R.id.item_count);
        tracking_tv = findViewById(R.id.tracking_tv);
        status = findViewById(R.id.status);
        scrollView = findViewById(R.id.scrollView);
        stimated_date_tv = findViewById(R.id.stimated_date_tv);
        cariar_title_tv = findViewById(R.id.cariar_title_tv);
        cariar_awb_no_tv = findViewById(R.id.cariar_awb_no_tv);
        track_on_web_tv = findViewById(R.id.track_on_web_tv);
        cariar_img = findViewById(R.id.cariar_img);
        scrollView.setVisibility(View.GONE);
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
        String server_url = getString(R.string.SERVER_URL) + "order-history-details/" + getIntent().getStringExtra("order_id");
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(OrderTrackActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(OrderTrackActivity.this, response);
                    if (resultResponse != null) {
                        JSONObject orders = resultResponse.getJSONObject("orders");
                        JSONObject currency = orders.getJSONObject("currency");
                        String currency_code = currency.getString("currency_code");
                        String order_no_str = orders.getString("order_no");
                        String order_total = orders.getString("order_total");
                        String created_at = orders.getString("created_at");
                        String total_items = orders.getString("total_items");
                        String payment_method = orders.getString("payment_method");
                        String shipping_country_code = orders.getString("shipping_country_code");
                        String status_str = orders.getString("status");
                        if (status_str.equals("N")){
                            status.setText(getString(R.string.new_str));
                            status.setTextColor(getResources().getColor(R.color.blue_clr));
                        }else if (status_str.equals("C")){
                            status.setText(getString(R.string.cancelled));
                            status.setTextColor(getResources().getColor(R.color.red_clr));
                        }else if (status_str.equals("R")){
                            status.setText(getString(R.string.ready_to_ship));
                            status.setTextColor(getResources().getColor(R.color.green_txt_clr));
                        }else if (status_str.equals("S")){
                            status.setText(getString(R.string.shipped));
                            status.setTextColor(getResources().getColor(R.color.green_txt_clr));
                        }else if (status_str.equals("D")){
                            status.setText(getString(R.string.delivered));
                            status.setTextColor(getResources().getColor(R.color.green_txt_clr));
                        }
                        order_no.setText(getString(R.string.order_no) + order_no_str);

                        total_price.setText(order_total + " " + currency_code);
                        item_count.setText("(" + getString(R.string.item_colon) + total_items + ")");
                        tracking_tv.setText(getString(R.string.awb_no)+orders.getString("awb_number"));

                        if (payment_method.equals("O")) {
                            paybale_mode.setText(getString(R.string.payment_mode) + getString(R.string.only_online));
                        } else if (payment_method.equals("C")) {
                            paybale_mode.setText(getString(R.string.payment_mode) + getString(R.string.cash_on_delivery));
                        } else if (payment_method.equals("W")) {
                            paybale_mode.setText(getString(R.string.payment_mode) + getString(R.string.wallet));
                        }
                        try {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = dateFormat.parse(created_at);
                            order_date.setText(getString(R.string.order_on) + android.text.format.DateFormat.format("dd, MMM yyyy", date));

                            Calendar c = Calendar.getInstance();
                            c.setTime(date);

                            if (shipping_country_code.equals("971")){
                                c.add(Calendar.DAY_OF_YEAR,1);
                                DateFormat first_date_formate = new SimpleDateFormat("dd, MMM yyyy");
                                String first_date= first_date_formate.format(c.getTime());

                                c.add(Calendar.DAY_OF_YEAR,1);
                                String sec_date= first_date_formate.format(c.getTime());
                                stimated_date_tv.setText(first_date+" to "+sec_date);
                            }else {
                                c.add(Calendar.DAY_OF_YEAR,2);
                                DateFormat first_date_formate = new SimpleDateFormat("dd, MMM yyyy");
                                String first_date= first_date_formate.format(c.getTime());

                                c.add(Calendar.DAY_OF_YEAR,3);
                                String sec_date= first_date_formate.format(c.getTime());
                                stimated_date_tv.setText(first_date+" to "+sec_date);
                            }


                        } catch (Exception e) {
                            Log.e("Exception in date",e.toString());
                        }

                        String dt = "2012-01-04";  // Start date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(sdf.parse(dt));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.DATE, 40);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                        SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy");
                        String output = sdf1.format(c.getTime());
                        cariar_awb_no_tv.setText(getString(R.string.awb_no)+""+orders.getString("awb_number"));
                        String awb_no_str=orders.getString("awb_number");
                        track_on_web_tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("https://smsaexpress.com/trackingdetails?tracknumbers="+awb_no_str));
                                startActivity(i);
                            }
                        });
                        getTrackTData(orders.getString("awb_number"),orders.getString("shipping_country_name"));
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(OrderTrackActivity.this);
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
                MethodClass.hideProgressDialog(OrderTrackActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(OrderTrackActivity.this);
                } else {
                    MethodClass.error_alert(OrderTrackActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(OrderTrackActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(OrderTrackActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(OrderTrackActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(OrderTrackActivity.this).addToRequestQueue(jsonObjectRequest);
    }


    public void getTrackTData(String awb_no,String country_id) {
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
        params.put("country_id", country_id);

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(OrderTrackActivity.this);
                Log.e("respUser", response.toString());
                try {

                    if (response.has("error")) {
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(OrderTrackActivity.this, DialogTypes.TYPE_ERROR)
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
                    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                    //JSONObject soapBody = response.getJSONObject("soapBody").getJSONObject("getTrackingResponse").getJSONObject("getTrackingResult").getJSONObject("diffgrdiffgram").getJSONObject("NewDataSet");
                    JSONObject soapBody1 = response.getJSONObject("xml").getJSONObject("soapBody");
                    if (soapBody1.has("getTrackingwithRefResponse")){
                        Object getTrackingResponse=new JSONTokener(soapBody1.getString("getTrackingwithRefResponse")).nextValue();
                        if (getTrackingResponse instanceof JSONObject) {
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

                        OrderTrackingAdapter trackingAdapter = new OrderTrackingAdapter(OrderTrackActivity.this, arrayList);
                        track_recy_view.setAdapter(trackingAdapter);
                    }

                    scrollView.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    MethodClass.error_alert(OrderTrackActivity.this);
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
                MethodClass.hideProgressDialog(OrderTrackActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(OrderTrackActivity.this);
                } else {
                    MethodClass.error_alert(OrderTrackActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(OrderTrackActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(OrderTrackActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(OrderTrackActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(OrderTrackActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}