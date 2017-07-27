/*
package thread.seopftware.mychef.etc;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Hashtable;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

*
 * Created by MSI on 2017-07-18.



public class Volley {


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
                map.put("KoreaName", KoreaName); // 2
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
*/


// 여기
/*
private void getUserInfo() {

        String url = "http://115.71.239.151/User_getName.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
@Override
public void onResponse(String response) {

        Log.d("parsing", response);
        try {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("result");

        JSONObject jo = jsonArray.getJSONObject(0);

        // 데이터 불러들이기
        Name=jo.getString("name");
        Phone=jo.getString("phone");

        // 데이터 뷰에 입력시키기
        tv_Name.setText(Name);
        tv_Phone.setText(Phone);

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

        Log.d(TAG, "UserEmail : "+UserEmail);
        Map<String,String> map = new Hashtable<>();
        map.put("User_Email", UserEmail);
        return map;
        }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        }*/
