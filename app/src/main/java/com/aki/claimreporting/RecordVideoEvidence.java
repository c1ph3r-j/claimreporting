package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA_AND_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_READ_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_RECORD_AUDIO;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_STORAGE;

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
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

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

public class RecordVideoEvidence extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    public static String vktxtfinaltext = "";
    public static String incident_id;
    public static String mVideoFileNameLocation;
    private static SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    public TextView proceed;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    public String stopval;
    FirebaseCrashlytics mCrashlytics;
    String[] permissions = PERMISSION_CAMERA_AND_STORAGE;
    DatabaseHelper mydb;
    boolean isCameraFacingBack = true;
    TextView switchCamBtn;
    PermissionHandler permissionManager;
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
    private TextView regsuccess;
    private boolean mIsRecording = false;
    private final CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            String methodName = "onOpened";
            try {
                mCameraDevice = camera;
                if (mIsRecording) {
                    try {
                        createVideoFileName();
                    } catch (Exception e) {
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
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
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
            return Collections.min(bigEnough, new RecordVideoEvidence.CompareSizeByArea());
        } else {
            return choices[0];
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video_evidence);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        preventSizeChange(this, getSupportActionBar());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Visual Evidence");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        permissionManager = new PermissionHandler(this);
        permissionManager.setPermissionResultListener(new PermissionHandler.PermissionResultListener() {
            @Override
            public void onPermissionGranted() {
                try {
                    initFunctions();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPermissionDenied() {
                permissionManager.showPermissionExplanationDialog(permissions);
            }
        });

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionManager.handleSettingsActivityResult(permissions, requestCode, resultCode);
    }


    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            createVideoFolder();
            stopval = "0";

            switchCamBtn = findViewById(R.id.switchCamBtn);
            switchCamBtn.setOnClickListener(onClickSwitchCam -> {
//            if (stopval.equals("0")) {
                isCameraFacingBack = !isCameraFacingBack;
                if (isCameraFacingBack) {
                    switchCamBtn.setText("Switch Front Camera");
                } else {
                    switchCamBtn.setText("Switch Back Camera");
                }
                switchCamera();
//            } else {
//                Toast.makeText(getApplicationContext(), "Please stop the video to proceed", Toast.LENGTH_SHORT).show();
//            }
            });
            regsuccess = (TextView) findViewById(R.id.txtregsuccess);
            mChronometer = (Chronometer) findViewById(R.id.chronometer);
            mTextureView = (TextureView) findViewById(R.id.textureView);
            mRecordstop = (ImageButton) findViewById(R.id.recordid);
            proceed = (TextView) findViewById(R.id.visualevidencenext);
            mRecordImageButton = (ImageButton) findViewById(R.id.recordstpid);

            if (permissionManager.hasPermissions(permissions)) {
                try {
                    initFunctions();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                permissionManager.requestPermissions(permissions);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFunctions() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        proceed.setOnClickListener(v -> {
            try {
                if (Objects.equals(stopval, "0")) {
                    Intent intent = new Intent(RecordVideoEvidence.this, AccidentDescription.class);
                    startActivity(intent);
                    finish();
                } else if (Objects.equals(stopval, "1")) {
                    Toast.makeText(getApplicationContext(), "Please stop the video to proceed", Toast.LENGTH_SHORT).show();
                } else {
                    UploadVisualAccident();
                    Intent intent = new Intent(RecordVideoEvidence.this, AccidentDescription.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        });


        mMediaRecorder = new MediaRecorder();
        mRecordstop.setOnClickListener(onClickStopRecord -> {
            try {

                if (!mIsRecording) {
                    stopval = "1";
                    switchCamBtn.setVisibility(View.GONE);
                    checkWriteStoragePermission();
                    proceed.setText("NEXT");
                    regsuccess.setText(vktxtfinaltext);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        mRecordImageButton.setOnClickListener(onClickRecordBtn -> {
            try {
                if (mIsRecording) {
                    mChronometer.stop();
                    stopval = "2";
                    switchCamBtn.setVisibility(View.VISIBLE);
                    mChronometer.setVisibility(View.INVISIBLE);
                    mIsRecording = false;
                    if (null != mMediaRecorder) {
                        try {
                            mMediaRecorder.stop();
                        } catch (RuntimeException ex) {
                            ex.printStackTrace();
                        }
                    }
                    mMediaRecorder.reset();
                    startPreview();

                } else {
                    Toast.makeText(RecordVideoEvidence.this, "You haven't captured the video. Please take a video.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        });
    }

    public byte[] videoconvert(String path) throws IOException {
        FileInputStream fis = null;
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

    public void UploadVisualAccident() {
        try {
            mCrashlytics = FirebaseCrashlytics.getInstance();
            Thread thread = new Thread(() -> AsyncTask.execute(() -> {

                try {
                    if (isNetworkConnected()) {

                        if (checkGPSStatus()) {
                            mydb = new DatabaseHelper(RecordVideoEvidence.this);
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
                                incident_id = incidePref.getString(ClaimType.CraIdval, "");
                                SharedPreferences sharedpreferences = getSharedPreferences("CRAID", Context.MODE_PRIVATE);
                                String craid = sharedpreferences.getString("CraIdval", "0");
                                SharedPreferences videonPref = getSharedPreferences("VideoFileLocation", MODE_PRIVATE);
                                final String filename = videonPref.getString("videofilepathlocation", "");
                                SharedPreferences locationPref = getSharedPreferences("LocationPref", MODE_PRIVATE);
                                MediaType mediaType = MediaType.parse("application/octet-stream");
                                try {
                                    byte[] byteArray = videoconvert(filename);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String uniqueID = UUID.randomUUID().toString();
                                String dateTime = localToGMT();
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
                                        .addFormDataPart("incidentUniqueCode", craid)
                                        .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                        .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                        .addFormDataPart("captureAttachmentID", uniqueID)
                                        .addFormDataPart("captureDateTime", dateTime)
                                        .addFormDataPart("attachmentTypeID", "e1f91e22-4b56-4f35-9ae7-9ab831d8a91f")
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
                                    String staticRes = staticResponse.body().string();
                                    Log.i(null, staticRes);
                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                                    if (staticJsonObj.getInt("rcode") == 1) {
                                        runOnUiThread(() -> {

                                        });
                                    }
                                } catch (final IOException ex) {

                                    mCrashlytics.recordException(ex);
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-pushvideo", ex.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.toString(), "AccidentVideoConfirm-pushvideo", e.toString());
                                }
                            });
                            thread1.start();
                        } else {
                            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(RecordVideoEvidence.this);
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
                } catch (Exception ex) {
                    mCrashlytics.recordException(ex);
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                    MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-pushvideo", ex.toString());
                }

            }));
            thread.start();
        } catch (Exception ex) {
            ex.getStackTrace();
            MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-pushvideo", ex.toString());
            mCrashlytics.recordException(ex);
        }

    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager;
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

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        try {
            if (mTextureView.isAvailable()) {
                setupCamera(320, 240);
                connectCamera();
            } else {
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void switchCamera() {
        closeCamera();
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
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getApplicationContext(),
//                        "Application will not run without camera services", Toast.LENGTH_SHORT).show();
//            }
//        }
//        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mIsRecording = true;
//                // mRecordImageButton.setImageResource(R.drawable.icn_stop);
//                try {
//                    createVideoFileName();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    MainActivity.MobileErrorLog(e.toString(), "AccidentVideoConfirm-onRequestPermissionResult", e.toString());
//                }
//            } else {
//                Toast.makeText(this,
//                        "App needs to save video to run", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                if (isCameraFacingBack) {
                    if (cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.LENS_FACING) ==
                            CameraCharacteristics.LENS_FACING_BACK) {
                        mCameraId = cameraId;
                        break;
                    }
                } else {
                    if (cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.LENS_FACING) ==
                            CameraCharacteristics.LENS_FACING_FRONT) {
                        mCameraId = cameraId;
                        break;
                    }
                }
            }
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(mCameraId);
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
            return;
        } catch (CameraAccessException ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-setupCamera", ex.toString());
        }
    }

    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(this,
                            "Video app required access to camera", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
                }, REQUEST_CAMERA_PERMISSION_RESULT);
            }

        } catch (CameraAccessException ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-connectCamera", ex.toString());
        }
    }

    private void startRecord() {
        try {
            setupMediaRecorder();
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            // surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            surfaceTexture.setDefaultBufferSize(320, 240);
            Surface previewSurface = new Surface(surfaceTexture);
            Surface recordSurface = mMediaRecorder.getSurface();
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            // mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_VIDEO_SNAPSHOT);
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
                            } catch (CameraAccessException ex) {
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-onConfigured", ex.toString());
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                            //Log.d(TAG, "onConfigureFailed: startRecord");
                        }
                    }, null);

        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-startRecord", ex.toString());
        }
    }

    private void startPreview() {
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        // surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
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
                            } catch (CameraAccessException ex) {
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-startPreview", ex.toString());
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {


                        }
                    }, null);
        } catch (CameraAccessException ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-startPreview", ex.toString());
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
        if (mBackgroundHandlerThread.isAlive()) {
            mBackgroundHandlerThread.quitSafely();
        }
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-createVideoFolder", ex.toString());
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
        mVideoFileNameLocation = videoFile.getAbsolutePath();
        SharedPreferences sharedPreference = getSharedPreferences("VideoFileLocation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString("videofilepathlocation", mVideoFileNameLocation);
        editor.commit();
        return videoFile;
    }

    private void checkWriteStoragePermission() {
        mIsRecording = true;
        //  mRecordImageButton.setImageResource(R.drawable.icn_stop);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        } else {
            mIsRecording = true;
            //  mRecordImageButton.setImageResource(R.drawable.icn_stop);
            try {
                createVideoFileName();
            } catch (IOException ex) {
                ex.printStackTrace();
                MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-checkWriteStoragePermission", ex.toString());
            }
            startRecord();
            mMediaRecorder.start();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.setVisibility(View.VISIBLE);
            mChronometer.start();
            //addnewtext?
            // Getvkyctext();
        }
    }

    private void setupMediaRecorder() {

        try {
            //mMediaRecorder=new MediaRecorder();
            //  mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            // mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setOutputFile(mVideoFileNameLocation);
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


        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-setUpMediaRecorder", ex.toString());
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
        try {
            finish();
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.toString(), "AccidentVideoConfirm-onOptionItemSelected", ex.toString());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RecordVideoEvidence.this, AccidentDescription.class));
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