package thread.seopftware.mychef.HomeUser;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;

import thread.seopftware.mychef.R;

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

/**
 * Created by MSI on 2017-07-25.
 */

public class User_Payment extends AppCompatActivity {

    private static String TAG="User_Payment";
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    private static final int CARD_REGISTER_ACTIVITY = 20000;


    // 결제 진행 정보
    TextView tv_KoreaName, tv_EnglishName, tv_TotalPrice, tv_Count;
    ImageView iv_FoodImage;
    String KoreaName, EnglishName, Price, imagePath, Count, Chef_Email;
    Button btn_Payment;

    // User Info
    String Name, Phone, UserEmail;
    TextView tv_Name, tv_Phone, tv_Address, tv_Date, tv_Time, tv_CardName;
    int year, month, day, hour, minute;
    GregorianCalendar calendar;

    int TotalPrice;
    RadioGroup radioGroup;
    LinearLayout payment;
    String Payment_method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_payment);

        calendar = new GregorianCalendar();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        // 유저 정보
        tv_Phone= (TextView) findViewById(R.id.tv_Phone);
        tv_Name= (TextView) findViewById(R.id.tv_Name);
        tv_Address= (TextView) findViewById(R.id.tv_Address);
        tv_Date= (TextView) findViewById(R.id.tv_Date);
        tv_Time= (TextView) findViewById(R.id.tv_Time);
        tv_CardName= (TextView) findViewById(R.id.tv_CardName);

        radioGroup= (RadioGroup) findViewById(R.id.radiogroup);


        btn_Payment= (Button) findViewById(R.id.btn_Payment);
        btn_Payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(User_Payment.this);
                dialog.setTitle("결제");
                dialog.setMessage("결제를 진행하시겠습니까?");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OrderList_Chef(); // 쉐프 주문 리스트 DB에 추가 -> FCM 가게 하는 방법은?
                        OrderList_User(); // 유저 주문 리스트 DB에 추가 + 장바구니 db 삭제
                        btn_Payment.setBackgroundColor(Color.rgb(224, 103, 54));

                        Intent intent=new Intent();
                        setResult(RESULT_OK, intent);
                        finish();

                        Toast.makeText(getApplicationContext(), "결제가 완료 되었습니다.\n쉐프의 맛있는 요리를 집에서 즐겨보세요!.", Toast.LENGTH_LONG).show();
                    }
                });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });


        tv_TotalPrice= (TextView) findViewById(R.id.tv_TotalPrice);
        TotalPrice();
        getUserInfo();

        //액션바 설정 부분
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("결제화면");

        payment= (LinearLayout) findViewById(R.id.payment);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (group.getId() == R.id.radiogroup) {
                    switch (checkedId) {
                        case R.id.rb_Pay:
                            payment.setVisibility(View.VISIBLE);
                            Payment_method="즉시 결제";
                            break;
                        case R.id.rb_Money:
                            payment.setVisibility(View.INVISIBLE);
                            Payment_method="현장 현금";
                            break;
                        case R.id.rb_Card:
                            payment.setVisibility(View.INVISIBLE);
                            Payment_method="현장 카드";
                            break;
                    }

                    Log.d("Payment_method", Payment_method);
                }
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), User_Payment_CardRegister.class);
                startActivityForResult(intent, CARD_REGISTER_ACTIVITY);
            }
        });

        LinearLayout address = (LinearLayout) findViewById(R.id.address);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), User_Payment_Address.class);
                startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY);
            }
        });

        LinearLayout date = (LinearLayout) findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog= new DatePickerDialog(User_Payment.this, dateSetListener, year, month, day);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            }
        });

        LinearLayout time = (LinearLayout) findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(User_Payment.this, timeSetListener, hour, minute, false);
                timePickerDialog.show();
            }
        });

    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String msg = String.format("%d년 %d월 %d일", year, monthOfYear+1, dayOfMonth);
            tv_Date.setText(msg);
            Toast.makeText(User_Payment.this, "출장 날짜를 선택 하셨습니다", Toast.LENGTH_SHORT).show();

        }

    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            if(hourOfDay>20 || hourOfDay<8) {
                Toast.makeText(User_Payment.this, "불가능한 출장 시간 입니다", Toast.LENGTH_SHORT).show();
                return;
            }

            if(hourOfDay>12) {
                String msg = String.format("%d시 %d분 %s", hourOfDay, minute, "(오후)");
                tv_Time.setText(msg);
            } else {
                String msg = String.format("%d시 %d분 %s", hourOfDay, minute, "(오전)");
                tv_Time.setText(msg);
            }
            Toast.makeText(User_Payment.this, "출장 시간을 선택 하셨습니다", Toast.LENGTH_SHORT).show();
        }
    };

    // 웹뷰 액티비티의 주소값 받아오기
    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){

            case SEARCH_ADDRESS_ACTIVITY:

                if(resultCode == RESULT_OK){

                    String address = intent.getExtras().getString("address");
                    String address2 = intent.getExtras().getString("address2");
                    if (address != null)
                        tv_Address.setText(address+"  "+address2);

                }
                break;

            case CARD_REGISTER_ACTIVITY:

                if(resultCode == RESULT_OK) {
                    String CardName= intent.getExtras().getString("cardname");

                    if(CardName !=null) {
                        tv_CardName.setText(CardName);
                    }
                }

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

    private void TotalPrice() {

        String url = "http://115.71.239.151/TotalPrice.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("TotalPrice parsing", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);

                        String Price = jo.getString("Price");
                        String Count = jo.getString("Count");

                        TotalPrice+= Integer.parseInt(Price)*Integer.parseInt(Count);
                    }

                    Log.d("총 가격", String.valueOf(TotalPrice));
                    tv_TotalPrice.setText(String.valueOf(TotalPrice)+" 원");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void getUserInfo() {

        String url = "http://115.71.239.151/User_getName.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    // 데이터 불러들이기
                    Name=jo.getString("name");
                    Phone=jo.getString("phone");

                    // 데이터 뷰에 입력시키기
                    tv_Name.setText(Name);
                    tv_Phone.setText(Phone);

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
                Map<String,String> map = new Hashtable<>();
                map.put("User_Email", UserEmail);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void OrderList_Chef() {

        String url = "http://115.71.239.151/OrderList_Chef.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);

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

                String Customer_Name=tv_Name.getText().toString();
                String Customer_Number=tv_Phone.getText().toString();
                String Customer_Location=tv_Address.getText().toString();
                String Order_Date=tv_Date.getText().toString() +" "+ tv_Time.getText().toString();

                Log.d(TAG, "Customer_Name : "+Customer_Name+"Number : "+Customer_Number+" Location : "+Customer_Location+" Date : "+Order_Date);
                map.put("Customer_Name", Customer_Name);
                map.put("Customer_Number", Customer_Number);
                map.put("Customer_Location", Customer_Location);
                map.put("Order_Date", Order_Date);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void OrderList_User() {

        String url = "http://115.71.239.151/OrderList_User.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);

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

                String Order_Date=tv_Date.getText().toString();
                String Order_Time=tv_Time.getText().toString();
                String Customer_Location=tv_Address.getText().toString();

                map.put("Order_Date", Order_Date);
                map.put("Order_Time", Order_Time);
                map.put("Food_Place", Customer_Location);
                map.put("Payment_method", Payment_method);

                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}