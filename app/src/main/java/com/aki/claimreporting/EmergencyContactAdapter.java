package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

public class EmergencyContactAdapter extends ArrayAdapter<EmergencyContactInfo> {

    public Activity mactivity;
    Context newcontext;
    ArrayList<EmergencyContactInfo> arrayofnearnew = new ArrayList<EmergencyContactInfo>();
    ArrayList<EmergencyContactInfo> arrayList;

    public EmergencyContactAdapter(Context context, ArrayList<EmergencyContactInfo> arrayofnear, Activity activity) {
        super(context, R.layout.listemergencycontact, arrayofnear);
        this.newcontext = context;
        this.mactivity = activity;
        this.arrayofnearnew = arrayofnear;
        this.arrayList = new ArrayList<EmergencyContactInfo>();
        this.arrayList.addAll(arrayofnearnew);

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        EmergencyContactInfo responsenew = arrayofnearnew.get(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listemergencycontact, parent, false);
            viewHolder.nearname = (TextView) convertView.findViewById(R.id.txtneardearname);
            viewHolder.nearphonenum = (TextView) convertView.findViewById(R.id.txtneardearphno);
            viewHolder.nearemail = (TextView) convertView.findViewById(R.id.txtneardearemail);
            viewHolder.neardelete = (ImageView) convertView.findViewById(R.id.deleteneardearbuttonid);

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


        viewHolder.nearname.setText(responsenew.getNearname());
        viewHolder.nearphonenum.setText(responsenew.getNearphone());
        viewHolder.nearemail.setText(responsenew.getNearemail());
        viewHolder.neardelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedPreferences nearsharedpreferences = newcontext.getSharedPreferences("NearDearDelete", Context.MODE_PRIVATE);
                    SharedPreferences.Editor nearideeditor = nearsharedpreferences.edit();
                    nearideeditor.putString(MainActivity.neardearUseridmap, responsenew.getNearid());
                    String id = responsenew.nearid;
                    System.out.println(id);
                    nearideeditor.apply();
                    new EmergencyContactList().DeleteNearDear(newcontext, mactivity);
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "getView", e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            }
        });

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
        arrayofnearnew.clear();
        if (charText.length() == 0) {
            arrayofnearnew.addAll(arrayList);
        } else {
            for (EmergencyContactInfo model : arrayList) {
                if (model.getNearname().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    arrayofnearnew.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView nearname;
        TextView nearphonenum;
        TextView nearemail;
        ImageView neardelete;
    }

}
