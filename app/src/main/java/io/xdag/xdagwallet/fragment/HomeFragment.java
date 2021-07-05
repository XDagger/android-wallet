package io.xdag.xdagwallet.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.web3j.protocol.http.HttpService;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.xdag.common.tool.AppBarStateChangedListener;
import io.xdag.common.tool.MLog;
import io.xdag.common.util.ToastUtil;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.adapter.TransactionAdapter;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.config.XdagConfig;
import io.xdag.xdagwallet.core.Block;
import io.xdag.xdagwallet.core.BlockBuilder;
import io.xdag.xdagwallet.crypto.ECKeyPair;
import io.xdag.xdagwallet.crypto.Keys;
import io.xdag.xdagwallet.crypto.MnemonicUtils;
import io.xdag.xdagwallet.crypto.SecureRandomUtils;
import io.xdag.xdagwallet.crypto.SimpleEncoder;
import io.xdag.xdagwallet.dialog.InputPwdDialogFragment;
import io.xdag.xdagwallet.dialog.SetPwdDialogFragment;
import io.xdag.xdagwallet.model.BlockDetailModel;
import io.xdag.xdagwallet.net.HttpRequest;
import io.xdag.xdagwallet.rpc.RpcManager;
import io.xdag.xdagwallet.rpc.Web3XdagFactory;
import io.xdag.xdagwallet.rpc.WebXdag;
import io.xdag.xdagwallet.rpc.error.WebErrorConsumer;
import io.xdag.xdagwallet.rpc.response.XdagBalance;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.BytesUtils;
import io.xdag.xdagwallet.util.CopyUtil;
import io.xdag.xdagwallet.util.RxUtil;
import io.xdag.xdagwallet.util.UpdateUtil;
import io.xdag.xdagwallet.util.XdagTime;
import io.xdag.xdagwallet.wallet.CreateWalletInteract;
import io.xdag.xdagwallet.wallet.Wallet;
import io.xdag.xdagwallet.widget.EmptyView;
import io.xdag.xdagwallet.wrapper.XdagEventManager;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static io.xdag.xdagwallet.util.BasicUtils.hash2Address;

