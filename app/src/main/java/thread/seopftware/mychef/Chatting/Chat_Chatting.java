package thread.seopftware.mychef.Chatting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.Locale;
import java.util.Map;

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

public class Chat_Chatting extends AppCompatActivity {


    private static String TAG="Chat_Chatting";
    BroadcastReceiver mReceiver;

    // 뷰
    EditText et_ChatInput;
    Button btn_Send;

    // 채팅 리스트뷰
    ListView listView;
    ListViewAdapter_Chat adapter;
    ListViewItem_Chat listViewItem_chat;
    ArrayList<ListViewItem_Chat> listViewItemList;

    String Login_Email;
    String room_number;
    String Login_Name, Login_Image; // 로그인된 나의 이름 및 사진
    String Sender_Name = null, Sender_Image = null; // 로그인된 나의 이름 및 사진
    String content_time, content_message; // 메세지 시간 및 내용
    String email_receiver, email_sender; // 메세지를 받는 사람, 메세지를 보내는 사람
    String Receiver_name;
    SimpleDateFormat simpleDateFormat;


    private ConstraintLayout flContainer;
    private DrawerLayout dlDrawer;
    private ListView lvNavList;
    private Chat_NaviListItem listViewItem_drawer;
    private Chat_NaviListAdapter adapter_drawer;
    private ArrayList<Chat_NaviListItem> listViewItemList_drawer;
    LinearLayout linearLayout;
    Button btn_Invite;
    String UserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_chatting);

        Intent intent1 = getIntent();
        room_number = intent1.getStringExtra("room_number");


        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.dwfox.myapplication.SEND_BROAD_CAST");

        // 액션바 작업
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("채팅방");

        adapter=new ListViewAdapter_Chat();
        listView= (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // drawer
        lvNavList = (ListView)findViewById(R.id.lv_activity_main_nav_list);
        flContainer = (ConstraintLayout)findViewById(R.id.fl_activity_main_container);
        dlDrawer = (DrawerLayout)findViewById(R.id.dl_activity_main_drawer);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        btn_Invite = (Button) findViewById(R.id.btn_Invite);
        btn_Invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "초대하기!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Chat_Invite_Friendlist.class);
                intent.putExtra("email", Login_Email); /// 나의 이메일 주소
                intent.putExtra("room_number", room_number); // 방 번호
                startActivity(intent);

            }
        });


        lvNavList.setOnItemClickListener(new DrawerItemClickListener());


        // 세션 유지를 위한 이메일 값 불러들이기
        SharedPreferences pref1 = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK=pref2.getString(FBAPI, "0");

        if(!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB chefemail: "+Login_Email);
        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA chefemail: "+Login_Email);
        } else { // 일반
            SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal chefemail: "+Login_Email);
        }

        Log.d(TAG, "접속된 Email : "+Login_Email);

        getMyInfo(); // 채팅에 필요한 정보 가져오기

        // view 객체 선언
        btn_Send= (Button) findViewById(R.id.btn_Send);
        et_ChatInput= (EditText) findViewById(R.id.et_ChatInput);

        // editText를 통해서 입력받은 데이터를 서버에 전송
        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content_message=et_ChatInput.getText().toString();

                if(content_message.length() == 0) {
                    Toast.makeText(getApplicationContext(), "메세지를 한 글자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                    et_ChatInput.requestFocus();
                    return;
                }

                if( content_message != null ) { // 만약 data가 비어있지 않다면 서버로 data 전송

                    long now=System.currentTimeMillis();
                    simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hh:dd a");
                    Log.d("시간이 이상함", String.valueOf(simpleDateFormat));
                    String Show_Time = simpleDateFormat.format(new Date(now));
                    String[] time_split=Show_Time.split("_");
                    String Date = time_split[0];
                    String Time = time_split[1];
                    Log.d("시간 확인", "Date : "+Date+" Time : "+Time);

                    TimeCheckDB(room_number, Show_Time); // 그 방에서 가장 마지막으로 보낸 메세지의 날짜와 오늘의 날짜가 다르면 addTimeItem() 해주기

                    /*
                    *
                    * 메세지를 서비스로 보내는 곳
                    *
                    * */

                    try{

                        Log.d(TAG, "**************************************************");
                        Log.d(TAG, "btn_Send : 전송 버튼 클릭 시 메세지를 서비스로 날린다.");
                        Log.d(TAG, "**************************************************");

                        // 메세지를 서비스로 보내는 곳
                        JSONObject object = new JSONObject();
                        object.put("room_status", "1");
                        object.put("room_number", room_number);
                        object.put("email_sender", Login_Email);
                        object.put("content_message", et_ChatInput.getText().toString());
                        object.put("content_time", Show_Time);
                        String Object_Data = object.toString();

                        Intent intent = new Intent(Chat_Chatting.this, Chat_Service.class); // 액티비티 ㅡ> 서비스로 메세지 전달
                        intent.putExtra("command", Object_Data);
                        startService(intent);

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
        });


        /*
    *
    *
    * 서비스가 서버로부터 받은 메세지를 받는 곳
    *
    * */

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String getMessage = intent.getStringExtra("MessageFromService");

                Log.d(TAG, "getMessage : " + getMessage);


                Log.d(TAG, "**************************************************");
                Log.d(TAG, "5. BroadcastReceiver : 서비스에서 받은 메세지를 리스트뷰에 추가했습니다.");
                Log.d(TAG, "**************************************************");


                try {

                    // 여기서는 보내는 사람이 받는 사람이 됨. 헷갈리지 않기!! (내가 메세지를 보낼 때와z 받을 때가 구현되어 있어서 헷갈리기 쉽다.)
                    // 상대방이 보낸 메세지 JSON 임
                    JSONObject jsonObject = new JSONObject(getMessage);

                    String room_status = jsonObject.getString("room_status");
                    String room_number2 = jsonObject.getString("room_number");
                    String email_sender = jsonObject.getString("email_sender");
                    String content_message = jsonObject.getString("content_message");

                    Log.d("room_status", room_status);
                    Log.d("room_number2", room_number2);
                    Log.d("email_sender", email_sender);
                    Log.d("content_message", content_message);


                    long now=System.currentTimeMillis();
                    simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREA);
                    Log.d("시간이 이상함", String.valueOf(simpleDateFormat));
                    String entrance_time = simpleDateFormat.format(new Date(now));


                    if(room_number.equals(room_number2)) {

                        if(email_sender.equals(Login_Email)) {

                            Log.d("여긴 오나여", "email_sender.equals(Login_Email) 안");

                            if(room_status.equals("0")) {

                                Log.d("여긴 오나여222222", "email_sender.equals(Login_Email) 안");

                                adapter.addItemTime(entrance_time); // 맨 처음 접속시 날짜 띄우기
                                adapter.addItem(content_message); // ""님이 입장하셨습니다.
                                adapter.notifyDataSetChanged();

                            }


                        } else {
                            if(room_status.equals("0")) { // 채팅방 최초 접속


                                adapter.addItemTime(entrance_time); // 맨 처음 접속시 날짜 띄우기
                                adapter.addItem(content_message); // ""님이 입장하셨습니다.
                                adapter.notifyDataSetChanged();

                            } else if(room_status.equals("1")) { // 채팅 메세지

                                String content_time = jsonObject.getString("content_time");
                                Log.d("content_time", content_time);

                                senderInfoDB(room_status, room_number, email_sender, content_message, content_time); // 보내는 사람의 이메일 값을 DB로 보낸 다음 닉네임/프로필 사진 받아옴



                            } else if(room_status.equals("2")) {

                                adapter.addItem(content_message); // ""님이 나가셨습니다.
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        registerReceiver(mReceiver, intentfilter);
    }

    // 보내는 사람 이메일 값 보내고 이름 및 프로필 사진 받아오기
    // 프로필 정보 받아오는 함수
    private void getMyInfo() {

        String url = "http://115.71.239.151/Chatting_Information.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("getChattingInfo parsing", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    // 데이터 불러들이기
                    Login_Name=jo.getString("name"); // 0
                    Login_Image=jo.getString("profile"); // 1

                    Log.d(TAG, "Login_Name : "+Login_Name+"Login_Image : "+Login_Image);

                    chat_getRoomMember(); // 룸 멤버 불러오기
                    chat_getMessage(); // 메세지 불러오기

                } catch (JSONException e) {
                    e.printStackTrace();
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

//                Log.d(TAG, "email_sender : "+email_sender);
                Map<String,String> map = new Hashtable<>();
                map.put("Login_Email", Login_Email);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // 나의 입장에서 보내는 사람의 이메일 주소
    private void senderInfoDB(final String room_status1, final String room_number1, final String email_sender1, final String content_message1, final String content_time1) {

        String url = "http://115.71.239.151/Chatting_SenderInfo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("senderInfoDB parsing", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    // 데이터 불러들이기
                    Sender_Name=jo.getString("name"); // 0
                    Sender_Image=jo.getString("profile"); // 1
                    Log.d(TAG, "senderInfoDB Sender_Name : "+Sender_Name+"senderInfoDB Sender_Image : "+Sender_Image);


                    Log.d(TAG, "리스트뷰 추가전 Sender_Name :"+Sender_Name+"리스트뷰 추가전 Sender_Image : "+Sender_Image);
                    adapter.addItem(email_sender1, Sender_Name, content_time1, content_message1, "http://115.71.239.151/"+Sender_Image);
                    adapter.notifyDataSetChanged();
                    chat_SaveMessage(room_status1, room_number1, email_sender1, content_message1, content_time1);
                    // 여기서 Login_Name은 자기 이름임. 보내는 사람의 이메일을 웹서버로 보낸 다음 그 값을 기반으로 닉네임/이미지를 불러와야함.

                } catch (JSONException e) {
                    e.printStackTrace();
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

                Log.d(TAG, "나의 입장에서 보내는 사람의 이메일 주소 email_sender : "+email_sender1);
                Map<String,String> map = new Hashtable<>();
                map.put("email_sender", email_sender1);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void chat_getMessage() { // 나의 방 번호 보내고 방 메세지 받아오기

        String url = "http://115.71.239.151/Chatting_GetMessage.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("서버에서 온 JSON", response);
                Log.d("parsing", response);
                try {
                    listViewItemList = new ArrayList<ListViewItem_Chat>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jo = jsonArray.getJSONObject(i);

                        String room_status=jo.getString("room_status");
                        String email_sender=jo.getString("email_sender");
                        String content_message=jo.getString("content_message");
                        String content_time=jo.getString("content_time");
                        String name=jo.getString("name");
                        String photostring=jo.getString("photostring");


                        Log.d(TAG, "room_status : " +room_status );
                        Log.d(TAG, "email_sender : " +email_sender );
                        Log.d(TAG, "content_message : " +content_message );
                        Log.d(TAG, "content_time : " +content_time );
                        Log.d(TAG, "name : " +name );

                        long now=System.currentTimeMillis();
                        simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREA);
                        Log.d("시간이 이상함", String.valueOf(simpleDateFormat));
                        String entrance_time = simpleDateFormat.format(new Date(now));



                            if(room_status.equals("0")) { // 채팅방 최초 접속

                                Log.d("여긴 오나?11111", "json 반복문");
                                adapter.addItemTime(entrance_time); // 맨 처음 접속시 날짜 띄우기
                                adapter.addItem(content_message); // ""님이 입장하셨습니다.

                            } else { // 채팅 메세지

                                Log.d("여긴 오나?222222", "json 반복문");
                                adapter.addItem(email_sender, name, content_time, content_message, "http://115.71.239.151/" + photostring);

                            }
//                            } else if(room_status.equals("2")) {
//
//                                adapter.addItem(content_message); // ""님이 나가셨습니다.
//                            }



/*                        listViewItem_chat=new ListViewItem_Chat();
                        listViewItem_chat.setStatus(room_status);
                        listViewItem_chat.setMessage(content_message);
                        listViewItem_chat.setTime(content_time);
                        listViewItem_chat.setName(name);
                        listViewItem_chat.setProfile("http://115.71.239.151/"+photostring);
                        listViewItemList.add(listViewItem_chat);*/
                    }

                    adapter.notifyDataSetChanged();

                    getNaviMemberDB(); // drawer 멤버 불러오기


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

                Log.d("Room_Number", room_number);
                Map<String,String> map = new Hashtable<>();
                map.put("room_number", room_number);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }





    private void chat_getRoomMember() { // 나의 방 번호 보내고 채팅방 참여자 이메일 가져오기 ( 받은 이메일을 서버에 보낸 다음 Array 에 담기 )

        String url = "http://115.71.239.151/Chatting_getRoomMember.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("서버에서 온 JSON", "Chatting_getRoomMember : " + response);

                String[] split=response.split("_#@#_");

                String people = split[0];
                String json = split[1];
                String room_member = null;

                Log.d(TAG, "people : " + people + "json : " + json);

                try {

                    ArrayList<String> list = new ArrayList<String>();

                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jo = jsonArray.getJSONObject(i);

                        room_member=jo.getString("Room_Member");
                        Log.d(TAG, "room_member : " +room_member );
                        list.add(room_member);

                    }



                    // 메세지를 서비스로 보내는 곳
                    JSONObject object = new JSONObject();
                    object.put("room_status", "5"); // array에 사람 추가
                    object.put("people", String.valueOf(list.size()));
                    object.put("room_number", room_number);

                    for(int i=0; i<list.size(); i++) {

                        Log.d(TAG, "room_member"+i +list.get(i));
                        object.put("room_member"+i, list.get(i));

                    }

                    String Object_Data = object.toString();

                    Intent intent = new Intent(getApplicationContext(), Chat_Service.class);
                    intent.putExtra("command", Object_Data);
                    startService(intent);


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

                Log.d("Room_Number", room_number);
                Map<String,String> map = new Hashtable<>();
                map.put("room_number", room_number);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void chat_SaveMessage(final String room_status, final String room_number, final String email_sender, final String content_message, final String content_time) {

        String url = "http://115.71.239.151/chat_SaveMessage.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("chat_SaveMessage", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

//                Log.d(TAG, "email_sender : "+email_sender);
                Map<String,String> map = new Hashtable<>();
                map.put("room_status", room_status);
                map.put("room_number", room_number);
                map.put("email_sender", email_sender);
                map.put("content_message", content_message);
                map.put("content_time", content_time);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }



    private void TimeCheckDB(final String room_number, final String content_time) { // 방 번호를 보낸 다음 현재 시간과 마지막 메세지 시간을 비교. 날짜가 다르면 addTimeItem() 해주기.

        String url = "http://115.71.239.151/Chatting_TimeCheck.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TimeCheckDB", response);

                // 마지막 메세지를 보낸 날짜와 현재의 시간을 비교. 날짜가 다르면 addTimeItem() / 같으면 addItem()만 해주기

                long now=System.currentTimeMillis();
                simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 E요일_hh:dd a", Locale.KOREA);
                Log.d("시간이 이상함", String.valueOf(simpleDateFormat));
                String Show_Time = simpleDateFormat.format(new Date(now));

                String[] time_split=Show_Time.split("_");
                String Date = time_split[0];
                String Time = time_split[1];
                Log.d("시간 확인", "Date : "+Date+" Time : "+Time);

                if(response.equals("0")) { // 마지막으로 메세지를 보낸 날짜와 지금 메세지를 보낸 날짜가 같을 때 + 메세지만 추가
                    adapter.addItem(Login_Email, Login_Name, Time, content_message, "http://115.71.239.151/"+Login_Image); // 서버 보내지 않고도 자체적으로 ListView에 띄우기
                    chat_SaveMessage("0", room_number, Login_Email, content_message, content_time);

                    Log.d(TAG, "TimeCheckDB 저장값 room_number : "+room_number+" email_sender : " +Login_Email+" content_message : " + content_message+" content_time : "+content_time);
                    et_ChatInput.setText("");
                } else if (response.equals("1")) { // 마지막으로 메세지를 보낸 날짜와 지금 메세지를 보낸 날짜가 다를 때 + 메세지/날짜 추가
                    adapter.addItemTime(Date);
                    adapter.addItem(Login_Email, Login_Name, Time, content_message, "http://115.71.239.151/"+Login_Image); // 서버 보내지 않고도 자체적으로 ListView에 띄우기
                    et_ChatInput.setText("");

                    chat_SaveMessage("0", room_number, Login_Email, content_message, content_time);
                    Log.d(TAG, "TimeCheckDB 저장값 room_number : "+room_number+" email_sender : " +Login_Email+" content_message : " + content_message+" content_time : "+content_time);
                }

                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

//                Log.d(TAG, "email_sender : "+email_sender);
                Map<String,String> map = new Hashtable<>();
                map.put("room_number", room_number);
                map.put("content_time", content_time);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // DB로 부터 해당 방 번호에 있는 사람들의 프사 및 이름 가지고 와서 ListView에 적용시키기.
    private void getNaviMemberDB() {

        String url = "http://115.71.239.151/getNaviMemberDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listViewItemList_drawer = new ArrayList<Chat_NaviListItem>();

                Log.d("parsing", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);

                        // 데이터 불러들이기
                        String email = jo.getString("email");
                        String name = jo.getString("name");
                        String profile = jo.getString("profile");
                        String friend = jo.getString("friend");

                        Log.d(TAG, "friend : " + friend);

                        // 데이터 뷰에 입력시키기
                        listViewItem_drawer=new Chat_NaviListItem();
                        listViewItem_drawer.setName(name);
                        listViewItem_drawer.setEmail(email);
                        listViewItem_drawer.setImage("http://115.71.239.151/"+profile);
                        listViewItem_drawer.setFriend(friend); // 친구 여부를 확인하기 위해서 만약 친구면 1, 친구가 아니면 0 값이 입력됨.
                        listViewItem_drawer.setIconDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.addfriend));

                        listViewItemList_drawer.add(listViewItem_drawer);
                    }

                    adapter_drawer= new Chat_NaviListAdapter(getApplicationContext(), R.layout.custom_drawer_item, listViewItemList_drawer);
                    lvNavList.setAdapter(adapter_drawer);

                } catch (JSONException e) {
                    e.printStackTrace();
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

                Log.d(TAG, "전달받은 room_number : " + room_number );
                Log.d(TAG, "전달받은 Login_Email : " + Login_Email );
                Map<String,String> map = new Hashtable<>();
                map.put("room_number", room_number);
                map.put("email_sender", Login_Email);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            switch (position) {

                case 0:
                    Log.d(TAG, "position 0 : " + position);
                    break;

                case 1:
                    Log.d(TAG, "position 1 : " + position);
                    break;

                case 2:
                    Log.d(TAG, "position 2 : " + position);
                    break;
            }

            dlDrawer.closeDrawer(linearLayout);



        }

    }


    // 뷰로 inflate 시켜주기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }


    //액션바 백키 버튼 구현
    // 메뉴에 해당하는 아이템들 클릭 시 호출되는 함수
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {

                Log.d(TAG, "~메세지 보내서 ~님이 나갔습니다. 메세지 띄우기 STATUS-0 , content_message 가 달라짐");
                finish();
                return true;
            }


            case R.id.navidrawer: {
                Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT).show();
                dlDrawer.openDrawer(linearLayout);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (dlDrawer.isDrawerOpen(linearLayout)) {
            dlDrawer.closeDrawer(linearLayout);
        } else {
            super.onBackPressed();
        }
    }
}
