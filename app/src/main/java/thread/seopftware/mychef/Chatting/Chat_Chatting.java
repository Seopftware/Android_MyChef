package thread.seopftware.mychef.Chatting;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    private static String TAG = "Chat_Chatting";
    BroadcastReceiver mReceiver;

    // 뷰
    EditText et_ChatInput;
    Button btn_Send, btn_Out;

    // 채팅 리스트뷰
    ListView listView;
    ListViewAdapter_Chat adapter;
    ListViewItem_Chat listViewItem_chat;
    ArrayList<ListViewItem_Chat> listViewItemList;

    String Login_Email;
    String room_number;
    String Login_Name, Login_Image; // 로그인된 나의 이름 및 사진
    String Sender_Name, Sender_Image; // 로그인된 나의 이름 및 사진
    String content_time, content_message; // 메세지 시간 및 내용
    String email_receiver, email_sender; // 메세지를 받는 사람, 메세지를 보내는 사람
    String Receiver_name;
    SimpleDateFormat simpleDateFormat;

    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;


    private ConstraintLayout flContainer;
    private DrawerLayout dlDrawer;
    private ListView lvNavList;
    private Chat_NaviListItem listViewItem_drawer;
    private Chat_NaviListAdapter adapter_drawer;
    private ArrayList<Chat_NaviListItem> listViewItemList_drawer;
    LinearLayout linearLayout;
    Button btn_Invite;
    ImageButton ibtn_Transfer;
    String UserEmail;

    // 이미지 호출 변수들
    private static final int REQUEST_ALBUM = 2002;
    Bitmap album_bitmap;
    Uri album_uri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_chatting);

        Intent intent1 = getIntent();
        room_number = intent1.getStringExtra("room_number");


        // 브로드 캐스트 메세지를 받기 위한 intent filter 동적 생성 (브로드 캐스트 리시버)
        // 이렇게 동적으로 브로드 캐스트 리시버를 설정하면 AndroidManifest에 인텐트 필터를 설정해 주지 않아도 된다.
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.dwfox.myapplication.SEND_BROAD_CAST");

        // 액션바 작업
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("채팅방");

        adapter = new ListViewAdapter_Chat();
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);


        // drawer
        lvNavList = (ListView) findViewById(R.id.lv_activity_main_nav_list);
        flContainer = (ConstraintLayout) findViewById(R.id.fl_activity_main_container);
        dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_main_drawer);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        btn_Invite = (Button) findViewById(R.id.btn_Invite);
        btn_Out = (Button) findViewById(R.id.btn_Out);
        ibtn_Transfer = (ImageButton) findViewById(R.id.ibtn_Transfer);
        ibtn_Transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "이미지 버튼 클릭!!");
                final CharSequence[] items = new CharSequence[]{"사진 보내기"};
                AlertDialog.Builder dialog = new  AlertDialog.Builder(Chat_Chatting.this);
                dialog.setTitle("메뉴");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(items[which]=="사진 보내기") {
                            showFileChooser();
                        }
                    }
                });
                dialog.show();
            }
        });


        btn_Invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // drawerlayout의 초대하기 버튼. 클릭 하면 drawer 닫히게 하기
                Toast.makeText(getApplicationContext(), "초대하기!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Chat_Invite_Friendlist.class);
                intent.putExtra("email", Login_Email); /// 나의 이메일 주소
                intent.putExtra("room_number", room_number); // 방 번호
                startActivity(intent);

                dlDrawer.closeDrawer(linearLayout);
            }
        });

        btn_Out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "채팅방 나가기 클릭 시 디비에서 룸멤버 삭제 및 메세지 날리기");
                roomMemberDeleteDB();


            }
        });


        lvNavList.setOnItemClickListener(new DrawerItemClickListener());


        // 세션 유지를 위한 이메일 값 불러들이기
        SharedPreferences pref1 = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK = pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK = pref2.getString(FBAPI, "0");

        if (!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            Login_Email = pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB chefemail: " + Login_Email);
        } else if (!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            Login_Email = pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA chefemail: " + Login_Email);
        } else { // 일반
            SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            Login_Email = pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal chefemail: " + Login_Email);
        }

        Log.d(TAG, "접속된 Email : " + Login_Email);

        getMyInfo(); // 채팅에 필요한 정보 가져오기
        getNaviMemberDB(); // drawer 멤버 불러오기


        // view 객체 선언
        btn_Send = (Button) findViewById(R.id.btn_Send);
        et_ChatInput = (EditText) findViewById(R.id.et_ChatInput);

        // editText를 통해서 입력받은 데이터를 서버에 전송
        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content_message = et_ChatInput.getText().toString();

                if (content_message.length() == 0) {
                    Toast.makeText(getApplicationContext(), "메세지를 한 글자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                    et_ChatInput.requestFocus();
                    return;
                }

                if (content_message != null) { // 만약 data가 비어있지 않다면 서버로 data 전송

                    long now = System.currentTimeMillis();
                    simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hh:dd a", Locale.KOREA);
                    Log.d("시간이 이상함", String.valueOf(simpleDateFormat));
                    String Show_Time = simpleDateFormat.format(new Date(now));
                    String[] time_split = Show_Time.split("_");
                    String Date = time_split[0];
                    String Time = time_split[1];
                    Log.d("시간 확인", "Date : " + Date + " Time : " + Time);

                    addNumMessage();


                    TimeCheckDB(room_number, Show_Time, content_message); // 그 방에서 가장 마지막으로 보낸 메세지의 날짜와 오늘의 날짜가 다르면 addTimeItem() 해주기

                }
            }
        });


        /*
    *
    *
    * 서비스가 서버로부터 받은 메세지를 받는 곳
    *
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

                    // 여기서는 보내는 사람이 받는 사람이 됨. 헷갈리지 않기!! (내가 메세지를 보낼 때와 받을 때가 구현되어 있어서 헷갈리기 쉽다.)
                    // 상대방이 보낸 메세지 JSON 임
                    JSONObject jsonObject = new JSONObject(getMessage);

                    String room_status = jsonObject.getString("room_status");
                    String room_number2 = jsonObject.getString("room_number");
                    String email_sender = jsonObject.getString("email_sender");
                    String content_message = jsonObject.getString("content_message");

                    Log.d("room_status", room_status);
                    Log.d("room_number2", room_number2); // room_number는 현재 내가 속해 있는 방 번호. room_number2는 상대방이 보낸 메세지의 방 번호
                    Log.d("email_sender", email_sender);
                    Log.d("content_message", content_message);


                    long now = System.currentTimeMillis();
                    simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREA);
                    Log.d("시간이 이상함", String.valueOf(simpleDateFormat));
                    String entrance_time = simpleDateFormat.format(new Date(now));


                    if (room_number.equals(room_number2)) {

                        // 내가 보낸 메세지가 나에게 보여줄 때
                        if (email_sender.equals(Login_Email)) {


                            if (room_status.equals("0")) {
//                                chat_SaveMessage("0", room_number2, email_sender, content_message, entrance_time);

                                adapter.addItemTime(entrance_time); // 맨 처음 접속시 날짜 띄우기
                                adapter.notifyDataSetChanged();
                                getNaviMemberDB();

                            }

//                            else if(room_status.equals("1")) {
//                                Log.d(TAG, "내가 보낸 메세지 room_status : " + room_status);
//                                Log.d(TAG, "내가 보낸 메세지 상태 1일 때 메세지를 저장하는 곳");
//
//                                String content_time = jsonObject.getString("content_time");
//                                Log.d("content_time", content_time);
//
//                                senderInfoDB(room_status, room_number, email_sender, content_message, content_time); // 보내는 사람의 이메일 값을 DB로 보낸 다음 닉네임/프로필 사진 받아옴
//                            }


                            // 상대방이 내가 보낸 메세지를 저장하는 곳
                        } else {
                            if (room_status.equals("0")) { // 채팅방 최초 접속

//                                chat_SaveMessage("0", room_number2, email_sender, content_message, entrance_time);

                                adapter.addItemTime(entrance_time); // 맨 처음 접속시 날짜 띄우기
                                adapter.addItem(content_message); // ""님이 입장하셨습니다.
                                adapter.notifyDataSetChanged();

                                getNaviMemberDB();


                            } else if (room_status.equals("1")) { // 채팅 메세지

                                String content_time = jsonObject.getString("content_time");
                                Log.d("content_time", content_time);


                                Log.d(TAG, "메세지 핸들러 부분 room_status 체크 : " + room_status);
                                senderInfoDB(room_status, room_number, email_sender, content_message, content_time); // 보내는 사람의 이메일 값을 DB로 보낸 다음 닉네임/프로필 사진 받아옴

                            } else if (room_status.equals("2")) { // ""님이 나가셨습니다.

                                adapter.addItem(content_message);
                                adapter.notifyDataSetChanged();

                                getNaviMemberDB();

                            } else if (room_status.equals("6")) {  // 사람을 채팅방에 초대했을 때

                                String content_time = jsonObject.getString("content_time");
                                Log.d("content_time", content_time);

                                adapter.addItem(content_message);
                                adapter.notifyDataSetChanged();

                                chat_SaveMessage("6", room_number2, email_sender, content_message, content_time);
                                getNaviMemberDB();


                            } else if (room_status.equals("7")) { // 1:1 방이 개설되었을 때



                            } else if(room_status.equals("999")) {
                                Log.d(TAG, "Handler room_status 999 일 때 (이미지 전송)");

                                String content_time = jsonObject.getString("content_time");
                                Log.d("content_time", content_time);

                                senderInfoDB(room_status, room_number, email_sender, content_message, content_time); // 보내는 사람의 이메일 값을 DB로 보낸 다음 닉네임/프로필 사진 받아옴
                            }
                        }

                        adapter.notifyDataSetChanged();

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

                Log.d(TAG, "getChattingInfo parsing : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    // 데이터 불러들이기
                    Login_Name = jo.getString("name"); // 0
                    Login_Image = jo.getString("profile"); // 1

                    Log.d(TAG, "Login_Name : " + Login_Name + "Login_Image : " + Login_Image);

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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

//                Log.d(TAG, "email_sender : "+email_sender);
                Map<String, String> map = new Hashtable<>();
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
                    Sender_Name = jo.getString("name"); // 0
                    Sender_Image = jo.getString("profile"); // 1
                    Log.d(TAG, "senderInfoDB Sender_Name : " + Sender_Name + "senderInfoDB Sender_Image : " + Sender_Image);

                    String[] time_split = content_time1.split("_");
                    String Date = time_split[0];
                    String Time = time_split[1];
                    Log.d("sender Info 시간 확인", "Date : " + Date + " Time : " + Time);

                    Log.d(TAG, "리스트뷰 추가전 Sender_Name :" + Sender_Name + "리스트뷰 추가전 Sender_Image : " + Sender_Image);

                    if(room_status1.equals("1")) { // 일반 메세지 띄우기
                        Log.d(TAG, "senderInfoDB status : 1 일 때");
                        adapter.addItem(email_sender1, Sender_Name, Time, content_message1, "http://115.71.239.151/" + Sender_Image);
                        adapter.notifyDataSetChanged();

//                        chat_SaveMessage(room_status1, room_number1, email_sender1, content_message1, content_time1);
                        // 여기서 Login_Name은 자기 이름임. 보내는 사람의 이메일을 웹서버로 보낸 다음 그 값을 기반으로 닉네임/이미지를 불러와야함.
                    }

                    else if(room_status1.equals("999")) { // 이미지 보여주기
                        Log.d(TAG, "senderInfoDB status : 999 일 때");
                        Log.d(TAG, "senderInfoDB 까지 오는가? (999) : 이미지 경로는?" + content_message1);

                        adapter.addImage(email_sender1, Sender_Name, Time, "http://115.71.239.151/"+ content_message1, "http://115.71.239.151/" + Sender_Image);
                        adapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d(TAG, "나의 입장에서 보내는 사람의 이메일 주소 email_sender : " + email_sender1);
                Map<String, String> map = new Hashtable<>();
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

                        String room_status = jo.getString("room_status");
                        String email_sender = jo.getString("email_sender");
                        String content_message = jo.getString("content_message");
                        String content_time = jo.getString("content_time");
                        String name = jo.getString("name");
                        String photostring = jo.getString("photostring");


                        Log.d(TAG, "room_status : " + room_status);
                        Log.d(TAG, "email_sender : " + email_sender);
                        Log.d(TAG, "content_message : " + content_message);
                        Log.d(TAG, "content_time : " + content_time);
                        Log.d(TAG, "name : " + name);

                        long now = System.currentTimeMillis();
                        simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREA);
                        Log.d("시간이 이상함", String.valueOf(simpleDateFormat));
                        String entrance_time = simpleDateFormat.format(new Date(now));

                        String[] time_split = content_time.split("_");
                        String Date = time_split[0];
                        String Time = time_split[1];
                        Log.d("시간 확인", "Date : " + Date + " Time : " + Time);

                        if (room_status.equals("0")) { // 채팅방 최초 접속
                            Log.d(TAG, "room_status 가 0일 때 : 채팅방 최초 생성시");

                            adapter.addItemTime(entrance_time); // 맨 처음 접속시 날짜 띄우기

                        } else if (room_status.equals("1")) { // 채팅 메세지
                            Log.d(TAG, "room_status 가 1일 때 : 메세지를 주고 받을 때");
                            adapter.addItem(email_sender, name, Time, content_message, "http://115.71.239.151/" + photostring);

                        } else if (room_status.equals("2")) {
                            Log.d(TAG, "room_status 가 2일 때 : 채팅방을 나갔을 때 (~님이 나가셨습니다.)");

                            adapter.addItem(content_message); // ""님이 나가셨습니다.

                        } else if (room_status.equals("6")) { // 이미 만들어진 방에 사람을 초대할 때
                            Log.d(TAG, "room_status 가 6일 때");


                        } else if (room_status.equals("999")) { // 이미지 불러들이기
                            Log.d(TAG, "room_status 가 999일 때");
                            adapter.addImage(Login_Email, Login_Name, Time, "http://115.71.239.151/"+content_message, "http://115.71.239.151/" + Login_Image); // 서버 보내지 않고도 자체적으로 ListView에 띄우기


                        }

                    }

                    adapter.notifyDataSetChanged();

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

                Log.d("Room_Number", room_number);
                Map<String, String> map = new Hashtable<>();
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

                String[] split = response.split("_#@#_");

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

                        room_member = jo.getString("Room_Member");
                        Log.d(TAG, "room_member : " + room_member);
                        list.add(room_member);

                    }


                    // 메세지를 서비스로 보내는 곳
                    JSONObject object = new JSONObject();
                    object.put("room_status", "5"); // array에 사람 추가
                    object.put("people", String.valueOf(list.size()));
                    object.put("room_number", room_number);

                    for (int i = 0; i < list.size(); i++) {

                        Log.d(TAG, "room_member" + i + list.get(i));
                        object.put("room_member" + i, list.get(i));

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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d("Room_Number", room_number);
                Map<String, String> map = new Hashtable<>();
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

//                Log.d(TAG, "email_sender : "+email_sender);
                Map<String, String> map = new Hashtable<>();
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


    // 방 번호를 보내고 해당 방에 있는 사람들의 메세지 수 +1 씩 해준다.
    // 메세지를 보내는 순간 NumMessage 숫자를 +1 씩 해준다.
    // 그리고 채팅방을 나가는 순간 ( back 키 클릭 시 ) 0으로 초기화 시켜준다.
    private void addNumMessage() {
        Log.d(TAG, "addNumMessage() 함수가 실행 됩니다.");

        String url = "http://115.71.239.151/addNumMessage.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("addNumMessage", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d(TAG, "room_number : " + room_number);
                Map<String, String> map = new Hashtable<>();
                map.put("room_number", room_number);
                map.put("Login_Email", Login_Email);
                return map;


            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // 채팅방을 벗어나는 순간 ( back 키 클릭 시 ) 0으로 초기화 시켜준다.
    private void resetNumMessage() {
        Log.d(TAG, "resetNumMessage() 함수가 실행 됩니다.");

        String url = "http://115.71.239.151/resetNumMessage.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "addNumMessage() 메세지 숫자가 0으로 초기화 됬나요!?  : " + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d(TAG, "resetNumMessage() 함수의 room_number : " + room_number);
                Log.d(TAG, "resetNumMessage() 함수의 Login_Email : " + Login_Email);
                Map<String, String> map = new Hashtable<>();
                map.put("room_number", room_number);
                map.put("Login_Email", Login_Email);
                return map;


            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void TimeCheckDB(final String room_number, final String content_time, final String content_message) { // 방 번호를 보낸 다음 현재 시간과 마지막 메세지 시간을 비교. 날짜가 다르면 addTimeItem() 해주기.

        String url = "http://115.71.239.151/Chatting_TimeCheck.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TimeCheckDB", response);

                // 마지막 메세지를 보낸 날짜와 현재의 시간을 비교. 날짜가 다르면 addTimeItem() / 같으면 addItem()만 해주기

                long now = System.currentTimeMillis();
                simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 E요일_hh:dd a", Locale.KOREA);
                Log.d("시간이 이상함", String.valueOf(simpleDateFormat));
                String Show_Time = simpleDateFormat.format(new Date(now));

                String[] time_split = Show_Time.split("_");
                String Date = time_split[0];
                String Time = time_split[1];
                Log.d("시간 확인", "Date : " + Date + " Time : " + Time);

                if (response.equals("0")) { // 마지막으로 메세지를 보낸 날짜와 지금 메세지를 보낸 날짜가 같을 때 + 메세지만 추가

                    Log.d(TAG, "**************************************************");
                    Log.d(TAG, "디버깅 중 여기 오나111111");
                    Log.d(TAG, "content message : " + content_message);
                    Log.d(TAG, "**************************************************");


                    adapter.addItem(Login_Email, Login_Name, Time, content_message, "http://115.71.239.151/" + Login_Image); // 서버 보내지 않고도 자체적으로 ListView에 띄우기
                    adapter.notifyDataSetChanged();
                    chat_SaveMessage("1", room_number, Login_Email, content_message, content_time);

                    Log.d(TAG, "TimeCheckDB 저장값 room_number : " + room_number + " email_sender : " + Login_Email + " content_message : " + content_message + " content_time : " + content_time);
                    et_ChatInput.setText("");


                } else if (response.equals("1")) { // 마지막으로 메세지를 보낸 날짜와 지금 메세지를 보낸 날짜가 다를 때 + 메세지/날짜 추가

                    Log.d(TAG, "**************************************************");
                    Log.d(TAG, "디버깅 중 여기 오나22222222");
                    Log.d(TAG, "**************************************************");

                    adapter.addItemTime(Date);
                    adapter.addItem(Login_Email, Login_Name, Time, content_message, "http://115.71.239.151/" + Login_Image); // 서버 보내지 않고도 자체적으로 ListView에 띄우기
                    adapter.notifyDataSetChanged();
                    et_ChatInput.setText("");

                    chat_SaveMessage("0", room_number, Login_Email, content_message, content_time);
                    chat_SaveMessage("1", room_number, Login_Email, content_message, content_time);
                    Log.d(TAG, "TimeCheckDB 저장값 room_number : " + room_number + " email_sender : " + Login_Email + " content_message : " + content_message + " content_time : " + content_time);
                }


                /*
                    *
                    * 메세지를 서비스로 보내는 곳
                    *
                    * */

                try {

                    Log.d(TAG, "**************************************************");
                    Log.d(TAG, "btn_Send : 전송 버튼 클릭 시 메세지를 서비스로 날린다.");
                    Log.d(TAG, "**************************************************");

                    // 메세지를 서비스로 보내는 곳
                    JSONObject object = new JSONObject();
                    object.put("room_status", "1");
                    object.put("room_number", room_number);
                    object.put("email_sender", Login_Email);
                    object.put("content_message", content_message);
                    object.put("content_time", Show_Time);
                    String Object_Data = object.toString();


                    Intent intent = new Intent(Chat_Chatting.this, Chat_Service.class); // 액티비티 ㅡ> 서비스로 메세지 전달
                    intent.putExtra("command", Object_Data);
                    startService(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

//                Log.d(TAG, "email_sender : "+email_sender);
                Map<String, String> map = new Hashtable<>();
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

                Log.d(TAG, "getNaviMemberDB parsing: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);

                        // 데이터 불러들이기
                        String email = jo.getString("email");
                        String name = jo.getString("name");
                        String profile = jo.getString("profile");
                        String friend = jo.getString("friend");

                        Log.d(TAG, "getNaviMemberDB (email) : " + email);
                        Log.d(TAG, "getNaviMemberDB (friend) : " + friend);


                        // 데이터 뷰에 입력시키기
                        listViewItem_drawer = new Chat_NaviListItem();
                        listViewItem_drawer.setName(name);
                        listViewItem_drawer.setEmail(email);
                        listViewItem_drawer.setImage("http://115.71.239.151/" + profile);
                        listViewItem_drawer.setFriend(friend); // 친구 여부를 확인하기 위해서 만약 친구면 1, 친구가 아니면 0 값이 입력됨.
                        listViewItem_drawer.setIconDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.addfriend));

                        listViewItemList_drawer.add(listViewItem_drawer);
                    }

                    adapter_drawer = new Chat_NaviListAdapter(getApplicationContext(), R.layout.custom_drawer_item, listViewItemList_drawer);
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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d(TAG, "전달받은 room_number : " + room_number);
                Log.d(TAG, "전달받은 Login_Email : " + Login_Email);
                Map<String, String> map = new Hashtable<>();
                map.put("room_number", room_number);
                map.put("email_sender", Login_Email);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    // 채팅방 나가기 클릭 시 디비에서 룸멤버 삭제 및 메세지 날리기
    // POST : EMAIL, ROOM_NUMBER  GET: 0 (성공), 1 (실패) => 성공시 메세지 날리기
    private void roomMemberDeleteDB() {

        String url = "http://115.71.239.151/roomMemberDeleteDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "roomMemberDeleteDB parsing (해당 이메일의 이름값 가져옴) : " + response);

                try {

                    Log.d(TAG, "**************************************************");
                    Log.d(TAG, "roomMemberDeleteDB (volley) : 채팅방 나가기 클릭 시 서버로 ~님이 나갑니다. 메세지 보내기");
                    Log.d(TAG, "**************************************************");

                    // 메세지를 서비스로 보내는 곳
                    JSONObject object = new JSONObject();
                    object.put("room_status", "2");
                    object.put("room_number", room_number);
                    object.put("email_sender", Login_Email);
                    object.put("content_message", response + "님이 나가셨습니다.");
                    String Object_Data = object.toString();

                    Intent intent = new Intent(Chat_Chatting.this, Chat_Service.class); // 액티비티 ㅡ> 서비스로 메세지 전달
                    intent.putExtra("command", Object_Data);
                    startService(intent);
                    dlDrawer.closeDrawer(linearLayout);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d(TAG, "Delete DB 입력값 Login_Email : " + Login_Email);
                Log.d(TAG, "Delete DB 입력값 room_number : " + room_number);
                Map<String, String> map = new Hashtable<>();
                map.put("Login_Email", Login_Email);
                map.put("room_number", room_number);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    //=========================================================================================================
    // 이미지 전송을 위한 앨범 호출
    //=========================================================================================================

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "사진 보내기"), REQUEST_ALBUM);
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ALBUM:
                album_uri = data.getData();
