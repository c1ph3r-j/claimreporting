package com.aki.claimreporting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class CustomServiceProvider extends ArrayAdapter<ServiceProviderModel> {

    public String address1val, address2val, address3val, cityval, countryval;
    public Context newcontext;
    DatabaseHelper mydb;
    ArrayList<ServiceProviderModel> arrayofserviceprovidernew = new ArrayList<ServiceProviderModel>();
    ArrayList<ServiceProviderModel> arrayserviceList;
    private int mSelectedPosition = -1;
    private RadioButton mSelectedRB;

    public CustomServiceProvider(Context context, ArrayList<ServiceProviderModel> arrayofservice) {
        super(context, R.layout.listserviceprovider, arrayofservice);
        this.newcontext = context;
        this.arrayofserviceprovidernew = arrayofservice;
        this.arrayserviceList = new ArrayList<ServiceProviderModel>();
        this.arrayserviceList.addAll(arrayofserviceprovidernew);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ServiceProviderModel responsenew = arrayofserviceprovidernew.get(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listserviceprovider, parent, false);
            viewHolder.entityName = (TextView) convertView.findViewById(R.id.txthospitalname);
            //viewHolder.sector  = (TextView) convertView.findViewById(R.id.txtsectorval);
            viewHolder.address1 = (TextView) convertView.findViewById(R.id.txtaddressval);
            //viewHolder.address2  = (TextView) convertView.findViewById(R.id.txdladdressval2);
            //viewHolder.address3  = (TextView) convertView.findViewById(R.id.txdladdressval3);
            // viewHolder.city  = (TextView) convertView.findViewById(R.id.txtcityval);
            //  viewHolder.country  = (TextView) convertView.findViewById(R.id.txtcountryval);
            viewHolder.phno = (TextView) convertView.findViewById(R.id.txtphnoval);
            //viewHolder.mapimg = (ImageView) convertView.findViewById(R.id.mapview);
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
        String semptytext = "-";
        viewHolder.entityName.setText(responsenew.getEntityName() + " - " + responsenew.getDistance() + " KM");

//        if(responsenew.getSector() == "null"){
//
//            viewHolder.sector.setText(semptytext);
//        }else {
//            viewHolder.sector.setText(responsenew.getSector());
//        }
        // viewHolder.sector.setText(responsenew.getsector());
        //viewHolder.tvtypeofcert.setText(responsenew.getTypeOfVehicleName());

        if (responsenew.getAddress1() == "null") {
            address1val = "";
        } else {
            address1val = responsenew.getAddress1();
        }
        if (responsenew.getAddress2() == "null") {
            address2val = "";
        } else {
            address2val = responsenew.getAddress2();
        }
        if (responsenew.getAddress3() == "null") {
            address3val = "";
        } else {
            address3val = responsenew.getAddress3();
        }
        if (responsenew.getCity() == "null") {
            cityval = "";
        } else {
            cityval = responsenew.getCity();
        }
        if (responsenew.getCounty() == "null") {
            countryval = "";
        } else {
            countryval = responsenew.getCity();
        }
        viewHolder.address1.setText(address1val + address2val + address3val + "," + cityval + "," + countryval);
//        if(responsenew.getAddress1() == "null"){
//
//
//            viewHolder.address1.setText(semptytext);
//        }else {
//            viewHolder.address1.setText(responsenew.getAddress1() + responsenew.getAddress2() + responsenew.getAddress3());
//        }
//        if(responsenew.getAddress2() == "null"){
//            viewHolder.address2.setText(semptytext);
//        }else {
//            viewHolder.address2.setText(responsenew.getAddress2());
//        }
//        if(responsenew.getAddress3() == "null"){
//
//            viewHolder.address3.setText(semptytext);
//        }else {
//            viewHolder.address3.setText(responsenew.getAddress3());
//        }
//        if(responsenew.getCity() == "null"){
//
//            viewHolder.city.setText(semptytext);
//        }else {
//            viewHolder.city.setText(responsenew.getCity());
//        }
//        if(responsenew.getCounty() == "null"){
//
//            viewHolder.country.setText(semptytext);
//        }else {
//            viewHolder.country.setText(responsenew.getCounty());
//        }
        if (responsenew.getPhnnum() == "null") {

            viewHolder.phno.setText(semptytext);
        } else {
            viewHolder.phno.setText("+254 " + responsenew.getPhnnum());
        }
        // viewHolder.city.setText(responsenew.getcity());
        // viewHolder.country.setText(responsenew.getcounty());

        final View finalConvertView = convertView;
        viewHolder.phno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                VehicleselectResponse response = getItem(position);
//                int vehRefId = response.getVehicleRefID();
//                int vehRefId1 = vehRefId;
                //Intent viewDetails = new Intent(getContext(), MyVehicleDetails.class);
                //finalConvertView.getContext().startActivity(viewDetails);
//                {
//
//                    if(position != mSelectedPosition && mSelectedRB != null){
//                        mSelectedRB.setChecked(false);
//                    }

                //    mSelectedPosition = position;
                ServiceProviderModel response = getItem(position);
                SharedPreferences sharedPreference = newcontext.getSharedPreferences("ServiceEntity", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreference.edit();
                editor.putString(ServiceProvider.eventtype, response.getDoctype());
                editor.putString(ServiceProvider.eventName, response.getEntityName());
                editor.putString(ServiceProvider.entityID, response.getEntityID());
                editor.commit();
//                    ServiceProvider.AddEntityinfo();
//                (newcontext as ServiceProvider).AddEntityinfo();
                ((ServiceProvider) newcontext).AddEntityinfo();
                // Intent viewDetails = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "789367456"));
                Intent viewDetails = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+254 " + response.getPhnnum()));
                finalConvertView.getContext().startActivity(viewDetails);
//                    mydb = new DatabaseHelper(getContext());
//                    if(mydb.getclaimstep1details().getCount() != 0)
//                    {
//                        mydb.deleteclaimstep1data();
//                    }
//                    boolean Isinserted = mydb.insertclaimstep1(response.getTypeOfVehicleID(),response.getVehicleRefID(),response.getRegistrationNo(), response.getTypeOfVehicleName(),response.getCoverTypeID(),response.getInsuranceCompanyID());
//                    if(Isinserted == true)
//                    {
//                        boolean test = Isinserted;
//                        Log.i(null,"Insertion Done");
//                    }
//                    else
//                    {
//                        boolean test = Isinserted;
//                        Log.i(null,"Not Insertion Done");
//                    }

//                    mSelectedRB = (RadioButton)v;
//                }
            }

        });

        viewHolder.address1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences locationPref = newcontext.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
                ServiceProviderModel response = getItem(position);
                String mapurl = "http://maps.google.com/maps?saddr=" + locationPref.getString(MainActivity.Latitude, null) + "," + locationPref.getString(MainActivity.Longitude, null) + "&daddr=" + response.getLatdt() + "," + response.getLongdt();
                // String mapurl = "http://maps.google.com/maps?saddr=" + "-1.25848"  + "," + "36.80537" + "&daddr=" + response.getLongdt() + "," + response.getLatdt();
                //String mapurl = "http://maps.google.com/maps?saddr=" + "-1.25848"  + "," + "36.80537" + "&daddr=" + "-1.25848" + "," + "36.80537";
                //Intent intentmap = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?saddr=-1.25848,36.80537&daddr=12.7350668,77.8252219"));
                Intent intentmap = new Intent(Intent.ACTION_VIEW, Uri.parse(mapurl));
                finalConvertView.getContext().startActivity(intentmap);
            }

        });

