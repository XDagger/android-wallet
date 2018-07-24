package io.xdag.xdagwallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import io.xdag.common.base.RefreshFragment;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.wrapper.XdagHandlerWrapper;
import java.util.Objects;
import org.greenrobot.eventbus.EventBus;

/**
 * created by lxm on 2018/7/18.
 *
 * link {@link MainActivity} and {@link HomeFragment}、{@link SendFragment}、
 * {@link ReceiveFragment}、{@link SettingFragment}
 */
public abstract class BaseMainFragment extends RefreshFragment {

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (enableEventBus()) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }
    }


    @Override public void onDestroy() {
        super.onDestroy();
        if (enableEventBus()) {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        }
    }


    public MainActivity getMainActivity() {
        if (mContext instanceof MainActivity) {
            return (MainActivity) mContext;
        } else {
            throw new RuntimeException("BaseMainFragment must attach to MainActivity");
        }
    }


    public XdagHandlerWrapper getXdagHandler() {
        return Objects.requireNonNull(getMainActivity()).getXdagHandler();
    }


    /**
     * if is {@link HomeFragment} enabled refresh
     */
    @Override protected boolean isRefresh() {
        return this instanceof HomeFragment;
    }


    public abstract int getPosition();


    /**
     * default enable EventBus
     */
    protected boolean enableEventBus() {
        return true;
    }
}
