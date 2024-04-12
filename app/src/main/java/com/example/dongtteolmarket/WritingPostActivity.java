package com.example.dongtteolmarket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.DatabaseReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;

public class WritingPostActivity extends AppCompatActivity {
    private FirebaseStorage storage;
    private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("image");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();

    Uri uri;
    Button post;
    ImageButton photo_button;
    ImageView imageView;
    String photo, boardID;

    int ho1, ho2;
    Dialog timepickDialog;
    public String location;
    String time1, time2, time = "", st, et;

    TextView startTime, endTime;
    ImageButton orderBtn;
    ImageButton logoBtn;
    ImageButton mypageBtn;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writing_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        timepickDialog = new Dialog(WritingPostActivity.this);
        timepickDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        timepickDialog.setCancelable(true);
        timepickDialog.setContentView(R.layout.timepicker);

        startTime = (TextView) findViewById(R.id.startTime_edit);

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timepickDialog.show();
                Button registerBtn = (Button) timepickDialog.findViewById(R.id.registerBtn);
                TimePicker timep1 = timepickDialog.findViewById(R.id.time1);

                timep1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        ho1 = hourOfDay;

                        time1 = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
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

        endTime.setClickable(true);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timepickDialog.show();
                Button registerBtn = (Button) timepickDialog.findViewById(R.id.registerBtn);
                TimePicker timep1 = timepickDialog.findViewById(R.id.time1);

                timep1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        ho1 = hourOfDay;

                        time1 = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
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

        imageView = findViewById(R.id.image);
        post = (Button) findViewById(R.id.post);
        storage = FirebaseStorage.getInstance();

        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityResult.launch(intent);
            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    String boardName = ((EditText) findViewById(R.id.boardName)).getText().toString();
                    int count = Integer.parseInt(((EditText) findViewById(R.id.count)).getText().toString());
                    int cost = Integer.parseInt(((EditText) findViewById(R.id.cost)).getText().toString());
                    String note = ((EditText) findViewById(R.id.note)).getText().toString();

                    DocumentReference addedDocRef = db.collection("Board").document();
                    boardID = addedDocRef.getId();
                    LocalDate now;


                    StorageReference fileRef;

                    if (boardName.length() > 0 && count > 0 && cost > 0 && note.length() > 0 && note.length() > 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            now = LocalDate.now();

                            fileRef = reference.child(boardName + now.toString());

                            //사진 업로드
                            fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Model model = new Model(uri.toString());
                                    String modelid = root.push().getKey();
                                    root.child(modelid).setValue(model);
                                    photo = boardName + now.toString();
                                }
                            });

                            photo = boardName + now.toString();

                            db.collection("User")
                                    .whereEqualTo("userID", user.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    location = document.getData().get("location").toString();

                                                    st = startTime.getText().toString();
                                                    et = endTime.getText().toString();
                                                    Board board = new Board(user.getUid(), boardID, boardName, location, cost, count, note, st, et, 0, photo, now.toString());
                                                    db.collection("Board").document(boardID)
                                                            .set(board)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    startToast("게시되었습니다!");
                                                                    Intent intent = new Intent(getApplicationContext(), ManagingPostActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    startToast("실패!");
                                                                } //실패했을때
                                                            });
                                                }
                                            }
                                        }
                                    });

                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(WritingPostActivity.this, "항목을 모두 채워 주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        uri = result.getData().getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imageView.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void startToast(String msg) {
        Toast.makeText(WritingPostActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}
