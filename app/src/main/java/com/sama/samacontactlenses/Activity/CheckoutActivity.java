package com.sama.samacontactlenses.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Adapter.BillingAddressAdapter;
import com.sama.samacontactlenses.Adapter.ChecketOutAdapter;
import com.sama.samacontactlenses.Adapter.CountryAdapter;
import com.sama.samacontactlenses.Adapter.CountryAdapter3;
import com.sama.samacontactlenses.Adapter.CountryCodeAdapter;
import com.sama.samacontactlenses.Adapter.ShippingAddressAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.sama.samacontactlenses.Common.Constant.ADDRESS_ID;
import static com.sama.samacontactlenses.Common.Constant.ADDRESS_LINE_1;
import static com.sama.samacontactlenses.Common.Constant.ADDRESS_LINE_2;
import static com.sama.samacontactlenses.Common.Constant.CART_DETAILS_ID;
import static com.sama.samacontactlenses.Common.Constant.CART_MASTER_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY;
import static com.sama.samacontactlenses.Common.Constant.CITY;
import static com.sama.samacontactlenses.Common.Constant.COUNTRY_NAME;
import static com.sama.samacontactlenses.Common.Constant.FULL_NAME;
import static com.sama.samacontactlenses.Common.Constant.HEADING;
import static com.sama.samacontactlenses.Common.Constant.IS_DEFAULT;
import static com.sama.samacontactlenses.Common.Constant.LEFT_AXIS;
import static com.sama.samacontactlenses.Common.Constant.LEFT_B_C;
import static com.sama.samacontactlenses.Common.Constant.LEFT_CYL;
import static com.sama.samacontactlenses.Common.Constant.LEFT_EYE;
import static com.sama.samacontactlenses.Common.Constant.LEFT_SPH;
import static com.sama.samacontactlenses.Common.Constant.PHONE;
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
import static com.sama.samacontactlenses.Common.Constant.SHIPPING_PRICE;
import static com.sama.samacontactlenses.Common.Constant.SHIPPNG_ALLOW;
import static com.sama.samacontactlenses.Common.Constant.SORT_NAME;
import static com.sama.samacontactlenses.Common.Constant.STATE;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;
import static com.sama.samacontactlenses.Helper.MethodClass.noFormat;

