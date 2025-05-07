package com.aki.claimreporting;

import static com.aki.claimreporting.MainActivity.preventSizeChange;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class ClaimHandler extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_handler);
        try {
            //this.getSupportActionBar().hide();

            preventSizeChange(this, getSupportActionBar());
            Objects.requireNonNull(getSupportActionBar()).setTitle("Claim Failed");
        } catch (Exception e) {
            e.printStackTrace();
        }
       try {

         init();
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void init(){
        try{
            LinearLayout backToDashBoardBtn = findViewById(R.id.backToDashBoardBtn);
            TextView errorMsgTv = findViewById(R.id.errorMessage);
            Intent intent = getIntent();
            if(intent != null){
                errorMsgTv.setText(intent.getStringExtra("errorMsg"));
            }


            //on back pressed handler
            backToDashBoardBtn.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
            getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    startActivity(new Intent(ClaimHandler.this,Dashboard.class));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}