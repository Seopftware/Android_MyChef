package thread.seopftware.mychef.Register;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
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

import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }


    private static String TAG = "Register_chef5";

    ProgressDialog progressDialog;

    private static final int REQUEST_ALBUM = 2002;

    //카메라
    boolean isAlbum = true; // 카메라인지 앨범인지 구별하기 위한 변수 (true:카메라 , false:앨범)
    String camera_path; // 파일 경로, 파일 이름
    String camera_name;
    ImageView iv_capture;
    Uri camera_uri;


    //앨범
    Bitmap album_bitmap;
    String album_path; // 파일 경로, 파일 이름
    String album_name;
    Uri album_uri;

    // DB에 입력되는 변수들
    String Name, Email, Password, PasswordConfirm, Phone;
    String Fbapi, Kakaoapi, CurrentTime;
    String Appeal, Appeal2;
    Button btn_RegisterConfirm;

    // JNI를 사용하기 위한 변수
    Mat gallery_input;
    Mat gallery_output;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_chef5);

        btn_RegisterConfirm = (Button) findViewById(R.id.btn_RegisterConfirm);

        // Toolbar 옵션
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("프로필 사진");



        //저장값 불러오기
        // Register_1 (회원가입 정보)
        SharedPreferences pref = getSharedPreferences(Register_chef.REGISTER_CHEF, MODE_PRIVATE);
        Name = pref.getString(Register_chef.NAME, "");
        Email = pref.getString(Register_chef.EMAIL, "");
        Password = pref.getString(Register_chef.PASSWORD, "");
        Phone = pref.getString(Register_chef.PHONE, "");
        CurrentTime = pref.getString(Register_chef.CURRENTTIME, "");

        // Register_4 (본인 소개)
        SharedPreferences pref4 = getSharedPreferences(Register_chef4.REGISTER_CHEF4, MODE_PRIVATE);
        Appeal = pref4.getString(Register_chef4.APPEAL, "");
        Appeal2 = pref4.getString(Register_chef4.APPEAL2, "");




        // 프로필 이미지 클릭시 다이얼로그 띄우기
        iv_capture = (ImageView) findViewById(R.id.iv_capture);
        iv_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = new CharSequence[]{"Take a Camera", "Choose from Gallery"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(Register_chef5.this);
                dialog.setTitle("MENU");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which] == "Take a Camera") {
                            Intent intent = new Intent(getApplicationContext(), Detector_camera.class);
                            startActivity(intent);

                        }

                        if (items[which] == "Choose from Gallery") {
                            showFileChooser();

                        }
                    }
                });
                dialog.show();
            }
        });


        // 이미지 인식에 필요한 파일 다운로드
        read_cascade_file();

        //===========================================================================================================
        // Detector_camera.class로 부터 받아온 값

        Intent intent= getIntent();
        if(intent.getStringExtra("Camera_strphoto")!=null) {


            // String(원래 uri) -> Uri로 변경
            isAlbum = true;
            String strphoto = intent.getStringExtra("Camera_strphoto");
            camera_uri = Uri.parse(strphoto);
            Log.d(TAG, "Detector_camera.class로 부터 받은 uri : "+ camera_uri);
            Glide.with(getApplicationContext()).load(camera_uri).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_capture);

            camera_path = intent.getStringExtra("Camera_Path"); // encoded화된 이미지 스트링
            camera_name = intent.getStringExtra("Camera_Name"); // 이미지 파일 이름

        }

    }
    // onCreate 끝
    //===========================================================================================================

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


    //===============================================앨범선택==========================================================
    // 1. 앨범 호출
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_ALBUM);
    }

    // 2. 선택한 앨범에서 이미지 데이터 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ALBUM:
                isAlbum = false; // 저장모드를 앨범으로

                album_uri = data.getData();

                String album_abpath = uriTopath(album_uri);
                Log.d(TAG, "얼굴 검출 이미지 절대 경로 : " + album_abpath);

                read_image_file(album_abpath); // 이미지 경로를 보내주고 그 값을 통해 Mat input에 값을 넣어줌

                long face = detect_album(cascadeClassifier_face, cascadeClassifier_eye, gallery_input.getNativeObjAddr(), gallery_output.getNativeObjAddr());


                //(얼굴인식 학습파일, 눈인식 학습파일, 이미지 경로,
                Log.d(TAG, "얼굴인식 되나여!!!?: " + face);


                try {
                    // 앨범에서 비트맵 값 얻어내기
                    album_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), album_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(face == 0) {
                    Toast.makeText(getApplicationContext(), "얼굴이 검출되지 않았습니다.\n다른 사진을 선택해 주세요.", Toast.LENGTH_LONG).show();
                } else {
                    Glide.with(this).load(album_uri).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_capture);

                    //Converting Bitmap to String
                    album_path = getStringImage(album_bitmap);
                    album_name = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";

                }

                break;
        }
    }

    // 이미지를 불러들인 후 값을 담기 위한 함수
    private void read_image_file(String path) {

        gallery_input = new Mat();
        gallery_output = new Mat();

        loadImage(path, gallery_input.getNativeObjAddr()); // 저장된 경로로 가서 이미지를 로드해옴

    }

    // uri -> path (Uri 값에서 이미지 절대 경로를 얻어냄)
    public String uriTopath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    // Bitmap -> String (encoded화된 String을 얻어냄)
    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //===========================================================================================================
    // 회원가입 마무리 버튼 눌렸을 때
    public void onClickedNext(View v) {
        // 프로필 사진 등록 안했을 때
        if (iv_capture.getDrawable() == null) {
            Toast.makeText(getApplicationContext(), "프로필 사진을 등록해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }


        Log.d(TAG, "**********************************************************************");
        Log.d(TAG, "(회원가입 마무리 버튼 클릭)");
        Log.d(TAG, "isAlbum : " + isAlbum);
        Log.d(TAG, "**********************************************************************");


        if(isAlbum==true) {
            Camera_Upload();
        }

        else if (isAlbum==false) {
            Album_Upload();
        }


    }
    //===========================================================================================================

    //===========================================================================================================
    // 카메라 사진을 서버에 업로드 하는 함수
    private void Camera_Upload() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://115.71.239.151/register_chef.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Camera_Upload Response is : " + response);

                        if (FB_LOGINCHECK.equals("0") && KAKAO_LOGINCHECK.equals("0")) // 일반 회원 가입
                        {
                            if (Integer.parseInt(response) == 0) {
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(Register_chef5.this, Login_login.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();



                            } else if (Integer.parseInt(response) == 1) {
                                Toast.makeText(Register_chef5.this, "error 발생", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } else {
                            if (Integer.parseInt(response) == 0) {
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(Register_chef5.this, Home_chef.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();



                            } else if (Integer.parseInt(response) == 1) {
                                Toast.makeText(Register_chef5.this, "error 발생", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Dismissing the progress dialog

                //Showing toast
                Toast.makeText(getApplicationContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();


                Log.d(TAG, " Name:" + Name + " Email: " + Email + " Password: " + Password + " Phone: " + Phone + "\n");

                map.put("Name", Name);
                map.put("Email", Email);
                map.put("Password", Password);
                map.put("Phone", Phone);
                map.put("Fbapi", FB_LOGINCHECK);
                map.put("Kakaoapi", KAKAO_LOGINCHECK);
                map.put("CurrentTime", CurrentTime);
                map.put("Appeal", Appeal);
                map.put("Appeal2", Appeal2);
                map.put("encoded_string", camera_path);
                map.put("image_name", camera_name);

                return map;
            }
        };
        requestQueue.add(request);
    }


    //===========================================================================================================
    // 앨범 사진을 서버에 업로드 하는 함수
    private void Album_Upload() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.71.239.151/register_chef.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "Volley Response is : " + response);

                        if (FB_LOGINCHECK == null && KAKAO_LOGINCHECK == null) // 일반 회원 가입
                        {
                            if (Integer.parseInt(response) == 0) {
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(Register_chef5.this, Login_login.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();


                            } else if (Integer.parseInt(response) == 1) {
                                Toast.makeText(Register_chef5.this, "error 발생", Toast.LENGTH_SHORT).show();
                                return;


                            }

                        } else {
                            if (Integer.parseInt(response) == 0) {
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(Register_chef5.this, Login_login.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();

                            } else if (Integer.parseInt(response) == 1) {
                                Toast.makeText(Register_chef5.this, "error 발생", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Creating parameters
                Map<String, String> map = new Hashtable<>();

                Log.d(TAG, "겔러리에서 사진 가지고 온 후 " + " Name:" + Name + " Email: " + Email + " Password: " + Password + " Phone: " + Phone + "\n");
                Log.d(TAG, "FBapi : " + FB_LOGINCHECK);
                Log.d(TAG, "KAKAOapi : " + KAKAO_LOGINCHECK);

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
                map.put("encoded_string", album_path);
                map.put("image_name", album_name);

                //returning parameters
                return map;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }




    //===========================================================================================================
    // 얼굴 탐지 기능을 위한 얼굴 학습 open cv xml 파일을 내 폰으로 복사하는 함수 - copyFile
    // 복사된 파일을 읽어 들인 후 CascadeClassifier 객체 생성 하는 함수 - readcascade_file


    // 폰으로 얼굴 감지 학습된 파일 복사
    private void copyFile(String filename) {
        String baseDir = Environment.getExternalStorageDirectory().getPath();
        String pathDir = baseDir + File.separator + filename;

        AssetManager assetManager = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            Log.d(TAG, "copyFile :: 다음 경로로 파일복사 " + pathDir);
            inputStream = assetManager.open(filename);
            outputStream = new FileOutputStream(pathDir);

            byte[] buffer = new byte[1024];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            inputStream.close();
            inputStream = null;

            outputStream.flush();
            outputStream.close();
            outputStream = null;

        } catch (Exception e) {
            Log.d(TAG, "copyFile :: 파일 복사 중 예외 발생 " + e.toString());
            e.printStackTrace();
        }
    }

    // 폰으로 복사한 파일을 JNI함수를 통해 읽어들임
    private void read_cascade_file() {

        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        Log.d(TAG, "read_cascade_file");

        //loadCascade 메소드는 외부 저장소의 특정 위치에서 해당 파일을 읽어와서 CascadeClassifier 객체로 로드

        cascadeClassifier_face = loadCascade( "haarcascade_frontalface_alt.xml");
        cascadeClassifier_eye = loadCascade( "haarcascade_eye_tree_eyeglasses.xml");
    }
    //===========================================================================================================


    //======================================================================================================================
    // C++ 적용 (JNI 함수들) - 얼굴 인식
    //======================================================================================================================

    public static native long loadCascade(String cascadeFileName); // 얼굴검출 학습 파일 읽기 함수
    public native void loadImage(String imageFileName, long img); // 이미지 파일 읽기 함수
    public static native long detect_album(long cascadeClassifier_face, long cascadeClassifier_eye, long matAddrInput, long matAddrResult); // 얼굴검출 후 원 그려주는 함수
    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;

}


/*
일반 카메라 호출 시

    public void captureCamera() {
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

    // 사진 찍은 후 파일 생성
    private File createImageFile() throws IOException {

        // 특정 경로와 폴더를 지정하지 않고, 메모리 최상 위치에 저장 방법
        imageFileName = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory(), imageFileName);
        try {
            storageDir.getParentFile().mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCurrentPhotoPath = storageDir.getAbsolutePath();
        return storageDir;
    }
 */