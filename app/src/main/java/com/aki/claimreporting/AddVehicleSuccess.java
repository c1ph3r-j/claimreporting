package com.aki.claimreporting;

import static com.aki.claimreporting.CertificateActivation.postURL;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddVehicleSuccess extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static String reqidval;
    public static FirebaseCrashlytics mCrashlytics;
    public LinearLayout backhome;
    public TextView pdfview;
    public TextView successtxt, videoclmtxt, videomotorclmtxt;
    public ProgressDialog progressdialog;
    public String certnumval;
    public android.app.AlertDialog.Builder dialog;
    public android.app.AlertDialog alert;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    public TextView vehcid;
    Context context;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        context = this;
        try {
            //this.getSupportActionBar().hide();
            Objects.requireNonNull(getSupportActionBar()).setTitle("Congratulations!");
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        setContentView(R.layout.activity_add_vehicle_success);
        preventSizeChange(this, getSupportActionBar());
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        activity = this;
        String loadlocaldata = "1";
        //getSupportActionBar().setTitle("Vehicle Success");
        init();
//        ImageView shareoption = (ImageView) findViewById(R.id.imageShare);
//        shareoption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//                sharingIntent.setType("text/plain");
//                String shareBody = "Test Vehicle";
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Vechile Information");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//                startActivity(Intent.createChooser(sharingIntent, "Share via"));
//            }
//        });


    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            GenerateVehiclePDF();
            videoclmtxt = (TextView) findViewById(R.id.videoclaim);
            videomotorclmtxt = (TextView) findViewById(R.id.videomotorclaim);

            SharedPreferences regnum = getSharedPreferences("CertficiatePref", MODE_PRIVATE);
            String regnum_id = regnum.getString(AddVehicle.regnumadddriver, "");

            successtxt = (TextView) findViewById(R.id.txtregsuccess);
            String successhtml = "Vehicle <b>" + regnum_id + "</b> is added successfully";
            successtxt.setText(Html.fromHtml(successhtml));
            backhome = findViewById(R.id.Btnuvehiclebacktohome);
            pdfview = (TextView) findViewById(R.id.Btnuvehicleviewpdf);
            pdfview.setPaintFlags(pdfview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            videoclmtxt.setPaintFlags(pdfview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            videomotorclmtxt.setPaintFlags(pdfview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            backhome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent otpIntent = new Intent(AddVehicleSuccess.this, Dashboard.class);
                        startActivity(otpIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }
            });
            pdfview.setOnClickListener(onPdf -> ViewVehiclePDF());

            videoclmtxt.setOnClickListener(onVideoClm -> {
                if (isNetworkConnected()) {
                    sharedpreferences = getSharedPreferences("HelpViewPref", MODE_PRIVATE);
                    SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                    vimeoeditor.putString(MainActivity.HelpVideoName, "VehicleClaimVideo");
                    vimeoeditor.apply();
                    Intent regsiter = new Intent(AddVehicleSuccess.this, VimeoVideo.class);
                    startActivity(regsiter);
                } else {
                    Toast.makeText(AddVehicleSuccess.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                }
            });

            videomotorclmtxt.setOnClickListener(onVideoMotorClm -> {
                if (isNetworkConnected()) {
                    sharedpreferences = getSharedPreferences("HelpViewPref", MODE_PRIVATE);
                    SharedPreferences.Editor vimeoeditor = sharedpreferences.edit();
                    vimeoeditor.putString(MainActivity.HelpVideoName, "VehicleMotorClaimVideo");
                    vimeoeditor.apply();
                    Intent regsiter = new Intent(AddVehicleSuccess.this, VimeoVideo.class);
                    startActivity(regsiter);
                } else {
                    Toast.makeText(AddVehicleSuccess.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                }
            });

            vehcid = (TextView) findViewById(R.id.txtvehcid);
            SharedPreferences vechidPref = getSharedPreferences("VehicleNewID", MODE_PRIVATE);
            String vechile_id = vechidPref.getString("Vechidshow", "");
            vehcid.setText("Ref-ID : " + vechile_id);
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

    public void GenerateVehiclePDF() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                try {
                    mydb = new DatabaseHelper(AddVehicleSuccess.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                MainActivity.stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
                progressdialog = new ProgressDialog(this);
                String encryptedSHA = "";
                String sourceStr = MainActivity.InsertMobileParameters();
                try {
                    encryptedSHA = AESCrypt.encrypt(sourceStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }

                try {
                    Thread thread = new Thread(() -> {
                        SharedPreferences certifnum = getSharedPreferences("CertificateNum", MODE_PRIVATE);
                        certnumval = certifnum.getString(CertificateActivation.CertNum, "");
                        SharedPreferences vechidval = getSharedPreferences("GenerateVehiclePDF", MODE_PRIVATE);
                        String vehcidapi = vechidval.getString("VechPDFID", "");
                        postURL = getString(R.string.uaturl) + "/app/Vehicle/GenerateVehiclePDF";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        JsonObject Details = new JsonObject();
                        try {
                            Details.addProperty("certificateNo", certnumval);
                            Details.addProperty("vehicleID", vehcidapi);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }

                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(postURL)
                                .header("Authorization", "Bearer " + MainActivity.stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse;
                        try {
                            runOnUiThread(() ->
                                    progressdialog = ProgressDialog.show(AddVehicleSuccess.this, "Loading", "Please wait...", true));
                            staticResponse = client.newCall(request).execute();
                            int statuscode = staticResponse.code();
                            if (statuscode == 401) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    unauthorize(AddVehicleSuccess.this);
                                });
                            } else {

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
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        mydb = new DatabaseHelper(AddVehicleSuccess.this);
                                        if (mydb.getregstep().getCount() != 0) {
                                            mydb.deleteregstep();
                                        }
                                        boolean regstepinserted = mydb.insertregstep("RegCompleted");
                                        if (regstepinserted) {
                                            boolean test = regstepinserted;
                                            Log.i(null, "Insertion Done");
                                        } else {
                                            boolean test = regstepinserted;
                                            Log.i(null, "Not Insertion Done");
                                        }
                                        //Intent step2 = new Intent(RegistrationStep4.this, PremiumAmount.class);
                                        // startActivity(step2);
                                    });

                                } else if (staticJsonObj.getInt("rcode") == 2) {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        //Intent step2 = new Intent(RegistrationStep4.this, PremiumAmount.class);
                                        // startActivity(step2);
                                    });

                                } else if (staticJsonObj.getInt("rcode") == 401) {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        unauthorize(AddVehicleSuccess.this);
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
                                                AlertDialog.Builder alert = new AlertDialog.Builder(AddVehicleSuccess.this);
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

                        } catch (final Exception e) {
                            progressdialog.dismiss();
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                            runOnUiThread(() ->
                                    Toast.makeText(AddVehicleSuccess.this, R.string.exceptioncheck, Toast.LENGTH_SHORT).show());
                        }
                    });
                    thread.start();

                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }

            } else {
                Toast.makeText(AddVehicleSuccess.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(AddVehicleSuccess.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

    /*public void downloadfile(String filepath) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            dialog = new android.app.AlertDialog.Builder(AddVehicleSuccess.this);
            dialog.setMessage("Your Vehicle registration report is downloading");
            alert = dialog.create();
            alert.show();
            SharedPreferences vechidPref = getSharedPreferences("VehicleNewID", MODE_PRIVATE);
            String vechile_id = vechidPref.getString("Vechidshow", "");
            String destination = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/";
            String fileName = vechile_id + ".pdf";
            //String fileName = "C-AA0074.pdf";
            destination += fileName;
            final Uri uri = Uri.parse("file://" + destination);

            String url = filepath; //paste url here

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Downloading....");
            request.setTitle(" TITLE ");
            request.setDestinationUri(uri);

            final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            //final String finalDestination = destination;
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    Log.d("Update status", "Download completed");
                    alert.dismiss();
                    showPdf();
                    unregisterReceiver(this);
                }
            };

            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }*/

    public void downloadfile(String filepath) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            // Show downloading dialog
            dialog = new android.app.AlertDialog.Builder(AddVehicleSuccess.this);
            dialog.setMessage("Your Vehicle registration report is downloading");
            alert = dialog.create();
            alert.show();

            // Fetch the vehicle ID from SharedPreferences
            SharedPreferences vechidPref = getSharedPreferences("VehicleNewID", MODE_PRIVATE);
            String vehicleId = vechidPref.getString("Vechidshow", "");
            String destination = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/";
            String fileName = vehicleId + ".pdf";
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



    public void showPdf() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            SharedPreferences vechidPref = getSharedPreferences("VehicleNewID", MODE_PRIVATE);
            String vechile_id = vechidPref.getString("Vechidshow", "");
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/" + vechile_id + ".pdf");
            Uri uri = FileProvider.getUriForFile(this, "com.aki.cralite.fileprovider", file);
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

    public void ViewVehiclePDF() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(() -> {
                    mydb = new DatabaseHelper(AddVehicleSuccess.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                MainActivity.stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }

                    /*SharedPreferences certifnum = getSharedPreferences("CertificateNum",MODE_PRIVATE);
                    String certnumval = certifnum.getString(CertificateActivation.CertNum,"");*/

                    postURL = getString(R.string.uaturl) + "/app/Vehicle/ViewVehiclePDF";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .build();
                    JsonObject Details = new JsonObject();
                    Details.addProperty("certificateNo", certnumval);
                    String insertString = Details.toString();
                    RequestBody body = RequestBody.create(JSON, insertString);
                    Request request = new Request.Builder()
                            .url(postURL)
                            .header("Authorization", "Bearer " + MainActivity.stokenval)
                            .header("MobileParameter", MainActivity.InsertMobileParameters())
                            .post(body)
                            .build();
                    Response staticResponse = null;
                    try {
                        runOnUiThread(() ->
                                progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true));
                        staticResponse = client.newCall(request).execute();
                        int statuscode = staticResponse.code();
                        if (statuscode == 401) {
                            runOnUiThread(() -> {
                                progressdialog.dismiss();
                                unauthorize(AddVehicleSuccess.this);
                            });
                        } else {
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
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    String pdfurldoc = null;
                                    try {
                                        pdfurldoc = staticJsonObj.getJSONObject("rObj").getString("blobDownloadURL");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                    SharedPreferences vechidPref = getSharedPreferences("VehicleNewID", MODE_PRIVATE);
                                    String vechile_id = vechidPref.getString("Vechidshow", "");
                                    File file1 = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/" + vechile_id + ".pdf");
                                    boolean deleted = file1.delete();
                                    boolean newvaldel = deleted;
                                    downloadfile(pdfurldoc);
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfurldoc));
                                    startActivity(browserIntent);
                                });

                            } else if (staticJsonObj.getInt("rcode") == 2) {

                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    Toast.makeText(AddVehicleSuccess.this, "PDF is generating", Toast.LENGTH_SHORT).show();
                                });
                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    unauthorize(AddVehicleSuccess.this);
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
                                            AlertDialog.Builder alert = new AlertDialog.Builder(AddVehicleSuccess.this);
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

                    } catch (final Exception e) {
                        runOnUiThread(progressdialog::dismiss);
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                        Toast.makeText(AddVehicleSuccess.this,
                                e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
                thread.start();
            } else {
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

   /* public void unauthorize() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddVehicleSuccess.this);
        dialog.setMessage("Your session have been expired. Please login again to continue");
        dialog.setPositiveButton("Ok", (dialog1, which) -> {
            mydb = new DatabaseHelper(AddVehicleSuccess.this);
            mydb.deletetokendata();
            mydb.deleteclaimstep2data();
            mydb.deleteregstep();
            mydb.deletethirdlocalimage();
            mydb.deletethirdpartydetails();
            mydb.deleteuserdata();
            mydb.deletetermsdata();
            mydb.deletetermsconditionsdata();
            mydb.deletedriverdetails();
            mydb.deletevehicledata();
            Intent login = new Intent(AddVehicleSuccess.this, Dashboard.class);
            startActivity(login);
        });
        dialog.setCancelable(false);
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }*/


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_help, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_name) {
//
//            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
//            View screenView = rootView.getRootView();
//            screenView.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
//            screenView.setDrawingCacheEnabled(false);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//            String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditor = sharedpreferences.edit();
//            supporteditor.putString(MainActivity.ReferrenceURL, "Add Vehicle Success");
//            supporteditor.apply();
//            sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
//            supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
//            supporteditorimg.apply();
//            Intent login = new Intent(AddVehicleSuccess.this, SupportTicket.class);
//            startActivity(login);
//            return true;
//        }
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent login = new Intent(AddVehicleSuccess.this, Dashboard.class);
        startActivity(login);
    }


}