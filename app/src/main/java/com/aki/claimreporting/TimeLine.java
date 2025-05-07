package com.aki.claimreporting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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


public class TimeLine extends Fragment {


    public static String reqidval;
    public static FirebaseCrashlytics mCrashlytics;
    public ProgressDialog progressdialog;
    public String stokenval;
    public View rootview;
    DatabaseHelper mydb;
    Activity activity;
    ListView listView;
    ArrayList<TimeLineModel> timelinelist = new ArrayList<>();
    private TimelineAdapter adapter;

    public TimeLine() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_time_line, container, false);
        rootview = inflater.inflate(R.layout.time_line_fragment, container, false);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        activity = getActivity();
        init();
        return rootview;
    }

    public void init() {
        try {
            gettimelinefo();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "init", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
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

    public void gettimelinefo() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
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
                    MainActivity.encryptedSHA = "";
                    String sourceStr = MainActivity.InsertMobileParameters();
                    try {
                        MainActivity.encryptedSHA = AESCrypt.encrypt(sourceStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }

                    Thread thread = new Thread(() -> {
                        //   SharedPreferences claimPref = requireContext().getSharedPreferences("CRAID", Context.MODE_PRIVATE);
                        //  String claimidval = claimPref.getString(CraIdval, "");
                        SharedPreferences claimPref = requireActivity().getSharedPreferences("ClaimDetailsView", Context.MODE_PRIVATE);
                        String incidenid = claimPref.getString(CustomClaimHistoryList.ClaimrefID, "");
                        MainActivity.postURL = getString(R.string.uaturl) + "/app/Claim/GetClaim";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();
                        JsonObject Details = new JsonObject();
                        Details.addProperty("incidentUniqueCode", incidenid);
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
                            requireActivity().runOnUiThread(() ->
                                    progressdialog = ProgressDialog.show(getActivity(), getString(R.string.loading), getString(R.string.please_wait), true));
                            staticResponse = client.newCall(request).execute();
                            assert staticResponse.body() != null;
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
                                timelinelist.clear();
                                JSONArray claimResponseList = staticJsonObj.getJSONObject("rObj").getJSONArray("getAllLifecycle");
                                int responselength = claimResponseList.length();
                                if (responselength != 0) {
                                    for (int i = 0; i < claimResponseList.length(); i++) {
                                        JSONObject claimObj = claimResponseList.getJSONObject(i);
                                        TimeLineModel element = new TimeLineModel(
                                                claimObj.getString("createdOn"),
                                                claimObj.getString("eventName"),
                                                claimObj.getString("eventType")

                                        );

                                        timelinelist.add(element);
                                    }
                                    activity.runOnUiThread(() -> {
                                        adapter = new TimelineAdapter(getActivity(), timelinelist);
                                        listView = (ListView) rootview.findViewById(R.id.listtimeline);
                                        listView.setAdapter(adapter);
                                        listView.setTextFilterEnabled(true);
                                        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                        progressdialog.dismiss();

//                                            adapter = new DriverMappingAdapter(getApplicationContext(), dataModels);
//
//                                            adapter = new DriverMappingAdapter(dataModels, getApplicationContext());
//                                            listView.setAdapter(adapter);
//                                            Spinner insurSpinnerVal = (Spinner) findViewById(R.id.spinnerinsurance);
//                                            insurSpinnerVal.setOnItemSelectedListener(RegistrationStep1.this);
//                                            ArrayAdapter insunameAdapter = new ArrayAdapter(RegistrationStep1.this,android.R.layout.simple_list_item_1,insurcomspinner);
//                                            insurSpinnerVal.setAdapter(insunameAdapter);

                                    });
                                }
                            } else {
                                requireActivity().runOnUiThread(() -> progressdialog.dismiss());
                                try {
                                    JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                    JSONObject index = rmsg.getJSONObject(0);
                                    requireActivity().runOnUiThread(() -> {
                                        String errorText;
                                        try {
                                            errorText = index.getString("errorText");
                                            AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
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
                        } catch (final Exception ex) {
                            requireActivity().runOnUiThread(progressdialog::dismiss);
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
                            requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(),
                                    ex.toString(), Toast.LENGTH_LONG).show());
                        }
                    });
                    thread.start();
                }


            } else {
                Toast.makeText(getActivity(), getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}