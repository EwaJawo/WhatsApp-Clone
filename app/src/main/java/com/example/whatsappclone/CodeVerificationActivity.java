package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.activities.CompleteInfoActivity;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProvider;
import com.example.whatsappclone.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class CodeVerificationActivity extends AppCompatActivity {

    Button mButtonCodeVerification;
    EditText mEditTextCode;
    TextView mTextViewSMS;
    ProgressBar mProgressBar;
    String mExtraPhone;
    String mVerificationId;
    AuthProvider mAuthProvider;
    UsersProvider mUserProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        mButtonCodeVerification = findViewById(R.id.btnCodeVerification);
        mEditTextCode = findViewById(R.id.editTextCodeVerification);
        mTextViewSMS = findViewById(R.id.textViewSMS);
        mProgressBar = findViewById(R.id.progressBar);
        mAuthProvider = new AuthProvider();
        mUserProvider = new UsersProvider();
        mExtraPhone = getIntent().getStringExtra("phone");
        mAuthProvider.sendCodeVerification(mExtraPhone, mCallbacks);
        mButtonCodeVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mEditTextCode.getText().toString();
                if (!code.equals("") && code.length() >= 6) {
                    signIn(code);
                } else {
                    Toast.makeText(CodeVerificationActivity.this, "Wpisz kod", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            mProgressBar.setVisibility(View.GONE);
            mTextViewSMS.setVisibility(View.GONE);


            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                mEditTextCode.setText(code);
                signIn(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            mProgressBar.setVisibility(View.GONE);
            mTextViewSMS.setVisibility(View.GONE);
            Toast.makeText(CodeVerificationActivity.this, "Wystąpił błąd:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            Toast.makeText(CodeVerificationActivity.this, "Kod został wysłany ", Toast.LENGTH_LONG).show();
            mVerificationId = verificationId;
        }
    };

    private void signIn(String code) {
        mAuthProvider.signPhone(mVerificationId, code).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User();
                    user.setId(mAuthProvider.getId());
                    user.setPhone(mExtraPhone);
                    mUserProvider.create(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            goToCompleteInfo();
                        }
                    });
                }
               else {
                    Toast.makeText(CodeVerificationActivity.this, "Nie można uwierzytelnić użytkownika", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void goToCompleteInfo() {
        Intent intent = new Intent(CodeVerificationActivity.this, CompleteInfoActivity.class);
        startActivity(intent);
    }
}