package com.example.dongtteolmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// 판매자 동떨오더 관리
public class OrderManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    String boardID, customerID;
    TextView pickupTimeTV, requestTV, countTV, callNumTV, sellerNameTV;

    ImageButton orderBtn;
    ImageButton logoBtn;
    ImageButton mypageBtn;
    int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db.collection("OrderList").whereEqualTo("sellerID", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        View dealview = (View) getLayoutInflater().inflate(R.layout.order2, null);
                        LinearLayout container = findViewById(R.id.orderLayout);

                        TextView boardNameTV = (TextView) dealview.findViewById(R.id.boardNameTV);
                        TextView customerNameTV = (TextView) dealview.findViewById(R.id.customerNameTV);
                        TextView stateTV = (TextView) dealview.findViewById(R.id.stateTV);
                        ImageView photoIV = (ImageView) dealview.findViewById(R.id.photoIV);
                        pickupTimeTV = (TextView) dealview.findViewById(R.id.pickupTimeTV);
                        requestTV = (TextView) dealview.findViewById(R.id.requestTV);
                        countTV = (TextView) dealview.findViewById(R.id.countTV);
                        callNumTV = (TextView) dealview.findViewById(R.id.callNumTV);

                        boardID = document.getData().get("boardID").toString();

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
                                                Glide.with(OrderManagementActivity.this).load(uri).into(photoIV);
                                            }
                                        });
                                    }
                                }
                            }
                        });

                        customerID = document.getData().get("customerID").toString();
                        db.collection("User").whereEqualTo("userID", customerID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        customerNameTV.setText(document.getData().get("userName").toString());
                                    }
                                }
                            }
                        });
                        customerNameTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), Store_review.class);
                                intent.putExtra("sellerID", "");
                                intent.putExtra("boardID", boardID);
                                intent.putExtra("customerID", customerID);
                                intent.putExtra("type", "구매자리뷰보기");
                                startActivity(intent);
                            }
                        });

                        String callNum = document.getData().get("callNum").toString();
                        String count = document.getData().get("count").toString();
                        String customerID = document.getData().get("customerID").toString();
                        String pickupTime = document.getData().get("pickupTime").toString();
                        String request = document.getData().get("request").toString();
                        String sellerID = document.getData().get("sellerID").toString();
                        String state = document.getData().get("state").toString();
                        String orderID = document.getData().get("orderID").toString();

                        countTV.setText(count);
                        requestTV.setText(request);
                        pickupTimeTV.setText(pickupTime);
                        callNumTV.setText(callNum);
                        stateTV.setText(state);

                        container.addView(dealview);
                        dealview.setClickable(true);

                        Button orderBT = (Button) dealview.findViewById(R.id.orderBT);
                        if (state.equals("주문완료")) {
                            orderBT.setText("주문 승인하기");
                        } else if (state.equals("픽업대기")) {
                            orderBT.setText("픽업 완료하기");
                        } else if (state.equals("픽업완료") || state.equals("구매자 리뷰완료")) {
                            orderBT.setText("리뷰 작성하기");
                        } else if (state.equals("구매자 판매자 리뷰완료") || state.equals("판매자 구매자 리뷰완료") || state.equals("판매자 리뷰완료")) {
                            stateTV.setText("리뷰완료");
                            orderBT.setVisibility(View.GONE);
                            //진서연 컴에서 색깔 안바껴서 없애버림 걍.. 고치삼!
//                            orderBT.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.background));
                        }

                        orderBT.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String s = document.getData().get("state").toString();
                                String boardID2 = document.getData().get("boardID").toString();

                                if (s.equals("주문완료")) {
                                    s = "픽업대기";
                                    OrderList orderList = new OrderList(boardID2, callNum, count, customerID, orderID, pickupTime, request, sellerID, s);
                                    db.collection("OrderList").document(orderID).set(orderList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(OrderManagementActivity.this, "erro: add User document", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    finish();
                                    Intent intent = getIntent();
                                    startActivity(intent);
                                } else if (s.equals("픽업대기")) {
                                    s = "픽업완료";
                                    OrderList orderList = new OrderList(boardID2, callNum, count, customerID, orderID, pickupTime, request, sellerID, s);
                                    db.collection("OrderList").document(orderID).set(orderList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(OrderManagementActivity.this, "erro: add User document", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    finish();
                                    Intent intent = getIntent();
                                    startActivity(intent);
                                } else if (s.equals("픽업완료")) {
                                    s = "판매자 리뷰완료";
                                    OrderList orderList = new OrderList(boardID2, callNum, count, customerID, orderID, pickupTime, request, sellerID, s);
                                    db.collection("OrderList").document(orderID).set(orderList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(OrderManagementActivity.this, "erro: add User document", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                                    intent.putExtra("customerID", customerID);
                                    intent.putExtra("type", "판매자");
                                    intent.putExtra("boardID", boardID);
                                    intent.putExtra("sellerID", user.getUid());
                                    startActivity(intent);
                                }
                                else if (s.equals("구매자 리뷰완료")) {
                                    s = "구매자 판매자 리뷰완료";
                                    OrderList orderList = new OrderList(boardID2, callNum, count, customerID, orderID, pickupTime, request, sellerID, s);
                                    db.collection("OrderList").document(orderID).set(orderList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(OrderManagementActivity.this, "erro: add User document", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                                    intent.putExtra("customerID", customerID);
                                    intent.putExtra("type", "판매자");
                                    intent.putExtra("boardID", boardID);
                                    intent.putExtra("sellerID", user.getUid());
                                    startActivity(intent);
                                } else if(s.equals("구매자 판매자 리뷰완료") || s.equals("판매자 구매자 리뷰완료")) {

                                    orderBT.setClickable(false);
                                }
                            }
                        });
                    }
                }
            }
        });

//하단 메뉴 바 버튼 클릭 이벤트 추가
        orderBtn = (ImageButton) findViewById(R.id.bt_sellBoard_orderList);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OrderManagementActivity.class);
                startActivity(intent);
            }
        });

        logoBtn = (ImageButton) findViewById(R.id.bt_sellBoard_market);
        logoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManagingPostActivity.class);
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
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

