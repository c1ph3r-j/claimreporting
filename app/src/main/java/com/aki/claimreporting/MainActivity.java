package com.aki.claimreporting;

import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Icon;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.Formatter;
import android.text.style.AbsoluteSizeSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.aki.claimreporting.termsandconditions.TermsAndConditions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final String HelpVideoName = "HelpVideoName";
    public static final String Latitude = "latitude";
    public static final String Longitude = "longitude";
    public static final String UniqueID = "uniquid";
    private static final String PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static FirebaseCrashlytics mCrashlytics;
    public static String ReferrenceURL;
    public static boolean isPartsStolen;
    public static String membercraid;
    public static String Vechidshow;
    public static String regnogetclaim;
    public static String latestVersion;
    public static String VechPDFID;
    public static String neardearUseridmap = "neardearUseridmap";
    public static int damagecount;
    public static int offlinedamagecount;
    public static int offlinedamagecountnew;
    public static int damagecountnew;
    public static String encryptedSHA;
    public static String postURL;
    public static boolean VideoEvidence;
    public static boolean VideoDeclaration;
    public static String cardocidval;
    public static String Address1 = "Address1";
    public static String appurl;
    public static String SupportImg;
    public static String CreateDriverRedirect;
    public static String driverUseridmap;
    public static String driverScreenID;
    public static String driveractiphn;
    public static String claiminmiddleflow;
    public static String ambulanceenabled;
    public static String towingagencyenabled;
    public static String policeinfoenabled;
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static String fbstokenval;
    public static String stokenval;
    public static String ModelID;
    public static Activity activity;
    public static Context context;
    public static DatabaseHelper mydb;
    public static String versionapkurl;
    public static int frontViewcount, backViewcount, driverSideViewcount, passengerSideViewcount, roofViewcount;
    public PermissionHandler permissionManager;
    public LocationManager locationManager;
    String[] permissions = new String[]{PERMISSION_LOCATION};
    ProgressDialog progressdialog;
    SharedPreferences sharedpreferences;

    public static void preventSizeChange(Activity context, ActionBar actionBar) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.fontScale = (float) 1.0; // prevent font size change
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        context.getResources().updateConfiguration(configuration, metrics);
        if (actionBar != null) {
            try {
                int sizeInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, metrics);
                SpannableString s = new SpannableString(actionBar.getTitle());
                s.setSpan(new AbsoluteSizeSpan(sizeInPx), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                actionBar.setTitle(s);
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "preventSizeChange", e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        }
    }

    // Method to get device and user details.
    public static String InsertMobileParameters() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            fbstokenval = "";
            mydb = new DatabaseHelper(context);
            if (mydb.getfirebaseTokendetails().getCount() != 0) {
                Cursor firebaseUserId = mydb.getfirebaseTokendetails();
                if (firebaseUserId.getCount() >= 1) {
                    while (firebaseUserId.moveToNext()) {
                        fbstokenval = firebaseUserId.getString(1);
                    }
                }
            }
            String deviceRootCheck;
            if (RootUtil.isDeviceRooted()) {
                deviceRootCheck = "1";
            } else {
                deviceRootCheck = "0";
            }
            String androidOS = Build.VERSION.RELEASE;
            String model = Build.MANUFACTURER + " - " + Build.MODEL;
            // SharedPreferences uniquePref = context.getSharedPreferences("UniquePref", MODE_PRIVATE);
            // final String uniqueIdVal = uniquePref.getString(MainActivity.UniqueID, null);
            UUID uniqueIdVal = UUID.randomUUID();
            SharedPreferences locationPref = context.getSharedPreferences("LocationPref", MODE_PRIVATE);
            final String address1 = locationPref.getString(MainActivity.Latitude, null) + "," + locationPref.getString(MainActivity.Longitude, null);
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            String ipaddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            Locale locale2 = Locale.ENGLISH;
            SimpleDateFormat sf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", locale2);
            Date d = new Date();
            String date = sf.format(d);
            JsonObject mobileparamters = new JsonObject();
            mobileparamters.addProperty("imeino1", uniqueIdVal.toString());
            mobileparamters.addProperty("imeino2", uniqueIdVal.toString());
            mobileparamters.addProperty("timezone", TimeZone.getDefault().getDisplayName());
            mobileparamters.addProperty("currentdatetime", date);
