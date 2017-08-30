package thread.seopftware.mychef.FCMessanging;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.Hashtable;
import java.util.Map;

import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLEMAIL;
import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLOGIN;
import static thread.seopftware.mychef.Login.Login_login.FACEBOOKLOGIN;
import static thread.seopftware.mychef.Login.Login_login.FBAPI;
import static thread.seopftware.mychef.Login.Login_login.FBEMAIL;
import static thread.seopftware.mychef.Login.Login_login.FB_LOGINCHECK;
import static thread.seopftware.mychef.Login.Login_login.KAAPI;
import static thread.seopftware.mychef.Login.Login_login.KAEMAIL;
import static thread.seopftware.mychef.Login.Login_login.KAKAOLOGIN;
import static thread.seopftware.mychef.Login.Login_login.KAKAO_LOGINCHECK;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstanceID";
    String email = null;

    // START refresh_token

    // 등록된 토큰 번호가 변경 되었을 경우 발생하는 함수 (
    // 사용자가 앱을 재설치 했을 경우.. or 새로운 폰에서 다운 받았을 경우
    // 업데이트 되는 token 번호를 서버에서 관리해줘야함

    @Override
    public void onTokenRefresh() {
        // Get Updated InstanceID token
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token : " + token);

        // 세션 유지를 위한 이메일 값 불러들이기
        SharedPreferences pref1 = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK = pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK = pref2.getString(FBAPI, "0");

        if (!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            email = pref.getString(FBEMAIL, "");
        } else if (!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            email = pref.getString(KAEMAIL, "");
        } else { // 일반
            SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            email = pref.getString(CHEFNORMALLEMAIL, "");
        }


        sendTokenNumberDB(token);
    }

    // 보내고자 하는 사진을 서버에 업로드
    private void sendTokenNumberDB(final String token) {
        String url = "http://115.71.239.151/sendTokenNumberDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "sendTokenNumberDB response : "+response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> map = new Hashtable<>();

                map.put("token", token); // 토큰 번호
                map.put("email", email); // 로그인 이메일 (보내는 사람 이메일)
                return map;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
