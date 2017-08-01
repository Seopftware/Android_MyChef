package thread.seopftware.mychef.Chatting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import thread.seopftware.mychef.R;

import static java.lang.Integer.parseInt;
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

public class Chat_Client extends AppCompatActivity {

    public static String UserID;
    private static String TAG="Chat_Client";

    private final static String IP = "115.71.239.151"; // 서버 접속 IP
    private final static String PORT = "8888"; // 서버 접속 PORT

    private BufferedReader networkReader = null;
    private PrintWriter networkWriter = null;

    //view type
    private static final int ENTRANCE = 0 ;
    private static final int MESSAGE = 1 ;

    // 서버
    Handler msghandler;
    SocketClient client;
    Socket socket;
    ReceiveThread receive;
    SendThread send;

    // 뷰
    EditText et_ChatInput;
    Button btn_Send, btn_Connect;

    LinkedList<SocketClient> threadList;
    ListView listView;
    ArrayList<ListViewItem_Chat> listViewItemList;
    ListViewAdapter_Chat adapter;

    Message hdmsg;
    String Login_Email, Login_Name, Current_Time, Login_Profile;
    String Current_Subject;
    SimpleDateFormat simpleDateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_client);

        // 액션바 작업
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("채팅방");

        adapter=new ListViewAdapter_Chat();
        listView= (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // 세션 유지를 위한 이메일 값 불러들이기
        SharedPreferences pref1 = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK=pref2.getString(FBAPI, "0");

        if(!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB chefemail: "+Login_Email);
        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA chefemail: "+Login_Email);
        } else { // 일반
            SharedPreferences pref = getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal chefemail: "+Login_Email);
        }

        Log.d(TAG, "접속된 Email : "+Login_Email);

        getChattingInfo(); // 채팅에 필요한 정보 가져오기

        // view 객체 선언
        btn_Send= (Button) findViewById(R.id.btn_Send);
        btn_Connect= (Button) findViewById(R.id.btn_Connect);
        et_ChatInput= (EditText) findViewById(R.id.et_ChatInput);
        threadList = new LinkedList<Chat_Client.SocketClient>();

        // 받은 메세지를 처리하는 핸들러
        msghandler = new Handler(){
            @Override
            public void handleMessage(Message hdmsg) {
                super.handleMessage(hdmsg);

                Log.d("Handler hdmsg", String.valueOf(hdmsg));

                if( hdmsg.what == 1111 ) {

                    String Message = hdmsg.obj.toString();
                    String[] split = Message.split("_@#@_");
                    Log.d(TAG, "Message(분해전) : "+Message);

                    int Message_ViewType= Integer.parseInt(split[0]); // 최초 접속인지 아닌지 분별 (VIEW TYPE)

                    Log.d(TAG, "핸들러 Message_ViewType" +Message_ViewType);

                    if(Message_ViewType == ENTRANCE) { // 채팅방 최초 접속시

                        String Message_Name = split[1]; // ""님이 접속하셨습니다.

                        Log.d(TAG, "Message_Name(분해후) : "+Message_Name);


                        // 서버로 부터 받은 메세지값을 ListView에 뿌려주는 역할
                        adapter.addItem(Message_Name); // 이름 값만 필요함.
                        adapter.notifyDataSetChanged();

                    } else if(Message_ViewType == MESSAGE) {

                        String Message_Status = split[1]; // 1. 나 or 상대방
                        String Message_Email = split[2]; // 2. 이메일
                        Log.d(TAG, "Message_Email(분해후) : "+Message_Email);

                        if(Message_Email.equals(Login_Email)) { // 보낸 사람의 이메일과 받는 사람의 이메일이 똑같을 경우 view로 뿌려주지 않는다.
                            Log.d(TAG, "내가 보낸 view 값이라 패스!");
                        } else {
                            String Message_Name = split[3]; // 3. 이름
                            String Message_Time = split[4]; // 4. 시간
                            String Message_Subject = split[5]; // 5. 메세지
                            String Message_ProfilePath = split[6]; // 6. 프로필 사진 주소

                            Log.d(TAG, "Message_Status(분해후) : "+Message_Status);
                            Log.d(TAG, "Message_Name(분해후) : "+Message_Name);
                            Log.d(TAG, "Message_Time(분해후) : "+Message_Time);
                            Log.d(TAG, "Message_Subject(분해후) : "+Message_Subject);
                            Log.d(TAG, "Message_ProfilePath(분해후) : "+Message_ProfilePath);

                            adapter.addItem(Message_Email, Message_Name, Message_Time, Message_Subject, Message_ProfilePath);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            }
        };

        // 서버와 연결하는 버튼
        btn_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new SocketClient(IP, PORT);
                client.start();
            }
        });

        // editText를 통해서 입력받은 데이터를 서버에 전송
        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Current_Subject=et_ChatInput.getText().toString();

                if(Current_Subject.length() == 0) {
                    Toast.makeText(getApplicationContext(), "메세지를 한 글자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                    et_ChatInput.requestFocus();
                    return;
                }

                if( Current_Subject != null ) { // 만약 data가 비어있지 않다면 서버로 data 전송

                    simpleDateFormat = new SimpleDateFormat("hh:dd a");
                    Current_Time = simpleDateFormat.format(new Date());

                    Log.d(TAG, "send버튼 클릭시 보내는 값 : "+"MESSAGE"+ MESSAGE +"Me: true"+ Login_Email+"_@#@_"+Login_Name+"_@#@_"+Current_Time+"_@#@_"+Current_Subject+"_@#@_"+Login_Profile);
                    adapter.addItem(Login_Email, Login_Name, Current_Time, Current_Subject, "http://115.71.239.151/"+Login_Profile);
                    adapter.notifyDataSetChanged();
                    et_ChatInput.setText("");

                    send = new SendThread(socket);
                    threadList.add(client);
                    send.start();

                }
            }
        });
    }

    //액션바 백키 버튼 구현
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class SocketClient extends Thread {
        boolean threadAlive;
        String ip;
        String port;

        private DataOutputStream output = null; // 서버로 데이터 전송?


        public SocketClient(String ip, String port) {
            threadAlive = true;
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(ip, parseInt(port));

                output = new DataOutputStream(socket.getOutputStream());
                receive = new ReceiveThread(socket); // 소켓과 연결 되면 ReceiveThread는 바로 작동 시작
                receive.start();

                output.writeUTF(ENTRANCE+"_@#@_"+Login_Name);

            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    class ReceiveThread extends Thread {
        private Socket socket = null;
        DataInputStream input;

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
                while(input !=null) {
                    String msg = input.readUTF();

                    if(msg !=null) {

                        hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 1111;
                        hdmsg.obj = msg;
                        msghandler.sendMessage(hdmsg);
                        Log.d(TAG, "(Receive Thread) 받은 메세지 : "+ hdmsg.obj.toString());
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

            try {
                if(output !=null) {
                    if(Current_Subject !=null) {
                        output.writeUTF(MESSAGE+"_@#@_"+true+"_@#@_"+Login_Email+ "_@#@_" +Login_Name+ "_@#@_" + Current_Time+ "_@#@_" + Current_Subject + "_@#@_" + "http://115.71.239.151/"+Login_Profile);
//                        adapter.addItem(true, Login_Email, Login_Name, Current_Time, sendmsg, "http://115.71.239.151/"+Login_Profile);
                    }
                } else {
//                    Log.d("소켓 종료 확인", "output 값은? :"+ output);
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

    // 이메일 값 보내고 이름 받아오기
    // 프로필 정보 받아오는 함수
    private void getChattingInfo() {

        String url = "http://115.71.239.151/Chatting_Information.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("getChattingInfo parsing", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(0);

                    // 데이터 불러들이기
                    Login_Name=jo.getString("name"); // 0
                    Login_Profile=jo.getString("profile"); // 1

                    Log.d(TAG, "Login_Name : "+Login_Name+"Login_Profile : "+Login_Profile);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.d(TAG, "Login_Email : "+Login_Email);
                Map<String,String> map = new Hashtable<>();
                map.put("Login_Email", Login_Email);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

//    private boolean sendChatMessage(int viewtype, Boolean message_status, String email, String name, String time, String message, String profile) {
//        chatArrayAdapter.add(new ChatMessage(viewtype, message_status, email, name, time, message, profile));
//        et_ChatInput.setText("");
//        return true;
//    }
}

/*
 메인

try {
            Socket c_socket = new Socket("115.71.239.151", 8888);

            Log.d(TAG, "서버와 연결되었습니다.");

            ReceiveThread rec_thread = new ReceiveThread();
            rec_thread.setSocket(c_socket);

            SendThread send_thread = new SendThread();
            send_thread.setSocket(c_socket);

            rec_thread.start();
            send_thread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/




/*
    // 서버로 부터 데이터를 받는 쓰레드
    public class ReceiveThread extends Thread {
        private Socket m_Socket;

        @Override
        public void run() {
            super.run();

            try {

                networkReader = new BufferedReader(new InputStreamReader(m_Socket.getInputStream()));

                String receiveString;
                String[] split;

                while (true) {
                    receiveString = networkReader.readLine();

                    split = receiveString.split(">");

                    if(split.length >= 2 && split[0].equals(Chat_Client.UserID)) {
                        continue;
                    }

                    Log.d(TAG, "Receive Thread (receiveString) : "+receiveString);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setSocket(Socket _socket) {
            m_Socket = _socket;
        }
    }

    // 서버로 데이터를 전송하는 쓰레드
    public class SendThread extends Thread {
        private Socket m_Socket;

        @Override
        public void run() {
            super.run();

            try{

                networkReader = new BufferedReader(new InputStreamReader(System.in));

                networkWriter = new PrintWriter(m_Socket.getOutputStream());

                String sendString;

                Log.d(TAG, "사용할 아이디 입력");
                Chat_Client.UserID = networkReader.readLine();

                networkWriter.println("IDhighkrs12345" + Chat_Client.UserID);
                networkWriter.flush();

                while (true) {
                    sendString = networkReader.readLine();

                    if(sendString.equals("exit")) {
                        break;
                    }

                    networkWriter.println(sendString);
                    networkWriter.flush();
                }

                networkWriter.close();
                networkReader.close();
                m_Socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setSocket(Socket _socket) {
            m_Socket = _socket;
        }
    }
 */