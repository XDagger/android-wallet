package io.xdag.xdagwallet.fragment;

import android.view.View;
import android.widget.Toast;

import io.xdag.common.base.BaseFragment;
import io.xdag.common.util.ToastUtil;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class HomeFragment extends BaseFragment {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View rootView) {

    }
}
