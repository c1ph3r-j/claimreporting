package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;
import static com.aki.claimreporting.MainActivity.stokenval;
import static com.aki.claimreporting.MainActivity.unauthorize;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

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

public class ThirdParty extends AppCompatActivity {
    FirebaseCrashlytics mCrashlytics;
    Button addThirdPartyBtnTwo;
    ListView listOfThirdPartiesView;
    ProgressDialog progressDialog;
    ArrayList<ThirdPartyModel> listOfThirdParties;
    ListOfThirdPartiesAdapter listOfThirdPartiesAdapter;
    LinearLayout proceedBtn, SkipButton;
    LinearLayout addThirdPartyLayout;
    LinearLayout noThirdPartyLayout;
    Button addThirdPartyBtnOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_party);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Third Party (Optional)");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
            preventSizeChange(this, getSupportActionBar());
            init();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            addThirdPartyBtnTwo = findViewById(R.id.addThirdPartyBtn2);
            listOfThirdPartiesView = findViewById(R.id.listOfThirdParties);
            addThirdPartyBtnOne = findViewById(R.id.addThirdPartyBtn);

            noThirdPartyLayout = findViewById(R.id.noThirdPartyLayout);
            addThirdPartyLayout = findViewById(R.id.addThirdPartyBtnView);
            addThirdPartyLayout.setVisibility(View.GONE);

            SkipButton = findViewById(R.id.SkipButton);
            proceedBtn = findViewById(R.id.nextid);

            listOfThirdParties = new ArrayList<>();
            listOfThirdPartiesAdapter = new ListOfThirdPartiesAdapter(this, listOfThirdParties);
            listOfThirdPartiesView.setAdapter(listOfThirdPartiesAdapter);

            CheckVisibility();

            addThirdPartyBtnTwo.setOnClickListener(onClickAddThirdParty -> {
                try {
                    createThirdPartyPopUpView();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            addThirdPartyBtnOne.setOnClickListener(onClickAddThirdParty -> {
                try {
                    createThirdPartyPopUpView();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            proceedBtn.setOnClickListener(onClickProceed -> {
                startActivity(new Intent(ThirdParty.this, AccidentDescription.class));
            });

            SkipButton.setOnClickListener(onClickProceed -> {
                startActivity(new Intent(ThirdParty.this, AccidentDescription.class));
            });
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    private void createThirdPartyPopUpView() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            Dialog dialog = new Dialog(this);
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.addd_third_party_pop_up, null);
            dialog.setContentView(popupView);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(ThirdParty.this, R.drawable.add_thridparty_bg));

            Button addThirdParty = dialog.findViewById(R.id.addThirdPartyToTheListBtn);
            EditText regNoField = dialog.findViewById(R.id.registrationNoField);
            EditText makeField = dialog.findViewById(R.id.makeField);
            EditText modelField = dialog.findViewById(R.id.modelField);
            EditText colorField = dialog.findViewById(R.id.colorField);

            addThirdParty.setOnClickListener(onClickAddThirdParty -> {
                try {
                    String regNo = regNoField.getText().toString().trim();
                    String make = makeField.getText().toString().trim();
                    String model = modelField.getText().toString().trim();
                    String color = colorField.getText().toString().trim();

                    if (regNo.isEmpty() && make.isEmpty() && model.isEmpty() && color.isEmpty()) {
                        Toast.makeText(this, "Please Enter Any One Of the Value Above!", Toast.LENGTH_SHORT).show();
                    } else {
                        ThirdPartyModel thirdPartyDetails = new ThirdPartyModel(regNo, model, make, color);
                        listOfThirdPartiesAdapter.addItemToTheList(thirdPartyDetails);
                        listOfThirdParties = listOfThirdPartiesAdapter.getNewList();
                        storeThirdPartyApi(regNo, make, model, color, dialog);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        locationManager = (LocationManager) ThirdParty.this.getSystemService(Context.LOCATION_SERVICE);
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

    private void storeThirdPartyApi(String regNo, String make, String model, String color, Dialog addThirdPartyView) {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {

            if (isNetworkConnected()) {

                if (checkGPSStatus()) {
                    DatabaseHelper mydb = new DatabaseHelper(ThirdParty.this);
                    if (mydb.getTokendetails().getCount() != 0) {
                        Cursor curseattachtoken = mydb.getTokendetails();
                        int counttoken = curseattachtoken.getCount();
                        if (counttoken >= 1) {
                            while (curseattachtoken.moveToNext()) {
                                stokenval = curseattachtoken.getString(1);
                            }
                        }
                    }

                    Thread thread = new Thread(() -> {
                        String appUrl = getString(R.string.uaturl) + "/app/Claim/AddIncidentThirdParty";
                        final MediaType JSON
                                = MediaType.parse("application/json; charset=utf-8");

                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(120, TimeUnit.SECONDS)
                                .writeTimeout(120, TimeUnit.SECONDS)
                                .readTimeout(120, TimeUnit.SECONDS)
                                .build();

                        JsonObject Details = new JsonObject();
                        SharedPreferences sharedpreferences = getSharedPreferences("CRAID", Context.MODE_PRIVATE);
                        String craid = sharedpreferences.getString("CraIdval", "0");
                        Details.addProperty("incidentUniqueCode", craid);
                        Details.addProperty("registrationNo", regNo);
                        Details.addProperty("make", make);
                        Details.addProperty("model", model);
                        Details.addProperty("color", color);
                        String insertString = Details.toString();
                        RequestBody body = RequestBody.create(JSON, insertString);
                        Request request = new Request.Builder()
                                .url(appUrl)
                                .header("Authorization", "Bearer " + MainActivity.stokenval)
                                .header("MobileParameter", MainActivity.InsertMobileParameters())
                                .post(body)
                                .build();
                        Response staticResponse;

                        try {
                            runOnUiThread(() -> progressDialog = ProgressDialog.show(ThirdParty.this, "Loading", "Please wait...", true));
                            staticResponse = client.newCall(request).execute();
                            assert staticResponse.body() != null;
                            String staticRes = Objects.requireNonNull(staticResponse.body()).string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);


                            if (staticResponse.code() == 401) {
                                runOnUiThread(() -> {
                                    try {
                                        runOnUiThread(() -> {
                                            progressDialog.dismiss();
                                            CheckVisibility();
                                            addThirdPartyView.dismiss();
                                            unauthorize(ThirdParty.this);
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                });
                            } else {
                                if (staticJsonObj.getInt("rcode") == 401) {
                                    try {
                                        runOnUiThread(() -> {
                                            progressDialog.dismiss();
                                            CheckVisibility();
                                            addThirdPartyView.dismiss();
                                            unauthorize(ThirdParty.this);
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                        mCrashlytics.recordException(e);
                                    }
                                } else if (staticJsonObj.getInt("rcode") == 200) {

                                    runOnUiThread(() -> {
                                        if (addThirdPartyLayout.getVisibility() == View.GONE) {
                                            addThirdPartyLayout.setVisibility(View.VISIBLE);
                                        }
                                        progressDialog.dismiss();
                                        CheckVisibility();
                                        addThirdPartyView.dismiss();
                                        try {
                                            JSONObject response = staticJsonObj.getJSONObject("rObj");
                                            System.out.println(response.get("rObj"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                                            mCrashlytics.recordException(e);
                                        }

                                    });
                                } else {
                                    try {
                                        runOnUiThread(() -> {
                                            progressDialog.dismiss();
                                            CheckVisibility();
                                            addThirdPartyView.dismiss();
                                        });
                                        JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                        JSONObject index = rmsg.getJSONObject(0);
                                        runOnUiThread(() -> {
                                            String errorText;
                                            try {
                                                errorText = index.getString("errorText");
                                                AlertDialog.Builder alert = new AlertDialog.Builder(ThirdParty.this);
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
                            }
                        } catch (final Exception ex) {
                            runOnUiThread(() -> {
                                progressDialog.dismiss();
                                CheckVisibility();
                                addThirdPartyView.dismiss();
                                ex.printStackTrace();
                                MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
                                mCrashlytics.recordException(ex);
                                Toast.makeText(ThirdParty.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                    thread.start();


                } else {
                    try {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ThirdParty.this);
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

            } else {
                try {
                    Toast.makeText(this, getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                    mCrashlytics.recordException(e);
                }
            }
        } catch (Exception ex) {
            //progressdialog.dismiss();
            ex.printStackTrace();
            MainActivity.MobileErrorLog(ex.getStackTrace()[0].getFileName() + " - " + methodName, ex.getMessage(), ex.toString());
            mCrashlytics.recordException(ex);
            Toast.makeText(ThirdParty.this, getString(R.string.admin), Toast.LENGTH_SHORT).show();
        }
    }

   /* public void unauthorize() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ThirdParty.this);
        dialog.setMessage("Your session have been expired. Please login again to continue");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper mydb = new DatabaseHelper(ThirdParty.this);
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
                Intent login = new Intent(ThirdParty.this, Login.class);
                startActivity(login);
            }
        });
        dialog.setCancelable(false);
        android.app.AlertDialog alert = dialog.create();
        alert.show();
    }*/

    void CheckVisibility() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            if (listOfThirdParties.size() > 0) {
                addThirdPartyLayout.setVisibility(View.VISIBLE);
                noThirdPartyLayout.setVisibility(View.GONE);
                listOfThirdPartiesView.setVisibility(View.VISIBLE);
//                proceedBtn.setVisibility(View.VISIBLE);
                SkipButton.setVisibility(View.GONE);
            } else {
                addThirdPartyLayout.setVisibility(View.GONE);
                noThirdPartyLayout.setVisibility(View.VISIBLE);
                listOfThirdPartiesView.setVisibility(View.GONE);
//                proceedBtn.setVisibility(View.GONE);
                SkipButton.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}