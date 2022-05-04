package com.sama.samacontactlenses.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.sama.samacontactlenses.Adapter.CategoryAdapter;
import com.sama.samacontactlenses.Adapter.CelebAdapter;
import com.sama.samacontactlenses.Adapter.HomeSliderAdapter;
import com.sama.samacontactlenses.Adapter.NewAdpater;
import com.sama.samacontactlenses.Adapter.OfferAdapter;
import com.sama.samacontactlenses.Adapter.TrendingAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.BANNER_BUTTON;
import static com.sama.samacontactlenses.Common.Constant.BANNER_HEADING;
import static com.sama.samacontactlenses.Common.Constant.BANNER_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.BANNER_URL;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_PARENT_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_TITLE;
import static com.sama.samacontactlenses.Common.Constant.CELEB_ID;
import static com.sama.samacontactlenses.Common.Constant.CELEB_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.CELEB_TITLE;
import static com.sama.samacontactlenses.Common.Constant.IS_CLICKEBLE;
import static com.sama.samacontactlenses.Common.Constant.IS_CLICKEBLE2;
import static com.sama.samacontactlenses.Common.Constant.IS_CLICKEBLE3;
import static com.sama.samacontactlenses.Common.Constant.NOTIFICATION_COUNT;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DISC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DISC_TYPE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_FAV;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_OFF;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Common.Constant.SHARED_PREF;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class HomeActivity extends AppCompatActivity {
    private SliderView imageSlider;
    public static ArrayList<HashMap<String, String>> tagArrayListId_1;
    public static ArrayList<HashMap<String, String>> tagArrayListId_2;
    public static ArrayList<HashMap<String, String>> tagArrayListId_3;
    public static ArrayList<HashMap<String, String>> tagArrayListId_4;
    public static ArrayList<HashMap<String, String>> tagArrayListId_5;
    public static ArrayList<HashMap<String, String>> tagArrayListId_6;

    private RecyclerView recy_view_trend_prod;
    private RecyclerView recy_view_new_prod;
    private RecyclerView recy_view_off_prod;
    private RecyclerView recy_view_cat_prod;
    private RecyclerView recy_view_celeb;

    private TextView trenHeader, newHeader, offerHeader, seeAltrend, seeAllnew, seeAlloff, seeAllCat;
    private LinearLayout trendLine, newLine1, newLine2, offerLine;
    private LinearLayout trenHeaderLay, offerHeaderLay, categoryheaderLay, newHeaderLay;
    private NestedScrollView scrollView;
    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_home);
        MethodClass.setBottomFun(HomeActivity.this);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        imageSlider = findViewById(R.id.imageSlider);
        trenHeaderLay = findViewById(R.id.trenHeaderLay);
        offerHeaderLay = findViewById(R.id.offerHeaderLay);
        categoryheaderLay = findViewById(R.id.categoryheaderLay);
        newHeaderLay = findViewById(R.id.newHeaderLay);
        seeAltrend = findViewById(R.id.seeAltrend);
        seeAllnew = findViewById(R.id.seeAllnew);
        seeAlloff = findViewById(R.id.seeAlloff);
        seeAllCat = findViewById(R.id.seeAllCat);
        recy_view_celeb = findViewById(R.id.recy_view_celeb);
        recy_view_cat_prod = findViewById(R.id.recy_view_cat_prod);
        recy_view_off_prod = findViewById(R.id.recy_view_off_prod);
        recy_view_new_prod = findViewById(R.id.recy_view_new_prod);
        recy_view_trend_prod = findViewById(R.id.recy_view_trend_prod);
        trenHeader = findViewById(R.id.trenHeader);
        newHeader = findViewById(R.id.newHeader);
        offerHeader = findViewById(R.id.offerHeader);
        trendLine = findViewById(R.id.trendLine);
        newLine1 = findViewById(R.id.newLine1);
        newLine2 = findViewById(R.id.newLine2);
        offerLine = findViewById(R.id.offerLine);
        scrollView = findViewById(R.id.scrollView);

        list();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setMenu(this);
        MethodClass.setBottomFun2(HomeActivity.this, bottomNavigation);
        MethodClass.setCartCount(this);
    }

    @SuppressLint("WrongConstant")
    public void home_menu(View view) {
        DrawerLayout drawer_layout = findViewById(R.id.drawer_layout);
        drawer_layout.openDrawer(Gravity.START);
    }

    public void search(View view) {
        Intent I = new Intent(this, SearchActivity.class);
        startActivity(I);
    }

    public void shopping_cart(View view) {
        Intent I = new Intent(this, ShoppingCartActivity.class);
        startActivity(I);
    }


    public void list() {
        Log.e("che2khh", "data" + PreferenceManager.getDefaultSharedPreferences(this).getString("conv_factor", ""));

        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        SharedPreferences pref = getSharedPreferences(SHARED_PREF, 0);
        String firebase_reg_no = pref.getString("regId", null);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "home/" + android_id;

        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", android_id);
        params.put("device_type", "A");
        params.put("firebase_reg_no",firebase_reg_no);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(HomeActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(HomeActivity.this, response);
                    if (resultResponse != null) {
                        if (PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).getBoolean("is_logged_in", false)) {
                            JSONObject currency = resultResponse.getJSONObject("currency");
                            JSONObject currency_conversion = currency.getJSONObject("currency_conversion");
                            PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).edit().putString("conv_factor", currency_conversion.getString("conv_factor")).commit();
                            PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).edit().putString("currency", currency.getString("currency_name")).commit();
                            PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).edit().putString("currency_code", currency.getString("currency_code")).commit();

                            JSONObject country = resultResponse.getJSONObject("country");
                            PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).edit().putString("country_id", country.getString("id")).commit();
                            PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).edit().putString("country_code", country.getString("sortname")).commit();
                        }
                        NOTIFICATION_COUNT = resultResponse.getString("notification_count");
                        MethodClass.setMenu(HomeActivity.this);
                        String total_cart_item = resultResponse.getString("total_cart_item");
                        PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).edit().putString("cart_count", total_cart_item).commit();
                        MethodClass.setCartCount(HomeActivity.this);

                        JSONObject product = resultResponse.getJSONObject("product");


                        JSONObject home_settings = product.getJSONObject("home_settings");
                        trenHeader.setText(home_settings.getString("trending_products_name"));
                        newHeader.setText(home_settings.getString("new_products_name"));
                        offerHeader.setText(home_settings.getString("offer_products_name"));


                        JSONArray all_banners = product.getJSONArray("all_banners");


                        tagArrayListId_1 = new ArrayList<HashMap<String, String>>();
                        for (int i = 0; i < all_banners.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            String banner_heading = all_banners.getJSONObject(i).getString("banner_heading");
                            String banner_buttton_cap = all_banners.getJSONObject(i).getString("banner_buttton_cap");
                            String banner_image = all_banners.getJSONObject(i).getString("banner_image");
                            String banner_buttton_url = all_banners.getJSONObject(i).getString("banner_buttton_url");

                            map.put(BANNER_HEADING, banner_heading);
                            map.put(BANNER_BUTTON, banner_buttton_cap);
                            map.put(BANNER_IMAGE, banner_image);
                            map.put(BANNER_URL, banner_buttton_url);
                            tagArrayListId_1.add(map);
                        }

                        HomeSliderAdapter imageAdapter = new HomeSliderAdapter(HomeActivity.this, tagArrayListId_1);
                        imageSlider.setSliderAdapter(imageAdapter);
                        imageSlider.startAutoCycle();

                        JSONArray trending = product.getJSONArray("trending");
                        JSONArray newPRod = product.getJSONArray("new");
                        JSONArray best_dela = product.getJSONArray("best_deal_product");
                        JSONArray category = resultResponse.getJSONArray("category");
                        JSONArray celebrity = product.getJSONArray("celebrity");

                        if (trending.length() > 0) {
                            tagArrayListId_2 = new ArrayList<HashMap<String, String>>();
                            for (int i = 0; i < trending.length(); i++) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                String id = trending.getJSONObject(i).getString("id");

                                String price_without_power = trending.getJSONObject(i).getString("price_without_power");
                                String image = "";
                                if (!MethodClass.checkNull(trending.getJSONObject(i).getString("default_image")).equals("")) {
                                    JSONObject default_image = trending.getJSONObject(i).getJSONObject("default_image");
                                    image = default_image.getString("image");
                                }
                                JSONObject product_by_language = trending.getJSONObject(i).getJSONObject("product_by_language");

                                String title = product_by_language.getString("title");
                                String description = "";
                                if (!trending.getJSONObject(i).getString("product_sub_category").equals("null")) {
                                    JSONObject product_sub_category = trending.getJSONObject(i).getJSONObject("product_sub_category");

                                    description = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                                } else {
                                    JSONObject product_sub_category = trending.getJSONObject(i).getJSONObject("product_parent_category");

                                    description = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                                }


                                map.put(PRODUCT_ID, id);
                                map.put(PRODUCT_TITLE, title);
                                map.put(PRODUCT_PRICE, price_without_power);
                                map.put(PRODUCT_DESC, description);
                                //JSONArray buy_product_by_language = trending.getJSONObject(i).getJSONArray("buy_product_by_language");
                                String offer_type = trending.getJSONObject(i).getString("offer_type");
                                String discount = trending.getJSONObject(i).getString("discount");
                                String discount_type = trending.getJSONObject(i).getString("discount_type");
                                String discount_valid_from = trending.getJSONObject(i).getString("discount_valid_from");
                                String discount_valid_till = trending.getJSONObject(i).getString("discount_valid_till");

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


                                map.put(PRODUCT_IMAGE, image);
                                tagArrayListId_2.add(map);
                            }

                            TrendingAdapter adapter = new TrendingAdapter(HomeActivity.this, tagArrayListId_2);
                            recy_view_trend_prod.setAdapter(adapter);
                            recy_view_trend_prod.setVisibility(View.VISIBLE);
                            trenHeader.setVisibility(View.VISIBLE);
                            trenHeaderLay.setVisibility(View.VISIBLE);
                            trendLine.setVisibility(View.VISIBLE);
                            seeAltrend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent I = new Intent(HomeActivity.this, SeeAllActivity.class);
                                    I.putExtra("from", "trend");
                                    startActivity(I);
                                }
                            });
                        } else {
                            recy_view_trend_prod.setVisibility(View.GONE);
                            trenHeaderLay.setVisibility(View.GONE);
                            trenHeader.setVisibility(View.GONE);
                            trendLine.setVisibility(View.GONE);
                        }

                        if (newPRod.length() > 0) {
                            tagArrayListId_3 = new ArrayList<HashMap<String, String>>();
                            for (int i = 0; i < newPRod.length(); i++) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                String id = newPRod.getJSONObject(i).getString("id");
                                String price_without_power = newPRod.getJSONObject(i).getString("price_without_power");
                                String image = "";
                                if (!MethodClass.checkNull(newPRod.getJSONObject(i).getString("default_image")).equals("")) {
                                    JSONObject default_image = newPRod.getJSONObject(i).getJSONObject("default_image");
                                    image = default_image.getString("image");
                                }
                                String wishlist = newPRod.getJSONObject(i).getString("wishlist");
                                JSONObject product_by_language = newPRod.getJSONObject(i).getJSONObject("product_by_language");
                                String title = product_by_language.getString("title");
                                String description = "";
                                if (!newPRod.getJSONObject(i).getString("product_sub_category").equals("null")) {
                                    JSONObject product_sub_category = newPRod.getJSONObject(i).getJSONObject("product_sub_category");

                                    description = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                                } else {
                                    JSONObject product_sub_category = newPRod.getJSONObject(i).getJSONObject("product_parent_category");

                                    description = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                                }
                                map.put(PRODUCT_ID, id);
                                map.put(PRODUCT_TITLE, title);
                                map.put(PRODUCT_PRICE, price_without_power);
                                map.put(PRODUCT_DESC, description);
                                map.put(PRODUCT_IMAGE, image);
                                String offer_type = newPRod.getJSONObject(i).getString("offer_type");
                                String discount = newPRod.getJSONObject(i).getString("discount");
                                String discount_type = newPRod.getJSONObject(i).getString("discount_type");
                                String discount_valid_from = newPRod.getJSONObject(i).getString("discount_valid_from");
                                String discount_valid_till = newPRod.getJSONObject(i).getString("discount_valid_till");

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
                                if (!wishlist.equals("null") && !wishlist.equals(null)) {
                                    map.put(PRODUCT_FAV, "Y");
                                } else {
                                    map.put(PRODUCT_FAV, "N");
                                }
                                tagArrayListId_3.add(map);
                            }

                            NewAdpater adapters = new NewAdpater(HomeActivity.this, tagArrayListId_3);
                            recy_view_new_prod.setAdapter(adapters);
                            recy_view_new_prod.setVisibility(View.VISIBLE);
                            newHeader.setVisibility(View.VISIBLE);
                            newLine1.setVisibility(View.VISIBLE);
                            newLine2.setVisibility(View.VISIBLE);
                            seeAllnew.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent I = new Intent(HomeActivity.this, SeeAllActivity.class);
                                    I.putExtra("from", "new");
                                    startActivity(I);
                                }
                            });
                        } else {
                            recy_view_new_prod.setVisibility(View.GONE);
                            newHeader.setVisibility(View.GONE);
                            newLine1.setVisibility(View.GONE);
                            newLine2.setVisibility(View.GONE);
                        }

                        if (best_dela.length() > 0) {
                            tagArrayListId_4 = new ArrayList<HashMap<String, String>>();
                            for (int i = 0; i < best_dela.length(); i++) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                String id = best_dela.getJSONObject(i).getString("id");
                                String price_without_power = best_dela.getJSONObject(i).getString("price_without_power");
                                String image = "";
                                if (!MethodClass.checkNull(best_dela.getJSONObject(i).getString("default_image")).equals("")) {
                                    JSONObject default_image = best_dela.getJSONObject(i).getJSONObject("default_image");
                                    image = default_image.getString("image");
                                }
                                JSONObject product_by_language = best_dela.getJSONObject(i).getJSONObject("product_by_language");
                                String title = product_by_language.getString("title");
                                String description = "";
                                if (!best_dela.getJSONObject(i).getString("product_sub_category").equals("null")) {
                                    JSONObject product_sub_category = best_dela.getJSONObject(i).getJSONObject("product_sub_category");

                                    description = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                                } else {
                                    JSONObject product_sub_category = best_dela.getJSONObject(i).getJSONObject("product_parent_category");

                                    description = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                                }
                                map.put(PRODUCT_ID, id);
                                map.put(PRODUCT_TITLE, title);
                                map.put(PRODUCT_PRICE, price_without_power);
                                map.put(PRODUCT_DESC, description);
                                map.put(PRODUCT_IMAGE, image);
                                String offer_type = best_dela.getJSONObject(i).getString("offer_type");
                                String discount = best_dela.getJSONObject(i).getString("discount");
                                String discount_type = best_dela.getJSONObject(i).getString("discount_type");
                                String discount_valid_from = best_dela.getJSONObject(i).getString("discount_valid_from");
                                String discount_valid_till = best_dela.getJSONObject(i).getString("discount_valid_till");

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
                                    Log.e("DATE", dCurrentDate.toString() + "---------------" + toDate.toString());
                                    Log.e("OFFER", offer_type);
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
                                tagArrayListId_4.add(map);
                            }

                            OfferAdapter adapterss = new OfferAdapter(HomeActivity.this, tagArrayListId_4);
                            recy_view_off_prod.setAdapter(adapterss);
                            recy_view_off_prod.setVisibility(View.VISIBLE);
                            offerHeader.setVisibility(View.VISIBLE);
                            offerHeaderLay.setVisibility(View.VISIBLE);
                            offerLine.setVisibility(View.VISIBLE);
                            seeAlloff.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent I = new Intent(HomeActivity.this, SeeAllActivity.class);
                                    I.putExtra("from", "off");
                                    startActivity(I);
                                }
                            });
                        } else {
                            recy_view_off_prod.setVisibility(View.GONE);
                            offerHeader.setVisibility(View.GONE);
                            offerLine.setVisibility(View.GONE);
                            offerHeaderLay.setVisibility(View.GONE);

                        }

                        if (celebrity.length() > 0) {
                            Log.e("length", String.valueOf(celebrity.length()));
                            tagArrayListId_6 = new ArrayList<HashMap<String, String>>();
                            for (int i = 0; i < celebrity.length(); i++) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                if (i < celebrity.length()) {
                                    Log.e("VALUE", String.valueOf(i));
                                    String id = celebrity.getJSONObject(i).getString("id");
                                    String picture = celebrity.getJSONObject(i).getString("image");
                                    JSONObject celebrity_by_language = celebrity.getJSONObject(i).getJSONObject("celebrity_details_by_language");
                                    String title = celebrity_by_language.getString("celebrity_name");
                                    String celebrity_to_product_count = celebrity.getJSONObject(i).getString("celebrity_to_product_count");
                                    map.put(PRODUCT_ID, id);
                                    map.put(PRODUCT_TITLE, title);
                                    map.put(PRODUCT_IMAGE, picture);
                                    map.put(IS_CLICKEBLE, celebrity_to_product_count);
                                }

                                if (i + 1 < celebrity.length()) {
                                    Log.e("VALUE", String.valueOf(i + 1));
                                    String id = celebrity.getJSONObject(i + 1).getString("id");
                                    String picture = celebrity.getJSONObject(i + 1).getString("image");
                                    String celebrity_to_product_count = celebrity.getJSONObject(i + 1).getString("celebrity_to_product_count");
                                    JSONObject celebrity_by_language = celebrity.getJSONObject(i + 1).getJSONObject("celebrity_details_by_language");
                                    String title = celebrity_by_language.getString("celebrity_name");
                                    map.put(CATEGORY_ID, id);
                                    map.put(CATEGORY_TITLE, title);
                                    map.put(CATEGORY_IMAGE, picture);
                                    map.put(IS_CLICKEBLE2, celebrity_to_product_count);
                                }
                                if (i + 2 < celebrity.length()) {
                                    Log.e("VALUE", String.valueOf(i + 2));
                                    String id = celebrity.getJSONObject(i + 2).getString("id");
                                    String picture = celebrity.getJSONObject(i + 2).getString("image");
                                    String celebrity_to_product_count = celebrity.getJSONObject(i + 2).getString("celebrity_to_product_count");
                                    JSONObject celebrity_by_language = celebrity.getJSONObject(i + 2).getJSONObject("celebrity_details_by_language");
                                    String title = celebrity_by_language.getString("celebrity_name");
                                    map.put(CELEB_ID, id);
                                    map.put(CELEB_TITLE, title);
                                    map.put(CELEB_IMAGE, picture);
                                    map.put(IS_CLICKEBLE3, celebrity_to_product_count);
                                }
                                tagArrayListId_6.add(map);
                                i = i + 2;
                            }
                            CelebAdapter adapterssss = new CelebAdapter(HomeActivity.this, tagArrayListId_6);
                            recy_view_celeb.setAdapter(adapterssss);
                        }


                        if (category.length() > 0) {
                            tagArrayListId_5 = new ArrayList<HashMap<String, String>>();
                            for (int i = 0; i < category.length(); i++) {
                                HashMap<String, String> map = new HashMap<String, String>();

                                if (i < category.length()) {
                                    String id = category.getJSONObject(i).getString("id");
                                    String parent_id = category.getJSONObject(i).getString("parent_id");
                                    String picture = category.getJSONObject(i).getString("picture");
                                    JSONObject category_by_language = category.getJSONObject(i).getJSONObject("category_by_language");
                                    String title = category_by_language.getString("title");
                                    String discount_type = category.getJSONObject(i).getString("discount_type");
                                    String discount_valid_from = category.getJSONObject(i).getString("discount_valid_from");
                                    String discount_valid_till = category.getJSONObject(i).getString("discount_valid_till");

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
                                            map.put(PRODUCT_OFF, discount_type);
                                        } else {
                                            map.put(PRODUCT_OFF, "N");
                                        }

                                    } else {
                                        map.put(PRODUCT_OFF, "N");
                                    }
                                    map.put(PRODUCT_ID, id);
                                    map.put(CATEGORY_PARENT_ID, parent_id);
                                    map.put(PRODUCT_TITLE, title);
                                    map.put(PRODUCT_IMAGE, picture);
                                }