//            mobileparamters.addProperty("ipAddress", address1);
            mobileparamters.addProperty("latitude", locationPref.getString(MainActivity.Latitude, null));
            mobileparamters.addProperty("longitude", locationPref.getString(MainActivity.Longitude, null));
            //   mobileparamters.addProperty("latitude", "25.67");
            //    mobileparamters.addProperty("longitude", "56.78");
            mobileparamters.addProperty("IpAddress", ipaddress);
            mobileparamters.addProperty("mobileType", "Android");
            mobileparamters.addProperty("mobileModel", model);
            mobileparamters.addProperty("mobileOSVersion", androidOS);

            if (fbstokenval.equals("") || fbstokenval.equals(null)) {
                mobileparamters.addProperty("fireBaseUserId", "dNYWj_o9N0vNr4PpPPjjKn:APA91bFRwRorMdQFhBL0DBz5XgfoeZ1oG0p5hBfpsnqsbPQDzeKExctfMPjkHrLxFwalDzR-6vQXXZookWcU-RBRwacisaMFCwbirjZeWcriRe6xPp9Qedd8-VQhlqE9WjlztEzk-1i3");
            } else {
                mobileparamters.addProperty("fireBaseUserId", fbstokenval);
            }

            mobileparamters.addProperty("appVersion", activity.getString(R.string.app_version));
            mobileparamters.addProperty("IsJailBroken", deviceRootCheck);
            // mobileparamters.addProperty("fireBaseuserid", fbstokenval);
            //            encryptedSHA = "";
