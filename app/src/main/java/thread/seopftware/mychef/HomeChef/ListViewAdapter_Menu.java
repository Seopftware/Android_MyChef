package thread.seopftware.mychef.HomeChef;

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

public class ListViewAdapter_Menu extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    ArrayList<ListViewItem_Menu> listViewItemList ;

    // ListViewAdapter의 생성자
    public ListViewAdapter_Menu(ArrayList<ListViewItem_Menu> listViewItemList) {
        this.listViewItemList=listViewItemList;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴.
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos=position;
        final Context context=parent.getContext();

        // "ListViewItem_Menu" Layout을 inflate하여 convertView 참조 획득

        if(convertView==null) {
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.listview_chefmenu, parent, false);
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem_Menu listViewItem=listViewItemList.get(position);

        // 화면에 표시될 View(Layout이 inflate된)으로 부터 위젯에 대한 참조 획득
        TextView Id= (TextView) convertView.findViewById(R.id.tv_Id);
        TextView KoreaName= (TextView) convertView.findViewById(R.id.tv_KoreaName);
        TextView EnglishName= (TextView) convertView.findViewById(R.id.tv_EnglishName);
        TextView Price= (TextView) convertView.findViewById(R.id.tv_Price);
        TextView Date= (TextView) convertView.findViewById(R.id.tv_Date);
        ImageView iv_Food= (ImageView) convertView.findViewById(R.id.iv_Profile);
        Glide.with(context).load(listViewItem.getImagePath()).into(iv_Food);

        // 아이템 내 각 위젯에 데이터 반영
        Id.setText(listViewItem.getId());
        KoreaName.setText(listViewItem.getKoreaName());
        EnglishName.setText(listViewItem.getEnglishName());
        Price.setText(listViewItem.getPrice());
        Date.setText(listViewItem.getDate());

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
