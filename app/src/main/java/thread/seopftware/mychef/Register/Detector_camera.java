package thread.seopftware.mychef.Register;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import thread.seopftware.mychef.R;

import static thread.seopftware.mychef.Register.Register_chef5.loadCascade;

public class Detector_camera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    static {
        System.loadLibrary("opencv_java3"); // lib 파일 이름
        System.loadLibrary("native-lib");
    }

    // 전역 변수
    private static final String TAG = "Detector_camera";
    private static final int IMAGE_CROP = 1111; // 이미지 크랍 request code

    // 얼굴인식 변수
    CameraBridgeViewBase mOpenCvCameraView;
    Mat matInput, matOutput;
    Uri camera_uri;
    Bitmap camera_bitmap; // 사진 저장을 위한 bitmap 변환
    // UI 변수
    FloatingActionButton fab;


    // 이미지를 받아오기 위한 콜백 함수
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {

            switch (status) {
                case LoaderCallbackInterface.SUCCESS:

                    mOpenCvCameraView.enableView();
                    break;

                default:
                    super.onManagerConnected(status);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 시스템 윈도우바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 스크린 항상 켜져 있기
        setContentView(R.layout.activity_face_detector);

        read_cascade_file();


        //===========================================================================================================
        //UI 객체 선언
        //===========================================================================================================
        fab = (FloatingActionButton) findViewById(R.id.fab);
        //플로팅 액션 버튼 클릭 이벤트
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmapOutput = Bitmap.createBitmap(matInput.cols(), matInput.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(matInput, bitmapOutput); // Mat를 bitmap 형태로 바꿔주는 함수
                Log.d(TAG, "bitmapOutput : " + bitmapOutput);

                camera_uri = getImageUri(getApplicationContext(), bitmapOutput); // 이미지 크랍을 시켜주기 위해 (Bitmap -> Uri)로 변형
                cropImage(); // 얼굴 인식 후 카메라로 찍은 사진을 Crop 해준다.
            }
        });

        //===========================================================================================================
        //CameraBridgeViewBase 옵션
        //===========================================================================================================
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE); // Surfaceview
        mOpenCvCameraView.setCvCameraViewListener(this); // 클릭 리스너
        mOpenCvCameraView.setCameraIndex(1); // 0으로 설정하면 front camera / 1로 설정하면 self camera
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS); // 콜백 함수

    }

    // onCreate 끝
    //===========================================================================================================



    //===========================================================================================================
    //CameraBridgeViewBase 옵션
    //===========================================================================================================

    // 이미지 크랍
    public void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(camera_uri, "image/*");
        cropIntent.putExtra("scale", true);

        cropIntent.putExtra("output", camera_uri); // 카메라 크랍된 이미지를 해당 경로에 저장

        startActivityForResult(cropIntent, IMAGE_CROP);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String bimaptoString(Bitmap bmp) {
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
            case IMAGE_CROP:
                camera_uri=data.getData();
                Log.d(TAG, "camera_uri (카메라): "+camera_uri);

                try {
                    // 앨범에서 비트맵 값 얻어내기
                    camera_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), camera_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String imagePath = bimaptoString(camera_bitmap);
                String imageName = "photo_" + String.valueOf(System.currentTimeMillis()) + ".jpg";


                String Camera_photo = String.valueOf(camera_uri);
                Intent intent = new Intent(getApplicationContext(), Register_chef5.class);
                intent.putExtra("Camera_strphoto", Camera_photo);
                intent.putExtra("Camera_Path", imagePath);
                intent.putExtra("Camera_Name", imageName);
                startActivity(intent);


                break;
        }
    }
    //===========================================================================================================




    //======================================================================================================================
    // 카메라로부터 영상을 가져올 때 마다 불러지는 함수.
    // 카메라로부터 영상을 읽어올 때, 전면 카메라의 경우 영상이 뒤집혀서 읽히기 때문에 180도 회전시켜 줘야함.
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        matInput = inputFrame.rgba(); // 사진 파일이 저장되어 있음. 이것을 비트맵으로 변환시켜줘야함.

        if ( matOutput != null ) matOutput.release();
        matOutput = new Mat(matInput.rows(), matInput.cols(), matInput.type());

        Core.flip(matInput, matInput, 1);
        final long face = detect_camera(cascadeClassifier_face, cascadeClassifier_eye, matInput.getNativeObjAddr(), matOutput.getNativeObjAddr()); // 얼굴을 검출하는 cpp 코드를 호출하는 부분

        new Thread(new Runnable() { // UI 변경을 위한 Thread
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(face==0) {

                            Log.d(TAG, "핸들러: 얼굴인식 안됨");
                            fab.setVisibility(View.INVISIBLE);

                        } else {

                            Log.d(TAG, "핸들러: 얼굴인식 됨");
                            fab.setVisibility(View.VISIBLE);

                        }
                    }
                });
            }
        }).start();

        return matOutput;
    }


    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

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
        Log.d(TAG, "read_cascade_file");

        copyFile("haarcascade_frontalface_alt.xml");
        copyFile("haarcascade_eye_tree_eyeglasses.xml");

        //loadCascade 메소드는 외부 저장소의 특정 위치에서 해당 파일을 읽어와서 CascadeClassifier 객체로 로드
        cascadeClassifier_face = loadCascade( "haarcascade_frontalface_alt.xml");
        cascadeClassifier_eye = loadCascade( "haarcascade_eye_tree_eyeglasses.xml");
    }
    //===========================================================================================================


    private void PhotoSave(final String path, final String name) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://115.71.239.151/PhotoSave.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "PhotoSave Response (path) : " + response);





                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();

                map.put("image_path", path);
                map.put("image_name", name);

                return map;
            }
        };
        requestQueue.add(request);
    }


    //======================================================================================================================
    // 생명주기
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    public static native long detect_camera(long cascadeClassifier_face, long cascadeClassifier_eye, long matAddrInput, long matAddrResult);
    public long cascadeClassifier_face = 0;
    public long cascadeClassifier_eye = 0;
}
