package com.example.whatsappclone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.whatsappclone.R;
import com.example.whatsappclone.providers.AuthProvider;
import com.example.whatsappclone.providers.ImageProvider;
import com.example.whatsappclone.providers.UsersProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetInfo extends BottomSheetDialogFragment {

    Button mButtonSave;
    Button mButtonCancle;
    EditText mEditTextInfo;
    ImageProvider mImageProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUserProvider;
    String info;

    public static BottomSheetInfo newInstance(String info) {
        BottomSheetInfo bottomSheetSelectImage = new BottomSheetInfo();
         Bundle args = new Bundle();
         args.putString("info",info);
         bottomSheetSelectImage.setArguments(args);
          return bottomSheetSelectImage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = getArguments().getString("info");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_info, container, false);

        mButtonSave = view.findViewById(R.id.btnSave);
        mButtonCancle= view.findViewById(R.id.btnCancel);
        mEditTextInfo = view.findViewById(R.id.editTextInfo);
        mEditTextInfo.setText(info);

        mImageProvider = new ImageProvider();
        mUserProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });

        mButtonCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
         return view;
    }

    private void updateInfo() {
        String info = mEditTextInfo.getText().toString();
        if (!info.equals("")) {
            mUserProvider.updateInfo(mAuthProvider.getId(), info).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dismiss();
                    Toast.makeText(getContext(), "Status został pomyślnie zaktualizowany", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
