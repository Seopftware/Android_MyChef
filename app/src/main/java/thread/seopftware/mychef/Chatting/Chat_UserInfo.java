package thread.seopftware.mychef.Chatting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
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

public class Chat_UserInfo extends AppCompatActivity {

    private final static String TAG = "Chat_UserInfo";
    ImageView iv_Profile;
    TextView tv_Message, tv_Name;
    LinearLayout LinearChat;
    ImageButton ibtn_Exit, ibtn_Chatting;
    String send_profile; // 이미지 확대를 위한 이미지 파일 넘기기 (Chat_PinzhZoom.class 로)

    String email, name; // 현재 내가 보고 있는 사람의 이메일
    String UserEmail; // 접속해 있는 나의 이메일

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_info);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        iv_Profile = (ImageView) findViewById(R.id.iv_Profile);
        tv_Message = (TextView) findViewById(R.id.tv_Message);
        tv_Name = (TextView) findViewById(R.id.tv_Name);
        ibtn_Exit = (ImageButton) findViewById(R.id.ibtn_Exit);
        ibtn_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearChat = (LinearLayout) findViewById(R.id.LinearChat);
        ibtn_Chatting = (ImageButton) findViewById(R.id.ibtn_Chatting);

        SharedPreferences pref1 = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK = pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK = pref2.getString(FBAPI, "0");

        if (!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            UserEmail = pref.getString(FBEMAIL, "");
        } else if (!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            UserEmail = pref.getString(KAEMAIL, "");
        } else { // 일반
            SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            UserEmail = pref.getString(CHEFNORMALLEMAIL, "");
        }

        ibtn_Chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                makeRoomDB();

            }
        });

        iv_Profile.setOnClickListener(new View.OnClickListener() { // 이미지 사진 클릭 시 이미지 확대 화면으로 넘어간다.
            @Override
            public void onClick(View v) {

                Log.d(TAG, "**************************************************");
                Log.d(TAG, "이미지 확대 보기 ( Pinch Zoom )");
                Log.d(TAG, "보내는 이미지 url 값 send_profile : " + send_profile);
                Log.d(TAG, "**************************************************");

                Intent intent = new Intent(getApplicationContext(), Chat_PinchZoom.class);
                intent.putExtra("profile", send_profile); // url 주소 인텐트로 넘겨주기
                startActivity(intent);
            }
        });


        ParseDB();
    }


    // db 데이터 로드
    private void ParseDB() {

        String url = "http://115.71.239.151/Chatting_InfoView.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing1", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    name = jo.getString("name");
                    String profile = jo.getString("profile");
                    String message = jo.getString("message");


                    tv_Name.setText(name);
                    tv_Message.setText(message);
                    Glide.with(getApplicationContext()).load("http://115.71.239.151/" + profile).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_Profile); // 프사

                    send_profile = "http://115.71.239.151/" + profile;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new Hashtable<>();
                map.put("email", email);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void makeRoomDB() {

        // php 에서 디비에 채팅방을 생성함.
        String url = "http://115.71.239.151/makeRoomDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "makeRoomDB response (room_number): " + response);

                final String room_number = response;

                    Intent intent1= new Intent(Chat_UserInfo.this, Chat_Chatting.class);
                    intent1.putExtra("room_number", room_number);
                    startActivity(intent1);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new Hashtable<>();
                map.put("email", email); // 내가 대화를 걸 사람의 이메일
                map.put("user_email", UserEmail); // 나의 이메일
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
