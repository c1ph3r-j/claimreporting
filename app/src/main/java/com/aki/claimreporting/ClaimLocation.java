package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.PermissionHandler.PERMISSION_LOCATION;
//import static com.azure.android.maps.control.options.SymbolLayerOptions.iconImage;
//import static com.azure.android.maps.control.options.SymbolLayerOptions.iconSize;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.azure.android.maps.control.AzureMaps;
//import com.azure.android.maps.control.MapControl;
//import com.azure.android.maps.control.layer.SymbolLayer;
//import com.azure.android.maps.control.options.AnimationType;
//import com.azure.android.maps.control.source.DataSource;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
//import com.mapbox.geojson.Feature;
//import com.mapbox.geojson.Point;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class ClaimLocation extends AppCompatActivity {

//    public static List<Point> points;
    public static ProgressDialog progressdialog;
    public static String incidentselecteddate;
    public static String incodentselectdatealone;
    public static boolean invalidtimeselected;
    public static boolean invaliddateselected;
    public static boolean pastdateornot;
    //NOTE CHANGE END 1
    public static String locationupdate;

    static {
        //AzureMaps.setSubscriptionKey("SnmdJBYVAuytqRG8zJo_yZb2kZZJeWMDQQT5D-GW2Wo");
//        AzureMaps.setSubscriptionKey("bVLE-0X6_4fJ5AR_1knIm5FQ5aG6oRtollKfqBLyhkk");
        // AzureMaps.setSubscriptionKey("bVLE-asfsafafasf");

        //Alternatively use Azure Active Directory authenticate.
        //AzureMaps.setAadProperties("<Your-AAD-clientId>", "<Your-AAD-appId>", "<Your-AAD-tenant>");
    }

    final Calendar myCalendar = Calendar.getInstance();
    PermissionHandler permissionManager;
    boolean isRedirected = false;
