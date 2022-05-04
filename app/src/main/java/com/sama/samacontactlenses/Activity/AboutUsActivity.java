package com.sama.samacontactlenses.Activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Adapter.FAQAdapter;
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

import static com.sama.samacontactlenses.Common.Constant.ABOUT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.ANSWER;
import static com.sama.samacontactlenses.Common.Constant.QUETION;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class AboutUsActivity extends AppCompatActivity {


    private ImageView image,imaGe1,imaGe2;
    private TextView head_1,desc_1;
    private TextView head_2,desc_2;
    private TextView head_3,desc_3;
    ScrollView scrollView;
    CardView card1,card2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_about_us);
        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.about_us));

        //image=findViewById(R.id.image);
        imaGe1=findViewById(R.id.imaGe1);
        imaGe2=findViewById(R.id.imaGe2);
        card1=findViewById(R.id.card1);
        card2=findViewById(R.id.card2);
        scrollView=findViewById(R.id.scrollView);

        head_1=findViewById(R.id.head_1);
        head_2=findViewById(R.id.head_2);
        //head_3=findViewById(R.id.head_3);
        desc_1=findViewById(R.id.desc_1);
        desc_2=findViewById(R.id.desc_2);
        //desc_3=findViewById(R.id.desc_3);
        getData();
    }

    public void back(View view) {
        onBackPressed();
    }

   public void getData() {
       MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "view-about-us";
        Log.e("server_url", server_url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, server_url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());

                try {
                    MethodClass.hideProgressDialog(AboutUsActivity.this);
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(AboutUsActivity.this, response);
                    if (resultResponse != null) {
                        JSONObject about_content = resultResponse.getJSONObject("about_content");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            head_1.setText(Html.fromHtml(about_content.getString("title_1_title"), Html.FROM_HTML_MODE_COMPACT));
                            head_2.setText(Html.fromHtml(about_content.getString("title_2_title"), Html.FROM_HTML_MODE_COMPACT));
                            //head_3.setText(Html.fromHtml(about_content.getString("title_3_title"), Html.FROM_HTML_MODE_COMPACT));
                            desc_1.setText(Html.fromHtml(about_content.getString("title_1_desc"), Html.FROM_HTML_MODE_COMPACT));
                            desc_2.setText(Html.fromHtml(about_content.getString("title_2_desc"), Html.FROM_HTML_MODE_COMPACT));
                            //desc_3.setText(Html.fromHtml(about_content.getString("title_3_desc"), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            head_1.setText(Html.fromHtml(about_content.getString("title_1_title")));
                            head_2.setText(Html.fromHtml(about_content.getString("title_2_title")));
                            //head_3.setText(Html.fromHtml(about_content.getString("title_3_title")));
                            desc_1.setText(Html.fromHtml(about_content.getString("title_1_desc")));
                            desc_2.setText(Html.fromHtml(about_content.getString("title_2_desc")));
                            //desc_3.setText(Html.fromHtml(about_content.getString("title_3_desc")));
                        }

                        String image_str=ABOUT_IMAGE+about_content.getString("image");
                        String image_str2=ABOUT_IMAGE+about_content.getString("image_2");
                        Picasso.get().load(image_str).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(imaGe1);
                        Picasso.get().load(image_str2).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(imaGe2);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    MethodClass.hideProgressDialog(AboutUsActivity.this);
                    MethodClass.error_alert(AboutUsActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(AboutUsActivity.this);
                MethodClass.error_alert(AboutUsActivity.this);
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(AboutUsActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(AboutUsActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(AboutUsActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(AboutUsActivity.this).addToRequestQueue(request);
    }

    public void showMore(View view){
        scrollView.scrollTo(0,0);
        imaGe2.requestFocus();
        card1.setVisibility(View.GONE);
        imaGe1.setVisibility(View.GONE);
        card2.setVisibility(View.VISIBLE);
        imaGe2.setVisibility(View.VISIBLE);
    }
    public void showLess(View view){
        scrollView.scrollTo(0,0);
        imaGe1.requestFocus();
        card1.setVisibility(View.VISIBLE);
        imaGe1.setVisibility(View.VISIBLE);
        card2.setVisibility(View.GONE);
        imaGe2.setVisibility(View.GONE);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}