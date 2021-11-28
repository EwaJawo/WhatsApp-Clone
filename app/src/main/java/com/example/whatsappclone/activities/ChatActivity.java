package com.example.whatsappclone.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.MessagesAdapter;
import com.example.whatsappclone.models.Chat;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProvider;
import com.example.whatsappclone.providers.ChatsProvider;
import com.example.whatsappclone.providers.MessagesProvider;
import com.example.whatsappclone.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


    String mExtraIdUser;
    String mExtraidChat;

    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;
    MessagesProvider mMessagesProvider;
    ImageView mImageViewBack;
    TextView mTextViewUsername;
    TextView mTextViewOnline;
    CircleImageView mCircleImageUser;
    EditText mEditTextMessage;
    ImageView mImageViewSend;

    MessagesAdapter mAdapter;
    RecyclerView mRecyclerViewMessages;
    LinearLayoutManager mLinearLayoutManager;

    Timer mTimer;
    ListenerRegistration mListenerChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mExtraIdUser = getIntent().getStringExtra("idUser");
        mExtraidChat = getIntent().getStringExtra("idChat");

        mUserProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mChatsProvider = new ChatsProvider();
        mMessagesProvider = new MessagesProvider();

        mEditTextMessage = findViewById(R.id.editTextMessage);
        mImageViewSend = findViewById(R.id.imageViewSend);
        mRecyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessages.setLayoutManager(mLinearLayoutManager);


        showChatToolbar(R.layout.chat_toolbar);
        getUserInfo();

        checkIfExistChat();


        mImageViewSend.setOnClickListener(v -> createMessage());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    private void createMessage() {

        String textMessage = mEditTextMessage.getText().toString();
        if (!textMessage.equals("")) {
            Message message = new Message();
            message.setIdChat(mExtraidChat);
            message.setIdSender(mAuthProvider.getId());
            message.setIdReceiver(mExtraIdUser);
            message.setMessage(textMessage);
            message.setStatus("WYSLANO");
            message.setTimestamp(new Date().getTime());

            mMessagesProvider.create(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mEditTextMessage.setText("");
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    mChatsProvider.updateNumberMessages(mExtraidChat);
                    // Toast.makeText(ChatActivity.this, "Wiadomość została utworzona poprawnie", Toast.LENGTH_SHORT).show();
                }
            });
        }  
        else {
            Toast.makeText(this, "Wpisz wiadomość", Toast.LENGTH_SHORT).show();
        }
    }
    

    private void checkIfExistChat() {
        mChatsProvider.getChatByUser1AndUser2(mExtraIdUser, mAuthProvider.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null) {
                    if (queryDocumentSnapshots.size() == 0) {
                        createChat();
                    }
                        else {
                            mExtraidChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                            getMessagesByChat();
                            updateStatus();
                            // Toast.makeText(ChatActivity.this, "Czat między dwoma użytkownikami już istnieje", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        });
    }



    private void updateStatus() {
        mMessagesProvider.getMessageNotRead(mExtraidChat).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for ( DocumentSnapshot document: queryDocumentSnapshots.getDocuments()) {
                    Message message = document.toObject(Message.class);

                    if (!message.getIdSender().equals(mAuthProvider.getId())) {
                        mMessagesProvider.updateStatus(message.getId(), "ODEBRANA");
                    }
                }
            }
        });
    }

    private void getMessagesByChat() {
        Query query = mMessagesProvider.getMessagesByChat(mExtraidChat);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        mAdapter = new MessagesAdapter(options, ChatActivity.this);
        mRecyclerViewMessages.setAdapter(mAdapter);
        mAdapter.startListening();

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateStatus();

                int numberMessage = mAdapter.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastMessagePosition == -1 || (positionStart >= (numberMessage - 1) && lastMessagePosition == (positionStart -1))) {
                    mRecyclerViewMessages.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setId(mAuthProvider.getId() + mExtraIdUser);
        chat.setTimestamp(new Date().getTime());
        chat.setNumberMessages(0);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mAuthProvider.getId());
        ids.add(mExtraIdUser);

        chat.setIds(ids);
        mExtraidChat = chat.getId();

        mChatsProvider.create(chat).addOnSuccessListener(aVoid -> {
            getMessagesByChat();
            //Toast.makeText(ChatActivity.this, "Czat został pomyślnie utworzony", Toast.LENGTH_SHORT).show();
        });
    }

    private void getUserInfo() {

        mUserProvider.getUserInfo(mExtraIdUser).addSnapshotListener((DocumentSnapshot documentSnapshot, FirebaseFirestoreException error) -> {
            if (documentSnapshot != null) {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    mTextViewUsername.setText(user.getUsername());
                    if (user.getImage() != null) {
                        if (!user.getImage().equals("")) {
                            Picasso.with(ChatActivity.this).load(user.getImage()).into(mCircleImageUser);
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
      mTextViewOnline =  view.findViewById(R.id.textViewOnline);

       mImageViewBack.setOnClickListener(v -> finish());
   }
}