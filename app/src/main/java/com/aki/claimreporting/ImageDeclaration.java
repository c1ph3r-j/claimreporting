package com.aki.claimreporting;

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
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
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

public class ImageDeclaration extends AppCompatActivity {

    public static final int RequestPermissionCode = 1;
    public static String mAudioFileName;
    public static RequestBody body;
    public static String reqidval, simagenameNW;
    public static LinearLayout botlinsignlay;
    public static String ImageName, simagename, ImageUniqeCaptureID, ImageisthirdSign;
    public static ListView list;
    public ProgressDialog progressdialog;
    public LinearLayout linearimage1, layout;
    public TextView txtpageno;
    public ImageView imgview;
    public int countvalidate;
    public int countvalidatenew;
    public int countvalidateincr;
    public TextView signhereval;
    public Bitmap bitmapaftersign;
    public ImageView clearsign;
    public SignaturePad claimsignature;
    public String simagebase64, sdocumentType;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    // ArrayList<UploadImageModelDeclaration> imagelist = new ArrayList<UploadImageModelDeclaration>();
    public int currentpageno, totallistvalue, imagesrc, currentvalue, currentforpageno;
    ImageView buttonStart, buttonrecording, buttonplay, buttonplaying, audiorecorddone;
    File AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    ArrayList<ImageDeclarationFragment> imageList;
    Activity activity;
    ViewPager2 ImageDelcareViewPager;
    TabLayout tabDotsForImageDeclare;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    MediaPlayer mediaPlayer;
    ArrayList dataModels;
    ListView listView;
    DatabaseHelper mydb;
    LinearLayout imagedeclare;
    TextView submitclaimtxt;
    ArrayList<AttachmentimageList> attachdeclareimgist = new ArrayList<>();
    private boolean isSignatured = false;
    private FirebaseCrashlytics mCrashlytics;

    public static void enablelayout(Activity activity) {
        LinearLayout botlinsign = (LinearLayout) activity.findViewById(R.id.bottomlistid);
        botlinsign.setVisibility(View.VISIBLE);
    }

    public static void disablelayout(Activity activity) {
        LinearLayout botlinsign = (LinearLayout) activity.findViewById(R.id.bottomlistid);
        botlinsign.setVisibility(View.VISIBLE);
    }

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

