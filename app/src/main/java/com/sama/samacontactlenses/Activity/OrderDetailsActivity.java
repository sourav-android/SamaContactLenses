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
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Adapter.OrderDetailsAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.CART_DETAILS_ID;
import static com.sama.samacontactlenses.Common.Constant.CART_MASTER_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY;
import static com.sama.samacontactlenses.Common.Constant.CURR_CODE;
import static com.sama.samacontactlenses.Common.Constant.LEFT_AXIS;
import static com.sama.samacontactlenses.Common.Constant.LEFT_B_C;
import static com.sama.samacontactlenses.Common.Constant.LEFT_CYL;
import static com.sama.samacontactlenses.Common.Constant.LEFT_EYE;
import static com.sama.samacontactlenses.Common.Constant.LEFT_SPH;
import static com.sama.samacontactlenses.Common.Constant.ORIGINAL_PRICE;
import static com.sama.samacontactlenses.Common.Constant.POWER;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_QTY;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Common.Constant.RESCHEDULE_DATE;
import static com.sama.samacontactlenses.Common.Constant.RESCHEDULE_TIME;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_AXIS;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_B_C;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_CYL;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_EYE;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_SPH;
import static com.sama.samacontactlenses.Common.Constant.STATUS;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class OrderDetailsActivity extends AppCompatActivity {
    private RecyclerView recy_view_trend_prod;
    private ArrayList<HashMap<String, String>> arrayList;
    private NestedScrollView scrollView;
    private TextView order_no,order_date,total_price,paybale_mode,item_count,status,discount_tv;
    private TextView name,address,phone;
    private TextView name_1,address_1,phone_1;
    private TextView wallet_tv,paybal_amount_tv;
    private Button track;
    private TextView shipping_price_tv;
    private TextView reschedule_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_order_details);
        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.order_details));
        MethodClass.setBottomFun(this);
        recy_view_trend_prod = findViewById(R.id.recy_view_trend_prod);
        order_no=findViewById(R.id.order_no);
        order_date=findViewById(R.id.order_date);
        total_price=findViewById(R.id.total_price);
        discount_tv=findViewById(R.id.discount_tv);
        paybale_mode=findViewById(R.id.paybale_mode);
        item_count=findViewById(R.id.item_count);
        status=findViewById(R.id.status);
        scrollView=findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        name_1 = findViewById(R.id.name_1);
        track = findViewById(R.id.track);
        address_1 = findViewById(R.id.address_1);
        phone_1 = findViewById(R.id.phone_1);
        wallet_tv = findViewById(R.id.wallet_tv);
        paybal_amount_tv = findViewById(R.id.paybal_amount_tv);
        shipping_price_tv = findViewById(R.id.shipping_price_tv);
        reschedule_date = findViewById(R.id.reschedule_date);

        getdata();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setMenu(this);
    }

    public void back(View view) {
        if(isTaskRoot()){
            startActivity(new Intent(OrderDetailsActivity.this,HomeActivity.class));
            // using finish() is optional, use it if you do not want to keep currentActivity in stack
            finish();
        }else{
            super.onBackPressed();
        }
    }

    public void getdata() {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "order-history-details/"+getIntent().getStringExtra("order_id");

        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(OrderDetailsActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(OrderDetailsActivity.this, response);
                    if (resultResponse != null) {
                        JSONObject orders=resultResponse.getJSONObject("orders");
                        String id=orders.getString("id");
                        String conv_factor = orders.getString("conv_factor" );
                        JSONObject currency = orders.getJSONObject("currency");
                        String currency_code = currency.getString("currency_code");
                        String order_no_str=orders.getString("order_no");
                        String order_total=orders.getString("order_total");
                        String payable_amount=orders.getString("payable_amount");
                        String total_discount=orders.getString("total_discount");
                        String created_at=orders.getString("created_at");
                        String total_items=orders.getString("total_items");
                        String payment_method=orders.getString("payment_method");
                        String awb_number=orders.getString("awb_number");
                        String status_str=orders.getString("status");

                        String rescdule_date = orders.getString("rescdule_date");
                        String reschedule_time = orders.getString("reschedule_time");
                        String internal_status = orders.getString("internal_status");

                        if (!rescdule_date.equals("null") && !reschedule_time.equals("null")  && !status_str.equals("D") && !internal_status.equals("NONE"))
                        reschedule_date.setText(getResources().getString(R.string.reschedule_on) + " " + rescdule_date + " " + reschedule_time);
                        else reschedule_date.setVisibility(View.GONE);

                        if (Double.parseDouble(orders.getString("shipping_price")) != 0){
                            shipping_price_tv.setText(getString(R.string.shippin_price_coln)+(orders.getString("shipping_price") +" "+currency_code));
                            shipping_price_tv.setVisibility(View.VISIBLE);
                        }else {
                            shipping_price_tv.setVisibility(View.GONE);
                        }

                        switch (status_str) {
                            case "N":
                                status.setText(getString(R.string.new_str));
                                status.setTextColor(getResources().getColor(R.color.blue_clr));
                                track.setVisibility(View.VISIBLE);
                                break;
                            case "C":
                                status.setText(getString(R.string.cancelled));
                                status.setTextColor(getResources().getColor(R.color.red_clr));
                                track.setVisibility(View.VISIBLE);
                                break;
                            case "R":
                                status.setText(getString(R.string.ready_to_ship));
                                status.setTextColor(getResources().getColor(R.color.green_txt_clr));
                                track.setVisibility(View.VISIBLE);
                                break;
                            case "S":
                                status.setText(getString(R.string.shipped));
                                status.setTextColor(getResources().getColor(R.color.green_txt_clr));
                                track.setVisibility(View.VISIBLE);
                                break;
                            case "D":
                                status.setText(getString(R.string.delivered));
                                status.setTextColor(getResources().getColor(R.color.green_txt_clr));
                                track.setVisibility(View.GONE);
                                break;
                        }

                        if(awb_number.equals("null")){
                            track.setVisibility(View.GONE);
                        }
                        order_no.setText(getString(R.string.order_no)+order_no_str);
                        if (Double.parseDouble(total_discount)==0){
                            discount_tv.setVisibility(View.GONE);
                        }else {
                            discount_tv.setText(getString(R.string.discount_collon)+total_discount +" "+currency_code);
                            discount_tv.setVisibility(View.VISIBLE);
                        }
                        paybal_amount_tv.setText(getString(R.string.total_paybale_amount_collon) + payable_amount + " " + currency_code);

                        /*if (Double.parseDouble(payable_amount) == 0) {
                            paybal_amount_tv.setVisibility(View.GONE);
                        } else {
                            paybal_amount_tv.setVisibility(View.VISIBLE);
                        }*/

                        String adjustment=orders.getString("adjustment");
                        if (Double.parseDouble(adjustment)!=0){
                            wallet_tv.setText(getString(R.string.used_wallet_ball)+" : "+adjustment+" "+currency_code);
                            wallet_tv.setVisibility(View.VISIBLE);
                        }else {
                            wallet_tv.setVisibility(View.GONE);
                        }

                        total_price.setText(order_total +" "+currency_code);
                        item_count.setText("("+getString(R.string.item_colon)+total_items+")");

                        if (payment_method.equals("O")){
                            paybale_mode.setText(getString(R.string.payment_mode)+" "+getString(R.string.only_online));
                        }else if (payment_method.equals("C")){
                            paybale_mode.setText(getString(R.string.payment_mode)+" "+getString(R.string.cash_on_delivery));
                        }else if (payment_method.equals("W")){
                            paybale_mode.setText(getString(R.string.payment_mode)+" "+getString(R.string.wallet));
                        }
                        try{
                            DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date=dateFormat.parse(created_at);
                            order_date.setText(getString(R.string.order_on)+android.text.format.DateFormat.format("dd, MMM yyyy",date));
                        }catch (Exception e){

                        }

                        name.setText(orders.getString("shipping_full_name"));
                        phone.setText(orders.getString("shipping_phone"));
                        String full_address="";
                        full_address=orders.getString("shipping_address_line_1");
                        full_address=full_address+", "+MethodClass.checkNull(orders.getString("shipping_address_line_2"));
                        full_address=full_address+", "+MethodClass.checkNull(orders.getString("shipping_city"));
                        full_address=full_address+", "+MethodClass.checkNull(orders.getString("shipping_state"));
                        full_address=full_address+", "+MethodClass.checkNull(orders.getString("shipping_country"));
                        full_address=full_address+", "+MethodClass.checkNull(orders.getString("shipping_pincode"));
                        address.setText(full_address);

                        name_1.setText(orders.getString("billing_full_name"));
                        phone_1.setText(orders.getString("billing_phone"));
                        String full_address_1="";
                        full_address_1=orders.getString("billing_address_line_1");
                        full_address_1=full_address_1+", "+MethodClass.checkNull(orders.getString("billing_address_line_2"));
                        full_address_1=full_address_1+", "+MethodClass.checkNull(orders.getString("billing_city"));
                        full_address_1=full_address_1+", "+MethodClass.checkNull(orders.getString("billing_state"));
                        full_address_1=full_address_1+", "+MethodClass.checkNull(orders.getString("billing_country"));
                        full_address_1=full_address_1+", "+MethodClass.checkNull(orders.getString("billing_pincode"));
                        address_1.setText(full_address_1);

                        JSONArray order_master_details=orders.getJSONArray("order_master_details");
                        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                        for (int i = 0; i < order_master_details.length(); i++) {
                            JSONObject cart_obj = order_master_details.getJSONObject(i);
                            String cart_details_id = cart_obj.getString("id");
                            String order_master_id = cart_obj.getString("order_master_id");
                            String original_price = cart_obj.getString("original_price");
                            String total = cart_obj.getString("total");
                            String quantity = cart_obj.getString("quantity");
                            //String power_type_no_power = cart_obj.getString("power_type_no_power");
                            //String power_type_with_power = cart_obj.getString("power_type_with_power");
                            String power = cart_obj.getString("power");
                            String left_eye = MethodClass.checkNull(cart_obj.getString("left_eye"));
                            String right_eye = MethodClass.checkNull(cart_obj.getString("right_eye"));
                            String left_sph = MethodClass.checkNull(cart_obj.getString("left_sph"));
                            String left_cyl = MethodClass.checkNull(cart_obj.getString("left_cyl"));
                            String left_axis = MethodClass.checkNull(cart_obj.getString("left_axis"));
                            String left_base_curve = MethodClass.checkNull(cart_obj.getString("left_base_curve"));
                            String right_sph = MethodClass.checkNull(cart_obj.getString("right_sph"));
                            String right_cyl = MethodClass.checkNull(cart_obj.getString("right_cyl"));
                            String right_axis = MethodClass.checkNull(cart_obj.getString("right_axis"));
                            String right_base_curve = MethodClass.checkNull(cart_obj.getString("right_base_curve"));


                            JSONObject product_by_language = cart_obj.getJSONObject("product_by_language");
                            String product_id = product_by_language.getString("product_id");
                            String title = product_by_language.getString("title");
                            String description = product_by_language.getString("description");

                            JSONObject default_image = cart_obj.getJSONObject("default_image");
                            String image = default_image.getString("image");

                            /*String category = "";
                            if(!cart_obj.getString("product_sub_category").equals("null")){
                                JSONObject product_sub_category = cart_obj.getJSONObject("product_sub_category");

                                category = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                            }else {
                                JSONObject product_sub_category = cart_obj.getJSONObject("product_parent_category");

                                category = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                            }*/
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(CART_DETAILS_ID, cart_details_id);
                            hashMap.put(CART_MASTER_ID, order_master_id);
                            hashMap.put(ORIGINAL_PRICE, original_price);
                            hashMap.put(PRODUCT_PRICE, total);
                            hashMap.put(PRODUCT_QTY, quantity);
                            hashMap.put(PRODUCT_ID, product_id);
                            hashMap.put(PRODUCT_TITLE, title);
                            hashMap.put(PRODUCT_DESC, description);
                            hashMap.put(PRODUCT_IMAGE, image);
                            hashMap.put(POWER, power);
                            hashMap.put(LEFT_EYE, left_eye);
                            hashMap.put(RIGHT_EYE, right_eye);
                            hashMap.put(LEFT_SPH, left_sph);
                            hashMap.put(LEFT_CYL, left_cyl);
                            hashMap.put(LEFT_AXIS, left_axis);
                            hashMap.put(LEFT_B_C, left_base_curve);
                            hashMap.put(RIGHT_SPH, right_sph);
                            hashMap.put(RIGHT_CYL, right_cyl);
                            hashMap.put(RIGHT_AXIS, right_axis);
                            hashMap.put(RIGHT_B_C, right_base_curve);
                            hashMap.put(CATEGORY, "");
                            hashMap.put(CURR_CODE, currency_code);
                            arrayList.add(hashMap);
                        }
                        OrderDetailsAdapter adapter = new OrderDetailsAdapter(OrderDetailsActivity.this, arrayList);
                        recy_view_trend_prod.setAdapter(adapter);

                        scrollView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(OrderDetailsActivity.this);
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
                MethodClass.hideProgressDialog(OrderDetailsActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(OrderDetailsActivity.this);
                } else {
                    MethodClass.error_alert(OrderDetailsActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(OrderDetailsActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(OrderDetailsActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(OrderDetailsActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(OrderDetailsActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void order_track(View view){
        Intent intent=new Intent(this,OrderTrackActivity.class);
        intent.putExtra("order_id",getIntent().getStringExtra("order_id"));
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }

}