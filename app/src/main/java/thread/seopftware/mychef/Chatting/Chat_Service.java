package thread.seopftware.mychef.Chatting;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import thread.seopftware.mychef.R;

import static java.lang.Integer.parseInt;
import static thread.seopftware.mychef.HomeChef.Fragment5_Setting.NOTIFY_MESSAGE;
import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLEMAIL;
import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLOGIN;
import static thread.seopftware.mychef.Login.Login_login.FACEBOOKLOGIN;
import static thread.seopftware.mychef.Login.Login_login.FBAPI;
import static thread.seopftware.mychef.Login.Login_login.FBEMAIL;
import static thread.seopftware.mychef.Login.Login_login.FB_LOGINCHECK;
import static thread.seopftware.mychef.Login.Login_login.KAAPI;
import static thread.seopftware.mychef.Login.Login_login.KAEMAIL;
import static thread.seopftware.mychef.Login.Login_login.KAKAOLOGIN;
import static thread.seopftware.mychef.Login.Login_login.KAKAO_LOGINCHECK;

public class Chat_Service extends Service {

    private static final String TAG = "Chat_Service";
    private static final String IP = "115.71.239.151"; // 서버 접속 IP
    private static final String PORT = "8888"; // 서버 접속 PORT

    Handler msghandler, handler;
    SocketClient client;
    Socket socket;


    Message hdmsg;
    DataOutputStream output = null;
    DataInputStream input = null;
    ReceiveThread receive;
    SendThread send;
    String command; // 메세지 내용이 담겨져 있음



    String Login_Email;
    String name, profile; // 노티피케이션을 위한 정보들 (메세지를 보낸 사람의 이름 및 프로필 사진)
    Bitmap noti_bitmap;


    NotificationManager Notifi_Manager;
    Notification Notifi_Message;

    @Override
    public IBinder onBind(Intent intent) { // onBind의 역할은 무엇인가?
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "**************************************************");
        Log.d(TAG, "on Create() : 서버와 소켓 연결");
        Log.d(TAG, "**************************************************");

        SharedPreferences pref1 = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK = pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK = pref2.getString(FBAPI, "0");

        if (!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            Login_Email = pref.getString(FBEMAIL, "");
        } else if (!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            Login_Email = pref.getString(KAEMAIL, "");
        } else { // 일반
            SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            Login_Email = pref.getString(CHEFNORMALLEMAIL, "");
        }

        Log.d(TAG, "접속된 Email : " + Login_Email);

        client = new SocketClient(IP, PORT);
        client.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand 실행됨");

