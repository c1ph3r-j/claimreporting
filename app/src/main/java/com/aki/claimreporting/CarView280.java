package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import okhttp3.MediaType;

public class CarView280 extends AppCompatActivity {

    public static DatabaseHelper mydb;
    public static byte[] regByte = null;
    public static String reqidval;
    public static String stokenval;
    public static String doctypeid;
    public static String ThirdPartyID;
    public static String carviewcircle1, carviewcircle2, carviewcircle3, carviewcircle4, carviewcircle5, carviewcircle6, carviewcircle7, carviewcircle8, carviewcircle9, carviewcircle10, carviewcircle11, carviewcircle12, carviewcircle13, carviewcircle14, carviewcircle15;
    //MainActivity tokenActivity = new MainActivity();
    //String token = MainActivity.Global.loginAPIToken;
    public String token;
    public String[] arrData;
    public int caridval;
    public String carnameval;
    public String cardescription;
    public String mergenamedescr;
    public SharedPreferences sharedpreferences, sharedpreferencesimg;
    public String uniqueID;
    public int attachmentid;
    public int primaryid;
    public int attachid;
    public String regnum;
    public String imagenew;
    public String imagename;
    public String imgData;
    public TextView proceedid;
    public TextView backdid;
    public ImageView circle1, circle2, circle3, circle4, circle5, circle6, circle7, circle8, circle9, circle10, circle11, circle12, circle13, circle14, circle15;
    public int cirlcestate1, cirlcestate2, cirlcestate3, cirlcestate4, cirlcestate5, cirlcestate6, cirlcestate7, cirlcestate8, cirlcestate9, cirlcestate10, cirlcestate11, cirlcestate12, cirlcestate13, cirlcestate14, cirlcestate15;
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private FirebaseCrashlytics mCrashlytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_view280);
        preventSizeChange(this, getSupportActionBar());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Take Photo Of Damaged Parts");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        init();
    }

    public void init() {
        try {
            backdid = (TextView) findViewById(R.id.carbackid);
            backdid.setText("<< BACK");
            backdid.setOnClickListener(onBackClicked -> onBackPressed());
            circle1 = (ImageView) findViewById(R.id.imgcircle1);
            circle2 = (ImageView) findViewById(R.id.imgcircle2);
            proceedid = (TextView) findViewById(R.id.carproceedid);
            circle3 = (ImageView) findViewById(R.id.imgcircle3);
            circle4 = (ImageView) findViewById(R.id.imgcircle4);
            circle5 = (ImageView) findViewById(R.id.imgcircle5);
            circle6 = (ImageView) findViewById(R.id.imgcircle6);
            circle7 = (ImageView) findViewById(R.id.imgcircle7);
            circle8 = (ImageView) findViewById(R.id.imgcircle8);
            circle9 = (ImageView) findViewById(R.id.imgcircle9);
            circle10 = (ImageView) findViewById(R.id.imgcircle10);
            circle11 = (ImageView) findViewById(R.id.imgcircle11);
            circle12 = (ImageView) findViewById(R.id.imgcircle12);
            circle13 = (ImageView) findViewById(R.id.imgcircle13);
            circle14 = (ImageView) findViewById(R.id.imgcircle14);
            circle15 = (ImageView) findViewById(R.id.imgcircle15);
            circle1.setImageResource(R.drawable.carno_1);
            circle2.setImageResource(R.drawable.carno_2);
            circle3.setImageResource(R.drawable.carno_3);
            circle4.setImageResource(R.drawable.carno_4);
            circle5.setImageResource(R.drawable.carno_5);
            circle6.setImageResource(R.drawable.carno_6);
            circle7.setImageResource(R.drawable.carno_7);
            circle8.setImageResource(R.drawable.carno_8);
            circle9.setImageResource(R.drawable.carno_9);
            circle10.setImageResource(R.drawable.carno_10);
            circle11.setImageResource(R.drawable.carno_11);
            circle12.setImageResource(R.drawable.carno_12);
            circle13.setImageResource(R.drawable.carno_13);
            circle14.setImageResource(R.drawable.carno_14);
            circle15.setImageResource(R.drawable.carno_15);

            cirlcestate1 = R.drawable.carno_1;
            cirlcestate2 = R.drawable.carno_2;
            cirlcestate3 = R.drawable.carno_3;
            cirlcestate4 = R.drawable.carno_4;
            cirlcestate5 = R.drawable.carno_5;
            cirlcestate6 = R.drawable.carno_6;
            cirlcestate7 = R.drawable.carno_7;
            cirlcestate8 = R.drawable.carno_8;
            cirlcestate9 = R.drawable.carno_9;
            cirlcestate10 = R.drawable.carno_10;
            cirlcestate11 = R.drawable.carno_11;
            cirlcestate12 = R.drawable.carno_12;
            cirlcestate13 = R.drawable.carno_13;
            cirlcestate14 = R.drawable.carno_14;
            cirlcestate15 = R.drawable.carno_15;

            if (carviewcircle1 == "1") {
                circle1.setImageResource(R.drawable.carnoselected_1);
                cirlcestate1 = R.drawable.carnoselected_1;
            }
            if (carviewcircle2 == "1") {
                circle2.setImageResource(R.drawable.carnoselected_2);
                cirlcestate2 = R.drawable.carnoselected_2;
            }
            if (carviewcircle3 == "1") {
                circle3.setImageResource(R.drawable.carnoselected_3);
                cirlcestate3 = R.drawable.carnoselected_3;
            }
            if (carviewcircle4 == "1") {
                circle4.setImageResource(R.drawable.carnoselected_4);
                cirlcestate4 = R.drawable.carnoselected_4;
            }
            if (carviewcircle5 == "1") {
                circle5.setImageResource(R.drawable.carnoselected_5);
                cirlcestate5 = R.drawable.carnoselected_5;
            }
            if (carviewcircle6 == "1") {
                circle6.setImageResource(R.drawable.carnoselected_6);
                cirlcestate6 = R.drawable.carnoselected_6;
            }
            if (carviewcircle7 == "1") {
                circle7.setImageResource(R.drawable.carnoselected_7);
                cirlcestate7 = R.drawable.carnoselected_7;
            }
            if (carviewcircle8 == "1") {
                circle8.setImageResource(R.drawable.carnoselected_8);
                cirlcestate8 = R.drawable.carnoselected_8;
            }
            if (carviewcircle9 == "1") {
                circle9.setImageResource(R.drawable.carnoselected_9);
                cirlcestate9 = R.drawable.carnoselected_9;
            }
            if (carviewcircle10 == "1") {
                circle10.setImageResource(R.drawable.carnoselected_10);
                cirlcestate10 = R.drawable.carnoselected_10;
            }
            if (carviewcircle11 == "1") {
                circle11.setImageResource(R.drawable.carnoselected_11);
                cirlcestate11 = R.drawable.carnoselected_11;
            }
            if (carviewcircle12 == "1") {
                circle12.setImageResource(R.drawable.carnoselected_12);
                cirlcestate12 = R.drawable.carnoselected_12;
            }
            if (carviewcircle13 == "1") {
                circle13.setImageResource(R.drawable.carnoselected_13);
                cirlcestate13 = R.drawable.carnoselected_13;
            }
            if (carviewcircle14 == "1") {
                circle14.setImageResource(R.drawable.carnoselected_14);
                cirlcestate14 = R.drawable.carnoselected_14;
            }
            if (carviewcircle15 == "1") {
                circle15.setImageResource(R.drawable.carnoselected_15);
                cirlcestate15 = R.drawable.carnoselected_15;
            }


            proceedid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNetworkConnected() == true) {
                        //                    mydb = new DatabaseHelper(CarView.this);
//                    if(mydb.getclaimstep().getCount() != 0)
//                    {
//                        mydb.deleteclaimstep();
//                    }
//                    boolean claimstepinserted = mydb.insertclaimstep("ClaimStep4");
//                    if(claimstepinserted == true)
//                    {
//                        boolean test = claimstepinserted;
//                        Log.i(null,"Insertion Done");
//                    }
//                    else
//                    {
//                        boolean test = claimstepinserted;
//                        Log.i(null,"Not Insertion Done");
//                    }
                        mydb = new DatabaseHelper(CarView280.this);
                        int countcheck = mydb.getlocalimageattachment().getCount();
                        int countcheck1 = countcheck;
//                    if( MainActivity.damagecountnew == 0)
//                    {
//                        Toast.makeText(CarView280.this, getString(R.string.carproceed), Toast.LENGTH_SHORT).show();
//                        return;
//
//                    }
//                    else if(mydb.getlocalimageattachment().getCount() == MainActivity.damagecountnew)
//                    {
//                        Intent car = new Intent(CarView280.this, ReviewOwnImages.class);
//                        startActivity(car);
//                    }
//                    else
//                    {
//                        Intent car = new Intent(CarView280.this, ProgressDialogUpload.class);
//                        startActivity(car);
//                    }
                        if (MainActivity.damagecountnew == 0) {
                            Toast.makeText(CarView280.this, getString(R.string.carproceed), Toast.LENGTH_SHORT).show();
                            return;

                        } else {
                            if (mydb.getlocalimageattachment().getCount() == MainActivity.damagecountnew) {
                                SharedPreferences incitype = getSharedPreferences("IncidentType", MODE_PRIVATE);
                                String incidenttypeval = incitype.getString(ClaimType.typeidincident, "");
                                if (incidenttypeval == "F82589E6-7344-47B2-A672-11013F538551") {
                                    startActivity(new Intent(CarView280.this, ClaimVisualArtifacts.class));
                                    finish();
                                } else {
                                    Intent car = new Intent(CarView280.this, ClaimVisualArtifacts.class);
                                    startActivity(car);
                                    finish();
                                }
                            } else {
                                Intent car = new Intent(CarView280.this, ProgressDialogUpload.class);
                                startActivity(car);
                            }
                        }
                    } else {
                        startActivity(new Intent(CarView280.this, ClaimVisualArtifacts.class));
                        finish();
                    }

                }
            });
            circle1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if( cirlcestate1 == R.drawable.carno_1){
//                    circle1.setImageResource(R.drawable.carnoselected_1);
//                    cirlcestate1=R.drawable.carnoselected_1;
                    carviewcircle1 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 1);
                    editor.putString("selectcartitle", "Front bumper");
                    editor.putString("selectcardescription", "I’ll need to check the front bumber");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "924409f8-45ee-4190-b528-45c423ab9b33");
                    modeldoceditor.commit();

                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle2 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 2);
                    editor.putString("selectcartitle", "Hood / Bonnet");
                    editor.putString("selectcardescription", "I’ll need to check the bonnet.");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "32be5921-65ab-4e53-9c0b-5d2a29e14d44");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle3 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 3);
                    editor.putString("selectcartitle", "Windscreen / Windshield");
                    editor.putString("selectcardescription", "I’ll need to check the Windscreen & Windshield");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "f3c4b1b6-c503-440a-b31b-f4aa487f5212");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle4 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 4);
                    editor.putString("selectcartitle", "Roof top");
                    editor.putString("selectcardescription", "I’ll need to check the Roof top");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "a6d93aa2-fed8-43f6-af59-128211a31722");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle5 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 5);
                    editor.putString("selectcartitle", "Rear window");
                    editor.putString("selectcardescription", "I’ll need to check the Rear window");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "57fc4782-16a9-4941-b3b4-1812582b191d");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle6 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 6);
                    editor.putString("selectcartitle", "Boot/Trunk");
                    editor.putString("selectcardescription", "I’ll need to check the Boot/Trunk");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "1485c11f-6c62-4ceb-9e6e-8405df8ea431");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle7 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 7);
                    editor.putString("selectcartitle", "Rear Bumper");
                    editor.putString("selectcardescription", "I’ll need to check the Rear Bumper");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "fb386688-c943-4217-8144-90512d5e0529");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle8 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 8);
                    editor.putString("selectcartitle", "Left front door");
                    editor.putString("selectcardescription", "I’ll need to check the Left front handle/door");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "f960785b-2f51-45cf-863b-e8206e395417");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle9 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 9);
                    editor.putString("selectcartitle", "Left rear door");
                    editor.putString("selectcardescription", "I’ll need to check the Left rear handle/door");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "57b4d294-fba3-46d0-b979-e9a4b59aa450");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle10.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle10 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 10);
                    editor.putString("selectcartitle", "Rear Left Fender");
                    editor.putString("selectcardescription", "I’ll need to check the Rear Left Fender");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();
                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "16bb1d4e-e989-49db-9684-7b1d8dd66211");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();

                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle11.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle11 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 11);
                    editor.putString("selectcartitle", "Right Front door");
                    editor.putString("selectcardescription", "I’ll need to check the Right Front handle/door");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "a2927342-d89d-4cf0-80c0-43c7dc7a87b6");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle12.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle12 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 12);
                    editor.putString("selectcartitle", "Right rear door");
                    editor.putString("selectcardescription", "I’ll need to check the Right rear handle/door");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "b741ad34-e06b-47a3-b766-9db57e4d1e8e");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle13.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle13 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 13);
                    editor.putString("selectcartitle", "Rear Right Fender");
                    editor.putString("selectcardescription", "I’ll need to check the Rear Right Fender");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "a9bdd83a-38d5-454a-bd77-1e4008f22ac2");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle14.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle14 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 14);
                    editor.putString("selectcartitle", "Front Left Fender");
                    editor.putString("selectcardescription", "I’ll need to check the Front Left Fender");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "547af566-11b9-4272-9f44-58797adfd8c8");
                    modeldoceditor.commit();

                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
            circle15.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    carviewcircle15 = "1";
                    SharedPreferences sharedPreference = getSharedPreferences("MyCarview", Context.MODE_PRIVATE);

                    //SharedPreferences sharedPref = getSh(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreference.edit();
                    editor.putInt("selectcarid", 15);
                    editor.putString("selectcartitle", "Front Right Fender");
                    editor.putString("selectcardescription", "I’ll need to check the Front Right Fender");
                    editor.commit();

                    SharedPreferences loadmodelPref = getSharedPreferences("LoadModelView", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = loadmodelPref.edit();
                    editor1.putString(MainActivity.ModelID, "2");
                    editor1.commit();

                    SharedPreferences modeldoctypePref = getSharedPreferences("DocumentType", Context.MODE_PRIVATE);
                    SharedPreferences.Editor modeldoceditor = modeldoctypePref.edit();
                    modeldoceditor.putString(CarView.doctypeid, "98f525ce-5e6f-4ad6-a339-35d1a49578fd");
                    modeldoceditor.commit();
                    SharedPreferences thirdpartytypePref = getSharedPreferences("ThirdParty", Context.MODE_PRIVATE);
                    SharedPreferences.Editor thirdpartyoceditor = thirdpartytypePref.edit();
                    thirdpartyoceditor.putString(CarView.ThirdPartyID, "1");
                    thirdpartyoceditor.commit();
                    Intent intent = new Intent(CarView280.this, CameraDamage.class);
                    startActivity(intent);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String test = "vale1";
        String test1 = test;
        // insurer = null;
        // insurer  = getBitmap(currentPhotoPath);
        //ObjectDetectorActivity.Global.img = insurer;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
            supporteditor.putString(MainActivity.ReferrenceURL, "Car View 2 Image Capture");
            supporteditor.commit();
            sharedpreferencesimg = getSharedPreferences("SupportPrefImg", Context.MODE_PRIVATE);
            SharedPreferences.Editor supporteditorimg = sharedpreferencesimg.edit();
            supporteditorimg.putString(MainActivity.SupportImg, encodedimg);
            supporteditorimg.commit();
            Intent login = new Intent(CarView280.this, SupportTicket.class);
            startActivity(login);
            return true;

            // Do something

        } else {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isNetworkConnected() == true) {
            if (MainActivity.claiminmiddleflow == "1") {
                finish();
            } else {
                if (MainActivity.ambulanceenabled.equals("No") && MainActivity.towingagencyenabled.equals("No") && MainActivity.policeinfoenabled.equals("No")) {
//                    Intent login = new Intent(CarView280.this, EmergencyInformation.class);
//                    startActivity(login);
                } else {
                    // Intent login = new Intent(CarView.this, ServiceProvider.class);
                    //startActivity(login);
                    finish();
                }
            }

        } else {
            finish();
        }

    }

}