package io.xdag.xdagwallet.fragment;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.base.BaseFragment;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.util.ClipBoardUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.widget.AlertWrap;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class ReceiveFragment extends BaseFragment {

    @BindView(R.id.receive_tv_address) TextView mTvAddress;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_receive;
    }


    @OnClick({ R.id.receive_tv_copy, R.id.receive_tv_address }) void copyAddress() {
        ((MainActivity) mContext).copyAddress(mTvAddress.getText().toString());
    }


    public static ReceiveFragment newInstance() {
        return new ReceiveFragment();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Toolbar toolbar = ((ToolbarActivity) mContext).getToolbar();
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitle(R.string.receive_xdag);

        }
    }
}
