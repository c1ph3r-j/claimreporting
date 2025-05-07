package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA_AND_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_READ_STORAGE;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_RECORD_AUDIO;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_STORAGE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClaimStolenTheft extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private static final int IMAGE_TAKE_CODE = 1234;
    public static DatabaseHelper mydb;
    public static int sizeOfTheArraylist;
    public static byte[] regByte = null;
    public static String stokenval;
    public static String phototaken;
    public static Bitmap insurer;
    public static String reqidval;
    public static String udiduniversal;
    static Bitmap bitmap;
    private static FirebaseCrashlytics mCrashlytics;
    public String encryptedSHA;
    public ProgressDialog progressdialog;
    public RecyclerView recyclerView;
    public ViewPager2 stolenPartsImageView;
    public ClaimStolenImageAdapter mAdapter;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    public View rootview;
    public int optionvisual = 0;
    public ImageView stolenimgclaim;
    public TableRow claimaddrow;
    public ListView list;
    public LinearLayout claimproceed, submitbutton;
    public TextView comments;
    // NOTE CHANGE END
//    TextView adressval;
    public TextView stolenlocationdescribe, txtotherdescribe;
    public RadioGroup otherlocation, stolenInfo;
    public LinearLayout addressmore, additionalInfoLayout, locationLayout;
    public boolean radioownlocation = false;
    String[] permissions = PERMISSION_CAMERA_AND_STORAGE;
    // NOTE CHANGE
    EditText streetField, addressField, cityField;
    String otherLocationVal;
    View dividerAboveCaptureImage;
    //NOTE CHANGE 1
    Spinner theftCauseSpinner;
    LinearLayout theftCauseLayout;
    LinearLayout otherReasonLayout;
    PermissionHandler permissionManager;
    //NOTE CHANGE END 1
    Activity activity;
    String currentPhotoPath;
    ArrayList<ViewPagerImageFragment> attachimglist = new ArrayList<>();
    ArrayList<AdditionalAttachclaimlist> attachimgist = new ArrayList<AdditionalAttachclaimlist>();
    boolean isCarMissing;

    public static void uploadclaimimages(Activity activity) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        AsyncTask.execute(

                new Runnable() {

                    @Override
                    public void run() {

                        try {
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
                            final MediaType mediaType = MediaType.parse("image/jpeg");
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                            Log.i(null, String.valueOf(bitmap.getByteCount()));
                            byte[] byteArray = stream.toByteArray();
                            regByte = byteArray;
                            SharedPreferences modeldoctypePref = activity.getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                            String cardocuniq_id = modeldoctypePref.getString(CarView.doctypeid, "");
                            String imgData = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            SharedPreferences incidePref = activity.getSharedPreferences("CRAID", MODE_PRIVATE);
                            String incident_id = incidePref.getString("CraIdval", "");
//                             SharedPreferences modeldoctypePref = activity.getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
//                            String cardocuniq_id = modeldoctypePref.getString(CarView.doctypeid,"");
                            SharedPreferences locationPref = activity.getSharedPreferences("LocationPref", MODE_PRIVATE);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                            String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system

                            String imagename = "StolenImg" + UUID.randomUUID().toString() + ".jpg";
                            MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
                            RequestBody body = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("fileName", imagename)
                                    .addFormDataPart(
                                            "image", imagename,
                                            RequestBody.create(mediaType, byteArray))
                                    .addFormDataPart("incidentUniqueCode", incident_id)
                                    .addFormDataPart("documentType", cardocuniq_id)
                                    .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                    .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                    .addFormDataPart("captureAttachmentID", udiduniversal)
                                    .addFormDataPart("captureDateTime", dateTime)
                                    .addFormDataPart("attachmentTypeID", "3a6ed13f-0cf3-4855-9378-9fa18781dafe")
                                    .addFormDataPart("isReturnURL", "true")
                                    .build();
                            Request request = new Request.Builder()
                                    .url(MainActivity.postURL)
                                    .method("POST", body)
                                    .addHeader("Authorization", "Bearer " + stokenval)
                                    .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
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
                                    if (cardocuniq_id.equals("3a6ed13f-0cf3-4855-9378-9fa18781dafe")) {
//                                        MainActivity.MobileErrorLog(reqidval,"Stolen-Flow",staticJsonObj.getJSONObject("rObj").getString("AttachmentRefNo"),"Stolen Theft Captured Images");
                                    }

                                }
                            } catch (final IOException e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
//                                MainActivity.MobileErrorLog(reqidval,"ClaimImage-uploadclaimimages",e.toString(),e.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_stolen_theft);
        preventSizeChange(this, getSupportActionBar());
        getSupportActionBar().setTitle(getString(R.string.stolen_theft_information));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        activity = this;
        phototaken = "0";
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
        init();

    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            mydb = new DatabaseHelper(ClaimStolenTheft.this);
            mydb.deleteClaimImgmor();

            isCarMissing = false;

            // NOTE CHANGE 2
            theftCauseSpinner = findViewById(R.id.theftCauseSpinner);
            otherReasonLayout = findViewById(R.id.otherReasonLayout);
            theftCauseLayout = findViewById(R.id.theftCauseLayout);


            theftCauseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (theftCauseSpinner.getSelectedItem().toString().equals("Other Reason")) {
                        otherReasonLayout.setVisibility(View.VISIBLE);
                    } else {
                        otherReasonLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            // NOTE CHANGE END 2

            txtotherdescribe = (TextView) findViewById(R.id.txtadditionalinfo);

            stolenlocationdescribe = (TextView) findViewById(R.id.stolenlocation);

            otherlocation = (RadioGroup) findViewById(R.id.stolenlocationradiogroup);
            addressmore = (LinearLayout) findViewById(R.id.txtaddressotherinfo);
            addressmore.setVisibility(View.GONE);
            dividerAboveCaptureImage = findViewById(R.id.dividerAboveCaptureImage);
            stolenInfo = findViewById(R.id.radioStolenGroup);
            stolenInfo.setOnCheckedChangeListener(this);
            comments = (EditText) findViewById(R.id.editcomments);
            // NOTE CHANGE
            streetField = findViewById(R.id.streetField);
            addressField = findViewById(R.id.AddressField);
            cityField = findViewById(R.id.cityField);
//            adressval = (EditText) findViewById(R.id.editaddaddress);
            // NOTE CHANGE END
            locationLayout = findViewById(R.id.locationLayout);
            locationLayout.setVisibility(View.GONE);
            additionalInfoLayout = findViewById(R.id.additionInfoLayout);
            additionalInfoLayout.setVisibility(View.GONE);
            SharedPreferences thirdisPref = getSharedPreferences("IsThirdParty", MODE_PRIVATE);
            //String isthird = thirdisPref.getString(ClaimRegFragment.Thirdpartyavailable,"");
            String isthird = "0";
            //Intent car = new Intent(PolicyInformation.this, ThirdPartyList.class);
            // startActivity(car);

            try {
                claimaddrow = (TableRow) findViewById(R.id.claimaddimgrow);
                if (isthird.equals("1")) {
                    claimaddrow.setVisibility(View.GONE);


                } else {
                    claimaddrow.setVisibility(View.VISIBLE);


                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            submitbutton = findViewById(R.id.butidprcd);
            try {
                submitbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // NOTE CHANGE 3
                        SharedPreferences thirdisPref = getSharedPreferences("IsThirdParty", MODE_PRIVATE);
                        String methodName = Objects.requireNonNull(new Object() {
                        }.getClass().getEnclosingMethod()).getName();
                        try {
                            if (stolenInfo.getCheckedRadioButtonId() == -1) {
                                Toast.makeText(ClaimStolenTheft.this, "Please Select The Type Of Theft!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (radioownlocation) {
                                // NOTE CHANGE
//                                if (adressval.getText().toString().equals("") || adressval.getText().toString().equals("null") || adressval.getText().toString().equals(null)) {
//                                    Toast.makeText(ClaimStolenTheft.this, getString(R.string.addressval), Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
                                if (streetField.getText().toString().trim().equals("") || streetField.getText().toString().trim().length() == 0) {
                                    showAlertToTheUser("Street!");
                                    return;
                                }
                                if (addressField.getText().toString().trim().equals("") || addressField.getText().toString().trim().length() == 0) {
                                    showAlertToTheUser("Address!");
                                    return;
                                }
                                if (cityField.getText().toString().trim().equals("") || cityField.getText().toString().trim().length() == 0) {
                                    showAlertToTheUser("City!");
                                    return;
                                }
                                otherLocationVal = streetField.getText().toString().trim() + "," + addressField.getText().toString().trim() + "," + cityField.getText().toString().trim();
                                // NOTE CHANGE END
                            }
                            if (isCarMissing) {
                                if (theftCauseSpinner.getSelectedItemPosition() == 0) {
                                    Toast.makeText(ClaimStolenTheft.this, "Please Select the circumstance of Loss to continue!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (theftCauseSpinner.getSelectedItemPosition() == 3 && (comments.getText().toString() == "" || comments.getText().toString() == null || comments.getText().toString().length() == 0)) {
                                    Toast.makeText(ClaimStolenTheft.this, getString(R.string.comments), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                runOnUiThread(() -> {

                                    //insertclaimpdf();
                                    if(isUserSignedUp()){
                                        Intent car = new Intent(ClaimStolenTheft.this, ClaimSuccess.class);
                                        startActivity(car);
                                    }else{
                                        try {
                                            showCustomDialog();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }


                                });
                               // insertclaimfinal();
                            } else {
                                if ((comments.getText().toString() == "" || comments.getText().toString() == null || comments.getText().toString().length() == 0)) {
                                    Toast.makeText(ClaimStolenTheft.this, "Please describe about the theft.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (!phototaken.equals("1")) {
                                    Toast.makeText(ClaimStolenTheft.this, "Please capture the Stolen Part images", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (permissionManager.hasPermissions(permissions)) {
                                    Intent car = new Intent(ClaimStolenTheft.this, AccidentDescription.class);
                                    startActivity(car);
                                } else {
                                    permissionManager.requestPermissions(permissions);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }

// NOTE CHANGE END 3

//                        if (MainActivity.isPartsStolen) {
//                            if (isthird.equals("1")) {
//
//
//
//                            } else {
//
//                                if (!radioownlocation) {
//                                    if (adressval.getText().toString().trim().equals("") || adressval.getText().toString().trim().equals("null") || adressval.getText().toString().equals(null)) {
//                                        Toast.makeText(ClaimStolenTheft.this, getString(R.string.addressval), Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//
//                                    Toast.makeText(ClaimStolenTheft.this, theftCauseSpinner.getSelectedItem().toString() + " " + theftCauseSpinner.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();
//                                    if(theftCauseSpinner.getSelectedItemPosition() == 0){
//                                        Toast.makeText(ClaimStolenTheft.this, "Please select the circumstance of the loss to continue.", Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//
//                                    if (theftCauseSpinner.getSelectedItemPosition()== 3 && (comments.getText().toString().equals("") || comments.getText().toString().equals("null") || comments.getText().toString().equals(null))) {
//                                        Toast.makeText(ClaimStolenTheft.this, getString(R.string.comments), Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//                                    if (phototaken == "1" || isCarMissing) {
//                                        Intent car = new Intent(ClaimStolenTheft.this, ClaimVisualArtifacts.class);
//                                        startActivity(car);
//                                    } else {
//                                        Toast.makeText(ClaimStolenTheft.this, "Please capture the Stolen Part images", Toast.LENGTH_SHORT).show();
//                                        return;
//
//                                    }
//                                }
//                            }
//                        } else {
//                            if (comments.getText().toString() == "" || comments.getText().toString() == null || comments.getText().toString().length() == 0) {
//                                Toast.makeText(ClaimStolenTheft.this, getString(R.string.comments), Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            insertclaimfinal();
//                            //  startActivity(new Intent(ClaimStolenTheft.this, ClaimSuccess.class));
//                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            stolenimgclaim = (ImageView) findViewById(R.id.imagestolenclaim);
            try {
                stolenimgclaim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (permissionManager.hasPermissions(permissions)) {
                            optionvisual = 1;
                            dispatchTakePictureIntent();
                        } else {
                            permissionManager.requestPermissions(permissions);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            otherlocation.setOnCheckedChangeListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

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

    private void showCustomDialog() {
        // Create and set up the dialog
        final Dialog dialog = new Dialog(ClaimStolenTheft.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove title bar
        dialog.setContentView(R.layout.dialog_custom);
        dialog.setCancelable(false); // Dialog can be canceled by clicking outside

        // Get references to views inside the dialog
        Button sign_in_now_btn= dialog.findViewById(R.id.Sign_in_now_btn);
        Button sign_in_later_btn = dialog.findViewById(R.id.Sign_in_later_btn);
        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);

        // Set up the close button
        sign_in_later_btn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent car = new Intent(ClaimStolenTheft.this, ClaimSuccess.class);
            startActivity(car);
        });

        sign_in_now_btn.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(ClaimStolenTheft.this,Registration.class);
            intent.putExtra("ImageDeclaration",true);
            startActivity(intent);
        });

        // Show the dialog
        dialog.show();
    }
    private void showAlertToTheUser(String alertType) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ClaimStolenTheft.this);
        alert.setCancelable(false);
        alert.setTitle("Alert");
        alert.setMessage("Please enter the " + alertType);
        alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
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
                            String imeiInput = Settings.Secure.getString(ClaimStolenTheft.this.getContentResolver(), Settings.Secure.ANDROID_ID);
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
                                            Toast.makeText(ClaimStolenTheft.this, "Please contact administrator to proceed and try again", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                } else {
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                //insertclaimpdf();
                                                Intent car = new Intent(ClaimStolenTheft.this, ClaimSuccess.class);
                                                startActivity(car);

                                            }
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
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(ClaimStolenTheft.this);
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

                            } catch (final IOException ex) {
                                runOnUiThread(progressdialog::dismiss);
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
//                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        Toast.makeText(ClaimStolenTheft.this,
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
                                Toast.makeText(ClaimStolenTheft.this,
                                        ex.toString(), Toast.LENGTH_LONG).show();
//                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ClaimStolenTheft.this);
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

    private void dispatchTakePictureIntent() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
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
                phototaken = "1";
                Uri photoURI = FileProvider.getUriForFile(ClaimStolenTheft.this,
                        "com.aki.claimreporting.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if (optionvisual == 1) {
                    startActivityForResult(takePictureIntent, IMAGE_TAKE_CODE);
                }

            }
        } else {
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

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        if (requestCode == IMAGE_TAKE_CODE) {
            //NOTE CHANGED ADDED TRY CATCH AND HANDLED THE ERROR
            try {
                insurer = null;
                insurer = getBitmap(currentPhotoPath);
                bitmap = insurer;
                //ObjectDetectorActivity.Global.img = insurer;
                //String photopath = currentPhotoPath;
//            String encodedString = MainActivity.bitmapconverstion(insurer,getActivity());
//            String encodedString1 = encodedString;
//            insurer.recycle();
                mydb = new DatabaseHelper(ClaimStolenTheft.this);
                udiduniversal = UUID.randomUUID().toString();
                boolean Isinserted = mydb.insertclaimmoreimg(currentPhotoPath, "AdditionalInfo", udiduniversal);
                if (Isinserted == true) {
                    boolean test = Isinserted;
                    Log.i(null, "Insertion Done");
                } else {
                    boolean test = Isinserted;
                    Log.i(null, "Not Insertion Done");
                }
                SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                modeldoceditor.putString(CarView.doctypeid, "3a6ed13f-0cf3-4855-9378-9fa18781dafe");
                modeldoceditor.apply();
                // NOTE CHANGE
                if (insurer == null) {
                    if (attachimgist.size() == 0)
                        phototaken = "0";
                    return;
                }
                //insurer.recycle();
                uploadclaimimages(ClaimStolenTheft.this);
                getimageslist();
                // NOTE CHANGE END
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
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

    public void getimageslist() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressdialog = ProgressDialog.show(ClaimStolenTheft.this, "Loading", "Please wait...", true);
                }
            });
            mydb = new DatabaseHelper(ClaimStolenTheft.this);
            Cursor curseattach = mydb.getClaimImgmore();
            attachimglist = new ArrayList<>();
            if (curseattach.getCount() >= 1) {
                while (curseattach.moveToNext()) {
                    insurer = null;
                    insurer = getBitmap(curseattach.getString(0));
                    String encodedString = MainActivity.convertBitmap(insurer, ClaimStolenTheft.this);
                    insurer.recycle();
                    AdditionalAttachclaimlist element = new AdditionalAttachclaimlist(
                            encodedString, curseattach.getString(1)
                    );
                    attachimglist.add(new ViewPagerImageFragment(encodedString));
                    attachimgist.add(element);
                }
//                ClaimVehicleSelection.valuelistadpt = attachimgist.size();
//

//                    recyclerView = findViewById(R.id.recyclerstolenimageView);
//                    recyclerView.setVisibility(View.VISIBLE);
//                    mAdapter = new ClaimStolenImageAdapter(ClaimStolenTheft.this,attachimgist);
//                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(ClaimStolenTheft.this);
//                    mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//                    recyclerView.setLayoutManager(mLayoutManager);
//                    recyclerView.setHasFixedSize(true);
//                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                            mLayoutManager.getOrientation());
//                    recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
//                            DividerItemDecoration.VERTICAL));
//                    recyclerView.addItemDecoration(dividerItemDecoration);
//                    recyclerView.setItemAnimator(new DefaultItemAnimator());
//                    recyclerView.setAdapter(mAdapter);
                currentPhotoPath = "";
//                });
                runOnUiThread(() -> progressdialog.dismiss());
            } else {
                runOnUiThread(() -> progressdialog.dismiss());
            }
//            stolenPartsImageView = findViewById(R.id.stolenPartsImageViewer);
//            ViewPagerImageAdapter viewPagerImageAdapter = new ViewPagerImageAdapter(getSupportFragmentManager(), attachimgist,  dpToPixels(2, this));
//            ShadowTransformer fragmentCardShadowTransformer = new ShadowTransformer(stolenPartsImageView, viewPagerImageAdapter);
//            fragmentCardShadowTransformer.enableScaling(true);
//
//            stolenPartsImageView.setAdapter(viewPagerImageAdapter);
//            stolenPartsImageView.setPageTransformer(false, fragmentCardShadowTransformer);
//            stolenPartsImageView.setOffscreenPageLimit(3);

            stolenPartsImageView = findViewById(R.id.stolenPartsImageViewer);
            ViewPagerImageAdapter viewPagerImageAdapter = new ViewPagerImageAdapter(ClaimStolenTheft.this, attachimglist);
            stolenPartsImageView.setAdapter(viewPagerImageAdapter);

            stolenPartsImageView.setClipToPadding(false);
            stolenPartsImageView.setClipChildren(false);
            stolenPartsImageView.setOffscreenPageLimit(3);
            stolenPartsImageView.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

            // Code for carousel view animation in viewpager2.
            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
            compositePageTransformer.addTransformer(new MarginPageTransformer(40));
            compositePageTransformer.addTransformer((page, position) -> {
                float val = 1 - Math.abs(position);
                page.setScaleY(0.95f + val * 0.15f);
            });

            stolenPartsImageView.setPageTransformer(compositePageTransformer);
            TabLayout tabLayout = findViewById(R.id.tabDots);
            new TabLayoutMediator(tabLayout, stolenPartsImageView, (tab, position) -> {

            }).attach();

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);

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
        try {
            if (id == R.id.action_name) {

                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                View screenView = rootView.getRootView();
                screenView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
                screenView.setDrawingCacheEnabled(false);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
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
                Intent login = new Intent(ClaimStolenTheft.this, SupportTicket.class);
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
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onOptionItemSelected", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup.getId() == stolenInfo.getId()) {
            if (i == R.id.radioStolenInfo1) {
                claimaddrow.setVisibility(View.VISIBLE);
                isCarMissing = false;
                dividerAboveCaptureImage.setVisibility(View.VISIBLE);
                txtotherdescribe.setText("Additional Information/Description of theft");
                stolenlocationdescribe.setText("Where was the Location of Theft");
                otherReasonLayout.setVisibility(View.VISIBLE);
                theftCauseLayout.setVisibility(View.GONE);

            } else {
                isCarMissing = true;
                claimaddrow.setVisibility(View.GONE);
                dividerAboveCaptureImage.setVisibility(View.GONE);
                // NOTE CHANGE
                txtotherdescribe.setText("Additional Information of the stolen car");
                stolenlocationdescribe.setText("Where did the theft occur");
                // NOTE CHANGE END.
                otherReasonLayout.setVisibility(View.GONE);
                theftCauseLayout.setVisibility(View.VISIBLE);
            }
            locationLayout.setVisibility(View.VISIBLE);
            additionalInfoLayout.setVisibility(View.VISIBLE);
        } else if (i == R.id.radiootherstolenlocation1) {
            radioownlocation = false;
            addressmore.setVisibility(View.GONE);
        } else if (i == R.id.radiotherstolenlocation2) {
            radioownlocation = true;
            addressmore.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}