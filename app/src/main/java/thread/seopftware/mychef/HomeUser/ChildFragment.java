package thread.seopftware.mychef.HomeUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import thread.seopftware.mychef.HomeChef.Home_Foodlook;
import thread.seopftware.mychef.R;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ChildFragment extends ListFragment {

    private static String TAG="Fragment_User_Menu";
    ListViewAdapter_User_Menu adapter;
    ListViewItem_User_Menu listViewItem_menu;
    ArrayList<ListViewItem_User_Menu> listViewItemList;
    String Id; // 음식 메뉴 고유 id

    String SearchWord;

    public ChildFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        getActivity().setTitle("MyChef_요리 목록");
        doAction();

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    void doAction() {
        Fragment parentFragment = getParentFragment();

        if(parentFragment instanceof Fragment_User_Search) {
            SearchWord = ((Fragment_User_Search) parentFragment).getString();
            Log.d("자식 프레그먼트 입력된 검색어 :", SearchWord);

        }
    }


    // 클릭 이벤트
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        TextView txt = (TextView)v.findViewById(R.id.tv_userId);
        Id =txt.getText().toString();
        Log.e("listview","food menu id 값 : "+Id);

        Intent intent=new Intent(getContext(), Home_Foodlook.class);
        intent.putExtra("Id",Id);
        startActivity(intent);

        super.onListItemClick(l, v, position, id);
    }

    // db 데이터 로드
    private void ParseDB() {

        String url = "http://115.71.239.151/foodparsing_user_search.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing1", response);

                try {
                    listViewItemList = new ArrayList<ListViewItem_User_Menu>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if(jsonArray.length()==0) {
                        Toast.makeText(getContext(), "검색 결과가 없습니다.\n다른 메뉴를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jo = jsonArray.getJSONObject(i);

                            String Id = jo.getString("id");
                            String KoreaName = jo.getString("KoreaName");
                            String EnglishName = jo.getString("EnglishName");
                            String ChefName = jo.getString("ChefName");
                            String Price = jo.getString("Price");
                            String imagePath = jo.getString("imagePath");

                            listViewItem_menu = new ListViewItem_User_Menu();
                            listViewItem_menu.setId(Id);
                            listViewItem_menu.setKoreaName(KoreaName);
                            listViewItem_menu.setEnglishName(EnglishName);
                            listViewItem_menu.setChefName(ChefName + " 쉐프");
                            listViewItem_menu.setPrice(Price + "원");
                            listViewItem_menu.setImagePath("http://115.71.239.151/" + imagePath);
                            listViewItemList.add(listViewItem_menu);
                        }
                        adapter = new ListViewAdapter_User_Menu(listViewItemList);
                        setListAdapter(adapter);
                    }

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

                Map<String,String> map = new Hashtable<>();
                map.put("SearchWord", "파스타");
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        Log.d(this.getClass().getSimpleName(), "onResume()");
        super.onResume();

        ParseDB(); // db 데이터 삽입
    }
}
