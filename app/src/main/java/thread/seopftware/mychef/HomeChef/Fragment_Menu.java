package thread.seopftware.mychef.HomeChef;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class Fragment_Menu extends ListFragment {

    ListViewAdapter_Menu adapter;

    // 생성자
    public Fragment_Menu() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final CharSequence[] items=new CharSequence[] {"수정하기", "삭제하기"};
                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                dialog.setTitle("MENU");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(items[which]=="수정하기") {
//                            Intent intent=new Intent(getContext(), )
                        }

                        if(items[which]=="삭제하기") {

                        }
                    }
                });
                dialog.show();

                return true;
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("MyChef_Menu");

        adapter=new ListViewAdapter_Menu();
        setListAdapter(adapter);

        // 데이터 추가 하는 곳.
        adapter.addItem("korea", "english", "price", "http://115.71.239.151/foodimage/tmp_1499671034785.jpg");

        return super.onCreateView(inflater, container, savedInstanceState);

    }


    // 클릭 이벤트
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//
//        ListViewItem_Menu item =(ListViewItem_Menu) l.getItemAtPosition(position);
//
//        String KoreaName=item.getKoreaName();
//        String EnglishName=item.getEnglishName();
//        String Price=item.getPrice();
//
//        super.onListItemClick(l, v, position, id);
//    }

    // 액티비티에서 데이터를 추가할 때 사용
//    public void addItem(String KoreaName, String EnglishName, String Price, String ImagePath) {
//        adapter.addItem(KoreaName, EnglishName, Price, ImagePath);
//    }

}
