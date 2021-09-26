package io.xdag.xdagwallet.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import io.xdag.xdagwallet.R;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class VerifyBackupSelectedMnemonicWordsAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public VerifyBackupSelectedMnemonicWordsAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String string) {
        helper.setText(R.id.tv_mnemonic_selected_word, string);
    }

}
