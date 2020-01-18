package com.smarifrahman.outletcreateui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smarifrahman.outletcreateui.R;

import java.util.ArrayList;
import java.util.List;

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.MyViewHolder> {
    private static final String TAG = "SearchViewAdapter";

    private Context mContext;
    private List<String> categories;

    public SearchViewAdapter(Context mContext, List<String> categories) {
        this.mContext = mContext;
        this.categories = categories;
        Log.d(TAG, "SearchViewAdapter: "+categories.size());
        Log.d(TAG, "SearchViewAdapter: list: "+categories);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.spinner_item_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.searcitem.setText(categories.get(position));
        Log.d(TAG, "onBindViewHolder: "+categories.get(position));

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView searcitem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            searcitem = itemView.findViewById(R.id.search_item_tv);
        }
    }
}
