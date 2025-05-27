package com.aki.claimreporting.stereo_vision;

import static com.aki.claimreporting.CameraDamage.bmpimg;
import static com.aki.claimreporting.CameraDamage.insurer;
import static com.aki.claimreporting.CameraDamage.localToGMT;
import static com.aki.claimreporting.CameraDamage.mCrashlytics;
import static com.aki.claimreporting.CameraDamage.sharedPreferencenew;
import static com.aki.claimreporting.CertificateActivation.postURL;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.aki.claimreporting.AddVehicle;
import com.aki.claimreporting.CaptureDamagedParts;
import com.aki.claimreporting.CarView;
import com.aki.claimreporting.DatabaseHelper;
import com.aki.claimreporting.MainActivity;
import com.aki.claimreporting.R;

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
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageCheckerActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://20.247.169.36:8002/detect-real-or-fake/v2/";
    public InputStream in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_checker);
        init();

        ActionBar support = getSupportActionBar();
        if(support != null) {
            support.setTitle("Image Processing");
        }
    }

    private void init() {
        try {

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "StereoImages");
            String ultraWideImagePath = getIntent().getStringExtra("WID_IMAGE");
            String telePhotoImagePath = getIntent().getStringExtra("TEL_IMAGE");
            assert ultraWideImagePath != null;
            File ultraWideImage = new File(dir, ultraWideImagePath);
            assert telePhotoImagePath != null;
            File telePhotoImage = new File(dir, telePhotoImagePath);

            postImages(telePhotoImage, ultraWideImage);

            getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {

                }
            });

        } catch (Exception e) {
            e.printStackTrace(System.out);
            showReCaptureDialog();
        }
    }


    private void postImages(final File telePhotoImage, final File ultraWideImage) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

                RequestBody telePhotoBody = RequestBody.create(MEDIA_TYPE_JPEG, telePhotoImage);
                RequestBody ultraWideBody = RequestBody.create(MEDIA_TYPE_JPEG, ultraWideImage);

                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("files", telePhotoImage.getName(), telePhotoBody)
                        .addFormDataPart("files", ultraWideImage.getName(), ultraWideBody)
                        .build();

                Request request = new Request.Builder()
                        .url(SERVER_URL)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().string();
                    boolean isFakeByLogic = isFakeByLogic(responseString);

                    SharedPreferences sharedPref = getSharedPreferences("CarImages", MODE_PRIVATE);
                    String currentFieldID = sharedPref.getString("CarImage", "N/A");

                    runOnUiThread(() -> {
                        if(isFakeByLogic) {
                            showReCaptureDialog();
                        } else {
                            String filePath = ultraWideImage.getAbsolutePath();
                            Matrix matrix = new Matrix();
                            matrix.postRotate(90.0f);
                            Bitmap ultraWideBitmap = BitmapFactory.decodeFile(ultraWideImage.getAbsolutePath());
                            Bitmap rotatedUltraWideBitmap = Bitmap.createBitmap(ultraWideBitmap, 0, 0, ultraWideBitmap.getWidth(), ultraWideBitmap.getHeight(), matrix, true);
                            handleUpload(rotatedUltraWideBitmap);
//                            CapturedVehicleImagesNew(rotatedUltraWideBitmap);
                        }
                    });

                } else {
                    throw new IOException("Unexpected response code: " + response.code());
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }).start();
    }

    private void showReCaptureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Oops!")
                .setCancelable(false)
                .setMessage("The image you captured doesn’t appear to be a valid vehicle—please retake and upload a clear, valid photo of the vehicle.")
                .setPositiveButton("Recapture", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ImageCheckerActivity.this, CameraPreview.class));
                        finish();
                    }
                })
                .setCancelable(false)  // Prevent closing by tapping outside
                .show();
    }

    private static boolean isFakeByLogic(String responseString) throws JSONException {
        JSONObject jsonResponse = new JSONObject(responseString);

        //        final double sharpness1 = jsonResponse.getDouble("sharpness1");
//        final double sharpness2 = jsonResponse.getDouble("sharpness2");
//
//        boolean isFakeByLogic;
//
//        if (isFake) {
//            isFakeByLogic = true;
//        } else {
////            isFakeByLogic = sharpness1 < 100.0 && sharpness2 < 300.0;
//            isFakeByLogic = (sharpness1 < 80 && sharpness2 < 450.0);
//        }
        return jsonResponse.getBoolean("isFake");
    }


    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void handleUpload(Bitmap ultraWideBitmap) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            //insurer = null;
            if (ultraWideBitmap == null) {
                try {


                    Intent car = new Intent(ImageCheckerActivity.this, CaptureDamagedParts.class);
                    startActivity(car);
                    finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                insurer = ultraWideBitmap;

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
                    damageimagesget(ImageCheckerActivity.this);

                } else {

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void damageimagesget(Activity activity) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        AsyncTask.execute(

                () -> {

                    try {

                        AtomicReference<DatabaseHelper> mydb = new AtomicReference<>(new DatabaseHelper(activity));
                        if (mydb.get().getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.get().getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    MainActivity.stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                        final MediaType mediaType = MediaType.parse("image/jpeg");
                        Bitmap bitmapnew = insurer;
                        ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
                        bitmapnew.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
                        byte[] byteArraynew = streamnew.toByteArray();
                        String imgDatatest = Base64.encodeToString(byteArraynew, Base64.DEFAULT);
                        SharedPreferences modelPref = activity.getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                        final String modelunique = modelPref.getString(AddVehicle.ModelID, null);

                        SharedPreferences incidePref = activity.getSharedPreferences("CRAID", MODE_PRIVATE);
                        String incident_id = incidePref.getString("CraIdval", "");
                        String certnumval = "";


                        SharedPreferences modeldoctypePref = activity.getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                        String cardocuniq_id = modeldoctypePref.getString(CarView.doctypeid, "");

                        //  postURL = activity.getString(R.string.uaturl) + "/app/Cognitive/DamageDetection";
                        postURL = activity.getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                        //postURL = "https://aki-cra-dev-mobileapp-v2.azurewebsites.net/api/v3/app/Upload/UploadFiles";
                        AtomicReference<String> imagename = new AtomicReference<>("DamageImg_" + UUID.randomUUID().toString() + ".jpg");
                        sharedPreferencenew = activity.getSharedPreferences("MyCarview", Context.MODE_PRIVATE);
                        int caridval = sharedPreferencenew.getInt("selectcarid", 0);
                        String carnameval = sharedPreferencenew.getString("selectcartitle", "");
                        int caridvalvalid = caridval;
                        String carnamevalvalid = carnameval;
                        SharedPreferences modeldoctypeval = activity.getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                        String cardocuniq_doc = modeldoctypeval.getString(CarView.doctypeid, "");
                        MainActivity.cardocidval = cardocuniq_doc;
                        SharedPreferences thirdmodelPref = activity.getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                        final String thirdpartyunique = thirdmodelPref.getString(CarView.ThirdPartyID, "");
                        try {
                            Intent car = new Intent(ImageCheckerActivity.this, CaptureDamagedParts.class);
                            startActivity(car);
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

                        MultipartBody body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("fileName", imagename.get())
                                .addFormDataPart(
                                        "image", imagename.get(),
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
                                String reqidval = staticJsonObj.getString("reqID");
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
                                            imagename.set(UUID.randomUUID().toString() + ".JPEG");
                                            sharedPreferencenew = activity.getSharedPreferences("MyCarview", Context.MODE_PRIVATE);
                                            //int caridval = sharedPreferencenew.getInt("selectcarid", 0);
//                                        String carnameval = sharedPreferencenew.getString("selectcartitle", "");
                                            String cardescription = sharedPreferencenew.getString("selectcardescription", "");
                                            String mergenamedescr = carnamevalvalid + " @ " + cardescription + " @ " + caridvalvalid;
                                            //String uniqueID = UUID.randomUUID().toString() + ".JPEG";
//                                        String uniqueID = UUID.randomUUID().toString();
                                            int attachmentid = 100;
                                            // regnum = "KYB789U";
                                            mydb.set(new DatabaseHelper(activity));
                                            Cursor curseattachtoken = mydb.get().getthirdpartydetails();
                                            int counttoken = curseattachtoken.getCount();
                                            String regnum = "";
                                            if (counttoken >= 1) {
                                                while (curseattachtoken.moveToNext()) {
                                                    regnum = curseattachtoken.getString(1);
                                                }
                                            }

                                            String imagenew = imgData;
                                            int attachid = 1;
                                            mydb.set(new DatabaseHelper(activity));

//                                                        int countsamplevalue = mydb.getdeletelocalalreadyid(String.valueOf(caridvalnewvalid)).getCount();
//                                                        if(mydb.getdeletelocalalreadyid(String.valueOf(caridvalnewvalid)).getCount() >= 1 )
//                                                        {
//                                                            mydb.deletelocalalreadyid(String.valueOf(caridvalnewvalid));
//                                                            MainActivity.damagecountnew = MainActivity.damagecountnew - 1;
//                                                        }

                                            int countnew = mydb.get().getinsertloceattachment().getCount();
                                            boolean Isinserted = mydb.get().insertlocalimageattachment(imagename.get(), attachid, imgData, caridvalvalid, carnamevalvalid, cardescription, mergenamedescr, uniqueID, attachmentid, regnum, imagenew);
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
}
