package thread.seopftware.mychef.Chatting;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class chat_navi_example extends AppCompatActivity {

    private static final String TAG = "chat_navi_example";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Chat_NaviListItem listViewItem;
    private Chat_NaviListAdapter adapter;
    private ArrayList<Chat_NaviListItem> listViewItemList;
    private ActionBarDrawerToggle mDrawerToggle;


    String room_number = "999";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_navi_example);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
/*        mDrawerList = (ListView) findViewById(R.id.right_drawer);*/


/*        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "클릭!", Toast.LENGTH_SHORT).show();
            }
        });*/

/*        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());*/

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);





        getNaviMemberDB();
    }

    private void getNaviMemberDB() {

        String url = "http://115.71.239.151/getNaviMemberDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listViewItemList = new ArrayList<Chat_NaviListItem>();

                Log.d("parsing", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);

                        // 데이터 불러들이기
                        String email = jo.getString("email");
                        String name = jo.getString("name");
                        String profile = jo.getString("profile");

                        // 데이터 뷰에 입력시키기
                        listViewItem=new Chat_NaviListItem();
                        listViewItem.setName(name);
                        listViewItem.setEmail(email);
                        listViewItem.setImage("http://115.71.239.151/"+profile);

                        listViewItemList.add(listViewItem);
                    }

                    adapter= new Chat_NaviListAdapter(getApplicationContext(), R.layout.custom_drawer_item, listViewItemList);
                    mDrawerList.setAdapter(adapter);

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

                Log.d(TAG, "전달받은 room_number : " + room_number );
                Map<String,String> map = new Hashtable<>();
                map.put("room_number", room_number);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            switch (position) {
                case 0:
            }


        }
    }
}
