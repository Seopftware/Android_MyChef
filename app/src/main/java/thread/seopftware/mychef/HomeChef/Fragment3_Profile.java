package thread.seopftware.mychef.HomeChef;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import thread.seopftware.mychef.Chef_Change_Appeal;
import thread.seopftware.mychef.Chef_Change_Password;
import thread.seopftware.mychef.R;

import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;
import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLEMAIL;
import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLOGIN;
import static thread.seopftware.mychef.Login.Login_login.FACEBOOKLOGIN;
import static thread.seopftware.mychef.Login.Login_login.FBEMAIL;
import static thread.seopftware.mychef.Login.Login_login.FB_LOGINCHECK;
import static thread.seopftware.mychef.Login.Login_login.KAAPI;
import static thread.seopftware.mychef.Login.Login_login.KAEMAIL;
import static thread.seopftware.mychef.Login.Login_login.KAKAOLOGIN;
import static thread.seopftware.mychef.Login.Login_login.KAKAO_LOGINCHECK;

public class Fragment3_Profile extends Fragment {

    // 갤러리 불러들이기
    static final int REQUEST_ALBUM = 2002;
    Bitmap album_bitmap;
    Uri album_uri;

    ImageView iv_Profile; // 프로필 사진
    TextView tv_Name, tv_Call, tv_Email; // 이름, 연락처, 이메일

    ImageView iv_SNS; // SNS 여부 그림
    TextView tv_SNS; // SNS 여부 텍스트

    String ChefEmail; // 세션 유지용 함수
    String Name, Call, Email, first_image; // 이름, 전화, 이메일, 프사

    // 생성자
    public Fragment3_Profile() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("MyChef_Profile"); // 타이틀 변경

        View view = inflater.inflate(R.layout.activity_fragment3__profile, container, false);
        TextView tv = (TextView) view.findViewById(R.id.tv_SNS); // SNS 로그인 여부에 따른 텍스트 변경
        ImageView iv= (ImageView) view.findViewById(R.id.iv_SNS);
        View lastview= (View) view.findViewById(R.id.lastview);
        LinearLayout SecondLinear=(LinearLayout) view.findViewById(R.id.SecondLinear);

        // 어필 변경
        LinearLayout appealchange=(LinearLayout) view.findViewById(R.id.appealchange);
        appealchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), Chef_Change_Appeal.class);
                startActivity(intent);

            }
        });

        LinearLayout passwordchange=(LinearLayout) view.findViewById(R.id.passwordchange);
        passwordchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), Chef_Change_Password.class);
                startActivity(intent);

            }
        });

        SharedPreferences pref1 = getActivity().getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getActivity().getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK=pref2.getString(KAAPI, "0");

        Log.d(TAG, "FB_LOGINCHECK : "+FB_LOGINCHECK);
        Log.d(TAG, "KAKAO_LOGINCHECK : "+KAKAO_LOGINCHECK);

        if(!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getActivity().getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            ChefEmail=pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB chefemail: "+ChefEmail);


            tv.setText("페이스북"); // 텍스트 변경
            tv.setTextColor(Color.parseColor("#3B5999")); // 텍스트 색상 변경
            iv.setImageResource(R.drawable.facebooklogo); // 이미지 변경

            // SNS 로그인시 비밀번호 변경 안되게끔
            SecondLinear.removeView(lastview);
            SecondLinear.removeViewInLayout(passwordchange);


        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getActivity().getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            ChefEmail=pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA chefemail: "+ChefEmail);

            tv.setText("카카오톡");
            tv.setTextColor(Color.parseColor("#FFE400"));
            iv.setImageResource(R.drawable.kakaotalk);

            // SNS 로그인시 비밀번호 변경 안되게끔
            SecondLinear.removeView(lastview);
            SecondLinear.removeViewInLayout(passwordchange);

        } else { // 일반
            SharedPreferences pref = getActivity().getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            ChefEmail=pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal chefemail: "+ChefEmail);
            tv.setText("연동 없음");
        }


        iv_Profile= (ImageView) view.findViewById(R.id.iv_Profile); // 프로필 사진
        iv_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChoose();
            }
        });
        tv_Name= (TextView) view.findViewById(R.id.tv_Name);
        tv_Call= (TextView) view.findViewById(R.id.tv_Call);
        tv_Email= (TextView) view.findViewById(R.id.tv_Email);

        iv_SNS= (ImageView) view.findViewById(R.id.iv_SNS);
        tv_SNS= (TextView) view.findViewById(R.id.tv_SNS);

        getDataFromDB(); // 사진, 이름, 연락처, 이메일, SNS 여부 판별하기.

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    // 갤러리
    private void showFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_ALBUM);
    }

    private String getStringImage (Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_ALBUM:

                album_uri = data.getData();
                Glide.with(this).load(album_uri).bitmapTransform(new CropCircleTransformation(getContext())).into(iv_Profile);
                try {
                    //Getting the Bitmap from Gallery
                    album_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), album_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                photoUpdateDB(); // 겔러리에서 사진 선택시 바로바로 DB에 업로드 되게끔.

                break;

        }
    }

    // 프로필 사진 업데이트 함수
    private void photoUpdateDB() {

        String url = "http://115.71.239.151/Chef_PicUpdate.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "photoUpdateDB response : "+response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String imagePath = getStringImage(album_bitmap);
                String imageName = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";


                Map<String,String> map = new Hashtable<>();
                map.put("ChefEmail",ChefEmail);
                map.put("imagePath", imagePath);
                map.put("imageName", imageName);
                return map;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // 쉐프 프로필 정보 받아오는 함수
    private void getDataFromDB() {

        String url = "http://115.71.239.151/Chef_ProfileParsing.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    // 데이터 불러들이기
                    Name=jo.getString("name"); // 0
                    Call=jo.getString("phone"); // 1
                    Email=jo.getString("email"); // 2
                    first_image=jo.getString("photostring"); // 3

                    // 데이터 뷰에 입력시키기
                    tv_Name.setText(Name);
                    tv_Call.setText(Call);
                    tv_Email.setText(Email);
                    Glide.with(getContext()).load("http://115.71.239.151/"+first_image).bitmapTransform(new CropCircleTransformation(getContext())).into(iv_Profile);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d(TAG, "쉐프 Email : "+ChefEmail);
                Map<String,String> map = new Hashtable<>();
                map.put("Chef_Email", ChefEmail);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }



}
