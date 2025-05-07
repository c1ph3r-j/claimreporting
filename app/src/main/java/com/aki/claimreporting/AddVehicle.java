package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_CAMERA_AND_STORAGE;
import static com.aki.claimreporting.ScanCertificate.qrcodedone;

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
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

public class AddVehicle extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public static final String ModelID = "Modelid";
    public static final String MakeCertificate = "MakeCertificate";
    public static final String CertNum = "CertNum";
    //    public static String certificatenumaddriver,regnumadddriver;
    public static final String certificatenumaddriver = "certificatenumaddriver";
    public static final String drivermapdriverid = "drivermapdriverid";
    public static final String regnumadddriver = "regnumadddriver";
    private static final int IMAGE_TAKE_CODE = 1234;
    public static LinearLayout digital, digitalnew;
    public static LinearLayout digaddimgvech;
    public static LinearLayout scannumlin;
    public static String reqidval;
    public static String qrcodefinal;
    public static String certcodefinal;
    public static String certstatuval;
    public static String numplateimageurl, numplateattachmentRefNo;
    public static String dateTimenew;
    public static FirebaseCrashlytics mCrashlytics;
    public static TextView policystdate, policyenddate;
    //    physicaltext,
    public static String membercraid;
    public static EditText certificatenumber, expirydate, certificateno, insurancecompany, insurancetype, speedmeter;
    public static EditText numplascanres;
    public static int qrcodelay;
    public static EditText insuredname;
    public static TextView diginsurval, digitalcertificateno, digitalpolicyno, digitalregno;
    public static EditText digitalchassisno, digitalmake, digitalmodel, digitalyear;
    public static byte[] regByte = null;
    public static DatabaseHelper mydb;
    public static String regnum, certificatenum, numplatscandone, numplateprogressstart, numplatscanvalue;
    public static Context context;
    public static String imageURL, imagename;
    public static ImageView uploadprog, uploadsucces;
    public static ImageView vechaddimg, vechaddimguploadone;
    public static TableRow digyestable;
    public static String certnumberdmvic;
    public static String phnumberdmvic;
    final Calendar myCalendar = Calendar.getInstance();
    public Bitmap insurer;
    public String vechilephototook;
    public String vechieattachid;
    public Bitmap rotatedBitmap;
    public Spinner insurspinedigital;
    public String insurecompanyval;
    public Date dateStart;
    public Date dateEnd;
    public String proceedid, suminsured, tonnage, passengercount, insuredpin, enginenumber, insuredphnno, insuredemail, certificateval, policynum, memcompany, certificatetype, coveragetype, policystart, policyend, regno, chassisno, makeval, modelval, yearmfg, insuredid, insurername, insurednameval;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    public String fromdatevalue, stokenval;
    public int numberplatescan = 0;
    public String postURL, startdate, enddate;
    public String todatevalue;
    public String expirydatevalue;
    public String sdocumentType;
    public String dateid = "0";
    public Date datefrom, dateto;
    public Date datefrompolicy, datetopolicy;
    public ProgressDialog progressdialog;
    public int versionos;
    public Date dateexpiry;
    public String encryptedSHA;
    public TextView step1button;
    PermissionHandler permissionManager;
    String[] permissions = PERMISSION_CAMERA_AND_STORAGE;
    String TAG = MainActivity.class.getSimpleName();
    String SITE_KEY = "6LdVl7wbAAAAAEVCngANf4viYJn9MiXVmZb4uzzF";
    String SECRET_KEY = "6LdVl7wbAAAAAIhFCsmraMDrJKoyj9DuUiiDrcNl";
    RequestQueue queue;
    String currentPhotoPath;
    List<String> insurcomspinner = new ArrayList<String>();
    List<InsuranceComInfo> insurnamelist = new ArrayList<>();
    LinearLayout buttonProceed;
    Activity activityAddVehicle;
    EditText regNoField;

    private String blockCharacterSet = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

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


//    public static void ocrdetectphysical()
//    {
//
//        if(QrCodeScanner.qrcodedone == 1)
//        {
//            SharedPreferences qrcodePref = context.getSharedPreferences("QrCodePref",MODE_PRIVATE);
//            if(qrcodePref.getString(QrCodeScanner.QrCode, "").contains("AKI"))
//            {
//                physicaltext.setVisibility(View.VISIBLE);
//                String cert = qrcodePref.getString(QrCodeScanner.QrCode, "");
//                String laststringa = cert.substring(cert.lastIndexOf(",")+1);
//                certificatenumber.setText(laststringa);
//                physicaltext.setText("*** You have scanned the Physical certificate ***");
//                physical.setVisibility(View.VISIBLE);
//                digital.setVisibility(View.GONE);
//            }
//            else
//            {
//                physical.setVisibility(View.GONE);
//                digital.setVisibility(View.VISIBLE);
//                physicaltext.setText("*** You have scanned the Digital certificate ***");
//
//            }
//        }
//        else {
//            physical.setVisibility(View.GONE);
//            // physical.setVisibility(View.VISIBLE);
//            digital.setVisibility(View.GONE);
//            physicaltext.setVisibility(View.GONE);
//        }
//    }

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
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            //this.getSupportActionBar().hide();
            Objects.requireNonNull(getSupportActionBar()).setTitle("Add Vehicle");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        setContentView(R.layout.activity_add_vehicle);
        preventSizeChange(this, getSupportActionBar());
        context = this;
        proceedid = "0";
        vechilephototook = "0";

