package com.example.dongtteolmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReserveActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private String sellerID, boardID,orderID;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("image");
    private FirebaseUser user = mAuth.getCurrentUser();
    String BID, photo, time1;
    ImageView img;
    TextView pickuptime;
    Dialog timepickDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserve);
        findViewById(R.id.ok_button).setOnClickListener(onClickListener);
        BID = getIntent().getExtras().getString("pre_id");
        sellerID = getIntent().getExtras().getString("sellerID");
        img = (ImageView) findViewById(R.id.photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db.collection("Board")
                .whereEqualTo("boardID", BID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                photo = document.getData().get("photo").toString();
                                StorageReference pathReference = storageReference.child(photo);

                                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(ReserveActivity.this).load(uri).into(img);
                                    }
                                });

                                TextView boardName = (TextView) findViewById(R.id.boardName);
                                boardName.setText(document.getData().get("boardName").toString());

                                TextView starttime = (TextView) findViewById(R.id.startTime_text);
                                starttime.setText(document.getData().get("startTime").toString());

                                TextView endtime = (TextView) findViewById(R.id.endTime_text);
                                endtime.setText(document.getData().get("endTime").toString());


                            }
                        }
                    }
                });

        db.collection("User")
                .whereEqualTo("userID", sellerID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for(QueryDocumentSnapshot document : task.getResult()){
                                TextView sellername = (TextView) findViewById(R.id.seller_name_text);
                                sellername.setText(document.getData().get("userName").toString());
                            }
                        }
                    }
                });

        timepickDialog = new Dialog(ReserveActivity.this);
        timepickDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timepickDialog.setCancelable(true);
        timepickDialog.setContentView(R.layout.timepicker);

        pickuptime = (TextView) findViewById(R.id.pickuptime_edit);

        pickuptime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                timepickDialog.show();
                Button registerBtn = (Button) timepickDialog.findViewById(R.id.registerBtn);
                TimePicker timep1 = timepickDialog.findViewById(R.id.time1);

                timep1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                        time1 = String.format("%02d", hourOfDay) + " : " + String.format("%02d", minute);
                        pickuptime.setText(time1);

                    }
                });


                registerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickuptime.setText(time1);
                        timepickDialog.dismiss();
                    }

                });
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
    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.ok_button:
                reserve();
                break;
        }
    };

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:{

                Intent intent = new Intent(getApplicationContext(), SubBoardDialogActivity.class);
                startActivity(intent);

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void reserve() {
        String pickuptime1=((TextView) findViewById(R.id.pickuptime_edit)).getText().toString();
        String callNum=((EditText)findViewById(R.id.callnum_edit)).getText().toString();
        String count=((EditText)findViewById(R.id.count_edit)).getText().toString();
        String request=((EditText)findViewById(R.id.request_edit)).getText().toString();


        if ( callNum.length() > 0 && count.length() > 0) {
            FirebaseUser user = mAuth.getCurrentUser();

            sellerID= getIntent().getExtras().getString("sellerID");
            boardID= getIntent().getExtras().getString("pre_id");

            DocumentReference addedDocRef = db.collection("OrderList").document();

            orderID=addedDocRef.getId();
            OrderList orderinfo=new OrderList(boardID,callNum, count, user.getUid(),orderID, pickuptime1, request, sellerID, "주문완료");

            //boardID
            db.collection("OrderList").document(orderID)

                    .set(orderinfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startToast("예약 성공");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            startToast("실패");
                        }
                    });
        }
        else { //정보 입력 전부 안됨
            if(callNum.length()==0){
                startToast("연락처를 적어주세요");
            }
            else if(count.length()==0){
                startToast("수량을 적어주세요");
            }
        }
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}