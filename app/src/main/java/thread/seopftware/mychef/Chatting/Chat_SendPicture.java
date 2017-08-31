package thread.seopftware.mychef.Chatting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import thread.seopftware.mychef.R;

public class Chat_SendPicture extends AppCompatActivity implements View.OnClickListener{

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    // 전역변수
    private static final String TAG = "Chat_SendPicture";

    // C++ 사용을 위한 변수
    Mat img_input;
    Mat img_output;

    // UI
    ImageView iv_Main; // 메인 사진
    ImageView iv_Filter, iv_Filter1, iv_Filter2, iv_Filter3, iv_Filter4, iv_Filter5, iv_Filter6, iv_Filter7, iv_Filter8; // 이미지 필터 적용되어 있는 사진
    ImageButton ibtn_Finish, ibtn_Confirm;

    // 변수
    String room_number, email_sender, content_time, image_path; // Service로 보낼 변수들
    Uri image_uri;
    int a,b,c,d,e,f,g,h,i = 0;
    Handler handler;
    Bitmap bitmapOutput;

    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 윈도우 상단의 상태바를 없애는 함수
        setContentView(R.layout.activity_chat_send_picture);

        // Chat_Chatting 클래스에서 받아오는 값들
        Intent intent = getIntent();
        room_number = intent.getStringExtra("room_number");
        email_sender = intent.getStringExtra("email_sender");
        content_time = intent.getStringExtra("content_time");
        image_path = intent.getStringExtra("image_path");

        String image_str = intent.getStringExtra("image_uri");
        image_uri = Uri.parse(image_str);


        // 액션바
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // UI 선언
        // 1.imageView
        iv_Main = (ImageView) findViewById(R.id.iv_Main);
        iv_Filter= (ImageView) findViewById(R.id.iv_Filter);
        iv_Filter1= (ImageView) findViewById(R.id.iv_Filter1);
        iv_Filter2= (ImageView) findViewById(R.id.iv_Filter2);
        iv_Filter3= (ImageView) findViewById(R.id.iv_Filter3);
        iv_Filter4= (ImageView) findViewById(R.id.iv_Filter4);
        iv_Filter5= (ImageView) findViewById(R.id.iv_Filter5);
        iv_Filter6= (ImageView) findViewById(R.id.iv_Filter6);
        iv_Filter7= (ImageView) findViewById(R.id.iv_Filter7);
        iv_Filter8= (ImageView) findViewById(R.id.iv_Filter8);

        iv_Filter.setOnClickListener(this);
        iv_Filter1.setOnClickListener(this);
        iv_Filter2.setOnClickListener(this);
        iv_Filter3.setOnClickListener(this);
        iv_Filter4.setOnClickListener(this);
        iv_Filter5.setOnClickListener(this);
        iv_Filter6.setOnClickListener(this);
        iv_Filter7.setOnClickListener(this);
        iv_Filter8.setOnClickListener(this);


        // 2.imageButton
        ibtn_Confirm= (ImageButton) findViewById(R.id.ibtn_Confirm);
        ibtn_Finish= (ImageButton) findViewById(R.id.ibtn_Finish);

        ibtn_Confirm.setOnClickListener(this);
        ibtn_Finish.setOnClickListener(this);




