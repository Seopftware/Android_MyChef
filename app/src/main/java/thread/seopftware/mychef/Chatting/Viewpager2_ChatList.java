package thread.seopftware.mychef.Chatting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
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

public class Viewpager2_ChatList extends Fragment {

    private static String TAG = "Viewpager2_ChatList";
    ListViewItem_ViewPager2_ChatList listViewItem;
    ListViewAdapter_ViewPager2_ChatList adapter;
    ArrayList<ListViewItem_ViewPager2_ChatList> listViewItemList;
    ListView listView;

    String Login_Email;


    BroadcastReceiver mReceiver;


    public Viewpager2_ChatList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_viewpager2_chatlist, container, false);
        getActivity().setTitle("MyChef_Chat");

        listView= (ListView) rootview.findViewById(R.id.listView);

        return rootview;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        SharedPreferences pref1 = getContext().getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getContext().getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK=pref2.getString(FBAPI, "0");

        if(!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getContext().getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB chefemail: "+Login_Email);
        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getContext().getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA chefemail: "+Login_Email);
        } else { // 일반
            SharedPreferences pref = getContext().getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal chefemail: "+Login_Email);
        }
        Log.d(TAG, "Login_Email : "+Login_Email);

        getRoomInfoDB();

/*        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                pos=position;
                longid=id;

                tv_RoomNumber= (TextView) view.findViewById(R.id.tv_RoomNumber);

                final CharSequence[] items=new CharSequence[] {"채팅방 나가기"};
                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                dialog.setTitle("MENU");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(items[which]=="채팅방 나가기") {

                            String room = tv_RoomNumber.getText().toString();
                            Log.d("롱클릭시 room 번호", room);
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });*/


        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.dwfox.myapplication.SEND_BROAD_CAST");


        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String getMessage = intent.getStringExtra("MessageFromService");

                Log.d(TAG, "getMessage : " + getMessage);


                Log.d(TAG, "*******************************************************************");
                Log.d(TAG, "5. BroadcastReceive (채팅 대기방 화면) - 메세지를 받는 순간 리스트뷰 지웠다가 다시 뿌려준다.");
                Log.d(TAG, "db를 업데이트 해주되 num message 값을 받아온 다음 +1 해준다");
                Log.d(TAG, "*******************************************************************");


                if(getMessage!=null) {

                    Log.d(TAG, "채팅방 대기화면에서 getRoomInfoDB() 함수 실행");
                    getRoomInfoDB();
                }
            }
        };
        getContext().registerReceiver(mReceiver, intentfilter);
    }

    // db 데이터 로드
    private void getRoomInfoDB() { // 보내는 값 : 이메일 , 받는 값 : 방 참여자 수, (해당 이메일 사용자의) 방 이름, 마지막 메세지 시간, 내용, 누적된 메세지 갯수
        Log.d(TAG, "getRoomInfoDB()가 실행");

        String url = "http://115.71.239.151/Chatting_getRoomInfo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Chat_getRoomInfo res:", response);

                try {
                    listViewItemList = new ArrayList<ListViewItem_ViewPager2_ChatList>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");


                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jo = jsonArray.getJSONObject(i);

                            String Room_Number = jo.getString("Room_Number"); // 1 방 번호

                            String Room_NumPeople=jo.getString("Room_NumPeople"); // 2 총 사람 수
                            String Room_NumMessage = jo.getString("Room_NumMessage"); // 3 안 읽은 메세지 수
                            String Room_Name= jo.getString("Room_Name"); // 4 방 이름
                            String Room_Profile = jo.getString("Room_Profile"); // 5 방의 프로필 사진

                            String Content_Message= jo.getString("Content_Message"); // 7 방의 마지막 메세지
                            String Content_Date = jo.getString("Content_Date"); // 6 방의 마지막 메세지 날짜

                            listViewItem = new ListViewItem_ViewPager2_ChatList();

                            listViewItem.setNumPeople(Room_NumPeople); // 1
                            listViewItem.setNumMessage(Room_NumMessage); // 2

                            listViewItem.setName(Room_Name); // 3
                            listViewItem.setMessage(Content_Message); // 4
                            listViewItem.setDate(Content_Date); // 5
                            listViewItem.setProfile("http://115.71.239.151/" + Room_Profile); // 6
                            listViewItem.setRoomNumber(Room_Number); // 7

                            listViewItemList.add(listViewItem);

                        }
                    adapter = new ListViewAdapter_ViewPager2_ChatList(getContext(), R.layout.listview_chat_friendlist, listViewItemList);
                    listView.setAdapter(adapter);


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
                map.put("Login_Email", Login_Email);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        Log.d(this.getClass().getSimpleName(), "onResume()");
        getRoomInfoDB();
        super.onResume();

//        ParseDB(); // db 데이터 삽입
    }
}
