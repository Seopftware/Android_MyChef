package thread.seopftware.mychef.Service;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import thread.seopftware.mychef.R;

public class ServiceExample extends AppCompatActivity {

    private Button btn_Start, btn_End;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_example);

        btn_Start= (Button) findViewById(R.id.btn_Start);
        btn_End= (Button) findViewById(R.id.btn_End);

        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "서비스 시작", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ServiceExample.this, MyService.class);
                startService(intent);
            }
        });


        btn_End.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "서비스 종료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ServiceExample.this, MyService.class);
                stopService(intent);

//                Intent intent1 = new Intent(ServiceExample.this, MyService.class);
//                stopService(intent1);
            }
        });
    }
}
