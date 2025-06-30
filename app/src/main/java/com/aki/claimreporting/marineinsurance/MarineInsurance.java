package com.aki.claimreporting.marineinsurance;

import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.aki.claimreporting.MainActivity.JSON;
import static com.aki.claimreporting.MainActivity.alertTheUser;
import static com.aki.claimreporting.MainActivity.checkGPSStatus;
import static com.aki.claimreporting.MainActivity.isNetworkConnected;
import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.mydb;
import static com.aki.claimreporting.MainActivity.unauthorize;
import static com.aki.claimreporting.MainActivity.withTitleAndMessage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import com.aki.claimreporting.AddVehicle;
import com.aki.claimreporting.ClaimVehicleSelection;
import com.aki.claimreporting.Dashboard;
import com.aki.claimreporting.Login;
import com.aki.claimreporting.MainActivity;
import com.aki.claimreporting.R;
import com.aki.claimreporting.marineinsurance.models.MarineInsuranceCargoModel;
import com.aki.claimreporting.marineinsurance.models.MarineInsuranceHullModel;
import com.aki.claimreporting.AESCrypt;
import com.aki.claimreporting.DatabaseHelper;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MarineInsurance extends AppCompatActivity {
    //tool bar
    //okHttpClient
    OkHttpClient client;
    //client response
    Response response = null;
    //database helper
    DatabaseHelper databaseHelper;
    //token
    String token;
    //security code
    String securityCode;
    //certificate classification id
    int certificateClassificationId;
    //linear layouts
    LinearLayout cargoLayout, hullLayout;
    //marine insurance hull model
    MarineInsuranceHullModel marineInsuranceHullModel;
    //text views
    TextView tonnage, passengers, categoryOfUse;
    //marine insurance cargo model
    MarineInsuranceCargoModel marineInsuranceCargoModel;
    //text views
    TextView marineTitle, certificateNumber, certificateVerification, memberCompanyName, policyNumber, startDate, endDate, coverType, tradeType, mode, voyageFrom, voyageTo, loadingAirport, destinationAirport, transhipping, territorialLimit, modeOfTransport, loadingAt, registrationNumber, portOfRegistration;
    //progress dialog
    ProgressDialog progressDialog;
    //linear layouts
    LinearLayout invalidLayout;
    TableLayout canceledLayout;
    TextView canceledDate, canceledReason;
    //scroll view
    ScrollView scrollView;
    //text view
    TextView invalidText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marine_insurance);
        getWindow().setStatusBarColor(getColor(R.color.purple_500));
        try {

            //initialize variable
            initializeVariables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //to initialize variables
    private void initializeVariables() {
        try {
            try {
                getSupportActionBar().setTitle("Marine Verification Result");
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            invalidLayout = findViewById(R.id.invalidQRCodeLayout);
            invalidText = findViewById(R.id.invalidText);
            scrollView = findViewById(R.id.scrollView);
            marineTitle = findViewById(R.id.marineTitle);
            certificateNumber = findViewById(R.id.marineCertificateNumber);
            certificateVerification = findViewById(R.id.marineCertificateNumberVerification);
            memberCompanyName = findViewById(R.id.marineMemberCompanyName);
            policyNumber = findViewById(R.id.marinePolicyNumber);
            startDate = findViewById(R.id.marineStartDate);
            endDate = findViewById(R.id.marineEndDate);
            coverType = findViewById(R.id.marineCoverType);
            tradeType = findViewById(R.id.marineTradeType);
            mode = findViewById(R.id.marineMode);
            voyageFrom = findViewById(R.id.marineVoyageFrom);
            voyageTo = findViewById(R.id.marineVoyageTo);
            loadingAirport = findViewById(R.id.marineLoadingAirPort);
            destinationAirport = findViewById(R.id.marineDestinationAirPort);
            transhipping = findViewById(R.id.marineTranshipping);
            tonnage = findViewById(R.id.marineTonnage);
            passengers = findViewById(R.id.marinePassenger);
            categoryOfUse = findViewById(R.id.marineCategoryOfUse);
            cargoLayout = findViewById(R.id.cargoLayout);
            canceledLayout = findViewById(R.id.cencelledCertificate);
            canceledLayout.setVisibility(View.GONE);
            canceledDate = findViewById(R.id.resultCancelledDate);
            canceledReason = findViewById(R.id.resultCancelledreason);
            hullLayout = findViewById(R.id.hullLayout);
            territorialLimit = findViewById(R.id.marineTerritorialLimit);
            modeOfTransport = findViewById(R.id.modeOfTransport);
            loadingAt = findViewById(R.id.startingPoint);
            registrationNumber = findViewById(R.id.marineRegistrationNumber);
            portOfRegistration = findViewById(R.id.marinePortOfRegistration);


            Intent intent = getIntent();
            securityCode = intent.getStringExtra("QrCodeValue");
            //to verify
            progressDialog = progressDialog();
            progressDialog.show();
            marineCertificateVerification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void unauthorize() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(MarineInsurance.this);
        dialog.setMessage("Your session have been expired. Please login again to continue");
        dialog.setPositiveButton("Ok", (dialog1, which) -> {
            mydb = new DatabaseHelper(MarineInsurance.this);
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
            Intent login = new Intent(MarineInsurance.this, Dashboard.class);
            startActivity(login);
        });
        dialog.setCancelable(false);
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }*/


    //marine certificate verification
    private void marineCertificateVerification() {
        try {
            // New Thread to fetch the data from the API : Initialization.
            Thread thread = new Thread(() -> {
                // Creating OkHttp3 Client to rise request to the API.
                client = new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .build();

                databaseHelper = new DatabaseHelper(MarineInsurance.this);
                if (databaseHelper.getTokendetails().getCount() != 0) {
                    Cursor cursor = databaseHelper.getTokendetails();
                    int count = cursor.getCount();
                    if (count >= 1) {
                        while (cursor.moveToNext()) {
                            token = cursor.getString(1);
                        }
                    }
                }
                databaseHelper.close();
                JsonObject responseBody = new JsonObject();
                /*  responseBody.addProperty("CertificateClassificationID",1);*/
                responseBody.addProperty("PrintCode", securityCode);
                // Response Body.
                RequestBody requestBody = RequestBody.create(JSON, responseBody.toString());
                // Rising Request to the API.
                Request request = null;
                try {
                    request = new Request.Builder().url(getString(R.string.uaturl) + "/app/Marine/MarineVerification")
                            .header("MobileParameter", MainActivity.InsertMobileParameters()) // FingerPrint.
                            .header("Authorization", "Bearer " + token)
                            .post(requestBody)
                            .build();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Getting the response from the API.
                try {
                    // Executing the response.
                    response = client.newCall(request).execute();
                    // If the response body is not null
                    // If the response body is not null
                    if (response.code() == 401) {
                        runOnUiThread(() -> {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            unauthorize(MarineInsurance.this);
                        });
                    } else {
                        // Converting the response body to string.
                        String staticRes = response.body().string();
                        // Converting response body to JSON Object.
                        final JSONObject staticJsonObj = new JSONObject(staticRes);

                        // If the response code is equal to 200 convert the json object and attach the list to spinner.
                        if (staticJsonObj.getInt("rcode") == 200) {
                            try {
                                scrollView.setVisibility(View.VISIBLE);
                                invalidLayout.setVisibility(View.GONE);
                                JSONObject responseObject = staticJsonObj.getJSONObject("rObj");
                                JSONObject marineInsuranceCertificateObj = responseObject.getJSONObject("MarineInsuranceCertificate");
                                certificateClassificationId = marineInsuranceCertificateObj.optInt("certificateClassificationID");
                                if (certificateClassificationId == 1) {
                                    // Updated key names to match the camelCase convention used in the JSON data
                                    String certificateNumber = marineInsuranceCertificateObj.optString("certificateNumber", " - ");
                                    String memberCompanyName = marineInsuranceCertificateObj.optString("memberCompanyName", " - ");
                                    int entityId = marineInsuranceCertificateObj.optInt("entityId", 0);
                                    String policyNumber = marineInsuranceCertificateObj.optString("policyNumber", " - ");
                                    String startDate = marineInsuranceCertificateObj.optString("startDate", " - ");
                                    String endDate = marineInsuranceCertificateObj.optString("endDate", " - ");
                                    String registrationNumber = marineInsuranceCertificateObj.optString("registrationnumber", " - ");
                                    String insuredBy = marineInsuranceCertificateObj.optString("insuredBy", " - ");
                                    String nameOfVessel = marineInsuranceCertificateObj.optString("nameofVessel", " - ");
                                    String insuredOn = marineInsuranceCertificateObj.optString("insuredOn", " - ");
                                    boolean isCancelled = marineInsuranceCertificateObj.optBoolean("isCancelled", false);
                                    String cancellationOn = marineInsuranceCertificateObj.optString("cancellationOn", " - ");
                                    String kenyaDateTime = marineInsuranceCertificateObj.optString("kenyaDateTime", " - ");
                                    String cancellationReason = marineInsuranceCertificateObj.optString("cancellationReason", " - ");
                                    String certificateStatus = marineInsuranceCertificateObj.optString("certificateStatus", " - ");
                                    int tonnage = marineInsuranceCertificateObj.optInt("tonnage", 0);
                                    int passengers = marineInsuranceCertificateObj.optInt("passengers", 0);
                                    String categoryOfUse = marineInsuranceCertificateObj.optString("categoryofUse", " - ");
                                    int certificateClassificationId = marineInsuranceCertificateObj.optInt("certificateClassificationID", 0);
                                    int statusCode = marineInsuranceCertificateObj.optInt("statusCode", 0);
                                    String requestLocalDate = marineInsuranceCertificateObj.optString("requestLocalDate", " - ");
                                    String policyStartDate = marineInsuranceCertificateObj.optString("policyStartDate", " - ");
                                    String policyEndDate = marineInsuranceCertificateObj.optString("policyEndDate", " - ");
                                    String portOfRegistration = marineInsuranceCertificateObj.optString("portOfRegistration", " - ");
                                    String territorialLimit = marineInsuranceCertificateObj.optString("territorialLimit", " - ");
                                    marineInsuranceHullModel = new MarineInsuranceHullModel(
                                            certificateNumber,
                                            memberCompanyName,
                                            entityId,
                                            policyNumber,
                                            startDate,
                                            endDate,
                                            registrationNumber,
                                            insuredBy,
                                            nameOfVessel,
                                            insuredOn,
                                            isCancelled,
                                            cancellationOn,
                                            kenyaDateTime,
                                            cancellationReason,
                                            certificateStatus,
                                            tonnage,
                                            passengers,
                                            categoryOfUse,
                                            certificateClassificationId,
                                            statusCode,
                                            requestLocalDate,
                                            policyStartDate,
                                            policyEndDate,
                                            territorialLimit,
                                            portOfRegistration
                                    );
                                }
                                else if (certificateClassificationId == 2) {
                                    // Updated key names to match the camelCase convention used in the JSON data
                                    String certificateNumber = marineInsuranceCertificateObj.optString("certificateNumber", " - ");
                                    String memberCompanyName = marineInsuranceCertificateObj.optString("memberCompanyName", " - ");
                                    int entityId = marineInsuranceCertificateObj.optInt("entityId", 0);
                                    String policyNumber = marineInsuranceCertificateObj.optString("policyNumber", " - ");
                                    String startDate = marineInsuranceCertificateObj.optString("startDate", " - ");
                                    String endDate = marineInsuranceCertificateObj.optString("endDate", " - ");
                                    String registrationNumber = marineInsuranceCertificateObj.optString("registrationnumber", " - ");
                                    String insuredBy = marineInsuranceCertificateObj.optString("insuredBy", " - ");
                                    String nameOfVessel = marineInsuranceCertificateObj.optString("nameofVessel", " - ");
                                    String insuredOn = marineInsuranceCertificateObj.optString("insuredOn", " - ");
                                    boolean isCancelled = marineInsuranceCertificateObj.optBoolean("isCancelled", false);
                                    String cancellationOn = marineInsuranceCertificateObj.optString("cancellationOn", " - ");
                                    String kenyaDateTime = marineInsuranceCertificateObj.optString("kenyaDateTime", " - ");
                                    String cancellationReason = marineInsuranceCertificateObj.optString("cancellationReason", " - ");
                                    String certificateStatus = marineInsuranceCertificateObj.optString("certificateStatus", " - ");
                                    String tradeType = marineInsuranceCertificateObj.optString("tradeType", " - ");
                                    String mode = marineInsuranceCertificateObj.optString("mode", " - ");
                                    int modeId = marineInsuranceCertificateObj.optInt("modeId", 0);
                                    String voyageFrom = marineInsuranceCertificateObj.optString("voyageFrom", " - ");
                                    String voyageTo = marineInsuranceCertificateObj.optString("voyageTo", " - ");
                                    String loadingAt = marineInsuranceCertificateObj.optString("loadingAt", " - ");
                                    String portOfDischarge = marineInsuranceCertificateObj.optString("portofDischarge", " - ");
                                    String transhipping = marineInsuranceCertificateObj.optString("transhipping", " - ");
                                    String coverType = marineInsuranceCertificateObj.optString("coverType", " - ");
                                    double sumInsured = marineInsuranceCertificateObj.optDouble("sumInsured", 0.0);
                                    int certificateClassificationId = marineInsuranceCertificateObj.optInt("certificateClassificationID", 0);
                                    int statusCode = marineInsuranceCertificateObj.optInt("statusCode", 0);
                                    String requestLocalDate = marineInsuranceCertificateObj.optString("requestLocalDate", " - ");
                                    String policyStartDate = marineInsuranceCertificateObj.optString("policyStartDate", " - ");
                                    String policyEndDate = marineInsuranceCertificateObj.optString("policyEndDate", " - ");
                                    marineInsuranceCargoModel = new MarineInsuranceCargoModel(
                                            certificateNumber,
                                            memberCompanyName,
                                            entityId,
                                            policyNumber,
                                            startDate,
                                            endDate,
                                            registrationNumber,
                                            insuredBy,
                                            nameOfVessel,
                                            insuredOn,
                                            isCancelled,
                                            cancellationOn,
                                            kenyaDateTime,
                                            cancellationReason,
                                            certificateStatus,
                                            tradeType,
                                            mode,
                                            modeId,
                                            voyageFrom,
                                            voyageTo,
                                            loadingAt,
                                            portOfDischarge,
                                            transhipping,
                                            coverType,
                                            sumInsured,
                                            certificateClassificationId,
                                            statusCode,
                                            requestLocalDate,
                                            policyStartDate,
                                            policyEndDate
                                    );
                                }
                                //assign values
                                runOnUiThread(this::assignValues);
                            } catch (Exception e) {
                                runOnUiThread(() -> {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                });
                                e.printStackTrace();

                            }
                        }
                        // If response code equals to 500 alter the user using the api response.
                        else if (staticJsonObj.getInt("rcode") == 500) {
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                alertTheUser("Alert", getString(R.string.ErrorMessage), MarineInsurance.this);
                            });
                        } else if (staticJsonObj.getInt("rcode") == 400) {
                            runOnUiThread(() -> {
                                scrollView.setVisibility(View.GONE);
                                invalidLayout.setVisibility(View.VISIBLE);
                                String errorText = getString(R.string.ErrorMessage);
                                try {
                                    JSONObject rObj = staticJsonObj.getJSONObject("rObj");
                                    errorText = rObj.optString("message", getString(R.string.ErrorMessage));
                                } catch (Exception e) {
                                    e.printStackTrace(System.out);
                                }
                                invalidText.setText(errorText);
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                alertTheUser(getString(R.string.alert), getString(R.string.ErrorMessage), MarineInsurance.this);
                            });
                        }
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        scrollView.setVisibility(View.GONE);
                        invalidLayout.setVisibility(View.VISIBLE);
                        invalidText.setText(getString(R.string.ErrorMessage));
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    });
                    e.printStackTrace();

                }
            });
            // Starting the thread.
            //Network and GPS Connection Checking
            if (isNetworkConnected(this)) {
                if (checkGPSStatus(this)) {
                    // Starting the thread.
                    thread.start();
                } else {
                    try {
                        runOnUiThread(() -> {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        });
                        AlertDialog.Builder dialogs = withTitleAndMessage(getString(R.string.alert), getString(R.string.gps_not_enabled), this);
                        dialogs.setCancelable(false);
                        dialogs.setPositiveButton("Ok", (dialog1, which) -> {
                            //this will navigate user to the device location settings screen
                            Intent intent = new Intent(ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        });
                        dialogs.show();
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        });
                        e.printStackTrace();

                    }
                }

            } else {
                runOnUiThread(() -> {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    makeText(MarineInsurance.this, getString(R.string.noNetwork), LENGTH_SHORT).show();
                });

            }
        } catch (Exception e) {
            runOnUiThread(() -> {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            });
            e.printStackTrace();
        }
    }

    //assign values
    private void assignValues() {
        try {
            canceledLayout.setVisibility(View.GONE);
            if (certificateClassificationId == 2) {
                cargoLayout.setVisibility(View.VISIBLE);
                hullLayout.setVisibility(View.GONE);
                if (marineInsuranceCargoModel.getCertificateClassificationId() != 0) {
                    String title = "Cargo";
                    marineTitle.setText(title);
                }
                certificateNumber.setText(checker(marineInsuranceCargoModel.getCertificateNumber()));
                if (marineInsuranceCargoModel.getStatusCode() != 0) {
                    certificateVerification.setText(Html.fromHtml(statusCode(marineInsuranceCargoModel.getStatusCode())));
                } else {
                    certificateVerification.setText(" - ");
                }
                memberCompanyName.setText(checker(marineInsuranceCargoModel.getMemberCompanyName()));
                policyNumber.setText(checker(marineInsuranceCargoModel.getPolicyNumber()));
                startDate.setText(checker(marineInsuranceCargoModel.getStartDate()));
                endDate.setText(checker(marineInsuranceCargoModel.getEndDate()));
                coverType.setText(checker(marineInsuranceCargoModel.getCoverType()));
                tradeType.setText(checker(marineInsuranceCargoModel.getTradeType()));
                mode.setText(checker(marineInsuranceCargoModel.getMode()));
                voyageFrom.setText(checker(marineInsuranceCargoModel.getVoyageFrom()));
                voyageTo.setText(checker(marineInsuranceCargoModel.getVoyageTo()));
                loadingAirport.setText(checker(marineInsuranceCargoModel.getLoadingAt()));
                destinationAirport.setText(checker(marineInsuranceCargoModel.getPortOfDischarge()));
                transhipping.setText(checker(marineInsuranceCargoModel.getTranshipping()));
                String modeOfTransPort = checker(marineInsuranceCargoModel.getMode());
                modeOfTransport.setText(modeOfTransPort.trim().equals("Air") ? "Destination Airport" : "Port of Discharge");
                loadingAt.setText(modeOfTransPort.trim().equals("Air") ? "Loading Airport" : "Loading at");
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if(marineInsuranceCargoModel.getStatusCode() == 300) {
                    canceledLayout.setVisibility(View.VISIBLE);
                    canceledDate.setText(checker(marineInsuranceCargoModel.getCancellationOn()));
                    canceledReason.setText(checker(marineInsuranceCargoModel.getCancellationReason()));
                }
            } else if (certificateClassificationId == 1) {
                hullLayout.setVisibility(View.VISIBLE);
                cargoLayout.setVisibility(View.GONE);
                if (marineInsuranceHullModel.getCertificateClassificationId() != 0) {
                    String title = "Hull";
                    marineTitle.setText(title);
                }
                certificateNumber.setText(checker(marineInsuranceHullModel.getCertificateNumber()));
                if (marineInsuranceHullModel.getStatusCode() != 0) {
                    certificateVerification.setText(Html.fromHtml(statusCode(marineInsuranceHullModel.getStatusCode())));
                } else {
                    certificateVerification.setText(" - ");
                }
                memberCompanyName.setText(checker(marineInsuranceHullModel.getMemberCompanyName()));
                policyNumber.setText(checker(marineInsuranceHullModel.getPolicyNumber()));
                startDate.setText(checker(marineInsuranceHullModel.getStartDate()));
                endDate.setText(checker(marineInsuranceHullModel.getEndDate()));
                tonnage.setText(checker(marineInsuranceHullModel.getTonnage()));
                passengers.setText(checker(marineInsuranceHullModel.getPassengers()));
                categoryOfUse.setText(checker(marineInsuranceHullModel.getCategoryOfUse()));
                territorialLimit.setText(checker(marineInsuranceHullModel.getTerritorialLimit()));
                registrationNumber.setText(checker(marineInsuranceHullModel.getRegistrationNumber()));
                portOfRegistration.setText(checker(marineInsuranceHullModel.getPortOfRegistration()));
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if(marineInsuranceHullModel.getStatusCode() == 300) {
                    canceledLayout.setVisibility(View.VISIBLE);
                    canceledDate.setText(checker(marineInsuranceHullModel.getCancellationOn()));
                    canceledReason.setText(checker(marineInsuranceHullModel.getCancellationReason()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //checker
    private String checker(Object value) {
        try {
            String eValue = String.valueOf(value).trim();
            if (value.equals("0") || value.equals("null") || value.equals(" - ") || eValue.trim().isEmpty()) {
                return " - ";
            } else {
                return " " + value;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return " - ";
        }
    }

    //status code checker
    private String statusCode(int statusCode) {
        try {
            switch (statusCode) {
                case 100:
                    return ("<font color=\"#6B8E23\"> (Genuine)</font>");
                case 200:
                    return ("<font color=\"#FFA500\"> (Expired)</font>");
                case 300:
                    return ("<font color=\"#964B00\"> (Cancelled)</font>");
                case 500:
                    return ("<font color=\"#6495ED\"> (Upcoming)</font>");
                default:
                    return " - ";

            }
        } catch (Exception e) {
            e.printStackTrace();
            return " - ";
        }

    }

    //progress dialog
    private ProgressDialog progressDialog() {
        try {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading");
            progressDialog.setCancelable(false);
            return progressDialog;
        } catch (Exception e) {
            e.printStackTrace();
            return new ProgressDialog(this);
        }
    }

    //on create option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem settingsItem = menu.findItem(R.id.action_to_home);
        MenuItemCompat.setIconTintList(settingsItem, ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        return true;
    }

    //on option item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_to_home) {
            startActivity(new Intent(this, Dashboard.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //on back pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            startActivity(new Intent(this, Dashboard.class));
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    //ERROR LOG
    private void log(String response) {
        try {
            Log.i(null, response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}