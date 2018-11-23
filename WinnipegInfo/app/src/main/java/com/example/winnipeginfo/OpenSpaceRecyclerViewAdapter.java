package com.example.winnipeginfo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class OpenSpaceRecyclerViewAdapter extends RecyclerView.Adapter<OpenSpaceRecyclerViewAdapter.OpenSpaceHolder> {
        List<OpenSpace> openSpaces;
        static final int TYPE_HEADER = 0;
        static final int TYPE_CELL = 1;
        private  static final String TAG="TEST";
        public class OpenSpaceHolder extends RecyclerView.ViewHolder {
        public TextView parkID, park_name, location, category, district, nbhd,ward,area_ha,water_area,land_area;

        public OpenSpaceHolder(View view) {
            super(view);
            parkID=(TextView) view.findViewById(R.id.tvOpenSpaceParkID);
            park_name=(TextView)view.findViewById(R.id.tvOpenSpaceParkName);
            location=(TextView)view.findViewById(R.id.tvOpenSpaceLocation);
            category=(TextView)view.findViewById(R.id.tvOpenSpaceCategory);
            district=(TextView)view.findViewById(R.id.tvOpenSpaceDistrict);
            nbhd=(TextView)view.findViewById(R.id.tvOpenSpaceNBHD);
            ward=(TextView)view.findViewById(R.id.tvOpenSpaceWard);
            area_ha=(TextView)view.findViewById(R.id.tvOpenSpaceAreaHa);
            water_area=(TextView)view.findViewById(R.id.tvOpenSpaceWaterArea);
            land_area=(TextView)view.findViewById(R.id.tvOpenSpaceLandArea);

            }
        }
        public OpenSpaceRecyclerViewAdapter(List<OpenSpace> openSpaces) {
            this.openSpaces = openSpaces;
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
            return openSpaces.size();
        }
        @Override
        public OpenSpaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.open_space_list_row, parent, false);
            return new OpenSpaceHolder(itemView);
        }

    @Override
    public void onBindViewHolder(OpenSpaceHolder holder, int position) {
       OpenSpace openSpace= openSpaces.get(position);
        holder.parkID.setText("ID: "+openSpace.getPark_id());
        holder.park_name.setText("Park: Name: "+openSpace.getPark_name());
        holder.location.setText("Location: "+openSpace.getLocation());
        holder.category.setText("Category: "+openSpace.getCategory());;
        holder.district.setText("District: "+openSpace.getDistrict());
        holder.nbhd.setText("Neighbourhood: "+openSpace.getNbhd());
        holder.ward.setText("Ward: "+openSpace.getWard());
        holder.area_ha.setText("Area Ha: "+openSpace.getArea_ha());
        holder.water_area.setText("Water Area: "+openSpace.getWater_area());
        holder.land_area.setText("Land Area: "+openSpace.getLand_area());
    }
}
