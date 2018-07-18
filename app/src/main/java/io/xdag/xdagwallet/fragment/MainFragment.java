package io.xdag.xdagwallet.fragment;

import io.xdag.common.base.BaseFragment;
import io.xdag.xdagwallet.MainActivity;

/**
 * created by lxm on 2018/7/18.
 *
 * link {@link MainActivity} and {@link HomeFragment}、{@link SendFragment}、
 * {@link ReceiveFragment}、{@link SettingFragment}
 */
public abstract class MainFragment extends BaseFragment {

    private RuntimeException mAttachException = new RuntimeException(
        "MainFragment must attach to MainActivity");


    public MainActivity getContext() {
        if (mContext instanceof MainActivity) {
            return (MainActivity) mContext;
        } else {
            throw mAttachException;
        }
    }
}
