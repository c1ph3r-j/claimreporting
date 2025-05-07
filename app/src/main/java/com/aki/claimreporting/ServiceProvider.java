package com.aki.claimreporting;

import static com.aki.claimreporting.CertificateActivation.postURL;
import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServiceProvider extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static String eventtype = "eventype";
    public static String eventName = "eventName";
    public static String entityID = "entityID";
    public static FirebaseCrashlytics mCrashlytics;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    // public TextView phnredirect, txtphno, txtsuccess;
    TabLayout tabLayout;
    ViewPager viewPager;
    Context context;
    //   TextView companyNote;
    LinearLayout Btnucarvieprd;
    private int[] tabIcons = {
            R.drawable.phone,
            R.drawable.addgrey,
            R.drawable.attachblue
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Emergency Services");
        preventSizeChange(this, getSupportActionBar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        context = this;
        init();
    }

    @SuppressLint("SetTextI18n")
    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            // txtsuccess = (TextView) findViewById(R.id.callcompany);
            // txtphno = (TextView) findViewById(R.id.txtcompphnoval);
        /*txtsuccess.setText("For " + ClaimVehicleList.tollfreecompany +  " claim support, dial ");
        txtphno.setText(ClaimVehicleList.tollfreenum);*/
            //  companyNote = findViewById(R.id.companynote);

//            Intent intentFrom = getIntent();
//            boolean isFromOtherServices = intentFrom.getBooleanExtra("isFromOtherServices", false);
//            if(isFromOtherServices){
//                companyNote.setVisibility(View.GONE);
//            }else{
//                companyNote.setVisibility(View.VISIBLE);
//            }

            try {
                //  txtsuccess.setText("For AKI claim support, dial");
//                txtphno.setText("+254 786592310");
//
//                // phnredirect =(TextView) findViewById(R.id.txtphnoval);
//                txtphno.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//               /* Intent viewDetails = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ClaimVehicleList.tollfreenum));
//                startActivity(viewDetails);*/
//                    }
//                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
            tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            viewPager = (ViewPager) findViewById(R.id.pager);
            try {
                if (MainActivity.ambulanceenabled.equals("Yes") && MainActivity.towingagencyenabled.equals("Yes") && MainActivity.policeinfoenabled.equals("Yes")) {
                    tabLayout.addTab(tabLayout.newTab().setText("Ambulance"));
                    tabLayout.addTab(tabLayout.newTab().setText("Towing Agencies"));
                    tabLayout.addTab(tabLayout.newTab().setText("Police"));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else if (MainActivity.ambulanceenabled.equals("Yes") && MainActivity.towingagencyenabled.equals("No") && MainActivity.policeinfoenabled.equals("No")) {

                    tabLayout.addTab(tabLayout.newTab().setText("Ambulance"));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else if (MainActivity.ambulanceenabled.equals("No") && MainActivity.towingagencyenabled.equals("Yes") && MainActivity.policeinfoenabled.equals("No")) {

                    tabLayout.addTab(tabLayout.newTab().setText("Towing Agencies"));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else if (MainActivity.ambulanceenabled.equals("No") && MainActivity.towingagencyenabled.equals("No") && MainActivity.policeinfoenabled.equals("Yes")) {

                    tabLayout.addTab(tabLayout.newTab().setText("Police"));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else if (MainActivity.ambulanceenabled.equals("Yes") && MainActivity.towingagencyenabled.equals("Yes") && MainActivity.policeinfoenabled.equals("No")) {

                    tabLayout.addTab(tabLayout.newTab().setText("Ambulance"));
                    tabLayout.addTab(tabLayout.newTab().setText("Towing Agencies"));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else if (MainActivity.ambulanceenabled.equals("No") && MainActivity.towingagencyenabled.equals("Yes") && MainActivity.policeinfoenabled.equals("Yes")) {

                    tabLayout.addTab(tabLayout.newTab().setText("Towing Agencies"));
                    tabLayout.addTab(tabLayout.newTab().setText("Police"));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else if (MainActivity.ambulanceenabled.equals("Yes") && MainActivity.towingagencyenabled.equals("No") && MainActivity.policeinfoenabled.equals("Yes")) {

                    tabLayout.addTab(tabLayout.newTab().setText("Ambulance"));
                    tabLayout.addTab(tabLayout.newTab().setText("Police"));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else if (MainActivity.ambulanceenabled.equals("No") && MainActivity.towingagencyenabled.equals("Yes") && MainActivity.policeinfoenabled.equals("Yes")) {
                    tabLayout.addTab(tabLayout.newTab().setText("Towing Agencies"));
                    tabLayout.addTab(tabLayout.newTab().setText("Police"));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else {
                    tabLayout.addTab(tabLayout.newTab().setText("Ambulance"));
                    tabLayout.addTab(tabLayout.newTab().setText("Towing Agencies"));
                    tabLayout.addTab(tabLayout.newTab().setText("Police"));
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                }
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }
//        tabLayout.addTab(tabLayout.newTab().setText("Ambulance"));
//        tabLayout.addTab(tabLayout.newTab().setText("Towing Agencies"));
//        tabLayout.addTab(tabLayout.newTab().setText("Police"));
//        //setupTabIcons();
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            Btnucarvieprd = findViewById(R.id.Btncarviewprcd);
            Intent intent = getIntent();
            boolean isFromOtherService = intent.getBooleanExtra("isFromOtherServices", false);

            if (isFromOtherService) {
                Btnucarvieprd.setVisibility(View.GONE);
            } else {
                Btnucarvieprd.setVisibility(View.VISIBLE);
            }

            try {
                Btnucarvieprd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                mydb = new DatabaseHelper(ServiceProvider.this);
//                if(mydb.getclaimstep().getCount() != 0)
//                {
//                    mydb.deleteclaimstep();
//                }
//                boolean claimstepinserted = mydb.insertclaimstep("ClaimStep3");
//                if(claimstepinserted == true)
//                {
//                    boolean test = claimstepinserted;
//                    Log.i(null,"Insertion Done");
//                }
//                else
//                {
//                    boolean test = claimstepinserted;
//                    Log.i(null,"Not Insertion Done");
//                }
                        SharedPreferences incidentsharedpreferences = getSharedPreferences("IncidentType", Context.MODE_PRIVATE);
                        if (incidentsharedpreferences.getString(ClaimType.typeidincident, "") == "B2EC755A-88EF-4F53-8911-C13688D226D3") {
                            startActivity(new Intent(ServiceProvider.this, ClaimStolenTheft.class));
                        } else {
                            startActivity(new Intent(ServiceProvider.this, CaptureDamagedParts.class));

//                            DisplayMetrics dm = getResources().getDisplayMetrics();
//                            int densityDpi = dm.densityDpi;
//                            if (densityDpi >= 320 && densityDpi <= 390) {
//                                Intent car = new Intent(ServiceProvider.this, CarView.class);
//                                startActivity(car);
//                            }
//                            if (densityDpi >= 280 && densityDpi <= 300) {
//                                Intent car = new Intent(ServiceProvider.this, CarView.class);
//                                startActivity(car);
//                            }
//                            if (densityDpi >= 310 && densityDpi <= 395) {
//                                Intent car = new Intent(ServiceProvider.this, CarView280.class);
//                                startActivity(car);
//                            }
//                            if (densityDpi >= 400 && densityDpi <= 520) {
//                                Intent car = new Intent(ServiceProvider.this, CarView400.class);
//                                startActivity(car);
//                            }
                        }

//                Intent car = new Intent(ServiceProvider.this, CarView.class);
//                startActivity(car);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }


            final MyServiceAdapter adapter = new MyServiceAdapter(context, this.getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater=getMenuInflater ();
//        inflater.inflate ( R.menu.home_page,menu );
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_name) {
//            Intent otpIntent = new Intent(ServiceProvider.this, CarView.class);
//            startActivity(otpIntent);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Intent intent = getIntent();
        boolean isFromOtherService = intent.getBooleanExtra("OtherService", false);
        if (!isFromOtherService) {
            getMenuInflater().inflate(R.menu.menu_help, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
            //noinspection SimplifiableIfStatement
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
                supporteditor.putString(MainActivity.ReferrenceURL, "Service Provider");
                supporteditor.commit();
                sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
                SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
                supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
                supporteditorimg.commit();
                Intent login = new Intent(ServiceProvider.this, SupportTicket.class);
                startActivity(login);
                return true;

                // Do something

            } else {
                onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void AddEntityinfo() {
        try {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {


                            mydb = new DatabaseHelper(ServiceProvider.this);
                            if (mydb.getTokendetails().getCount() != 0) {
                                Cursor curseattachtoken = mydb.getTokendetails();
                                int counttoken = curseattachtoken.getCount();
                                if (counttoken >= 1) {
                                    while (curseattachtoken.moveToNext()) {
                                        MainActivity.stokenval = curseattachtoken.getString(1);
                                    }
                                }
                            }
                            SharedPreferences incidePref = getSharedPreferences("CRAID", MODE_PRIVATE);
                            String incident_id = incidePref.getString("CraIdval", "");

                            SharedPreferences entityrof = getSharedPreferences("ServiceEntity", Context.MODE_PRIVATE);
                            String eventypeval = entityrof.getString(eventtype, "");
                            String eventnameval = entityrof.getString(eventName, "");
                            String eventidval = entityrof.getString(entityID, "");
                            postURL = "https://uat-aki.claims.digital/api/app/Claim/AddLifecycle";
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");
                            OkHttpClient client = new OkHttpClient();
                            JsonObject Details = new JsonObject();
                            Details.addProperty("incidentUniqueCode", incident_id);
                            Details.addProperty("eventType", eventypeval);
                            Details.addProperty("eventName", eventnameval);
                            Details.addProperty("entityID", eventidval);
                            String insertString = Details.toString();
                            RequestBody body = RequestBody.create(JSON, insertString);
                            Request request = new Request.Builder()
                                    .url(postURL)
                                    //.header("Authorization", "Bearer " +"eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w")
                                    .header("Authorization", "Bearer " + MainActivity.stokenval)
                                    .header("MobileParameter", MainActivity.InsertMobileParameters())
                                    .post(body)
                                    .build();
                            Response staticResponse = null;
                            try {
                                staticResponse = client.newCall(request).execute();
                                String staticRes = staticResponse.body().string();
                                Log.i(null, staticRes);
                                final JSONObject staticJsonObj = new JSONObject(staticRes);
                                if (staticJsonObj.getInt("rcode") == 1) {

                                } else {
                                    try {
                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                        JSONObject index = rmsg.getJSONObject(0);
                                        runOnUiThread(() -> {
                                            String errorText = null;
                                            String trnId = null;
                                            try {
                                                errorText = index.getString("errorText");
                                                trnId = staticJsonObj.getString("trnID");
                                                AlertDialog.Builder alert = new AlertDialog.Builder(ServiceProvider.this);
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
                                // progressdialog.dismiss();
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + "AddEntityInfo", ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
//                                mCrashlytics.recordException(ex);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Toast.makeText(ServiceProvider.this,ex.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                // getActivity().runOnUiThread(new Runnable() {
//                                        public void run() {
//
//                                            Toast.makeText(getActivity(),
//                                                    ex.toString(), Toast.LENGTH_LONG).show();
//                                        }
//                                    });
                            } catch (JSONException ex) {
                                //progressdialog.dismiss();
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + "AddEntityInfo", ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                                // Toast.makeText(MainActivity.this,ex.toString(), Toast.LENGTH_LONG).show();
                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                        }
                    });
                    thread.start();

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + "AddEntityInfo", ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Intent login = new Intent(ServiceProvider.this, EmergencyInformation.class);
        // Intent login = new Intent(MainActivity.this, ClaimFinalForm.class);
        //startActivity(login);

        //finishAffinity(); // or finish();
    }

}