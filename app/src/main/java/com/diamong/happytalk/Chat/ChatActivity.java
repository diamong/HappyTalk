package com.diamong.happytalk.Chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diamong.happytalk.R;
import com.diamong.happytalk.model.ChatModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String destinationUid;
    private Button button;
    private EditText editText;

    private String uid, chatRoomUid;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        destinationUid = getIntent().getStringExtra("destinationUid");

        recyclerView=findViewById(R.id.chatactivity_recyclerview);
        button = findViewById(R.id.chatactivity_button);
        editText = findViewById(R.id.chatactivity_edittext);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);

                if (chatRoomUid == null) {
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    checkChatRoom();
                                }
                            });

                }else {
                   ChatModel.Comment comment = new ChatModel.Comment();
                   comment.uid=uid;
                   comment.message=editText.getText().toString();
                    FirebaseDatabase.getInstance()
                            .getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment);
                }
            }
        });





    }

    void checkChatRoom() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            ChatModel chatModel = item.getValue(ChatModel.class);
                            if (chatModel.users.containsKey(destinationUid)) {
                                chatRoomUid = item.getKey();
                                button.setEnabled(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                                recyclerView.setAdapter(new RecyclerviewAdapter());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<ChatModel.Comment> comments;

        public RecyclerviewAdapter() {
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            comments.clear();
                            for (DataSnapshot item : dataSnapshot.getChildren()){
                                comments.add(item.getValue(ChatModel.Comment.class));
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message,viewGroup,false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ((MessageViewHolder)viewHolder).textView_message.setText(comments.get(i).message);

        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayoutDestination;


            public MessageViewHolder(View view) {
                super(view);
                textView_message=view.findViewById(R.id.itemmessage_txtview);
                textView_name=view.findViewById(R.id.itemmessage_txtview_name);
                imageView_profile=view.findViewById(R.id.itemmessage_imgview_profile);
                linearLayoutDestination=view.findViewById(R.id.itemmessage_linearlayout_destination);
            }
        }
    }
}
