package thread.seopftware.mychef.Chatting;

import android.content.Intent;
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

public class Chat_UserInfo extends AppCompatActivity {

    final static String TAG1 ="Chat_UserInfo";
    ImageView iv_Profile;
    TextView tv_Message, tv_Name;
    LinearLayout LinearChat;
    ImageButton ibtn_Exit;
    String send_profile; // 이미지 확대를 위한 이미지 파일 넘기기 (Chat_PinzhZoom.class 로)

    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_info);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        iv_Profile= (ImageView) findViewById(R.id.iv_Profile);
        tv_Message= (TextView) findViewById(R.id.tv_Message);
        tv_Name= (TextView) findViewById(R.id.tv_Name);
        ibtn_Exit= (ImageButton) findViewById(R.id.ibtn_Exit);
        ibtn_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearChat= (LinearLayout) findViewById(R.id.LinearChat);

        LinearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                try{

                    Log.d(TAG1, "**************************************************");
                    Log.d(TAG1, "1:1 채팅 걸기");
                    Log.d(TAG1, "**************************************************");

                    // 메세지를 서비스로 보내는 곳
                    JSONObject object = new JSONObject();
                    object.put("room_status", "0");
                    object.put("room_number", room_number);
                    object.put("email_receiver", email_receiver);
                    object.put("email_sender", Login_Email);
                    object.put("content_message", Receiver_name+"이 입장했습니다.");
                    String Object_Data = object.toString();

                    Intent intent = new Intent(Chat_UserInfo.this, Chat_Service.class); // 액티비티 ㅡ> 서비스로 메세지 전달
                    intent.putExtra("command", Object_Data);
                    startService(intent);


                } catch (JSONException e){
                    e.printStackTrace();
                }*/
            }
        });

        iv_Profile.setOnClickListener(new View.OnClickListener() { // 이미지 사진 클릭 시 이미지 확대 화면으로 넘어간다.
            @Override
            public void onClick(View v) {

                Log.d(TAG1, "**************************************************");
                Log.d(TAG1, "이미지 확대 보기 ( Pinch Zoom )");
                Log.d(TAG1, "보내는 이미지 url 값 send_profile : "+ send_profile);
                Log.d(TAG1, "**************************************************");

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

                    String name = jo.getString("name");
                    String profile = jo.getString("profile");
                    String message = jo.getString("message");



                    tv_Name.setText(name);
                    tv_Message.setText(message);
                    Glide.with(getApplicationContext()).load("http://115.71.239.151/"+profile).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_Profile); // 프사

                    send_profile = "http://115.71.239.151/"+profile;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map = new Hashtable<>();
                map.put("email", email);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
