package thread.seopftware.mychef;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            Intent intent=new Intent(getApplicationContext(), Login_choose.class);
            startActivity(intent);
            finish();
        }
    };

    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}
