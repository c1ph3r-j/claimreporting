package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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


public class Videorecorder extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    public static String imagename, incident_id;
    public static String reqidval;
    public static String mVideoFileName;
    private static SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    public FirebaseCrashlytics mCrashlytics;
    public android.app.ProgressDialog progressdialog;
    public TextView prcd;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    public String stopval;
    public JSONArray arrvideodatanew;
    DatabaseHelper mydb;
    ArrayList<VideoKycResponse> videoKycResponses = new ArrayList<>();
    private TextureView mTextureView;
    private CameraDevice mCameraDevice;
    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private String mCameraId;
    private Size mPreviewSize;
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    private Chronometer mChronometer;
    private int mTotalRotation;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private ImageButton mRecordImageButton, mRecordstop;
    private TextView kyctextnew;
    private boolean mIsRecording = false;
    private final CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            String methodName = "onOpened";
            mCameraDevice = camera;
            if (mIsRecording) {
                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
                startRecord();
                mMediaRecorder.start();
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.setVisibility(View.VISIBLE);
                mChronometer.start();
            } else {
                startPreview();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setupCamera(320, 240);
            connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };
    private File mVideoFolder;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String localToGMT() {
        String finalDateString = "";
        try {
            // Get the current date and time in GMT
            Date currentDate = new Date();
            SimpleDateFormat gmtFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US);
            gmtFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

            finalDateString = gmtFormatter.format(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
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

    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrienatation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        return (sensorOrienatation + deviceOrientation + 360) % 360;
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<Size>();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else {
            return choices[0];
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videorecorder);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = "onCreate";
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Visual Evidence");
            preventSizeChange(this, getSupportActionBar());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#C3BE49"));
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
            videokyctext();
            createVideoFolder();
            stopval = "0";
            kyctextnew = (TextView) findViewById(R.id.kyctext);
//        locationPref = getSharedPreferences("LocationPref",MODE_PRIVATE);
//        final String latitude = locationPref.getString(Latitude, null);
//        final String longitude = locationPref.getString(Longitude, null);
//        vehicleRegPref = getSharedPreferences("VehicleRegistration",MODE_PRIVATE);
//        final String vehInstance = vehicleRegPref.getString(VehicleInstanceID,null);
//        final String instanceType = vehicleRegPref.getString(InstanceTypeID,null);
//        final String[] arrData = {"Hi","Video KYC","Hope you like it","Cheers!"};
//        // final ImageButton[] all= {btn1, btn2, btn3, btn4};
//        final Handler handler1 = new Handler();
//        try {
//
//            for (int i = 0; i<=arrData.length ; i++) {
//                final String positionnew = arrData[i];
//
//                handler1.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        kyctextnew.setText(positionnew);
//                    }
//
//                }, 4000 * i);
//            }
//
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }

            /*  TextView prcd = (TextView) findViewById(R.id.prcdid);*/
            prcd = (TextView) findViewById(R.id.visualdeclarenext);

            prcd.setOnClickListener(onClickProceed -> {
                try {
                    // MainActivity.Global.isVideoTaken = true;
                    //  finish();
                    if (Objects.equals(stopval, "0")) {
                        // finish();
                        Intent intent = new Intent(Videorecorder.this, ClaimVisualArtifacts.class);
                        startActivity(intent);
//                    SharedPreferences incitype = getSharedPreferences("IncidentType",MODE_PRIVATE);
//                    String incidenttypeval = incitype.getString(ClaimRegFragment.typeidincident,"");
//                    if(incidenttypeval == "B2EC755A-88EF-4F53-8911-C13688D226D3")
//                    {
//                        SharedPreferences thirdisPref = getSharedPreferences("IsThirdParty",MODE_PRIVATE);
//                        String isthird = thirdisPref.getString(ClaimRegFragment.Thirdpartyavailable,"");
//                        if(isthird.equals("1"))
//                        {
//                            insertclaimfinal();
//
//                        }
//                        else
//                        {
//                            Intent signUpIntent = new Intent(Videorecorder.this, StolenImageDeclaration.class);
//                            startActivity(signUpIntent);
//                        }
//
//                    }
//                    else
//                    {
//                        Intent signUpIntent = new Intent(Videorecorder.this, ImageDeclaration.class);
//                        startActivity(signUpIntent);
//                    }
                    } else if (Objects.equals(stopval, "1")) {
                        Toast.makeText(getApplicationContext(), "Please stop the video to proceed", Toast.LENGTH_SHORT).show();
                    } else {
                        //finish();
                        UploadVisualDeclaration();
                        Intent intent = new Intent(Videorecorder.this, ClaimVisualArtifacts.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });
            mMediaRecorder = new MediaRecorder();
            mChronometer = (Chronometer) findViewById(R.id.chronometer);
            mTextureView = (TextureView) findViewById(R.id.textureView);
            mRecordstop = (ImageButton) findViewById(R.id.recordid);
            mRecordstop.setOnClickListener(onClickRecordStop -> {
                try {
                    if (!mIsRecording) {
                        stopval = "1";
                        checkWriteStoragePermission();
                        prcd.setText("NEXT");
                        Getvkyctext();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }


            });
            mRecordImageButton = (ImageButton) findViewById(R.id.recordstpid);
            mRecordImageButton.setOnClickListener(onClickRecordImage -> {
                try {
                    if (mIsRecording) {
                        mChronometer.stop();
                        stopval = "2";
                        mChronometer.setVisibility(View.INVISIBLE);
                        mIsRecording = false;
                        if (null != mMediaRecorder) {
                            try {
                                mMediaRecorder.stop();
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                        }
                        mMediaRecorder.reset();
                        startPreview();

                    } else {
                        Toast.makeText(Videorecorder.this, "You haven't captured the video. Please take a video.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
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

    public byte[] videoconvert(String path) throws IOException {
        String methodName = "videoConvert";
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
        int n;
        while (-1 != (n = fis.read(buf)))
            bos.write(buf, 0, n);
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    public void UploadVisualDeclaration() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            Thread thread = new Thread(() ->
                    AsyncTask.execute(() -> {
                        try {
                            if (isNetworkConnected()) {
                                if (checkGPSStatus()) {
                                    mydb = new DatabaseHelper(Videorecorder.this);
                                    if (mydb.getTokendetails().getCount() != 0) {
                                        Cursor curseattachtoken = mydb.getTokendetails();
                                        int counttoken = curseattachtoken.getCount();
                                        if (counttoken >= 1) {
                                            while (curseattachtoken.moveToNext()) {
                                                MainActivity.stokenval = curseattachtoken.getString(1);
                                            }
                                        }
                                    }
                                    Thread thread1 = new Thread(() -> {
                                        SharedPreferences incidePref = getSharedPreferences("IncidentUniqueID", MODE_PRIVATE);
                                        incident_id = incidePref.getString(ClaimType.CraIdval, ""); //error Incident1type class and shared pref

                                        SharedPreferences videonPref = getSharedPreferences("VideoFile", MODE_PRIVATE);
                                        final String filename = videonPref.getString("videofilepath", "");
                                        SharedPreferences locationPref = getSharedPreferences("LocationPref", MODE_PRIVATE);
                                        String uniqueID = UUID.randomUUID().toString();
                                        // SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                                        String dateTime = localToGMT();
                                        // MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/UploadClaimFiles";
                                        MainActivity.postURL = getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                                        OkHttpClient client = new OkHttpClient.Builder()
                                                .connectTimeout(120, TimeUnit.SECONDS)
                                                .writeTimeout(120, TimeUnit.SECONDS)
                                                .readTimeout(120, TimeUnit.SECONDS)
                                                .build();
                                        RequestBody body = new MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("fileName", filename)
                                                .addFormDataPart(
                                                        "image", filename,
                                                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                                                new File(filename)))
                                                .addFormDataPart("transactionID", incident_id)
                                                .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                                .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                                .addFormDataPart("captureAttachmentID", uniqueID)
                                                .addFormDataPart("captureDateTime", dateTime)
                                                .addFormDataPart("attachmentTypeID", "5c6cf73c-d686-44d5-824c-f7d9ec7a7f30")
                                                .addFormDataPart("isReturnURL", "false")
                                                .build();
                                        Request request = new Request.Builder()
                                                .url(MainActivity.postURL)
                                                .method("POST", body)
                                                .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                                .build();
                                        Response staticResponse;
                                        try {
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
                                                System.out.println("DONE");
                                            }
                                        } catch (final Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    });
                                    thread1.start();
                                } else {
                                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Videorecorder.this);
                                    dialog.setMessage("GPS locations is not enabled.Please enable it");
                                    dialog.setPositiveButton("Ok", (dialog1, which) -> {
                                        //this will navigate user to the device location settings screen
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent);
                                    });
                                    android.app.AlertDialog alert = dialog.create();
                                    alert.show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    }));
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void Getvkyctext() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            runOnUiThread(() -> {
                Thread thread = new Thread(() -> {
                    try {
                        JSONArray arraylistdata = arrvideodatanew;
                        int arraysize = arraylistdata.length() - 1;
                        int ival = 0;
                        boolean isvalreached = true;
                        while (isvalreached) {
                            int ivalincr = ival;
                            JSONObject vehicleObj = arraylistdata.getJSONObject(ivalincr);
                            String seqno = vehicleObj.getString("vkycSequenceNo");
                            String sec = vehicleObj.getString("displaySeconds");
                            int secval = Integer.parseInt(sec);
                            String vktxt = vehicleObj.getString("vkycText");
                            runOnUiThread(() -> kyctextnew.setText(vktxt));
                            Thread.sleep(1000 * secval);
                            if (arraysize == ivalincr) {
                                isvalreached = false;
                            }
                            ival = ivalincr + 1;
                        }
            /*    for (int i = 0; i<=arraysize ; i++) {
                    JSONObject vehicleObj = arraylistdata.getJSONObject(i);

                    String seqno = vehicleObj.getString("vkycSequenceNo");
                    String sec = vehicleObj.getString("displaySeconds");
                    int valsec = 5000;
                    String vktxt = vehicleObj.getString("vkycText");
                    int timerf = 5000 *i;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    TextView  kyctextnew = (TextView) findViewById(R.id.kyctext);
                                    kyctextnew.setText(vktxt);

                                }
                            });
                        }
                    }, 5000);

                   */
                        /* final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    TextView  kyctextnew = (TextView) findViewById(R.id.kyctext);
                                    kyctextnew.setText(vktxt);

                                }
                            });

                        }
                    }, 2 *1000);*/
                        /*


i++;

                    *//*try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*//*

                         *//* try {

                        Thread.sleep(2000 * i) ;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;*//*

                         *//*      VideoKycResponse element = new VideoKycResponse(vehicleObj.getString("vkycSequenceNo"),vehicleObj.getString("displaySeconds"),vehicleObj.getString("vkycText"));
                    videoKycResponses.add(element);*//*
                    //JSONArray jsArray = new JSONArray(videoKycResponses);
                    //String dvsvd = "dsv";
                    //  kyctextnew.setText(vktxt);
                    // Thread.sleep(valsec);
                                                *//*int newval =  i;
                                                int newval1 =  newval;
                                                int newdelay = new Integer(sec)*1000;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        kyctextnew.setText(vktxt);
                                                        try {
                                                            Thread.sleep(newdelay);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });*//*

                         *//* final Handler handler = new Handler();
                                                Runnable runnable = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // do something
                                                        kyctextnew.setText(vktxt);
                                                        handler.postDelayed(this, 5000L);  // 1 second delay
                                                    }
                                                };
                                                handler.post(runnable);

                                                final Handler handler1 = new Handler();

                                                try {

                                                    handler1.postDelayed(new Runnable() {

                                                        @Override
                                                        public void run() {


                                                            if(newval == arraysize)
                                                            {
                                                                testbol = true;
                                                              //  stopec();
                                                            }

                                                        }

                                                    }, newdelay);


                                                }
                                                catch (Exception ex)
                                                {
                                                    ex.printStackTrace();
                                                    MainActivity.MobileErrorLog(reqidval,"ThirdpartyCarView400-videokyctext",ex.toString(),ex.toString());
                                                }
*//*

                }*/

                /*int arraysize = videoKycResponses.size() - 1;
                int arraysize1 = arraysize;
                ArrayList<VideoKycResponse> videoKycResponsesnew = videoKycResponses;

                JSONArray jsonArray = new JSONArray();

                for (int i=0; i < videoKycResponses.size(); i++) {
                    jsonArray.put(videoKycResponses.get(i));

                }
                String sval = "fd";
                int arraysizenew = jsonArray.length() - 1;
                int arraysize1new = arraysizenew;
                for (int i = 0; i<=arraysizenew ; i++) {
                    JSONObject vehicleObj = jsonArray.getJSONObject(i);

                    String seqno = vehicleObj.getString("vkycSequenceNo");
                    String sec = vehicleObj.getString("displaySeconds");
                    int valsec = 5000;
                    String vktxt = vehicleObj.getString("vkycText");
                    kyctextnew.setText(vktxt);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }*/
                /*for (int i=0; i < videoKycResponses.size(); i++) {
                    jsonArray.put(videoKycResponses.get(i));
                    JSONObject vehicleObj = jsonArray.getJSONObject(i);
                    String seqno = vehicleObj.getString("vkycSequenceNo");
                    String sec = vehicleObj.getString("displaySeconds");
                    int valsec = 5000;
                    String vktxt = vehicleObj.getString("vkycText");
                    kyctextnew.setText(vktxt);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;

                }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                });
                thread.start();
            });

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(Videorecorder.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

    public void videokyctext() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                if (checkGPSStatus()) {
                    mydb = new DatabaseHelper(Videorecorder.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                MainActivity.stokenval = curseattachtoken.getString(1);
                                // stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w";
                                //stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w";
                                //  stokenval = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ilg1ZVhrNHh5b2pORnVtMWtsMll0djhkbE5QNC1jNTdkTzZRR1RWQndhTmsifQ.eyJleHAiOjE2MTIyNTM4NTYsIm5iZiI6MTYxMjI1MDI1NiwidmVyIjoiMS4wIiwiaXNzIjoiaHR0cHM6Ly9jcmF1YXR2Mi5iMmNsb2dpbi5jb20vNjBjMTY5ZmItMDZlYy00ZWMyLWFkYjMtM2NlM2U2YjE5ZGEzL3YyLjAvIiwic3ViIjoiMTczYWU2Y2MtN2M2MS00NWI1LWFiZjMtZWQ1OGNkOTY0YWNkIiwiYXVkIjoiNDMwNzJiYTAtZDY2Ni00MzVkLWE0YjMtYzMwZDZmZDdhMmYxIiwibm9uY2UiOiJkZWZhdWx0Tm9uY2UiLCJpYXQiOjE2MTIyNTAyNTYsImF1dGhfdGltZSI6MTYxMjI1MDI1Niwib2lkIjoiMTczYWU2Y2MtN2M2MS00NWI1LWFiZjMtZWQ1OGNkOTY0YWNkIiwibmFtZSI6InVua25vd24iLCJlbWFpbHMiOlsicmFtZXNoLnNAc3dpZnRhbnQuY29tIl0sInRmcCI6IkIyQ18xX3NpZ25pbl9hbmRfc2lnbnVwIn0.l5Ww3TIhU1vHzyEUjughWcJ84aTrSPXAx2lwbEFU0NnCmYJG_Q5zcLBgdRN7uR7LPAU3ViZeqlKhFG3bYOMTRLWqi1f06CxAwX1VjLGHEaSBCCBIBBd5GZT6y5eYT5_oh2BdcKSjmCzly5A_c5K8fpEuxLenc47Ob8UrdS8vo5c3RIi3PsiaKEqMKQ9fKtIvSF__vu5zG0yH3-uzvqvHGaJfvx5jXIja5xNnYIoHoMmWkPHNSU4W4awkE5zUjA7w4MLjIga977eYRF6Coj3w-m3eOUNxCYTx1VY_qa8TybgSuYlhmZxRiv9N4LcsokM8hgAQdMbEMhfIy_NxOygv4g";
                            }
                        }
                    }
                    // stokenval="eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6IjE5YzRiNTVjLWUyMzQtNDZlMy1hOTZlLWZjYThiOGM3YjhmMyIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiMmJkMzY3YjgtNTAxMi00MGY0LWI4Y2YtOWYwMGM3OTlkODZkIiwiUHJpbWFyeUdyb3VwU2lkIjoiMTljNGI1NWMtZTIzNC00NmUzLWE5NmUtZmNhOGI4YzdiOGYzIiwibmJmIjoxNjEyNDIzNzI4LCJleHAiOjE2NDM1Mjc3MjgsImlhdCI6MTYxMjQyMzcyOCwiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.ejjLV1gsYFKXHQdnnmvoQGh15eIFUCTKpaf2BSS5tpgfgvGKfDssp6uhHmAF65DWx1DnUggejpLXpI90uuPO8AL-Lx1oEOW2eRRyiyH32pX7RKE8glx5lRgCZl8yTo5gvBKXXqv3ar57mtOQR46j42ETPlgGI-LYCN17XChA7_R-8cMBFxLLBmuOLm_PfIuYMhZMy75o06JUDYN5furImwN3e-1jPaOIJ6flI47echpw-_bERjYY4cpwjQOSVrYBO2zfCFGs_-fjEdC5hbzhj9ODdjcc5voOrX3nbo1JKyrikjDNnj4OG645p8dD4tizR_ScKXo40XO0Bz5mr_5cdRo_vhg72bFqTCuHQ67rulKNYu_EDhfz3uwEnKXAT2ckB4UeDYxkrCpVifbX5rQdgUAML8XDkt3XaEAYJqIApQO7_PYaCefkMVnyo2p0QPeBZy-r5xd7RE9uZk886ESfJ4LT3ct_LneKecoNsxeWaiQzc1pvw68W8SIUEu3bOdIYJ7di04F5x0ICw70Z_opcuJLAffsNhkbzDImy5dy1HFcDXEf1zwWRjDeMJvX958-Ibh3WBagKDZ2A9FdSKEysmJF2eQvFSb3sAl975PjwGKnSUNuK6Zq2GAg_TwH4C-XjU91_N7RqtftWBtpQpBvowtUQjezduoT2aqyp7yXfVMY";

                    //    stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6IjE5YzRiNTVjLWUyMzQtNDZlMy1hOTZlLWZjYThiOGM3YjhmMyIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiMmJkMzY3YjgtNTAxMi00MGY0LWI4Y2YtOWYwMGM3OTlkODZkIiwiUHJpbWFyeUdyb3VwU2lkIjoiMTljNGI1NWMtZTIzNC00NmUzLWE5NmUtZmNhOGI4YzdiOGYzIiwibmJmIjoxNjEyNDIzNzI4LCJleHAiOjE2NDM1Mjc3MjgsImlhdCI6MTYxMjQyMzcyOCwiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.ejjLV1gsYFKXHQdnnmvoQGh15eIFUCTKpaf2BSS5tpgfgvGKfDssp6uhHmAF65DWx1DnUggejpLXpI90uuPO8AL-Lx1oEOW2eRRyiyH32pX7RKE8glx5lRgCZl8yTo5gvBKXXqv3ar57mtOQR46j42ETPlgGI-LYCN17XChA7_R-8cMBFxLLBmuOLm_PfIuYMhZMy75o06JUDYN5furImwN3e-1jPaOIJ6flI47echpw-_bERjYY4cpwjQOSVrYBO2zfCFGs_-fjEdC5hbzhj9ODdjcc5voOrX3nbo1JKyrikjDNnj4OG645p8dD4tizR_ScKXo40XO0Bz5mr_5cdRo_vhg72bFqTCuHQ67rulKNYu_EDhfz3uwEnKXAT2ckB4UeDYxkrCpVifbX5rQdgUAML8XDkt3XaEAYJqIApQO7_PYaCefkMVnyo2p0QPeBZy-r5xd7RE9uZk886ESfJ4LT3ct_LneKecoNsxeWaiQzc1pvw68W8SIUEu3bOdIYJ7di04F5x0ICw70Z_opcuJLAffsNhkbzDImy5dy1HFcDXEf1zwWRjDeMJvX958-Ibh3WBagKDZ2A9FdSKEysmJF2eQvFSb3sAl975PjwGKnSUNuK6Zq2GAg_TwH4C-XjU91_N7RqtftWBtpQpBvowtUQjezduoT2aqyp7yXfVMY";

                    //stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6ImIxNTk5OGVkLWUzNjQtNGMyYy1iZDI5LTc4ODM2MDFjMWQ3NSIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiYzQ4ZjIxNTAtMmYyMy00YmY5LThhNWUtZDJkOGU3YmZlMDdlIiwiUHJpbWFyeUdyb3VwU2lkIjoiYjE1OTk4ZWQtZTM2NC00YzJjLWJkMjktNzg4MzYwMWMxZDc1IiwibmJmIjoxNjExOTQwMjM4LCJleHAiOjE2NDMwNDQyMzgsImlhdCI6MTYxMTk0MDIzOCwiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.OZdutk4UgBiF7MMdyuqASEHKYCDaRIgVi9-ZluBumA9ZLIgYZ8BqGIWEKXwi4VAh-GmLmM_oh6RwHjmyVus2Z846fvUYUxeV3ypQSFUGUVPBePNcCZ1z3b-a_EcltRsRv7OinlAKy_EL0mlZUf3soS-TzYClwlJ-zab1KEFNnEg7la4vYQVTQ-dFg9FyJWc3DpU8pzy2lPoVlRjxTeVl5Fs9a98PYuD_btnZiuFCfXPl3zs3nDzAzhfQkl2zHF-fAJz-9rujfy5HPpATRjDC0__OByfSxW9lMU1rtpLVLJPn86nYBk-z3ustV_Q3OswbIDLhz_85o9nhy0TZjmGjKdnZM9pE6xS4TJZWVfGKeh3pd415TfE-tD-Y4usLAhw794Jsxxz4yxFEaRwFGpudaOb3psX-edWfqFTesVzPr3HvuPVkQ2XTYHdUB55cvtBF4v_kt-Rxml6epQZ-AzxwmGb_n_dwNU-H-OkaD8bS_ClAmaI1Pk3a-2QpDHOAmQziUy0A31ZD9QXJ7N0ofO4oSRPXNe_0CWDVeEnVLLa4GOltYRjPuSuqyiGwyXHYNCMCMPnmU87oySY6LH2TRSVLTMeDaWE8EK3B5DgW-ajTW29pYQ45c4CdcmXr4VzwBWcWfMFst2AK31RZh3kVFvKRWv82DxYCX0Wp0h8JCpRuUGA";
                    // progressdialog = new ProgressDialog(getActivity());
                    MainActivity.encryptedSHA = "";
                    String sourceStr = MainActivity.InsertMobileParameters();
                    try {
                        MainActivity.encryptedSHA = AESCrypt.encrypt(sourceStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread thread = new Thread(() -> {
                        SharedPreferences incidePref = getSharedPreferences("IncidentUniqueID", MODE_PRIVATE);
                        String incident_id = incidePref.getString(ClaimType.CraIdval, ""); //error Incident1type class and shared pref
                        MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/ViewClaimVkyc";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        JsonObject Details = new JsonObject();
                        Details.addProperty("incidentUniqueCode", incident_id);
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
                                runOnUiThread(() -> {
                                    try {
                                        JSONArray arrvideodata = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllVkyc");
                                        try {
                                            int arraysize = arrvideodata.length() - 1;
                                            int arraysize1 = arraysize;
                                            arrvideodatanew = arrvideodata;
                                           /* while (i < arraysize) {

                                                JSONObject vehicleObj = arrvideodata.getJSONObject(i);
                                                String seqno = vehicleObj.getString("vkycSequenceNo");
                                                String sec = vehicleObj.getString("displaySeconds");
                                                String vktxt = vehicleObj.getString("vkycText");
                                                int newdelay = new Integer(sec)*100;
                                                int newval =  i;
                                                final Handler handler1 = new Handler();

                                                try {

                                                    handler1.postDelayed(new Runnable() {

                                                        @Override
                                                        public void run() {

                                                            kyctextnew.setText(vktxt);
                                                            if(newval == arraysize)
                                                            {
                                                                testbol = true;
                                                                stopec();
                                                            }

                                                        }

                                                    }, newdelay );


                                                }
                                                catch (Exception ex)
                                                {
                                                    ex.printStackTrace();
                                                    MainActivity.MobileErrorLog(reqidval,"ThirdpartyCarView400-videokyctext",ex.toString(),ex.toString());
                                                }

                                                i++;
                                               // System.out.println("i is : " + i);
                                            }*/
                                            for (int i = 0; i <= arraysize; i++) {
                                                JSONObject vehicleObj = arrvideodata.getJSONObject(i);

                                                String seqno = vehicleObj.getString("vkycSequenceNo");
                                                String sec = vehicleObj.getString("displaySeconds");
                                                int valsec = 5000;
                                                String vktxt = vehicleObj.getString("vkycText");


                                                VideoKycResponse element = new VideoKycResponse(vehicleObj.getString("vkycSequenceNo"), vehicleObj.getString("displaySeconds"), vehicleObj.getString("vkycText"));
                                                videoKycResponses.add(element);
                                                //JSONArray jsArray = new JSONArray(videoKycResponses);
                                                //String dvsvd = "dsv";
                                                //  kyctextnew.setText(vktxt);
                                                // Thread.sleep(valsec);
                                                /*int newval =  i;
                                                int newval1 =  newval;
                                                int newdelay = new Integer(sec)*1000;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        kyctextnew.setText(vktxt);
                                                        try {
                                                            Thread.sleep(newdelay);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });*/

                                               /* final Handler handler = new Handler();
                                                Runnable runnable = new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // do something
                                                        kyctextnew.setText(vktxt);
                                                        handler.postDelayed(this, 5000L);  // 1 second delay
                                                    }
                                                };
                                                handler.post(runnable);

                                                final Handler handler1 = new Handler();

                                                try {

                                                    handler1.postDelayed(new Runnable() {

                                                        @Override
                                                        public void run() {


                                                            if(newval == arraysize)
                                                            {
                                                                testbol = true;
                                                              //  stopec();
                                                            }

                                                        }

                                                    }, newdelay);


                                                }
                                                catch (Exception ex)
                                                {
                                                    ex.printStackTrace();
                                                    MainActivity.MobileErrorLog(reqidval,"ThirdpartyCarView400-videokyctext",ex.toString(),ex.toString());
                                                }
*/
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                });
                            }
                        } catch (final IOException e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                            runOnUiThread(() -> Toast.makeText(Videorecorder.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    });
                    thread.start();
                }
            } else {
                Toast.makeText(Videorecorder.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(Videorecorder.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (mTextureView.isAvailable()) {
            setupCamera(320, 240);
            connectCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Application will not run without camera services", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mIsRecording = true;
                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onRequestPermissionResult", e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            } else {
                Toast.makeText(this,
                        "App needs to save video to run", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void setupCamera(int width, int height) {
        String methodName = "setupCamera";
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_BACK) {
                    continue;
                }
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                mTotalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
                boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
                int rotatedWidth = width;
                int rotatedHeight = height;
                if (swapRotation) {
                    rotatedWidth = height;
                    rotatedHeight = width;
                }
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
                mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotatedWidth, rotatedHeight);
                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
                } else {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                        Toast.makeText(this,
                                "Video app required access to camera", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                    }, REQUEST_CAMERA_PERMISSION_RESULT);
                }

            } else {
                cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "ConnectCamera", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void startRecord() {
        String methodName = "startRecord";
        try {
            setupMediaRecorder();
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(320, 240);
            Surface previewSurface = new Surface(surfaceTexture);
            Surface recordSurface = mMediaRecorder.getSurface();
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCaptureRequestBuilder.addTarget(recordSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, recordSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            try {
                                session.setRepeatingRequest(
                                        mCaptureRequestBuilder.build(), null, null
                                );
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            //Log.d(TAG, "onConfigureFailed: startRecord");
                        }
                    }, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPreview() {
        String methodName = "startPreview";
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(320, 240);
        Surface previewSurface = new Surface(surfaceTexture);

        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            try {
                                session.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void startBackgroundThread() {
        mBackgroundHandlerThread = new HandlerThread("Camera2VideoImage");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "stopBackgroundThread", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void createVideoFolder() {
        try {
            File movieFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            //  File movieFile = Environment.getExternalStorageDirectory(Environment.DIRECTORY_MOVIES);
            mVideoFolder = new File(movieFile, "camera2VideoImage");
            if (!mVideoFolder.exists()) {
                mVideoFolder.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "createVideoFolder", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    private File createVideoFileName() throws IOException {
//        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String prepend = "VIDEO_" + timestamp + "_";
//
//
//        File videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);
//        mVideoFileName = videoFile.getAbsolutePath();
//        SharedPreferences sharedPreference = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreference.edit();
//        editor.putString("videofilepath", mVideoFileName);
//        editor.commit();
//        return videoFile;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "VIDEO_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File videoFile = File.createTempFile(
                prepend,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );
        mVideoFileName = videoFile.getAbsolutePath();
        SharedPreferences sharedPreference = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString("videofilepath", mVideoFileName);
        editor.apply();
        return videoFile;
    }

    private void checkWriteStoragePermission() {
        mIsRecording = true;
        //  mRecordImageButton.setImageResource(R.drawable.icn_stop);
        try {
            createVideoFileName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startRecord();
        mMediaRecorder.start();
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.setVisibility(View.VISIBLE);
        mChronometer.start();
        Getvkyctext();
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED) {
//            mIsRecording = true;
//            //  mRecordImageButton.setImageResource(R.drawable.icn_stop);
//            try {
//                createVideoFileName();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            startRecord();
//            mMediaRecorder.start();
//            mChronometer.setBase(SystemClock.elapsedRealtime());
//            mChronometer.setVisibility(View.VISIBLE);
//            mChronometer.start();
//            Getvkyctext();
//
//        } else {
//            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                Toast.makeText(this, "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
//            }
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
//        }
    }

    private void setupMediaRecorder() {
        try {
            //mMediaRecorder=new MediaRecorder();
            //  mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            // mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setOutputFile(mVideoFileName);
            mMediaRecorder.setVideoEncodingBitRate(1000000);
            mMediaRecorder.setVideoFrameRate(30);
            //mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
            mMediaRecorder.setVideoSize(320, 240);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setOrientationHint(mTotalRotation);
            mMediaRecorder.prepare();
            //new line code added by me
            //mMediaRecorder.start();


        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "setupMediaRecorder", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    public byte[] convert(String path) throws IOException {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        for (int readNum; (readNum = fis.read(b)) != -1; ) {
            bos.write(b, 0, readNum);
        }

        byte[] bytes = bos.toByteArray();

        return bytes;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private static class CompareSizeByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) (lhs.getWidth() * lhs.getHeight()) -
                    (long) (rhs.getWidth() * rhs.getHeight()));
        }
    }

}