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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class GrivienceAdapter extends ArrayAdapter<GrivienceInfo> {

    public String validfromdate;
    Context mcontext;
    ArrayList<GrivienceInfo> arrayofdrivernew = new ArrayList<GrivienceInfo>();
    ArrayList<GrivienceInfo> arrayList;

    public GrivienceAdapter(Context context, ArrayList<GrivienceInfo> arrayofdriver) {
        super(context, R.layout.listgrievance, arrayofdriver);

        this.arrayofdrivernew = arrayofdriver;
        this.arrayList = new ArrayList<GrivienceInfo>();
        this.arrayList.addAll(arrayofdrivernew);
        mcontext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        GrivienceInfo responsenew = arrayofdrivernew.get(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listgrievance, parent, false);
            viewHolder.idva = (TextView) convertView.findViewById(R.id.txtidval);
            viewHolder.regnova = (TextView) convertView.findViewById(R.id.txtregnoval);
            viewHolder.dateva = (TextView) convertView.findViewById(R.id.txtdatecreated);
            viewHolder.statusva = (TextView) convertView.findViewById(R.id.txtstatusval);

            // viewHolder.regimage = (ImageView) convertView.findViewById(R.id.viewbuttonid);
            // viewHolder.certimage = (ImageView) convertView.findViewById(R.id.deletebuttonid);
            //  viewHolder.makeimag = (ImageView) convertView.findViewById(R.id.deletebuttonid);
            //  viewHolder.cmpnyimg = (ImageView) convertView.findViewById(R.id.deletebuttonid);
            // viewHolder.radiosuggestion = (RadioButton) convertView.findViewById(R.id.Radiovehicle);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.

        viewHolder.idva.setText(responsenew.getIdval());
        viewHolder.regnova.setText(responsenew.getRegnoval());


        String dtStart = responsenew.getDateval();
        SimpleDateFormat formatstart = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date datefrompolicy = formatstart.parse(dtStart);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            validfromdate = formatter.format(Date.parse(String.valueOf(datefrompolicy)));
        } catch (ParseException e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        viewHolder.dateva.setText(validfromdate);
        viewHolder.regnova.setText(responsenew.getRegnoval());
        viewHolder.statusva.setText(responsenew.getGrivStatus());

        final View finalConvertView = convertView;

//        viewHolder.radiosuggestion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
////                VehicleselectResponse response = getItem(position);
////                int vehRefId = response.getVehicleRefID();
////                int vehRefId1 = vehRefId;
        //Intent viewDetails = new Intent(getContext(), MyVehicleDetails.class);
//                //finalConvertView.getContext().startActivity(viewDetails);
//                {
//
//                    if(position != mSelectedPosition && mSelectedRB != null){
//                        mSelectedRB.setChecked(false);
//                    }
//
//                    mSelectedPosition = position;
//                    mSelectedRB = (RadioButton)v;
//                }
//            }
//
//        });
//        if(mSelectedPosition != position){
//            viewHolder.radiosuggestion.setChecked(false);
//        }else{
//            viewHolder.radiosuggestion.setChecked(true);
//            if(mSelectedRB != null && viewHolder.radiosuggestion != mSelectedRB){
//                mSelectedRB = viewHolder.radiosuggestion;
//            }
//        }
        // Return the completed view to render on screen
        return convertView;

    }

    //filter
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayofdrivernew.clear();
        if (charText.length() == 0) {
            arrayofdrivernew.addAll(arrayList);
        } else {
            for (GrivienceInfo model : arrayList) {
                if (model.getRegnoval().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    arrayofdrivernew.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView idva;
        TextView regnova;
        TextView dateva;
        TextView statusva;

    }

}