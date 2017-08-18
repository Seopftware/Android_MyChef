package thread.seopftware.mychef.Chatting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import thread.seopftware.mychef.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by MSI on 2017-08-16.
 */

public class ListViewAdapter_Invite extends BaseAdapter {

    ArrayList<ListViewItem_Invite> listViewItemList; // Adapter에 추가된 데이터를 저장하기 위한 Arraylist (원본 데이터 리스트)
    ArrayList<ListViewItem_Invite> filteredItemList = listViewItemList;
    Filter listFilter;

    Context context;
    int layout;

    public ListViewAdapter_Invite() {

    }


    public ListViewAdapter_Invite(Context context, int layout, ArrayList<ListViewItem_Invite> listViewItemList) {
        this.context=context;
        this.layout=layout;
        this.listViewItemList=listViewItemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos= position;
        final Context context = parent.getContext();

        if(convertView == null) {
//            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listview_chat_invite, parent, false);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_chat_invite2, parent, false);

        }

        // 화면에 표시될 View(Layout이 inflate된) 으로부터 위젯에 대한 참조 획득
        ImageView iv_Profile = (ImageView) convertView.findViewById(R.id.iv_Profile);
        TextView tv_Name = (TextView) convertView.findViewById(R.id.tv_Name);
        TextView tv_Email = (TextView) convertView.findViewById(R.id.tv_Email);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

        // ArrayList에서 해당 position에 위치한 데이터 참조 획득
        ListViewItem_Invite listViewItem = listViewItemList.get(pos);

        // 아이템 내 각 위젯에 데이터 반영
        tv_Name.setText(listViewItem.getName());
        tv_Email.setText(listViewItem.getEmail());
        Glide.with(context).load(listViewItem.getImage()).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(iv_Profile);

        return convertView;
    }






    // 어댑터에 사용되느 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return listViewItemList.size();
    }


    // 지정한 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


/*    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        filteredItemList.clear();

        if(charText.length() == 0 ) {
            filteredItemList.addAll(listViewItemList);
        } else {
            for()
        }

    }


    @Override
    public Filter getFilter() {
        if(listFilter == null) {
            listFilter = new ListFilter();
        }

        return listFilter;
    }


    public class ListFilter extends Filter {

        // 필터링을 수행하는 함수.
        // 필터링을 수행하는 루프를 이 함수에 구현한 다음, 필터링된 결과 리스트를 FilterResults에 담아서 리턴
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0) {

                results.values = listViewItemList;
                results.count = listViewItemList.size();

                Log.d("adapter", "listViewItemlist 사이즈는? "+ listViewItemList.size());
                Log.d("adapter", "listViewItemlist 벨류는? "+ listViewItemList);


            } else {

                ArrayList<ListViewItem_Invite> itemList = new ArrayList<ListViewItem_Invite>();
                for(ListViewItem_Invite item : listViewItemList) {

                    Log.d("adapter", "item의 값은 ? " + item);

                    if(item.getName().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        itemList.add(item);
                    }
                }

                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        // performFiltering() 함수에서 필터링된 결과를 UI에 갱신시키는 역할을 수행
        // Adapter를 통한 ListView 갱신 작업 구현
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredItemList = (ArrayList<ListViewItem_Invite>) results.values;

            //notify
            if(results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }
    }*/
}
