package thread.seopftware.mychef.HomeChef;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import thread.seopftware.mychef.R;

public class Fragment5_Setting extends Fragment {

    private static final String TAG = "Fragment5_Setting";
    public static final String NOTIFY_MESSAGE = "notificaton_message";
    Switch mSwitch;

    // 생성자
    public Fragment5_Setting() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.activity_fragment5__setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("MyChef_Setting");

        mSwitch = (Switch) view.findViewById(R.id.switch2);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Switch 버튼 상태 체크 : " + isChecked);

                if (isChecked == true) { // 만약 버튼의 상태가 true 일 때 (thumb가 오른쪽에 있을 때)

                    SharedPreferences pref = getContext().getSharedPreferences(NOTIFY_MESSAGE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("status", isChecked);
                    editor.commit();

                } else { // 만약 버튼의 상태가 false 일 때 (thumb가 왼쪽에 있을 때)

                    SharedPreferences pref = getContext().getSharedPreferences(NOTIFY_MESSAGE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("status", isChecked);
                    editor.commit();

                }
            }
        });


    }

}


/*

seekBar_message.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

// 드래그 중에 발생하는 함수
@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "onProgressChanged SeekBar : " + seekBar);
        Log.d(TAG, "onProgressChanged progress : " + progress); // 프로그레스바의 진행 정도를 나타냄

        }

// 최초 드래그 시 발생하는 함수
@Override
public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStartTrackingTouch SeekBar : " + seekBar);

        }

// 드래그를 멈출 때 발생하는 함수 (시크바에서 손을 땠을 때)
@Override
public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStopTrackingTouch : " + seekBar);

        }
        });*/
