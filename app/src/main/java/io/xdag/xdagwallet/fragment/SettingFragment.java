package io.xdag.xdagwallet.fragment;

import android.view.View;
import io.xdag.common.base.BaseFragment;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/24.
 *
 * desc :
 */
public class SettingFragment extends BaseFragment {

    @Override protected int getLayoutResId() {
        return R.layout.fragment_setting;
    }


    @Override protected void initView(View rootView) {

    }


    public static SettingFragment newInstance() {
        return new SettingFragment();
    }
}
