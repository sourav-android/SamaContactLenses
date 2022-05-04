package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sama.samacontactlenses.Activity.CheckoutActivity;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sama.samacontactlenses.Common.Constant.ADDRESS_ID;
import static com.sama.samacontactlenses.Common.Constant.ADDRESS_LINE_1;
import static com.sama.samacontactlenses.Common.Constant.ADDRESS_LINE_2;
import static com.sama.samacontactlenses.Common.Constant.CITY;
import static com.sama.samacontactlenses.Common.Constant.COUNTRY_NAME;
import static com.sama.samacontactlenses.Common.Constant.FULL_NAME;
import static com.sama.samacontactlenses.Common.Constant.HEADING;
import static com.sama.samacontactlenses.Common.Constant.IS_DEFAULT;
import static com.sama.samacontactlenses.Common.Constant.PHONE;
import static com.sama.samacontactlenses.Common.Constant.SHIPPING_PRICE;
import static com.sama.samacontactlenses.Common.Constant.SHIPPNG_ALLOW;
import static com.sama.samacontactlenses.Common.Constant.SORT_NAME;
import static com.sama.samacontactlenses.Common.Constant.STATE;
import static com.sama.samacontactlenses.Helper.MethodClass.noFormat;

public class ShippingAddressAdapter extends RecyclerView.Adapter<ShippingAddressAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;
    int selectedPosition = 10000;
    boolean is_first=true;
    public ShippingAddressAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.address_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.name.setText(map.get(FULL_NAME));
        holder.heading.setText(map.get(HEADING));
        String full_address="";
        full_address=map.get(ADDRESS_LINE_1);
        full_address=full_address+", "+MethodClass.checkNull(map.get(ADDRESS_LINE_2));
        full_address=full_address+", "+MethodClass.checkNull(map.get(CITY));
        full_address=full_address+", "+MethodClass.checkNull(map.get(STATE));
        full_address=full_address+", "+MethodClass.checkNull(map.get(COUNTRY_NAME));
        holder.address.setText(full_address);
        holder.phone.setText(map.get(PHONE));
        if (is_first){
            if (map.get(IS_DEFAULT).equals("Y")){
                is_first=false;
                ((CheckoutActivity)activity).shippingAddressID=map.get(ADDRESS_ID);
                ((CheckoutActivity)activity).country_sortName=map.get(SORT_NAME);
                selectedPosition=position;
                if (!map.get(SORT_NAME).equals("AE")){
                    ((CheckoutActivity)activity).online_btn.performClick();
                    ((CheckoutActivity)activity).cod_btn.setVisibility(View.GONE);
                    ((CheckoutActivity)activity).cod_charge_tv.setVisibility(View.GONE);
                }else {
                    ((CheckoutActivity)activity).cod_btn.setVisibility(View.VISIBLE);
                    ((CheckoutActivity)activity).cod_charge_tv.setVisibility(View.VISIBLE);
                    ((CheckoutActivity)activity).checkAstigmatism();
                }
                if (((CheckoutActivity)activity).is_shipping_price.equals("Y")){
                    Double shipping_charge=Double.parseDouble(map.get(SHIPPING_PRICE))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(activity).getString("conv_factor",""));
                    Double totalAmount=Double.parseDouble(((CheckoutActivity)activity).total_amount)*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(activity).getString("conv_factor",""));
                    ((CheckoutActivity)activity).paybale_amount_tv.setText(noFormat((shipping_charge+totalAmount))+" "+PreferenceManager.getDefaultSharedPreferences(activity).getString("currency_code",""));
                    ((CheckoutActivity)activity).shipping_price_tv.setText(noFormat(shipping_charge)+" "+PreferenceManager.getDefaultSharedPreferences(activity).getString("currency_code",""));
                    ((CheckoutActivity)activity).shipping_price_lay.setVisibility(View.VISIBLE);
                }else {
                    ((CheckoutActivity)activity).shipping_price_lay.setVisibility(View.GONE);
                }
            }
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CheckoutActivity)activity).shippingAddressID=map.get(ADDRESS_ID);
                ((CheckoutActivity)activity).country_sortName=map.get(SORT_NAME);
                if (!map.get(SORT_NAME).equals("AE")){
                    ((CheckoutActivity)activity).online_btn.performClick();
                    ((CheckoutActivity)activity).cod_btn.setVisibility(View.GONE);
                    ((CheckoutActivity)activity).cod_charge_tv.setVisibility(View.GONE);
                }else {
                    ((CheckoutActivity)activity).cod_btn.setVisibility(View.VISIBLE);
                    ((CheckoutActivity)activity).cod_charge_tv.setVisibility(View.VISIBLE);
                    ((CheckoutActivity)activity).checkAstigmatism();
                }
                if (((CheckoutActivity)activity).is_shipping_price.equals("Y")){
                    Double shipping_charge=Double.parseDouble(map.get(SHIPPING_PRICE))*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(activity).getString("conv_factor",""));
                    Double totalAmount=Double.parseDouble(((CheckoutActivity)activity).total_amount)*Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(activity).getString("conv_factor",""));
                    ((CheckoutActivity)activity).paybale_amount_tv.setText(noFormat((shipping_charge+totalAmount))+" "+PreferenceManager.getDefaultSharedPreferences(activity).getString("currency_code",""));

                    ((CheckoutActivity)activity).shipping_price_tv.setText(noFormat(shipping_charge)+" "+PreferenceManager.getDefaultSharedPreferences(activity).getString("currency_code",""));
                    ((CheckoutActivity)activity).shipping_price_lay.setVisibility(View.VISIBLE);
                }else {
                    ((CheckoutActivity)activity).shipping_price_lay.setVisibility(View.GONE);
                }
                if (map.get(SHIPPNG_ALLOW).equals("N")) {
                    Toast.makeText(activity, activity.getString(R.string.country_not_allow), Toast.LENGTH_LONG).show();
                }
                    selectedPosition = position;
                    notifyDataSetChanged();
            }
        });

        if (selectedPosition == position) {
            holder.item.setBackground(activity.getDrawable(R.drawable.pink_strok));
        } else {
            holder.item.setBackground(activity.getDrawable(R.drawable.border_rounded_line));
        }
    }
    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,address,phone,heading;
        private LinearLayout item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            heading = itemView.findViewById(R.id.heading);
            address = itemView.findViewById(R.id.address);
            phone = itemView.findViewById(R.id.phone);
            item = itemView.findViewById(R.id.item);
        }
    }

}
