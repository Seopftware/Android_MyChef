package thread.seopftware.mychef.etc;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by MSI on 2017-06-28.
 */

public class BackPressCloseHandler {

    private long backKeyPressedTime=0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity=context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime+2000) {
            backKeyPressedTime = System.currentTimeMillis(); // System.currentTimeMillis()는 현재의 시간을 나타냄.
            showGuide();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime+2000) {
            activity.finish();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
