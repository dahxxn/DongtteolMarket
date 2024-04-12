package com.example.dongtteolmarket;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// 구매자 동떨오더 목록
public class OrderListActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    ImageButton orderBtn;
    ImageButton logoBtn;
    ImageButton mypageBtn;

    String boardID, sellerID, orderID;
    TextView stateTV, pickupTimeTV, requestTV, countTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db.collection("OrderList").whereEqualTo("customerID", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        View dealview = (View) getLayoutInflater().inflate(R.layout.order, null);
                        LinearLayout container = findViewById(R.id.orderLayout);

                        TextView boardNameTV = (TextView) dealview.findViewById(R.id.boardNameTV);
                        TextView sellerNameTV = (TextView) dealview.findViewById(R.id.sellerNameTV);
//
//                        db.collection("User").whereEqualTo("userID", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        customerNameTV.setText(document.getData().get("userName").toString());
//                                    }
//                                }
//                            }
//                        });

                        ImageView photoIV = (ImageView) dealview.findViewById(R.id.photoIV);
                        stateTV = (TextView) dealview.findViewById(R.id.stateTV);
                        pickupTimeTV = (TextView) dealview.findViewById(R.id.pickupTimeTV);
                        requestTV = (TextView) dealview.findViewById(R.id.requestTV);
                        countTV = (TextView) dealview.findViewById(R.id.countTV);

                        boardID = document.getData().get("boardID").toString();
                        orderID = document.getData().get("orderID").toString();
                        sellerID = document.getData().get("sellerID").toString();

                        db.collection("Board").whereEqualTo("boardID", boardID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        boardNameTV.setText(document.getData().get("boardName").toString());

                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference storageReference = storage.getReference();
                                        StorageReference pathReference = storageReference.child(document.getData().get("photo").toString());
                                        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(OrderListActivity.this).load(uri).into(photoIV);
                                            }
                                        });
                                    }
                                }
                            }
                        });

//
                        db.collection("User").whereEqualTo("userID", sellerID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        sellerNameTV.setText(document.getData().get("userName").toString());
                                    }
                                }
                            }
                        });

                        countTV.setText(document.getData().get("count").toString());
                        stateTV.setText(document.getData().get("state").toString());
                        requestTV.setText(document.getData().get("request").toString());
                        pickupTimeTV.setText(document.getData().get("pickupTime").toString());

                        if (document.getData().get("state").toString().equals("픽업완료")) {
                            pickupTimeTV.setText("리뷰");
                            pickupTimeTV.setTextColor(Color.parseColor("#DB5531"));
//                            pickupTimeTV.setTextSize(Dimension.DP, 45);
//                            pickupTimeTV.setPadding(20, 20, 20, 20);
                            pickupTimeTV.setClickable(true);
                            pickupTimeTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                                    intent.putExtra("customerID", user.getUid());
                                    intent.putExtra("type", "구매자");
                                    intent.putExtra("boardID", boardID);
                                    intent.putExtra("sellerID", sellerID);
                                    startActivity(intent);
                                }
                            });

                            DocumentReference washingtonRef = db.collection("OrderList").document(orderID);
                            washingtonRef
                                    .update("state", "구매자 리뷰완료")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {}
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) { }
                                    });

                        }
                        else if (document.getData().get("state").toString().equals("판매자 리뷰완료")) {
                            pickupTimeTV.setText("리뷰");
                            pickupTimeTV.setTextColor(Color.parseColor("#DB5531"));
//                            pickupTimeTV.setTextSize(Dimension.DP, 14);
//                            pickupTimeTV.setPadding(20, 20, 20, 20);
                            pickupTimeTV.setClickable(true);
                            pickupTimeTV.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                                    intent.putExtra("customerID", user.getUid());
                                    intent.putExtra("type", "구매자");
                                    intent.putExtra("boardID", boardID);
                                    intent.putExtra("sellerID", sellerID);
                                    startActivity(intent);
                                }
                            });

                            DocumentReference washingtonRef = db.collection("OrderList").document(orderID);
                            washingtonRef
                                    .update("state", "판매자 구매자 리뷰완료")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {}
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) { }
                                    });

                            }
                        else if(document.getData().get("state").toString().equals("판매자 구매자 리뷰완료") ||document.getData().get("state").toString().equals("구매자 판매자 리뷰업완료") ){
//                            pickupTimeTV.setBackgroundResource(R.drawable.statebox_gray);
                            stateTV.setText("리뷰완료");
                        }
//
                        container.addView(dealview);
                        dealview.setClickable(true);
                    }
                }

            }
        });


        //하단 메뉴 바 버튼 클릭 이벤트 추가
        orderBtn = (ImageButton) findViewById(R.id.bt_sellBoard_orderList);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OrderListActivity.class);
                startActivity(intent);

            }
        });

        logoBtn = (ImageButton) findViewById(R.id.bt_sellBoard_market);
        logoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SellBoardActivity.class);
                startActivity(intent);
            }
        });

        mypageBtn = (ImageButton) findViewById(R.id.bt_sellBoard_mypage);
        mypageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MypageActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}