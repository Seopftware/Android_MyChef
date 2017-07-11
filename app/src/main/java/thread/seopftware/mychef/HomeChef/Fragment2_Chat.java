package thread.seopftware.mychef.HomeChef;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class Fragment2_Chat extends ListFragment {

    ListViewAdapter_Menu adapter;

    // 생성자
    public Fragment2_Chat() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("MyChef_Chat");

        adapter=new ListViewAdapter_Menu();
        setListAdapter(adapter);

//        adapter.addItem("korea", "english", "price");
//        adapter.addItem("korea1", "english2", "price3");
//        adapter.addItem("korea1", "english2", "price3");

        return super.onCreateView(inflater, container, savedInstanceState);

//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.activity_fragment__menu, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        ListViewItem_Menu item =(ListViewItem_Menu) l.getItemAtPosition(position);

        String KoreaName=item.getKoreaName();
        String EnglishName=item.getEnglishName();
        String Price=item.getPrice();

        super.onListItemClick(l, v, position, id);
    }

//    public void addItem(String KoreaName, String EnglishName, String Price) {
//        adapter.addItem(KoreaName, EnglishName, Price);
//    }
}

