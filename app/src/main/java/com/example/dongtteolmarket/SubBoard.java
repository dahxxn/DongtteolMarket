package com.example.dongtteolmarket;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class SubBoard extends LinearLayout {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private DatabaseReference mDatabase;

    public String location;
    public String boardID;
    ImageButton heart;

    ImageView medal;

    public SubBoard() {
        super(null);
    }

    public SubBoard(Context context, AttributeSet attrs, String location) {
        super(context, attrs);
        this.location = location;
        init(context);

    }

    public SubBoard(Context context, String location, String boardID) {
        super(context);
        this.location = location;
        this.boardID = boardID;

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sub_board, this, true);

        db.collection("Board")
                .whereEqualTo("boardID", boardID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ImageView img = (ImageView) findViewById(R.id.iv_subBoard_png);
                                //이미지 가져오는 코드
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageReference = storage.getReference();
                                //board에 photo에 저장된 이미지 이름으로 경로 설정
                                StorageReference pathReference = storageReference.child(document.getData().get("photo").toString());
                                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(SubBoard.this).load(uri).into(img);
                                    }
                                });


                                TextView store = (TextView) findViewById(R.id.tv_subBoard_storeName);
                                //가게명 가져오는 코드
                                String sellerID = document.getData().get("userID").toString();

                                store.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, Store_review.class);
                                        intent.putExtra("sellerID", sellerID);
                                        intent.putExtra("boardID", boardID);
                                        intent.putExtra("customerID", "");
                                        intent.putExtra("type", "판매자리뷰보기");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }
                                });


                                db.collection("User")
                                        .whereEqualTo("userID", sellerID)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot dc : task.getResult()) {
                                                        store.setText(dc.getData().get("userName").toString());
                                                        medal = (ImageView) findViewById(R.id.iv_subBoard_medal);
                                                        String star = dc.getData().get("star").toString();

                                                        if (Double.parseDouble(star) >= 4.0) {
                                                            medal.setImageResource(R.drawable.medal);
                                                        } else if (Double.parseDouble(star) >= 3.0) {

                                                            medal.setImageResource(R.drawable.medal2);
                                                        } else {
                                                            medal.setImageResource(R.drawable.medal3);
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                        });

                                //게시판 클릭시 게시글 창 뜨고-> 해당 창을 통해 픽업예약 하도록
                                LinearLayout board = (LinearLayout) findViewById(R.id.bt_subBoard_btn);
                                // GridLayout board = (GridLayout)findViewById(R.id.bt_subBoard_btn);
                                board.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getContext(), SubBoardDialogActivity.class);
                                        intent.putExtra("boardID", boardID);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }
                                });


                                TextView title = (TextView) findViewById(R.id.tv_subBoard_title);
                                title.setText(document.getData().get("boardName").toString());

                                TextView count = (TextView) findViewById(R.id.tv_subBoard_count);
                                count.setText(document.getData().get("count").toString());

                                TextView cost = (TextView) findViewById(R.id.tv_subBoard_cost);
                                cost.setText(document.getData().get("cost").toString());

                                TextView time = (TextView) findViewById(R.id.tv_subBoard_time);
                                time.setText(document.getData().get("endTime").toString());

//
//                                TextView note = (TextView) findViewById(R.id.tv_subBoard_note);
//                                note.setText(document.getData().get("note").toString());

//                                ImageButton heart = (ImageButton) findViewById(R.id.bt_subBoard_heart);
//
//                                heart.setOnClickListener(new OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent(context, ReserveActivity.class);
//                                        intent.putExtra("boardID",boardID);
//                                        intent.putExtra("sellerID",sellerID);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        context.startActivity(intent);
//                                    }
//                                });
                            }
                        }
                    }

                });

    }

}

