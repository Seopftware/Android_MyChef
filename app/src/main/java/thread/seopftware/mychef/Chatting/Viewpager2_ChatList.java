package thread.seopftware.mychef.Chatting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import thread.seopftware.mychef.GoogleMap_Module.Navigate_CustomerLocation;
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

public class Viewpager2_ChatList extends ListFragment {

    private static String TAG = "Viewpager2_ChatList";
    ListViewAdapter_ViewPager2_ChatList adapter;
    ListViewItem_ViewPager2_ChatList listViewItem;
    ArrayList<ListViewItem_ViewPager2_ChatList> listViewItemList;

    String Login_Email;

    int pos;
    long longid;

    TextView tv_RoomNumber;

    public Viewpager2_ChatList() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
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

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

                        if(items[which]=="출장지역 찾아가기") {
                            Intent intent = new Intent(getContext(), Navigate_CustomerLocation.class);
                            startActivity(intent);
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    // 한번 클릭 시 해당 메뉴 상세 보기 화면 으로
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        tv_RoomNumber = (TextView) v.findViewById(R.id.tv_RoomNumber);
        String room=tv_RoomNumber.getText().toString();
        Log.e(TAG,"클릭시 room 번호 : "+ room);

        Intent intent=new Intent(getContext(), Chat_Client.class);
        intent.putExtra("room_number", room);
        startActivity(intent);

        super.onListItemClick(l, v, position, id);
    }

    // db 데이터 로드
    private void getRoomInfoDB() { // 보내는 값 : 이메일 , 받는 값 : 방 참여자 수, (해당 이메일 사용자의) 방 이름, 마지막 메세지 시간, 내용, 누적된 메세지 갯수

        String url = "http://115.71.239.151/Chatting_getRoomInfo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Chat_getRoomInfo res:", response);

                try {
                    listViewItemList = new ArrayList<ListViewItem_ViewPager2_ChatList>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if(jsonArray.length()==0) {
                        Toast.makeText(getContext(), "예약 중이신 출장 계획이 없습니다.\n예약을 먼저 진행해 주세요.", Toast.LENGTH_SHORT).show();
                        setListShown(true);
                        return;

                    } else {
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
                        adapter = new ListViewAdapter_ViewPager2_ChatList(listViewItemList);
                        setListAdapter(adapter);
                    }

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
        super.onResume();

//        ParseDB(); // db 데이터 삽입
    }
}
