package thread.seopftware.mychef.HomeUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import thread.seopftware.mychef.R;

public class User_Payment_Address extends AppCompatActivity {
    
    private WebView browser;
    EditText et_SpecificAddress, et_Address;

    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_payment_address);

        //액션바 설정 부분
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Get the ActionBar here to configure the way it behaves.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("출장 주소 입력");

        et_Address= (EditText) findViewById(R.id.et_Address);
        et_SpecificAddress= (EditText) findViewById(R.id.et_SpecificAddress);
        
        browser = (WebView) findViewById(R.id.WebView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                browser.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

//        browser.loadUrl("file:///android_asset/daum.html");
//        browser.loadUrl("http://www.daddyface.com/public/daum.html");
        browser.loadUrl("http://cdn.rawgit.com/jolly73-df/DaumPostcodeExample/master/DaumPostcodeExample/app/src/main/assets/daum.html");
    }
    
    class MyJavaScriptInterface {
        
        @JavascriptInterface
        public void processDATA(final String data) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Log.d("data", data);
                            et_Address.setText(data);
                        }
                    });
                }
            }).start();


        }
    }

    //액션바 백키 버튼 구현
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }

            case R.id.action_button: {

                if(et_Address.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    et_Address.requestFocus();
                    return true;
                }

                if (et_SpecificAddress.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "상세 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    et_SpecificAddress.requestFocus();
                    return true;
                }

                String address=et_Address.getText().toString();
                String address2=et_SpecificAddress.getText().toString();

                Bundle extra = new Bundle();
                Intent intent = new Intent();
                extra.putString("address", address);
                extra.putString("address2", address2);
                intent.putExtras(extra);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        return true;
    }
    
    
}
