package com.example.dongtteolmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Store_review extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    public LinearLayout list;
    ArrayList<String> reviewList = new ArrayList<>();

    String SID, CID, BID, store_name, Type, writer, result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_review);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기

        list = (LinearLayout) findViewById(R.id.ly_review_list);
        //subBoard에서 -> 판매자 리뷰보기, OrderManagement에서 -> 구매자리뷰보기
        Type = getIntent().getExtras().getString("type");
        SID = getIntent().getExtras().getString("sellerID");
        BID = getIntent().getExtras().getString("boardID");
        CID = getIntent().getExtras().getString("customerID");

        ImageView type_icon = (ImageView) findViewById(R.id.type_icon);

        if (Type.equals("판매자리뷰보기")) {
            type_review(SID, "구매자");
        } else if (Type.equals("구매자리뷰보기")) {
            type_icon.setImageResource(R.drawable.customer);
            type_review(CID, "판매자");
        }

    }

    private void type_review(String id, String user_type) {
        String fname = null;
        if (user_type.equals("구매자")) {
            fname = "sellerID";
        } else if (user_type.equals("판매자")) {
            fname = "customerID";
        }

        db.collection("User")
                .whereEqualTo("userID", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //위치 파악
                                store_name = document.getData().get("userName").toString();
                                result = document.getData().get("star").toString();

                                TextView review_store_name = (TextView) findViewById(R.id.review_store);
                                review_store_name.setText(store_name);

                                //메달색 변경
                                ImageView medal = (ImageView) findViewById(R.id.icon);
                                if (Double.parseDouble(result) >= 4.0) {
                                    medal.setImageResource(R.drawable.medal);
                                } else if (Double.parseDouble(result) >= 3.0) {
                                    medal.setImageResource(R.drawable.medal2);
                                } else {
                                    medal.setImageResource(R.drawable.medal3);
                                }

                                //점수
                                TextView avg_score = (TextView) findViewById(R.id.avg_score);
                                if (result.equals("0")) {
                                    avg_score.setText("-");
                                } else avg_score.setText(result);

                            }

                        }
                    }
                });

        db.collection("Review")
                .whereEqualTo(fname, id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                writer = document.getData().get("writer").toString();


                                if (user_type.equals("판매자") && writer.equals("판매자")) {
                                    reviewList.add(document.getData().get("customerID").toString());

                                    try {
                                        Review2 n_layout = new Review2(getApplicationContext(), user_type, document.getData().get("reviewID").toString(), document.getData().get("boardID").toString(), document.getData().get("sellerID").toString());
                                        list.addView(n_layout);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (user_type.equals("구매자") && writer.equals("구매자")) {
                                    reviewList.add(document.getData().get("sellerID").toString());

                                    try {
                                        Review2 n_layout = new Review2(getApplicationContext(), user_type, document.getData().get("reviewID").toString(), document.getData().get("boardID").toString(), document.getData().get("sellerID").toString());
                                        list.addView(n_layout);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                        }

                    }
                });

    }

    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
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