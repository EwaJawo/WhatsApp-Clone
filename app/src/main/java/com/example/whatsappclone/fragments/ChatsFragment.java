package com.example.whatsappclone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.ChatsAdapter;
import com.example.whatsappclone.models.Chat;
import com.example.whatsappclone.providers.AuthProvider;
import com.example.whatsappclone.providers.ChatsProvider;
import com.example.whatsappclone.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatsFragment extends Fragment {

    View mView;
    RecyclerView mRecyclerViewChats;
    ChatsAdapter mAdapter;

    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatProvider;


    public ChatsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat, container, false);
        mRecyclerViewChats = mView.findViewById(R.id.recyclerViewChats);
        mUserProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mChatProvider = new ChatsProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewChats.setLayoutManager(linearLayoutManager);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mChatProvider.getUserChats(mAuthProvider.getId());
        
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();

        mAdapter = new ChatsAdapter(options, getContext());
        mRecyclerViewChats.setAdapter(mAdapter);
        mAdapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}