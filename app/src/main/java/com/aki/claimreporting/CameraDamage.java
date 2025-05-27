package com.aki.claimreporting;


import static com.aki.claimreporting.CaptureDamagedParts.activityCaptureDamagedParts;
import static com.aki.claimreporting.CaptureDamagedParts.isActivityPaused;
import static com.aki.claimreporting.CertificateActivation.postURL;
import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.aki.claimreporting.stereo_vision.CameraPreview;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
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

public class CameraDamage extends AppCompatActivity {

    private static final int IMAGE_TAKE_CODE = 1876;
    public static String destfilepath;
    public static Bitmap insurer;
    public static Bitmap bmpimg;
    public static FirebaseCrashlytics mCrashlytics;
    public static byte[] regByte = null;
    public static String reqidval;
    public static DatabaseHelper mydb;
    public static Bitmap destnew;
    public static RequestBody body;
    public static String imageURL, imagename, incident_id;
    public static String regnum, membercraid, certnumval;
    public static String carnamevalvalid;
    public static SharedPreferences sharedPreferencenew;
    public static int caridvalvalid;
    public static int caridvalnewvalid;
    public static int firstimeornot;
    public String currentPhotoPath;
    public int optionvisual = 0;
    InputStream in = null;
    Context context;

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static void damageofflineimagesget(Activity activity) {

        sharedPreferencenew = activity.getSharedPreferences("MyCarview", Context.MODE_PRIVATE);
        int caridval = sharedPreferencenew.getInt("selectcarid", 0);
        String carnameval = sharedPreferencenew.getString("selectcartitle", "");
        caridvalvalid = caridval;
        carnamevalvalid = carnameval;
        SharedPreferences modeldoctypeval = activity.getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
        String cardocuniq_doc = modeldoctypeval.getString(CarView.doctypeid, "");
        MainActivity.cardocidval = cardocuniq_doc;
        String uniqueID = UUID.randomUUID().toString();
        String filename = UUID.randomUUID().toString() + ".JPEG";
        String imgData = destfilepath;
        String imagename = UUID.randomUUID().toString() + ".JPEG";
        sharedPreferencenew = activity.getSharedPreferences("MyCarview", Context.MODE_PRIVATE);
        String cardescription = sharedPreferencenew.getString("selectcardescription", "");
        String mergenamedescr = carnamevalvalid + " @ " + cardescription + " @ " + caridvalvalid;
        int attachmentid = 100;
        mydb = new DatabaseHelper(activity);
        Cursor curseattachtoken = mydb.getthirdpartydetails();
        int counttoken = curseattachtoken.getCount();
        if (counttoken >= 1) {
            while (curseattachtoken.moveToNext()) {
                regnum = curseattachtoken.getString(1);
            }
        }

        String imagenew = imgData;
        int attachid = 1;
        SharedPreferences thirdmodelPref = activity.getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
        final String thirdpartyunique = thirdmodelPref.getString(CarView.ThirdPartyID, "");
        if (thirdpartyunique.equals("1")) {
            mydb = new DatabaseHelper(activity);
            int countsamplevalue = mydb.getdeletelocalalreadyid(String.valueOf(caridvalvalid)).getCount();
            if (mydb.getdeletelocalalreadyid(String.valueOf(caridvalvalid)).getCount() >= 1) {
                mydb.deletelocalalreadyid(String.valueOf(caridvalvalid));
            }

            int countnew = mydb.getinsertloceattachment().getCount();
            boolean Isinserted = mydb.insertlocalimageattachment(imagename, attachid, imgData, caridvalvalid, carnamevalvalid, cardescription, mergenamedescr, uniqueID, attachmentid, regnum, imagenew);
            if (Isinserted == true) {
                boolean test = Isinserted;
                Log.i(null, "Insertion Done");
            } else {
                boolean test = Isinserted;
                Log.i(null, "Not Insertion Done");
            }
        } else {
            mydb = new DatabaseHelper(activity);
            int countsamplevalue = mydb.getdeletethirdlocalalreadyid(String.valueOf(caridvalvalid), regnum).getCount();
            if (mydb.getdeletethirdlocalalreadyid(String.valueOf(caridvalvalid), regnum).getCount() >= 1) {
                mydb.deletethirdlocalalreadyid(String.valueOf(caridvalvalid));
            }

            int countnew = mydb.getinsertloceattachment().getCount();
            boolean Isinserted = mydb.insertthirdimages(imagename, attachid, imgData, caridvalvalid, carnamevalvalid, cardescription, mergenamedescr, uniqueID, attachmentid, regnum, imagenew);
            if (Isinserted == true) {
                boolean test = Isinserted;
                Log.i(null, "Insertion Done");
            } else {
                boolean test = Isinserted;
                Log.i(null, "Not Insertion Done");
            }
        }
    }

