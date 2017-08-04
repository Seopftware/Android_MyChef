package thread.seopftware.mychef.HomeUser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class OrderList_viewpager3 extends ListFragment {

    private static String TAG="OrderList_viewpager";
    ListViewAdapter_User_ViewPager adapter;
    ListViewItem_User_ViewPager listViewItem_menu;
    ArrayList<ListViewItem_User_ViewPager> listViewItemList;
    String Id; // 음식 메뉴 고유 id

    String SearchWord;
    String UserEmail;

    TextView Food_Id, Chef_Number, tv_Food_Name;
    String Food_Name;


    public OrderList_viewpager3() {

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
        super.onActivityCreated(savedInstanceState);
    }


    // db 데이터 로드
    private void ParseDB() {

        String url = "http://115.71.239.151/Orderlist_User_Parsing3.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing3", response);

                try {
                    listViewItemList = new ArrayList<ListViewItem_User_ViewPager>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if(jsonArray.length()==0) {
                        setListShown(true);
                        return;

                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jo = jsonArray.getJSONObject(i);

                            String Chef_Name = jo.getString("Chef_Name");
                            String Chef_Profile = jo.getString("Food_Image");
                            String Chef_Phone = jo.getString("Chef_Phone");

                            String Food_Id = jo.getString("Food_Id");
                            String Food_Name = jo.getString("Food_Name");
                            String Food_Count = jo.getString("Food_Count");
                            String Food_Date = jo.getString("Food_Date");
                            String Food_Time = jo.getString("Food_Time");
                            String Food_Place = jo.getString("Food_Place");

                            listViewItem_menu = new ListViewItem_User_ViewPager();
                            listViewItem_menu.setChef_Name(Chef_Name + " 쉐프님");
                            listViewItem_menu.setChef_Number(Chef_Phone + " 쉐프님");

                            listViewItem_menu.setFood_Id(Food_Id);
                            listViewItem_menu.setFood_Name(Food_Name);
                            listViewItem_menu.setFood_Count(Food_Count + " (인분)");
                            listViewItem_menu.setFood_Date(Food_Date);
                            listViewItem_menu.setFood_Time(Food_Time);
                            listViewItem_menu.setFood_Place(Food_Place);
                            listViewItem_menu.setChef_Profile("http://115.71.239.151/" + Chef_Profile);
                            listViewItemList.add(listViewItem_menu);

                            adapter = new ListViewAdapter_User_ViewPager(listViewItemList);
                            setListAdapter(adapter);
                        }
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
                map.put("Status", "0");

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

        ParseDB(); // db 데이터 갱신
    }
}
