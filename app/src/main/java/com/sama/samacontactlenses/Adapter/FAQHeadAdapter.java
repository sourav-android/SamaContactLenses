package com.sama.samacontactlenses.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sama.samacontactlenses.Common.Constant.ANSWER;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY_TITLE;
import static com.sama.samacontactlenses.Common.Constant.DESC;
import static com.sama.samacontactlenses.Common.Constant.QUETION;

public class FAQHeadAdapter extends RecyclerView.Adapter<FAQHeadAdapter.ViewHolder> {
    Activity activity;
    public ArrayList<HashMap<String, String>> map_list;
    public FAQHeadAdapter(Activity activity, ArrayList<HashMap<String, String>> map_list) {
        this.activity = activity;
        this.map_list = map_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.faq_head_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final HashMap<String, String> map = map_list.get(position);

        holder.cat.setText(map.get(CATEGORY_TITLE));
        String arrayData=map.get(DESC);
        try {
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
            JSONArray faq = new JSONArray(arrayData);
            for (int i = 0; i < faq.length(); i++) {
                JSONObject object = faq.getJSONObject(i);
                String faq_ques = object.getJSONObject("faq_details_by_language").getString("faq_ques");
                String faq_answer = object.getJSONObject("faq_details_by_language").getString("faq_answer");
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(QUETION, faq_ques);
                hashMap.put(ANSWER, faq_answer);
                arrayList.add(hashMap);
            }
            FAQAdapter adapter = new FAQAdapter(activity, arrayList);
            holder.recy_view.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public int getItemCount() {
        return map_list == null ? 0 : map_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cat;
        private RecyclerView recy_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cat = (TextView) itemView.findViewById(R.id.cat);
            recy_view = (RecyclerView) itemView.findViewById(R.id.recy_view);

        }
    }
}
