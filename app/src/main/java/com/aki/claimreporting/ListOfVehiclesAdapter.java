package com.aki.claimreporting;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListOfVehiclesAdapter extends ArrayAdapter<VehicleselectClaimResponse> {

    ArrayList<VehicleselectClaimResponse> listOfClaimVehicles;
    Activity context;

    public ListOfVehiclesAdapter(@NonNull Activity context, ArrayList<VehicleselectClaimResponse> listOfClaimVehicles) {
        super(context, R.layout.claim_vehicle_details_layout);
        this.listOfClaimVehicles = listOfClaimVehicles;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ListOfVehiclesAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.claim_vehicle_details_layout, parent, false);
            viewHolder.vehicleName = convertView.findViewById(R.id.vehicleName);
            viewHolder.vehicleNumber = convertView.findViewById(R.id.vehicleNumber);
            viewHolder.addVehiclesBtn = convertView.findViewById(R.id.addVehiclesBtn);
            viewHolder.addVehiclesLayout = convertView.findViewById(R.id.addVehiclesView);
            viewHolder.vehicleDetailsLayout = convertView.findViewById(R.id.vehicleDetailsView);
            //result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ListOfVehiclesAdapter.ViewHolder) convertView.getTag();
            //result=convertView;
        }
        viewHolder.addVehiclesLayout.setVisibility(View.GONE);
        viewHolder.vehicleDetailsLayout.setVisibility(View.GONE);
        if (listOfClaimVehicles.size() == 0) {
            viewHolder.addVehiclesLayout.setVisibility(View.VISIBLE);
            viewHolder.addVehiclesBtn.setOnClickListener(onClickAddVehicles ->
                    context.startActivity(new Intent(context, AddVehicle.class)));
        } else {
            VehicleselectClaimResponse response = listOfClaimVehicles.get(position);
            viewHolder.vehicleDetailsLayout.setVisibility(View.VISIBLE);
            viewHolder.vehicleNumber.setText(response.getRegistrationNo());
            String vehicleName = response.getVehicleMake() + "-" + response.getVehicleModel();
            viewHolder.vehicleName.setText(vehicleName);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return (listOfClaimVehicles.size() == 0) ? 1 : listOfClaimVehicles.size();
    }

    private static class ViewHolder {
        LinearLayout addVehiclesLayout, vehicleDetailsLayout;
        Button addVehiclesBtn;
        TextView vehicleNumber;
        TextView vehicleName;
    }
}