/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class HomeFragment extends BaseMainFragment {

    @BindView(R.id.home_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.home_tv_address)
    TextView mTvAddress;
    @BindView(R.id.home_tv_balance)
    TextView mTvBalance;

    // update
    @BindView(R.id.version_layout)
    LinearLayout mVersionLayout;
    @BindView(R.id.version_desc)
    TextView mTvVersionDesc;
    @BindView(R.id.version_update)
    TextView mTvVersionUpdate;
    @BindView(R.id.version_close)
    TextView mTvVersionClose;
    private InputPwdDialogFragment mInputPwdDialogFragment;
    private SetPwdDialogFragment mSetPwdDialogFragment;
    private TransactionAdapter mAdapter;
    private View mEmptyView;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private XdagEventManager mXdagEventManager;
    private Wallet wallet;
    private String address = "";
    private static final String TAG = "HomeFragment";
    private CreateWalletInteract createWalletInteract;
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvAddress.setText(R.string.not_ready);
        mCollapsingToolbarLayout.setTitle(getString(R.string.not_ready));
        mRecyclerView.setHasFixedSize(true);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangedListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                getRefreshDelegate().setRefreshEnabled(
                    state.equals(AppBarStateChangedListener.State.EXPANDED));
            }
        });
        if (mEmptyView == null) {
            mEmptyView = new EmptyView(mContext);
            mEmptyView.setOnClickListener(v -> requestTransaction());
        }
        if (mAdapter == null) {
            mAdapter = new TransactionAdapter(null);
            mAdapter.setEmptyView(mEmptyView);
        }
        mRecyclerView.setAdapter(mAdapter);
        //rpcManager.getXdagBalanceAsyncTask().execute(mContext,mTvBalance,"0xfc23b61db3a1dce083e82da5a2ccfd91a2211f7c");
        mXdagEventManager = XdagEventManager.getInstance((MainActivity) mContext);
        mXdagEventManager.initDialog();
        createWalletInteract = new CreateWalletInteract();
        if(XdagConfig.getInstance().getWallet()==null&&loadWallet().exists()){
            showInputPwdDialog(getResources().getString(R.string.please_input_password));
        }
        else{
            mTvAddress.setText(XdagConfig.getInstance().getAddress());
            loadBalance(XdagConfig.getInstance().getAddress());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== Config.REQUEST_CODE_INPUT_PWD_LOGIN){
            if(resultCode == Activity.RESULT_OK){
                String password= data.getStringExtra(InputPwdDialogFragment.PASSWORD);
                Log.i(TAG,"input password is " + password);
                mInputPwdDialogFragment.dismiss();
//                wallet = loadAndUnlockWallet(password);
//                walletStart();
                createWalletInteract.load(password,mContext).subscribe(this::showAddress_Balance, this::showError);

            }else {
                Log.i(TAG,"input password canceled");
                mInputPwdDialogFragment.dismiss();
                return;
            }
        }
        else if(requestCode == Config.REQUEST_CODE_SET_PWD){
            if(resultCode == Activity.RESULT_OK){
                String password1 = data.getStringExtra(SetPwdDialogFragment.PASSWORD1);
                String password2 = data.getStringExtra(SetPwdDialogFragment.PASSWORD2);
                if(password1.equals(password2)){
                    mSetPwdDialogFragment.dismiss();
                    //TODO:弹出加载框
                    wallet = createNewWallet(password1);
                    walletStart();
                }else{
                    //TODO:提示UI两次密码不一致
                    mSetPwdDialogFragment.dismiss();
                    Log.e(TAG,"password not the same");
                    showSetPwdDialog(getResources().getString(R.string.error_password_not_same));
                    return;
                }
            }else{
                Log.w(TAG,"illegal result code from set password dialog" + resultCode);
                return;
            }
        }
        else{
            Log.w(TAG,"unknow request code from dialog" + requestCode);
            return;
        }
        return;
    }
    private void showAddress_Balance(Wallet wallet){
        mTvAddress.setText(XdagConfig.getInstance().getAddress());
        loadBalance(XdagConfig.getInstance().getAddress());
    }
    private void showError(Throwable errorInfo){
        showInputPwdDialog(getResources().getString(R.string.error_password));
    }

    private void showInputPwdDialog(String title){
        //input password to unlock wallet
        if(mInputPwdDialogFragment == null){
            mInputPwdDialogFragment = new InputPwdDialogFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        mInputPwdDialogFragment.setArguments(bundle);
        mInputPwdDialogFragment.setTargetFragment(HomeFragment.this,2);
        mInputPwdDialogFragment.show(getFragmentManager(),"input password");
    }


    private void showSetPwdDialog(String title){
        //set password to create wallet
        if(mSetPwdDialogFragment == null){
            mSetPwdDialogFragment = new SetPwdDialogFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        mSetPwdDialogFragment.setArguments(bundle);
        mSetPwdDialogFragment.setTargetFragment(HomeFragment.this,Config.REQUEST_CODE_SET_PWD);
        mSetPwdDialogFragment.show(getFragmentManager(),"set password");
    }


    private void walletStart(){
        XdagConfig.getInstance().setWallet(wallet);
        if (!wallet.isHdWalletInitialized()) {
            initializedHdSeed(wallet, System.out);
        }
        List<ECKeyPair> accounts = wallet.getAccounts();
        if (accounts.isEmpty()) {
            ECKeyPair key = wallet.addAccountWithNextHdKey();
            wallet.flush();
            System.out.println("New Address:" + BytesUtils.toHexString(Keys.toBytesAddress(key)));
            Log.i("Wallet","New WalletAddress:" + BytesUtils.toHexString(Keys.toBytesAddress(key)));
        }
        Log.i("Wallet","New WalletAddress:" + BytesUtils.toHexString(Keys.toBytesAddress( wallet.getAccount(0) )));
        loadAddress();
        mTvAddress.setText(address);
        //loadBalance();
        loadBalance(XdagConfig.getInstance().getAddress());
    }


    private void loadBalance(String address) {
        getBalance(address);
    }


    private void loadAddress() {
        try {
            File file = new File(mContext.getFilesDir(),"xdag/address.dat");
            byte[] address = FileUtils.readFileToByteArray(file);
            byte[] adr = new byte[32];
            System.arraycopy(address,0,adr,0,32);
            this.address = hash2Address(adr);
            Config.setAddress(this.address);
        } catch (IOException e) {
            ToastUtil.show("加载地址失败");
            e.printStackTrace();
        }
    }


    @Override
    protected void initData() {
        super.initData();
        requestUpdate();
        //requestTransaction();
    }


    private void requestUpdate() {
        mDisposable.add(
            HttpRequest.get().getVersionInfo(versionModel -> {
                MLog.i(versionModel);
                UpdateUtil.update(versionModel, mVersionLayout,
                    mTvVersionDesc, mTvVersionUpdate, mTvVersionClose);
            })
        );
    }


    private void requestTransaction() {
        mDisposable.add(HttpRequest.get()
            .getTransactions(mContext,"fY3P4RZuhnwz+JACZup/CoK6Yl6yEwGn", this::showTransaction));//mTvAddress.getText().toString()
    }


    private void showTransaction(List<BlockDetailModel.BlockAsAddress> blockAsAddresses) {
        mAdapter.setNewData(blockAsAddresses);
        AlertUtil.show(mContext, R.string.success_refresh);
    }


    public void showNotReady() {
        mTvAddress.setText(R.string.not_ready);
        mCollapsingToolbarLayout.setTitle(getString(R.string.not_ready));
        mAdapter.setNewData(null);
    }


    @OnClick(R.id.home_tv_address)
    void copyAddress() {
        CopyUtil.copyAddress(mContext, mTvAddress.getText().toString());
    }


    @Override
    public void onDestroy() {
        RxUtil.dispose(mDisposable);
        super.onDestroy();
    }


    @Override
    public void onRefresh() {
        super.onRefresh();
        requestTransaction();
        loadBalance(XdagConfig.getInstance().getAddress());
    }


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public int getPosition() {
        return 0;
    }

    public Wallet loadWallet() {
        return new Wallet();
    }

    public Wallet loadAndUnlockWallet(String password) {
        Wallet wallet = loadWallet();
        if (!wallet.unlock(password)) {
            System.err.println("Invalid password");
        }

        return wallet;
    }

    public Wallet createNewWallet(String newPassword) {
        if (newPassword == null) {
            return null;
        }
        //setPassword(newPassword);
        Wallet wallet = loadWallet();

        if (!wallet.unlock(newPassword) || !wallet.flush()) {
            ToastUtil.show("Create New WalletError");
            return null;
        }

        return wallet;
    }

    public boolean initializedHdSeed(Wallet wallet, PrintStream printer) {
        if (wallet.isUnlocked() && !wallet.isHdWalletInitialized()) {
            // HD Mnemonic
            printer.println("HdWallet Initializing...");
            byte[] initialEntropy = new byte[16];
            SecureRandomUtils.secureRandom().nextBytes(initialEntropy);
            String phrase = MnemonicUtils.generateMnemonic(initialEntropy);
            printer.println("HdWallet Mnemonic:"+ phrase);

            wallet.initializeHdWallet(phrase);
            wallet.flush();
            printer.println("HdWallet Initialized Successfully!");
            return true;
        }
        return false;
    }

    private Disposable getBalance(String address){
        return Observable.just(address)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(@NonNull String address) throws Exception {
                        WebXdag web = Web3XdagFactory.build(new HttpService(Config.POOL_TEST));
                        XdagBalance w = web.xdagGetBalance(address).sendAsync().get();
                        Log.i("余额:",w.getBalance());
                        return w.getBalance();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setmTvBalance,new WebErrorConsumer());
    }

    public void setmTvBalance(String balance){
        //mTvBalance.setText(balance);
        mCollapsingToolbarLayout.setTitle(balance);
    }


}
