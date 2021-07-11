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



import butterknife.BindView;
import butterknife.OnClick;

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



import java.util.List;



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
                mDisposable.add(createWalletInteract.load(password,mContext).subscribe(this::showAddress_Balance, this::showError));
            }else {
                Log.i(TAG,"input password canceled");
                mInputPwdDialogFragment.dismiss();
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


    private void loadBalance(String address) {
        getBalance(address);
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

    private void getBalance(String address){
        mDisposable.add(RpcManager.get().getBalance(address).subscribe(this::setmTvBalance,new WebErrorConsumer()));
    }
    public void setmTvBalance(String balance){
        //mTvBalance.setText(balance);
        mCollapsingToolbarLayout.setTitle(balance);
    }


}
