//package thread.seopftware.mychef.Chatting;
//
//import android.content.Context;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import thread.seopftware.mychef.R;
//
//
//public class ListViewArrayAdapter_Chat extends ArrayAdapter {
//
//    private static final int ENTRANCE = 0 ;
//    private static final int MESSAGE = 1 ;
//    private static final int ITEM_VIEW_TYPE_MAX = 2;
//
//    private TextView tv_MeMessage, tv_YouMessage; // 메세지
//    private TextView tv_MeTime, tv_YouTime;
//    private TextView tv_YouName;
//    private ImageView iv_YouProfile;
//    private LinearLayout LinearMe, LinearYou;
//    private List chatMessageList = new ArrayList();
//
//
//    public ListViewArrayAdapter_Chat(Context context, int textViewResourceId) {
//        super(context, textViewResourceId);
//    }
//
//    @Override
//    public void add(ChatMessage object) {
//        chatMessageList.add(object);
//        super.add(object);
//    }
//
//
//    public int getCount() {
//
//        this.chatMessageList.size();
//
//    }
//
//    public ChatMessage getItem(int index) {
//        return this.chatMessageList.get(index);
//    }
//
//    @Override
//    public int getViewTypeCount() {
//
//        return ITEM_VIEW_TYPE_MAX ;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        ;
//    }
//
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        final Context context=parent.getContext();
//        int viewType = 0;
//        Log.d("adapter view type", String.valueOf(viewType));
//
//
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            ListViewItem_Chat listViewItem_chat = listViewItemList.get(position);
//
//            viewType = getItemViewType(position);
//
//            switch (viewType) {
//                case ENTRANCE:
//                    convertView = inflater.inflate(R.layout.listview_chatting_entrance, parent, false);
//
//                    TextView tv_Entrance = (TextView) convertView.findViewById(R.id.tv_Entrance);
//                    tv_Entrance.setText(listViewItem_chat.getName());
//                    break;
//
//                case MESSAGE:
//                    convertView = inflater.inflate(R.layout.listview_chatting_room, parent, false);
//
//                    // 나의 정보 레이아웃 객체 선언
//                    LinearMe = (LinearLayout) convertView.findViewById(R.id.LinearMe);
//                    tv_MeMessage = (TextView) convertView.findViewById(R.id.tv_MeMessage);
//                    tv_MeTime= (TextView) convertView.findViewById(R.id.tv_MeTime);
//
//                    // 상대방 정보 레이아웃 객체 선언
//                    LinearYou= (LinearLayout) convertView.findViewById(R.id.LinearYou);
//                    tv_YouMessage = (TextView) convertView.findViewById(R.id.tv_YouMessage);
//                    tv_YouName= (TextView) convertView.findViewById(R.id.tv_YouName);
//                    tv_YouTime= (TextView) convertView.findViewById(R.id.tv_YouTime);
//                    iv_YouProfile= (ImageView) convertView.findViewById(R.id.iv_YouProfile);
//
//                    // 나의 정보 레이아웃 값 입력
//                    tv_MeMessage.setText(listViewItem_chat.getMessage()); // 메세지 내용
//                    tv_MeTime.setText(listViewItem_chat.getTime()); // 보내는 시간
//
//                    // 상대방 정보 레이아웃 값 입력
//                    tv_YouName.setText(listViewItem_chat.getName()); // 이름
//                    tv_YouTime.setText(listViewItem_chat.getTime()); // 보내는 시간
//                    tv_YouMessage.setText(listViewItem_chat.getMessage()); // 메세지 내용
//                    Glide.with(context).load(listViewItem_chat.getProfile()).into(iv_YouProfile); // 프로필
//
//                    LinearMe.setVisibility(listViewItem_chat.getStatus() ? View.VISIBLE : View.GONE); // right면 내가 보낸 것. LinearMe만 보이게끔
//                    LinearYou.setVisibility(listViewItem_chat.getStatus() ? View.GONE : View.VISIBLE); // right면 LinearYou 가림.
//                    break;
//
//            }
//        }
//        return convertView;
//    }
//
//    // 지정한 위치에 있는 데이터 리턴
//    @Override
//    public Object getItem(int position) {
//        return listViewItemList.get(position);
//    }
//
//    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    public void addItem(String name) { // 최초 입장시
//        ListViewItem_Chat item = new ListViewItem_Chat();
//
//        item.setType(ENTRANCE);
//        item.setName(name);
//
//        listViewItemList.add(item);
//    }
//
//    public void addItem(Boolean status, String email, String name, String time, String message, String profile) { // 최초 입장시
//        ListViewItem_Chat item = new ListViewItem_Chat();
//
//        item.setType(MESSAGE);
//        item.setStatus(status);
//        item.setEmail(email);
//        item.setName(name);
//        item.setTime(time);
//        item.setMessage(message);
//        item.setProfile(profile);
//
//        listViewItemList.add(item);
//    }
//
//
//
//}
