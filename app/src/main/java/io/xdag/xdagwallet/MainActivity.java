package io.xdag.xdagwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.xdagwallet.fragment.HomeFragment;
import io.xdag.xdagwallet.fragment.ReceiveFragment;
import io.xdag.xdagwallet.fragment.SendFragment;
import io.xdag.xdagwallet.fragment.SettingFragment;

/**
 * created by ssyijiu  on 2018/5/22
 * <p>
 * desc : The home activity
 */

public class MainActivity extends ToolbarActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.navigation)
    BottomNavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private HomeFragment mHomeFragment;
    private ReceiveFragment mReceiveFragment;
    private SendFragment mSendFragment;
    private SettingFragment mSettingFragment;
    private Fragment mShowFragment;

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
        mNavigationView.setOnNavigationItemSelectedListener(this);
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
    public void onBackPressed() {
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(launcherIntent);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                showFragment(mHomeFragment);
                break;
            case R.id.action_receive:
                showFragment(mReceiveFragment);
                break;
            case R.id.action_send:
                showFragment(mSendFragment);
                break;
            case R.id.action_setting:
                showFragment(mSettingFragment);
                break;
            default:
        }
        return true;
    }


    @Override
    protected int getToolbarTitle() {
        return R.string.app_name;
    }
}
