package thread.seopftware.mychef.Service;

import android.os.Handler;

/**
 * Created by MSI on 2017-08-09.
 */

public class ServiceThread extends  Thread {

    Handler handler;
    boolean isRun = true;

    public ServiceThread(Handler handler) {
        this.handler=handler;
    }

    public void stopForever() {
        synchronized (this){
            this.isRun = false;
        } // 동기화란 하나의 자원(데이터)에 대해서 여러 스레드가 사용하려고 할때 한 시점에서 하나의 스레드만 사용할 수 있도록 하는 것
    }

    public void run() {
        // 반복적으로 수행할 작업
        while (isRun) {
            handler.sendEmptyMessage(0); // 쓰레드에 있는 핸들러에게 메세지를 보냄

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
