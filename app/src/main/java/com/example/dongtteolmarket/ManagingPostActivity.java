package com.example.dongtteolmarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ManagingPostActivity extends AppCompatActivity {
    private TextView sellerName, starText;
//    private String sellerName, starText;

    public String boardID;
    public GridLayout boardView;

    ImageButton orderBtn;
    ImageButton logoBtn;
    ImageButton mypageBtn;
    Button postBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<String> boardList = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managing_post);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        boardView = (GridLayout) findViewById(R.id.sellerBoard_list);

        // 가게명, 별점 db에서 가져와서 나타내기
        db.collection("User")
                .whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sellerName = (TextView) findViewById(R.id.seller_name);
                                sellerName.setText(document.getData().get("userName").toString());

                                starText = (TextView) findViewById(R.id.star);
                                starText.setText(document.getData().get("star").toString());

                                starText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), Store_review.class);
                                        intent.putExtra("sellerID", user.getUid());
                                        intent.putExtra("boardID", boardID);
                                        intent.putExtra("type","판매자리뷰보기");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }

                                });
                            }
                        }
                    }
                });

        db.collection("Board")
                .whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                boardList.add(document.getData().get("boardID").toString());

                                BoardView layout = new BoardView(getApplicationContext(), user.getUid(), document.getData().get("boardID").toString());
                                boardView.addView(layout);
                            }
                        }
                    }
                });

        // 판매중과 판매완료된 게시글 따로 보이게 하는 것

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

        postBtn = (Button) findViewById(R.id.bt_sellBoard_post);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WritingPostActivity.class);
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
}