    public static String localToGMTNew() {

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
        }
        return finalDateString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_declaration);
        preventSizeChange(this, getSupportActionBar());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Review Photographs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        activity = this;
        countvalidatenew = 0;
        countvalidate = 0;
        init();
    }

    public void init() {

        try {
            botlinsignlay = (LinearLayout) findViewById(R.id.bottomlistid);

            ImageDelcareViewPager = findViewById(R.id.ImageDelcareViewPager);
            tabDotsForImageDeclare = findViewById(R.id.tabDotsForImageDeclare);
            imageList = new ArrayList<>();
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
            }

            // buttonStart = (ImageView) findViewById(R.id.Imgrecord);
            imagedeclare = findViewById(R.id.imagedeclareid);
            submitclaimtxt = findViewById(R.id.submitclaimtxt);

            try {
                if (isNetworkConnected() == true) {
                    submitclaimtxt.setText("SUBMIT YOUR CLAIM");
                } else {
                    submitclaimtxt.setText("SAVE YOUR OFFLINE CLAIM");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // buttonrecording = (ImageView) findViewById(R.id.Imgrecording);
            // audiorecorddone = (ImageView) findViewById(R.id.audiorecorededdone);
            // buttonplay = (ImageView) findViewById(R.id.Imgplay);
            //  buttonplaying = (ImageView)findViewById(R.id.Imgplaying);
            //  buttonStart.setVisibility(View.VISIBLE);
            //  buttonrecording.setVisibility(View.GONE);
            //  buttonplay.setVisibility(View.GONE);
            //   audiorecorddone.setVisibility(View.GONE);
            //buttonplay.setVisibility(View.VISIBLE);
            // buttonplaying.setVisibility(View.GONE);

            try {
                imagedeclare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    Bitmap signatureBitmap = signaturePad.getTransparentSignatureBitmap();
//                    ObjectDetectorActivity.Global.img = signatureBitmap;
//                    MainActivity.uploadimages(ImageDeclaration.this);
//                    Intent car = new Intent(ImageDeclaration.this, Videorecorder.class);
//                    startActivity(car);
                        /*  pushsignature();*/

                        if (isNetworkConnected() == true) {
                            try {
                                if (isSignatured == true) {
                                    bitmapaftersign = claimsignature.getSignatureBitmap();
                                    pushfinalsignature();



                                } else {
                                    Toast.makeText(ImageDeclaration.this, getString(R.string.ClaimSignatureRequired), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Toast.makeText(ImageDeclaration.this, ex.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Intent car = new Intent(ImageDeclaration.this, ClaimOfflineSuccess.class);
                            startActivity(car);
                        }


                        // insertclaimfinal();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
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
//                    Toast.makeText(ImageDeclaration.this, "Recording started",
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
//                    /*pushaudio();*/
//                    buttonStart.setVisibility(View.VISIBLE);
//                    buttonrecording.setVisibility(View.GONE);
//                    audiorecorddone.setVisibility(View.VISIBLE);
//                    buttonplay.setVisibility(View.VISIBLE);
//                    Toast.makeText(ImageDeclaration.this, "Recording Completed",
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
//                    Toast.makeText(ImageDeclaration.this, "Recording Playing",
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
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            getImageList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showCustomDialog() {
        // Create and set up the dialog
        final Dialog dialog = new Dialog(ImageDeclaration.this);
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
                Intent car = new Intent(ImageDeclaration.this, ClaimSuccess.class);
                startActivity(car);
            }
        });

        sign_in_now_btn.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(ImageDeclaration.this,Registration.class);
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
//    public void MediaRecorderReady(){
//        mediaRecorder=new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
//        mediaRecorder.setOutputFile(AudioSavePathInDevice);
//    }

//    private File createAudioFileName() throws IOException {
////        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
////        String prepend = "VIDEO_" + timestamp + "_";
////
////
////        File videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);
////        mVideoFileName = videoFile.getAbsolutePath();
////        SharedPreferences sharedPreference = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
////        SharedPreferences.Editor editor = sharedPreference.edit();
////        editor.putString("videofilepath", mVideoFileName);
////        editor.apply();
////        return videoFile;
//
//        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String prepend = "Audio_" + timestamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
//        File audioFile = File.createTempFile(
//                prepend,  /* prefix */
//                ".3gp",         /* suffix */
//                storageDir      /* directory */
//        );
//        mAudioFileName = audioFile.getAbsolutePath();
////        SharedPreferences sharedPreference = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
////        SharedPreferences.Editor editor = sharedPreference.edit();
////        editor.putString("videofilepath", mAudioFileName);
////        editor.apply();
//        return audioFile;
//    }

    private void getImageList() {
        try {
            imageList = new ArrayList<>();
            mydb = new DatabaseHelper(ImageDeclaration.this);
            mCrashlytics = FirebaseCrashlytics.getInstance();


            Cursor curseattach = mydb.getalldecalrationimagesgroup();
            int counttest1 = curseattach.getCount();
            if (counttest1 >= 1) {
                while (curseattach.moveToNext()) {
                    AttachmentimageList element = new AttachmentimageList(
                            curseattach.getString(0)

                    );
                    attachdeclareimgist.add(element);
                    imageList.add(new ImageDeclarationFragment(element));
                }
            }
            Cursor curseattach1 = mydb.getthirdgroupdecalration();
            int counttest12 = curseattach1.getCount();
            if (counttest12 >= 1) {
                while (curseattach1.moveToNext()) {
                    AttachmentimageList element = new AttachmentimageList(
                            curseattach1.getString(0)

                    );
                    attachdeclareimgist.add(element);
                    imageList.add(new ImageDeclarationFragment(element));
                }
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //ArrayList<AllVehicleResponse> arrayOfUsers = allvehclist;
//                    ImageDeclareAdapter attachListView = new ImageDeclareAdapter(ImageDeclaration.this, attachdeclareimgist,ImageDeclaration.this);
//                    list = (ListView) findViewById(R.id.listimgdeclareimages);
//                    //  list.addFooterView(botlinsignlay);
//                    list.setAdapter(attachListView);
                    try {
                        ImageDeclarationAdapter viewPagerImageAdapter = new ImageDeclarationAdapter(ImageDeclaration.this, imageList);
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
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void pushfinalaudio()
//    {
//        try
//        {
//
//            final String filenamecheck = String.valueOf(AudioSavePathInDevice);
//            if(filenamecheck == null || filenamecheck == "null" | filenamecheck =="")
//            {
//                insertclaimfinal();
//            }
//            else {
//                progressdialog = new ProgressDialog(this);
//                mydb = new DatabaseHelper(ImageDeclaration.this);
//                if (mydb.getTokendetails().getCount() != 0) {
//                    Cursor curseattachtoken = mydb.getTokendetails();
//                    int counttoken = curseattachtoken.getCount();
//                    if (counttoken >= 1) {
//                        while (curseattachtoken.moveToNext()) {
//                            MainActivity.stokenval = curseattachtoken.getString(1);
//                        }
//                    }
//                }
//                SharedPreferences certifPref = getSharedPreferences("ClaimInsert", MODE_PRIVATE);
//                String certnum = certifPref.getString(ClaimRegFragment.CertificateID, "");
//                SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
//                String incident_id = incidePref.getString(ClaimRegFragment.CraIdval, "");
//                sdocumentType = "064b1ed5-a782-41ba-ba78-d73a443a08c2";
//                String uniqueID = UUID.randomUUID().toString();
//                final String filename = String.valueOf(AudioSavePathInDevice);
//                SharedPreferences locationPref = getSharedPreferences("LocationPref", MODE_PRIVATE);
//                // SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
//                String dateTime = localToGMT();
//                SharedPreferences imgnameuniqueid = getSharedPreferences("UniqueCaptureID",MODE_PRIVATE);
//                String simagenameuniqueid = imgnameuniqueid.getString(ImageUniqeCaptureID,"");
//                SharedPreferences thirdpartid = activity.getSharedPreferences("ThirdPartyID",Context.MODE_PRIVATE);
//                final String thirdpartidval = thirdpartid.getString(ThirdPartyDetails.ThirdPartyImgID, "");
//                MediaType mediaType = MediaType.parse("application/octet-stream");
//                //MainActivity.postURL = "https://uat-aki.claims.digital/api/app/Claim/UploadClaimFiles";
//                MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/UploadClaimFiles";
//                OkHttpClient client = new OkHttpClient.Builder()
//                        .connectTimeout(120, TimeUnit.SECONDS)
//                        .writeTimeout(120, TimeUnit.SECONDS)
//                        .readTimeout(120, TimeUnit.SECONDS)
//                        .build();
//
//                    body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                            .addFormDataPart("Images", filename,
//                                    RequestBody.create(MediaType.parse("application/octet-stream"),
//                                            new File(filename)))
//                            .addFormDataPart("certificateNo", certnum)
//                            .addFormDataPart("incidentUniqueCode", incident_id)
//                            .addFormDataPart("documentType", sdocumentType)
//                            .addFormDataPart("geoTagLat",locationPref.getString(MainActivity.Latitude, null))
//                            .addFormDataPart("geoTagLon",locationPref.getString(MainActivity.Longitude, null))
//                            .addFormDataPart("captureAttachmentID", uniqueID)
//                            .addFormDataPart("captureDateTime",dateTime)
//                            .build();
//                Request request = new Request.Builder()
//                        .url(MainActivity.postURL)
//                        .method("POST", body)
//                        .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
//                        .addHeader("MobileParameter", MainActivity.InsertMobileparameters())
//                        .build();
//                Response staticResponse = null;
//                try {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
//                            // progressdialog.show(activity, "Loading", "Please wait...", true);
//                        }
//                    });
//                    staticResponse = client.newCall(request).damagedPartOfTheCar();
//                    String staticRes = staticResponse.body().string();
//                    Log.i(null, staticRes);
//                    final JSONObject staticJsonObj = new JSONObject(staticRes);
//                    try {
//                        reqidval = staticJsonObj.getString("reqID");
//                    } catch (JSONException ex) {
//                        ex.printStackTrace();
//                    }
//                    if (staticJsonObj.getInt("rcode") == 1) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                progressdialog.dismiss();
//                            }
//                        });
//                        insertclaimfinal();
//                    }
//                    else
//                    {   runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressdialog.dismiss();
//                        }
//                    });
//                        insertclaimfinal();
//                    }
//                } catch (final IOException ex) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressdialog.dismiss();
//                        }
//                    });
//                    ex.printStackTrace();
//                    MainActivity.MobileErrorLog( reqidval, "ImageDeclaration-pushaudio", ex.toString(), ex.toString());
//                }
//            }
//        }
//        catch (Exception ex) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    progressdialog.dismiss();
//                }
//            });
//            // TODO Auto-generated catch block
//            ex.printStackTrace();
//            MainActivity.MobileErrorLog(reqidval,"ImageDeclaration-pushaudio",ex.toString(),ex.toString());
//        }
//
//
//
//    }

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

    public void insertclaimfinal() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected() == true) {

                if (checkGPSStatus() == true) {

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
                            String certnum = certifPref.getString("CertificateID", "");
                            String vehicrefid = certifPref.getString("Vechilerefid", "");
                            MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/AddClaim";
                            SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                            String incidenttypeval = incitype.getString("typeidincident", "");
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");

                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(360, TimeUnit.SECONDS)
                                    .writeTimeout(360, TimeUnit.SECONDS)
                                    .readTimeout(360, TimeUnit.SECONDS)
                                    .build();
                            JsonObject Details = new JsonObject();
                            //Details.addProperty("incidentUniqueCode",incident_id);
                            Details.addProperty("incidentUniqueCode", incident_id);
                            Details.addProperty("incLocation", locPref.getString(MainActivity.Address1, ""));
                            Details.addProperty("driverUserId", driver_id);
                            Details.addProperty("certificateNo", certnum);
                            Details.addProperty("claimTypeID", incidenttypeval);
                            Details.addProperty("VehicleId", vehicrefid);
                            //Device unique code
                            String imeiInput = Settings.Secure.getString(ImageDeclaration.this.getContentResolver(), Settings.Secure.ANDROID_ID);
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
//                                        progressdialog = ProgressDialog.show(activity, getString(R.string.loading), getString(R.string.please_wait), true);

                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
                                    }
                                });
                                staticResponse = client.newCall(request).execute();
                                String staticRes = staticResponse.body().string();
                                Log.i(null, staticRes);
                                if (staticRes.equals("")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
                                            Toast.makeText(ImageDeclaration.this, "Please contact administrator to proceed and try again", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                } else {
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
                                        runOnUiThread(progressdialog::dismiss);
                                        try {
                                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                            JSONObject index = rmsg.getJSONObject(0);
                                            runOnUiThread(() -> {
                                                String errorText;
                                                try {
                                                    errorText = index.getString("errorText");
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(ImageDeclaration.this);
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
                                                }finally {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if(progressdialog != null){
                                                                if(progressdialog.isShowing()){
                                                                    progressdialog.dismiss();
                                                                }
                                                            }
                                                        }
                                                    });
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(progressdialog != null){
                                            if(progressdialog.isShowing()){
                                                progressdialog.dismiss();
                                            }
                                        }
                                    }
                                });
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(reqidval, "ImageDeclaration-insertclaimfinal", ex.toString());
                                mCrashlytics.recordException(ex);
                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(progressdialog != null){
                                            if(progressdialog.isShowing()){
                                                progressdialog.dismiss();
                                            }
                                        }
                                    }
                                });
                            } catch (JSONException ex) {
                                progressdialog.dismiss();
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(reqidval, "ImageDeclaration-insertclaimfinal", ex.toString());
                                mCrashlytics.recordException(ex);
                                runOnUiThread(() -> Toast.makeText(ImageDeclaration.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());

                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                            finally {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(progressdialog != null){
                                            if(progressdialog.isShowing()){
                                                progressdialog.dismiss();
                                            }
                                        }
                                    }
                                });// Toast.makeText(MainActivity.this,ex.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    thread.start();

                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ImageDeclaration.this);
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
                Toast.makeText((Context) this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            //progressdialog.dismiss();
            ex.getStackTrace();
            MainActivity.MobileErrorLog(reqidval, "ImageDeclaration-insertclaimfinal", ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }

    }

    public void pushfinalsignature() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected() == true) {
                try {
                    Thread thread = new Thread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            mydb = new DatabaseHelper(ImageDeclaration.this);
                            if (mydb.getTokendetails().getCount() != 0) {
                                Cursor curseattachtoken = mydb.getTokendetails();
                                int counttoken = curseattachtoken.getCount();
                                if (counttoken >= 1) {
                                    while (curseattachtoken.moveToNext()) {
                                        MainActivity.stokenval = curseattachtoken.getString(1);
                                    }
                                }
                            }

                            Bitmap signatureBitmap = bitmapaftersign;
                            //ObjectDetectorActivity.Global.img = signatureBitmap;
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            simagebase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            SharedPreferences certifPref = getSharedPreferences("ClaimInsert", MODE_PRIVATE);
                            String certnum = certifPref.getString("CertificateID", "");
                            SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
                            String incident_id = incidePref.getString("CraIdval", "");
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

//                            body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                                    .addFormDataPart(
//                                            "Images","Signature.jpg",
//                                            RequestBody.create(mediaType, byteArray))
//                                    .addFormDataPart("certificateNo",certnum)
//                                    .addFormDataPart("incidentUniqueCode",incident_id)
//                                    .addFormDataPart("documentType",sdocumentType)
//                                    .addFormDataPart("geoTagLat",locationPref.getString(MainActivity.Latitude, null))
//                                    .addFormDataPart("geoTagLon",locationPref.getString(MainActivity.Longitude, null))
//                                    .addFormDataPart("captureAttachmentID", uniqueID)
//                                    .addFormDataPart("captureDateTime",dateTime)
//                                    .build();

                            body = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("fileName", "Signature.jpg")
                                    .addFormDataPart(
                                            "image", "Signature.jpg",
                                            RequestBody.create(mediaType, byteArray))
                                    .addFormDataPart("certificateNo", certnum)
                                    .addFormDataPart("incidentUniqueCode", incident_id)
                                    //.addFormDataPart("incidentUniqueCode","221215-39")
                                    .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                    .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                    .addFormDataPart("captureAttachmentID", uniqueID)
                                    .addFormDataPart("captureDateTime", dateTime)
                                    .addFormDataPart("attachmentTypeID", sdocumentType)
                                    .addFormDataPart("isReturnURL", "false")
                                    .build();

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
                                }
                                if (staticJsonObj.getInt("rcode") == 1) {


                                    /*runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
//                                            progressdialog.dismiss();
                                           // insertclaimfinal();
                                            // pushfinalaudio();
                                            //insertclaimfinal();
                                        }
                                    });*/


                                    // insertclaimfinal();
//
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        progressdialog.dismiss();
//
//                                    }
//                                });

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
                                            //insertclaimpdf();
                                            mydb = new DatabaseHelper(ImageDeclaration.this);
                                            if (mydb.getclaimofflineiddetails().getCount() != 0) {
                                                mydb.deleteclaimofflineiddata();
                                            }
                                            MainActivity.VideoEvidence = false;
                                            MainActivity.VideoDeclaration = false;
                                            SharedPreferences sharedPreference = getSharedPreferences("VisualImageFile", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreference.edit();
                                            editor.putString("visimagefilepath", "");
                                            editor.apply();

                                            SharedPreferences sharedPreference1 = getSharedPreferences("VisualAudioFile", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor1 = sharedPreference1.edit();
                                            editor1.putString("visaudiofilepath", "");
                                            editor1.apply();

                                            SharedPreferences sharedPreference2 = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor2 = sharedPreference2.edit();
                                            editor2.putString("videofilepath", "");
                                            editor2.apply();


                                            SharedPreferences sharedPreference3 = getSharedPreferences("VideoFileLocation", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor3 = sharedPreference3.edit();
                                            editor3.putString("videofilepathlocation", "");
                                            editor3.apply();
                                            runOnUiThread(progressdialog::dismiss);
                                            if(isUserSignedUp()){
                                                Intent car = new Intent(ImageDeclaration.this, ClaimSuccess.class);
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
                                } else {
                                    runOnUiThread(progressdialog::dismiss);
                                    try {
                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                        JSONObject index = rmsg.getJSONObject(0);
                                        runOnUiThread(() -> {
                                            String errorText;
                                            try {
                                                errorText = index.getString("errorText");
                                                AlertDialog.Builder alert = new AlertDialog.Builder(ImageDeclaration.this);
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
                            } catch (final Exception ex) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                    }
                                });
                                // progressdialog.dismiss();
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(reqidval, "ImageDeclaration-insertsignature", ex.toString());
                                mCrashlytics.recordException(ex);
                                //   Toast.makeText(ClaimSuccess.this,ex.toString(), Toast.LENGTH_LONG).show();
                                // getActivity().runOnUiThread(new Runnable() {
//                                        public void run() {
//
//                                            Toast.makeText(getActivity(),
//                                                    ex.toString(), Toast.LENGTH_LONG).show();
//                                        }
//                                    });
                            } finally {
                                // Toast.makeText(MainActivity.this,ex.toString(), Toast.LENGTH_LONG).show();
                            }
                            //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                        }
                    });
                    thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "pushFinalSignature", e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            } else {
                //  Toast.makeText(getActivity(),getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressdialog.dismiss();
                }
            });
            //progressdialog.dismiss();
            ex.getStackTrace();
            MainActivity.MobileErrorLog(reqidval, "ImageDeclaration-insertsignaturef", ex.toString());
            mCrashlytics.recordException(ex);
        }finally {
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
            supporteditor.putString(MainActivity.ReferrenceURL, "Accident Image Declaration");
            supporteditor.apply();
            sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
            SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
            supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
            supporteditorimg.apply();
            Intent login = new Intent(ImageDeclaration.this, SupportTicket.class);
            startActivity(login);
            return true;

            // Do something

        } else {
            try {
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return super.onOptionsItemSelected(item);
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
}