package thread.seopftware.mychef;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

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
                   if(items[which]=="Choose from Gallery") {


                   }

                   if(items[which]=="Take a Camera") {

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
}
