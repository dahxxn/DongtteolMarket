package com.example.dongtteolmarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    EditText ed_email, ed_pw;
    TextView bt_login;
    TextView bt_join;
    String state_check, customerID, sellerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        bt_login = (TextView) findViewById(R.id.bt_login_login);
        bt_join = (TextView) findViewById(R.id.bt_login_join);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_email = (EditText) findViewById(R.id.ed_login_email);
                ed_pw = (EditText) findViewById(R.id.ed_login_pw);

                String email = ed_email.getText().toString();
                String pw = ed_pw.getText().toString();

                mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            //구매자 , 판매자 구별해서 메인 화면 연결
                            db.collection("User")
                                    .whereEqualTo("userID", user.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.getData().get("type").equals("customer")) {
                                                    Intent intent = new Intent(LoginActivity.this, SellBoardActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Intent intent = new Intent(LoginActivity.this, ManagingPostActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
//                                                Toast.makeText(LoginActivity.this, "환영합니다", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });


                        } else {
                            Toast.makeText(LoginActivity.this, "이메일/비밀번호를 확인해 주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        bt_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinSelectActivity.class);
                startActivity(intent);
            }
        });

    }

}