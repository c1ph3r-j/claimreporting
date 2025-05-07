package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupportTicket extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static String membercraid;
    public static String reqidval, valimg;
    public TextView craid, screename;
    public FirebaseCrashlytics mCrashlytics;
    public ProgressDialog progressdialog;
    public LinearLayout reportissue;
    public EditText title, description;
    public byte[] datasupport;
    SharedPreferences sharedpreferences, sharedpreferencesnew;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            //this.getSupportActionBar().hide();
            getSupportActionBar().setTitle("Support Ticket");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#C3BE49"));

            // Set BackgroundDrawable
            getSupportActionBar().setBackgroundDrawable(colorDrawable);

            preventSizeChange(this, getSupportActionBar());

        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);

        }
        setContentView(R.layout.activity_support_ticket);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        activity = this;
        init();
    }


    public void init() {
        try {
            craid = (TextView) findViewById(R.id.txtmemberidval);
            screename = (TextView) findViewById(R.id.txtscreenval);
            title = (EditText) findViewById(R.id.txttitleval);
            description = (EditText) findViewById(R.id.editTextTextMultiLine);
            reportissue = findViewById(R.id.BtnReportIssue);
            reportissue.setOnClickListener(onClickReport -> CreateSupportTicket());
            sharedpreferences = getSharedPreferences("CRAID", Context.MODE_PRIVATE);
            membercraid = sharedpreferences.getString("CraIdval", "");
            sharedpreferences = getSharedPreferences("SupportPrefName", MODE_PRIVATE);
            screename.setText(sharedpreferences.getString(MainActivity.ReferrenceURL, ""));
            craid.setText(membercraid);
            sharedpreferencesnew = getSharedPreferences("SupportPrefImg", MODE_PRIVATE);
            valimg = sharedpreferencesnew.getString(MainActivity.SupportImg, null);
            ImageView screenshot = (ImageView) findViewById(R.id.Imgscreenprintval);
            datasupport = Base64.decode(valimg, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(datasupport, 0, datasupport.length);
            screenshot.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false));


        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + "=init", ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }


    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void CreateSupportTicket() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mydb = new DatabaseHelper(SupportTicket.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    MainActivity.stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                        final MediaType mediaType = MediaType.parse("image/jpeg");
                        MainActivity.postURL = getString(R.string.uaturl) + "/app/DevOps/CreateBug";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        RequestBody body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart(
                                        "Images", "Support.png",
                                        RequestBody.create(mediaType, datasupport))
                                .addFormDataPart("title", title.getText().toString())
                                .addFormDataPart("description", description.getText().toString())
                                .addFormDataPart("ImagesByte", valimg)
                                .build();
                        Request request = new Request.Builder()
                                .url(MainActivity.postURL)
                                .method("POST", body)
                                .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
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
                            try {
                                reqidval = staticJsonObj.getString("reqID");
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                            }
                            if (staticJsonObj.getInt("rcode") == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                        try {
                                            successsupportticket(staticJsonObj.getString("trnID"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    }
                                });

                            } else if (staticJsonObj.getInt("rcode") == -2) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                        try {
                                            //  updateemail();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                            mCrashlytics.recordException(ex);
////                                            MainActivity.MobileErrorLog(reqidval,"SupportTicket",ex.toString());
                                        }
                                    }
                                });


                            } else {
                                runOnUiThread(progressdialog::dismiss);
                                try {
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    runOnUiThread(() -> {
                                        String errorText;
                                        try {
                                            errorText = index.getString("errorText");
                                            AlertDialog.Builder alert = new AlertDialog.Builder(SupportTicket.this);
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
                        } catch (final Exception ex) {

                            progressdialog.dismiss();
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
                            Toast.makeText(SupportTicket.this,
                                    ex.toString(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
                thread.start();
            } else {
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            progressdialog.dismiss();
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }
    }

//    public void updateemail() {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(SupportTicket.this);
//        //dialog.setMessage("Do you like to port the data to this new device?");
//        dialog.setMessage("Before you submit a support ticket, please update your email address in the profile section.");
//        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//
//            }
//        });
//        AlertDialog alert = dialog.create();
//        alert.show();
//    }

    public void successsupportticket(String refid) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(SupportTicket.this);
        //dialog.setMessage("Do you like to port the data to this new device?");
        dialog.setMessage("Your support ticket has been created successfully your reference ID : " + refid);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
//        dialog.setNegativeButton("No(Let me correct the Info)", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //this will navigate user to the device location settings screen
//                dialog.dismiss();
//            }
//        });
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return super.onOptionsItemSelected(item);
    }
}