package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Objects;

public class InvalidResultView extends AppCompatActivity {
    TextView invalidMsgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invalid_result_view);

        try {
            preventSizeChange(this, getSupportActionBar());
            Objects.requireNonNull(getSupportActionBar()).setTitle("Verification Result");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            invalidMsgView = findViewById(R.id.invalidMsgView);

            String type = getIntent().getStringExtra("type");
            if(type != null) {
                if(type.equals("motor")) {
                    invalidMsgView.setText(R.string.invalid_crt);
                } else if (type.equals("life")) {
                    invalidMsgView.setText(R.string.invalid_life_crt);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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