package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sama.samacontactlenses.Activity.HomeActivity;
import com.sama.samacontactlenses.Activity.OrderDetailsActivity;
import com.sama.samacontactlenses.Activity.SearchActivity;
import com.sama.samacontactlenses.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.sama.samacontactlenses.Common.Constant.CATEGORY_ID;
import static com.sama.samacontactlenses.Common.Constant.CRAETED_AT;
import static com.sama.samacontactlenses.Common.Constant.NOTIFICATION_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.NOTIICATION_TYPE;
import static com.sama.samacontactlenses.Common.Constant.ORDER_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;

public class NotifcationAdapter extends RecyclerView.Adapter<NotifcationAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;

    public NotifcationAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);
        holder.title.setText(map.get(PRODUCT_TITLE));
        holder.desc.setText(map.get(PRODUCT_DESC));
        Picasso.get().load(NOTIFICATION_IMAGE+map.get(PRODUCT_IMAGE)).placeholder(activity.getDrawable(R.drawable.logo_2)).error(activity.getDrawable(R.drawable.logo_2)).into(holder.image);
        try{
            DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date=dateFormat.parse(map.get(CRAETED_AT));
            holder.time.setText(android.text.format.DateFormat.format("dd, MMM yyyy",date));
        }catch (Exception e){

        }
        if (map.get(NOTIICATION_TYPE).equals("O")){
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, OrderDetailsActivity.class);
                    intent.putExtra("order_id",map.get(ORDER_ID));
                    activity.startActivity(intent);
                }
            });
        }
        if (map.get(NOTIICATION_TYPE).equals("C")){
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, SearchActivity.class);
                    intent.putExtra("category_id",map.get(CATEGORY_ID));
                    intent.putExtra("parent_id","");
                    intent.putExtra("key","");
                    intent.putExtra("sort","");
                    activity.startActivity(intent);
                }
            });
        }
        if (map.get(NOTIICATION_TYPE).equals("H")){
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView desc,title,time;
        private ImageView image;
        private LinearLayout item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            desc=itemView.findViewById(R.id.desc);
            time=itemView.findViewById(R.id.time);
            image=itemView.findViewById(R.id.image);
            item=itemView.findViewById(R.id.item);
        }
    }
}
