package thread.seopftware.mychef.HomeUser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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

import thread.seopftware.mychef.R;

import static com.facebook.FacebookSdk.getApplicationContext;
import static thread.seopftware.mychef.R.id.listview;

public class Fragment_User_Search extends Fragment {

    ListViewAdapter_User_Search adapter;
    ListViewItem_User_Search listViewItem_search;
    ArrayList<ListViewItem_User_Search> listViewItemList;
    ListView listView;

    String Word, Ranking;
    View view1;
    Button btn_Ranking, btn_Search;
    ImageButton btn_Delete;
    EditText et_SearchWord;

    String inputWord;

    FrameLayout frameLayout;
    TextView tv_PopularWord;

    String InputWord;

    public Fragment_User_Search() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_fragment_user_search, container, false);

//        View view = inflater.inflate(R.layout.activity_fragment_user_search, container, false);
        getActivity().setTitle("MyChef_메뉴 검색");

        listView= (ListView) rootview.findViewById(listview);
        btn_Search= (Button) rootview.findViewById(R.id.btn_Search);
        frameLayout= (FrameLayout) rootview.findViewById(R.id.child_frame);
        et_SearchWord= (EditText) rootview.findViewById(R.id.et_SearchWord);
        btn_Delete= (ImageButton) rootview.findViewById(R.id.btn_Delete);
        tv_PopularWord= (TextView) rootview.findViewById(R.id.tv_PopularWord);
        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 이메일 입력 안했을 때
                if (et_SearchWord.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    et_SearchWord.requestFocus();
                    return;
                }

                SearchWord();

                listView.setVisibility(View.GONE);
                frameLayout.bringToFront();
                tv_PopularWord.setText("검색어 : "+et_SearchWord.getText().toString());
                InputWord=et_SearchWord.getText().toString();

                Fragment fragment;
                fragment = new ChildFragment();
                setChildFragment(fragment);
            }
        });

        btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_SearchWord.setText("");
            }
        });


        view1 = inflater.inflate(R.layout.listview_search, container, false);
//        btn_Ranking= (Button) view1.findViewById(R.id.btn_Ranking);

        return rootview;
    }

    public String getString() {
        return InputWord;
    }

    private void setChildFragment(Fragment child) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {
            Log.d("여긴 옴?", "확인");
            childFt.replace(R.id.child_frame, child);
            childFt.commit();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SearchRanking();

    }

    private void SearchRanking() {

        String url = "http://115.71.239.151/SearchRankingList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);
                try {
                    listViewItemList = new ArrayList<ListViewItem_User_Search>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < 10; i++) {

                        JSONObject jo = jsonArray.getJSONObject(i);
                        Word= jo.getString("Word");

                        if(i<=2) {

                            listViewItem_search = new ListViewItem_User_Search();
                            listViewItem_search.setWord(Word);
                            listViewItem_search.setRanking(Integer.toString(i+1));
                            listViewItemList.add(listViewItem_search);

//                            btn_Ranking.setText(Integer.toString(i+1));
//                            btn_Ranking.setBackgroundColor(Color.rgb(224, 103, 54));

                        } else {

                            listViewItem_search = new ListViewItem_User_Search();
                            listViewItem_search.setWord(Word);
                            listViewItemList.add(listViewItem_search);
                            listViewItem_search.setRanking(Integer.toString(i+1));
                        }

                    }
                    adapter= new ListViewAdapter_User_Search(getContext(), R.layout.listview_search, listViewItemList);
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
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // EditText에서 검색어를 입력하면 DB에 해당 검색어를 저장하고, 검색어를 메뉴 프레그먼트로 보낸다.
    private void SearchWord() {
        //Showing the progress dialog

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.71.239.151/SearchWord.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Search", "Volley Response is : "+response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String,String> map = new Hashtable<>();
                inputWord=et_SearchWord.getText().toString();

                Log.d("inputWord",inputWord);
                map.put("inputWord", inputWord);



                //returning parameters
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
