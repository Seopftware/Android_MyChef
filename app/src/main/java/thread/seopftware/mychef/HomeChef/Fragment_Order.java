package thread.seopftware.mychef.HomeChef;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by MSI on 2017-07-11.
 */

public class Fragment_Order extends ListFragment {

    ListViewAdapter_Order adapter;

    // 생성자
    public Fragment_Order() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("MyChef_Home");

        adapter=new ListViewAdapter_Order();
        setListAdapter(adapter);

//        adapter.addItem("korea", "english", "price", "http://115.71.239.151/foodimage/tmp_1499671034785.jpg");
//        adapter.addItem("korea1", "english2", "price3", "http://115.71.239.151/foodimage/tmp_1499671034785.jpg");
//        adapter.addItem("korea1", "english2", "price3", "http://115.71.239.151/foodimage/tmp_1499671034785.jpg");
//        adapter.addItem("korea", "english", "price", "http://115.71.239.151/foodimage/tmp_1499671034785.jpg");
//        adapter.addItem("korea1", "english2", "price3", "http://115.71.239.151/foodimage/tmp_1499671034785.jpg");
//        adapter.addItem("korea1", "english2", "price3", "http://115.71.239.151/foodimage/tmp_1499671034785.jpg");
//        adapter.addItem("korea", "english", "price", "http://115.71.239.151/foodimage/tmp_1499671034785.jpg");
//        adapter.addItem("korea1", "english2", "price3", "http://115.71.239.151/foodimage/tmp_1499671034785.jpg");
//        adapter.addItem("korea1", "english2", "price3", "http://115.71.239.151/foodimage/tmp_1499671034785.jpg");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        ListViewItem_Menu item =(ListViewItem_Menu) l.getItemAtPosition(position);

        String KoreaName=item.getKoreaName();
        String EnglishName=item.getEnglishName();
        String Price=item.getPrice();

        super.onListItemClick(l, v, position, id);
    }

    public void addItem(String KoreaName, String EnglishName, String Price, String ImagePath) {
        adapter.addItem(KoreaName, EnglishName, Price, ImagePath);
    }
}
