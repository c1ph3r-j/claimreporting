package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DriverMappingAdapter extends ArrayAdapter<DriverMappingModel> {
    public static ArrayList<DriverMappingSendAPI> drivermaplist = new ArrayList<DriverMappingSendAPI>();
    public Activity mactivity;
    public String validfromdate, validtodate;
    //    private ArrayList dataSet;
    Context mcontext;
    ArrayList<DriverMappingModel> dataSetnew = new ArrayList<DriverMappingModel>();
    ArrayList<DriverMappingModel> drivermaptList;

    public DriverMappingAdapter(Context context, ArrayList<DriverMappingModel> arrayofdriverlet) {
        super(context, R.layout.list_view_driver_mapping, arrayofdriverlet);
        this.dataSetnew = arrayofdriverlet;
        this.drivermaptList = new ArrayList<DriverMappingModel>();
        this.drivermaptList.addAll(dataSetnew);
        mcontext = context;

    }

    //    @Override
//    public int getCount() {
//        return dataSetnew.size();
//    }
//    @Override
//
//    public DriverMappingDatamodel getItem(int position) {
//        return (DriverMappingDatamodel) dataSetnew.get(position);
//    }
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        DriverMappingModel item = dataSetnew.get(position);
        DriverMappingAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new DriverMappingAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_view_driver_mapping, parent, false);
            viewHolder.driverNameself = (TextView) convertView.findViewById(R.id.txtdriverName);
            viewHolder.mobileNo = (TextView) convertView.findViewById(R.id.txtdriverDLNum);
            viewHolder.driverDLValidFrom = (TextView) convertView.findViewById(R.id.txtdlvalidfrom);
            viewHolder.driverDLValidTill = (TextView) convertView.findViewById(R.id.txdlvalidto);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxdriver);
            viewHolder.activateornot = (ImageView) convertView.findViewById(R.id.Notactivatedriver);
            viewHolder.coloractivate = (TableRow) convertView.findViewById(R.id.tablerowid);

            //result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DriverMappingAdapter.ViewHolder) convertView.getTag();
            //result=convertView;
        }
        if (item.getSelfDriver() == true) {
            viewHolder.driverNameself.setText(item.driverName + " (Self)");
        } else {
            viewHolder.driverNameself.setText(item.driverName);
        }

        viewHolder.mobileNo.setText(item.driverDLNum);
//        viewHolder.driverDLValidFrom.setText(item.driverDLValidFrom);
//        viewHolder.driverDLValidTill.setText(item.driverDLValidTill);

        String dtStart = item.driverDLValidFrom;
        String dtend = item.driverDLValidTill;
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
        final View finalConvertView = convertView;
        viewHolder.driverDLValidFrom.setText(validfromdate);
        viewHolder.driverDLValidTill.setText(validtodate);
        String valucheck = item.driverstatus;
        String valucheck1 = valucheck;
        if (item.driverstatus.equals("Activated")) {
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#C3BE49"));
            viewHolder.coloractivate.setBackground(colorDrawable);
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.activateornot.setVisibility(View.GONE);
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (((CompoundButton) view).isChecked()) {

                        DriverMappingModel response = getItem(position);

//                        SharedPreferences certPref = mcontext.getSharedPreferences("DriverMapID", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = certPref.edit();
//                        editor.putString(RegistrationStep1.drivermapdriverid, response.getDriverUserId());
//                        editor.commit();
                        SharedPreferences modelPref = mcontext.getSharedPreferences("CertficiatePref", Context.MODE_PRIVATE);
                        final String certnum = modelPref.getString(AddVehicle.certificatenumaddriver, null);
                        DriverMappingSendAPI element = new DriverMappingSendAPI(response.getDriverUserId(), certnum);
                        drivermaplist.add(element);
//                        try {
//                            new DriverMapping().insertdrivermapselection(mcontext,mactivity);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    } else {

                        System.out.println("Un-Checked");
                        for (int i = 0; i < drivermaplist.size(); i++) {
                            if (item.getDriverUserId().equalsIgnoreCase(drivermaplist.get(i).getDriverUserId())) {
                                drivermaplist.remove(i);
                            }
                        }
//                        DriverMappingSendAPI element = new DriverMappingSendAPI(response.getDriverUserId(), certnum);
//                        drivermaplist.remove(element);

                    }


                }
            });


        } else if (item.driverstatus.equals("Not Activated")) {
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#f8c471"));
            viewHolder.coloractivate.setBackground(colorDrawable);
            viewHolder.activateornot.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setVisibility(View.GONE);
            viewHolder.activateornot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SharedPreferences driversharedpreferences = mcontext.getSharedPreferences("DriverUserActivation", Context.MODE_PRIVATE);
                    SharedPreferences.Editor driverideeditor = driversharedpreferences.edit();
                    driverideeditor.putString(MainActivity.driverUseridmap, item.getDriverUserId());
                    driverideeditor.commit();
                    SharedPreferences identitydriscrpreferences = mcontext.getSharedPreferences("DriverScreen", Context.MODE_PRIVATE);
                    SharedPreferences.Editor identitydriscreditor = identitydriscrpreferences.edit();
                    identitydriscreditor.putString(MainActivity.driverScreenID, "1");
                    identitydriscreditor.commit();
                    SharedPreferences drivephncrpreferences = mcontext.getSharedPreferences("DriverPhoneActivation", Context.MODE_PRIVATE);
                    SharedPreferences.Editor drivphncreditor = drivephncrpreferences.edit();
                    drivphncreditor.putString(MainActivity.driveractiphn, item.getMobileNo());
                    drivphncreditor.commit();
                    /*Intent driveractivae = new Intent(getContext(), DriverActivation.class);
                    finalConvertView.getContext().startActivity(driveractivae);*/ //cant find this class.
                }
            });
        } else {

        }
//        viewHolder.checkBox.setChecked(item.checked);
//        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//            }
//        });
        return convertView;
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    private static class ViewHolder {
        TextView driverNameself;
        TextView mobileNo;
        TextView driverDLValidFrom;
        TextView driverDLValidTill;
        CheckBox checkBox;
        ImageView activateornot;
        TableRow coloractivate;
    }
}