package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class VimeoVideo extends AppCompatActivity {

    Activity activity;
    FirebaseCrashlytics mCrashlytics;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vimeo_video);
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        mCrashlytics = FirebaseCrashlytics.getInstance();
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Video Player");
            preventSizeChange(this, getSupportActionBar());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ColorDrawable colorDrawable
                    = new ColorDrawable(Color.parseColor("#C3BE49"));


            // Set BackgroundDrawable
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
            activity = this;
            progressDialog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
            progressDialog.setCancelable(false);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

        try {
            WebView webView = (WebView) findViewById(R.id.webview);

            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.clearCache(true);
            webView.clearHistory();
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(true);


            try {
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        progressDialog.show();
                        view.loadUrl(url);

                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, final String url) {
                        progressDialog.dismiss();
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
                        request.grant(request.getResources());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

            SharedPreferences uniquePref = getSharedPreferences("HelpViewPref", MODE_PRIVATE);
            final String uniqueidval = uniquePref.getString(MainActivity.HelpVideoName, null);
            switch (uniqueidval) {
                case "VehicleRegister":
                    webView.loadUrl("https://player.vimeo.com/video/545024134");
                    break;
                case "Grevience":
                    webView.loadUrl("https://player.vimeo.com/video/545316887");
                    break;
                case "Collision":
                    webView.loadUrl("https://player.vimeo.com/video/544994170");
                    break;
                case "SelfAccident":
                    webView.loadUrl("https://player.vimeo.com/video/545056742");
                    break;
                case "StolenAccident":
                    webView.loadUrl("https://player.vimeo.com/video/545090185");
                    break;
                case "UserRegister":
                    webView.loadUrl("https://player.vimeo.com/video/545311210");
                    break;
                case "VehicleClaimVideo":
                    webView.loadUrl("https://player.vimeo.com/video/544994170");
                    break;
                case "VehicleMotorClaimVideo":
                    webView.loadUrl("https://player.vimeo.com/video/535511168");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {

            finish();
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "onOptionItemSelected", e.getMessage(), e.toString());
            mCrashlytics.recordException(e);
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
//        Intent login = new Intent(VimeoVideo.this, HomePage.class);
//        startActivity(login);
    }

}