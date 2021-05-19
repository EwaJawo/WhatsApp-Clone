package com.example.whatsappclone.providers;

import com.example.whatsappclone.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsersProvider {

    private CollectionReference mCollection;

    public UsersProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }
    public Task<Void> create(User user) {
        return mCollection.document(user.getId()).set(user);
    }
}
