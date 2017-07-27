package thread.seopftware.mychef.HomeUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import thread.seopftware.mychef.R;

public class User_Payment_CardRegister extends AppCompatActivity {

    private static String TAG="User_Payment_CardRegister";

    Button btn_Payment;

    RadioGroup radioGroup;
    LinearLayout person;
    LinearLayout company;

    EditText et_CardName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cardregister);


        radioGroup= (RadioGroup) findViewById(R.id.radiogroup);

        person= (LinearLayout) findViewById(R.id.person);
        company= (LinearLayout) findViewById(R.id.company);

        et_CardName= (EditText) findViewById(R.id.et_CardName);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (group.getId() == R.id.radiogroup) {
                    switch (checkedId) {
                        case R.id.rb_Person:
                            person.setVisibility(View.VISIBLE);
                            company.setVisibility(View.GONE);
                            break;
                        case R.id.rb_Company:
                            person.setVisibility(View.GONE);
                            company.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        });

        // 유저 정보
        btn_Payment= (Button) findViewById(R.id.btn_Payment);
        btn_Payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 이메일 입력 안했을 때
                if (et_CardName.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    et_CardName.requestFocus();
                    return;
                }

                Bundle extra = new Bundle();
                Intent intent = new Intent();
                extra.putString("cardname", et_CardName.getText().toString());
                intent.putExtras(extra);
                setResult(RESULT_OK, intent);
                finish();

                Toast.makeText(getApplicationContext(), "카드 등록을 완료 했습니다.", Toast.LENGTH_SHORT).show();
            }
        });


        //액션바 설정 부분
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("카드등록");

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
