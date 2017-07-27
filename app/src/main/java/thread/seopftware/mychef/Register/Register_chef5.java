package thread.seopftware.mychef.Register;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import thread.seopftware.mychef.HomeChef.Home_chef;
import thread.seopftware.mychef.Login.Login_login;
import thread.seopftware.mychef.R;

import static thread.seopftware.mychef.Login.Login_login.FB_LOGINCHECK;
import static thread.seopftware.mychef.Login.Login_login.KAKAO_LOGINCHECK;


public class Register_chef5 extends AppCompatActivity {

    private static String TAG="Register_chef5";

    ProgressDialog progressDialog;

    static final int REQUEST_CAMERA = 2001;
    static final int REQUEST_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;

    //카메라
    boolean isAlbum=false; // 카메라인지 앨범인지 구별하기 위한 변수
    String encoded_string; // 파일 경로, 파일 이름

    ImageView iv_capture;
    String mCurrentPhotoPath;
    Bitmap camera_bitmap;
    Uri camera_uri;


    //앨범
    String imageFileName;
    Bitmap album_bitmap;
    Uri album_uri;

    // DB에 입력되는 변수들
    String Name, Email, Password, PasswordConfirm, Phone;
    String Fbapi, Kakaoapi, CurrentTime;
    String Appeal, Appeal2;
    Button btn_RegisterConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_chef5);

        btn_RegisterConfirm= (Button) findViewById(R.id.btn_RegisterConfirm);

        // Toolbar 옵션
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("프로필 사진");

        //저장값 불러오기
        // Register_1 (회원가입 정보)
        SharedPreferences pref=getSharedPreferences(Register_chef.REGISTER_CHEF, MODE_PRIVATE);
        Name=pref.getString(Register_chef.NAME,"");
        Email=pref.getString(Register_chef.EMAIL,"");
        Password=pref.getString(Register_chef.PASSWORD,"");
        Phone=pref.getString(Register_chef.PHONE,"");
