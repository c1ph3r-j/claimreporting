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
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

public class ClaimVisualArtifacts extends AppCompatActivity {

    private static final int IMAGE_TAKE_CODE = 1234;
    public static FirebaseCrashlytics mCrashlytics;
    public static String mVisualImageName;
    public static RequestBody body;
    public static String mAudioFileName;
    public static boolean catpuredimage;
    public static byte[] regByte = null;
    public static String imageURL;
    public ImageView buttonStart, buttonrecording, buttonplay, buttonplaying, audiorecorddone;
    public VideoView videoViewdeclare, videoVieweviden;
    public ImageView imagedeclareimg;
    public ImageView visualimguploadone, videodecluploadone, videoevideuploadone;
    public int optionvisual = 0;
    public Bitmap visualimgrotatedBitmap;
    public String sdocumentType;
    public Bitmap insurer;
    public ProgressDialog progressdialog;
    TextView videodeclare, videoevidence, imagedeclare;
    LinearLayout nextbtn;
    ImageView videoevidenceclose, videodeclareclose, imagedeclareclose;
    String currentPhotoPath;
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    File AudioSavePathInDevice = null;
    DatabaseHelper mydb;
    Activity activity;

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Accident Description");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        setContentView(R.layout.activity_claim_visual_artifacts);
        preventSizeChange(this, getSupportActionBar());
        mCrashlytics = FirebaseCrashlytics.getInstance();
        activity = this;
        init();
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            nextbtn = findViewById(R.id.vechileinfonext);

            buttonStart = (ImageView) findViewById(R.id.Imgrecord);
            buttonrecording = (ImageView) findViewById(R.id.Imgrecording);
            audiorecorddone = (ImageView) findViewById(R.id.audiorecorededdone);
            buttonplay = (ImageView) findViewById(R.id.Imgplay);
            buttonplaying = (ImageView) findViewById(R.id.Imgplaying);

            buttonplay = (ImageView) findViewById(R.id.Imgplay);
            buttonplaying = (ImageView) findViewById(R.id.Imgplaying);
            buttonStart.setVisibility(View.VISIBLE);
            buttonrecording.setVisibility(View.GONE);
            buttonplay.setVisibility(View.GONE);
            audiorecorddone.setVisibility(View.GONE);
            buttonplaying.setVisibility(View.GONE);

            try {
                nextbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pushfinalaudio();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                buttonStart.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View view) {
                        try {
                            AudioSavePathInDevice = createAudioFileName();
                        } catch (IOException e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }

                        MediaRecorderReady();

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }

