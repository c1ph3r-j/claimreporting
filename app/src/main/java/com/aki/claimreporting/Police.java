package com.aki.claimreporting;


import static com.aki.claimreporting.CertificateActivation.postURL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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

public class Police extends Fragment {

    public static String reqidval;
    public String stokenval, encryptedSHA;
    public ProgressDialog progressdialog;
    public View rootview;
    public LinearLayout policeprese;
    public LinearLayout policenorec;
    public ListView list;
    SearchView searchView;
    Activity activity;
    ArrayList<ServiceProviderModel> serviceproviderlist = new ArrayList<ServiceProviderModel>();
    CustomServiceProvider customServiceProvider;
    DatabaseHelper mydb;
    SharedPreferences sharedpreferences;
    private FirebaseCrashlytics mCrashlytics;
    private TextView dialpolice;

    public Police() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.police_fragment, container, false);
        init();
        mCrashlytics = FirebaseCrashlytics.getInstance();
        return rootview;
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            //  policeprese = (LinearLayout) rootview.findViewById(R.id.policepresent);
            dialpolice = (TextView) rootview.findViewById(R.id.dialpolice);
            dialpolice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent viewDetails = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:999"));
                    startActivity(viewDetails);
                }
            });
            policenorec = (LinearLayout) rootview.findViewById(R.id.policenorecords);
            policenorec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentmap = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=near%20by%20police%20stations"));
                    startActivity(intentmap);
                }
            });
            //  policeprese.setVisibility(View.GONE);
            // policenorec.setVisibility(View.GONE);
