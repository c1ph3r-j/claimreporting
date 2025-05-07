package com.aki.claimreporting;

import static com.aki.claimreporting.GetMotorInsureDetails.motorInsuranceDetails;
import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class MotorInsureResult extends AppCompatActivity {

    FirebaseCrashlytics mCrashlytics;

    ScrollView verificationResultView;
    TextView certificateField, typeOfCertificateField, vehicleField,
            chassisField, engineField, modelField, makeField, yearOfManField, licensedToCarryField,
            tonnageField, insurerField, policyField, policyBeginDateField, policyEndDateField;

    LinearLayout canceledLayout;
    TextView canceledDate, canceledReason;
    String certificate, typeOfCertificate, vehicle,
            chassis, engine, make, yearOfMan, licensedToCarry,
            tonnage, insurer, policy, policyBeginDate, policyEndDate, classificationId, typeOfCover, statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_result);
        preventSizeChange(this, getSupportActionBar());

        mCrashlytics = FirebaseCrashlytics.getInstance();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Verification Result");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            certificateField = findViewById(R.id.certificateValue);
            typeOfCertificateField = findViewById(R.id.typeOfCertificateValue);
            vehicleField = findViewById(R.id.vehicleValue);
            chassisField = findViewById(R.id.ChassisValue);
            engineField = findViewById(R.id.EngineValue);
            makeField = findViewById(R.id.MakeValue);
            modelField = findViewById(R.id.ModelValue);
            canceledLayout = findViewById(R.id.cencelledCertificate);
            canceledDate = findViewById(R.id.resultCancelledDate);
            canceledReason = findViewById(R.id.resultCancelledreason);
            yearOfManField = findViewById(R.id.YearOfManufacturerValue);
            licensedToCarryField = findViewById(R.id.licensedToCarryValue);
            tonnageField = findViewById(R.id.TonnageValue);
            insurerField = findViewById(R.id.insurerValue);
            policyField = findViewById(R.id.policyValue);
            policyBeginDateField = findViewById(R.id.policyBeginDateValue);
            policyEndDateField = findViewById(R.id.PolicyEndDateValue);
            verificationResultView = findViewById(R.id.verificationResultView);

            try {
                // Assuming motorInsuranceDetails is a JSONObject representing the motor insurance details
                String certificate = motorInsuranceDetails.optString("certificateNumber", " - ");
                String registrationNo = motorInsuranceDetails.optString("registrationnumber", " - ");
                String typeOfCertificate = motorInsuranceDetails.optString("certificateClassificationID", " - ");
                String vehicle = motorInsuranceDetails.optString("vehicleModel", " - ");
                String chassis = motorInsuranceDetails.optString("chassisnumber", " - ");
                String engine = motorInsuranceDetails.optString("engineNumber", " - ");
                String classificationId = motorInsuranceDetails.optString("certificateClassificationID", " - ");
                String typeOfCover = motorInsuranceDetails.optString("typeOfcover", " - ");
                String statusCode = motorInsuranceDetails.optString("statusCode", " - ");
                String make = motorInsuranceDetails.optString("vehicleMake", " - ");
                String yearOfMan = motorInsuranceDetails.optString("vehicleRegistrationYear", " - ");
                String licensedToCarry = motorInsuranceDetails.optString("passengersCount", " - ");
                String tonnage = motorInsuranceDetails.optString("tonnage", " - ");
                String insurer = motorInsuranceDetails.optString("insuredBy", " - ");
                String policy = motorInsuranceDetails.optString("insurancePolicyNumber", " - ");
                String policyBeginDate = motorInsuranceDetails.optString("validFrom", " - ");
                String policyEndDate = motorInsuranceDetails.optString("validTill", " - ");
                String cancelledDateTimeString = motorInsuranceDetails.optString("cancelledDateTimeString", " - ");
                String cancellationReason = motorInsuranceDetails.optString("cancellationReason", " - ");
                try {
                    switch (classificationId) {
                        case "1":
                            switch (typeOfCover) {
                                case "100":
                                    typeOfCertificate = "Class A - PSV Unmarked (Comprehensive)";
                                    break;
                                case "200":
                                    typeOfCertificate = "Class A - PSV Unmarked (TPO)";
                                    break;
                                case "300":
                                    typeOfCertificate = "Class A - PSV Unmarked (TPTF)";
                                    break;
                            }

                            break;
                        case "2":
                            switch (typeOfCover) {
                                case "100":
                                    typeOfCertificate = "Type B - Commercial Vehicle (Comprehensive)";
                                    break;
                                case "200":
                                    typeOfCertificate = "Type B - Commercial Vehicle (TPO)";
                                    break;
                                case "300":
                                    typeOfCertificate = "Type B - Commercial Vehicle (TPTF)";
                                    break;
                            }
                            break;
                        case "3":
                            switch (typeOfCover) {
                                case "100":
                                    typeOfCertificate = "Type C - Private Car (Comprehensive)";
                                    break;
                                case "200":
                                    typeOfCertificate = "Type C - Private Car (TPO)";
                                    break;
                                case "300":
                                    typeOfCertificate = "Type C - Private Car (TPTF)";
                                    break;
                            }
                            break;
                        case "4":
                            switch (typeOfCover) {
                                case "100":
                                    typeOfCertificate = "Type D - Motor Cycle (Comprehensive)";
                                    break;
                                case "200":
                                    typeOfCertificate = "Type D - Motor Cycle (TPO)";
                                    break;
                                case "300":
                                    typeOfCertificate = "Type D - Motor Cycle (TPTF)";
                                    break;
                            }
                            break;
                        case "5":
                            typeOfCertificate = "Aviation";
                            break;
                        case "6":
                            switch (typeOfCover) {
                                case "100":
                                    typeOfCertificate = "Type A - Bus (Comprehensive)";
                                    break;
                                case "200":
                                    typeOfCertificate = "Type A - Bus (TPO)";
                                    break;
                                case "300":
                                    typeOfCertificate = "Type A - Bus (TPTF)";
                                    break;
                            }
                            break;
                        case "7":
                            switch (typeOfCover) {
                                case "100":
                                    typeOfCertificate = "Type A - Matatu (Comprehensive)";
                                    break;
                                case "200":
                                    typeOfCertificate = "Type A - Matatu (TPO)";
                                    break;
                                case "300":
                                    typeOfCertificate = "Type A - Matatu (TPTF)";
                                    break;
                            }
                            break;
                        case "8":
                            switch (typeOfCover) {
                                case "100":
                                    typeOfCertificate = "Type A - Taxi (Comprehensive)";
                                    break;
                                case "200":
                                    typeOfCertificate = "Type A - Taxi (TPO)";
                                    break;
                                case "300":
                                    typeOfCertificate = "Type A - Taxi (TPTF)";
                                    break;
                            }
                            break;
                        case "9":
                            switch (typeOfCover) {
                                case "100":
                                    typeOfCertificate = "Type D - PSV (Comprehensive)";
                                    break;
                                case "200":
                                    typeOfCertificate = "Type D - PSV (TPO)";
                                    break;
                                case "300":
                                    typeOfCertificate = "Type D - PSV (TPTF)";
                                    break;
                            }
                            break;
                        case "10":
                            switch (typeOfCover) {
                                case "100":
                                    typeOfCertificate = "Type D – Motor Cycle Commercial (Comprehensive)";
                                    break;
                                case "200":
                                    typeOfCertificate = "Type D – Motor Cycle Commercial (TPO)";
                                    break;
                                case "300":
                                    typeOfCertificate = "Type D – Motor Cycle Commercial (TPTF)";
                                    break;
                            }
                            break;
                    }


                    typeOfCertificateField.setText(checkNullOrEmpty(typeOfCertificate));

                    canceledLayout.setVisibility(View.GONE);
                    switch (statusCode) {
                        case "100":
                            certificate = certificate + ("<font color=\"#6B8E23\"> (Genuine)</font>");
                            break;
                        case "200":
                            certificate = certificate + ("<font color=\"##FFA500\"> (Expired)</font>");
                            break;
                        case "300":
                            certificate = certificate + ("<font color=\"#964B00\"> (Cancelled)</font>");
                            canceledLayout.setVisibility(View.VISIBLE);
                            canceledDate.setText(checkNullOrEmpty(cancelledDateTimeString));
                            canceledReason.setText(checkNullOrEmpty(cancellationReason));
                            break;
                        case "500":
                            certificate = certificate + ("<font color=\"#6495ED\"> (Upcoming)</font>");
                            break;
                    }

                    certificateField.setText(Html.fromHtml(checkNullOrEmpty(certificate), Html.FROM_HTML_MODE_COMPACT));
                    vehicleField.setText(checkNullOrEmpty(registrationNo));
                    chassisField.setText(checkNullOrEmpty(chassis));
                    engineField.setText(checkNullOrEmpty(engine));
                    String model = vehicle;
                    vehicle = "";
                    if(!(make == null || make.trim().isEmpty() || make.equalsIgnoreCase("null") || make.equalsIgnoreCase(" - "))) {
                        vehicle = make;
                    }



                    modelField.setText(checkNullOrEmpty(model));
                    makeField.setText(checkNullOrEmpty(vehicle));
                    yearOfManField.setText(checkNullOrEmpty(yearOfMan));
                    licensedToCarryField.setText(checkNullOrEmpty(licensedToCarry));
                    tonnageField.setText(checkNullOrEmpty(tonnage));
                    insurerField.setText(checkNullOrEmpty(insurer));
                    policyField.setText(checkNullOrEmpty(policy));
                    policyBeginDateField.setText(checkNullOrEmpty(policyBeginDate));
                    policyEndDateField.setText(checkNullOrEmpty(policyEndDate));

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

        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public static String checkNullOrEmpty(String input) {
        // Check if the input is null, empty, or equals "null"
        if (input == null || input.trim().isEmpty() || input.equalsIgnoreCase("null") || input.equals("0")) {
            // Return fallback value if any of the above conditions are met
            return " - ";
        }
        // Return the original input if it is valid
        return input;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_to_home) {
            Intent otpIntent = new Intent(MotorInsureResult.this, Dashboard.class);
            startActivity(otpIntent);
            return true;
        } else {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}