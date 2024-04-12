package com.example.dongtteolmarket;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class JoinSellerActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    int flag = 0;
    EditText seller_num;
    TextView check_text;
    private Button button1;
    private TextView txtResult;
    String area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_seller);

        seller_num = findViewById(R.id.ed_seller_num);
        check_text = (TextView) findViewById(R.id.check_text);

        findViewById(R.id.bt_join_button).setOnClickListener(onClickListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button num_check = (Button) findViewById(R.id.seller_num_check);
        num_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String result = data(seller_num.getText().toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (result.contains("일반과세자")) {
                                        flag = 1;
                                        check_text.setText("사업자가 확인되었습니다.");
                                    } else {
                                        flag = 0;
                                        check_text.setText("다시 시도해 주세요.");
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        button1 = (Button) findViewById(R.id.button1);
        txtResult = (TextView) findViewById(R.id.txtResult);


        // 위치 관리자 객체 참조하기
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(JoinSellerActivity.this, new String[]{
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
                        startToast(area+"의 고객들과 거래할 수 있어요!");
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

    String data(String inputText) throws IOException {
        String apiUrl = "https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey=XFysiYAHvQrbx3TtzyAEcR9nrniEEa9aZcXVso/0wJb/Lk5txxJ1e8Y2fcMbZkf0wevXxQl3tQ6VqMnk2fqk2Q==";
        String jsonData = "{\"b_no\": [\"" + inputText + "\"]}";

        URL url = new URL(apiUrl);

        // HTTP 연결 설정
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // 데이터 전송
        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.writeBytes(jsonData);
            wr.flush();
        }

        // 응답 읽기
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 응답 출력
            System.out.println(response.toString());

            if (response.toString().contains("일반과세자")) {
                flag = 1;
            } else if (response.toString().contains("등록되지 않은")) {
                flag = 0;
            }

            return response.toString();
        } else {
            // 오류 응답 출력
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            String inputLine;
            StringBuilder errorResponse = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                errorResponse.append(inputLine);
            }
            in.close();

            System.out.println("Error Response: " + errorResponse.toString());
            return errorResponse.toString();
        }
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
//        startToast(area);
        String seller_num = ((EditText) findViewById(R.id.ed_seller_num)).getText().toString();

        if (flag == 0) {
            startToast("사업자등록번호를 인증해 주세요.");
        }

        if (email.length() > 0 && password.length() > 0 && password_check.length() > 0 && area.length() > 0 && userName.length() > 0 && flag == 1) { //정보 전부 입력됐는지 확인
            if (password.equals(password_check)) { //비밀번호 확인이 되었는지 체크
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, (task) -> {
                            if (task.isSuccessful()) { //Authentication에 등록 성공
                                FirebaseUser user = mAuth.getCurrentUser();

                                User userInfo = null;
                                userInfo = new User(user.getUid(), email, password, userName, area, "seller", 0);

                                //firestore에 User database 저장---
                                db.collection("User").document(user.getUid())
                                        .set(userInfo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                startToast("회원가입에 성공하였습니다!");
                                                Intent intent = new Intent(JoinSellerActivity.this, LoginActivity.class);
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
            } else { //비밀번호 확인 실패
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
