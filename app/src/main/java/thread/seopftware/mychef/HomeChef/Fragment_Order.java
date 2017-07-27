package thread.seopftware.mychef.HomeChef;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import thread.seopftware.mychef.R;

/**
 * Created by MSI on 2017-07-11.
 */

public class Fragment_Order extends Fragment {

    ViewPager viewPager;
    PagerSlidingTabStrip tabs;

    // 생성자
    public Fragment_Order() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("MyChef_주문내역");
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_fragment1_user_order, container, false);

        viewPager= (ViewPager) rootview.findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);


        tabs= (PagerSlidingTabStrip) rootview.findViewById(R.id.tabs);
        tabs.setShouldExpand(true);
        tabs.setViewPager(viewPager);

        return rootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    // 페이지마다 보여줄 타이틀을 정해준다.
    private String[] pageTitle = {"예약 중인 출장", "완료된 출장", "취소된 출장"};
    private class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitle[position];
        }

        /**
         * View Pager의 Fragment 들은 각각 Index를 가진다. * Android OS로 부터 요청된 Pager의 Index를 보내주면, * 해당되는 Fragment를 리턴시킨다. * @param position * @return
         */
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Log.d("pos", String.valueOf(position));
                return new OrderList_chef_viewpager();
            } else if (position == 1) {
                return new OrderList_chef_viewpager2();
            } else {
                return new OrderList_chef_viewpager3();
            }
        }

        /**
         * View Pager에 몇개의 Fragment가 들어가는지 설정 * @return
         */
        @Override
        public int getCount() {
            return 3;
        }
    }
}
