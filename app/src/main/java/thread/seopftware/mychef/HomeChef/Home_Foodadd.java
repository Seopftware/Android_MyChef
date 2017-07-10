package thread.seopftware.mychef.HomeChef;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import thread.seopftware.mychef.R;

import static thread.seopftware.mychef.Login.Login_choose.FACEBOOKLOGIN;
import static thread.seopftware.mychef.Login.Login_choose.FBEMAIL;
import static thread.seopftware.mychef.Login.Login_choose.FB_LOGINCHECK;
import static thread.seopftware.mychef.Login.Login_choose.KAEMAIL;
import static thread.seopftware.mychef.Login.Login_choose.KAKAOLOGIN;
import static thread.seopftware.mychef.Login.Login_choose.KAKAO_LOGINCHECK;
import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLEMAIL;
import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLOGIN;

public class Home_Foodadd extends AppCompatActivity {

    private static String TAG="Home_Foodadd";

    //이미지 관련 함수
    ImageView iv_capture;
    String mCurrentPhotoPath;
    Uri photoURI, albumURI;
    boolean isAlbum=false;
    static final int REQUEST_ALBUM = 2002;
    public static final int AREACHOOSE = 999;

    //이미지 멀티 초이스 관련 변수
    private static final int RC_CODE_PICKER = 2000;
    private ArrayList<Image> images = new ArrayList<>();
    String image_first="0", image_second="0", image_third="0";



    //앨범
    String imageFileName;
    Bitmap album_bitmap;
    Uri album_uri;

    // 입력값 변수들
    EditText et_KoreaName, et_EnglishName, et_Price, et_Description, et_Ingredients, et_Area;
    Bitmap first_bitmap, second_bitmap, third_bitmap;

    ImageView iv_first, iv_second, iv_third;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_foodadd);

        // 변수 객체 선언
        et_KoreaName= (EditText) findViewById(R.id.et_KoreaName);
        et_EnglishName= (EditText) findViewById(R.id.et_EnglishName);
        et_Price= (EditText) findViewById(R.id.et_Price);
        et_Description= (EditText) findViewById(R.id.et_Description);
        et_Ingredients= (EditText) findViewById(R.id.et_Ingredients);
        et_Area= (EditText) findViewById(R.id.et_Area);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("요리 등록");

        // 메인 이미지
        iv_capture= (ImageView) findViewById(R.id.iv_capture);
        iv_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showFileChooser();
            }
        });

//        iv_first= (ImageView) findViewById(R.id.iv_first);
//        iv_second= (ImageView) findViewById(R.id.iv_second);
//        iv_third= (ImageView) findViewById(R.id.iv_third);

