package io.xdag.xdagwallet.fragment;

import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import io.xdag.common.base.BaseFragment;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;

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
        ((MainActivity) mContext).copyText(mTvAddress.getText().toString());
    }


    public static ReceiveFragment newInstance() {
        return new ReceiveFragment();
    }

}
