package io.xdag.xdagwallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import butterknife.BindView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.ActivityStack;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.fragment.BaseMainFragment;
import io.xdag.xdagwallet.fragment.HomeFragment;
import io.xdag.xdagwallet.fragment.MoreFragment;
import io.xdag.xdagwallet.fragment.ReceiveFragment;
import io.xdag.xdagwallet.fragment.SendFragment;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.ToolbarUtil;
import io.xdag.xdagwallet.widget.BottomBar;
import io.xdag.xdagwallet.widget.BottomBarItem;
import io.xdag.xdagwallet.wrapper.XdagEvent;
import io.xdag.xdagwallet.wrapper.XdagEventManager;
import io.xdag.xdagwallet.wrapper.XdagHandlerWrapper;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * created by ssyijiu  on 2018/5/22
 * <p>
 * desc : The home activity
 */

public class MainActivity extends ToolbarActivity {

    private static final String EXTRA_RESTORE = "extra_restore";
    private static final String EXTRA_SWITCH_POOL = "extra_switch_pool";
    @BindView(R.id.bottom_navigation)
    BottomBar mBottomBar;

    private FragmentManager mFragmentManager;
    private HomeFragment mHomeFragment;
    private ReceiveFragment mReceiveFragment;
    private SendFragment mSendFragment;
    private MoreFragment mSettingFragment;
    public BaseMainFragment mShowFragment;

    private boolean mRestore;
    private XdagEventManager mXdagEventManager;


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.getInstance().finishNotTopActivities();
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }


    @Override
    protected boolean enableEventBus() {
        return true;
    }


    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            addFragment();
        } else {
            recoverFragment();
        }
        initBottomBar();
    }


    @Override
    protected void parseIntent(Intent intent) {
        super.parseIntent(intent);
        mRestore = intent.getBooleanExtra(EXTRA_RESTORE, false);
    }


    @Override
    protected void initData() {
        mXdagEventManager = XdagEventManager.getInstance(this);
        mXdagEventManager.initDialog();

        // request permissions
        AndPermission.with(mContext)
            .runtime()
            .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
            .onGranted(data -> connectToPool())
            .start();
    }


    private void connectToPool() {
        if (mRestore) {
            if (getXdagHandler().restoreWallet()) {
                getXdagHandler().connectToPool(Config.getPoolAddress());
            } else {
                AlertUtil.show(mContext, R.string.error_restore_xdag_wallet);
            }

        } else {
            if (getXdagHandler().createWallet()) {
                getXdagHandler().connectToPool(Config.getPoolAddress());
            } else {
                AlertUtil.show(mContext, R.string.error_create_xdag_wallet);
            }
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean switchPool = intent.getBooleanExtra(EXTRA_SWITCH_POOL, false);
        if (switchPool) {
            XdagHandlerWrapper.getInstance(this).disconnectPool();
            mHomeFragment.showNotReady();
            showFragment(mHomeFragment);
            mBottomBar.setCurrentItem(mHomeFragment.getPosition());
        }
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().exit();
    }


    /**
     * the event from c
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ProcessXdagEvent(XdagEvent event) {
        mXdagEventManager.manageEvent(event);
    }


    private void initBottomBar() {

        mBottomBar.addItem(
            new BottomBarItem(mContext, R.mipmap.ic_home, R.mipmap.ic_home_unselected,
                getString(R.string.home)));
        mBottomBar.addItem(
            new BottomBarItem(mContext, R.mipmap.ic_receive, R.mipmap.ic_receive_unselected,
                getString(R.string.receive)));
        mBottomBar.addItem(
            new BottomBarItem(mContext, R.mipmap.ic_send, R.mipmap.ic_send_unselected,
                getString(R.string.send)));
        mBottomBar.addItem(
            new BottomBarItem(mContext, R.mipmap.ic_more, R.mipmap.ic_more_unselected,
                getString(R.string.settings)));

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override public void onTabSelected(int position, int prePosition) {
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
            }


            @Override public void onTabUnselected(int position) { }


            @Override public void onTabReselected(int position) { }
        });
    }


    private void addFragment() {
        mHomeFragment = HomeFragment.newInstance();
        mReceiveFragment = ReceiveFragment.newInstance();
        mSendFragment = SendFragment.newInstance();
        mSettingFragment = MoreFragment.newInstance();

        addFragmentToActivity(mFragmentManager, mHomeFragment, R.id.container,
            HomeFragment.class.getName());
        addFragmentToActivity(mFragmentManager, mReceiveFragment, R.id.container,
            ReceiveFragment.class.getName());
        addFragmentToActivity(mFragmentManager, mSendFragment, R.id.container,
            SendFragment.class.getName());
        addFragmentToActivity(mFragmentManager, mSettingFragment, R.id.container,
            MoreFragment.class.getName());

        showFragment(mHomeFragment);
    }


    private void recoverFragment() {
        mHomeFragment = (HomeFragment) mFragmentManager.findFragmentByTag(
            HomeFragment.class.getName());
        mReceiveFragment = (ReceiveFragment) mFragmentManager.findFragmentByTag(
            ReceiveFragment.class.getName());
        mSendFragment = (SendFragment) mFragmentManager.findFragmentByTag(
            SendFragment.class.getName());
        mSettingFragment = (MoreFragment) mFragmentManager.findFragmentByTag(
            MoreFragment.class.getName());
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
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_RESTORE, restore);
        context.startActivity(intent);
    }


    public static void switchPool(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_SWITCH_POOL, true);
        context.startActivity(intent);
    }
}
