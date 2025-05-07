package com.aki.claimreporting;

import static com.aki.claimreporting.CertificateActivation.postURL;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA_AND_STORAGE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class Grievance extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private static final int IMAGE_TAKE_CODE = 1234;
    public static FirebaseCrashlytics mCrashlytics;
    public static boolean catpuredfakeimage;
    public static String reqidval;
    public static String regnotxtval;
    //List<GrievanceTypeInfo> grievencelist = new ArrayList<GrievanceTypeInfo>();
    public static EditText descrip, grieiencedate, regnoval;
    public static EditText txtotherinfo, txtphonnootherinfo, txtemailidotherinfo, txtofficeaddotherinfo;
    public static TextView username, userphonno;
    public static String insuranceid;
    public static String brokerid;
    public static String brokername;
    public static String grievencetypeid;
    public static String grievencetypename;
    final Calendar myCalendar = Calendar.getInstance();
    public ProgressDialog progressdialog;
    public RadioGroup radioagrivGroup;
    public RadioButton radiovgrivButton;
    public String dateid = "0";
    public String sdocumentType;
    public String stokenval, encryptedSHA;
    public boolean dateselectedornot;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    public Bitmap insurer;
    public String griviencedatevalue;
    public LinearLayout brokelin, otherinfolinear;
    public LinearLayout btnprcd;
    public String usernamdb, usernamdbfirst, usernamdblast;
    public String validfromgriv;
    public String insurancecompanyid;
    public String brokercompanyid;
    public String brokercompanyname;
    public int optionvisual = 0;
    public boolean directselect, brokerselect, otherselect;
    public ImageView imagefakecertTakePhoto, imgfakecertTuploadone;
    PermissionHandler permissionManager;
    String[] permissions = PERMISSION_CAMERA_AND_STORAGE;
    Activity activity;
    DatabaseHelper mydb;
    String currentPhotoPath;
    LinearLayout userNameLayout, userPhoneNumberLayout;
    List<String> insurcomspinner = new ArrayList<String>();
    List<InsuranceComInfo> insurnamelist = new ArrayList<InsuranceComInfo>();
    List<String> insurbrokpinner = new ArrayList<String>();
    List<BrokerInfo> insurbroklist = new ArrayList<BrokerInfo>();
    List<String> grivencespinner = new ArrayList<String>();
    Spinner insurSpinnerVal;

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
        setContentView(R.layout.activity_grievance);
        preventSizeChange(this, getSupportActionBar());
        dateselectedornot = false;
        getSupportActionBar().setTitle("Create Grievance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        activity = this;
        mCrashlytics = FirebaseCrashlytics.getInstance();
        directselect = false;
        brokerselect = false;
        otherselect = false;
        catpuredfakeimage = false;
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        //if(R.id. = )
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (adapterView.getId() == R.id.spinnerinsurancegrie) {//your code here
                String test = insurcomspinner.get(position);
                InsuranceComInfo insur = insurnamelist.get(position);
                insurancecompanyid = insur.getInsurerID();

                SharedPreferences sharedPreference = getSharedPreferences("InsuranceID", MODE_PRIVATE);
                //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreference.edit();
                editor.putString(insuranceid, insurancecompanyid);
                editor.commit();
                //                case R.id.spinnerbrokergrie:
//                    //your code here
//                    String testnew = insurbrokpinner.get(position);
//                    BrokerInfo brok = insurbroklist.get(position);
//                    brokercompanyid = brok.getBrokerID();
//                    brokercompanyname = brok.getBrokerName();
//                    brokerid = brok.getBrokerID();
//                    SharedPreferences broksharedPreference = getSharedPreferences("BrokerID", MODE_PRIVATE);
//                    SharedPreferences.Editor brokeditor = broksharedPreference.edit();
//                    brokeditor.putString(brokername, brokercompanyname);
//                    brokeditor.commit();
//                    break;
//                case R.id.spinnertypegreve:
//                    //your code here
//                    String grive = grivencespinner.get(position);
//                    GrievanceTypeInfo grivence = grievencelist.get(position);
//                    grievencetypeid = grivence.getGrievancetypeID();
//                    grievencetypename = grivence.getGrievancetypeName();
//                    break;
            }

//            if (view.getId() == R.id.spinnerinsurancegrie) {
//                // first spinner selected
//                String test = insurcomspinner.get(position);
//                InsuranceComInfo insur = insurnamelist.get(position);
//                insurancecompanyid = insur.getInsurerID();
//
//                SharedPreferences sharedPreference = getSharedPreferences("InsuranceID", MODE_PRIVATE);
//                //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreference.edit();
//                editor.putString(insuranceid, insurancecompanyid);
//                editor.commit();
//            }
//            else if(view.getId() == R.id.spinnerbrokergrie)
//            {
//                String testnew = insurbrokpinner.get(position);
//                BrokerInfo brok = insurbroklist.get(position);
//                brokercompanyid = brok.getBrokerID();
//                brokercompanyname = brok.getBrokerName();
//                brokerid = brok.getBrokerID();
//                SharedPreferences broksharedPreference = getSharedPreferences("BrokerID", MODE_PRIVATE);
//                SharedPreferences.Editor brokeditor = broksharedPreference.edit();
//                brokeditor.putString(brokername, brokercompanyname);
//                brokeditor.commit();
//            }
//            else if(view.getId() == R.id.spinnertypegreve)
//            {
//                String grive = grivencespinner.get(position);
//                GrievanceTypeInfo grivence = grievencelist.get(position);
//                grievencetypeid = grivence.getGrievancetypeID();
//                grievencetypename = grivence.getGrievancetypeName();
//
//            }


        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }

        //session.setvehicleid(testnew);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {

            insurSpinnerVal = (Spinner) findViewById(R.id.spinnerinsurancegrie);
            try {
                getuserprofile();
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            // getGrievancesType();
            try {
                getinsurancecompany();
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            // getbrokerlist();
//        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                  int dayOfMonth) {
//                // TODO Auto-generated method stub
//                if(dateid == "1"){
//
//                }
//            }
//
//        };

            imagefakecertTakePhoto = (ImageView) findViewById(R.id.imagefakecertTakePhoto);

            imgfakecertTuploadone = (ImageView) findViewById(R.id.imagefakecertuploadeddone);
            try {
                imagefakecertTakePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (permissionManager.hasPermissions(permissions)) {
                            if (regnoval.getText().toString().length() == 0) {
                                Toast.makeText(Grievance.this, getString(R.string.captregno), Toast.LENGTH_SHORT).show();
                            } else {
                                SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                                SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                                modeldoceditor.putString(CarView.doctypeid, "b9c6b305-e494-4430-9745-1aafcedb0a92");
                                modeldoceditor.commit();
                                optionvisual = 1;
                                dispatchTakePictureIntent();
                            }
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

            txtotherinfo = (EditText) findViewById(R.id.txtotherinterype);

            txtofficeaddotherinfo = (EditText) findViewById(R.id.txtgrivofficeaddressval);
            txtemailidotherinfo = (EditText) findViewById(R.id.txtgrivoemailval);
            txtphonnootherinfo = (EditText) findViewById(R.id.txtgrivophonenumbval);

            otherinfolinear = (LinearLayout) findViewById(R.id.otherinterinfolinear);
            otherinfolinear.setVisibility(View.GONE);
            //brokelin = (LinearLayout) findViewById(R.id.brokerinfolinear);
//        brokelin.setVisibility(View.GONE);
            radioagrivGroup = (RadioGroup) findViewById(R.id.grivradiogroup);
            try {
                radioagrivGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if (i == R.id.radioothegriv1) {// brokelin.setVisibility(View.GONE);
                            otherinfolinear.setVisibility(View.GONE);
                            directselect = true;
                            brokerselect = false;
                            otherselect = false;
                        } else if (i == R.id.radiothegriv3) {// brokelin.setVisibility(View.GONE);
                            otherinfolinear.setVisibility(View.VISIBLE);
                            directselect = false;
                            brokerselect = false;
                            otherselect = true;

                            // reportaccident.setVisibility(View.GONE);
                            //linearothervehile.setVisibility(View.GONE);
                        }


                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            userNameLayout = (LinearLayout) findViewById(R.id.lineardigital);
            userNameLayout.setVisibility(isUserSignedUp() ? View.VISIBLE : View.GONE);
            userPhoneNumberLayout = (LinearLayout) findViewById(R.id.userPhoneNumberLayout);
            userPhoneNumberLayout.setVisibility(isUserSignedUp() ? View.VISIBLE : View.GONE);
            username = (TextView) findViewById(R.id.editusername);
            userphonno = (TextView) findViewById(R.id.edituserphno);
            descrip = (EditText) findViewById(R.id.editdescri);
            regnoval = (EditText) findViewById(R.id.editregno);

//        mydb = new DatabaseHelper(RegistrationNotPhysical.this);
//        if(mydb.getUserdetails().getCount() != 0) {
//            Cursor curseattachtoken = mydb.getUserdetails();
//            int counttoken = curseattachtoken.getCount();
//            if (counttoken >= 1) {
//                while (curseattachtoken.moveToNext()) {
//                    try {
//                        usernamdbfirst = AESCrypt.decrypt(curseattachtoken.getString(1));
//                        usernamdblast = AESCrypt.decrypt(curseattachtoken.getString(2));
//                        usernamdb = usernamdbfirst + " " + usernamdblast;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        username.setText(usernamdb);
//        userphonno.setText("789345234");
            btnprcd = findViewById(R.id.Btnregistergrevience);

            try {
                btnprcd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        insertVehicleGrievances();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            grieiencedate = findViewById(R.id.editdategreive);

            try {
                grieiencedate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createDatePicker(view);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

//        grieiencedate.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
////                new DatePickerDialog(Grievance.this, date, myCalendar
////                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
////                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//
//            DatePickerDialog datePickerDialog=new DatePickerDialog(Grievance.this, date, myCalendar
//                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                    myCalendar.get(Calendar.DAY_OF_MONTH));
//
//            //following line to restrict future date selection
//            // datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//            dateid = "1";
//            datePickerDialog.show();
//
//
//            //dateselectedornot = true;
//        }
//    });

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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

    public byte[] audioconvert(String path) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while (-1 != (n = fis.read(buf)))
            bos.write(buf, 0, n);
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionManager.handleSettingsActivityResult(permissions, requestCode, resultCode);
        if (requestCode == IMAGE_TAKE_CODE) {
            //TODO: action
            if (getValidBitmap(currentPhotoPath) != null) {
                insurer = getBitmap(currentPhotoPath);
                //ObjectDetectorActivity.Global.img = insurer;
                catpuredfakeimage = true;
                pushfakecertimage();
                // bitmapconverstion(insurer);
                imgfakecertTuploadone.setVisibility(View.VISIBLE);
                //  MainActivity.uploadimages(RegistrationStep4.this);
            }


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

    public void pushfakecertimage() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
                    try {
                        mydb = new DatabaseHelper(Grievance.this);
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

                    Thread thread = new Thread(new Runnable() {

                        public void run() {

                            SharedPreferences locationPref = getSharedPreferences("LocationPref", Context.MODE_PRIVATE);

//                            SharedPreferences incidePref = activity.getSharedPreferences("CRAID",MODE_PRIVATE);
//                            String incident_id = incidePref.getString(ClaimRegFragment.CraIdval,"");
//                            SharedPreferences driverPref = activity.getSharedPreferences("DriverID",MODE_PRIVATE);
//                            String driver_id = driverPref.getString(PolicyInformation.DriverUniqueID,"");
//                            SharedPreferences certifPref = activity.getSharedPreferences("ClaimInsert",MODE_PRIVATE);
//                            String certnum = certifPref.getString(ClaimRegFragment.CertificateID,"");
//                            String vehicrefid = certifPref.getString(ClaimRegFragment.Vechilerefid,"");
//                            //MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/AddClaim";
//                            SharedPreferences incitype = getSharedPreferences("IncidentType",MODE_PRIVATE);
//                            String incidenttypeval = incitype.getString(ClaimRegFragment.typeidincident,"");

                            Bitmap bitmapnew = insurer;
                            ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
                            bitmapnew.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
                            // String outStr = ObjectDetectorActivity.Global.outputStr;
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
                            sdocumentType = "36d07b7e-b4c0-4b18-9832-4e5f5e99d08b";
                            postURL = activity.getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
                            RequestBody body = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("fileName", "GrivienceImg.jpg")
                                    .addFormDataPart(
                                            "image", "GrivienceImg.jpg",
                                            RequestBody.create(mediaType, byteArraynew))
                                    .addFormDataPart("documentType", sdocumentType)
                                    .addFormDataPart("certificateNo", "")
                                    .addFormDataPart("incidentUniqueCode", "")
                                    .addFormDataPart("VehicleRegistrationNumber", regnoval.getText().toString())
                                    .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                    .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                    .addFormDataPart("captureAttachmentID", uniqueID)
                                    .addFormDataPart("attachmentTypeID", sdocumentType)
                                    .addFormDataPart("captureDateTime", dateTime)
                                    .build();
                            Request request = new Request.Builder()
                                    .url(postURL)
                                    .method("POST", body)
                                    .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                    .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
                                    .build();
                            Response staticResponse = null;
                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog = ProgressDialog.show(Grievance.this, "Loading", "Please wait...", true);
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
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
                                                AlertDialog.Builder alert = new AlertDialog.Builder(Grievance.this);
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
                            } catch (final IOException | JSONException ex) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog.dismiss();
                                    }
                                });
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                            }

                        }
                    });
                    thread.start();


                } else {
                    try {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Grievance.this);
                        dialog.setMessage(getString(R.string.location_not_enabled));
                        dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //this will navigate user to the device location settings screen
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                        android.app.AlertDialog alert = dialog.create();
                        alert.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }


            } else {
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }


        } catch (Exception ex) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressdialog.dismiss();
                }
            });
            // TODO Auto-generated catch block
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
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

    public void getuserprofile() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    mydb = new DatabaseHelper(Grievance.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                                // stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w";
                                //stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w";
                            }
                        }
                    }
                    //stokenval ="eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6ImEzYTBmYzE5LTllYmUtNGIxMS05ZWRlLTY4NjA3MjgzYTg2OSIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiOWQxYTQ2YjUtY2NkNy00ZWMwLTljYjItMTJhM2IxZWUwNjk0IiwiUHJpbWFyeUdyb3VwU2lkIjoiYTNhMGZjMTktOWViZS00YjExLTllZGUtNjg2MDcyODNhODY5IiwibmJmIjoxNjEyODUzMDEwLCJleHAiOjE2NDM5NTcwMTAsImlhdCI6MTYxMjg1MzAxMCwiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.0z1FEMyukKGtjj8HTMQWb1rP5yWXXbWU7VVf5PojfGyUHmZTszwbCvNnYhACVQCJz5U1NieW0tXxxjOC_HtM9ST5_D3PSUS4KWYOtAS99VMWu8zeJMRIT-DugTByimWacw3tNfUdJZYhDB1tp-ym7dOWT5EPXKDGSZyyR9gru0dXTpDO3wma8-dZRt1zMd6Nmil9idn1SaP37d_ohIgiB0_vRdDkH3l2C6fKfvDusKlNyrr4LYITe0FzdPhil4r6xHC03aegxypDjqdBI4BeFAZjR_UMJeXtmPSnmAyeINrOwCC8p7ZHe2td8c6ok_JcuZ1Usw86Hg4N1VfAUExQuUcSf5f_j8Kyy1u8jwIw9fk1pzSo6kVlf9F0z1MYamfbKNJsC6wfooFK0TMEYqUxL8OlFeUX1K0a7Ba9v52qQh-o7-NMrhE4Sy8w1ZtjyreHSlavDrzoK-C82iZ-CWx218EBm5T1hsX-s842AwkIhXtcjJAj5H8_fly9lxqXhZiiCbLoKjnbOEvic9MdFFbPjkbV3GSnM_-5Gbm9KNAyAxsk8HL-Do-Mw1i0rGHRxqupWEdUg2lWv6Vk5im_n5gv2T0tg6vRcFUvGLz_9Yv-abnhO0Km-HTLmevV2ScMiNKhdGJLPQleu9CwBRQa80PMMUeb_6bmequ1OinipEsrT6Y";
                    // stokenval ="eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6ImEzYTBmYzE5LTllYmUtNGIxMS05ZWRlLTY4NjA3MjgzYTg2OSIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiOWQxYTQ2YjUtY2NkNy00ZWMwLTljYjItMTJhM2IxZWUwNjk0IiwiUHJpbWFyeUdyb3VwU2lkIjoiYTNhMGZjMTktOWViZS00YjExLTllZGUtNjg2MDcyODNhODY5IiwibmJmIjoxNjEyODUzMDEwLCJleHAiOjE2NDM5NTcwMTAsImlhdCI6MTYxMjg1MzAxMCwiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.0z1FEMyukKGtjj8HTMQWb1rP5yWXXbWU7VVf5PojfGyUHmZTszwbCvNnYhACVQCJz5U1NieW0tXxxjOC_HtM9ST5_D3PSUS4KWYOtAS99VMWu8zeJMRIT-DugTByimWacw3tNfUdJZYhDB1tp-ym7dOWT5EPXKDGSZyyR9gru0dXTpDO3wma8-dZRt1zMd6Nmil9idn1SaP37d_ohIgiB0_vRdDkH3l2C6fKfvDusKlNyrr4LYITe0FzdPhil4r6xHC03aegxypDjqdBI4BeFAZjR_UMJeXtmPSnmAyeINrOwCC8p7ZHe2td8c6ok_JcuZ1Usw86Hg4N1VfAUExQuUcSf5f_j8Kyy1u8jwIw9fk1pzSo6kVlf9F0z1MYamfbKNJsC6wfooFK0TMEYqUxL8OlFeUX1K0a7Ba9v52qQh-o7-NMrhE4Sy8w1ZtjyreHSlavDrzoK-C82iZ-CWx218EBm5T1hsX-s842AwkIhXtcjJAj5H8_fly9lxqXhZiiCbLoKjnbOEvic9MdFFbPjkbV3GSnM_-5Gbm9KNAyAxsk8HL-Do-Mw1i0rGHRxqupWEdUg2lWv6Vk5im_n5gv2T0tg6vRcFUvGLz_9Yv-abnhO0Km-HTLmevV2ScMiNKhdGJLPQleu9CwBRQa80PMMUeb_6bmequ1OinipEsrT6Y";
                    progressdialog = new ProgressDialog(Grievance.this);
                    encryptedSHA = "";
                    String sourceStr = MainActivity.InsertMobileParameters();
                    try {
                        encryptedSHA = AESCrypt.encrypt(sourceStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }

                    try {
                        Thread thread = new Thread(new Runnable() {

                            public void run() {
                                postURL = getString(R.string.uaturl) + "/app/UAD/GetUserProfile";
                                final MediaType JSON
                                        = MediaType.parse("application/json; charset=utf-8");
                                OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(120, TimeUnit.SECONDS)
                                        .writeTimeout(120, TimeUnit.SECONDS)
                                        .readTimeout(120, TimeUnit.SECONDS)
                                        .build();
                                JsonObject Details = new JsonObject();
                                String insertString = Details.toString();
                                RequestBody body = RequestBody.create(JSON, insertString);
                                Request request = new Request.Builder()
                                        .url(postURL)
                                        .header("Authorization", "Bearer " + stokenval)
                                        .header("MobileParameter", MainActivity.InsertMobileParameters())
                                        .post(body)
                                        .build();
                                Response staticResponse = null;

                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog = ProgressDialog.show(Grievance.this, "Loading", "Please wait...", true);
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
                                        final JSONObject userResponselist = staticJsonObj.getJSONObject("rObj").getJSONObject("getUserProfile");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();

                                                try {
                                                    username.setText(AESCrypt.decrypt(userResponselist.getString("firstName")));
                                                    String formattedNumber = String.format("%s %s %s", AESCrypt.decrypt(userResponselist.getString("mobileNo")).subSequence(0, 3), AESCrypt.decrypt(userResponselist.getString("mobileNo")).subSequence(3, 6), AESCrypt.decrypt(userResponselist.getString("mobileNo")).subSequence(6, 9));
                                                    userphonno.setText("+254 " + formattedNumber);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }

                                            }
                                        });


                                    } else if (staticJsonObj.getInt("rcode") == 401) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                unauthorize(Grievance.this);
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
                                                    progressdialog.dismiss();
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(Grievance.this);
                                                    alert.setCancelable(false);
                                                    alert.setMessage(errorText);
                                                    alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                                    alert.show();
                                                } catch (JSONException e) {
                                                    progressdialog.dismiss();
                                                    e.printStackTrace();
                                                }
                                            });
                                        } catch (Exception e) {
                                            progressdialog.dismiss();
                                            e.printStackTrace();
                                        }

                                    }
                                } catch (final Exception ex) {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        ex.printStackTrace();
                                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                        mCrashlytics.recordException(ex);
                                        Toast.makeText(Grievance.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                    });
                                }
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
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Grievance.this);
                        dialog.setMessage(getString(R.string.location_not_enabled));
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }

            } else {
                Toast.makeText(Grievance.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            //progressdialog.dismiss();
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(Grievance.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        radiovgrivButton = (RadioButton) findViewById(checkedId);
        if (checkedId == R.id.radioothegriv1) {

        }
//        else if(checkedId == R.id.radiothegriv2)
//        {
//
//        }
    }


    /*public void unauthorize() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Grievance.this);
            dialog.setMessage(getString(R.string.unauthMessge));
            dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mydb = new DatabaseHelper(Grievance.this);
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
                    Intent login = new Intent(Grievance.this, Registration.class);
                    startActivity(login);
                }
            });
            dialog.setCancelable(false);
            android.app.AlertDialog alert = dialog.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }*/


    private void updatedateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        System.out.println(sdf.format(myCalendar.getTime()));
        grieiencedate = findViewById(R.id.editdategreive);
        grieiencedate.setText(sdf.format(myCalendar.getTime()));
        griviencedatevalue = sdf.format(myCalendar.getTime());
        dateselectedornot = true;
    }

    public void getinsurancecompany() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        AsyncTask.execute(

                new Runnable() {

                    @Override
                    public void run() {

                        try {
                            if (isNetworkConnected()) {

                                if (checkGPSStatus()) {

                                   /* mydb = new DatabaseHelper(Grievance.this);
                                    if (mydb.getTokendetails().getCount() != 0) {
                                        Cursor curseattachtoken = mydb.getTokendetails();
                                        int counttoken = curseattachtoken.getCount();
                                        if (counttoken >= 1) {
                                            while (curseattachtoken.moveToNext()) {
                                                MainActivity.stokenval = curseattachtoken.getString(1);
                                            }
                                        }
                                    }*/
                                    mydb = new DatabaseHelper(Grievance.this);
                                    Cursor curseattachtoken = mydb.getTokendetails();
                                    try {
                                        if (curseattachtoken != null && curseattachtoken.getCount() > 0) {
                                            while (curseattachtoken.moveToNext()) {
                                                MainActivity.stokenval = curseattachtoken.getString(1);
                                            }
                                        }
                                    } finally {
                                        if (curseattachtoken != null) {
                                            curseattachtoken.close(); // Ensure the cursor is closed after use
                                        }
                                    }

                                    encryptedSHA = "";
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
                                            postURL = getString(R.string.uaturl) + "/app/MasterData/GetAllinsurer";
                                            final MediaType JSON
                                                    = MediaType.parse("application/json; charset=utf-8");

                                            OkHttpClient client = new OkHttpClient.Builder()
                                                    .connectTimeout(120, TimeUnit.SECONDS)
                                                    .writeTimeout(120, TimeUnit.SECONDS)
                                                    .readTimeout(120, TimeUnit.SECONDS)
                                                    .build();
                                            JsonObject Details = new JsonObject();
//                            Details.addProperty("certificateNo",sfirstname);
//                            Details.addProperty("policyNo",slastname);
//                            Details.addProperty("certificateType",spassportNo);
//                            Details.addProperty("coverageType",spassportvalfmdt);
//                            Details.addProperty("policyBeginDate",spassportvalenddt);
//                            Details.addProperty("policyEndDate",spassportcountry);
//                            Details.addProperty("registrationNo",snationalID);
//                            Details.addProperty("vINNumber",snationalID);
//                            Details.addProperty("make",snationalID);
//                            Details.addProperty("model",snationalID);
//                            Details.addProperty("yearOfMfg",snationalID);
//                            Details.addProperty("insurerID",snationalID);
//                            Details.addProperty("insuredName",snationalID);
//                            Details.addProperty("insuredNationalID",snationalID);
//                            Details.addProperty("insuredPIN",snationalID);
//                            Details.addProperty("insurerName",snationalID);
//                            Details.addProperty("prevCertificateNo",snationalID);
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
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
//                                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
//                                                    }
//                                                });
                                                staticResponse = client.newCall(request).execute();
                                                String staticRes = staticResponse.body().string();
                                                Log.i(null, staticRes);
                                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                                if (staticJsonObj.getInt("rcode") == 1) {
                                                    JSONArray insuranceResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllinsurer");
                                                    for (int i = 0; i < insuranceResponseList.length(); i++) {
                                                        JSONObject insurObj = insuranceResponseList.getJSONObject(i);
                                                        InsuranceComInfo element = new InsuranceComInfo(
                                                                insurObj.getString("insurerID"),
                                                                insurObj.getInt("dMVICMemberCompanyID"),
                                                                insurObj.getInt("iMIDSMemberCompanyID"),
                                                                insurObj.getString("insurerName")
                                                        );
                                                        insurcomspinner.add(insurObj.getString("insurerName"));
                                                        insurnamelist.add(element);
                                                    }
                                                    initializeArrayAdapter();

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            progressdialog.dismiss();
                                                        }
                                                    });
//                                                    runOnUiThread(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            progressdialog.dismiss();
//                                                        }
//                                                    });
//                                                    Intent redirect = new Intent(RegistrationStep1.this, RegistrationStep4.class);
//                                                    startActivity(redirect);
                                                }
//                                else if(staticJsonObj.getInt("rcode") == 2)
//                                {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progressdialog.dismiss();
//                                            userexist();
//
//                                        }
//                                    });
//                                }
                                                else {
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
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(Grievance.this);
                                                                alert.setCancelable(false);
                                                                alert.setMessage(errorText);
                                                                alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                                                                alert.show();
                                                            } catch (JSONException e) {
                                                                runOnUiThread(progressdialog::dismiss);
                                                                e.printStackTrace();
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        runOnUiThread(progressdialog::dismiss);
                                                        e.printStackTrace();
                                                    }

                                                }
                                            } catch (final IOException ex) {

                                                // progressdialog.dismiss();
                                                ex.printStackTrace();
                                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                                mCrashlytics.recordException(ex);
                                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                                runOnUiThread(new Runnable() {
                                                    public void run() {

                                                        Toast.makeText(Grievance.this,
                                                                ex.toString(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            } catch (JSONException ex) {
                                                //   progressdialog.dismiss();
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                                ex.printStackTrace();
                                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                                mCrashlytics.recordException(ex);
//                                                Toast.makeText(RegistrationStep1.this,
//                                                        ex.toString(), Toast.LENGTH_LONG).show();
                                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                                            }
                                        }
                                    });
                                    thread.start();


                                } else {
                                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Grievance.this);
                                    dialog.setMessage(getString(R.string.location_not_enabled));
                                    dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {

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
//                                Toast.makeText(this,getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            //progressdialog.dismiss();
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
                            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                        }

                    }
                });
    }

    private void initializeArrayAdapter() {
        try {
            runOnUiThread(() -> {
                if(progressdialog.isShowing()){
                    progressdialog.dismiss();
                }
                ArrayAdapter insunameAdapter = new ArrayAdapter(Grievance.this, android.R.layout.simple_list_item_1, insurcomspinner);
                insurSpinnerVal.setAdapter(insunameAdapter);
                insurSpinnerVal.setOnItemSelectedListener(Grievance.this);

            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    public void getGrievancesType()
//    {
//        AsyncTask.execute(
//
//                new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        try {
//                            if(isNetworkConnected()) {
//
//                                if (checkGPSStatus())
//                                {
//
//                                    mydb = new DatabaseHelper(RegistrationNotPhysical.this);
//                                    if(mydb.getTokendetails().getCount() != 0) {
//                                        Cursor curseattachtoken = mydb.getTokendetails();
//                                        int counttoken = curseattachtoken.getCount();
//                                        if (counttoken >= 1) {
//                                            while (curseattachtoken.moveToNext()) {
//                                                MainActivity.stokenval = curseattachtoken.getString(1);
//                                            }
//                                        }
//                                    }
//
//                                    MainActivity.encryptedSHA = "";
//                                    String sourceStr = MainActivity.InsertMobileparameters();
//                                    try {
//                                        MainActivity.encryptedSHA = AESUtils.encrypt(sourceStr);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    Thread thread = new Thread(new Runnable() {
//
//                                        public void run() {
//                                            MainActivity.postURL = getString(R.string.uaturl) + "/app/MasterData/GetAllGrievancesType";
//                                            final MediaType JSON
//                                                    = MediaType.parse("application/json; charset=utf-8");
//
//                                            OkHttpClient client = new OkHttpClient.Builder()
//                                                    .connectTimeout(120, TimeUnit.SECONDS)
//                                                    .writeTimeout(120, TimeUnit.SECONDS)
//                                                    .readTimeout(120, TimeUnit.SECONDS)
//                                                    .build();
//                                            JsonObject Details = new JsonObject();
//                                            String insertString = Details.toString();
//                                            RequestBody body = RequestBody.create(JSON, insertString);
//                                            Request request = new Request.Builder()
//                                                    .url(MainActivity.postURL)
//                                                    .header("Authorization", "Bearer " +MainActivity.stokenval)
//                                                    .header("MobileParameter", MainActivity.InsertMobileparameters())
//                                                    .post(body)
//                                                    .build();
//                                            Response staticResponse = null;
//
//                                            try {
////                                                runOnUiThread(new Runnable() {
////                                                    @Override
////                                                    public void run() {
////                                                        progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
////                                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
////                                                    }
////                                                });
//                                                staticResponse = client.newCall(request).execute();
//                                                String staticRes = staticResponse.body().string();
//                                                Log.i(null,staticRes);
//                                                final JSONObject staticJsonObj = new JSONObject(staticRes);
//                                                if (staticJsonObj.getInt("rcode") == 1)
//                                                {
//                                                    JSONArray griveanceResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllgrievancesType");
//                                                    for(int i=0; i<griveanceResponseList.length();i++) {
//                                                        JSONObject grivObj = griveanceResponseList.getJSONObject(i);
//                                                        GrievanceTypeInfo element = new GrievanceTypeInfo(
//                                                                grivObj.getString("grievancesTypeID"),
//                                                                grivObj.getString("grievancesType")
//                                                        );
//                                                        grivencespinner.add(grivObj.getString("grievancesType"));
//                                                        grievencelist.add(element);
//                                                    }
//                                                    runOnUiThread(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            Spinner grivSpinnerVal = (Spinner) findViewById(R.id.spinnertypegreve);
//                                                            grivSpinnerVal.setOnItemSelectedListener(RegistrationNotPhysical.this);
//                                                            ArrayAdapter griveAdapter = new ArrayAdapter(RegistrationNotPhysical.this,android.R.layout.simple_list_item_1,grivencespinner);
//                                                            grivSpinnerVal.setAdapter(griveAdapter);
//
//                                                        }
//                                                    });
////                                                    runOnUiThread(new Runnable() {
////                                                        @Override
////                                                        public void run() {
////                                                            progressdialog.dismiss();
////                                                        }
////                                                    });
////                                                    Intent redirect = new Intent(RegistrationStep1.this, RegistrationStep4.class);
////                                                    startActivity(redirect);
//                                                }
////                                else if(staticJsonObj.getInt("rcode") == 2)
////                                {
////                                    runOnUiThread(new Runnable() {
////                                        @Override
////                                        public void run() {
////                                            progressdialog.dismiss();
////                                            userexist();
////
////                                        }
////                                    });
////                                }
//                                                else
//                                                {
//                                                    runOnUiThread(new Runnable() {
//                                                        public void run() {
//                                                            // progressdialog.dismiss();
//                                                            try {
//                                                                Toast.makeText(RegistrationNotPhysical.this, staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText") , Toast.LENGTH_SHORT).show();
//                                                            } catch (JSONException e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                            return;
//                                                        }
//                                                    });
//                                                }
//                                            } catch (final IOException ex)
//                                            {
//                                                // progressdialog.dismiss();
//                                                ex.printStackTrace();
//                                                mCrashlytics.recordException(ex);
//                                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//
//                                                runOnUiThread(new Runnable() {
//                                                    public void run() {
//
//                                                        Toast.makeText(RegistrationNotPhysical.this,
//                                                                ex.toString(), Toast.LENGTH_LONG).show();
//                                                    }
//                                                });
//                                            } catch (JSONException ex)
//                                            {
//                                                //   progressdialog.dismiss();
////                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
////                                startActivity(redirect);
//                                                ex.printStackTrace();
//                                                mCrashlytics.recordException(ex);
////                                                Toast.makeText(RegistrationStep1.this,
////                                                        ex.toString(), Toast.LENGTH_LONG).show();
//                                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//                                            }
//                                        }
//                                    });
//                                    thread.start();
//
//
//
//                                } else
//                                {
//                                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(RegistrationNotPhysical.this);
//                                    dialog.setMessage(getString(R.string.location_not_enabled));
//                                    dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            //this will navigate user to the device location settings screen
//                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                            startActivity(intent);
//                                        }
//                                    });
//                                    android.app.AlertDialog alert = dialog.create();
//                                    alert.show();
//                                }
//
//
//                            }
//                            else
//                            {
////                                Toast.makeText(this,getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        catch (Exception ex)
//                        {
//                            //progressdialog.dismiss();
//                            ex.getStackTrace();
//                            mCrashlytics.recordException(ex);
//                            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//
//                        }
//
//                    }
//                });
//    }

    public void getbrokerlist() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        AsyncTask.execute(

                new Runnable() {

                    @Override
                    public void run() {

                        try {
                            if (isNetworkConnected()) {

                                if (checkGPSStatus()) {

                                    mydb = new DatabaseHelper(Grievance.this);
                                    if (mydb.getTokendetails().getCount() != 0) {
                                        Cursor curseattachtoken = mydb.getTokendetails();
                                        int counttoken = curseattachtoken.getCount();
                                        if (counttoken >= 1) {
                                            while (curseattachtoken.moveToNext()) {
                                                MainActivity.stokenval = curseattachtoken.getString(1);
                                            }
                                        }
                                    }

                                    encryptedSHA = "";
                                    String sourceStr = MainActivity.InsertMobileParameters();
                                    try {
                                        encryptedSHA = AESCrypt.encrypt(sourceStr);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        Thread thread = new Thread(new Runnable() {

                                            public void run() {
                                                postURL = getString(R.string.uaturl) + "/app/MasterData/GetAllBroker";
                                                final MediaType JSON
                                                        = MediaType.parse("application/json; charset=utf-8");

                                                OkHttpClient client = new OkHttpClient.Builder()
                                                        .connectTimeout(120, TimeUnit.SECONDS)
                                                        .writeTimeout(120, TimeUnit.SECONDS)
                                                        .readTimeout(120, TimeUnit.SECONDS)
                                                        .build();
                                                JsonObject Details = new JsonObject();
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
//
                                                    staticResponse = client.newCall(request).execute();
                                                    String staticRes = staticResponse.body().string();
                                                    Log.i(null, staticRes);
                                                    final JSONObject staticJsonObj = new JSONObject(staticRes);
                                                    if (staticJsonObj.getInt("rcode") == 1) {
                                                        JSONArray insuranceResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("betAllBroker");
                                                        for (int i = 0; i < insuranceResponseList.length(); i++) {
                                                            JSONObject insurObj = insuranceResponseList.getJSONObject(i);
                                                            BrokerInfo element = new BrokerInfo(
                                                                    insurObj.getString("brokerEntityID"),
                                                                    insurObj.getString("brokerEntity")
                                                            );
                                                            insurbrokpinner.add(insurObj.getString("brokerEntity"));
                                                            insurbroklist.add(element);
                                                        }
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
//                                                            Spinner insurSpinnerVal = (Spinner) findViewById(R.id.spinnerbrokergrie);
//                                                            insurSpinnerVal.setOnItemSelectedListener(RegistrationNotPhysical.this);
//                                                            ArrayAdapter insunameAdapter = new ArrayAdapter(RegistrationNotPhysical.this,android.R.layout.simple_list_item_1,insurbrokpinner);
//                                                            insurSpinnerVal.setAdapter(insunameAdapter);

                                                            }
                                                        });
//                                                    runOnUiThread(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            progressdialog.dismiss();
//                                                        }
//                                                    });
//                                                    Intent redirect = new Intent(RegistrationStep1.this, RegistrationStep4.class);
//                                                    startActivity(redirect);
                                                    }
//                                else if(staticJsonObj.getInt("rcode") == 2)
//                                {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progressdialog.dismiss();
//                                            userexist();
//
//                                        }
//                                    });
//                                }
                                                    else {
                                                        try {
                                                            runOnUiThread(progressdialog::dismiss);
                                                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                            JSONObject index = rmsg.getJSONObject(0);
                                                            runOnUiThread(() -> {
                                                                String errorText;
                                                                try {
                                                                    errorText = index.getString("errorText");
                                                                    AlertDialog.Builder alert = new AlertDialog.Builder(Grievance.this);
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
                                                } catch (final IOException ex) {
                                                    // progressdialog.dismiss();
                                                    ex.printStackTrace();
                                                    mCrashlytics.recordException(ex);
                                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                                    runOnUiThread(new Runnable() {
                                                        public void run() {

                                                            Toast.makeText(Grievance.this,
                                                                    ex.toString(), Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                } catch (JSONException ex) {
                                                    //   progressdialog.dismiss();
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                                    ex.printStackTrace();
                                                    mCrashlytics.recordException(ex);
//                                                Toast.makeText(RegistrationStep1.this,
//                                                        ex.toString(), Toast.LENGTH_LONG).show();
                                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                                                }
                                            }
                                        });
                                        thread.start();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                } else {
                                    try {
                                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Grievance.this);
                                        dialog.setMessage(getString(R.string.location_not_enabled));
                                        dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //this will navigate user to the device location settings screen
                                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                startActivity(intent);
                                            }
                                        });
                                        android.app.AlertDialog alert = dialog.create();
                                        alert.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }


                            } else {
//                                Toast.makeText(this,getString(R.string.nonetwork) , Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            //progressdialog.dismiss();
                            ex.getStackTrace();
                            mCrashlytics.recordException(ex);
                            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                        }

                    }
                });
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

    public void insertVehicleGrievances() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
                    regnotxtval = regnoval.getText().toString();
                    String tstval = regnotxtval;
                    String tstval1 = tstval;

                    if (regnotxtval == "" || regnotxtval == null || regnotxtval == "null" || regnotxtval.length() == 0) {
                        Toast.makeText(Grievance.this, getString(R.string.regno), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (otherselect) {

                        if (txtotherinfo.getText().toString() == "" || txtotherinfo.getText().toString() == null || txtotherinfo.getText().toString() == "null" || txtotherinfo.getText().toString().length() == 0) {
                            Toast.makeText(Grievance.this, getString(R.string.grivmintname), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (txtofficeaddotherinfo.getText().toString().length() == 0 && txtemailidotherinfo.getText().toString().length() == 0 && txtphonnootherinfo.getText().toString().length() == 0) {
                            Toast.makeText(Grievance.this, getString(R.string.grivmanda), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (txtemailidotherinfo.getText().toString() == "" || txtemailidotherinfo.getText().toString() == null || txtemailidotherinfo.getText().toString().length() == 0) {
                        } else {

                            String emailInput = txtemailidotherinfo.getText().toString().trim();
                            String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                            if (emailInput.matches(emailPattern)) {
                            } else {
                                Toast.makeText(Grievance.this, getString(R.string.invalidemaild), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (txtphonnootherinfo.getText().toString().length() != 0) {
                            if (txtphonnootherinfo.getText().toString().length() != 9) {
                                Toast.makeText(Grievance.this, getString(R.string.validphoneno), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                    }
                    if (dateselectedornot == false) {
                        Toast.makeText(Grievance.this, getString(R.string.dategrivience), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mydb = new DatabaseHelper(Grievance.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                MainActivity.stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                    SharedPreferences modelPref = getSharedPreferences("QrCodePref", Context.MODE_PRIVATE);
                    final String qrcodeval = modelPref.getString(ScanCertificate.QrCode, null);
                    encryptedSHA = "";
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
                            postURL = getString(R.string.uaturl) + "/app/Grievances/AddVehicleGrievances";
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");
                            SimpleDateFormat formatstart = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                            try {
                                Date datefromgriv = formatstart.parse(grieiencedate.getText().toString());
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
                                validfromgriv = formatter.format(Date.parse(String.valueOf(datefromgriv)));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                            }
                            SharedPreferences insurid = getSharedPreferences("InsuranceID", MODE_PRIVATE);
                            String insuridval = insurid.getString(insuranceid, "");
                            SharedPreferences brokerinfo = getSharedPreferences("BrokerID", MODE_PRIVATE);
                            String brokerameval = brokerinfo.getString(brokername, "");
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
//                            JSONParser parser = new JSONParser();
//                            JSONObject json = (JSONObject) parser.parse(stringToParse);
//                            Read more: https://www.java67.com/2016/10/3-ways-to-convert-string-to-json-object-in-java.html#ixzz6kjiE2TFK
                            JsonObject CertInputJson = new JsonObject();
                            JsonObject Details = new JsonObject();
                            Details.addProperty("vehicleregistrationnumber", regnotxtval);
                            Details.addProperty("expirydate", CertificateActivation.RegistrationGlobal.inputexpirydate);
                            Details.addProperty("printcode", CertificateActivation.RegistrationGlobal.inputprintcode);
                            String insertString = Details.toString();
                            JsonObject Details1 = new JsonObject();
                            Details1.addProperty("vehicleregistrationnumber", regnotxtval);
                            Details1.addProperty("expirydate", CertificateActivation.RegistrationGlobal.outputexpirydate);
                            Details1.addProperty("printcode", CertificateActivation.RegistrationGlobal.outputprintcode);
                            String insertString1 = Details1.toString();
                            CertInputJson.add("CertificatesInputJson", Details);
                            CertInputJson.add("CertificatesOutputJson", Details1);
                            CertInputJson.addProperty("dateOfInsurance", validfromgriv);
                            CertInputJson.addProperty("Content", descrip.getText().toString());
                            CertInputJson.addProperty("grievancesTypeID", "57845ce9-07ed-451a-af5e-0a61219c0801");
                            CertInputJson.addProperty("grievancesType", "Invalid");

                            if (directselect) {
                                CertInputJson.addProperty("targetEntityID", insuridval);

                            }
//                            else if (brokerselect)
//                            {
//                                CertInputJson.addProperty("targetEntityID",insuridval);
//                                CertInputJson.addProperty("brokerEntityID",brokerid);
//                                CertInputJson.addProperty("brokerEntityName",brokerameval);
//                            }
                            else {
                                CertInputJson.addProperty("targetEntityID", insuridval);
                                CertInputJson.addProperty("brokerEntityID", "0cef7857-3370-4267-9a6e-b0f9572dafe5");
                                CertInputJson.addProperty("brokerEntityName", txtotherinfo.getText().toString());
                                CertInputJson.addProperty("officeAddress", txtofficeaddotherinfo.getText().toString());
                                CertInputJson.addProperty("brokerEmailID", txtemailidotherinfo.getText().toString());
                                CertInputJson.addProperty("brokerPhoneno", txtphonnootherinfo.getText().toString());
                            }


                            CertInputJson.addProperty("integrationTransactionRefNo", CertificateActivation.reqidval);
                            String insertString3 = CertInputJson.toString();
                            String insertString4 = insertString3;
                            RequestBody body = RequestBody.create(JSON, insertString3);
                            Request request = new Request.Builder()
                                    .url(postURL)
                                    .header("MobileParameter", MainActivity.InsertMobileParameters())
                                    .header("Authorization", "Bearer " + MainActivity.stokenval)
                                    .post(body)
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
                                Log.i(null, staticRes);
                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                int errorcode = staticJsonObj.getInt("rcode");
                                if (staticJsonObj.getInt("rcode") == 1) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            progressdialog.dismiss();
                                            String valuid = "";
                                            try {
                                                valuid = staticJsonObj.getJSONObject("rObj").getString("GrievancesId");
                                                successVehicleGrievances(valuid);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
//                                    Intent login = new Intent(RegistrationNotPhysical.this, HomePage.class);
//                                    startActivity(login);
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
                                                AlertDialog.Builder alert = new AlertDialog.Builder(Grievance.this);
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
                            } catch (final IOException ex) {
                                progressdialog.dismiss();
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                runOnUiThread(new Runnable() {
                                    public void run() {

                                        Toast.makeText(Grievance.this,
                                                ex.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (JSONException ex) {
                                progressdialog.dismiss();
//                                Intent redirect = new Intent(Grievance.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                        }
                    });
                    thread.start();


                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Grievance.this);
                    dialog.setMessage(getString(R.string.location_not_enabled));
                    dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alert = dialog.create();
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
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }
    }

    public void successVehicleGrievances(String grivienceid) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Grievance.this);
            //dialog.setMessage("Do you like to port the data to this new device?");
            dialog.setMessage(getString(R.string.SuccessGrievanceMessage) + grivienceid);
            dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent login = new Intent(Grievance.this, Dashboard.class);
                    startActivity(login);
                }
            });
//        dialog.setNegativeButton("No(Let me correct the Info)", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //this will navigate user to the device location settings screen
//                dialog.dismiss();
//            }
//        });
            AlertDialog alert = dialog.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "SuccessVehicleGrievance", e.getMessage(), e.toString());
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
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
                sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
                SharedPreferences.Editor supporteditor = sharedpreferences.edit();
                supporteditor.putString(MainActivity.ReferrenceURL, "Creat Grivience");
                supporteditor.commit();
                sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
                SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
                supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
                supporteditorimg.commit();
                Intent login = new Intent(Grievance.this, SupportTicket.class);
                startActivity(login);
                return true;

                // Do something

            } else {
                try {
                    Intent intent = getIntent();
                    if(intent.getBooleanExtra("grievance",false)){
                        Intent login = new Intent(Grievance.this, OtherServices.class);
                        startActivity(login);
                    }else{
                        Intent login = new Intent(Grievance.this, AddVehicle.class);
                        startActivity(login);
                    }
//                    Intent login = new Intent(Grievance.this, Registration.class);
//                    startActivity(login);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + "onOptionItemSelected", ex.getMessage(), ex.toString());
                    mCrashlytics.recordException(ex);
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
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Intent intent = getIntent();
            if(intent.getBooleanExtra("grievance",false)){
                Intent login = new Intent(Grievance.this, OtherServices.class);
                startActivity(login);
            }else{
                Intent login = new Intent(Grievance.this, AddVehicle.class);
                startActivity(login);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        //  Intent login = new Intent(Grievance.this, AddVehicle.class);
        // Intent login = new Intent(MainActivity.this, ClaimFinalForm.class);
        // startActivity(login);
        //finishAffinity(); // or finish();
    }

    public void createDatePicker(View view) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            DatePickerDialog datePickerDialog = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                datePickerDialog = new DatePickerDialog(Grievance.this);
                //following line to restrict future date selection
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dateid = "1";
                datePickerDialog.show();

                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePickerDatePicker, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updatedateLabel();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }


}