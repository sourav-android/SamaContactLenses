package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sama.samacontactlenses.Activity.ForgotPasswordActivity;
import com.sama.samacontactlenses.Activity.OrderHistoryActivity;
import com.sama.samacontactlenses.Activity.SignUpActivity;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.sama.samacontactlenses.Common.Constant.FLAG_IMAGE_URL;

public class CountryCodeAdapter extends BaseAdapter {
    Activity activity;
    LayoutInflater inflter;
    ArrayList<MethodClass.CountryModelCode> arrayList;

    public CountryCodeAdapter(Activity activity, ArrayList<MethodClass.CountryModelCode> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        inflter = (LayoutInflater.from(activity));
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }



    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.country_spin_lay, null);
        LinearLayout list_item = (LinearLayout) view.findViewById(R.id.list_item);
        TextView spin_text = (TextView) view.findViewById(R.id.spin_text);
        ImageView spin_image = (ImageView) view.findViewById(R.id.spin_image);
        String sortname=arrayList.get(i).countrySortName.toLowerCase()+".png";
        Picasso.get().load(FLAG_IMAGE_URL+sortname).placeholder(R.drawable.flag_dummy).error(R.drawable.flag_dummy).into(spin_image);
        spin_text.setText(arrayList.get(i).countryCode);

       /* list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getLocalClassName().equals("Activity.ForgotPasswordActivity")) {
                    ((ForgotPasswordActivity)activity).country_id=arrayList.get(i).countryCode;
                }else if (activity.getLocalClassName().equals("Activity.SignUpActivity")){
                    ((SignUpActivity)activity).country_id=arrayList.get(i).countryCode;
                }
            }
        });*/
        return view;
    }
}

