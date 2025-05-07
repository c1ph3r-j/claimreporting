package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class HelpVideos extends AppCompatActivity {

    public static final String HelpVideoName = "HelpVideoName";
    public static String mVideoFileName;
    public final int TIMEOUT_CONNECTION = 5000;//5sec
    public final int TIMEOUT_SOCKET = 30000;//30sec
    public LinearLayout uatenvirlinear;
    public ProgressDialog progressdialog;
    SharedPreferences sharedpreferences;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_videos);
        preventSizeChange(this, getSupportActionBar());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Help Videos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));
        mCrashlytics = FirebaseCrashlytics.getInstance();
        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        activity = this;
        init();

    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
//        uatenvirlinear = (LinearLayout) findViewById(R.id.lineuatenv);
//        SharedPreferences envuatshared = getSharedPreferences("ENVUAT",Context.MODE_PRIVATE);
//        String uatenvi = envuatshared.getString(MainActivity.uatenvironment,"");
//        if(uatenvi.equals("0"))
//        {
//            uatenvirlinear.setVisibility(View.GONE);
//        }
//        else
//        {
//            uatenvirlinear.setVisibility(View.VISIBLE);
//        }
            TableLayout vehiclereg = (TableLayout) findViewById(R.id.txtvehicle);
            try {
                vehiclereg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isNetworkConnected()) {
                            sharedpreferences = getSharedPreferences("HelpViewPref", MODE_PRIVATE);
                            SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                            vimeoeditor.putString(HelpVideoName, "VehicleRegister");
                            vimeoeditor.commit();
                            Intent regsiter = new Intent(HelpVideos.this, VimeoVideo.class);
                            startActivity(regsiter);
                        } else {
                            Toast.makeText(HelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            TableLayout craregister = (TableLayout) findViewById(R.id.txtcra);
            try {
                craregister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isNetworkConnected()) {
                            sharedpreferences = getSharedPreferences("HelpViewPref", MODE_PRIVATE);
                            SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                            vimeoeditor.putString(HelpVideoName, "Grevience");
                            vimeoeditor.commit();
                            Intent regsiter = new Intent(HelpVideos.this, VimeoVideo.class);
                            startActivity(regsiter);
                        } else {
                            Toast.makeText(HelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            TableLayout cracollision = (TableLayout) findViewById(R.id.txtclaimCollision);
            try {
                cracollision.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isNetworkConnected()) {
                            sharedpreferences = getSharedPreferences("HelpViewPref", MODE_PRIVATE);
                            SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                            vimeoeditor.putString(HelpVideoName, "Collision");
                            vimeoeditor.commit();
                            Intent regsiter = new Intent(HelpVideos.this, VimeoVideo.class);
                            startActivity(regsiter);
                        } else {
                            Toast.makeText(HelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            TableLayout craselfacci = (TableLayout) findViewById(R.id.txtself);
            try {
                craselfacci.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isNetworkConnected()) {
                            sharedpreferences = getSharedPreferences("HelpViewPref", MODE_PRIVATE);
                            SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                            vimeoeditor.putString(HelpVideoName, "SelfAccident");
                            vimeoeditor.commit();
                            Intent regsiter = new Intent(HelpVideos.this, VimeoVideo.class);
                            startActivity(regsiter);
                        } else {
                            Toast.makeText(HelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            TableLayout craastolentacc = (TableLayout) findViewById(R.id.txtclaimstolen);
            try {
                craastolentacc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isNetworkConnected()) {
                            sharedpreferences = getSharedPreferences("HelpViewPref", MODE_PRIVATE);
                            SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                            vimeoeditor.putString(HelpVideoName, "StolenAccident");
                            vimeoeditor.commit();
                            Intent regsiter = new Intent(HelpVideos.this, VimeoVideo.class);
                            startActivity(regsiter);
                        } else {
                            Toast.makeText(HelpVideos.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

//            TableLayout craoffline = (TableLayout) findViewById(R.id.txtofflineclaim);
//            try {
//                craoffline.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                /*Intent regsiter = new Intent(HelpVideos.this, OfflineClaimVideo.class);
//                startActivity(regsiter);*/
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                mCrashlytics.recordException(e);
//            }

            //downloadoffline();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }


    public void downloadoffline() {
        try {

            File file = new File("/storage/emulated/0/Android/data/com.aki.claimreporting/files/Movies/VIDEO_Offline.mp4");
            if (file.exists()) {
                String test = "test";
                String test1 = test;
            } else {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog = ProgressDialog.show(HelpVideos.this, getString(R.string.loading), getString(R.string.please_wait), true);

                                    // progressdialog.show(activity, "Loading", "Please wait...", true);
                                }
                            });
                            String prepend = "VIDEO_Offline_";
                            File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
                            String newval = storageDir + "/VIDEO_Offline.mp4";
//            String newval1 = newval;
//            File videoFile = File.createTempFile(
//                    "VIDEO_Offline_",  /* prefix */
//                    ".mp4",         /* suffix */
//                    storageDir      /* directory */
//            );
//            mVideoFileName = videoFile.getAbsolutePath();
                            mVideoFileName = newval;
                            // String imageURL = "https://swiftanthr.blob.core.windows.net/arun/Offline_Claim_Flow.mp4?sp=r&st=2021-05-02T17:26:36Z&se=2022-04-01T01:26:36Z&spr=https&sv=2020-02-10&sr=b&sig=iQJ%2FJ3jEk5Bzk%2F1wgBWZOUDL1Z%2BXHdxtMTYBvbJ2QXw%3D";
                            //String imageURL = "https://swiftanthr.blob.core.windows.net/arun/CRA%20Offline%20Claim%20Flow.mp4?sp=r&st=2021-05-05T11:05:55Z&se=2022-05-25T19:05:55Z&spr=https&sv=2020-02-10&sr=b&sig=yrdyBPqkfWlkSQBYYss2zqgpW8lrJlldf4DdbtDSE6w%3D";
                            // String imageURL = "https://swiftanthr.blob.core.windows.net/arun/CRAOfflineFinal.mp4?sp=r&st=2021-05-05T11:45:28Z&se=2025-01-15T19:45:28Z&spr=https&sv=2020-02-10&sr=b&sig=9gOB86zpSibtLlr%2BzJT6MuSh0EJyvuCXeRp7M1M4Cb0%3D";

                            String imageURL = "https://swiftanthr.blob.core.windows.net/arun/OfflineFlowV1.mp4?sp=r&st=2021-08-29T07:40:26Z&se=2029-05-26T15:40:26Z&spr=https&sv=2020-08-04&sr=b&sig=iKLpz1IilNU9319gfJsxAcxOCFEvwK7Rhn7P1B3JIcE%3D";
                            URL url = new URL(imageURL);
                            long startTime = System.currentTimeMillis();
                            //LogMainActivity.MobileErrorLog( "image download beginning: "+imageURL);

                            //Open a connection to that URL.
                            URLConnection ucon = url.openConnection();

                            //this timeout affects how long it takes for the app to realize there's a connection problem
                            ucon.setReadTimeout(TIMEOUT_CONNECTION);
                            ucon.setConnectTimeout(TIMEOUT_SOCKET);


                            //Define InputStreams to read from the URLConnection.
                            // uses 3KB download buffer
                            InputStream is = ucon.getInputStream();
                            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
                            FileOutputStream outStream = new FileOutputStream(mVideoFileName);
                            byte[] buff = new byte[5 * 1024];

                            //Read bytes (and store them) until there is nothing more to read(-1)
                            int len;
                            while ((len = inStream.read(buff)) != -1) {
                                outStream.write(buff, 0, len);
                            }

                            //clean up
                            outStream.flush();
                            outStream.close();
                            inStream.close();


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog.dismiss();
                                }
                            });

                        } catch (Exception ex) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog.dismiss();
                                }
                            });
                            ex.printStackTrace();
                        }

                    }
                });
                thread.start();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {

            Intent login = new Intent(HelpVideos.this, Dashboard.class);
            startActivity(login);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onOptionItemSelected", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // finish();
        Intent login = new Intent(HelpVideos.this, Dashboard.class);
        startActivity(login);
    }
}