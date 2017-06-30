package thread.seopftware.mychef.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import thread.seopftware.mychef.R;
import thread.seopftware.mychef.Register.Register_chef;
import thread.seopftware.mychef.Register.Register_user;

public class Login_register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
    }

    public void onClickedChef(View v) {
        Intent intent=new Intent(getApplicationContext(), Register_chef.class);
        startActivity(intent);
    }

    public void onClickedUser(View v) {
        Intent intent=new Intent(getApplicationContext(), Register_user.class);
        startActivity(intent);
    }
}
