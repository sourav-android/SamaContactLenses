package com.sama.samacontactlenses.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sama.samacontactlenses.Adapter.FAQAdapter;
import com.sama.samacontactlenses.Adapter.RecentOrderHistoryAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.CREATE_DATE;
import static com.sama.samacontactlenses.Common.Constant.CURR_CODE;
import static com.sama.samacontactlenses.Common.Constant.ORDER_ID;
import static com.sama.samacontactlenses.Common.Constant.ORDER_NO;
import static com.sama.samacontactlenses.Common.Constant.PAYMENT_MODE;
import static com.sama.samacontactlenses.Common.Constant.STATUS;
import static com.sama.samacontactlenses.Common.Constant.TOTAL_ITEM;
import static com.sama.samacontactlenses.Common.Constant.TOTAL_PRICE;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;

public class WalletActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.content_wallet);
        TextView title=findViewById(R.id.title);
        title.setText(getString(R.string.curr));
        wallet();
    }

    public void back(View view) {
        onBackPressed();
    }
    
    private void wallet(){
        MaterialCardView e_m = findViewById(R.id.e_m);
        MaterialCardView o_r = findViewById(R.id.o_r);
        MaterialCardView raksur = findViewById(R.id.raksur);
        MaterialCardView k_d = findViewById(R.id.k_d);
        MaterialCardView b_h_d = findViewById(R.id.b_h_d);
        MaterialCardView u_s_d = findViewById(R.id.u_s_d);

        e_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WalletActivity.this,WalletHistoryActivity.class);
                intent.putExtra("currency_code","AED");
                startActivity(intent);
            }
        });

        o_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WalletActivity.this,WalletHistoryActivity.class);
                intent.putExtra("currency_code","OMR");
                startActivity(intent);
            }
        });

        raksur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WalletActivity.this,WalletHistoryActivity.class);
                intent.putExtra("currency_code","SAR");
                startActivity(intent);
            }
        });

        k_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WalletActivity.this,WalletHistoryActivity.class);
                intent.putExtra("currency_code","KWD");
                startActivity(intent);
            }
        });
        b_h_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WalletActivity.this,WalletHistoryActivity.class);
                intent.putExtra("currency_code","BHD");
                startActivity(intent);
            }
        });
        u_s_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WalletActivity.this,WalletHistoryActivity.class);
                intent.putExtra("currency_code","USD");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}