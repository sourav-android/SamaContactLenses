package com.sama.samacontactlenses.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.sama.samacontactlenses.Adapter.ShoppingCartAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.CART_DETAILS_ID;
import static com.sama.samacontactlenses.Common.Constant.CART_MASTER_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY;
import static com.sama.samacontactlenses.Common.Constant.FREE_QTY;
import static com.sama.samacontactlenses.Common.Constant.LEFT_AXIS;
import static com.sama.samacontactlenses.Common.Constant.LEFT_B_C;
import static com.sama.samacontactlenses.Common.Constant.LEFT_CYL;
import static com.sama.samacontactlenses.Common.Constant.LEFT_EYE;
import static com.sama.samacontactlenses.Common.Constant.LEFT_SPH;
import static com.sama.samacontactlenses.Common.Constant.POWER;
import static com.sama.samacontactlenses.Common.Constant.POWER_TYPE_NO_POWER;
import static com.sama.samacontactlenses.Common.Constant.POWER_TYPE_WITH_POWER;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_QTY;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_AXIS;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_B_C;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_CYL;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_EYE;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_SPH;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;
import static com.sama.samacontactlenses.Helper.MethodClass.noFormat;

public class ShoppingCartActivity extends AppCompatActivity {
    private RecyclerView recy_view_trend_prod;
    private ArrayList<HashMap<String, String>> arrayList;
    private TextView item_tv, total_price_tv, discount_tv, paybale_amount_tv,offertext;
    private Button checkout_btn;
    private NestedScrollView scrollView;
    private LinearLayout discount_lay;
    private LinearLayout offerLay,discountLay;

