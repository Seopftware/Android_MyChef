package thread.seopftware.mychef.HomeChef;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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

public class OrderList_chef_viewpager2 extends ListFragment {

    private static String TAG = "OrderList_chef_pager";
    ListViewAdapter_Chef_ViewPager adapter;
    ListViewItem_Chef_ViewPager listViewItem_menu;
    ArrayList<ListViewItem_Chef_ViewPager> listViewItemList;
    String Id; // 음식 메뉴 고유 id

    String UserEmail;

    TextView Food_Id, Chef_Number, tv_Food_Name;
    String Food_Name;

    int pos;
    long longid;


    public OrderList_chef_viewpager2() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

        ParseDB();
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    // 한번 클릭 시 해당 메뉴 상세 보기 화면 으로
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Food_Id = (TextView) v.findViewById(R.id.tv_Food_Id);
        String Id=Food_Id.getText().toString();
        Log.e("listview","food menu id 값 : "+Id);

        Intent intent=new Intent(getContext(), Home_Foodlook.class);
        intent.putExtra("Id", Id);
        startActivity(intent);

        super.onListItemClick(l, v, position, id);
    }

    // db 데이터 로드
    private void ParseDB() {

        String url = "http://115.71.239.151/Orderlist_Chef_Parsing2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing1", response);

                try {
                    listViewItemList = new ArrayList<ListViewItem_Chef_ViewPager>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if(jsonArray.length()==0) {
                        setListShown(true);
                        return;

                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jo = jsonArray.getJSONObject(i);

                            String Food_Id=jo.getString("Food_Id");
                            String Food_Image = jo.getString("Food_Image");
                            String Food_Name = jo.getString("Food_Name");
                            String Food_Count= jo.getString("Food_Count");
                            String Food_Date = jo.getString("Food_Date");
                            String Food_Time = jo.getString("Food_Time");
                            String Food_Place = jo.getString("Food_Place");

                            String User_Name = jo.getString("User_Name");

                            listViewItem_menu = new ListViewItem_Chef_ViewPager();
                            listViewItem_menu.setChef_Name(User_Name+" 고객님");

                            listViewItem_menu.setFood_Id(Food_Id);
                            listViewItem_menu.setFood_Name(Food_Name);
                            listViewItem_menu.setFood_Count(Food_Count+" (인분)");
                            listViewItem_menu.setFood_Date(Food_Date);
                            listViewItem_menu.setFood_Time(Food_Time);
                            listViewItem_menu.setFood_Place(Food_Place);
                            listViewItem_menu.setChef_Profile("http://115.71.239.151/" + Food_Image);
                            listViewItemList.add(listViewItem_menu);

                        }
                        adapter = new ListViewAdapter_Chef_ViewPager(listViewItemList);
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
