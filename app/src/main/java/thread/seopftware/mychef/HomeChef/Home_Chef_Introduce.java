package thread.seopftware.mychef.HomeChef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import thread.seopftware.mychef.R;


public class Home_Chef_Introduce extends AppCompatActivity {

    ImageView iv_Profile;
    TextView tv_ChefName, tv_ChefIntroduce;

    String Profile, Name, Introduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_chef_introduce);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("쉐프 소개");

        iv_Profile= (ImageView) findViewById(R.id.iv_Profile);
        tv_ChefName= (TextView) findViewById(R.id.tv_ChefName);
        tv_ChefIntroduce= (TextView) findViewById(R.id.tv_ChefIntroduce);

        Intent intent=getIntent();
        Profile=intent.getExtras().getString("Profile");
        Name=intent.getExtras().getString("Name");
        Introduce=intent.getExtras().getString("Introduce");

        Glide.with(getApplicationContext()).load("http://115.71.239.151/"+Profile).into(iv_Profile); // 쉐프 사진
        tv_ChefName.setText(Name+" 쉐프");
        tv_ChefIntroduce.setText(Introduce);
    }

    //액션바 백키 버튼 구현
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
