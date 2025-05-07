package com.aki.claimreporting;


import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClaimInfo extends Fragment {

    public static String reqidval;
    public static String loginemail, loginphn, loginame;
    public ListView list;
    public String stokenval, encryptedSHA;
    public String locationmap;
    public ProgressDialog progressdialog;
    //    public ClaimDamageImages mAdapter; // no usage found
    public android.app.AlertDialog.Builder dialog;
    public android.app.AlertDialog alert;
    public JSONObject assessordt, garagedt, investigatordt;
    public View rootview;
    //    ArrayList<DamageImageClaim> claimdamagelist = new ArrayList<DamageImageClaim>(); //no usage found
    public TextView txtincident, txtincidenttype, txtlocation, txtcoordinate, txtdatetime, txtstatus, txtpolicy, txtcovertype, txtclaimtype, txtname, txtcommencingdt, txtenddt, txtphno, txtemail, txtregno, txtcertificateno, txtcapacity, txtmake, txtmodel, txtyear, txtdrivername, txtdriverlicense, txtlicencevalidfrm, txtlicencevalidto, txtdriverphno, txtclaimuser, txtassessor, txtgarage, txtinvestigator, txtambulance, txttowing, txtcalpolice;
    public String sincident, sincidenttype, slocation, scoordinate, sdatetime, sstatus, spolicy, scovertype, sclaimtype, sname, scommencingdt, senddt, sphno, semail, sregno, scertificateno, scapacity, smake, smodel, syear, sdrivername, sdriverlicense, slicencevalidfrm, slicencevalidto, sdriverphno, sclaimuser, sassessor, sgarage, sinvestigator, sambulance, stowing, scalpolice;
    public ImageView claimmoreimg;
    public boolean assesoruse, garrageuse, investigateuse;
    public TextView loginusernmval;
    public TextView loginuserval;
    public TextView loginuseremailidval;
    public ImageView imgpdfviewmemberdt, imgpdfviewdt;
    public String typeidvalue;
    DatabaseHelper mydb;
    SharedPreferences sharedpreferences;
    Activity activity;
    private FirebaseCrashlytics mCrashlytics;


    public ClaimInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_claim_info, container, false);
        rootview = inflater.inflate(R.layout.claim_info_fragment, container, false);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        init();
        return rootview;
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            imgpdfviewdt = (ImageView) rootview.findViewById(R.id.imgpdfview);
            imgpdfviewmemberdt = (ImageView) rootview.findViewById(R.id.imgpdfviewmember);

