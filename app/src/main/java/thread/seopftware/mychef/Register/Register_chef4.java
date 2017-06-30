package thread.seopftware.mychef.Register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import thread.seopftware.mychef.R;

public class Register_chef4 extends AppCompatActivity {

    private static String TAG="Register_chef4";


    public static final String REGISTER_CHEF4="Register_Chef4";
    public static final String APPEAL="APPEALKey";
    public static final String APPEAL2="APPEAL2Key";

    EditText et_Appeal, et_Appeal2;
    Button btn_Register4Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_chef4);

        et_Appeal= (EditText) findViewById(R.id.et_Appeal);
        et_Appeal2= (EditText) findViewById(R.id.et_Appeal2);
        btn_Register4Next= (Button) findViewById(R.id.btn_Register4Next);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("요리 실력 어필");
    }

    //액션바 빽키 구현
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickedNext(View v) {

        // 나를 표현하는 한마디 입력 안했을 때
        if(et_Appeal.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(),"나를 표현하는 한마디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_Appeal.requestFocus();
            return;
        }

        //  자기 소개 입력 안했을 때
        if(et_Appeal2.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(),"자기소개 란을 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_Appeal2.requestFocus();
            return;
        }

        // SharedPreference에 값 담기. 마지막 회원가입 화면에서 값 불러들이기
        SharedPreferences pref=getSharedPreferences(REGISTER_CHEF4, MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();

        editor.putString(APPEAL, et_Appeal.getText().toString());
        editor.putString(APPEAL2, et_Appeal2.getText().toString());
        editor.commit();

        Log.d(TAG, "appeal: "+et_Appeal.getText().toString()+" appeal 2:"+et_Appeal2.getText().toString());

        Intent intent=new Intent(getApplicationContext(), Register_chef5.class);
        startActivity(intent);
    }
}
