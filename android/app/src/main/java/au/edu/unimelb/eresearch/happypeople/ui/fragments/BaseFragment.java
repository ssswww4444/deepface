package au.edu.unimelb.eresearch.happypeople.ui.fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class BaseFragment extends Fragment {
    protected FragmentActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FragmentActivity){
            mActivity = (FragmentActivity) context;
        }
    }

    public FragmentActivity getFActivity() {
        if (mActivity == null) {
            mActivity = getActivity();
        }
        return mActivity;
    }
}
