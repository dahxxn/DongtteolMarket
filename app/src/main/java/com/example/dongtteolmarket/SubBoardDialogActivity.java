package com.example.dongtteolmarket;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class SubBoardDialogActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ImageButton orderBtn;
    ImageButton logoBtn;
    ImageButton mypageBtn;

    String userType,boardID, sellerID, pre_id;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        setContentView(R.layout.sub_board_dialog);

        //이전버튼
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        Intent intent = getIntent();
        boardID = intent.getStringExtra("boardID");


        db.collection("Board")
                .whereEqualTo("boardID",boardID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){

                                sellerID = document.getData().get("userID").toString();

                                //이미지
                                ImageView sub_img = (ImageView)findViewById(R.id.iv_subBoardDialog_img);
                                //이미지 가져오는 코드

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageReference = storage.getReference();
                                //board에 photo에 저장된 이미지 이름으로 경로 설정
                                StorageReference pathReference = storageReference.child(document.getData().get("photo").toString());
                                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(SubBoardDialogActivity.this).load(uri).into(sub_img);
                                    }
                                });

                                //그 외 게시글 정보
                                ImageView medal = (ImageView)findViewById(R.id.iv_subBoard_medal);
                                TextView sub_store = (TextView)findViewById(R.id.tv_subBoardDialog_storeName);
                                TextView sub_title = (TextView)findViewById(R.id.tv_subBoardDialog_name);
                                TextView sub_count = (TextView)findViewById(R.id.tv_subBoardDialog_count);
                                TextView sub_cost = (TextView)findViewById(R.id.tv_subBoardDialog_cost);
                                TextView sub_note = (TextView)findViewById(R.id.tv_subBoardDialog_note);
                                TextView sub_time = (TextView)findViewById(R.id.tv_subBoardDialog_time);


                                db.collection("User")
                                        .whereEqualTo("userID",document.getData().get("userID").toString())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    for(QueryDocumentSnapshot dc : task.getResult()){
                                                        sub_store.setText(dc.getData().get("userName").toString());
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


                                sub_title.setText(document.getData().get("boardName").toString());
                                sub_count.setText(document.getData().get("count").toString());
                                sub_cost.setText(document.getData().get("cost").toString() + "원");
                                sub_note.setText(document.getData().get("note").toString());
                                String time_str = document.getData().get("startTime").toString() + "~" + document.getData().get("endTime").toString();
                                sub_time.setText(time_str);

                                //픽업 예약 버튼
                                Button pickup_btn = (Button)findViewById(R.id.bt_subBoardDialog_reserve);
                                pickup_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(SubBoardDialogActivity.this, ReserveActivity.class);
                                        intent.putExtra("pre_id", boardID); //안되면 이거 pre_id로 바꾸기
                                        intent.putExtra("sellerID", sellerID);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        startActivity(intent);
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
                Intent intent = new Intent(getApplicationContext(), OrderListActivity.class);
                startActivity(intent);
//                if (userType.equals("customer")) {
//                    Intent intent = new Intent(getApplicationContext(), OrderListActivity.class);
//                    startActivity(intent);
//                } else if (userType.equals("seller")) {
//                    Intent intent = new Intent(getApplicationContext(), OrderManagementActivity.class);
//                    startActivity(intent);
//                }
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

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:{

                Intent intent = new Intent(getApplicationContext(), SellBoardActivity.class);
                startActivity(intent);

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
