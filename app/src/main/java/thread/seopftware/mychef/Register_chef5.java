package thread.seopftware.mychef;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static thread.seopftware.mychef.Login_choose.FB_LOGINCHECK;
import static thread.seopftware.mychef.Login_choose.KAKAO_LOGINCHECK;
import static thread.seopftware.mychef.Register_chef.CURRENTTIME;
import static thread.seopftware.mychef.Register_chef.EMAIL;
import static thread.seopftware.mychef.Register_chef.FB_ID;
import static thread.seopftware.mychef.Register_chef.KAKAO_ID;
import static thread.seopftware.mychef.Register_chef.NAME;
import static thread.seopftware.mychef.Register_chef.PASSWORD;
import static thread.seopftware.mychef.Register_chef.PASSWORDCONFIRM;
import static thread.seopftware.mychef.Register_chef.PHONE;
import static thread.seopftware.mychef.Register_chef.REGISTER_CHEF;
import static thread.seopftware.mychef.Register_chef2.COMPANYDESCRIPTION;
import static thread.seopftware.mychef.Register_chef2.COMPANYEND;
import static thread.seopftware.mychef.Register_chef2.COMPANYNAME;
import static thread.seopftware.mychef.Register_chef2.COMPANYSTART;
import static thread.seopftware.mychef.Register_chef2.REGISTER_CHEF2;
import static thread.seopftware.mychef.Register_chef3.CERTIFICATION;
import static thread.seopftware.mychef.Register_chef3.CERTIFICATION2;
import static thread.seopftware.mychef.Register_chef3.CERTIFICATION3;
import static thread.seopftware.mychef.Register_chef3.REGISTER_CHEF3;
import static thread.seopftware.mychef.Register_chef4.APPEAL;
import static thread.seopftware.mychef.Register_chef4.APPEAL2;
import static thread.seopftware.mychef.Register_chef4.REGISTER_CHEF4;

public class Register_chef5 extends AppCompatActivity {

    private static String TAG="Register_chef5";


    static final int REQUEST_TAKE_PHOTO = 2001;
    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;

    ImageView iv_capture;
    String mCurrentPhotoPath;
    Uri photoURI, albumURI;
    Button btn_RegisterConfirm;
    boolean isAlbum=false;

    String Name, Email, Password, PasswordConfirm, Phone;
    String Fbapi, Kakaoapi, CurrentTime;
    String CompanyName, CompanyStart, CompanyEnd, CompanyDescription;
    String Certification, Certification2, Certification3;
    String Appeal, Appeal2;

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

        // Register_1 (회원가입 정보)
        SharedPreferences pref=getSharedPreferences(REGISTER_CHEF, MODE_PRIVATE);
        Name=pref.getString(NAME,"");
        Email=pref.getString(EMAIL,"");
        Password=pref.getString(PASSWORD,"");
        PasswordConfirm=pref.getString(PASSWORDCONFIRM,"");
        Phone=pref.getString(PHONE,"");
        Fbapi=pref.getString(FB_ID,"null");
        Kakaoapi=pref.getString(KAKAO_ID,"null");
        CurrentTime=pref.getString(CURRENTTIME, "");


        // Register_2 (경력 사항)
        SharedPreferences pref2=getSharedPreferences(REGISTER_CHEF2, MODE_PRIVATE);
        CompanyName=pref2.getString(COMPANYNAME,"");
        CompanyStart=pref2.getString(COMPANYSTART,"");
        CompanyEnd=pref2.getString(COMPANYEND,"");
        CompanyDescription=pref2.getString(COMPANYDESCRIPTION,"");

        // Register_3 (자격증 정보)
        SharedPreferences pref3=getSharedPreferences(REGISTER_CHEF3, MODE_PRIVATE);
        Certification=pref3.getString(CERTIFICATION,"");
        Certification2=pref3.getString(CERTIFICATION2,"null");
        Certification3=pref3.getString(CERTIFICATION3,"null");

        // Register_4 (본인 소개)
        SharedPreferences pref4=getSharedPreferences(REGISTER_CHEF4, MODE_PRIVATE);
        Appeal=pref4.getString(APPEAL,"");
        Appeal2=pref4.getString(APPEAL2,"");

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

