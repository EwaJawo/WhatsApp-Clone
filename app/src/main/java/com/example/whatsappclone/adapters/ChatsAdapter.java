package com.example.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ChatActivity;
import com.example.whatsappclone.models.Chat;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.providers.AuthProvider;
import com.example.whatsappclone.providers.MessagesProvider;
import com.example.whatsappclone.providers.UsersProvider;
import com.example.whatsappclone.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

    Context context;
    AuthProvider authProvider;
    UsersProvider usersProvider;
    MessagesProvider messagesProvider;
    User user;
    ListenerRegistration listener;
    ListenerRegistration listenerLastMessage;



    public ChatsAdapter(FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
        messagesProvider = new MessagesProvider();
        user = new User();
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Chat chat) {

        String idUser = "";
            for (int i = 0; i <chat.getIds().size(); i ++) {
                if (!authProvider.getId().equals(chat.getIds().get(i))) {
                    idUser = chat.getIds().get(i);
                    break;
                }
            }
            getLastMessage(holder, chat.getId());
            getUserInfo(holder, idUser);
            getMessagesNotRead(holder, chat.getId());

            setWriting(holder, chat);
            clickMyView(holder, chat.getId(), idUser);
        }

    private void setWriting(ViewHolder holder, Chat chat) {
        if (chat.getWriting() != null) {
            if (!chat.getWriting().equals("")) {
                if (!chat.getWriting().equals(authProvider.getId())){
                    holder.textViewWriting.setVisibility(View.VISIBLE);
                    holder.textViewLastMessage.setVisibility(View.GONE);

                }
                else {
                    holder.textViewWriting.setVisibility(View.GONE);
                    holder.textViewLastMessage.setVisibility(View.VISIBLE);
                }
            }
            else{
                holder.textViewWriting.setVisibility(View.GONE);
                holder.textViewLastMessage.setVisibility(View.VISIBLE);            }
        }

    }

    private void getMessagesNotRead(final ViewHolder holder, final String idChat) {
        messagesProvider.getReceiverMessagesNotRead(idChat, authProvider.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot != null) {
                    int size = querySnapshot.size();
                    if (size > 0) {
                        holder.frameLayoutMessagesNotRead.setVisibility(View.VISIBLE);
                        holder.textViewMessagesNotRead.setText(String.valueOf(size));
                        holder.textViewTimestamp.setTextColor(context.getResources().getColor(R.color.greenAccent));
                    }
                    else {
                        holder.frameLayoutMessagesNotRead.setVisibility(View.GONE);
                        holder.textViewTimestamp.setTextColor(context.getResources().getColor(R.color.grayDark));

                    }
                }
            }
        });
    }

    private void getLastMessage(final ViewHolder holder, String idChat) {
        listenerLastMessage = messagesProvider.getLastMessage(idChat).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot != null) {
                    int size = querySnapshot.size();
                    if (size > 0) {
                        Message message = querySnapshot.getDocuments().get(0).toObject(Message.class);
                        holder.textViewLastMessage.setText(message.getMessage());
                        holder.textViewTimestamp.setText(RelativeTime.timeFormatAMPM(message.getTimestamp(), context));

                        if (message.getIdSender().equals(authProvider.getId())) {
                            holder.imageViewCheck.setVisibility(View.VISIBLE);
                            if (message.getStatus().equals("WYSLANO")) {
                                holder.imageViewCheck.setImageResource(R.drawable.icon_double_check_gray);
                            }
                           else if (message.getStatus().equals("ODEBRANO")) {
                                holder.imageViewCheck.setImageResource(R.drawable.icon_double_check_blue);
                            }
                        }
                        else{
                            holder.imageViewCheck.setVisibility(View.GONE);
                        }

                    }
                }
            }
        });
    }


    private void clickMyView(ViewHolder holder, final String idChat,  final String idUser) {
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(idChat, idUser);
            }
        });
    }

    private void getUserInfo(final ViewHolder holder, String idUser) {
            listener = usersProvider.getUserInfo(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                    if (documentSnapshot != null) {
                        if (documentSnapshot.exists()) {
                            user = documentSnapshot.toObject(User.class);
                            holder.textViewUsername.setText(user.getUsername());
                            if (user.getImage() != null) {
                                if (!user.getImage().equals("")) {
                                    Picasso.with(context).load(user.getImage()).into(holder.circleImageUser);
                                } else {
                                    holder.circleImageUser.setImageResource(R.drawable.ic_person);
                                }
                            }
                            else {
                                holder.circleImageUser.setImageResource(R.drawable.ic_person);
                            }
                        }
                    }
                }
            });
    }

    public ListenerRegistration getListener(){
        return listener;
    }

    public ListenerRegistration getListenerLastMessage(){
        return listenerLastMessage;
    }


    private void goToChatActivity(String idChat, String idUser) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idUser", idUser);
        intent.putExtra("idChat", idChat);
        context.startActivity(intent);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent,false);
         return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewLastMessage;
        TextView textViewTimestamp;
        CircleImageView circleImageUser;
        ImageView imageViewCheck;
        FrameLayout frameLayoutMessagesNotRead;
        TextView textViewMessagesNotRead;
        TextView textViewWriting;
        View myView;



        public ViewHolder(View view) {
            super(view);
            myView = view;
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewLastMessage = view.findViewById(R.id. textViewLastMessage);
            textViewTimestamp = view.findViewById(R.id.textViewTimestamp);
            circleImageUser = view.findViewById(R.id.circleImageUser);
            imageViewCheck = view.findViewById(R.id.imageViewCheck);
            frameLayoutMessagesNotRead = view.findViewById(R.id.frameLayoutMessagesNotRead);
            textViewMessagesNotRead = view.findViewById(R.id.textViewMessagesNotRead);
            textViewWriting = view.findViewById(R.id.textViewWriting);

        }

    }

}
