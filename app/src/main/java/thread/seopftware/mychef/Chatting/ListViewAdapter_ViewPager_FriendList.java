package thread.seopftware.mychef.Chatting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import thread.seopftware.mychef.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewAdapter_ViewPager_FriendList extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    ArrayList<ListViewItem_ViewPager_FriendList> listViewItemList ;
    Context context;
    int layout;

    // ListViewAdapter의 생성자
    public ListViewAdapter_ViewPager_FriendList(Context context, int layout, ArrayList<ListViewItem_ViewPager_FriendList> listViewItemList) {
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
            convertView=inflater.inflate(R.layout.listview_chat_friendlist, parent, false);
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem_ViewPager_FriendList listViewItem=listViewItemList.get(position);

        // 화면에 표시될 View(Layout이 inflate된)으로 부터 위젯에 대한 참조 획득
        TextView tv_Name= (TextView) convertView.findViewById(R.id.tv_Name);
        TextView tv_Message= (TextView) convertView.findViewById(R.id.tv_Message);
        TextView tv_Email= (TextView) convertView.findViewById(R.id.tv_Email);
        ImageView iv_Profile= (ImageView) convertView.findViewById(R.id.iv_Profile);
        Glide.with(context).load(listViewItem.getProfile()).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_Profile);

        // 아이템 내 각 위젯에 데이터 반영
        tv_Name.setText(listViewItem.getName());
        tv_Message.setText(listViewItem.getMessage());
        tv_Email.setText(listViewItem.getEmail());

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


}
