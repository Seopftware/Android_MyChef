package thread.seopftware.mychef;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register_chef extends AppCompatActivity {

    private static String TAG="Register_chef";


    EditText et_Name, et_Email, et_Password, et_PasswordConfirm;
    Button btn_EmailCheck;
    HttpTask task;



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

        et_Name= (EditText) findViewById(R.id.et_Name);
        et_Email= (EditText) findViewById(R.id.et_Email);
        et_Password= (EditText) findViewById(R.id.et_Password);
        et_PasswordConfirm= (EditText) findViewById(R.id.et_PasswordConfirm);
        btn_EmailCheck= (Button) findViewById(R.id.btn_EmailCheck);
        btn_EmailCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=et_Email.getText().toString();
                InsertData task=new InsertData();
                task.execute(Email);
            }
        });


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

        // 이름 입력 안했을 때
        if(et_Name.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(),"이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_Name.requestFocus();
            return;
        }


        // 비밀번호 입력 안했을 때
        if(et_Password.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(), "Password를 입력하세요!", Toast.LENGTH_SHORT).show();
            et_Password.requestFocus();
            return;
        }

        // 비밀번호 정규식 체크
        if(PasswordValid(et_Password.getText().toString())==false) {
            Toast.makeText(getApplicationContext(), "비밀번호는 '특수문자'를 포함하여 '여덟 글자 이상'을 입력하셔야 합니다.", Toast.LENGTH_SHORT).show();
            et_Password.requestFocus();
            return;
        }

        // 비밀번호 확인 입력 안했을 때
        if(et_PasswordConfirm.getText().toString().length()==0) {
            Toast.makeText(getApplicationContext(), "PasswordConfirm를 입력하세요!", Toast.LENGTH_SHORT).show();
            et_PasswordConfirm.requestFocus();
            return;

        }

        // 비밀번호 일치하지 않을 때
        if(!et_Password.getText().toString().equals(et_PasswordConfirm.getText().toString())) {
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            et_Password.setText("");
            et_PasswordConfirm.setText("");
            et_PasswordConfirm.requestFocus();
            return;
        }



//        Intent intent=new Intent(getApplicationContext(), Register_chef2.class);
//        startActivity(intent);
    }


    // 정규식을 이용한 이메일 형식 체크
    private boolean EmailValid(String email) {
        String mail = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(mail);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    // 정규식을 이용한 비밀번호 형식 체크
    private boolean PasswordValid(String password) {
        String password1 ="^[a-zA-Z0-9!@.#$%^&*?_~]{8,16}$";
        Pattern pass =Pattern.compile(password1);
        Matcher word= pass.matcher(password);
        return word.matches();
    }

    // 안드로이드, php 통신
    class HttpTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    //Email 중복체크
    class InsertData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Register_chef.this, "잠시만 기다려 주세요.", null,true,true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d("Register_chef", "POST response :" +result);

            // 이메일 입력 안했을 때
            if(et_Email.getText().toString().length()==0) {
                Toast.makeText(getApplicationContext(),"이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                et_Email.requestFocus();
                return;
            }

            // 이메일 정규식 체크
            else if(EmailValid(et_Email.getText().toString())==false) {
                Toast.makeText(getApplicationContext(), "올바른 E-mail 형식이 아닙니다.\nex)abcd@nova.com", Toast.LENGTH_SHORT).show();
                et_Email.requestFocus();
                return;
            } else {
                if(Integer.parseInt(result)==0) {

                    LinearLayout ll= (LinearLayout) findViewById(R.id.ll3);
                    TextView tv=new TextView(getApplicationContext());
                    ll.removeAllViews();
                    tv.setText(" *사용가능한 아이디 입니다.");
                    tv.setTextSize(10);
                    tv.setTextColor(Color.BLUE);
                    ll.addView(tv);



                } else if(Integer.parseInt(result)==1) {

                    LinearLayout ll= (LinearLayout) findViewById(R.id.ll3);
                    TextView tv=new TextView(getApplicationContext());
                    ll.removeAllViews();
                    tv.setText(" *이미 존재하는 아이이디 입니다.");
                   tv.setTextSize(10);
                    tv.setTextColor(Color.RED);
                    ll.addView(tv);
                }

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String Email=(String) params[0];

            String serverURL="http://115.71.239.151/emailcheck.php";
            String postParameters = "Email=" +Email;

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream=httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode=httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code :"+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode==HttpURLConnection.HTTP_OK) {
                    inputStream=httpURLConnection.getInputStream();
                } else {
                    inputStream=httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader=new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

                StringBuilder sb=new StringBuilder();
                String line=null;

                while((line=bufferedReader.readLine())!=null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

                } catch(Exception e) {
                    Log.d(TAG, "InsertData: Error ", e);
                    return new String("Error : "+e.getMessage());
                }

        }
    }
}
