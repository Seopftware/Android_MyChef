package thread.seopftware.mychef;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class Register_chef5 extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;

    ImageView iv_capture;
    String mCurrentPhotoPath;
    Uri photoURI, albumURI;
    boolean isAlbum=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_chef5);

        // Toolbar 옵션
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("프로필 사진");

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
                       getAlbum();
                   }
               }
               });
               dialog.show();
           }
        });


    }

    // Toolbar 빽키 구현ddaasd
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

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
                Glide.with(this).load(photoURI).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_capture);
                break;

            case REQUEST_IMAGE_CROP:
                galleryAddPic();
                photoURI=data.getData();
                Glide.with(this).load(photoURI).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_capture);
                break;
        }
    }
}