    public static String localToGMT() {

        String finalDateString = "";
        String validfromdate = "";
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
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

    public static Float convertToFloat(Double doubleValue) {
        return doubleValue == null ? null : doubleValue.floatValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        setContentView(R.layout.activity_camera_damage);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);

        }
        preventSizeChange(this, getSupportActionBar());
        try {
            mydb = new DatabaseHelper(CameraDamage.this);
            mCrashlytics = FirebaseCrashlytics.getInstance();
            optionvisual = 1;


            if(hasTelephotoLens()) {
                startActivity(new Intent(this, CameraPreview.class));
                finish();
            } else {
                dispatchTakePictureIntent();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private boolean hasTelephotoLens() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIds = manager.getCameraIdList();
            if (cameraIds.length < 3) {
                return false;
            }
            for (String cameraId : cameraIds) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                float[] focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK && focalLengths != null) {
                    for (float focalLength : focalLengths) {
                        if (focalLength > 5.0f) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (CameraAccessException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void dispatchTakePictureIntent() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(CameraDamage.this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(CameraDamage.this,
                        "com.aki.claimreporting.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (optionvisual == 1) {
                    startActivityForResult(takePictureIntent, IMAGE_TAKE_CODE);
                }

            }
        }
        else {
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

//    @SuppressLint("QueryPermissionsNeeded")
//    public void dispatchTakePictureIntent() {
//        String methodName = Objects.requireNonNull(new Object() {
//        }.getClass().getEnclosingMethod()).getName();
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(CameraDamage.this.getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                mCrashlytics.recordException(e);
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(CameraDamage.this,
//                        "com.aki.claimreporting.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                if (optionvisual == 1) {
//                    startActivityForResult(takePictureIntent, IMAGE_TAKE_CODE);
//                }
//
//            }
//        }
//        else {
//            //for pixel mobile phones
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.aki.claimreporting.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//
//                startActivityForResult(takePictureIntent, IMAGE_TAKE_CODE);
//
//            }
//        }
//    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        if (requestCode == IMAGE_TAKE_CODE) {
            try {
                //insurer = null;
                if (getValidBitmap(currentPhotoPath) == null) {
                    try {


                        Intent car = new Intent(CameraDamage.this, CaptureDamagedParts.class);
                        startActivity(car);
//                        DisplayMetrics dm = getResources().getDisplayMetrics();
//                        int densityDpi = dm.densityDpi;
//                        if (densityDpi >= 320 && densityDpi <= 390) {
//                            Intent car = new Intent(CameraDamage.this, CarView.class);
//                            startActivity(car);
//                        }
//                        if (densityDpi >= 280 && densityDpi <= 300) {
//                            Intent car = new Intent(CameraDamage.this, CarView.class);
//                            startActivity(car);
//                        }
//                        if (densityDpi >= 310 && densityDpi <= 395) {
//                            Intent car = new Intent(CameraDamage.this, CarView280.class);
//                            startActivity(car);
//                        }
//                        if (densityDpi >= 400 && densityDpi <= 520) {
//                            Intent car = new Intent(CameraDamage.this, CarView400.class);
//                            startActivity(car);
//                        }
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    insurer = getBitmap(currentPhotoPath);
                    destfilepath = currentPhotoPath;

                    if (CaptureDamagedParts.carviewselectionid == "1") {
                        MainActivity.frontViewcount = MainActivity.frontViewcount + 1;
                        // frontViewCount.setVisibility(View.VISIBLE);
                    }
                    if (CaptureDamagedParts.carviewselectionid == "4") {
                        MainActivity.roofViewcount = MainActivity.roofViewcount + 1;
                        // cirlcestate2 = R.drawable.carnoselected_2;
                    }
                    if (CaptureDamagedParts.carviewselectionid == "7") {
                        MainActivity.backViewcount = MainActivity.backViewcount + 1;
                        // cirlcestate3 = R.drawable.carnoselected_3;
                    }
                    if (CaptureDamagedParts.carviewselectionid == "8") {
                        MainActivity.passengerSideViewcount = MainActivity.passengerSideViewcount + 1;
                    }
                    if (CaptureDamagedParts.carviewselectionid == "11") {
                        MainActivity.driverSideViewcount = MainActivity.driverSideViewcount + 1;
                        // cirlcestate5 = R.drawable.carnoselected_5;
                    }

                    if (isNetworkConnected() == true) {
                        MainActivity.damagecount = 1 + MainActivity.damagecountnew;
                        MainActivity.damagecountnew = MainActivity.damagecount;
                        MainActivity.Global.imgocr = insurer;
                        damageimagesget(CameraDamage.this);

                    } else {
                        MainActivity.damagecount = 1 + MainActivity.damagecountnew;
                        MainActivity.damagecountnew = MainActivity.damagecount;
                        MainActivity.Global.imgocr = insurer;

                        try {
                            Intent car = new Intent(CameraDamage.this, CaptureDamagedParts.class);
                            startActivity(car);
//                            DisplayMetrics dm = getResources().getDisplayMetrics();
//                            int densityDpi = dm.densityDpi;
//                            if (densityDpi >= 320 && densityDpi <= 390) {
//                                Intent car = new Intent(CameraDamage.this, CarView.class);
//                                startActivity(car);
//                            }
//                            if (densityDpi >= 280 && densityDpi <= 300) {
//                                Intent car = new Intent(CameraDamage.this, CarView.class);
//                                startActivity(car);
//                            }
//                            if (densityDpi >= 310 && densityDpi <= 395) {
//                                Intent car = new Intent(CameraDamage.this, CarView280.class);
//                                startActivity(car);
//                            }
//                            if (densityDpi >= 400 && densityDpi <= 520) {
//                                Intent car = new Intent(CameraDamage.this, CarView400.class);
//                                startActivity(car);
//                            }
                            finish();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        try {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {

                                    damageofflineimagesget(CameraDamage.this);
                                }
                            }, 1000);   //5 seconds

                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    }


//                    if(isNetworkConnected() == true) {
//                        MainActivity.damagecount = 1 + MainActivity.damagecountnew;
//                        MainActivity.damagecountnew = MainActivity.damagecount;
////                            ObjectDetectorActivity.Global.imgocr = insurer;
//                        damageimagesget(CameraDamage.this);
////                            SharedPreferences thirdmodelPref = getSharedPreferences("ThirdParty",Context.MODE_PRIVATE);
////                            final String thirdpartyunique = thirdmodelPref.getString(CarView.ThirdPartyID, "");
////                            if(thirdpartyunique.equals("1")) {
////                                DisplayMetrics dm = getResources().getDisplayMetrics();
////                                int densityDpi = dm.densityDpi;
////                                if(densityDpi >= 320 && densityDpi <= 390)
////                                {
////
////                                    //finish();
////                                    Intent intent = new Intent(CameraDamage.this, CarView.class);
////                                    startActivity(inten
// finish();t);
////                                }
////                                if(densityDpi >= 280 && densityDpi <= 310)
////                                {
////
////                                    //finish();
////                                    //CarView.carviewcircle1 = "1";
////                                    Intent intent = new Intent(CameraDamage.this, CarView280.class);
////                                    startActivity(inten
// finish();t);
////
////                                }
////                                if (densityDpi >= 400 && densityDpi <= 520)
////                                {
////
////                                    //finish();
////                                    Intent intent = new Intent(CameraDamage.this, CarView400.class);
////                                    startActivity(inten
// finish();t);
////                                }
////                            }
////                            else
////                            {
////                                DisplayMetrics dm = getResources().getDisplayMetrics();
////                                int densityDpi = dm.densityDpi;
////                                if(densityDpi >= 320 && densityDpi <= 390)
////                                {
////
////
////                                    Intent intent = new Intent(CameraDamage.this, ThirdPartyCarView.class);
////                                    startActivity(inten
// finish();t);
////                                }
////                                if(densityDpi >= 280 && densityDpi <= 310)
////                                {
////
////
////                                    Intent intent = new Intent(CameraDamage.this, ThirdPartyCarView280.class);
////                                    startActivity(inten
// finish();t);
////                                }
////                                if (densityDpi >= 400 && densityDpi <= 520)
////                                {
////
////
////                                    Intent intent = new Intent(CameraDamage.this, ThirdPartyCarView400.class);
////                                    startActivity(inten
// finish();t);
////                                }
////                            }
////                            try {
////                                new Handler().postDelayed(new Runnable() {
////                                    public void run() {
////
////                                        damageimagesget(CameraDamage.this);
////                                    }
////                                }, 1000);   //5 seconds
////
////                            } catch (Exception ex) {
////                                ex.getStackTrace();
////                                mCrashlytics.recordException(ex);
////                            }
//                    }
                }


                //  getdamagedetection();
                // insertdamageimage();
                // getdamagedetection();

            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        } else {
            MainActivity.MobileErrorLog(reqidval, "ClaimImage-Damage", "ClaimImage-Damage");
        }
    }

    public void damageimagesget(Activity activity) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        AsyncTask.execute(

                () -> {

                    try {
//                            mydb = new DatabaseHelper(activity);
//                            Cursor curseattachuser = mydb.getUserdetails();
//                            int countuser = curseattachuser.getCount();
//                            if (countuser >= 1) {
//                                while (curseattachuser.moveToNext()) {
//                                    membercraid = curseattachuser.getString(3);
//                                }
//                            }
                        //  SharedPreferences locationPref = activity.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
                        //  final String latitude = locationPref.getString(MainActivity.Latitude, null);
                        // final String longitude = locationPref.getString(MainActivity.Longitude, null);
//                            Bitmap workingBitmap = Bitmap.createBitmap(insurer);
//                            Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
//                            Matrix matrix = new Matrix();
//                            matrix.postRotate(90);
//                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(mutableBitmap, mutableBitmap.getWidth(), mutableBitmap.getHeight(), true);
//                            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
//                            Bitmap dest = rotatedBitmap;
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
//                            String imagevaluebind = "CRA Android";
//                            String imagevaluebind1 = dateTime;
//                            String imagevaluebind2 = UUID.randomUUID().toString();
//                            String imagevaluebind3 = latitude + " / " + longitude;
//                            String imagevaluebind4 = membercraid;
//                            Canvas csnew = new Canvas(dest);
//                            Paint myPaint = new Paint();
//                            myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//                            myPaint.setColor(Color.WHITE);
//                            myPaint.setStrokeWidth(10);
//                            csnew.drawRect(0, 0, 1400, 420, myPaint);
//
//                            Bitmap destnewlatest = dest;
//                            Canvas cs = new Canvas(destnewlatest);
//                            Paint tPaint = new Paint();
//                            tPaint.setColor(Color.RED);
//                            int fontSize = 70;
//                            tPaint.setTextSize(fontSize);
//                            tPaint.setTextAlign(Paint.Align.LEFT);
//                            cs.drawText(imagevaluebind, 0, (0 + tPaint.getTextSize()), tPaint);
//                            cs.drawText(imagevaluebind1, 0, (100 + tPaint.getTextSize()), tPaint);
//                            cs.drawText(imagevaluebind2, 0, (200 + tPaint.getTextSize()), tPaint);
//                            cs.drawText(imagevaluebind3 + " , " + imagevaluebind4, 0, (300 + tPaint.getTextSize()), tPaint);
//                            Bitmap bitmap = destnewlatest;
//                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
//                            Log.i(null, String.valueOf(bitmap.getByteCount()));

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
                        final MediaType mediaType = MediaType.parse("image/jpeg");

                        //New Bitmap code compression
//                            Bitmap newBitmapcomp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
//                            Bitmap bitmapnew = newBitmapcomp;
//                            ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
//                            bitmapnew.compress(Bitmap.CompressFormat.JPEG, 50, streamnew);
//                            String outStr = ObjectDetectorActivity.Global.outputStr;
//                            Log.i(null, String.valueOf(bitmap.getByteCount()));
//                            byte[] byteArraynew = streamnew.toByteArray();

                        //Old Comppression Method
                        Bitmap bitmapnew = insurer;
                        ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
                        bitmapnew.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
                        byte[] byteArraynew = streamnew.toByteArray();
                        regByte = byteArraynew;
                        String imgDatatest = Base64.encodeToString(byteArraynew, Base64.DEFAULT);
                        String imgData1 = imgDatatest;
                        //  regByte = byteArraynew;
                        //   String imgData = Base64.encodeToString(byteArraynew, Base64.DEFAULT);
                        // String imgDatanew =imgData;
                        SharedPreferences modelPref = activity.getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                        final String modelunique = modelPref.getString(AddVehicle.ModelID, null);

                        SharedPreferences incidePref = activity.getSharedPreferences("CRAID", MODE_PRIVATE);
                        incident_id = incidePref.getString("CraIdval", "");
                        certnumval = "";


                        SharedPreferences modeldoctypePref = activity.getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                        String cardocuniq_id = modeldoctypePref.getString(CarView.doctypeid, "");

                        //  postURL = activity.getString(R.string.uaturl) + "/app/Cognitive/DamageDetection";
                        postURL = activity.getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                        //postURL = "https://aki-cra-dev-mobileapp-v2.azurewebsites.net/api/v3/app/Upload/UploadFiles";
                        imagename = "DamageImg_" + UUID.randomUUID().toString() + ".jpg";
                        sharedPreferencenew = activity.getSharedPreferences("MyCarview", Context.MODE_PRIVATE);
                        int caridval = sharedPreferencenew.getInt("selectcarid", 0);
                        String carnameval = sharedPreferencenew.getString("selectcartitle", "");
                        caridvalvalid = caridval;
                        carnamevalvalid = carnameval;
                        SharedPreferences modeldoctypeval = activity.getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                        String cardocuniq_doc = modeldoctypeval.getString(CarView.doctypeid, "");
                        MainActivity.cardocidval = cardocuniq_doc;
                        SharedPreferences thirdmodelPref = activity.getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                        final String thirdpartyunique = thirdmodelPref.getString(CarView.ThirdPartyID, "");
                        try {
                            Intent car = new Intent(CameraDamage.this, CaptureDamagedParts.class);
                            startActivity(car);
//                            DisplayMetrics dm = getResources().getDisplayMetrics();
//                            int densityDpi = dm.densityDpi;
//                            if (densityDpi >= 320 && densityDpi <= 390) {
//                                Intent car = new Intent(CameraDamage.this, CarView.class);
//                                startActivity(car);
//                            }
//                            if (densityDpi >= 280 && densityDpi <= 300) {
//                                Intent car = new Intent(CameraDamage.this, CarView.class);
//                                startActivity(car);
//                            }
//                            if (densityDpi >= 310 && densityDpi <= 395) {
//                                Intent car = new Intent(CameraDamage.this, CarView280.class);
//                                startActivity(car);
//                            }
//                            if (densityDpi >= 400 && densityDpi <= 520) {
//                                Intent car = new Intent(CameraDamage.this, CarView400.class);
//                                startActivity(car);
//                            }
                            finish();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                        SharedPreferences locationPref = activity.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
                        String lattest = locationPref.getString(MainActivity.Latitude, null);
                        String longtest = locationPref.getString(MainActivity.Longitude, null);
                        //SharedPreferences locationPref = activity.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
                        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                        //  String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
                        String dateTime = null;
                        dateTime = localToGMT();
                        //  postURL = "https://uat-aki.claims.digital/api/app/Cognitive/DamageDetection";
                        String uniqueID = UUID.randomUUID().toString();
                        SharedPreferences thirdmodelidpref = activity.getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                        final String thirdpartyunq = thirdmodelidpref.getString(CarView.ThirdPartyID, "");

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
                                //.addFormDataPart("attachmentTypeID",MainActivity.cardocidval)
                                //.addFormDataPart("attachmentTypeID","20b8857d-4884-4cc8-96a9-8c04a161c617")
                                .addFormDataPart("attachmentTypeID", MainActivity.cardocidval)
                                .addFormDataPart("isReturnURL", "true")
                                .build();


                        Request request = new Request.Builder()
                                .url(postURL)
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                            if (staticJsonObj.getInt("rcode") == 1) {
                                //MainActivity.MobileErrorLog(reqidval,"Claim Image Detection",staticJsonObj.getJSONObject("rObj").getString("damageDetectionResponse"),"Claim Damage Captured Images");
                                //MainActivity.MobilInfolog(activity,reqidval,"Claim Image Detection",staticRes,staticRes);
                                String newimgurl = staticJsonObj.getJSONObject("rObj").getString("blobDownloadURL");
                                String imageurl = newimgurl;

                                try {
                                    runOnUiThread(() -> {
//                                        try {
//                                            boolean isMotorVehicle = staticJsonObj.getJSONObject("rObj").getBoolean("isMoterVehicle");
//                                            if (!isMotorVehicle && activityCaptureDamagedParts != null && !isActivityPaused) {
//                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activityCaptureDamagedParts);
//                                                alertDialogBuilder.setTitle("Alert");
//                                                alertDialogBuilder.setMessage(getString(R.string.not_motor_vehicle_str));
//                                                alertDialogBuilder.setPositiveButton("Proceed", (dialog, which) -> dialog.dismiss());
//                                                alertDialogBuilder.setNegativeButton("Re-Capture", (dialog, which) -> dialog.dismiss());
//                                                alertDialogBuilder.setCancelable(false);
//                                                alertDialogBuilder.show();
//                                            }
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
                                        new Handler().postDelayed(() -> {
                                            // caridvalnewvalid =  caridvalvalid;
                                            Log.i("URL", imageurl);
                                            URL url = null;
                                            try {
                                                url = new URL(imageurl);
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }
                                            URLConnection urlConn = null;
                                            try {
                                                assert url != null;
                                                urlConn = url.openConnection();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }
                                            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
                                            try {
                                                httpConn.connect();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }

                                            try {
                                                in = httpConn.getInputStream();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }

                                            bmpimg = BitmapFactory.decodeStream(in);
                                            String filename = UUID.randomUUID().toString() + ".JPEG";
                                            File sd = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                                            File destfile = new File(sd, filename);

                                            Bitmap bitmapfile = bmpimg;
                                            try {
                                                FileOutputStream out = new FileOutputStream(destfile);
                                                bmpimg.compress(Bitmap.CompressFormat.JPEG, 80, out);
                                                out.flush();
                                                out.close();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }

                                            String imgData = destfile.toString();
                                            String imagename = UUID.randomUUID().toString() + ".JPEG";
                                            sharedPreferencenew = activity.getSharedPreferences("MyCarview", Context.MODE_PRIVATE);
                                            //int caridval = sharedPreferencenew.getInt("selectcarid", 0);
//                                        String carnameval = sharedPreferencenew.getString("selectcartitle", "");
                                            String cardescription = sharedPreferencenew.getString("selectcardescription", "");
                                            String mergenamedescr = carnamevalvalid + " @ " + cardescription + " @ " + caridvalvalid;
                                            //String uniqueID = UUID.randomUUID().toString() + ".JPEG";
//                                        String uniqueID = UUID.randomUUID().toString();
                                            int attachmentid = 100;
                                            // regnum = "KYB789U";
                                            mydb = new DatabaseHelper(activity);
                                            Cursor curseattachtoken = mydb.getthirdpartydetails();
                                            int counttoken = curseattachtoken.getCount();
                                            if (counttoken >= 1) {
                                                while (curseattachtoken.moveToNext()) {
                                                    regnum = curseattachtoken.getString(1);
                                                }
                                            }

                                            String imagenew = imgData;
                                            int attachid = 1;
                                            mydb = new DatabaseHelper(activity);

//                                                        int countsamplevalue = mydb.getdeletelocalalreadyid(String.valueOf(caridvalnewvalid)).getCount();
//                                                        if(mydb.getdeletelocalalreadyid(String.valueOf(caridvalnewvalid)).getCount() >= 1 )
//                                                        {
//                                                            mydb.deletelocalalreadyid(String.valueOf(caridvalnewvalid));
//                                                            MainActivity.damagecountnew = MainActivity.damagecountnew - 1;
//                                                        }

                                            int countnew = mydb.getinsertloceattachment().getCount();
                                            boolean Isinserted = mydb.insertlocalimageattachment(imagename, attachid, imgData, caridvalvalid, carnamevalvalid, cardescription, mergenamedescr, uniqueID, attachmentid, regnum, imagenew);
                                            if (Isinserted) {
                                                boolean test = Isinserted;
                                                //Toast.makeText(,"DataInserted", Toast.LENGTH_SHORT).show();
                                                Log.i(null, "Insertion Done");
                                            } else {
                                                boolean test = Isinserted;
                                                //Toast.makeText(getActivity(),"DataNotInserted", Toast.LENGTH_SHORT).show();
                                                Log.i(null, "Not Insertion Done");
                                            }
                                        }, 3000);
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }

//                                    try {
//                                        new GetImageFromUrl().execute(imageurl);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//


                            }
                        } catch (final IOException e) {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
//                                        MainActivity.MobileErrorLog(reqidval,"CreateDriver-GetAddSelfDriver",e.toString());
//                                        //Toast.makeText(activity, e.toString() , Toast.LENGTH_SHORT).show();
                                    Toast.makeText(activity, activity.getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            });

                            // Toast.makeText(activity,e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                Toast.makeText(activity, activity.getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                        // Toast.makeText(activity,e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public Bitmap getBitmap(String path) {
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

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = CameraDamage.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onBackPressed() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
            //super.onBackPressed();
            Toast.makeText(CameraDamage.this, "Please wait your image is uploading", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }
}