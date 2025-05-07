package com.aki.claimreporting;

import static com.aki.claimreporting.AddVehicle.certificatenumaddriver;
import static com.aki.claimreporting.AddVehicle.regnumadddriver;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_EXTERNAL_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_READ_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_STORAGE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class CustomVehicleHistoryList extends ArrayAdapter<VehicleselectResponse> {

    Context mcontext;
    Activity mactivity;
    ArrayList<VehicleselectResponse> arrayofvehiclenew = new ArrayList<VehicleselectResponse>();
    ArrayList<VehicleselectResponse> arrayList;
    PermissionHandler permissionManager;
    String[] permissions = PERMISSION_LOCATION_STORAGE;

    public CustomVehicleHistoryList(Context context, ArrayList<VehicleselectResponse> arrayofvehicle, Activity activity, PermissionHandler permissionManager) {
        super(context, R.layout.listvehiclehistory, arrayofvehicle);

        this.arrayofvehiclenew = arrayofvehicle;
        this.arrayList = new ArrayList<VehicleselectResponse>();
        this.arrayList.addAll(arrayofvehiclenew);
        mcontext = context;
        mactivity = activity;
        this.permissionManager = permissionManager;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        VehicleselectResponse responsenew = arrayofvehiclenew.get(position);
        ViewHolder viewHolder; // view lookup cache stored in tag
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listvehiclehistory, parent, false);
            viewHolder.vehicletype = (TextView) convertView.findViewById(R.id.txtvehicletype);
            viewHolder.regnum = (TextView) convertView.findViewById(R.id.txtregno);
            viewHolder.certno = (TextView) convertView.findViewById(R.id.txtCertificateno);
            viewHolder.insurancename = (TextView) convertView.findViewById(R.id.txtInsucmpny);
            //  viewHolder.sharebutton  = (ImageView) convertView.findViewById(R.id.sharebuttonid);
            viewHolder.viewbutton = (LinearLayout) convertView.findViewById(R.id.viewbuttonid);
            //   viewHolder.statusbutton = (ImageView) convertView.findViewById(R.id.statusbuttonid);
            viewHolder.deletebutton = (ImageView) convertView.findViewById(R.id.deletebuttonid);
            viewHolder.removebutton = (ImageView) convertView.findViewById(R.id.removebuttonid);
            viewHolder.colorvehicle = (TableRow) convertView.findViewById(R.id.colormyownvehicle);
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
        //    viewHolder.sharebutton.setVisibility(View.GONE);
        viewHolder.viewbutton.setVisibility(View.GONE);
        //viewHolder.deletebutton.setVisibility(View.GONE);
        viewHolder.removebutton.setVisibility(View.GONE);
        viewHolder.regnum.setText(responsenew.getRegistrationNo());
        viewHolder.certno.setText(responsenew.getCertificateNo());
        //viewHolder.tvtypeofcert.setText(responsenew.getTypeOfVehicleName());
        viewHolder.insurancename.setText(responsenew.getInsuranceCompanyName());
        viewHolder.vehicletype.setText(responsenew.getPolicyNo());
//        String tesvehid = responsenew.getVehicleReferernceID();
//        try {
//            String tesvehid1 = AESCrypt.decrypt(responsenew.getVehicleRefID());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        final View finalConvertView = convertView;
        viewHolder.viewbutton.setVisibility(View.VISIBLE);
//        if (responsenew.getIsSubmitted() == "false") {
//            viewHolder.viewbutton.setVisibility(View.GONE);
//            viewHolder.statusbutton.setBackgroundResource(R.drawable.activateicon);
//            viewHolder.statusbutton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        SharedPreferences certificatenum = getContext().getSharedPreferences("CertificateNum", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor certificatenumeeditor = certificatenum.edit();
//                        certificatenumeeditor.putString(CertNum, responsenew.getCertificateNo());
//                        certificatenumeeditor.apply();
//
//                        SharedPreferences Vechnewpref = getContext().getSharedPreferences("VehicleNewID", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor vecheprefednew = Vechnewpref.edit();
//                        vecheprefednew.putString(MainActivity.Vechidshow, responsenew.getVehicleReferernceID());//created vechidshow
//                        vecheprefednew.apply();
//                        SharedPreferences sharedPreferencenew = getContext().getSharedPreferences("IsCreateDriver", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editornew = sharedPreferencenew.edit();
//                        editornew.putString(MainActivity.CreateDriverRedirect, "4");
//                        editornew.apply();
//                        SharedPreferences VechPreferencenew = getContext().getSharedPreferences("GenerateVehiclePDF", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor vecheditornew = VechPreferencenew.edit();
//
//                        vecheditornew.putString(MainActivity.VechPDFID, AESCrypt.decrypt(responsenew.getVehicleRefID())); //created vechpdfid
//
//                        vecheditornew.apply();
//
//                        SharedPreferences certPref = mcontext.getSharedPreferences("CertficiatePref", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = certPref.edit();
//                        editor.putString(certificatenumaddriver, responsenew.getCertificateNo());
//                        editor.putString(regnumadddriver, responsenew.getRegistrationNo());
//                        editor.apply();
//                        Intent redirect = new Intent(getContext(), DriverMapping.class);
//                        finalConvertView.getContext().startActivity(redirect);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                        mCrashlytics.recordException(e);
//                    }
//                }
//            });
//
//            //   viewHolder.statusbutton.setsrcCompat(R.drawable.validateotp);
//        } else {
//            viewHolder.viewbutton.setVisibility(View.VISIBLE);
//            viewHolder.statusbutton.setBackgroundResource(R.drawable.completed);
//
//        }

//
        if (responsenew.getOwnVehicleID().contains("0a9f5fde-70e2-49d3-8464-8f38cd4db784")) {
            //    viewHolder.sharebutton.setVisibility(View.GONE);
            //  viewHolder.viewbutton.setVisibility(View.VISIBLE);
            //viewHolder.deletebutton.setVisibility(View.GONE);
            // viewHolder.removebutton.setVisibility(View.VISIBLE);
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#C3BE49"));
            viewHolder.colorvehicle.setBackground(colorDrawable);

        } else {
            //   viewHolder.sharebutton.setVisibility(View.VISIBLE);
            //    viewHolder.viewbutton.setVisibility(View.VISIBLE);
            // viewHolder.deletebutton.setVisibility(View.VISIBLE);
            // viewHolder.removebutton.setVisibility(View.GONE);
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#C3BE49"));
            viewHolder.colorvehicle.setBackground(colorDrawable);
        }


        viewHolder.viewbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permissionManager.hasPermissions(permissions)) {
                    VehicleselectResponse response = getItem(position);
                    String certno = response.getCertificateNo();
                    String regno = response.getRegistrationNo();
                    SharedPreferences viewvehcilepreferences = mcontext.getSharedPreferences("ViewVehicle", Context.MODE_PRIVATE);
                    SharedPreferences.Editor viewvehceditor = viewvehcilepreferences.edit();
                    viewvehceditor.putString(MyVehicles.ViewCertificateno, certno);
                    viewvehceditor.apply();
                    SharedPreferences viewregpreferences = mcontext.getSharedPreferences("ViewRegNum", Context.MODE_PRIVATE);
                    SharedPreferences.Editor viewregeditor = viewregpreferences.edit();
                    viewregeditor.putString(MyVehicles.ViewRegno, regno);
                    viewregeditor.apply();
                    SharedPreferences refidpreferences = mcontext.getSharedPreferences("VehicleRefID", Context.MODE_PRIVATE);
                    SharedPreferences.Editor refideditor = refidpreferences.edit();
                    refideditor.putString(MyVehicles.Viewvehicleref, response.getVehicleRefID());
                    refideditor.apply();
                    SharedPreferences certPref = mcontext.getSharedPreferences("CertficiatePref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = certPref.edit();
                    editor.putString(certificatenumaddriver, response.getCertificateNo());
                    editor.putString(regnumadddriver, response.getRegistrationNo());
                    editor.apply();
                    Intent login = new Intent(getContext(), VehicleInformationView.class);
                    finalConvertView.getContext().startActivity(login);
                } else {
                    permissionManager.requestPermissions(permissions);
                }
            }
        });

        viewHolder.deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(mcontext);
                dialog.setMessage("Are you sure you want to delete this vehicle?");
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //this will navigate user to the device location settings screen
                        VehicleselectResponse response = getItem(position);
                        String certno = response.getCertificateNo();
                        SharedPreferences viewvehcilepreferences = mcontext.getSharedPreferences("ViewVehicle", Context.MODE_PRIVATE);
                        SharedPreferences.Editor viewvehceditor = viewvehcilepreferences.edit();
                        viewvehceditor.putString(MyVehicles.ViewCertificateno, certno);
                        viewvehceditor.apply();
                        MyVehicles.Deleteuservehicle(mactivity);
                        // arrayofvehiclenew.remove(position);
                        // notifyDataSetChanged();
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
//        viewHolder.radiosuggestion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
////                VehicleselectResponse response = getItem(position);
////                int vehRefId = response.getVehicleRefID();
////                int vehRefId1 = vehRefId;
//                //Intent viewDetails = new Intent(getContext(), MyVehicleDetails.class);
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
        arrayofvehiclenew.clear();
        if (charText.length() == 0) {
            arrayofvehiclenew.addAll(arrayList);
        } else {
            for (VehicleselectResponse model : arrayList) {
                if (model.getRegistrationNo().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    arrayofvehiclenew.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView regnum;
        TextView certno;
        TextView insurancename;
        TextView vehicletype;
        //ImageView sharebutton;
        LinearLayout viewbutton;
        ImageView statusbutton;
        ImageView deletebutton;
        ImageView removebutton;
        TableRow colorvehicle;

    }

}
