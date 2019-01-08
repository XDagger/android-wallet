package io.xdag.xdagwallet.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import io.xdag.common.base.AlertBuilder;
import io.xdag.xdagwallet.R;

/**
 * created by ssyijiu  on 2018/7/25
 */
public class LoadingBuilder extends AlertBuilder {

    private TextView mTvMessage;

    public LoadingBuilder(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
        View view = View.inflate(getContext(), R.layout.layout_dialog_loading, null);
        setView(view);
        mTvMessage = view.findViewById(R.id.dialog_loading_tv);
    }

    @Override
    public LoadingBuilder setMessage(int messageId) {
        mTvMessage.setText(messageId);
        return this;
    }
}