//        int SDK_INT = android.os.Build.VERSION.SDK_INT;
//        if (SDK_INT > 8) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//            //your codes here
//
//        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        activityAddVehicle = this;
        insuredid = "";


        try {
            digyestable = (TableRow) findViewById(R.id.digregyestable);


            //uploadprog = (ImageView) findViewById(R.id.imageuploading);
            // uploadsucces = (ImageView) findViewById(R.id.imageuploadeddone);


            digitalcertificateno = (TextView) findViewById(R.id.editdigitalcertificateno);
            digitalregno = (TextView) findViewById(R.id.editdigitalregno);
            digitalchassisno = (EditText) findViewById(R.id.editdigitalchassisno);
            digitalpolicyno = (TextView) findViewById(R.id.editdigitalpolicyno);
            digitalmake = (EditText) findViewById(R.id.editdigitalmake);
            digitalmodel = (EditText) findViewById(R.id.editdigitalmodel);
            digitalyear = (EditText) findViewById(R.id.editdigitalyear);

            digaddimgvech = findViewById(R.id.addimgvech);


            vechaddimg = (ImageView) findViewById(R.id.imagevechTakePhotodig);
            vechaddimguploadone = (ImageView) findViewById(R.id.imagevehcuploadeddonedig);

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
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
            vechaddimg.setOnClickListener(onClickAddVehicle -> {
//                SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
//                SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
//                modeldoceditor.putString(CarView.doctypeid, "b9c6b305-e494-4430-9745-1aafcedb0a92");
//                modeldoceditor.apply();
                if (permissionManager.hasPermissions(permissions)) {
                    dispatchTakePictureIntent();
                } else {
                    permissionManager.requestPermissions(permissions);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


        try {
            //scannumlin = (LinearLayout) findViewById(R.id.linearscannum);
            //  numplascanres = (EditText) findViewById(R.id.editnumberplatecapture);
            //  numplascanres.setFilters(new InputFilter[] { filter });
            expirydate = (EditText) findViewById(R.id.editexpirydt);
            //   scannumlin.setVisibility(View.GONE);
//        uploadprog.setVisibility(View.GONE);
            //   uploadsucces.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        init();
        try {
            mCrashlytics = FirebaseCrashlytics.getInstance();
            regNoField = findViewById(R.id.vehicleRegisterNumberField);
            regNoField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                  proceedid = "0";
                    digitalnew.setVisibility(View.GONE);
                    digaddimgvech.setVisibility(View.GONE);
                    step1button.setText(R.string.validate_motor_insurance_certificate);
                }
            });
            // certificateno = (EditText) findViewById(R.id.editcertificateno);
            // insuredname = (EditText) findViewById(R.id.editinsured);
            //insurancecompany = (EditText) findViewById(R.id.editinsurancecmpny);
            //insurancetype = (EditText) findViewById(R.id.editinsurancetype);
            policystdate = (TextView) findViewById(R.id.editpolicystdt);
            policyenddate = (TextView) findViewById(R.id.editpolicyenddt);
            digital = (LinearLayout) findViewById(R.id.lineardigital);
            digitalnew = (LinearLayout) findViewById(R.id.lineardigitalnew);
            digitalnew.setVisibility(View.GONE);
            diginsurval = (TextView) findViewById(R.id.insurdigitalval);
//        physicaltext = (TextView) findViewById(R.id.digitalorphysical);
            //certificatenumber = (EditText) findViewById(R.id.editcertificateno);
//        if(numplatscandone == "1")
//        {
//            scannumlin.setVisibility(View.VISIBLE);
//            numplascanres.setText(numplatscanvalue);
//           // numplascanres.setText(numplascanres.getText().toString());
//        }
//        if(numplateprogressstart == "1")
//        {
//            uploadprog.setVisibility(View.VISIBLE);
//            uploadsucces.setVisibility(View.GONE);
//        }

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        try {
            if (qrcodelay == 1) {
                if (qrcodedone == 1) {
                    expirydate.setEnabled(true);
                    SharedPreferences qrcodePref = getSharedPreferences("QrCodePref", MODE_PRIVATE);
                    if (qrcodePref.getString(ScanCertificate.QrCode, "").startsWith("AKI")) {
                        try {
                            //                    physicaltext.setVisibility(View.VISIBLE);
//                    //String cert = qrcodePref.getString(QrCodeScanner.QrCode, "");
//                    //  String laststringa = cert.substring(cert.lastIndexOf(",")+1);
//                    // certificatenumber.setText(laststringa);
//                    physicaltext.setText(R.string.you_have_scanned_physical_certificate);
                            digital.setVisibility(View.VISIBLE);
                            digitalnew.setVisibility(View.GONE);


                            //old code
                            // physical.setVisibility(View.VISIBLE);
                            // digital.setVisibility(View.GONE);
                            // digitalnew.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    } else {
                        try {
                            //old code
                            digital.setVisibility(View.VISIBLE);
                            digitalnew.setVisibility(View.GONE);
//                    physicaltext.setText(R.string.you_have_scanned_digital_certificate);
                            // getCertificateInfo();
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    }
                } else {
                    try {
                        // physical.setVisibility(View.VISIBLE);
                        digital.setVisibility(View.GONE);
                        digitalnew.setVisibility(View.GONE);
//                physicaltext.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                }
            } else {
                try {
                    // physical.setVisibility(View.VISIBLE);
                    digital.setVisibility(View.GONE);
                    digitalnew.setVisibility(View.GONE);
//            physicaltext.setVisibility(View.GONE);
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


        step1button = findViewById(R.id.Btnstep1proceed);
        buttonProceed = findViewById(R.id.proceedBtn);
        try {
            buttonProceed.setOnClickListener(onClickStep1 -> {
//                Intent step2 = new Intent(AddVehicle.this, RegistrationStep2.class);
//                startActivity(step2);
//                if (qrcodedone == 0) {
//                    Toast.makeText(activity, "Please Scan a certificate to continue!", Toast.LENGTH_SHORT).show();
//                } else if (Objects.equals(proceedid, "1")) {
//                    try {
//                        addvehicleinfo();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                        mCrashlytics.recordException(e);
//                    }
//                } else {
//                    try {
//                        GetProceedDetails();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                        mCrashlytics.recordException(e);
//                    }
//                }


                String regNoVal = regNoField.getText().toString().trim();

                if (regNoVal.isEmpty()) {
                    Toast.makeText(context, "Please Enter Register Number Of the Vehicle To Continue", Toast.LENGTH_SHORT).show();
                } else if (Objects.equals(proceedid, "1")) {
                    addvehicleinfo();
                } else {

                    GetProceedDetails();
                }


            });
            step1button.setText(R.string.validate_motor_insurance_certificate);

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
//        numplascanres.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {}
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                numplatscanvalue = String.valueOf(s);
//
//            }
//        });


//        ImageView certificateqrView = findViewById(R.id.imagecertificateTakePhoto);
//        try {
//            certificateqrView.setOnClickListener(onClickCertificateQrView -> {
//                qrcodelay = 1;
//                SharedPreferences sharedPreference = getSharedPreferences("QrCodeNavigation", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreference.edit();
//                editor.putString("QrCodeCheck", "0");
//                editor.apply();
//                Intent intent = new Intent(AddVehicle.this, ScanCertificate.class);
//                startActivity(intent);
//                finish();
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//            mCrashlytics.recordException(e);
//        }
//        ImageView numberplateView = (ImageView) findViewById(R.id.imagenumberplateTakePhoto);
//        numberplateView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                try {
//                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
//                    modeldoceditor.putString(CarView.doctypeid, "bc250be1-59e5-49ef-9ee9-66af6d7a1eff");
//                    modeldoceditor.commit();
//                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = loadmodelPref.edit();
//                    editor.putString(ModelID, "1");
//                    editor.commit();
//                    SharedPreferences licencepref = getSharedPreferences("LicenceRedirect", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor licenceeditor = licencepref.edit();
//                    licenceeditor.putString(MainActivity.PushID, "0");
//                    licenceeditor.commit();
//                    Intent intent = new Intent(AddVehicle.this, CameraDamage.class);
//                    startActivity(intent);
//
////                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
////                    SharedPreferences.Editor editor = loadmodelPref.edit();
////                    editor.putString(ModelID, "1");
////                    editor.commit();
////                    SharedPreferences licencepref = getSharedPreferences("LicenceRedirect", Context.MODE_PRIVATE);
////                    SharedPreferences.Editor licenceeditor = licencepref.edit();
////                    licenceeditor.putString(MainActivity.PushID, "0");
////                    licenceeditor.commit();
////                    Intent intent = new Intent(AddVehicle.this, ObjectDetectorActivity.class);
////                    startActivity(intent);
////                    finish();
//
////                    int versionos = Build.VERSION.SDK_INT;
////                    if(versionos < 29){
////                        SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
////                        SharedPreferences.Editor editor = loadmodelPref.edit();
////                        editor.putString(ModelID, "1");
////                        editor.commit();
////                        SharedPreferences licencepref = getSharedPreferences("LicenceRedirect", Context.MODE_PRIVATE);
////                        SharedPreferences.Editor licenceeditor = licencepref.edit();
////                        licenceeditor.putString(MainActivity.PushID, "0");
////                        licenceeditor.commit();
////                        Intent intent = new Intent(AddVehicle.this, CameraDamage.class);
////                        startActivity(intent);
////                    }
////                    else
////                    {
////                        SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
////                        SharedPreferences.Editor editor = loadmodelPref.edit();
////                        editor.putString(ModelID, "1");
////                        editor.commit();
////                        SharedPreferences licencepref = getSharedPreferences("LicenceRedirect", Context.MODE_PRIVATE);
////                        SharedPreferences.Editor licenceeditor = licencepref.edit();
////                        licenceeditor.putString(MainActivity.PushID, "0");
////                        licenceeditor.commit();
////                        Intent intent = new Intent(AddVehicle.this, ObjectDetectorActivity.class);
////                        startActivity(intent);
////                        finish();
////                    }
//                }
//                catch (Exception ex)
//                {
//                    ex.printStackTrace();
//                    Toast.makeText(AddVehicle.this,R.string.exceptioncheck, Toast.LENGTH_SHORT).show();
//                }
//
////                SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
////                SharedPreferences.Editor editor = loadmodelPref.edit();
////                editor.putString(ModelID, "1");
////                editor.commit();
////                SharedPreferences licencepref = getSharedPreferences("LicenceRedirect", Context.MODE_PRIVATE);
////                SharedPreferences.Editor licenceeditor = licencepref.edit();
////                licenceeditor.putString(MainActivity.PushID, "0");
////                licenceeditor.commit();
////                Intent intent = new Intent(AddVehicle.this, ObjectDetectorActivity.class);
////                startActivity(intent);
////                finish();
//
//
//            }
//        });

    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            queue = Volley.newRequestQueue(getApplicationContext());

            try {
                //getinsurancecompany();
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        if (dateid == "1") {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateFromLabel();
                        } else if (dateid == "2") {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateToLabel();
                        } else if (dateid == "3") {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateexpiryLabel();
                        }
                    }

                };
                expirydate.setOnClickListener(onClickExpiryDate -> {
                    try {
                        DatePickerDialog d = new DatePickerDialog(AddVehicle.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                        d.getDatePicker().setMinDate(new Date().getTime());
                        d.show();
                        dateid = "3";
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (requestCode == IMAGE_TAKE_CODE) {
                //TODO: action

                if (getValidBitmap(currentPhotoPath) != null) {
                    insurer = getBitmap(currentPhotoPath);
                    vechilephototook = "1";
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
                            rotatedBitmap = rotateImage(insurer, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(insurer, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(insurer, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = insurer;
                    }

                    pushimagevechile();
                    // pushimagevechile();
                    //pushimageclaim();

                    // bitmapconverstion(insurer);

                    //  MainActivity.uploadimages(RegistrationStep4.this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

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

    public void pushimagevechile() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
                    mydb = new DatabaseHelper(AddVehicle.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                MainActivity.stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }

                    Thread thread = new Thread(() -> {

                        SharedPreferences locationPref = getSharedPreferences("LocationPref", Context.MODE_PRIVATE);


//                            Bitmap bitmapnew = insurer;
//                            ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
//                            bitmapnew.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
//                            String outStr = ObjectDetectorActivity.Global.outputStr;
//                            byte[] byteArraynew = streamnew.toByteArray();

                        Bitmap bitmapnew = rotatedBitmap;
//                            Matrix matrix = new Matrix();
//                            matrix.postRotate(90);
//
//                            Bitmap rotated = Bitmap.createBitmap(bitmapnew, 0, 0, bitmapnew.getWidth(), bitmapnew.getHeight(),
//                                    matrix, true);


                        ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
                        bitmapnew.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
                        // rotated.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
                        byte[] byteArraynew = streamnew.toByteArray();

                        String latval = locationPref.getString(MainActivity.Latitude, null);
                        String longval = locationPref.getString(MainActivity.Longitude, null);
                        final MediaType mediaType = MediaType.parse("image/jpeg");
                        String imgData = Base64.encodeToString(byteArraynew, Base64.DEFAULT);
                        String imgDatanew = imgData;
                        String dateTime = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            dateTime = localToGMT();
                        }
                        String uniqueID = UUID.randomUUID().toString();
                        sdocumentType = "ed7b4931-9bbe-482c-ab3b-430fcf733f4c";
                        postURL = activityAddVehicle.getString(R.string.uaturl) + "/app/Upload/UploadFiles";
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        RequestBody body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("fileName", "Vehicle.jpg")
                                .addFormDataPart(
                                        "image", "Vehicle.jpg",
                                        RequestBody.create(mediaType, byteArraynew))
                                .addFormDataPart("documentType", sdocumentType)
                                .addFormDataPart("attachmentTypeID", sdocumentType)
                                .addFormDataPart("geoTagLat", locationPref.getString(MainActivity.Latitude, null))
                                .addFormDataPart("geoTagLon", locationPref.getString(MainActivity.Longitude, null))
                                .addFormDataPart("captureAttachmentID", uniqueID)
                                .addFormDataPart("captureDateTime", dateTime)
                                .build();
                        Request request = new Request.Builder()
                                .url(postURL)
                                .method("POST", body)
                                .addHeader("Authorization", "Bearer " + MainActivity.stokenval)
                                .addHeader("MobileParameter", MainActivity.InsertMobileParameters())
                                .build();
                        Response staticResponse;
                        try {
                            runOnUiThread(() ->
                                    progressdialog = ProgressDialog.show(AddVehicle.this, getString(R.string.loading), getString(R.string.please), true));
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
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    try {
                                        vechieattachid = staticJsonObj.getJSONObject("rObj").getString("AttachmentID");
                                        boolean isMotorVehicle = staticJsonObj.getJSONObject("rObj").getBoolean("isMoterVehicle");
                                        if (!isMotorVehicle) {
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                                            alertDialogBuilder.setTitle("Alert");
                                            alertDialogBuilder.setMessage(getString(R.string.not_motor_vehicle_str));
                                            alertDialogBuilder.setPositiveButton("Proceed", (dialog, which) -> dialog.dismiss());
                                            alertDialogBuilder.setNegativeButton("Re-Capture", (dialog, which) -> dialog.dismiss());
                                            alertDialogBuilder.setCancelable(false);
                                            alertDialogBuilder.show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                    vechaddimguploadone.setVisibility(View.VISIBLE);
                                });

                            } else if (staticJsonObj.getInt("rcode") == -5) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    try {
                                        notvalidvechicle(staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText"));
                                        vechaddimguploadone.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
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
                                            AlertDialog.Builder alert = new AlertDialog.Builder(AddVehicle.this);
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
                        } catch (final IOException | JSONException e) {
                            runOnUiThread(progressdialog::dismiss);
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }

                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddVehicle.this);
                    dialog.setMessage(getString(R.string.gps_not_enabled));
                    dialog.setPositiveButton("Ok", (dialog1, which) -> {
                        //this will navigate user to the device location settings screen
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    });
                    android.app.AlertDialog alert = dialog.create();
                    alert.show();
                }


            } else {
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            runOnUiThread(() -> progressdialog.dismiss());
            // TODO Auto-generated catch block
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


    }

    private void updateexpiryLabel() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            String myFormat = "dd-MMM-yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            expirydate.setText(sdf.format(myCalendar.getTime()));
            expirydatevalue = sdf.format(myCalendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void updateFromLabel() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            fromdatevalue = sdf.format(myCalendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void updateToLabel() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            todatevalue = sdf.format(myCalendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

//    public void geterror(String eval) {
//        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(RegistrationStep1.this);
//        //dialog.setMessage("Do you like to port the data to this new device?");
//        dialog.setMessage(eval);
//        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        androidx.appcompat.app.AlertDialog alert = dialog.create();
//        alert.show();
//    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    protected void handleSiteVerify(final String responseToken) {
        //it is google recaptcha siteverify server
        //you can place your server url
        String url = "https://www.google.com/recaptcha/api/siteverify";
        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            //code logic when captcha returns true Toast.makeText(getApplicationContext(),String.valueOf(jsonObject.getBoolean("success")),Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), String.valueOf(jsonObject.getString("error-codes")), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        Log.d(TAG, "JSON exception: " + ex.getMessage());

                    }
                },
                error -> Log.d(TAG, "Error message: " + error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret", SECRET_KEY);
                params.put("response", responseToken);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
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

    private void GetProceedDetails() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (isNetworkConnected()) {
                if (checkGPSStatus()) {

//                    if (expirydate.getText().toString().contains("Please") || expirydate.getText().toString() == "" || expirydate.getText().toString() == null || expirydate.getText().toString().length() == 0) {
//
//                    } else {
//                        try {
//                            String dtStart = expirydate.getText().toString();
//                            String dtEnd = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());
//                            SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
//                            try {
//                                dateStart = format.parse(dtStart);
//                                dateEnd = format.parse(dtEnd);
//                                System.out.println(dateStart);
//                                System.out.println(dateEnd);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                                mCrashlytics.recordException(e);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                            mCrashlytics.recordException(e);
//                        }
//                    }

                    String specialchar = "[a-zA-Z0-9 ]*";
//                        if(numplatscandone != "1")
//                        {
//                            Toast.makeText(AddVehicle.this, getString(R.string.scannum), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        else if(numplascanres.getText().toString() == "" || numplascanres.getText().toString() == null || numplascanres.getText().toString().length() == 0)
//                        {
//                            Toast.makeText(AddVehicle.this, getString(R.string.numplatreq), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        else if(numplascanres.getText().toString().length() > 10)
//                        {
//                            Toast.makeText(AddVehicle.this, getString(R.string.numplategreater), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        else if(specialchar.matches(numplascanres.getText().toString())) {
//                            Toast.makeText(AddVehicle.this, getString(R.string.numplategreater), Toast.LENGTH_SHORT).show();
//                            return;
//                        }

//                    SharedPreferences modelPref = getSharedPreferences("QrCodePref", Context.MODE_PRIVATE);
//                    String qrcodeval = modelPref.getString(ScanCertificate.QrCode, null);
//                    qrcodefinal = qrcodeval;
//                    try {
//                        if (qrcodefinal.equals("") || qrcodefinal.length() == 0) {
//                            Toast.makeText(AddVehicle.this, getString(R.string.scancertificate), Toast.LENGTH_SHORT).show();
//                            return;
//                        } else if (expirydate.getText().toString().contains("Please") || expirydate.getText().toString() == "" || expirydate.getText().toString() == null || expirydate.getText().toString().length() == 0) {
//                            Toast.makeText(AddVehicle.this, getString(R.string.enterexpirydate), Toast.LENGTH_SHORT).show();
//                            return;
//                        } else if (dateStart.compareTo(dateEnd) < 0) {
//                            Toast.makeText(AddVehicle.this, getString(R.string.expiredcertificate), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                        mCrashlytics.recordException(e);
//                    }

                    try {
                        getinsurancecompany();
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    //getCertificateInfo();
                    // else {


//                                Intent step2 = new Intent(AddVehicle.this, RegistrationStep2.class);
//                                startActivity(step2);
                    //  }
//                        SharedPreferences qrcodePref = getSharedPreferences("QrCodePref", MODE_PRIVATE);
//                        if (qrcodePref.getString(QrCodeScanner.QrCode, "").startsWith("AKI")) {
//
//                            if (certificateno.getText().toString() == "" || certificateno.getText().toString() == null || certificateno.getText().toString().length() == 0) {
//                                Toast.makeText(AddVehicle.this, getString(R.string.entercertificateno), Toast.LENGTH_SHORT).show();
//                                return;
//                            } else if (insuredname.getText().toString() == "" || insuredname.getText().toString() == null || insuredname.getText().toString().length() == 0) {
//                                Toast.makeText(AddVehicle.this, getString(R.string.enterinsuredname), Toast.LENGTH_SHORT).show();
//                                return;
//                            } else if (insurancecompany.getText().toString() == "" || insurancecompany.getText().toString() == null || insurancecompany.getText().toString().length() == 0) {
//                                Toast.makeText(AddVehicle.this, getString(R.string.enterinsurancecompany), Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            else if (policystdate.getText().toString().contains("Please") || policystdate.getText().toString() == "" || policystdate.getText().toString() == null || policystdate.getText().toString().length() == 0) {
//                                Toast.makeText(AddVehicle.this, getString(R.string.enterpolicystdate), Toast.LENGTH_SHORT).show();
//                                return;
//                            } else if (policyenddate.getText().toString().contains("Please") || policyenddate.getText().toString() == "" || policyenddate.getText().toString() == null || policyenddate.getText().toString().length() == 0) {
//                                Toast.makeText(AddVehicle.this, getString(R.string.enterpolicyenddate), Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            else {
//                                getCertificateInfo();
////                                Intent step2 = new Intent(AddVehicle.this, RegistrationStep2.class);
////                                startActivity(step2);
//                            }
//
//                        } else {
//                            if(numplatscandone != "1")
//                            {
//                                Toast.makeText(AddVehicle.this, getString(R.string.scannum), Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            else if (expirydate.getText().toString().contains("Please") || expirydate.getText().toString() == "" || expirydate.getText().toString() == null || expirydate.getText().toString().length() == 0) {
//                                Toast.makeText(AddVehicle.this, getString(R.string.enterexpirydate), Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            else if(numplascanres.getText().toString() == "" || numplascanres.getText().toString() == null || numplascanres.getText().toString().length() == 0)
//                            {
//                                Toast.makeText(AddVehicle.this, getString(R.string.numplatreq), Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            else if(numplascanres.getText().toString().length() > 10)
//                            {
//                                Toast.makeText(AddVehicle.this, getString(R.string.numplategreater), Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                             else {
//
//                                 getinsurancecompany();
//
////                                Intent step2 = new Intent(AddVehicle.this, RegistrationStep2.class);
////                                startActivity(step2);
//                            }
//                        }


                } else {
                    try {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AddVehicle.this);
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


                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    public String InsertMobileparameters() {
        SharedPreferences uniquePref = context.getSharedPreferences("Uniquepref", MODE_PRIVATE);
        final String uniqueidval = uniquePref.getString(MainActivity.UniqueID, null);
        SharedPreferences locationPref = getSharedPreferences("LocationPref", MODE_PRIVATE);
        final String address1 = locationPref.getString(MainActivity.Latitude, null) + locationPref.getString(MainActivity.Longitude, null) + locationPref.getString("Address1", null) + locationPref.getString("Address2", null) + locationPref.getString("State", null) + locationPref.getString("District", null) + locationPref.getString("Locality", null) + locationPref.getString("PostalCode", null);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipaddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        JsonObject mobileparamters = new JsonObject();
        mobileparamters.addProperty("imeino1", uniqueidval);
        mobileparamters.addProperty("timezone", TimeZone.getDefault().getDisplayName());
        mobileparamters.addProperty("currentdatetime", java.text.DateFormat.getDateTimeInstance().format(new Date()));
        mobileparamters.addProperty("Location", address1);
        mobileparamters.addProperty("IpAddress", ipaddress);
        //        encryptedSHA = "";
//        String sourceStr = uniqueidval + ipaddress;
//        try {
//            encryptedSHA = AESUtils.encrypt(sourceStr);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return mobileparamters.toString();
    }

    public void getCertificateInfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    // getinsurancecompany();
                    progressdialog = new ProgressDialog(this);
                    mydb = new DatabaseHelper(AddVehicle.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                    SharedPreferences qrcodePref = getSharedPreferences("QrCodePref", MODE_PRIVATE);
                    if (qrcodePref.getString(ScanCertificate.QrCode, "").startsWith("AKI")) {
                        SharedPreferences modelPref = getSharedPreferences("QrCodePref", Context.MODE_PRIVATE);
                        String qrcodeval = modelPref.getString(ScanCertificate.QrCode, null);
                        qrcodefinal = qrcodeval;
                    } else {
                        SharedPreferences modelPref = getSharedPreferences("QrCodePref", Context.MODE_PRIVATE);
                        String qrcodeval = modelPref.getString(ScanCertificate.QrCode, null);
                        qrcodefinal = qrcodeval;
                    }

//                        encryptedSHA = "";
//                        String sourceStr = InsertMobileparameters();
//                        try {
//                            encryptedSHA = AESUtils.encrypt(sourceStr);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    Thread thread = new Thread(() -> {
                        postURL = getString(R.string.uaturl) + "/app/Integration/GetCertificateByRegistrationNo";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        JsonObject Details = new JsonObject();
                        String regNoVal = regNoField.getText().toString().trim();
                        String regNoValnew = regNoVal.trim(); // Remove leading and trailing spaces
                        String regNoValfinal = regNoValnew.replaceAll("\\s", "");
                        Details.addProperty("LicencePlate", regNoValfinal);
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(postURL)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .header("Authorization", "Bearer " + stokenval)
                                .post(body)
                                .build();
                        Response staticResponse;

                        try {
                            runOnUiThread(() ->
                                    progressdialog = ProgressDialog.show(activityAddVehicle, getString(R.string.loading), getString(R.string.please), true));
                            staticResponse = client.newCall(request).execute();
                            int statuscode = staticResponse.code();
                            if (statuscode == 401) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    unauthorize(AddVehicle.this);
                                });
                            } else {
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
                                int errorcode = staticJsonObj.getInt("rcode");
                                if (staticJsonObj.getInt("rcode") == 1) {
                                    final JSONObject staticfinalObj = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate");
                                    certstatuval = staticfinalObj.getString("certificateStatus");
                                    // if(staticfinalObj.getString("certificateNo") == "null" || staticfinalObj.getString("certificateNo")  == null)
                                    if (staticfinalObj.getString("certificateNo") == "null") {
                                        runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            //  RegistrationGlobal.inputregno = numplascanres.getText().toString();
                                            //  RegistrationGlobal.inputexpirydate = expirydate.getText().toString();
                                            //   RegistrationGlobal.inputprintcode = qrcodefinal;
                                            RegistrationGlobal.outputregno = "";
                                            RegistrationGlobal.outputexpirydate = "";
                                            RegistrationGlobal.outputprintcode = "";
                                            askVehicleGrievances();
                                        });
                                    } else {
                                        runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            step1button.setText("NEXT");
                                            SharedPreferences qrcodePref1 = getSharedPreferences("QrCodePref", MODE_PRIVATE);
                                            if (qrcodePref1.getString(ScanCertificate.QrCode, "").startsWith("AKI")) {


                                                digaddimgvech.setVisibility(View.VISIBLE);
                                                digitalnew.setVisibility(View.GONE);

                                                String[] phycert = qrcodePref1.getString(ScanCertificate.QrCode, "").split(",");
                                                try {
                                                    SharedPreferences makecertificate = getSharedPreferences("MakeCertificate", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor makecertificateeditor = makecertificate.edit();
                                                    makecertificateeditor.putString(AddVehicle.MakeCertificate, staticfinalObj.getString("make"));
                                                    makecertificateeditor.commit();
                                                    if (staticfinalObj.getString("certificateNo") == "null" || staticfinalObj.getString("certificateNo") == null)
//                                                                {
//                                                                    oldcertificateno.setText("");
//                                                                }
//                                                                else
//                                                                {
//                                                                    oldcertificateno.setText(staticfinalObj.getString("certificateNo"));
//                                                                }
//                                                            if(staticfinalObj.getString("registratioNo") == "null" || staticfinalObj.getString("registratioNo") == null)
//                                                            {
//                                                                oldregno.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                oldregno.setText(staticfinalObj.getString("registratioNo"));
//                                                            }
//
//                                                            if(staticfinalObj.getString("chassisNumber") == "null" || staticfinalObj.getString("chassisNumber") == null)
//                                                            {
//                                                                oldchassisno.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                oldchassisno.setText(staticfinalObj.getString("chassisNumber"));
//                                                            }
//                                                            if(staticfinalObj.getString("policyno") == "null" || staticfinalObj.getString("policyno") == null)
//                                                            {
//                                                                oldpolicyno.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                oldpolicyno.setText(staticfinalObj.getString("policyno"));
//                                                            }
//                                                            if(staticfinalObj.getString("policyBeginDate") == "null" || staticfinalObj.getString("policyBeginDate") == null)
//                                                            {
//                                                                //digitalpolicystdt.setText("");
//                                                                policystdate.setText("");
//                                                            }


                                                        //  digitalpolicystdt.setText(staticfinalObj.getString("policyBeginDate"));
                                                        policystdate.setText(staticfinalObj.getString("policyBeginDate").substring(0, 10));
                                                    fromdatevalue = staticfinalObj.getString("policyBeginDate");

                                                    if (staticfinalObj.getString("policyEndDate") == "null" || staticfinalObj.getString("policyEndDate") == null) {
                                                        //digitalpolicyenddt.setText("");
                                                        policyenddate.setText("");
                                                    } else {
                                                        //digitalpolicyenddt.setText(staticfinalObj.getString("policyEndDate"));
                                                        policyenddate.setText(staticfinalObj.getString("policyEndDate"));
                                                        todatevalue = staticfinalObj.getString("policyEndDate");
                                                    }
//                                                            if(staticfinalObj.getString("make") == "null" || staticfinalObj.getString("make") == null)
//                                                            {
//                                                                oldmake.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                oldmake.setText(staticfinalObj.getString("make"));
//                                                            }
//                                                            if(staticfinalObj.getString("model") == "null" || staticfinalObj.getString("model") == null)
//                                                            {
//                                                                oldmodel.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                oldmodel.setText(staticfinalObj.getString("model"));
//                                                            }
//                                                            if(staticfinalObj.getString("yearofManufacture") == "null" || staticfinalObj.getString("yearofManufacture") == null)
//                                                            {
//                                                                oldyear.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                oldyear.setText(staticfinalObj.getString("yearofManufacture"));
//                                                            }
//                                                            oldinsurval.setText(staticfinalObj.getString("memberCompanyName"));
                                                    //String myString = staticfinalObj.getString("memberCompanyName");
                                                    //  ArrayAdapter myAdap = (ArrayAdapter) insurspinedigital.getAdapter();
                                                    // int spinnerPosition = myAdap.getPosition(myString);
                                                    // insurspinedigital.setSelection(spinnerPosition);

//                                                        if(!numplatscanvalue.equals(staticfinalObj.getString("registratioNo")))
//                                                        {
//                                                            regnummismatch();
//                                                        }
                                                } catch (JSONException e) {
                                                    runOnUiThread(progressdialog::dismiss);
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }


//                                                            digitalregno.setEnabled(true);
//                                                            digitalchassisno.setEnabled(true);
//                                                            digitalpolicyno.setEnabled(true);
//                                                            policystdate.setEnabled(true);
//                                                            policyenddate.setEnabled(true);
                                            } else {
                                                // digitalregno.setEnabled(true);
                                                digitalchassisno.setEnabled(true);
//                                                            digitalpolicyno.setEnabled(true);
//                                                            policystdate.setEnabled(true);
//                                                            policyenddate.setEnabled(true);

                                                digitalnew.setVisibility(View.VISIBLE);
                                                digaddimgvech.setVisibility(View.VISIBLE);

                                                try {
                                                    SharedPreferences makecertificate = getSharedPreferences("MakeCertificate", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor makecertificateeditor = makecertificate.edit();
                                                    makecertificateeditor.putString(AddVehicle.MakeCertificate, staticfinalObj.getString("make"));
                                                    makecertificateeditor.commit();

                                                    if (staticfinalObj.getString("certificateNo") == "null" || staticfinalObj.getString("certificateNo") == null) {
                                                        digitalcertificateno.setText("");
                                                    } else {
                                                        digitalcertificateno.setText(staticfinalObj.getString("certificateNo"));
                                                    }
                                                    if (staticfinalObj.getString("registratioNo") == "null" || staticfinalObj.getString("registratioNo") == null) {

                                                        digyestable.setVisibility(View.GONE);


                                                        //digitalregno.setText("N/A");
                                                    } else {

                                                        digyestable.setVisibility(View.VISIBLE);
                                                        digitalregno.setText(staticfinalObj.getString("registratioNo"));
                                                    }

                                                    if (staticfinalObj.getString("chassisNumber") == "null" || staticfinalObj.getString("chassisNumber") == null) {
                                                        digitalchassisno.setText("");
                                                    } else {
                                                        digitalchassisno.setText(staticfinalObj.getString("chassisNumber"));
                                                    }
                                                    if (staticfinalObj.getString("policyno") == "null" || staticfinalObj.getString("policyno") == null) {
                                                        digitalpolicyno.setText("");
                                                    } else {
                                                        digitalpolicyno.setText(staticfinalObj.getString("policyno"));
                                                    }
                                                    if (staticfinalObj.getString("policyBeginDate") == "null" || staticfinalObj.getString("policyBeginDate") == null) {
                                                        //digitalpolicystdt.setText("");
                                                        policystdate.setText("");
                                                    } else {
                                                        //  digitalpolicystdt.setText(staticfinalObj.getString("policyBeginDate"));
                                                        policystdate.setText(staticfinalObj.getString("policyBeginDate").substring(0, 10));
                                                        fromdatevalue = staticfinalObj.getString("policyBeginDate");
                                                    }
                                                    if (staticfinalObj.getString("policyEndDate") == "null" || staticfinalObj.getString("policyEndDate") == null) {
                                                        //digitalpolicyenddt.setText("");
                                                        policyenddate.setText("");
                                                    } else {
                                                        //digitalpolicyenddt.setText(staticfinalObj.getString("policyEndDate"));
                                                        policyenddate.setText(staticfinalObj.getString("policyEndDate"));
                                                        todatevalue = staticfinalObj.getString("policyEndDate");
                                                    }
                                                    if (staticfinalObj.getString("make") == "null" || staticfinalObj.getString("make") == null) {
                                                        digitalmake.setText("");
                                                    } else {
                                                        digitalmake.setText(staticfinalObj.getString("make"));
                                                    }
                                                    if (staticfinalObj.getString("model") == "null" || staticfinalObj.getString("model") == null) {
                                                        digitalmodel.setText("");
                                                    } else {
                                                        digitalmodel.setText(staticfinalObj.getString("model"));
                                                    }
                                                    if (staticfinalObj.getString("yearofManufacture") == "null" || staticfinalObj.getString("yearofManufacture") == null) {
                                                        digitalyear.setText("");
                                                    } else {
                                                        digitalyear.setText(staticfinalObj.getString("yearofManufacture"));
                                                    }
                                                    diginsurval.setText(staticfinalObj.getString("memberCompanyName"));

                                                    // insuredid = staticfinalObj.getString("memberCompanyID");

                                                    certnumberdmvic = staticfinalObj.getString("certificateNo");
                                                    phnumberdmvic = staticfinalObj.getString("insuredPhoneNumber");


                                                    //String myString = staticfinalObj.getString("memberCompanyName");
                                                    //  ArrayAdapter myAdap = (ArrayAdapter) insurspinedigital.getAdapter();
                                                    // int spinnerPosition = myAdap.getPosition(myString);
                                                    // insurspinedigital.setSelection(spinnerPosition);

//                                                        if(!numplatscanvalue.equals(staticfinalObj.getString("registratioNo")))
//                                                        {
//                                                            regnummismatch();
//                                                        }
                                                } catch (JSONException e) {
                                                    runOnUiThread(progressdialog::dismiss);
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }

//                                                            digitalregno.setEnabled(false);
//                                                            digitalchassisno.setEnabled(true);
//                                                            digitalpolicyno.setEnabled(false);
//                                                            policystdate.setEnabled(false);
//                                                            policyenddate.setEnabled(false);
                                            }
                                            proceedid = "1";
//                                                        physical.setVisibility(View.GONE);
//                                                        digital.setVisibility(View.VISIBLE);
//                                                        digitalnew.setVisibility(View.VISIBLE);
//                                                        try {
//                                                            SharedPreferences makecertificate = getSharedPreferences("MakeCertificate", Context.MODE_PRIVATE);
//                                                            SharedPreferences.Editor makecertificateeditor = makecertificate.edit();
//                                                            makecertificateeditor.putString(AddVehicle.MakeCertificate, staticfinalObj.getString("make"));
//                                                            makecertificateeditor.commit();
//
//                                                            if(staticfinalObj.getString("certificateNo") == "null" || staticfinalObj.getString("certificateNo") == null)
//                                                            {
//                                                                digitalcertificateno.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                digitalcertificateno.setText(staticfinalObj.getString("certificateNo"));
//                                                            }
//                                                            if(staticfinalObj.getString("registratioNo") == "null" || staticfinalObj.getString("registratioNo") == null)
//                                                            {
//                                                                digitalregno.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                digitalregno.setText(staticfinalObj.getString("registratioNo"));
//                                                            }
//
//                                                            if(staticfinalObj.getString("chassisNumber") == "null" || staticfinalObj.getString("chassisNumber") == null)
//                                                            {
//                                                                digitalchassisno.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                digitalchassisno.setText(staticfinalObj.getString("chassisNumber"));
//                                                            }
//                                                            if(staticfinalObj.getString("policyno") == "null" || staticfinalObj.getString("policyno") == null)
//                                                            {
//                                                                digitalpolicyno.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                digitalpolicyno.setText(staticfinalObj.getString("policyno"));
//                                                            }
//                                                            if(staticfinalObj.getString("policyBeginDate") == "null" || staticfinalObj.getString("policyBeginDate") == null)
//                                                            {
//                                                                //digitalpolicystdt.setText("");
//                                                                policystdate.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                //  digitalpolicystdt.setText(staticfinalObj.getString("policyBeginDate"));
//                                                                policystdate.setText(staticfinalObj.getString("policyBeginDate"));
//                                                                fromdatevalue = staticfinalObj.getString("policyBeginDate");
//                                                            }
//                                                            if(staticfinalObj.getString("policyEndDate") == "null" || staticfinalObj.getString("policyEndDate") == null)
//                                                            {
//                                                                //digitalpolicyenddt.setText("");
//                                                                policyenddate.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                //digitalpolicyenddt.setText(staticfinalObj.getString("policyEndDate"));
//                                                                policyenddate.setText(staticfinalObj.getString("policyEndDate"));
//                                                                todatevalue = staticfinalObj.getString("policyEndDate");
//                                                            }
//                                                            if(staticfinalObj.getString("make") == "null" || staticfinalObj.getString("make") == null)
//                                                            {
//                                                                digitalmake.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                digitalmake.setText(staticfinalObj.getString("make"));
//                                                            }
//                                                            if(staticfinalObj.getString("model") == "null" || staticfinalObj.getString("model") == null)
//                                                            {
//                                                                digitalmodel.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                digitalmodel.setText(staticfinalObj.getString("model"));
//                                                            }
//                                                            if(staticfinalObj.getString("yearofManufacture") == "null" || staticfinalObj.getString("yearofManufacture") == null)
//                                                            {
//                                                                digitalyear.setText("");
//                                                            }
//                                                            else
//                                                            {
//                                                                digitalyear.setText(staticfinalObj.getString("yearofManufacture"));
//                                                            }
//                                                            diginsurval.setText(staticfinalObj.getString("memberCompanyName"));

                                            try {
                                                for (int i = 0; i < insurnamelist.size(); i++) {
                                                    System.out.println(insurnamelist.get(i));
                                                    if (insurnamelist.get(i).getdMVICMemberCompanyID() == staticfinalObj.getInt("memberCompanyID")) {
                                                        insuredid = insurnamelist.get(i).getInsurerID();
                                                    } else {

                                                    }
//                                                                String phnprefval = myPrefixlist.get(i);
//                                                                String refval2phn = phonenonew.getText().toString().substring(0,2);
//                                                                String refval3phn = phonenonew.getText().toString().substring(0,3);

                                                }

//                                                            String  myString = staticfinalObj.getString("memberCompanyName");
//                                                            ArrayAdapter myAdap = (ArrayAdapter) insurspinedigital.getAdapter();
//                                                            int spinnerPosition = myAdap.getPosition(myString);
//                                                            insurspinedigital.setSelection(spinnerPosition);
                                            } catch (Exception e) {
                                                runOnUiThread(progressdialog::dismiss);
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }

//
////                                                        if(!numplatscanvalue.equals(staticfinalObj.getString("registratioNo")))
////                                                        {
////                                                            regnummismatch();
////                                                        }
//                                                        } catch (JSONException e) {
//                                                            e.printStackTrace();
//                                                            mCrashlytics.recordException(e);
//                                                            MainActivity.MobileErrorLog(reqidval,"AddVehicle-getCertificateInfo",e.toString(),e.toString());
//                                                        }

                                            if (certstatuval.contains("Cancelled")) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        step1button.setVisibility(View.GONE);
                                                        cancelledcert();
                                                        return;
                                                    }
                                                });
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        step1button.setVisibility(View.VISIBLE);
                                                    }
                                                });
                                            }


                                        });


//

                                        try {
                                            certificateval = staticfinalObj.getString("certificateNo");
//                                        policynum = staticfinalObj.getString("policyno");
                                            certificatetype = staticfinalObj.getString("typeOfInsurance");
                                            coveragetype = staticfinalObj.getString("typeofCover");
//                                        policystart= staticfinalObj.getString("policyBeginDate");
//                                        policyend = staticfinalObj.getString("policyEndDate");
//                                        regno = staticfinalObj.getString("registratioNo");
//                                        chassisno = staticfinalObj.getString("chassisNumber");
//                                        makeval = staticfinalObj.getString("make");
//                                        modelval = staticfinalObj.getString("model");
//                                        yearmfg = "2012";
                                            insurername = staticfinalObj.getString("insuredName");
                                            memcompany = staticfinalObj.getString("memberCompanyName");
                                            enginenumber = staticfinalObj.getString("engineNumber");
                                            insuredpin = staticfinalObj.getString("insuredPIN");
//
//
                                            int testnum = Integer.parseInt(String.valueOf(staticfinalObj.getInt("sumInsured")));

                                            suminsured = String.valueOf(testnum);
                                            tonnage = staticfinalObj.getString("tonnage");
                                            passengercount = staticfinalObj.getString("passengersCount");
                                            if (staticfinalObj.getString("insuredPhoneNumber") == "null" || staticfinalObj.getString("insuredPhoneNumber") == null) {
                                                insuredphnno = "";
                                            } else {
                                                try {
                                                    insuredphnno = staticfinalObj.getString("insuredPhoneNumber");
                                                    insuredphnno = insuredphnno.replaceAll("\\s", "");
                                                } catch (Exception e) {
                                                    insuredphnno = "";
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }

                                            }
                                            if (staticfinalObj.getString("insuredEmailID") == "null" || staticfinalObj.getString("insuredEmailID") == null) {
                                                insuredemail = "";
                                            } else {
                                                insuredemail = staticfinalObj.getString("insuredEmailID");
                                            }


                                        } catch (Exception e) {
                                            runOnUiThread(progressdialog::dismiss);
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }


                                        // addvehicleinfo();
//                                        final JSONObject staticfinalObj = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate");
//                                        if(staticfinalObj.getBoolean("isDigitalCertificate"))
//                                        {
//                                            Intent redirect = new Intent(AddVehicle.this, RegistrationStep4.class);
//                                            startActivity(redirect);
//                                        }
//                                        else
//                                        {
//                                            Intent redirect = new Intent(AddVehicle.this, RegistrationNotPhysical.class);
//                                            startActivity(redirect);
//
//                                        }

                                    }


                                } else if (staticJsonObj.getInt("rcode") == 2) {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                    });
                                    RegistrationGlobal.outputregno = "";
                                    RegistrationGlobal.outputexpirydate = "";
                                    RegistrationGlobal.outputprintcode = "";
                                    askVehicleGrievances();
                                } else if (staticJsonObj.getInt("rcode") == 401) {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        unauthorize(AddVehicle.this);
                                    });
                                } else {
                                    runOnUiThread(progressdialog::dismiss);
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    runOnUiThread(() -> {
                                        String errorText;
                                        try {
                                            errorText = index.getString("errorText");
                                            AlertDialog.Builder alert = new AlertDialog.Builder(AddVehicle.this);
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
                                }
                            }

                        } catch (final Exception e) {
                            runOnUiThread(progressdialog::dismiss);
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                            runOnUiThread(() ->
                                    Toast.makeText(AddVehicle.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                        }
                    });
                    thread.start();

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddVehicle.this);
                    dialog.setMessage(R.string.gps_not_enabled);
                    dialog.setPositiveButton("Ok", (dialog1, which) -> {
                        //this will navigate user to the device location settings screen
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    });
                    AlertDialog alert = dialog.create();
                    alert.show();
                }
            } else {
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(AddVehicle.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

//    public void regnummismatch() {
//        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(AddVehicle.this);
//        dialog.setMessage(R.string.number_plate_you_scanned);
//        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//
//            }
//        });
////        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
////
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                //this will navigate user to the device location settings screen
////                dialog.dismiss();
////            }
////        });
//        androidx.appcompat.app.AlertDialog alert = dialog.create();
//        alert.show();
//    }

    public void askVehicleGrievances() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            runOnUiThread(() -> {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AddVehicle.this);
                //dialog.setMessage("Do you like to port the data to this new device?");
                dialog.setMessage(getString(R.string.qr_code_and_expiry_does_not_match));
                dialog.setPositiveButton(R.string.yes_create_grievance, (dialog1, which) -> {
                    if (isUserSignedUp()) {
                        Intent login = new Intent(AddVehicle.this, Grievance.class);
                        login.putExtra("add-vehicle", true);
                        startActivity(login);
                    } else {
                        Intent intent = new Intent(AddVehicle.this, Registration.class);
                        intent.putExtra("add-vehicle", true);
                        startActivity(intent);
                    }
                });
                dialog.setNegativeButton(R.string.no_let_me_correct_info, (dialog12, which) -> {
                    //this will navigate user to the device location settings screen
                    // expirydate.setEnabled(true);
                    dialog12.dismiss();
                });
                AlertDialog alert = dialog.create();
                alert.show();
            });
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

    public void addvehicleinfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    SharedPreferences qrcodePref = getSharedPreferences("QrCodePref", MODE_PRIVATE);
                    if (qrcodePref.getString(ScanCertificate.QrCode, "").startsWith("AKI")) {
                        //  certificatetype = staticfinalObj.getString("typeOfInsurance");
                        //coveragetype= staticfinalObj.getString("typeofCover");
                        policystart = fromdatevalue;
                        policyend = todatevalue;

                        // policystart= digitalpolicystdt.getText().toString();
                        //  policyend = digitalpolicyenddt.getText().toString();
                        //insurername= staticfinalObj.getString("insuredName");
                        // memcompany = staticfinalObj.getString("memberCompanyName");
                    } else {
                        certificateval = digitalcertificateno.getText().toString();
                        regno = regNoField.getText().toString().trim();

                        chassisno = digitalchassisno.getText().toString();
                        policynum = digitalpolicyno.getText().toString();
                        //  certificatetype = staticfinalObj.getString("typeOfInsurance");
                        //coveragetype= staticfinalObj.getString("typeofCover");
                        policystart = fromdatevalue;
                        policyend = todatevalue;

                        // policystart= digitalpolicystdt.getText().toString();
                        //  policyend = digitalpolicyenddt.getText().toString();
                        makeval = digitalmake.getText().toString();
                        modelval = digitalmodel.getText().toString();
                        yearmfg = digitalyear.getText().toString();
                        //insurername= staticfinalObj.getString("insuredName");
                        // memcompany = staticfinalObj.getString("memberCompanyName");
                    }


                    if (certificateval == null || certificateval == "" || certificateval.length() == 0) {
                        Toast.makeText(AddVehicle.this, getString(R.string.certificateno), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (certificateval.length() < 8) {
                        Toast.makeText(AddVehicle.this, getString(R.string.certificatemin), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (certificateval.length() > 12) {
                        Toast.makeText(AddVehicle.this, getString(R.string.certificatemax), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (policynum == null || policynum == "" || policynum.length() == 0) {
                        Toast.makeText(AddVehicle.this, getString(R.string.policyno), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (regno == null || regno == "" || regno.length() == 0) {
                        Toast.makeText(AddVehicle.this, getString(R.string.regno), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (regno.length() < 5) {
                        Toast.makeText(AddVehicle.this, getString(R.string.regmin), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (regno.length() > 12) {
                        Toast.makeText(AddVehicle.this, getString(R.string.regmax), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (chassisno == null || chassisno == "" || chassisno.length() == 0) {
                        Toast.makeText(AddVehicle.this, getString(R.string.chassisno), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (chassisno.length() < 3) {
                        Toast.makeText(AddVehicle.this, getString(R.string.chassismin), Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    else if (chassisno.length() > 12) {
//                        Toast.makeText(AddVehicle.this, getString(R.string.chassismax), Toast.LENGTH_SHORT).show();
//                        return;
//                    }

//                    else if (policynum.length() < 8) {
//                        Toast.makeText(AddVehicle.this, getString(R.string.policymin), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    else if (policynum.length() > 12) {
//                        Toast.makeText(AddVehicle.this, getString(R.string.policymax), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    else if (policystart == null || policystart == "" || policystart.length() == 0) {
                        Toast.makeText(AddVehicle.this, getString(R.string.policystartno), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (policyend == null || policyend == "" || policyend.length() == 0) {
                        Toast.makeText(AddVehicle.this, getString(R.string.policyendno), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String dtStart = fromdatevalue;
                    String dtend = todatevalue;
                    SimpleDateFormat formatstart = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    SimpleDateFormat formatend = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    try {
                        datefrom = formatstart.parse(dtStart);
                        dateto = formatend.parse(dtend);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                        startdate = formatter.format(Date.parse(String.valueOf(datefrom)));
                        enddate = formatter.format(Date.parse(String.valueOf(dateto)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    if (datefrom.compareTo(dateto) > 0) {
                        Toast.makeText(AddVehicle.this, getString(R.string.greaterdate), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (makeval == null || makeval == "" || makeval.length() == 0) {
                        Toast.makeText(AddVehicle.this, getString(R.string.makeno), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (modelval == null || modelval == "" || modelval.length() == 0) {
                        Toast.makeText(AddVehicle.this, getString(R.string.modelno), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (yearmfg == null || yearmfg == "" || yearmfg.length() == 0) {
                        Toast.makeText(AddVehicle.this, getString(R.string.yearno), Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    else if (yearmfg.length() != 4) {
//                        Toast.makeText(AddVehicle.this, getString(R.string.yearminmax), Toast.LENGTH_SHORT).show();
//                        return;
//                    }

                    if (yearmfg.equals("")) {

                    } else {
                        String yearval = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                        String yearval1 = yearval;
                        int yearcom = Integer.parseInt(yearval);
                        int testyear = Integer.parseInt(yearmfg);
                        if (yearmfg.length() != 4) {
                            Toast.makeText(AddVehicle.this, getString(R.string.yearminmax), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (testyear <= 1990 || testyear > yearcom) {
                            Toast.makeText(AddVehicle.this, getString(R.string.yearinbetween), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    mydb = new DatabaseHelper(AddVehicle.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }

//                    encryptedSHA = "";
//                    String sourceStr = MainActivity.InsertMobileparameters();
//                    try {
//                        encryptedSHA = AESUtils.encrypt(sourceStr);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    Thread thread = new Thread(() -> {

                        SharedPreferences certPref = getSharedPreferences("CertficiatePref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = certPref.edit();
                        editor.putString(certificatenumaddriver, certificateval);
                        editor.putString(regnumadddriver, regno);
                        editor.commit();
                        SharedPreferences certificatenum = getSharedPreferences("CertificateNum", Context.MODE_PRIVATE);
                        SharedPreferences.Editor certificatenumeeditor = certificatenum.edit();
                        certificatenumeeditor.putString(AddVehicle.CertNum, certificateval);
                        certificatenumeeditor.commit();

                        postURL = getString(R.string.uaturl) + "/app/Vehicle/AddVehicle";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        String dtStart1 = policystart;
                        String dtend1 = policyend;
                        SimpleDateFormat formatstart1 = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                        SimpleDateFormat formatend1 = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                        try {
                            datefrompolicy = formatstart1.parse(dtStart1);
                            datetopolicy = formatend1.parse(dtend1);
                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                            startdate = formatter.format(Date.parse(String.valueOf(datefrompolicy)));
                            enddate = formatter.format(Date.parse(String.valueOf(datetopolicy)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        //OkHttpClient client = new OkHttpClient();
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        JsonObject Details = new JsonObject();
                        Details.addProperty("imageAttachmentID", vechieattachid);
                        Details.addProperty("ownVehicleID", VehicleOwnership.ownVehicleval);
                        Details.addProperty("authorizedID", VehicleOwnership.authorizedval);
                        Details.addProperty("certificateNo", certificateval);
                        Details.addProperty("policyNo", policynum);
                        if (certificatetype == "null") {
                            Details.addProperty("certificateType", "");
                        } else {
                            Details.addProperty("certificateType", certificatetype);
                        }
                        // Details.addProperty("certificateType",certificatetype);
                        Details.addProperty("coverageType", coveragetype);
                        Details.addProperty("policyBeginDate", startdate);
                        Details.addProperty("policyEndDate", enddate);
                        Details.addProperty("registrationNo", regno);
                        Details.addProperty("vINNumber", chassisno);
                        Details.addProperty("make", makeval);
                        Details.addProperty("model", modelval);
                        Details.addProperty("yearOfMfg", yearmfg);
                        Details.addProperty("insurerID", insuredid);
                        Details.addProperty("insuredName", insurername);
                        Details.addProperty("insuredNationalID", "");
//                            if(insuredpin == ""|| insuredpin == "null" || insuredpin == null)
//                            {
//                                Details.addProperty("insuredPIN","A123456789X");
//                            }
//                            else
//                            {
//                                Details.addProperty("insuredPIN",insuredpin);
//                            }
                        Details.addProperty("insuredPIN", insuredpin);
                        Details.addProperty("insurerName", "");
                        Details.addProperty("sumInsured", suminsured);
                        Details.addProperty("tonnage", tonnage);
                        Details.addProperty("passengersCount", passengercount);
                        Details.addProperty("insuredMailId", insuredemail);
                        Details.addProperty("insuredPhoneNo", insuredphnno);
                        Details.addProperty("insurerName", memcompany);
                        Details.addProperty("engineNumber", enginenumber);
                        Details.addProperty("speedoMeterReading", "");
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(postURL)
                                .header("Authorization", "Bearer " + stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse;

                        try {
                            runOnUiThread(() ->
                                    progressdialog = ProgressDialog.show(activityAddVehicle, getString(R.string.loading), getString(R.string.please), true));
                            staticResponse = client.newCall(request).execute();
                            int statuscode = staticResponse.code();
                            if (statuscode == 401) {
                                runOnUiThread(() -> {
                                    progressdialog.dismiss();
                                    unauthorize(AddVehicle.this);
                                });
                            } else {
                                try {
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

                                        if (certstatuval.contains("Cancelled")) {
                                            runOnUiThread(() -> {
                                                progressdialog.dismiss();
                                                cancelledcert();
                                            });
                                        } else {
                                            String loadlocaldata = "1";
                                            //insertnumberplateimg();
                                            mydb = new DatabaseHelper(AddVehicle.this);
                                            boolean regstepinserted = mydb.insertregstep("Step1");
                                            if (regstepinserted) {
                                                boolean test = regstepinserted;
                                                Log.i(null, "Insertion Done");
                                            } else {
                                                boolean test = regstepinserted;
                                                Log.i(null, "Not Insertion Done");
                                            }
                                            runOnUiThread(() -> progressdialog.dismiss());
                                            qrcodelay = 0;
                                            SharedPreferences Vechnewpref = getSharedPreferences("VehicleNewID", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor vecheprefednew = Vechnewpref.edit();
                                            vecheprefednew.putString("Vechidshow", staticJsonObj.getJSONObject("rObj").getString("vehicleRefID"));
                                            vecheprefednew.commit();
                                            SharedPreferences sharedPreferencenew = getSharedPreferences("IsCreateDriver", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editornew = sharedPreferencenew.edit();
                                            editornew.putString("CreateDriverRedirect", "1");
                                            editornew.commit();
                                            SharedPreferences VechPreferencenew = getSharedPreferences("GenerateVehiclePDF", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor vecheditornew = VechPreferencenew.edit();
                                            vecheditornew.putString("VechPDFID", staticJsonObj.getJSONObject("rObj").getString("vehicleID"));
                                            vecheditornew.commit();
                                            Intent redirect = new Intent(AddVehicle.this, DriverMapping.class);
                                            startActivity(redirect);
                                        }


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

                                    else if (staticJsonObj.getInt("rcode") == 401) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                unauthorize(AddVehicle.this);
                                            }
                                        });
                                    } else {
                                        runOnUiThread(progressdialog::dismiss);
                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                        JSONObject index = rmsg.getJSONObject(0);
                                        runOnUiThread(() -> {
                                            String errorText;
                                            try {
                                                errorText = index.getString("errorText");
                                                AlertDialog.Builder alert = new AlertDialog.Builder(AddVehicle.this);
                                                alert.setCancelable(false);
                                                alert.setMessage(errorText);
                                                alert.setNegativeButton("Ok", (dialog, which) -> {
                                                    if (progressdialog.isShowing()) {
                                                        progressdialog.dismiss();
                                                    }
                                                    dialog.dismiss();
                                                });
                                                alert.show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            }
                        } catch (final Exception e) {
                            progressdialog.dismiss();
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                            runOnUiThread(() -> Toast.makeText(AddVehicle.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                        } finally {
                            runOnUiThread(() -> {
                                if (progressdialog.isShowing()) {
                                    progressdialog.dismiss();
                                }
                            });
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddVehicle.this);
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
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(AddVehicle.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

    public void insertnumberplateimg() {

        AsyncTask.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {

                try {
                    if (isNetworkConnected()) {

                        if (checkGPSStatus()) {

                            mydb = new DatabaseHelper(AddVehicle.this);
                            if (mydb.getTokendetails().getCount() != 0) {
                                Cursor curseattachtoken = mydb.getTokendetails();
                                int counttoken = curseattachtoken.getCount();
                                if (counttoken >= 1) {
                                    while (curseattachtoken.moveToNext()) {
                                        stokenval = curseattachtoken.getString(1);
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
                            SharedPreferences modelPref = getSharedPreferences("CertficiatePref", Context.MODE_PRIVATE);
                            final String certnum = modelPref.getString(AddVehicle.certificatenumaddriver, null);
                            SharedPreferences locationPref = activityAddVehicle.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
                            // dateTimenew = localToGMT();
                            String dateTime = localToGMT();
                            Thread thread = new Thread(new Runnable() {

                                public void run() {
                                    postURL = getString(R.string.uaturl) + "/app/Vehicle/UpdateVehicleAttachment";
                                    final MediaType JSON
                                            = MediaType.parse("application/json; charset=utf-8");

                                    OkHttpClient client = new OkHttpClient();
                                    JsonObject Details = new JsonObject();
                                    Details.addProperty("certificateNo", certnum);
                                    Details.addProperty("captureAttachmentID", UUID.randomUUID().toString());
                                    Details.addProperty("captureDateTime", dateTime);
                                    Details.addProperty("geoTagLat", locationPref.getString(MainActivity.Latitude, null));
                                    Details.addProperty("geoTagLon", locationPref.getString(MainActivity.Longitude, null));
                                    Details.addProperty("description", "");
                                    Details.addProperty("attachmentTypeID", "bccd1611-f2e7-4f6b-95b4-4d6d864193d4");
                                    Details.addProperty("ImageURL", numplateimageurl);
                                    Details.addProperty("attachmentRefNo", numplateattachmentRefNo);
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
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                progressdialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
//                                                // progressdialog.show(activity, "Loading", "Please wait...", true);
//                                            }
//                                        });
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
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    progressdialog.dismiss();
//                                                }
//                                            });
                                            //DriverMappingAdapter.drivermaplist.clear();

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
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        progressdialog.dismiss();
//
//                                                    }
//                                                });
                                        }
                                    } catch (final IOException ex) {
                                        // progressdialog.dismiss();
                                        ex.printStackTrace();
                                        MainActivity.MobileErrorLog(reqidval, "DriverMapping-insertdrivermap", ex.toString());
                                        mCrashlytics.recordException(ex);
                                        //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                        runOnUiThread(new Runnable() {
                                            public void run() {

                                                Toast.makeText(AddVehicle.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (JSONException ex) {
                                        //  progressdialog.dismiss();
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                        ex.printStackTrace();
                                        MainActivity.MobileErrorLog(reqidval, "DriverMapping-insertdrivermap", ex.toString());
                                        mCrashlytics.recordException(ex);
                                        //  Toast.makeText(DriverMapping.this,ex.toString(), Toast.LENGTH_LONG).show();
                                        //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                                    }
                                }
                            });
                            thread.start();


                        } else {
                            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddVehicle.this);
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
//                        Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    //progressdialog.dismiss();
                    ex.getStackTrace();
                    MainActivity.MobileErrorLog(reqidval, "DriverMapping-insertdrivermap", ex.toString());
                    mCrashlytics.recordException(ex);
//                    Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                }
            }

        });

    }

    private String changeDateFormat(String currentFormat, String requiredFormat, String dateString) {
        String result = "";
        SimpleDateFormat formatterOld = new SimpleDateFormat(currentFormat, Locale.getDefault());
        SimpleDateFormat formatterNew = new SimpleDateFormat(requiredFormat, Locale.getDefault());
        Date date = null;
        try {
            date = formatterOld.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            result = formatterNew.format(date);
        }
        return result;
    }

//    public class RegistrationStep4 {
//        Context context;
//
//        public RegistrationStep4(Context context) {
//            this.context = context;
//        }
//
//
//        public void Update() {
//            EditText txtView = (EditText) ((Activity) context).findViewById(R.id.editvinnumber);
//            txtView.setText("Hello");
//        }
//    }

    public void getinsurancecompany() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    mydb = new DatabaseHelper(AddVehicle.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
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
                    Thread thread = new Thread(() -> {
                        try {

                            postURL = getString(R.string.uaturl) + "/app/MasterData/GetAllinsurer";
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
                            Response staticResponse;

                            try {
                                runOnUiThread(() ->
                                        progressdialog = ProgressDialog.show(activityAddVehicle, getString(R.string.loading), getString(R.string.please), true));
                                staticResponse = client.newCall(request).execute();
                                int statuscode = staticResponse.code();
                                if (statuscode == 401) {
                                    runOnUiThread(() -> {
                                        progressdialog.dismiss();
                                        unauthorize(AddVehicle.this);
                                    });
                                } else {
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
                                        runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            diginsurval.setText("");

//                                                                insurspinedigital = (Spinner) findViewById(R.id.spinnerinsurdigital);
//                                                                insurspinedigital.setEnabled(false);
//                                                                insurspinedigital.setClickable(false);
//                                                                insurspinedigital.setOnItemSelectedListener(AddVehicle.this);
//                                                                ArrayAdapter insunamedigAdapter = new ArrayAdapter(AddVehicle.this,android.R.layout.simple_list_item_1,insurcomspinner);
//                                                                insurspinedigital.setAdapter(insunamedigAdapter);
//                                                                Spinner insurSpinnerVal = (Spinner) findViewById(R.id.spinnerinsurance);
//                                                                insurSpinnerVal.setOnItemSelectedListener(AddVehicle.this);
//                                                                ArrayAdapter insunameAdapter = new ArrayAdapter(AddVehicle.this,android.R.layout.simple_list_item_1,insurcomspinner);
//                                                                insurSpinnerVal.setAdapter(insunameAdapter);
                                            getCertificateInfo();
                                        });

//                                                    Intent redirect = new Intent(AddVehicle.this, RegistrationStep4.class);
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

                                    else if (staticJsonObj.getInt("rcode") == 401) {
                                        runOnUiThread(() -> {
                                            progressdialog.dismiss();
                                            unauthorize(AddVehicle.this);
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
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(AddVehicle.this);
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

                            } catch (final IOException e) {
                                runOnUiThread(progressdialog::dismiss);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                runOnUiThread(() ->
                                        Toast.makeText(AddVehicle.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                            }

                        } catch (Exception e) {
                            runOnUiThread(progressdialog::dismiss);
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddVehicle.this);
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
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void cancelledcert() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddVehicle.this);
        dialog.setMessage("This certificate is cancelled by insurance company and hence cannot be used for adding vehicle to AKI-CRA");
        dialog.setPositiveButton("Ok", (dialog1, which) -> dialog1.dismiss());
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }

//    public static void getOCRReading(Activity activity)
//    {
//
//        AsyncTask.execute(
//
//                new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        try
//                        {
//                            mydb = new DatabaseHelper(activity);
//                            if(mydb.getTokendetails().getCount() != 0) {
//                                Cursor curseattachtoken = mydb.getTokendetails();
//                                int counttoken = curseattachtoken.getCount();
//                                if (counttoken >= 1) {
//                                    while (curseattachtoken.moveToNext()) {
//                                        MainActivity.stokenval = curseattachtoken.getString(1);
//                                        //stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w";
//                                    }
//                                }
//                            }
////                            certilay = (LinearLayout) root.findViewById(R.id.certificatelinearlay);
////                            certilay.setVisibility(View.GONE);
////                            regnolay = (LinearLayout) root.findViewById(R.id.regnumberlinear);
////                            regnolay.setVisibility(View.GONE);
//                            // final TextView resultStr = (TextView)findViewById(R.id.numbresult);
//                            Thread thread = new Thread(new Runnable() {
//                                @RequiresApi(api = Build.VERSION_CODES.O)
//                                public void run() {
//
//                                    numplatscandone = "0";
//                                    numplateprogressstart = "1";
//
//                                    // uploadprog = (ImageView)activity.findViewById(R.id.imageuploading);
//                                    // uploadsucces = (ImageView)activity.findViewById(R.id.imageuploadeddone);
//
//                                    final MediaType mediaType
//                                            = MediaType.parse("image/jpeg");
//                                    // Bitmap bitmap = ObjectDetectorActivity.Global.img;
////                                    Bitmap bitmap = null;
////                                    int versionos = Build.VERSION.SDK_INT;
////                                    if(versionos < 29){
////                                        bitmap   = ObjectDetectorActivity.Global.imgocr;
////                                    }
////                                    else
////                                    {
////                                        bitmap = ObjectDetectorActivity.Global.img;
////                                    }
//                                    Bitmap bitmap   = ObjectDetectorActivity.Global.imgocr;
//                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
////                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
//                                    Log.i(null, String.valueOf(bitmap.getByteCount()));
//                                    byte[] byteArray = stream.toByteArray();
//                                    regByte = byteArray;
//                                    String imgData = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                                    SharedPreferences modelPref = activity.getSharedPreferences("LoadModelView",Context.MODE_PRIVATE);
//                                    final String modelunique = modelPref.getString(ModelID, null);
//                                    if(modelunique.equals("1")) {
//                                        //  Number plate detection API
//                                        //imageURL = "https://prod-27.southcentralus.logic.azure.com/workflows/7d432446b67449ea995f913ef0155595/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=Iugh3MJRWap7ajA9MW4utA7hYKISWn1IkmAwIOSoNXA";
//                                        imageURL = activity.getString(R.string.uaturl) + "/app/Cognitive/LicensePlateReaderNew";
//                                        //imageURL = "https://aki-cra.swiftant.com/api/app/Cognitive/LicensePlateReader";
//                                        imagename = "LicenceImg_" + UUID.randomUUID().toString() + ".png";
//                                    }
//                                    else if(modelunique.equals("6")){
//
//                                        imageURL = activity.getString(R.string.uaturl) + "/app/Cognitive/NationalIDReader";
//                                        imagename = "NationalImg_" + UUID.randomUUID().toString() + ".png";
//                                    }
//                                    else if(modelunique.equals("7")){
//                                        imageURL = activity.getString(R.string.uaturl) + "/app/Cognitive/DLReader";
//                                        imagename = "DLImg_" + UUID.randomUUID().toString() + ".png";
//                                    }
////                                    activity.runOnUiThread(new Runnable() {
////                                        @Override
////                                        public void run() {
////                                            uploadprog.setVisibility(View.VISIBLE);
////                                            uploadsucces.setVisibility(View.GONE);
////                                        }
////                                    });
//
//
//                                    // String imageURL = "https://prod-15.southcentralus.logic.azure.com/workflows/f30978d0e9a748db9802b8c7cba199c2/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=_5GTMDPuBStLcXZoTuw7OJh5Zl6NnilpIa1xQFG4F8M";
//                                    //Certification Detection new API
//                                    SharedPreferences locationPref = context.getSharedPreferences("LocationPref", MODE_PRIVATE);
//                                    //    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
//                                    //  String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
//                                    String dateTime = localToGMT();
//                                    final MediaType JSON
//                                            = MediaType.parse("application/json; charset=utf-8");
//                                    OkHttpClient client = new OkHttpClient.Builder()
//                                            .connectTimeout(120, TimeUnit.SECONDS)
//                                            .writeTimeout(120, TimeUnit.SECONDS)
//                                            .readTimeout(120, TimeUnit.SECONDS)
//                                            .build();
//                                    RequestBody body = new MultipartBody.Builder()
//                                            .setType(MultipartBody.FORM)
//                                            .addFormDataPart("fileName",AddVehicle.imagename)
//                                            .addFormDataPart(
//                                                    "image",AddVehicle.imagename,
//                                                    RequestBody.create(mediaType, regByte))
//                                            .addFormDataPart("certificateNo","")
//                                            .addFormDataPart("incidentUniqueCode","")
//                                            .addFormDataPart("geoTagLat",locationPref.getString(MainActivity.Latitude, null))
//                                            .addFormDataPart("geoTagLon",locationPref.getString(MainActivity.Longitude, null))
//                                            .addFormDataPart("captureAttachmentID",UUID.randomUUID().toString())
//                                            .addFormDataPart("captureDateTime",dateTime)
//                                            .build();
//                                    Request request = new Request.Builder()
//                                            .url(imageURL)
//                                            .header("MobileParameter", MainActivity.InsertMobileparameters())
//                                            .header("Authorization", "Bearer " + MainActivity.stokenval)
//                                            .post(body)
//                                            .build();
//                                    Response staticResponse = null;
//                                    try {
////                                        if(modelunique.equals("1")) {
////                                           // uploadprog.setVisibility(View.VISIBLE);
////                                           // uploadsucces.setVisibility(View.GONE);
//                                        activity.runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                uploadprog.setVisibility(View.VISIBLE);
//                                                uploadsucces.setVisibility(View.GONE);
//                                            }
//                                        });
//                                        staticResponse = client.newCall(request).execute();
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
//                                        int errorcode = staticJsonObj.getInt("rcode");
//                                        if (staticJsonObj.getInt("rcode") == 1)
//                                        {
//                                            numplateimageurl = staticJsonObj.getJSONObject("rObj").getString("ImageURL");
//                                            numplateattachmentRefNo   = staticJsonObj.getJSONObject("rObj").getString("attachmentRefNo");
//                                            activity.runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    try {
//                                                        if(modelunique.equals("1")) {
//                                                            //  Number plate detection API
//                                                            // getnumberplatevisible();
////                                                            final JSONObject staticfinalObj = imageJsonObj.getJSONObject("rObj").getJSONObject("vehicleRegNumber");
////                                                            certificateval = staticfinalObj.getString("certificateNo");
//                                                            //   uploadprog = (ImageView)activity.findViewById(R.id.imageuploading);
//                                                            // uploadsucces = (ImageView)activity.findViewById(R.id.imageuploadeddone);
//
//                                                            uploadprog.setVisibility(View.GONE);
//                                                            uploadsucces.setVisibility(View.VISIBLE);
//                                                            numplatscandone = "1";
//                                                            numplateprogressstart = "0";
//                                                            scannumlin.setVisibility(View.VISIBLE);
//                                                            //  numplascanres = (EditText)activity.findViewById(R.id.editnumberplatecapture);
//                                                            numplascanres.setText(staticJsonObj.getJSONObject("rObj").getString("vehicleRegNumber"));
//                                                            numplatscanvalue = staticJsonObj.getJSONObject("rObj").getString("vehicleRegNumber");
////                                                                try{
////                                                                    String newimgurl = staticJsonObj.getJSONObject("rObj").getString("inputImg");
////                                                                    String imageurl = newimgurl;
////                                                                    InputStream in = null;
////
////                                                                    try
////                                                                    {
////                                                                        Log.i("URL", imageurl);
////                                                                        URL url = new URL(imageurl);
////                                                                        URLConnection urlConn = url.openConnection();
////                                                                        HttpURLConnection httpConn = (HttpURLConnection) urlConn;
////                                                                        httpConn.connect();
////
////                                                                        in = httpConn.getInputStream();
////                                                                    }
////                                                                    catch (MalformedURLException e)
////                                                                    {
////                                                                        e.printStackTrace();
////                                                                    }
////                                                                    catch (IOException e)
////                                                                    {
////                                                                        e.printStackTrace();
////                                                                    }
////                                                                    Bitmap bmpimg = BitmapFactory.decodeStream(in);
////
////                                                                    mydb = new DatabaseHelper(activity);
////                                                                    Cursor curseattachuser = mydb.getUserdetails();
////                                                                    int countuser = curseattachuser.getCount();
////                                                                    if (countuser >= 1) {
////                                                                        while (curseattachuser.moveToNext()) {
////                                                                            membercraid = curseattachuser.getString(3);
////                                                                        }
////                                                                    }
////                                                                    SharedPreferences locationPref = activity.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
////                                                                    final String latitude = locationPref.getString(MainActivity.Latitude, null);
////                                                                    final String longitude = locationPref.getString(MainActivity.Longitude, null);
////                                                                    Bitmap workingBitmap = bmpimg;
////                                                                    Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
////                                                                    Matrix matrix = new Matrix();
////                                                                    matrix.postRotate(90);
////                                                                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(mutableBitmap, mutableBitmap.getWidth(), mutableBitmap.getHeight(), true);
////                                                                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
////                                                                    Bitmap dest = rotatedBitmap;
////                                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////                                                                    String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
////                                                                    String imagevaluebind = "CRA Android";
////                                                                    String imagevaluebind1 = dateTime;
////                                                                    String imagevaluebind2 = UUID.randomUUID().toString();
////                                                                    String imagevaluebind3 = latitude + " / " + longitude;
////                                                                    String imagevaluebind4 = membercraid;
//////                                                                    Canvas csnew = new Canvas(dest);
//////                                                                    Paint myPaint = new Paint();
//////                                                                    myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//////                                                                    myPaint.setColor(Color.WHITE);
//////                                                                    myPaint.setStrokeWidth(10);
//////                                                                    csnew.drawRect(0, 0, 100, 100, myPaint);
////
////                                                                    Bitmap destnewlatest = dest;
////                                                                    Canvas cs = new Canvas(destnewlatest);
////                                                                    Paint tPaint = new Paint();
////                                                                    tPaint.setColor(Color.RED);
////                                                                    int fontSize = 17;
////                                                                    tPaint.setTextSize(fontSize);
////                                                                    tPaint.setTextAlign(Paint.Align.LEFT);
////                                                                    cs.drawText(imagevaluebind, 0, (0 + tPaint.getTextSize()), tPaint);
////                                                                    cs.drawText(imagevaluebind1, 0, (100 + tPaint.getTextSize()), tPaint);
////                                                                    cs.drawText(imagevaluebind2, 0, (200 + tPaint.getTextSize()), tPaint);
////                                                                    cs.drawText(imagevaluebind3 + " , " + imagevaluebind4, 0, (300 + tPaint.getTextSize()), tPaint);
////                                                                    Bitmap bitmap = destnewlatest;
////                                                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
////                                                                    Log.i(null, String.valueOf(bitmap.getByteCount()));
////
////                                                                    mydb = new DatabaseHelper(activity);
////                                                                    if (mydb.getTokendetails().getCount() != 0) {
////                                                                        Cursor curseattachtoken = mydb.getTokendetails();
////                                                                        int counttoken = curseattachtoken.getCount();
////                                                                        if (counttoken >= 1) {
////                                                                            while (curseattachtoken.moveToNext()) {
////                                                                                MainActivity.stokenval = curseattachtoken.getString(1);
////                                                                            }
////                                                                        }
////                                                                    }
//                                                            //New Bitmap code compression
////                            Bitmap newBitmapcomp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
////                            Bitmap bitmapnew = newBitmapcomp;
////                            ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
////                            bitmapnew.compress(Bitmap.CompressFormat.JPEG, 50, streamnew);
////                            String outStr = ObjectDetectorActivity.Global.outputStr;
////                            Log.i(null, String.valueOf(bitmap.getByteCount()));
////                            byte[] byteArraynew = streamnew.toByteArray();
//
//                                                            //Old Comppression Method
////                                                                    Bitmap bitmapnew = bitmap;
////                                                                    ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
////                                                                    bitmapnew.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
////                                                                    String outStr = ObjectDetectorActivity.Global.outputStr;
////                                                                    Log.i(null, String.valueOf(bitmap.getByteCount()));
////                                                                    byte[] byteArraynew = streamnew.toByteArray();
////                                                                    ObjectDetectorActivity.Global.img = bitmapnew;
//                                                            // MainActivity.uploadimages(activity);
////                                                                }
////                                                                catch(Exception ex)
////                                                                {
////
////                                                                }
////                                                            final JSONObject result = imageJsonObj.getJSONObject("callBackObj");
////                                                            String regnoval = result.getString("ExtractNumber");
//                                                            //  ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
//                                                            // ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//
//                                                            //license.setText(regnoval);
//                                                        }
//                                                        else if(modelunique.equals("3")){
//
//                                                            final JSONObject result = staticJsonObj.getJSONObject("callBackObj");
//                                                            String vinnoval = result.getString("ExtractNumber");
//                                                            // RegistrationStep4.vinnumberbind(context);
//                                                            // editvin.setText(vinnoval);
//
//                                                        }
//                                                        else if(modelunique.equals("7")){
//                                                            final JSONObject result = staticJsonObj.getJSONObject("rObj").getJSONObject("DLInfo");
//                                                            String licenno = result.getString("licenseNo");
//                                                            String licenno1 = licenno;
//                                                            // RegistrationStep4.vinnumberbind(context);
//                                                            // editvin.setText(vinnoval);
//
//                                                        }
//                                                    }
//                                                    catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                        mCrashlytics.recordException(e);
//                                                        MainActivity.MobileErrorLog(reqidval,"AddVehicle-getOCRReading",e.toString(),staticRes);
//                                                    }
//                                                }
//                                            });
//                                        }
//                                        else if(staticJsonObj.getInt("rcode") == 2)
//                                        {
//                                            numplateimageurl = staticJsonObj.getJSONObject("rObj").getString("ImageURL");
//                                            numplateattachmentRefNo   = staticJsonObj.getJSONObject("rObj").getString("attachmentRefNo");
//                                            activity.runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    //  uploadprog.setVisibility(View.GONE);
//                                                    uploadprog.setVisibility(View.GONE);
//                                                    uploadsucces.setVisibility(View.VISIBLE);
//                                                    numplatscandone = "1";
//                                                    numplateprogressstart = "0";
//                                                    scannumlin.setVisibility(View.VISIBLE);
//                                                    try {
//                                                        Toast.makeText(activity, staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText") + ".Please rescan or enter it manually" , Toast.LENGTH_SHORT).show();
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                        mCrashlytics.recordException(e);
//                                                        MainActivity.MobileErrorLog(reqidval,"AddVehicle-getOCRReading",e.toString(),staticRes);
////                                                        activity.runOnUiThread(new Runnable() {
////                                                            @Override
////                                                            public void run() {
////                                                                Toast.makeText(activity, e.toString() , Toast.LENGTH_SHORT).show();
////                                                            }
////                                                        });
//                                                    }
//                                                }
//                                            });
//
//                                        }
//                                        else
//                                        {
//                                            activity.runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    //  uploadprog.setVisibility(View.GONE);
//                                                    uploadprog.setVisibility(View.GONE);
//                                                    uploadsucces.setVisibility(View.VISIBLE);
//                                                    numplatscandone = "1";
//                                                    numplateprogressstart = "0";
//                                                    scannumlin.setVisibility(View.VISIBLE);
//                                                    try {
//                                                        Toast.makeText(activity, staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText") + ".Please rescan or enter it manually" , Toast.LENGTH_SHORT).show();
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                        mCrashlytics.recordException(e);
//                                                        MainActivity.MobileErrorLog(reqidval,"AddVehicle-getOCRReading",e.toString(),staticRes);
////                                                        activity.runOnUiThread(new Runnable() {
////                                                            @Override
////                                                            public void run() {
////                                                                Toast.makeText(activity, e.toString() , Toast.LENGTH_SHORT).show();
////                                                            }
////                                                        });
//                                                    }
//                                                }
//                                            });
//                                        }
//                                    } catch (final IOException e) {
//                                        e.printStackTrace();
//                                        MainActivity.MobileErrorLog(reqidval,"AddVehicle-getOCRReading",e.toString(),MainActivity.stokenval);
////                                        activity.runOnUiThread(new Runnable() {
////                                            @Override
////                                            public void run() {
////                                                Toast.makeText(activity, e.toString() , Toast.LENGTH_SHORT).show();
////                                            }
////                                        });
//                                    } catch (final JSONException e) {
//                                        e.printStackTrace();
//                                        mCrashlytics.recordException(e);
//                                        MainActivity.MobileErrorLog(reqidval,"AddVehicle-getOCRReading",e.toString(),MainActivity.stokenval);
////                                        activity.runOnUiThread(new Runnable() {
////                                            @Override
////                                            public void run() {
////                                                Toast.makeText(activity, e.toString() , Toast.LENGTH_SHORT).show();
////                                            }
////                                        });
//                                    }
//                                }
//                            });
//                            thread.start();
//                        }
//
//                        catch (Exception e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                            mCrashlytics.recordException(e);
//                            MainActivity.MobileErrorLog(reqidval,"AddVehicle-getOCRReading",e.toString(),MainActivity.stokenval);
////                            activity.runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    Toast.makeText(activity, e.toString() , Toast.LENGTH_SHORT).show();
////                                }
////                            });
//                        }
//
//                    }
//                });
//
//    }

    /*public void unauthorize() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddVehicle.this);
        dialog.setMessage("Your session have been expired. Please login again to continue");
        dialog.setPositiveButton("Ok", (dialog1, which) -> {
            mydb = new DatabaseHelper(AddVehicle.this);
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
            Intent login = new Intent(AddVehicle.this, Dashboard.class);
            startActivity(login);
        });
        dialog.setCancelable(false);
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }*/

    public void notvalidvechicle(String errortxt) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddVehicle.this);
        dialog.setMessage(errortxt);
        dialog.setPositiveButton("Ok", (dialog1, which) -> dialog1.dismiss());
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }

    public void getnumberplatevisible() {

    }


//    public static void getOCRReading(Activity activity)
//    {
//
//        AsyncTask.execute(
//
//                new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        try
//                        {
//                            mydb = new DatabaseHelper(activity);
//                            if(mydb.getTokendetails().getCount() != 0) {
//                                Cursor curseattachtoken = mydb.getTokendetails();
//                                int counttoken = curseattachtoken.getCount();
//                                if (counttoken >= 1) {
//                                    while (curseattachtoken.moveToNext()) {
//                                        MainActivity.stokenval = curseattachtoken.getString(1);
//                                        //stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w";
//                                    }
//                                }
//                            }
////                            certilay = (LinearLayout) root.findViewById(R.id.certificatelinearlay);
////                            certilay.setVisibility(View.GONE);
////                            regnolay = (LinearLayout) root.findViewById(R.id.regnumberlinear);
////                            regnolay.setVisibility(View.GONE);
//                            // final TextView resultStr = (TextView)findViewById(R.id.numbresult);
//                            Thread thread = new Thread(new Runnable() {
//                                public void run() {
//
//                                    numplatscandone = "0";
//                                    numplateprogressstart = "1";
//
//                                   // uploadprog = (ImageView)activity.findViewById(R.id.imageuploading);
//                                   // uploadsucces = (ImageView)activity.findViewById(R.id.imageuploadeddone);
//
//                                    final MediaType mediaType
//                                            = MediaType.parse("image/jpeg");
//                                   // Bitmap bitmap = ObjectDetectorActivity.Global.img;
////                                    Bitmap bitmap = null;
////                                    int versionos = Build.VERSION.SDK_INT;
////                                    if(versionos < 29){
////                                        bitmap   = ObjectDetectorActivity.Global.imgocr;
////                                    }
////                                    else
////                                    {
////                                        bitmap = ObjectDetectorActivity.Global.img;
////                                    }
//                                    Bitmap bitmap   = ObjectDetectorActivity.Global.imgocr;
//                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
////                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
//                                    String outStr = ObjectDetectorActivity.Global.outputStr;
//                                    Log.i(null, String.valueOf(bitmap.getByteCount()));
//                                    byte[] byteArray = stream.toByteArray();
//                                    regByte = byteArray;
//                                    String imgData = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                                    SharedPreferences modelPref = activity.getSharedPreferences("LoadModelView",Context.MODE_PRIVATE);
//                                    final String modelunique = modelPref.getString(ModelID, null);
//                                    if(modelunique.equals("1")) {
//                                        //  Number plate detection API
//                                        //imageURL = "https://prod-27.southcentralus.logic.azure.com/workflows/7d432446b67449ea995f913ef0155595/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=Iugh3MJRWap7ajA9MW4utA7hYKISWn1IkmAwIOSoNXA";
//                                        imageURL = activity.getString(R.string.uaturl) + "/app/Cognitive/LicensePlateReader";
//                                        //imageURL = "https://aki-cra.swiftant.com/api/app/Cognitive/LicensePlateReader";
//                                        imagename = "LicenceImg_" + UUID.randomUUID().toString() + ".png";
//                                    }
//                                    else if(modelunique.equals("6")){
//
//                                        imageURL = activity.getString(R.string.uaturl) + "/app/Cognitive/NationalIDReader";
//                                        imagename = "NationalImg_" + UUID.randomUUID().toString() + ".png";
//                                    }
//                                    else if(modelunique.equals("7")){
//                                        imageURL = activity.getString(R.string.uaturl) + "/app/Cognitive/DLReader";
//                                        imagename = "DLImg_" + UUID.randomUUID().toString() + ".png";
//                                    }
////                                    activity.runOnUiThread(new Runnable() {
////                                        @Override
////                                        public void run() {
////                                            uploadprog.setVisibility(View.VISIBLE);
////                                            uploadsucces.setVisibility(View.GONE);
////                                        }
////                                    });
//
//
//                                    // String imageURL = "https://prod-15.southcentralus.logic.azure.com/workflows/f30978d0e9a748db9802b8c7cba199c2/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=_5GTMDPuBStLcXZoTuw7OJh5Zl6NnilpIa1xQFG4F8M";
//                                    //Certification Detection new API
//                                    SharedPreferences locationPref = context.getSharedPreferences("LocationPref", MODE_PRIVATE);
//                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
//                                    String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
//                                    final MediaType JSON
//                                            = MediaType.parse("application/json; charset=utf-8");
//                                    OkHttpClient client = new OkHttpClient.Builder()
//                                            .connectTimeout(120, TimeUnit.SECONDS)
//                                            .writeTimeout(120, TimeUnit.SECONDS)
//                                            .readTimeout(120, TimeUnit.SECONDS)
//                                            .build();
//                                    JsonObject Details = new JsonObject();
//                                    Details.addProperty("fileName",imagename);
//                                    Details.addProperty("incidentUniqueCode","");
//                                    Details.addProperty("certificateNo","1231");
//                                    Details.addProperty("geoTagLat",locationPref.getString(MainActivity.Latitude, null));
//                                    Details.addProperty("geoTagLon",locationPref.getString(MainActivity.Longitude, null));
//                                    Details.addProperty("captureAttachmentID",UUID.randomUUID().toString());
//                                    Details.addProperty("captureDateTime",dateTime);
//                                    Details.addProperty("image",imgData);
//                                    String insertString = Details.toString();
//                                    RequestBody body = RequestBody.create(JSON, insertString);
//                                    Request request = new Request.Builder()
//                                            .url(imageURL)
//                                            .header("MobileParameter", MainActivity.InsertMobileparameters())
//                                            .header("Authorization", "Bearer " + MainActivity.stokenval)
//                                            .post(body)
//                                            .build();
//                                    Response staticResponse = null;
//                                    try {
////                                        if(modelunique.equals("1")) {
////                                           // uploadprog.setVisibility(View.VISIBLE);
////                                           // uploadsucces.setVisibility(View.GONE);
//                                           activity.runOnUiThread(new Runnable() {
//                                                @Override
//                                              public void run() {
//                                                    uploadprog.setVisibility(View.VISIBLE);
//                                                    uploadsucces.setVisibility(View.GONE);
//                                             }
//                                           });
//                                        staticResponse = client.newCall(request).execute();
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
//                                        int errorcode = staticJsonObj.getInt("rcode");
//                                        if (staticJsonObj.getInt("rcode") == 1)
//                                        {
//                                            activity.runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    try {
//                                                        if(modelunique.equals("1")) {
//                                                            //  Number plate detection API
//                                                           // getnumberplatevisible();
////                                                            final JSONObject staticfinalObj = imageJsonObj.getJSONObject("rObj").getJSONObject("vehicleRegNumber");
////                                                            certificateval = staticfinalObj.getString("certificateNo");
//                                                         //   uploadprog = (ImageView)activity.findViewById(R.id.imageuploading);
//                                                           // uploadsucces = (ImageView)activity.findViewById(R.id.imageuploadeddone);
//
//                                                                uploadprog.setVisibility(View.GONE);
//                                                                uploadsucces.setVisibility(View.VISIBLE);
//                                                                numplatscandone = "1";
//                                                             numplateprogressstart = "0";
//                                                                scannumlin.setVisibility(View.VISIBLE);
//                                                                //  numplascanres = (EditText)activity.findViewById(R.id.editnumberplatecapture);
//                                                                numplascanres.setText(staticJsonObj.getJSONObject("rObj").getString("vehicleRegNumber"));
//                                                                numplatscanvalue = staticJsonObj.getJSONObject("rObj").getString("vehicleRegNumber");
////                                                                try{
////                                                                    String newimgurl = staticJsonObj.getJSONObject("rObj").getString("inputImg");
////                                                                    String imageurl = newimgurl;
////                                                                    InputStream in = null;
////
////                                                                    try
////                                                                    {
////                                                                        Log.i("URL", imageurl);
////                                                                        URL url = new URL(imageurl);
////                                                                        URLConnection urlConn = url.openConnection();
////                                                                        HttpURLConnection httpConn = (HttpURLConnection) urlConn;
////                                                                        httpConn.connect();
////
////                                                                        in = httpConn.getInputStream();
////                                                                    }
////                                                                    catch (MalformedURLException e)
////                                                                    {
////                                                                        e.printStackTrace();
////                                                                    }
////                                                                    catch (IOException e)
////                                                                    {
////                                                                        e.printStackTrace();
////                                                                    }
////                                                                    Bitmap bmpimg = BitmapFactory.decodeStream(in);
////
////                                                                    mydb = new DatabaseHelper(activity);
////                                                                    Cursor curseattachuser = mydb.getUserdetails();
////                                                                    int countuser = curseattachuser.getCount();
////                                                                    if (countuser >= 1) {
////                                                                        while (curseattachuser.moveToNext()) {
////                                                                            membercraid = curseattachuser.getString(3);
////                                                                        }
////                                                                    }
////                                                                    SharedPreferences locationPref = activity.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
////                                                                    final String latitude = locationPref.getString(MainActivity.Latitude, null);
////                                                                    final String longitude = locationPref.getString(MainActivity.Longitude, null);
////                                                                    Bitmap workingBitmap = bmpimg;
////                                                                    Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
////                                                                    Matrix matrix = new Matrix();
////                                                                    matrix.postRotate(90);
////                                                                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(mutableBitmap, mutableBitmap.getWidth(), mutableBitmap.getHeight(), true);
////                                                                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
////                                                                    Bitmap dest = rotatedBitmap;
////                                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////                                                                    String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
////                                                                    String imagevaluebind = "CRA Android";
////                                                                    String imagevaluebind1 = dateTime;
////                                                                    String imagevaluebind2 = UUID.randomUUID().toString();
////                                                                    String imagevaluebind3 = latitude + " / " + longitude;
////                                                                    String imagevaluebind4 = membercraid;
//////                                                                    Canvas csnew = new Canvas(dest);
//////                                                                    Paint myPaint = new Paint();
//////                                                                    myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//////                                                                    myPaint.setColor(Color.WHITE);
//////                                                                    myPaint.setStrokeWidth(10);
//////                                                                    csnew.drawRect(0, 0, 100, 100, myPaint);
////
////                                                                    Bitmap destnewlatest = dest;
////                                                                    Canvas cs = new Canvas(destnewlatest);
////                                                                    Paint tPaint = new Paint();
////                                                                    tPaint.setColor(Color.RED);
////                                                                    int fontSize = 17;
////                                                                    tPaint.setTextSize(fontSize);
////                                                                    tPaint.setTextAlign(Paint.Align.LEFT);
////                                                                    cs.drawText(imagevaluebind, 0, (0 + tPaint.getTextSize()), tPaint);
////                                                                    cs.drawText(imagevaluebind1, 0, (100 + tPaint.getTextSize()), tPaint);
////                                                                    cs.drawText(imagevaluebind2, 0, (200 + tPaint.getTextSize()), tPaint);
////                                                                    cs.drawText(imagevaluebind3 + " , " + imagevaluebind4, 0, (300 + tPaint.getTextSize()), tPaint);
////                                                                    Bitmap bitmap = destnewlatest;
////                                                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
////                                                                    Log.i(null, String.valueOf(bitmap.getByteCount()));
////
////                                                                    mydb = new DatabaseHelper(activity);
////                                                                    if (mydb.getTokendetails().getCount() != 0) {
////                                                                        Cursor curseattachtoken = mydb.getTokendetails();
////                                                                        int counttoken = curseattachtoken.getCount();
////                                                                        if (counttoken >= 1) {
////                                                                            while (curseattachtoken.moveToNext()) {
////                                                                                MainActivity.stokenval = curseattachtoken.getString(1);
////                                                                            }
////                                                                        }
////                                                                    }
//                                                                    //New Bitmap code compression
////                            Bitmap newBitmapcomp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
////                            Bitmap bitmapnew = newBitmapcomp;
////                            ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
////                            bitmapnew.compress(Bitmap.CompressFormat.JPEG, 50, streamnew);
////                            String outStr = ObjectDetectorActivity.Global.outputStr;
////                            Log.i(null, String.valueOf(bitmap.getByteCount()));
////                            byte[] byteArraynew = streamnew.toByteArray();
//
//                                                                    //Old Comppression Method
////                                                                    Bitmap bitmapnew = bitmap;
////                                                                    ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
////                                                                    bitmapnew.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
////                                                                    String outStr = ObjectDetectorActivity.Global.outputStr;
////                                                                    Log.i(null, String.valueOf(bitmap.getByteCount()));
////                                                                    byte[] byteArraynew = streamnew.toByteArray();
////                                                                    ObjectDetectorActivity.Global.img = bitmapnew;
//                                                                   // MainActivity.uploadimages(activity);
////                                                                }
////                                                                catch(Exception ex)
////                                                                {
////
////                                                                }
////                                                            final JSONObject result = imageJsonObj.getJSONObject("callBackObj");
////                                                            String regnoval = result.getString("ExtractNumber");
//                                                                //  ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
//                                                                // ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//
//                                                            //license.setText(regnoval);
//                                                        }
//                                                        else if(modelunique.equals("3")){
//
//                                                            final JSONObject result = staticJsonObj.getJSONObject("callBackObj");
//                                                            String vinnoval = result.getString("ExtractNumber");
//                                                           // RegistrationStep4.vinnumberbind(context);
//                                                           // editvin.setText(vinnoval);
//
//                                                        }
//                                                        else if(modelunique.equals("7")){
//                                                            final JSONObject result = staticJsonObj.getJSONObject("rObj").getJSONObject("DLInfo");
//                                                            String licenno = result.getString("licenseNo");
//                                                            String licenno1 = licenno;
//                                                            // RegistrationStep4.vinnumberbind(context);
//                                                            // editvin.setText(vinnoval);
//
//                                                        }
//                                                    }
//                                                    catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                        MainActivity.MobileErrorLog(reqidval,"AddVehicle-getOCRReading",e.toString(),e.toString());
//                                                    }
//                                                }
//                                            });
//                                        }
//                                        else
//                                        {
//                                            activity.runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                          //  uploadprog.setVisibility(View.GONE);
//                                                    uploadprog.setVisibility(View.GONE);
//                                                    uploadsucces.setVisibility(View.VISIBLE);
//                                                    numplatscandone = "1";
//                                                    numplateprogressstart = "0";
//                                                    scannumlin.setVisibility(View.VISIBLE);
//                                                    try {
//                                                        Toast.makeText(activity, staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText") + ".Please rescan or enter it manually" , Toast.LENGTH_SHORT).show();
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
////                                                        activity.runOnUiThread(new Runnable() {
////                                                            @Override
////                                                            public void run() {
////                                                                Toast.makeText(activity, e.toString() , Toast.LENGTH_SHORT).show();
////                                                            }
////                                                        });
//                                                    }
//                                                }
//                                            });
//                                   }
//                                    } catch (final IOException e) {
//                                        e.printStackTrace();
//                                        MainActivity.MobileErrorLog(reqidval,"AddVehicle-getOCRReading",e.toString(),e.toString());
////                                        activity.runOnUiThread(new Runnable() {
////                                            @Override
////                                            public void run() {
////                                                Toast.makeText(activity, e.toString() , Toast.LENGTH_SHORT).show();
////                                            }
////                                        });
//                                    } catch (final JSONException e) {
//                                        e.printStackTrace();
//                                        MainActivity.MobileErrorLog(reqidval,"AddVehicle-getOCRReading",e.toString(),e.toString());
////                                        activity.runOnUiThread(new Runnable() {
////                                            @Override
////                                            public void run() {
////                                                Toast.makeText(activity, e.toString() , Toast.LENGTH_SHORT).show();
////                                            }
////                                        });
//                                    }
//                                }
//                            });
//                            thread.start();
//                        }
//
//                        catch (Exception e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                            MainActivity.MobileErrorLog(reqidval,"AddVehicle-getOCRReading",e.toString(),e.toString());
////                            activity.runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    Toast.makeText(activity, e.toString() , Toast.LENGTH_SHORT).show();
////                                }
////                            });
//                        }
//
//                    }
//                });
//
//    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String test = insurcomspinner.get(position);
        InsuranceComInfo insur = insurnamelist.get(position);
        insuredid = insur.getInsurerID();
        //List<InsuranceComInfo> beerDrinkers = select(persons, having(on(InsuranceComInfo.class).getAge(), greaterThan(16)));

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
//
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_name) {
//
//            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
//            View screenView = rootView.getRootView();
//            screenView.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
//            screenView.setDrawingCacheEnabled(false);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//            String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditor = sharedpreferences.edit();
//            supporteditor.putString(MainActivity.ReferrenceURL, "Add Vehicle Step1");
//            supporteditor.apply();
//            sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
//            SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
//            supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
//            supporteditorimg.apply();
//            Intent login = new Intent(AddVehicle.this, SupportTicket.class);
//            startActivity(login);
//            return true;
//
//            // Do something
//
//
//        } else {
        try {
            mydb = new DatabaseHelper(AddVehicle.this);
            if (mydb.getregstep().getCount() != 0) {
                mydb.deleteregstep();
            }
            boolean regstepinserted = mydb.insertregstep("RegCompleted");
            if (regstepinserted) {
                boolean test = regstepinserted;
                Log.i(null, "Insertion Done");
            } else {
                boolean test = regstepinserted;
                Log.i(null, "Not Insertion Done");
            }
            qrcodelay = 0;
            numplateprogressstart = "0";
//            Intent login = new Intent(AddVehicle.this, VehicleOwnership.class);
            // Intent login = new Intent(MainActivity.this, ClaimFinalForm.class);
//            startActivity(login);
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
//        }


        return super.onOptionsItemSelected(item);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_help, menu);
//        return true;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mydb = new DatabaseHelper(AddVehicle.this);
        if (mydb.getregstep().getCount() != 0) {
            mydb.deleteregstep();
        }
        boolean regstepinserted = mydb.insertregstep("RegCompleted");
        if (regstepinserted) {
            boolean test = regstepinserted;
            Log.i(null, "Insertion Done");
        } else {
            boolean test = regstepinserted;
            Log.i(null, "Not Insertion Done");
        }
        qrcodelay = 0;
        numplateprogressstart = "0";
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    public static class RegistrationGlobal {

        public static String inputregno;
        public static String inputexpirydate;
        public static String inputprintcode;
        public static String outputregno;
        public static String outputexpirydate;
        public static String outputprintcode;
    }


}