//            LinearLayout thirdPartyDetails = rootview.findViewById(R.id.viewThirdPartyDetails);
//            try {
//                thirdPartyDetails.setOnClickListener(onClickThirdPartyDetails ->
//                        startActivity(new Intent(requireActivity(), ThirdPartyDetails.class)));
//            } catch (Exception e) {
//                e.printStackTrace();
//                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                mCrashlytics.recordException(e);
//            }
            try {
                imgpdfviewdt.setOnClickListener(onClickViewPdf -> {
                    typeidvalue = "1";
                    getclaimpdf();
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            try {
                imgpdfviewmemberdt.setOnClickListener(onClickViewImage -> {
                    typeidvalue = "2";
                    getclaimpdf();
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            //   loginusernmval = (TextView) rootview.findViewById(R.id.txtloginusernmval);
            //   loginuserval = (TextView) rootview.findViewById(R.id.txtloginuserval);
            //    loginuseremailidval = (TextView) rootview.findViewById(R.id.txtloginuseremailidval);
            claimmoreimg = (ImageView) rootview.findViewById(R.id.imageclaimmore);
            try {
                claimmoreimg.setOnClickListener(onClickClaimMoreImg -> {
                    Intent login = new Intent(requireActivity(), ClaimHistoryImage.class);
                    startActivity(login);
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            mydb = new DatabaseHelper(requireActivity());
            Cursor curseattachuser = mydb.getUserdetails();
            int countuser = curseattachuser.getCount();
            try {
                if (countuser >= 1) {
                    while (curseattachuser.moveToNext()) {

                        try {
                            loginame = AESCrypt.decrypt(curseattachuser.getString(1));
                            loginphn = curseattachuser.getString(3);
                            loginemail = curseattachuser.getString(4);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            //       loginusernmval.setText(loginame);
            //   loginuserval.setText(loginphn);
            //    loginuseremailidval.setText(loginemail);
            getclaimnfo();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
            txtlocation = (TextView) rootview.findViewById(R.id.txtlocationval);
            txtlocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentmap = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + locationmap));
                    startActivity(intentmap);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gps_enabled || network_enabled;

    }
    public void downloadfile(String filepath) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            // Show downloading dialog
            dialog = new android.app.AlertDialog.Builder(requireActivity());
            dialog.setMessage(getString(R.string.claim_report_downloading));
            alert = dialog.create();
            alert.show();

            // Fetch the incident ID from SharedPreferences
            SharedPreferences claimPref = requireActivity().getSharedPreferences("ClaimDetailsView", Context.MODE_PRIVATE);
            String incidentId = claimPref.getString(CustomClaimHistoryList.ClaimrefID, "");
            String destination = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/";
            String fileName = incidentId + ".pdf";
            destination += fileName;

            // Open URL connection to download the file
            URL url = new URL(filepath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            // Read the input stream
            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }

            // Close streams
            inputStream.close();
            fileOutputStream.close();
            urlConnection.disconnect();

            // Download complete, dismiss the dialog and show PDF
            alert.dismiss();
            showPdf();

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }


    /*public void downloadfile(String filepath) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            dialog = new android.app.AlertDialog.Builder(requireActivity());
            dialog.setMessage(getString(R.string.claim_report_downloading));
            alert = dialog.create();
            alert.show();
            SharedPreferences claimPref = requireActivity().getSharedPreferences("ClaimDetailsView", Context.MODE_PRIVATE);
            String incident_id = claimPref.getString(CustomClaimHistoryList.ClaimrefID, "");
            String destination = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/";
            String fileName = incident_id + ".pdf";
            destination += fileName;
            final Uri uri = Uri.parse("file://" + destination);

            String url = filepath; //paste url here

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Downloading....");
            request.setTitle(" TITLE ");
            request.setDestinationUri(uri);

            final DownloadManager manager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);

            //final String finalDestination = destination;
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    Log.d("Update status", "Download completed");
                    alert.dismiss();
                    showPdf();
                    requireActivity().unregisterReceiver(this);
                }
            };

            requireActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }*/

    public void showPdf() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            SharedPreferences claimPref = requireActivity().getSharedPreferences("ClaimDetailsView", Context.MODE_PRIVATE);
            String incident_id = claimPref.getString(CustomClaimHistoryList.ClaimrefID, "");
            File file = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/" + incident_id + ".pdf");
            //File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/C-AA0074.pdf");
            Uri uri = FileProvider.getUriForFile(requireActivity(), "com.aki.claimreporting.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


    }

    public void getclaimnfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    mydb = new DatabaseHelper(requireActivity());
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                MainActivity.stokenval = curseattachtoken.getString(1);
                                // stokenval = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ilg1ZVhrNHh5b2pORnVtMWtsMll0djhkbE5QNC1jNTdkTzZRR1RWQndhTmsifQ.eyJleHAiOjE2MTIyNDQzMzIsIm5iZiI6MTYxMjI0MDczMiwidmVyIjoiMS4wIiwiaXNzIjoiaHR0cHM6Ly9jcmF1YXR2Mi5iMmNsb2dpbi5jb20vNjBjMTY5ZmItMDZlYy00ZWMyLWFkYjMtM2NlM2U2YjE5ZGEzL3YyLjAvIiwic3ViIjoiMTczYWU2Y2MtN2M2MS00NWI1LWFiZjMtZWQ1OGNkOTY0YWNkIiwiYXVkIjoiNDMwNzJiYTAtZDY2Ni00MzVkLWE0YjMtYzMwZDZmZDdhMmYxIiwibm9uY2UiOiJkZWZhdWx0Tm9uY2UiLCJpYXQiOjE2MTIyNDA3MzIsImF1dGhfdGltZSI6MTYxMjI0MDczMiwib2lkIjoiMTczYWU2Y2MtN2M2MS00NWI1LWFiZjMtZWQ1OGNkOTY0YWNkIiwibmFtZSI6InVua25vd24iLCJlbWFpbHMiOlsicmFtZXNoLnNAc3dpZnRhbnQuY29tIl0sInRmcCI6IkIyQ18xX3NpZ25pbl9hbmRfc2lnbnVwIn0.d4i1F4Xwd9rKQF2iYFsN4PRy_7RSpO2VKhyBBDN-l1rUHBiJaA0JqRKNdM_e-AUqQqaBkV5LGTBf4MxnGf5LGLA4mPygiEk18NUsNr_Z734WcA9zqYA_j4cPWicg-KEE0wkMIUnmMMRFGSdZssaSook9qe5utiHJoOWj5O_f3NSJYuzf97-grlOuQywYZfJLzRHqMwfPl0nDxt_oqQZM3RxMYQzoVf6mYfcVnadnnFUlnuRe2lDKwpRzTQIynhaFANd1VwsgF2OC41JckkTw_vCNmgtzJzd_YCI2D0GeNUsLHaXr61SITdvKMSQyE9KtYXgrO2ldu7CNWhp7qr_7DQ";
                            }
                        }
                    }
                    // stokenval = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ilg1ZVhrNHh5b2pORnVtMWtsMll0djhkbE5QNC1jNTdkTzZRR1RWQndhTmsifQ.eyJleHAiOjE2MTIyODk0NzgsIm5iZiI6MTYxMjI4NTg3OCwidmVyIjoiMS4wIiwiaXNzIjoiaHR0cHM6Ly9jcmF1YXR2Mi5iMmNsb2dpbi5jb20vNjBjMTY5ZmItMDZlYy00ZWMyLWFkYjMtM2NlM2U2YjE5ZGEzL3YyLjAvIiwic3ViIjoiMTczYWU2Y2MtN2M2MS00NWI1LWFiZjMtZWQ1OGNkOTY0YWNkIiwiYXVkIjoiNDMwNzJiYTAtZDY2Ni00MzVkLWE0YjMtYzMwZDZmZDdhMmYxIiwibm9uY2UiOiJkZWZhdWx0Tm9uY2UiLCJpYXQiOjE2MTIyODU4NzgsImF1dGhfdGltZSI6MTYxMjI4NTg3OCwib2lkIjoiMTczYWU2Y2MtN2M2MS00NWI1LWFiZjMtZWQ1OGNkOTY0YWNkIiwibmFtZSI6InVua25vd24iLCJlbWFpbHMiOlsicmFtZXNoLnNAc3dpZnRhbnQuY29tIl0sInRmcCI6IkIyQ18xX3NpZ25pbl9hbmRfc2lnbnVwIn0.nnScy6epA1kwiDG2KabMbZQw1Zpc6Yun-zLCai5x3oDYj7RAYh3N4SIjSD1owoHjTMdSPf9ev5edcxvWRIaTOn8h9LPKCZ9pZcneP1d3Vm91oZq-RnB0xWmw25FsM4tn0w-rFgpxChi6SXWlgpXKgDUWVW0IaLI8p4c7aAzxWqcUK31jbUWWCasEEfcICUsRHNj62oSV2riOFUcQFk_AFVO2_QlUNY-vLSnxpEmX2iiffVGAgX8aY_128bHOaPEfBxwI7DcVWBoPyVdEnQGw9lOmY5iaLmBsTFpPomUXXqcgXEtkkhuKQlcY8sYefwz4sUTuxUMwrTaZAbQz8VX6xA";

                    progressdialog = new ProgressDialog(requireActivity());
                    encryptedSHA = "";
                    //  String sourceStr = MainActivity.InsertMobileparameters();
                    //String smobileparam = "{ \"imeino1\": \"355844090234339\", \"imeino2\": \"355844093244335\", \"timezone\": \"+0630GMT:IST Asia/Kolkata\", \"currentdatetime\": \"12-10-2019 18:33:14\", \"Latitude\": \"-1.25848\", \"Longitude\": \"36.80537\",\"IpAddress\": \"192.168.2.100\"}";
//                    try {
//                        encryptedSHA = AESUtils.encrypt(sourceStr);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    Thread thread = new Thread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        public void run() {
                            MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/GetClaim";
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
                            SharedPreferences claimPref = requireActivity().getSharedPreferences("ClaimDetailsView", Context.MODE_PRIVATE);
                            String incidenid = claimPref.getString(CustomClaimHistoryList.ClaimrefID, "");
//                            String incidenid ="221216-62";
                            JsonObject Details = new JsonObject();
                            try {
                                Details.addProperty("incidentUniqueCode", incidenid);
                            } catch (Exception e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                            String insertString = Details.toString();
                            RequestBody body = RequestBody.create(JSON, insertString);
                            Request request = new Request.Builder()
                                    .url(MainActivity.postURL)
                                    .header("Authorization", "Bearer " + MainActivity.stokenval)
                                    .header("MobileParameter", MainActivity.InsertMobileParameters())
                                    .post(body)
                                    .build();
                            Response staticResponse = null;

                            try {
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog = ProgressDialog.show(requireActivity(), "Loading", "Please wait...", true);
                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
                                    }
                                });
                                staticResponse = client.newCall(request).execute();
                                String staticRes = staticResponse.body().string();
                                Log.i(null, staticRes);
                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                try {
                                    reqidval = staticJsonObj.getString("reqID");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                if (staticJsonObj.getInt("rcode") == 1) {
                                    //final JSONObject staticfinalObj = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate");
                                    // sOTP= staticJsonObj.getJSONObject("rObj").getString("OTP");
                                    //  sOTPID= staticJsonObj.getJSONObject("rObj").getString("OTPID");

                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (progressdialog.isShowing()) {
                                                progressdialog.dismiss();
                                            }
                                        }
                                    });
                                    JSONObject claimResponseList = staticJsonObj.getJSONObject("rObj").getJSONObject("getClaim");

                                    txtincident = (TextView) rootview.findViewById(R.id.txtincidentval);
                                    txtincidenttype = (TextView) rootview.findViewById(R.id.txtincidenttype);
                                    txtlocation = (TextView) rootview.findViewById(R.id.txtlocationval);
                                    //    txtcoordinate = (TextView) rootview.findViewById(R.id.txtcoordinateval);
                                    txtdatetime = (TextView) rootview.findViewById(R.id.txtdatetimeval);
                                    txtstatus = (TextView) rootview.findViewById(R.id.txtstatusval);
                                    txtpolicy = (TextView) rootview.findViewById(R.id.txtpolicynoval);
                                    txtcovertype = (TextView) rootview.findViewById(R.id.txtpolicycoverval);
                                    txtclaimtype = (TextView) rootview.findViewById(R.id.txtclaimtypeval);
                                    txtname = (TextView) rootview.findViewById(R.id.txtnameval);
                                    txtcommencingdt = (TextView) rootview.findViewById(R.id.txtcommencingdtval);
                                    txtenddt = (TextView) rootview.findViewById(R.id.txtenddtval);
                                    txtphno = (TextView) rootview.findViewById(R.id.txtphonenoval);
                                    txtemail = (TextView) rootview.findViewById(R.id.txtmailidval);
                                    txtregno = (TextView) rootview.findViewById(R.id.txtregnoval);
                                    txtcertificateno = (TextView) rootview.findViewById(R.id.txtcertificateval);
                                    txtcapacity = (TextView) rootview.findViewById(R.id.txtcapacityval);
                                    txtmake = (TextView) rootview.findViewById(R.id.txtmakeval);
                                    txtmodel = (TextView) rootview.findViewById(R.id.txtmodelval);
                                    txtyear = (TextView) rootview.findViewById(R.id.txtyearval);
                                    txtdrivername = (TextView) rootview.findViewById(R.id.txtdrivernameval);
                                    txtdriverlicense = (TextView) rootview.findViewById(R.id.txtdriverlicenseval);
                                    txtlicencevalidfrm = (TextView) rootview.findViewById(R.id.txtlicensevalidfrmval);
                                    txtlicencevalidto = (TextView) rootview.findViewById(R.id.txtlicensevalidtoval);
                                    txtdriverphno = (TextView) rootview.findViewById(R.id.txtdriverphnoval);
                                    txtclaimuser = (TextView) rootview.findViewById(R.id.txtclaimuserval);
                                    txtassessor = (TextView) rootview.findViewById(R.id.txtassessorval);
                                    txtgarage = (TextView) rootview.findViewById(R.id.txtgarageval);
                                    txtinvestigator = (TextView) rootview.findViewById(R.id.txtinvestigatorval);
                                    txtambulance = (TextView) rootview.findViewById(R.id.txtambulanceval);
                                    txttowing = (TextView) rootview.findViewById(R.id.txttowingval);
                                    txtcalpolice = (TextView) rootview.findViewById(R.id.txtcalpoliceval);

                                    //JSONObject claimObj1 = claimResponseList.getJSONObject(0);
                                    JSONObject policydt = claimResponseList.getJSONObject("vehicle");
                                    try {
                                        JSONObject driverdt = claimResponseList.getJSONObject("driver");
                                        sdrivername = driverdt.getString("driverName");
                                        sdriverlicense = driverdt.getString("driverDLNum");
                                        String dlvalidfromdt = driverdt.getString("driverDLValidFrom");
                                        String dlvalidenddt = driverdt.getString("driverDLValidTill");
                                        SimpleDateFormat formatdldt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                        try {
                                            Date datecommencingfrom = formatdldt.parse(dlvalidfromdt);
                                            Date dateend = formatdldt.parse(dlvalidenddt);

                                            SimpleDateFormat formattecommencingdate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

                                            slicencevalidfrm = formattecommencingdate.format(Date.parse(String.valueOf(datecommencingfrom)));
                                            slicencevalidto = formattecommencingdate.format(Date.parse(String.valueOf(dateend)));


                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        if (driverdt.getString("mobileNo") == "null") {
                                            sdriverphno = "";
                                        } else {
                                            sdriverphno = driverdt.getString("mobileNo");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                        sdrivername = "-";
                                        sdriverlicense = "-";
                                        sdriverphno = "-";
                                        slicencevalidfrm = "-";
                                        slicencevalidto = "-";
                                    }


                                    JsonParser parser = new JsonParser();
                                    JsonObject jsonObjectassesor = parser.parse(String.valueOf(claimResponseList)).getAsJsonObject();
                                    boolean assesor = jsonObjectassesor.has("assessor");
                                    JsonObject jsonObjectgarage = parser.parse(String.valueOf(claimResponseList)).getAsJsonObject();
                                    boolean garage = jsonObjectgarage.has("garage");
                                    JsonObject jsonObjectinvestigate = parser.parse(String.valueOf(claimResponseList)).getAsJsonObject();
                                    boolean investigate = jsonObjectinvestigate.has("investigator");
                                    if (assesor) {
                                        String valuenullassesor = claimResponseList.getString("assessor");
                                        if (valuenullassesor == "null") {

                                        } else {
                                            assesoruse = true;
                                            assessordt = claimResponseList.getJSONObject("assessor");
                                        }

                                    }

                                    if (garage) {
                                        String valuenullgarage = claimResponseList.getString("garage");
                                        if (valuenullgarage == "null") {

                                        } else {
                                            garrageuse = true;
                                            garagedt = claimResponseList.getJSONObject("garage");
                                        }

                                    }
                                    if (investigate) {
                                        String valuenullinvestigate = claimResponseList.getString("garage");
                                        if (valuenullinvestigate == "null") {

                                        } else {
                                            investigateuse = true;
                                            investigatordt = claimResponseList.getJSONObject("investigator");
                                        }

                                    }


                                    sincident = claimResponseList.getString("id");
                                    sincidenttype = claimResponseList.getString("claimType");
                                    slocation = AESCrypt.decrypt(claimResponseList.getString("incLocation"));
                                    scoordinate = "0";
                                    //-scoordinate = claimObj1.getString("incidentUniqueCode");


                                    String dtStart1 = claimResponseList.getString("createdOn");
                                    SimpleDateFormat formatstart = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    try {
                                        Date datefrompolicy = formatstart.parse(dtStart1);

                                        SimpleDateFormat formattedate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

                                        sdatetime = formattedate.format(Date.parse(String.valueOf(datefrompolicy)));


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }


                                    if (claimResponseList.getBoolean("isSubmitted")) {
                                        sstatus = "Submitted";
                                    } else {
                                        sstatus = "Not Submitted";
                                    }

                                    spolicy = policydt.getString("policyNo");
                                    scovertype = policydt.getString("coverageType");
                                    sclaimtype = policydt.getString("certificateType");
                                    sname = policydt.getString("insuredName");

                                    String commencingdt = policydt.getString("policyBeginDate");
                                    String enddt = policydt.getString("policyEndDate");
                                    SimpleDateFormat formatcommencingdt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    try {
                                        Date datecommencingfrom = formatcommencingdt.parse(commencingdt);
                                        Date dateend = formatcommencingdt.parse(enddt);

                                        SimpleDateFormat formattecommencingdate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

                                        scommencingdt = formattecommencingdate.format(Date.parse(String.valueOf(datecommencingfrom)));
                                        senddt = formattecommencingdate.format(Date.parse(String.valueOf(dateend)));


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }

                                    // scommencingdt = policydt.getString("policyBeginDate");
                                    //senddt = policydt.getString("policyEndDate");
                                    sphno = policydt.getString("insuredPhoneNo");
                                    semail = policydt.getString("insuredMailId");
                                    sregno = policydt.getString("registrationNo");
                                    scertificateno = policydt.getString("certificateNo");
                                    scapacity = "0";
                                    smake = policydt.getString("make");
                                    smodel = policydt.getString("model");
                                    syear = policydt.getString("yearOfMfg");


                                    sclaimuser = "";

                                    if (assesoruse) {
                                        sassessor = assessordt.getString("entityName");
                                    } else {
                                        sassessor = "-";
                                    }
                                    if (garrageuse) {
                                        sgarage = garagedt.getString("entityName");
                                    } else {
                                        sgarage = "-";
                                    }
                                    if (investigateuse) {
                                        sinvestigator = investigatordt.getString("entityName");
                                    } else {
                                        sinvestigator = "-";
                                    }

                                    if (claimResponseList.getString("isCallAmbulance") == "null" || claimResponseList.getString("isCallAmbulance") == "false") {
                                        sambulance = "No";
                                    } else {
                                        sambulance = "Yes";
                                        //    sambulance = claimResponseList.getString("isCallAmbulance");
                                    }
                                    if (claimResponseList.getString("isCallTowing") == "null" || claimResponseList.getString("isCallTowing") == "false") {
                                        stowing = "No";
                                    } else {
                                        stowing = "Yes";
                                        //  stowing = claimResponseList.getString("isCallTowing");
                                    }

                                    if (claimResponseList.getString("isCallPolice") == "null" || claimResponseList.getString("isCallTowing") == "false") {

                                        scalpolice = "No";
                                    } else {
                                        scalpolice = "Yes";
                                        //   scalpolice = claimResponseList.getString("isCallPolice");
                                    }


                                    requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (progressdialog.isShowing()) {
                                                progressdialog.dismiss();
                                            }
                                            txtincident.setText(sincident);
                                            txtincidenttype.setText(sincidenttype);
                                            //  txtlocation.setText(slocation);
                                            locationmap = slocation;
                                            //   txtcoordinate.setText(scoordinate);
                                            txtdatetime.setText(sdatetime);
                                            txtstatus.setText(sstatus);
                                            txtpolicy.setText(spolicy);
                                            txtcovertype.setText(scovertype);
                                            txtclaimtype.setText(sclaimtype);
                                            txtname.setText(sname);
                                            txtcommencingdt.setText(scommencingdt);
                                            txtenddt.setText(senddt);
                                            txtphno.setText(sphno);
                                            txtemail.setText(semail);
                                            txtregno.setText(sregno);
                                            txtcertificateno.setText(scertificateno);
                                            txtcapacity.setText(scapacity);
                                            txtmake.setText(smake);
                                            txtmodel.setText(smodel);
                                            txtyear.setText(syear);
                                            txtdrivername.setText(sdrivername);
                                            txtdriverlicense.setText(sdriverlicense);
                                            txtlicencevalidfrm.setText(slicencevalidfrm);
                                            txtlicencevalidto.setText(slicencevalidto);
                                            txtdriverphno.setText(sdriverphno);
                                            txtclaimuser.setText(sclaimuser);
                                            txtgarage.setText(sgarage);
                                            txtassessor.setText(sassessor);
                                            txtinvestigator.setText(sinvestigator);
                                            txtambulance.setText(sambulance);
                                            txttowing.setText(stowing);
                                            txtcalpolice.setText(scalpolice);
                                            //Intent step2 = new Intent(CreateDriver.this, DriverMapping.class);
                                            // startActivity(step2);

                                        }
                                    });
                                } else {
                                    try {
                                        requireActivity().runOnUiThread(progressdialog::dismiss);
                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                        JSONObject index = rmsg.getJSONObject(0);
                                        requireActivity().runOnUiThread(() -> {
                                            String errorText;
                                            try {
                                                errorText = index.getString("errorText");
                                                AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
                                                alert.setCancelable(false);
                                                alert.setMessage(errorText);
                                                alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                                alert.show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }
                                        });
                                    } catch (Exception e) {
                                        requireActivity().runOnUiThread(progressdialog::dismiss);
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }

                                }
                            } catch (final Exception e) {
                                requireActivity().runOnUiThread(() -> {
                                    if (progressdialog.isShowing())
                                        progressdialog.dismiss();
                                });
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(),
                                        e.toString(), Toast.LENGTH_LONG).show());
                            }
                        }

                    });
                    thread.start();
                }
            } else {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), getString(R.string.nonetwork), Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show());
        }
    }

    public void getclaimpdf() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(() -> {
                    mydb = new DatabaseHelper(requireActivity());
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                MainActivity.stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                    SharedPreferences claimPref = requireActivity().getSharedPreferences("ClaimDetailsView", Context.MODE_PRIVATE);
                    String incidenid = claimPref.getString(CustomClaimHistoryList.ClaimrefID, "");
                    MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/ViewClaimPDF";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .build();
                    JsonObject Details = new JsonObject();
                    try {
                        //  Details.addProperty("incidentUniqueCode", ClaimType.CraIdval);
                        Details.addProperty("incidentUniqueCode", incidenid);
                        Details.addProperty("typeOfPDF", typeidvalue);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    String insertString = Details.toString();
                    RequestBody body = RequestBody.create(JSON, insertString);
                    Request request = new Request.Builder()
                            .url(MainActivity.postURL)
                            .header("Authorization", "Bearer " + MainActivity.stokenval)
                            .header("MobileParameter", MainActivity.InsertMobileParameters())
                            .post(body)
                            .build();
                    Response staticResponse;
                    try {
                        requireActivity().runOnUiThread(() ->
                                progressdialog = ProgressDialog.show(requireActivity(), "Loading", "Please wait...", true));
                        staticResponse = client.newCall(request).execute();
                        assert staticResponse.body() != null;
                        String staticRes = staticResponse.body().string();
                        Log.i(null, staticRes);
                        final JSONObject staticJsonObj = new JSONObject(staticRes);
                        try {
                            reqidval = staticJsonObj.getString("reqID");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        if (staticJsonObj.getInt("rcode") == 1) {
                            requireActivity().runOnUiThread(() -> {
                                if (progressdialog.isShowing())
                                    progressdialog.dismiss();
                                String pdfurldoc = null;
                                String bimaYanguDocUrl = null;
                                try {
                                    pdfurldoc = staticJsonObj.getJSONObject("rObj").getString("blobDownloadURL");
                                    bimaYanguDocUrl = staticJsonObj.getJSONObject("rObj").getString("memberblobDownloadURL");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                downloadfile((typeidvalue.equals("1")? pdfurldoc: bimaYanguDocUrl));
                            });

                        } else if (staticJsonObj.getInt("rcode") == 2) {

                            requireActivity().runOnUiThread(() -> {
                                if (progressdialog.isShowing()) {
                                    progressdialog.dismiss();
                                }
                                Toast.makeText(requireActivity(), "PDF is generating", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            try {
                                requireActivity().runOnUiThread(progressdialog::dismiss);
                                JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                JSONObject index = rmsg.getJSONObject(0);
                                requireActivity().runOnUiThread(() -> {
                                    String errorText;
                                    try {
                                        errorText = index.getString("errorText");
                                        AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
                                        alert.setCancelable(false);
                                        alert.setMessage(errorText);
                                        alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                        alert.show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                });
                            } catch (Exception e) {
                                requireActivity().runOnUiThread(progressdialog::dismiss);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                        }
                    } catch (final IOException | JSONException e) {
                        requireActivity().runOnUiThread(() -> {
                            if (progressdialog.isShowing())
                                progressdialog.dismiss();

                        });
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                });
                thread.start();
            } else {
                Toast.makeText(requireActivity(), getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

}