//                Glide.with(this).load(album_uri).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into();

                try {
                    // 앨범에서 비트맵 값 얻어내기
                    album_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), album_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String imagePath = getStringImage(album_bitmap);
                String imageName = "photo_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                photoUpdateDB(imagePath, imageName);
                break;
        }
    }

    // 보내고자 하는 사진을 서버에 업로드
    private void photoUpdateDB(final String imagePath, final String imageName) {
        long now = System.currentTimeMillis();
        simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hh:dd a", Locale.KOREA);
        final String Show_Time = simpleDateFormat.format(new Date(now));

        String[] time_split = Show_Time.split("_");
        final String Time = time_split[1];

        String url = "http://115.71.239.151/photoUpdateDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // 업로드에 성공하면 saveMessage & addItem
                Log.d(TAG, "photoUpdateDB response : "+response);


                String Path=response; // DB에 저장된 사진의 경로



                // 나에게 추가
                adapter.addImage(Login_Email, Login_Name, Time, "http://115.71.239.151/"+Path, "http://115.71.239.151/" + Login_Image); // 서버 보내지 않고도 자체적으로 ListView에 띄우기
                adapter.notifyDataSetChanged();



                try {

                    Log.d(TAG, "**************************************************");
                    Log.d(TAG, "이미지 보내기!!!");
                    Log.d(TAG, "**************************************************");

                    // 메세지를 서비스로 보내는 곳
                    JSONObject object = new JSONObject();
                    object.put("room_status", "999");
                    object.put("room_number", room_number);
                    object.put("email_sender", Login_Email);
                    object.put("content_message", Path); // DB 이미지 경로
                    object.put("content_time", Show_Time);
                    String Object_Data = object.toString();

                    Intent intent = new Intent(Chat_Chatting.this, Chat_Service.class); // 액티비티 ㅡ> 서비스로 메세지 전달
                    intent.putExtra("command", Object_Data);
                    startService(intent);

                    addNumMessage();

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

                Map<String,String> map = new Hashtable<>();

                // 메세지 및 이미지 경로 DB에 저장 -> 메세지 뿌리기
                map.put("imagePath", imagePath);
                map.put("imageName", imageName);
                map.put("room_status", "999"); // 메세지 상태 (이미지 999)
                map.put("room_number", room_number); // 방 번호
                map.put("email_sender", Login_Email); // 로그인 이메일 (보내는 사람 이메일)
                map.put("content_time", Show_Time);
                return map;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    //=========================================================================================================



    //=========================================================================================================
    // 네비게이션 드로어 함수 부분 (채팅방 정보 표시)
    //=========================================================================================================
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
    //=========================================================================================================




    //=========================================================================================================
    //리스트뷰 위치 기억을 하기 위한 함수들
    //=========================================================================================================

//    @Override
//    protected void onRestoreInstanceState(Bundle state) {
//        super.onRestoreInstanceState(state);
//        mListState = state.getParcelable(LIST_STATE);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//
//        if (mListState != null)
//            listView.onRestoreInstanceState(mListState);
//        mListState = null;
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle state) {
//        super.onSaveInstanceState(state);
//        mListState = listView.onSaveInstanceState();
//        state.putParcelable(LIST_STATE, mListState);
//    }

    @Override
    public void onPause() {
        super.onPause();
        resetNumMessage(); // 채팅방 목록 다시 뿌려 주기
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver); // 브로드 캐스트 리시버 끊기
    }
}
