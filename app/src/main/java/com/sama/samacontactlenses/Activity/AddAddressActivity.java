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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.sama.samacontactlenses.Adapter.AddressAdapter;
import com.sama.samacontactlenses.Adapter.CountryAdapter;
import com.sama.samacontactlenses.Adapter.CountryCodeAdapter;
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
import static com.sama.samacontactlenses.Common.Constant.CITY;
import static com.sama.samacontactlenses.Common.Constant.COUNTRY_NAME;
import static com.sama.samacontactlenses.Common.Constant.EMAIL_ADDRESS;
import static com.sama.samacontactlenses.Common.Constant.FULL_NAME;
import static com.sama.samacontactlenses.Common.Constant.PHONE;
import static com.sama.samacontactlenses.Common.Constant.STATE;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class AddAddressActivity extends AppCompatActivity{
    private EditText name, email, phone, address_1, address_2, city, state, heading;
    private Spinner country_code_spin;
    private Spinner country_spin;
    private Button add_adres_btn;

    ArrayList<MethodClass.CountryModelCode> countryCodeArrayList;
    ArrayList<MethodClass.CountryModelName> countryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_add_adress);
        TextView title=findViewById(R.id.title);
        title.setText(getString(R.string.add_address));
        MethodClass.setBottomFun(this);


        country_code_spin = findViewById(R.id.country_code_spin);
        country_spin = findViewById(R.id.country_spin);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address_1 = findViewById(R.id.address_1);
        address_2 = findViewById(R.id.address_2);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        heading = findViewById(R.id.heading);
        add_adres_btn = findViewById(R.id.add_adres_btn);
        getCountryCode();
        add_adres_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_address();
            }
        });

        name.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("name",""));
        email.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("email",""));
        phone.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("phone_number",""));

    }


    public void add_address() {
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

        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }

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
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "add-address-book";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("full_name", name.getText().toString().trim());
        params.put("email", email.getText().toString().trim());
        params.put("phone", phone.getText().toString().trim());
        params.put("country_code", countryCodeArrayList.get(country_code_spin.getSelectedItemPosition()).countryCode);
        params.put("address_line_1", address_1.getText().toString().trim());
        params.put("address_line_2", address_2.getText().toString().trim());
        params.put("city", city.getText().toString().trim());
        params.put("state", state.getText().toString().trim());
        params.put("address_heading", heading.getText().toString().trim());
        params.put("country_id", String.valueOf(countryArrayList.get(country_spin.getSelectedItemPosition()).tag));

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("parames", jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(AddAddressActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(AddAddressActivity.this, response);
                    if (resultResponse != null) {
                        if (resultResponse.getString("is_phone_verified").equals("N")) {
                            String otp = resultResponse.getString("otp");
                            Toast.makeText(AddAddressActivity.this, getString(R.string.otp_has_been_sent_to_your_mobile), Toast.LENGTH_SHORT).show();
                            String id = resultResponse.getJSONObject("address").getString("id");
                            Intent inten = new Intent(AddAddressActivity.this, AddressPhoneVerifyActivity.class);
                            inten.putExtra("phone",phone.getText().toString().trim());
                            inten.putExtra("country_id",String.valueOf(countryArrayList.get(country_spin.getSelectedItemPosition()).tag));
                            inten.putExtra("code",otp);
                            inten.putExtra("address_id",id);
                            inten.putExtra("type","A");
                            startActivity(inten);
                            /*LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(AddAddressActivity.this, DialogTypes.TYPE_SUCCESS)
                                    .setTitle(resultResponse.getString("message"))
                                    .setDescription(resultResponse.getString("meaning"))
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
                                    Intent inten = new Intent(AddAddressActivity.this, AddressPhoneVerifyActivity.class);
                                    inten.putExtra("phone",phone.getText().toString().trim());
                                    inten.putExtra("country_id",String.valueOf(countryArrayList.get(country_spin.getSelectedItemPosition()).tag));
                                    inten.putExtra("code",otp);
                                    inten.putExtra("address_id",id);
                                    inten.putExtra("type","A");
                                    startActivity(inten);
                                }
                            });*/

                        } else {
                            String message = resultResponse.getString("message");
                            String meaning = resultResponse.getString("meaning");
                            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(AddAddressActivity.this, DialogTypes.TYPE_SUCCESS)
                                    .setTitle(message)
                                    .setDescription(meaning)
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
                        }
                    }
                } catch (Exception e) {
                    MethodClass.error_alert(AddAddressActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(AddAddressActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(AddAddressActivity.this);
                } else {
                    MethodClass.error_alert(AddAddressActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(AddAddressActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(AddAddressActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(AddAddressActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(AddAddressActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void getCountryCode() {
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "get-countries-code";
        Log.e("server_url",server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    MethodClass.hideProgressDialog(AddAddressActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(AddAddressActivity.this, response);
                    if (resultResponse != null) {

                        JSONArray countries = resultResponse.getJSONArray("countries");
                        countryCodeArrayList = new ArrayList<>();
                        countryArrayList = new ArrayList<>();
                        for (int i = 0; i < countries.length(); i++) {
                            String id = countries.getJSONObject(i).getString("id");
                            String name = countries.getJSONObject(i).getString("name");
                            String sortname = countries.getJSONObject(i).getString("sortname");
                            String country_code = countries.getJSONObject(i).getString("country_code");
                            MethodClass.CountryModelCode countryModelCode = new MethodClass.CountryModelCode(name, sortname, country_code, id);
                            countryCodeArrayList.add(countryModelCode);
                            MethodClass.CountryModelName countryModelName = new MethodClass.CountryModelName(name, sortname, country_code, id);
                            countryArrayList.add(countryModelName);
                        }
                        CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(AddAddressActivity.this, countryCodeArrayList);
                        country_code_spin.setAdapter(countryCodeAdapter);

                        String country_phone_code=PreferenceManager.getDefaultSharedPreferences(AddAddressActivity.this).getString("country_phone_code","");
                        for (int i = 0; i < countryCodeArrayList.size(); i++) {
                            MethodClass.CountryModelCode stringWithTag = countryCodeArrayList.get(i);
                            if (Objects.equals(stringWithTag.countryCode, country_phone_code)) {
                                country_code_spin.setSelection(i);
                                break;
                            }
                        }


                        CountryAdapter countryAdapter = new CountryAdapter(AddAddressActivity.this, countryArrayList);
                        country_spin.setAdapter(countryAdapter);

                        String country_id=PreferenceManager.getDefaultSharedPreferences(AddAddressActivity.this).getString("country_id","");
                        for (int i = 0; i < countryArrayList.size(); i++) {
                            MethodClass.CountryModelName stringWithTag = countryArrayList.get(i);
                            if (Objects.equals(String.valueOf(stringWithTag.tag), country_id)) {
                                country_spin.setSelection(i);
                                break;
                            }
                        }
                        //setUpGClient();
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(AddAddressActivity.this);
                    MethodClass.error_alert(AddAddressActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(AddAddressActivity.this);
                MethodClass.error_alert(AddAddressActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(AddAddressActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(AddAddressActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(AddAddressActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(AddAddressActivity.this).addToRequestQueue(request);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setMenu(this);
    }
    public void back(View view) {
        onBackPressed();
    }


    public void countryCodePopup(View view) {
        MethodClass.countryCodePopup(AddAddressActivity.this,countryCodeArrayList,country_code_spin);
    }

    public void countryNamePopup(View view) {
        MethodClass.countryNamePopup(AddAddressActivity.this,countryArrayList,country_spin);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}