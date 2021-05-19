package com.example.whatsappclone.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.R;

public class CodeVerificationActivity extends AppCompatActivity {

    Button mButtonCodeVerification;
    String mExtraPhone;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        mButtonCodeVerification = findViewById(R.id.btnCodeVerification);
        mExtraPhone = getIntent().getStringExtra( "phone");
        mButtonCodeVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CodeVerificationActivity.this, "Użytkownik kliknął przycisk", Toast.LENGTH_SHORT).show();
            }
        });

    }

}