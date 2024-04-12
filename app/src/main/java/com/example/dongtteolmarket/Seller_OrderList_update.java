package com.example.dongtteolmarket;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
//import android.widget.Toolbar;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.Calendar;

public class Seller_OrderList_update extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("image");
    String photo, BID,pre_name, pre_price, pre_count, pre_note, pre_starttime, pre_endtime;
    Uri uri;
    ImageView img;
    Dialog timepickDialog;
    int ho1, ho2;
    String time1, time2, time = "";
    TextView startTime, endTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_list_update);
        BID = getIntent().getExtras().getString("pre_id");
        img = (ImageView) findViewById(R.id.photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //게시판 사진 보여줌
        // 제목, 수량 등을 hint settext 여기서 해야됨!!!!!!!!! 추가해야됨!!!!!!!!!
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
                                        Glide.with(Seller_OrderList_update.this).load(uri).into(img);
                                    }
                                });

                                EditText name = (EditText) findViewById(R.id.name_edit);
                                name.setText(document.getData().get("boardName").toString());

                                EditText price = (EditText) findViewById(R.id.price_edit);
                                price.setText(document.getData().get("cost").toString());

                                EditText count = (EditText) findViewById(R.id.count_edit);
                                count.setText(document.getData().get("count").toString());

                                EditText note = (EditText) findViewById(R.id.note_edit);
                                note.setText(document.getData().get("note").toString());

                                TextView starttime = (TextView) findViewById(R.id.startTime_edit);
                                starttime.setText(document.getData().get("startTime").toString());

                                TextView endtime = (TextView) findViewById(R.id.endTime_edit);
                                endtime.setText(document.getData().get("endTime").toString());

                            }
                        }
                    }
                });


        findViewById(R.id.ok_button).setOnClickListener(onClickListener);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

                startActivityForResult(intent, 1);


            }
        });

        timepickDialog = new Dialog(Seller_OrderList_update.this);
        timepickDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timepickDialog.setCancelable(true);
        timepickDialog.setContentView(R.layout.timepicker);

        startTime = (TextView) findViewById(R.id.startTime_edit);

        startTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                timepickDialog.show();
                Button registerBtn = (Button) timepickDialog.findViewById(R.id.registerBtn);
                TimePicker timep1 = timepickDialog.findViewById(R.id.time1);

                timep1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        ho1 = hourOfDay;

                        time1 = String.format("%02d", hourOfDay) + " : " + String.format("%02d", minute);
                        startTime.setText(time1);

                    }
                });


                registerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startTime.setText(time1);
                        timepickDialog.dismiss();
                    }
                });
            }
        });

        endTime = (TextView) findViewById(R.id.endTime_edit);

        endTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                timepickDialog.show();
                Button registerBtn = (Button) timepickDialog.findViewById(R.id.registerBtn);
                TimePicker timep1 = timepickDialog.findViewById(R.id.time1);

                timep1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        ho1 = hourOfDay;

                        time1 = String.format("%02d", hourOfDay) + " : " + String.format("%02d", minute);
                        endTime.setText(time1);

                    }
                });

                registerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:{

                Intent intent = new Intent(getApplicationContext(), ManagingPostActivity.class);
                startActivity(intent);

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener onClickListener = (v) -> {

        final Calendar c = Calendar.getInstance();

        switch (v.getId()) {
            case R.id.ok_button:
                board_update();
                if(uri != null){
                    photo_update(uri);
                }
                Intent intent=new Intent(getApplicationContext(),ManagingPostActivity.class);
                startActivity(intent);
                break;
        }
    };


    private void board_update(){
//        starttime_edit = (TextView) findViewById(R.id.starttime_edit);



        String name=((EditText)findViewById(R.id.name_edit)).getText().toString();
        String count=((EditText)findViewById(R.id.count_edit)).getText().toString();
        String note=((EditText)findViewById(R.id.note_edit)).getText().toString();
        String startTime = ((TextView)findViewById(R.id.startTime_edit)).getText().toString();
        String price=((EditText)findViewById(R.id.price_edit)).getText().toString();

        if (name.length() > 0 && count.length() > 0 && price.length() > 0) {

            db.collection("Board")
                    .whereEqualTo("boardID", BID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    pre_name = document.getData().get("boardName").toString();
                                    pre_price = document.getData().get("cost").toString();
                                    pre_count = document.getData().get("count").toString();
                                    pre_note = document.getData().get("note").toString();
                                    pre_starttime = document.getData().get("startTime").toString();
                                    pre_endtime = document.getData().get("endTime").toString();

                                    DocumentReference washingtonRef = db.collection("Board").document(BID);

                                    //걍 insert 하면 안되낭.. 이름 바꼈을때..
                                    if(!pre_name.equals(name)) {
                                        washingtonRef
                                                .update("boardName", name)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
//                                                        startToast("이름 update 성공");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
//                                                        startToast("실패");
                                                    }
                                                });
                                    }
                                    if(!pre_price.equals(price)) {
                                        washingtonRef
                                                .update("cost",price)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
//                                                        startToast("가격 update 성공");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
//                                                        startToast("실패");
                                                    }
                                                });
                                    }
                                    if(!pre_count.equals(count)) {
                                        washingtonRef
                                                .update("count", count)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
//                                                        startToast("수량 update 성공");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
//                                                        startToast("실패");
                                                    }
                                                });
                                    }
                                    if(!pre_note.equals(note)) {
                                        washingtonRef
                                                .update("note", note)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
//                                                        startToast("설명 update 성공");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
//                                                        startToast("실패");
                                                    }
                                                });
                                    }
                                    if(!pre_starttime.equals(startTime)) {
                                        washingtonRef
                                                .update("startTime", startTime)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) { }
                                                });
                                    }
                                    if(!pre_endtime.equals(endTime)) {
                                        washingtonRef
                                                .update("endTime", endTime)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    });


        }
        else { //정보 입력 전부 안됨
            if(name.length()==0){
                startToast("상품이름을 적어주세요");
            }
            else if(count.length()==0){
                startToast("수량을 적어주세요");
            }
            else if(price.length()==0){
                startToast("가격을 적어주세요");
            }
        }

    }
    private void photo_update(Uri uri){

        StorageReference pathReference = storageReference.child(photo);
        img=(ImageView) findViewById(R.id.photo);
        pathReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Model model = new Model(uri.toString());
                        String modelid = root.push().getKey();
                        root.child(modelid).setValue(model);
                        startToast("사진 업데이트 성공!");
                        String profileImage =photo;

                        db.collection("Board").document(user.getUid())
                                .update("photo", profileImage);

                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Seller_OrderList_update.this, "업로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    //갤러리에서 사진가져오기
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    img=(ImageView) findViewById(R.id.photo);
                    img.setImageURI(uri);
                }
                break;
        }
    }
}