                        buttonStart.setVisibility(View.GONE);
                        buttonrecording.setVisibility(View.VISIBLE);
//                Toast.makeText(ClaimVisualArtifacts.this, "Recording started",
//                        Toast.LENGTH_LONG).show();

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                buttonrecording.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            mediaRecorder.stop();
                            audioconvert(String.valueOf(AudioSavePathInDevice));
                        } catch (IOException e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        /*pushaudio();*/
                        buttonStart.setVisibility(View.VISIBLE);
                        buttonrecording.setVisibility(View.GONE);
                        audiorecorddone.setVisibility(View.VISIBLE);
                        buttonplay.setVisibility(View.VISIBLE);
//                Toast.makeText(ClaimVisualArtifacts.this, "Recording Completed",
//                        Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                buttonplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) throws IllegalArgumentException,
                            SecurityException, IllegalStateException {

                        buttonplay.setVisibility(View.GONE);
                        audiorecorddone.setVisibility(View.VISIBLE);
                        buttonplaying.setVisibility(View.VISIBLE);

                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(String.valueOf(AudioSavePathInDevice));
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }

                        mediaPlayer.start();
//                Toast.makeText(ClaimVisualArtifacts.this, "Recording Playing",
//                        Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            try {
                buttonplaying.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View view) {
                        buttonplay.setVisibility(View.VISIBLE);
                        audiorecorddone.setVisibility(View.VISIBLE);
                        buttonplaying.setVisibility(View.GONE);
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            MediaRecorderReady();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            videoevidence = (TextView) findViewById(R.id.videoevidencebtntxt);
            videodeclare = (TextView) findViewById(R.id.videodeclarebtntxt);
            imagedeclare = (TextView) findViewById(R.id.uploadimagedelcare);


            videoVieweviden = (VideoView) findViewById(R.id.videoViewevidence);
            videoViewdeclare = (VideoView) findViewById(R.id.videodeclareView);
            imagedeclareimg = (ImageView) findViewById(R.id.imagedelcareimg);

            videoevideuploadone = (ImageView) findViewById(R.id.videoevidenceuploaddone);
            videodecluploadone = (ImageView) findViewById(R.id.videodeclareuploaddone);
            visualimguploadone = (ImageView) findViewById(R.id.visualimageuploaddone);


            videoevidenceclose = (ImageView) findViewById(R.id.videoevidencecloseicon);
            videodeclareclose = (ImageView) findViewById(R.id.videodeclarecloseicon);
            imagedeclareclose = (ImageView) findViewById(R.id.imagedelcarecloseicon);


            try {
                imagedeclare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                        SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                        modeldoceditor.putString(CarView.doctypeid, "b9c6b305-e494-4430-9745-1aafcedb0a92");
                        modeldoceditor.commit();
                        optionvisual = 1;
                        dispatchTakePictureIntent();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                videoevidence.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent login = new Intent(ClaimVisualArtifacts.this, VisualAccidentLocation.class);
                        startActivity(login);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                videodeclare.setOnClickListener(onClickVideoDeclare -> {
                    Intent login = new Intent(ClaimVisualArtifacts.this, Videorecorder.class);
                    startActivity(login);
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            SharedPreferences sharedPreferencenew = getSharedPreferences("VideoFileLocation", Context.MODE_PRIVATE);
            String filepath = sharedPreferencenew.getString("videofilepathlocation", "");
            try {
                if (filepath != "") {
                    videoVieweviden.setVisibility(View.VISIBLE);
                    videoevidence.setVisibility(View.GONE);
                    videoevidenceclose.setVisibility(View.VISIBLE);
                    videoevideuploadone.setVisibility(View.VISIBLE);

                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(videoVieweviden);
                    Uri uri = Uri.parse(filepath);
                    videoVieweviden.setMediaController(mediaController);
                    videoVieweviden.setVideoURI(uri);
                    videoVieweviden.requestFocus();
                    videoVieweviden.start();

//            videoVieweviden.setVideoPath(filepath);
//            videoVieweviden.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            try {
                videoevidenceclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        videoVieweviden.setVisibility(View.GONE);
                        videoevidenceclose.setVisibility(View.GONE);
                        videoevidence.setVisibility(View.VISIBLE);
                        videoevideuploadone.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            SharedPreferences sharedPreference = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
            String filepathvideodecl = sharedPreference.getString("videofilepath", "");
            try {
                if (filepathvideodecl != "") {
                    videoViewdeclare.setVisibility(View.VISIBLE);
                    videodeclare.setVisibility(View.GONE);
                    videodeclareclose.setVisibility(View.VISIBLE);
                    videodecluploadone.setVisibility(View.VISIBLE);

                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(videoViewdeclare);
                    Uri uri = Uri.parse(filepathvideodecl);
                    videoViewdeclare.setMediaController(mediaController);
                    videoViewdeclare.setVideoURI(uri);
                    videoViewdeclare.requestFocus();
                    videoViewdeclare.start();

//            videoViewdeclare.setVideoPath(filepathvideodecl);
//            videoViewdeclare.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            try {
                videodeclareclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        videoViewdeclare.setVisibility(View.GONE);
                        videodeclareclose.setVisibility(View.GONE);
                        videodeclare.setVisibility(View.VISIBLE);
                        videodecluploadone.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                imagedeclareclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imagedeclareimg.setVisibility(View.GONE);
                        imagedeclareclose.setVisibility(View.GONE);
                        imagedeclare.setVisibility(View.VISIBLE);
                        visualimguploadone.setVisibility(View.GONE);

                    }
                });
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

    public byte[] audioconvert(String path) throws IOException {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        FileInputStream fis = null;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    private File createAudioFileName() throws IOException {

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "Audio_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File audioFile = File.createTempFile(
                prepend,  /* prefix */
                ".3gp",         /* suffix */
                storageDir      /* directory */
        );
        mAudioFileName = audioFile.getAbsolutePath();
        return audioFile;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void declaredImages() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            mCrashlytics = FirebaseCrashlytics.getInstance();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    AsyncTask.execute(new Runnable() {

                        @Override
                        public void run() {

                            try {

                                if (isNetworkConnected() == true) {

                                    if (checkGPSStatus() == true) {
                                        mydb = new DatabaseHelper(ClaimVisualArtifacts.this);
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

                                                SharedPreferences incidePref = activity.getSharedPreferences("IncidentUniqueID", MODE_PRIVATE);
                                                String incident_id = incidePref.getString(ClaimType.CraIdval, ""); //IncidentTypeClass and shared pref val error

//                                                SharedPreferences driverPref = activity.getSharedPreferences("DriverID",MODE_PRIVATE);
//                                                String driver_id = driverPref.getString(PolicyInformation.DriverUniqueID,"");
//                                                SharedPreferences certifPref = activity.getSharedPreferences("ClaimInsert",MODE_PRIVATE);
//                                                String certnum = certifPref.getString(ClaimType.CertificateID,"");
//                                                String vehicrefid = certifPref.getString(ClaimType.Vechilerefid,"");
                                                //MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/AddClaim";
//                                                SharedPreferences incitype = getSharedPreferences("IncidentType",MODE_PRIVATE);
//                                                String incidenttypeval = incitype.getString(ClaimType.typeidincident,"");

                                                Bitmap bitmapnew = insurer;
                                                ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
                                                bitmapnew.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
                                                //                                               String outStr = MainActivity.Global.outputStr;
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
                                                //   MainActivity.postURL =  activity.getString(R.string.uaturl) + "/app/Claim/UploadClaimFiles";
                                                MainActivity.postURL = getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                                                OkHttpClient client = new OkHttpClient.Builder()
                                                        .connectTimeout(120, TimeUnit.SECONDS)
                                                        .writeTimeout(120, TimeUnit.SECONDS)
                                                        .readTimeout(120, TimeUnit.SECONDS)
                                                        .build();


                                                RequestBody body = new MultipartBody.Builder()
                                                        .setType(MultipartBody.FORM)
                                                        .addFormDataPart("fileName", "AddDoc.jpg")
                                                        .addFormDataPart(
                                                                "image", "AddDoc.jpg",
                                                                RequestBody.create(mediaType, byteArraynew))
//                                                        .addFormDataPart("certificateNo",certnum)
                                                        .addFormDataPart("transactionID", incident_id)
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
                                                            progressdialog = ProgressDialog.show(ClaimVisualArtifacts.this, "Loading", "Please wait...", true);
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
                                                                progressdialog.dismiss();
                                                                MainActivity.VideoEvidence = false;
                                                                MainActivity.VideoDeclaration = false;
                                                            }
                                                        });

                                                    } else {
                                                        runOnUiThread(() -> progressdialog.dismiss());
                                                        runOnUiThread(() -> {
                                                            try {
                                                                JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                                JSONObject index = rmsg.getJSONObject(0);
                                                                runOnUiThread(() -> {
                                                                    String errorText;
                                                                    try {
                                                                        errorText = index.getString("errorText");
                                                                        AlertDialog.Builder alert = new AlertDialog.Builder(ClaimVisualArtifacts.this);
                                                                        alert.setCancelable(false);
                                                                        alert.setMessage(errorText);
                                                                        alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                                                        alert.show();
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                });
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        });

                                                    }
                                                } catch (final IOException | JSONException e) {
                                                    runOnUiThread(() -> progressdialog.dismiss());
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }

                                            }
                                        });
                                        thread.start();


                                    } else {
                                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ClaimVisualArtifacts.this);
                                        dialog.setMessage("GPS locations is not enabled.Please enable it");
                                        dialog.setPositiveButton("Ok", (dialog1, which) -> {
                                            //this will navigate user to the device location settings screen
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivity(intent);
                                        });
                                        android.app.AlertDialog alert = dialog.create();
                                        alert.show();
                                    }


                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            Toast.makeText(ClaimVisualArtifacts.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }


                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        progressdialog.dismiss();
                                    }
                                });
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }

                        }
                    });
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    private void dispatchTakePictureIntent() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.aki.claimreporting.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, IMAGE_TAKE_CODE);


            }
        }else {
            //for pixel mobile phones
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.aki.claimreporting.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, IMAGE_TAKE_CODE);

            }
        }
    }

    public Bitmap getBitmap(String path) {
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (requestCode == IMAGE_TAKE_CODE) {
                //TODO: action

                if (getValidBitmap(currentPhotoPath) == null) {

                    //finish();
                } else {
                    insurer = getBitmap(currentPhotoPath);
                    MainActivity.Global.img = insurer;
                    catpuredimage = true;
                    mVisualImageName = currentPhotoPath;
                    if (isNetworkConnected() == true) {
                        //pushimageclaim();
                    } else {
                        SharedPreferences sharedPreference = getSharedPreferences("VisualImageFile", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreference.edit();
                        editor.putString("visimagefilepath", mVisualImageName);
                        editor.commit();
                    }
                    ExifInterface ei = null;
                    try {
                        ei = new ExifInterface(currentPhotoPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);


                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            visualimgrotatedBitmap = rotateImage(insurer, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            visualimgrotatedBitmap = rotateImage(insurer, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            visualimgrotatedBitmap = rotateImage(insurer, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            visualimgrotatedBitmap = insurer;
                    }

                    imagedeclareimg.setImageBitmap(visualimgrotatedBitmap);
                    imagedeclareimg.setVisibility(View.VISIBLE);
                    imagedeclare.setVisibility(View.GONE);
                    imagedeclareclose.setVisibility(View.VISIBLE);
                    visualimguploadone.setVisibility(View.VISIBLE);
                    //  MainActivity.uploadimages(RegistrationStep4.this);
                    declaredImages();
                }

                SharedPreferences sharedPreferencenew = getSharedPreferences("VideoFileLocation", Context.MODE_PRIVATE);
                String filepath = sharedPreferencenew.getString("videofilepathlocation", "");
                if (filepath != "") {
                    videoVieweviden.setVisibility(View.VISIBLE);
                    videoevidence.setVisibility(View.GONE);
                    videoevidenceclose.setVisibility(View.VISIBLE);
                    videoevideuploadone.setVisibility(View.VISIBLE);


                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(videoVieweviden);
                    Uri uri = Uri.parse(filepath);
                    videoVieweviden.setMediaController(mediaController);
                    videoVieweviden.setVideoURI(uri);
                    videoVieweviden.requestFocus();
                    videoVieweviden.start();

//                videoVieweviden.setVideoPath(filepath);
//                videoVieweviden.start();

                }

                SharedPreferences sharedPreference = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
                String filepathvideodecl = sharedPreference.getString("videofilepath", "");
                if (filepathvideodecl != "") {
                    videoViewdeclare.setVisibility(View.VISIBLE);
                    videodeclare.setVisibility(View.GONE);
                    videodeclareclose.setVisibility(View.VISIBLE);
                    videodecluploadone.setVisibility(View.VISIBLE);

                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(videoViewdeclare);
                    Uri uri = Uri.parse(filepathvideodecl);
                    videoViewdeclare.setMediaController(mediaController);
                    videoViewdeclare.setVideoURI(uri);
                    videoViewdeclare.requestFocus();
                    videoViewdeclare.start();

//                videoViewdeclare.setVideoPath(filepathvideodecl);
//                videoViewdeclare.start();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    public Bitmap getValidBitmap(String path) {
        Bitmap bitmap = null;
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);

            //image.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        return bitmap;
    }

    public void pushfinalaudio() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                try {

                    final String filenamecheck = String.valueOf(AudioSavePathInDevice);
                    if (filenamecheck == null || filenamecheck == "null" | filenamecheck == "") {

                        SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                        String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
                        if (incidenttypeval == "B2EC755A-88EF-4F53-8911-C13688D226D3") {
//                                    SharedPreferences thirdisPref = getSharedPreferences("IsThirdParty",MODE_PRIVATE);
//                                    String isthird = thirdisPref.getString(ClaimType.Thirdpartyavailable,"");
//                                    if(isthird.equals("1"))
//                                    {
//                                        insertclaimfinal();
//
//                                    }
//                                    else
//                                    {
                            Intent signUpIntent = new Intent(ClaimVisualArtifacts.this, StolenImageDeclaration.class);
                            startActivity(signUpIntent);
//                                    }

                        } else {
                            Intent signUpIntent = new Intent(ClaimVisualArtifacts.this, ImageDeclaration.class);
                            startActivity(signUpIntent);
                        }

                    } else {

                        mydb = new DatabaseHelper(ClaimVisualArtifacts.this);
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
                        final String filename = String.valueOf(AudioSavePathInDevice);
                        SharedPreferences locationPref = getSharedPreferences("LocationPref", MODE_PRIVATE);
                        // SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                        String dateTime = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            dateTime = localToGMT();
                        }

                        MediaType mediaType = MediaType.parse("application/octet-stream");
                        //MainActivity.postURL = "https://uat-aki.claims.digital/api/app/Claim/UploadClaimFiles";
                        //MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/UploadClaimFiles";
                        MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                        //MainActivity.postURL = getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();

//                body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                        .addFormDataPart("Images", filename,
//                                RequestBody.create(MediaType.parse("application/octet-stream"),
//                                        new File(filename)))
//                        .addFormDataPart("certificateNo", certnum)
//                        .addFormDataPart("incidentUniqueCode", incident_id)
//                        .addFormDataPart("documentType", sdocumentType)
//                        .addFormDataPart("geoTagLat",locationPref.getString(MainActivity.Latitude, null))
//                        .addFormDataPart("geoTagLon",locationPref.getString(MainActivity.Longitude, null))
//                        .addFormDataPart("captureAttachmentID", uniqueID)
//                        .addFormDataPart("captureDateTime",dateTime)
//                        .build();

                        body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("fileName", filename)
                                .addFormDataPart(
                                        "image", filename,
                                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                                new File(filename)))
                                .addFormDataPart("certificateNo", certnum)
                                .addFormDataPart("incidentUniqueCode", incident_id)
                                .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                .addFormDataPart("captureAttachmentID", uniqueID)
                                .addFormDataPart("captureDateTime", dateTime)
                                //.addFormDataPart("attachmentTypeID",MainActivity.cardocidval)
                                //.addFormDataPart("attachmentTypeID","20b8857d-4884-4cc8-96a9-8c04a161c617")
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
                                    progressdialog = ProgressDialog.show(ClaimVisualArtifacts.this, "Loading", "Please wait...", true);
                                    // progressdialog.show(activity, "Loading", "Please wait...", true);
                                }
                            });
                            staticResponse = client.newCall(request).execute();
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);
                            try {
                                String reqidval = staticJsonObj.getString("reqID");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                            if (staticJsonObj.getInt("rcode") == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                        SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                                        String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
                                        if (incidenttypeval == "B2EC755A-88EF-4F53-8911-C13688D226D3") {
//                                    SharedPreferences thirdisPref = getSharedPreferences("IsThirdParty",MODE_PRIVATE);
//                                    String isthird = thirdisPref.getString(ClaimType.Thirdpartyavailable,"");
//                                    if(isthird.equals("1"))
//                                    {
//                                        insertclaimfinal();
//
//                                    }
//                                    else
//                                    {
                                            Intent signUpIntent = new Intent(ClaimVisualArtifacts.this, StolenImageDeclaration.class);
                                            startActivity(signUpIntent);
//                                    }

                                        } else {
                                            Intent signUpIntent = new Intent(ClaimVisualArtifacts.this, ImageDeclaration.class);
                                            startActivity(signUpIntent);
                                        }
                                    }
                                });
                                // pushimageclaim();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                        SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                                        String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
                                        if (incidenttypeval == "B2EC755A-88EF-4F53-8911-C13688D226D3") {
//                                SharedPreferences thirdisPref = getSharedPreferences("IsThirdParty",MODE_PRIVATE);
//                                String isthird = thirdisPref.getString(ClaimType.Thirdpartyavailable,"");
//                                if(isthird.equals("1"))
//                                {
//                                    insertclaimfinal();
//
//                                }
//                                else
//                                {
                                            Intent signUpIntent = new Intent(ClaimVisualArtifacts.this, StolenImageDeclaration.class);
                                            startActivity(signUpIntent);
//                                }

                                        } else {
                                            Intent signUpIntent = new Intent(ClaimVisualArtifacts.this, ImageDeclaration.class);
                                            startActivity(signUpIntent);
                                        }
                                    }
                                });
                                //pushimageclaim();
                            }
                        } catch (final IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog.dismiss();
                                }
                            });
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                            //MainActivity.MobileErrorLog( reqidval, "ImageDeclaration-pushaudio", ex.toString(), ex.toString());
                        }
                    }


                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressdialog.dismiss();
                        }
                    });
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                    //MainActivity.MobileErrorLog(reqidval,"ImageDeclaration-pushaudio",ex.toString(),ex.toString());
                }
            } else {

                SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
                if (incidenttypeval.equals("B2EC755A-88EF-4F53-8911-C13688D226D3")) {
//                    SharedPreferences thirdisPref = getSharedPreferences("IsThirdParty",MODE_PRIVATE);
//                    String isthird = thirdisPref.getString(ClaimType.Thirdpartyavailable,"");
//                    if(isthird.equals("1"))
//                    {
//                        insertclaimfinal();
//
//                    }
//                    else
//                    {
                    Intent signUpIntent = new Intent(ClaimVisualArtifacts.this, StolenImageDeclaration.class);
                    startActivity(signUpIntent);
//                    }

                } else {
                    Intent signUpIntent = new Intent(ClaimVisualArtifacts.this, ImageDeclaration.class);
                    startActivity(signUpIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }


    public void insertclaimfinal() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

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
                    Thread thread = new Thread(new Runnable() {

                        public void run() {
                            SharedPreferences locPref = activity.getSharedPreferences("LocationCurrent", MODE_PRIVATE);
                            SharedPreferences incidePref = activity.getSharedPreferences("IncidentUniqueID", MODE_PRIVATE);
                            String incident_id = incidePref.getString(ClaimType.CraIdval, ""); //error Incident1type class and shared pref


                            MainActivity.postURL = getString(R.string.uaturl) + "/claim/Incident/IncidentSubmit";
                            SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                            String incidenttypeval = incitype.getString(ClaimType.typeidincident, ""); //error Incident1type class and shared pref

                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");

                            OkHttpClient client = new OkHttpClient();
                            JsonObject Details = new JsonObject();
                            Details.addProperty("incidentUniqueID", incident_id);
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
                                        progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);

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
                                            Toast.makeText(ClaimVisualArtifacts.this, "Please contact administrator to proceed and try again", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                } else {
                                    Log.i(null, staticRes);
                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                                    if (staticJsonObj.getInt("rcode") == 200) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                //insertclaimpdf();
                                                mydb = new DatabaseHelper(ClaimVisualArtifacts.this);
                                                MainActivity.VideoEvidence = false;
                                                MainActivity.VideoDeclaration = false;
                                                SharedPreferences sharedPreference = getSharedPreferences("VisualImageFile", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreference.edit();
                                                editor.putString("visimagefilepath", "");
                                                editor.commit();

                                                SharedPreferences sharedPreference1 = getSharedPreferences("VisualAudioFile", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor1 = sharedPreference1.edit();
                                                editor1.putString("visaudiofilepath", "");
                                                editor1.commit();

                                                SharedPreferences sharedPreference2 = getSharedPreferences("VideoFile", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor2 = sharedPreference2.edit();
                                                editor2.putString("videofilepath", "");
                                                editor2.commit();


                                                SharedPreferences sharedPreference3 = getSharedPreferences("VideoFileLocation", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor3 = sharedPreference3.edit();
                                                editor3.putString("videofilepathlocation", "");
                                                editor3.commit();
//                                                Intent car = new Intent(ClaimVisualArtifacts.this, ClaimSuccess.class);
//                                                startActivity(car); //error intent

                                            }
                                        });
                                    } else {
                                        try {
                                            progressdialog.dismiss();
                                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                            JSONObject index = rmsg.getJSONObject(0);
                                            runOnUiThread(() -> {
                                                String errorText = null;
                                                String trnId = null;
                                                try {
                                                    errorText = index.getString("errorText");
                                                    trnId = staticJsonObj.getString("trnID");
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(ClaimVisualArtifacts.this);
                                                    alert.setCancelable(false);
                                                    alert.setMessage(errorText);
                                                    alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                                    alert.show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                progressdialog.dismiss();
                                                try {
                                                    Toast.makeText(ClaimVisualArtifacts.this, staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText"), Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }
                                                return;
                                            }
                                        });
                                    }
                                }

                            } catch (final IOException e) {
                                runOnUiThread(progressdialog::dismiss);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        Toast.makeText(ClaimVisualArtifacts.this,
                                                e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (JSONException e) {
                                runOnUiThread(progressdialog::dismiss);
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                runOnUiThread(() -> {
                                    Toast.makeText(ClaimVisualArtifacts.this,
                                            e.toString(), Toast.LENGTH_LONG).show();
                                });
                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ClaimVisualArtifacts.this);
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
        } catch (Exception e) {
            //progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

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
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
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
                SharedPreferences sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
                SharedPreferences.Editor supporteditor = sharedpreferences.edit();
                supporteditor.putString(MainActivity.ReferrenceURL, "Service Provider");
                supporteditor.apply();
                SharedPreferences sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
                SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
                supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
                supporteditorimg.apply();
                Intent login = new Intent(ClaimVisualArtifacts.this, SupportTicket.class);
                startActivity(login);
                return true;

                // Do something

            } else {
                onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return super.onOptionsItemSelected(item);
    }
}