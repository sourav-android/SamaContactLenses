package com.sama.samacontactlenses.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class PasswordResetActivity extends AppCompatActivity {
    private EditText pass, confpass;
    private Button button1,button2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_password_reset);
        pass = findViewById(R.id.pass);
        confpass = findViewById(R.id.confpass);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        
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
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confpass.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    confpass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confpass.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    confpass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    confpass.setTransformationMethod(new HideReturnsTransformationMethod());
                }
            }
        });

    }

    public void submit(View view) {


        if (pass.getText().toString().length() == 0) {
            pass.setError(getString(R.string.enterpas));
            pass.requestFocus();
            return;
        }
        if (!confpass.getText().toString().trim().equals(pass.getText().toString().trim())) {
            confpass.setError(getString(R.string.confpasswrr));
            confpass.requestFocus();
            return;
        }

        if (!isNetworkConnected(PasswordResetActivity.this)) {
            MethodClass.network_error_alert(PasswordResetActivity.this);
            return;
        }


        MethodClass.showProgressDialog(PasswordResetActivity.this);
        String server_url = getString(R.string.SERVER_URL) + "update-pass-user";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", getIntent().getStringExtra("phone"));
        params.put("password", confpass.getText().toString().trim());
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("parames", jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(PasswordResetActivity.this);
                Log.e("respLogin", response.toString());
                try {
                    JSONObject resultResponce = MethodClass.get_result_from_webservice(PasswordResetActivity.this, response);
                    if (resultResponce != null) {

                        String message=resultResponce.getString("message");
                        String meaning=resultResponce.getString("meaning");
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(PasswordResetActivity.this, DialogTypes.TYPE_SUCCESS)
                                .setTitle(message)
                                .setDescription(meaning)
                                .setPositiveText("Ok")
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                        Intent intent = new Intent(PasswordResetActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .build();
                        alertDialog.show();

                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(PasswordResetActivity.this);
                    e.printStackTrace();
                    Log.e("login_parce", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(PasswordResetActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(PasswordResetActivity.this);
                } else {
                    MethodClass.error_alert(PasswordResetActivity.this);
                }
            }
        }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(PasswordResetActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(PasswordResetActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(PasswordResetActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(PasswordResetActivity.this).addToRequestQueue(jsonObjectRequest);
    }


    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }

}