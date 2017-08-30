package thread.seopftware.mychef.Chatting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import thread.seopftware.mychef.R;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
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


public class ListViewAdapter_Chat extends BaseAdapter {

    private static final String TAG = "ListViewAdapter_Chat";
    private static final int ENTRANCE = 0;
    private static final int MESSAGE = 1;
    private static final int DATE = 2;
    private static final int IMAGE = 3;
    private static final int AUDIO = 4;
    private static final int ITEM_VIEW_TYPE_MAX = 5;

    private TextView tv_MeEmail, tv_YouEmail;
    private TextView tv_MeMessage, tv_YouMessage; // 메세지
    private TextView tv_MeTime, tv_YouTime;
    private TextView tv_YouName;
    private TextView tv_Status;
    private ImageView iv_YouProfile, iv_MeImage, iv_YouImage;
    private LinearLayout LinearMe, LinearYou, LinearEntrance;


    // Voice
    private ImageButton ibtn_MePlay, ibtn_YouPlay; // 플레이 버튼
    private TextView tv_MePlayTime, tv_YouPlayTime;
    MediaPlayer mediaPlayer;

    ArrayList<ListViewItem_Chat> listViewItemList = new ArrayList<ListViewItem_Chat>(); // Adapter에 추가된 데이터를 저장하기 위한 ArrayList

    Context context;
    int layout;
    LayoutInflater mInflater;
    String Login_Email;

/*    public ListViewAdapter_Chat(Context context, int layout, ArrayList<ListViewItem_Chat> listViewItemList) {

        this.context = context;
        this.layout = layout;
        this.listViewItemList = listViewItemList;
    }*/

