package com.example.dongtteolmarket;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BoardView extends LinearLayout {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    public String boardID, userID;

    public BoardView(Context context, AttributeSet attrs, String userID){
        super(context, attrs);
        this.userID = userID;
        init(context);
    }

    public BoardView(Context context, String userID, String boardID){
        super(context);
        this.userID = userID;
        this.boardID = boardID;
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.board_view, this, true);

        db.collection("Board")
                .whereEqualTo("boardID", boardID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){

                                ImageView img = (ImageView) findViewById(R.id.board_photo);

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageReference = storage.getReference();
                                StorageReference pathReference = storageReference.child(document.getData().get("photo").toString());
                                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(BoardView.this).load(uri).into(img);
                                    }
                                });



                                TextView title = (TextView) findViewById(R.id.board_title);
                                title.setText(document.getData().get("boardName").toString());

                                TextView count = (TextView) findViewById(R.id.board_count);
                                count.setText(document.getData().get("count").toString());

                                TextView cost = (TextView) findViewById(R.id.board_cost);
                                cost.setText(document.getData().get("cost").toString());

                                TextView note = (TextView) findViewById(R.id.board_note);
                                note.setText(document.getData().get("note").toString());

                                ImageButton edit = (ImageButton) findViewById(R.id.edit_button);

                                edit.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, Seller_OrderList_update.class);
                                        intent.putExtra("pre_id",boardID);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }
                                });
                            }
                        }
                    }
                });
    }

}
