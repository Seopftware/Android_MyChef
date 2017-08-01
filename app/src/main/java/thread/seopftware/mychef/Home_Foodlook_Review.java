package thread.seopftware.mychef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import thread.seopftware.mychef.HomeUser.ListViewAdapter_Comment;
import thread.seopftware.mychef.HomeUser.ListViewItem_Comment;

public class Home_Foodlook_Review extends AppCompatActivity {

    private static String TAG="Home_Foodlook_Review";

    TextView tv_Name, tv_Date, tv_Comment;
    String User_Name, User_Email, Created_Date, Comment, ratingNumber;
    double ratingAvg;
    RatingBar ratingBar;
    ListView listView;

    ListViewAdapter_Comment adapter;
    ListViewItem_Comment listViewItem_comment;
    ArrayList<ListViewItem_Comment> listViewItemList;

    String Food_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_foodlook_review);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("리뷰");

        Intent intent=getIntent();
        Food_Id=intent.getStringExtra("Food_Id");
        Log.d(TAG, "전달받은 Food_Id : " + Food_Id );

        tv_Name= (TextView) findViewById(R.id.tv_Name);
        tv_Date= (TextView) findViewById(R.id.tv_Date);
        tv_Comment= (TextView) findViewById(R.id.tv_Comment);
        ratingBar= (RatingBar) findViewById(R.id.ratingBar);
        listView= (ListView) findViewById(R.id.listView);

        getCommentDB();

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

    private void getCommentDB() {

        String url = "http://115.71.239.151/getCommentDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listViewItemList = new ArrayList<ListViewItem_Comment>();

                Log.d("parsing", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        // 데이터 불러들이기
                        Comment=jo.getString("Comment");
                        Created_Date=jo.getString("Created_Date");
                        ratingNumber=jo.getString("Rating");
                        User_Name=jo.getString("User_Name");
                        User_Email=jo.getString("User_Email");
                        double Avg=Math.round(Float.parseFloat(ratingNumber));

                        // 데이터 뷰에 입력시키기
                        listViewItem_comment=new ListViewItem_Comment();
                        listViewItem_comment.setName(User_Name +" 고객님");
                        listViewItem_comment.setEmail(User_Email);
                        listViewItem_comment.setCreatedDate(Created_Date);
                        listViewItem_comment.setComment(Comment);
                        listViewItem_comment.setRatingBar(Avg*2);

                        listViewItemList.add(listViewItem_comment);
                    }

                    adapter= new ListViewAdapter_Comment(listViewItemList);
                    listView.setAdapter(adapter);

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

                Log.d(TAG, "전달받은 Food_Id : " + Food_Id );
                Map<String,String> map = new Hashtable<>();
                map.put("Food_Id", Food_Id);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


}