//        viewHolder.mapimg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences locationPref = newcontext.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
//                ServiceProviderModel response = getItem(position);
//                String mapurl = "http://maps.google.com/maps?saddr=" + locationPref.getString(MainActivity.Latitude, null)  + "," + locationPref.getString(MainActivity.Longitude, null) + "&daddr=" + response.getLatdt() + "," + response.getLongdt();
//                //String mapurl = "http://maps.google.com/maps?saddr=" + "-1.25848"  + "," + "36.80537" + "&daddr=" + response.getLongdt() + "," + response.getLatdt();
//                //String mapurl = "http://maps.google.com/maps?saddr=" + "-1.25848"  + "," + "36.80537" + "&daddr=" + "-1.25848" + "," + "36.80537";
//                //Intent intentmap = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?saddr=-1.25848,36.80537&daddr=12.7350668,77.8252219"));
//                Intent intentmap = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapurl));
//                finalConvertView.getContext().startActivity(intentmap);
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
        arrayofserviceprovidernew.clear();
        if (charText.length() == 0) {
            arrayofserviceprovidernew.addAll(arrayserviceList);
        } else {
            for (ServiceProviderModel model : arrayserviceList) {
                if (model.getEntityName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    arrayofserviceprovidernew.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView entityName;
        //TextView sector;
        TextView address1;
        //TextView address2;
        //TextView address3;
//        TextView city;
//        TextView country;
        TextView phno;
        //        TextView dist;
        //        ImageView regimage;
//        ImageView certimage;
//        ImageView makeimag;
//        ImageView cmpnyimg;
        //RadioButton radiosuggestion;
        ImageView mapimg;
    }

}