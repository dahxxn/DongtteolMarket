package com.example.dongtteolmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SellBoardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    ImageButton orderBtn;
    ImageButton logoBtn;
    ImageButton mypageBtn;

    TextView userName;
    TextView userLoc;

    ArrayList<String> boardList = new ArrayList<>();

    public String location;
    public GridLayout lin_lyt;

    String state_check, customerID, sellerID, success, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sell_board);

        lin_lyt = (GridLayout) findViewById(R.id.ly_sellBoard_list);

        //현재 사용자 위치 파악 후 게시글 정보 저장
        db.collection("User")
                .whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //위치 파악
                                location = document.getData().get("location").toString();

                                //레이아웃 설정
//                                userName = (TextView) findViewById(R.id.tv_sellBoard_userName);
//                                userName.setText(document.getData().get("userName").toString());
                                userType = document.getData().get("type").toString();
                                userLoc = (TextView) findViewById(R.id.tv_sellBoard_location);
                                userLoc.setText(location);
                            }


                            //게시판 생성 : 지역, 시간의 조건에 맞는 게시물만 뜨도록
                            db.collection("Board")
                                    .whereEqualTo("location", location) //같은 지역구인 게시글만 보여주도록 조건 설정
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    boardList.add(document.getData().get("boardID").toString());

                                                    //판매 시작시간, 마감시간에 따라 게시글 목록 보여주도록 하는 코드
                                                    long now = System.currentTimeMillis();
                                                    Date date = new Date(now);
                                                    SimpleDateFormat now_time = new SimpleDateFormat(("k:mm"));

                                                    String now_time_string = now_time.format(date); //현재 시간
                                                    String start_time = document.getData().get("startTime").toString(); //시작시간
                                                    String end_time = document.getData().get("endTime").toString(); //마감 시간

                                                    SimpleDateFormat time_format = new SimpleDateFormat("k:mm");

                                                    try {
                                                        Date end_date = time_format.parse(end_time);
                                                        Date now_date = time_format.parse(now_time_string);
                                                        Date start_date = time_format.parse(start_time);

                                                        if (end_date.after(now_date)) { //마감시간 전인 게시물만
                                                            //시작시간이 지난 게시물만
                                                            if (start_date.before(now_date)) {
                                                                SubBoard n_layout = new SubBoard(getApplicationContext(), location, document.getData().get("boardID").toString());
                                                                lin_lyt.addView(n_layout);
                                                            }
                                                        } else {
                                                            continue;
                                                        }

                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                            }
                                        }
                                    });
                        }
                    }
                });




        //하단 메뉴 바 버튼 클릭 이벤트 추가
        orderBtn = (ImageButton) findViewById(R.id.bt_sellBoard_orderList);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equals("customer")) {
                    Intent intent = new Intent(getApplicationContext(), OrderListActivity.class);
                    startActivity(intent);
                } else if (userType.equals("seller")) {
                    Intent intent = new Intent(getApplicationContext(), OrderManagementActivity.class);
                    startActivity(intent);
                }
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

    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }

    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}