    public void onClickedNext(View v) {
        // 프로필 사진 등록 안했을 때
        if(iv_capture.getDrawable()==null) {
            Toast.makeText(getApplicationContext(),"프로필 사진을 등록해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        Intent intent=new Intent(getApplicationContext(), Home_chef.class);
//        startActivity(intent);
//        finish();
//        Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다." , Toast.LENGTH_SHORT).show();

        String photoString=photoURI.toString();
        Log.d("사진", "photoURI :"+photoURI);
        Log.d("사진", "photoString :"+photoString);

        // db에 값 입력
        InsertData task=new InsertData();
//        task.execute(Name, Email, Password, PasswordConfirm, Phone, Fbapi, Kakaoapi, CurrentTime, CompanyName, CompanyStart, CompanyEnd, CompanyDescription, Certification, Certification2, Certification3, Appeal, Appeal2, photoString);
        task.execute(Name, Email, Password, PasswordConfirm, Phone, Fbapi, Kakaoapi, CurrentTime, Appeal, Appeal2, photoString);
        //회원가입과 동시에 shared에 저장되어 있는 모든 값 날리기 edtitor.clear();

//        Intent intent=new Intent(getApplicationContext(), Login_login.class);
//        startActivity(intent);
//        finish();
    }

    class InsertData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Register_chef5.this, "잠시만 기다려 주세요.", null, true, true);
        }

        @Override
        protected String doInBackground(String... params) {

            // Register_1 (회원가입)
            String Name=(String) params[0];
            String Email=(String) params[1];
            String Password=(String) params[2];
            String PasswordConfirm=(String) params[3];
            String Phone=(String) params[4];
            String Api_Id=(String) params[5];
            String Kakao_Id=(String) params[6];
            String CurrentTime=(String) params[7];

            Log.d(TAG, "Name=" +Name+" &Email=" +Email+" &Password="+Password+" &PasswordConfirm="+PasswordConfirm+" &Phone="+Phone+" &Api_Id="+Api_Id+" &Kakao_Id="+Kakao_Id+" &CurrentTime="+CurrentTime);

//            // Register_2 (경력사항)
//            String CompanyName=(String) params[8];
//            String CompanyStart=(String) params[9];
//            String CompanyEnd=(String) params[10];
//            String CompanyDescription=(String) params[11];
//
//            Log.d(TAG, "CompanyName : "+CompanyName+"CompanyStart : "+CompanyStart+"CompanyEnd : "+CompanyEnd+"CompanyDescription : "+CompanyDescription);

//            // Register_3 (자격증 정보)
//            String Certification=(String) params[8];
//            String Certification2=(String) params[9];
//            String Certification3=(String) params[10];
//
//            Log.d(TAG,"Certification : "+Certification+"Certification2 : "+Certification2+"Certification3 : "+Certification3);

            // Register_4 (본인 소개)
            String Appeal=(String) params[8];
            String Appeal2=(String) params[9];

            Log.d(TAG, "Appeal : "+Appeal+"Appeal 2: "+Appeal2);

            //Register_5 (프로필)
            String photoString=(String) params[10];

            Log.d(TAG, "photoString : "+photoString);

            String serverURL="http://115.71.239.151/register_chef.php";
//            String postParameters = "Name=" +Name+" &Email=" +Email+" &Password="+Password+" &PasswordConfirm="+PasswordConfirm+" &Phone="+Phone+" &Api_Id="+Api_Id+" &Kakao_Id="+Kakao_Id+" &CurrentTime="+CurrentTime+" &Certification="+Certification+" &Certification2="+Certification2+" &Certification3="+Certification3+" &Appeal="+Appeal+" &Appeal2="+Appeal2+" &photoString="+photoString;
            String postParameters = "Name=" +Name+" &Email=" +Email+" &Password="+Password+" &PasswordConfirm="+PasswordConfirm+" &Phone="+Phone+" &Api_Id="+Api_Id+" &Kakao_Id="+Kakao_Id+" &CurrentTime="+CurrentTime+" &Appeal="+Appeal+" &Appeal2="+Appeal2+" &photoString="+photoString;
            Log.d(TAG, "postParameters : "+postParameters);
            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream=httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode=httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code -"+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode== HttpURLConnection.HTTP_OK) {
                    inputStream=httpURLConnection.getInputStream();
                } else {
                    inputStream=httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader=new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

                StringBuilder sb=new StringBuilder();
                String line=null;

                while((line=bufferedReader.readLine())!=null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return new String("Error : "+e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response :" +result);

            if(FB_LOGINCHECK==null && KAKAO_LOGINCHECK==null) // 일반 회원 가입
            {
                if(Integer.parseInt(result)==0) {
                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다." , Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(Register_chef5.this, Login_login.class);
                    startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    finish();

                } else if (Integer.parseInt(result)==1) {
                    Toast.makeText(Register_chef5.this, "error 발생", Toast.LENGTH_SHORT).show();
                    return;
                }

            } else {
                if(Integer.parseInt(result)==0) {
                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다." , Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(Register_chef5.this, Home_chef.class);
                    startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    finish();

                } else if (Integer.parseInt(result)==1) {
                    Toast.makeText(Register_chef5.this, "error 발생", Toast.LENGTH_SHORT).show();
                    return;
                }

            }



        }
    }
}
