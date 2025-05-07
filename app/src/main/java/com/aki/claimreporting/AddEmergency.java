package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddEmergency extends AppCompatActivity {


    public static final int PICK_CONTACT = 1;
    private static final int PERMISSIONS_REQUEST = 99;
    private static final String PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static String stokenval;
    public static ArrayList<NearDearInfo> nearlist = new ArrayList<>();
    public static RecyclerView recyclerView;
    public static DatabaseHelper mydb;
    public static FirebaseCrashlytics mCrashlytics;
    public LinearLayout emailidnear, phnumnear, namenear;
    public ImageView contact;
    public LinearLayout btnaddprcd;
    public String contactId;
    public EditText adddear, contactinfo, phninfo, emailidinfo;
    ProgressDialog progressDialog;
    private NearDearAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emergency);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            preventSizeChange(this, getSupportActionBar());
            Objects.requireNonNull(getSupportActionBar()).setTitle("Emergency Contact");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            init();
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
            try {
                emailidnear = (LinearLayout) findViewById(R.id.emailidnearlinear);
                namenear = (LinearLayout) findViewById(R.id.namenearlinear);
                phnumnear = (LinearLayout) findViewById(R.id.phnumnearlinear);
                recyclerView = findViewById(R.id.recyclernearView);
                btnaddprcd = findViewById(R.id.linearRegProceed);
                contact = (ImageView) findViewById(R.id.contactneardear);
                contactinfo = (EditText) findViewById(R.id.editnearanddear);
                phninfo = (EditText) findViewById(R.id.editnearphnno);
                emailidinfo = (EditText) findViewById(R.id.editemailid);
                adddear = (EditText) findViewById(R.id.adddearid);
                nearlist.clear();
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                contact.setOnClickListener(onCLickContact -> {
                    if (nearlist.size() == 5) {
                        Toast.makeText(AddEmergency.this, getString(R.string.moredear), Toast.LENGTH_SHORT).show();
                    } else {
                        if (!hasPermission()) {
                            requestPermission();
                            return;
                        }
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, PICK_CONTACT);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                btnaddprcd.setOnClickListener(onClickProceed -> {
                    if (nearlist.size() > 0) {
                        insertneardear();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                adddear.setOnClickListener(onClickAddDear -> {
                    String emailInput = emailidinfo.getText().toString().trim();
                    String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                    if (contactinfo.getText().toString().equals("") || contactinfo.getText().toString().length() == 0) {
                        Toast.makeText(AddEmergency.this, getString(R.string.nearname), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (phninfo.getText().toString().equals("") || phninfo.getText().toString().length() == 0) {
                        Toast.makeText(AddEmergency.this, getString(R.string.nearphon), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (phninfo.getText().toString().length() != 9) {
                        Toast.makeText(AddEmergency.this, getString(R.string.validphoneno), Toast.LENGTH_SHORT).show();
                        return;
                    } else if (emailInput.equals("") || emailInput == null || emailInput.length() == 0) {
//                    Toast.makeText(UserRegistration.this, getString(R.string.nearemailid), Toast.LENGTH_SHORT).show();
//                    return;
                    } else {
                        if (!emailInput.matches(emailPattern)) {
                            Toast.makeText(AddEmergency.this, getString(R.string.invalidemaild), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    NearDearInfo element = new NearDearInfo(contactinfo.getText().toString(), phninfo.getText().toString(), (emailidinfo.getText().toString().trim().length() == 0) ? "-" : emailidinfo.getText().toString().trim());
                    nearlist.add(element);
                    mAdapter = new NearDearAdapter(nearlist);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    contactinfo.setText("");
                    phninfo.setText("");
                    emailidinfo.setText("");
                    //neardearwarning();
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

//    public void neardearwarning() {
//        String methodName = Objects.requireNonNull(new Object() {
//        }.getClass().getEnclosingMethod()).getName();
//        try {
//            AlertDialog.Builder dialog = new AlertDialog.Builder(AddEmergency.this);
//            dialog.setMessage("Emergency contact information is completely personal and will not be shared with anyone. It is encrypted and stored in DB and will be used only to share updates only when you meet with accidents.");
//            dialog.setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss());
//            AlertDialog alert = dialog.create();
//            alert.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//            mCrashlytics.recordException(e);
//        }
//    }

    private boolean hasPermission() {
        Log.i(null, String.valueOf(checkSelfPermission(PERMISSION_READ_CONTACTS) == PackageManager.PERMISSION_GRANTED));
        return checkSelfPermission(PERMISSION_READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {
        shouldShowRequestPermissionRationale(PERMISSION_READ_CONTACTS);
        requestPermissions(new String[]{PERMISSION_READ_CONTACTS}, PERMISSIONS_REQUEST);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager;
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

    @SuppressLint("Range")
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (reqCode == PICK_CONTACT) {
                try {
                    String emailIdOfContact = "";
                    String contactName;
                    String phnNum = "";
                    emailidnear.setVisibility(View.VISIBLE);
                    namenear.setVisibility(View.VISIBLE);
                    phnumnear.setVisibility(View.VISIBLE);
                    adddear.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();

                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        String id = cursor.getString(cursor
                                .getColumnIndex(BaseColumns._ID));
                        contactId = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.Contacts._ID));
                        contactName = cursor.getString(cursor
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = ?", new String[]{id}, null);

                        while (phones.moveToNext()) {
                            phnNum = phones.getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        phones.close();

                        Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
                        while (emails.moveToNext()) {
                            emailIdOfContact = emails.getString(emails
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        }
                        emails.close();
                        contactinfo.setText(contactName);
                        String newvalphn = phnNum.replace(" ", "");
                        String valphntrim = newvalphn.trim();
                        String valphn = valphntrim.length() >= 9 ? valphntrim.substring(valphntrim.length() - 9) : "";
                        String phnnumber = valphn.trim();
                        phninfo.setText(phnnumber);
                        if (emailIdOfContact.contains("@")) {
                            emailidinfo.setText(emailIdOfContact);
                        } else {
                            emailidinfo.setText("");
                        }
                        cursor.close();
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

    public void insertneardear() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        AsyncTask.execute(() -> {
            try {
                if (isNetworkConnected()) {

                    if (checkGPSStatus()) {
                        try {
                            for (int id = 0; id < nearlist.size(); id++) {
                                NearDearInfo nearDearInfo = nearlist.get(id);
                                String nearnmae = nearDearInfo.getNearname();
                                String nearemail = nearDearInfo.getNearemailid();
                                String nearphn = nearDearInfo.getNearphnum();
                                mydb = new DatabaseHelper(AddEmergency.this);
                                if (mydb.getTokendetails().getCount() != 0) {
                                    Cursor curseattachtoken = mydb.getTokendetails();
                                    int counttoken = curseattachtoken.getCount();
                                    if (counttoken >= 1) {
                                        while (curseattachtoken.moveToNext()) {
                                            stokenval = curseattachtoken.getString(1);
                                        }
                                    }
                                }
                                try {
                                    runOnUiThread(() -> {
                                        progressDialog = new ProgressDialog(this);
                                        progressDialog.setCancelable(false);
                                        progressDialog.setMessage("Please wait ...");
                                        progressDialog.show();
                                    });
                                    Thread thread = new Thread(() -> {
                                        MainActivity.postURL = getString(R.string.uaturl) + "/app/NearDear/AddNearDear";
                                        final MediaType JSON
                                                = MediaType.parse("application/json; charset=utf-8");

                                        OkHttpClient client = new OkHttpClient.Builder()
                                                .connectTimeout(120, TimeUnit.SECONDS)
                                                .writeTimeout(120, TimeUnit.SECONDS)
                                                .readTimeout(120, TimeUnit.SECONDS)
                                                .build();
                                        JsonObject Details = new JsonObject();
                                        try {
                                            Details.addProperty("nearDearName", AESCrypt.encrypt(nearnmae));
                                            Details.addProperty("nearDearEmail", AESCrypt.encrypt(nearemail));
                                            Details.addProperty("nearDearPhone", AESCrypt.encrypt(nearphn));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }
                                        String insertString = Details.toString();
                                        RequestBody body = RequestBody.create(JSON, insertString);
                                        Request request = new Request.Builder()
                                                .url(MainActivity.postURL)
                                                .header("Authorization", "Bearer " + stokenval)
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
                                                runOnUiThread(progressDialog::dismiss);
                                                Log.d("NEAR DEAR", "INSERTED");
                                                startActivity(new Intent(AddEmergency.this, EmergencyContactList.class));
                                                finish();
                                            } else {
                                                try {
                                                    runOnUiThread(progressDialog::dismiss);
                                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                                    JSONObject index = rmsg.getJSONObject(0);
                                                    runOnUiThread(() -> {
                                                        String errorText;
                                                        try {
                                                            errorText = index.getString("errorText");
                                                            AlertDialog.Builder alert = new AlertDialog.Builder(AddEmergency.this);
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
                                        } catch (final Exception e) {
                                            runOnUiThread(progressDialog::dismiss);
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                            runOnUiThread(() -> Toast.makeText(AddEmergency.this, getString(R.string.admin), Toast.LENGTH_SHORT).show());
                                        }
                                    });
                                    thread.start();
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
                    } else {
                        try {
                            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(AddEmergency.this);
                            dialog.setMessage("GPS locations is not enabled.Please enable it");
                            dialog.setPositiveButton("Ok", (dialog1, which) -> {
                                //this will navigate user to the device location settings screen
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
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
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }
}