package thread.seopftware.mychef.Chatting;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

import thread.seopftware.mychef.R;

/**
 * Created by MSI on 2017-08-16.
 */

// 리스트뷰 아이템의 체크 여부를 확인하기 위해 ViewGroup 생성
public class CheckableLinearLayout extends LinearLayout implements Checkable {

    public  CheckableLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    // 현재의 체크 상태를 리턴
    @Override
    public boolean isChecked() { // 아이템 클릭 시 체크박스에도 클릭 효과 주기
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        return checkBox.isChecked();
    }

    // 현재의 체크 상태를 바꿈. ( UI 반영 )
    @Override
    public void toggle() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        setChecked(checkBox.isChecked() ? false : true);
    }

    // 현재의 체크 상태를 checked 변수 대로 설정
    @Override
    public void setChecked(boolean checked) {
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

        if(checkBox.isChecked()!=checked) {  // 현 체크박스 상태와 입력된 체크박스 상태가 다르다면 맞춰주기
            checkBox.setChecked(checked);
        }
    }


}
