package thread.seopftware.mychef.etc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import thread.seopftware.mychef.R;

/**
 * Created by MSI on 2017-07-14.
 */

public class Fragment_example extends Fragment {

    // 생성자
    public Fragment_example() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_fragment3__profile, container, false);
        getActivity().setTitle("MyChef_Call");


        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
