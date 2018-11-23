package com.example.winnipeginfo;

import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CommunityRecyclerViewAdapter extends RecyclerView.Adapter<CommunityRecyclerViewAdapter.CommunityHolder>  {

    List<Community> communities;

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;
    private  static final String TAG="TEST";
    public class CommunityHolder extends RecyclerView.ViewHolder {
        public TextView name, address;

        public CommunityHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tvCommunityName);
            address = (TextView) view.findViewById(R.id.tvCommunityAddress);
        }
    }

    public CommunityRecyclerViewAdapter(List<Community> communities) {
        this.communities = communities;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    @Override
    public CommunityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_list_row, parent, false);
        return new CommunityHolder(itemView);
    }


    @Override
    public void onBindViewHolder(CommunityHolder holder, int position) {
        Community community = communities.get(position);
        holder.name.setText(community.getName());
        holder.address.setText(community.getAddress());

    }

}