//                                if (i + 1 < category.length()) {
//                                    String id = category.getJSONObject(i + 1).getString("id");
//                                    String parent_id = category.getJSONObject(i + 1).getString("parent_id");
//                                    String picture = category.getJSONObject(i + 1).getString("picture");
//                                    JSONObject category_by_language = category.getJSONObject(i + 1).getJSONObject("category_by_language");
//                                    String title = category_by_language.getString("title");
//                                    map.put(CATEGORY_ID, id);
//                                    map.put(CATEGORY_PARENT_ID2, parent_id);
//                                    map.put(CATEGORY_TITLE, title);
//                                    map.put(CATEGORY_IMAGE, picture);
//                                }


                                tagArrayListId_5.add(map);
                            }
                            CategoryAdapter adaptersss = new CategoryAdapter(HomeActivity.this, tagArrayListId_5);
                            recy_view_cat_prod.setAdapter(adaptersss);
                            recy_view_cat_prod.setVisibility(View.VISIBLE);

                            seeAllCat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent I = new Intent(HomeActivity.this, AllCategoryActivity.class);
                                    startActivity(I);
                                }
                            });


                        } else {
                            recy_view_cat_prod.setVisibility(View.GONE);
                        }

                        if (home_settings.getString("show_new_product").equals("N")) {
                            recy_view_new_prod.setVisibility(View.GONE);
                            newHeader.setVisibility(View.GONE);
                            newHeaderLay.setVisibility(View.GONE);
                            newLine1.setVisibility(View.GONE);
                            newLine2.setVisibility(View.GONE);
                        }
                        if (home_settings.getString("show_trending_product").equals("N")) {
                            recy_view_trend_prod.setVisibility(View.GONE);
                            trenHeader.setVisibility(View.GONE);
                            trenHeaderLay.setVisibility(View.GONE);
                            trendLine.setVisibility(View.GONE);
                        }
                        if (home_settings.getString("show_offer_product").equals("N")) {
                            recy_view_off_prod.setVisibility(View.GONE);
                            offerHeader.setVisibility(View.GONE);
                            offerHeaderLay.setVisibility(View.GONE);
                            offerLine.setVisibility(View.GONE);

                        }
                        startPro();
                        scrollView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(HomeActivity.this);
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
                MethodClass.hideProgressDialog(HomeActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(HomeActivity.this);
                } else {
                    MethodClass.error_alert(HomeActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(HomeActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(HomeActivity.this).addToRequestQueue(jsonObjectRequest);
    }


    public void startPro(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            String suff = deepLink.getLastPathSegment();
                            Log.e("deepLink", String.valueOf(suff));
                            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("product_id", String.valueOf(suff));
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }




    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }

}
