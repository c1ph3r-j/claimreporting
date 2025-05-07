package com.aki.claimreporting;

import static android.app.PendingIntent.getActivity;
import static com.aki.claimreporting.ClaimType.CraIdval;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_READ_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_STORAGE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


public class InsuranceCompanySupport extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int Upload_Support_IMAGE = 1234;
    public static DatabaseHelper mydb;
    public static FirebaseCrashlytics mCrashlytics;
    public static byte[] regByte = null;
    public static String stokenval;
    public static String reqidval;
    public static String imgattachreference;
    public static String memberCompanyID, objectTypeID, objectTypeName, object1, object2, object3;
    public static String supportRefiD;
    public static String membercompany;
    public ProgressDialog progressdialog;
    public byte[] arrayaccidentimages;
    public Bitmap insurer;
    String dateTime;
    Activity activity;
    ImageView moreimgsupport;
    LinearLayout lininsured;
    TextView uatinsur;
    LinearLayout submitButton;
    EditText editTextTextMultiLinetxt;
    EditText edittitletxt;
    TextView attachname;
    String currentPhotoPath;
    Spinner supportSpinnerVal;

    PermissionHandler permissionManager;
    String[] permissions = PERMISSION_LOCATION_STORAGE;
    ArrayList<String> supportspinner = new ArrayList<>();
    List<SupportObjectList> supportlist = new ArrayList<>();

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
        Objects.requireNonNull(getSupportActionBar()).setTitle("Contact Insurance Company");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        setContentView(R.layout.activity_insurance_company_support);
        try {
            preventSizeChange(this, getSupportActionBar());
            permissionManager = new PermissionHandler(this);
            permissionManager.setPermissionResultListener(new PermissionHandler.PermissionResultListener() {
                @Override
                public void onPermissionGranted() {

                }

                @Override
                public void onPermissionDenied() {
                    permissionManager.showPermissionExplanationDialogC(permissions);
                }
            });
            activity = this;
            submitButton = findViewById(R.id.submitBtnInsuranceSupport);
            supportSpinnerVal = (Spinner) findViewById(R.id.spinnersupports);
            init();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


        //Have to set the submission details here..
        //don't know which method is to be set.
        try {
            submitButton.setOnClickListener(onClickSubmit -> insertsupportticket());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onCreate", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            edittitletxt = (EditText) findViewById(R.id.edittitle);
            editTextTextMultiLinetxt = (EditText) findViewById(R.id.editTextTextMultiLine);
            lininsured = (LinearLayout) findViewById(R.id.lineinsur);
            uatinsur = (TextView) findViewById(R.id.uatinsur);
            attachname = (TextView) findViewById(R.id.editattachment);
            moreimgsupport = (ImageView) findViewById(R.id.uploadimg);

            try {
                getSupportObjectlist();
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            try {
                moreimgsupport.setOnClickListener(onClickMoreImg -> {
                    if (permissionManager.hasPermissions(permissions)) {
                        Intent intent = getFileChooserIntent();
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Upload_Support_IMAGE);
                    } else {
                        permissionManager.requestPermissions(permissions);
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

    public byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionManager.handleSettingsActivityResult(permissions, requestCode, resultCode);
        System.out.println(requestCode + "\tupload support Image: " + Upload_Support_IMAGE);

        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (requestCode == Upload_Support_IMAGE) {

                try {
                    // uploadimage = "1";
                    Uri uri = data.getData();
                    currentPhotoPath = uri.toString();


                    currentPhotoPath = data.getData().toString();
                    System.out.println(currentPhotoPath + "qwertyuiop");

                    String filenew = getContentResolver().getType(uri);
                  /*  String filetype;
                    if (filenew == null || filenew == "null") {
                        filetype = uri.toString();

                    } else {
                        filetype = getContentResolver().getType(uri);
                    }*/
                    ContentResolver contentResolver =getContentResolver();
                    String filetype = contentResolver.getType(uri);
                    assert filetype != null;
                    if (filetype.contains("image") || filetype.contains("jpg") || filetype.contains("png")) {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        arrayaccidentimages = readBytes(inputStream);
//                    uploadaccidentimages  = BitmapFactory.decodeStream(inputStream);
                        String encoded1 = Base64.encodeToString(arrayaccidentimages, Base64.DEFAULT);
                        String encoded2 = encoded1;


                        // insurer  = BitmapFactory.decodeStream(inputStream);
                        insurer = BitmapFactory.decodeByteArray(arrayaccidentimages, 0, arrayaccidentimages.length);
                        /*ObjectDetectorActivity.Global.img = insurer;*/

                        uploadsupportimages();
                    } else {
                        currentPhotoPath = "";
                        supportinvalidimg();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


    }

    public void supportinvalidimg() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Alert!")
                    .setMessage(getString(R.string.file_invalid_image))
                    .setPositiveButton("OK",
                            (dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private Intent getFileChooserIntent() {
        //String[] mimeTypes = {"image/*", "application/pdf"};
        String[] mimeTypes = {"image/*"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
        if (mimeTypes.length > 0) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }

        return intent;
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

    public void uploadsupportimages() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    mydb = new DatabaseHelper(activity);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                    final MediaType mediaType;
                    String filename;

                    mediaType = MediaType.parse("image/jpeg");
                    Bitmap bitmap = insurer;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    Log.i(null, String.valueOf(bitmap.getByteCount()));
                    byte[] byteArray = stream.toByteArray();
                    regByte = byteArray;
                    String imgData = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    String imgDatanew = imgData;
                    filename = "ClaimImg.jpg";

                    dateTime = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dateTime = localToGMT();
                    }
                    String uniqueID = UUID.randomUUID().toString();
                    SharedPreferences locationPref = activity.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
                    String latval = locationPref.getString(MainActivity.Latitude, null);
                    String longval = locationPref.getString(MainActivity.Longitude, null);
                    String cardocuniq_id = "50a17ba8-00d1-42d5-9866-dc1e27ed2db1";
                    Thread thread = new Thread(() -> {
                        try {
                            String postURL = getString(R.string.uaturl) + "/app/Upload/UploadFiles";
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
                                            RequestBody.create(mediaType, regByte))
                                    .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                    .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                    .addFormDataPart("captureAttachmentID", uniqueID)
                                    .addFormDataPart("captureDateTime", dateTime)
                                    .addFormDataPart("attachmentTypeID", cardocuniq_id)
                                    .addFormDataPart("isReturnURL", "false")
                                    .build();
                            Request request = new Request.Builder()
                                    .url(postURL)
                                    .method("POST", body)
                                    .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
                                    .addHeader("Authorization", "Bearer " + stokenval)
                                    .build();
                            Response staticResponse;
                            try {
                                runOnUiThread(() -> progressdialog = ProgressDialog.show(InsuranceCompanySupport.this, getString(R.string.loading), getString(R.string.please_wait), true));
                                staticResponse = client.newCall(request).execute();
                                assert staticResponse.body() != null;
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
                                    runOnUiThread(() -> {
                                        try {
//                                                commentsval = "";
//                                                addspotcomments.setText("");
//                                                accspotcomments.setText("");
//                                                documenttype.setSelection(0);

//                                                AttachmentRefNum element = new AttachmentRefNum(staticJsonObj.getJSONObject("rObj").getString("AttachmentID"));
//                                                iteminfo.add(element);
                                            attachname.setText("CRA_Img_Support.png");
                                            currentPhotoPath = "";
                                            //  iteminfo.add(staticJsonObj.getJSONObject("rObj").getString("AttachmentID"));
                                            imgattachreference = staticJsonObj.getJSONObject("rObj").getString("AttachmentID");
                                            progressdialog.dismiss();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                    });
                                }
                            } catch (final IOException | JSONException e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                runOnUiThread(() -> progressdialog.dismiss());
                                MainActivity.MobileErrorLog(reqidval, "ClaimImage-uploadclaimimages", e.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    });
                    thread.start();
                } else {
                    try {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(InsuranceCompanySupport.this);
                        dialog.setMessage(getString(R.string.gps_not_enabled));
                        dialog.setPositiveButton("Ok", (dialog1, which) -> {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        });
                        AlertDialog alert = dialog.create();
                        alert.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }
            } else {
                Toast.makeText(InsuranceCompanySupport.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            runOnUiThread(() -> progressdialog.dismiss());
        }

    }

    public void insertsupportticket() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                if (checkGPSStatus()) {
                    if (supportSpinnerVal.getSelectedItem().toString().contains("Select")) {
                        Toast.makeText(InsuranceCompanySupport.this, getString(R.string.selectinsoption), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (edittitletxt.getText().toString() == null || edittitletxt.getText().toString() == "" || edittitletxt.getText().toString().length() == 0) {
                        Toast.makeText(InsuranceCompanySupport.this, getString(R.string.subjectman), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (editTextTextMultiLinetxt.getText().toString() == null || editTextTextMultiLinetxt.getText().toString() == "" || editTextTextMultiLinetxt.getText().toString().length() == 0) {
                        Toast.makeText(InsuranceCompanySupport.this, getString(R.string.bodyman), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        mydb = new DatabaseHelper(InsuranceCompanySupport.this);
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

                    try {
                        Thread thread = new Thread(() -> {
                            SharedPreferences incidePref = activity.getSharedPreferences("CRAID", MODE_PRIVATE);
                            String incident_id = incidePref.getString(CraIdval, "");
                            String postURL = getString(R.string.uaturl) + "/app/Support/InsertSupport";
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");

                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();

                            JsonObject Details = new JsonObject();
                            Details.addProperty("supportTitle", edittitletxt.getText().toString());
                            Details.addProperty("supportDesc", editTextTextMultiLinetxt.getText().toString());
                            Details.addProperty("memberCompanyID", memberCompanyID);
                            Details.addProperty("objectTypeID", objectTypeID);
                            Details.addProperty("objectTypeName", objectTypeName);
                            Details.addProperty("object1", object1);
                            Details.addProperty("object2", object2);
                            Details.addProperty("object3", object3);
//                          Details.addProperty("attachments", jsonData);
                            Details.addProperty("attachments", "[{'attachmentID':'C-AC1060'},{'attachmentID':'C-AC1061'}]");
                            //   String listString = String.join(", ", list);

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
                                runOnUiThread(() -> progressdialog = ProgressDialog.show(activity, getString(R.string.loading), getString(R.string.please_wait), true));
                                staticResponse = client.newCall(request).execute();
                                assert staticResponse.body() != null;
                                String staticRes = staticResponse.body().string();
                                Log.i(null, staticRes);
                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                if (staticJsonObj.getInt("rcode") == 1) {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        try {
                                            supportRefiD = staticJsonObj.getJSONObject("rObj").getString("supportRefID");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        Intent car = new Intent(InsuranceCompanySupport.this, SupportSuccess.class);
                                        startActivity(car);
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
                                                AlertDialog.Builder alert = new AlertDialog.Builder(InsuranceCompanySupport.this);
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
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                });
                            }
                        });
                        thread.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                } else {
                    try {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(InsuranceCompanySupport.this);
                        dialog.setMessage(getString(R.string.gps_not_enabled));
                        dialog.setPositiveButton("Ok", (dialog1, which) -> {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        });
                        AlertDialog alert = dialog.create();
                        alert.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }
            }
        } catch (Exception ex) {
            runOnUiThread(() -> {
                progressdialog.dismiss();
                ex.printStackTrace();
                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                mCrashlytics.recordException(ex);
            });
        }
    }

    public void getSupportObjectlist() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (isNetworkConnected()) {

                if (checkGPSStatus()) {


                    mydb = new DatabaseHelper(InsuranceCompanySupport.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                MainActivity.stokenval = curseattachtoken.getString(1);
                                System.out.println(MainActivity.stokenval + "\tqwertyuiop");
                            }
                        }
                    }

                    try {
                        Thread thread = new Thread(() -> {

                            String postURL = getString(R.string.uaturl) + "/app/Support/GetObjectTypes";
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
                            JsonObject Details = new JsonObject();
                            Details.addProperty("organizationTypeID", "35");
                            String insertString = Details.toString();
                            RequestBody body = RequestBody.create(JSON, insertString);
                            Request request = new Request.Builder()
                                    .url(postURL)
                                    .header("Authorization", "Bearer " + MainActivity.stokenval)
                                    .header("mobileParameter", MainActivity.InsertMobileParameters())
                                    .post(body)
                                    .build();
                            Response staticResponse;
                            try {
                                runOnUiThread(() ->
                                        progressdialog = ProgressDialog.show(InsuranceCompanySupport.this, getString(R.string.loading), getString(R.string.please_wait), true));
                                staticResponse = client.newCall(request).execute();
                                int statuscode = staticResponse.code();
                                if (statuscode == 401) {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        unauthorize(InsuranceCompanySupport.this);
                                        return;
                                    });
                                }
                                assert staticResponse.body() != null;
                                String staticRes = staticResponse.body().string();
                                Log.i(null, staticRes);
                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                if (staticJsonObj.getInt("rcode") == 1) {
                                    supportspinner.add("Select the option");
                                    JSONArray supportResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getObjectTypes");
                                    for (int i = 0; i < supportResponseList.length(); i++) {
                                        JSONObject supportObj = supportResponseList.getJSONObject(i);
                                        SupportObjectList element = new SupportObjectList(
                                                supportObj.getString("objectTypeID"),
                                                supportObj.getString("objectTypeName"),
                                                supportObj.getString("object1"),
                                                supportObj.getString("object2"),
                                                supportObj.getString("object3"),
                                                supportObj.getString("object4"),
                                                supportObj.getString("object5")
                                        );
                                        if (supportObj.getString("objectTypeID").equals("2")) {
                                            supportspinner.add(supportObj.getString("objectTypeName") + " - " + supportObj.getString("object2"));
                                            supportlist.add(element);
                                        } else if (supportObj.getString("objectTypeID").equals("4")) {
                                            supportspinner.add(supportObj.getString("objectTypeName"));
                                            supportlist.add(element);
                                        } else {
                                            supportspinner.add(supportObj.getString("objectTypeName") + " - " + supportObj.getString("object1"));
                                            supportlist.add(element);
                                        }

                                    }
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        supportSpinnerVal = (Spinner) findViewById(R.id.spinnersupports);
                                        supportSpinnerVal.setOnItemSelectedListener(InsuranceCompanySupport.this);
                                        ArrayAdapter<String> insunameAdapter = new ArrayAdapter<>(InsuranceCompanySupport.this, android.R.layout.simple_list_item_1, supportspinner);
                                        supportSpinnerVal.setAdapter(insunameAdapter);


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
                                                AlertDialog.Builder alert = new AlertDialog.Builder(InsuranceCompanySupport.this);
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
                                }
                            } catch (final Exception ex) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    Toast.makeText(InsuranceCompanySupport.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                        thread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(InsuranceCompanySupport.this);
                    dialog.setMessage(getString(R.string.gps_not_enabled));
                    dialog.setPositiveButton("Ok", (dialog1, which) -> {
                        //this will navigate user to the device location settings screen
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();
                }


            } else {
                Toast.makeText(InsuranceCompanySupport.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(InsuranceCompanySupport.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

   /* public void unauthorize() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(InsuranceCompanySupport.this);
        dialog.setMessage("Your session have been expired. Please login again to continue");
        dialog.setPositiveButton("Ok", (dialog1, which) -> {
            mydb = new DatabaseHelper(InsuranceCompanySupport.this);
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
            Intent login = new Intent(InsuranceCompanySupport.this, Login.class);
            startActivity(login);
        });
        dialog.setCancelable(false);
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }*/

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        try {
            if (adapterView.getId() == R.id.spinnersupports) {
                SupportObjectList support = supportlist.get(position - 1);
                String value1 = supportSpinnerVal.getSelectedItem().toString();
                String value2 = value1;
                if (supportSpinnerVal.getSelectedItem().toString().contains("Select")) {
                    lininsured.setVisibility(View.GONE);
                } else if (supportSpinnerVal.getSelectedItem().toString().contains("Others")) {
                    memberCompanyID = support.getObject4();
                    objectTypeID = support.getObjectTypeID();
                    objectTypeName = support.getObjectTypeName();
                    object1 = support.getObject1();
                    object2 = support.getObject2();
                    object3 = support.getObject3();
                    lininsured.setVisibility(View.VISIBLE);
                    uatinsur.setText("AKI, will receive this notification for action");
                    membercompany = "AKI";
                } else {
                    memberCompanyID = support.getObject4();
                    objectTypeID = support.getObjectTypeID();
                    objectTypeName = support.getObjectTypeName();
                    object1 = support.getObject1();
                    object2 = support.getObject2();
                    object3 = support.getObject3();
                    lininsured.setVisibility(View.VISIBLE);
                    membercompany = support.getObject5();
                    uatinsur.setText(support.getObject5() + ", will receive this notification for action");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + "onItemSelected", ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            lininsured.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        try {
            finish();
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
        finish();
    }
}