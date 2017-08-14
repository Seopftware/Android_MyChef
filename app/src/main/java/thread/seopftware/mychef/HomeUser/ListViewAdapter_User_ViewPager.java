package thread.seopftware.mychef.HomeUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import thread.seopftware.mychef.R;

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewAdapter_User_ViewPager extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    ArrayList<ListViewItem_User_ViewPager> listViewItemList ;

    // ListViewAdapter의 생성자
    public ListViewAdapter_User_ViewPager(ArrayList<ListViewItem_User_ViewPager> listViewItemList) {
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
            convertView=inflater.inflate(R.layout.listview_viewpager, parent, false);
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListViewItem_User_ViewPager listViewItem=listViewItemList.get(position);

        // 화면에 표시될 View(Layout이 inflate된)으로 부터 위젯에 대한 참조 획득
        TextView tv_Chef_Name= (TextView) convertView.findViewById(R.id.tv_Chef_Name);
        TextView tv_Chef_Email= (TextView) convertView.findViewById(R.id.tv_Chef_Email);
        ImageView iv_Chef_Profile= (ImageView) convertView.findViewById(R.id.iv_Profile);
        Glide.with(context).load(listViewItem.getChef_Profile()).into(iv_Chef_Profile);

        TextView tv_Food_Name= (TextView) convertView.findViewById(R.id.tv_Food_Name);
        TextView tv_Food_Count= (TextView) convertView.findViewById(R.id.tv_Food_Count);
        TextView tv_Food_Date= (TextView) convertView.findViewById(R.id.tv_Food_Date);
        TextView tv_Food_Time= (TextView) convertView.findViewById(R.id.tv_Food_Time);
        TextView tv_Food_Place= (TextView) convertView.findViewById(R.id.tv_Food_Place);

        // 안보이는 정보
        TextView tv_Food_Id= (TextView) convertView.findViewById(R.id.tv_Food_Id);
        TextView tv_Chef_Number= (TextView) convertView.findViewById(R.id.tv_Chef_Number);

        // 아이템 내 각 위젯에 데이터 반영
        tv_Chef_Name.setText(listViewItem.getChef_Name());
        tv_Food_Name.setText(listViewItem.getFood_Name());
        tv_Food_Count.setText(listViewItem.getFood_Count());
        tv_Food_Date.setText(listViewItem.getFood_Date());
        tv_Food_Time.setText(listViewItem.getFood_Time());
        tv_Food_Place.setText(listViewItem.getFood_Place());
        tv_Chef_Email.setText(listViewItem.getChef_Email());

        tv_Food_Id.setText(listViewItem.getFood_Id());
        tv_Chef_Number.setText(listViewItem.getChef_Number());

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
