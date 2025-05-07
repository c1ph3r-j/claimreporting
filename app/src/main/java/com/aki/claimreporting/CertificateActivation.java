package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.unauthorize;
import static com.aki.claimreporting.ScanCertificate.QrCode;
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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.RequestQueue;
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

public class CertificateActivation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String ModelID = "Modelid";
    public static final String MakeCertificate = "MakeCertificate";
    public static final String CertNum = "CertNum";
    public static final String certificatenumaddriver = "certificatenumaddriver";
    public static final String drivermapdriverid = "drivermapdriverid";
    public static final String regnumadddriver = "regnumadddriver";
    private static final int IMAGE_TAKE_CODE = 1234;
    public static LinearLayout digital, digitalnew, physical;
    public static LinearLayout digaddimgvech;
    public static LinearLayout scannumlin;
    public static String reqidval;
    public static String qrcodefinal;
    public static String certcodefinal;
    public static String certstatuval;
    public static String numplateimageurl, numplateattachmentRefNo;
    public static String dateTimenew;
    public static FirebaseCrashlytics mCrashlytics;
    public static TextView policyendolddate, policystolddate, policystdate, policyenddate;
    //    physicaltext,
    public static String membercraid;
    public static EditText certificatenumber, expirydate, certificateno, insurancecompany, insurancetype, speedmeter;
    public static EditText numplascanres;
    public static int qrcodelay = 0;
    public static EditText insuredname;
    public static String activationidref;
    public static TextView diginsurval, digitalcertificateno, digitalpolicyno, digitalregno;
    public static TextView oldinsurval, oldcertificateno;
    public static EditText oldyear, oldmodel, oldmake, oldpolicyno, oldchassisno, oldregno;
    public static EditText digitalpolicystdt, digitalpolicyenddt;
    public static TextView digitalchassisno, digitalmake, digitalmodel, digitalyear;
    public static byte[] regByte = null;
    public static DatabaseHelper mydb;
    public static String postURL, startdate, enddate;
    public static Context context;
    public static ImageView vechaddimg, vechaddimguploadone;
    public static String expirydateparam, certificatenumparam, regnumparam, yearOfMfgparam, insurerIDparam, insuredNameparam, insuredPINparam, sumInsuredparam, tonnageparam, passengersCountparam;
    public static String policyNoparam, certificateTypeparam, coverageTypeparam, policyBeginDateparam, vINNumberparam, makeparam, modelparam, insuredMailIdparam, insuredPhoneNoparam, insurerNameparam, engineNumberparam;
    public static String certnumberdmvic;
    public static String phnumberdmvic;
    public static TableRow digyestable;
    public static LinearLayout griviencecreatelin;
    public static TextView creategrivtxt;
    final Calendar myCalendar = Calendar.getInstance();
    public boolean captchaverifyornot;
    public String loginphn;
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
    String TAG = MainActivity.class.getSimpleName();
    CheckBox btnverifyCaptcha;
    ImageView Captchadone;
    List<InsuranceComInfo> insurnamelist = new ArrayList<>();
    boolean captchashow;
    LinearLayout Captchashowornt;
    String SITE_KEY = "6LdVl7wbAAAAAEVCngANf4viYJn9MiXVmZb4uzzF";
    String SECRET_KEY = "6LdVl7wbAAAAAIhFCsmraMDrJKoyj9DuUiiDrcNl";
    RequestQueue queue;
    String currentPhotoPath;
    Activity activity;
    List<String> insurcomspinner = new ArrayList<String>();
    LinearLayout proceedBtn;
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
        Objects.requireNonNull(getSupportActionBar()).setTitle("Activation Process");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        mCrashlytics = FirebaseCrashlytics.getInstance();
        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        setContentView(R.layout.activity_certificate_activation);
        preventSizeChange(this, getSupportActionBar());
        context = this;
        proceedid = "0";
        vechilephototook = "0";
        captchaverifyornot = false;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        activity = this;
        insuredid = "";

        try {
            digyestable = (TableRow) findViewById(R.id.digregyestable);
            griviencecreatelin = (LinearLayout) findViewById(R.id.griviencecreatelin);
            creategrivtxt = (TextView) findViewById(R.id.Btncreategriv);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        try {
            creategrivtxt.setOnClickListener(view -> {
                Intent login = new Intent(CertificateActivation.this, Grievance.class);
                startActivity(login);
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        try {
            griviencecreatelin.setVisibility(View.GONE);
            //uploadprog = (ImageView) findViewById(R.id.imageuploading);

            digitalcertificateno = (TextView) findViewById(R.id.editdigitalcertificateno);
            digitalregno = (TextView) findViewById(R.id.editdigitalregno);
            digitalchassisno = (TextView) findViewById(R.id.editdigitalchassisno);
            digitalpolicyno = (TextView) findViewById(R.id.editdigitalpolicyno);

            digitalmake = (TextView) findViewById(R.id.editdigitalmake);
            digitalmodel = (TextView) findViewById(R.id.editdigitalmodel);
            digitalyear = (TextView) findViewById(R.id.editdigitalyear);


            expirydate = (EditText) findViewById(R.id.editexpirydt);

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        try {
            mCrashlytics = FirebaseCrashlytics.getInstance();

            policystdate = (TextView) findViewById(R.id.editpolicystdt);
            policyenddate = (TextView) findViewById(R.id.editpolicyenddt);
            digital = (LinearLayout) findViewById(R.id.lineardigital);
            digitalnew = (LinearLayout) findViewById(R.id.lineardigitalnew);
            diginsurval = (TextView) findViewById(R.id.insurdigitalval);
//        physicaltext = (TextView) findViewById(R.id.digitalorphysical);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        if (qrcodelay == 1) {
            if (qrcodedone == 1) {
                try {
                    {
                        expirydate.setEnabled(true);
                        SharedPreferences qrcodePref = getSharedPreferences("QrCodePref", MODE_PRIVATE);
                        System.out.println(qrcodePref.getString(QrCode, "").startsWith("AKI"));

                        if (qrcodePref.getString(QrCode, " ").startsWith("AKI")) {
//                    physicaltext.setVisibility(View.VISIBLE);
//
//                    physicaltext.setText(getString(R.string.you_have_scanned_the_digital_certificate));

                            digital.setVisibility(View.VISIBLE);
                            digitalnew.setVisibility(View.GONE);


                        } else if ((!(qrcodePref.getString(QrCode, " ").equals(" ") || qrcodePref.getString(QrCode, "").equals("")))) {
                            //old code

                            digital.setVisibility(View.VISIBLE);
                            digitalnew.setVisibility(View.GONE);
//                    physicaltext.setText(getString(R.string.you_have_scanned_the_digital_certificate));

                        } else {
                            digital.setVisibility(View.GONE);
                            digitalnew.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            } else {
                try {
                    ClearDB();
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
                ClearDB();
                digital.setVisibility(View.GONE);
                digitalnew.setVisibility(View.GONE);
//            physicaltext.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        }


        step1button = findViewById(R.id.Btnactivateproceed);
        proceedBtn = findViewById(R.id.LayoutForActivateBtn);
        try {
            proceedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (proceedid == "1") {

                        activatevehicle();
                    } else {

                        GetProceedDetails();

                    }

                }
            });
            step1button.setText(getString(R.string.validate_your_certificate));
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        ImageView certificateqrView = (ImageView) findViewById(R.id.imagecertificateTakePhoto);
        try {
            certificateqrView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreference = getSharedPreferences("QrCodeNavigation", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putString("QrCodeCheck", "2");
                    editor.apply();
                    Intent intent = new Intent(CertificateActivation.this, ScanCertificate.class);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    void ClearDB() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            SharedPreferences modelPref = getSharedPreferences("QrCodePref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = modelPref.edit();
            editor.putString(QrCode, " ");
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {


            queue = Volley.newRequestQueue(getApplicationContext());

            try {
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

                try {
                    expirydate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new DatePickerDialog(CertificateActivation.this, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                            dateid = "3";
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
//        policystolddate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                new DatePickerDialog(CertificateActivation.this, date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//                dateid = "1";
//
//            }
//        });

//        policyendolddate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new DatePickerDialog(CertificateActivation.this, date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//                dateid = "2";
//            }
//        });


        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
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

                if (getValidBitmap(currentPhotoPath) == null) {

                    //finish();
                } else {
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

                    //MainActivity.uploadimages(RegistrationStep4.this);
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
                    try {
                        mydb = new DatabaseHelper(CertificateActivation.this);
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
                        Thread thread = new Thread(new Runnable() {

                            public void run() {

                                SharedPreferences locationPref = getSharedPreferences("LocationPref", Context.MODE_PRIVATE);


                                Bitmap bitmapnew = rotatedBitmap;


                                ByteArrayOutputStream streamnew = new ByteArrayOutputStream();
                                bitmapnew.compress(Bitmap.CompressFormat.JPEG, 80, streamnew);
                                byte[] byteArraynew = streamnew.toByteArray();

                                String latval = locationPref.getString(MainActivity.Latitude, null);
                                String longval = locationPref.getString(MainActivity.Longitude, null);
                                final MediaType mediaType = MediaType.parse("image/jpeg");
                                String imgData = Base64.encodeToString(byteArraynew, Base64.DEFAULT);
                                String imgDatanew = imgData;
                                String dateTime = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    dateTime = localToGMT();
                                }
                                String uniqueID = UUID.randomUUID().toString();
                                sdocumentType = "ed7b4931-9bbe-482c-ab3b-430fcf733f4c";
                                postURL = activity.getString(R.string.uaturl) + "/app/Vehicle/UploadVehicleFiles";
                                OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(120, TimeUnit.SECONDS)
                                        .writeTimeout(120, TimeUnit.SECONDS)
                                        .readTimeout(120, TimeUnit.SECONDS)
                                        .build();
                                RequestBody body = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("fileName", "Vehicle.jpg")
                                        .addFormDataPart(
                                                "Images", "Vehicle.jpg",
                                                RequestBody.create(mediaType, byteArraynew))
                                        .addFormDataPart("certificateNo", digitalcertificateno.getText().toString())
                                        .addFormDataPart("documentType", sdocumentType)
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
                                Response staticResponse = null;
                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog = ProgressDialog.show(CertificateActivation.this, "Loading", "Please wait...", true);
                                            // progressdialog.show(activity, "Loading", "Please wait...", true);
                                        }
                                    });
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                progressdialog.dismiss();
                                                try {
                                                    vechieattachid = staticJsonObj.getJSONObject("rObj").getString("AttachmentRefNo");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }
                                                //  Intent redirect = new Intent(RegistrationStep1.this, DriverMapping.class);
                                                // startActivity(redirect);
                                                vechaddimguploadone.setVisibility(View.VISIBLE);
                                            }
                                        });

                                    } else if (staticJsonObj.getInt("rcode") == -5) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                try {
                                                    notvalidvechicle(staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText"));
                                                    vechaddimguploadone.setVisibility(View.GONE);

//                                                digitalyear.setText(imgData);
//                                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                                intent.setData(Uri.parse(imgData));
//                                                startActivity(intent);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }

                                            }
                                        });
                                    } else {
                                        try {
                                            runOnUiThread(() -> progressdialog.dismiss());
                                            JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                            JSONObject index = rmsg.getJSONObject(0);
                                            runOnUiThread(() -> {
                                                String errorText = null;
                                                String trnId = null;
                                                try {
                                                    errorText = index.getString("errorText");
                                                    trnId = staticJsonObj.getString("trnID");
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(CertificateActivation.this);
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
                                } catch (final IOException | JSONException e) {
                                    runOnUiThread(() -> progressdialog.dismiss());
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
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
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(CertificateActivation.this);
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
        }


    }

    public void getinsurancecompany() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    try {
                        mydb = new DatabaseHelper(CertificateActivation.this);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
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

                            //  OkHttpClient client = new OkHttpClient();
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
                                    .header("Authorization", "Bearer " + stokenval)
                                    .header("MobileParameter", MainActivity.InsertMobileParameters())
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
                                int statuscode = staticResponse.code();
                                if (statuscode == 401) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
                                            unauthorize(CertificateActivation.this);
                                            return;
                                        }
                                    });
                                } else {
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                progressdialog.dismiss();

                                                //diginsurval.setText("");

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

                                            }
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                unauthorize(CertificateActivation.this);
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
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(CertificateActivation.this);
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
                                                    Toast.makeText(CertificateActivation.this, staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText"), Toast.LENGTH_SHORT).show();
                                                    //Toast.makeText(AddVehicle.this, getString(R.string.admin) , Toast.LENGTH_SHORT).show();
                                                } catch (Exception e) {
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

                                        Toast.makeText(CertificateActivation.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                runOnUiThread(progressdialog::dismiss);
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
//                                                Toast.makeText(AddVehicle.this,
//                                                        ex.toString(), Toast.LENGTH_LONG).show();
                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                        }
                    });
                    thread.start();


                } else {
                    try {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(CertificateActivation.this);
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


            }

        } catch (Exception e) {
            runOnUiThread(progressdialog::dismiss);
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }


    }

    private void updateexpiryLabel() {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        expirydate.setText(sdf.format(myCalendar.getTime()));
        expirydatevalue = sdf.format(myCalendar.getTime());
    }

    private void updateFromLabel() {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        policystolddate.setText(sdf.format(myCalendar.getTime()));
        fromdatevalue = sdf.format(myCalendar.getTime());
    }

    private void updateToLabel() {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        policyendolddate.setText(sdf.format(myCalendar.getTime()));
        todatevalue = sdf.format(myCalendar.getTime());
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

    private void GetProceedDetails() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (isNetworkConnected()) {
                if (checkGPSStatus()) {
                    SharedPreferences modelPref = getSharedPreferences("QrCodePref", Context.MODE_PRIVATE);
                    String qrcodeval = modelPref.getString(QrCode, null);
                    qrcodefinal = qrcodeval;
                    if (qrcodefinal.equals("") || qrcodefinal.length() == 0) {
                        Toast.makeText(CertificateActivation.this, getString(R.string.scancertificate), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (expirydate.getText().toString().contains(getString(R.string.Please)) || expirydate.getText().toString() == "" || expirydate.getText().toString() == null || expirydate.getText().toString().length() == 0) {
                        Toast.makeText(context, R.string.please_enter_expiry_date, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String dtStart = expirydate.getText().toString();
                    String dtEnd = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());
                    SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
                    try {
                        dateStart = format.parse(dtStart);
                        dateEnd = format.parse(dtEnd);
                        System.out.println(dateStart);
                        System.out.println(dateEnd);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }

                    if (dateStart.compareTo(dateEnd) < 0) {
                        Toast.makeText(CertificateActivation.this, getString(R.string.expiredcertificate), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    getinsurancecompany();

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CertificateActivation.this);
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
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    public void getCertificateInfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {

                    // getinsurancecompany();
                    progressdialog = new ProgressDialog(this);
                    mydb = new DatabaseHelper(CertificateActivation.this);
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
                    if (qrcodePref.getString(QrCode, "").startsWith("AKI")) {
                        SharedPreferences modelPref = getSharedPreferences("QrCodePref", Context.MODE_PRIVATE);
                        String qrcodeval = modelPref.getString(QrCode, null);
                        qrcodefinal = qrcodeval;
                    } else {
                        SharedPreferences modelPref = getSharedPreferences("QrCodePref", Context.MODE_PRIVATE);
                        String qrcodeval = modelPref.getString(QrCode, null);
                        qrcodefinal = qrcodeval;
                    }

                    Thread thread = new Thread(new Runnable() {

                        public void run() {
                            postURL = getString(R.string.uaturl) + "/app/Integration/GetCertificate";
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");

                            //OkHttpClient client = new OkHttpClient();
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
                            JsonObject Details = new JsonObject();
                            // Details.addProperty("LicencePlate", numplascanres.getText().toString());
                            Details.addProperty("ExpiryDate", expirydate.getText().toString());
                            Details.addProperty("PrintCode", qrcodefinal);
                            String insertString = Details.toString();
                            RequestBody body = RequestBody.create(JSON, insertString);
                            Request request = new Request.Builder()
                                    .url(postURL)
                                    .header("MobileParameter", MainActivity.InsertMobileParameters())
                                    .header("Authorization", "Bearer " + stokenval)
                                    .post(body)
                                    .build();
                            Response staticResponse = null;

                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog = ProgressDialog.show(activity, getString(R.string.Loading), getString(R.string.please_wait), true);
                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
                                    }
                                });
                                staticResponse = client.newCall(request).execute();
                                int statuscode = staticResponse.code();
                                if (statuscode == 401) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
                                            unauthorize(CertificateActivation.this);
                                            return;
                                        }
                                    });
                                } else {
                                    expirydate.setEnabled(false);
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

                                        try {
                                            expirydateparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("policyEndDate");
                                            certificatenumparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("certificateNo");
                                            regnumparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("registratioNo");
                                            System.out.println(regnumparam);
                                            policyNoparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("policyno");
                                            certificateTypeparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("typeOfInsurance");
                                            coverageTypeparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("typeofCover");
                                            policyBeginDateparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("policyBeginDate");
                                            vINNumberparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("chassisNumber");

                                            makeparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("make");
                                            modelparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("model");
                                            yearOfMfgparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("yearofManufacture");
                                            insurerIDparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("memberCompanyID");
                                            insuredNameparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("insuredName");
                                            insuredPINparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("insuredPIN");
                                            sumInsuredparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("sumInsured");
                                            tonnageparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("tonnage");
                                            passengersCountparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("passengersCount");
                                            insuredMailIdparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("insuredEmailID");
                                            insuredPhoneNoparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("insuredPhoneNumber");
                                            insurerNameparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("memberCompanyName");
                                            engineNumberparam = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("engineNumber");

                                            certnumberdmvic = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("certificateNo");
                                            phnumberdmvic = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate").getString("insuredPhoneNumber");

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    griviencecreatelin.setVisibility(View.VISIBLE);
                                                }
                                            });

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }


                                        if (staticfinalObj.getString("certificateNo") == "null") {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressdialog.dismiss();
                                                    //  RegistrationGlobal.inputregno = numplascanres.getText().toString();
                                                    RegistrationGlobal.inputexpirydate = expirydate.getText().toString();
                                                    RegistrationGlobal.inputprintcode = qrcodefinal;
                                                    RegistrationGlobal.outputregno = "";
                                                    RegistrationGlobal.outputexpirydate = "";
                                                    RegistrationGlobal.outputprintcode = "";
                                                    askVehicleGrievances();
                                                }
                                            });
                                        } else {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressdialog.dismiss();
                                                    step1button.setText(getString(R.string.ActivateNowText));
                                                    SharedPreferences qrcodePref = getSharedPreferences("QrCodePref", MODE_PRIVATE);
                                                    if (qrcodePref.getString(QrCode, "").startsWith("AKI")) {

                                                        policystolddate.setEnabled(true);
                                                        policyendolddate.setEnabled(true);
                                                        oldchassisno.setEnabled(true);
                                                        physical.setVisibility(View.VISIBLE);
                                                        // digaddimgvech.setVisibility(View.VISIBLE);
                                                        digitalnew.setVisibility(View.GONE);

                                                        String[] phycert = qrcodePref.getString(QrCode, "").split(",");
                                                        try {
                                                            SharedPreferences makecertificate = getSharedPreferences("MakeCertificate", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor makecertificateeditor = makecertificate.edit();
                                                            makecertificateeditor.putString(MakeCertificate, staticfinalObj.getString("make"));
                                                            makecertificateeditor.commit();

                                                            oldcertificateno.setText(staticfinalObj.getString("certificateNo"));
//                                                                if(staticfinalObj.getString("certificateNo") == "null" || staticfinalObj.getString("certificateNo") == null)
//                                                                {
//                                                                    oldcertificateno.setText("");
//                                                                }
//                                                                else
//                                                                {
//                                                                    oldcertificateno.setText(staticfinalObj.getString("certificateNo"));
//                                                                }
                                                            if (staticfinalObj.getString("registratioNo") == "null" || staticfinalObj.getString("registratioNo") == null) {
                                                                digitalregno.setText("");
                                                            } else {
                                                                digitalregno.setText(staticfinalObj.getString("registratioNo"));
                                                            }

                                                            if (staticfinalObj.getString("chassisNumber") == "null" || staticfinalObj.getString("chassisNumber") == null) {
                                                                oldchassisno.setText("");
                                                            } else {
                                                                oldchassisno.setText(staticfinalObj.getString("chassisNumber"));
                                                            }

                                                            if (staticfinalObj.getString("policyBeginDate") == "null" || staticfinalObj.getString("policyBeginDate") == null) {
                                                                //digitalpolicystdt.setText("");
                                                                policystdate.setText("");
                                                            } else {
                                                                //  digitalpolicystdt.setText(staticfinalObj.getString("policyBeginDate"));
                                                                policystdate.setText(staticfinalObj.getString("policyBeginDate"));
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
                                                                oldmake.setText("");
                                                            } else {
                                                                oldmake.setText(staticfinalObj.getString("make"));
                                                            }
                                                            if (staticfinalObj.getString("model") == "null" || staticfinalObj.getString("model") == null) {
                                                                oldmodel.setText("");
                                                            } else {
                                                                oldmodel.setText(staticfinalObj.getString("model"));
                                                            }
                                                            if (staticfinalObj.getString("yearofManufacture") == "null" || staticfinalObj.getString("yearofManufacture") == null) {
                                                                oldyear.setText("");
                                                            } else {
                                                                oldyear.setText(staticfinalObj.getString("yearofManufacture"));
                                                            }
                                                            oldinsurval.setText(staticfinalObj.getString("memberCompanyName"));
                                                            //String myString = staticfinalObj.getString("memberCompanyName");
                                                            //  ArrayAdapter myAdap = (ArrayAdapter) insurspinedigital.getAdapter();
                                                            // int spinnerPosition = myAdap.getPosition(myString);
                                                            // insurspinedigital.setSelection(spinnerPosition);

//                                                        if(!numplatscanvalue.equals(staticfinalObj.getString("registratioNo")))
//                                                        {
//                                                            regnummismatch();
//                                                        }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                            mCrashlytics.recordException(e);
                                                        }


                                                        digitalregno.setEnabled(true);
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
                                                        // digaddimgvech.setVisibility(View.VISIBLE);


                                                        try {
                                                            SharedPreferences makecertificate = getSharedPreferences("MakeCertificate", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor makecertificateeditor = makecertificate.edit();
                                                            makecertificateeditor.putString(MakeCertificate, staticfinalObj.getString("make"));
                                                            makecertificateeditor.commit();

                                                            if (staticfinalObj.getString("certificateNo") == "null" || staticfinalObj.getString("certificateNo") == null) {
                                                                digitalcertificateno.setText("-");
                                                            } else {
                                                                digitalcertificateno.setText(staticfinalObj.getString("certificateNo"));
                                                            }
                                                            if (staticfinalObj.getString("registratioNo") == "null" || staticfinalObj.getString("registratioNo") == null) {
                                                                digitalregno.setText("");
                                                            } else {
                                                                digitalregno.setText(staticfinalObj.getString("registratioNo"));
                                                            }

                                                            if (staticfinalObj.getString("chassisNumber") == "null" || staticfinalObj.getString("chassisNumber") == null) {
                                                                digitalchassisno.setText("-");
                                                            } else {
                                                                digitalchassisno.setText(staticfinalObj.getString("chassisNumber"));
                                                            }
                                                            if (staticfinalObj.getString("policyno") == "null" || staticfinalObj.getString("policyno") == null) {
                                                                digitalpolicyno.setText("-");
                                                            } else {
                                                                digitalpolicyno.setText(staticfinalObj.getString("policyno"));
                                                            }
                                                            if (staticfinalObj.getString("policyBeginDate") == "null" || staticfinalObj.getString("policyBeginDate") == null) {
                                                                //digitalpolicystdt.setText("");
                                                                policystdate.setText("-");
                                                            } else {
                                                                //  digitalpolicystdt.setText(staticfinalObj.getString("policyBeginDate"));
                                                                policystdate.setText(staticfinalObj.getString("policyBeginDate"));
                                                                fromdatevalue = staticfinalObj.getString("policyBeginDate");
                                                            }
                                                            if (staticfinalObj.getString("policyEndDate") == "null" || staticfinalObj.getString("policyEndDate") == null) {
                                                                //digitalpolicyenddt.setText("");
                                                                policyenddate.setText("-");
                                                            } else {
                                                                //digitalpolicyenddt.setText(staticfinalObj.getString("policyEndDate"));
                                                                policyenddate.setText(staticfinalObj.getString("policyEndDate"));
                                                                todatevalue = staticfinalObj.getString("policyEndDate");
                                                            }
                                                            if (staticfinalObj.getString("make") == "null" || staticfinalObj.getString("make") == null) {
                                                                digitalmake.setText("-");
                                                            } else {
                                                                digitalmake.setText(staticfinalObj.getString("make"));
                                                            }
                                                            if (staticfinalObj.getString("model") == "null" || staticfinalObj.getString("model") == null) {
                                                                digitalmodel.setText("-");
                                                            } else {
                                                                digitalmodel.setText(staticfinalObj.getString("model"));
                                                            }
                                                            if (staticfinalObj.getString("yearofManufacture") == "null" || staticfinalObj.getString("yearofManufacture") == null) {
                                                                digitalyear.setText("-");
                                                            } else {
                                                                digitalyear.setText(staticfinalObj.getString("yearofManufacture"));
                                                            }
                                                            diginsurval.setText(staticfinalObj.getString("memberCompanyName"));

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                            mCrashlytics.recordException(e);
                                                        }

                                                    }
                                                    proceedid = "1";

                                                    try {
                                                        for (int i = 0; i < insurnamelist.size(); i++) {
                                                            System.out.println(insurnamelist.get(i));
                                                            if (insurnamelist.get(i).getdMVICMemberCompanyID() == staticfinalObj.getInt("memberCompanyID")) {
                                                                insuredid = insurnamelist.get(i).getInsurerID();
                                                                insurerIDparam = insuredid;
                                                            } else {

                                                            }


                                                        }


                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                        mCrashlytics.recordException(e);
                                                    }


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


                                                }
                                            });


                                            certificatetype = staticfinalObj.getString("typeOfInsurance");
                                            coveragetype = staticfinalObj.getString("typeofCover");
//
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


                                        }


                                    } else if (staticJsonObj.getInt("rcode") == 401) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                unauthorize(CertificateActivation.this);
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
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(CertificateActivation.this);
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
                                                    Toast.makeText(CertificateActivation.this, staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText"), Toast.LENGTH_SHORT).show();
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

                                        Toast.makeText(CertificateActivation.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                runOnUiThread(progressdialog::dismiss);
//                                    Intent redirect = new Intent(RegistrationStep1.this, RegistrationStep4.class);
//                                    startActivity(redirect);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                        }
                    });
                    thread.start();

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CertificateActivation.this);
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
        } catch (Exception e) {
            //progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(CertificateActivation.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
            //Toast.makeText(this,ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }
    }

//    public void regnummismatch() {
//        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(RegistrationStep1.this);
//        dialog.setMessage("Number Plate which you have scanned and the registration number which have found on Certificate is not matched.Please check");
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(CertificateActivation.this);
            //dialog.setMessage("Do you like to port the data to this new device?");
            dialog.setMessage(getString(R.string.QrCodeMissMatchMessage) +
                    "\n" +
                    getString(R.string.ClaimDeniedMessage) +
                    "\n" +
                    getString(R.string.PleaseCreateGrievanceMessage));
            dialog.setPositiveButton(getString(R.string.YesRegisterComplaintMessage), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent login = new Intent(CertificateActivation.this, Grievance.class);
                    startActivity(login);
                }
            });
            dialog.setNegativeButton(getString(R.string.NoLetHimMeCorrectMessage), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //this will navigate user to the device location settings screen
                    expirydate.setEnabled(true);
                    dialog.dismiss();
                }
            });
            AlertDialog alert = dialog.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void activatevehicle() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {


                    mydb = new DatabaseHelper(CertificateActivation.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }


                    Thread thread = new Thread(new Runnable() {

                        public void run() {
                            SharedPreferences certPref = getSharedPreferences("CertficiatePref", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = certPref.edit();
                            editor.putString(certificatenumaddriver, certificateval);
                            editor.putString(regnumadddriver, regno);
                            editor.commit();
                            SharedPreferences certificatenum = getSharedPreferences("CertificateNum", Context.MODE_PRIVATE);
                            SharedPreferences.Editor certificatenumeeditor = certificatenum.edit();
                            certificatenumeeditor.putString(CertNum, certificateval);
                            certificatenumeeditor.commit();

                            postURL = getString(R.string.uaturl) + "/app/Integration/CertificateActivate";
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");


                            String dtend = expirydateparam;

                            SimpleDateFormat formatend = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                            try {

                                datetopolicy = formatend.parse(dtend);
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
                                enddate = formatter.format(Date.parse(String.valueOf(datetopolicy)));
                                System.out.println(enddate);
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
                            Details.addProperty("expirydate", expirydate.getText().toString());
                            Details.addProperty("certificatenumber", certificatenumparam);
                            Details.addProperty("registrationchassisno", regnumparam);
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
                                        progressdialog = ProgressDialog.show(activity, getString(R.string.Loading), getString(R.string.please_wait), true);
                                    }
                                });
                                staticResponse = client.newCall(request).execute();
                                int statuscode = staticResponse.code();
                                if (statuscode == 401) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();
                                            unauthorize(CertificateActivation.this);
                                            return;
                                        }
                                    });
                                } else {
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                try {
                                                    activationidref = staticJsonObj.getJSONObject("rObj").getString("TransactionID");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                    mCrashlytics.recordException(e);
                                                }

                                                mydb = new DatabaseHelper(CertificateActivation.this);
                                                Cursor curseattachuser = mydb.getUserdetails();
                                                int countuser = curseattachuser.getCount();
                                                if (countuser >= 1) {
                                                    while (curseattachuser.moveToNext()) {
                                                        try {
                                                            loginphn = curseattachuser.getString(4);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                            mCrashlytics.recordException(e);
                                                        }
                                                    }
                                                }
                                                Intent login = new Intent(CertificateActivation.this, ActivationSuccess.class);
                                                startActivity(login);
//                                                String lgnphnupdate = loginphn.replace("+254","");
//                                                String lgnphnupdatenew = lgnphnupdate.replace("-","");
//
//                                                if(lgnphnupdatenew.trim().equals(phnumberdmvic))
//                                                {
//                                                    Intent login = new Intent(CertificateActivation.this, ActivationSuccess.class);
//                                                    startActivity(login);
//                                                }
//                                                else
//                                                {
//                                                    Intent redirect = new Intent(CertificateActivation.this, ActivationSuccess.class);
//                                                    startActivity(redirect);
//                                                }

                                            }
                                        });

                                    } else if (staticJsonObj.getInt("rcode") == 2) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                try {
                                                    Toast.makeText(CertificateActivation.this, getString(R.string.this_certificate_has_already_been_activated), Toast.LENGTH_SHORT).show();
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
                                                unauthorize(CertificateActivation.this);
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
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(CertificateActivation.this);
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
                                                    Toast.makeText(CertificateActivation.this, staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText"), Toast.LENGTH_SHORT).show();
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

                                        Toast.makeText(CertificateActivation.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                progressdialog.dismiss();
//                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
//                                startActivity(redirect);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                runOnUiThread(() -> Toast.makeText(CertificateActivation.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());

                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                        }
                    });
                    thread.start();


                } else {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(CertificateActivation.this);
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
                Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(CertificateActivation.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

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

                            mydb = new DatabaseHelper(CertificateActivation.this);
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
                            final String certnum = modelPref.getString(certificatenumaddriver, null);
                            SharedPreferences locationPref = activity.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
                            // dateTimenew = localToGMT();
                            String dateTime = localToGMT();


                            try {
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


                                            } else {

                                            }
                                        } catch (final IOException ex) {
                                            // progressdialog.dismiss();
                                            ex.printStackTrace();
                                            MainActivity.MobileErrorLog(reqidval, "DriverMapping-insertdrivermap", ex.toString());
                                            mCrashlytics.recordException(ex);
                                            //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

                                            runOnUiThread(new Runnable() {
                                                public void run() {

                                                    Toast.makeText(CertificateActivation.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            try {
                                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(CertificateActivation.this);
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

    public void cancelledcert() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(CertificateActivation.this);
            dialog.setMessage(getString(R.string.Certificate_cancelled_message));
            dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
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

    /*public void unauthorize() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(CertificateActivation.this);
            dialog.setMessage(getString(R.string.SessionExpiredMessage));
            dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mydb = new DatabaseHelper(CertificateActivation.this);
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
                    Intent login = new Intent(CertificateActivation.this, Dashboard.class);
                    startActivity(login);
                }
            });
            android.app.AlertDialog alert = dialog.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }*/

    public void notvalidvechicle(String errortxt) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(CertificateActivation.this);
            dialog.setMessage(errortxt);
            dialog.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
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

    public void getnumberplatevisible() {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String test = insurcomspinner.get(position);


//        InsuranceComInfo insur = insurnamelist.get(position);
//        insuredid = insur.getInsurerID();


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
//        try {
//            if (id == R.id.action_name) {
//
//                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
//                View screenView = rootView.getRootView();
//                screenView.setDrawingCacheEnabled(true);
//                Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
//                screenView.setDrawingCacheEnabled(false);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                byte[] byteArray = byteArrayOutputStream .toByteArray();
//
//                String encodedimg = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                sharedpreferences = getSharedPreferences("SupportPrefName", Context.MODE_PRIVATE);
//                SharedPreferences.Editor supporteditor = sharedpreferences.edit();
//                supporteditor.putString(MainActivity.ReferrenceURL,"Add Vehicle Step1");
//                supporteditor.commit();
//                sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
//                SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
//                supporteditorimg.putString(MainActivity.SupportImg,encodedimg);
//                supporteditorimg.commit();
//                Intent login = new Intent(CertificateActivation.this, SupportTicket.class);
//                startActivity(login);
//                return true;
//
//                // Do something
//
//
//            }

//            else {
        try {
            Intent login = new Intent(CertificateActivation.this, Dashboard.class);
            startActivity(login);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            MainActivity.MobileErrorLog( e.getStackTrace()[0].getFileName() + " - " + methodName,  e.getMessage(), e.toString());
//            mCrashlytics.recordException(e);
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
        ClearDB();
        Intent login = new Intent(CertificateActivation.this, Dashboard.class);
        startActivity(login);
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