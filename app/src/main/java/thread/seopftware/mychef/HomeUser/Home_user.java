package thread.seopftware.mychef.HomeUser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import thread.seopftware.mychef.HomeChef.Fragment4_Call;
import thread.seopftware.mychef.Login.Login_choose;
import thread.seopftware.mychef.R;
import thread.seopftware.mychef.etc.BackPressCloseHandler;

import static thread.seopftware.mychef.Login.Login_choose.AUTOLOGIN;
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

public class Home_user extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG="Home_user";

    private BackPressCloseHandler backPressCloseHandler; // 백키 구현

    String UserEmail;
    String Name;

    TextView tv_UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_userview);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        tv_UserName= (TextView) headerview.findViewById(R.id.tv_UserName);

        getName();

        displaySelectedScreen(R.id.nav_menu);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment fragment=null;

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.d("오긴오나?", "음..");

            fragment= new Fragment_User_Search();

            if(fragment!=null) {
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        displaySelectedScreen(item.getItemId());

        return true;
    }

    // 이메일 값 보내고 이름 받아오기
    // 쉐프 프로필 정보 받아오는 함수
    private void getName() {

        String url = "http://115.71.239.151/User_getName.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    // 데이터 불러들이기
                    Name=jo.getString("name"); // 0

                    // 데이터 뷰에 입력시키기
                    tv_UserName.setText(Name);
                    Toast.makeText(getApplicationContext(), Name+"유저님 환영합니다 !!", Toast.LENGTH_LONG).show();

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
                Map<String,String> map = new Hashtable<>();
                map.put("User_Email", UserEmail);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void displaySelectedScreen(int itemId) {
        Fragment fragment=null;

        switch (itemId) {
            case R.id.nav_menu: // 모든 메뉴 보기
                fragment = new Fragment_User_Menu();
                break;

            case R.id.nav_cartlist: // 장바구니 목록 및 결제 화면
                fragment = new Fragment_User_Cart();
                break;

            case R.id.nav_orderlist: // 주문 내역
                fragment = new Fragment1_User_Order();
                break;

            case R.id.nav_chat: // 1:1 문의
                fragment = new Fragment2_User_Chat();
                break;

            case R.id.nav_call: // 고객센터
                fragment = new Fragment4_Call();
                break;

            case R.id.nav_settings: // 환경 설정
                fragment = new Fragment4_User_Setting();
                break;

            case R.id.nav_logout: // 로그아웃
                SharedPreferences autologin=getSharedPreferences(AUTOLOGIN, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=autologin.edit();
                editor.clear();
                editor.commit();
                finish();

                if(!KAKAO_LOGINCHECK.equals("0")) {
                    //카카오톡 로그아웃
                    UserManagement.requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            Intent intent=new Intent(getApplicationContext(), Login_choose.class);
                            startActivity(intent);
                            finish();

                            KAKAO_LOGINCHECK="0";

                            //ka api 저장값 초기화
                            SharedPreferences pref2 = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = pref2.edit();
                            editor2.clear();
                            editor2.commit();


                        }
                    });
                }

                else if(!FB_LOGINCHECK.equals("0")) {
                    //페이스북 로그아웃
                    LoginManager.getInstance().logOut();
                    Intent intent1 = new Intent(getApplicationContext(), Login_choose.class);
                    startActivity(intent1);
                    finish();

                    //fb api 저장값 초기화
                    SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = pref.edit();
                    editor1.clear();
                    editor1.commit();
                    FB_LOGINCHECK = "0";
                }

                else {
                    Intent intent2=new Intent(getApplicationContext(), Login_choose.class);
                    startActivity(intent2);
                    finish();
                }
        }

        if(fragment!=null) {
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, fragment);
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }
}