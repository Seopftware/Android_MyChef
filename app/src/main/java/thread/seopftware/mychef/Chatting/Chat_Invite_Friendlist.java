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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import thread.seopftware.mychef.R;

public class Chat_Invite_Friendlist extends AppCompatActivity {

    private static final String TAG = "Chat_Invite_FriendList";
    Button btn_Search;
    EditText et_SearchWord;

    ListView listView; // 리스트뷰
    ListViewItem_Invite listViewItem; // 리스트뷰 아이템
    ArrayList<ListViewItem_Invite> listViewItemList; // 리스트뷰 아이템 리스트
    ListViewAdapter_Invite adapter; // 리스트뷰 어댑터

    String room_number;
    ArrayList<String> memberList;
    String invite_email, invite_name;


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
        String email = intent.getStringExtra("email");
        room_number = intent.getStringExtra("room_number");

        listView = (ListView) findViewById(R.id.listView);
        btn_Search = (Button) findViewById(R.id.btn_Search);
        et_SearchWord = (EditText) findViewById(R.id.et_SearchWord);

//        memberListDB();
        friendListDB(email, room_number);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = (Object) parent.getAdapter().getItem(position);

            }
        };


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

    private void memberListDB() { // 이미 채팅방에 존재하는 이메일 제외시키기 위해서 필요

        String url = "http://115.71.239.151/memberListDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing1", response);

                try {
                    memberList = new ArrayList<String>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jo = jsonArray.getJSONObject(i);

                        String email = jo.getString("email");
                        memberList.add(email);

                        Log.d("arrayList", "i 번째 : " + memberList.get(i));
                    }

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
                map.put("room_number", room_number);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // db 데이터 로드
    private void friendListDB(String email, final String room_number) {

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

//                        for(int k=0; k<memberList.size(); k++) {
//
//                            if(email.equals(memberList.get(k))) {
//                                break;
//                            } else {
//
//                                break;
//                            }
//
//                        }

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

                String email = "inseop0813@gmail.com";
                Map<String, String> map = new Hashtable<>();
                map.put("user_email", email);
                map.put("room_number", room_number);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

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

                try {

                    Log.d(TAG, "**************************************************");
                    Log.d(TAG, "btn_Send : 전송 버튼 클릭 시 메세지를 서비스로 날린다.");
                    Log.d(TAG, "**************************************************");

                    // 이메일 주소를 보내서 룸에 추가 시킨다. ( 서버 작업 )
                    // todo 디비에도 insert 시켜줘야 한다. ( POST: EMAIL, ROOM_NUMBER )

                    JSONObject object = new JSONObject();
                    object.put("room_status", "6");
                    object.put("room_number", room_number);
                    object.put("invite_email", invite_email);
                    object.put("content_message", invite_name + "님이 초대 되셨습니다.");
                    String Object_Data = object.toString();

                    Intent intent = new Intent(Chat_Invite_Friendlist.this, Chat_Service.class); // 액티비티 ㅡ> 서비스로 메세지 전달
                    intent.putExtra("command", Object_Data);
                    startService(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
            return super.onOptionsItemSelected(item);

    }
}
