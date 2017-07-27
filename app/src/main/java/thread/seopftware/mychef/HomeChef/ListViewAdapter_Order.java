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

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewAdapter_Order extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem_Order> listViewItemList = new ArrayList<ListViewItem_Order>();

    // ListViewAdapter의 생성자
    public ListViewAdapter_Order() {

    }

    // Adapter에 사용되는 데이터의 개수를 기턴.
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
            convertView=inflater.inflate(R.layout.listview_cheforder, parent, false);
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem_Order listViewItem=listViewItemList.get(position);

        // 화면에 표시될 View(Layout이 inflate된)으로 부터 위젯에 대한 참조 획득
        TextView KoreaName= (TextView) convertView.findViewById(R.id.tv_KoreaName);
        TextView EnglishName= (TextView) convertView.findViewById(R.id.tv_EnglishName);
        TextView Price= (TextView) convertView.findViewById(R.id.tv_Price);
        ImageView iv_Food= (ImageView) convertView.findViewById(R.id.iv_Chef_Profile);
        Glide.with(context).load(listViewItem.getImagePath()).into(iv_Food);

        // 아이템 내 각 위젯에 데이터 반영
        KoreaName.setText(listViewItem.getKoreaName());
        EnglishName.setText(listViewItem.getEnglishName());
        Price.setText(listViewItem.getPrice());


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

    // 아이템 데이터 추가를 위한 함수.
    public void addItem(String KoreaName, String EnglishName, String Price, String ImagePath) {
        ListViewItem_Order item=new ListViewItem_Order();

        item.setKoreaName(KoreaName);
        item.setEnglishName(EnglishName);
        item.setPrice(Price);
        item.setImagePath(ImagePath);

        listViewItemList.add(item);
    }




}
