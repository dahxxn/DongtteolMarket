package com.example.dongtteolmarket;

import static android.widget.Toast.makeText;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MypageEditActivity extends AppCompatActivity {
    Dialog editName_dialog, editPW_dialog, editArea_dialog;
    LinearLayout editName, editPW, editArea;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ImageButton orderBtn;
    ImageButton logoBtn;
    ImageButton mypageBtn;

    String userType;
    TextView userTitle, userName, userEmail, userPW, userArea;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        setContentView(R.layout.mypage_edit);

        //이전버튼
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db.collection("User")
                .whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                userType = document.getData().get("type").toString();

                                ImageView userProfile = (ImageView) findViewById(R.id.iv_mypageEdit_profile);
                                TextView tv_name ;

                                switch (userType){
                                    case "customer":
                                        tv_name = (TextView)findViewById(R.id.tv_mypageEdit_name);
                                        tv_name.setText("이름");
                                        userProfile.setImageResource(R.drawable.customer);
                                        break;
                                    case "seller":
                                        tv_name = (TextView)findViewById(R.id.tv_mypageEdit_name);
                                        tv_name.setText("상점명");
                                        userProfile.setImageResource(R.drawable.seller);
                                        break;

                                }

                                editName = (LinearLayout)findViewById(R.id.bt_Editname);
                                editName_dialog = new Dialog(MypageEditActivity.this);
                                editName_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                editName_dialog.setContentView(R.layout.mypage_edit_name);

                                editName.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        editName_dialog.show();

                                        TextView dialog_title = editName_dialog.findViewById(R.id.tv_editName_title);
                                        TextView pre_name = editName_dialog.findViewById(R.id.tv_editName_origin_nickname);

                                        switch(userType){
                                            case "customer":
                                                dialog_title.setText("이름 변경");
                                                pre_name.setText("원래 닉네임 : "+document.getData().get("userName").toString());
                                                break;

                                            case "seller":
                                                dialog_title.setText("상점명 변경");
                                                pre_name.setText("원래 상점명 : "+document.getData().get("userName").toString());
                                                break;
                                        }

                                        //닉네임 변경 팝업창
                                        Button changeBtn = editName_dialog.findViewById(R.id.bt_editName_change);
                                        changeBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                String change_name = ((EditText)editName_dialog.findViewById(R.id.et_editName_name1)).getText().toString();
                                                DocumentReference washingtonRef = db.collection("User").document(user.getUid());

                                                if(!change_name.equals(userName.toString())){
                                                    if(!change_name.equals("")){
                                                        washingtonRef
                                                                .update("userName",change_name)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        makeText(getApplicationContext(), "변경 되었습니다!", Toast.LENGTH_SHORT).show();
                                                                        editName_dialog.dismiss();
                                                                        Intent intent = new Intent(getApplicationContext(), MypageEditActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                });
                                                    }else{
                                                        makeText(getApplicationContext(), "이름을 입력해주세요!", Toast.LENGTH_SHORT).show();
                                                    }

                                                }else{
                                                    makeText(getApplicationContext(), "이전 명과 동일합니다!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                });


                                editPW = (LinearLayout)findViewById(R.id.bt_editPW);
                                editPW_dialog = new Dialog(MypageEditActivity.this);
                                editPW_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                editPW_dialog.setContentView(R.layout.mypage_edit_pw);

                                editPW.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        editPW_dialog.show();

                                        //비밀번호 변경 팝업창
                                        Button changeBtn = editPW_dialog.findViewById(R.id.bt_editPW_change);
                                        changeBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String change_pw1 = ((EditText)editPW_dialog.findViewById(R.id.et_editPW_pw1)).getText().toString();
                                                String change_pw2 = ((EditText)editPW_dialog.findViewById(R.id.et_editPW_pw2)).getText().toString();
                                                DocumentReference washingtonRef = db.collection("User").document(user.getUid());

                                                if(change_pw1.equals(change_pw2)){
                                                    if(change_pw1.equals("") || change_pw2.equals("")){
                                                        makeText(getApplicationContext(), "비밀번호를 입력해주세요!", Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        washingtonRef
                                                                .update("password",change_pw2)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        makeText(getApplicationContext(), "변경 되었습니다!", Toast.LENGTH_SHORT).show();
                                                                        editPW_dialog.dismiss();
                                                                        Intent intent = new Intent(getApplicationContext(), MypageEditActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                });
                                                    }

                                                }else{
                                                    makeText(getApplicationContext(), "비밀번호가 동일하지 않습니다!", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

                                    }
                                });


                                editArea = (LinearLayout)findViewById(R.id.bt_editArea);
                                editArea_dialog = new Dialog(MypageEditActivity.this);
                                editArea_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                editArea_dialog.setContentView(R.layout.mypage_edit_area);

                                editArea.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        editArea_dialog.show();

                                        //지역 변경 팝업창
                                        Button changeBtn = editArea_dialog.findViewById(R.id.bt_editArea_change);
                                        changeBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                String change_area = ((EditText)editArea_dialog.findViewById(R.id.et_editArea_area)).getText().toString();
                                                DocumentReference washingtonRef = db.collection("User").document(user.getUid());

                                                if(change_area.equals("")){
                                                    makeText(getApplicationContext(), "지역을 입력해주세요!", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    washingtonRef
                                                            .update("location",change_area)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    makeText(getApplicationContext(), "변경 되었습니다!", Toast.LENGTH_SHORT).show();
                                                                    editArea_dialog.dismiss();
                                                                    Intent intent = new Intent(getApplicationContext(), MypageEditActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                }


                                            }
                                        });

                                    }
                                });

                                userName=(TextView) findViewById(R.id.tv_mypageEdit_nickname);
                                userEmail=(TextView) findViewById(R.id.tv_mypageEdit_email);
                                userPW=(TextView) findViewById(R.id.tv_mypageEdit_pw);
                                userArea=(TextView) findViewById(R.id.tv_mypageEdit_area);

                                String pw_lock = "";

                                for(int i = 0 ; i<document.getData().get("password").toString().length(); i++){
                                    pw_lock += "*";
                                }

                                userName.setText(document.getData().get("userName").toString());
                                userEmail.setText(document.getData().get("email").toString());
                                userPW.setText(pw_lock);
                                userArea.setText(document.getData().get("location").toString());
                            }
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
                if(userType.equals("seller")){
                    Intent intent = new Intent(getApplicationContext(), ManagingPostActivity.class);
                    startActivity(intent);
                }else if(userType.equals("customer")){
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

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:{

                Intent intent = new Intent(getApplicationContext(), MypageActivity.class);
                startActivity(intent);

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
