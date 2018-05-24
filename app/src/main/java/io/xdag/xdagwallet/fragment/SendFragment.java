package io.xdag.xdagwallet.fragment;

import android.view.View;
import io.xdag.common.base.BaseFragment;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/24.
 *
 * desc :
 */
public class SendFragment extends BaseFragment {

    @Override protected int getLayoutResId() {
        return R.layout.fragment_send;
    }


    @Override protected void initView(View rootView) {

    }

    public static SendFragment newInstance() {
        return new SendFragment();
    }
}
