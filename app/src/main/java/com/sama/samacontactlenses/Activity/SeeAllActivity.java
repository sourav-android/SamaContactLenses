package com.sama.samacontactlenses.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Adapter.SearchAdapter;
import com.sama.samacontactlenses.Adapter.SeeAllAdapter;
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
import java.util.Map;
import java.util.Objects;

import static com.sama.samacontactlenses.Common.Constant.CATEGORY_TITLE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DISC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DISC_TYPE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_FAV;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_OFF;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class SeeAllActivity extends AppCompatActivity {
    private RecyclerView recy_view_trend_prod;
    public static ArrayList<HashMap<String,String>> tagArrayListId_1;
    String min_price="",max_price="";
    private EditText search;
    private String key = "";
    private TextView headerName;
    private MeowBottomNavigation bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_see_all);
        MethodClass.setBottomFun(SeeAllActivity.this);
        recy_view_trend_prod = findViewById(R.id.recy_view_trend_prod);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        headerName = findViewById(R.id.headerName);
        getImages();

    }
    public void getImages() {

        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String server_url = "";
        if(getIntent().getStringExtra("from").equals("trend")){
            headerName.setText(getString(R.string.trendin));
            server_url = getString(R.string.SERVER_URL) + "list-of-trending-products";
        }
        if(getIntent().getStringExtra("from").equals("new")){
            headerName.setText(getString(R.string.newpro));
            server_url = getString(R.string.SERVER_URL) + "list-of-new-products";
        }
        if(getIntent().getStringExtra("from").equals("off")){
            headerName.setText(getString(R.string.off));
            server_url = getString(R.string.SERVER_URL) + "list-of-offer-products";
        }
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("jsonObject",jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(SeeAllActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(SeeAllActivity.this, response);
                    if (resultResponse != null) {
                        JSONArray products = resultResponse.getJSONArray("products");
                        tagArrayListId_1 = new ArrayList<HashMap<String, String>>();
                        for (int i = 0; i < products.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            String id = products.getJSONObject(i).getString("id");
                            String price_without_power = products.getJSONObject(i).getString("price_without_power");
                            JSONObject default_image = products.getJSONObject(i).getJSONObject("default_image");
                            String wishlist = products.getJSONObject(i).getString("wishlist");
                            String image = default_image.getString("image");
                            JSONObject product_by_language = products.getJSONObject(i).getJSONObject("product_by_language");
                            String  title = product_by_language.getString("title");
                            String  description = product_by_language.getString("description");
                            String category = "";
                            if(!products.getJSONObject(i).getString("product_sub_category").equals("null")){
                                JSONObject product_sub_category = products.getJSONObject(i).getJSONObject("product_sub_category");

                                category = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                            }else {
                                JSONObject product_sub_category = products.getJSONObject(i).getJSONObject("product_parent_category");

                                category = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                            }
                            String offer_type = products.getJSONObject(i).getString("offer_type");
                            String discount = products.getJSONObject(i).getString("discount");
                            String discount_type = products.getJSONObject(i).getString("discount_type");
                            String discount_valid_from = products.getJSONObject(i).getString("discount_valid_from");
                            String discount_valid_till = products.getJSONObject(i).getString("discount_valid_till");

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
                                if(!discount_type.equals("null")){
                                    map.put(PRODUCT_DISC, discount);
                                    map.put(PRODUCT_DISC_TYPE, discount_type);
                                }else {
                                    map.put(PRODUCT_DISC_TYPE, "N");
                                }

                            }/*else if(offer_type.equals("BUYXGETY") && !best_dela.getJSONObject(i).getString("buy_product_by_language").equals("null")){
                                    if(!offer_type.equals("null")){
                                        map.put(PRODUCT_OFF, "BUYXGETY");
                                        map.put(PRODUCT_DISC, discount);
                                        map.put(PRODUCT_DISC_TYPE, discount_type);
                                    }else {
                                        map.put(PRODUCT_OFF, "N");
                                    }
                                }*/else {
                                map.put(PRODUCT_DISC_TYPE, "N");
                            }
                            map.put(PRODUCT_ID, id);
                            map.put(PRODUCT_TITLE,title);
                            map.put(PRODUCT_PRICE,price_without_power);
                            map.put(PRODUCT_DESC,description);
                            map.put(PRODUCT_IMAGE,image);
                            if(!wishlist.equals("null") && !wishlist.equals(null)){
                                map.put(PRODUCT_FAV,"Y");
                            }else {
                                map.put(PRODUCT_FAV,"N");
                            }
                            map.put(CATEGORY_TITLE,category);
                            tagArrayListId_1.add(map);
                        }
                        SeeAllAdapter adapter = new SeeAllAdapter(SeeAllActivity.this,tagArrayListId_1);
                        recy_view_trend_prod.setAdapter(adapter);


                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(SeeAllActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(SeeAllActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(SeeAllActivity.this);
                } else {
                    MethodClass.error_alert(SeeAllActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(SeeAllActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(SeeAllActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(SeeAllActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(SeeAllActivity.this).addToRequestQueue(jsonObjectRequest);
    }
    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setCartCount(this);
        MethodClass.setMenu(this);
        MethodClass.setBottomFun2(this,bottomNavigation);
    }
    public void back(View view) {
        onBackPressed();
    }
    public void search(View view) {
        Intent I = new Intent(this, SearchActivity.class);
        startActivity(I);
    }
    public void shopping_cart(View view) {
        Intent I = new Intent(this, ShoppingCartActivity.class);
        startActivity(I);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}