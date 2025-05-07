package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StolenImageDeclaration extends AppCompatActivity {

    public static final int RequestPermissionCode = 1;
    public static String simagenameNW;
    public static String mAudioFileName;
    public static String ImageName, simagename, ImageUniqeCaptureID, ImageisthirdSign;
    public static String reqidval;
    public static LinearLayout botlinsignlay;
    public static ListView list;
    private static FirebaseCrashlytics mCrashlytics;
    public ProgressDialog progressdialog;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    public LinearLayout linearimage1, layout;
    public TextView txtpageno;
    public String simagebase64, sdocumentType;
    public ImageView imgview;
    public Bitmap bitmapaftersign;
    public ImageView clearsign;
    public SignaturePad claimsignature;
    public int countvalidate;
    public TextView signhereval;
    public int countvalidatenew;
    public int countvalidateincr;
    public int currentpageno, totallistvalue, imagesrc, currentvalue, currentforpageno;
    ImageView buttonStart, buttonrecording, buttonplay, buttonplaying, audiorecorddone;
    File AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    ViewPager2 ImageDelcareViewPager;
    TabLayout tabDotsForImageDeclare;
    Activity activity;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    MediaPlayer mediaPlayer;
    ArrayList dataModels;
    ListView listView;
    DatabaseHelper mydb;
    ArrayList<StolenImageViewFragment> imageList;
    LinearLayout imagedeclare;
    ArrayList<AttachmentStolenimageList> attachstolendeclareimgist = new ArrayList<AttachmentStolenimageList>();
    private boolean isSignatured = false;

    public static void enablelayout(Activity activity) {
        botlinsignlay = (LinearLayout) activity.findViewById(R.id.stolenbottomlistid);
        botlinsignlay.setVisibility(View.VISIBLE);

    }

    public static void disablelayout(Activity activity) {
        botlinsignlay = (LinearLayout) activity.findViewById(R.id.stolenbottomlistid);
        botlinsignlay.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String localToGMT() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
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
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            // localToGMTNew();
        }
        return finalDateString;

    }

    public static String localToGMTNew() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        DateFormat dftime = DateFormat.getTimeInstance();
        DateFormat dfdate = DateFormat.getDateInstance();
        dftime.setTimeZone(TimeZone.getTimeZone("gmt"));
        dfdate.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = dfdate.format(new Date()) + " " + dftime.format(new Date());

        String strDate = gmtTime;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date convertedDate = new Date();
        String finalDateString = "";
        try {
            convertedDate = dateFormat.parse(strDate);
            SimpleDateFormat sdfnewformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
            finalDateString = sdfnewformat.format(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return finalDateString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stolen_image_declaration);
        preventSizeChange(this, getSupportActionBar());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Review Photographs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        activity = this;
        countvalidatenew = 0;
        countvalidate = 0;
        init();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            ImageDelcareViewPager = findViewById(R.id.ImageDelcareViewPager);
            tabDotsForImageDeclare = findViewById(R.id.tabDotsForImageDeclare);

            botlinsignlay = (LinearLayout) findViewById(R.id.bottomlistid);

            signhereval = (TextView) findViewById(R.id.signhereid);

            clearsign = (ImageView) findViewById(R.id.clearsingature);
            clearsign.setVisibility(View.GONE);
            claimsignature = (SignaturePad) findViewById(R.id.claimsignaturePad);

            try {
                claimsignature.setOnSignedListener(new SignaturePad.OnSignedListener() {
                    @Override
                    public void onStartSigning() {
                        signhereval.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSigned() {
                        //Toast.makeText(ImageDeclaration.this, "OnFinishes", Toast.LENGTH_SHORT).show();
                        isSignatured = true;
                        clearsign.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onClear() {
                        isSignatured = false;
                        signhereval.setVisibility(View.VISIBLE);
                        clearsign.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                clearsign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        claimsignature.clear();
                        isSignatured = false;
                        clearsign.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
//            claimsignature.setOnSignedListener(new SignaturePad.OnSignedListener() {
//
//                @Override
//                public void onStartSigning() {
//
//                }
//
//                public void onSigned() {
//                    //Event triggered when the pad is signed
//                    isSignatured = true;
//                }
//
//                @Override
//                public void onClear() {
//
//                }
//            });


            //buttonStart = (ImageView) findViewById(R.id.Imgrecord);
            imagedeclare = findViewById(R.id.imagedeclareid);
//            buttonrecording = (ImageView) findViewById(R.id.Imgrecording);
//            audiorecorddone = (ImageView) findViewById(R.id.audiorecorededdone);
//            buttonplay = (ImageView) findViewById(R.id.Imgplay);
//            buttonplaying = (ImageView)findViewById(R.id.Imgplaying);
//            buttonStart.setVisibility(View.VISIBLE);
//            buttonrecording.setVisibility(View.GONE);
//            //    buttonplay.setVisibility(View.VISIBLE);
//            buttonplay.setVisibility(View.GONE);
//            audiorecorddone.setVisibility(View.GONE);
//            buttonplaying.setVisibility(View.GONE);
            try {
                imagedeclare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    Bitmap signatureBitmap = signaturePad.getTransparentSignatureBitmap();
//                    ObjectDetectorActivity.Global.img = signatureBitmap;
//                    MainActivity.uploadimages(ImageDeclaration.this);
//                    Intent car = new Intent(ImageDeclaration.this, Videorecorder.class);

                            if (isNetworkConnected()) {
                                try {
                                    if (isSignatured) {
                                        bitmapaftersign = claimsignature.getSignatureBitmap();
                                        pushfinalsignature();
                                    } else {
                                        Toast.makeText(StolenImageDeclaration.this, getString(R.string.ClaimSignatureRequired), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    Toast.makeText(StolenImageDeclaration.this, ex.toString(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Intent car = new Intent(StolenImageDeclaration.this, ClaimOfflineSuccess.class);
                                startActivity(car);
                            }
                        }

//                    startActivity(car);


                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
//            signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
//
//                @Override
//                public void onStartSigning() {
//
//                }
//
//                public void onSigned() {
//                    //Event triggered when the pad is signed
//                    isSignatured = true;
//                }
//
//                @Override
//                public void onClear() {
//
//                }
//            });
//            buttonStart.setOnClickListener(new View.OnClickListener() {
//                @RequiresApi(api = Build.VERSION_CODES.O)
//                @Override
//                public void onClick(View view) {
//                    try {
//                        AudioSavePathInDevice = createAudioFileName();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    MediaRecorderReady();
//
//                    try {
//                        mediaRecorder.prepare();
//                        mediaRecorder.start();
//                    } catch (IllegalStateException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    buttonStart.setVisibility(View.GONE);
//                    buttonrecording.setVisibility(View.VISIBLE);
//                    Toast.makeText(StolenImageDeclaration.this, "Recording started",
//                            Toast.LENGTH_LONG).show();
//
//                }
//            });
//
//            buttonrecording.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    try {
//                        mediaRecorder.stop();
//                        audioconvert(String.valueOf(AudioSavePathInDevice));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    //pushaudio();
//                    buttonStart.setVisibility(View.VISIBLE);
//                    buttonrecording.setVisibility(View.GONE);
//                    audiorecorddone.setVisibility(View.VISIBLE);
//                    buttonplay.setVisibility(View.VISIBLE);
//                    Toast.makeText(StolenImageDeclaration.this, "Recording Completed",
//                            Toast.LENGTH_LONG).show();
//                }
//            });
//
//            buttonplay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) throws IllegalArgumentException,
//                        SecurityException, IllegalStateException {
//
//                    buttonplay.setVisibility(View.GONE);
//                    audiorecorddone.setVisibility(View.VISIBLE);
//                    buttonplaying.setVisibility(View.VISIBLE);
//
//                    mediaPlayer = new MediaPlayer();
//                    try {
//                        mediaPlayer.setDataSource(String.valueOf(AudioSavePathInDevice));
//                        mediaPlayer.prepare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    mediaPlayer.start();
//                    Toast.makeText(StolenImageDeclaration.this, "Recording Playing",
//                            Toast.LENGTH_LONG).show();
//                }
//            });
//
//            buttonplaying.setOnClickListener(new View.OnClickListener() {
//                @RequiresApi(api = Build.VERSION_CODES.O)
//                @Override
//                public void onClick(View view) {
//                    buttonplay.setVisibility(View.VISIBLE);
//                    audiorecorddone.setVisibility(View.VISIBLE);
//                    buttonplaying.setVisibility(View.GONE);
//                    if(mediaPlayer != null){
//                        mediaPlayer.stop();
//                        mediaPlayer.release();
//                        MediaRecorderReady();
//                    }
//                }
//            });

            try {
                getImageList();
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

    }


    private void getImageList() {
  try {
      String methodName = Objects.requireNonNull(new Object() {
      }.getClass().getEnclosingMethod()).getName();
      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              progressdialog = ProgressDialog.show(StolenImageDeclaration.this, "Loading", "Please wait...", true);
          }
      });
      imageList = new ArrayList<>();
      mydb = new DatabaseHelper(StolenImageDeclaration.this);
      mCrashlytics = FirebaseCrashlytics.getInstance();


      Cursor curseattach = mydb.getClaimImgmore();
      int counttest1 = curseattach.getCount();
      if (counttest1 >= 1) {
          while (curseattach.moveToNext()) {
              AttachmentStolenimageList element = new AttachmentStolenimageList(curseattach.getString(0));
              attachstolendeclareimgist.add(element);
              imageList.add(new StolenImageViewFragment(element));
          }
          runOnUiThread(() -> progressdialog.dismiss());
      } else {
          runOnUiThread(() -> progressdialog.dismiss());
      }


      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              //ArrayList<AllVehicleResponse> arrayOfUsers = allvehclist;
//                    StolenImageDeclareAdapter attachListView = new StolenImageDeclareAdapter(StolenImageDeclaration.this, attachstolendeclareimgist,StolenImageDeclaration.this);
//                    list = (ListView) findViewById(R.id.liststolenimgdeclareimages);
//                    //  list.addFooterView(botlinsignlay);
//                    list.setAdapter(attachListView);

              try {
                  StolenImageDeclarationAdapter viewPagerImageAdapter = new StolenImageDeclarationAdapter(StolenImageDeclaration.this, imageList);
                  ImageDelcareViewPager.setAdapter(viewPagerImageAdapter);

                  ImageDelcareViewPager.setClipToPadding(false);
                  ImageDelcareViewPager.setClipChildren(false);
                  ImageDelcareViewPager.setOffscreenPageLimit(3);
                  ImageDelcareViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                  // Code for carousel view animation in viewpager2.
                  CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                  compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                  compositePageTransformer.addTransformer((page, position) -> {
                      float val = 1 - Math.abs(position);
                      page.setScaleY(0.95f + val * 0.15f);
                  });

                  ImageDelcareViewPager.setPageTransformer(compositePageTransformer);
                  new TabLayoutMediator(tabDotsForImageDeclare, ImageDelcareViewPager, (tab, position) -> {
                  }).attach();

              } catch (Exception e) {
                  e.printStackTrace();
                  MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                  mCrashlytics.recordException(e);
              }
          }
      });
  }catch (Exception e){
      e.printStackTrace();
      progressdialog.dismiss();

  }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getImageList();

    }

    public byte[] audioconvert(String path) throws IOException {
        FileInputStream fis = null;
        // FileInputStream fis = new FileInputStream(new File(yourUri));
        //byte[] buf = new byte[1024];
        //byte[] videoBytes = baos.toByteArray();
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    private File createAudioFileName() throws IOException {
//        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String prepend = "VIDEO_" + timestamp + "_";
//
//
//        File videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);
//        mVideoFileName = videoFile.getAbsolutePath();
//        SharedPreferences sharedPreference = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreference.edit();
//        editor.putString("videofilepath", mVideoFileName);
//        editor.apply();
//        return videoFile;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "Audio_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File audioFile = File.createTempFile(
                prepend,  /* prefix */
                ".3gp",         /* suffix */
                storageDir      /* directory */
        );
        mAudioFileName = audioFile.getAbsolutePath();
//        SharedPreferences sharedPreference = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreference.edit();
//        editor.putString("videofilepath", mAudioFileName);
//        editor.apply();
        return audioFile;
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void insertclaimfinal() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected() == true) {

                if (checkGPSStatus() == true) {

                    progressdialog = new ProgressDialog(this);
                    MainActivity.encryptedSHA = "";
                    String sourceStr = MainActivity.InsertMobileParameters();
                    try {
                        MainActivity.encryptedSHA = AESCrypt.encrypt(sourceStr);
                    } catch (Exception e) {
                        e.printStackTrace();
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
                            SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                            String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
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
                            //Device unique code
                            String imeiInput = Settings.Secure.getString(StolenImageDeclaration.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                            Details.addProperty("uniqueID",imeiInput);
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog = ProgressDialog.show(activity, getString(R.string.loading), getString(R.string.please_wait), true);

                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
                                    }
                                });
                                staticResponse = client.newCall(request).execute();
                                String staticRes = staticResponse.body().string();
                                if (staticRes.equals("")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
                                            Toast.makeText(StolenImageDeclaration.this, "Please contact administrator to proceed and try again", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                } else {
                                    Log.i(null, staticRes);
                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                                    try {
                                        reqidval = staticJsonObj.getString("reqID");
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                        progressdialog.dismiss();
                                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                        mCrashlytics.recordException(ex);
                                    }
                                    if (staticJsonObj.getInt("rcode") == 1) {
//                                        mydb = new DatabaseHelper(ImageDeclaration.this);
//                                        if(mydb.getclaimstep().getCount() != 0)
//                                        {
//                                            mydb.deleteclaimstep();
//                                        }
//                                        boolean claimstepinserted = mydb.insertclaimstep("ClaimCompleted");
//                                        if(claimstepinserted == true)
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

                                    } else {
                                        try {
                                            runOnUiThread(progressdialog::dismiss);
                                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                            JSONObject index = rmsg.getJSONObject(0);
                                            runOnUiThread(() -> {
                                                String errorText;
                                                try {
                                                    errorText = index.getString("errorText");
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(StolenImageDeclaration.this);
                                                    alert.setCancelable(false);
                                                    alert.setMessage(errorText);
                                                    alert.setNegativeButton("Ok", (dialog, which) -> {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressdialog.dismiss();
                                                            }
                                                        });
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
                                }

                            } catch (final IOException ex) {
                                runOnUiThread(progressdialog::dismiss);
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        Toast.makeText(StolenImageDeclaration.this,
                                                ex.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (JSONException ex) {
                                runOnUiThread(progressdialog::dismiss);
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                                Toast.makeText(StolenImageDeclaration.this,
                                        ex.toString(), Toast.LENGTH_LONG).show();
//                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(StolenImageDeclaration.this);
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
        } catch (Exception ex) {
            //progressdialog.dismiss();
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
//            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }

    }

    private void showCustomDialog() {
        // Create and set up the dialog
        final Dialog dialog = new Dialog(StolenImageDeclaration.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove title bar
        dialog.setContentView(R.layout.dialog_custom);
        dialog.setCancelable(false); // Dialog can be canceled by clicking outside

        // Get references to views inside the dialog
        Button sign_in_now_btn= dialog.findViewById(R.id.Sign_in_now_btn);
        Button sign_in_later_btn = dialog.findViewById(R.id.Sign_in_later_btn);
        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);

        // Set up the close button
        sign_in_later_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent car = new Intent(StolenImageDeclaration.this, ClaimSuccess.class);
                startActivity(car);
            }
        });

        sign_in_now_btn.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(StolenImageDeclaration.this,Registration.class);
            intent.putExtra("ImageDeclaration",true);
            startActivity(intent);
        });

        // Show the dialog
        dialog.show();
    }

    private boolean isUserSignedUp() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            mydb = new DatabaseHelper(this);
            if (mydb.getTokendetails().getCount() != 0 && mydb.getUserPhoneNumber().length() == 9) {
                Cursor firebaseUserId = mydb.getTokendetails();
                return firebaseUserId.getCount() >= 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            return false;
        }
        return false;
    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void pushfinalaudio()
//    {
//                            try
//                            {
//                                final String filenamecheck = String.valueOf(AudioSavePathInDevice);
//                                if(filenamecheck == null || filenamecheck == "null" | filenamecheck =="")
//                                {
//                                       insertclaimfinal();
//                                }
//                                else {
//                                    mydb = new DatabaseHelper(StolenImageDeclaration.this);
//                                    if (mydb.getTokendetails().getCount() != 0) {
//                                        Cursor curseattachtoken = mydb.getTokendetails();
//                                        int counttoken = curseattachtoken.getCount();
//                                        if (counttoken >= 1) {
//                                            while (curseattachtoken.moveToNext()) {
//                                                MainActivity.stokenval = curseattachtoken.getString(1);
//                                            }
//                                        }
//                                    }
//                                    SharedPreferences certifPref = getSharedPreferences("ClaimInsert",MODE_PRIVATE);
//                                    String certnum = certifPref.getString(ClaimRegFragment.CertificateID,"");
//                                    SharedPreferences incidePref = getSharedPreferences("CRAID",MODE_PRIVATE);
//                                    String incident_id = incidePref.getString(ClaimRegFragment.CraIdval,"");
//                                    sdocumentType = "064b1ed5-a782-41ba-ba78-d73a443a08c2";
//                                    String uniqueID = UUID.randomUUID().toString();
//                                    final String filename = String.valueOf(AudioSavePathInDevice);
//                                    SharedPreferences locationPref = getSharedPreferences("LocationPref", MODE_PRIVATE);
//                                  //  SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
//
//                                    String dateTime = localToGMT();
//                                    MediaType mediaType = MediaType.parse("application/octet-stream");
//                                    MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/UploadClaimFiles";
//                                    OkHttpClient client = new OkHttpClient.Builder()
//                                            .connectTimeout(120, TimeUnit.SECONDS)
//                                            .writeTimeout(120, TimeUnit.SECONDS)
//                                            .readTimeout(120, TimeUnit.SECONDS)
//                                            .build();
//                                    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                                            .addFormDataPart("Images",filename,
//                                                    RequestBody.create(MediaType.parse("application/octet-stream"),
//                                                            new File(filename)))
//                                            .addFormDataPart("certificateNo",certnum)
//                                            .addFormDataPart("incidentUniqueCode",incident_id)
//                                            .addFormDataPart("documentType","1b22329f-3d6b-4d1d-86fe-be0c95503dc1")
//                                            .addFormDataPart("geoTagLat",locationPref.getString(MainActivity.Latitude, null))
//                                            .addFormDataPart("geoTagLon",locationPref.getString(MainActivity.Longitude, null))
//                                            .addFormDataPart("captureAttachmentID", uniqueID)
//                                            .addFormDataPart("captureDateTime",dateTime)
//                                            .build();
////                                RequestBody body
////                                RequestBody body = new MultipartBody.Builder()
////                                        .setType(MultipartBody.FORM)
////                                        .addFormDataPart(
////                                                "Images","CertificateNumber.jpg",
////                                                RequestBody.create(mediaType, byteArray))
////                                        .addFormDataPart("certificateNo","4583363")
////                                        .addFormDataPart("documentType","werdfs")
////                                        .build();
//                                    Request request = new Request.Builder()
//                                            .url(MainActivity.postURL)
//                                            .method("POST", body)
//                                            .addHeader("Authorization", "Bearer "+ MainActivity.stokenval)
//                                            .addHeader("MobileParameter", MainActivity.InsertMobileparameters())
//                                            .build();
//                                    Response staticResponse = null;
//                                    try {
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
//                                                // progressdialog.show(activity, "Loading", "Please wait...", true);
//                                            }
//                                        });
//                                        staticResponse = client.newCall(request).damagedPartOfTheCar();
//                                        String staticRes = staticResponse.body().string();
//                                        Log.i(null,staticRes);
//                                        final JSONObject staticJsonObj = new JSONObject(staticRes);
//                                        try {
//                                            reqidval = staticJsonObj.getString("reqID");
//                                        }
//                                        catch (JSONException ex)
//                                        {
//                                            ex.printStackTrace();
//                                        }
//                                        if (staticJsonObj.getInt("rcode") == 1)
//                                        {
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    progressdialog.dismiss();
//                                                }
//                                            });
//                                            insertclaimfinal();
//                                        }
//                                    } catch (final IOException ex) {
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                progressdialog.dismiss();
//                                            }
//                                        });
//                                        ex.printStackTrace();
////                                        MainActivity.MobileErrorLog(reqidval,"ImageDeclaration-pushaudio",ex.toString(),ex.toString());
//                                    }
//                                }
//
//                            }
//                            catch (Exception ex) {
//                                // TODO Auto-generated catch block
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        progressdialog.dismiss();
//                                    }
//                                });
//                                ex.printStackTrace();
////                                MainActivity.MobileErrorLog(reqidval,"ImageDeclaration-pushaudio",ex.toString(),ex.toString());
//                            }
//
//    }

    public void pushfinalsignature() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected() == true) {
                Thread thread = new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        mydb = new DatabaseHelper(StolenImageDeclaration.this);
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
                        Bitmap signatureBitmap = bitmapaftersign;
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        simagebase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        SharedPreferences certifPref = getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                        String certnum = certifPref.getString(ClaimType.CertificateID, "");
                        SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
                        String incident_id = incidePref.getString(ClaimType.CraIdval, "");
                        sdocumentType = "b682f6dc-a040-4210-83e9-d696c4af15c1";
                        String uniqueID = UUID.randomUUID().toString();
                        final String filename = String.valueOf(AudioSavePathInDevice);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog = ProgressDialog.show(activity, getString(R.string.loading), getString(R.string.please_wait), true);
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
                                countvalidatenew = countvalidateincr + 1;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                        //  pushfinalaudio();
                                        //insertclaimfinal();
                                        //
                                        //    MainActivity.MobileErrorLog(reqidval,"StolenSignature",staticJsonObj.getJSONObject("rObj").getString("AttachmentRefNo"),"Stolen Theft Signature");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                //insertclaimpdf();
                                                if(isUserSignedUp()){
                                                    Intent car = new Intent(StolenImageDeclaration.this, ClaimSuccess.class);
                                                    startActivity(car);
                                                }else{
                                                    try {
                                                        showCustomDialog();
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }


                                            }
                                        });
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
                                            AlertDialog.Builder alert = new AlertDialog.Builder(StolenImageDeclaration.this);
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
                        } catch (final IOException | JSONException ex) {
                            runOnUiThread(() -> progressdialog.dismiss());
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
//                            MainActivity.MobileErrorLog(reqidval,"ImageDeclaration-pushsignature",ex.toString(),ex.toString());
                        }
                    }
                });
                thread.start();
            } else {
                Toast.makeText(StolenImageDeclaration.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            runOnUiThread(() -> progressdialog.dismiss());
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
//            MainActivity.MobileErrorLog(reqidval,"ImageDeclaration-pushsignature",ex.toString(),ex.toString());
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_name) {

            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            View screenView = rootView.getRootView();
            screenView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
            screenView.setDrawingCacheEnabled(false);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
            sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
            SharedPreferences.Editor supporteditor = sharedpreferences.edit();
            supporteditor.putString(MainActivity.ReferrenceURL, "Stolen Image Declaration");
            supporteditor.apply();
            sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
            SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
            supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
            supporteditorimg.apply();
            Intent login = new Intent(StolenImageDeclaration.this, SupportTicket.class);
            startActivity(login);
            return true;

            // Do something

        } else {
            try {
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onOptionItemSelected", e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}