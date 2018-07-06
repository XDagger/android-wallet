package io.xdag.xdagwallet.fragment;

import android.os.Handler;
import io.xdag.common.base.BaseFragment;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class SettingFragment extends BaseFragment {
    private Handler mXdagMessageHandler;

    public void setMessagehandler(Handler xdagMessageHandler){
        this.mXdagMessageHandler = xdagMessageHandler;
    }
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_setting;
    }

}
