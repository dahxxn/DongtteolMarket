package com.example.dongtteolmarket;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MypageActivity extends AppCompatActivity {
    ImageButton orderBtn;
    ImageButton logoBtn;
    ImageButton mypageBtn;

    LinearLayout editInfoBtn, logoutBtn, deleteBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String usertype;
    TextView userName,userType,userEmail,userLocation;
    ImageView userProfile, userMedal;

    Dialog logoutDialog,  deleteDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        setContentView(R.layout.mypage);


        db.collection("User")
                .whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                usertype = document.getData().get("type").toString();

                                userMedal = (ImageView) findViewById(R.id.iv_mypage_medal);
                                userProfile = (ImageView) findViewById(R.id.iv_mypage_profile);
                                userType = (TextView) findViewById(R.id.tv_mypage_type);

                                if(usertype.equals("customer")){
                                    userType.setText("구매자 계정");
                                    userProfile.setImageResource(R.drawable.customer);
                                }else{
                                    userType.setText("판매자 계정");
                                    userProfile.setImageResource(R.drawable.seller);

                                    String star = document.getData().get("star").toString();
                                    if (Double.parseDouble(star) >= 4.0) {
                                        userMedal.setImageResource(R.drawable.medal);
                                    } else if (Double.parseDouble(star) >= 3.0) {
                                        userMedal.setImageResource(R.drawable.medal2);
                                    } else {
                                        userMedal.setImageResource(R.drawable.medal3);
                                    }
                                }


                                userName = (TextView) findViewById(R.id.tv_mypage_name);
                                userName.setText(document.getData().get("userName").toString());


                                userEmail = (TextView) findViewById(R.id.tv_mypage_email);
                                userEmail.setText(document.getData().get("email").toString());

                                userLocation = (TextView) findViewById(R.id.tv_mypage_locaton);
                                userLocation.setText(document.getData().get("location").toString());

                            }
                        }

                    }
                });

        editInfoBtn = (LinearLayout) findViewById(R.id.bt_mypage_edit);
        editInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MypageEditActivity.class);
                startActivity(intent);

            }
        });

        logoutBtn = (LinearLayout) findViewById(R.id.bt_mypage_logout);
        logoutDialog = new Dialog(MypageActivity.this);
        logoutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logoutDialog.setContentView(R.layout.mypage_logout_dialog);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog.show();

                Button noBtn =  logoutDialog.findViewById(R.id.nobtn);
                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logoutDialog.dismiss();
                    }
                });

                Button yesBtn =  logoutDialog.findViewById(R.id.yesbtn);
                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        deleteBtn = (LinearLayout) findViewById(R.id.bt_mypage_delete);
        deleteDialog = new Dialog(MypageActivity.this);
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteDialog.setContentView(R.layout.mypage_delete_dialog);

        deleteBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                deleteDialog.show();;

                Button noBtn = deleteDialog.findViewById(R.id.nobtn);
                noBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDialog.dismiss();
                    }
                });

                Button yesBtn = deleteDialog.findViewById(R.id.yesbtn);
                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.collection("User").document(user.getUid())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //게시글 모두 삭제
                                                db.collection("Board")
                                                        .whereEqualTo("userID", user.getUid())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                ArrayList<String> boardList = new ArrayList<>();
                                                                for(QueryDocumentSnapshot document : task.getResult()){
                                                                    boardList.add(document.getId());
                                                                }

                                                                for(int i = 0 ; i<boardList.size() ; i++){
                                                                    db.collection("Board").document(boardList.get(i)).delete();
                                                                }
                                                            }
                                                        });

                                                //게시글 다 삭제후 다시 로그인 화면으로
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                });


                    }
                });
            }
        });

        //하단 메뉴 바 버튼 클릭 이벤트 추가
        orderBtn = (ImageButton) findViewById(R.id.bt_sellBoard_orderList);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usertype.equals("customer")) {
                    Intent intent = new Intent(getApplicationContext(), OrderListActivity.class);
                    startActivity(intent);
                } else if (usertype.equals("seller")) {
                    Intent intent = new Intent(getApplicationContext(), OrderManagementActivity.class);
                    startActivity(intent);
                }
            }
        });

        logoBtn = (ImageButton) findViewById(R.id.bt_sellBoard_market);
        logoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(usertype.equals("seller")){
                    Intent intent = new Intent(getApplicationContext(), ManagingPostActivity.class);
                    startActivity(intent);
                }else if(usertype.equals("customer")){
                    Intent intent = new Intent(getApplicationContext(), SellBoardActivity.class);
                    startActivity(intent);
                }
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
