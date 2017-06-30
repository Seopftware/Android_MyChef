package thread.seopftware.mychef.HomeChef;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import thread.seopftware.mychef.Login.Login_choose;
import thread.seopftware.mychef.R;
import thread.seopftware.mychef.etc.BackPressCloseHandler;

import static android.graphics.Color.BLACK;
import static thread.seopftware.mychef.Login.Login_choose.AUTOLOGIN;
import static thread.seopftware.mychef.Login.Login_choose.FB_LOGINCHECK;
import static thread.seopftware.mychef.Login.Login_choose.KAKAO_LOGINCHECK;

public class Home_chef extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BackPressCloseHandler backPressCloseHandler; // 백키 구현


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_chef);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("MyChef");

//        final int abTitleId = getResources().getIdentifier("action_bar_title", "id", "android");
//        findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"클릭!! 홈으로", Toast.LENGTH_SHORT).show();
//            }
//        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundColor(BLACK);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), Home_Foodadd.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(R.menu.home_chef, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orderlist) { // 1. 주문현황

        } else if (id == R.id.nav_menu) { // 2. 메뉴 관리

        } else if (id == R.id.nav_chat) { // 3. 1:1 문의 현황

        } else if (id == R.id.nav_account) { // 4. 프로필 설정

        } else if (id == R.id.nav_call) { // 5.고객센터

        } else if (id == R.id.nav_settings) { // 6.환경설정

        } else if (id == R.id.nav_logout)  { // 7. 로그아웃

            SharedPreferences autologin=getSharedPreferences(AUTOLOGIN, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor=autologin.edit();
            editor.clear();
            editor.commit();
            finish();

            if(KAKAO_LOGINCHECK!=null) {
                //카카오톡 로그아웃
                UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent=new Intent(getApplicationContext(), Login_choose.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            else if(FB_LOGINCHECK!=null) {
                //페이스북 로그아웃
                LoginManager.getInstance().logOut();
                Intent intent=new Intent(getApplicationContext(), Login_choose.class);
                startActivity(intent);
                finish();
            }

            else {
                Intent intent=new Intent(getApplicationContext(), Login_choose.class);
                startActivity(intent);
                finish();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
