package com.sama.samacontactlenses.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.SimpleMultiPartRequest;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
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
import com.sama.samacontactlenses.Helper.RequestManager;
import com.sama.samacontactlenses.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sama.samacontactlenses.Common.Constant.USER_IMAGE_URL;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class EditUserActivity extends AppCompatActivity {
    public static Activity activity;
    private EditText name, email, phone;
    private Spinner country_code_spin;
    private Button submit;
    private TextView name_tv, email_tv;
    private CircleImageView image_view;
    String country_code = "";
    private String img_path = "";
    private ImageView edit_pic;
    private EditText pass, confpass;
    private Button button1, button2;

    ArrayList<MethodClass.CountryModelCode> countryCodeArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_edit_user);
        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.edit_profile));
        MethodClass.setBottomFun(this);
        activity=EditUserActivity.this;
        country_code_spin = findViewById(R.id.country_code_spin);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        submit = findViewById(R.id.submit);
        name_tv = findViewById(R.id.name_tv);
        email_tv = findViewById(R.id.email_tv);
        image_view = findViewById(R.id.user_image);
        edit_pic = findViewById(R.id.edit_pic);
        pass = findViewById(R.id.pass);
        confpass = findViewById(R.id.confpass);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_user();
            }
        });

        edit_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickImage();
            }
        });
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
        getUser();

    }

    public void update_user() {
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

        if (pass.getText().toString().trim().length()!=0 || confpass.getText().toString().trim().length()!=0) {
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
        }

        MethodClass.showProgressDialog(this);
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
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "user-update";
        Log.e("server_url", server_url);
        SimpleMultiPartRequest simpleMultiPartRequest = new SimpleMultiPartRequest(Request.Method.POST, server_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("onResponse: ", response);
                MethodClass.hideProgressDialog(EditUserActivity.this);
                try {
                    JSONObject jsonObject = MethodClass.get_result_from_webservice(EditUserActivity.this, new JSONObject(response));
                    if (jsonObject != null) {
                        if (jsonObject.getString("is_phone_verified").equals("Y")) {
                            Toast.makeText(EditUserActivity.this, getString(R.string.otp_has_been_sent_to_your_mobile), Toast.LENGTH_SHORT).show();
                            Intent inten = new Intent(EditUserActivity.this, UserPhoneVerifyActivity.class);
                            inten.putExtra("phone",jsonObject.getString("phone"));
                            inten.putExtra("country_id",jsonObject.getString("country_code"));
                            inten.putExtra("code",jsonObject.getString("otp"));
                            startActivity(inten);
                        } else {
                            String message = jsonObject.getString("message");
                            String meaning = jsonObject.getString("meaning");
                            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(EditUserActivity.this, DialogTypes.TYPE_SUCCESS)
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
                                    getUser();
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(EditUserActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(EditUserActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(EditUserActivity.this);
                } else {
                    MethodClass.error_alert(EditUserActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        RequestQueue requestQueue = RequestManager.getnstance(EditUserActivity.this);
        simpleMultiPartRequest.addMultipartParam("name", "text", name.getText().toString().trim());
        simpleMultiPartRequest.addMultipartParam("email", "text", email.getText().toString().trim());
        simpleMultiPartRequest.addMultipartParam("phone", "text", phone.getText().toString().trim());
        simpleMultiPartRequest.addMultipartParam("country_code", "text", countryCodeArrayList.get(country_code_spin.getSelectedItemPosition()).countryCode);
        if (!img_path.equals("") && !img_path.equals("null") && !img_path.equals(null)) {
            simpleMultiPartRequest.addFile("image", img_path);
        }
        if (pass.getText().toString().trim().length()!=0 || confpass.getText().toString().trim().length()!=0) {
            simpleMultiPartRequest.addMultipartParam("new_password", "text", pass.getText().toString().trim());
            simpleMultiPartRequest.addMultipartParam("confirm_password", "text", confpass.getText().toString().trim());
        }
        Log.e("image", img_path);

        simpleMultiPartRequest.setFixedStreamingMode(true);
        int socketTimeout = 100000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 3, 5);
        simpleMultiPartRequest.setRetryPolicy(policy);
        requestQueue.getCache().clear();
        requestQueue.add(simpleMultiPartRequest);
    }

    public void getCountryCode() {
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "get-countries-code";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    MethodClass.hideProgressDialog(EditUserActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(EditUserActivity.this, response);
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
                        CountryCodeAdapter countryCodeAdapter = new CountryCodeAdapter(EditUserActivity.this, countryCodeArrayList);
                        country_code_spin.setAdapter(countryCodeAdapter);

                        for (int i = 0; i < countryCodeArrayList.size(); i++) {
                            MethodClass.CountryModelCode stringWithTag = countryCodeArrayList.get(i);
                            if (Objects.equals(stringWithTag.countryCode, country_code)) {
                                country_code_spin.setSelection(i);
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(EditUserActivity.this);
                    MethodClass.error_alert(EditUserActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(EditUserActivity.this);
                MethodClass.error_alert(EditUserActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(EditUserActivity.this).addToRequestQueue(request);
    }

    public void getUser() {
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "edit-profile";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    MethodClass.hideProgressDialog(EditUserActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(EditUserActivity.this, response);
                    if (resultResponse != null) {
                        name_tv.setText(resultResponse.getString("name"));
                        name.setText(resultResponse.getString("name"));
                        phone.setText(resultResponse.getString("phone"));
                        email_tv.setText(MethodClass.checkNull(resultResponse.getString("email")));
                        email.setText(MethodClass.checkNull(resultResponse.getString("email")));
                        String image = resultResponse.getString("image");
                        String user_image_str = USER_IMAGE_URL + image;
                        Picasso.get().load(user_image_str).placeholder(R.drawable.logo_2).error(R.drawable.logo_2).into(image_view);
                        country_code = resultResponse.getString("country_code");

                        PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).edit().putString("name", resultResponse.getString("name")).commit();
                        PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).edit().putString("email", resultResponse.getString("email")).commit();
                        PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).edit().putString("profile_pic", image).commit();
                        PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).edit().putString("phone_number", resultResponse.getString("phone")).commit();
                        PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).edit().putString("country_phone_code", country_code).commit();


                        getCountryCode();
                        if (!phone.getText().toString().trim().equals(resultResponse.getString("phone"))){
                            phone.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    checkPhone(phone);
                                }
                            });

                        }

                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(EditUserActivity.this);
                    MethodClass.error_alert(EditUserActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(EditUserActivity.this);
                MethodClass.error_alert(EditUserActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(EditUserActivity.this).addToRequestQueue(request);
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
                    MethodClass.error_alert(EditUserActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.error_alert(EditUserActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(EditUserActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(EditUserActivity.this).addToRequestQueue(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setMenu(this);
    }

    public void back(View view) {
        onBackPressed();
    }

    ////////////////////////Q_IMAGE FUNS////////////////////////////////
    private void clickImage() {
        //In this method sho popup to select which functionality to choose your profile pic
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.chose_from_library), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserActivity.this);
        builder.setTitle(getString(R.string.post_review));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    ImagePicker.Companion.with(EditUserActivity.this)
                            .cameraOnly()    //User can only capture image using Camera
                            .start();
                } else if (items[item].equals(getString(R.string.chose_from_library))) {
                    ImagePicker.Companion.with(EditUserActivity.this)
                            .galleryOnly()    //User can only capture image using Camera
                            .start();

                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();//here show popup


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            img_path = MethodClass.getRightAngleImage(ImagePicker.Companion.getFilePath(data));
            Log.e("image", img_path);
            File imgFile = new File(img_path);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image_view.setImageBitmap(myBitmap);
            }
            Log.e("requestCode", String.valueOf(requestCode));
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            image_view.setVisibility(View.GONE);
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            image_view.setVisibility(View.GONE);
        }
    }

    public void countryCodePopup(View view) {
        MethodClass.countryCodePopup(EditUserActivity.this,countryCodeArrayList,country_code_spin);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }

}