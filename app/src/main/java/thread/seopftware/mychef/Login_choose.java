package thread.seopftware.mychef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Login_choose extends AppCompatActivity {

    Button btnRegister;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_choose);

        btnRegister=(Button) findViewById(R.id.btnRegister);
        btnLogin=(Button) findViewById(R.id.btnLogin);

    }

    public void onClickedRegister(View v) {
        Intent intent=new Intent(getApplicationContext(), Login_register.class);
        startActivity(intent);
    }

    public void onClickedLogin(View v) {
        Intent intent=new Intent(getApplicationContext(), Login_login.class);
        startActivity(intent);
    }
}
