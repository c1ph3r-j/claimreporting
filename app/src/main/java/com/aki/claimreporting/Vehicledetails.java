package com.aki.claimreporting;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Vehicledetails extends Fragment {
    public static String reqidval;
    public String stokenval, encryptedSHA, validfromdate, validtodate;
    public ProgressDialog progressdialog;
    public View rootview;
    public ImageView imgpdfview;
    DatabaseHelper mydb;
    private FirebaseCrashlytics mCrashlytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.vehicledetails_fragment, container, false);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        init();
        return rootview;
    }

    public void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            getmyvehicledetails();
            imgpdfview = (ImageView) rootview.findViewById(R.id.imgpdfview);
            try {
                imgpdfview.setOnClickListener(onClickImgPdf -> ViewVehiclePDF());
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


    public void ViewVehiclePDF() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(() -> {
                    mydb = new DatabaseHelper(getActivity());
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                MainActivity.stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }
                    SharedPreferences viewvehPref = getActivity().getSharedPreferences("ViewVehicle", Context.MODE_PRIVATE);
                    String certnumview = viewvehPref.getString(MyVehicles.ViewCertificateno, "");
                    SharedPreferences vehcidPref = getActivity().getSharedPreferences("VehicleRefID", Context.MODE_PRIVATE);
                    String vehcidprefval = vehcidPref.getString(MyVehicles.Viewvehicleref, "");
                    String vehicleidprefval = null;
                    try {
                        vehicleidprefval = AESCrypt.decrypt(vehcidprefval);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }

                    MainActivity.postURL = getString(R.string.uaturl) + "/app/Vehicle/ViewVehiclePDF";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient();
                    JsonObject Details = new JsonObject();
                    Details.addProperty("certificateNo", certnumview);
                    Details.addProperty("vehicleID", vehicleidprefval);
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
                        getActivity().runOnUiThread(() -> {
                            progressdialog = ProgressDialog.show(getActivity(), getString(R.string.loading), getString(R.string.please_wait), true);
                        });
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
                            getActivity().runOnUiThread(() -> {
                                progressdialog.dismiss();
                                String pdfurldoc = null;
                                try {
                                    pdfurldoc = staticJsonObj.getJSONObject("rObj").getString("blobDownloadURL");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                    mCrashlytics.recordException(e);
                                }
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdfurldoc));
                                startActivity(browserIntent);
                            });
                        } else if (staticJsonObj.getInt("rcode") == 2) {
                            getActivity().runOnUiThread(() -> {
                                progressdialog.dismiss();
                                Toast.makeText(getActivity(), getString(R.string.pdf_is_generating), Toast.LENGTH_SHORT).show();
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
                    } catch (final Exception ex) {
                        getActivity().runOnUiThread(() -> {
                            progressdialog.dismiss();
                            ex.printStackTrace();
                            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                            mCrashlytics.recordException(ex);
                            Toast.makeText(getActivity(),
                                    ex.toString(), Toast.LENGTH_LONG).show();
                        });
                    }
                });
                thread.start();
            } else {
                Toast.makeText(getActivity(), getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            progressdialog.dismiss();
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void getmyvehicledetails() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (isNetworkConnected()) {
                Thread thread = new Thread(() -> {
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
                    SharedPreferences viewvehPref = getActivity().getSharedPreferences("ViewVehicle", Context.MODE_PRIVATE);
                    String certnumview = viewvehPref.getString(MyVehicles.ViewCertificateno, "");
                    SharedPreferences vehcidPref = getActivity().getSharedPreferences("VehicleRefID", Context.MODE_PRIVATE);
                    String vehcidprefval = vehcidPref.getString(MyVehicles.Viewvehicleref, "");
                    String certnumencrypt = null;
                    String vehicleidprefval = null;
                    try {
                        certnumencrypt = AESCrypt.encrypt(certnumview);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    try {
                        String encryptest = AESCrypt.decrypt(certnumencrypt);
                        vehicleidprefval = AESCrypt.decrypt(vehcidprefval);
                        String encryptest2 = encryptest;
                    } catch (Exception e) {
                        e.printStackTrace();
                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                        mCrashlytics.recordException(e);
                    }
                    MainActivity.postURL = getString(R.string.uaturl) + "/app/Vehicle/GetVehicle";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient();
                    JsonObject Details = new JsonObject();
                    Details.addProperty("certificateNo", certnumview);
                    Details.addProperty("vehicleID", vehicleidprefval);
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
                        getActivity().runOnUiThread(() -> {
                            progressdialog = ProgressDialog.show(getActivity(), getString(R.string.loading), getString(R.string.please_wait), true);
                        });
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
                            getActivity().runOnUiThread(() -> {
                                try {
                                    final JSONObject vehicleResponselist = staticJsonObj.getJSONObject("rObj").getJSONObject("getVehicle");
                                    TextView policyno = (TextView) rootview.findViewById(R.id.txtpolicynoval);
                                    policyno.setText(AESCrypt.decrypt(vehicleResponselist.getString("policyNo")));
                                    // TextView certificateType = (TextView) rootview.findViewById(R.id.txtcertificateval);
                                    // certificateType.setText(vehicleResponselist.getString("certificateType"));
                                    TextView coverageType = (TextView) rootview.findViewById(R.id.txtcoverageval);
                                    coverageType.setText(AESCrypt.decrypt(vehicleResponselist.getString("coverageType")));
                                    TextView certificateNo = (TextView) rootview.findViewById(R.id.txtcertificatenoval);
                                    certificateNo.setText(AESCrypt.decrypt(vehicleResponselist.getString("certificateNo")));
                                    String dtStart = vehicleResponselist.getString("policyBeginDate");
                                    String dtend = vehicleResponselist.getString("policyEndDate");
                                    SimpleDateFormat formatstart = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    SimpleDateFormat formatend = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    try {
                                        Date datefrompolicy = formatstart.parse(dtStart);
                                        Date datetopolicy = formatend.parse(dtend);
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                        validfromdate = formatter.format(Date.parse(String.valueOf(datefrompolicy)));
                                        validtodate = formatter.format(Date.parse(String.valueOf(datetopolicy)));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                    TextView policyBeginDate = (TextView) rootview.findViewById(R.id.txtpolicystdateval);
                                    policyBeginDate.setText(validfromdate);
                                    TextView policyEndDate = (TextView) rootview.findViewById(R.id.txtpolicyenddtval);
                                    policyEndDate.setText(validtodate);
                                    TextView registrationNo = (TextView) rootview.findViewById(R.id.txtregistrationnoval);
                                    registrationNo.setText(AESCrypt.decrypt(vehicleResponselist.getString("registrationNo")));
                                    TextView vINNumber = (TextView) rootview.findViewById(R.id.txtvinnoval);
                                    vINNumber.setText(AESCrypt.decrypt(vehicleResponselist.getString("vINNumber")));
                                    TextView make = (TextView) rootview.findViewById(R.id.txtmakeval);
                                    make.setText(AESCrypt.decrypt(vehicleResponselist.getString("make")));
                                    TextView model = (TextView) rootview.findViewById(R.id.txtmodelval);
                                    model.setText(AESCrypt.decrypt(vehicleResponselist.getString("model")));
                                    TextView yearOfMfg = (TextView) rootview.findViewById(R.id.txtyearval);
                                    yearOfMfg.setText(vehicleResponselist.getString("yearOfMfg"));
                                    TextView insurerName = (TextView) rootview.findViewById(R.id.txtinsucompanyval);
                                    insurerName.setText(vehicleResponselist.getString("insurerName"));
                                    TextView insuredName = (TextView) rootview.findViewById(R.id.txtpolicyholderval);
                                    insuredName.setText(AESCrypt.decrypt(vehicleResponselist.getString("insuredName")));
                                    TextView insuredPhoneNo = (TextView) rootview.findViewById(R.id.txtinsuredphnoval);
                                    insuredPhoneNo.setText(AESCrypt.decrypt(vehicleResponselist.getString("insuredPhoneNo")));
                                    TextView insuredMailId = (TextView) rootview.findViewById(R.id.txtinsuredmailval);
                                    insuredMailId.setText(AESCrypt.decrypt(vehicleResponselist.getString("insuredMailId")));
                                    progressdialog.dismiss();
                                } catch (Exception ex) {
                                    getActivity().runOnUiThread(() -> progressdialog.dismiss());
                                    ex.printStackTrace();
                                    MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                    mCrashlytics.recordException(ex);
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
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                        MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                        mCrashlytics.recordException(ex);
                        getActivity().runOnUiThread(() -> {
                            progressdialog.dismiss();
                            Toast.makeText(getActivity(), getString(R.string.admin), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                thread.start();
            } else {
                Toast.makeText(getActivity(), getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            requireActivity().runOnUiThread(() -> progressdialog.dismiss());
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
        }
    }
}