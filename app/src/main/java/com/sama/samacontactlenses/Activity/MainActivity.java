package com.sama.samacontactlenses.Activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
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
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Adapter.CountryAdapter2;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    MaterialCardView engMat,arbMat;
    ImageView tickArb,tickEng;
    TextView arbTxt,engTxt;
    private Spinner shi_spin_country;
    String country_id = "";

    private GoogleApiClient googleApiClient;
    private double lat = 0, long_ = 0;
    private boolean one_time = false;
    private LocationManager locationManager;
    private Location mylocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 3000;
    private ArrayList<MethodClass.CountryModelName2> countryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(MainActivity.this);
        setContentView(R.layout.activity_main);

        engMat = findViewById(R.id.engMat);
        arbMat = findViewById(R.id.arbMat);
        tickArb = findViewById(R.id.tickArb);
        tickEng = findViewById(R.id.tickEng);
        arbTxt = findViewById(R.id.arbTxt);
        engTxt = findViewById(R.id.engTxt);
        shi_spin_country = findViewById(R.id.shi_spin_country);

        engMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                engMat.setStrokeColor(getResources().getColor(R.color.colorPrimary));
                arbMat.setStrokeColor(getResources().getColor(R.color.border));
                tickEng.setVisibility(View.VISIBLE);
                tickArb.setVisibility(View.GONE);
                engTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
                arbTxt.setTextColor(getResources().getColor(R.color.txtColor));
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("lang","en").commit();
                MethodClass.set_locale(MainActivity.this);
            }
        });
        arbMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                engMat.setStrokeColor(getResources().getColor(R.color.border));
                arbMat.setStrokeColor(getResources().getColor(R.color.colorPrimary));
                tickEng.setVisibility(View.GONE);
                tickArb.setVisibility(View.VISIBLE);
                engTxt.setTextColor(getResources().getColor(R.color.txtColor));
                arbTxt.setTextColor(getResources().getColor(R.color.colorPrimary));
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("lang","ar").commit();
                MethodClass.set_locale(MainActivity.this);
            }
        });
        getLang();


        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("is_first_time",false)){
            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(MainActivity.this, DialogTypes.TYPE_QUESTION)
                    .setTitle(getString(R.string.notification))
                    .setDescription(getString(R.string.please_give_notification_permission))
                    .setPositiveText(getString(R.string.ok))
                    .setPositiveListener(new ClickListener() {
                        @Override
                        public void onClick(LottieAlertDialog lottieAlertDialog) {
                            lottieAlertDialog.dismiss();
                            openNotificationSettingsForApp("1002");
                            PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean("is_first_time", true).commit();
                        }
                    })
                    .build();
            alertDialog.show();
        }

    }


    private void openNotificationSettingsForApp(String channelId) {
        // Links to this app's notification settings.
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && channelId!=null){
            intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_CHANNEL_ID,channelId);
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        }
        intent.putExtra("app_package", getPackageName());
        intent.putExtra("app_uid", getApplicationInfo().uid);
        startActivity(intent);
    }
    public void getLang(){
        MethodClass.customisNetworkConnected(this);
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "get-language-countries";
        HashMap<String, String> params = new HashMap<String, String>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    //MethodClass.hideProgressDialog(MainActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(MainActivity.this, response);
                    if (resultResponse != null) {

                        JSONArray countries = resultResponse.getJSONArray("countries");
                        countryArrayList = new ArrayList<>();
                        for (int i = 0; i <countries.length() ; i++) {
                            String id= countries.getJSONObject(i).getString("id");
                            String name= countries.getJSONObject(i).getString("name");
                            String sortname= countries.getJSONObject(i).getString("sortname");
                            String country_code= countries.getJSONObject(i).getString("country_code");
                            String currency_code= countries.getJSONObject(i).getJSONObject("currency").getString("currency_code");
                            String conv_factor= countries.getJSONObject(i).getJSONObject("currency").getJSONObject("currency_conversion").getString("conv_factor");
                            MethodClass.CountryModelName2 countryModelName=new MethodClass.CountryModelName2(name,sortname,country_code,currency_code,conv_factor,id);
                            countryArrayList.add(countryModelName);
                        }
                        CountryAdapter2 countryAdapter=new CountryAdapter2(MainActivity.this, countryArrayList);
                        shi_spin_country.setAdapter(countryAdapter);
                        shi_spin_country.setEnabled(false);
                        shi_spin_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                MethodClass.CountryModelName2 stringWithTag= countryArrayList.get(position);
                                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("currency", stringWithTag.currencyCode).commit();
                                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("currency_code",stringWithTag.currencyCode).commit();
                                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("conv_factor",stringWithTag.currencyConvert).commit();
                                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("country_id", String.valueOf(stringWithTag.tag)).commit();
                                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("country_code",stringWithTag.countrySortName).commit();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        setUpGClient();
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(MainActivity.this);
                    MethodClass.custom_error(MainActivity.this,e);
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(MainActivity.this);
                MethodClass.custom_error(MainActivity.this,error);
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(request);
    }

    public void start(View view){
        MethodClass.customisNetworkConnected(this);
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "change-lang";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lang",PreferenceManager.getDefaultSharedPreferences(this).getString("lang","en"));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    MethodClass.hideProgressDialog(MainActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(MainActivity.this, response);
                    if (resultResponse != null) {
                        String status = resultResponse.getString("status");
                        MethodClass.go_to_next_activity(MainActivity.this,HomeActivity.class);
                        finish();
                    }
                } catch (JSONException error) {
                    MethodClass.hideProgressDialog(MainActivity.this);
                    MethodClass.custom_error(MainActivity.this,error);
                    FirebaseCrashlytics.getInstance().recordException(error);
                    Log.e("JSONException", error.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(MainActivity.this);
                MethodClass.custom_error(MainActivity.this,error);
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(request);
    }


    public void openSpinPopup(View view){
        MethodClass.countryNamePopup2(MainActivity.this,countryArrayList,shi_spin_country);
    }



    private synchronized void setUpGClient() {

        try {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
            googleApiClient.connect();
        } catch (Exception error) {
            MethodClass.custom_error(MainActivity.this,error);
            FirebaseCrashlytics.getInstance().setCustomKey("Loca_str_key", error.toString());
            error.printStackTrace();
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
                        MethodClass.shoExitDialog(MainActivity.this);
                        break;
                }
                break;
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else {

            MethodClass.shoExitDialog(MainActivity.this);
        }
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
                                    int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
                                                } catch (Exception error) {
                                                    MethodClass.custom_error(MainActivity.this,error);
                                                    FirebaseCrashlytics.getInstance().recordException(error);
                                                    error.printStackTrace();
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
                                        status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS_GPS);
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
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("country_code2",countryCode).commit();

                    for (int i = 0; i < countryArrayList.size(); i++) {
                        MethodClass.CountryModelName2 stringWithTag= countryArrayList.get(i);
                        if (Objects.equals(stringWithTag.countrySortName, countryCode)){
                            shi_spin_country.setSelection(i);
                        }
                    }
                    MethodClass.hideProgressDialog(MainActivity.this);
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

            } catch (Exception error) {
                //MethodClass.custom_error(MainActivity.this,error);
                FirebaseCrashlytics.getInstance().setCustomKey("Loca33_str_key", error.toString());
                MethodClass.hideProgressDialog(MainActivity.this);
                error.printStackTrace();
                Toast.makeText(MainActivity.this, getString(R.string.failed_to_fetch_location), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        MethodClass.hideProgressDialog(this);
        super.onDestroy();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}