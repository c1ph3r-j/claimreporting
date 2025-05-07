package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.mCrashlytics;
import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class CustomerVimeVideo extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    Activity activity;
    private ProgressDialog progDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_vime_video);
        preventSizeChange(this, getSupportActionBar());
        getSupportActionBar().setTitle("Video Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#C3BE49"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        activity = this;
        progDailog = ProgressDialog.show(activity, getString(R.string.loading), getString(R.string.please_wait), true);
        progDailog.setCancelable(false);

        init();

    }

    void init() {
        String methodName = Objects.requireNonNull(new Object() {
        }.getClass().getEnclosingMethod()).getName();
        try {
            WebView webView = (WebView) findViewById(R.id.webview);

            try {
                webView.getSettings().setAllowFileAccessFromFileURLs(true);
                webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.clearCache(true);
                webView.clearHistory();
                webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webView.getSettings().setMediaPlaybackRequiresUserGesture(true);
            } catch (Exception e) {
                e.printStackTrace();
                MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + methodName, e.getMessage(), e.toString());
                mCrashlytics.recordException(e);
            }

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

            SharedPreferences uniquePref = getSharedPreferences("ConsumerHelpViewPref", MODE_PRIVATE);
            final String uniqueidval = uniquePref.getString("ConsumerHelpVideoName", null);
            if (uniqueidval.equals("whatIsInsurance")) {
                webView.loadUrl("https://player.vimeo.com/video/847949575");
            } else if (uniqueidval.equals("UnderstandYourPolicy")) {
                webView.loadUrl("https://player.vimeo.com/video/847957087");
            } else if (uniqueidval.equals("InsuranceClaimExplained")) {
                webView.loadUrl("https://player.vimeo.com/video/847955064");
            } else if (uniqueidval.equals("UnderstandLifeInsurance")) {
                webView.loadUrl("https://player.vimeo.com/video/847957647");
            }
//            else if (uniqueidval.equals("VehicleValuation")) {
//                webView.loadUrl("https://player.vimeo.com/video/535549471");
//            } else if (uniqueidval.equals("VehicleWriteOff")) {
//                webView.loadUrl("https://player.vimeo.com/video/535566968");
//            }
            else if (uniqueidval.equals("UnderstandingMotorInsurance")) {
                webView.loadUrl("https://player.vimeo.com/video/847955905");
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
            MainActivity.MobileErrorLog(e.getStackTrace()[0].getFileName() + " - " + "OnOptionItemSelected", e.getMessage(), e.toString());
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