//        // 멀티 이미지
//        findViewById(R.id.button_pick_image).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                start();
//            }
//        });

    }

    // 멀티 이미지 선택 함수
    public void start() {

        ImagePicker imagePicker = ImagePicker.create(this);
//                .folderMode(true) // set folder mode (false by default)
//                .folderTitle("Folder") // folder selection title
//                .imageTitle("Tap to select"); // image selection title

        imagePicker.limit(3)
                .multi()// max images can be selected (99 by default)
                .showCamera(false) // show camera or not (true by default)
//                .origin(images) // original selected images, used in multi mode
                .start(RC_CODE_PICKER); // start image picker activity with request code
    }

    // 멀티이미지 출력함수
    private void printImages(List<Image> images) {
        if (images == null) return;

        int l=images.size();

            if(l>=1) {
                image_first=images.get(0).getPath();
                Glide.with(this).load(image_first).into(iv_first);


                if(l>=2) {
                image_second=images.get(1).getPath();
                Glide.with(this).load(image_second).into(iv_second);

                if(l>=3) {
                    image_third=images.get(2).getPath();
                    Glide.with(this).load(image_first).into(iv_third);

                    ImageButton ib= (ImageButton) findViewById(R.id.button_pick_image);
                    ib.setImageResource(R.drawable.change);
                    }
                }
            }

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

    //===============================================앨범선택==========================================================
    // 앨범 호출
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_ALBUM);
    }

    private String getStringImage (Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_ALBUM:

                album_uri = data.getData();
                Glide.with(this).load(album_uri).into(iv_capture);
                try {
                    //Getting the Bitmap from Gallery
                    album_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), album_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

//            case RC_CODE_PICKER:
//                images = (ArrayList<Image>) ImagePicker.getImages(data);
//                printImages(images);
//
//                Uri firsturi=Uri.fromFile(new File(image_first));
//
//
//                try {
//                    first_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), firsturi);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                return;
        }
    }

    // 등록 버튼 클릭
    public void onClickedConfirm(View v) {

        // 한글 요리명 입력 안했을 때
        if (et_KoreaName.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "한국 요리명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_KoreaName.requestFocus();
            return;
        }

        // 영어 요리명 입력 안했을 때
        if (et_EnglishName.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "영어 요리명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_KoreaName.requestFocus();
            return;
        }

        // 요리 가격 입력 안했을 때
        if (et_Price.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "요리 가격을 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_Price.requestFocus();
            return;
        }

        // 요리 설명 입력 안했을 때
        if (et_Description.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "요리에 대한 설명을 해주세요.", Toast.LENGTH_SHORT).show();
            et_Description.requestFocus();
            return;
        }

        // 재료 설명 입력 안했을 때
        if (et_Ingredients.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "재료에 대한 설명을 해주세요.", Toast.LENGTH_SHORT).show();
            et_Ingredients.requestFocus();
            return;
        }

        // 출장 가능 지역 입력 안했을 때
        if (et_Area.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "출장 가능한 지역에 대해 입력해주세요.", Toast.LENGTH_SHORT).show();
            et_Area.requestFocus();
            return;
        }

        // 음식 사진 입력안했을 때
        if(iv_capture.getDrawable()==null) {
            Toast.makeText(getApplicationContext(),"요리리 사진을 등록해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        // 음식 사진 입력안했을 때
//        if(images.size()==0) {
//            Toast.makeText(getApplicationContext(),"추가 사진을 1장 이상 등록해주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }

        InsertDB();

    }

    private void InsertDB() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.71.239.151/foodadd.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Disimissing the progress dialog
                        loading.dismiss();
//                        //Showing toast message of the response
//                        Toast.makeText(getApplicationContext(), response , Toast.LENGTH_LONG).show();

                        Log.d(TAG, "Volley Response is : "+response);
                        loading.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getApplicationContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                loading.dismiss();


                //Creating parameters
                Map<String,String> map = new Hashtable<>();


                // 텍스트 입력 정보들
                String KoreaName=et_KoreaName.getText().toString(); // 1
                String EnglishName=et_EnglishName.getText().toString(); // 2
                String Price=et_Price.getText().toString(); // 3
                String Description=et_Description.getText().toString(); // 4
                String Ingredients=et_Ingredients.getText().toString(); // 5
                String Area=et_Area.getText().toString(); // 6

                //이미지
                //Converting Bitmap to String
                String imagePath = getStringImage(album_bitmap); // 7
                String imageName = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"; // 8

//                String imageName1 = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"; // 13
//                String imageName2 = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"; // 14
//                String imageName3 = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"; // 15


                //요리사 정보를 불러들이기 위한 요리사 이메일 저장
                String ChefEmail; // 9

                Log.d(TAG, "FB_LOGINCHECK : "+FB_LOGINCHECK);
                Log.d(TAG, "KAKAO_LOGINCHECK : "+KAKAO_LOGINCHECK);

                if(!FB_LOGINCHECK.equals("0")) {
                    SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
                    ChefEmail=pref.getString(FBEMAIL, "");
                    Log.d(TAG, "FB chefemail: "+ChefEmail);
                } else if(!KAKAO_LOGINCHECK.equals("0")) {
                    SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
                    ChefEmail=pref.getString(KAEMAIL, "");
                    Log.d(TAG, "KA chefemail: "+ChefEmail);
                } else { // 일반
                    SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
                    ChefEmail=pref.getString(CHEFNORMALLEMAIL, "");
                    Log.d(TAG, "Normal chefemail: "+ChefEmail);
                }


                //Adding parameters, 입력 변수들
                map.put("KoreaName", KoreaName); // 1
                map.put("EnglishName", EnglishName); // 2
                map.put("Price", Price); // 3
                map.put("Description", Description); // 4
                map.put("Ingredients", Ingredients); // 5
                map.put("Area", Area); // 6
                map.put("Chef_Email", ChefEmail); // 11
                // 이미지 + 쉐프 이메일
                map.put("image_Path", imagePath); // 7
                map.put("image_Name", imageName); // 1

//                String imagePath1 = getStringImage(first_bitmap); // 7
//
//                Log.d(TAG, "image_Path : "+imagePath);
//                Log.d(TAG, "image_Path1 : "+imagePath1);
//                Log.d(TAG, "image_Second : "+image_second);
//                Log.d(TAG, "image_Third : "+image_third);
//
//                // 추가 이미지
//                map.put("image_Path1", imagePath1); // 8
//                map.put("image_Name1", imageName1); // 2
//
//                map.put("image_Path2", image_second); // 9
//                map.put("image_Name2", imageName2); // 3
//
//                map.put("image_Path3", image_third); // 10
//                map.put("image_Name3", imageName3); // 4



                //returning parameters
                return map;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }



}




/*
    // 지역선택 검색기능
    public void onClickedSelectArea(View v) {
        Intent intent=new Intent(this, Home_Choosearea.class);
        startActivity(intent);
    }
*/