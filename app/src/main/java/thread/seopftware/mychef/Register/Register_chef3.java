package thread.seopftware.mychef.Register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import thread.seopftware.mychef.R;

public class Register_chef3 extends AppCompatActivity {

    public static final String REGISTER_CHEF3="Register_Chef3";
    public static final String CERTIFICATION="CertificationKey";
    public static final String CERTIFICATION2="Certification2Key";
    public static final String CERTIFICATION3="Certification3Key";

    EditText et_Certification, et_Certification2=null, et_Certification3=null;
    Button btn_Register3Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_chef3);

        et_Certification= (EditText) findViewById(R.id.et_Certification);
        et_Certification2= (EditText) findViewById(R.id.et_Certification2);
        et_Certification3= (EditText) findViewById(R.id.et_Certification3);
        btn_Register3Next= (Button) findViewById(R.id.btn_Register3Next);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("자격증 보유 현황");
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

        // 자격증 입력 안했을 때
        if(et_Certification.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(),"자격증을 하나 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_Certification.requestFocus();
            return;
        }

        // SharedPreference에 값 담기. 마지막 회원가입 화면에서 값 불러들이기
        SharedPreferences pref=getSharedPreferences(REGISTER_CHEF3, MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();

        editor.putString(CERTIFICATION, et_Certification.getText().toString());
        editor.putString(CERTIFICATION2, et_Certification2.getText().toString());
        editor.putString(CERTIFICATION3, et_Certification3.getText().toString());
        editor.commit();


        Intent intent=new Intent(getApplicationContext(), Register_chef4.class);
        startActivity(intent);
    }
}
