package com.example.dongtteolmarket;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class JoinCustomerActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button button1;
    private TextView txtResult;
    String area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_customer);

        findViewById(R.id.bt_join_button).setOnClickListener(onClickListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        button1 = (Button) findViewById(R.id.button1);
        txtResult = (TextView) findViewById(R.id.txtResult);


        // 위치 관리자 객체 참조하기
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(JoinCustomerActivity.this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                } else {
                    // 가장최근 위치정보 가져오기
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        String provider = location.getProvider();
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        double altitude = location.getAltitude();
                        String address = getCurrentAddress(latitude, longitude);

                        txtResult.setText(address);
                        button1.setVisibility(View.GONE);
                        startToast(area+"의 상품들을 확인할 수 있어요!");
                    }
                }
            }
        });

    }

    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        area = address.getAdminArea().split("광역시")[0] + " " + address.getSubLocality();
        return address.getAddressLine(0).toString() + "\n";

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.bt_join_button:
                signUp();
                break;
        }
    };

    private void signUp() {
        String email = ((EditText) findViewById(R.id.ed_join_id)).getText().toString();
        String password = ((EditText) findViewById(R.id.ed_join_pw)).getText().toString();
        String password_check = ((EditText) findViewById(R.id.ed_join_pw_check)).getText().toString();
        String userName = ((EditText) findViewById(R.id.ed_join_nickname)).getText().toString();
//        String area = ((EditText) findViewById(R.id.ed_join_area)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && password_check.length() > 0 && area.length() > 0 && userName.length() > 0) { //정보 전부 입력됐는지 확인
            if (password.equals(password_check)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, (task) -> {
                            if (task.isSuccessful()) { //Authentication에 등록 성공
                                FirebaseUser user = mAuth.getCurrentUser();

                                User userInfo = null;
                                userInfo = new User(user.getUid(), email, password, userName, area, "customer", 0);

                                //firestore에 User database 저장---
                                db.collection("User").document(user.getUid())
                                        .set(userInfo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                startToast("회원가입에 성공하였습니다!");
                                                Intent intent = new Intent(JoinCustomerActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                startToast("erro: add User document");
                                            }
                                        });
                            } else { ////Authentication에 등록 실패
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                }
                            }
                        });
            } else {
                startToast("비밀번호를 다시 확인해 주세요");
            }
        } else { //정보 입력 전부 안됨
            startToast("회원 정보를 모두 입력해 주세요");
        }
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
