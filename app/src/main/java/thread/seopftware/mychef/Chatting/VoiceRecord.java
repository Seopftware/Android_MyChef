package thread.seopftware.mychef.Chatting;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import thread.seopftware.mychef.R;

public class VoiceRecord extends AppCompatActivity {

    private static final String TAG = "VoiceRecord";

    // ACTIVITY UI
    ImageButton ibtn_Record; // 녹음하기
    ImageButton ibtn_Close; // 녹음창 닫기
    Button btn_Send; // 녹음 파일 보내기
    Chronometer chronometer, chronometer2;


    // 미디어 플레이어
    MediaPlayer player;
    MediaRecorder recorder;

    // 음성 파일 저장 경로
    private static String RECORDED_FILE;


    // oK HTTP Request Queue
    RequestQueue rq;

    // 메세지 전송을 위한 JSON
    String room_number, email_sender, content_time;
    String responseStr; // 음성 파일 경로

    int i, j=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_record);

        Intent intent = getIntent();
        room_number = intent.getStringExtra("room_number");
        email_sender = intent.getStringExtra("email_sender");
        content_time = intent.getStringExtra("content_time");

        File sdcard = Environment.getExternalStorageDirectory();
        String fileName = "voice_" + String.valueOf(System.currentTimeMillis()) + ".mp4";
        File file = new File(sdcard, fileName);
        RECORDED_FILE = file.getAbsolutePath();

        ibtn_Record = (ImageButton) findViewById(R.id.ibtn_Record);
        ibtn_Close = (ImageButton) findViewById(R.id.ibtn_Close);
        btn_Send = (Button) findViewById(R.id.btn_Send);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer2 = (Chronometer) findViewById(R.id.chronometer2);

        // #FFE400
        // 전송 버튼
        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Log.d(TAG, "**************************************************");
                    Log.d(TAG, "음성 파일 보내기!!!");
                    Log.d(TAG, "**************************************************");

                    // 메세지를 서비스로 보내는 곳
                    JSONObject object = new JSONObject();
                    object.put("room_status", "888");
                    object.put("room_number", room_number);
                    object.put("content_message", responseStr);
                    object.put("email_sender", email_sender);
                    object.put("content_time", content_time);
                    String Object_Data = object.toString();

                    Intent intent = new Intent(VoiceRecord.this, Chat_Service.class); // 액티비티 ㅡ> 서비스로 메세지 전달
                    intent.putExtra("command", Object_Data);
                    startService(intent);

                    addNumMessage();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        // 녹음 버튼
        ibtn_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                i=0+i;

                // 녹음 시작
                if (i==0) {

                    Toast.makeText(getApplicationContext(), "녹음 시작", Toast.LENGTH_SHORT).show();

                    // 녹화 중지 버튼으로 이미지 변경
                    Drawable record_stop = getResources().getDrawable(R.drawable.record_stop);
                    ibtn_Record.setImageDrawable(record_stop);


//                   if( recorder != null ) {
//                        recorder.stop();
//                        recorder.release();
//                        recorder = null;
//                    }

                    // MediaRecorder 객체 생성 및 녹음에 필요한 정보 설정
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 오디오 입력 설정
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 미디어 포맷 ( MPEG_4 란 : 영상, 음성을 디지털 데이터로 전송, 저장하기 위한 규격 )
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); // 인코더 설정
                    recorder.setOutputFile(RECORDED_FILE); // 결과물 파일

                    try {
                        Toast.makeText(getApplicationContext(), "녹음 시작", Toast.LENGTH_SHORT).show();

                        // 아래 두개의 메소드를 이용해 녹음이 시작됨.
                        recorder.prepare();
                        recorder.start();

                        // 크로노미터 시간 시작
                        chronometer.setBase(SystemClock.elapsedRealtime()); // 내가 원하는 시간부터 시작 (현재 버튼을 눌린 시점으로 셋) default 00:00
                        chronometer.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    i++;
                }

                // 녹음 중지
                else if(i==1) {

                    // 음성 재생 버튼으로 이미지 변경
                    Drawable record_stop = getResources().getDrawable(R.drawable.play_start);
                    ibtn_Record.setImageDrawable(record_stop);

                    Toast.makeText(getApplicationContext(), "녹음 중지", Toast.LENGTH_SHORT).show();



                    if(recorder==null) {
                        return;
                    }

                    recorder.stop();
                    recorder.reset();
                    recorder.release();
                    recorder = null;

                    // 크로노미터 스탑
                    chronometer.stop();

                    ContentValues values = new ContentValues(10);
                    values.put(MediaStore.MediaColumns.TITLE, "Recorded");
                    values.put(MediaStore.Audio.Media.ALBUM, "Audio Album");
                    values.put(MediaStore.Audio.Media.ARTIST, "MyChef");
                    values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Audio");
                    values.put(MediaStore.Audio.Media.IS_RINGTONE, 1);
                    values.put(MediaStore.Audio.Media.IS_MUSIC, 1);
                    values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis()/1000);
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4");
                    values.put(MediaStore.Audio.Media.DATA, RECORDED_FILE);


                    // URI (Uniform resource identifier)
                    // 프로토콜 + 도메인으로 구성된 구조