        read_image_file(image_path);
        Glide.with(this).load(image_uri).into(iv_Main);
        Glide.with(this).load(image_uri).into(iv_Filter);

    }

    // 필터링 할 이미지 파일을 불러들이는 작업
    private void read_image_file(String path) {
        img_input = new Mat();
        img_output = new Mat();

        // 필터링 하고자 하는 파일의 경로를 보낸다.
        loadImage(path, img_input.getNativeObjAddr());
        imageprocess_and_showResult1();
        imageprocess_and_showResult2();
        imageprocess_and_showResult3();
        imageprocess_and_showResult4();
        imageprocess_and_showResult5();
        imageprocess_and_showResult6();
        imageprocess_and_showResult7();
        imageprocess_and_showResult8();
    }

    private void imageprocess_and_showResult() { // 이미지 필터 처리 하는 곳 (한번에)

        Log.d(TAG, "imageprocess_and_showResult");


        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Log.d(TAG, "image_uri : " + image_uri);
                    bitmapOutput = Glide.with(getApplicationContext()).load(image_uri).asBitmap().into(300, 380).get();
                    Log.d(TAG, "bitmapOutput : " + bitmapOutput);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            iv_Main.setImageBitmap(bitmapOutput);
                        }
                    });


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void imageprocess_and_showResult1() { // 이미지 필터 처리 하는 곳
        imageprocessing1(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());

        Bitmap bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);

        b=0+b;

        if(b==0) {
            Log.d(TAG, "맨 처음 적용");
            iv_Filter1.setImageBitmap(bitmapOutput);
            b++;
        } else if (b>=1) {
            Log.d(TAG, "나중에는 메인에만 적용");
            iv_Main.setImageBitmap(bitmapOutput);
        }

    }

    private void imageprocess_and_showResult2() { // 이미지 필터 처리 하는 곳
        imageprocessing2(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());

        bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);

        c=0+c;

        if(c==0) {
            iv_Filter2.setImageBitmap(bitmapOutput);
            c++;

        } else if (c>=1){
            iv_Main.setImageBitmap(bitmapOutput);
        }
    }

    private void imageprocess_and_showResult3() { // 이미지 필터 처리 하는 곳
        imageprocessing3(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());

        bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);

        a=0+a;

        if(a==0) {
            iv_Filter3.setImageBitmap(bitmapOutput);
            a++;

        } else if (a>=1){
            iv_Main.setImageBitmap(bitmapOutput);
        }
    }

    private void imageprocess_and_showResult4() { // 이미지 필터 처리 하는 곳
        imageprocessing4(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());

        bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);

        d=0+d;

        if(d==0) {
            iv_Filter4.setImageBitmap(bitmapOutput);
            d++;

        } else if (d>=1){
            iv_Main.setImageBitmap(bitmapOutput);
        }
    }

    private void imageprocess_and_showResult5() { // 이미지 필터 처리 하는 곳
        imageprocessing5(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());

        bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);

        e=0+e;

        if(e==0) {
            iv_Filter5.setImageBitmap(bitmapOutput);
            e++;

        } else if (e>=1){
            iv_Main.setImageBitmap(bitmapOutput);
        }
    }

    private void imageprocess_and_showResult6() { // 이미지 필터 처리 하는 곳
        imageprocessing6(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());

        bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);

        f=0+f;

        if(f==0) {
            iv_Filter6.setImageBitmap(bitmapOutput);
            f++;

        } else if (f>=1){
            iv_Main.setImageBitmap(bitmapOutput);
        }
    }

    private void imageprocess_and_showResult7() { // 이미지 필터 처리 하는 곳
        imageprocessing7(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());

        bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);

        g=0+g;

        if(g==0) {
            iv_Filter7.setImageBitmap(bitmapOutput);
            g++;

        } else if (g>=1){
            iv_Main.setImageBitmap(bitmapOutput);
        }
    }

    private void imageprocess_and_showResult8() { // 이미지 필터 처리 하는 곳
        imageprocessing8(img_input.getNativeObjAddr(), img_output.getNativeObjAddr());

        bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output, bitmapOutput);

        h=0+h;

        if(h==0) {
            iv_Filter8.setImageBitmap(bitmapOutput);
            h++;

        } else if (h>=1){
            iv_Main.setImageBitmap(bitmapOutput);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // 이미지 버튼
            case R.id.ibtn_Confirm:
                Log.d(TAG, "R.id.ibtn_Confirm 클릭");
                String imagePath = getStringImage(bitmapOutput);
                String imageName = "photo_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                photoUpdateDB(imagePath, imageName);

                finish();
                break;

            case R.id.ibtn_Finish:
                Log.d(TAG, "R.id.ibtn_Finish 클릭");

                finish();
                break;


            // original 사진
            case R.id.iv_Filter:
                Log.d(TAG, "R.id.iv_Filter 클릭");
                imageprocess_and_showResult();
                break;

            case R.id.iv_Filter1:
                Log.d(TAG, "R.id.iv_Filter1 클릭");
                imageprocess_and_showResult1();
                break;

            case R.id.iv_Filter2:
                Log.d(TAG, "R.id.iv_Filter2 클릭");
                imageprocess_and_showResult2();
                break;

            case R.id.iv_Filter3:
                Log.d(TAG, "R.id.iv_Filter3 클릭");
                imageprocess_and_showResult3();
                break;

            case R.id.iv_Filter4:
                Log.d(TAG, "R.id.iv_Filter4 클릭");
                imageprocess_and_showResult4();
                break;

            case R.id.iv_Filter5:
                Log.d(TAG, "R.id.iv_Filter5 클릭");
                imageprocess_and_showResult5();
                break;

            case R.id.iv_Filter6:
                Log.d(TAG, "R.id.iv_Filter6 클릭");
                imageprocess_and_showResult6();
                break;

            case R.id.iv_Filter7:
                Log.d(TAG, "R.id.iv_Filter7 클릭");
                imageprocess_and_showResult7();
                break;

            case R.id.iv_Filter8:
                Log.d(TAG, "R.id.iv_Filter8 클릭");
                imageprocess_and_showResult8();
                break;
        }
    }


    // 보내고자 하는 사진을 서버에 업로드
    private void photoUpdateDB(final String imagePath, final String imageName) {
        long now = System.currentTimeMillis();
        simpleDateFormat = new SimpleDateFormat("yyyyMMdd_hh:dd a", Locale.KOREA);
        final String Show_Time = simpleDateFormat.format(new Date(now));

        String[] time_split = Show_Time.split("_");
        final String Time = time_split[1];

        String url = "http://115.71.239.151/photoUpdateDB.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // 업로드에 성공하면 saveMessage & addItem
                Log.d(TAG, "photoUpdateDB response : "+response);


                String Path=response; // DB에 저장된 사진의 경로

                try {

                    Log.d(TAG, "**************************************************");
                    Log.d(TAG, "이미지 보내기!!!");
                    Log.d(TAG, "**************************************************");

                    // 메세지를 서비스로 보내는 곳
                    JSONObject object = new JSONObject();
                    object.put("room_status", "999");
                    object.put("room_number", room_number);
                    object.put("email_sender", email_sender);
                    object.put("content_message", Path); // DB 이미지 경로
                    object.put("content_time", Show_Time);
                    String Object_Data = object.toString();

                    Intent intent = new Intent(Chat_SendPicture.this, Chat_Service.class); // 액티비티 ㅡ> 서비스로 메세지 전달
                    intent.putExtra("command", Object_Data);
                    startService(intent);

                    addNumMessage();

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

                // 메세지 및 이미지 경로 DB에 저장 -> 메세지 뿌리기
                map.put("imagePath", imagePath);
                map.put("imageName", imageName);
                map.put("room_status", "999"); // 메세지 상태 (이미지 999)
                map.put("room_number", room_number); // 방 번호
                map.put("email_sender", email_sender); // 로그인 이메일 (보내는 사람 이메일)
                map.put("content_time", Show_Time);
                return map;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

    // 메세지 보낸 갯수 +1
    private void addNumMessage() {
        Log.d(TAG, "addNumMessage() 함수가 실행 됩니다.");

        String url = "http://115.71.239.151/addNumMessage.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("addNumMessage", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d(TAG, "room_number : " + room_number);
                Map<String, String> map = new Hashtable<>();
                map.put("room_number", room_number);
                map.put("Login_Email", email_sender);
                return map;


            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public native void loadImage(String imageFileName, long img);
    public native void imageprocessing1(long inputImage, long outputImage);
    public native void imageprocessing2(long inputImage, long outputImage);
    public native void imageprocessing3(long inputImage, long outputImage);
    public native void imageprocessing4(long inputImage, long outputImage);
    public native void imageprocessing5(long inputImage, long outputImage);
    public native void imageprocessing6(long inputImage, long outputImage);
    public native void imageprocessing7(long inputImage, long outputImage);
    public native void imageprocessing8(long inputImage, long outputImage);
}
