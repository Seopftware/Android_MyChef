package thread.seopftware.mychef.Chatting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import thread.seopftware.mychef.R;

public class Chat_Invite_Friendlist extends AppCompatActivity {

    private static final String TAG = "Chat_Invite_FriendList";

    // 채팅 UI
    Button btn_Search;
    EditText et_SearchWord;

    ListView listView; // 리스트뷰
    ListViewItem_Invite listViewItem; // 리스트뷰 아이템
    ArrayList<ListViewItem_Invite> listViewItemList; // 리스트뷰 아이템 리스트
    ListViewAdapter_Invite adapter; // 리스트뷰 어댑터

    String room_number, email; // 방 번호, 나의 이메일
    ArrayList<String> memberList;
    String invite_email, invite_name;
    SimpleDateFormat simpleDateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__invite_friendlist);

        // 액션바 작업
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("친구 초대하기");

        // SlideMenuActivity로 부터 받아오는 값들
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        room_number = intent.getStringExtra("room_number");

        listView = (ListView) findViewById(R.id.listView);
        btn_Search = (Button) findViewById(R.id.btn_Search);
        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_text = et_SearchWord.getText().toString();

                searchDB(email, room_number, search_text);
            }
        });


        et_SearchWord = (EditText) findViewById(R.id.et_SearchWord);
        friendListDB(email, room_number);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_Email = (TextView) view.findViewById(R.id.tv_Email);
                String email = tv_Email.getText().toString();

                TextView tv_Name = (TextView) view.findViewById(R.id.tv_Name);
                String name = tv_Name.getText().toString();

                invite_email = email;
                invite_name = name;

                Log.d(TAG, "email : " + email);
                Log.d(TAG, "name : " + name);
            }
        });

    }

    // 멤버 초대 버튼 클릭 후 디비에 멤버 추가 해주기 ( POST: 초대받는 사람의 이메일, 방 번호  GET: 성공 여부 메세지 )
    private void memberInviteDB() {

        String url = "http://115.71.239.151/memberInviteDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "memberInviteDB parsing : " + response);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d(TAG, "memeberInviteDB POST 초대받는 사람의 이메일 : " + invite_email);
                Log.d(TAG, "memeberInviteDB POST 방번호 : " + room_number);
                Map<String, String> map = new Hashtable<>();
                map.put("invite_email", invite_email);
                map.put("room_number", room_number);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // db 데이터 로드
    private void friendListDB(final String email, final String room_number) {

        String url = "http://115.71.239.151/Chatting_FriendList3.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing1", response);

                try {
                    listViewItemList = new ArrayList<ListViewItem_Invite>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jo = jsonArray.getJSONObject(i);

                        String email = jo.getString("email");
                        String name = jo.getString("name");
                        String profile = jo.getString("profile");

                        listViewItem = new ListViewItem_Invite();
                        listViewItem.setEmail(email);
                        listViewItem.setName(name);
                        listViewItem.setImage("http://115.71.239.151/" + profile);
                        listViewItemList.add(listViewItem);
                    }

                    adapter = new ListViewAdapter_Invite(getApplicationContext(), R.layout.listview_chat_invite, listViewItemList);
                    listView.setAdapter(adapter);

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
                map.put("user_email", email);
                map.put("room_number", room_number);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    // 나의 이메일 및 이름 및 텍스트 보내고 일치하는 친구 목록 가지고 오기
    private void searchDB(final String email, final String room_number, final String text) {

        String url = "http://115.71.239.151/Chatting_FriendList4.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing1", response);

                try {
                    listViewItemList = new ArrayList<ListViewItem_Invite>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jo = jsonArray.getJSONObject(i);

                        String email = jo.getString("email");
                        String name = jo.getString("name");
                        String profile = jo.getString("profile");

                        listViewItem = new ListViewItem_Invite();
                        listViewItem.setEmail(email);
                        listViewItem.setName(name);
                        listViewItem.setImage("http://115.71.239.151/" + profile);
                        listViewItemList.add(listViewItem);


                    }
                    adapter = new ListViewAdapter_Invite(getApplicationContext(), R.layout.listview_chat_invite, listViewItemList);
                    listView.setAdapter(adapter);

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
                map.put("user_email", email);
                map.put("room_number", room_number);
                map.put("search_text", text);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    // 액션바 메뉴 아이템 기능 함수들
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_invite, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }

            case R.id.confirm: {
                Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "메뉴 아이템 클릭 시 invite_email : " + invite_email);

                memberInviteDB();

                try {

                    Log.d(TAG, "**************************************************");
                    Log.d(TAG, "btn_Send : 전송 버튼 클릭 시 메세지를 서비스로 날린다.");
                    Log.d(TAG, "**************************************************");

                    // 이메일 주소를 보내서 룸에 추가 시킨다. ( 서버 작업 )
                    // todo 디비에도 insert 시켜줘야 한다. ( POST: EMAIL, ROOM_NUMBER )

                    long now = System.currentTimeMillis();
                    simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hh:dd a");
                    Log.d("시간이 이상함", String.valueOf(simpleDateFormat));
                    String content_time = simpleDateFormat.format(new Date(now));
                    Log.d(TAG, "check 버튼 클릭 시 보내는 시간 값 : " + content_time);

                    JSONObject object = new JSONObject();
                    object.put("room_status", "6");
                    object.put("room_number", room_number);
                    object.put("email_sender", invite_email);
                    object.put("content_message", invite_name + "님이 초대 되셨습니다.");
                    object.put("content_time", content_time);
                    String Object_Data = object.toString();

                    Log.d(TAG, "check 버튼 클릭 시 서버로 보내는 JSON 값 : " + Object_Data);

                    Intent intent = new Intent(Chat_Invite_Friendlist.this, Chat_Service.class); // 액티비티 ㅡ> 서비스로 메세지 전달
                    intent.putExtra("command", Object_Data);
                    startService(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        return super.onOptionsItemSelected(item);

    }
}
