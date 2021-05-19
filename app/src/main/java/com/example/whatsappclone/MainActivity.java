package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button mButtonSendCode;
    EditText mEditTextPhone;
    CountryCodePicker mCountryCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonSendCode = findViewById(R.id.btnSendCode);
        mEditTextPhone = findViewById(R.id.editTextPhone);
        mCountryCode = findViewById(R.id.ccp);
        mButtonSendCode.setOnClickListener(v -> {
          //  goToCodeVerificationActivity();
            getData();
        });
    }

    private void getData() {
        String code = mCountryCode.getSelectedCountryCodeWithPlus();
        String phone = mEditTextPhone.getText().toString();

        if (phone.equals("")) {
            Toast.makeText(this, "Wpisz numer telefonu", Toast.LENGTH_SHORT).show();
        }
        else {
            goToCodeVerificationActivity(code + phone);
            Toast.makeText(MainActivity.this, "Numer telefonu" + code + " " + phone, Toast.LENGTH_LONG).show();
        }
    }

     private void goToCodeVerificationActivity(String phone) {
        Intent intent = new Intent(MainActivity.this, CodeVerificationActivity.class);
        intent.putExtra( "phone", phone);
        startActivity(intent);
    }
}