        // Notification Manager 객체화
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
            Log.d(TAG, "로그인 시 서비스 값 : " + intent.getStringExtra("command"));

        }

        else if(intent.getStringExtra("command")!=null) {
            command = intent.getStringExtra("command"); // 액티비티 ㅡ> 서비스로 보낸 내용 (전송 버튼 클릭 시 받아오는 데이터)
            Log.d(TAG, "전송 버튼 클릭 시  : " + intent.getStringExtra("command"));


            send = new SendThread(socket);
            send.start();


        }

        else if(intent.getStringExtra("list")!=null) {
            command = intent.getStringExtra("list");
            Log.d(TAG, "저장된 채팅방 입장 시  : " + intent.getStringExtra("command"));

        }


        // 서비스 ㅡ> 액티비티로 메세지를 보내는 곳
        msghandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Log.d("서비스 Handler hdmsg", String.valueOf(hdmsg));

                if( hdmsg.what == 1111 ) {
                    String Message = hdmsg.obj.toString();
                    Log.d(TAG, "서비스 Message(분해전) : "+Message);

                    // 메세지를 분해
                    // 1. 내가 보낸 메세지 인가 내가 보낸 메세지가 아닌가 구분 (내가 보내면 알람 안오게)
                    // 2. 메세지를 보낸 사람의 이름
                    // 3. 메세지의 내용
                    // intent 보내고 방 번호 같으면 add로 보여주고 아니면 말구!

                    try {
                        JSONObject jo = new JSONObject(Message);

                        String email = jo.getString("email_sender");
                        String content = jo.getString("content_message");
                        String room_number = jo.getString("room_number");
                        String room_status = jo.getString("room_status");


                        if(email.equals(Login_Email)) {

                            Log.d(TAG, "내가 보낸 메세지는 노티피케이션 작동 안함");

                        } else {

                            if(room_status.equals("999")) { // 이미지를 보내거나 받았을 경우 (이미지 경로가 아닌 이미지
                                content="image";
                                getNotifyInfo(email, content, room_number);
                            }

                            getNotifyInfo(email, content, room_number); // 메세지를 보낸 사람의 프로필 사진과 메세지 내용을 노티피케이션을 통해 띄우기

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "**************************************************");
                    Log.d(TAG, "4. Message Handler 서버에서 받은 메세지를  액티비티로 보넀습니다.");
                    Log.d(TAG, "브로드 캐스트 액션 ( com.dwfox.myapplication.SEND_BROAD_CAST )");
                    Log.d(TAG, "**************************************************");

                    Intent sendIntent = new Intent("com.dwfox.myapplication.SEND_BROAD_CAST");
                    sendIntent.putExtra("MessageFromService", Message);
                    sendBroadcast(sendIntent);
                }
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
                Log.d(TAG, "소켓 연결!! SocketClient 쓰레드");

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

                            Log.d(TAG, "***************************************************************");
                            Log.d(TAG, "3. ReceiveThread : 서버에서 날아온 메세지를 서비스에서 받았습니다.");
                            Log.d(TAG, "***************************************************************");

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

                        Log.d(TAG, "**************************************************");
                        Log.d(TAG, "2. SendThread에서 서버로 메세지를 보넀습니다.");
                        Log.d(TAG, "**************************************************");

                        Log.d("SEND THREAD", "SendThread 값 : " + command);
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

    private void getNotifyInfo(final String email, final String content, final String room_number) {

        String url = "http://115.71.239.151/getNotifyInfo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "getChattingInfo parsing : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    // 데이터 불러들이기
                    name = jo.getString("name"); // 0
                    profile = jo.getString("profile"); // 1

                    Log.d(TAG, "name : " + name + "profile : " + profile);

                    SharedPreferences pref = getSharedPreferences(NOTIFY_MESSAGE, Context.MODE_PRIVATE);
                    Boolean notify_status = pref.getBoolean("status", true);
                    Log.d(TAG, "notify_status : " + notify_status);

                    if(notify_status == true) { // 노티피케이션 설정 되어 있다면 노티파이 알람이 오게끔
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "name : " + name + "profile : " + profile);

                                try {
                                    Bitmap noti_bitmap = Glide.with(getApplicationContext()).load("http://115.71.239.151/" + profile).asBitmap().into(100, 100).get();

                                    Log.d(TAG, "비트맵111 : " + noti_bitmap);

                                    Log.d(TAG, "**************************************************");
                                    Log.d(TAG, "노티피케이션이 작동하는 곳");
                                    Log.d(TAG, "**************************************************");

                                    // 알람을 클릭했을 때, 특정 액티비티를 활성화시킬 인텐트 객체 준비
                                    Intent intent = new Intent(Chat_Service.this, Chat_Chatting.class);
                                    intent.putExtra("room_number", room_number);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(Chat_Service.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                    Notifi_Message = new Notification.Builder(getApplicationContext())
                                            .setContentTitle(name) // 알림의 상단바 (제목) 설정
                                            .setContentText(content) // 알림의 하단바 (내용) 설정
                                            .setLargeIcon(noti_bitmap)
                                            .setSmallIcon(R.drawable.chatmessage2)
                                            .setTicker("MyChef에서 알림이 왔습니다.") // 알림이 뜰 때 잠깐 표시되는 메세지
                                            .setWhen(System.currentTimeMillis()) // 알림이 표시되는 시간 설정
                                            .setContentIntent(pendingIntent) // 알람 클릭 시 반응
                                            .setAutoCancel(true) // 클릭하면 자동으로 노티피케이션 알람이 없어짐
                                            .build();

/*                                     .setLargeIcon(BitmapFactory.decodeResource(getResources(),android.R.drawable.star_on))*/
/*                                     .setPriority(Notification.PRIORITY_MAX) // 상단에 레이아웃의 형태로 사용자에게 표시됨*/


                                    Notifi_Message.defaults = Notification.DEFAULT_VIBRATE; // 소리 or 진동 추가
                                    Notifi_Message.flags = Notification.FLAG_ONLY_ALERT_ONCE; // 알림 소리를 한번만 내도록
                                    Notifi_Message.flags = Notification.FLAG_AUTO_CANCEL; // 확인하면 자동으로 알림이 제거 되도록

                                    // 알람 띄우기
                                    Notifi_Manager.notify(777, Notifi_Message);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else { // 만약 설정이 안되어 있다면 노티 안감
                        Log.d(TAG, "노티피케이션 설정을 OFF 중");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d(TAG, "email_sender : "+email);
                Map<String, String> map = new Hashtable<>();
                map.put("email_sender", email);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}



/*    //서버로 부터 받은 이미지를 bitmap화 ( URL -> BITMAP)
    public static Bitmap getBitmapFromURL(String src) {

        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Log.d(TAG, "리턴된 bitmap : " + bitmap);

            return bitmap;


        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }*/

