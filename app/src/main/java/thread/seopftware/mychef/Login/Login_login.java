package thread.seopftware.mychef.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import thread.seopftware.mychef.Chatting.Chat_Service;
import thread.seopftware.mychef.HomeChef.Home_chef;
import thread.seopftware.mychef.HomeUser.Home_user;
import thread.seopftware.mychef.R;

import static thread.seopftware.mychef.Login.Login_choose.AUTOLOGIN;

public class Login_login extends AppCompatActivity {

    private static String TAG = "Login_login";
    public static final String CHEFNORMALLOGIN="Chef_NormaLoginkey";
    public static final String CHEFNORMALLEMAIL="Chef_NormaLoginkey";
    public static String FB_LOGINCHECK="0";
    public static String KAKAO_LOGINCHECK="0";

    public static final String FACEBOOKLOGIN = "FacebookApi_Login";
    public static final String FBNAME = "FB_NameKey";
    public static final String FBEMAIL = "FB_EmailKey";
    public static final String FBAPI = "FB_ApiKey";


    //KAKAO LOGIN API

    public static final String KAKAOLOGIN = "KakaoApi_Login";
    public static final String KANAME = "KA_NameKey";
    public static final String KAEMAIL = "KA_EmailKey";
    public static final String KAAPI = "KA_ApiKey";


    EditText et_Email, et_Password;
    RadioGroup rg_Choose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("로그인");

        et_Email= (EditText) findViewById(R.id.et_Email);
        et_Password= (EditText) findViewById(R.id.et_Password);
        rg_Choose= (RadioGroup) findViewById(R.id.rg_Choose);

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

    public void onClickedLogin(View v) {

        int id=rg_Choose.getCheckedRadioButtonId();
        RadioButton rb=(RadioButton) findViewById(id);


        Log.d(TAG, "radio button : "+rb.getText().toString());
        String choose=rb.getText().toString();

        String InputEmail=et_Email.getText().toString();
        String InputPassword=et_Password.getText().toString();


        if(choose.equals("user")) {
            Login_UserCheck login=new Login_UserCheck();
            login.execute(InputEmail, InputPassword);


        } else if(choose.equals("chef")) {
            Login_ChefCheck login=new Login_ChefCheck();
            login.execute(InputEmail, InputPassword);
        }

        SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(CHEFNORMALLEMAIL, et_Email.getText().toString());
        editor.commit();


    }

    class Login_UserCheck extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Login_login.this, "Please Wait",null,true,true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d(TAG, "POST response :" +result);

            if(Integer.parseInt(result)==2) {

                SharedPreferences autologin=getSharedPreferences(AUTOLOGIN, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=autologin.edit();
                editor.putInt("Status", 1);
                editor.commit();

                Toast.makeText(getApplicationContext(), "소켓 서비스 시작", Toast.LENGTH_SHORT).show();
                Intent intent1=new Intent(Login_login.this, Chat_Service.class);
                intent1.putExtra("command", "0");
                Log.d("페북 로그인", "여기 지나감");
                startService(intent1);

                Intent intent=new Intent(Login_login.this, Home_user.class);
                startActivity(intent);
                finish();




            } else if (Integer.parseInt(result)==1) {
                Toast.makeText(getApplicationContext(), "이메일 또는 비밀번호를 다시 확인해주세요." , Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String InputEmail=(String) params[0];
            String InputPassword=(String) params[1];

            String serverURL="http://115.71.239.151/idcheck.php";
            String postParameters = "InputEmail=" +InputEmail+" &InputPassword="+InputPassword;

            Log.d(TAG, "postParameters : "+postParameters);

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

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error :", e);
                return new String("Error : "+e.getMessage());
            }
        }
    }

    class Login_ChefCheck extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Login_login.this, "Please Wait",null,true,true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response :" +result);


            if(Integer.parseInt(result)==2) {

                SharedPreferences autologin=getSharedPreferences(AUTOLOGIN, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=autologin.edit();
                editor.putInt("Status", 2);
                editor.commit();

                Intent intent=new Intent(Login_login.this, Home_chef.class);
                startActivity(intent);
                finish();

            } else if (Integer.parseInt(result)==1) {
                Toast.makeText(getApplicationContext(), "이메일 또는 비밀번호를 다시 확인해주세요." , Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String InputEmail=(String) params[0];
            String InputPassword=(String) params[1];

            String serverURL="http://115.71.239.151/login_chef.php";
            String postParameters = "InputEmail=" +InputEmail+" &InputPassword="+InputPassword;

            Log.d(TAG, "postParameters : "+postParameters);

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

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error :", e);
                return new String("Error : "+e.getMessage());
            }
        }
    }

}
