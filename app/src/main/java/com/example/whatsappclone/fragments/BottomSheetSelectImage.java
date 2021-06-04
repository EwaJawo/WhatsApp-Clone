package com.example.whatsappclone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ProfileActivity;
import com.example.whatsappclone.providers.AuthProvider;
import com.example.whatsappclone.providers.ImageProvider;
import com.example.whatsappclone.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetSelectImage extends BottomSheetDialogFragment {


    LinearLayout mLinearLayoutDeleteImage;
    LinearLayout mLinearLayoutSelectImage;

    ImageProvider mImageProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUserProvider;

    String image;


    public static BottomSheetSelectImage newInstance(String url) {
        BottomSheetSelectImage bottomSheetSelectImage = new BottomSheetSelectImage();
         Bundle args = new Bundle();
         args.putString("image",url);
         bottomSheetSelectImage.setArguments(args);
          return bottomSheetSelectImage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image = getArguments().getString("image");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_select_image, container, false);
        mLinearLayoutDeleteImage = view.findViewById(R.id.linearLayoutDeleteImage);
        mLinearLayoutSelectImage = view.findViewById(R.id.linearLayoutSelectImage);

        mImageProvider = new ImageProvider();
        mUserProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        mLinearLayoutDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });

        mLinearLayoutSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage();
            }
        });
         return view;
    }

    private void updateImage() {
        ((ProfileActivity)getActivity()).startPix();
    }

    private void deleteImage() {
        mImageProvider.delete(image).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()) {
                   mUserProvider.updateImage(mAuthProvider.getId(), null).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task2) {
                           if (task2.isSuccessful()){
                               //setImageDefault();
                               Toast.makeText(getContext(), "Obraz został usunięty poprawnie", Toast.LENGTH_SHORT).show();
                           }
                           else {
                               Toast.makeText(getContext(), "Nie udało się usunąć obrazu", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });

               }
               else {
                   Toast.makeText(getContext(), "Nie udało się usunąć obrazu", Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    private void setImageDefault() {
        ((ProfileActivity)getActivity()).setImageDefault();

    }
}
