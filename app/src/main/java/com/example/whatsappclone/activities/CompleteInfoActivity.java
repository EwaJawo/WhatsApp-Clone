package com.example.whatsappclone.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProvider;
import com.example.whatsappclone.providers.ImageProvider;
import com.example.whatsappclone.providers.UsersProvider;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteInfoActivity extends AppCompatActivity {

    TextInputEditText mTextInputUsername;
    Button mButtonConfirm;
    CircleImageView mCircleImagePhoto;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;

    Options mOptions;
    ArrayList<String> mReturnValues = new ArrayList<>();


     File mImageFile;
    String mUsername = "";
    ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info2);

        mTextInputUsername = findViewById(R.id.textInputUsername);
        mButtonConfirm = findViewById(R.id.btnConfirm);
        mCircleImagePhoto = findViewById(R.id.circleImagePhoto);

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mImageProvider =new ImageProvider();

        mDialog = new ProgressDialog(CompleteInfoActivity.this);
        mDialog.setTitle("Poczekaj chwilę");
        mDialog.setMessage("Zapisywanie informacji");
        mOptions  = Options.init()
                .setRequestCode(100)
                .setCount(1)
                .setFrontfacing(false)
                .setPreSelectedUrls(mReturnValues)
                .setExcludeVideos(true)
                .setVideoDurationLimitinSeconds(0)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath("/pix/images");

        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = mTextInputUsername.getText().toString();
                if(!mUsername.equals("")&& mImageFile !=null) {
                    saveImage();
                }
                else{
                    Toast.makeText(CompleteInfoActivity.this, "Wybierz obraz i wprowadzić swoją nazwę użytkownika", Toast.LENGTH_LONG).show();
                        }
            }
        });

        mCircleImagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPix();
            }
        });
    }

    private void startPix() {
        Pix.start(CompleteInfoActivity.this, mOptions);
    }

    private void updateUserInfo(String url) {

            User user = new User();
            user.setUsername(mUsername);
            user.setId(mAuthProvider.getId());
            user.setImage(url);
            mUsersProvider.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    goToHomeActivity();
                }
            });
    }

    private void goToHomeActivity() {

        mDialog.dismiss();
        Toast.makeText(CompleteInfoActivity.this, "Informacje zostały poprawnie zaktualizowane", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CompleteInfoActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void saveImage() {
        mDialog.show();
        mImageProvider.save(CompleteInfoActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getDownloadUri().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            updateUserInfo(url);
                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(CompleteInfoActivity.this, " Nie można zapisać obrazu", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            mReturnValues= data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            mImageFile = new File(mReturnValues.get(0));
            mCircleImagePhoto.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(CompleteInfoActivity.this, mOptions);
                } else {
                    Toast.makeText(CompleteInfoActivity.this, "Proszę o pozwolenie na dostęp do kamery", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}

