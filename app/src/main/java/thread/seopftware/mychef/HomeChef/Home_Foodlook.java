package thread.seopftware.mychef.HomeChef;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Hashtable;
import java.util.Map;

import thread.seopftware.mychef.HomeUser.User_Payment;
import thread.seopftware.mychef.R;

import static thread.seopftware.mychef.Login.Login_choose.AUTOLOGIN;
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

public class Home_Foodlook extends AppCompatActivity {

    private static String TAG="Home_Foodlook";

    TextView tv_KoreaName, tv_EnglishName;
    ImageView iv_FoodImage;

    TextView tv_Price, tv_Review;
    RatingBar ratingBar;
    Button btn_Cart;

    ImageView iv_ChefProfile;
    TextView tv_ChefName;
    TextView tv_Description, tv_Ingredients, tv_Area;

    Button btn_Review;

    Intent intent; // 쉐프 상세 소개

    String Id;
    String KoreaName;
    String EnglishName;
    String Price;
    String Description;
    String Ingredients;
    String Area;
    String Chef_Email;
    String imagePath;
    String name;
    String appeal2;
    String photostring;
    String foodmenu_id;
    String UserEmail;

    Button btn_AddCart, btn_PayNow, btn_CartTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_foodlook);

        Intent intent=getIntent();
        foodmenu_id=intent.getExtras().getString("Id");

        SharedPreferences pref1 = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK=pref2.getString(FBAPI, "0");

        if(!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            UserEmail=pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB chefemail: "+UserEmail);
        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            UserEmail=pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA chefemail: "+UserEmail);
        } else { // 일반
            SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            UserEmail=pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal chefemail: "+UserEmail);
        }
        Log.d(TAG, "UserEmail : "+UserEmail);
        Log.d(TAG, "id : "+foodmenu_id);

        iv_FoodImage= (ImageView) findViewById(R.id.iv_Chef_Profile);
        iv_ChefProfile= (ImageView) findViewById(R.id.iv_ChefProfile);
        tv_KoreaName= (TextView) findViewById(R.id.tv_KoreaName);
        tv_EnglishName= (TextView) findViewById(R.id.tv_EnglishName);
        tv_Price= (TextView) findViewById(R.id.tv_Price);
        tv_Review= (TextView) findViewById(R.id.tv_Review);
        tv_ChefName= (TextView) findViewById(R.id.tv_ChefName);
        tv_ChefName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getApplicationContext(), Home_Chef_Introduce.class);
                intent.putExtra("Profile",photostring);
                intent.putExtra("Name", name);
                intent.putExtra("Introduce", appeal2);
                startActivity(intent);

            }
        });

        tv_Description= (TextView) findViewById(R.id.tv_Description);
        tv_Ingredients= (TextView) findViewById(R.id.tv_Ingredients);
        tv_Area= (TextView) findViewById(R.id.tv_Area);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("메뉴 상세 정보");

        btn_AddCart= (Button) findViewById(R.id.btn_AddCart);
        btn_PayNow= (Button) findViewById(R.id.btn_PayNow);
        btn_CartTotal= (Button) findViewById(R.id.btn_CartTotal);

        SharedPreferences autologin=getSharedPreferences(AUTOLOGIN, Activity.MODE_PRIVATE);
        int status=autologin.getInt("Status", 0);

        Log.d("TAG", "Food look status :" + status);
        if (status==2) {

            // 만약 Chef로 화면 클릭 시 결제버튼 막기
            ConstraintLayout ll = (ConstraintLayout) findViewById(R.id.PaymentLayout);
            ll.removeAllViews();
            getDataFromDB();
            Button btn= (Button) findViewById(R.id.btn_CartTotal);
            btn.setVisibility(View.GONE);
        } else {
            getDataFromDB();
            CartTotal();
        }

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

    // 카트에 더하기
    public void onClickedAddCart(View v) {
        AddCartDB();
    }

    // 결제화면으로 바로 넘어가기
    public void onClickedPayNow(View v) {
        CartPayment();
    }

    private void getDataFromDB() {


        String url = "http://115.71.239.151/foodlook.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    Id=jo.getString("id"); // 0
                    KoreaName=jo.getString("KoreaName"); // 1
                    EnglishName=jo.getString("EnglishName"); // 2
                    Price=jo.getString("Price"); // 3
                    Description=jo.getString("Description"); // 4
                    Ingredients=jo.getString("Ingredients"); // 5
                    Area=jo.getString("Area"); // 6
                    Chef_Email=jo.getString("Chef_Email"); // 7
                    imagePath=jo.getString("imagePath"); // 8
                    name=jo.getString("name"); // 9
                    appeal2=jo.getString("appeal2"); // 11
                    photostring=jo.getString("photostring"); // 12

                    // 1. 한글이름, 영어이름, 음식 사진
                    tv_KoreaName.setText(KoreaName);
                    tv_EnglishName.setText(EnglishName);
                    Glide.with(getApplicationContext()).load("http://115.71.239.151/"+imagePath).into(iv_FoodImage);

                    // 2. 레이팅바, 리뷰 갯수, 가격
                    tv_Price.setText(Price+"원");

                    // 3. 쉐프 사진, 쉐프 이름
                    Glide.with(getApplicationContext()).load("http://115.71.239.151/"+photostring).into(iv_ChefProfile);
                    tv_ChefName.setText(name+" 쉐프");

                    // 4. 요리설명, 요리재료, 출장가능 지역
                    tv_Description.setText(Description);
                    tv_Ingredients.setText(Ingredients);
                    tv_Area.setText(Area);


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

                Log.d(TAG, "보내는 ID (foodmenu_id) : "+foodmenu_id);
                Map<String,String> map = new Hashtable<>();
                map.put("Id", foodmenu_id);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void AddCartDB() {

        String url = "http://115.71.239.151/CartAdd.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);

                Button button= (Button) findViewById(R.id.btn_CartTotal);
                Button button1= (Button) findViewById(R.id.btn_AddCart);
                button1.setBackgroundColor(Color.rgb(189, 189, 189));
                button1.setEnabled(false);
                button.setText(response);

                Toast.makeText(getApplicationContext(), KoreaName+"이 장바구니에 추가되었습니다.", Toast.LENGTH_LONG).show();

                if(response.equals("4")) {
                    Toast.makeText(getApplicationContext(), "장바구니는 최대 4개까지만 추가 가능합니다.", Toast.LENGTH_LONG).show();
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

                Map<String,String> map = new Hashtable<>();

                map.put("Food_Id", foodmenu_id);
                map.put("KoreaName", KoreaName); // 2
                map.put("EnglishName", EnglishName); // 3
                map.put("Price", Price); // 4
                map.put("FoodImage", imagePath); // 5
                map.put("Count", "1"); // 6
                map.put("Chef_Email", Chef_Email); // 7
                map.put("User_Email", UserEmail);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void CartTotal() {

        String url = "http://115.71.239.151/CartTotal.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);

                String[] split = response.split("_@#@_"); // Key값에 들어있는 벨류를 꺼내 split() 함수로 분해한다.

                String TotalNum = split[0];
                String BtnPrevent = split[1];

                Log.e(TAG, "총 갯수: " + TotalNum);
                Log.e(TAG, "확인 넘버: " + BtnPrevent); // 0이면 똑같은 메뉴가 있다는 뜻. 1이면 똑같은 메뉴가 없다는 뜻.

                Button button= (Button) findViewById(R.id.btn_CartTotal);
                Button button1= (Button) findViewById(R.id.btn_AddCart);

                if(BtnPrevent.equals("0")) { // 장바구니에 이미 담겨 있을 떄

                    button1.setBackgroundColor(Color.rgb(189, 189, 189));
                    button1.setEnabled(false);
                    button.setText(TotalNum); // 토탈 장바구니 갯수

                } else { // 장바구니에 아이템이 담겨져 있지 않을 때

                    button.setText(TotalNum); // 토탈 장바구니 갯수
                    button1.setBackgroundColor(Color.rgb(255, 0, 0));
                    button1.setEnabled(true);

                }

                if(TotalNum.equals("4")) {
                    button1.setBackgroundColor(Color.rgb(189, 189, 189));
                    button1.setEnabled(false);
                    button.setText(TotalNum); // 토탈 장바구니 갯수
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

                Map<String,String> map = new Hashtable<>();
                map.put("KoreaName", KoreaName); // 2
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void CartPayment() {

        String url = "http://115.71.239.151/CartPayment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);

                Toast.makeText(getApplicationContext(), "결제 화면으로 이동합니다.", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getApplicationContext(), User_Payment.class);
                startActivity(intent);
                finish();
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

                map.put("Food_Id", foodmenu_id);
                map.put("KoreaName", KoreaName); // 2
                map.put("EnglishName", EnglishName); // 3
                map.put("Price", Price); // 4
                map.put("FoodImage", imagePath); // 5
                map.put("Count", "1"); // 6
                map.put("Chef_Email", Chef_Email); // 7
                map.put("User_Email", UserEmail);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
