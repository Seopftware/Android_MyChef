package thread.seopftware.mychef.Chatting;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;

/**
 * Created by MSI on 2017-08-09.
 */

public class MainActivity extends Activity {

    //액티비티에서 선언.
    private MainService mService; //서비스 클래스

    //서비스 커넥션 선언.
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService.MainServiceBinder binder = (MainService.MainServiceBinder) service;
            mService = binder.getService(); //서비스 받아옴
            mService.registerCallback(mCallback); //콜백 등록
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    //서비스에서 아래의 콜백 함수를 호출하며, 콜백 함수에서는 액티비티에서 처리할 내용 입력
    private MainService.ICallback mCallback = new MainService.ICallback() {
        public void recvData() {

            //처리할 일들..
        }
    };

    //서비스 시작. (버튼에서 onClick 이벤트 걸어놓은 것)
    public void startServiceMethod(View v){
        Intent Service = new Intent(this, MainService.class);
        bindService(Service, mConnection, Context.BIND_AUTO_CREATE);
    }

    //액티비티에서 서비스 함수 호출
    public void test() {
        mService.myServiceFunc();
    }

}
