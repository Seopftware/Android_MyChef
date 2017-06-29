package thread.seopftware.mychef;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login_choose extends AppCompatActivity {

    //FB LOGIN API
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    public static String FB_LOGINCHECK=null;
    public static String KAKAO_LOGINCHECK=null;

    public static final String FACEBOOKLOGIN = "FacebookApi_Login";
    public static final String FBNAME = "FB_NameKey";
    public static final String FBEMAIL = "FB_EmailKey";


    //KAKAO LOGIN API
    private SessionCallback callback;
    public static final String KAKAOLOGIN = "KakaoApi_Login";
    public static final String KANAME = "KA_NameKey";
    public static final String KAEMAIL = "KA_EmailKey";


    private static String TAG="Login_choose";
    public static final String AUTOLOGIN="Auto_login";

    Button btnRegister;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //==========================================================FB LOGIN API START=============================================================
        FacebookSdk.sdkInitialize(getApplicationContext()); // SDK 초기화 (setContentView 보다 먼저 실행되어야함)
        setContentView(R.layout.activity_login_choose);

        SharedPreferences autologin=getSharedPreferences(AUTOLOGIN, Activity.MODE_PRIVATE);
        int status=autologin.getInt("Status", 0);

        // 저장 값들 초기화
        FB_LOGINCHECK=null;
        KAKAO_LOGINCHECK=null;

        //fb api 저장값 초기화
        SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        //ka api 저장값 초기화
        SharedPreferences pref2 = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        SharedPreferences.Editor editor2 = pref2.edit();
        editor2.clear();
        editor2.commit();

        Log.d(TAG, "status: "+status);

            if(status==1) {
                Intent intent=new Intent(getApplicationContext(), Home_user.class);
                startActivity(intent);
                finish();
            }

            else if(status==2) {
                Intent intent=new Intent(getApplicationContext(), Home_chef.class);
                startActivity(intent);
                finish();
            }

        callbackManager = CallbackManager.Factory.create();  //로그인 응답을 처리할 콜백 관리자
        loginButton = (LoginButton)findViewById(R.id.fb_login_button); //페이스북 로그인 버튼

        //유저 정보, 친구정보, 이메일 정보등을 수집하기 위해서는 허가(퍼미션)를 받아야 합니다.
        loginButton.setReadPermissions("public_profile", "email"); //"user_friends"
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) { //로그인 성공시 호출되는 메소드

                Log.e("토큰",loginResult.getAccessToken().getToken());
                Log.e("유저아이디",loginResult.getAccessToken().getUserId());
                Log.e("퍼미션 리스트",loginResult.getAccessToken().getPermissions()+"");

                String UserId=loginResult.getAccessToken().getUserId();
                FB_LOGINCHECK=UserId;

                //loginResult.getAccessToken() 정보를 가지고 유저 정보를 가져올수 있습니다.
                GraphRequest request =GraphRequest.newMeRequest(loginResult.getAccessToken() ,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    Log.e("user profile",object.toString());

                                    String Email = object.getString("email");       // 이메일
                                    String name = object.getString("name");         // 이름
                                    String gender = object.getString("gender");     // 성별

                                    Log.d("TAG","페이스북 이메일 -> " + Email);
                                    Log.d("TAG","페이스북 이름 -> " + name);
                                    Log.d("TAG","페이스북 성별 -> " + gender);

                                    SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString(FBNAME, name);
                                    editor.putString(FBEMAIL, Email);
                                    editor.commit();


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

                // 만약에 db에 id값이 존재 한다면 쉐프 or 유저 화면으로, db에 id값이 존재하지 않는다면 회원가입 선택화면으로.
                CheckFB_Id check=new CheckFB_Id();
                check.execute(FB_LOGINCHECK);

            }

            @Override
            public void onError(FacebookException error) { }

            @Override
            public void onCancel() { }
        });
        //==========================================================FB LOGIN API END=============================================================

        //kakao login api
        /**카카오톡 로그아웃 요청**/
        //한번 로그인이 성공하면 세션 정보가 남아있어서 로그인창이 뜨지 않고 바로 onSuccess()메서드를 호출합니다.
        //테스트 하시기 편하라고 매번 로그아웃 요청을 수행하도록 코드를 넣었습니다 ^^
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                //로그아웃 성공 후 하고싶은 내용 코딩 ~
            }
        });

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);

        btnRegister=(Button) findViewById(R.id.btnRegister);
        btnLogin=(Button) findViewById(R.id.btnLogin);

    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                    String profileUrl = userProfile.getProfileImagePath();
                    String userId = String.valueOf(userProfile.getId());
                    String userName = userProfile.getNickname();
                    String Email= userProfile.getEmail();

                    KAKAO_LOGINCHECK=userId;

                    Log.e("UserProfile", profileUrl);
                    Log.e("UserId", userId);
                    Log.e("UserName", userName);
                    Log.e("Email", Email);

                    SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(KANAME, userName);
                    editor.putString(KAEMAIL, Email);
                    editor.commit();

                    // 만약에 db에 id값이 존재 한다면 쉐프 or 유저 화면으로, db에 id값이 존재하지 않는다면 회원가입 선택화면으로.
                    CheckKA_Id check=new CheckKA_Id();
                    check.execute(KAKAO_LOGINCHECK);

                }
            });

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
        }
    }

    public void onClickedRegister(View v) {
        Intent intent=new Intent(getApplicationContext(), Login_register.class);
        startActivity(intent);
    }

    public void onClickedLogin(View v) {
        Intent intent=new Intent(getApplicationContext(), Login_login.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 페이스북 로그인 결과를 콜백매니저에 담는다
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    class CheckFB_Id extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Login_choose.this, "Please Wait",null,true,true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST fb login response :" +result);
            Log.d(TAG, "POST fb id :" +FB_LOGINCHECK);

            if(Integer.parseInt(result)==1) {
                Intent intent=new Intent(getApplicationContext(), Login_register.class);
                startActivity(intent);
                finish();

            } else if (Integer.parseInt(result)==2) {
                Intent intent=new Intent(getApplicationContext(), Home_user.class);
                startActivity(intent);
                finish();

                SharedPreferences autologin=getSharedPreferences(AUTOLOGIN, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=autologin.edit();
                editor.putInt("Status", 1);
                editor.commit();

            } else if(Integer.parseInt(result)==3) {
                Intent intent=new Intent(getApplicationContext(), Home_chef.class);
                startActivity(intent);
                finish();

                SharedPreferences autologin=getSharedPreferences(AUTOLOGIN, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=autologin.edit();
                editor.putInt("Status", 2);
                editor.commit();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String Fb_api=(String) params[0];

            String serverURL="http://115.71.239.151/check_apiid.php";
            String postParameters = "Fb_api=" +Fb_api;

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
                Log.d(TAG, "POST response code -"+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode== HttpURLConnection.HTTP_OK) {
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
                Log.d(TAG, "InsertData: Error ", e);
                return new String("Error : "+e.getMessage());
            }
        }
    }

    class CheckKA_Id extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Login_choose.this, "Please Wait",null,true,true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST kakao login response :" +result);

            if(Integer.parseInt(result)==1) {
                Intent intent=new Intent(getApplicationContext(), Login_register.class);
                startActivity(intent);
                finish();

            } else if (Integer.parseInt(result)==2) {
                Intent intent=new Intent(getApplicationContext(), Home_user.class);
                startActivity(intent);
                finish();

                SharedPreferences autologin=getSharedPreferences(AUTOLOGIN, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=autologin.edit();
                editor.putInt("Status", 1);
                editor.commit();

            } else if(Integer.parseInt(result)==3) {
                Intent intent=new Intent(getApplicationContext(), Home_chef.class);
                startActivity(intent);
                finish();

                SharedPreferences autologin=getSharedPreferences(AUTOLOGIN, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=autologin.edit();
                editor.putInt("Status", 2);
                editor.commit();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String Ka_api=(String) params[0];

            String serverURL="http://115.71.239.151/check_kakaoapi.php";
            String postParameters = "Ka_api=" +Ka_api;

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
                Log.d(TAG, "POST response code -"+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode== HttpURLConnection.HTTP_OK) {
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
                Log.d(TAG, "InsertData: Error ", e);
                return new String("Error : "+e.getMessage());
            }
        }
    }
}
