package thread.seopftware.mychef.FCMessanging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import thread.seopftware.mychef.HomeChef.Home_chef;
import thread.seopftware.mychef.R;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG ="FCMessagingService";

    // START receive message


    // 단말기에서 FCM 메세지를 받으면 onMessageReceived() 함수가 실행됨.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        String title = remoteMessage.getData().get("title");

        Log.d(TAG, "**************************************************");
        Log.d(TAG, "FCMessagingService에서 push_notification 발생");
        Log.d(TAG, "**************************************************");

        sendNotification(remoteMessage.getData().get("message"));


        // 알람 설정 여부에 따라서 알람 받지 않기 (Shared Preferences 사용)


    }

    // 노티피케이션을 보내는 함수
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, Home_chef.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 111, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.chef);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.chef2)
//                .setColor(getApplicationContext().getResources().getColor())
                .setColor(Color.parseColor("#E27C3E"))
                .setContentTitle("고객 출장 요청")
                .setContentText(messageBody)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(111, notificationBuilder.build());

    }

}
