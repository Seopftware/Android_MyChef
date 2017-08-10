//package thread.seopftware.mychef.Chatting;
//
//import android.os.Handler;
//import android.os.Message;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.text.SimpleDateFormat;
//
//import static android.R.id.input;
//import static java.lang.Integer.parseInt;
//
///**
// * Created by MSI on 2017-08-09.
// */
//
//class SocketClient extends Thread {
//
//    private static final String TAG = "Chat_Service";
//    private static final String IP = "115.71.239.151"; // 서버 접속 IP
//    private static final String PORT = "8888"; // 서버 접속 PORT
//
//    boolean threadAlive;
//    String ip;
//    String port;
//
//    public SocketClient(String ip, String port) {
//        threadAlive = true;
//        this.ip = ip;
//        this.port = port;
//    }
//
//    @Override
//    public void run() { // 맨 처음 서버에 데이터를 보내는 곳
//        try {
//
//            socket = new Socket(ip, parseInt(port));
//            output = new DataOutputStream(socket.getOutputStream());
//            receive = new ReceiveThread(socket); // 소켓과 연결 되면 ReceiveThread는 바로 작동 시작
//            receive.start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    class ReceiveThread extends Thread {
//        private Socket socket = null;
//
//        public ReceiveThread(Socket Socket) {
//            this.socket = Socket;
//
//            try {
//                input = new DataInputStream(socket.getInputStream());
//
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//        }
//
//        public void run() {
//            try {
//                while (input != null) {
//                    String msg = input.readUTF(); // 받은 메세지
//
//                    if (msg != null) {
//
//                        hdmsg = msghandler.obtainMessage();
//                        hdmsg.what = 1111;
//                        hdmsg.obj = msg;
//                        msghandler.sendMessage(hdmsg);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    class SendThread extends Thread {
//        private Socket socket = null;
//        DataOutputStream output;
//
//        public SendThread(Socket Socket) {
//            this.socket = Socket;
//            try {
//
//                output = new DataOutputStream(socket.getOutputStream());
//
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//        }
//
//        public void run() {  // 메세지 전송부
///*
//        try {
//            // JSON 형식으로 보내줘야 함
//            JSONObject object = new JSONObject();
//            object.put("room_status", "1"); // 메세지 전송
//            object.put("room_number", room_number);
//            object.put("email_sender", Login_Email);
//            object.put("content_message", content_message);
//            object.put("content_time", content_time);
//
//            if(output !=null) {
//                if(content_message !=null) {
//                    output.writeUTF(object.toString());
//                }
//            } else {
//                Log.d("소켓 종료?? sendthread ", "output 값은? :"+ output);
////                    output.close();
////                    socket.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NullPointerException npe) {
//            npe.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }*/
//        }
//    }
//}