//            String sourceStr = uniqueIdVal + ipaddress;
//            try {
//                encryptedSHA = AESUtils.encrypt(sourceStr);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            String mobileparam = mobileparamters.toString();
            String mobileparam1 = mobileparam;
            return mobileparamters.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            return "";
        }
    }

    public static String keepAlphanumericAndSpecialChars(String inputString) {
        // Define a regular expression pattern that matches non-alphanumeric characters
        Pattern pattern = Pattern.compile("[^A-Za-z0-9\\W_]");

        // Use a Matcher to replace all characters that match the pattern with an empty string
        Matcher matcher = pattern.matcher(inputString);
        String cleanedString = matcher.replaceAll("");

        return cleanedString;
    }

    public static void main(String[] args) {
        String inputString = "Hello, World! 12345 $%^";
        String cleanedString = keepAlphanumericAndSpecialChars(inputString);
        System.out.println(cleanedString);  // Output: "Hello, World! 12345 $%^"
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertBitmap(Bitmap bitmapNew, Context context) {

        SharedPreferences locationPref = context.getSharedPreferences("LocationPref", Context.MODE_PRIVATE);
        final String latitude = locationPref.getString(MainActivity.Latitude, null);
        final String longitude = locationPref.getString(MainActivity.Longitude, null);
        Bitmap workingBitmap = Bitmap.createBitmap(bitmapNew);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mutableBitmap, mutableBitmap.getWidth(), mutableBitmap.getHeight(), true);
        Bitmap dest = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system

        membercraid = "UNI897K";
        String imageValueBind = "CRA Android";
        String imageValueBind2 = UUID.randomUUID().toString();
        String imageValueBind3 = latitude + " / " + longitude;
        String imageValueBind4 = membercraid;
        Canvas csNew = new Canvas(dest);
        Paint myPaint = new Paint();
        myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        myPaint.setColor(Color.WHITE);
        myPaint.setStrokeWidth(10);
        csNew.drawRect(0, 0, 1400, 420, myPaint);

        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setColor(Color.RED);
        int fontSize = 70;
        tPaint.setTextSize(fontSize);
        tPaint.setTextAlign(Paint.Align.LEFT);

        cs.drawText(imageValueBind, 0, (0 + tPaint.getTextSize()), tPaint);
        cs.drawText(dateTime, 0, (100 + tPaint.getTextSize()), tPaint);
        cs.drawText(imageValueBind2, 0, (200 + tPaint.getTextSize()), tPaint);
        cs.drawText(imageValueBind3 + " , " + imageValueBind4, 0, (300 + tPaint.getTextSize()), tPaint);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        dest.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        Log.i(null, String.valueOf(dest.getByteCount()));
        byte[] byteArray = stream.toByteArray();
        //String img = Convert.ToBase64String(byteArray);
        // return
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static void MobileErrorLog(final String methodNameVal, final String errorMessage, final String errorDescription) {
        try {
            mCrashlytics = FirebaseCrashlytics.getInstance();
            Thread thread = new Thread(() ->
                    AsyncTask.execute(() -> {
                        mydb = new DatabaseHelper(activity);
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor firebaseUserId = mydb.getTokendetails();
                            if (firebaseUserId.getCount() >= 1) {
                                while (firebaseUserId.moveToNext()) {
                                    stokenval = firebaseUserId.getString(1);
                                }
                            }
                        }
                        String errorLogURL = activity.getString(R.string.uaturl) + "/app/Log/InsertLog";
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        JsonObject inserErrorDetails = new JsonObject();
                        inserErrorDetails.addProperty("requestID", "");
                        inserErrorDetails.addProperty("methodID", methodNameVal);
                        inserErrorDetails.addProperty("errorMessage", errorMessage);
                        inserErrorDetails.addProperty("errorDesc", errorDescription);
                        inserErrorDetails.addProperty("LogOn", java.text.DateFormat.getDateTimeInstance().format(new Date()));
                        inserErrorDetails.addProperty("eventType", "1");
                        String erroeString = inserErrorDetails.toString();
                        RequestBody errologBody = RequestBody.create(JSON, erroeString);
                        Request errorlogRequest = new Request.Builder()
                                .url(errorLogURL)
                                .header("Authorization", "Bearer " + stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(errologBody)
                                .build();
                        Response errologResponse;
                        try {
                            errologResponse = client.newCall(errorlogRequest).execute();
                            assert errologResponse.body() != null;
                            String staticRes = errologResponse.body().string();
                            Log.i(null, staticRes);
                        } catch (Exception ex) {


                        }

                    }));
            thread.start();
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
        }


    }

    private void initializeShortCutManager() {
        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

                //first shortcut
                //chat
                Intent verifyCertificateIntent = new Intent(this, MainActivity.class);

                verifyCertificateIntent.putExtra("from", "certificate_verification");

                verifyCertificateIntent.setAction(Intent.ACTION_VIEW);
                ShortcutInfo firstShortCut = new ShortcutInfo.Builder(this, "1")
                        .setShortLabel("Verify Certificate")
                        .setIcon(Icon.createWithResource(this, R.drawable.verifycertificatenew))
                        .setIntent(verifyCertificateIntent)
                        .build();

                Intent reportClaimIntent = new Intent(this, MainActivity.class);

                reportClaimIntent.putExtra("from", "report_claim");

                reportClaimIntent.setAction(Intent.ACTION_VIEW);
                ShortcutInfo secondShortCut = new ShortcutInfo.Builder(this, "2")
                        .setShortLabel("Report claim")
                        .setIcon(Icon.createWithResource(this, R.drawable.appclaimnew))
                        .setIntent(reportClaimIntent)
                        .build();


                //shortcut info arraylist
                ArrayList<ShortcutInfo> shortcutInfoArrayList = new ArrayList<>();
                shortcutInfoArrayList.add(firstShortCut);
                shortcutInfoArrayList.add(secondShortCut);

                shortcutManager.setDynamicShortcuts(shortcutInfoArrayList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preventSizeChange(this, getSupportActionBar());
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(this);
        mCrashlytics = FirebaseCrashlytics.getInstance();

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
        try {
            // To initialize functions and variables.
            init();
            initializeShortCutManager();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onCreate", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

//        try {
//            Intent intent = getIntent();
//            Uri data = intent.getData();
//
//            if (data != null) {
//                // Handle the deep link data here
//                String datanew = data.toString();
//                String datanew1 = datanew;
//                // You can extract the relevant information from the URI and perform the necessary actions
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onCreate", e.getMessage(), e.toString());
//            mCrashlytics.recordException(e);
//        }

    }// End Of OnCreate().

    // Method to initialize the Variables and basic functionalities.
    @SuppressLint("HardwareIds")
    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            // Context and Activity.
            activity = this;
            context = this;
            // Crashlytics
            mCrashlytics = FirebaseCrashlytics.getInstance();
            // Local Database
            mydb = new DatabaseHelper(MainActivity.this);
            // Device Unique Id
            try {
                String deviceUniqueId = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                sharedpreferences = getSharedPreferences("Uniquepref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(UniqueID, deviceUniqueId);
                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            // Enabling Strict Mode for thread.
            try {
                int SDK_INT = Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            // Check's if the user is already logged in Or Not
            // if not forward them to login / Registration page
//            try {
//                mydb = new DatabaseHelper(MainActivity.this);
//                if (mydb.getTokendetails().getCount() != 0) {
//                    Cursor userToken = mydb.getTokendetails();
//                    if (userToken.getCount() >= 1) {
//                        Intent i = new Intent(MainActivity.this, Dashboard.class);
//                        startActivity(i);
//                    } else if (hasPermission()) {
//                        startActivity(new Intent(MainActivity.this, WelcomeScreen.class));
//                    } else {
//                        Intent i = new Intent(MainActivity.this, WelcomeScreen.class);
//                        startActivity(i);
//                    }
//                } else if (hasPermission()) {
//                    startActivity(new Intent(MainActivity.this, WelcomeScreen.class));
//                } else {
//                    Intent i = new Intent(MainActivity.this, WelcomeScreen.class);
//                    startActivity(i);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                mCrashlytics.recordException(e);
//            }


            // CHANGE
            getapiupdateversion();

            // To Fetch the user location for every 2 minutes
            try {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        getLocation();
                        handler.postDelayed(this, 1000); //now is every 2 minutes
                    }
                }, 1000); //Every 120000 ms (2 minutes)
            } catch (Exception ex) {
                ex.printStackTrace();
                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                mCrashlytics.recordException(ex);
            }

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


//        try {
//            ComponentName componentName = new ComponentName(this, MyFirebaseInstanceService.class);
//            getPackageManager().setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
//        catch (Exception ex){
//            ex.printStackTrace();
//        }

    }// End Of init().

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void getapiupdateversion() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(() -> {
                    try {


                        MainActivity.postURL = getString(R.string.uaturl) + "/app/MasterData/GetAllMobileVersion";
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
                                .url(MainActivity.postURL)
                                .header("mobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse = null;
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog = ProgressDialog.show(MainActivity.this, "Loading", "Please wait...", true);
                                    // progressdialog.show(activity, "Loading", "Please wait...", true);
                                }
                            });
                            staticResponse = client.newCall(request).execute();
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);
                            try {
                                if (staticJsonObj.getInt("rcode") == 1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                progressdialog.dismiss();
                                                //  String versionapi = staticJsonObj.getJSONObject("rObj").getJSONObject("getAllAndroidLiteVersion").getString("mobileOSVersion");
                                                String versioninapp = getString(R.string.app_version);
                                                // String versionapkurl = staticJsonObj.getJSONObject("rObj").getJSONObject("getAllAndroidLiteVersion").getString("aPKURL");
                                                //  JsonArray versionapiver = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllAndroidVersion");
                                                // getJSONObject("mobileOSVersion").getString("aPKURL");
                                                ArrayList<String> mylist = new ArrayList<String>();
                                                JSONArray versionapiver = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllAndroidLiteVersion");
                                                for (int i = 0; i < versionapiver.length(); i++) {
                                                    JSONObject versionobj = versionapiver.getJSONObject(i);
                                                    mylist.add(versionobj.getString("mobileOSVersion"));
                                                }
                                                if (mylist.contains(versioninapp)) {
                                                    try {
                                                        new Handler().postDelayed(() -> {
                                                            //Intent login = new Intent(MainActivity.this, Welcome.class);

                                                            // Check's if the user is already logged in Or Not
                                                            // if not forward them to login / Registration page
                                                            try {
                                                                String from = getIntent().getStringExtra("from");
                                                                mydb = new DatabaseHelper(MainActivity.this);
                                                                if (mydb.getTokendetails().getCount() != 0) {
                                                                    Cursor userToken = mydb.getTokendetails();
                                                                    if (userToken.getCount() >= 1) {
                                                                        Intent i = new Intent(MainActivity.this, Dashboard.class);
                                                                        i.putExtra("from", from);
                                                                        startActivity(i);
                                                                    } else if (permissionManager.hasPermissions(permissions)) {
                                                                        startActivity(new Intent(MainActivity.this, TermsAndConditions.class));
                                                                    } else {
                                                                        Intent i = new Intent(MainActivity.this, TermsAndConditions.class);
                                                                        startActivity(i);
                                                                    }
                                                                } else if (permissionManager.hasPermissions(permissions)) {
                                                                    startActivity(new Intent(MainActivity.this, TermsAndConditions.class));
                                                                } else {
                                                                    Intent i = new Intent(MainActivity.this, TermsAndConditions.class);
                                                                    startActivity(i);
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                                mCrashlytics.recordException(e);
                                                            }
                                                        }, 1000);   //5 seconds

                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                        mCrashlytics.recordException(ex);
                                                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                                    }
                                                } else {
                                                    versionapkurl = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllAndroidLiteVersion").getJSONObject(mylist.size() - 1).getString("aPKURL");
                                                    latestVersion = mylist.get(mylist.size() - 1);
                                                    //CHANGE B1 TODO UPDATE VERSION REDIRECTION
                                                    startActivity(new Intent(MainActivity.this, UpdateVersionScreen.class));
                                                    String from = getIntent().getStringExtra("from");
                                                    mydb = new DatabaseHelper(MainActivity.this);

//                                                    try {
//                                                        String from = getIntent().getStringExtra("from");
//                                                        mydb = new DatabaseHelper(MainActivity.this);
//                                                        if (mydb.getTokendetails().getCount() != 0) {
//                                                            Cursor userToken = mydb.getTokendetails();
//                                                            if (userToken.getCount() >= 1) {
//                                                                Intent i = new Intent(MainActivity.this, Dashboard.class);
//                                                                i.putExtra("from", from);
//                                                                startActivity(i);
//                                                            } else if (permissionManager.hasPermissions(permissions)) {
//                                                                startActivity(new Intent(MainActivity.this, WelcomeScreen.class));
//                                                            } else {
//                                                                Intent i = new Intent(MainActivity.this, WelcomeScreen.class);
//                                                                startActivity(i);
//                                                            }
//                                                        } else if (permissionManager.hasPermissions(permissions)) {
//                                                            startActivity(new Intent(MainActivity.this, WelcomeScreen.class));
//                                                        } else {
//                                                            Intent i = new Intent(MainActivity.this, WelcomeScreen.class);
//                                                            startActivity(i);
//                                                        }
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                                                        mCrashlytics.recordException(e);
//                                                    }
//                                                    iszdx
                                                    // CHANGE B1

                                                    //                                                MainActivity.UpdateVersionAPk = versionapkurl;
                                                    //                                                Intent login = new Intent(MainActivity.this, UpdateVersion.class);
                                                    //                                                startActivity(login);
                                                }
                                            } catch (Exception e) {
                                                progressdialog.dismiss();
                                                e.printStackTrace();
                                                mCrashlytics.recordException(e);
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            }
                                        }
                                    });

                                } else {
                                    try {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressdialog.dismiss();
                                                Toast.makeText(MainActivity.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        mCrashlytics.recordException(ex);
                                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());

                                    }
                                    return;

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (final IOException ex) {

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ex.printStackTrace();
                                    mCrashlytics.recordException(ex);
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    progressdialog.dismiss();
                                    Toast.makeText(MainActivity.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException ex) {
                            //progressdialog.dismiss();
                            ex.printStackTrace();
                            mCrashlytics.recordException(ex);
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());

                            progressdialog.dismiss();
                            // Toast.makeText(MainActivity.this,ex.toString(), Toast.LENGTH_LONG).show();
                            //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            } else {

                Toast.makeText(MainActivity.this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            progressdialog.dismiss();
            ex.printStackTrace();
            mCrashlytics.recordException(ex);
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());

        }
    }

    //alert the user dialog
    public static void alertTheUser(String Title, String Message, Context context) {
        try {
            // Alert Dialog to Alert the user.
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(Title);
            dialog.setMessage(Message);
            dialog.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }// End Of alertTheUser().

    public static AlertDialog.Builder alertWarning(String Title, String Message, Context context) {
        try {
            // Alert Dialog to Alert the user.
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(Title);
            dialog.setMessage(Message);
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
            return new AlertDialog.Builder(context);
        }
    }// End Of alertTheUser().

    //to check the network is connected or not
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    //GPS Alert dialog
    public static AlertDialog.Builder withTitleAndMessage(String title, String message, Context context) {

        return new AlertDialog.Builder(context).setTitle(title).setMessage(message);
    }

    //to check the user GPS Status
    public static boolean checkGPSStatus(Context context) {
        LocationManager locationManager;
        boolean gps_enabled = false;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }
        return gps_enabled;
    }

    // Method to fetch user Location.
    @SuppressLint("MissingPermission")
    public void getLocation() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (Exception e) {
//            e.printStackTrace();
//            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//            mCrashlytics.recordException(e);
        }
    }// End of getLocation().

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(getActivity(), "GPS and Internet enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(final Location location) {
        AsyncTask.execute(() -> {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            sharedpreferences = getSharedPreferences("LocationPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Latitude, String.valueOf(latitude));
            editor.putString(Longitude, String.valueOf(longitude));
            editor.apply();
            try {
                // Log.e("latitude", "inside latitude--" + latitude);
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
//                        String address = addresses.get(0).getAdminArea();
//                        String add1 = addresses.get(0).getFeatureName();
//                        String add2 = addresses.get(0).getPremises();
//                        String city = addresses.get(0).getSubAdminArea();
//                        String state = addresses.get(0).getAdminArea();
//                        String country = addresses.get(0).getCountryName();
//                        String postalCode = addresses.get(0).getPostalCode();
//                        String knownName = addresses.get(0).getLocality();
                    // String addressDet = add1 + "\n" + add2 + "\n" + city + "\n " + state + "\n" + country + "\n" + postalCode;
                    String addressDet = address.getAddressLine(0);
                    SharedPreferences locashared = getSharedPreferences("LocationCurrent", MODE_PRIVATE);
                    SharedPreferences.Editor editorloca = locashared.edit();
                    editorloca.putString(Address1, addressDet);
                    editorloca.apply();
                    //  MainActivity.Global.currentLocation = address + "\n" + city + "\n" + state + "\n" + country + "\n"  + postalCode;
//                        editor.putString(Address1, add1);
//                        editor.putString(Address2,add2);
//                        editor.putString(State,state);
//                        editor.putString(District,city);
//                        editor.putString(Locality,knownName);
//                        editor.putString(PostalCode,postalCode);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onLocationChanged", e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
                ;
            }

        });
    }

    public static class Global {
        public static Bitmap img;
        public static Bitmap imgocr;
        public static String outputStr;
    }

    //un authorization
    public static void unauthorize(Activity activity) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
       try{
           android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(activity);
           dialog.setMessage("Your session have been expired. Please login again to continue");
           dialog.setPositiveButton("Ok", (dialog1, which) -> {
               mydb = new DatabaseHelper(activity);
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
               try{
                   loginUser(activity);
               }catch (Exception e ){
                   e.printStackTrace();
               }
           });
           android.app.AlertDialog alert = dialog.create();
           alert.show();
       }catch (Exception e){
           e.printStackTrace();
           MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
           mCrashlytics.recordException(e);
       }
    }

    //mobile login API
    public static void loginUser(Activity activity) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected(activity)) {
                if (checkGPSStatus(activity)) {
                    Thread thread = new Thread(() -> {
                        MainActivity.appurl = activity.getString(R.string.uaturl) + "/app/Account/GetToken";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();

                        String imeiInput = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                        JsonObject detailsInput = new JsonObject();
                        try {
                            detailsInput.addProperty("uniqueID", imeiInput);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }
                        String insertString = detailsInput.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(MainActivity.appurl)
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
                            if (staticJsonObj.getInt("rcode") == 1) {
                                activity.runOnUiThread(() -> {

                                    try {
                                        String sToken = staticJsonObj.getJSONObject("rObj").getString("token");
                                        mydb = new DatabaseHelper(activity);
                                        if (mydb.getTokendetails().getCount() != 0) {
                                            mydb.deletetokendata();
                                        }
                                        if (mydb.getUserdetails().getCount() != 0) {
                                            mydb.deleteuserdata();
                                        }
                                        mydb.inserttoken(sToken);
                                        boolean IsProfileinserted = mydb.insertuserdetails("", staticJsonObj.getJSONObject("rObj").optString("cRAID",""), "","");
                                        if (IsProfileinserted) {
                                            Log.i(null, "Insertion Done");
                                        } else {
                                            Log.i(null, "Not Insertion Done");
                                        }
                                        Intent login = new Intent(activity, Dashboard.class);
                                        activity.startActivity(login);
                                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                                        /*Intent i = new Intent(TermsAndConditions.this, Dashboard.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.enter, R.anim.exit);*/
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                });
                            } else if (staticJsonObj.getInt("rcode") == 401) {
                                try {
                                   /* runOnUiThread(() -> progressdialog.dismiss());*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                            } else {
                                try {
                                    /*runOnUiThread(progressdialog::dismiss);*/
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    activity.runOnUiThread(() -> {
                                        String errorText;
                                        try {
                                            errorText = index.getString("errorText");
                                            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(activity);
                                            alert.setCancelable(false);
                                            alert.setMessage(errorText);
                                            alert.setNegativeButton("Ok", (dialog, which) -> {
                                                dialog.dismiss();
                                                /*progressdialog.dismiss();*/
                                            });
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
                            activity.runOnUiThread(() -> {
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                                Toast.makeText(activity, activity.getString(R.string.admin), Toast.LENGTH_SHORT).show();
                            });
                        } catch (JSONException ex) {
                            activity.runOnUiThread(() -> {
                                try {
                                    /*runOnUiThread(() -> progressdialog.dismiss());*/
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
                                    Toast.makeText(activity, activity.getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                    //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
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
                    });
                    thread.start();
                } else {
                    try {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(activity);
                        dialog.setMessage("GPS locations is not enabled.Please enable it");
                        dialog.setPositiveButton("Ok", (dialog1, which) -> {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(intent);
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
                try {
                    Toast.makeText(activity, activity.getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(activity, activity.getString(R.string.admin), Toast.LENGTH_SHORT).show();
        } finally {

        }

    }
}