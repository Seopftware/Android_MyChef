package thread.seopftware.mychef.Chatting;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import thread.seopftware.mychef.R;

import static java.lang.Integer.parseInt;

public class Chat_Service extends Service {

    private static final String TAG = "Chat_Service";
    private static final String IP = "115.71.239.151"; // 서버 접속 IP
    private static final String PORT = "8888"; // 서버 접속 PORT

    Handler msghandler;
    SocketClient client;
    static Socket socket;


    Message hdmsg;
    static DataOutputStream output = null;
    DataInputStream input = null;
    ReceiveThread receive;
    SendThread send;
    String command; // 메세지 내용이 담겨져 있음


    NotificationManager Notifi_Manager;
    Notification Notifi_Message;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "여기서 소켓이 연결됨");
        client = new SocketClient(IP, PORT);
        client.start();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand 실행됨");
        Notifi_Manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (intent == null) {
            return Service.START_STICKY;
        } else {
            processCommand(intent);
        }

        return super.onStartCommand(intent, flags, startId);

    }

    private void processCommand(Intent intent) {
        Log.d(TAG, "processCommand 실행됨");
        Log.d(TAG, "intent.getStringExtra(command) : " + intent.getStringExtra("command"));


        if(intent.getStringExtra("command").equals("0")) {
            Log.d(TAG, "command : " + intent.getStringExtra("command"));
        }

        else if(intent.getStringExtra("command")!=null) {
            command = intent.getStringExtra("command"); // 액티비티 ㅡ> 서비스로 보낸 내용 (전송 버튼 클릭 시 받아오는 데이터)
            Log.d(TAG, "command : " + intent.getStringExtra("command"));
            send = new SendThread(socket);
            send.start();
        }

        msghandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Log.d("서비스 Handler hdmsg", String.valueOf(hdmsg));

                String Message = hdmsg.obj.toString();
                Log.d(TAG, "서비스 Message(분해전) : "+Message);



                Intent intent = new Intent(Chat_Service.this, Chat_Client.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(Chat_Service.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

                Toast.makeText(getApplicationContext(), "노티 옵니까아아", Toast.LENGTH_LONG).show();


                Intent showIntent = new Intent(getApplicationContext(), Chat_Client.class);
                showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                showIntent.putExtra("command", Message);
                startActivity(showIntent);



            }
        };



    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    class SocketClient extends Thread {

        boolean threadAlive;
        String ip;
        String port;

        public SocketClient(String ip, String port) {
            threadAlive = true;
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() { // 맨 처음 서버에 데이터를 보내는 곳
            try {

                socket = new Socket(ip, parseInt(port));
                output = new DataOutputStream(socket.getOutputStream());
                receive = new ReceiveThread(socket); // 소켓과 연결 되면 ReceiveThread는 바로 작동 시작
                receive.start();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


        class ReceiveThread extends Thread {
            private Socket socket = null;

            public ReceiveThread(Socket Socket) {
                this.socket = Socket;

                try {
                    input = new DataInputStream(socket.getInputStream());


                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

            public void run() {
                try {
                    while (input != null) {
                        String msg = input.readUTF(); // 받은 메세지

                        if (msg != null) {

                            hdmsg = msghandler.obtainMessage();
                            hdmsg.what = 1111;
                            hdmsg.obj = msg;
                            msghandler.sendMessage(hdmsg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    class SendThread extends Thread {
        private Socket socket = null;
        DataOutputStream output;

        public SendThread(Socket Socket) {
            this.socket = Socket;
            try {

                output = new DataOutputStream(socket.getOutputStream());

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        public void run() {  // 메세지 전송부

//            Log.d("service", command);
            try {
                if(output !=null) {
                    if(command !=null) {
                        output.writeUTF(command);
                    }
                } else {
                    Log.d("소켓 종료?? sendthread ", "output 값은? :"+ output);
//                    output.close();
//                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        }
    }
}

