package io.xdag.xdagwallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import butterknife.BindView;
import io.xdag.common.Common;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.dialog.InputDialog;
import io.xdag.xdagwallet.dialog.LoadingDialog;
import io.xdag.xdagwallet.fragment.BaseMainFragment;
import io.xdag.xdagwallet.fragment.HomeFragment;
import io.xdag.xdagwallet.fragment.ReceiveFragment;
import io.xdag.xdagwallet.fragment.SendFragment;
import io.xdag.xdagwallet.fragment.SettingFragment;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.ToolbarUtil;
import io.xdag.xdagwallet.wrapper.XdagHandlerWrapper;

/**
 * created by ssyijiu  on 2018/5/22
 * <p>
 * desc : The home activity
 */

public class MainActivity extends ToolbarActivity {

    @BindView(R.id.bottom_navigation)
    AHBottomNavigation mNavigationView;
    private FragmentManager mFragmentManager;
    private BaseMainFragment mHomeFragment;
    private BaseMainFragment mReceiveFragment;
    private BaseMainFragment mSendFragment;
    private BaseMainFragment mSettingFragment;
    private BaseMainFragment mShowFragment;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }


    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            addFragment();
        } else {
            recoverFragment();
        }
        initNavigationView();
    }


    @Override
    protected void initData() {
        // request permissions
        AndPermission.with(mContext)
                .runtime()
                .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        connectToPool();
                    }
                })
                .start();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showFragment(mHomeFragment);
        connectToPool();

    }


    private void connectToPool() {
        if (Config.isRestore()) {
            if (getXdagHandler().restoreWallet()) {
                getXdagHandler().connectToPool(Config.POLL_ADDRESS);
            } else {
                AlertUtil.show(mContext, R.string.error_restore_xdag_wallet);
            }

        } else {
            if (getXdagHandler().createWallet()) {
                getXdagHandler().connectToPool(Config.POLL_ADDRESS);
            } else {
                AlertUtil.show(mContext, R.string.error_create_xdag_wallet);
            }
        }
    }


    private void initNavigationView() {
        // create items
        AHBottomNavigationItem home =
                new AHBottomNavigationItem(getString(R.string.home), R.drawable.ic_home);
        AHBottomNavigationItem receive =
                new AHBottomNavigationItem(getString(R.string.receive), R.drawable.ic_receive);
        AHBottomNavigationItem send =
                new AHBottomNavigationItem(getString(R.string.send), R.drawable.ic_send);
        AHBottomNavigationItem setting =
                new AHBottomNavigationItem(getString(R.string.setting), R.drawable.ic_setting);
        // add items
        mNavigationView.addItem(home);
        mNavigationView.addItem(receive);
        mNavigationView.addItem(send);
        mNavigationView.addItem(setting);
        // the selected item color
        mNavigationView.setAccentColor(Common.getColor(R.color.colorPrimary));
        // the unselected item color
        mNavigationView.setInactiveColor(Common.getColor(R.color.GERY));
        // set titles
        mNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        // set current item selected
        mNavigationView.setCurrentItem(0);
        ToolbarUtil.setToolbar(0, getToolbar());
        // set listeners
        mNavigationView.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        showFragment(mHomeFragment);
                        break;
                    case 1:
                        showFragment(mReceiveFragment);
                        break;
                    case 2:
                        showFragment(mSendFragment);
                        break;
                    case 3:
                        showFragment(mSettingFragment);
                        break;
                }
                return true;
            }
        });
    }


    private void addFragment() {
        mHomeFragment = HomeFragment.newInstance();
        mReceiveFragment = ReceiveFragment.newInstance();
        mSendFragment = SendFragment.newInstance();
        mSettingFragment = SettingFragment.newInstance();

        addFragmentToActivity(mFragmentManager, mHomeFragment, R.id.container,
                HomeFragment.class.getName());
        addFragmentToActivity(mFragmentManager, mReceiveFragment, R.id.container,
                ReceiveFragment.class.getName());
        addFragmentToActivity(mFragmentManager, mSendFragment, R.id.container,
                SendFragment.class.getName());
        addFragmentToActivity(mFragmentManager, mSettingFragment, R.id.container,
                SettingFragment.class.getName());

        showFragment(mHomeFragment);
    }


    private void recoverFragment() {
        mHomeFragment = (HomeFragment) mFragmentManager.findFragmentByTag(
                HomeFragment.class.getName());
        mReceiveFragment = (ReceiveFragment) mFragmentManager.findFragmentByTag(
                ReceiveFragment.class.getName());
        mSendFragment = (SendFragment) mFragmentManager.findFragmentByTag(
                SendFragment.class.getName());
        mSettingFragment = (SettingFragment) mFragmentManager.findFragmentByTag(
                SettingFragment.class.getName());
    }


    private void showFragment(BaseMainFragment fragment) {
        if (mShowFragment != fragment) {
            mFragmentManager.beginTransaction()
                    .hide(mHomeFragment)
                    .hide(mReceiveFragment)
                    .hide(mSendFragment)
                    .hide(mSettingFragment)
                    .show(fragment)
                    .commit();
            mShowFragment = fragment;
            ToolbarUtil.setToolbar(mShowFragment.getPosition(), getToolbar());
        }
    }


    @Override
    protected int getToolbarMode() {
        return ToolbarMode.MODE_NONE;
    }


    @Override
    protected int getToolbarTitle() {
        return R.string.app_name;
    }


    public XdagHandlerWrapper getXdagHandler() {
        return XdagHandlerWrapper.getInstance(this);
    }


    public static void start(Activity context, boolean restore) {
        Config.setRestore(restore);
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        context.finish();
    }
}
