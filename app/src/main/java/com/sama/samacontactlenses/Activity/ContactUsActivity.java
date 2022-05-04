package com.sama.samacontactlenses.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import static com.sama.samacontactlenses.Helper.MethodClass.openWhatsAppConversation;

public class ContactUsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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
    ArrayList<MethodClass.CountryModelCode> countryCodeArrayList;
    private Spinner country_code_spin;

    private TextView address_tv, phone_tv, email_tv;
    private EditText name, email, phone, subject, message;
    private ImageView whatsapp_img,facebook_img,twiiter_img,instagram_img,youtube_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_contact_us);
        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.contact_us));
        country_code_spin = findViewById(R.id.country_code_spin);
        address_tv = findViewById(R.id.address_tv);
        phone_tv = findViewById(R.id.phone_tv);
        email_tv = findViewById(R.id.email_tv);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        subject = findViewById(R.id.subject);
        message = findViewById(R.id.message);
        whatsapp_img = findViewById(R.id.whatsapp_img);
        facebook_img = findViewById(R.id.facebook_img);
        twiiter_img = findViewById(R.id.twiiter_img);
        instagram_img = findViewById(R.id.instagram_img);
        youtube_img = findViewById(R.id.youtube_img);

        name.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("name", ""));
        email.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("email", ""));
        phone.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("phone_number", ""));

        getAddress();
    }


    public void getAddress() {
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "show-contact-us";
        Log.e("server_url", server_url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    MethodClass.hideProgressDialog(ContactUsActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(ContactUsActivity.this, response);
                    if (resultResponse != null) {
                        JSONObject contact_content = resultResponse.getJSONObject("contact_content");
                        email_tv.setText(contact_content.getString("email"));
                        address_tv.setText(contact_content.getString("address"));
                        phone_tv.setText("+" + contact_content.getString("country_code") + " " + contact_content.getString("phone"));
                        String facebook_url=resultResponse.getString("facebook_url");
                        String twitter_url=resultResponse.getString("twitter_url");
                        String instagram_url=resultResponse.getString("instagram_url");
                        String whatsapp_no=resultResponse.getString("whatsapp_no");
                        String youtube_url=resultResponse.getString("youtube_url");
                        String phone="+"+contact_content.getString("country_code")+contact_content.getString("phone");
                        phone_tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+phone));
                                startActivity(intent);
                            }
                        });

                        facebook_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(facebook_url));
                                startActivity(i);
                            }
                        });
                        youtube_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(youtube_url));
                                startActivity(i);
                            }
                        });
                        twiiter_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(twitter_url));
                                startActivity(i);
                            }
                        });
                        instagram_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(instagram_url));
                                startActivity(i);
                            }
                        });
                        whatsapp_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openWhatsAppConversation(ContactUsActivity.this,whatsapp_no,"hiiiii");
                            }
                        });


                        getCountryCode();
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(ContactUsActivity.this);
                    MethodClass.error_alert(ContactUsActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(ContactUsActivity.this);
                MethodClass.error_alert(ContactUsActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ContactUsActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ContactUsActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ContactUsActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ContactUsActivity.this).addToRequestQueue(request);
    }

    public void Send(View view) {
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
        if (subject.getText().toString().length() == 0) {
            subject.setError(getString(R.string.enter_subject));
            subject.requestFocus();
            return;
        }
        if (message.getText().toString().length() == 0) {
            message.setError(getString(R.string.enter_message));
            message.requestFocus();
            return;
        }

        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "post-contact-us-form";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", getIntent().getStringExtra("id"));
        params.put("name", name.getText().toString().trim());
        params.put("email", email.getText().toString().trim());
        params.put("phone", phone.getText().toString().trim());
        params.put("subject", subject.getText().toString().trim());
        params.put("country_code", countryCodeArrayList.get(country_code_spin.getSelectedItemPosition()).countryCode);
        params.put("message", message.getText().toString().trim());
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("parames", jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(ContactUsActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(ContactUsActivity.this, response);
                    if (resultResponse != null) {
                        String message = resultResponse.getString("message");
                        String meaning = resultResponse.getString("meaning");
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(ContactUsActivity.this, DialogTypes.TYPE_SUCCESS)
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
                } catch (Exception e) {
                    MethodClass.error_alert(ContactUsActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(ContactUsActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(ContactUsActivity.this);
                } else {
                    MethodClass.error_alert(ContactUsActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ContactUsActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ContactUsActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ContactUsActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ContactUsActivity.this).addToRequestQueue(jsonObjectRequest);
    }


    public void back(View view) {
        onBackPressed();
    }

    //here onActivityResult of this activity for get google response
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//here call super onActivityResult
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:

                        MethodClass.shoExitDialog(ContactUsActivity.this);
                        break;
                }
                break;
        }
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
                    MethodClass.hideProgressDialog(ContactUsActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(ContactUsActivity.this, response);
                    if (resultResponse != null) {

                        JSONArray countries = resultResponse.getJSONArray("countries");
                        countryCodeArrayList = new ArrayList<>();
                        for (int i = 0; i < countries.length(); i++) {
                            String id = countries.getJSONObject(i).getString("id");
                            String name = countries.getJSONObject(i).getString("name");
                            String sortname = countries.getJSONObject(i).getString("sortname");
                            String country_code = countries.getJSONObject(i).getString("country_code");
                            MethodClass.CountryModelCode countryModelCode = new MethodClass.CountryModelCode(name, sortname, country_code, id);
                            countryCodeArrayList.add(countryModelCode);
                        }
                        CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(ContactUsActivity.this, countryCodeArrayList);
                        country_code_spin.setAdapter(countryCodeAdapter);

                        String country_phone_code=PreferenceManager.getDefaultSharedPreferences(ContactUsActivity.this).getString("country_phone_code","");
                        for (int i = 0; i < countryCodeArrayList.size(); i++) {
                            MethodClass.CountryModelCode stringWithTag = countryCodeArrayList.get(i);
                            if (Objects.equals(stringWithTag.countryCode, country_phone_code)) {
                                country_code_spin.setSelection(i);
                                break;
                            }
                        }
                        //setUpGClient();
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(ContactUsActivity.this);
                    MethodClass.error_alert(ContactUsActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(ContactUsActivity.this);
                MethodClass.error_alert(ContactUsActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ContactUsActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ContactUsActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ContactUsActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ContactUsActivity.this).addToRequestQueue(request);
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
        int permissionLocation = ContextCompat.checkSelfPermission(ContactUsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
        int permissionLocation = ContextCompat.checkSelfPermission(ContactUsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else {

            MethodClass.shoExitDialog(ContactUsActivity.this);
        }
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(ContactUsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
                                    int permissionLocation = ContextCompat.checkSelfPermission(ContactUsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
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
                                        status.startResolutionForResult(ContactUsActivity.this, REQUEST_CHECK_SETTINGS_GPS);
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
                    for (int i = 0; i < countryCodeArrayList.size(); i++) {
                        MethodClass.CountryModelCode stringWithTag = countryCodeArrayList.get(i);
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
                Toast.makeText(ContactUsActivity.this, getString(R.string.failed_to_fetch_location), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void countryCodePopup(View view) {
        MethodClass.countryCodePopup(ContactUsActivity.this,countryCodeArrayList,country_code_spin);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}