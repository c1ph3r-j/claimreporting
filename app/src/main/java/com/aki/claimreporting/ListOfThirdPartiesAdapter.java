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

public class ListOfThirdPartiesAdapter extends ArrayAdapter<ThirdPartyModel> {
    ArrayList<ThirdPartyModel> listOfThirdParties;

    public ListOfThirdPartiesAdapter(@NonNull Context context, ArrayList<ThirdPartyModel> listOfThirdParties) {
        super(context, R.layout.third_party_details_view);
        this.listOfThirdParties = listOfThirdParties;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        ThirdPartyModel response = listOfThirdParties.get(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.third_party_details_view, parent, false);

            viewHolder.regNoVal = convertView.findViewById(R.id.regNoVal);
            viewHolder.modelVal = convertView.findViewById(R.id.modelVal);
            viewHolder.makeVal = convertView.findViewById(R.id.makeVal);
            viewHolder.colorVal = convertView.findViewById(R.id.colorVal);
            viewHolder.thirdPartyCount = convertView.findViewById(R.id.thirdPartyCount);

            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }


        try {
            String thirdPartyCountVal = "Third Party : " + (position + 1);
            viewHolder.thirdPartyCount.setText(thirdPartyCountVal);
            viewHolder.regNoVal.setText((response.getRegistrationNumber().isEmpty()) ? "-" : response.getRegistrationNumber());
            viewHolder.makeVal.setText((response.getMake().isEmpty()) ? "-" : response.getMake());
            viewHolder.modelVal.setText((response.getModel().isEmpty()) ? "-" : response.getModel());
            viewHolder.colorVal.setText((response.getColor().isEmpty()) ? "-" : response.getColor());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        return convertView;
    }

    public ArrayList<ThirdPartyModel> getNewList() {
        return listOfThirdParties;
    }

    @Override
    public int getCount() {
        return listOfThirdParties.size();
    }

    public void addItemToTheList(ThirdPartyModel thirdPartyModel) {
        listOfThirdParties.add(thirdPartyModel);
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        TextView regNoVal, makeVal, modelVal, colorVal, thirdPartyCount;
    }
}
