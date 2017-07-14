package thread.seopftware.mychef.HomeChef;

import android.content.Intent;
import android.os.Bundle;
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

import thread.seopftware.mychef.R;

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
    String imagePath;
    String name;
    String appeal2;
    String photostring;

    String foodmenu_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_foodlook);

        iv_FoodImage= (ImageView) findViewById(R.id.iv_FoodImage);
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

        getDataFromDB();

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

    private void getDataFromDB() {
        Intent intent=getIntent();
        foodmenu_id=intent.getExtras().getString("Id");

        Log.d(TAG, "id : "+foodmenu_id);

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
                    imagePath=jo.getString("imagePath"); // 8
                    name=jo.getString("name"); // 9
                    appeal2=jo.getString("appeal2"); // 11
                    photostring=jo.getString("photostring"); // 12

                    Log.d("APPEAL2", appeal2 );

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
}