    public ListViewAdapter_Chat() {

    }

    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_VIEW_TYPE_MAX;
    }

    @Override
    public int getItemViewType(int position) {
        return listViewItemList.get(position).getType();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        int viewType = 0;

        // 세션 유지를 위한 이메일 값 불러들이기
        SharedPreferences pref1 = context.getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK = pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = context.getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK = pref2.getString(FBAPI, "0");

        if (!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = context.getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            Login_Email = pref.getString(FBEMAIL, "");
        } else if (!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = context.getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            Login_Email = pref.getString(KAEMAIL, "");
        } else { // 일반
            SharedPreferences pref = context.getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            Login_Email = pref.getString(CHEFNORMALLEMAIL, "");
        }


        if (convertView == null) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewType = getItemViewType(position);

            switch (viewType) {
                case ENTRANCE:
                    viewType = R.layout.listview_chatting_entrance;
                    break;

                case MESSAGE:
                    viewType = R.layout.listview_chatting_room;
                    break;

                case DATE:
                    viewType = R.layout.listview_chatting_entrance;
                    break;

                case IMAGE:
                    viewType = R.layout.listview_chatting_room_image;
                    break;

                case AUDIO:
                    viewType = R.layout.listview_chatting_room_voice;
                    break;
            }

            convertView = mInflater.inflate(viewType, parent, false);
        }

        viewType = getItemViewType(position);
        ListViewItem_Chat listViewItem_chat = listViewItemList.get(position);
        switch (viewType) {
            case ENTRANCE:
                convertView = mInflater.inflate(R.layout.listview_chatting_entrance, parent, false);
                TextView tv_Entrance = (TextView) convertView.findViewById(R.id.tv_Entrance);
                tv_Entrance.setText(listViewItem_chat.getName());
                break;

            case MESSAGE:
                convertView = mInflater.inflate(R.layout.listview_chatting_room, parent, false);

                // 나의 정보 레이아웃 객체 선언
                LinearMe = (LinearLayout) convertView.findViewById(R.id.LinearMe);
                tv_MeMessage = (TextView) convertView.findViewById(R.id.tv_MeMessage);
                tv_MeTime = (TextView) convertView.findViewById(R.id.tv_MeTime);
                tv_MeEmail = (TextView) convertView.findViewById(R.id.tv_MeEmail);
                tv_Status = (TextView) convertView.findViewById(R.id.tv_Status);

                // 상대방 정보 레이아웃 객체 선언
                LinearYou = (LinearLayout) convertView.findViewById(R.id.LinearYou);
                tv_YouMessage = (TextView) convertView.findViewById(R.id.tv_YouMessage);
                tv_YouName = (TextView) convertView.findViewById(R.id.tv_YouName);
                tv_YouTime = (TextView) convertView.findViewById(R.id.tv_YouTime);
                iv_YouProfile = (ImageView) convertView.findViewById(R.id.iv_YouProfile);
                tv_YouEmail = (TextView) convertView.findViewById(R.id.tv_YouEmail);

                // 나의 정보 레이아웃 값 입력
                tv_MeMessage.setText(listViewItem_chat.getMessage()); // 메세지 내용
                tv_MeTime.setText(listViewItem_chat.getTime()); // 보내는 시간
                tv_MeEmail.setText(listViewItem_chat.getEmail());

                // 상대방 정보 레이아웃 값 입력
                tv_YouName.setText(listViewItem_chat.getName()); // 이름
                tv_YouTime.setText(listViewItem_chat.getTime()); // 보내는 시간
                tv_YouMessage.setText(listViewItem_chat.getMessage()); // 메세지 내용
                tv_YouEmail.setText(listViewItem_chat.getEmail());
                Glide.with(context).load(listViewItem_chat.getProfile()).into(iv_YouProfile); // 프로필

                iv_YouProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String email = listViewItemList.get(position).getEmail();
                        Log.d(TAG, "email : " + email);

                        Intent intent = new Intent(context, Chat_UserInfo.class);
                        intent.putExtra("email", email);
                        context.startActivity(intent);
                    }
                });

                if (listViewItemList.get(position).getEmail().equals(Login_Email)) {
                    LinearMe.setVisibility(View.VISIBLE);
                    LinearYou.setVisibility(View.GONE);

                    if (position > 0 && listViewItemList.get(position).getEmail().equals(listViewItemList.get(position - 1).getEmail())) {
                        listViewItemList.get(position - 1).setTime("");
                    }

                } else {
                    LinearMe.setVisibility(View.GONE);
                    LinearYou.setVisibility(View.VISIBLE);

                    if (position > 0 && listViewItemList.get(position).getEmail().equals(listViewItemList.get(position - 1).getEmail())) {
                        listViewItemList.get(position - 1).setTime("");
                        tv_YouName.setVisibility(View.GONE);
                        iv_YouProfile.setVisibility(View.INVISIBLE);
                    } else {
                        tv_YouName.setVisibility(View.VISIBLE);
                        iv_YouProfile.setVisibility(View.VISIBLE);
                    }
                }
                break;

            case DATE:
                convertView = mInflater.inflate(R.layout.listview_chatting_date, parent, false);

                TextView tv_ShowDate = (TextView) convertView.findViewById(R.id.tv_ShowDate);
                tv_ShowDate.setText(listViewItem_chat.getTime());
                break;

            case IMAGE:
                convertView = mInflater.inflate(R.layout.listview_chatting_room_image, parent, false);

                // 나의 정보 레이아웃 객체 선언
                LinearMe = (LinearLayout) convertView.findViewById(R.id.LinearMe);
                tv_MeTime = (TextView) convertView.findViewById(R.id.tv_MeTime);
                tv_MeEmail = (TextView) convertView.findViewById(R.id.tv_MeEmail);
                tv_Status = (TextView) convertView.findViewById(R.id.tv_Status);

                // 이미지 전송 (나)
                iv_MeImage = (ImageView) convertView.findViewById(R.id.iv_MeImage);
                Glide.with(context).load(listViewItem_chat.getChatting_image()).into(iv_MeImage);

                iv_MeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "이미지 클릭!!!", Toast.LENGTH_SHORT).show();

                        String profile = listViewItemList.get(position).getChatting_image();

                        Log.d(TAG, "**************************************************");
                        Log.d(TAG, "이미지 확대 보기 ( Pinch Zoom )");
                        Log.d(TAG, "보내는 이미지 url 값 send_profile : " + profile);
                        Log.d(TAG, "**************************************************");

                        Intent intent = new Intent(getApplicationContext(), Chat_PinchZoom.class);
                        intent.putExtra("profile", profile); // url 주소 인텐트로 넘겨주기
                        context.startActivity(intent);
                    }
                });



                // 나의 정보 레이아웃 값 입력
                tv_MeTime.setText(listViewItem_chat.getTime()); // 보내는 시간
                tv_MeEmail.setText(listViewItem_chat.getEmail());


                // 상대방 정보 레이아웃 객체 선언
                LinearYou = (LinearLayout) convertView.findViewById(R.id.LinearYou);
                tv_YouName = (TextView) convertView.findViewById(R.id.tv_YouName);
                tv_YouTime = (TextView) convertView.findViewById(R.id.tv_YouTime);
                tv_YouEmail = (TextView) convertView.findViewById(R.id.tv_YouEmail);

                // 이미지 전송 (상대방)
                iv_YouImage = (ImageView) convertView.findViewById(R.id.iv_YouImage);
                Glide.with(context).load(listViewItem_chat.getChatting_image()).into(iv_YouImage);


                // 이미지 경로 저장할 get/set 추가 하고 glide에 때려 박음 된다. 상대방의 프로필 이미지 띄우는 거랑 똑같다. 똑같은 원리로 적용하면 된다.
                // 상대방 정보 레이아웃 값 입력
                tv_YouName.setText(listViewItem_chat.getName()); // 이름
                tv_YouTime.setText(listViewItem_chat.getTime()); // 보내는 시간
                tv_YouEmail.setText(listViewItem_chat.getEmail());

                iv_YouProfile = (ImageView) convertView.findViewById(R.id.iv_YouProfile); // 프로필 사진
                Glide.with(context).load(listViewItem_chat.getProfile()).into(iv_YouProfile); // 프로필


                iv_YouProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String email = listViewItemList.get(position).getEmail();
                        Log.d(TAG, "email : " + email);

                        Intent intent = new Intent(context, Chat_UserInfo.class);
                        intent.putExtra("email", email);
                        context.startActivity(intent);
                    }
                });


                // 메세지를 보내는 사람에 따라서 보이는 View 설정
                if (listViewItemList.get(position).getEmail().equals(Login_Email)) {
                    LinearMe.setVisibility(View.VISIBLE);
                    LinearYou.setVisibility(View.GONE);

                    if (position > 0 && listViewItemList.get(position).getEmail().equals(listViewItemList.get(position - 1).getEmail())) {
                        listViewItemList.get(position - 1).setTime("");
                    }

                } else {
                    LinearMe.setVisibility(View.GONE);
                    LinearYou.setVisibility(View.VISIBLE);

                    if (position > 0 && listViewItemList.get(position).getEmail().equals(listViewItemList.get(position - 1).getEmail())) {

                        listViewItemList.get(position - 1).setTime("");
                        tv_YouName.setVisibility(View.GONE);
                        iv_YouProfile.setVisibility(View.GONE);

                    } else {

                        tv_YouName.setVisibility(View.VISIBLE);
                        iv_YouProfile.setVisibility(View.VISIBLE);
                    }
                }
                break;

            case AUDIO:
                convertView = mInflater.inflate(R.layout.listview_chatting_room_voice, parent, false);

                // 나의 정보 레이아웃 객체 선언
                LinearMe = (LinearLayout) convertView.findViewById(R.id.LinearMe);
                tv_MeTime = (TextView) convertView.findViewById(R.id.tv_MeTime);
                tv_MeEmail = (TextView) convertView.findViewById(R.id.tv_MeEmail);
                tv_Status = (TextView) convertView.findViewById(R.id.tv_Status);
                ibtn_MePlay = (ImageButton) convertView.findViewById(R.id.ibtn_MePlay); // 재생 버튼 클릭
                tv_MePlayTime = (TextView) convertView.findViewById(R.id.tv_MePlayTime); // 재생 시간 표시

                ibtn_MePlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String url = listViewItemList.get(position).getMessage();
                        try {

                            playAudio(url);
                            Toast.makeText(getApplicationContext(), "재생 시작", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                // 상대방 정보 레이아웃 객체 선언
                LinearYou = (LinearLayout) convertView.findViewById(R.id.LinearYou);
                tv_YouName = (TextView) convertView.findViewById(R.id.tv_YouName);
                tv_YouTime = (TextView) convertView.findViewById(R.id.tv_YouTime);
                iv_YouProfile = (ImageView) convertView.findViewById(R.id.iv_YouProfile);
                tv_YouEmail = (TextView) convertView.findViewById(R.id.tv_YouEmail);
                ibtn_YouPlay = (ImageButton) convertView.findViewById(R.id.ibtn_YouPlay); // 재생 버튼 클릭
                tv_YouPlayTime = (TextView) convertView.findViewById(R.id.tv_YouPlayTime); // 재생 시간 표시

                // 나의 정보 레이아웃 값 입력
                tv_MeTime.setText(listViewItem_chat.getTime()); // 보내는 시간
                tv_MeEmail.setText(listViewItem_chat.getEmail());

                // 상대방 정보 레이아웃 값 입력
                tv_YouName.setText(listViewItem_chat.getName()); // 이름
                tv_YouTime.setText(listViewItem_chat.getTime()); // 보내는 시간
                tv_YouEmail.setText(listViewItem_chat.getEmail());
                Glide.with(context).load(listViewItem_chat.getProfile()).into(iv_YouProfile); // 프로필

                iv_YouProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String email = listViewItemList.get(position).getEmail();
                        Log.d(TAG, "email : " + email);

                        Intent intent = new Intent(context, Chat_UserInfo.class);
                        intent.putExtra("email", email);
                        context.startActivity(intent);
                    }
                });

                ibtn_YouPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String url = listViewItemList.get(position).getMessage();
                        try {

                            playAudio(url);
                            Toast.makeText(getApplicationContext(), "재생 시작", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                if (listViewItemList.get(position).getEmail().equals(Login_Email)) {
                    LinearMe.setVisibility(View.VISIBLE);
                    LinearYou.setVisibility(View.GONE);

                    if (position > 0 && listViewItemList.get(position).getEmail().equals(listViewItemList.get(position - 1).getEmail())) {
                        listViewItemList.get(position - 1).setTime("");
                    }

                } else {
                    LinearMe.setVisibility(View.GONE);
                    LinearYou.setVisibility(View.VISIBLE);

                    if (position > 0 && listViewItemList.get(position).getEmail().equals(listViewItemList.get(position - 1).getEmail())) {
                        listViewItemList.get(position - 1).setTime("");
                        tv_YouName.setVisibility(View.GONE);
                        iv_YouProfile.setVisibility(View.INVISIBLE);
                    } else {
                        tv_YouName.setVisibility(View.VISIBLE);
                        iv_YouProfile.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    // 지정한 위치에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 입장 메세지
    public void addItem(String name) { // 최초 입장시 ~님이 입장하셨습니다.
        ListViewItem_Chat item = new ListViewItem_Chat();

        item.setType(ENTRANCE);
        item.setName(name);

        listViewItemList.add(item);
    }

    // 일반 메세지
    public void addItem(String email, String name, String time, String message, String profile) {
        ListViewItem_Chat item = new ListViewItem_Chat();

        item.setType(MESSAGE);
        item.setEmail(email);
        item.setName(name);
        item.setTime(time);
        item.setMessage(message);
        item.setProfile(profile);

        listViewItemList.add(item);
    }

    // 시간 표시
    public void addItemTime(String Date) { // 최초 입장시 최상단에 시간 표시
        ListViewItem_Chat item = new ListViewItem_Chat();

        item.setType(DATE);
        item.setTime(Date);

        listViewItemList.add(item);
    }

    // 사진 메세지
    public void addImage(String email, String name, String time, String image, String profile) {
        ListViewItem_Chat item = new ListViewItem_Chat();

        item.setType(IMAGE);
        item.setEmail(email);
        item.setName(name);
        item.setTime(time);
        item.setChatting_image(image);
        item.setProfile(profile);

        listViewItemList.add(item);
    }

    // 음성 메세지
    public void addVoice(String email, String name, String time, String voiceurl, String profile) {
        ListViewItem_Chat item = new ListViewItem_Chat();

        item.setType(AUDIO);
        item.setEmail(email);
        item.setName(name);
        item.setTime(time);
        item.setMessage(voiceurl);
        item.setProfile(profile);

        listViewItemList.add(item);
    }

    private void playAudio(String url) throws Exception {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer(); // 미디어 객체 생성하고 초기화 후 시작
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    private void killMediaPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.release(); // 미디어 플레이어 리소스 해제
        }
    }

}