public class CheckoutActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
    private RecyclerView recy_view_trend_prod;
    private NestedScrollView scrollView;
    private TextView item_tv, total_price_tv, discount_tv;
    public TextView paybale_amount_tv;
    private Button checkout_btn;
    private LinearLayout discount_lay;

    ArrayList<MethodClass.CountryModelCode> countryCodeArrayList;
    ArrayList<MethodClass.CountryModelName3> countryArrayList;

    private TextView name, email, phone, address_1, address_2, city, state, heading;
    private TextView name_1, email_1, phone_1, address_b_1, address_2_1, city_1, state_1, heading_1;
    private LinearLayout shipping_lay, biling_lay;
    private Spinner country_code_spin, country_code_spin_1;
    private Spinner country_spin, country_spin_1;
    private CheckBox is_same_chk;
    public Button online_btn, cod_btn;

    private RecyclerView address_recy, billing_address_recy;

    public String shippingAddressID = "", billingAddressId = "";
    LinearLayout shipping_manual_lay, billing_manual_lay;
    LinearLayout shipping_list_lay, billing_list_lay;
    Button shi_type_manual_btn, bill_type_manual_btn;
    Button shi_list_btn, bill_list_btn;
    public String country_sortName = "";

    ArrayList<HashMap<String, String>> adress_arrayList;

    private CheckBox wallet_bal_che;

    public TextView cod_charge_tv;

    public String is_astigmatism="",is_cod = "";


    private GoogleApiClient googleApiClient;
    private double lat = 0, long_ = 0;
    private boolean one_time = false;
    private LocationManager locationManager;
    private Location mylocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 3000;

    public LinearLayout shipping_price_lay;
    public TextView shipping_price_tv;
    public ArrayList<String> shipping_price_array;
    public String is_shipping_price="",total_amount="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.content_checkout);
        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.checkout));
        recy_view_trend_prod = findViewById(R.id.recy_view_trend_prod);
        item_tv = findViewById(R.id.item_tv);
        total_price_tv = findViewById(R.id.total_price_tv);
        discount_tv = findViewById(R.id.discount_tv);
        paybale_amount_tv = findViewById(R.id.paybale_amount_tv);
        checkout_btn = findViewById(R.id.checkout_btn);
        scrollView = findViewById(R.id.scrollView);
        discount_lay = findViewById(R.id.discount_lay);
        address_recy = findViewById(R.id.address_recy);
        billing_address_recy = findViewById(R.id.billing_address_recy);
        shipping_manual_lay = findViewById(R.id.shipping_manual_lay);
        billing_manual_lay = findViewById(R.id.billing_manual_lay);
        shipping_list_lay = findViewById(R.id.shipping_list_lay);
        billing_list_lay = findViewById(R.id.billing_list_lay);
        shi_type_manual_btn = findViewById(R.id.shi_type_manual_btn);
        bill_type_manual_btn = findViewById(R.id.bill_type_manual_btn);
        shi_list_btn = findViewById(R.id.shi_list_btn);
        bill_list_btn = findViewById(R.id.bill_list_btn);
        cod_charge_tv = findViewById(R.id.cod_charge_tv);

        country_code_spin = findViewById(R.id.country_code_spin);
        country_code_spin.setEnabled(false);
        country_spin = findViewById(R.id.shi_spin_country);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address_1 = findViewById(R.id.address_1);
        address_2 = findViewById(R.id.address_2);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        heading = findViewById(R.id.heading);

        country_code_spin_1 = findViewById(R.id.country_code_spin_1);
        country_code_spin_1.setEnabled(false);
        country_spin_1 = findViewById(R.id.shi_spin_country_1);
        name_1 = findViewById(R.id.name_1);
        email_1 = findViewById(R.id.email_1);
        phone_1 = findViewById(R.id.phone_1);
        address_b_1 = findViewById(R.id.address_b_1);
        address_2_1 = findViewById(R.id.address_2_1);
        city_1 = findViewById(R.id.city_1);
        state_1 = findViewById(R.id.state_1);
        heading_1 = findViewById(R.id.heading_1);

        name.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("name", ""));
        email.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("email", ""));
        phone.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("phone_number", ""));
        name_1.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("name", ""));
        email_1.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("email", ""));
        phone_1.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("phone_number", ""));


        shipping_lay = findViewById(R.id.shipping_lay);
        biling_lay = findViewById(R.id.biling_lay);
        is_same_chk = findViewById(R.id.is_same_chk);
        online_btn = findViewById(R.id.online_btn);
        cod_btn = findViewById(R.id.cod_btn);
        wallet_bal_che = findViewById(R.id.wallet_bal_che);

        shipping_price_lay = findViewById(R.id.shipping_price_lay);
        shipping_price_tv = findViewById(R.id.shipping_price_tv);

        scrollView.setVisibility(View.GONE);
        getCart();


        shi_type_manual_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shipping_list_lay.setVisibility(View.GONE);
                shipping_manual_lay.setVisibility(View.VISIBLE);
                checkCodButton();
            }
        });
        shi_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shipping_list_lay.setVisibility(View.VISIBLE);
                shipping_manual_lay.setVisibility(View.GONE);
                checkAstigmatism();
            }
        });

        bill_type_manual_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billing_list_lay.setVisibility(View.GONE);
                billing_manual_lay.setVisibility(View.VISIBLE);
            }
        });
        bill_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billing_list_lay.setVisibility(View.VISIBLE);
                billing_manual_lay.setVisibility(View.GONE);
            }
        });
        is_same_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    biling_lay.setVisibility(View.GONE);
                } else {
                    biling_lay.setVisibility(View.VISIBLE);
                    if (adress_arrayList.size() > 0) {
                        billing_list_lay.setVisibility(View.VISIBLE);
                        billing_manual_lay.setVisibility(View.GONE);
                    } else {
                        billing_list_lay.setVisibility(View.GONE);
                        billing_manual_lay.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout_post();
            }
        });
        online_btn.setTag("O");
        online_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online_btn.setBackground(getDrawable(R.drawable.pink_grad_button));
                online_btn.setText(getString(R.string.only_online));
                cod_btn.setBackground(getDrawable(R.drawable.grey_round));
                cod_btn.setText(getString(R.string.only_cod));
                online_btn.setTag("O");
            }
        });
        cod_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countryArrayList.get(country_spin.getSelectedItemPosition()).countrySortName.equals("AE") || country_sortName.equals("AE")) {
                    online_btn.setBackground(getDrawable(R.drawable.grey_round));
                    online_btn.setText(getString(R.string.only_online));
                    cod_btn.setBackground(getDrawable(R.drawable.pink_grad_button));
                    cod_btn.setText(getString(R.string.only_cod));
                    online_btn.setTag("C");
                } else {
                    Toast.makeText(CheckoutActivity.this, getString(R.string.cod_just_available_in_uae), Toast.LENGTH_SHORT).show();
                }

            }
        });
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
            server_url = getString(R.string.SERVER_URL) + "get-checkout";
        } else {
            server_url = getString(R.string.SERVER_URL) + "get-checkout/" + android_id;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("currency_code", PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("currency_code", "AED"));
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("server_url", server_url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                getList();
                setUpGClient();
                MethodClass.hideProgressDialog(CheckoutActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(CheckoutActivity.this, response);
                    if (resultResponse != null) {
                        JSONObject currency = resultResponse.getJSONObject("currency");

                        PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).edit().putString("currency_code", currency.getString("currency_code")).commit();
                        PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).edit().putString("conv_factor", currency.getJSONObject("currency_conversion").getString("conv_factor")).commit();

                        String cart_str = resultResponse.getString("cart");
                        if (cart_str.equals("null") || cart_str.equals(null)) {
                            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(CheckoutActivity.this, DialogTypes.TYPE_WARNING)
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

                        is_astigmatism=resultResponse.getString("is_astigmatism");
                        is_cod=resultResponse.getString("is_cod");
                        is_shipping_price=resultResponse.getString("is_shipping_price");


                        if (Double.parseDouble(resultResponse.getString("wallet_balance")) != 0) {
                            String wallet_balance = resultResponse.getString("wallet_balance");
                            String currency_code = resultResponse.getJSONObject("currency").getString("currency_code");
                            wallet_bal_che.setText(getString(R.string.i_want_to_make_payment_from_my_wallet_balance) + wallet_balance + " " + currency_code + ")");
                            wallet_bal_che.setVisibility(View.VISIBLE);
                        } else {
                            wallet_bal_che.setVisibility(View.GONE);
                        }

                        double cod_cha=Double.parseDouble(resultResponse.getString("cod_charge"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("conv_factor",""));
                        cod_charge_tv.setText(getString(R.string.cod_charge)+" "+noFormat(cod_cha)+" "+PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("currency_code",""));

                        JSONObject cart = resultResponse.getJSONObject("cart");
                        item_tv.setText(cart.getString("total_item") + getString(R.string.items));
                        total_price_tv.setText((noFormat( Double.parseDouble(cart.getString("subtotal")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("currency_code", "AED")));
                        if (Float.parseFloat(cart.getString("total_discount")) > 0) {
                            discount_tv.setText((noFormat(Double.parseDouble(cart.getString("total_discount")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("currency_code", "AED")));
                            discount_lay.setVisibility(View.VISIBLE);
                        } else {
                            discount_lay.setVisibility(View.GONE);
                        }
                        total_amount=cart.getString("total");
                        paybale_amount_tv.setText((noFormat(Double.parseDouble(cart.getString("total")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("currency_code", "AED")));

                        JSONArray product_cart_arr = cart.getJSONArray("product_cart_merchant_details");
                        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                        for (int i = 0; i < product_cart_arr.length(); i++) {
                            JSONObject cart_obj = product_cart_arr.getJSONObject(i);
                            String cart_details_id = cart_obj.getString("id");
                            String cart_master_id = cart_obj.getString("cart_master_id");
                            String total = cart_obj.getString("total");
                            String quantity = cart_obj.getString("quantity");
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
                            hashMap.put(CATEGORY, pro_cat);
                            hashMap.put(LEFT_SPH, left_sph);
                            hashMap.put(LEFT_CYL, left_cyl);
                            hashMap.put(LEFT_AXIS, left_axis);
                            hashMap.put(LEFT_B_C, left_base_curve);
                            hashMap.put(RIGHT_SPH, right_sph);
                            hashMap.put(RIGHT_CYL, right_cyl);
                            hashMap.put(RIGHT_AXIS, right_axis);
                            hashMap.put(RIGHT_B_C, right_base_curve);
                            hashMap.put(CATEGORY, pro_cat);
                            arrayList.add(hashMap);
                        }
                        ChecketOutAdapter adapter = new ChecketOutAdapter(CheckoutActivity.this, arrayList);
                        recy_view_trend_prod.setAdapter(adapter);

                        JSONArray countries = resultResponse.getJSONArray("countries");
                        countryCodeArrayList = new ArrayList<>();
                        countryArrayList = new ArrayList<>();
                        shipping_price_array = new ArrayList<>();
                        for (int i = 0; i < countries.length(); i++) {
                            String id = countries.getJSONObject(i).getString("id");
                            String name = countries.getJSONObject(i).getString("name");
                            String sortname = countries.getJSONObject(i).getString("sortname");
                            String country_code = countries.getJSONObject(i).getString("country_code");
                            String shipping_price = countries.getJSONObject(i).getString("shipping_price");
                            String shipping_allowed = countries.getJSONObject(i).getString("shipping_allowed");
                            MethodClass.CountryModelCode countryModelCode = new MethodClass.CountryModelCode(name, sortname, country_code, id);
                            countryCodeArrayList.add(countryModelCode);
                            MethodClass.CountryModelName3 countryModelName = new MethodClass.CountryModelName3(shipping_allowed,name, sortname, country_code, id);
                            countryArrayList.add(countryModelName);
                            shipping_price_array.add(shipping_price);
                        }
                        CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(CheckoutActivity.this, countryCodeArrayList);
                        country_code_spin.setAdapter(countryCodeAdapter);
                        country_code_spin_1.setAdapter(countryCodeAdapter);

                        String country_phone_code=PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_phone_code","");
                        for (int i = 0; i < countryCodeArrayList.size(); i++) {
                            MethodClass.CountryModelCode stringWithTag = countryCodeArrayList.get(i);
                            if (Objects.equals(stringWithTag.countryCode, country_phone_code)) {
                                country_code_spin.setSelection(i);
                                country_code_spin_1.setSelection(i);
                                break;
                            }
                        }

                        CountryAdapter3 countryAdapter = new CountryAdapter3(CheckoutActivity.this, countryArrayList);
                        country_spin.setAdapter(countryAdapter);
                        country_spin_1.setAdapter(countryAdapter);


                        country_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (is_shipping_price.equals("Y")){
                                    Double shipping_charge=Double.parseDouble(shipping_price_array.get(position))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("conv_factor",""));
                                    try {
                                        Double total_price = Double.parseDouble(cart.getString("total"))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("conv_factor",""));
                                        Double payble_amount=shipping_charge+total_price;
                                        paybale_amount_tv.setText(noFormat(payble_amount)+" "+PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("currency_code",""));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    shipping_price_tv.setText(noFormat(shipping_charge)+" "+PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("currency_code",""));
                                    shipping_price_lay.setVisibility(View.VISIBLE);
                                }else {
                                    shipping_price_lay.setVisibility(View.GONE);
                                }

                                if (countryArrayList.get(country_spin.getSelectedItemPosition()).shippingAllowed.equals("N")) {
                                    Toast.makeText(CheckoutActivity.this, getString(R.string.country_not_allow), Toast.LENGTH_LONG).show();
                                }

                                if (is_cod.equals("N")) {
                                    online_btn.performClick();
                                    cod_btn.setVisibility(View.GONE);
                                    cod_charge_tv.setVisibility(View.GONE);
                                }else {
                                    if (is_astigmatism.equals("Y")) {
                                        online_btn.performClick();
                                        cod_btn.setVisibility(View.GONE);
                                        cod_charge_tv.setVisibility(View.GONE);
                                    }else {
                                        Log.e("COUNTRY_NAME", countryArrayList.get(country_spin.getSelectedItemPosition()).countrySortName+" "+PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_code2","AE") );
                                        if (!countryArrayList.get(country_spin.getSelectedItemPosition()).countrySortName.equals("AE")) {
                                            online_btn.performClick();
                                            cod_btn.setVisibility(View.GONE);
                                            cod_charge_tv.setVisibility(View.GONE);
                                        }else if (!countryArrayList.get(country_spin.getSelectedItemPosition()).countrySortName.equals("AE") && PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_code2","AE").equals("AE")) {
                                            online_btn.performClick();
                                            cod_btn.setVisibility(View.GONE);
                                            cod_charge_tv.setVisibility(View.GONE);
                                        }else if (countryArrayList.get(country_spin.getSelectedItemPosition()).countrySortName.equals("AE") && !PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_code2","AE").equals("AE")) {
                                            online_btn.performClick();
                                            cod_btn.setVisibility(View.GONE);
                                            cod_charge_tv.setVisibility(View.GONE);
                                        }else {
                                            cod_btn.setVisibility(View.VISIBLE);
                                            cod_charge_tv.setVisibility(View.VISIBLE);
                                            //checkAstigmatism();
                                        }
                                    }

                                }


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });





                        String country_id=PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_id","");
                        Log.e("country_id11",country_id);
                        for (int i = 0; i < countryArrayList.size(); i++) {
                            MethodClass.CountryModelName3 stringWithTag = countryArrayList.get(i);
                            if (Objects.equals(String.valueOf(stringWithTag.tag), country_id)) {
                                Log.e("country_id22",String.valueOf(stringWithTag.tag));
                                country_spin.setSelection(i);
                                country_spin_1.setSelection(i);
                                break;
                            }
                        }
                        //setUpGClient();
                        scrollView.setVisibility(View.VISIBLE);

                    }
                } catch (Exception e) {
                    MethodClass.error_alert(CheckoutActivity.this);
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
                MethodClass.hideProgressDialog(CheckoutActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(CheckoutActivity.this);
                } else {
                    MethodClass.error_alert(CheckoutActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(CheckoutActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void checkout_post() {
        if (shipping_manual_lay.getVisibility() == View.VISIBLE) {
            if (heading.getText().toString().length() == 0) {
                heading.setError(getString(R.string.enterheading));
                heading.requestFocus();
                return;
            }
            if (name.getText().toString().length() == 0) {
                name.setError(getString(R.string.entername));
                name.requestFocus();
                return;
            }
            if (!MethodClass.emailValidator(email.getText().toString().trim())) {
                email.setError(getString(R.string.enteremail));
                email.requestFocus();
                return;
            }
            if (phone.getText().toString().length() == 0) {
                phone.setError(getString(R.string.enterphone));
                phone.requestFocus();
                return;
            }
            if (address_1.getText().toString().length() == 0) {
                address_1.setError(getString(R.string.enter_address));
                address_1.requestFocus();
                return;
            }

            if (city.getText().toString().length() == 0) {
                city.setError(getString(R.string.enter_city));
                city.requestFocus();
                return;
            }
            if (state.getText().toString().length() == 0) {
                state.setError(getString(R.string.enter_state));
                state.requestFocus();
                return;
            }
        } else {
            if (shippingAddressID.equals("")) {
                Toast.makeText(CheckoutActivity.this, getString(R.string.please_select_address), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (is_same_chk.isChecked()) {
            if (shipping_manual_lay.getVisibility() == View.VISIBLE) {
                name_1.setText(name.getText().toString().trim());
                heading_1.setText(heading.getText().toString().trim());
                email_1.setText(email.getText().toString().trim());
                phone_1.setText(phone.getText().toString().trim());
                address_b_1.setText(address_1.getText().toString().trim());
                address_2_1.setText(address_2.getText().toString().trim());
                city_1.setText(city.getText().toString().trim());
                state_1.setText(state.getText().toString().trim());
                country_code_spin_1.setSelection(country_code_spin.getFirstVisiblePosition());
                country_spin_1.setSelection(country_spin.getFirstVisiblePosition());
            } else {
                billingAddressId = shippingAddressID;
            }
        } else {
            if (billing_manual_lay.getVisibility() == View.VISIBLE) {
                if (heading_1.getText().toString().length() == 0) {
                    heading_1.setError(getString(R.string.enterheading));
                    heading_1.requestFocus();
                    return;
                }
                if (name_1.getText().toString().length() == 0) {
                    name_1.setError(getString(R.string.entername));
                    name_1.requestFocus();
                    return;
                }
                if (!MethodClass.emailValidator(email_1.getText().toString().trim())) {
                    email_1.setError(getString(R.string.enteremail));
                    email_1.requestFocus();
                    return;
                }
                if (phone_1.getText().toString().length() == 0) {
                    phone_1.setError(getString(R.string.enterphone));
                    phone_1.requestFocus();
                    return;
                }
                if (address_b_1.getText().toString().length() == 0) {
                    address_b_1.setError(getString(R.string.enter_address));
                    address_b_1.requestFocus();
                    return;
                }

                if (city_1.getText().toString().length() == 0) {
                    city_1.setError(getString(R.string.enter_city));
                    city_1.requestFocus();
                    return;
                }

                if (state_1.getText().toString().length() == 0) {
                    state_1.setError(getString(R.string.enter_state));
                    state_1.requestFocus();
                    return;
                }
            } else {
                if (billingAddressId.equals("")) {
                    Toast.makeText(this, getString(R.string.please_select_address), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "checkout";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();

        if (shipping_manual_lay.getVisibility() == View.VISIBLE) {
            params.put("shipping_full_name", name.getText().toString().trim());
            params.put("shipping_address_heading", heading.getText().toString().trim());
            params.put("shipping_email", email.getText().toString().trim());
            params.put("shipping_phone", phone.getText().toString().trim());
            params.put("shipping_country_code", countryCodeArrayList.get(country_code_spin.getSelectedItemPosition()).countryCode);
            params.put("shipping_address_line_1", address_1.getText().toString().trim());
            params.put("shipping_address_line_2", address_2.getText().toString().trim());
            params.put("shipping_city", city.getText().toString().trim());
            params.put("shipping_state", state.getText().toString().trim());
            params.put("shipping_country", String.valueOf(countryArrayList.get(country_spin.getSelectedItemPosition()).tag));
        } else {
            params.put("shipping_address_id", shippingAddressID);
        }

        if (is_same_chk.isChecked()) {
            if (shipping_manual_lay.getVisibility() == View.VISIBLE) {
                params.put("billing_full_name", name_1.getText().toString().trim());
                params.put("billing_address_heading", heading_1.getText().toString().trim());
                params.put("billing_email", email_1.getText().toString().trim());
                params.put("billing_phone", phone_1.getText().toString().trim());
                params.put("billing_country_code", countryCodeArrayList.get(country_code_spin_1.getSelectedItemPosition()).countryCode);
                params.put("billing_address_line_1", address_b_1.getText().toString().trim());
                params.put("billing_address_line_2", address_2_1.getText().toString().trim());
                params.put("billing_city", city_1.getText().toString().trim());
                params.put("billing_state", state_1.getText().toString().trim());
                params.put("billing_country", String.valueOf(countryArrayList.get(country_spin_1.getSelectedItemPosition()).tag));

            }else {
                params.put("billing_address_id", billingAddressId);
            }
        }else {
            if (billing_manual_lay.getVisibility() == View.VISIBLE) {
                params.put("billing_full_name", name_1.getText().toString().trim());
                params.put("billing_address_heading", heading_1.getText().toString().trim());
                params.put("billing_email", email_1.getText().toString().trim());
                params.put("billing_phone", phone_1.getText().toString().trim());
                params.put("billing_country_code", countryCodeArrayList.get(country_code_spin_1.getSelectedItemPosition()).countryCode);
                params.put("billing_address_line_1", address_b_1.getText().toString().trim());
                params.put("billing_address_line_2", address_2_1.getText().toString().trim());
                params.put("billing_city", city_1.getText().toString().trim());
                params.put("billing_state", state_1.getText().toString().trim());
                params.put("billing_country", String.valueOf(countryArrayList.get(country_spin_1.getSelectedItemPosition()).tag));
            } else {
                params.put("billing_address_id", billingAddressId);
            }
        }

        params.put("payment_method", Objects.equals(String.valueOf(online_btn.getTag()), "O") ? "O" : "C");
        params.put("currency_code", PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("currency_code", "AED"));
        params.put("wallet_used", wallet_bal_che.isChecked() ? "Y" : "N");
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("parames", jsonObject.toString());

        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(CheckoutActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(CheckoutActivity.this, response);
                    if (resultResponse != null) {
                        if (resultResponse.getString("is_phone_verified").equals("N")){
                            String address_id = resultResponse.getString("address_id");
                            String otp = resultResponse.getString("otp");
                            String order_no = resultResponse.getJSONObject("order").getString("order_no");
                            Toast.makeText(CheckoutActivity.this, getString(R.string.otp_has_been_sent_to_your_mobile), Toast.LENGTH_SHORT).show();
                            Intent inten = new Intent(CheckoutActivity.this, AddressPhoneVerifyActivity.class);
                            inten.putExtra("phone",phone.getText().toString().trim());
                            inten.putExtra("country_id",countryCodeArrayList.get(country_code_spin.getSelectedItemPosition()).tag);
                            inten.putExtra("code",otp);
                            inten.putExtra("order_no", order_no);
                            inten.putExtra("address_id",address_id);
                            inten.putExtra("type","C");
                            startActivity(inten);

                           /* LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(CheckoutActivity.this, DialogTypes.TYPE_SUCCESS)
                                    .setTitle(resultResponse.getJSONObject("message").getString("message"))
                                    .setDescription(resultResponse.getJSONObject("message").getString("meaning"))
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
                                    Intent inten = new Intent(CheckoutActivity.this, AddressPhoneVerifyActivity.class);
                                    inten.putExtra("phone",phone.getText().toString().trim());
                                    inten.putExtra("country_id",countryCodeArrayList.get(country_code_spin.getSelectedItemPosition()).tag);
                                    inten.putExtra("code",otp);
                                    inten.putExtra("order_no", order_no);
                                    inten.putExtra("address_id",address_id);
                                    inten.putExtra("type","C");
                                    startActivity(inten);
                                }
                            });*/
                        }else {
                            Intent intent = new Intent(CheckoutActivity.this, PlaceOrderActivity.class);
                            intent.putExtra("order_no", resultResponse.getJSONObject("order").getString("order_no"));
                            startActivity(intent);
                        }

                    }
                } catch (Exception e) {
                    MethodClass.error_alert(CheckoutActivity.this);
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
                MethodClass.hideProgressDialog(CheckoutActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(CheckoutActivity.this);
                } else {
                    MethodClass.error_alert(CheckoutActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(CheckoutActivity.this).addToRequestQueue(jsonObjectRequest);
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
                MethodClass.hideProgressDialog(CheckoutActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(CheckoutActivity.this, response);
                    if (resultResponse != null) {
                        JSONArray user_address_book = resultResponse.getJSONArray("user_address_book");
                        adress_arrayList = new ArrayList<>();
                        if (user_address_book.length() > 0) {
                            for (int i = 0; i < user_address_book.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                JSONObject object = user_address_book.getJSONObject(i);
                                hashMap.put(ADDRESS_ID, object.getString("id"));
                                hashMap.put(FULL_NAME, object.getString("full_name"));
                                hashMap.put(PHONE, object.getString("phone"));
                                hashMap.put(ADDRESS_LINE_1, object.getString("address_line_1"));
                                hashMap.put(ADDRESS_LINE_2, object.getString("address_line_2"));
                                hashMap.put(CITY, object.getString("city"));
                                hashMap.put(STATE, object.getString("state"));
                                hashMap.put(HEADING, object.getString("address_heading"));
                                hashMap.put(IS_DEFAULT, object.getString("is_default"));
                                hashMap.put(SHIPPNG_ALLOW, object.getJSONObject("get_country").getString("shipping_allowed"));
                                hashMap.put(COUNTRY_NAME, object.getJSONObject("get_country").getString("name"));
                                hashMap.put(SORT_NAME, object.getJSONObject("get_country").getString("sortname"));
                                hashMap.put(SHIPPING_PRICE, object.getJSONObject("get_country").getString("shipping_price"));
                                adress_arrayList.add(hashMap);
                            }
                            ShippingAddressAdapter shippingAddressAdapter = new ShippingAddressAdapter(CheckoutActivity.this, adress_arrayList);
                            address_recy.setAdapter(shippingAddressAdapter);
                            BillingAddressAdapter billingAddressAdapter = new BillingAddressAdapter(CheckoutActivity.this, adress_arrayList);
                            billing_address_recy.setAdapter(billingAddressAdapter);
                            shipping_list_lay.setVisibility(View.VISIBLE);
                            shipping_manual_lay.setVisibility(View.GONE);
                        } else {
                            shi_list_btn.setVisibility(View.GONE);
                            bill_list_btn.setVisibility(View.GONE);
                            shipping_list_lay.setVisibility(View.GONE);
                            shipping_manual_lay.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(CheckoutActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(CheckoutActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(CheckoutActivity.this);
                } else {
                    MethodClass.error_alert(CheckoutActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(CheckoutActivity.this).addToRequestQueue(jsonObjectRequest);
    }


    public void countryCodePopup(View view){
        MethodClass.countryCodePopup(CheckoutActivity.this,countryCodeArrayList,country_code_spin);
    }
    public void countryCodePopup1(View view){
        MethodClass.countryCodePopup(CheckoutActivity.this,countryCodeArrayList,country_code_spin_1);
    }
    public void countryNamePopup(View view){
        MethodClass.countryNamePopup3(CheckoutActivity.this,countryArrayList,country_spin);
    }
    public void countryNamePopup1(View view){
        MethodClass.countryNamePopup3(CheckoutActivity.this,countryArrayList,country_spin_1);
    }

    public void checkAstigmatism(){
        if (is_cod.equals("N")) {
            online_btn.performClick();
            cod_btn.setVisibility(View.GONE);
            cod_charge_tv.setVisibility(View.GONE);
        }else {
            if (is_astigmatism.equals("Y")) {
                online_btn.performClick();
                cod_btn.setVisibility(View.GONE);
                cod_charge_tv.setVisibility(View.GONE);
            }else {
                if (!country_sortName.equals("AE")) {
                    Log.e("COUNTRY", country_sortName+" "+PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_code2","AE") );
                    online_btn.performClick();
                    cod_btn.setVisibility(View.GONE);
                    cod_charge_tv.setVisibility(View.GONE);
                }
                else if (!country_sortName.equals("AE") && PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_code2","AE").equals("AE")) {
                    Log.e("COUNTRY", country_sortName+" "+PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_code2","AE") );
                    online_btn.performClick();
                    cod_btn.setVisibility(View.GONE);
                    cod_charge_tv.setVisibility(View.GONE);
                }else if (country_sortName.equals("AE") && !PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_code2","AE").equals("AE")) {
                    Log.e("COUNTRY", country_sortName+" "+PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_code2","AE") );
                    online_btn.performClick();
                    cod_btn.setVisibility(View.GONE);
                    cod_charge_tv.setVisibility(View.GONE);
                }else {
                    cod_btn.setVisibility(View.VISIBLE);
                    cod_charge_tv.setVisibility(View.VISIBLE);
                    //checkAstigmatism();
                }
            }

        }
    }
    public void checkCodButton(){
        if (is_cod.equals("N")) {
            online_btn.performClick();
            cod_btn.setVisibility(View.GONE);
            cod_charge_tv.setVisibility(View.GONE);
        }else {
            if (is_astigmatism.equals("Y")) {
                online_btn.performClick();
                cod_btn.setVisibility(View.GONE);
                cod_charge_tv.setVisibility(View.GONE);
            }else {
                if (!countryArrayList.get(country_spin.getSelectedItemPosition()).countrySortName.equals("AE")) {
                    online_btn.performClick();
                    cod_btn.setVisibility(View.GONE);
                    cod_charge_tv.setVisibility(View.GONE);
                }else if (!countryArrayList.get(country_spin.getSelectedItemPosition()).countrySortName.equals("AE") && PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_code2","AE").equals("AE")) {
                    online_btn.performClick();
                    cod_btn.setVisibility(View.GONE);
                    cod_charge_tv.setVisibility(View.GONE);
                }else if (countryArrayList.get(country_spin.getSelectedItemPosition()).countrySortName.equals("AE") && !PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).getString("country_code2","AE").equals("AE")) {
                    online_btn.performClick();
                    cod_btn.setVisibility(View.GONE);
                    cod_charge_tv.setVisibility(View.GONE);
                }else {
                    cod_btn.setVisibility(View.VISIBLE);
                    cod_charge_tv.setVisibility(View.VISIBLE);
                    //checkAstigmatism();
                }
            }

        }
    }


    //for location

    private synchronized void setUpGClient() {

        try {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
            googleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            Double latitude = mylocation.getLatitude();
            Double longitude = mylocation.getLongitude();
            if (latitude != null && longitude != null) {
                if (!one_time) {
                    lat = latitude;
                    long_ = longitude;
                    getCurrentAddress(location);
                    one_time = true;
                }
                if (lat == 0 && long_ == 0) {
                    lat = latitude;
                    lat = longitude;
                    getCurrentAddress(location);
                }

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:

                        MethodClass.shoExitDialog(CheckoutActivity.this);
                        break;
                }
                break;
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else {

            MethodClass.shoExitDialog(CheckoutActivity.this);
        }
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat.checkSelfPermission(CheckoutActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Do something after 100ms
                                                //Toast.makeText(HomeActivity.this, "FINE2 "+mylocation.getLatitude(), Toast.LENGTH_SHORT).show();
                                                try {
                                                    Double latitude = mylocation.getLatitude();
                                                    Double longitude = mylocation.getLongitude();
                                                    lat = latitude;
                                                    long_ = longitude;
                                                    Log.e("LATITUDE___", String.valueOf(lat));
                                                    Log.e("LONGITUDE", String.valueOf(long_));
                                                    getCurrentAddress(mylocation);
                                                    if (latitude != null && longitude != null) {
                                                        if (!one_time) {
                                                            one_time = true;
                                                        }
                                                    } else {
                                                        one_time = false;
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Log.e("error", e.getMessage());
                                                }
                                            }
                                        }, 2000);
                                        //Toast.makeText(HomeActivity.this, "FINE2 "+USER_LAT.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.

                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(CheckoutActivity.this, REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });


                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void getCurrentAddress(Location location) {
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {

            try {

                if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }

                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) this);
            } catch (Exception ex) {
                Log.i("msg", "fail to request location update, ignore", ex);
            }
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            Geocoder gcd = new Geocoder(getBaseContext(),
                    Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String locality = addresses.get(0).getLocality();
                    String subLocality = addresses.get(0).getSubLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    String countryCode = addresses.get(0).getCountryCode();
                    Log.e("countryCode",countryCode);
                    Log.e("country",country);
                    PreferenceManager.getDefaultSharedPreferences(CheckoutActivity.this).edit().putString("country_code2",countryCode).commit();

                    MethodClass.hideProgressDialog(CheckoutActivity.this);
                    String currentLocation;
                    if (subLocality != null) {

                        currentLocation = locality + "," + subLocality;
                        /*CURRENT_ADDRESS = address;
                        location_tv.setText(CURRENT_ADDRESS);
                        current_location_tv.setText(CURRENT_ADDRESS);*/
                        Log.e("currentLocation", address);
                    } else {
                        /*CURRENT_ADDRESS = address;
                        location_tv.setText(CURRENT_ADDRESS);
                        current_location_tv.setText(CURRENT_ADDRESS);*/
                        currentLocation = locality;
                        Log.e("currentLocation", address);
                    }
                    String current_locality = locality;
                }

            } catch (Exception e) {
                MethodClass.hideProgressDialog(CheckoutActivity.this);
                e.printStackTrace();
                Toast.makeText(CheckoutActivity.this, getString(R.string.failed_to_fetch_location), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }

}