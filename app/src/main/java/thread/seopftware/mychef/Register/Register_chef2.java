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

public class Register_chef2 extends AppCompatActivity {

    public static final String REGISTER_CHEF2="Register_Chef2";
    public static final String COMPANYNAME="CompanyNameKey";
    public static final String COMPANYSTART="CompanyStartKey";
    public static final String COMPANYEND="CompanyEndKey";
    public static final String COMPANYDESCRIPTION="CompanyDescriptionKey";

    EditText et_CompanyName, et_CompanyStart, et_CompanyEnd, et_CompanyDescription;
    Button btn_Register2Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_chef2);

        et_CompanyName= (EditText) findViewById(R.id.et_CompanyName);
        et_CompanyStart= (EditText) findViewById(R.id.et_CompanyStart);
        et_CompanyEnd= (EditText) findViewById(R.id.et_CompanyEnd);
        et_CompanyDescription= (EditText) findViewById(R.id.et_CompanyDescription);
        btn_Register2Next= (Button) findViewById(R.id.btn_Register2Next);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); // 액션바 뒤로가기 버튼 나타내기
        actionBar.setTitle("경력사항");
    }

    public void onClickedNext(View v) {

        // 업체명 입력 안했을 때
        if(et_CompanyName.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(),"업체명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_CompanyName.requestFocus();
            return;
        }

        // 근무 시작일 입력 안했을 때
        if(et_CompanyStart.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(),"근무 시작 날짜를 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_CompanyStart.requestFocus();
            return;
        }

        // 근무 종료일 입력 안했을 때
        if(et_CompanyEnd.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(),"근무 종료 날짜 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_CompanyEnd.requestFocus();
            return;
        }

        // 주요 업무 입력 안했을 때
        if(et_CompanyDescription.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(),"주요 업무를 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_CompanyDescription.requestFocus();
            return;
        }

        // SharedPreference에 값 담기. 마지막 회원가입 화면에서 값 불러들이기
        SharedPreferences pref=getSharedPreferences(REGISTER_CHEF2, MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();

        editor.putString(COMPANYNAME, et_CompanyName.getText().toString());
        editor.putString(COMPANYSTART, et_CompanyStart.getText().toString());
        editor.putString(COMPANYEND, et_CompanyEnd.getText().toString());
        editor.putString(COMPANYDESCRIPTION, et_CompanyDescription.getText().toString());
        editor.commit();

        Intent intent=new Intent(getApplicationContext(), Register_chef3.class);
        startActivity(intent);

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
}
