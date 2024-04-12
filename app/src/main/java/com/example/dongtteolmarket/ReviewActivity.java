package com.example.dongtteolmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ReviewActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int star_cnt = 0; //누른 버튼 번호
    ImageView Img1, Img2, Img3, Img4, Img5;
    TextView title;
    private String content, reviewID, BID, CID, SID, user_type, writer;
    private int sum, cnt;
    private double result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기

        Img1 = (ImageView) findViewById(R.id.star1);
        Img2 = (ImageView) findViewById(R.id.star2);
        Img3 = (ImageView) findViewById(R.id.star3);
        Img4 = (ImageView) findViewById(R.id.star4);
        Img5 = (ImageView) findViewById(R.id.star5);

        findViewById(R.id.ok_button).setOnClickListener(onClickListener);

        findViewById(R.id.star1).setOnClickListener(onClickListener);
        findViewById(R.id.star2).setOnClickListener(onClickListener);
        findViewById(R.id.star3).setOnClickListener(onClickListener);
        findViewById(R.id.star4).setOnClickListener(onClickListener);
        findViewById(R.id.star5).setOnClickListener(onClickListener);

        user_type = getIntent().getExtras().getString("type");
        title=(TextView) findViewById(R.id.review_title);

        if(user_type.equals("판매자")){
            title.setText("거래하신 구매자는 어떠셨나요?");
        }

    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:{
                if(user_type.equals("판매자")){
                    Intent intent = new Intent(getApplicationContext(), OrderManagementActivity.class);
                    startActivity(intent);
                }
                else if(user_type.equals("구매자")){
                    Intent intent = new Intent(getApplicationContext(), OrderListActivity.class);
                    startActivity(intent);
                }

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            //별 색깔 바꾸기
            case R.id.star1:
                star_cnt = 1;
                Img1.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
//                startToast("별점1");
                break;
            case R.id.star2:
                star_cnt = 2;
                Img1.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img2.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
//                startToast("별점2");
                break;
            case R.id.star3:
                star_cnt = 3;
                Img1.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img2.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img3.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
//                startToast("별점3");
                break;
            case R.id.star4:
                star_cnt = 4;

                Img1.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img2.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img3.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img4.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);

//                startToast("별점4");
                break;
            case R.id.star5:
                star_cnt = 5;

                Img1.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img2.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img3.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img4.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img5.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
//                startToast("별점5");
                break;
            case R.id.ok_button:
                review();
                if(user_type.equals("판매자")){
                    Intent intent = new Intent(getApplicationContext(), OrderManagementActivity.class);
                    startActivity(intent);
                }
                else if(user_type.equals("구매자")){
                    Intent intent = new Intent(getApplicationContext(), OrderListActivity.class);
                    startActivity(intent);
                }
                break;
        }
    };

    private void review() {
        content = ((EditText) findViewById(R.id.content_edit)).getText().toString();
        if (content.length() > 0 && star_cnt > 0) {
            FirebaseUser user = mAuth.getCurrentUser();
            DocumentReference addedDocRef = db.collection("Review").document();

            BID = getIntent().getExtras().getString("boardID");
            CID = getIntent().getExtras().getString("customerID");
            SID = getIntent().getExtras().getString("sellerID");

            reviewID=addedDocRef.getId();

            if(user_type.equals("구매자")){

                Review review_info = new Review(BID,content,CID, reviewID, SID, star_cnt,"구매자");
                db.collection("Review").document(reviewID)
                        .set(review_info)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

//                                startToast("구매자 리뷰등록 성공");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {}
                        });

                //구매자  -> 판매자 별점 update
                star_update(SID);
            }
            else if(user_type.equals("판매자")){

                Review review_info = new Review(BID,content, CID, reviewID, SID, star_cnt,"판매자");
                db.collection("Review").document(reviewID)
                        .set(review_info)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                startToast("판매자 리뷰등록 성공");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {}
                        });

                //판매자 -> 구매자 별점 update
                star_update(CID);

            }
        }}

    private void star_update(String id){
        String fname = null;
        if(user_type.equals("구매자")){
            fname="sellerID";
        }
        else if(user_type.equals("판매자")){
            fname="customerID";
        }

        db.collection("Review")
                .whereEqualTo(fname, id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                writer = document.getData().get("writer").toString();

                                if(user_type.equals("판매자") && writer.equals("판매자")){
                                    sum += Integer.valueOf(document.getData().get("star").toString());
                                    cnt++;

                                }
                                else if(user_type.equals("구매자") && writer.equals("구매자")){
                                    sum += Integer.valueOf(document.getData().get("star").toString());
                                    cnt++;

                                }

                                //총 별점..
                                if (cnt > 0) {
                                    result = (double)sum /(double) cnt;
                                    if(result<=(double)1 && result>(double)0){
                                        //탈퇴 db에서 정보 삭제, 무섭다..
                                        DocumentReference washingtonRef = db.collection("User").document(id);
                                        washingtonRef.delete();

                                        startToast("탈퇴 완");
//                                        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
//                                        startActivity(intent);
                                    }
                                } else { result= sum; }

                            }

                        }

                    }
                });

        db.collection("User")
                .whereEqualTo("userID", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference washingtonRef = db.collection("User").document(id);
                                washingtonRef
                                        .update("star", Double.parseDouble(String.format("%.1f", result)))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
//                                                startToast("별점 update 성공");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {}
                                        });
                            }
                        } else {
                            if (content.length() == 0) {
                                startToast("한글자 이상 작성해주세요");
                            } else if (star_cnt == 0) {
                                startToast("별점을 한개 이상 남겨주세요");
                            }

                        }
                    }
                });
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}