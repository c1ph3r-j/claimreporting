package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ClaimHistoryImage extends AppCompatActivity {

    public ListView list;
    public String stokenval, encryptedSHA;
    public ProgressDialog progressdialog;
    public ClaimDamageImages mAdapter;
    DatabaseHelper mydb;
    SharedPreferences sharedpreferences;
    Activity activity;
    ArrayList<Bitmap> listOfClaimDamageImages = new ArrayList<>();
    ArrayList<DamageImageClaim> claimdamagelist = new ArrayList<DamageImageClaim>();
    private FirebaseCrashlytics mCrashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_history_image);
        preventSizeChange(this, getSupportActionBar());
        mCrashlytics = FirebaseCrashlytics.getInstance();

        getSupportActionBar().setTitle("Incident Images");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        init();
    }

    public void init() {
        getclaimImg();
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

    public void getclaimImg() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    mydb = new DatabaseHelper(ClaimHistoryImage.this);
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

                    progressdialog = new ProgressDialog(ClaimHistoryImage.this);
                    encryptedSHA = "";
                    String sourceStr = MainActivity.InsertMobileParameters();
                    //String smobileparam = "{ \"imeino1\": \"355844090234339\", \"imeino2\": \"355844093244335\", \"timezone\": \"+0630GMT:IST Asia/Kolkata\", \"currentdatetime\": \"12-10-2019 18:33:14\", \"Latitude\": \"-1.25848\", \"Longitude\": \"36.80537\",\"IpAddress\": \"192.168.2.100\"}";
                    try {
                        encryptedSHA = AESCrypt.encrypt(sourceStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }


                    try {
                        Thread thread = new Thread(new Runnable() {

                            @RequiresApi(api = Build.VERSION_CODES.N)
                            public void run() {
                                MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/GetClaim";
                                final MediaType JSON
                                        = MediaType.parse("application/json; charset=utf-8");
                                OkHttpClient client = new OkHttpClient();
                                SharedPreferences claimPref = getSharedPreferences("ClaimDetailsView", Context.MODE_PRIVATE);
                                String incidenid = claimPref.getString(CustomClaimHistoryList.ClaimrefID, "");
                                String claiminc = null;
                                try {
                                    claiminc = AESCrypt.encrypt(incidenid);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                // String incidenid ="210129-174223119";
                                JsonObject Details = new JsonObject();
                                Details.addProperty("incidentUniqueCode", incidenid);
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog = ProgressDialog.show(ClaimHistoryImage.this, "Loading", "Please wait...", true);
                                            // progressdialog.show(activity, "Loading", "Please wait...", true);
                                        }
                                    });
                                    staticResponse = client.newCall(request).execute();
                                    String staticRes = staticResponse.body().string();
                                    Log.i(null, staticRes);
                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                                    if (staticJsonObj.getInt("rcode") == 1) {
                                        //final JSONObject staticfinalObj = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate");
                                        // sOTP= staticJsonObj.getJSONObject("rObj").getString("OTP");
                                        //  sOTPID= staticJsonObj.getJSONObject("rObj").getString("OTPID");


                                        JSONArray claimdamageul = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllAttachment");
                                        for (int i = 0; i < claimdamageul.length(); i++) {
                                            JSONObject vehicleObj = claimdamageul.getJSONObject(i);
                                            DamageImageClaim element = new DamageImageClaim(vehicleObj.getString("imageURL"));
                                            claimdamagelist.add(element);
                                        }

                                        for (DamageImageClaim eachImageUrl : claimdamagelist) {
                                            try {
                                                URL url = null;
                                                try {
                                                    url = new URL(eachImageUrl.getImageURL());
                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }
                                                ByteArrayOutputStream output = new ByteArrayOutputStream();

                                                try {
                                                    try (InputStream inputStream = url.openStream()) {
                                                        int n = 0;
                                                        byte[] buffer = new byte[1024];
                                                        while (-1 != (n = inputStream.read(buffer))) {
                                                            output.write(buffer, 0, n);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                        mCrashlytics.recordException(e);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }

                                                byte[] imgdmg = output.toByteArray();
                                                byte[] imgdmg1 = imgdmg;
                                                Bitmap bmp = BitmapFactory.decodeByteArray(imgdmg, 0, imgdmg.length);
                                                listOfClaimDamageImages.add(bmp);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ClaimDamageImages attachListView = new ClaimDamageImages(ClaimHistoryImage.this, listOfClaimDamageImages);
                                                list = (ListView) findViewById(R.id.claimmoreattachmentimages);
                                                list.setAdapter(attachListView);
                                                progressdialog.dismiss();

                                                //Intent step2 = new Intent(CreateDriver.this, DriverMapping.class);
                                                // startActivity(step2);

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
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(ClaimHistoryImage.this);
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
                                } catch (final IOException | JSONException e) {
                                    runOnUiThread(progressdialog::dismiss);
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            Toast.makeText(ClaimHistoryImage.this,
                                                    e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                            }

                        });
                        thread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }


                } else {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(ClaimHistoryImage.this);
//                    dialog.setMessage(getString(R.string.gps_not_enabled));
//                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //this will navigate user to the device location settings screen
//                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivity(intent);
//                        }
//                    });
//                    AlertDialog alert = dialog.create();
//                    alert.show();
                }


            } else {

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(ClaimHistoryImage.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            runOnUiThread(new Runnable() {
                public void run() {
                    progressdialog.dismiss();
                    Toast.makeText(ClaimHistoryImage.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String methodName = Objects.requireNonNull(new Object() {
                }
                        .getClass()
                        .getEnclosingMethod())
                .getName();
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getClassName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return super.onOptionsItemSelected(item);
    }


}