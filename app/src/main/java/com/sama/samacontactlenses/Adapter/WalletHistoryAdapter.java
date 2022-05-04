package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Activity.AddressActivity;
import com.sama.samacontactlenses.Activity.EditAddressActivity;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.ADDRESS_ID;
import static com.sama.samacontactlenses.Common.Constant.ADDRESS_LINE_1;
import static com.sama.samacontactlenses.Common.Constant.ADDRESS_LINE_2;
import static com.sama.samacontactlenses.Common.Constant.BALANCE;
import static com.sama.samacontactlenses.Common.Constant.BALANCE_TYPE;
import static com.sama.samacontactlenses.Common.Constant.CITY;
import static com.sama.samacontactlenses.Common.Constant.COUNTRY_NAME;
import static com.sama.samacontactlenses.Common.Constant.CRAETED_AT;
import static com.sama.samacontactlenses.Common.Constant.CURR_CODE;
import static com.sama.samacontactlenses.Common.Constant.DESC;
import static com.sama.samacontactlenses.Common.Constant.EMAIL_ADDRESS;
import static com.sama.samacontactlenses.Common.Constant.FULL_NAME;
import static com.sama.samacontactlenses.Common.Constant.HEADING;
import static com.sama.samacontactlenses.Common.Constant.IS_DEFAULT;
import static com.sama.samacontactlenses.Common.Constant.PHONE;
import static com.sama.samacontactlenses.Common.Constant.STATE;

public class WalletHistoryAdapter extends RecyclerView.Adapter<WalletHistoryAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;
    public WalletHistoryAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.wallet_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.desc.setText(map.get(DESC));
        if (map.get(BALANCE_TYPE).equals("OUT")){
            holder.icon_text.setText("-");
            holder.icon_text.setTextColor(activity.getResources().getColor(R.color.red));
            holder.balence.setText("- "+map.get(BALANCE)+" "+map.get(CURR_CODE));
            holder.balence.setTextColor(activity.getResources().getColor(R.color.red));
        }else {
            holder.icon_text.setText("+");
            holder.icon_text.setTextColor(activity.getResources().getColor(R.color.green_dark));
            holder.balence.setText("+ "+map.get(BALANCE)+" "+map.get(CURR_CODE));
            holder.balence.setTextColor(activity.getResources().getColor(R.color.green_dark));
        }

        try{
            DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date=dateFormat.parse(map.get(CRAETED_AT));
            holder.time.setText(android.text.format.DateFormat.format("dd, MMM yyyy",date));
        }catch (Exception e){

        }

    }
    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView icon_text,desc,time,balence;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon_text = itemView.findViewById(R.id.icon_text);
            desc = itemView.findViewById(R.id.desc);
            time = itemView.findViewById(R.id.time);
            balence = itemView.findViewById(R.id.balence);
        }
    }





}
