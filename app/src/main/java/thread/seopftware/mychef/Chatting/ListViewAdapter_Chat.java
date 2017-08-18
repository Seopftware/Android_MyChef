package thread.seopftware.mychef.Chatting;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import thread.seopftware.mychef.R;

import static android.content.Context.MODE_PRIVATE;
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
    private static final int ENTRANCE = 0 ;
    private static final int MESSAGE = 1 ;
    private static final int DATE = 2 ;
    private static final int ITEM_VIEW_TYPE_MAX = 3;

    private TextView tv_MeEmail, tv_YouEmail;
    private TextView tv_MeMessage, tv_YouMessage; // 메세지
    private TextView tv_MeTime, tv_YouTime;
    private TextView tv_YouName;
    private TextView tv_Status;
    private ImageView iv_YouProfile;
    private LinearLayout LinearMe, LinearYou, LinearEntrance;
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
        return ITEM_VIEW_TYPE_MAX ;
    }

    @Override
    public int getItemViewType(int position) {
        return listViewItemList.get(position).getType();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context=parent.getContext();
        int viewType = 0;

        // 세션 유지를 위한 이메일 값 불러들이기
        SharedPreferences pref1 = context.getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = context.getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK=pref2.getString(FBAPI, "0");

        if(!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = context.getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(FBEMAIL, "");
        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = context.getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(KAEMAIL, "");
        } else { // 일반
            SharedPreferences pref = context.getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            Login_Email=pref.getString(CHEFNORMALLEMAIL, "");
        }



        if(convertView==null) {
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
                    tv_MeTime= (TextView) convertView.findViewById(R.id.tv_MeTime);
                    tv_MeEmail= (TextView) convertView.findViewById(R.id.tv_MeEmail);
                    tv_Status= (TextView) convertView.findViewById(R.id.tv_Status);

                    // 상대방 정보 레이아웃 객체 선언
                    LinearYou= (LinearLayout) convertView.findViewById(R.id.LinearYou);
                    tv_YouMessage = (TextView) convertView.findViewById(R.id.tv_YouMessage);
                    tv_YouName= (TextView) convertView.findViewById(R.id.tv_YouName);
                    tv_YouTime= (TextView) convertView.findViewById(R.id.tv_YouTime);
                    iv_YouProfile= (ImageView) convertView.findViewById(R.id.iv_YouProfile);
                    tv_YouEmail= (TextView) convertView.findViewById(R.id.tv_YouEmail);

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

                    if(listViewItemList.get(position).getEmail().equals(Login_Email)) {
                        LinearMe.setVisibility(View.VISIBLE);
                        LinearYou.setVisibility(View.GONE);

                        if(position > 0 && listViewItemList.get(position).getEmail().equals(listViewItemList.get(position-1).getEmail())) {
                            listViewItemList.get(position-1).setTime("");
                        }

                    } else {
                        LinearMe.setVisibility(View.GONE);
                        LinearYou.setVisibility(View.VISIBLE);

                        if(position > 0 && listViewItemList.get(position).getEmail().equals(listViewItemList.get(position-1).getEmail())) {
                            listViewItemList.get(position-1).setTime("");
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

    public void addItem(String name) { // 최초 입장시 ~님이 입장하셨습니다.
        ListViewItem_Chat item = new ListViewItem_Chat();

        item.setType(ENTRANCE);
        item.setName(name);

        listViewItemList.add(item);
    }

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

    public void addItemTime(String Date) { // 최초 입장시 최상단에 시간 표시
        ListViewItem_Chat item = new ListViewItem_Chat();

        item.setType(DATE);
        item.setTime(Date);

        listViewItemList.add(item);
    }



}
