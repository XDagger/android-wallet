package io.xdag.xdagwallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import butterknife.BindView;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;

/**
 * created by lxm on 2018/8/10.
 *
 * Setting
 */
public class SettingActivity extends ToolbarActivity
    implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.settings_sw_usage) SwitchCompat mSwitchUsage;


    @Override protected int getLayoutResId() {
        return R.layout.activity_setting;
    }


    @Override protected void initView(View rootView, Bundle savedInstanceState) {
        mSwitchUsage.setOnCheckedChangeListener(this);
    }


    @Override protected void initData() {
        super.initData();
        mSwitchUsage.setChecked(Config.isNotDisplayUsage());

    }


    @Override protected int getToolbarTitle() {
        return R.string.setting;
    }


    public static void start(Activity context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }


    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.settings_sw_usage:
                Config.setNotDisplayUsage(isChecked);
                break;
            default:
        }
    }
}
