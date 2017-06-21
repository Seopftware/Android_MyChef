package thread.seopftware.mychef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register_chef extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_chef);

        //액션바 설정 부분
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("회원가입");

        //텍스트뷰 해당 글자에 링크 걸기
        TextView tvLinkify = (TextView) findViewById(R.id.firsttitle);
        String text = "가입하시면 이용약관과 개인정보 취급방침,\n환불 및 취소약관에 동의하신 것으로 간주됩니다.";
        tvLinkify.setText(text);

        Linkify.TransformFilter mTransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return "";
            }
        };

        Pattern pattern1 = Pattern.compile("이용약관");
        Pattern pattern2 = Pattern.compile("개인정보 취급방침");
        Pattern pattern3 = Pattern.compile("환불 및 취소약관");

        Linkify.addLinks(tvLinkify, pattern1, "http://blog.naver.com/manadra", null, mTransform);
        Linkify.addLinks(tvLinkify, pattern2, "http://blog.naver.com/manadra", null, mTransform);
        Linkify.addLinks(tvLinkify, pattern3, "http://blog.naver.com/manadra", null, mTransform);
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

    public void onClickedNext(View V) {
        Intent intent=new Intent(getApplicationContext(), Register_chef2.class);
        startActivity(intent);
    }
}
