package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA_AND_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_READ_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_STORAGE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CustomClaimHistoryList extends ArrayAdapter<ClaimhistoryResponse> {

    public static final String ClaimrefID = "Claimrefid";
    public static DatabaseHelper mydb;
    public static String typeidincident = "typeidincident";
    public String validfromdate;
    public String validfromdateinc;
    public Activity mactivity;
    SharedPreferences sharedpreferences;

    PermissionHandler permissionManager;
    String[] permissions = PERMISSION_CAMERA_AND_STORAGE;
    ArrayList<ClaimhistoryResponse> arrayofclaimhistorynew = new ArrayList<ClaimhistoryResponse>();
    ArrayList<ClaimhistoryResponse> arrayclaimList;

    public CustomClaimHistoryList(Context context, ArrayList<ClaimhistoryResponse> arrayofvehicle, Activity activity, PermissionHandler permissionManager) {
        super(context, R.layout.listclaimhistory, arrayofvehicle);
        mactivity = activity;
        this.arrayofclaimhistorynew = arrayofvehicle;
        this.arrayclaimList = new ArrayList<ClaimhistoryResponse>();
        this.arrayclaimList.addAll(arrayofclaimhistorynew);
        this.permissionManager = permissionManager;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ClaimhistoryResponse responsenew = arrayofclaimhistorynew.get(position);
        CustomClaimHistoryList.ViewHolder viewHolder; // view lookup cache stored in tag
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new CustomClaimHistoryList.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listclaimhistory, parent, false);
            viewHolder.TypeCertificate = (TextView) convertView.findViewById(R.id.txtvehicletype);
            viewHolder.ClaimRefID = (TextView) convertView.findViewById(R.id.textviewrefidval);
            viewHolder.ClaimType = (TextView) convertView.findViewById(R.id.textviewclaimtypeval);
            viewHolder.RegistrationNo = (TextView) convertView.findViewById(R.id.textviewregnoval);
            viewHolder.Make = (TextView) convertView.findViewById(R.id.textviewmakeval);
            viewHolder.Model = (TextView) convertView.findViewById(R.id.textviewmodelval);
            viewHolder.Yearofmanf = (TextView) convertView.findViewById(R.id.textviewyearval);
            viewHolder.ChassisNo = (TextView) convertView.findViewById(R.id.textviewchassisval);
            viewHolder.Coverage = (TextView) convertView.findViewById(R.id.textviewcoverageval);
            viewHolder.CertificateNo = (TextView) convertView.findViewById(R.id.textviewcertifyval);
            viewHolder.Claimdate = (TextView) convertView.findViewById(R.id.textviewclaimdateval);
            viewHolder.claimimg = (LinearLayout) convertView.findViewById(R.id.Claimimgview);
            //  viewHolder.claimsucessimg = (ImageView) convertView.findViewById(R.id.Claimsucessfaif);


//            viewHolder.Model  = (TextView) convertView.findViewById(R.id.Claimimgview);
//            viewHolder.Yearofmanf  = (TextView) convertView.findViewById(R.id.Claimviewpdf);
//            viewHolder.ChassisNo  = (TextView) convertView.findViewById(R.id.Claimviewpdf);
//            viewHolder.TypeCertificate  = (TextView) convertView.findViewById(R.id.Claimviewpdf);
//            viewHolder.Coverage  = (TextView) convertView.findViewById(R.id.Claimviewpdf);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (CustomClaimHistoryList.ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.ClaimRefID.setText(responsenew.getClaimRefID());
        viewHolder.CertificateNo.setText(responsenew.getCertificateNo());
        viewHolder.RegistrationNo.setText(responsenew.getRegistrationNo());
        viewHolder.ClaimType.setText(responsenew.getClaimType());
        viewHolder.Make.setText(responsenew.getMake());
        viewHolder.Model.setText(responsenew.getModel());
        viewHolder.Yearofmanf.setText(responsenew.getYearOfRegistration());
        viewHolder.ChassisNo.setText(responsenew.getChassisNo());
        viewHolder.TypeCertificate.setText(responsenew.getTypeCertificate());
        viewHolder.Coverage.setText(responsenew.getCoverage());

        final View finalConvertView = convertView;
//        if (responsenew.getIsSubmitted() == "false") {
//            viewHolder.claimimg.setVisibility(View.GONE);
//            viewHolder.claimsucessimg.setBackgroundResource(R.drawable.activateicon);
//            viewHolder.claimsucessimg.setOnClickListener(onClickClaimSuccessImg -> {
//                try {
//                    String incdt = responsenew.getClaimdate();
//                    SimpleDateFormat formatstartinc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                    try {
//                        Date datefrominc = formatstartinc.parse(incdt);
//                        SimpleDateFormat formatterin = new SimpleDateFormat("dd-MM-yyyy");
//                        validfromdateinc = formatterin.format(Date.parse(String.valueOf(datefrominc)));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                        mCrashlytics.recordException(e);
//                    }
//                    ClaimLocation.incidentselecteddate = validfromdateinc;
//                    if (responsenew.getClaimType().equals("Collision")) {
//                        SharedPreferences incidentsharedpreferences = getContext().getSharedPreferences("IncidentType", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor incidenttypeditor = incidentsharedpreferences.edit();
//                        incidenttypeditor.putString(typeidincident, "630CF0B1-C91C-48D5-BD09-2F23D6C3AAB8");
//                        incidenttypeditor.apply();
//                    }
//                    if (responsenew.getClaimType().equals("Self-Involving")) {
//                        SharedPreferences incidentsharedpreferences = getContext().getSharedPreferences("IncidentType", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor incidenttypeditor = incidentsharedpreferences.edit();
//                        incidenttypeditor.putString(typeidincident, "F82589E6-7344-47B2-A672-11013F538551");
//                        incidenttypeditor.apply();
//                    }
//                    if (responsenew.getClaimType().equals("Stolen/Theft")) {
//                        SharedPreferences incidentsharedpreferences = getContext().getSharedPreferences("IncidentType", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor incidenttypeditor = incidentsharedpreferences.edit();
//                        incidenttypeditor.putString(typeidincident, "B2EC755A-88EF-4F53-8911-C13688D226D3");
//                        incidenttypeditor.apply();
//                    }
//
//                    if (responsenew.getClaimType().equals("Stolen/Theft")) {
//                        Toast.makeText(onClickClaimSuccessImg.getContext(), "Stolen/Theft cannot be initiated in the middle as of now.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        mydb = new DatabaseHelper(getContext());
//                        Cursor curseattachimage = mydb.getlocalimageattachment();
//                        int countimage = curseattachimage.getCount();
//
//                        if (countimage > 0) {
//                            mydb.deletealllocalimage();
//                        }
//                        Cursor cursethirdattachimage = mydb.getthirdlocalimages();
//                        int countthirdimage = cursethirdattachimage.getCount();
//
//                        if (countthirdimage > 0) {
//                            mydb.deletethirdlocalimage();
//                        }
//                        Cursor cursethirdpartdetails = mydb.getthirdpartydetails();
//                        int countthirddetail = cursethirdpartdetails.getCount();
//
//                        if (countthirddetail > 0) {
//                            mydb.deletethirdpartydetails();
//                        }
//
//                        Cursor cursestolenpartdetails = mydb.getClaimImgmore();
//                        int countstolendetail = cursestolenpartdetails.getCount();
//
//                        if (countstolendetail > 0) {
//                            mydb.deleteClaimImgmor();
//                        }
//
//                        SharedPreferences certpref = getContext().getSharedPreferences("ClaimInsert", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor certeditor = certpref.edit();
//                        certeditor.putString("CertificateID", responsenew.getCertificateNo());
//                        certeditor.putString("Vechilerefid", responsenew.getIsvehicleId());
//                        certeditor.apply();
//
//                        SharedPreferences sharedpreferences = getContext().getSharedPreferences("CRAID", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor craeeditor = sharedpreferences.edit();
//                        craeeditor.putString(CraIdval, responsenew.getClaimRefID());
//                        craeeditor.apply();
//                        MainActivity.regnogetclaim = responsenew.getRegistrationNo();
//                        MainActivity.claiminmiddleflow = "1";
//                        //MyClaims.GetClaimAttachment(mactivity);
////                        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
////                        int densityDpi = dm.densityDpi;
////                        if(densityDpi >= 320 && densityDpi <= 390)
////                        {
////                            Intent car = new Intent(getContext(), CarView.class);
////                            finalConvertView.getContext().startActivity(car);
////                        }
////                        if(densityDpi >= 280 && densityDpi <= 310)
////                        {
////                            Intent car = new Intent(getContext(), CarView280.class);
////                            finalConvertView.getContext().startActivity(car);
////                        }
////                        if (densityDpi >= 400 && densityDpi <= 520)
////                        {
////                            Intent car = new Intent(getContext(), CarView400.class);
////                            finalConvertView.getContext().startActivity(car);
////                        }
//
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                    mCrashlytics.recordException(e);
//                }
//            });
//
//        } else {
//            viewHolder.claimimg.setVisibility(View.VISIBLE);
//            viewHolder.claimsucessimg.setBackgroundResource(R.drawable.completed);
//        }

        String dtStart = responsenew.getClaimdate();
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

        viewHolder.Claimdate.setText(validfromdate);


        viewHolder.claimimg.setOnClickListener(onCLickClaimImg -> {
            if (permissionManager.hasPermissions(permissions)) {
                ClaimhistoryResponse response = getItem(position);
                String claimrefidnew = String.valueOf(response.getClaimRefID());
                SharedPreferences claimPref = getContext().getSharedPreferences("ClaimDetailsView", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = claimPref.edit();
                editor.putString(ClaimrefID, claimrefidnew);
                editor.apply();
                Intent viewDetails = new Intent(getContext(), ClaimInformationView.class);
                finalConvertView.getContext().startActivity(viewDetails);
//                int vehRefId1 = vehRefId;
            } else {
                permissionManager.requestPermissions(permissions);
            }
        });
//        viewHolder.claimpdf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//               //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(responsenew.getClaimDocument()));
//               //finalConvertView.getContext().startActivity(browserIntent);
//            }
//        });
        // Return the completed view to render on screen
        return convertView;

    }

    //filter
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayofclaimhistorynew.clear();
        if (charText.length() == 0) {
            arrayofclaimhistorynew.addAll(arrayclaimList);
        } else {
            for (ClaimhistoryResponse model : arrayclaimList) {
                if (String.valueOf(model.getRegistrationNo()).toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    arrayofclaimhistorynew.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView ClaimRefID;
        TextView CertificateNo;
        TextView RegistrationNo;
        TextView ClaimType;
        TextView Make;
        TextView Model;
        TextView Yearofmanf;
        TextView ChassisNo;
        TextView TypeCertificate;
        TextView Coverage;
        TextView Claimdate;
        LinearLayout claimimg;
        // ImageView claimsucessimg;

    }

}