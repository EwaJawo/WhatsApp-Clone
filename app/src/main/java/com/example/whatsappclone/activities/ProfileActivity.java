package com.example.whatsappclone.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.R;
import com.example.whatsappclone.fragments.BottomSheetSelectImage;
import com.example.whatsappclone.utils.MyToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileActivity extends AppCompatActivity {

    FloatingActionButton mFabSelectImage;
    BottomSheetSelectImage mBottomSheetSelectImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        MyToolbar.show(this,"Profil",true);

        mFabSelectImage = findViewById(R.id.fabSelectImage);
        mFabSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetSelectImage();

            }
        });
    }

    private void openBottomSheetSelectImage() {
        mBottomSheetSelectImage = BottomSheetSelectImage.newInstance();
        mBottomSheetSelectImage.show(getSupportFragmentManager(), mBottomSheetSelectImage.getTag());
    }

}