    private EditText coupon_et;
    private LinearLayout coupon_discount_lay;
    private TextView coupon_discount_tv;
    private Button coupon_apply_btn,coupon_cancel_btn;
    private String cart_id="";
    private boolean couponStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_shopping_cart);
        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.shopping_cart));
        MethodClass.setBottomFun(this);
        recy_view_trend_prod = findViewById(R.id.recy_view_trend_prod);

        coupon_et = findViewById(R.id.coupon_et);
        coupon_apply_btn = findViewById(R.id.coupon_apply_btn);
        coupon_cancel_btn = findViewById(R.id.coupon_cancel_btn);
        coupon_discount_tv = findViewById(R.id.coupon_discount_tv);
        coupon_discount_lay = findViewById(R.id.coupon_discount_lay);

        item_tv = findViewById(R.id.item_tv);
        total_price_tv = findViewById(R.id.total_price_tv);
        discount_tv = findViewById(R.id.discount_tv);
        paybale_amount_tv = findViewById(R.id.paybale_amount_tv);
        checkout_btn = findViewById(R.id.checkout_btn);
        scrollView = findViewById(R.id.scrollView);
        discount_lay = findViewById(R.id.discount_lay);
        offerLay = findViewById(R.id.offerLay);
        discountLay = findViewById(R.id.discountLay);
        scrollView.setVisibility(View.GONE);
        getCart();
        checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getBoolean("is_logged_in", false)) {
                    Intent intent=new Intent(ShoppingCartActivity.this,CheckoutActivity.class);
                    startActivity(intent);
                } else {
                    LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(ShoppingCartActivity.this, DialogTypes.TYPE_WARNING)
                            .setTitle(getString(R.string.login_required))
                            .setDescription(getString(R.string.please_login_first))
                            .setPositiveText(getString(R.string.login))
                            .setNegativeText(getString(R.string.cancel))
                            .setPositiveListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog lottieAlertDialog) {
                                    Intent intent=new Intent(ShoppingCartActivity.this, LoginActivity.class);
                                    intent.putExtra("type","C");
                                    startActivity(intent);
                                    lottieAlertDialog.dismiss();
                                }
                            }).setNegativeListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog lottieAlertDialog) {
                                    lottieAlertDialog.dismiss();
                                }
                            })
                            .build();
                    alertDialog.show();
                }

            }
        });

        coupon_apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coupon_check(true,"A");
            }
        });
        coupon_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coupon_check(true,"R");
            }
        });
    }

    public void coupon_check(boolean messageShow,String type) {
        if (coupon_et.getText().toString().length() == 0) {
            coupon_et.setError(getString(R.string.please_enter_coupon_code));
            coupon_et.requestFocus();
            return;
        }

        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "apply-coupon";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("coupon_code",coupon_et.getText().toString().trim());
        params.put("type",type);
        params.put("id",cart_id);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("parames", jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(ShoppingCartActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(ShoppingCartActivity.this, response);
                    if (resultResponse != null) {

                        coupon_discount_tv.setText((noFormat(Double.parseDouble(resultResponse.getString("coupon_discount"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor","")))) +" "+PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED"));
                        paybale_amount_tv.setText((noFormat(Double.parseDouble(resultResponse.getString("total"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor","")))) +" "+PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED"));

                        if (resultResponse.getJSONObject("message").getString("code").equals("-15003")){
                            coupon_apply_btn.setVisibility(View.GONE);
                            coupon_et.setEnabled(false);
                            couponStatus=true;
                            coupon_discount_lay.setVisibility(View.VISIBLE);
                            coupon_cancel_btn.setVisibility(View.VISIBLE);
                        }else {
                            coupon_apply_btn.setVisibility(View.VISIBLE);
                            coupon_et.setEnabled(true);
                            coupon_et.setText("");
                            couponStatus=false;
                            coupon_cancel_btn.setVisibility(View.GONE);
                            coupon_discount_lay.setVisibility(View.GONE);
                            //getCart();
                        }
                        if (messageShow){
                            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(ShoppingCartActivity.this, DialogTypes.TYPE_SUCCESS)
                                    .setTitle(resultResponse.getJSONObject("message").getString("message"))
                                    .setDescription(resultResponse.getJSONObject("message").getString("meaning"))
                                    .setPositiveText("Ok")
                                    .setPositiveListener(new ClickListener() {
                                        @Override
                                        public void onClick(LottieAlertDialog lottieAlertDialog) {
                                            lottieAlertDialog.dismiss();
                                        }
                                    })
                                    .build();
                            alertDialog.show();
                        }

                    }
                } catch (Exception e) {
                    MethodClass.error_alert(ShoppingCartActivity.this);
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
                MethodClass.hideProgressDialog(ShoppingCartActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(ShoppingCartActivity.this);
                } else {
                    MethodClass.error_alert(ShoppingCartActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ShoppingCartActivity.this).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setMenu(this);
    }

    public void back(View view) {
        onBackPressed();
    }

    public void getCart() {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = "";
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_logged_in", false)) {
            server_url = getString(R.string.SERVER_URL) + "get-cart";
        } else {
            server_url = getString(R.string.SERVER_URL) + "get-cart/" + android_id;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("currency_code", PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED"));
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("server_url", server_url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(ShoppingCartActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(ShoppingCartActivity.this, response);
                    if (resultResponse != null) {
                        String cart_str = resultResponse.getString("cart");
                        if (cart_str.equals("null") || cart_str.equals(null)) {
                            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(ShoppingCartActivity.this, DialogTypes.TYPE_WARNING)
                                    .setTitle(getString(R.string.oops))
                                    .setDescription(getString(R.string.you_have_no_product_in_cart))
                                    .setPositiveText(getString(R.string.ok))
                                    .setPositiveListener(new ClickListener() {
                                        @Override
                                        public void onClick(LottieAlertDialog lottieAlertDialog) {
                                            lottieAlertDialog.dismiss();
                                        }
                                    })
                                    .build();
                            alertDialog.show();
                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    onBackPressed();
                                }
                            });
                            return;
                        }
                        JSONObject currency = resultResponse.getJSONObject("currency");

                        PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).edit().putString("currency_code",currency.getString("currency_code")).commit();
                        PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).edit().putString("conv_factor",currency.getJSONObject("currency_conversion").getString("conv_factor")).commit();

                        JSONObject cart = resultResponse.getJSONObject("cart");
                        cart_id=cart.getString("id");

                        String isOfferApplyOnAllProduct = resultResponse.getString("offerEligible");
                        if(isOfferApplyOnAllProduct.equals("A")){

                            // inflate (create) another copy of our custom layout
                            LayoutInflater inflater = getLayoutInflater();
                            View myLayout = inflater.inflate(R.layout.offer_list_item, offerLay, false);
                            offerLay.removeAllViews();
                            // make changes to our custom layout and its subviews
                            TextView offertext = (TextView) myLayout.findViewById(R.id.offertext);
                            // add our custom layout to the main layout
                            JSONArray category = resultResponse.getJSONArray("category");
                            String rem = "";
                            if(category.length()>0){
                                offerLay.removeAllViews();
                                Log.e("LENGTH", String.valueOf(category.length()));
                                for (int i = 0; i <category.length() ; i++) {
                                    rem = category.getJSONObject(i).getString("rem");

                                }
                            }
                            offertext.setText(getString(R.string.you_are_eligiblr_to_get_one_free_)+" "+rem+" "+getString(R.string.please_add_more));

/*
                            offertext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent I = new Intent(ShoppingCartActivity.this, SearchActivity.class);
                                    I.putExtra("category_id","");
                                    I.putExtra("parent_id","");
                                    I.putExtra("key","");
                                    I.putExtra("sort","");
                                    I.putExtra("min_price","");
                                    startActivity(I);
                                }
                            });
*/
                            offerLay.addView(myLayout);
                            offerLay.setVisibility(View.VISIBLE);
                        }else if(isOfferApplyOnAllProduct.equals("C")){
                            JSONArray category = resultResponse.getJSONArray("category");
                            if(category.length()>0){
                                offerLay.removeAllViews();
                                Log.e("LENGTH", String.valueOf(category.length()));
                                for (int i = 0; i <category.length() ; i++) {
                                    String discount_valid_from = category.getJSONObject(i).getString("offer_valid_from");
                                    String discount_valid_till = category.getJSONObject(i).getString("offer_valid_to");
                                    JSONObject category_details = category.getJSONObject(i).getJSONObject("category_details");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date dCurrentDate = null;
                                    Date toDate = null;
                                    Date fromDate = null;
                                    try {
                                        fromDate = dateFormat.parse(discount_valid_from);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        toDate = dateFormat.parse(discount_valid_till);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        dCurrentDate = dateFormat.parse(dateFormat.format(new Date()));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if ((dCurrentDate.equals(fromDate) || dCurrentDate.after(fromDate)) && (dCurrentDate.before(toDate) || dCurrentDate.equals(toDate))) {
                                        String varian = "";
                                        String discount_on = category.getJSONObject(i).getString("discount_on");
                                        if(!discount_on.equals("null")) {
                                            JSONArray discOn = new JSONArray(discount_on);
                                            for (int j = 0; j < discOn.length(); j++) {
                                                if (discOn.getJSONObject(j).getString("power").equals("NP")) {
                                                    varian = "No Power";
                                                }
                                                if (discOn.getJSONObject(j).getString("power").equals("WP")) {
                                                    varian = varian + ",With Power";
                                                }
                                                if (discOn.getJSONObject(j).getString("power").equals("WA")) {
                                                    varian = varian + ",With Asigmatism";
                                                }
                                            }
                                        }
                                    }
                                    // inflate (create) another copy of our custom layout
                                    LayoutInflater inflater = getLayoutInflater();
                                    View myLayout = inflater.inflate(R.layout.offer_list_item, offerLay, false);
                                    // make changes to our custom layout and its subviews
                                    TextView offertext = (TextView) myLayout.findViewById(R.id.offertext);
                                    // add our custom layout to the main layout

                                    //offertext.setText("You are eligible to get "+category.getJSONObject(i).getString("free_quantity")+" FREE product from "+category_details.getString("title")+".");
                                    //offertext.setText("If you buy "+category.getJSONObject(i).getString("buy_quantity")+" product then you will get "+category.getJSONObject(i).getString("free_quantity")+" FREE product from "+category_details.getString("title")+".");
                                    offertext.setText(getString(R.string.you_are_eligible_to_get)+category.getJSONObject(i).getString("free_quantity")+getString(R.string.item_if_you_buy)+category.getJSONObject(i).getString("buy_quantity")+getString(R.string.from)+category_details.getString("title")+".");
                                    Log.e("LENGTH", category_details.getString("title"));
                                    String c_id = category.getJSONObject(i).getString("category_id");
/*
                                    offertext.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent I = new Intent(ShoppingCartActivity.this, SearchActivity.class);
                                            I.putExtra("category_id",c_id);
                                            I.putExtra("parent_id","");
                                            I.putExtra("key","");
                                            I.putExtra("sort","");
                                            I.putExtra("min_price","");
                                            startActivity(I);
                                        }
                                    });
*/
                                    offerLay.addView(myLayout);
                                    offerLay.setVisibility(View.VISIBLE);
                                }
                            }

                        }else {
                            offerLay.setVisibility(View.GONE);
                        }

                        ////////////////////////////////////////////////////////////////////////////////////////////////////
                        String discountEligible = resultResponse.getString("discountEligible");
                        if(discountEligible.equals("A")){
                            JSONArray discountCategory = resultResponse.getJSONArray("discountCategory");
                            if(discountCategory.length()>0){
                                discountLay.removeAllViews();
                                Log.e("LENGTH", String.valueOf(discountCategory.length()));
                                for (int i = 0; i <discountCategory.length() ; i++) {
                                    String discount_valid_from = discountCategory.getJSONObject(i).getString("discount_valid_from");
                                    String discount_valid_till = discountCategory.getJSONObject(i).getString("discount_valid_till");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date dCurrentDate = null;
                                    Date toDate = null;
                                    Date fromDate = null;
                                    try {
                                        fromDate = dateFormat.parse(discount_valid_from);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        toDate = dateFormat.parse(discount_valid_till);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        dCurrentDate = dateFormat.parse(dateFormat.format(new Date()));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if ((dCurrentDate.equals(fromDate) || dCurrentDate.after(fromDate)) && (dCurrentDate.before(toDate) || dCurrentDate.equals(toDate))) {
                                        String varian = "";
                                        /*String discount_on = discountCategory.getJSONObject(i).getString("discount_on");
                                        if(!discount_on.equals("null")) {
                                            JSONArray discOn = new JSONArray(discount_on);
                                            for (int j = 0; j < discOn.length(); j++) {
                                                if (discOn.getJSONObject(j).getString("power").equals("NP")) {
                                                    varian = "No Power";
                                                }
                                                if (discOn.getJSONObject(j).getString("power").equals("WP")) {
                                                    varian = varian + ",With Power";
                                                }
                                                if (discOn.getJSONObject(j).getString("power").equals("WA")) {
                                                    varian = varian + ",With Asigmatism";
                                                }
                                            }
                                        }*/
                                    }
                                    // inflate (create) another copy of our custom layout
                                    LayoutInflater inflater = getLayoutInflater();
                                    View myLayout = inflater.inflate(R.layout.offer_list_item, discountLay, false);
                                    // make changes to our custom layout and its subviews
                                    TextView offertext = (TextView) myLayout.findViewById(R.id.offertext);
                                    // add our custom layout to the main layout

                                    //offertext.setText("You are eligible to get "+category.getJSONObject(i).getString("free_quantity")+" FREE product from "+category_details.getString("title")+".");

                                    if (discountCategory.getJSONObject(i).getString("discount_type").equals("F")){
                                        offertext.setText(getString(R.string.if_you_buy_minimum)+(noFormat(Double.parseDouble(discountCategory.getJSONObject(i).getString("discount_min_order_total"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor",""))))+" "+ PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED")+getString(R.string.product_then_you_will_get_flat_discount_of)+(String.format(Locale.ENGLISH, "%.3f",Double.parseDouble(discountCategory.getJSONObject(i).getString("discount"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor",""))))+" "+ PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED"));
                                    }else {
                                        offertext.setText(getString(R.string.if_you_buy_minimum)+(noFormat(Double.parseDouble(discountCategory.getJSONObject(i).getString("discount_min_order_total"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor",""))))+" "+ PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED")+getString(R.string.product_then_you_will_get)+discountCategory.getJSONObject(i).getString("discount")+getString(R.string.percent_discount));
                                    }

                                    offertext.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent I = new Intent(ShoppingCartActivity.this, SearchActivity.class);
                                            I.putExtra("category_id","");
                                            I.putExtra("parent_id","");
                                            I.putExtra("key","");
                                            I.putExtra("sort","");
                                            I.putExtra("min_price","");
                                            startActivity(I);
                                        }
                                    });
                                    discountLay.addView(myLayout);
                                    discountLay.setVisibility(View.VISIBLE);
                                }
                            }
                        }else if(discountEligible.equals("C")){
                            JSONArray discountCategory = resultResponse.getJSONArray("discountCategory");
                            if(discountCategory.length()>0){
                                discountLay.removeAllViews();
                                Log.e("LENGTH", String.valueOf(discountCategory.length()));
                                for (int i = 0; i <discountCategory.length() ; i++) {
                                    String discount_valid_from = discountCategory.getJSONObject(i).getString("discount_valid_from");
                                    String discount_valid_till = discountCategory.getJSONObject(i).getString("discount_valid_till");
                                    JSONObject category_details = discountCategory.getJSONObject(i).getJSONObject("category_details");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date dCurrentDate = null;
                                    Date toDate = null;
                                    Date fromDate = null;
                                    try {
                                        fromDate = dateFormat.parse(discount_valid_from);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        toDate = dateFormat.parse(discount_valid_till);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        dCurrentDate = dateFormat.parse(dateFormat.format(new Date()));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if ((dCurrentDate.equals(fromDate) || dCurrentDate.after(fromDate)) && (dCurrentDate.before(toDate) || dCurrentDate.equals(toDate))) {
                                        String varian = "";
                                        /*String discount_on = discountCategory.getJSONObject(i).getString("discount_on");
                                        if(!discount_on.equals("null")) {
                                            JSONArray discOn = new JSONArray(discount_on);
                                            for (int j = 0; j < discOn.length(); j++) {
                                                if (discOn.getJSONObject(j).getString("power").equals("NP")) {
                                                    varian = "No Power";
                                                }
                                                if (discOn.getJSONObject(j).getString("power").equals("WP")) {
                                                    varian = varian + ",With Power";
                                                }
                                                if (discOn.getJSONObject(j).getString("power").equals("WA")) {
                                                    varian = varian + ",With Asigmatism";
                                                }
                                            }
                                        }*/
                                    }
                                    // inflate (create) another copy of our custom layout
                                    LayoutInflater inflater = getLayoutInflater();
                                    View myLayout = inflater.inflate(R.layout.offer_list_item, discountLay, false);
                                    // make changes to our custom layout and its subviews
                                    TextView offertext = (TextView) myLayout.findViewById(R.id.offertext);
                                    // add our custom layout to the main layout

                                    //offertext.setText("You are eligible to get "+category.getJSONObject(i).getString("free_quantity")+" FREE product from "+category_details.getString("title")+".");

                                    if (discountCategory.getJSONObject(i).getString("discount_type").equals("F")){
                                        offertext.setText(getString(R.string.if_you_buy_minimum)+(noFormat(Double.parseDouble(discountCategory.getJSONObject(i).getString("discount_min_order_total"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor",""))))+" "+ PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED")+getString(R.string.product_then_you_will_get_flat_discount_of)+(String.format(Locale.ENGLISH, "%.3f",Double.parseDouble(discountCategory.getJSONObject(i).getString("discount"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor",""))))+" "+ PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED")+" from "+category_details.getString("title"));
                                    }else {
                                        offertext.setText(getString(R.string.if_you_buy_minimum)+(noFormat(Double.parseDouble(discountCategory.getJSONObject(i).getString("discount_min_order_total"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor",""))))+" "+ PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED")+getString(R.string.product_then_you_will_get)+discountCategory.getJSONObject(i).getString("discount")+getString(R.string.percent_discount_from)+category_details.getString("title"));
                                    }
                                    Log.e("LENGTH", category_details.getString("title"));
                                    String c_id = discountCategory.getJSONObject(i).getString("id");
                                    offertext.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent I = new Intent(ShoppingCartActivity.this, SearchActivity.class);
                                            I.putExtra("category_id",c_id);
                                            I.putExtra("parent_id","");
                                            I.putExtra("key","");
                                            I.putExtra("sort","");
                                            I.putExtra("min_price","");
                                            startActivity(I);
                                        }
                                    });
                                    discountLay.addView(myLayout);
                                    discountLay.setVisibility(View.VISIBLE);
                                }
                            }

                        }else {
                            discountLay.setVisibility(View.GONE);
                        }
                        ////////////////////////////////////////////////////////////////////////////////////////////////////


                        item_tv.setText(cart.getString("total_item") + getString(R.string.items));
                        total_price_tv.setText((noFormat(Double.parseDouble(cart.getString("subtotal"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor","")))) +" "+PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED"));
                        if (Float.parseFloat(cart.getString("offer_discount")) > 0) {
                            discount_tv.setText((noFormat(Double.parseDouble(cart.getString("offer_discount"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor","")))) +" "+PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED"));
                            discount_lay.setVisibility(View.VISIBLE);
                        } else {
                            discount_lay.setVisibility(View.GONE);
                        }
                        paybale_amount_tv.setText((noFormat(Double.parseDouble(cart.getString("total"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor","")))) +" "+PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED"));

                        JSONArray product_cart_arr = cart.getJSONArray("product_cart_merchant_details");
                        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                        for (int i = 0; i < product_cart_arr.length(); i++) {
                            JSONObject cart_obj = product_cart_arr.getJSONObject(i);
                            String cart_details_id = cart_obj.getString("id");
                            String cart_master_id = cart_obj.getString("cart_master_id");
                            String total = cart_obj.getString("total");
                            String quantity = cart_obj.getString("quantity");
                            String free_quantity = cart_obj.getString("free_quantity");
                            String power_type_no_power = cart_obj.getString("power_type_no_power");
                            String power_type_with_power = cart_obj.getString("power_type_with_power");
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
                            String is_free = MethodClass.checkNull(cart_obj.getString("is_free"));

                            JSONObject product_by_language = cart_obj.getJSONObject("product_by_language");
                            String product_id = product_by_language.getString("product_id");
                            String title = product_by_language.getString("title");
                            String description = product_by_language.getString("description");

                            JSONObject default_image = cart_obj.getJSONObject("default_image");
                            String image = default_image.getString("image");

                            JSONObject category = cart_obj.getJSONObject("get_product").getJSONObject("product_parent_category").getJSONObject("category");
                            String pro_cat = category.getJSONObject("category_by_language").getString("title");


                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(CART_DETAILS_ID, cart_details_id);
                            hashMap.put(CART_MASTER_ID, cart_master_id);
                            hashMap.put(PRODUCT_PRICE, total);
                            //hashMap.put(PRODUCT_FREE, is_free);
                            hashMap.put(PRODUCT_QTY, quantity);
                            hashMap.put(PRODUCT_ID, product_id);
                            hashMap.put(PRODUCT_TITLE, title);
                            hashMap.put(PRODUCT_DESC, description);
                            hashMap.put(PRODUCT_IMAGE, image);
                            hashMap.put(POWER_TYPE_NO_POWER, power_type_no_power);
                            hashMap.put(POWER_TYPE_WITH_POWER, power_type_with_power);
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
                            hashMap.put(CATEGORY, pro_cat);
                            hashMap.put(FREE_QTY, free_quantity);
                            arrayList.add(hashMap);
                        }
                        ShoppingCartAdapter adapter = new ShoppingCartAdapter(ShoppingCartActivity.this, arrayList);
                        recy_view_trend_prod.setAdapter(adapter);
                        scrollView.setVisibility(View.VISIBLE);

                        String coupon_code = cart.getString("coupon_code");
                        if(!coupon_code.equals("null") && !coupon_code.equals("")){
                            coupon_discount_tv.setText((noFormat(Double.parseDouble(cart.getString("coupon_discount"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor","")))) +" "+PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED"));
                            paybale_amount_tv.setText((noFormat(Double.parseDouble(cart.getString("total"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("conv_factor","")))) +" "+PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("currency_code","AED"));
                            coupon_apply_btn.setVisibility(View.GONE);
                            coupon_et.setText(cart.getString("coupon_code"));
                            coupon_et.setEnabled(false);
                            couponStatus=true;
                            coupon_discount_lay.setVisibility(View.VISIBLE);
                            coupon_cancel_btn.setVisibility(View.VISIBLE);
                        }else {
                            if (couponStatus){
                                coupon_check(false,"R");
                            }
                        }
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(ShoppingCartActivity.this);
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
                MethodClass.hideProgressDialog(ShoppingCartActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(ShoppingCartActivity.this);
                } else {
                    MethodClass.error_alert(ShoppingCartActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ShoppingCartActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ShoppingCartActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }


}