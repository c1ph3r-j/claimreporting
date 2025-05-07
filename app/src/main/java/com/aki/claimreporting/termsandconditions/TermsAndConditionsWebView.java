package com.aki.claimreporting.termsandconditions;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aki.claimreporting.R;


public class TermsAndConditionsWebView extends AppCompatActivity {


    //web view
    WebView webView;
    //tool bar
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions_web_view);
        getWindow().setStatusBarColor(getColor(R.color.appColor));
        try {

            //initialize variables
            initializeVariables();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    //initialize variables
    private void initializeVariables() {
        try {
            webView = findViewById(R.id.termsAndConditionsWebView);
            toolbar = findViewById(R.id.toolBar);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> {
                // Handle the navigation icon click event here
                getOnBackPressedDispatcher().onBackPressed(); // This can be used to mimic the back button's behavior
            });
            getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    startActivity(new Intent(TermsAndConditionsWebView.this, TermsAndConditions.class));
                }
            });
            // Enable JavaScript (if required for your HTML content)
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            // Set a WebViewClient to handle page navigation inside the WebView
            webView.setWebViewClient(new WebViewClient());

            // Load the HTML URL into the WebView
            String htmlUrl = getString(R.string.termsconditons); // Replace with your HTML URL
            webView.loadUrl(htmlUrl);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


}