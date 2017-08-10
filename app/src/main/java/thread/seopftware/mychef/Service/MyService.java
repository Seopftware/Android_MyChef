package thread.seopftware.mychef.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import thread.seopftware.mychef.R;

public class MyService extends Service {

    NotificationManager Notifi_Manager;
    Notification Notifi_Message;
    ServiceThread thread;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notifi_Manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();

        thread = new ServiceThread(handler);
        thread.start();

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    // 서비스가 종료될 때 할 작업
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(MyService.this, ServiceExample.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notifi_Message=new Notification.Builder(getApplicationContext())
                    .setContentTitle("Content Title")
                    .setContentText("Content Text")
                    .setSmallIcon(R.drawable.kakaoaccount_icon)
                    .setTicker("알람!!!")
                    .setContentIntent(pendingIntent)
                    .build();

            Notifi_Message.defaults = Notification.DEFAULT_SOUND; // 소리추가
            Notifi_Message.flags = Notification.FLAG_ONLY_ALERT_ONCE; // 알림 소리를 한번만 내도록
            Notifi_Message.flags = Notification.FLAG_AUTO_CANCEL; // 확인하면 자동으로 알림이 제거 되도록

            Notifi_Manager.notify(777, Notifi_Message);

            Toast.makeText(getApplicationContext(), "노티 확인", Toast.LENGTH_LONG).show();
        }
    }
}
