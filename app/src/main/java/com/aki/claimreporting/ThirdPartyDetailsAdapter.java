package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class ThirdPartyDetailsAdapter extends ArrayAdapter<ThirdPartyDetailsModel> {

    ArrayList<ThirdPartyDetailsModel> listOfThirdParty;

    public ThirdPartyDetailsAdapter(@NonNull Context context, ArrayList<ThirdPartyDetailsModel> listOfThirdParty) {
        super(context, R.layout.thirdparty_list_item, listOfThirdParty);
        this.listOfThirdParty = listOfThirdParty;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ThirdPartyDetailsModel model = listOfThirdParty.get(position);
        ViewHolder viewHolder;
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        if (convertView == null) {
            viewHolder = new ThirdPartyDetailsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.thirdparty_list_item, parent, false);
            convertView.setTag(viewHolder);

            viewHolder.thirdPartyName = convertView.findViewById(R.id.createdByNameVal);
            viewHolder.thirdPartyIncidentId = convertView.findViewById(R.id.createdByIncidentId);
            viewHolder.thirdPartyMobileNo = convertView.findViewById(R.id.createdByMobileNoVal);
            viewHolder.thirdPartyVehicleNo = convertView.findViewById(R.id.createdByVehicleNoVal);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            viewHolder.thirdPartyIncidentId.setText(model.getIncidentId());
            viewHolder.thirdPartyName.setText(model.getName());
            viewHolder.thirdPartyVehicleNo.setText(model.getVehicleNo());
            viewHolder.thirdPartyMobileNo.setText(model.getMobileNo());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return listOfThirdParty.size();
    }

    @Nullable
    @Override
    public ThirdPartyDetailsModel getItem(int position) {
        return listOfThirdParty.get(position);
    }

    public static class ViewHolder {
        TextView thirdPartyName, thirdPartyMobileNo, thirdPartyVehicleNo, thirdPartyIncidentId;
    }
}