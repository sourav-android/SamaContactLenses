package com.sama.samacontactlenses.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Adapter.CurrencyPopUpAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sama.samacontactlenses.Common.Constant.CURRENCY;
import static com.sama.samacontactlenses.Common.Constant.CURRENCY_CONVERSION;
import static com.sama.samacontactlenses.Common.Constant.CURRENCY_ID;
import static com.sama.samacontactlenses.Common.Constant.USER_IMAGE_URL;
import static com.sama.samacontactlenses.Helper.MethodClass.Userlogout;

public class UserActivity extends AppCompatActivity {
    private MeowBottomNavigation bottomNavigation;
    private TextView headerName, name_tv, email_tv,currency_text;
    private CircleImageView user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_user);
        MethodClass.setBottomFun(UserActivity.this);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        user_image = findViewById(R.id.user_image);
        headerName = findViewById(R.id.title);
        name_tv = findViewById(R.id.name_tv);
        email_tv = findViewById(R.id.email_tv);
        currency_text = findViewById(R.id.currency_text);
        headerName.setText(getString(R.string.user_account));
        currency_text.setText(getString(R.string.curr) + " : "+PreferenceManager.getDefaultSharedPreferences(this).getString("currency_code",""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String user_image_str = USER_IMAGE_URL + PreferenceManager.getDefaultSharedPreferences(this).getString("profile_pic", "");
        Picasso.get().load(user_image_str).placeholder(R.drawable.logo_2).error(R.drawable.logo_2).into(user_image);
        String user_name = PreferenceManager.getDefaultSharedPreferences(this).getString("name", "");
        String user_email = PreferenceManager.getDefaultSharedPreferences(this).getString("email", "");
        name_tv.setText(user_name);
        email_tv.setText(user_email);
        MethodClass.setMenu(this);
        MethodClass.setBottomFun2(this, bottomNavigation);
    }

    public void back(View view) {
        onBackPressed();
    }

    public void address(View view) {
        Intent intent = new Intent(this, AddressActivity.class);
        startActivity(intent);
    }
    public void wallet(View view) {
        Intent intent=new Intent(this,WalletHistoryActivity.class);
        startActivity(intent);
    }
    public void orderHis(View view) {
        Intent intent = new Intent(this, OrderHistoryActivity.class);
        startActivity(intent);
    }
    public void currency(View view) {
        get_currency_popup(currency_text,UserActivity.this);
    }

    public void userProfule(View view) {
        Intent intent = new Intent(this, EditUserActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(UserActivity.this, DialogTypes.TYPE_WARNING)
                .setTitle(getString(R.string.log_out))
                .setDescription(getString(R.string.do_you_want_to_log_out))
                .setPositiveText(getString(R.string.okay))
                .setNegativeText(getString(R.string.cancel))
                .setPositiveListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                        lottieAlertDialog.dismiss();
                        Userlogout(UserActivity.this);
                    }
                }).setNegativeListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                        lottieAlertDialog.dismiss();
                    }
                })
                .build();
        alertDialog.show();
    }

    public void get_currency_popup(TextView currency_text, Activity activity) {
        MethodClass.showProgressDialog(activity);
        String server_url = activity.getString(R.string.SERVER_URL) + "currency-list";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lang", PreferenceManager.getDefaultSharedPreferences(activity).getString("lang", "en"));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    MethodClass.hideProgressDialog(activity);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(activity, response);
                    if (resultResponse != null) {

                        JSONArray currency = resultResponse.getJSONArray("currency");
                        Dialog dialog = new Dialog(activity);
                        dialog.setContentView(R.layout.currency_popup);
                        dialog.setTitle(activity.getString(R.string.select_currency));
                        Window window = dialog.getWindow();
                        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        RecyclerView recy_view=dialog.findViewById(R.id.recy_view);
                        ArrayList<HashMap<String,String>> arrayList=new ArrayList<>();
                        for (int i = 0; i < currency.length(); i++) {
                            HashMap<String ,String > hashMap=new HashMap<>();
                            hashMap.put(CURRENCY_ID,currency.getJSONObject(i).getString("id"));
                            hashMap.put(CURRENCY,currency.getJSONObject(i).getString("currency_code"));
                            hashMap.put(CURRENCY_CONVERSION,currency.getJSONObject(i).getJSONObject("currency_conversion").getString("conv_factor"));
                            arrayList.add(hashMap);
                        }
                        CurrencyPopUpAdapter currencyPopUpAdapter=new CurrencyPopUpAdapter(dialog,currency_text,activity,arrayList);
                        recy_view.setAdapter(currencyPopUpAdapter);
                        recy_view.setFocusable(false);
                        dialog.show();
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(activity);
                    MethodClass.error_alert(activity);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(activity);
                MethodClass.error_alert(activity);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(activity).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(activity).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(activity).addToRequestQueue(request);

    }

    public void change_currency(String id,Activity activity) {
        MethodClass.showProgressDialog(activity);
        String server_url = activity.getString(R.string.SERVER_URL) + "update-currency";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("currency_id", id);
        Log.e("params",MethodClass.Json_rpc_format(params).toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, MethodClass.Json_rpc_format(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    MethodClass.hideProgressDialog(activity);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(activity, response);
                    if (resultResponse != null) {
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_SUCCESS)
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
                                Intent I = new Intent(UserActivity.this,HomeActivity.class);
                                I.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(I);
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(activity);
                    MethodClass.error_alert(activity);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(activity);
                MethodClass.error_alert(activity);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(activity).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(activity).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(activity).addToRequestQueue(request);

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}