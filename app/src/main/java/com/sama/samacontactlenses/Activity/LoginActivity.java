package com.sama.samacontactlenses.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
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
import java.util.Map;
import java.util.Objects;

import static com.sama.samacontactlenses.Common.Constant.SHARED_PREF;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout fblogin;
    LoginButton login_button;
    CallbackManager callbackManager;
    private EditText email_et, pass;
    private Integer SAMA_SIGN_IN = 1;
    private LinearLayout googleLogin;
    private Button button1;

    ArrayList<MethodClass.CountryModelCode> cityArrayList;
    private Spinner country_code_spin, country_code_spin_popup;
    private String fromSocial = "null", social_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        FacebookSdk.sdkInitialize(getApplicationContext());//here initialize Facebook Sdk
        setContentView(R.layout.activity_login);
        email_et = findViewById(R.id.email_et);
        pass = findViewById(R.id.pass);
        googleLogin = findViewById(R.id.googleLogin);
        fblogin = (LinearLayout) findViewById(R.id.fblogin);
        login_button = findViewById(R.id.login_button);
        country_code_spin = findViewById(R.id.country_code_spin);
        country_code_spin.setEnabled(false);
        button1 = findViewById(R.id.button1);
        //here Facebook callback Manager
        callbackManager = CallbackManager.Factory.create();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pass.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pass.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    pass.setTransformationMethod(new HideReturnsTransformationMethod());
                }
            }
        });
        getCountryCode();

       /* try {
            PackageInfo info = getPackageManager().getPackageInfo("com.sama.samacontactlenses", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        //here facebook login click button
        fblogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile profile = Profile.getCurrentProfile().getCurrentProfile();
                if (profile != null) {
                    // user has logged in
                    LoginManager.getInstance().logOut();
                    login_button.performClick();
                } else {
                    // user has not logged in
                    login_button.performClick();
                }
            }
        });

        //here google sign in create option
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);//here create google sign client

        //here google button click event
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //here check my google have loged in or not if loged in first log outthen log in
                if (mGoogleSignInClient != null) {
                    mGoogleSignInClient.signOut();
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, SAMA_SIGN_IN);
                } else {
                    //here first log in with google
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, SAMA_SIGN_IN);
                }
            }
        });
        //here facebook call back when come the response
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("onSuccess");
                String accessToken = loginResult.getAccessToken().getToken();//here facebook accessToken provided by facebook
                Log.i("accessToken", accessToken);

                //here create GraphRequest for get user information  with passing accessToken (loginResult.getAccessToken())
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        //this is response  method of facebook GraphRequest in this method got response(user details)
                        Log.i("LoginActivity", response.toString());
                        try {
                            MethodClass.showProgressDialog(LoginActivity.this);
                            //here get registration id(used of get notification) of SharedPreferences
                           /* SharedPreferences pref = LoginActivity.this.getSharedPreferences(SHARED_PREF, 0);
                            String regId = pref.getString("regId", null);//here reg id*/
                            //here get facebook user data like id and name;
                            final String id = object.getString("id");
                            final String firstname = object.getString("first_name");
                            final String lastname = object.getString("last_name");
                            String email = "";
                            if (object.has("email")) {
                                email = object.getString("email");
                            }
                            String imageURL = "";
                            JSONObject data = response.getJSONObject();
                            if (data.has("picture")) {
                                imageURL = data.getJSONObject("picture").getJSONObject("data").getString("url");
                            }

                            Log.w("id", id);
                            Log.w("firstname", firstname);
                            Log.w("lastname", lastname);
                            Log.w("email", email);
                            Log.w("imageURL", imageURL);

                            SharedPreferences pref = getSharedPreferences(SHARED_PREF, 0);
                            String firebase_reg_no = pref.getString("regId", null);
                            String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            String server_url = getString(R.string.SERVER_URL) + "facebook-login";
                            Log.e("server_url", server_url);
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("device_id", android_id);
                            params.put("device_type", "A");
                            params.put("firebase_reg_no", firebase_reg_no);
                            params.put("name", firstname + " " + lastname);
                            params.put("facebook_id", id);
                            params.put("country_id", PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("country_id", ""));
                            if (!email.equals("") && !email.equals(null)) {
                                params.put("email", email);
                            }
                            if (!imageURL.equals("") && !imageURL.equals(null)) {
                                params.put("image", imageURL);
                            }

                            JSONObject jsonObject = MethodClass.Json_rpc_format(params);
                            Log.e("parames", jsonObject.toString());


                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    MethodClass.hideProgressDialog(LoginActivity.this);
                                    Log.e("respLogin", response.toString());
                                    try {
                                        JSONObject resultResponce = MethodClass.get_result_from_webservice(LoginActivity.this, response);
                                        if (resultResponce != null) {
                                            if (resultResponce.getString("is_phone_verified").equals("Y")) {
                                                String token = resultResponce.getString("token");
                                                JSONObject userdata = resultResponce.getJSONObject("user");
                                                String id = userdata.getString("id");
                                                String name = userdata.getString("name");
                                                String email = userdata.getString("email");
                                                String profile_picture = userdata.getString("image");
                                                String phone = userdata.getString("phone");

                                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("token", token).commit();
                                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("name", name).commit();
                                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("user_id", id).commit();
                                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("email", email).commit();
                                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putBoolean("is_logged_in", true).commit();
                                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("profile_pic", profile_picture).commit();
                                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("phone_number", phone).commit();
                                                String country_phone_code = userdata.getString("country_code");
                                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("country_phone_code", country_phone_code).commit();

                                                if (getIntent().getExtras() != null) {
                                                    if (getIntent().getStringExtra("type").equals("C")) {
                                                        Intent intent = new Intent(LoginActivity.this, ShoppingCartActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    } else {
                                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    }
                                                } else {
                                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                }
                                            } else {
                                                fromSocial = "facebook";
                                                social_id = id;
                                                getPhone();
                                            }

                                        }
                                    } catch (JSONException e) {
                                        MethodClass.error_alert(LoginActivity.this);
                                        e.printStackTrace();
                                        Log.e("login_parce", e.toString());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    MethodClass.hideProgressDialog(LoginActivity.this);
                                    if (error.toString().contains("ConnectException")) {
                                        MethodClass.network_error_alert(LoginActivity.this);
                                    } else {
                                        MethodClass.error_alert(LoginActivity.this);
                                    }
                                }
                            }) {
                                @Override
                                public Map getHeaders() throws AuthFailureError {
                                    HashMap headers = new HashMap();
                                    headers.put("Content-Type", "application/json");
                                    headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("lang", "en"));
                                    if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getBoolean("is_logged_in", false)) {
                                        headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("token", ""));
                                    }
                                    Log.e("getHeaders: ", headers.toString());
                                    return headers;
                                }
                            };
                            MySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);

                        } catch (JSONException e) {
                            //here facebook graph request JSONException
                            e.printStackTrace();
                            //Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //here request user data parameter by Bundle
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender, birthday,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();//here send request to facebook server
            }

            //here facebook onCancel method
            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            //here facebook onError method
            @Override
            public void onError(FacebookException exception) {
                //here network problem any type then come this catch function here and show pop up to something wrong
                System.out.println("onError");
                Toast.makeText(LoginActivity.this, exception.getMessage().toString(), Toast.LENGTH_LONG).show();
                Log.v("LoginActivity", exception.getMessage().toString());
            }
        });

    }

    public void login(View view) {
        String get_email = "", get_pswrd = "";
        get_email = email_et.getText().toString().trim();
        get_pswrd = pass.getText().toString().trim();

        if (get_email.length() == 0) {
            email_et.setError(getString(R.string.enterphone));
            email_et.requestFocus();
            return;
        }
        /*if (!MethodClass.emailValidator(get_email)) {
            email_et.setError("please enter valid email address");
            email_et.requestFocus();
            return;
        }*/
        if (get_pswrd.length() == 0) {
            pass.setError(getString(R.string.enterpas));
            pass.requestFocus();
            return;
        }

        if (!isNetworkConnected(LoginActivity.this)) {
            MethodClass.network_error_alert(LoginActivity.this);
            return;
        }


        MethodClass.showProgressDialog(LoginActivity.this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences pref = getSharedPreferences(SHARED_PREF, 0);
        String firebase_reg_no = pref.getString("regId", null);
        String server_url = getString(R.string.SERVER_URL) + "login";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("device_id", android_id);
        params.put("device_type", "A");
        params.put("firebase_reg_no", firebase_reg_no);
        params.put("email", get_email);
        params.put("password", get_pswrd);
        params.put("country_code", cityArrayList.get(country_code_spin.getSelectedItemPosition()).countryCode);
        params.put("country_id", PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("country_id", ""));
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("parames", jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(LoginActivity.this);
                Log.e("respLogin", response.toString());
                try {
                    JSONObject resultResponce = MethodClass.get_result_from_webservice(LoginActivity.this, response);
                    if (resultResponce != null) {


                        String status = resultResponce.getString("status");
                        if (status.equals("A")) {
                            JSONObject userdata = resultResponce.getJSONObject("userdata");
                            String id = userdata.getString("id");
                            String name = userdata.getString("name");
                            String email = userdata.getString("email");
                            String profile_picture = userdata.getString("image");
                            String phone = userdata.getString("phone");

                            String token = resultResponce.getString("token");

                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("token", token).commit();
                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("name", name).commit();
                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("user_id", id).commit();
                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("email", email).commit();
                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putBoolean("is_logged_in", true).commit();
                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("profile_pic", profile_picture).commit();
                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("phone_number", phone).commit();
                            String country_phone_code = userdata.getString("country_code");
                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("country_phone_code", country_phone_code).commit();

                            if (getIntent().getExtras() != null) {
                                if (getIntent().getStringExtra("type").equals("C")) {
                                    Intent intent = new Intent(LoginActivity.this, ShoppingCartActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            } else {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } else if (status.equals("U")) {
                            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(LoginActivity.this, DialogTypes.TYPE_WARNING)
                                    .setTitle(resultResponce.getString("message"))
                                    .setDescription(resultResponce.getString("meaning"))
                                    .setPositiveText(getString(R.string.ok))
                                    .setPositiveListener(new ClickListener() {
                                        @Override
                                        public void onClick(LottieAlertDialog lottieAlertDialog) {
                                            lottieAlertDialog.dismiss();
                                            resendOtp();
                                        }
                                    })
                                    .build();
                            alertDialog.show();
                        } else if (status.equals("I")) {
                            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(LoginActivity.this, DialogTypes.TYPE_ERROR)
                                    .setTitle(resultResponce.getString("message"))
                                    .setDescription(resultResponce.getString("meaning"))
                                    .setPositiveText(getString(R.string.ok))
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
                } catch (JSONException e) {
                    MethodClass.error_alert(LoginActivity.this);
                    e.printStackTrace();
                    Log.e("login_parce", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(LoginActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(LoginActivity.this);
                } else {
                    MethodClass.error_alert(LoginActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void signUp(View view) {
        Intent I = new Intent(LoginActivity.this, SignUpActivity.class);
        if (getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("type").equals("C")) {
                I.putExtra("type", "C");
            }
        }
        startActivity(I);
    }

    private void resendOtp() {
        if (!isNetworkConnected(LoginActivity.this)) {
            MethodClass.network_error_alert(LoginActivity.this);
            return;
        }
        MethodClass.showProgressDialog(LoginActivity.this);
        String server_url = getString(R.string.SERVER_URL) + "resend-code";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", email_et.getText().toString().trim());
        params.put("country_code", cityArrayList.get(country_code_spin.getSelectedItemPosition()).countryCode);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("params", jsonObject.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(LoginActivity.this);
                JSONObject jsonObject = MethodClass.get_result_from_webservice(LoginActivity.this, response);
                try {
                    if (jsonObject != null) {
                        String otp = jsonObject.getString("vcode");
                        Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                        intent.putExtra("phone", email_et.getText().toString().trim());
                        intent.putExtra("country_id", cityArrayList.get(country_code_spin.getSelectedItemPosition()).countryCode);
                        intent.putExtra("code", otp);
                        if (getIntent().getExtras() != null) {
                            if (getIntent().getStringExtra("type").equals("C")) {
                                intent.putExtra("type", "C");
                            } else {
                                intent.putExtra("type", "S");
                            }
                        } else {
                            intent.putExtra("type", "S");
                        }
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("resp", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(LoginActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(request);
    }


    //here onActivityResult of this activity for get google response
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);//here call  callbackManager when come from facebook response

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SAMA_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);//call google handleSignInResult method
        }

        super.onActivityResult(requestCode, resultCode, data);//here call super onActivityResult
    }

    //here create google handleSignInResult
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class); //here create class to get google user data after completed sign in authentication
            // Signed in successfully, show authenticated UI.
            //here fetch user data of google user
            Log.w("Googlesign", account.getEmail());
            Log.w("Googlesign", account.getDisplayName());
            Log.w("Googlesign", account.getGivenName());
            Log.w("Googlesign", account.getFamilyName());
            Log.w("Googlesign", account.getId());
            String g_first_name, g_last_name, g_id, g_email, g_photp = "";

            g_first_name = account.getDisplayName();
            g_last_name = account.getFamilyName();
            g_email = account.getEmail();
            g_id = account.getId();

            if (account.getPhotoUrl() != null) {
                g_photp = account.getPhotoUrl().toString();
            } else {
                g_photp = "";
            }

            MethodClass.showProgressDialog(LoginActivity.this);
            SharedPreferences pref = getSharedPreferences(SHARED_PREF, 0);
            String firebase_reg_no = pref.getString("regId", null);
            String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String server_url = getString(R.string.SERVER_URL) + "google-login";
            Log.e("server_url", server_url);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("device_id", android_id);
            params.put("device_type", "A");
            params.put("firebase_reg_no", firebase_reg_no);
            params.put("name", g_first_name);
            params.put("google_id", g_id);
            params.put("country_id", PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("country_id", ""));
            if (!g_email.equals("") && !g_email.equals(null)) {
                params.put("email", g_email);
            }
            if (!g_photp.equals("") && !g_photp.equals(null)) {
                params.put("image", g_photp);
            }

            JSONObject jsonObject = MethodClass.Json_rpc_format(params);
            Log.e("parames", jsonObject.toString());


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    MethodClass.hideProgressDialog(LoginActivity.this);
                    Log.e("respLogin", response.toString());
                    try {
                        JSONObject resultResponce = MethodClass.get_result_from_webservice(LoginActivity.this, response);
                        if (resultResponce != null) {
                            if (resultResponce.getString("is_phone_verified").equals("Y")) {
                                String token = resultResponce.getString("token");
                                JSONObject userdata = resultResponce.getJSONObject("user");
                                String id = userdata.getString("id");
                                String name = userdata.getString("name");
                                String email = userdata.getString("email");
                                String profile_picture = userdata.getString("image");
                                String phone = userdata.getString("phone");

                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("token", token).commit();
                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("name", name).commit();
                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("user_id", id).commit();
                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("email", email).commit();
                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putBoolean("is_logged_in", true).commit();
                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("profile_pic", profile_picture).commit();
                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("phone_number", phone).commit();
                                String country_phone_code = userdata.getString("country_code");
                                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("country_phone_code", country_phone_code).commit();


                                if (getIntent().getExtras() != null) {
                                    if (getIntent().getStringExtra("type").equals("C")) {
                                        Intent intent = new Intent(LoginActivity.this, ShoppingCartActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                            } else {
                                fromSocial = "google";
                                social_id = g_id;
                                getPhone();
                            }
                        }
                    } catch (JSONException e) {
                        MethodClass.error_alert(LoginActivity.this);
                        e.printStackTrace();
                        Log.e("login_parce", e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MethodClass.hideProgressDialog(LoginActivity.this);
                    if (error.toString().contains("ConnectException")) {
                        MethodClass.network_error_alert(LoginActivity.this);
                    } else {
                        MethodClass.error_alert(LoginActivity.this);
                    }
                }
            }) {
                @Override
                public Map getHeaders() throws AuthFailureError {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("lang", "en"));
                    if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getBoolean("is_logged_in", false)) {
                        headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("token", ""));
                    }
                    Log.e("getHeaders: ", headers.toString());
                    return headers;
                }
            };
            MySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);

        } catch (Exception e) {
            //here google Exception handling function
            e.printStackTrace();
            Log.e("ERRORGoogl", e.toString());
        }
    }


    public void back(View view) {
        onBackPressed();
    }


    public void forgotPassword(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        if (getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("type").equals("C")) {
                intent.putExtra("type", "C");
            } else {
                intent.putExtra("type", "S");
            }
        } else {
            intent.putExtra("type", "S");
        }
        startActivity(intent);
    }


    private void getPhone() {
        Dialog dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.get_phone_popup);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        EditText phone_et = (EditText) dialog.findViewById(R.id.phone_et);
        country_code_spin_popup = (Spinner) dialog.findViewById(R.id.country_code_spin);
        Button submit = (Button) dialog.findViewById(R.id.submit);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        ImageView cancel1 = (ImageView) dialog.findViewById(R.id.cancel1);
        LinearLayout country_popup_click = (LinearLayout) dialog.findViewById(R.id.country_popup_click);
        getCountryCode();
        phone_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //checkPhone(phone_et);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPhoneOtp(phone_et);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        country_popup_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MethodClass.countryCodePopup(LoginActivity.this, cityArrayList, country_code_spin_popup);
            }
        });
        dialog.show();
    }

    public void checkPhone(EditText phone_et) {
        String server_url = getString(R.string.SERVER_URL) + "check-duplicate-phone";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", phone_et.getText().toString().trim());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try {
                    if (response.has("error")) {
                        JSONObject jsonObject = response.getJSONObject("error");
                        String meaning = jsonObject.getString("meaning");
                        phone_et.setError(meaning);
                        phone_et.requestFocus();
                        return;
                    }
                    phone_et.setTag("checked");
                } catch (JSONException e) {
                    MethodClass.error_alert(LoginActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.error_alert(LoginActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(request);
    }

    private void sendPhoneOtp(EditText phone_et) {
        if (phone_et.getText().toString().length() == 0) {
            phone_et.setError(getString(R.string.enterphone));
            phone_et.requestFocus();
            return;
        }
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "update-phone-social-login";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", phone_et.getText().toString().trim());
        params.put("country_code", cityArrayList.get(country_code_spin_popup.getSelectedItemPosition()).countryCode);
        if (fromSocial.equals("google")) {
            params.put("google_id", social_id);
        } else {
            params.put("facebook_id", social_id);
        }
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("jsonObject", jsonObject.toString());

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

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try {
                    MethodClass.hideProgressDialog(LoginActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(LoginActivity.this, response);
                    if (resultResponse != null) {
                        String otp = resultResponse.getString("otp");
                        Toast.makeText(LoginActivity.this, getString(R.string.otp_has_been_sent_to_your_mobile), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                        intent.putExtra("phone", phone_et.getText().toString().trim());
                        intent.putExtra("country_id", cityArrayList.get(country_code_spin_popup.getSelectedItemPosition()).countryCode);
                        intent.putExtra("code", otp);
                        if (getIntent().getExtras() != null) {
                            if (getIntent().getStringExtra("type").equals("C")) {
                                intent.putExtra("type", "C");
                            } else {
                                intent.putExtra("type", "S");
                            }
                        } else {
                            intent.putExtra("type", "S");
                        }
                        startActivity(intent);

                       /* LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(LoginActivity.this, DialogTypes.TYPE_SUCCESS)
                                .setTitle("Registration Success")
                                .setDescription("Please verify your account in the next step with the otp sent to your mobile and email")
                                .setPositiveText("Ok")
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                                        intent.putExtra("phone", phone_et.getText().toString().trim());
                                        intent.putExtra("country_id", cityArrayList.get(country_code_spin_popup.getSelectedItemPosition()).countryCode);
                                        intent.putExtra("code", otp);
                                        if (getIntent().getExtras() != null) {
                                            if (getIntent().getStringExtra("type").equals("C")) {
                                                intent.putExtra("type", "C");
                                            } else {
                                                intent.putExtra("type", "S");
                                            }
                                        } else {
                                            intent.putExtra("type", "S");
                                        }
                                        startActivity(intent);
                                    }
                                })
                                .build();
                        alertDialog.show();*/
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(LoginActivity.this);
                    MethodClass.error_alert(LoginActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(LoginActivity.this);
                MethodClass.error_alert(LoginActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(request);
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
                    MethodClass.hideProgressDialog(LoginActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(LoginActivity.this, response);
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

                        String country_id = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("country_id", "");
                        Log.e("country_id", country_id);
                        if (!fromSocial.equals("null")) {
                            CountryCodeAdapter countryCodeAdapter1 = new CountryCodeAdapter(LoginActivity.this, cityArrayList);
                            country_code_spin_popup.setAdapter(countryCodeAdapter1);
                            for (int i = 0; i < cityArrayList.size(); i++) {
                                MethodClass.CountryModelCode stringWithTag = cityArrayList.get(i);
                                if (Objects.equals(stringWithTag.tag, country_id)) {
                                    country_code_spin_popup.setSelection(i);
                                }
                            }
                        } else {
                            CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(LoginActivity.this, cityArrayList);
                            country_code_spin.setAdapter(countryCodeAdapter);
                            for (int i = 0; i < cityArrayList.size(); i++) {
                                MethodClass.CountryModelCode stringWithTag = cityArrayList.get(i);
                                if (Objects.equals(stringWithTag.tag, country_id)) {
                                    country_code_spin.setSelection(i);
                                }
                            }
                        }


                        //setUpGClient();
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(LoginActivity.this);
                    MethodClass.error_alert(LoginActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(LoginActivity.this);
                MethodClass.error_alert(LoginActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(request);
    }

    public void countryCodePopup(View view) {
        MethodClass.countryCodePopup(LoginActivity.this, cityArrayList, country_code_spin);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}