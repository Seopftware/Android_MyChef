package thread.seopftware.mychef.HomeUser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import thread.seopftware.mychef.Chatting.Viewpager2_ChatList;
import thread.seopftware.mychef.Chatting.Viewpager_FriendList;
import thread.seopftware.mychef.R;

public class Fragment2_User_Chat extends ListFragment {


    ViewPager viewPager;
    PagerSlidingTabStrip tabs;
    String UserEmail;

    // 생성자
    public Fragment2_User_Chat() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("MyChef_Chat");

        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_fragment2_user_chat, container, false);

        viewPager = (ViewPager) rootview.findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0);

        tabs = (PagerSlidingTabStrip) rootview.findViewById(R.id.tabs);
        tabs.setShouldExpand(true);
        tabs.setViewPager(viewPager);

        return rootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    // 페이지마다 보여줄 타이틀을 정해준다.
    private String[] pageTitle = {"친구 목록", "채팅 목록"};
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
                return new Viewpager_FriendList();
            } else if(position == 1) {
                return new Viewpager2_ChatList();
            }
            return null;
        }

        /**
         * View Pager에 몇개의 Fragment가 들어가는지 설정 * @return
         */
        @Override
        public int getCount() {
            return 2;
        }
    }
}

