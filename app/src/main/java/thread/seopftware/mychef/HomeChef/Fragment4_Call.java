package thread.seopftware.mychef.HomeChef;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import thread.seopftware.mychef.R;

import static android.content.Intent.ACTION_DIAL;
import static android.content.Intent.ACTION_SENDTO;
import static android.content.Intent.ACTION_VIEW;

public class Fragment4_Call extends Fragment implements View.OnClickListener {

    // 생성자
    public Fragment4_Call() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_fragment4__call, container, false);
        getActivity().setTitle("MyChef_Call");

        LinearLayout LinearCall= (LinearLayout) view.findViewById(R.id.LinearCall);
        LinearLayout LinearEmail= (LinearLayout) view.findViewById(R.id.LinearEmail);
        LinearLayout LinearHomePage= (LinearLayout) view.findViewById(R.id.LinearHomePage);

        LinearCall.setOnClickListener(this);
        LinearEmail.setOnClickListener(this);
        LinearHomePage.setOnClickListener(this);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LinearCall:
                Intent intent=new Intent(ACTION_DIAL, Uri.parse("tel:010-4167-5164")); // 전화문의
                startActivity(intent);
                break;

            case R.id.LinearEmail:
                Intent intent2=new Intent(ACTION_SENDTO, Uri.parse("mailto:inseop0813@gmail.com")); // 전화문의
                startActivity(intent2);
                break;

            case R.id.LinearHomePage:
                Intent intent3=new Intent(ACTION_VIEW, Uri.parse("http://www.MyChef.com")); // 홈페이지 문의
                startActivity(intent3);
                break;




        }
    }
}
