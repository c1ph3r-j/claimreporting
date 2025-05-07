package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TimelineAdapter extends ArrayAdapter<TimeLineModel> {

    public int sarraysize;
    public String sclaimdate1, sclaimtime1;
    Context mcontext;
    ArrayList<TimeLineModel> arrayoftimelinenew = new ArrayList<TimeLineModel>();
    ArrayList<TimeLineModel> arrayList;

    public TimelineAdapter(Context context, ArrayList<TimeLineModel> arrayoftimeline) {
        super(context, R.layout.listtimeline, arrayoftimeline);

        this.arrayoftimelinenew = arrayoftimeline;
        this.arrayList = new ArrayList<TimeLineModel>();
        this.arrayList.addAll(arrayoftimelinenew);
        mcontext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TimeLineModel responsenew = arrayoftimelinenew.get(position);
        sarraysize = arrayoftimelinenew.size();
        int sposition = position;


        TimelineAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new TimelineAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listtimeline, parent, false);

            viewHolder.txtclaimdate = (TextView) convertView.findViewById(R.id.txtclaimdate1);
            viewHolder.txtclaimevent = (TextView) convertView.findViewById(R.id.txtclaimreported1);

            viewHolder.imgclaim = (ImageView) convertView.findViewById(R.id.imgtimeline1);

            // viewHolder.regimage = (ImageView) convertView.findViewById(R.id.viewbuttonid);
            // viewHolder.certimage = (ImageView) convertView.findViewById(R.id.deletebuttonid);
            //  viewHolder.makeimag = (ImageView) convertView.findViewById(R.id.deletebuttonid);
            //  viewHolder.cmpnyimg = (ImageView) convertView.findViewById(R.id.deletebuttonid);
            // viewHolder.radiosuggestion = (RadioButton) convertView.findViewById(R.id.Radiovehicle);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (TimelineAdapter.ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        /*if(responsenew.getSelfDriver() == true){
            viewHolder.drivername.setText(responsenew.getDriverName() + " (Self)");
        }else {
            viewHolder.drivername.setText(responsenew.getDriverName());
        }*/
        //int responselength = responsenew.;
        String dtStart1 = responsenew.getClaimdatetime();

        SimpleDateFormat formatstart = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date datefrompolicy = formatstart.parse(dtStart1);
            SimpleDateFormat formattedate = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            SimpleDateFormat formattetime = new SimpleDateFormat("hh:mm:ss");
            sclaimdate1 = formattedate.format(Date.parse(String.valueOf(datefrompolicy)));
            sclaimtime1 = formattetime.format(Date.parse(String.valueOf(datefrompolicy)));


        } catch (ParseException e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "getView", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        TimeLineModel user = getItem(position);
        viewHolder.txtclaimdate.setText(sclaimdate1 + "\n" + sclaimtime1);
        viewHolder.txtclaimevent.setText(responsenew.getClaimeventname() + "\n" + responsenew.getClaimevent());


        if (sarraysize == 0) {
            viewHolder.imgclaim.setImageResource(R.drawable.heightstart);
        } else if ((sarraysize - 1) == position) {
            viewHolder.imgclaim.setImageResource(R.drawable.heightend);

        } else {
            viewHolder.imgclaim.setImageResource(R.drawable.heightmiddle);

        }


        /*String dtStart = responsenew.getDriverDLValidFrom();
        String dtend = responsenew.getDriverDLValidTill();
        SimpleDateFormat formatstart = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatend = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date datefrompolicy = formatstart.parse(dtStart);
            Date datetopolicy = formatend.parse(dtend);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            validfromdate = formatter.format(Date.parse(String.valueOf(datefrompolicy)));
            validtodate = formatter.format(Date.parse(String.valueOf(datetopolicy)));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        // JSONArray claimResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllLifecycle");


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
        arrayoftimelinenew.clear();
        if (charText.length() == 0) {
            arrayoftimelinenew.addAll(arrayList);
        } else {
            for (TimeLineModel model : arrayList) {
                if (model.getClaimevent().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    arrayoftimelinenew.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView txtclaimdate;
        ImageView imgclaim;
        TextView txtclaimevent;


    }
}
