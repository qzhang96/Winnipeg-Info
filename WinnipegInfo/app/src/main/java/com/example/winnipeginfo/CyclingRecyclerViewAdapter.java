package com.example.winnipeginfo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CyclingRecyclerViewAdapter extends RecyclerView.Adapter<CyclingRecyclerViewAdapter.CyclingHolder>  {

    List<Cycling> cyclings;

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;
    private  static final String TAG="TEST";
    public class CyclingHolder extends RecyclerView.ViewHolder {
        public TextView id, st_name, location,type,city_area,ward,nbhd;

        public CyclingHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.tvCyclingID);
            st_name = (TextView) view.findViewById(R.id.tvCyclingStName);
            location=(TextView)view.findViewById(R.id.tvCyclingLocation);
            type=(TextView)view.findViewById(R.id.tvCyclingType);
            city_area=(TextView)view.findViewById(R.id.tvCyclingCityArea);
            ward=(TextView)view.findViewById(R.id.tvCyclingWard);
            nbhd=(TextView)view.findViewById(R.id.tvCyclingNBHD);

        }
    }

    public CyclingRecyclerViewAdapter(List<Cycling> cyclings) {
        this.cyclings= cyclings;
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
        return cyclings.size();
    }

    @Override
    public CyclingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cyclings_list_row, parent, false);
        return new CyclingHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CyclingHolder holder, int position) {
        Cycling cycling=cyclings.get(position);
        holder.id.setText("ID: "+cycling.getId());
        holder.st_name.setText("ST. Name :"+cycling.getSt_name());
        holder.location.setText("Location: "+cycling.getLocation());
        holder.type.setText("Type: "+cycling.getType());
        holder.ward.setText("Ward: "+cycling.getWard());
        holder.nbhd.setText("Neighbourhood: "+cycling.getNbhd());
    }




}
