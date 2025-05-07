package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OfflineClaimPending extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static String regnumoff;
    public static TextView txtvechidval;
    public static FirebaseCrashlytics mCrashlytics;
    public static Button butback, butcancel;
    public static String sdocumentType;
    public static byte[] regByte = null;
    public static Bitmap insurer;
    public static SharedPreferences sharedPreferencenew;
    public static RequestBody body;
    public static String imageURL, imagename, incident_id, certnumval;
    public static int caridvalvalid, carnamevalvalid;
    public static String reqidval;
    public static String regnum, membercraid;
    public static String regnumpushimg;
    public ProgressDialog progressdialog;
    Activity activity;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String localToGMT() {

        String finalDateString = "";
        String validfromdate = "";
        try {
            // Get the current date and time in GMT
            Date currentDate = new Date();
            SimpleDateFormat gmtFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US);
            gmtFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

            finalDateString = gmtFormatter.format(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
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

    public static void damageofflinelocalimgproceed(Activity activity) {
        AsyncTask.execute(

                new Runnable() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {

                        try {

                            mydb = new DatabaseHelper(activity);
                            if (mydb.getTokendetails().getCount() != 0) {
                                Cursor curseattachtoken = mydb.getTokendetails();
                                int counttoken = curseattachtoken.getCount();
                                if (counttoken >= 1) {
                                    while (curseattachtoken.moveToNext()) {
                                        MainActivity.stokenval = curseattachtoken.getString(1);
                                    }
                                }
                            }


                            if (mydb.getclaimofflineiddetails().getCount() != 0) {
                                Cursor curseattachtoken = mydb.getclaimofflineiddetails();
                                int counttoken = curseattachtoken.getCount();
                                if (counttoken >= 1) {
                                    while (curseattachtoken.moveToNext()) {
                                        incident_id = curseattachtoken.getString(1);
                                        SharedPreferences sharedpreferences = activity.getSharedPreferences("CRAID", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor craeeditor = sharedpreferences.edit();
                                        craeeditor.putString(ClaimType.CraIdval, incident_id);
                                        craeeditor.commit();
                                    }
                                }
                            }

                            certnumval = "";


                            final MediaType mediaType = MediaType.parse("image/jpeg");


                            if (mydb.getinsertloceattachment().getCount() >= 1) {
                                Cursor curseattachtoken = mydb.getinsertloceattachment();
                                int counttoken = curseattachtoken.getCount();
                                if (counttoken >= 1) {
                                    while (curseattachtoken.moveToNext()) {
                                        MainActivity.offlinedamagecount = 1 + MainActivity.offlinedamagecountnew;

                                        insurer = getBitmap(curseattachtoken.getString(3));
                                        Bitmap bitmapnew = insurer;
                                        ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
                                        bitmapnew.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
//                                        String outStr = ObjectDetectorActivity.Global.outputStr;
                                        byte[] byteArraynew = streamnew.toByteArray();
                                        regByte = byteArraynew;
                                        String imgDatatest = Base64.encodeToString(byteArraynew, Base64.DEFAULT);
                                        String imgData1 = imgDatatest;


                                        String simagename = curseattachtoken.getString(0);
                                        if (simagename.equals("Front bumper")) {
                                            MainActivity.cardocidval = "924409f8-45ee-4190-b528-45c423ab9b33";
                                        } else if (simagename.equals("Hood / Bonnet")) {
                                            MainActivity.cardocidval = "32be5921-65ab-4e53-9c0b-5d2a29e14d44";
                                        } else if (simagename.equals("Windscreen / Windshield")) {
                                            MainActivity.cardocidval = "f3c4b1b6-c503-440a-b31b-f4aa487f5212";
                                        } else if (simagename.equals("Roof top")) {
                                            MainActivity.cardocidval = "a6d93aa2-fed8-43f6-af59-128211a31722";
                                        } else if (simagename.equals("Rear window")) {
                                            MainActivity.cardocidval = "57fc4782-16a9-4941-b3b4-1812582b191d";
                                        } else if (simagename.equals("Boot/Trunk")) {
                                            MainActivity.cardocidval = "1485c11f-6c62-4ceb-9e6e-8405df8ea431";
                                        } else if (simagename.equals("Rear Bumper")) {
                                            MainActivity.cardocidval = "fb386688-c943-4217-8144-90512d5e0529";
                                        } else if (simagename.equals("Left front door")) {
                                            MainActivity.cardocidval = "f960785b-2f51-45cf-863b-e8206e395417";
                                        } else if (simagename.equals("Left rear door")) {
                                            MainActivity.cardocidval = "57b4d294-fba3-46d0-b979-e9a4b59aa450";
                                        } else if (simagename.equals("Rear Left Fender")) {
                                            MainActivity.cardocidval = "16bb1d4e-e989-49db-9684-7b1d8dd66211";
                                        } else if (simagename.equals("Right Front door")) {
                                            MainActivity.cardocidval = "a2927342-d89d-4cf0-80c0-43c7dc7a87b6";
                                        } else if (simagename.equals("Right rear door")) {
                                            MainActivity.cardocidval = "b741ad34-e06b-47a3-b766-9db57e4d1e8e";
                                        } else if (simagename.equals("Rear Right Fender")) {
                                            MainActivity.cardocidval = "a9bdd83a-38d5-454a-bd77-1e4008f22ac2";
                                        } else if (simagename.equals("Front Left Fender")) {
                                            MainActivity.cardocidval = "547af566-11b9-4272-9f44-58797adfd8c8";
                                        } else if (simagename.equals("Front Right Fender")) {
                                            MainActivity.cardocidval = "98f525ce-5e6f-4ad6-a339-35d1a49578fd";
                                        }


                                        MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                                        imagename = "DamageImg_" + UUID.randomUUID().toString() + ".jpg";
//                                        caridvalvalid = caridval;
//                                        carnamevalvalid = carnameval;
                                        //  MainActivity.cardocidval = cardocuniq_doc;
                                        SharedPreferences locationPref = activity.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
                                        String dateTime = localToGMT();
                                        String uniqueID = curseattachtoken.getString(4);

                                        OkHttpClient client = new OkHttpClient.Builder()
                                                .connectTimeout(120, TimeUnit.SECONDS)
                                                .writeTimeout(120, TimeUnit.SECONDS)
                                                .readTimeout(120, TimeUnit.SECONDS)
                                                .build();

                                        body = new MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("fileName", imagename)
                                                .addFormDataPart(
                                                        "image", imagename,
                                                        RequestBody.create(mediaType, byteArraynew))
                                                .addFormDataPart("certificateNo", certnumval)
                                                .addFormDataPart("incidentUniqueCode", incident_id)
                                                .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                                .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                                .addFormDataPart("captureAttachmentID", uniqueID)
                                                .addFormDataPart("captureDateTime", dateTime)
                                                .addFormDataPart("attachmentTypeID", MainActivity.cardocidval)
                                                .addFormDataPart("isReturnURL", "true")
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
                                            } catch (JSONException ex) {
                                                ex.printStackTrace();
                                            }
                                            if (staticJsonObj.getInt("rcode") == 1) {
                                                MainActivity.offlinedamagecountnew = MainActivity.offlinedamagecount;
                                                //MainActivity.MobileErrorLog(reqidval,"Claim Image Detection",staticJsonObj.getJSONObject("rObj").getString("damageDetectionResponse"),"Claim Damage Captured Images");
                                                //  MainActivity.MobilInfolog(activity,reqidval,"Claim Image Detection",staticRes,staticRes);

                                            }
                                        } catch (final IOException e) {
                                            activity.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    e.printStackTrace();
                                                    //Toast.makeText(activity, e.toString() , Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(activity, activity.getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            });
                                            // Toast.makeText(activity,e.toString(), Toast.LENGTH_SHORT).show();
                                        }

                                    }


                                }
                            }


                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    e.printStackTrace();
                                    Toast.makeText(activity, activity.getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            });
                            // Toast.makeText(activity,e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
        }

        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_claim_pending);
        preventSizeChange(this, getSupportActionBar());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Offline Claim");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        activity = this;
        init();
    }

    public void init() {
        try {
            txtvechidval = (TextView) findViewById(R.id.txtvechid);
            butback = (Button) findViewById(R.id.Btnuclaimoffsubmit);
            butcancel = (Button) findViewById(R.id.Btnuclaimcancel);
            SharedPreferences vecidpref = getSharedPreferences("ClaimInsert", MODE_PRIVATE);
            String vechid = vecidpref.getString("Vechilerefid", "");
            mydb = new DatabaseHelper(OfflineClaimPending.this);
            Cursor curseattachtoken = mydb.getspecificvehicledetails(vechid);
            int counttoken = curseattachtoken.getCount();
            if (counttoken >= 1) {
                while (curseattachtoken.moveToNext()) {

                    regnumoff = curseattachtoken.getString(1);

                }
            }
            try {
                txtvechidval.setText("Vehicle Number : " + AESCrypt.decrypt(regnumoff));
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "init", e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                butback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // mydb.deleteclaimofflineiddata();
                        //  mydb.deletealllocalimage();
                        //mydb.deletethirdlocalimage();

                        if (isNetworkConnected()) {
                            getclaimofflineincdentid();
                        } else {
                            Toast.makeText(OfflineClaimPending.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                        }


//                    if(isNetworkConnected())
//                    {
//                        Intent otpIntent = new Intent(OfflineClaimPending.this, OfflineProgressDialogUpload.class);
//                        startActivity(otpIntent);
//                        damageofflinelocalimgproceed(OfflineClaimPending.this);
//                    }
//
//                   else
//                    {
//                         Toast.makeText(OfflineClaimPending.this,getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
//                    }


                        //   Intent otpIntent = new Intent(OfflineClaimPending.this, Dashboard.class);
                        //   startActivity(otpIntent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                butcancel.setOnClickListener(onClickBtnCancel -> {
                    mydb.deleteclaimofflineiddata();
                    mydb.deletealllocalimage();
                    mydb.deletethirdlocalimage();
                    Intent otpIntent = new Intent(OfflineClaimPending.this, Dashboard.class);
                    startActivity(otpIntent);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void getclaimofflineincdentid() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mydb = new DatabaseHelper(OfflineClaimPending.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    MainActivity.stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                        if (mydb.getclaimofflineiddetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getclaimofflineiddetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    incident_id = curseattachtoken.getString(1);
                                }
                            }
                        }

                        MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/GetClaimBySMS";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        // OkHttpClient client = new OkHttpClient();
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        JsonObject Details = new JsonObject();
                        Details.addProperty("incSMSText", incident_id);
                        // Details.addProperty("incSMSText", "12334556");
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(MainActivity.postURL)
                                //.header("Authorization", "Bearer " +"eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w")
                                .header("Authorization", "Bearer " + MainActivity.stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse = null;
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
                                    // progressdialog.show(activity, "Loading", "Please wait...", true);
                                }
                            });
                            staticResponse = client.newCall(request).execute();
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);
                            if (staticJsonObj.getInt("rcode") == 1) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            progressdialog.dismiss();
                                            String claimid = staticJsonObj.getJSONObject("rObj").getString("incidentUniqueCode");
                                            mydb = new DatabaseHelper(OfflineClaimPending.this);
                                            if (mydb.getclaimofflineiddetails().getCount() != 0) {
                                                mydb.deleteclaimofflineiddata();
                                            }
                                            boolean Isinserted = mydb.insertclaimofflineid(claimid);
                                            if (Isinserted) {
                                                boolean test = Isinserted;
                                                Log.i(null, "Insertion Done");
                                            } else {
                                                boolean test = Isinserted;
                                                Log.i(null, "Not Insertion Done");
                                            }

                                            Intent otpIntent = new Intent(OfflineClaimPending.this, OfflineProgressDialogUpload.class);
                                            startActivity(otpIntent);
                                            damageofflinelocalimgproceed(OfflineClaimPending.this);

                                        } catch (Exception e) {
                                            progressdialog.dismiss();
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } else {
                                try {
                                    runOnUiThread(progressdialog::dismiss);
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    runOnUiThread(() -> {
                                        String errorText = null;
                                        String trnId = null;
                                        try {
                                            errorText = index.getString("errorText");
                                            trnId = staticJsonObj.getString("trnID");
                                            AlertDialog.Builder alert = new AlertDialog.Builder(OfflineClaimPending.this);
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        progressdialog.dismiss();
                                        //damageofflinelocalimgproceed(OfflineClaimPending.this);

                                    }
                                });

                            }
                        } catch (final IOException ex) {
                            // progressdialog.dismiss();
                            ex.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    progressdialog.dismiss();

                                }
                            });
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    progressdialog.dismiss();

                                }
                            });
                        }
                    }
                });
                thread.start();
            } else {
                //  Toast.makeText(getActivity(),getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        progressdialog.dismiss();
                    } catch (Exception e) {
                        progressdialog.dismiss();
                        e.printStackTrace();
                    }
                }
            });
            MainActivity.MobileErrorLog(reqidval, "PolicyInformation-getvehiledetails", ex.toString());
            mCrashlytics.recordException(ex);
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent login = new Intent(OfflineClaimPending.this, Dashboard.class);
        // Intent login = new Intent(MainActivity.this, ClaimFinalForm.class);
        startActivity(login);
        //finishAffinity(); // or finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
            //noinspection SimplifiableIfStatement
//            if (id == R.id.action_name) {
//
//                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
//                View screenView = rootView.getRootView();
//                screenView.setDrawingCacheEnabled(true);
//                Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
//                screenView.setDrawingCacheEnabled(false);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                byte[] byteArray = byteArrayOutputStream .toByteArray();
//
//                String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                SharedPreferences sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
//                SharedPreferences.Editor supporteditor = sharedpreferences.edit();
//                supporteditor.putString(MainActivity.ReferrenceURL,"Service Provider");
//                supporteditor.apply();
//                SharedPreferences sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
//                SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
//                supporteditorimg.putString(MainActivity.SupportImg,encodedimg);
//                supporteditorimg.apply();
//                Intent login = new Intent(ThirdPartyShare.this, SupportTicket.class);
//                startActivity(login);
//                return true;
//
//                // Do something
//
//            }else{
            onBackPressed();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return super.onOptionsItemSelected(item);
    }
}