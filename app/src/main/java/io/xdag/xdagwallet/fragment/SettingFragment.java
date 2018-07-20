package io.xdag.xdagwallet.fragment;

import butterknife.OnClick;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.util.AlertUtil;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class SettingFragment extends BaseMainFragment {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_setting;
    }


    @OnClick(R.id.setting_backup) void setting_backup() {
        if (getXdagHandler().backupWallet()) {
            AlertUtil.show(mContext, R.string.success_backup_xdag_wallet);
        } else {
            AlertUtil.show(mContext, R.string.error_backup_xdag_wallet);
        }
    }


    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

}
