package com.example.dongtteolmarket;




import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Review2 extends LinearLayout {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private DatabaseReference mDatabase;
    private String sellerID,Type, RID, SID, BID ,CID, star, content, writer;
    ImageView Img1, Img2, Img3, Img4, Img5;
    private ArrayList<String> idarr=new ArrayList<>();

    public Review2(Context context){
        super(context);
        init(context);
    }

    public Review2(){
        super(null);
    }
    public Review2(Context context, String type, AttributeSet attrs,String reviewID,String boardID, String sellerID){
        super(context, attrs);
        this.Type=type;
        this.RID=reviewID;
        this.SID=sellerID;
        this.BID=boardID;
        init(context);
    }
    public Review2(Context context,String type,String reviewID,String boardID,String sellerID){
        super(context);
        this.Type=type;
        this.RID=reviewID;
        this.SID=sellerID;
        this.BID=boardID;
        init(context);
    }
    private void init(Context context){

        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.review2,this,true);


        if(RID!=null){
            db.collection("Review")
                    .whereEqualTo("reviewID",RID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot dc : task.getResult()) {

                                    SID = dc.getData().get("sellerID").toString();
                                    CID = dc.getData().get("customerID").toString();
                                    star = dc.getData().get("star").toString();
                                    content = dc.getData().get("content").toString();

                                    //고객이름
                                    if(Type.equals("판매자")){
                                        user_review(SID);
                                    }
                                    else if(Type.equals("구매자")){
                                        user_review(CID);
                                    }
                                    if(star!=null){
                                        star_icon(star);
                                    }

                                }
                            }
                        }
                    });

        }
        else{
            startToast("리뷰없음");
        }


    }
    private void star_icon(String star){
        Img1 = (ImageView) findViewById(R.id.star1);
        Img2 = (ImageView) findViewById(R.id.star2);
        Img3 = (ImageView) findViewById(R.id.star3);
        Img4 = (ImageView) findViewById(R.id.star4);
        Img5 = (ImageView) findViewById(R.id.star5);

        switch (star.charAt(0)) {
            //별 색깔 바꾸기
            case '1':
                Img1.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
//                startToast("별점1");
                break;
            case '2':
                Img1.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img2.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
//                startToast("별점2");
                break;
            case '3':
                Img1.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img2.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img3.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
//                startToast("별점3");
                break;
            case '4':
                Img1.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img2.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img3.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img4.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);

//                startToast("별점4");
                break;
            case '5':
                Img1.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img2.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img3.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img4.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
                Img5.setColorFilter(Color.parseColor("#DB5531"), PorterDuff.Mode.SRC_IN);
//                startToast("별점5");
                break;

        }
    }
    private void user_review(String id){

//        TextView score = (TextView) findViewById(R.id.star);
//        score.setText(star);

        TextView review_content = (TextView) findViewById(R.id.content);
        review_content.setText(content);

        db.collection("User")
                .whereEqualTo("userID", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot dc1 : task.getResult()) {

                            TextView customer = (TextView) findViewById(R.id.customerName);
                            customer.setText(dc1.getData().get("userName").toString());

                            break;
                        }
                    }
                });

    }
    private void startToast(String msg) {
        Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
