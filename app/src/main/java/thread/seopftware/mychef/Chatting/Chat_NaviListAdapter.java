package thread.seopftware.mychef.Chatting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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

/**
 * Created by MSI on 2017-08-14.
 */

public class Chat_NaviListAdapter extends BaseAdapter{

    ArrayList<Chat_NaviListItem> listViewItemList;
    Context context;
    int layout;
    String Login_Email;

    ImageButton ibtn_addFriend;

    public Chat_NaviListAdapter(Context context, int layout, ArrayList<Chat_NaviListItem> listViewItemList) {
        this.context = context;
        this.layout = layout;
        this.listViewItemList = listViewItemList;

    }

    public Chat_NaviListAdapter(ArrayList<Chat_NaviListItem> listViewItemList) {

        this.listViewItemList = listViewItemList;

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final int pos = position;


        // 세션 유지를 위한 이메일 값 불러들이기
        SharedPreferences pref1 = context.getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = context.getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK=pref2.getString(FBAPI, "0");

        if(!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = context.getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB email: "+Login_Email);
        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = context.getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA email: "+Login_Email);
        } else { // 일반
            SharedPreferences pref = context.getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal email: "+Login_Email);
        }
        Log.d(TAG, "UserEmail : "+Login_Email);




        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_drawer_item, parent, false);

        }

        Chat_NaviListItem listViewItem = listViewItemList.get(position);
        TextView tv_Name = (TextView) convertView.findViewById(R.id.tv_Name);
        TextView tv_Friend = (TextView) convertView.findViewById(R.id.tv_Friend);
        ImageView iv_Profile = (ImageView) convertView.findViewById(R.id.iv_Profile);
        Glide.with(context).load(listViewItem.getImage()).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_Profile);
        ibtn_addFriend = (ImageButton) convertView.findViewById(R.id.ibtn_addFriend);


        ibtn_addFriend.setOnClickListener(new View.OnClickListener() { // 친구 추가 버튼 클릭 시 -> 친구 추가 버튼 사라지기
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "친구추가 클릭!!", Toast.LENGTH_SHORT).show();

                // todo ListView Adapter : 포지션값도 제대로 들어갔는데.. 왜 다른 값들이 사라지는 걸까?
                Log.d(TAG, "position : " + position);
                ibtn_addFriend.setVisibility(View.INVISIBLE);
                notifyDataSetChanged();

                String friend_email = listViewItemList.get(position).getEmail();
                Log.d(TAG, "friend_email :  " + friend_email);
                addFriendDB(friend_email, position); // chat_friend DB 테이블에 insert 를 해준다.


            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo 이메일값 넘겨서 상세 정보 보기

                String friend_email = listViewItemList.get(position).getEmail();
                Log.d(TAG, "상세 보기 화면으로 넘길 이메일 friend_email :  " + friend_email);

                Intent intent = new Intent(getApplicationContext(), Chat_UserInfo.class);
                intent.putExtra("email", friend_email);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });


        tv_Name.setText(listViewItem.getName());
        tv_Friend.setText(listViewItem.getFriend());
        ibtn_addFriend.setImageDrawable(listViewItem.getIconDrawable());

        if(listViewItemList.get(position).getFriend().equals("1") || listViewItemList.get(position).getEmail().equals(Login_Email)) {

            ibtn_addFriend.setVisibility(View.GONE);

        }


        return convertView;
    }

    // 나와 친구가 아닌 이메일 주소를 친구 추가하는 부분
    // 보내는 값 : 나의 이메일, 친구 이메일
    // 받는 값 : 업데이트 여부?
    private void addFriendDB(final String friend_email, final int position) {

        String url = "http://115.71.239.151/addFriendDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("addFriendDB parsing", response);
                if(response.equals("0")) { // 쿼리문 성공! 만약 쿼리문이 성공하면,



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

                Log.d(TAG, "전달받은 friend_email : " + friend_email );
                Map<String,String> map = new Hashtable<>();
                map.put("Login_Email", Login_Email);
                map.put("Friend_Email", friend_email);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }





    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
