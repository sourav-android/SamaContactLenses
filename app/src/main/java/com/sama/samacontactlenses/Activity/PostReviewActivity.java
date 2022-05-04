package com.sama.samacontactlenses.Activity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.Helper.RequestManager;
import com.sama.samacontactlenses.R;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.ScaleRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE_URL;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class PostReviewActivity extends AppCompatActivity {

    private ImageView prd_img;
    private TextView pro_title_tv, category, price_tv;
    private ScaleRatingBar simpleRatingBar;
    private EditText desc_et;
    private LinearLayout image_upload,plus_lay;
    private String img_path="";
    private ImageView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.activity_post_review);
        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.post_review_only));
        MethodClass.setBottomFun(this);
        prd_img = findViewById(R.id.prd_img);
        pro_title_tv = findViewById(R.id.pro_title_tv);
        category = findViewById(R.id.category);
        price_tv = findViewById(R.id.price_tv);
        simpleRatingBar = findViewById(R.id.simpleRatingBar);
        desc_et = findViewById(R.id.desc_et);
        image_upload = findViewById(R.id.image_upload);
        plus_lay = findViewById(R.id.plus_lay);
        image_view = findViewById(R.id.image_view);
        getProduct();

        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickImage();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setMenu(this);
    }

    public void back(View view) {
        onBackPressed();
    }


    public void getProduct() {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "product-details";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("product_id", getIntent().getStringExtra("product_id"));

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("CATGOEY", new JSONObject(params).toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(PostReviewActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(PostReviewActivity.this, response);
                    if (resultResponse != null) {
                        JSONObject product = resultResponse.getJSONObject("product").getJSONObject("product_details");
                        JSONArray images = product.getJSONArray("images");
                        String image = images.getJSONObject(0).getString("image");
                        Picasso.get().load(PRODUCT_IMAGE_URL + image).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(prd_img);
                        pro_title_tv.setText(product.getJSONObject("product_by_language").getString("title"));
                        category.setText(product.getJSONObject("product_parent_category").getJSONObject("category").getJSONObject("category_by_language").getString("title"));
                        Double conv_fac = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(PostReviewActivity.this).getString("conv_factor", ""));
                        String curr_code = PreferenceManager.getDefaultSharedPreferences(PostReviewActivity.this).getString("currency_code", "AED");
                        price_tv.setText((Double.parseDouble(product.getString("price_without_power"))*conv_fac) + " "+curr_code);

                    }
                } catch (JSONException e) {
                    MethodClass.error_alert(PostReviewActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(PostReviewActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(PostReviewActivity.this);
                } else {
                    MethodClass.error_alert(PostReviewActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(PostReviewActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(PostReviewActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(PostReviewActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(PostReviewActivity.this).addToRequestQueue(jsonObjectRequest);
    }


    public void submit(View view) {

        float rating =simpleRatingBar.getRating();
        if (rating<1){
            Toast.makeText(this, getString(R.string.please_give_your_rating), Toast.LENGTH_SHORT).show();
            return;
        }
        String description=desc_et.getText().toString().trim();
        if (description.length()==0){
            desc_et.setError("Please enter your description");
            desc_et.requestFocus();
            return;
        }

       /* if (img_path.equals("") || img_path.equals("null") || img_path.equals(null)) {
            Toast.makeText(this, getString(R.string.please_select_image), Toast.LENGTH_SHORT).show();
            return;
        }*/
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        
        MethodClass.showProgressDialog(this);
        String server_url = getString(R.string.SERVER_URL) + "post-product-review";
        SimpleMultiPartRequest simpleMultiPartRequest = new SimpleMultiPartRequest(Request.Method.POST, server_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("onResponse: ", response);
                MethodClass.hideProgressDialog(PostReviewActivity.this);
                try {
                    JSONObject jsonObject = MethodClass.get_result_from_webservice(PostReviewActivity.this, new JSONObject(response));
                    if (jsonObject != null) {
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(PostReviewActivity.this, DialogTypes.TYPE_SUCCESS)
                                .setTitle("Post Review")
                                .setDescription("Review Successfully Post")
                                .setPositiveText(getString(R.string.ok))
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                        Intent intent = new Intent(PostReviewActivity.this, ProductDetailsActivity.class);
                                        intent.putExtra("product_id", getIntent().getStringExtra("product_id"));
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .build();
                        alertDialog.show();

                        }
                } catch (JSONException e) {
                    MethodClass.error_alert(PostReviewActivity.this);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(PostReviewActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(PostReviewActivity.this);
                } else {
                    MethodClass.error_alert(PostReviewActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(PostReviewActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(PostReviewActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(PostReviewActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        RequestQueue requestQueue = RequestManager.getnstance(PostReviewActivity.this);
        simpleMultiPartRequest.addMultipartParam("product_id", "text", getIntent().getStringExtra("product_id"));
        simpleMultiPartRequest.addMultipartParam("review_text", "text", description);
        simpleMultiPartRequest.addMultipartParam("review_stars", "text", String.valueOf(rating));
        if (!img_path.equals("") && !img_path.equals("null") && !img_path.equals(null)) {
            simpleMultiPartRequest.addFile("image", img_path);
        }
        Log.e("product_id",getIntent().getStringExtra("product_id"));
        Log.e("review_text",description);
        Log.e("review_stars", String.valueOf(rating));
        Log.e("image", img_path);

        simpleMultiPartRequest.setFixedStreamingMode(true);
        int socketTimeout = 100000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 3, 5);
        simpleMultiPartRequest.setRetryPolicy(policy);
        requestQueue.getCache().clear();
        requestQueue.add(simpleMultiPartRequest);
    }



    ////////////////////////Q_IMAGE FUNS////////////////////////////////
    private void clickImage() {
        //In this method sho popup to select which functionality to choose your profile pic
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.chose_from_library), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(PostReviewActivity.this);
        builder.setTitle(getString(R.string.post_review));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    ImagePicker.Companion.with(PostReviewActivity.this)
                            .cameraOnly()    //User can only capture image using Camera
                            .start();
                } else if (items[item].equals(getString(R.string.chose_from_library))) {
                    ImagePicker.Companion.with(PostReviewActivity.this)
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
            Log.e("image",img_path);
            File imgFile = new  File(img_path);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image_view.setImageBitmap(myBitmap);
                image_view.setVisibility(View.VISIBLE);
                plus_lay.setVisibility(View.GONE);
            }

            Log.e("requestCode", String.valueOf(requestCode));
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            image_view.setVisibility(View.GONE);
            plus_lay.setVisibility(View.VISIBLE);
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            image_view.setVisibility(View.GONE);
            plus_lay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }

}