//    MapControl mapControl;
//    List<List<Point>> pointsnew;
//    ArrayList<Point> pointstest;
    Bundle newinstance;
    Activity activity;
    double firstlat;
    double firstlong;
    double longitude;
    double latitude;
    EditText incdtevalid;
    LinearLayout nextpgclaimalin;
    EditText incdtetimevalid;
    //NOTE CHANGE 1
    EditText streetField, addressField, cityField;
    String[] permissions = new String[]{PERMISSION_LOCATION};

    LinearLayout alternateLocationView;
    TextView changeLocationType;
    LinearLayout bottomMenuView;
    boolean isAlternateLocationViewVisible;
    boolean validationlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Accident Location / Date Time");
        setContentView(R.layout.activity_claim_location);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        preventSizeChange(this, getSupportActionBar());
        try {
            activity = this;
            newinstance = savedInstanceState;
//            mapControl = findViewById(R.id.mapcontrol);
//            mapControl.onCreate(newinstance);
            invaliddateselected = true;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            permissionManager = new PermissionHandler(this);
            permissionManager.setPermissionResultListener(new PermissionHandler.PermissionResultListener() {
                @Override
                public void onPermissionGranted() {
                    init();
                }

                @Override
                public void onPermissionDenied() {
                    permissionManager.showPermissionExplanationDialog(permissions);
                }
            });
            if (permissionManager.hasPermissions(permissions)) {
                init();
            } else {
                permissionManager.requestPermissions(permissions);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionManager.handleSettingsActivityResult(permissions, requestCode, resultCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getAllLatLong();
                }
            }, 500);
            //NOTE CHANGE 2
            streetField = findViewById(R.id.streetField);
            addressField = findViewById(R.id.AddressField);
            cityField = findViewById(R.id.cityField);
            //NOTE CHANGE END 2


            alternateLocationView = findViewById(R.id.alternateLocationView);

            changeLocationType = findViewById(R.id.chooseLocationType);
            bottomMenuView = findViewById(R.id.BottomMenuLocationView);
            isAlternateLocationViewVisible = false;
            validationlocation = false;


            alternateLocationView.setVisibility(View.GONE);
            changeLocationType.setText("Different Incident Location?");

            changeLocationType.setOnClickListener(onClickChangeLocation -> {
                if (isAlternateLocationViewVisible) {
                    alternateLocationView.setVisibility(View.GONE);
                    changeLocationType.setText("Different Incident Location?");
                    validationlocation = false;
                    bottomMenuView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                } else {
                    alternateLocationView.setVisibility(View.VISIBLE);
                    changeLocationType.setText("Switch to Live Incident Location?");
                    validationlocation = true;
                    bottomMenuView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                }
                isAlternateLocationViewVisible = !isAlternateLocationViewVisible;
            });

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public void getAllLatLong() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            nextpgclaimalin = findViewById(R.id.nextOnClaimLocation);
            try {
                nextpgclaimalin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if(isAlternateLocationViewVisible)
//                        {
//                            AlertDialog.Builder alert = new AlertDialog.Builder(ClaimLocation.this);
//                            alert.setCancelable(false);
//                            alert.setMessage("Please enter your Incident Location");
//                            alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
//                            alert.show();
//                            return;
//                        }


                        //NOTE CHANGE 3
                        if (validationlocation) {
                            if (streetField.getText().toString().trim().equals("") || streetField.getText().toString().trim().length() == 0) {
                                showAlertToTheUser("Street");
                                return;
                            }
                            if (addressField.getText().toString().trim().equals("") || addressField.getText().toString().trim().length() == 0) {
                                showAlertToTheUser("Address");
                                return;
                            }
                            if (cityField.getText().toString().trim().equals("") || cityField.getText().toString().trim().length() == 0) {
                                showAlertToTheUser("City");
                                return;
                            }
                            locationupdate = streetField.getText().toString().trim() + "," + addressField.getText().toString().trim() + "," + cityField.getText().toString().trim();

                        }
                        //NOTE CHANGE END 3
                        Intent redirect = new Intent(ClaimLocation.this, ClaimVehicleSelection.class);
                        startActivity(redirect);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            incdtevalid = (EditText) findViewById(R.id.incdttimevalidity);
            incdtetimevalid = (EditText) findViewById(R.id.incdttimepick);
            Date currentDate = new Date();
            SimpleDateFormat gmtFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            gmtFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            String currentDateandTime = gmtFormatter.format(currentDate);
            SimpleDateFormat sdfnew = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String currentDateandTimenew = sdfnew.format(new Date());
            incdtevalid.setText(currentDateandTime);
            incdtetimevalid.setText(currentDateandTimenew);
            try {
                DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateincidentLabeloff();
                };
                try {
                    incdtevalid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            DatePickerDialog datePickerDialog = new DatePickerDialog(ClaimLocation.this, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH));

                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DATE, -29);

                            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                            datePickerDialog.show();


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

            int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = myCalendar.get(Calendar.MINUTE);
            try {
                incdtetimevalid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TimePickerDialog timePickerDialog = new TimePickerDialog(ClaimLocation.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {

                                        Calendar datetime = Calendar.getInstance();
                                        Calendar c = Calendar.getInstance();
                                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        datetime.set(Calendar.MINUTE, minute);
                                        if (datetime.getTimeInMillis() >= c.getTimeInMillis()) {
                                            if (invaliddateselected == true) {
                                                Toast.makeText(ClaimLocation.this, "You can't select the future time", Toast.LENGTH_LONG).show();
                                                invalidtimeselected = true;
                                            } else {
                                                incdtetimevalid.setText(hourOfDay + ":" + minute);
                                                invalidtimeselected = false;
                                            }
                                        } else {
                                            incdtetimevalid.setText(hourOfDay + ":" + minute);
                                            invalidtimeselected = false;
                                        }

                                    }
                                }, hour, minute, false);
                        timePickerDialog.show();

                    }
                });
                incidentselecteddate = incdtevalid.getText().toString() + " " + incdtetimevalid.getText().toString();
                pastdateornot = false;
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        longitude = location.getLongitude();
//        latitude = location.getLatitude();

            try {
                SharedPreferences locPref = getSharedPreferences("LocationPref", MODE_PRIVATE);

                longitude = Double.valueOf(locPref.getString(MainActivity.Longitude, ""));
                latitude = Double.valueOf(locPref.getString(MainActivity.Latitude, ""));
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
//        try {
//            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            longitude = location.getLongitude();
//            latitude = location.getLatitude();
//            SharedPreferences sharedpreferences = getSharedPreferences("LocationPref", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedpreferences.edit();
//            editor.putString(MainActivity.Latitude, String.valueOf(latitude));
//            editor.putString(MainActivity.Longitude, String.valueOf(longitude));
//            editor.commit();
//            Geocoder geocoder;
//            List<Address> addresses;
//            geocoder = new Geocoder(ClaimLocation.this, Locale.getDefault());
//
//            try {
//                // Log.e("latitude", "inside latitude--" + latitude);
//                addresses = geocoder.getFromLocation(latitude, longitude, 1);
//
//                if (addresses != null && addresses.size() > 0) {
//                    Address address = addresses.get(0);
//                    String addressDet = address.getAddressLine(0);
//                    SharedPreferences locashared = getSharedPreferences("LocationCurrent", MODE_PRIVATE);
//                    SharedPreferences.Editor editorloca = locashared.edit();
//                    editorloca.putString(MainActivity.Address1, addressDet);
//                    editorloca.commit();
//                }
//
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            //    Toast.makeText(getApplicationContext(),new Double(longitude).toString(), Toast.LENGTH_SHORT).show();
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//            Toast.makeText(getApplicationContext(),ex.toString(),Toast.LENGTH_SHORT).show();
//        }

//            try {
//                mapControl.onReady(map -> {
//                    try {
//                        mapControl = findViewById(R.id.mapcontrol);
//                        mapControl.onCreate(newinstance);
//
//
//                        DataSource source = new DataSource();
//                        map.sources.add(source);
//                        map.images.add("my-custom-icon", R.drawable.mapicon);
//                        map.setCamera(
//                                com.azure.android.maps.control.options.CameraOptions.center(Point.fromLngLat(longitude, latitude)),
//                                com.azure.android.maps.control.options.CameraOptions.zoom(14),
//                                com.azure.android.maps.control.options.AnimationOptions.animationType(AnimationType.FLY),
//                                com.azure.android.maps.control.options.AnimationOptions.animationDuration(3000)
//
//                        );
////Create a point feature.
//                        Feature feature = Feature.fromGeometry(Point.fromLngLat(longitude, latitude));
//
////Add a property to the feature.
//                        feature.addStringProperty("title", "Hello World!");
//
////Add the feature to the data source.
//                        source.add(feature);
//
////Create a symbol layer to render icons and/or text at points on the map.
//                        SymbolLayer layer = new SymbolLayer(source, iconImage("my-custom-icon"), iconSize(0.2f));
//
//
////Add the layer to the map.
//                        map.layers.add(layer);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                        mCrashlytics.recordException(e);
//                    }
//
//
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                mCrashlytics.recordException(e);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void showAlertToTheUser(String alertType) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ClaimLocation.this);
        alert.setCancelable(false);
        alert.setTitle("Alert");
        alert.setMessage("Please enter the " + alertType);
        alert.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    private void updateincidentLabeloff() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            String myFormat = "dd-MMM-yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date sdfstrDate = sdf.parse(sdf.format(myCalendar.getTime()));
            SimpleDateFormat sdfnew = new SimpleDateFormat(myFormat, Locale.US);
            Date sdfnewstrDate = sdfnew.parse(sdfnew.format(new Date()));
            String testnew = "test";
            if (sdfnewstrDate.compareTo(sdfstrDate) <= 0) {
                try {
                    String myFormatnew = "dd-MMM-yyyy";

                    SimpleDateFormat sdfval = new SimpleDateFormat(myFormatnew, Locale.US);
                    incdtevalid.setText(sdfval.format(myCalendar.getTime()));
                    String date = sdfval.format(myCalendar.getTime());
                    invaliddateselected = true;

                    SimpleDateFormat sdfnewtime = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String currentDateandTimenew = sdfnewtime.format(new Date());
                    String time = currentDateandTimenew;
                    incdtetimevalid.setText(currentDateandTimenew);
                    incidentselecteddate = date + " " + time;

                    pastdateornot = false;
//                    incodentselectdatealone = date;
//                    String dateString = incidentselecteddate;
//                    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
//
//                    try {
//                        Date datepast = format.parse(dateString);
//                        Date now = new Date();
//
//                        if (datepast.before(now)) {
//                            // The date is in the past
//                            pastdateornot = true;
//
//                        }
//                        else if (datepast.equals(now)) {
//                            // The date is the current date
//                            pastdateornot = false;
//                        }
//                        else {
//                            // The date is in the future
//                            pastdateornot = false;
//
//                        }
//
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            } else {
                try {
                    String myFormatnew = "dd-MMM-yyyy";
                    SimpleDateFormat sdfval = new SimpleDateFormat(myFormatnew, Locale.US);
                    incdtevalid.setText(sdfval.format(myCalendar.getTime()));
                    String date = sdfval.format(myCalendar.getTime());
                    invaliddateselected = false;

                    SimpleDateFormat sdfnewtime = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String currentDateandTimenew = sdfnewtime.format(new Date());
                    String time = currentDateandTimenew;
                    incdtetimevalid.setText(currentDateandTimenew);
                    incidentselecteddate = date + " " + time;
                    pastdateornot = true;
//                    incodentselectdatealone = date;
//                    String data = incidentselecteddate;
//                    String data1 = "test";
//                    String dateString = incidentselecteddate;
//                    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
//
//                    try {
//                        Date datepast = format.parse(dateString);
//                        Date now = new Date();
//
//                        if (datepast.before(now)) {
//                            // The date is in the past
//                            pastdateornot = true;
//
//                        }
//                        else if (date.equals(now)) {
//                            // The date is the current date
//                            pastdateornot = false;
//                        }
//                        else {
//                            // The date is in the future
//                            pastdateornot = false;
//
//                        }
//
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
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
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mapControl.onResume();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mapControl.onStart();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapControl.onPause();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mapControl.onStop();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mapControl.onLowMemory();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapControl.onDestroy();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapControl.onSaveInstanceState(outState);
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}