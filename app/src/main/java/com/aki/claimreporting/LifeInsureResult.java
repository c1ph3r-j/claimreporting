package com.aki.claimreporting;

import static com.aki.claimreporting.GetLifeInsureDetails.insuranceDetails;
import static com.aki.claimreporting.MotorInsureResult.checkNullOrEmpty;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class LifeInsureResult extends AppCompatActivity {

    FirebaseCrashlytics mCrashlytics;

    String certificateNo, kenyaIdProposer, kraPinProposer, kenyaIdAssured, statusCode,
            dateOfCommencement, dateOfMaturity, sumAssuredKsh, typeOfAssurance, policyNo, classificationName;

    TextView certificateNoField, kenyaIdProposerFiled, kraPinProposerField, kenyaIdAssuredField, certificateName,
            dateOfCommencementField, dateOfMaturityField, sumAssuredKshField, typeOfAssuranceField, policyNoField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_insure_result);

        mCrashlytics = FirebaseCrashlytics.getInstance();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Verification Result");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }



    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            certificateNoField = findViewById(R.id.certificateValueLI);
            policyNoField = findViewById(R.id.policyNoLi);
            certificateName = findViewById(R.id.certificateName);
            kenyaIdAssuredField = findViewById(R.id.KenyaIdAssuredValue);
            kraPinProposerField = findViewById(R.id.KRAPinValue);
            dateOfCommencementField = findViewById(R.id.DateOfCommencementValue);
            dateOfMaturityField = findViewById(R.id.DateOfMaturityValue);
            sumAssuredKshField = findViewById(R.id.SumAssuredValue);
            typeOfAssuranceField = findViewById(R.id.TypeOfAssuranceValue);
            kenyaIdProposerFiled = findViewById(R.id.KenyaIdValue);
            try {
                String classificationName = insuranceDetails.optString("classificationName", "---") + " " + certificateName.getText();
                String certificateNo = insuranceDetails.optString("certificateNo", "---");
                String kenyaIdProposer = insuranceDetails.optString("kenyaIDProposer", "---");
                String kraPinProposer = insuranceDetails.optString("krapinProposer", "---");
                String kenyaIdAssured = insuranceDetails.optString("kenyaIDAssured", "---");
                String dateOfCommencement = insuranceDetails.optString("dateOfCommencement", "---");
                String dateOfMaturity = insuranceDetails.optString("dateOfMaturity", "---");
                String sumAssuredKsh = insuranceDetails.optString("sumAssuredKSh", "---");
                String typeOfAssurance = insuranceDetails.optString("typeOfAssurance", "---");
                String policyNo = insuranceDetails.optString("policyNo", "---");
                String statusCode = insuranceDetails.optString("statusCode", "---");

                switch (statusCode) {
                    case "100":
                        certificateNo = certificateNo + ("<font color=\"#6B8E23\"> (Genuine)</font>");
                        break;
                    case "200":
                        certificateNo = certificateNo + ("<font color=\"##FFA500\"> (Expired)</font>");
                        break;
                    case "300":
                        certificateNo = certificateNo + ("<font color=\"#964B00\"> (Cancelled)</font>");
                        break;
                    case "500":
                        certificateNo = certificateNo + ("<font color=\"#6495ED\"> (Upcoming)</font>");
                        break;
                }

                // Use the method to handle each field
                certificateName.setText(checkNullOrEmpty(classificationName));
                certificateNoField.setText(Html.fromHtml(checkNullOrEmpty(certificateNo)));
                policyNoField.setText(checkNullOrEmpty(policyNo));
                kenyaIdProposerFiled.setText(checkNullOrEmpty(kenyaIdProposer));
                kraPinProposerField.setText(checkNullOrEmpty(kraPinProposer));
                dateOfCommencementField.setText(checkNullOrEmpty(dateOfCommencement));
                dateOfMaturityField.setText(checkNullOrEmpty(dateOfMaturity));
                typeOfAssuranceField.setText(checkNullOrEmpty(typeOfAssurance));
                kenyaIdAssuredField.setText(checkNullOrEmpty(kenyaIdAssured));
                sumAssuredKshField.setText(checkNullOrEmpty(sumAssuredKsh));


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
            Intent otpIntent = new Intent(this, Dashboard.class);
            startActivity(otpIntent);
            return true;
        } else {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}