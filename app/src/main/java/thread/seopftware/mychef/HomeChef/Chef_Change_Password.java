package thread.seopftware.mychef.HomeChef;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import thread.seopftware.mychef.R;

import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLEMAIL;
import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLOGIN;
import static thread.seopftware.mychef.Login.Login_login.FACEBOOKLOGIN;
import static thread.seopftware.mychef.Login.Login_login.FBAPI;
import static thread.seopftware.mychef.Login.Login_login.FBEMAIL;
import static thread.seopftware.mychef.Login.Login_login.FB_LOGINCHECK;
import static thread.seopftware.mychef.Login.Login_login.KAAPI;
import static thread.seopftware.mychef.Login.Login_login.KAEMAIL;
import static thread.seopftware.mychef.Login.Login_login.KAKAOLOGIN;
import static thread.seopftware.mychef.Login.Login_login.KAKAO_LOGINCHECK;

public class Chef_Change_Password extends AppCompatActivity {

    private static String TAG="Chef_Change_Appeal";

    EditText et_CurrentPW, et_ChangePW, et_ConfirmPW;
    String ChefEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef__change__password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("비밀번호 변경");

        et_CurrentPW= (EditText) findViewById(R.id.et_CurrentPW);
        et_ChangePW= (EditText) findViewById(R.id.et_ChangePW);
        et_ConfirmPW= (EditText) findViewById(R.id.et_ConfirmPW);

        SharedPreferences pref1 = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK=pref2.getString(FBAPI, "0");

        if(!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            ChefEmail=pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB chefemail: "+ChefEmail);


        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            ChefEmail=pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA chefemail: "+ChefEmail);


        } else { // 일반
            SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            ChefEmail=pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal chefemail: "+ChefEmail);

        }

    }

    //액션바 백키 버튼 구현
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 정규식을 이용한 비밀번호 형식 체크
    private boolean PasswordValid(String password) {
        String password1 = "^[a-zA-Z0-9!@.#$%^&*?_~]{8,16}$";
        Pattern pass = Pattern.compile(password1);
        Matcher word = pass.matcher(password);
        return word.matches();
    }

    public void onClickedConfirm(View v) {

        // 비밀번호 입력 안했을 때
        if (et_CurrentPW.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "현재 비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
            et_CurrentPW.requestFocus();
            return;
        }

        // 비밀번호 정규식 체크
        if (PasswordValid(et_ChangePW.getText().toString()) == false) {
            Toast.makeText(getApplicationContext(), "비밀번호는 '특수문자'를 포함하여 '여덟 글자 이상'을 입력하셔야 합니다.", Toast.LENGTH_SHORT).show();
            et_ChangePW.requestFocus();
            return;
        }

        // 비밀번호 확인 입력 안했을 때
        if (et_ConfirmPW.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "비밀번호 확인을 입력하세요!", Toast.LENGTH_SHORT).show();
            et_ConfirmPW.requestFocus();
            return;

        }

        // 비밀번호 일치하지 않을 때
        if (!et_ChangePW.getText().toString().equals(et_ConfirmPW.getText().toString())) {
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            et_ChangePW.requestFocus();
            return;
        }

        updateData(); // 데이터 업데이트 시키기
    }

    // 1. 데이터 db 업데이트
    private void updateData() {

        String url = "http://115.71.239.151/Chef_Change_Password.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);

                if(Integer.parseInt(response)==0) {
                    Toast.makeText(getApplicationContext(), "비밀번호 변경이 완료되었습니다." , Toast.LENGTH_LONG).show();
                    finish();

                } else if (Integer.parseInt(response)==1) {
                    Toast.makeText(getApplicationContext(), "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    et_CurrentPW.requestFocus();
                    return;
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map = new Hashtable<>();

                String CurrentPW=et_CurrentPW.getText().toString();
                String ChangePW=et_ChangePW.getText().toString();

                map.put("ChefEmail", ChefEmail);
                map.put("CurrentPW", CurrentPW);
                map.put("ChangePW", ChangePW);


                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


}