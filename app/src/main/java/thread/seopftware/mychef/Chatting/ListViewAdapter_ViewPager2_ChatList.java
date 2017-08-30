package thread.seopftware.mychef.Chatting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import thread.seopftware.mychef.R;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.login.widget.ProfilePictureView.TAG;
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

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewAdapter_ViewPager2_ChatList extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    ArrayList<ListViewItem_ViewPager2_ChatList> listViewItemList;
    Context context;
    int layout;
    String Login_Email;

    // ListViewAdapter의 생성자
    public ListViewAdapter_ViewPager2_ChatList(Context context, int layout, ArrayList<ListViewItem_ViewPager2_ChatList> listViewItemList) {
        this.context = context;
        this.layout = layout;
        this.listViewItemList = listViewItemList;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴.
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴.
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();


        // 나의 이메일 호출

        SharedPreferences pref1 = context.getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = context.getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK=pref2.getString(FBAPI, "0");

        if(!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = context.getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(FBEMAIL, "");
        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = context.getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(KAEMAIL, "");
        } else { // 일반
            SharedPreferences pref = context.getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(CHEFNORMALLEMAIL, "");
        }



        // "ListViewItem_Menu" Layout을 inflate하여 convertView 참조 획득

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_chat_roomlist, parent, false);
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem_ViewPager2_ChatList listViewItem = listViewItemList.get(position);

        // 화면에 표시될 View(Layout이 inflate된)으로 부터 위젯에 대한 참조 획득
        final TextView tv_Name = (TextView) convertView.findViewById(R.id.tv_Name);
        TextView tv_Date = (TextView) convertView.findViewById(R.id.tv_Date);
        TextView tv_Message = (TextView) convertView.findViewById(R.id.tv_Message);
        TextView tv_RoomNumber = (TextView) convertView.findViewById(R.id.tv_RoomNumber);

        ImageView iv_Profile = (ImageView) convertView.findViewById(R.id.iv_Profile);
        Glide.with(context).load(listViewItem.getProfile()).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_Profile);


        ImageView iv_Icon = (ImageView) convertView.findViewById(R.id.iv_Icon); // 아이콘 나타내기 (이미지뷰 or 녹음)



        Button btn_NumMessage = (Button) convertView.findViewById(R.id.btn_NumMessage);
        Button btn_NumPeople = (Button) convertView.findViewById(R.id.btn_NumPeople);

        // 아이템 내 각 위젯에 데이터 반영
        tv_Name.setText(listViewItem.getName());
        tv_Date.setText(listViewItem.getDate());
        tv_Message.setText(listViewItem.getMessage());
        tv_RoomNumber.setText(listViewItem.getRoomNumber());

        btn_NumMessage.setText(listViewItem.getNumMessage());
        btn_NumPeople.setText(listViewItem.getNumPeople());

        if (listViewItemList.get(position).getNumMessage().equals("0")) {
            btn_NumMessage.setVisibility(View.INVISIBLE);
        } else {
            btn_NumMessage.setVisibility(View.VISIBLE);
        }

        if (listViewItemList.get(position).getDate().equals("null")) {
            tv_Date.setVisibility(View.INVISIBLE);
        } else {
            tv_Date.setVisibility(View.VISIBLE);
        }

        if (listViewItemList.get(position).getMessage().equals("null")) {
            tv_Message.setVisibility(View.INVISIBLE);
        } else {
            tv_Message.setVisibility(View.VISIBLE);
        }

        if(listViewItemList.get(position).getIcon().equals("image")) {
            Glide.with(context).load("http://115.71.239.151/images/icon_image.png").into(iv_Icon);
        } else if(listViewItemList.get(position).getIcon().equals("voice")) {
            Glide.with(context).load("http://115.71.239.151/images/icon_voice.png").into(iv_Icon);
        } else {
            Glide.with(context).load("http://115.71.239.151/images/white.png").into(iv_Icon);
        }





        // 한번 클릭 시 채팅방 입장
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String room = listViewItemList.get(position).getRoomNumber();
                String name = listViewItemList.get(position).getName();
                String people = listViewItemList.get(position).getNumPeople();
                Log.e(TAG, "클릭시 room 번호 : " + room);

                Intent intent = new Intent(context, Chat_Chatting.class);
                intent.putExtra("entrance", "entrance");
                intent.putExtra("room_number", room);
                intent.putExtra("name", name);
                intent.putExtra("people", people);
                context.startActivity(intent);

            }
        });


        // 롱 클릭 시 dialog창 띄우기
        // 1. 채팅방 이름 변경
        // 2. 채팅방 사진 변경
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final String room = listViewItemList.get(position).getRoomNumber();
                Log.e(TAG, "롱클릭시 room 번호 : " + room);

                final CharSequence[] items = new CharSequence[]{"이름 변경", "사진 변경"};
                AlertDialog.Builder maindialog = new AlertDialog.Builder(context);
                maindialog.setTitle("메뉴");
                maindialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which] == "이름 변경") {
                            final EditText editText = new EditText(context);
                            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(context);
                            dialog1.setTitle("변경할 채팅방 이름");
                            dialog1.setView(editText);

                            // 확인 버튼 클릭 시
                            // 변경된 채팅방 이름을 DB로 쏴주기
                            dialog1.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String inputValue = editText.getText().toString();
                                    Toast.makeText(context, "입력값은 : " + inputValue, Toast.LENGTH_SHORT).show();

                                    updateChatNameDB(inputValue, room);

                                    }
                            });

                            //취소 버튼 클릭 시
                            dialog1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            dialog1.show();
                        }

                        if (items[which] == "사진 변경") {
                            Intent sendIntent = new Intent("CHATROOM_NAME_UPDATE");
                            sendIntent.putExtra("MessageFromAdapter", "photo");
                            sendIntent.putExtra("room_number", room);
                            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.sendBroadcast(sendIntent);
                        }
                    }
                });
                maindialog.show();
                return false;
            }
        });
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴.
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴.
    @Override
    public Object getItem(int position) {
        return null;
    }


    // 채팅방 이름 변경
    // POST : 방 번호, 나의 이메일, 변경할 방 이름
    // GET : 성공 여부 -> 방 정보 갱신
    private void updateChatNameDB(final String inputValue, final String room_number) {

        String url = "http://115.71.239.151/updateChatNameDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "updateChatNameDB parsing: " + response);

                Log.d(TAG, "**************************************************");
                Log.d(TAG, "브로드 캐스트 액션 ( CHATROOM_NAME_UPDATE )");
                Log.d(TAG, "**************************************************");

                Intent sendIntent = new Intent("CHATROOM_NAME_UPDATE");
                sendIntent.putExtra("MessageFromAdapter", "name");
                context.sendBroadcast(sendIntent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d(TAG, "여기도안옴??");
                // 변경할 채팅방 이름
                Map<String, String> map = new Hashtable<>();
                map.put("room_name", inputValue);
                map.put("login_email", Login_Email);
                map.put("room_number", room_number);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }





}
