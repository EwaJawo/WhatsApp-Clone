package com.example.whatsappclone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.ContactsAdapter;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    View mView;
    RecyclerView mRecyclerViewContacts;
    ContactsAdapter mAdapter;
    UsersProvider mUserProvider;


    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_contacs, container, false);
        mRecyclerViewContacts = mView.findViewById(R.id.recyclerViewContacts);
        mUserProvider = new UsersProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewContacts.setLayoutManager(linearLayoutManager);
        return mView;
     }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mUserProvider.getAllUserByName();
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                                                    .setQuery(query, User.class)
                                                    .build();
        mAdapter = new ContactsAdapter(options, getContext());
        mRecyclerViewContacts.setAdapter(mAdapter);
        mAdapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}