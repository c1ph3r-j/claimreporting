package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OfflineProgressDialogUpload extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static String incident_id, certnumval;
    public static String reqidval;
    public static RequestBody body;
    public static FirebaseCrashlytics mCrashlytics;
    public ProgressDialog progressdialog;
    public Bitmap insurer;
    public String sdocumentType;

    int countvalidatenew, countvalidateincr;

    Activity activity;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String localToGMT() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        String finalDateString = "";
        String validfromdate = "";
        try {
            String value = Instant.now().toString();
            String dtStart = value;
            SimpleDateFormat formatstart = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                Date datefrompolicy = formatstart.parse(dtStart);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                finalDateString = formatter.format(Date.parse(String.valueOf(datefrompolicy)));
            } catch (ParseException e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            // localToGMTNew();
        }
        return finalDateString;
        //String value = Instant.now().toString();
//        DateFormat dftime = DateFormat.getTimeInstance();
//        DateFormat dfdate = DateFormat.getDateInstance();
//        dftime.setTimeZone(TimeZone.getTimeZone("gmt"));
//        dfdate.setTimeZone(TimeZone.getTimeZone("gmt"));
//        String gmtTime = dfdate.format(new Date()) + " " + dftime.format(new Date());
//
//        String strDate = gmtTime;
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss aaa");
//        Date convertedDate = new Date();
//        String finalDateString = "";
//        try {
//            convertedDate = dateFormat.parse(strDate);
//            SimpleDateFormat sdfnewformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
//            finalDateString = sdfnewformat.format(convertedDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            localToGMTNew();
//        }
        //return finalDateString;
    }

    public static Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            //image.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "getBitmap", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return bitmap;
    }

    public static void uploadClaimImages(Activity activity, String imageString) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        AsyncTask.execute(

                new Runnable() {

                    @Override
                    public void run() {

                        try {
                            String stokenval = "";
                            String udiduniversal = UUID.randomUUID().toString();
                            mydb = new DatabaseHelper(activity);
                            if (mydb.getTokendetails().getCount() != 0) {
                                Cursor curseattachtoken = mydb.getTokendetails();
                                int counttoken = curseattachtoken.getCount();
                                if (counttoken >= 1) {
                                    while (curseattachtoken.moveToNext()) {
                                        stokenval = curseattachtoken.getString(1);
                                    }
                                }
                            }
                            final MediaType mediaType = MediaType.parse("image/jpeg");
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            Bitmap bitmap = getBitmap(imageString);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                            Log.i(null, String.valueOf(bitmap.getByteCount()));
                            byte[] byteArray = stream.toByteArray();
                            SharedPreferences modeldoctypePref = activity.getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                            String cardocuniq_id = modeldoctypePref.getString(CarView.doctypeid, "");
                            String imgData = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            SharedPreferences incidePref = activity.getSharedPreferences("CRAID", MODE_PRIVATE);
//                            String incident_id = incidePref.getString(ClaimRegFragment.CraIdval,"");
//                             SharedPreferences modeldoctypePref = activity.getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
//                            String cardocuniq_id = modeldoctypePref.getString(CarView.doctypeid,"");
                            String incidentId = incidePref.getString("CraIdval", "");
                            SharedPreferences locationPref = activity.getSharedPreferences("LocationPref", MODE_PRIVATE);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                            String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
                            MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Claim/UploadClaimFiles";
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
                            RequestBody body = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart(
                                            "Images", "Signature.jpg",
                                            RequestBody.create(mediaType, byteArray))
                                    .addFormDataPart("incidentUniqueCode", incidentId)
                                    .addFormDataPart("documentType", cardocuniq_id)
                                    .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                    .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                    .addFormDataPart("captureAttachmentID", udiduniversal)
                                    .addFormDataPart("captureDateTime", dateTime)
                                    .build();
                            Request request = new Request.Builder()
                                    .url(MainActivity.postURL)
                                    .method("POST", body)
                                    .addHeader("Authorization", "Bearer " + stokenval)
                                    .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
                                    .build();
                            Response staticResponse = null;
                            try {
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
                                    if (cardocuniq_id.equals("3a6ed13f-0cf3-4855-9378-9fa18781dafe")) {
//                                        MainActivity.MobileErrorLog(reqidval,"Stolen-Flow",staticJsonObj.getJSONObject("rObj").getString("AttachmentRefNo"),"Stolen Theft Captured Images");
                                    }

                                }
                            } catch (final IOException e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
//                                MainActivity.MobileErrorLog(reqidval,"ClaimImage-uploadclaimimages",e.toString(),e.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_progress_dialog_upload);
        preventSizeChange(this, getSupportActionBar());
        getSupportActionBar().setTitle("Offline Claim Registration");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        activity = this;
        final Handler handler = new Handler();
        try {
            handler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        mydb = new DatabaseHelper(OfflineProgressDialogUpload.this);
                        Cursor curseattachtoken = mydb.getinsertloceattachment();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken == MainActivity.offlinedamagecountnew) {

                            pushvideo();
                            //Intent car = new Intent(OfflineProgressDialogUpload.this, ClaimTermsConditions.class);
                            return;
                        }

                        handler.postDelayed(this, 5000); //now is every 2 minutes
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }
            }, 5000); //Every 120000 ms (2 minutes)
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            return false;

        } else {
            return true;
        }

    }

    public byte[] videoconvert(String path) throws IOException {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        FileInputStream fis = null;
        // FileInputStream fis = new FileInputStream(new File(yourUri));
        //byte[] buf = new byte[1024];
        //byte[] videoBytes = baos.toByteArray();
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        /*for (int readNum; (readNum = fis.read(b)) != -1;) {
            bos.write(buf, 0, readNum);
        }*/
        int n;
        while (-1 != (n = fis.read(buf)))
            bos.write(buf, 0, n);
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    public void pushvideo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            SharedPreferences sharedPreferencenew = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
            String filepath = sharedPreferencenew.getString("videofilepath", "");
            if (filepath.equals("")) {
                pushlocationvideo();
            } else {
                if (isNetworkConnected()) {

                    if (checkGPSStatus()) {
                        mydb = new DatabaseHelper(OfflineProgressDialogUpload.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    MainActivity.stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                        Thread thread = new Thread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            public void run() {
                                certnumval = "";
                                SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
                                incident_id = incidePref.getString(ClaimType.CraIdval, "");
                                SharedPreferences videonPref = getSharedPreferences("VideoFile", MODE_PRIVATE);
                                final String filename = videonPref.getString("videofilepath", "");
                                SharedPreferences locationPref = getSharedPreferences("LocationPref", MODE_PRIVATE);
                                MediaType mediaType = MediaType.parse("application/octet-stream");
                                try {
                                    byte[] byteArray = videoconvert(filename);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                String uniqueID = UUID.randomUUID().toString();
                                // SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                                String dateTime = localToGMT();
                                MainActivity.postURL = getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                                OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(120, TimeUnit.SECONDS)
                                        .writeTimeout(120, TimeUnit.SECONDS)
                                        .readTimeout(120, TimeUnit.SECONDS)
                                        .build();
                                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                        .addFormDataPart("fileName", filename)
                                        .addFormDataPart(
                                                "image", filename,
                                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                                        new File(filename)))
                                        .addFormDataPart("certificateNo", certnumval)
                                        .addFormDataPart("incidentUniqueCode", incident_id)
                                        .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                        .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                        .addFormDataPart("captureAttachmentID", uniqueID)
                                        .addFormDataPart("captureDateTime", dateTime)
                                        //.addFormDataPart("attachmentTypeID","5c6cf73c-d686-44d5-824c-f7d9ec7a7f30")
                                        .addFormDataPart("attachmentTypeID", "5c6cf73c-d686-44d5-824c-f7d9ec7a7f30")
                                        .build();
                                Request request = new Request.Builder()
                                        .url(MainActivity.postURL)
                                        .method("POST", body)
                                        .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                        .header("MobileParameter", MainActivity.InsertMobileParameters())
                                        .build();
                                Response staticResponse = null;
                                try {

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
                                        runOnUiThread(() -> pushlocationvideo());
                                    } else {
                                        try {
                                            runOnUiThread(progressdialog::dismiss);
                                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                            JSONObject index = rmsg.getJSONObject(0);
                                            runOnUiThread(() -> {
                                                String errorText;
                                                try {
                                                    errorText = index.getString("errorText");
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(OfflineProgressDialogUpload.this);
                                                    alert.setCancelable(false);
                                                    alert.setMessage(errorText);
                                                    alert.setNegativeButton("Ok", (dialog, which) -> {
                                                        pushlocationvideo();
                                                        dialog.dismiss();
                                                    });
                                                    alert.show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    }

                                } catch (final IOException | JSONException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //  progressdialog.dismiss();
                                        }
                                    });
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            }
                        });
                        thread.start();
                    } else {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(OfflineProgressDialogUpload.this);
                        dialog.setMessage("GPS locations is not enabled.Please enable it");
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //this will navigate user to the device location settings screen
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                        android.app.AlertDialog alert = dialog.create();
                        alert.show();
                    }
                } else {
//                                Toast.makeText(this,getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
                }
            }


        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // progressdialog.dismiss();
                }
            });
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


    }

    public void pushlocationvideo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            SharedPreferences sharedPreferencenew = getSharedPreferences("VideoFileLocation", Context.MODE_PRIVATE);
            String filepath = sharedPreferencenew.getString("videofilepathlocation", "");
            if (filepath.equals("")) {
                pushimagevisual();
            } else {
                if (isNetworkConnected()) {

                    if (checkGPSStatus()) {
                        mydb = new DatabaseHelper(OfflineProgressDialogUpload.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    MainActivity.stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                        Thread thread = new Thread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            public void run() {
                                certnumval = "";
                                SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
                                incident_id = incidePref.getString(ClaimType.CraIdval, "");
                                SharedPreferences videonPref = getSharedPreferences("VideoFileLocation", MODE_PRIVATE);
                                final String filename = videonPref.getString("videofilepathlocation", "");
                                SharedPreferences locationPref = getSharedPreferences("LocationPref", MODE_PRIVATE);
                                MediaType mediaType = MediaType.parse("application/octet-stream");
                                Log.i("IncidentId", incident_id + locationPref.getString(MainActivity.Latitude, null) + locationPref.getString(MainActivity.Latitude, null) + " val");
                                try {
                                    byte[] byteArray = videoconvert(filename);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                String uniqueID = UUID.randomUUID().toString();
                                // SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                                String dateTime = localToGMT();
                                MainActivity.postURL = getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                                OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(120, TimeUnit.SECONDS)
                                        .writeTimeout(120, TimeUnit.SECONDS)
                                        .readTimeout(120, TimeUnit.SECONDS)
                                        .build();
                                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                        .addFormDataPart("fileName", filename)
                                        .addFormDataPart(
                                                "image", filename,
                                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                                        new File(filename)))
                                        .addFormDataPart("certificateNo", certnumval)
                                        .addFormDataPart("incidentUniqueCode", incident_id)
                                        .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                        .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                        .addFormDataPart("captureAttachmentID", uniqueID)
                                        .addFormDataPart("captureDateTime", dateTime)
                                        //.addFormDataPart("attachmentTypeID","5c6cf73c-d686-44d5-824c-f7d9ec7a7f30")
                                        .addFormDataPart("attachmentTypeID", "e1f91e22-4b56-4f35-9ae7-9ab831d8a91f")
                                        .build();
                                Request request = new Request.Builder()
                                        .url(MainActivity.postURL)
                                        .method("POST", body)
                                        .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                        .header("MobileParameter", MainActivity.InsertMobileParameters())
                                        .build();
                                Response staticResponse = null;
                                try {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        progressdialog = ProgressDialog.show(OfflineProgressDialogUpload.this, "Loading", "Please wait...", true);
//                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
//                                    }
//                                });
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
                                        //MainActivity.MobilInfolog(OfflineProgressDialogUpload.this,reqidval,"VideoKYC-pushvideo",staticRes,staticRes);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                pushimagevisual();

                                                //  progressdialog.dismiss();
                                                //     Intent car = new Intent(OfflineProgressDialogUpload.this, ClaimTermsConditions.class);
                                                //  startActivity(car);
                                                //  finish();
//                                            SharedPreferences incitype = getSharedPreferences("IncidentType",MODE_PRIVATE);
//                                            String incidenttypeval = incitype.getString(ClaimType.typeidincident,"");
//                                            if(incidenttypeval == "B2EC755A-88EF-4F53-8911-C13688D226D3")
//                                            {
//                                                SharedPreferences thirdisPref = getSharedPreferences("IsThirdParty",MODE_PRIVATE);
//                                                String isthird = thirdisPref.getString(ClaimType.Thirdpartyavailable,"");
//                                                if(isthird.equals("1"))
//                                                {
//
//                                                }
//                                                else
//                                                {
//                                                    Intent signUpIntent = new Intent(OfflineProgressDialogUpload.this, StolenImageDeclaration.class);
//                                                    startActivity(signUpIntent);
//                                                }
//
//                                            }
//                                            else
//                                            {
//                                                Intent signUpIntent = new Intent(OfflineProgressDialogUpload.this, ImageDeclaration.class);
//                                                startActivity(signUpIntent);
//                                            }
                                            }
                                        });
                                    } else {
                                        try {
                                            runOnUiThread(progressdialog::dismiss);
                                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                            JSONObject index = rmsg.getJSONObject(0);
                                            runOnUiThread(() -> {
                                                String errorText;
                                                try {
                                                    errorText = index.getString("errorText");
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(OfflineProgressDialogUpload.this);
                                                    alert.setCancelable(false);
                                                    alert.setMessage(errorText);
                                                    alert.setNegativeButton("Ok", (dialog, which) -> {
                                                        pushimagevisual();
                                                        dialog.dismiss();
                                                    });
                                                    alert.show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    }

                                } catch (final IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pushimagevisual();
                                            //  progressdialog.dismiss();
                                        }
                                    });
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                } catch (JSONException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pushimagevisual();
                                            //  progressdialog.dismiss();
                                        }
                                    });
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            }
                        });
                        thread.start();
                    } else {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(OfflineProgressDialogUpload.this);
                        dialog.setMessage("GPS locations is not enabled.Please enable it");
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //this will navigate user to the device location settings screen
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                        android.app.AlertDialog alert = dialog.create();
                        alert.show();
                    }
                } else {
//                                Toast.makeText(this,getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
                }
            }


        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // progressdialog.dismiss();
                }
            });
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


    }

    public void pushimagevisual() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            SharedPreferences sharedPreferencenew = getSharedPreferences("VisualImageFile", Context.MODE_PRIVATE);
            String filepath = sharedPreferencenew.getString("visimagefilepath", "");
            if (filepath.equals("")) {
                pushaudiovisual();
            } else {

                if (isNetworkConnected()) {

                    if (checkGPSStatus()) {
                        mydb = new DatabaseHelper(OfflineProgressDialogUpload.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    MainActivity.stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }

                        Thread thread = new Thread(new Runnable() {

                            public void run() {

                                SharedPreferences locationPref = getSharedPreferences("LocationPref", Context.MODE_PRIVATE);

                                SharedPreferences incidePref = activity.getSharedPreferences("CRAID", MODE_PRIVATE);
                                String incident_id = incidePref.getString(ClaimType.CraIdval, "");
                                SharedPreferences driverPref = activity.getSharedPreferences("DriverID", MODE_PRIVATE);
                                String driver_id = driverPref.getString("DriverUniqueID", "");
                                SharedPreferences certifPref = activity.getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                                String certnum = certifPref.getString(ClaimType.CertificateID, "");
                                String vehicrefid = certifPref.getString("Vechilerefid", "");
                                //MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/AddClaim";
                                SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                                String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
                                insurer = getBitmap(filepath);
                                Bitmap bitmapnew = insurer;
                                ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
                                bitmapnew.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
//                                String outStr = ObjectDetectorActivity.Global.outputStr;
                                byte[] byteArraynew = streamnew.toByteArray();
                                String latval = locationPref.getString(MainActivity.Latitude, null);
                                String longval = locationPref.getString(MainActivity.Longitude, null);
                                final MediaType mediaType = MediaType.parse("image/jpeg");
                                //    String imgData = Base64.encodeToString(byteArray, Base64.DEFAULT);
                                //  String imgDatanew =imgData;
                                String dateTime = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    dateTime = localToGMT();
                                }
                                String uniqueID = UUID.randomUUID().toString();
                                sdocumentType = "b6d1a613-9242-4b6a-b0c4-39b80d72cbe8";
                                MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                                OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(120, TimeUnit.SECONDS)
                                        .writeTimeout(120, TimeUnit.SECONDS)
                                        .readTimeout(120, TimeUnit.SECONDS)
                                        .build();
                                RequestBody body = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("fileName", uniqueID)
                                        .addFormDataPart(
                                                "image", uniqueID,
                                                RequestBody.create(mediaType, byteArraynew))
                                        .addFormDataPart("certificateNo", certnum)
                                        .addFormDataPart("incidentUniqueCode", incident_id)
                                        .addFormDataPart("attachmentTypeID", sdocumentType)
                                        .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                        .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                        .addFormDataPart("captureAttachmentID", uniqueID)
                                        .addFormDataPart("captureDateTime", dateTime)
                                        .build();
                                Request request = new Request.Builder()
                                        .url(MainActivity.postURL)
                                        .method("POST", body)
                                        .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                        .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
                                        .build();
                                Response staticResponse = null;
                                try {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progressdialog = ProgressDialog.show(OfflineProgressDialogUpload.this, "Loading", "Please wait...", true);
//                                            // progressdialog.show(activity, "Loading", "Please wait...", true);
//                                        }
//                                    });
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // progressdialog.dismiss();
                                                pushaudiovisual();
                                            }
                                        });

                                    } else {
                                        try {
                                            runOnUiThread(progressdialog::dismiss);
                                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                            JSONObject index = rmsg.getJSONObject(0);
                                            runOnUiThread(() -> {
                                                String errorText;
                                                try {
                                                    errorText = index.getString("errorText");
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(OfflineProgressDialogUpload.this);
                                                    alert.setCancelable(false);
                                                    alert.setMessage(errorText);
                                                    alert.setNegativeButton("Ok", (dialog, which) -> {
                                                        pushaudiovisual();
                                                        dialog.dismiss();
                                                    });
                                                    alert.show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }

                                    }
                                } catch (final IOException | JSONException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // progressdialog.dismiss();
                                        }
                                    });
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }

                            }
                        });
                        thread.start();


                    } else {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(OfflineProgressDialogUpload.this);
                        dialog.setMessage("GPS locations is not enabled.Please enable it");
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //this will navigate user to the device location settings screen
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                        android.app.AlertDialog alert = dialog.create();
                        alert.show();
                    }


                } else {
                    Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                }

            }
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // progressdialog.dismiss();
                }
            });
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    public void pushaudiovisual() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            SharedPreferences sharedPreferencenew = getSharedPreferences("VisualAudioFile", Context.MODE_PRIVATE);
            String filepath = sharedPreferencenew.getString("visaudiofilepath", "");
            if (filepath.equals("")) {
                mydb = new DatabaseHelper(OfflineProgressDialogUpload.this);
                Cursor curseattachtoken = mydb.getclaimofflineiddetails();
                int counttoken = curseattachtoken.getCount();
                if (counttoken >= 1) {
                    MainActivity.offlinedamagecount = 0;
                    MainActivity.offlinedamagecountnew = 0;
                    mydb.deleteclaimofflineiddata();
//                    Intent signUpIntent = new Intent(OfflineProgressDialogUpload.this, ImageDeclaration.class);
//                    startActivity(signUpIntent);

                }
                pushfinalsignature();
            } else {
                mydb = new DatabaseHelper(OfflineProgressDialogUpload.this);
                if (mydb.getTokendetails().getCount() != 0) {
                    Cursor curseattachtoken = mydb.getTokendetails();
                    int counttoken = curseattachtoken.getCount();
                    if (counttoken >= 1) {
                        while (curseattachtoken.moveToNext()) {
                            MainActivity.stokenval = curseattachtoken.getString(1);
                        }
                    }
                }
                SharedPreferences certifPref = getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                String certnum = certifPref.getString(ClaimType.CertificateID, "");
                SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
                String incident_id = incidePref.getString(ClaimType.CraIdval, "");
                sdocumentType = "064b1ed5-a782-41ba-ba78-d73a443a08c2";
                String uniqueID = UUID.randomUUID().toString();
                final String filename = String.valueOf(filepath);
                SharedPreferences locationPref = getSharedPreferences("LocationPref", MODE_PRIVATE);
                // SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                String dateTime = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dateTime = localToGMT();
                }

                MediaType mediaType = MediaType.parse("application/octet-stream");
                //MainActivity.postURL = "https://uat-aki.claims.digital/api/app/Claim/UploadClaimFiles";
                MainActivity.postURL = getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                //MainActivity.postURL = getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .build();

                body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("fileName", filename)
                        .addFormDataPart(
                                "image", filename,
                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                        new File(filename)))
                        .addFormDataPart("certificateNo", certnum)
                        .addFormDataPart("incidentUniqueCode", incident_id)
                        .addFormDataPart("attachmentTypeID", sdocumentType)
                        .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                        .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                        .addFormDataPart("captureAttachmentID", uniqueID)
                        .addFormDataPart("captureDateTime", dateTime)
                        .build();
                Request request = new Request.Builder()
                        .url(MainActivity.postURL)
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                        .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
                        .build();
                Response staticResponse = null;
                try {

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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //  progressdialog.dismiss();
                                pushfinalsignature();

                            }
                        });
                        // pushimageclaim();
                    } else {
                        try {
                            runOnUiThread(progressdialog::dismiss);
                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                            JSONObject index = rmsg.getJSONObject(0);
                            runOnUiThread(() -> {
                                String errorText;
                                try {
                                    errorText = index.getString("errorText");
                                    AlertDialog.Builder alert = new AlertDialog.Builder(OfflineProgressDialogUpload.this);
                                    alert.setCancelable(false);
                                    alert.setMessage(errorText);
                                    alert.setNegativeButton("Ok", (dialog, which) -> {
                                        pushfinalsignature();
                                        dialog.dismiss();
                                    });
                                    alert.show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    }
                } catch (final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //   progressdialog.dismiss();
                        }
                    });
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            }
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // progressdialog.dismiss();
                }
            });
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    public void pushfinalsignature() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        mydb = new DatabaseHelper(OfflineProgressDialogUpload.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    MainActivity.stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                        countvalidateincr = countvalidatenew;
                        SharedPreferences sharedPreferences = getSharedPreferences("stolenImageOfflinePref", MODE_PRIVATE);
                        String bitmapVal = sharedPreferences.getString("SignatureBitmapPref", "NoVal");
                        byte[] byteArray = bitmapVal.getBytes();

//                        Toast.makeText(activity, byteArray.toString() + " ", Toast.LENGTH_SHORT).show();

                        SharedPreferences certifPref = getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                        String certnum = certifPref.getString(ClaimType.CertificateID, "");
                        SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
                        String incident_id = incidePref.getString(ClaimType.CraIdval, "");
                        sdocumentType = "b682f6dc-a040-4210-83e9-d696c4af15c1";
                        String uniqueID = UUID.randomUUID().toString();

                        final String filename = sharedPreferences.getString("AudioPathPref", "NoVal");
                        SharedPreferences locationPref = getSharedPreferences("LocationPref", MODE_PRIVATE);
                        // SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");

                        String dateTime = localToGMT();
                        final MediaType mediaType = MediaType.parse("image/jpeg");
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        // MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/UploadClaimFiles";
                        MainActivity.postURL = getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
//            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                    .addFormDataPart(
//                            "Images","Signature.jpg",
//                            RequestBody.create(mediaType, byteArray))
//                    .addFormDataPart("certificateNo",certnum)
//                    .addFormDataPart("incidentUniqueCode",incident_id)
//                    .addFormDataPart("documentType",sdocumentType)
//                    .addFormDataPart("geoTagLat",locationPref.getString(MainActivity.Latitude, null))
//                    .addFormDataPart("geoTagLon",locationPref.getString(MainActivity.Longitude, null))
//                    .addFormDataPart("captureAttachmentID", uniqueID)
//                    .addFormDataPart("captureDateTime",dateTime)
//                    .build();

                        RequestBody body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("fileName", "Signature.jpg")
                                .addFormDataPart(
                                        "image", "Signature.jpg",
                                        RequestBody.create(mediaType, byteArray))
                                .addFormDataPart("certificateNo", certnum)
                                .addFormDataPart("incidentUniqueCode", incident_id)
                                .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                .addFormDataPart("captureAttachmentID", uniqueID)
                                .addFormDataPart("captureDateTime", dateTime)
                                .addFormDataPart("attachmentTypeID", sdocumentType)
                                .addFormDataPart("isReturnURL", "false")
                                .build();

//                                RequestBody body
//                                RequestBody body = new MultipartBody.Builder()
//                                        .setType(MultipartBody.FORM)
//                                        .addFormDataPart(
//                                                "Images","CertificateNumber.jpg",
//                                                RequestBody.create(mediaType, byteArray))
//                                        .addFormDataPart("certificateNo","4583363")
//                                        .addFormDataPart("documentType","werdfs")
//                                        .build();
                        Request request = new Request.Builder()
                                .url(MainActivity.postURL)
                                .method("POST", body)
                                .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
                                .build();
                        Response staticResponse = null;
                        try {

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
                                countvalidatenew = countvalidateincr + 1;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        progressdialog.dismiss();
                                        insertclaimfinal();
                                        //  pushfinalaudio();
                                        //insertclaimfinal();
                                        //                                            MainActivity.MobileErrorLog(reqidval,"StolenSignature",staticJsonObj.getJSONObject("rObj").getString("AttachmentRefNo"),"Stolen Theft Signature");

                                    }
                                });
                            } else {
                                try {
                                    runOnUiThread(progressdialog::dismiss);
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    runOnUiThread(() -> {
                                        String errorText;
                                        try {
                                            errorText = index.getString("errorText");
                                            AlertDialog.Builder alert = new AlertDialog.Builder(OfflineProgressDialogUpload.this);
                                            alert.setCancelable(false);
                                            alert.setMessage(errorText);
                                            alert.setNegativeButton("Ok", (dialog, which) -> {
                                                insertclaimfinal();
                                                dialog.dismiss();
                                            });
                                            alert.show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            }
                        } catch (final IOException | JSONException e) {

                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
//                            MainActivity.MobileErrorLog(reqidval,"ImageDeclaration-pushsignature",ex.toString(),ex.toString());
                        }
                    }
                });
                thread.start();
            } else {
                //  Toast.makeText(getActivity(),getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
//            MainActivity.MobileErrorLog(reqidval,"ImageDeclaration-pushsignature",ex.toString(),ex.toString());
        }
    }

    public void insertclaimfinal() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    progressdialog = new ProgressDialog(this);
                    MainActivity.encryptedSHA = "";
                    String sourceStr = MainActivity.InsertMobileParameters();
                    try {
                        MainActivity.encryptedSHA = AESCrypt.encrypt(sourceStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    Thread thread = new Thread(new Runnable() {

                        public void run() {
                            SharedPreferences locPref = activity.getSharedPreferences("LocationCurrent", MODE_PRIVATE);
                            SharedPreferences incidePref = activity.getSharedPreferences("CRAID", MODE_PRIVATE);
                            String incident_id = incidePref.getString(ClaimType.CraIdval, "");
                            SharedPreferences driverPref = activity.getSharedPreferences("DriverID", MODE_PRIVATE);
                            String driver_id = driverPref.getString("DriverUniqueID", "");
                            SharedPreferences certifPref = activity.getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                            String certnum = certifPref.getString(ClaimType.CertificateID, "");
                            String vehicrefid = certifPref.getString("Vechilerefid", "");
                            MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/AddClaim";
                            SharedPreferences incidentsharedpreferences = getSharedPreferences("IncidentType", Context.MODE_PRIVATE);
                            String incidenttypeval = incidentsharedpreferences.getString(ClaimType.typeidincident, "");
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");

                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
                            JsonObject Details = new JsonObject();
                            Details.addProperty("incidentUniqueCode", incident_id);
                            Details.addProperty("incLocation", locPref.getString(MainActivity.Address1, ""));
                            Details.addProperty("driverUserId", driver_id);
                            Details.addProperty("certificateNo", certnum);
                            Details.addProperty("claimTypeID", incidenttypeval);
                            Details.addProperty("VehicleId", vehicrefid);
                            String insertString = Details.toString();
                            RequestBody body = RequestBody.create(JSON, insertString);
                            Request request = new Request.Builder()
                                    .url(MainActivity.postURL)
                                    .method("POST", body)
                                    .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                    .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
                                    .build();
                            Response staticResponse = null;

                            try {

                                staticResponse = client.newCall(request).execute();
                                String staticRes = staticResponse.body().string();
                                if (staticRes.equals("")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
//                                            progressdialog.dismiss();
                                            Toast.makeText(OfflineProgressDialogUpload.this, "Please contact administrator to proceed and try again", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                } else {
                                    Log.i(null, staticRes);
                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                                    try {
                                        reqidval = staticJsonObj.getString("reqID");
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                    if (staticJsonObj.getInt("rcode") == 1) {
//                                        mydb = new DatabaseHelper(ImageDeclaration.this);
//                                        if(mydb.getclaimstep().getCount() != 0)
//                                        {
//                                            mydb.deleteclaimstep();
//                                        }
//                                        boolean claimstepinserted = mydb.insertclaimstep("ClaimCompleted");
//                                        if(claimstepinserted)
//                                        {
//                                            boolean test = claimstepinserted;
//                                            Log.i(null,"Insertion Done");
//                                        }
//                                        else
//                                        {
//                                            boolean test = claimstepinserted;
//                                            Log.i(null,"Not Insertion Done");
//                                        }
                                        //final JSONObject staticfinalObj = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate");
//                                    final JSONArray staticjsonval = staticJsonObj.getJSONArray("rmsg").getJSONArray(0);
//                                    String rmsgval2 = staticjsonval.getString(1);
//                                    String rmsgval3 = rmsgval2;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //insertclaimpdf();
                                                Intent car = new Intent(OfflineProgressDialogUpload.this, ClaimSuccess.class);
                                                startActivity(car);

                                            }
                                        });
                                    } else {
                                        try {
                                            runOnUiThread(progressdialog::dismiss);
                                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                            JSONObject index = rmsg.getJSONObject(0);
                                            runOnUiThread(() -> {
                                                String errorText;
                                                try {
                                                    errorText = index.getString("errorText");
                                                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(OfflineProgressDialogUpload.this);
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
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    }
                                }

                            } catch (final IOException e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
//                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        Toast.makeText(OfflineProgressDialogUpload.this,
                                                e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (JSONException e) {
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                Toast.makeText(OfflineProgressDialogUpload.this,
                                        e.toString(), Toast.LENGTH_LONG).show();
//                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(OfflineProgressDialogUpload.this);
                    dialog.setMessage(getString(R.string.gps_not_enabled));
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    android.app.AlertDialog alert = dialog.create();
                    alert.show();
                }


            } else {
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
//            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }

    }

    public void pushDamageCapturedImages() {
        DatabaseHelper myDb = new DatabaseHelper(OfflineProgressDialogUpload.this);
        Cursor listOfImages = myDb.getClaimImgmore();
        if (listOfImages.getCount() != 0) {
            listOfImages.moveToFirst();
            while (listOfImages.moveToNext()) {
                uploadClaimImages(this, listOfImages.getString(0));
            }
        }
    }


}