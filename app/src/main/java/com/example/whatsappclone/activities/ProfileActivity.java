package com.example.whatsappclone.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsappclone.R;
import com.example.whatsappclone.fragments.BottomSheetInfo;
import com.example.whatsappclone.fragments.BottomSheetSelectImage;
import com.example.whatsappclone.fragments.BottomSheetUsername;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProvider;
import com.example.whatsappclone.providers.ImageProvider;
import com.example.whatsappclone.providers.UsersProvider;
import com.example.whatsappclone.utils.MyToolbar;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    FloatingActionButton mFabSelectImage;
    BottomSheetSelectImage mBottomSheetSelectImage;
    BottomSheetUsername mBottomSheetUsername;
    BottomSheetInfo mBottomSheetInfo;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;

    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewInfo;

    CircleImageView mCircleImageProfile;
    ImageView mImageViewEditUsername;
    ImageView mImageViewEditInfo;
    User mUser;

    Options mOptions;
    ArrayList<String> mReturnValues = new ArrayList<>();
    File mImageFile;

    ListenerRegistration mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        MyToolbar.show(this,"Profil",true);

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewInfo = findViewById(R.id.textViewInfo);
        mCircleImageProfile = findViewById(R.id.circleImageProfile);
        mImageViewEditUsername = findViewById(R.id.imageViewEditUsername);
        mImageViewEditInfo = findViewById(R.id.imageViewEditInfo);


        mOptions  = Options.init()
                .setRequestCode(100)
                .setCount(1)
                .setFrontfacing(false)
                .setPreSelectedUrls(mReturnValues)
                .setSpanCount(4)
                .setMode(Options.Mode.All)
                .setVideoDurationLimitinSeconds(0)
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath("/pix/images");

        mFabSelectImage = findViewById(R.id.fabSelectImage);
        mFabSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                openBottomSheetSelectImage();
            }
        });

        mImageViewEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetUsername();
            }
        });

        mImageViewEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetInfo();
            }
        });

        getUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.remove();
        }
    }

    private void getUserInfo() {
       mListener =  mUsersProvider.getUserInfo(mAuthProvider.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        mUser = documentSnapshot.toObject(User.class);
                        mTextViewUsername.setText(mUser.getUsername());
                        mTextViewPhone.setText(mUser.getPhone());
                        mTextViewInfo.setText(mUser.getInfo());
                        if(mUser.getImage() != null) {
                            if (!mUser.getImage().equals("")) {
                                Picasso.with(ProfileActivity.this).load(mUser.getImage()).into(mCircleImageProfile);
                            }
                            else{
                                setImageDefault();
                            }
                        }
                        else{
                            setImageDefault();
                        }
                    }
                }
            }
        });
    }

    private void openBottomSheetSelectImage() {
        if(mUser != null) {
            mBottomSheetSelectImage = BottomSheetSelectImage.newInstance(mUser.getImage());
            mBottomSheetSelectImage.show(getSupportFragmentManager(), mBottomSheetSelectImage.getTag());
        }
        else {
            Toast.makeText(this, "Nie udało się załadować informacji", Toast.LENGTH_SHORT).show();
        }
    }

    private void openBottomSheetInfo() {
        if(mUser != null) {
            mBottomSheetInfo = BottomSheetInfo.newInstance(mUser.getInfo());
            mBottomSheetInfo.show(getSupportFragmentManager(), mBottomSheetInfo.getTag());
        }
        else {
            Toast.makeText(this, "Nie udało się załadować informacji", Toast.LENGTH_SHORT).show();
        }
    }

    private void openBottomSheetUsername() {
        if(mUser != null) {
            mBottomSheetUsername = BottomSheetUsername.newInstance(mUser.getUsername());
            mBottomSheetUsername.show(getSupportFragmentManager(), mBottomSheetUsername.getTag());
        }
        else {
            Toast.makeText(this, "Nie udało się załadować informacji", Toast.LENGTH_SHORT).show();
        }
    }
    public void setImageDefault() {
        mCircleImageProfile.setImageResource(R.drawable.ic_person_white);
    }

    public void startPix() {
        Pix.start(ProfileActivity.this, mOptions);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            mReturnValues= data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            mImageFile = new File(mReturnValues.get(0));
            mCircleImageProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            saveImage();
         }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(ProfileActivity.this, mOptions);
                } else {
                    Toast.makeText(ProfileActivity.this, "Proszę o pozwolenie na dostęp do kamery", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    private void saveImage() {
        mImageProvider = new ImageProvider();
        mImageProvider.save(ProfileActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    mImageProvider.getDownloadUri().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            mUsersProvider.updateImage(mAuthProvider.getId(), url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ProfileActivity.this, "Obraz został poprawnie zaktualizowany", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                else {
                    Toast.makeText(ProfileActivity.this, " Nie można zapisać obrazu", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
