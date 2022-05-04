package com.sama.samacontactlenses.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Adapter.CategorySearchAdapter;
import com.sama.samacontactlenses.Adapter.SearchAdapter;
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

import static com.sama.samacontactlenses.Common.Constant.CATEGORY_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_TITLE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DISC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DISC_TYPE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_FAV;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class SearchActivity extends AppCompatActivity {
    public static Activity search_activity;
    private RecyclerView recy_view_trend_prod;
    public static ArrayList<HashMap<String, String>> tagArrayListId_1;
    private EditText search;
    private String key = "";
    public String SELECTED_CATEGORY = "";
    public String SELECTED_PARENT = "";
    public String CAT_ID = "";
    public String SORT = "";
    private RecyclerView cat_recy;
    private LinearLayout searchLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_search);
        MethodClass.setBottomFun(SearchActivity.this);
        search_activity = this;
        recy_view_trend_prod = findViewById(R.id.recy_view_trend_prod);
        cat_recy = findViewById(R.id.cat_recy);
        search = findViewById(R.id.search);
        searchLay = findViewById(R.id.searchLay);


        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("category_id")){
                CAT_ID = getIntent().getStringExtra("category_id");
                SELECTED_CATEGORY = getIntent().getStringExtra("category_id");
            }
            if (getIntent().getExtras().containsKey("parent_id")){
                SELECTED_PARENT=getIntent().getStringExtra("parent_id");
            }
            if (getIntent().getExtras().containsKey("key")){
                key=getIntent().getStringExtra("key");
                search.setText(key);
            }
            if (getIntent().getExtras().containsKey("sort")){
                SORT=getIntent().getStringExtra("sort");
            }
            getImages();
        }
        getCategory();
        if (!CAT_ID.equals("")) {
            search.setVisibility(View.GONE);
            searchLay.setVisibility(View.GONE);
        } else {
            search.requestFocus();
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    key = s.toString();
                    searchResult();
                }
            });
            search.setVisibility(View.VISIBLE);
            searchLay.setVisibility(View.VISIBLE);
        }
    }

    public void getImages() {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "search";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cat_id", SELECTED_CATEGORY);
        params.put("parent_id", SELECTED_PARENT);
        params.put("keyword",key);
        params.put("order_by",SORT);
        Log.e("CATGOEY", new JSONObject(params).toString());
        /*if (!Objects.equals(getIntent().getExtras(), null)) {
            if (!getIntent().getStringExtra("key").equals("")) {
                params.put("keyword", getIntent().getStringExtra("key"));
                search.setText(getIntent().getStringExtra("key"));
            }
            if (!getIntent().getStringExtra("sort").equals("")) {
                params.put("order_by", getIntent().getStringExtra("sort"));
            }
        }else {
            params.put("keyword", search.getText().toString().trim());
        }*/
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("jsonObject", jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(SearchActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(SearchActivity.this, response);
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
                            String title = product_by_language.getString("title");
                            String description = product_by_language.getString("description");
                            String category = "";
                            if (!products.getJSONObject(i).getString("product_sub_category").equals("null")) {
                                JSONObject product_sub_category = products.getJSONObject(i).getJSONObject("product_sub_category");

                                category = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                            } else {
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
                                if (!discount_type.equals("null")) {
                                    map.put(PRODUCT_DISC, discount);
                                    map.put(PRODUCT_DISC_TYPE, discount_type);
                                } else {
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
                                }*/ else {
                                map.put(PRODUCT_DISC_TYPE, "N");
                            }
                            map.put(PRODUCT_ID, id);
                            map.put(PRODUCT_TITLE, title);
                            map.put(PRODUCT_PRICE, price_without_power);
                            map.put(PRODUCT_DESC, description);
                            map.put(PRODUCT_IMAGE, image);
                            if (!wishlist.equals("null") && !wishlist.equals(null)) {
                                map.put(PRODUCT_FAV, "Y");
                            } else {
                                map.put(PRODUCT_FAV, "N");
                            }
                            map.put(CATEGORY_TITLE, category);
                            tagArrayListId_1.add(map);
                        }
                        SearchAdapter adapter = new SearchAdapter(SearchActivity.this, tagArrayListId_1);
                        recy_view_trend_prod.setAdapter(adapter);


                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(SearchActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(SearchActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(SearchActivity.this);
                } else {
                    MethodClass.error_alert(SearchActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(SearchActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void searchResult() {

        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "search";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("cat_id", SELECTED_CATEGORY);
        params.put("parent_id", SELECTED_PARENT);
        params.put("keyword",key);
        params.put("order_by",SORT);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("jsonObject", jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(SearchActivity.this, response);
                    if (resultResponse != null) {
                        JSONArray products = resultResponse.getJSONArray("products");
                        tagArrayListId_1 = new ArrayList<HashMap<String, String>>();
                        Log.e("LENGTH", String.valueOf(products.length()));
                        for (int i = 0; i < products.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            String id = products.getJSONObject(i).getString("id");
                            String price_without_power = products.getJSONObject(i).getString("price_without_power");
                            String image = "";
                            if (!products.getJSONObject(i).getString("default_image").equals("null")) {
                                JSONObject default_image = products.getJSONObject(i).getJSONObject("default_image");
                                image = default_image.getString("image");
                            }

                            String wishlist = products.getJSONObject(i).getString("wishlist");

                            JSONObject product_by_language = products.getJSONObject(i).getJSONObject("product_by_language");
                            String title = product_by_language.getString("title");
                            Log.e("LENGTH", title);
                            String description = product_by_language.getString("description");
                            String category = "";
                            if (!products.getJSONObject(i).getString("product_sub_category").equals("null")) {
                                JSONObject product_sub_category = products.getJSONObject(i).getJSONObject("product_sub_category");

                                category = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                            } else {
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
                                if (!discount_type.equals("null")) {
                                    map.put(PRODUCT_DISC, discount);
                                    map.put(PRODUCT_DISC_TYPE, discount_type);
                                } else {
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
                                }*/ else {
                                map.put(PRODUCT_DISC_TYPE, "N");
                            }
                            map.put(PRODUCT_ID, id);
                            map.put(PRODUCT_TITLE, title);
                            map.put(PRODUCT_PRICE, price_without_power);
                            map.put(PRODUCT_DESC, description);
                            map.put(PRODUCT_IMAGE, image);
                            if (!wishlist.equals("null") && !wishlist.equals(null)) {
                                map.put(PRODUCT_FAV, "Y");
                            } else {
                                map.put(PRODUCT_FAV, "N");
                            }
                            map.put(CATEGORY_TITLE, category);
                            tagArrayListId_1.add(map);
                        }
                        SearchAdapter adapter = new SearchAdapter(SearchActivity.this, tagArrayListId_1);
                        recy_view_trend_prod.setAdapter(adapter);


                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(SearchActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());

                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(SearchActivity.this);
                } else {
                    MethodClass.error_alert(SearchActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(SearchActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void getCategory() {

        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }

        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "list-of-categories";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();

        if (!CAT_ID.equals("")) {
            params.put("cat_id", CAT_ID);
        }

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("jsonObject", jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(SearchActivity.this, response);
                    if (resultResponse != null) {
                        JSONArray products = resultResponse.getJSONArray("products");
                        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
                        if (CAT_ID.equals("")) {
                            HashMap<String, String> maps = new HashMap<String, String>();
                            maps.put(CATEGORY_ID, "");
                            maps.put(PRODUCT_ID, "");
                            maps.put(CATEGORY_TITLE, getString(R.string.all));
                            arrayList.add(maps);
                        }

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject category_by_language = products.getJSONObject(i).getJSONObject("category_by_language");
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(CATEGORY_ID, category_by_language.getString("category_id"));
                            map.put(PRODUCT_ID, products.getJSONObject(i).getString("parent_id"));
                            map.put(CATEGORY_TITLE, category_by_language.getString("title"));
                            arrayList.add(map);
                        }
                        CategorySearchAdapter adapter = new CategorySearchAdapter(SearchActivity.this, arrayList);
                        cat_recy.setAdapter(adapter);


                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(SearchActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());

                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(SearchActivity.this);
                } else {
                    MethodClass.error_alert(SearchActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(SearchActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(SearchActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setCartCount(this);
        MethodClass.setMenu(this);
    }

    public void filter(View view) {
        Intent intent = new Intent(this, FilterActivity.class);
        if (search.getText().toString().trim().length() !=0){
            intent.putExtra("key", search.getText().toString().trim());
        }
        if (!Objects.equals(getIntent().getExtras(), null)) {
            if (getIntent().getExtras().containsKey("sort")){
                if (!getIntent().getStringExtra("sort").equals("")) {
                    intent.putExtra("sort", getIntent().getStringExtra("sort"));
                }
            }

        }
        startActivity(intent);
    }

    public void back(View view) {
        onBackPressed();
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