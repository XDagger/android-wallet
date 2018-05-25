package io.xdag.xdagwallet.fragment;

import android.support.v7.widget.Toolbar;
import android.view.View;
import io.xdag.common.base.BaseFragment;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class SettingFragment extends BaseFragment {

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_setting;
    }


    @Override
    protected void initView(View rootView) {

    }


    @Override public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Toolbar toolbar = ((ToolbarActivity) mContext).mToolbar;
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitle(R.string.setting);

        }
    }
}
