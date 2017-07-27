package thread.seopftware.mychef.HomeUser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import thread.seopftware.mychef.R;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ListViewAdapter_User_Payment extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    ArrayList<ListViewItem_User_Payment> listViewItemList ;
    Context context;
    int layout;
    String Current_Count;
    String Current_KoreaName;

    String Update_Count;

    Animation anim;


    // ListViewAdapter의 생성자
    public ListViewAdapter_User_Payment(Context context, int layout, ArrayList<ListViewItem_User_Payment> listViewItemList) {
        this.context=context;
        this.layout=layout;
        this.listViewItemList=listViewItemList;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴.
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴.
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final int pos=position;
        final Context context=parent.getContext();

        // "ListViewItem_Menu" Layout을 inflate하여 convertView 참조 획득

        if(convertView==null) {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.listview_payment1, parent, false);
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListViewItem_User_Payment listViewItem=listViewItemList.get(position);

        // 화면에 표시될 View(Layout이 inflate된)으로 부터 위젯에 대한 참조 획득
        final TextView KoreaName= (TextView) convertView.findViewById(R.id.tv_KoreaName);
        TextView EnglishName= (TextView) convertView.findViewById(R.id.tv_EnglishName);
        TextView ChefEmail= (TextView) convertView.findViewById(R.id.tv_ChefEmail);
        TextView Price= (TextView) convertView.findViewById(R.id.tv_Price);
        final TextView Count= (TextView) convertView.findViewById(R.id.tv_Count);
        final ImageButton btn_Delete= (ImageButton) convertView.findViewById(R.id.btn_Delete);
        ImageView iv_FoodImage= (ImageView) convertView.findViewById(R.id.iv_Chef_Profile);
        Glide.with(context).load(listViewItem.getImagePath()).into(iv_FoodImage);

        final Button btn_Addition= (Button) convertView.findViewById(R.id.btn_Addition);
        final Button btn_Subtraction= (Button) convertView.findViewById(R.id.btn_Subtraction);

        // 아이템 내 각 위젯에 데이터 반영
        KoreaName.setText(listViewItem.getKoreaName());
        EnglishName.setText(listViewItem.getEnglishName());
        ChefEmail.setText(listViewItem.getChefName());
        Price.setText(listViewItem.getPrice());
        Count.setText(listViewItem.getCount());

        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_anim);


        // 플러스 버튼 클릭 시
        btn_Addition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 서버로 보낼 데이터 (음식 갯수, 음식 이름)
//                Log.d("실험중", "Count 가져오기 : "+listViewItemList.get(position).Count.getText().toString());

                Current_Count=Count.getText().toString();
                Log.d("Current_Count", Current_Count);
                Current_KoreaName=KoreaName.getText().toString();
                Log.d("Current_KoreaName", KoreaName.getText().toString());

                listViewItemList.get(position).setCount(Integer.toString(Integer.parseInt(Current_Count)+1));
                notifyDataSetChanged();

                btn_Addition.startAnimation(anim);

                AddCount(); // db 업데이트
//                CurrentPrice(); // 가격 최신화

            }
        });

        // 뺴기 버튼 클릭 시
        btn_Subtraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Current_Count=Count.getText().toString();
                Log.d("Current_Count", Current_Count);
                Current_KoreaName=KoreaName.getText().toString();
                Log.d("Current_KoreaName", KoreaName.getText().toString());

                if(Current_Count.equals("1")) {
                    Toast.makeText(getApplicationContext(), "최소한 1개 이상은 구매하셔야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    listViewItemList.get(position).setCount(Integer.toString(Integer.parseInt(Current_Count)-1));
                    notifyDataSetChanged();

                    btn_Subtraction.startAnimation(anim);
                    SubtractCount();
                    return;
                }
            }
        });

        btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Current_KoreaName=KoreaName.getText().toString();
                Log.d("Current_KoreaName", KoreaName.getText().toString());

                listViewItemList.remove(position);
                notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), Current_KoreaName+"을 삭제하셨습니다.", Toast.LENGTH_SHORT).show();

                DeleteItem();
            }
        });


        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴.
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴.
    @Override
    public Object getItem(int position) {
        return null;
    }

    // Count 더하기
    private void AddCount() {

        String url = "http://115.71.239.151/AddCount.php";
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
                map.put("Current_Count", Current_Count);
                map.put("Current_KoreaName", Current_KoreaName);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // Count 빼기
    private void SubtractCount() {

        String url = "http://115.71.239.151/AddCount_Subtract.php";
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
                map.put("Current_Count", Current_Count);
                map.put("Current_KoreaName", Current_KoreaName);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // 아이템 삭제하기
    private void DeleteItem() {

        String url = "http://115.71.239.151/CartItemDelete.php";
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
                map.put("Current_KoreaName", Current_KoreaName);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
