package com.sama.samacontactlenses.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class ForgotPasswordActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Spinner country_code_spin;
    private EditText phone;

    //////Location/////////////
    private GoogleApiClient googleApiClient;
    private double lat = 0, long_ = 0;
    private boolean one_time = false;
    private LocationManager locationManager;
    private Location mylocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 3000;
    ArrayList<MethodClass.CountryModelCode> cityArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_forgot_password);
        phone = findViewById(R.id.phone);
        country_code_spin = findViewById(R.id.country_code_spin);
        getCountryCode();
    }

    public void submit(View view) {

        String get_phone = "";
        get_phone = phone.getText().toString().trim();

        if (get_phone.length() == 0) {
            phone.setError(getString(R.string.enterphone));
            phone.requestFocus();
            return;
        }

        if (!isNetworkConnected(ForgotPasswordActivity.this)) {
            MethodClass.network_error_alert(ForgotPasswordActivity.this);
            return;
        }

        SmsRetrieverClient client = SmsRetriever.getClient(ForgotPasswordActivity.this);
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

        MethodClass.showProgressDialog(ForgotPasswordActivity.this);
        String server_url = getString(R.string.SERVER_URL) + "forgot-pass-user";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", get_phone);
        params.put("country_code", cityArrayList.get(country_code_spin.getSelectedItemPosition()).countryCode);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("parames", jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(ForgotPasswordActivity.this);
                Log.e("respLogin", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(ForgotPasswordActivity.this, response);
                    if (resultResponse != null) {


                        String otp = resultResponse.getString("vcode");
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.otp_has_been_sent_to_your_mobile), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyActivity.class);
                        intent.putExtra("phone", phone.getText().toString().trim());
                        intent.putExtra("country_id", cityArrayList.get(country_code_spin.getSelectedItemPosition()).countryCode);
                        intent.putExtra("code", otp);
                        if (getIntent().getExtras() !=null){
                            if (getIntent().getStringExtra("type").equals("C")){
                                intent.putExtra("type","C");
                            }else {
                                intent.putExtra("type","S");
                            }
                        }else {
                            intent.putExtra("type","S");
                        }
                        startActivity(intent);
                       /* LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(ForgotPasswordActivity.this, DialogTypes.TYPE_SUCCESS)
                                .setTitle(getString(R.string.otp_send_successfully))
                                .setDescription(getString(R.string.otp_description))
                                .setPositiveText(getString(R.string.ok))
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyActivity.class);
                                        intent.putExtra("phone", phone.getText().toString().trim());
                                        intent.putExtra("country_id", cityArrayList.get(country_code_spin.getSelectedItemPosition()).countryCode);
                                        intent.putExtra("code", otp);
                                        if (getIntent().getExtras() !=null){
                                            if (getIntent().getStringExtra("type").equals("C")){
                                                intent.putExtra("type","C");
                                            }else {
                                                intent.putExtra("type","S");
                                            }
                                        }else {
                                            intent.putExtra("type","S");
                                        }
                                        startActivity(intent);
                                    }
                                })
                                .build();
                        alertDialog.show();*/
                    }

                } catch (JSONException e) {
                    MethodClass.error_alert(ForgotPasswordActivity.this);
                    e.printStackTrace();
                    Log.e("login_parce", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(ForgotPasswordActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(ForgotPasswordActivity.this);
                } else {
                    MethodClass.error_alert(ForgotPasswordActivity.this);
                }
            }
        }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ForgotPasswordActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ForgotPasswordActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ForgotPasswordActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ForgotPasswordActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void getCountryCode() {
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "get-countries-code";
        HashMap<String, String> params = new HashMap<String, String>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    MethodClass.hideProgressDialog(ForgotPasswordActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(ForgotPasswordActivity.this, response);
                    if (resultResponse != null) {

                        JSONArray countries = resultResponse.getJSONArray("countries");
                        cityArrayList = new ArrayList<>();
                        for (int i = 0; i < countries.length(); i++) {
                            String id = countries.getJSONObject(i).getString("id");
                            String name = countries.getJSONObject(i).getString("name");
                            String sortname = countries.getJSONObject(i).getString("sortname");
                            String country_code = countries.getJSONObject(i).getString("country_code");
                            MethodClass.CountryModelCode countryModelCode = new MethodClass.CountryModelCode(name, sortname, country_code, id);
                            cityArrayList.add(countryModelCode);
                        }
                        CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(ForgotPasswordActivity.this, cityArrayList);
                        country_code_spin.setAdapter(countryCodeAdapter);

                        String country_id=PreferenceManager.getDefaultSharedPreferences(ForgotPasswordActivity.this).getString("country_id","");
                        for (int i = 0; i < cityArrayList.size(); i++) {
                            MethodClass.CountryModelCode stringWithTag = cityArrayList.get(i);
                            if (Objects.equals(stringWithTag.tag, country_id)) {
                                country_code_spin.setSelection(i);
                            }
                        }
                        //setUpGClient();
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(ForgotPasswordActivity.this);
                    MethodClass.error_alert(ForgotPasswordActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(ForgotPasswordActivity.this);
                MethodClass.error_alert(ForgotPasswordActivity.this);
            }
        }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ForgotPasswordActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ForgotPasswordActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ForgotPasswordActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ForgotPasswordActivity.this).addToRequestQueue(request);
    }

    public void back(View view) {
        onBackPressed();
    }

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


    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(ForgotPasswordActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
        int permissionLocation = ContextCompat.checkSelfPermission(ForgotPasswordActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else {

            MethodClass.shoExitDialog(ForgotPasswordActivity.this);
        }
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(ForgotPasswordActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
                                    int permissionLocation = ContextCompat.checkSelfPermission(ForgotPasswordActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
                                        status.startResolutionForResult(ForgotPasswordActivity.this, REQUEST_CHECK_SETTINGS_GPS);
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
                    Log.e("countryCode", countryCode);
                    Log.e("country", country);
                    for (int i = 0; i < cityArrayList.size(); i++) {
                        MethodClass.CountryModelCode stringWithTag = cityArrayList.get(i);
                        if (Objects.equals(stringWithTag.countrySortName, countryCode)) {
                            country_code_spin.setSelection(i);
                        }
                    }
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
                e.printStackTrace();
                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.failed_to_fetch_location), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void countryCodePopup(View view) {
        MethodClass.countryCodePopup(ForgotPasswordActivity.this,cityArrayList,country_code_spin);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}