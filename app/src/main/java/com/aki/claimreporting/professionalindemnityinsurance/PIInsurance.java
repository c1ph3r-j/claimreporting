package com.aki.claimreporting.professionalindemnityinsurance;

import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.aki.claimreporting.MainActivity.JSON;
import static com.aki.claimreporting.MainActivity.alertTheUser;
import static com.aki.claimreporting.MainActivity.checkGPSStatus;
import static com.aki.claimreporting.MainActivity.isNetworkConnected;
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
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import com.aki.claimreporting.Dashboard;
import com.aki.claimreporting.Login;
import com.aki.claimreporting.MainActivity;
import com.aki.claimreporting.R;
import com.aki.claimreporting.professionalindemnityinsurance.models.ProfessionalIndemnityModel;
import com.aki.claimreporting.AESCrypt;
import com.aki.claimreporting.DatabaseHelper;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PIInsurance extends AppCompatActivity {

    //tool bar
    Toolbar toolbar;
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
    //progress dialog
    ProgressDialog progressDialog;
    //PI Certificate Model
    ProfessionalIndemnityModel professionalIndemnityModel;
    //text views
    TextView certificateNumber, certificateVerification, memberCompanyName, policyNumber, startDate, endDate, occupation, specialization;
    //linear layouts
    LinearLayout invalidLayout;
    //scroll view
    ScrollView scrollView;
    TableLayout canceledLayout;
    TextView canceledDate, canceledReason;

    //text view
    TextView invalidText;

    //table layout
    TableRow occupationLayout, specializationLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piinsurance);
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
            getSupportActionBar().setTitle("PI Verification Result");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        invalidLayout = findViewById(R.id.invalidQRCodeLayout);
        invalidText = findViewById(R.id.invalidText);
        scrollView = findViewById(R.id.scrollView);
        Intent intent = getIntent();
        securityCode = intent.getStringExtra("QrCodeValue");
        certificateNumber = findViewById(R.id.piCertificateNumber);
        certificateVerification = findViewById(R.id.piCertificateNumberVerification);
        memberCompanyName = findViewById(R.id.piMemberCertificateNumber);
        policyNumber = findViewById(R.id.piPolicyNumber);
        startDate = findViewById(R.id.piStartDate);
        endDate = findViewById(R.id.piEndDate);
        occupation = findViewById(R.id.piOccupation);
        specialization = findViewById(R.id.piSpecialization);
        specializationLayout = findViewById(R.id.specializationLayout);
        occupationLayout = findViewById(R.id.occupationLayout);
        canceledLayout = findViewById(R.id.cencelledCertificate);
        canceledDate = findViewById(R.id.resultCancelledDate);
        canceledReason = findViewById(R.id.resultCancelledreason);
        canceledLayout.setVisibility(View.GONE);
        progressDialog = progressDialog();
        progressDialog.show();
        //professional indemnity
        piCertificateVerification();
    }

    /*public void unauthorize() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(PIInsurance.this);
        dialog.setMessage("Your session have been expired. Please login again to continue");
        dialog.setPositiveButton("Ok", (dialog1, which) -> {
            mydb = new DatabaseHelper(PIInsurance.this);
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
            Intent login = new Intent(PIInsurance.this, Dashboard.class);
            startActivity(login);
        });
        dialog.setCancelable(false);
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }*/

    //pi certificate verification
    private void piCertificateVerification() {
        try {
            // New Thread to fetch the data from the API : Initialization.
            Thread thread = new Thread(() -> {
                // Creating OkHttp3 Client to rise request to the API.
                client = new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .build();

                databaseHelper = new DatabaseHelper(PIInsurance.this);
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
                /*responseBody.addProperty("CertificateClassificationID",1);*/
                responseBody.addProperty("PrintCode", securityCode);
                // Response Body.
                RequestBody requestBody = RequestBody.create(JSON, responseBody.toString());
                // Rising Request to the API.
                Request request = null;
                try {
                    request = new Request.Builder().url(getString(R.string.uaturl) + "/app/PI/PIVerification")
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
                    if (response.code() == 401) {
                        runOnUiThread(() -> {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            unauthorize(PIInsurance.this);
                        });
                    } else {
                        // Converting the response body to string.
                        assert response.body() != null;
                        String staticRes = response.body().string();
                        // Printing the response body in the log.
                        // log(staticRes);
                        // Converting response body to JSON Object.
                        final JSONObject staticJsonObj = new JSONObject(staticRes);

                        // If the response code is equal to 200 convert the json object and attach the list to spinner.
                        if (staticJsonObj.getInt("rcode") == 200) {
                            try {
                                scrollView.setVisibility(View.VISIBLE);
                                invalidLayout.setVisibility(View.GONE);
                                JSONObject responseObject = staticJsonObj.getJSONObject("rObj");

                                JSONObject pIInsuranceCertificateObj = responseObject.getJSONObject("PIInsuranceCertificate");

// Use camelCase keys for extracting values
                                String certificateNumber = pIInsuranceCertificateObj.optString("certificateNumber", "");
                                String memberCompanyName = pIInsuranceCertificateObj.optString("memberCompanyName", "");
                                int entityId = pIInsuranceCertificateObj.optInt("entityId", 0);
                                String policyNumber = pIInsuranceCertificateObj.optString("policyNumber", "");
                                String occupation = pIInsuranceCertificateObj.optString("occupation", "");
                                String specialisation = pIInsuranceCertificateObj.optString("specialisation", "");
                                String startDate = pIInsuranceCertificateObj.optString("startDate", "");
                                String endDate = pIInsuranceCertificateObj.optString("endDate", "");
                                String insuredBy = pIInsuranceCertificateObj.optString("insuredBy", "");
                                boolean isCancelled = pIInsuranceCertificateObj.optBoolean("isCancelled", false);
                                String cancellationOn = pIInsuranceCertificateObj.optString("cancellationOn", "");
                                String kenyaDateTime = pIInsuranceCertificateObj.optString("kenyaDateTime", "");
                                String cancellationReason = pIInsuranceCertificateObj.optString("cancellationReason", "");
                                String certificateStatus = pIInsuranceCertificateObj.optString("certificateStatus", "");
                                int certificateClassificationId = pIInsuranceCertificateObj.optInt("certificateClassificationID", 0);
                                int statusCode = pIInsuranceCertificateObj.optInt("statusCode", 0);
                                String requestLocalDate = pIInsuranceCertificateObj.optString("requestLocalDate", "");
                                String policyStartDate = pIInsuranceCertificateObj.optString("policyStartDate", "");
                                String policyEndDate = pIInsuranceCertificateObj.optString("policyEndDate", "");

                                professionalIndemnityModel = new ProfessionalIndemnityModel(
                                        certificateNumber,
                                        memberCompanyName,
                                        entityId,
                                        policyNumber,
                                        occupation,
                                        specialisation,
                                        startDate,
                                        endDate,
                                        insuredBy,
                                        isCancelled,
                                        cancellationOn,
                                        kenyaDateTime,
                                        cancellationReason,
                                        certificateStatus,
                                        certificateClassificationId,
                                        statusCode,
                                        requestLocalDate,
                                        policyStartDate,
                                        policyEndDate
                                );
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
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                alertTheUser(getString(R.string.alert), getString(R.string.ErrorMessage), PIInsurance.this);

                            });
                        } else if (staticJsonObj.getInt("rcode") == 400) {
                            runOnUiThread(() -> {
                                scrollView.setVisibility(View.GONE);
                                invalidLayout.setVisibility(View.VISIBLE);
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                alertTheUser(getString(R.string.alert), getString(R.string.ErrorMessage), PIInsurance.this);

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
                    makeText(PIInsurance.this, getString(R.string.noNetwork), LENGTH_SHORT).show();
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

    //to assign values
    private void assignValues() {
        try {
            certificateNumber.setText(checker(professionalIndemnityModel.getCertificateNumber()));
            if (professionalIndemnityModel.getStatusCode() != 0) {
                certificateVerification.setText(Html.fromHtml(statusCode(professionalIndemnityModel.getStatusCode())));
            } else {
                certificateVerification.setText(" - ");
            }
            if (checker(professionalIndemnityModel.getCertificateNumber()).contains("PID")) {
                ((TextView) findViewById(R.id.pageTitle)).setText(R.string.doctor_certificate_);
            }
            if(professionalIndemnityModel.getStatusCode() == 300) {
                canceledLayout.setVisibility(View.VISIBLE);
                canceledDate.setText(checker(professionalIndemnityModel.getCancellationOn()));
                canceledReason.setText(checker(professionalIndemnityModel.getCancellationReason()));
            }
            memberCompanyName.setText(checker(professionalIndemnityModel.getMemberCompanyName()));
            policyNumber.setText(checker(professionalIndemnityModel.getPolicyNumber()));
            startDate.setText(checker(professionalIndemnityModel.getStartDate()));
            endDate.setText(checker(professionalIndemnityModel.getEndDate()));
            System.out.println(professionalIndemnityModel.getSpecialisation());
            specializationLayout.setVisibility(professionalIndemnityModel.getSpecialisation().equals("null") ? View.GONE : View.VISIBLE);
            occupationLayout.setVisibility(professionalIndemnityModel.getOccupation().equals("null") ? View.GONE : View.VISIBLE);
            occupation.setText(checker(professionalIndemnityModel.getOccupation()));
            specialization.setText(checker(professionalIndemnityModel.getSpecialisation()));
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
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
                    return "";

            }
        } catch (Exception e) {
            e.printStackTrace();
            return " - ";
        }

    }

    //date format
    private String dateFormat(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            Date date = inputFormat.parse(inputDate);
            assert date != null;
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
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

    //ERROR LOG
    private void log(String response) {
        try {
            Log.i(null, response);
        } catch (Exception ex) {
            ex.printStackTrace();
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

}