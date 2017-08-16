package thread.seopftware.mychef.Chatting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

import thread.seopftware.mychef.R;

public class Chat_Invite_Friendlist extends AppCompatActivity {


    Button btn_Search;
    EditText et_SearchWord;

    ListView listView; // 리스트뷰
    ListViewItem_Invite listViewItem; // 리스트뷰 아이템
    ArrayList<ListViewItem_Invite> listViewItemList; // 리스트뷰 아이템 리스트
    ListViewAdapter_Invite adapter; // 리스트뷰 어댑터



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__invite_friendlist);

        Intent intent=getIntent();
        String email = intent.getStringExtra("email");

        listView= (ListView) findViewById(R.id.listView);
        btn_Search= (Button) findViewById(R.id.btn_Search);
        et_SearchWord= (EditText) findViewById(R.id.et_SearchWord);

        friendListDB();
    }


    // db 데이터 로드
    private void friendListDB() {

        String url = "http://115.71.239.151/Chatting_FriendList3.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing1", response);

                try {
                    listViewItemList = new ArrayList<ListViewItem_Invite>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jo = jsonArray.getJSONObject(i);

                        String email = jo.getString("email");
                        String name = jo.getString("name");
                        String profile = jo.getString("profile");

                        listViewItem = new ListViewItem_Invite();
                        listViewItem.setEmail(email);
                        listViewItem.setName(name);
                        listViewItem.setImage("http://115.71.239.151/" + profile);
                        listViewItemList.add(listViewItem);
                    }

                    adapter = new ListViewAdapter_Invite(getApplicationContext(), R.layout.listview_chat_invite, listViewItemList);
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String email = "inseop0813@gmail.com";
                Map<String,String> map = new Hashtable<>();
                map.put("User_Email", email);

                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
