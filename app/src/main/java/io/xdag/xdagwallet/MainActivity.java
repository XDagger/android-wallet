package io.xdag.xdagwallet;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import io.xdag.common.base.ToolbarActivity;
import io.xdag.common.tool.ToolbarMode;
import io.xdag.common.util.ClipBoardUtil;
import io.xdag.xdagwallet.fragment.HomeFragment;
import io.xdag.xdagwallet.fragment.ReceiveFragment;
import io.xdag.xdagwallet.fragment.SendFragment;
import io.xdag.xdagwallet.fragment.SettingFragment;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.ToolbarUtil;
import io.xdag.xdagwallet.wrapper.XdagWrapper;
import java.io.File;
import java.util.List;

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
    private HandlerThread mXdagProcessThread;
    private Handler mXdagMessageHandler;

    private static final String TAG = "XdagWallet";
    private static final int PERMISSION_REQUESTCODE = 1;

    private static final int MSG_CONNECT_TO_POOL = 1;
    private static final int MSG_DISCONNECT_FROM_POOL = 2;
    private static final int MSG_XFER_XDAG_COIN = 3;


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
        initPermissions();
    }


    private void initXdagFiles() {
        String xdagFolderPath = "/sdcard/xdag";
        File file = new File(xdagFolderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * request permissions
     */
    private void initPermissions() {

        AndPermission.with(mContext)
            .runtime()
            .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
            .onGranted(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {

                    initXdagFiles();
                    initData();
                }
            })
            .start();
    }


    private void initData() {

        mXdagProcessThread = new HandlerThread("XdagProcessThread");
        mXdagProcessThread.start();
        mXdagMessageHandler = new Handler(mXdagProcessThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.arg1) {
                    case MSG_CONNECT_TO_POOL: {
                        Log.i(TAG, "receive msg connect to the pool thread id " +
                            Thread.currentThread().getId());
                        Bundle data = msg.getData();
                        String poolAddr = data.getString("pool");
                        XdagWrapper xdagWrapper = XdagWrapper.getInstance();
                        xdagWrapper.XdagConnectToPool(poolAddr);
                    }
                    break;
                    case MSG_DISCONNECT_FROM_POOL: {
                        XdagWrapper xdagWrapper = XdagWrapper.getInstance();
                        xdagWrapper.XdagDisConnectFromPool();
                    }
                    break;
                    case MSG_XFER_XDAG_COIN: {
                        Log.i(TAG,
                            "receive msg xfer coin thread id " + Thread.currentThread().getId());
                        Bundle data = msg.getData();
                        String address = data.getString("address");
                        String amount = data.getString("amount");
                        XdagWrapper xdagWrapper = XdagWrapper.getInstance();
                        xdagWrapper.XdagXferToAddress(address, amount);
                    }
                    break;
                    default: {
                        Log.e(TAG, "unkown command from ui");
                    }
                    break;
                }
            }
        };
        //connect to pool
        String poolAddr = "xdagmine.com:13654";
        Message msg = Message.obtain();
        Bundle data = new Bundle();
        data.putString("pool", poolAddr);
        msg.arg1 = MSG_CONNECT_TO_POOL;
        msg.setData(data);
        mXdagMessageHandler.sendMessage(msg);

        mHomeFragment.setMessagehandler(mXdagMessageHandler);
        mReceiveFragment.setMessagehandler(mXdagMessageHandler);
        mSendFragment.setMessagehandler(mXdagMessageHandler);
        mSettingFragment.setMessagehandler(mXdagMessageHandler);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                showFragment(mHomeFragment);
                break;
            case R.id.navigation_receive:
                showFragment(mReceiveFragment);
                break;
            case R.id.navigation_send:
                showFragment(mSendFragment);
                break;
            case R.id.navigation_setting:
                showFragment(mSettingFragment);
                break;
            default:
        }
        ToolbarUtil.setToolbar(item.getItemId(), getToolbar());
        return true;
    }


    @Override
    protected int getToolbarMode() {
        return ToolbarMode.MODE_NONE;
    }


    @Override
    protected int getToolbarTitle() {
        return R.string.app_name;
    }
}
