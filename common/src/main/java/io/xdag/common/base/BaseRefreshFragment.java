package io.xdag.common.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.xdag.common.R;

/**
 * created by lxm on 2018/5/25.
 *
 * desc :
 */
public abstract class BaseRefreshFragment extends BaseFragment {

    @Override protected int getLayoutResId() {
        return 0;
    }


    @Nullable @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_refresh, container, false);

    }


    @Override protected void beforeInitView() {
        super.beforeInitView();

    }
}
