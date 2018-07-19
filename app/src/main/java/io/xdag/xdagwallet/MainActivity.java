package io.xdag.xdagwallet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import io.xdag.common.Common;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.common.util.SDCardUtil;
import io.xdag.xdagwallet.fragment.HomeFragment;
import io.xdag.xdagwallet.fragment.ReceiveFragment;
import io.xdag.xdagwallet.fragment.SendFragment;
import io.xdag.xdagwallet.fragment.SettingFragment;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.ToolbarUtil;
import io.xdag.xdagwallet.wrapper.XdagHandlerWrapper;
import java.io.File;
import java.util.List;

/**
 * created by ssyijiu  on 2018/5/22
 * <p>
 * desc : The home activity
 */

public class MainActivity extends ToolbarActivity {

    private static final String XDAG_FILE = "xdag";
    @BindView(R.id.bottom_navigation)
    AHBottomNavigation mNavigationView;
    private FragmentManager mFragmentManager;
    private HomeFragment mHomeFragment;
    private ReceiveFragment mReceiveFragment;
    private SendFragment mSendFragment;
    private SettingFragment mSettingFragment;
    private Fragment mShowFragment;

    private XdagHandlerWrapper mHandlerWrapper;


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


    /**
     * 1、{@link #initPermissions()}
     * 2、{@link #initXdagFile()}
     * 3、{@link #getXdagHandler()} and connectToPool
     */
    @Override
    protected void initData() {
        initPermissions();
    }


    /**
     * request permissions,
     * if permissions granted call {@link #initXdagFile()}
     */
    private void initPermissions() {

        AndPermission.with(mContext)
            .runtime()
            .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
            .onGranted(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    initXdagFile();
                }
            })
            .start();
    }


    /**
     * create xdag file: sdcard/xdag/
     * if success connectToPool
     */
    private void initXdagFile() {
        if (SDCardUtil.isAvailable()) {
            File file = new File(SDCardUtil.getSDCardPath(), XDAG_FILE);
            if (!file.exists() && !file.mkdirs()) {
                AlertUtil.show(mContext, R.string.error_file_make_fail);
            } else {
                getXdagHandler().connectToPool(Config.POLL_ADDRESS);
            }
        } else {
            AlertUtil.show(mContext, R.string.error_sdcard_not_available);
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
            @Override public boolean onTabSelected(int position, boolean wasSelected) {
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
                ToolbarUtil.setToolbar(position, getToolbar());
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


    private void showFragment(Fragment fragment) {
        if (mShowFragment != fragment) {
            mFragmentManager.beginTransaction()
                .hide(mHomeFragment)
                .hide(mReceiveFragment)
                .hide(mSendFragment)
                .hide(mSettingFragment)
                .show(fragment)
                .commit();
            mShowFragment = fragment;
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
        if (mHandlerWrapper == null) {
            mHandlerWrapper = new XdagHandlerWrapper(MainActivity.this);
        }
        return mHandlerWrapper;
    }
}
