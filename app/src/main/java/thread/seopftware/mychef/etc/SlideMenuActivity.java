package thread.seopftware.mychef.etc;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import thread.seopftware.mychef.Chatting.Chat_Invite_Friendlist;
import thread.seopftware.mychef.Chatting.Chat_NaviListAdapter;
import thread.seopftware.mychef.Chatting.Chat_NaviListItem;
import thread.seopftware.mychef.R;

import static com.google.android.gms.internal.zzt.TAG;
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


public class SlideMenuActivity extends AppCompatActivity {

    private ConstraintLayout flContainer;
    private DrawerLayout dlDrawer;
    private ListView lvNavList;
    private Chat_NaviListItem listViewItem_drawer;
    private Chat_NaviListAdapter adapter_drawer;
    private ArrayList<Chat_NaviListItem> listViewItemList_drawer;
    LinearLayout linearLayout;
    Button btn_Invite;

    String room_number = "999";
    String email_sender = "inseop0813@gmail.com";
    String UserEmail;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_menu);

        // 로그인된 이메일 값 불러오기
        SharedPreferences pref1 = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK=pref2.getString(FBAPI, "0");

        if(!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            UserEmail=pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB chefemail: "+UserEmail);
        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            UserEmail=pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA chefemail: "+UserEmail);
        } else { // 일반
            SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            UserEmail=pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal chefemail: "+UserEmail);
        }
        Log.d(TAG, "UserEmail : "+UserEmail);


        // 액션바 작업
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("채팅방");

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
                intent.putExtra("email", UserEmail);
                intent.putExtra("room_number", room_number);
                startActivity(intent);

            }
        });


        lvNavList.setOnItemClickListener(new DrawerItemClickListener());

        getNaviMemberDB();

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
                Map<String,String> map = new Hashtable<>();
                map.put("room_number", room_number);
                map.put("email_sender", email_sender);
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