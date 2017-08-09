package thread.seopftware.mychef.Chatting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class Viewpager_FriendList extends ListFragment {

    private static String TAG = "Viewpager_FriendList";
    ListViewAdapter_ViewPager_FriendList adapter;
    ListViewItem_ViewPager_FriendList listViewItem_list;
    ArrayList<ListViewItem_ViewPager_FriendList> listViewItemList;


    String UserEmail;



    public Viewpager_FriendList() {

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

//        ParseDB();

        for(int i=0; i<10; i++) {
            adapter = new ListViewAdapter_ViewPager_FriendList(listViewItemList);
//            adapter.addItem("인섭", "굿잡", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTkuKlcnEMILJXiRkK2I2J5Ohiw7SfY4pbYkOP9qqcmq7yvL041");
//            adapter.addItem("인섭", "굿잡", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTkuKlcnEMILJXiRkK2I2J5Ohiw7SfY4pbYkOP9qqcmq7yvL041");
//            adapter.addItem("인섭", "굿잡", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTkuKlcnEMILJXiRkK2I2J5Ohiw7SfY4pbYkOP9qqcmq7yvL041");
//            adapter.addItem("인섭", "굿잡", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTkuKlcnEMILJXiRkK2I2J5Ohiw7SfY4pbYkOP9qqcmq7yvL041");
//            adapter.addItem("인섭", "굿잡", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTkuKlcnEMILJXiRkK2I2J5Ohiw7SfY4pbYkOP9qqcmq7yvL041");
//            adapter.addItem("인섭", "굿잡", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTkuKlcnEMILJXiRkK2I2J5Ohiw7SfY4pbYkOP9qqcmq7yvL041");

        }
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                int pos=position;
                long longid=id;

                final CharSequence[] items=new CharSequence[] {"즐겨찾기 추가", "친구 삭제"};
                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                dialog.setTitle("MENU");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(items[which]=="즐겨찾기 추가") {
                            // 채팅창으로 이동
//                            String Customer_Name = tv_UserName.getText().toString();
//                            String Customer_Location = tv_FoodPlace.getText().toString();
//                            Log.d("인텐트 보내는 값", Customer_Name+Customer_Location);
//
//                            Intent intent = new Intent(getContext(), GoogleMapExample.class);
//                            intent.putExtra("Customer_Name", Customer_Name);
//                            intent.putExtra("Customer_Location", Customer_Location);
//                            startActivity(intent);
                        }

                        if(items[which]=="친구 삭제") {
//                            Intent intent = new Intent(getContext(), Navigate_CustomerLocation.class);
//                            startActivity(intent);
//                            DeleteFriend();
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) { // 한번 클릭시 채팅방 띄우기

//        Food_Id = (TextView) v.findViewById(R.id.tv_Food_Id);
//        String Id=Food_Id.getText().toString();
//        Log.e("listview","food menu id 값 : "+Id);
//
//        Intent intent=new Intent(getContext(), Home_Foodlook.class);
//        intent.putExtra("Id", Id);
//        startActivity(intent);

        super.onListItemClick(l, v, position, id);
    }

    // db 데이터 로드
    private void ParseDB() {

        String url = "http://115.71.239.151/.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing1", response);

                try {
                    listViewItemList = new ArrayList<ListViewItem_ViewPager_FriendList>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if(jsonArray.length()==0) {
                        setListShown(true);
                        return;

                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {

//                            JSONObject jo = jsonArray.getJSONObject(i);
//
//                            String Food_Id=jo.getString("Food_Id");
//                            String Food_Image = jo.getString("Food_Image");
//                            String Food_Name = jo.getString("Food_Name");
//                            String Food_Count= jo.getString("Food_Count");
//                            String Food_Date = jo.getString("Food_Date");
//                            String Food_Time = jo.getString("Food_Time");
//                            String Food_Place = jo.getString("Food_Place");
//                            String User_Name = jo.getString("User_Name");
//
//                            listViewItem_list = new ListViewItem_ViewPager_FriendList();
//                            listViewItem_list.setChef_Name(User_Name+" 고객님");
//                            listViewItem_list.setFood_Id(Food_Id);
//                            listViewItem_menu.setChef_Profile("http://115.71.239.151/" + Food_Image);
//                            listViewItemList.add(listViewItem_menu);

                        }
                        adapter = new ListViewAdapter_ViewPager_FriendList(listViewItemList);
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
