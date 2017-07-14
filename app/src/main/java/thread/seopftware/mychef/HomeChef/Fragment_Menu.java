package thread.seopftware.mychef.HomeChef;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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

import thread.seopftware.mychef.R;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
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

public class Fragment_Menu extends ListFragment {

    private static String TAG="Fragment_Menu";
    ListViewAdapter_Menu adapter;
    ListViewItem_Menu listViewItem_menu;
    ArrayList<ListViewItem_Menu> listViewItemList;
    String Id; // 음식 메뉴 고유 id
    String id;

    // 생성자
    public Fragment_Menu() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("MyChef_Menu");

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id1) {

                final CharSequence[] items=new CharSequence[] {"수정하기", "삭제하기"};
                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                dialog.setTitle("MENU");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(items[which]=="수정하기") {

                            TextView txt = (TextView) view.findViewById(R.id.tv_Id);
                            id =txt.getText().toString();
                            Log.e("listview","food menu id 값 : "+id);

                            Intent intent=new Intent(getContext(), Home_Foodmodify.class);
                            intent.putExtra("Id",id);
                            startActivity(intent);

                        }

                        if(items[which]=="삭제하기") {

                            TextView txt = (TextView) view.findViewById(R.id.tv_Id);
                            id =txt.getText().toString();
                            Log.e("listview","food menu id 값 : "+id);

                            DeleteDB();

                            for(int i=0; i<listViewItemList.size();i++){
                                if(listViewItemList.get(position).getId().equals(listViewItemList.get(i).getId())){
                                    listViewItemList.remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
                dialog.show();

                return true;
            }
        });

    }

    // 클릭 이벤트
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        TextView txt = (TextView)v.findViewById(R.id.tv_Id);
        Id =txt.getText().toString();
        Log.e("listview","food menu id 값 : "+Id);

        Intent intent=new Intent(getContext(), Home_Foodlook.class);
        intent.putExtra("Id",Id);
        startActivity(intent);

        super.onListItemClick(l, v, position, id);
    }

    // db 데이터 로드
    private void ParseDB() {

        String url = "http://115.71.239.151/foodparsing.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);
                try {
                    listViewItemList = new ArrayList<ListViewItem_Menu>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jo = jsonArray.getJSONObject(i);

                        String Id=jo.getString("id");
                        String KoreaName=jo.getString("KoreaName");
                        String EnglishName=jo.getString("EnglishName");
                        String Price=jo.getString("Price");
                        String imagePath=jo.getString("imagePath");
                        String Date=jo.getString("created");

                        listViewItem_menu=new ListViewItem_Menu();
                        listViewItem_menu.setId(Id);
                        listViewItem_menu.setKoreaName(KoreaName);
                        listViewItem_menu.setEnglishName(EnglishName);
                        listViewItem_menu.setPrice(Price+"원");
                        listViewItem_menu.setImagePath("http://115.71.239.151/"+imagePath);
                        listViewItem_menu.setDate(Date);
                        listViewItemList.add(listViewItem_menu);
                    }
                    adapter= new ListViewAdapter_Menu(listViewItemList);
                    setListAdapter(adapter);

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

                Map<String,String> map = new Hashtable<>();

                String ChefEmail;

                SharedPreferences pref1 = getActivity().getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
                KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

                SharedPreferences pref2 = getActivity().getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
                FB_LOGINCHECK=pref2.getString(FBAPI, "0");

                Log.d(TAG, "KAKAO API :"+KAKAO_LOGINCHECK);
                Log.d(TAG, "FB API :"+FB_LOGINCHECK);

                if(!FB_LOGINCHECK.equals("0")) {
                    SharedPreferences pref = getActivity().getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
                    ChefEmail=pref.getString(FBEMAIL, "");
                    Log.d(TAG, "FB chefemail: "+ChefEmail);
                } else if(!KAKAO_LOGINCHECK.equals("0")) {
                    SharedPreferences pref = getActivity().getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
                    ChefEmail=pref.getString(KAEMAIL, "");
                    Log.d(TAG, "KA chefemail: "+ChefEmail);
                } else { // 일반
                    SharedPreferences pref = getActivity().getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
                    ChefEmail=pref.getString(CHEFNORMALLEMAIL, "");
                    Log.d(TAG, "Normal chefemail: "+ChefEmail);
                }

                Log.d(TAG, "Chef Email: "+ChefEmail);
                //Adding parameters, 입력 변수들
                map.put("ChefEmail", ChefEmail);

                //returning parameters
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // db 데이터 삭제
    private void DeleteDB() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getContext(),"Uploading...","Please wait...",false,false);

        String url = "http://115.71.239.151/fooddelete.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Log.d("response", response);

                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                loading.dismiss();

                //Creating parameters
                Map<String,String> map = new Hashtable<>();

                Log.d(TAG, "보내는 ID : "+id);
                map.put("Id", id);

                //returning parameters
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
