package thread.seopftware.mychef.HomeUser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import thread.seopftware.mychef.R;

public class Fragment4_User_Setting extends Fragment {

    TextView mTextView1;

    // 생성자
    public Fragment4_User_Setting() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("MyChef_Setting");
//        View view = inflater.inflate( R.layout.activity_fragment4_user_setting, container, false );
//        mTextView1 = (TextView)view.findViewById( R.id.textView);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_fragment4_user_setting, container, false);

//        Button btn = (Button) rootView.findViewById(R.id.button6);
//        btn.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                Home_user mainActivity = (Home_user) getActivity();
//                mainActivity.onFragmentChanged(0);
//            }
//        });

//        Bundle bundle = new Bundle();
//        bundle.putString("key","abc"); // Put anything what you want
//
//        Fragment_User_Search fragment2 = new Fragment_User_Search();
//        fragment2.setArguments(bundle);
//
//        getFragmentManager()
//                .beginTransaction()
//                .replace(R.id.usercontainer, fragment2)
//                .commit();

        return rootView;

    }

    public void setText( String text ) {
        mTextView1.setText( text );
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    
}
