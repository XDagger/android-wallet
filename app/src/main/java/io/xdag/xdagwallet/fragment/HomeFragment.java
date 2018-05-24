package io.xdag.xdagwallet.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import io.xdag.common.base.BaseFragment;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/24.
 *
 * desc :
 */
public class HomeFragment extends BaseFragment {

    @Override protected int getLayoutResId() {
        return R.layout.fragment_home;
    }


    @Override protected void initView(View rootView) {

    }


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
}