//        Fbapi=pref.getString(Register_chef.FB_ID,"null");
//        Kakaoapi=pref.getString(Register_chef.KAKAO_ID,"null");
        CurrentTime=pref.getString(Register_chef.CURRENTTIME, "");

        // Register_4 (본인 소개)
        SharedPreferences pref4=getSharedPreferences(Register_chef4.REGISTER_CHEF4, MODE_PRIVATE);
        Appeal=pref4.getString(Register_chef4.APPEAL,"");
        Appeal2=pref4.getString(Register_chef4.APPEAL2,"");

        // 프로필 이미지
        iv_capture= (ImageView) findViewById(R.id.iv_capture);
        iv_capture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final CharSequence[] items=new CharSequence[] {"Choose from Gallery", "Take a Camera"};
                AlertDialog.Builder dialog=new AlertDialog.Builder(Register_chef5.this);
                dialog.setTitle("MENU");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(items[which]=="Take a Camera") {
                            captureCamera();
                        }

                        if(items[which]=="Choose from Gallery") {
                            showFileChooser();
                        }
                    }
                });
                dialog.show();
            }
        });

    }

    // Toolbar 빽키 구현
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //===============================================카메라촬영==========================================================
    // 사진찍기
    public void captureCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                camera_uri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    // 이미지 크랍
    public void cropImage(){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(camera_uri, "image/*");
        cropIntent.putExtra("scale", true);

        if(isAlbum == false) {
            cropIntent.putExtra("output", camera_uri); // 카메라 크랍된 이미지를 해당 경로에 저장
        }

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    // 파일 생성
    private File createImageFile() throws IOException {

        // 특정 경로와 폴더를 지정하지 않고, 메모리 최상 위치에 저장 방법
        imageFileName = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory(), imageFileName);
        try{
            storageDir.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCurrentPhotoPath = storageDir.getAbsolutePath();
        return storageDir;
    }

    // 갤러리 새로고침, ACTION_MEDIA_MOUNTED는 하나의 폴더, FILE은 하나의 파일을 새로 고침할 때 사용함
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
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
            case REQUEST_CAMERA:
                isAlbum = false;
                cropImage();
                break;

            case REQUEST_ALBUM:
                isAlbum = true;
                album_uri = data.getData();
                Glide.with(this).load(album_uri).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_capture);
                try {
                    //Getting the Bitmap from Gallery
                    album_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), album_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case REQUEST_IMAGE_CROP:
                galleryAddPic();
                camera_uri=data.getData();
                Log.d(TAG, "camera_uri (카메라): "+camera_uri);
                Glide.with(this).load(camera_uri).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_capture);
                break;
        }
    }

    public void onClickedNext(View v) {
        // 프로필 사진 등록 안했을 때
        if(iv_capture.getDrawable()==null) {
            Toast.makeText(getApplicationContext(),"프로필 사진을 등록해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "isAlbum : (true or false) :" +isAlbum );

        if(isAlbum==false) {
            Log.d(TAG, "camera_uri (백그라운드): "+camera_uri);

            camera_bitmap = BitmapFactory.decodeFile(camera_uri.getPath());
            Log.d(TAG, "camera_bitmap: "+camera_bitmap+"file uri : "+camera_uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            camera_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            camera_bitmap.recycle();

            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            Camera_Upload();

        } else if (isAlbum==true) {
            Album_Upload();
        }

    }

       private void Camera_Upload() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://115.71.239.151/register_chef.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Volley Response is : "+response);
                        loading.dismiss();

                        if(FB_LOGINCHECK.equals("0") && KAKAO_LOGINCHECK.equals("0")) // 일반 회원 가입
                        {
                            if(Integer.parseInt(response)==0) {
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다." , Toast.LENGTH_LONG).show();

                                Intent intent=new Intent(Register_chef5.this, Login_login.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();

                                progressDialog.dismiss();


                            } else if (Integer.parseInt(response)==1) {
                                Toast.makeText(Register_chef5.this, "error 발생", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } else {
                            if(Integer.parseInt(response)==0) {
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다." , Toast.LENGTH_LONG).show();

                                Intent intent=new Intent(Register_chef5.this, Home_chef.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();

                                progressDialog.dismiss();


                            } else if (Integer.parseInt(response)==1) {
                                Toast.makeText(Register_chef5.this, "error 발생", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Dismissing the progress dialog
                loading.dismiss();

                //Showing toast
                Toast.makeText(getApplicationContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();


                Log.d(TAG, " Name:"+Name+" Email: "+Email+" Password: "+Password+" Phone: "+Phone+"\n");

                map.put("Name", Name);
                map.put("Email", Email);
                map.put("Password", Password);
                map.put("Phone", Phone);
                map.put("Fbapi", FB_LOGINCHECK);
                map.put("Kakaoapi", KAKAO_LOGINCHECK);
                map.put("CurrentTime", CurrentTime);
                map.put("Appeal", Appeal);
                map.put("Appeal2", Appeal2);
                map.put("encoded_string",encoded_string);
                map.put("image_name", imageFileName);

                return map;
            }
        };
        requestQueue.add(request);
    }

    private void Album_Upload(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.71.239.151/register_chef.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();

                        Log.d(TAG, "Volley Response is : "+response);
                        loading.dismiss();

                        if(FB_LOGINCHECK==null && KAKAO_LOGINCHECK==null) // 일반 회원 가입
                        {
                            if(Integer.parseInt(response)==0) {
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다." , Toast.LENGTH_LONG).show();

                                Intent intent=new Intent(Register_chef5.this, Login_login.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();

                                progressDialog.dismiss();


                            } else if (Integer.parseInt(response)==1) {
                                Toast.makeText(Register_chef5.this, "error 발생", Toast.LENGTH_SHORT).show();
                                return;


                            }

                        } else {
                            if(Integer.parseInt(response)==0) {
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다." , Toast.LENGTH_LONG).show();

                                Intent intent=new Intent(Register_chef5.this, Login_login.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();

                            } else if (Integer.parseInt(response)==1) {
                                Toast.makeText(Register_chef5.this, "error 발생", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
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
                //Converting Bitmap to String
                String image = getStringImage(album_bitmap);
                String CameraFileName = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";

                //Creating parameters
                Map<String,String> map = new Hashtable<>();

                Log.d(TAG, "겔러리에서 사진 가지고 온 후 "+" Name:"+Name+" Email: "+Email+" Password: "+Password+" Phone: "+Phone+"\n");
                Log.d(TAG, "FBapi : "+FB_LOGINCHECK);
                Log.d(TAG, "KAKAOapi : "+KAKAO_LOGINCHECK);

                //Adding parameters
                map.put("Name", Name);
                map.put("Email", Email);
                map.put("Password", Password);
                map.put("Phone", Phone);
                map.put("Fbapi", FB_LOGINCHECK);
                map.put("Kakaoapi", KAKAO_LOGINCHECK);
                map.put("CurrentTime", CurrentTime);
                map.put("Appeal", Appeal);
                map.put("Appeal2", Appeal2);
                map.put("encoded_string",image);
                map.put("image_name", CameraFileName);

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
