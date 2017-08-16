package thread.seopftware.mychef.Chatting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import thread.seopftware.mychef.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class Chat_PinchZoom extends AppCompatActivity {

    private static final String TAG = "Chat_PinchZoom";
    PhotoViewAttacher attacher; // Pinch Zoom 라이브러리
    ImageView iv_Profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_pinch_zoom);

        Intent intent = getIntent();
        String url = intent.getStringExtra("profile");

        Log.d(TAG, "값 확인 url : " + url);


        PhotoView photoView = (PhotoView) findViewById(R.id.widget_photoview);
        Glide.with(getApplicationContext()).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(photoView);

    }
}
