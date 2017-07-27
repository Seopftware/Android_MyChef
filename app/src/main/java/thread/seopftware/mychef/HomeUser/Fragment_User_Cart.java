package thread.seopftware.mychef.HomeUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import thread.seopftware.mychef.R;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;
import static thread.seopftware.mychef.R.id.listview;

public class Fragment_User_Cart extends Fragment {

    private static final int ORDERLIST_ACTIVITY = 30000;

    TextView tv_KoreaName, tv_EnglishName, tv_Price, tv_Count;
    ImageView iv_FoodImage;
    String KoreaName, EnglishName, Price, imagePath, Count, Chef_Email;

    ListViewAdapter_User_Payment adapter;
    ListViewItem_User_Payment listViewItem_payment;
    ArrayList<ListViewItem_User_Payment> listViewItemList;
    ListView listView;

    Button btn_Payment;
    TextView tv_TotalPrice, tv_MyPhone;
    int TotalPrice;

    // 생성자
    public Fragment_User_Cart() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("MyChef_장바구니");
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_fragment_user_cart, container, false);

        tv_TotalPrice= (TextView) rootview.findViewById(R.id.tv_TotalPrice);
        tv_MyPhone= (TextView) rootview.findViewById(R.id.tv_MyPhone);

        // 결제하기 버튼
        btn_Payment = (Button) rootview.findViewById(R.id.btn_Payment);
        btn_Payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), User_Payment.class);
                startActivityForResult(intent, ORDERLIST_ACTIVITY);
            }
        });

        listView= (ListView) rootview.findViewById(listview);

        return rootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CartParsingList();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){

            case ORDERLIST_ACTIVITY:

                if(resultCode == RESULT_OK){
                    Log.d("결제 후 전환", "안오면?");
                    Fragment fragment = new Fragment1_User_Order();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
        }
    }


    private void CartParsingList() {

        String url = "http://115.71.239.151/CartParsingList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("parsing", response);
                try {
                    listViewItemList = new ArrayList<ListViewItem_User_Payment>();


                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    if(jsonArray.length()==0) {

                        Toast.makeText(getContext(), "장바구니가 비어있습니다.", Toast.LENGTH_SHORT).show();
                        btn_Payment.setVisibility(View.GONE);
                        return;

                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);

                        String KoreaName = jo.getString("KoreaName");
                        String EnglishName = jo.getString("EnglishName");
                        String Price = jo.getString("Price");
                        String Count = jo.getString("Count");
                        String FoodImage = jo.getString("FoodImage");
                        String Chef_Email = jo.getString("Chef_Email");

                        TotalPrice+=Integer.parseInt(Price)*Integer.parseInt(Count);
                        Log.d("총 가격", String.valueOf(TotalPrice));

                        listViewItem_payment = new ListViewItem_User_Payment();
                        listViewItem_payment.setKoreaName(KoreaName);
                        listViewItem_payment.setEnglishName(EnglishName);
                        listViewItem_payment.setChefName(Chef_Email + " 쉐프");
                        listViewItem_payment.setPrice(Price + "원");
                        listViewItem_payment.setCount(Count);
                        listViewItem_payment.setImagePath("http://115.71.239.151/" + FoodImage);
                        listViewItemList.add(listViewItem_payment);
                    }
                    adapter= new ListViewAdapter_User_Payment(getContext(), R.layout.listview_payment1, listViewItemList);

                    listView.setAdapter(adapter);
//                    tv_TotalPrice.setText(String.valueOf(TotalPrice)+"원");

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

}
