package thread.seopftware.mychef.HomeChef;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import thread.seopftware.mychef.Chatting.Chat_Service;
import thread.seopftware.mychef.Chatting.Viewpager2_ChatList;
import thread.seopftware.mychef.Chatting.Viewpager_FriendList;
import thread.seopftware.mychef.R;

import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.internal.zzt.TAG;
import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLEMAIL;
import static thread.seopftware.mychef.Login.Login_login.CHEFNORMALLOGIN;
import static thread.seopftware.mychef.Login.Login_login.FACEBOOKLOGIN;
import static thread.seopftware.mychef.Login.Login_login.FBAPI;
import static thread.seopftware.mychef.Login.Login_login.FBEMAIL;
import static thread.seopftware.mychef.Login.Login_login.FB_LOGINCHECK;
import static thread.seopftware.mychef.Login.Login_login.KAAPI;
import static thread.seopftware.mychef.Login.Login_login.KAEMAIL;
import static thread.seopftware.mychef.Login.Login_login.KAKAOLOGIN;
import static thread.seopftware.mychef.Login.Login_login.KAKAO_LOGINCHECK;

/**
 * Created by MSI on 2017-07-11.
 */

public class Fragment2_Chat extends Fragment {

    ViewPager viewPager;
    PagerSlidingTabStrip tabs;

    // 생성자
    public Fragment2_Chat() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("MyChef_Chat");
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.activity_fragment2__chat, container, false);

        viewPager= (ViewPager) rootview.findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0);


        tabs= (PagerSlidingTabStrip) rootview.findViewById(R.id.tabs);
        tabs.setShouldExpand(true);
        tabs.setViewPager(viewPager);

        return rootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        String ChefEmail;

        SharedPreferences pref1 = getActivity().getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
        KAKAO_LOGINCHECK=pref1.getString(KAAPI, "0");

        SharedPreferences pref2 = getActivity().getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
        FB_LOGINCHECK=pref2.getString(FBAPI, "0");

        Log.d(TAG, "KAKAO API :"+KAKAO_LOGINCHECK);
        Log.d(TAG, "FB API :"+FB_LOGINCHECK);

        if(!FB_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getActivity().getSharedPreferences(FACEBOOKLOGIN, MODE_PRIVATE);
            ChefEmail=pref.getString(FBEMAIL, "");
            Log.d(TAG, "FB chefemail: "+ChefEmail);
        } else if(!KAKAO_LOGINCHECK.equals("0")) {
            SharedPreferences pref = getActivity().getSharedPreferences(KAKAOLOGIN, MODE_PRIVATE);
            ChefEmail=pref.getString(KAEMAIL, "");
            Log.d(TAG, "KA chefemail: "+ChefEmail);
        } else { // 일반
            SharedPreferences pref = getActivity().getSharedPreferences(CHEFNORMALLOGIN, MODE_PRIVATE);
            ChefEmail=pref.getString(CHEFNORMALLEMAIL, "");
            Log.d(TAG, "Normal chefemail: "+ChefEmail);
        }

        Log.d(TAG, "Chef Email: "+ChefEmail);

        try{

        Log.d(TAG, "**************************************************");
        Log.d(TAG, "Fragement2_Chat : 이메일을 서버로 보내 접속자 리스트에 추가한다.");
        Log.d(TAG, "**************************************************");

            // 메세지를 서비스로 보내는 곳
            JSONObject object = new JSONObject();
            object.put("room_status", "10");
            object.put("email_sender", ChefEmail);
            String Object_Data = object.toString();

/*            Toast.makeText(getContext(), "소켓 서비스 시작", Toast.LENGTH_SHORT).show();*/
            Intent intent1=new Intent(getContext(), Chat_Service.class);
            intent1.putExtra("command", Object_Data);
            Log.d(TAG, "이메일을 서버에 보낸다.");
            getContext().startService(intent1);

        } catch (JSONException e){
            e.printStackTrace();
        }

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
