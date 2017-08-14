package thread.seopftware.mychef.Chatting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
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

public class Viewpager_FriendList extends Fragment {

    private static String TAG = "Viewpager_FriendList";
    ListViewAdapter_ViewPager_FriendList adapter;
    ListViewItem_ViewPager_FriendList listViewItem_list;
    ArrayList<ListViewItem_ViewPager_FriendList> listViewItemList;
    ListView listView;

    String UserEmail;


    ImageView iv_Profile;
    TextView tv_Name, tv_Message;


    public Viewpager_FriendList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_viewpager_friendlist, container, false);
        getActivity().setTitle("MyChef_Chat");

        listView= (ListView) rootview.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                TextView tv_Email= (TextView) view.findViewById(R.id.tv_Email);


                String email = tv_Email.getText().toString();

                Log.d(TAG, "email : " + email);
                Intent intent = new Intent(getContext(), Chat_UserInfo.class);
                intent.putExtra("email", email);
                startActivity(intent);

            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id1) {

                final CharSequence[] items=new CharSequence[] {"수정하기", "삭제하기"};
                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                dialog.setTitle("MENU");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(items[which]=="삭제하기") {

                            TextView txt = (TextView) view.findViewById(R.id.tv_Email);
                            String email =txt.getText().toString();
                            Log.e("listview","email : "+ email);

//                            DeleteDB();
//
//                            for(int i=0; i<listViewItemList.size();i++){
//                                if(listViewItemList.get(position).getId().equals(listViewItemList.get(i).getId())){
//                                    listViewItemList.remove(position);
//                                    adapter.notifyDataSetChanged();
//                                }
//                            }
                        }
                    }
                });
                dialog.show();

                return true;
            }
        });

        iv_Profile= (ImageView) rootview.findViewById(R.id.iv_Profile);
        tv_Name= (TextView) rootview.findViewById(R.id.tv_Name);
        tv_Message= (TextView) rootview.findViewById(R.id.tv_Message);

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
            UserEmail=pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB chefemail: "+UserEmail);
        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getContext().getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            UserEmail=pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA chefemail: "+UserEmail);
        } else { // 일반
            SharedPreferences pref = getContext().getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            UserEmail=pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal chefemail: "+UserEmail);
        }
        Log.d(TAG, "UserEmail : "+UserEmail);

        MyInfoDB();


    }

    // db 데이터 로드
    private void MyInfoDB() {

        String url = "http://115.71.239.151/Chatting_FriendList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing1", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    JSONObject jo = jsonArray.getJSONObject(0);



                    String profile = jo.getString("profile");
                    String name = jo.getString("name");
                    String message = jo.getString("message");

                    tv_Name.setText(name); // 이름
                    tv_Message.setText(message);
                    Glide.with(getContext()).load("http://115.71.239.151/"+profile).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_Profile); // 프사


                    ParseDB(); // DB로부터 나의 친구 리스트 가져오기기

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
                map.put("User_Email", UserEmail);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    // db 데이터 로드
    private void ParseDB() {

        String url = "http://115.71.239.151/Chatting_FriendList2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing1", response);

                try {
                    listViewItemList = new ArrayList<ListViewItem_ViewPager_FriendList>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jo = jsonArray.getJSONObject(i);

                            String email = jo.getString("email");
                            String name = jo.getString("name");
                            String profile = jo.getString("profile");
                            String message = jo.getString("message");

                            listViewItem_list = new ListViewItem_ViewPager_FriendList();
                            listViewItem_list.setEmail(email);
                            listViewItem_list.setName(name);
                            listViewItem_list.setMessage(message);
                            listViewItem_list.setProfile("http://115.71.239.151/" + profile);
                            listViewItemList.add(listViewItem_list);

                        }
                        adapter = new ListViewAdapter_ViewPager_FriendList(getContext(), R.layout.listview_chat_friendlist, listViewItemList);
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
                map.put("User_Email", UserEmail);

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

        ParseDB(); // db 데이터 삽입
    }
}
