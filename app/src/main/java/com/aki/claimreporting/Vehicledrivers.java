package com.aki.claimreporting;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Vehicledrivers extends Fragment {

    public static final String VehicleInstanceID = "VehicleInstanceID";
    public static final String InstanceTypeID = "InstanceTypeID";
    /*private MyDriiverViewModel mViewModel;*/ // no usages found
    public static String reqidval;
    public static DatabaseHelper mydb;
    private static FirebaseCrashlytics mCrashlytics;
    public View rootview;
    public ListView list;
    public ImageView addvehicle;
    public ProgressDialog progressdialog;
    public String stokenval, encryptedSHA;
    public ImageView imgaddriverveh;
    SearchView searchView;
    TextView noDriverTextView;
    DriverVehicleListAdapter driverVehicleListAdapter;
    DriverAllDataModel driverAllDataModel;
    FrameLayout progressOverlay;
    ArrayList<DriverVehicleDataModel> driverlist = new ArrayList<DriverVehicleDataModel>();
    SharedPreferences sharedpreferences;
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Vehicledrivers activity;

    public static void DeletevehicleDriver(Context activity) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            mydb = new DatabaseHelper(activity);
            if (mydb.getTokendetails().getCount() != 0) {
                Cursor curseattachtoken = mydb.getTokendetails();
                int counttoken = curseattachtoken.getCount();
                if (counttoken >= 1) {
                    while (curseattachtoken.moveToNext()) {
                        MainActivity.stokenval = curseattachtoken.getString(1);
                    }
                }
            }

            // String newphnval =  phonenonew.getText().toString();
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
                    // MainActivity.postURL = getString(R.string.uaturl) + "/app/Driver/AddDriver";
                    MainActivity.postURL = activity.getString(R.string.uaturl) + "/app/Driver/DeleteVehicleDriver";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    SharedPreferences modelPref = activity.getSharedPreferences("DriverUserActivation", Context.MODE_PRIVATE);
                    String driveruserid = modelPref.getString(MainActivity.driverUseridmap, null);
                    SharedPreferences vehrefPref = activity.getSharedPreferences("VehicleRefID", Context.MODE_PRIVATE);
                    String vehrefid = vehrefPref.getString(MyVehicles.Viewvehicleref, null);
                    OkHttpClient client = new OkHttpClient();
                    JsonObject Details = new JsonObject();
                    try {
                        Details.addProperty("driverId", driveruserid.trim());
                        Details.addProperty("vehicleId", AESCrypt.decrypt(vehrefid));
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }

                    String insertString = Details.toString();
                    RequestBody body = RequestBody.create(JSON, insertString);
                    Request request = new Request.Builder()
                            .url(MainActivity.postURL)
                            .header("Authorization", "Bearer " + MainActivity.stokenval)
                            .header("MobileParameter", MainActivity.InsertMobileParameters())
                            .post(body)
                            .build();
                    Response staticResponse;

                    try {
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        progressdialog = ProgressDialog.show(getActivity(), getString(R.string.loading), getString(R.string.please_wait), true);
//                                        // progressdialog.show(activity, "Loading", "Please wait...", true);
//                                    }
//                                });
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

//                                    getActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progressdialog.dismiss();
//                                            SharedPreferences deletecall = getActivity().getSharedPreferences("DriverDelete", Context.MODE_PRIVATE);
//                                            SharedPreferences.Editor deletecalleditor = deletecall.edit();
//                                            deletecalleditor.putString(MainActivity.driverdeleteID,"0");
//                                            deletecalleditor.commit();
//                                            SharedPreferences driverscreenPref =  getActivity().getSharedPreferences("DriverScreen", Context.MODE_PRIVATE);
//                                            final String driverscreenid = driverscreenPref.getString(MainActivity.driverScreenID, null);
//                                            if(driverscreenid == "1")
//                                            {
//                                                Intent step2 = new Intent( getActivity(), DriverMapping.class);
//                                                startActivity(step2);
//                                            }
//                                            else
//                                            {
//                                                Intent step2 = new Intent( getActivity(), HomePage.class);
//                                                startActivity(step2);
//                                            }

//                                        }
//                                    });
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
//                                    activity.runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            progressdialog.dismiss();
//                                            try {
//                                                Toast.makeText(getActivity(), staticJsonObj.getJSONArray("rmsg").getJSONObject(0).getString("errorText") , Toast.LENGTH_SHORT).show();
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                            return;
//                                        }
//                                    });
                        }
                    } catch (final IOException ex) {
                        ex.printStackTrace();
                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                        mCrashlytics.recordException(ex);
//                                progressdialog.dismiss();
//                                ex.printStackTrace();
//                                mCrashlytics.recordException(ex);
//                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
//
//                                getActivity().runOnUiThread(new Runnable() {
//                                    public void run() {
//
//                                        Toast.makeText(getActivity(),
//                                                ex.toString(), Toast.LENGTH_LONG).show();
//                                    }
//                                });
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                        mCrashlytics.recordException(ex);
//                                progressdialog.dismiss();
////                                Intent redirect = new Intent(UserRegistration.this, RegistrationStep4.class);
////                                startActivity(redirect);
//                                ex.printStackTrace();
//                                mCrashlytics.recordException(ex);
//                                Toast.makeText(getActivity(),
//                                        ex.toString(), Toast.LENGTH_LONG).show();
                        //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                    }
                }
            });
            thread.start();


        } catch (Exception ex) {
            //progressdialog.dismiss();
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
//
//            mCrashlytics.recordException(ex);
//           Toast.makeText(getActivity(),ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.vehicledrivers_fragment, container, false);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        init();
        return rootview;
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            noDriverTextView = rootview.findViewById(R.id.noDriverTextView);
        /*imgaddriverveh = (ImageView) rootview.findViewById(R.id.imgAdddrivervehicle);
        imgaddriverveh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreference = getActivity().getSharedPreferences("IsCreateDriver", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreference.edit();
                editor.putString(MainActivity.CreateDriverRedirect, "2");
                editor.commit();
                getActivity().finish();
                Intent login = new Intent(getActivity(), DriverMapping.class);
                startActivity(login);
            }
        });*/
            searchView = (SearchView) rootview.findViewById(R.id.searchView);
            try {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        if (TextUtils.isEmpty(newText)) {
                            driverVehicleListAdapter.filter("");
                            list.clearTextFilter();
                        } else {
                            driverVehicleListAdapter.filter(newText);
                        }
                        return true;

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            getvehicledrivers();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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

    public void getvehicledrivers() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected() == true) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mydb = new DatabaseHelper(getActivity());
                        if (mydb.getTokendetails().getCount() != 0) {
                            Cursor curseattachtoken = mydb.getTokendetails();
                            int counttoken = curseattachtoken.getCount();
                            if (counttoken >= 1) {
                                while (curseattachtoken.moveToNext()) {
                                    stokenval = curseattachtoken.getString(1);
                                }
                            }
                        }
                        //stokenval = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6ImY4ZGIxNDE4LTdjMzYtNDJhMC04ZTMxLTcwOWY2NzMwMzBiMiIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiOTlkMzYxMjYtOTNiMS00YTdjLWEyODMtNmRhNTBhYTNlZDMwIiwiUHJpbWFyeUdyb3VwU2lkIjoiZjhkYjE0MTgtN2MzNi00MmEwLThlMzEtNzA5ZjY3MzAzMGIyIiwibmJmIjoxNjExNTQ0NjU2LCJleHAiOjE2NDI2NDg2NTYsImlhdCI6MTYxMTU0NDY1NiwiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.vB9HkEazWX08-HgBHJjOjr4aPUN4tfm54mcNlritZ8AnKDMmetzieRcX9TISRaQMd_Yx612ce1qVfMETj7LZXxP4BOd-f_QhlGqvPKLpODmGuMjPuYVvVQdDB4JDZX0eUwAU-SHgWA4eNXMYQfisZ7FoNshmbcfnRsrqyyXqefQLP7ouMOnHzYq0D8KUFGj6M2PzpIrPjzSb9pH1nMGzbpIWEvJDFPLGlSVAQQJKDHMuTPlfTSNk48-MIeAnh-S3Z9_xGW7Qs-JbKUOdg9NMfSy_EjN-fIYp-lRSlPpx25uz5Ql1F5_l02wM1c6-txFSrhQ2-cQ-33bw11YrVfymEsi9-_UKFW947_vMl6_FCCT2nTKQpMw3d3k1S2Onk1soVpBERbYpf5GgWs5pzlc5CuEI3DOR97yHUfU-8vFQfAh7l9VNr_8SicQlGIPm_F8WqOL1ikHe8geGW_fXt_gxhQlSIgBn0z8PPnf5RVxiUR_01eHQhoZg8zB6KEMQd5MQAy4c2Muyl5j7Y_cOv08Q74CN26MbK1LFdn8BS2gxdr6qqAW2iNBxEe8-wuR4e_B0OlfiYSPTdDzB-SbJUUD0VvOOmo-KefmaY0JQ4NQNejCucpCicV_mtAM9xpwilPyEHvw70OoMxxsd0mcZhX2_-gfsY8BJcLv0GlFrwtG8bbw";
                        SharedPreferences viewvehPref = getActivity().getSharedPreferences("ViewVehicle", Context.MODE_PRIVATE);
                        String certnumview = viewvehPref.getString(MyVehicles.ViewCertificateno, "");
                        SharedPreferences vehcidPref = getActivity().getSharedPreferences("VehicleRefID", Context.MODE_PRIVATE);
                        String vehcidprefval = vehcidPref.getString(MyVehicles.Viewvehicleref, "");

                        String certnumencrypt = null;
                        String vehciddecrypt = null;
                        try {
                            vehciddecrypt = AESCrypt.decrypt(vehcidprefval);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                            mCrashlytics.recordException(e);
                        }

                        MainActivity.postURL = getString(R.string.uaturl) + "/app/Vehicle/GetVehicle";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        JsonObject Details = new JsonObject();
                        Details.addProperty("certificateNo", certnumview);
                        Details.addProperty("vehicleID", vehciddecrypt);
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(MainActivity.postURL)
                                //.header("Authorization", "Bearer " +"eyJhbGciOiJSUzI1NiIsImtpZCI6IjUxNERDNjQ4RUIwOTAzREUzMzc1QUZDQzg3NUZFQzJEMjk0QTEwOEYiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiMWFjY2UxNGItNmY3OS00Y2I2LTk0OGQtNmYzNjE3MjdmYjE3IiwicHJpbWFyeXNpZCI6IjFhY2NlMTRiLTZmNzktNGNiNi05NDhkLTZmMzYxNzI3ZmIxNyIsInByaW1hcnlncm91cHNpZCI6Ijc5OWIyYTc3LTUzY2UtNDIwMi1iZmUzLTYwZWY0Yzk0MTQ3ZCIsIlJvbGUiOiIxYWNjZTE0Yi02Zjc5LTRjYjYtOTQ4ZC02ZjM2MTcyN2ZiMTciLCJQcmltYXJ5U2lkIjoiM2FmNDhiMGMtMGM0Yi00YmU2LWFiY2MtZDU0MDUzNWRjN2I1IiwiUHJpbWFyeUdyb3VwU2lkIjoiNzk5YjJhNzctNTNjZS00MjAyLWJmZTMtNjBlZjRjOTQxNDdkIiwibmJmIjoxNjEwMzM2MTY3LCJleHAiOjE2NDE0NDAxNjcsImlhdCI6MTYxMDMzNjE2NywiaXNzIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIiwiYXVkIjoiaHR0cHM6Ly91YXQtYWtpLmNsYWltcy5kaWdpdGFsIn0.H6uM6XXVxPlxEzKbLSoMCjqKuhq9G3wdkRy4Etty1-G33KwOJaS9DEhVIRiQRk3ApLcNgwEraQX2gJbKdTcPW6bLSwADnlZQkNHip9pxDwmI06EHaOoYNoFBXjUjwws-SNQbOVaVkjH1eKkct_khfc0n-rRJzUJtSOOW0MYmrQg9BBbyhmTqCuQNonwf9n9jsaJFXoae7T4uZEsnfadOSo05w9wAQm7k4u3yA0KB8IdlKFpR5K4rbUYMRuOjLPUbf0yXDHJYKxm1rqPPFZhxWK7fAq2CRUlbGpuLWt_pg7x9-7lrnJC2dbwX_2X6ZW-3XTNOfo0R7M2HAK--OPiBeVbZ-MsiOoIjQ2iJQ08GzYxfCf9hD_dG37QX6Na5zf3AecZyrVUCNKBJ3-ILHgNEZET6FbnEnwEiFAj2KdeKRk5VB13mhD1al8uDU1m3ukUKZ2VXbdsCCsySS5G7BXvxaOjnhW0CeZcYhv_lCuczCJMvdsVZUO2Wua83p-zJF-nLWh5a7A8jCxzvhN78yvigbuU5ZFmMmjFKKP3dgwBHGSGx3fTfm7tPUdvzigVdUEmWoNwcEc88pSYQKOyGkOszJb6s63wtxJyZPzjV3EkLOSuiE7yG3QCA_ZIFTEgEOi8L5tTDrWKJZ26zWP98_WkFNKumoqLcnixn3-6TsLjv77w")
                                .header("Authorization", "Bearer " + stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse = null;
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressdialog = ProgressDialog.show(getActivity(), getString(R.string.loading), getString(R.string.please_wait), true);
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

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            progressdialog.dismiss();

                                            JSONArray driverResponseList = staticJsonObj.getJSONObject("rObj").getJSONObject("getVehicle").getJSONArray("drivers");
                                            if (driverResponseList.length() > 0) {
                                                for (int i = 0; i < driverResponseList.length(); i++) {
                                                    JSONObject driverObj = driverResponseList.getJSONObject(i);
                                                    DriverVehicleDataModel element = new DriverVehicleDataModel(driverObj.getString("driverUserId"), AESCrypt.decrypt(driverObj.getString("driverName")), driverObj.getString("driverDLCountry"), AESCrypt.decrypt(driverObj.getString("driverDLNum")), driverObj.getString("driverDLValidFrom"), driverObj.getString("driverDLValidTill"), AESCrypt.decrypt(driverObj.getString("mobileNo")), driverObj.getBoolean("isSelfDriver"), driverObj.getString("cRAID"));
                                                    // element.VehicleRefID = vehicleObj.getInt("VehicleRefID");
                                                    element.driverName = AESCrypt.decrypt(driverObj.getString("driverName"));
                                                    element.mobileNo = AESCrypt.decrypt(driverObj.getString("mobileNo"));
                                                    element.driverDLNum = AESCrypt.decrypt(driverObj.getString("driverDLNum"));
                                                    element.driverDLValidFrom = driverObj.getString("driverDLValidFrom");
                                                    element.driverDLValidTill = driverObj.getString("driverDLValidTill");
                                                    element.driverDLValidTill = driverObj.getString("driverDLValidTill");
                                                    element.driverCRAID = driverObj.getString("cRAID");
                                                    driverlist.add(element);
                                                }
                                            } else {
                                                noDriverTextView.setVisibility(View.VISIBLE);
                                                noDriverTextView.setText(R.string.noDriversText);
                                            }
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // setInvisible();
                                                    driverVehicleListAdapter = new DriverVehicleListAdapter(getActivity(), driverlist, getFragmentManager());
                                                    list = (ListView) rootview.findViewById(R.id.vehiclelistdrivers);
                                                    list.setAdapter(driverVehicleListAdapter);
                                                    // setListViewHeightBasedOnItems(list);
                                                    list.setTextFilterEnabled(true);
                                                    list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                                                }
                                            });

                                        } catch (Exception e) {
                                            getActivity().runOnUiThread(() -> {
                                                progressdialog.dismiss();
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            });
                                        }
                                    }
                                });

                            } else {
                                try {
                                    requireActivity().runOnUiThread(progressdialog::dismiss);
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    getActivity().runOnUiThread(() -> {
                                        String errorText;
                                        try {
                                            errorText = index.getString("errorText");
                                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
                            getActivity().runOnUiThread(() -> {
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                                progressdialog.dismiss();
                                Toast.makeText(getActivity(), getString(R.string.admin), Toast.LENGTH_SHORT).show();
                            });
                            // getActivity().runOnUiThread(new Runnable() {​
//                                        public void run() {​
//
//                                            Toast.makeText(getActivity(),
//                                                    ex.toString(), Toast.LENGTH_LONG).show();
//                                        }​
//                                    }​);
                        } catch (JSONException ex) {
                            getActivity().runOnUiThread(() -> {
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                                progressdialog.dismiss();
                            });
                            //progressdialog.dismiss();
                            //Toast.makeText(MainActivity.this,ex.toString(), Toast.LENGTH_LONG).show();
                            //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                        }
                    }
                });
                thread.start();
            } else {
                Toast.makeText(getActivity(), getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }
    }
}