//            searchView = (SearchView) rootview.findViewById(R.id.searchView);
//            try {
//                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                    @Override
//                    public boolean onQueryTextSubmit(String query) {
//
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onQueryTextChange(String newText) {
//
//                        try {
//                            if (TextUtils.isEmpty(newText)) {
//                                customServiceProvider.filter("");
//                                list.clearTextFilter();
//                            } else {
//                                customServiceProvider.filter(newText);
//                            }
//                            return true;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                            mCrashlytics.recordException(e);
//                        }
//                        return true;
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
//                mCrashlytics.recordException(e);
//            }
//
//            getserviceproviderinfo();
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

    public void getserviceproviderinfo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected() == true) {

                if (checkGPSStatus() == true) {

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
                    progressdialog = new ProgressDialog(getActivity());
                    encryptedSHA = "";
                    String sourceStr = MainActivity.InsertMobileParameters();
                    String smobileparam = MainActivity.InsertMobileParameters();
                    //String smobileparam = "{ \"imeino1\": \"355844090234339\", \"imeino2\": \"355844093244335\", \"timezone\": \"+0630GMT:IST Asia/Kolkata\", \"currentdatetime\": \"12-10-2019 18:33:14\", \"Latitude\": \"36.80537\", \"Longitude\": \"-1.25848\",\"IpAddress\": \"192.168.2.100\"}";
                    try {
                        encryptedSHA = AESCrypt.encrypt(sourceStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    Thread thread = new Thread(new Runnable() {

                        public void run() {
                            postURL = getString(R.string.uaturl) + "/app/ServiceProvider/GetAllServiceProviderByLocation";
                            //MainActivity.postURL = "https://uat-aki.claims.digital/api/app/ServiceProvider/GetAllServiceProviderByLocation";
                            final MediaType JSON
                                    = MediaType.parse("application/json; charset=utf-8");
                            //OkHttpClient client = new OkHttpClient();
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(120, TimeUnit.SECONDS)
                                    .writeTimeout(120, TimeUnit.SECONDS)
                                    .readTimeout(120, TimeUnit.SECONDS)
                                    .build();
                            JsonObject Details = new JsonObject();
                            Details.addProperty("doctype", "PoliceStation");
                            //  Details.addProperty("doctype", "PoliceStation");
                            // Details.addProperty("doctype", "TowingAgencies");
                            //Details.addProperty("doctype", "Ambulance");
                            // Details.addProperty("doctype", "Assessors");
                            // Details.addProperty("doctype", "Garages");
                            // Details.addProperty("doctype", "InsuranceCompany");
                            String insertString = Details.toString();
                            RequestBody body = RequestBody.create(JSON, insertString);
                            Request request = new Request.Builder()
                                    .url(postURL)
                                    .header("Authorization", "Bearer " + stokenval)
                                    .header("MobileParameter", smobileparam)
//                                    .header("MobileParameters", MainActivity.InsertMobileparameters())
                                    .post(body)
                                    .build();
                            Response staticResponse = null;

                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressdialog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true);
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
                                    //final JSONObject staticfinalObj = staticJsonObj.getJSONObject("rObj").getJSONObject("iN_DMVICCertificate");
                                    // sOTP= staticJsonObj.getJSONObject("rObj").getString("OTP");
                                    //  sOTPID= staticJsonObj.getJSONObject("rObj").getString("OTPID");
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressdialog.dismiss();

                                            //Intent step2 = new Intent(CreateDriver.this, DriverMapping.class);
                                            // startActivity(step2);
                                        }
                                    });

//
                                    JSONArray serviceproviderList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllServiceProvider");
                                    if (serviceproviderList.length() == 0) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                policenorec.setVisibility(View.VISIBLE);
                                            }
                                        });


                                    } else {
                                        serviceproviderlist = new ArrayList<>();
                                        for (int i = 0; i < serviceproviderList.length(); i++) {
                                            try {

                                                JSONObject serviceObj = serviceproviderList.getJSONObject(i);
                                                if (serviceObj.getString("location") == null || serviceObj.getString("location") == "null") {
                                                    String testval = "testval";
                                                } else {
                                                    Double latval = serviceObj.getJSONObject("location").getJSONArray("coordinates").getDouble(1);
                                                    Double longval = serviceObj.getJSONObject("location").getJSONArray("coordinates").getDouble(1);
                                                    String latdata = String.valueOf(latval);
                                                    String longdata = String.valueOf(longval);
                                                    ServiceProviderModel element = new ServiceProviderModel(serviceObj.getString("entityName"), serviceObj.getString("sector"), serviceObj.getString("address1"), serviceObj.getString("address2"), serviceObj.getString("address3"), serviceObj.getString("city"), serviceObj.getString("county"), serviceObj.getString("phoneNumber"), serviceObj.getString("distanceRoute"), serviceObj.getString("doctype"), serviceObj.getString("id"), latdata, longdata);
                                                    // element.VehicleRefID = vehicleObj.getInt("VehicleRefID");
                                                    element.entityName = serviceObj.getString("entityName");
                                                    element.sector = serviceObj.getString("sector");
//                            element.InsuranceCompanyID =  vehicleObj.getInt("InsuranceCompanyID");
                                                    element.address1 = serviceObj.getString("address1");
                                                    element.address2 = serviceObj.getString("address2");
                                                    element.address3 = serviceObj.getString("address3");
                                                    element.city = serviceObj.getString("city");
                                                    element.county = serviceObj.getString("county");
                                                    element.Phnnum = serviceObj.getString("phoneNumber");
                                                    element.distance = serviceObj.getString("distanceRoute");
                                                    element.doctype = serviceObj.getString("doctype");
                                                    element.entityID = serviceObj.getString("id");
                                                    element.latdt = latdata;
                                                    element.longdt = longdata;
//                            element.TypeOfVehicleID =  vehicleObj.getInt("TypeOfVehicleID");
//                            element.CoverTypeID =  vehicleObj.getInt("CoverTypeID");
//                            element.YearOfManufacture = vehicleObj.getString("YearOfManufacture");
//                            element.VehicleMake =  vehicleObj.getString("VehicleMake");
//                            element.VehicleModel = vehicleObj.getString("VehicleModel");
//                            element.PolicyStartDate = vehicleObj.getString("PolicyStartDate");

                                                    //regspinner.add(vehicleObj.getString("RegistrationNo"));
                                                    serviceproviderlist.add(element);
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                                mCrashlytics.recordException(e);
                                            }

                                        }

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // setInvisible();
                                                policeprese.setVisibility(View.VISIBLE);
                                                customServiceProvider = new CustomServiceProvider(getActivity(), serviceproviderlist);

                                                list = (ListView) rootview.findViewById(R.id.listserviceprovider);
                                                list.setAdapter(customServiceProvider);
                                                // setListViewHeightBasedOnItems(list);
                                                list.setTextFilterEnabled(true);
                                                list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                                                        ServiceProviderModel dataModel = (ServiceProviderModel) serviceproviderlist.get(position);
                                                        String entityname = dataModel.getEntityName();
                                                        customServiceProvider.notifyDataSetChanged();
                                                    }
                                                });

                                            }
                                        });
                                    }


                                } else {
                                    try {
                                        activity.runOnUiThread(progressdialog::dismiss);
                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                        JSONObject index = rmsg.getJSONObject(0);
                                        activity.runOnUiThread(() -> {
                                            String errorText = null;
                                            String trnId = null;
                                            try {
                                                errorText = index.getString("errorText");
                                                trnId = staticJsonObj.getString("trnID");
                                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
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
                            } catch (final IOException e) {
                                requireActivity().runOnUiThread(progressdialog::dismiss);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {

                                        Toast.makeText(getActivity(), getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                requireActivity().runOnUiThread(progressdialog::dismiss);
                                e.printStackTrace();
                                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                mCrashlytics.recordException(e);
                                Toast.makeText(getActivity(), getString(R.string.admin), Toast.LENGTH_SHORT).show();
                                //  MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));
                            }
                        }

                    });
                    thread.start();


                } else {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//                    dialog.setMessage("GPS locations is not enabled.Please enable it");
//                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //this will navigate user to the device location settings screen
//                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivity(intent);
//                        }
//                    });
//                    AlertDialog alert = dialog.create();
//                    alert.show();
                }

            } else {
                Toast.makeText(getActivity(), getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //progressdialog.dismiss();
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
            Toast.makeText(getActivity(), getString(R.string.admin), Toast.LENGTH_SHORT).show();
            // MainActivity.Mobileerrorlog(getApplicationContext(),ex.getMessage().toString(),postURL,String.valueOf(errorcode));

        }
    }
}