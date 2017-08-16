package thread.seopftware.mychef.Chatting;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import thread.seopftware.mychef.R;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewAdapter_ViewPager2_ChatList extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    ArrayList<ListViewItem_ViewPager2_ChatList> listViewItemList ;
    Context context;
    int layout;

    // ListViewAdapter의 생성자
    public ListViewAdapter_ViewPager2_ChatList(Context context, int layout, ArrayList<ListViewItem_ViewPager2_ChatList> listViewItemList) {
        this.context=context;
        this.layout=layout;
        this.listViewItemList=listViewItemList;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴.
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴.
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final int pos=position;
        final Context context=parent.getContext();

        // "ListViewItem_Menu" Layout을 inflate하여 convertView 참조 획득

        if(convertView==null) {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.listview_chat_roomlist, parent, false);
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem_ViewPager2_ChatList listViewItem=listViewItemList.get(position);

        // 화면에 표시될 View(Layout이 inflate된)으로 부터 위젯에 대한 참조 획득
        final TextView tv_Name= (TextView) convertView.findViewById(R.id.tv_Name);
        TextView tv_Date= (TextView) convertView.findViewById(R.id.tv_Date);
        TextView tv_Message= (TextView) convertView.findViewById(R.id.tv_Message);
        TextView tv_RoomNumber= (TextView) convertView.findViewById(R.id.tv_RoomNumber);

        ImageView iv_Profile= (ImageView) convertView.findViewById(R.id.iv_Profile);
        Glide.with(context).load(listViewItem.getProfile()).into(iv_Profile);

        Button btn_NumMessage= (Button) convertView.findViewById(R.id.btn_NumMessage);
        Button btn_NumPeople= (Button) convertView.findViewById(R.id.btn_NumPeople);

        // 아이템 내 각 위젯에 데이터 반영
        tv_Name.setText(listViewItem.getName());
        tv_Date.setText(listViewItem.getDate());
        tv_Message.setText(listViewItem.getMessage());
        tv_RoomNumber.setText(listViewItem.getRoomNumber());

        btn_NumMessage.setText(listViewItem.getNumMessage());
        btn_NumPeople.setText(listViewItem.getNumPeople());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String room = listViewItemList.get(position).getRoomNumber();
                Log.e(TAG, "클릭시 room 번호 : " + room);

                Intent intent = new Intent(context, Chat_Chatting.class);
                intent.putExtra("room_number", room);
                context.startActivity(intent);

            }
        });

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴.
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴.
    @Override
    public Object getItem(int position) {
        return null;
    }

/*    public void addItem(String Name, String Message, String Profile) {
        ListViewItem_ViewPager_FriendList item=new ListViewItem_ViewPager_FriendList();

        item.setName(Name);
        item.setMessage(Message);
        item.setProfile(Profile);

        listViewItemList.add(item);
    }*/

}
