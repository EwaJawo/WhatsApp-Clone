package com.example.whatsappclone.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.Chat;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProvider;
import com.example.whatsappclone.providers.ChatsProvider;
import com.example.whatsappclone.providers.MessageProvider;
import com.example.whatsappclone.providers.UsersProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser;
    String mExtraidChat;

    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatProvider;
    MessageProvider mMessageProvider;
    ImageView mImageViewBack;
    TextView mTextViewUsername;
    CircleImageView mCircleImageUser;
    EditText mEditTextMessage;
    ImageView mImageViewSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mExtraIdUser = getIntent().getStringExtra("idUser");
        mExtraidChat = getIntent().getStringExtra("idChat");

        mUserProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mChatProvider = new ChatsProvider();
        mMessageProvider = new MessageProvider();

        mEditTextMessage = findViewById(R.id.editTextMessage);
        mImageViewSend = findViewById(R.id.imageViewSend);


        showChatToolbar(R.layout.chat_toolbar);
        getUserInfo();

        checkIfExistChat();


        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMessage();
            }
        });
    }

    private void createMessage() {

        String textMessage = mEditTextMessage.getText().toString();

        if (!textMessage.equals("")) {
            Message message = new Message();
            message.setIdChat(mExtraidChat);
            message.setIdSender(mAuthProvider.getId());
            message.setIdReceiver(mExtraIdUser);
            message.setMessage(textMessage);
            message.setStatus("Wysłany");
            message.setTimestamp(new Date().getTime());

            mMessageProvider.create(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mEditTextMessage.setText("");
                    Toast.makeText(ChatActivity.this, "Wiadomość została utworzona poprawnie", Toast.LENGTH_SHORT).show();
                }
            });
        }  
        else {
            Toast.makeText(this, "Wpisz wiadomość", Toast.LENGTH_SHORT).show();
        }
    }
    

    private void checkIfExistChat() {
        mChatProvider.getChatByUser1AndUser2(mExtraIdUser, mAuthProvider.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null) {
                    if (queryDocumentSnapshots.size() == 0) {
                        createChat();
                    }
                        else {
                            mExtraidChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Toast.makeText(ChatActivity.this, "Czat między dwoma użytkownikami już istnieje", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        });
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setId(mAuthProvider.getId() + mExtraIdUser);
        chat.setTimestamp(new Date().getTime());

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mAuthProvider.getId());
        ids.add(mExtraIdUser);

        chat.setIds(ids);

        mExtraidChat = chat.getId();


        mChatProvider.create(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ChatActivity.this, "Czat został pomyślnie utworzony", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getUserInfo() {
        mUserProvider.getUserInfo(mExtraIdUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot !=null) {
                   if (documentSnapshot.exists()) {
                       User user = documentSnapshot.toObject(User.class);
                       mTextViewUsername.setText(user.getUsername());
                       if (user.getImage() != null) {
                         if(!user.getImage().equals("")){
                             Picasso.with(ChatActivity.this).load(user.getImage()).into(mCircleImageUser);
                         }
                       }

                   }
                }
            }
        });
    }

    private void showChatToolbar(int resource){
       Toolbar toolbar = findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
       ActionBar actionBar = getSupportActionBar();
       actionBar.setTitle("");
       actionBar.setDisplayShowHomeEnabled(true);
       actionBar.setDisplayShowCustomEnabled(true);
       LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View view = inflater.inflate(resource, null);
       actionBar.setCustomView(view);

      mImageViewBack = view.findViewById(R.id.imageViewBack);
      mTextViewUsername = view.findViewById(R.id.textViewUsername);
      mCircleImageUser = view.findViewById(R.id.circleImageUser);
       mImageViewBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });
   }
}