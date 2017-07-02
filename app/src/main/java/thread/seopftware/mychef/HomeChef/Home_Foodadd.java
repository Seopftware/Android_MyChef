package thread.seopftware.mychef.HomeChef;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

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
    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;
    public static final int AREACHOOSE = 999;

    String encoded_string, image_name;
    Bitmap bitmap;
    File file;
    Uri file_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_foodadd);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("요리 등록");

        // 프로필 이미지
        iv_capture= (ImageView) findViewById(R.id.iv_capture);
        iv_capture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final CharSequence[] items=new CharSequence[] {"Choose from Gallery", "Take a Camera"};
                AlertDialog.Builder dialog=new AlertDialog.Builder(Home_Foodadd.this);
                dialog.setTitle("MENU");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(items[which]=="Take a Camera") {
                            captureCamera();
                        }

                        if(items[which]=="Choose from Gallery") {
                            getAlbum();
                        }
                    }
                });
                dialog.show();
            }
        });
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

    //========================================================================이미지 관련 함수============================================================

    // 카메라 호출
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
                photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // 앨범 호출
    public void getAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    // 이미지 크랍
    public void cropImage(){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("scale", true);

        if(isAlbum == false) {
            cropIntent.putExtra("output", photoURI); // 크랍된 이미지를 해당 경로에 저장
        } else if(isAlbum == true){
            cropIntent.putExtra("output", albumURI); // 크랍된 이미지를 해당 경로에 저장
        }

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    // 파일 생성
    private File createImageFile() throws IOException {

        // 특정 경로와 폴더를 지정하지 않고, 메모리 최상 위치에 저장 방법
        String imageFileName = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory(), imageFileName);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        String resName="@drawable/photo1";
        String packName = this.getPackageName(); // 패키지명
        int resID = getResources().getIdentifier(resName, "drawable", packName);



        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                isAlbum = false;
                cropImage();
                break;

            case REQUEST_TAKE_ALBUM:
                isAlbum = true;
                File albumFile = null;
                try {
                    albumFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(albumFile != null){
                    albumURI = Uri.fromFile(albumFile);
                }
                photoURI = data.getData();
//                Glide.with(this).load(photoURI).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_capture);

                Glide.with(this).load(photoURI).into(iv_capture);
                break;

            case REQUEST_IMAGE_CROP:
                galleryAddPic();
                photoURI=data.getData();
//                Glide.with(this).load(photoURI).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_capture);
                Glide.with(this).load(photoURI).placeholder(resID).into(iv_capture);
                break;
        }
    }

    // 등록 버튼 클릭
    public void onClickedConfirm(View v) {

        finish();

        String chefemail=null;

        if(FB_LOGINCHECK!=null) {
            SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            chefemail=pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB chefemail: "+chefemail);
        } else if(KAKAO_LOGINCHECK!=null) {
            SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            chefemail=pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA chefemail: "+chefemail);
        } else { // 일반
            SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            chefemail=pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal chefemail: "+chefemail);
        }






    }

}

/*
    // 지역선택 검색기능
    public void onClickedSelectArea(View v) {
        Intent intent=new Intent(this, Home_Choosearea.class);
        startActivity(intent);
    }
*/