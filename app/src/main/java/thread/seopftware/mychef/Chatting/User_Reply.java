package thread.seopftware.mychef.Chatting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;

import thread.seopftware.mychef.R;

public class User_Reply extends AppCompatActivity {

    String Food_Id, Food_Name, User_Email;
    String Rating_Number=null;
    RatingBar ratingBar;
    TextView tv_Explain, tv_RatingBar, tv_TextByte;
    EditText et_Reply;

    GregorianCalendar calendar;
    int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reply);

        //액션바 설정 부분
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("댓글 작성");
        
        tv_Explain= (TextView) findViewById(R.id.tv_Explain);
        tv_RatingBar= (TextView) findViewById(R.id.tv_RatingBar);
        tv_TextByte= (TextView) findViewById(R.id.tv_TextByte);
        et_Reply= (EditText) findViewById(R.id.et_Reply);
        et_Reply.addTextChangedListener(new myWatcher());

        ratingBar= (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tv_RatingBar.setText("평점 : "+rating+"/5.0");
                Rating_Number= String.valueOf(rating);
            }
        });

        Intent intent=getIntent();
        Food_Id=intent.getStringExtra("Food_Id");
        Food_Name=intent.getStringExtra("Food_Name");
        User_Email=intent.getStringExtra("User_Email");

        tv_Explain.setText(Food_Name+" 요리에 대한 고객님의 솔직한 평을 남겨주세요.");

        Log.d("댓글화면", "Food_Id : "+Food_Id);
        Log.d("댓글화면", "Food_Name : "+Food_Name);
    }

    //액션바 백키 버튼 구현
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }

            case R.id.action_button: {

                if(tv_RatingBar.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "평점을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if(Rating_Number == "null") {
                    Toast.makeText(getApplicationContext(), "평점을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (et_Reply.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    et_Reply.requestFocus();
                    return true;
                }

                Comment_Insert();

                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }

    public class myWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            tv_TextByte.setText(s.length() + " / 80 글자");
        }
    }

    private void Comment_Insert() {

        String url = "http://115.71.239.151/Comment_Insert.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Comment_Insert parsing", response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
                }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String Comment=et_Reply.getText().toString();
//                String Rating=tv_RatingBar.getText().toString();

                Map<String,String> map = new Hashtable<>();

                calendar = new GregorianCalendar();

                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day= calendar.get(Calendar.DAY_OF_MONTH);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);


                map.put("Food_Id", Food_Id);
                map.put("Rating", Rating_Number);
                map.put("Comment", Comment);
                map.put("User_Email", User_Email);
                map.put("Date", year+"년 "+month+1+"월 "+day+"일");
                return map;
            }
        };

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
            }

}
