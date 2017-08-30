package thread.seopftware.mychef.Chatting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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


    // 이미지 호출 변수들
    private static final int REQUEST_ALBUM = 2002;
    Bitmap album_bitmap;
    Uri album_uri;
    SimpleDateFormat simpleDateFormat;
    String room_number;

    public Viewpager2_ChatList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_viewpager2_chatlist, container, false);
        getActivity().setTitle("MyChef_Chat");

        listView= (ListView) rootview.findViewById(R.id.listView);

//        listView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(getContext(), "롱클릭", Toast.LENGTH_SHORT).show();
//
//                return false;
//            }
//        });

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
        intentfilter.addAction("CHATROOM_NAME_UPDATE");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String getMessage = intent.getStringExtra("MessageFromService"); // 서비스에서 보내는 브로드 캐스트
                String chatRoomUpdate = intent.getStringExtra("MessageFromAdapter"); // 어댑터에서 보내는 브로드 캐스트

                Log.d(TAG, "getMessage : " + getMessage);
                Log.d(TAG, "chatRoomUpdate : " + chatRoomUpdate);


                Log.d(TAG, "*******************************************************************");
                Log.d(TAG, "5. BroadcastReceive (채팅 대기방 화면) - 메세지를 받는 순간 리스트뷰 지웠다가 다시 뿌려준다.");
                Log.d(TAG, "db를 업데이트 해주되 num message 값을 받아온 다음 +1 해준다");
                Log.d(TAG, "*******************************************************************");


                if(getMessage!=null) {

                    Log.d(TAG, "채팅방 대기화면에서 getRoomInfoDB() 함수 실행");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                getRoomInfoDB();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                else if (chatRoomUpdate.equals("name")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(300);
                                getRoomInfoDB();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

                else if (chatRoomUpdate.equals("photo")) {
                    room_number = intent.getStringExtra("room_number");
                    Log.d(TAG, "Viewpager2 채팅방 목록 room_number" + room_number);

                    showFileChooser();
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
                            String Message_Icon = jo.getString("Message_Icon"); // 7 어떤 아이콘을 사용할지 여부

                            Log.d(TAG, "*******************************************************************");
                            Log.d(TAG, "Room_NumMessage 어디감!!? : " + Room_NumMessage);
                            Log.d(TAG, "*******************************************************************");

                            listViewItem = new ListViewItem_ViewPager2_ChatList();

                            listViewItem.setNumPeople(Room_NumPeople); // 1
                            listViewItem.setNumMessage(Room_NumMessage); // 2

                            listViewItem.setName(Room_Name); // 3
                            listViewItem.setMessage(Content_Message); // 4
                            listViewItem.setDate(Content_Date); // 5
                            listViewItem.setProfile("http://115.71.239.151/" + Room_Profile); // 6
                            listViewItem.setRoomNumber(Room_Number); // 7
                            listViewItem.setIcon(Message_Icon);

                            listViewItemList.add(listViewItem);

                        }
                    adapter = new ListViewAdapter_ViewPager2_ChatList(getContext(), R.layout.listview_chat_roomlist, listViewItemList);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ALBUM:
                album_uri = data.getData();
//                Glide.with(this).load(album_uri).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into();

                try {
                    // 앨범에서 비트맵 값 얻어내기
                    album_bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), album_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String imagePath = getStringImage(album_bitmap);
                String imageName = "photo_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                updateChatPhoto(imagePath, imageName);
                break;
        }
    }

    // 보내고자 하는 사진을 서버에 업로드
    private void updateChatPhoto(final String imagePath, final String imageName) {
        String url = "http://115.71.239.151/updateChatPhoto.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // 업로드에 성공하면 saveMessage & addItem
                Log.d(TAG, "updateChatPhoto response : "+response);

                getRoomInfoDB();

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

                map.put("room_number", room_number); // 방 번호
                map.put("email_sender", Login_Email); // 로그인 이메일 (보내는 사람 이메일)
                return map;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    //=========================================================================================================


    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mReceiver); // 브로드 캐스트 리시버 끊기
    }
    
}
