package thread.seopftware.mychef;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class Chat_Client extends AppCompatActivity {

    public static String UserID;
    private static String TAG="Chat_Client";

    private final static String IP = "115.71.239.151"; // 서버 접속 IP
    private final static String PORT = "8888"; // 서버 접속 PORT

    private BufferedReader networkReader = null;
    private PrintWriter networkWriter = null;

    Handler msghandler;
    SocketClient client;
    Socket socket;
    ReceiveThread receive;
    SendThread send;

    TextView tv_Me, tv_You;
    EditText et_ChatInput;
    Button btn_Send, btn_Connect;

    String idByANDROID_ID;

    LinkedList<SocketClient> threadList;

    ListView listView;
    Message hdmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_client);

        tv_Me= (TextView) findViewById(R.id.tv_Me);
        tv_You= (TextView) findViewById(R.id.tv_You);
        btn_Send= (Button) findViewById(R.id.btn_Send);
        btn_Connect= (Button) findViewById(R.id.btn_Connect);
        et_ChatInput= (EditText) findViewById(R.id.et_ChatInput);
        listView= (ListView) findViewById(R.id.listView);
        threadList = new LinkedList<Chat_Client.SocketClient>();

        idByANDROID_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "안드로이드 기기 고유 번호"+idByANDROID_ID); // 디바이스가 최초 Boot 될 때 생성되는 64-bit 값

        msghandler = new Handler(){
            @Override
            public void handleMessage(Message hdmsg) {
                super.handleMessage(hdmsg);

                if( hdmsg.what == 1111 ) {
                    tv_Me.append(hdmsg.obj.toString() + "\n");
                }
            }
        };

        // 서버와 연결
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
                String data=et_ChatInput.getText().toString();

                Log.d(TAG, "et 입력값 " + data);
                if( data != null ) { // 만약 data가 비어있지 않다면 서버로 data 전송

                    Log.d(TAG, "서버로 보내는 데이터 " + data);
                    send = new SendThread(socket);
                    threadList.add(client);
                    send.start();

                    et_ChatInput.setText(""); // 보낸 후 editText 초기화
                }
            }
        });
    }

    class SocketClient extends Thread {
        boolean threadAlive;
        String ip;
        String port;
        String mac;

        //        PrintWriter printWriter = null; // 서버에 데이터를 전송
        BufferedReader bufferedReader = null; // 서버로 부터 온 데이터를 읽는다.
        OutputStream outputStream = null;

        private DataOutputStream output = null;


        public SocketClient(String ip, String port) {
            threadAlive = true;
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(ip, Integer.parseInt(port));

                output = new DataOutputStream(socket.getOutputStream());
                receive = new ReceiveThread(socket); // 소켓과 연결 되면 ReceiveThread는 바로 작동 시작
                receive.start();

                output.writeUTF(idByANDROID_ID); // 안드로이드 고유 번호 전송
            }
             catch(IOException e){
                    e.printStackTrace();
             }
        }
    }

    class ReceiveThread extends Thread {
        private Socket socket = null;
        DataInputStream input;

        public ReceiveThread(Socket socket) {
            this.socket = socket;

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
                        Log.d(TAG, "test");

                        hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 1111;
                        hdmsg.obj = msg;
                        msghandler.sendMessage(hdmsg);

                        Log.d(TAG, "받은 메세지 : "+ hdmsg.obj.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SendThread extends Thread {
        private Socket socket;

        String sendmsg = et_ChatInput.getText().toString();
        DataOutputStream output;

        public SendThread(Socket socket) {
            this.socket = socket;
            try {

                output = new DataOutputStream(socket.getOutputStream());

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        public void run() {  // 메세지 전송부

            try {
                if(output !=null) {
                    if(sendmsg !=null) {
                        output.writeUTF(idByANDROID_ID + " : " + sendmsg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

        }
    }

    private static class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
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