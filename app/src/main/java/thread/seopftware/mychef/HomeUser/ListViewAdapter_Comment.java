package thread.seopftware.mychef.HomeUser;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import thread.seopftware.mychef.R;

/**
 * Created by MSI on 2017-07-11.
 */

public class ListViewAdapter_Comment extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    ArrayList<ListViewItem_Comment> listViewItemList ;

    // ListViewAdapter의 생성자
    public ListViewAdapter_Comment(ArrayList<ListViewItem_Comment> listViewItemList) {
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
            convertView=inflater.inflate(R.layout.listview_review_comment, parent, false);
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListViewItem_Comment listViewItem=listViewItemList.get(position);

        // 화면에 표시될 View(Layout이 inflate된)으로 부터 위젯에 대한 참조 획득
        TextView tv_Date= (TextView) convertView.findViewById(R.id.tv_Date);
        TextView tv_Name= (TextView) convertView.findViewById(R.id.tv_Name);
        TextView tv_Email= (TextView) convertView.findViewById(R.id.tv_Email);
        TextView tv_Comment= (TextView) convertView.findViewById(R.id.tv_Comment);
        RatingBar ratingBar= (RatingBar) convertView.findViewById(R.id.ratingBar);
        Button btn_Edit= (Button) convertView.findViewById(R.id.btn_Edit);
        Button btn_Delete= (Button) convertView.findViewById(R.id.btn_Delete);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        // 아이템 내 각 위젯에 데이터 반영
        tv_Date.setText(listViewItem.getCreatedDate());
        tv_Name.setText(listViewItem.getName());
        tv_Comment.setText(listViewItem.getComment());
        ratingBar.setProgress((int) listViewItem.getRatingBar());

        btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataSetChanged();
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

}
