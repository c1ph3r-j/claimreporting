package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DriverVehicleListAdapter extends ArrayAdapter<DriverVehicleDataModel> {

    public String validfromdate, validtodate;
    Context mcontext;
    FragmentManager fragmentManager;
    ArrayList<DriverVehicleDataModel> arrayofdrivernew = new ArrayList<DriverVehicleDataModel>();
    ArrayList<DriverVehicleDataModel> arrayList;

    public DriverVehicleListAdapter(Context context, ArrayList<DriverVehicleDataModel> arrayofdriver, FragmentManager fragmentManager) {
        super(context, R.layout.vehicledriverlist, arrayofdriver);

        this.arrayofdrivernew = arrayofdriver;
        this.arrayList = new ArrayList<DriverVehicleDataModel>();
        this.arrayList.addAll(arrayofdrivernew);
        mcontext = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DriverVehicleDataModel responsenew = arrayofdrivernew.get(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.vehicledriverlist, parent, false);

            viewHolder.drivername = (TextView) convertView.findViewById(R.id.txtdrivername);
            viewHolder.mobileno = (TextView) convertView.findViewById(R.id.txtdriverphno);
            viewHolder.driverdlnum = (TextView) convertView.findViewById(R.id.txtdriverdlno);
            viewHolder.driverdlvalidfrm = (TextView) convertView.findViewById(R.id.txtdrivervalidityfrom);
            viewHolder.driverdlvalidtill = (TextView) convertView.findViewById(R.id.txtdrivervalidityto);
            // viewHolder.viewdriverbutton  = (ImageView) convertView.findViewById(R.id.viewbuttonid);
            viewHolder.deletedriverbutton = (ImageView) convertView.findViewById(R.id.deletebuttonid);
            // viewHolder.activatedriverbutton  = (ImageView) convertView.findViewById(R.id.activatebuttonid);
            viewHolder.colordriver = (TableRow) convertView.findViewById(R.id.tablerowcolor);
            viewHolder.driverphno = (LinearLayout) convertView.findViewById(R.id.phnodriver);

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
        if (responsenew.isSelfDriver() == true) {
            viewHolder.drivername.setText(responsenew.getDriverName() + " (Self)");
            viewHolder.driverphno.setVisibility(View.GONE);
        } else {
            viewHolder.drivername.setText(responsenew.getDriverName());
            viewHolder.driverphno.setVisibility(View.VISIBLE);
            viewHolder.mobileno.setText(responsenew.getMobileNo());
        }

        viewHolder.driverdlnum.setText(responsenew.getDriverDLNum());


        String dtStart = responsenew.getDriverDLValidFrom();
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
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "getView", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        viewHolder.driverdlvalidfrm.setText(validfromdate);
        viewHolder.driverdlvalidtill.setText(validtodate);
        // viewHolder.activatedriverbutton.setVisibility(View.GONE);
        // viewHolder.viewdriverbutton.setVisibility(View.GONE);
        viewHolder.deletedriverbutton.setVisibility(View.GONE);

        final View finalConvertView = convertView;

        if (responsenew.isSelfDriver() == true) {
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#3598DB"));
            viewHolder.colordriver.setBackground(colorDrawable);
            //viewHolder.viewdriverbutton.setVisibility(View.VISIBLE);
            viewHolder.deletedriverbutton.setVisibility(View.GONE);
            // viewHolder.activatedriverbutton.setVisibility(View.VISIBLE);

        } else {
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#3598DB"));
            viewHolder.colordriver.setBackground(colorDrawable);
            //viewHolder.viewdriverbutton.setVisibility(View.VISIBLE);
            viewHolder.deletedriverbutton.setVisibility(View.VISIBLE);
            // viewHolder.activatedriverbutton.setVisibility(View.GONE);

            viewHolder.deletedriverbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mcontext);
                    dialog.setMessage("Are you sure you want to delete this driver?");
                    dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences driversharedpreferences = mcontext.getSharedPreferences("DriverUserActivation", Context.MODE_PRIVATE);
                            SharedPreferences.Editor driverideeditor = driversharedpreferences.edit();
                            driverideeditor.putString(MainActivity.driverUseridmap, responsenew.getDriverUserId());
                            driverideeditor.apply();
                            Vehicledrivers.DeletevehicleDriver(mcontext);
                            arrayofdrivernew.remove(position);
                            notifyDataSetChanged();
                            //  MyDriiver scanCertificatephysical = new MyDriiver();
//                        MyDriiver scanCertificatephysical = new MyDriiver();
//                        FragmentManager manager =fragmentManager;
//                        manager.beginTransaction().replace(R.id.nav_host_fragment,scanCertificatephysical,scanCertificatephysical.getTag()).apply();
//                    Fragment mFragment = new MyDriiver();
//                    fragmentManager.beginTransaction().replace(R.id.nav_mydriver,mFragment).apply();

                        }
                    });
                    dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //this will navigate user to the device location settings screen
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();

                }
            });
        }


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
            for (DriverVehicleDataModel model : arrayList) {
                if (model.getDriverName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    arrayofdrivernew.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView drivername;
        Boolean isself;
        TextView driverdlcountry;
        TextView driverdlnum;
        TextView driverdlvalidfrm;
        TextView driverdlvalidtill;
        TextView mobileno;
        // ImageView viewdriverbutton;
        ImageView deletedriverbutton;
        //  ImageView activatedriverbutton;
        TableRow colordriver;
        LinearLayout driverphno;


    }

}