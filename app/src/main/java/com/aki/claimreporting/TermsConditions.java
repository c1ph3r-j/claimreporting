package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class TermsConditions extends AppCompatActivity implements View.OnTouchListener,
        ViewTreeObserver.OnScrollChangedListener {
    public static String reqidval;
    public static FirebaseCrashlytics mCrashlytics;
    public WebView webView;
    public Button agreebutton;
    public CheckBox checktermscondition;
    public SignaturePad mSignaturePad;
    public ProgressDialog progressdialog;
    public String termidvalunew;
    public String encryptedSHA, stokenval, simagebase64;
    Activity activity;
    ScrollView scrollView;
    boolean checkboxterms;
    DatabaseHelper mydb;
    private ProgressDialog progDailog;
    private boolean isSignatured = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Terms and Conditions");
        preventSizeChange(this, getSupportActionBar());
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        mCrashlytics = FirebaseCrashlytics.getInstance();
        activity = this;
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();

        try {
            progDailog = ProgressDialog.show(activity, getString(R.string.loading), getString(R.string.please_wait), true);
            progDailog.setCancelable(false);


            scrollView = findViewById(R.id.scrollView);
            scrollView.setOnTouchListener(this);
            scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
            webView = (WebView) findViewById(R.id.webview);

            webView.setInitialScale(180);

            webView.clearCache(true);
            webView.clearHistory();
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(true);

            try {
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        progDailog.show();
                        view.loadUrl(url);

                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, final String url) {
                        progDailog.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                webView.setWebChromeClient(new WebChromeClient() {

                    @Override
                    public void onPermissionRequest(final PermissionRequest request) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            request.grant(request.getResources());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            try {
                webView.loadUrl(getString(R.string.termsconditons));
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


   /* public void unauthorize() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(TermsConditions.this);
            dialog.setMessage(getString(R.string.session_expired));
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mydb = new DatabaseHelper(TermsConditions.this);
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
                    Intent login = new Intent(TermsConditions.this, Login.class);
                    startActivity(login);
                }
            });
            android.app.AlertDialog alert = dialog.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }
    }*/

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void onScrollChanged() {
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int topDetector = scrollView.getScrollY();
        int bottomDetector = view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
        if (bottomDetector == 0) {
            //   Toast.makeText(getBaseContext(),"Scroll View bottom reached",Toast.LENGTH_SHORT).show();
            //agreebutton.setVisibility(View.VISIBLE);
        }
        if (topDetector <= 0) {
            //agreebutton.setVisibility(View.GONE);
            //Toast.makeText(getBaseContext(),"Scroll View top reached",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();

    }

}