//                Uri audioUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values); // 녹음된 파일을 내용 제공자를 이용해 녹음 목록으로 저장

                    Uri uri= Uri.parse("file:///"+RECORDED_FILE);
                    Log.d(TAG, "uri : " + uri);

                    File file = new File(uri.getPath());
                    Log.d(TAG, "file : " + file);

                    // 서버에 음성파일 업로드 + 보내기 버튼 활성화 시키기
                    voiceUpload(file);

                    // 보내기 버튼 활성화
                    btn_Send.setEnabled(true);
                    btn_Send.setBackgroundColor(Color.rgb(255, 228, 0));

                    i++;
                }




                // 3. 녹음된 거 재생 & 중지 반복
                else if(i==2) {
                    j=0+j;

                    // 크로노미터 시간 설정
//                    chronometer.setBase(Long.parseLong(chronometer.getText().toString())); // 내가 원하는 시간부터 시작 (현재 버튼을 눌린 시점으로 셋) default 00:00


                    // 녹음 재생
                    if(j==0) {
                        Toast.makeText(getApplicationContext(), "녹음 파일 재생", Toast.LENGTH_SHORT).show();

                        Drawable record_stop = getResources().getDrawable(R.drawable.play_stop);
                        ibtn_Record.setImageDrawable(record_stop);



                        if(player != null) {

                            player.stop();
                            player.release();
                            player = null;

                        }

                        try {
                            player = new MediaPlayer();

                            player.setDataSource(RECORDED_FILE);
                            player.prepare();
                            player.start();

//                            chronometer.setVisibility(View.INVISIBLE);
//                            chronometer2.setVisibility(View.VISIBLE);
//
//                            // 크로노미터 시간 시작
//                            chronometer2.setBase(SystemClock.elapsedRealtime()); // 내가 원하는 시간부터 시작 (현재 버튼을 눌린 시점으로 셋) default 00:00
//                            chronometer2.start();

                            String time = chronometer.getText().toString();
                            Log.d(TAG, "chronometer time :" + time); // 00:00

                            String chronotime1 = time.substring(3,5); // 00:(00) => 뒷 부분 00 가져옴 (x 부터 . y전까지)
                            String chronotime = chronotime1.replaceAll("\\p{Z}", "");
                            Log.d(TAG, "크로노타임11 : "+ chronotime1);
                            Log.d(TAG, "크로노타임 : "+ chronotime);


                            int a = player.getDuration();
                            long b = player.getDuration();
                            int c = Math.round(b/1000);

                            Log.d(TAG, "재생 시간 어떻게 표시되나요!??"+ a/1000);
                            Log.d(TAG, "반올림한 시간"+ Math.round(b/1000));


                            String playtime = null;

                            if(c < 10) {
                                playtime = "0" + String.valueOf(c);
                                Log.d(TAG, "재생 시간 어떻게 표시되나요!??"+ playtime);

                            } else {
                                playtime = String.valueOf(c);
                            }


                            Log.d(TAG, "여기서의 playtime은? "+ playtime);

                            if(chronotime.equals(playtime)) {
//                                Toast.makeText(getApplicationContext(), "녹음 파일 재생 중지", Toast.LENGTH_SHORT).show();
//
//                                chronometer2.stop();
//                                chronometer2.setBase(SystemClock.elapsedRealtime()); // 내가 원하는 시간부터 시작 (현재 버튼을 눌린 시점으로 셋) default 00:00
//                                Drawable play_start = getResources().getDrawable(R.drawable.play_start);
//                                ibtn_Record.setImageDrawable(play_start);
//
//
//                                player.stop();
//                                player.release();
//                                player = null;
//
//                                // 보내기 버튼 활성화
//                                btn_Send.setEnabled(true);
//                                btn_Send.setBackgroundColor(Color.rgb(255, 228, 0));

                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        j++;

                        Log.d(TAG, "j 111111"+j);

                        // 재생 중에는 보내기 버튼 비활성화 시키기
                        btn_Send.setEnabled(false);
                        btn_Send.setBackgroundColor(Color.rgb(243, 243, 243));

//                        Drawable play_start = getResources().getDrawable(R.drawable.play_start);
//                        ibtn_Record.setImageDrawable(play_start);

                    }

                    // 녹음 중지
                    else if(j==1) {
                        Toast.makeText(getApplicationContext(), "녹음 파일 재생 중지", Toast.LENGTH_SHORT).show();

                        chronometer2.stop();
                        chronometer2.setBase(SystemClock.elapsedRealtime()); // 내가 원하는 시간부터 시작 (현재 버튼을 눌린 시점으로 셋) default 00:00
                        Drawable play_start = getResources().getDrawable(R.drawable.play_start);
                        ibtn_Record.setImageDrawable(play_start);


                        player.stop();
                        player.release();
                        player = null;

                        // 보내기 버튼 활성화
                        btn_Send.setEnabled(true);
                        btn_Send.setBackgroundColor(Color.rgb(255, 228, 0));

//                        Drawable play_start = getResources().getDrawable(R.drawable.play_start);
//                        ibtn_Record.setImageDrawable(play_start);

                        Log.d(TAG, "j 00000"+j);
                    }

                }





            }
        });

    }

    protected void voiceUpload(File file) {

        OkHttpClient client = new OkHttpClient();

        String url = "http://115.71.239.151/voiceUpload.php";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("voice"), file)) // 1.키, 2.파일이름, 3.RequestBody
                .addFormDataPart("room_number", room_number)
                .addFormDataPart("email_sender", email_sender)
                .addFormDataPart("content_time", content_time)
                .build(); // 몸통 완성

        final Request request = new Request.Builder()
                .url(url) // 몸통을 보낼 url 주소 (서버 url)
                .post(requestBody) // 보내고자 하는 contents 들
                .build(); // 보내기

        // 콜백 함수
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "Okhttp voiceUpload response : " + response);
                Log.d(TAG, "Okhttp voiceUpload call : " + call);

                responseStr = response.body().string();
                Log.d(TAG, "Okhttp voiceUpload responseStr : " + responseStr);

            }
        });
    }

    // 방 번호를 보내고 해당 방에 있는 사람들의 메세지 수 +1 씩 해준다.
    // 메세지를 보내는 순간 NumMessage 숫자를 +1 씩 해준다.
    // 그리고 채팅방을 나가는 순간 ( back 키 클릭 시 ) 0으로 초기화 시켜준다.
    private void addNumMessage() {
        Log.d(TAG, "addNumMessage() 함수가 실행 됩니다.");

        OkHttpClient client = new OkHttpClient();

        String url = "http://115.71.239.151/addNumMessage.php";

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("room_number", room_number)
                .addFormDataPart("email_sender", email_sender)
                .build(); // 몸통 완성

        final Request request = new Request.Builder()
                .url(url) // 몸통을 보낼 url 주소 (서버 url)
                .post(requestBody) // 보내고자 하는 contents 들
                .build(); // 보내기

        // 콜백 함수
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "Okhttp voiceUpload response : " + response);
                Log.d(TAG, "Okhttp voiceUpload call : " + call);

                String addNumMessageresponse = response.body().string();
                Log.d(TAG, "Okhttp addNumMessage response : " + addNumMessageresponse);

                finish();
            }
        });
    }

    protected void onPause() {
        if(recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }

        if( chronometer !=null) {
            chronometer.stop();
        }

        super.onPause();
    }

    protected void onResume() {
        super.onResume();

        recorder = new MediaRecorder(); // 액티비티 재시작 시 MediaRecorder 객체 생성
    }
}



/*

volleyer 예제
Header를 이해 못헀음..
                volleyer(rq)
                        .post(url)
                        .addHeader("header1", "value1")
                        .addFilePart("voice", file)
                        .withListener(new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "response : " +response);
                            }
                        })
                        .withErrorListener(new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "error : " +error);
                            }
                        })
                        .execute();





                        //    private void sendVoiceDB() {
//        Log.d(TAG, "sendVoiceDB() 함수가 실행 됩니다.");
//
//        String url = "http://115.71.239.151/sendVoiceDB.php";
//        VolleyM stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "sendVoiceDB() respopnse  : " + response);  // 결과 : url 주소 (음성 파일이 저장되어 있는 url 경로)
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // Anything you want
//            }
//        }) {
//            @Override
//            protected Map<String, File> getParams() throws AuthFailureError {
//
//                Log.d(TAG, "resetNumMessage() 함수의 Login_Email : " + Login_Email);
//                Map<String, File> map = new Hashtable<>();
//                map.put("Login_Email", Login_Email);
//                return map;
//
//
//            }
//
////            @Override
////            protected Map<String, File> get
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(stringRequest);
//    }

 */