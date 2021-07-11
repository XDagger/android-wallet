package io.xdag.xdagwallet.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.bertsir.zbar.QRManager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;


import org.web3j.protocol.http.HttpService;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.config.XdagConfig;
import io.xdag.xdagwallet.core.Address;
import io.xdag.xdagwallet.core.Block;
import io.xdag.xdagwallet.core.BlockBuilder;
import io.xdag.xdagwallet.crypto.ECKeyPair;
import io.xdag.xdagwallet.crypto.Keys;
import io.xdag.xdagwallet.dialog.InputPwdDialogFragment;
import io.xdag.xdagwallet.rpc.RpcManager;
import io.xdag.xdagwallet.rpc.TransactionList;
import io.xdag.xdagwallet.rpc.Web3XdagFactory;
import io.xdag.xdagwallet.rpc.WebXdag;
import io.xdag.xdagwallet.rpc.error.WebErrorConsumer;
import io.xdag.xdagwallet.rpc.response.TransactionState;
import io.xdag.xdagwallet.util.AlertUtil;
import io.xdag.xdagwallet.util.BasicUtils;
import io.xdag.xdagwallet.util.BytesUtils;
import io.xdag.xdagwallet.util.StringUtils;
import io.xdag.xdagwallet.util.XdagPaymentURI;
import io.xdag.xdagwallet.util.XdagTime;
import io.xdag.xdagwallet.util.ZbarUtil;
import io.xdag.xdagwallet.wallet.CreateWalletInteract;
import io.xdag.xdagwallet.wallet.Wallet;
import io.xdag.xdagwallet.wallet.WalletUtils;


/**
 * created by lxm on 2018/5/24.
 * <p>
 * desc :
 */
public class SendFragment extends BaseMainFragment implements Toolbar.OnMenuItemClickListener {

    @BindView(R.id.send_et_amount)
    EditText mEtAmount;
    @BindView(R.id.send_et_address)
    EditText mEtAddress;
    @BindView(R.id.send_et_remark)
    EditText mEtRemark;
    @BindView(R.id.send_btn_xdag)
    Button mBtnSendXdag;
    @BindView(R.id.send_tv_available)
    TextView mTvAvailable;
    @BindView(R.id.transition_current)
    TextView mTvTransaction;
    InputPwdDialogFragment mInputPwdDialogFragment;
    private String mBalance;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private static final String TAG = "SendFragment";
    private CreateWalletInteract createWalletInteract;
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_send;
    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        getToolbar().inflateMenu(R.menu.toolbar_scan);
        getToolbar().setOnMenuItemClickListener(this);
        createWalletInteract = new CreateWalletInteract();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_scan) {
            AndPermission.with(mContext)
                    .runtime()
                    .permission(Permission.READ_EXTERNAL_STORAGE, Permission.CAMERA)
                    .onGranted(data -> ZbarUtil.startScan(mContext, new QRManager.OnScanResultCallback() {
                        @Override
                        public void onScanSuccess(String result) {
                            XdagPaymentURI xdagPaymentURI = XdagPaymentURI.parse(result);
                            if(xdagPaymentURI != null) {
                                String address = xdagPaymentURI.getAddress();
                                if(!TextUtils.isEmpty(address)) {
                                    mEtAddress.setText(address);
                                }
                                Double amount = xdagPaymentURI.getAmount();
                                if(amount != null) {
                                    mEtAmount.setText(String.valueOf(amount));
                                }
                            }
                        }
                        @Override
                        public void onScanFailed() {
                            AlertUtil.show(mContext, R.string.error_cannot_identify_qr_code);
                        }
                    }))
                    .start();
        }
        return false;
    }


    @OnClick({R.id.send_btn_xdag})
    void send_btn_xdag() {
        showInputPwdDialog(getResources().getString(R.string.please_input_password));
    }

    private void showInputPwdDialog(String title){
        if(mInputPwdDialogFragment == null){
            mInputPwdDialogFragment = new InputPwdDialogFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        mInputPwdDialogFragment.setArguments(bundle);
        mInputPwdDialogFragment.setTargetFragment(SendFragment.this, Config.REQUEST_CODE_SEND_TRANSACTION);
        mInputPwdDialogFragment.show(getFragmentManager(),"input password");
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== Config.REQUEST_CODE_SEND_TRANSACTION){
            if(resultCode == Activity.RESULT_OK){
                String password= data.getStringExtra(InputPwdDialogFragment.PASSWORD);
                mInputPwdDialogFragment.dismiss();
                try{
                    createWalletInteract.send(password).subscribe(this::sendTransaction,this::showError);
                }catch (Exception e){
                    //提示UI要求重新输入密码
                    Log.e(TAG,"wallet unlock exception");
                    showInputPwdDialog(getResources().getString(R.string.error_password));
                    return;
                }
            }else {
                Log.i(TAG,"input password canceled");
                mInputPwdDialogFragment.dismiss();
            }
        }
    }

    private void showError(Throwable errorInfo){
        showInputPwdDialog(getResources().getString(R.string.error_password));
    }

    private void sendTransaction(ECKeyPair keyPair){
        new Thread(new Runnable(){
            @Override
            public void run(){
                String address = mEtAddress.getText().toString();
                String amount = mEtAmount.getText().toString();
                String remark = mEtRemark.getText().toString();
                if(address.length()==0||address==null){
                    AlertUtil.show(mContext,"地址为空");
                    return;
                }
                if(address.length()!=32){
                    AlertUtil.show(mContext,"请输入有效地址");
                    return;
                }
                XdagXferToAddress(keyPair,address,amount,remark);
                Log.i(TAG,"address: "+address+" amount: "+amount+" remark: "+remark);
            }
        }).start();
    }

    public void XdagXferToAddress(ECKeyPair keyPair,String address,String amount,String remark){
        long xdagTime = XdagTime.getCurrentTimestamp();
        Address from = new Address(BasicUtils.address2Hash(XdagConfig.getInstance().getAddress()));
        Address to  = new Address(BasicUtils.address2Hash(address));
        double amount1 = Double.parseDouble(amount);
        if(amount1<=0){
            AlertUtil.show(mContext,"请输入有效的数量");
            return;
        }
        textClear();
        long amount2 = BasicUtils.xdag2amount(amount1);
        Block block = BlockBuilder.generateTransactionBlock(keyPair,xdagTime,from,to,amount2,remark);
        block.signOut(keyPair);
        Log.i(TAG,"HashLow"+BytesUtils.toHexString(block.getHashLow()));
        sendTransaction(BytesUtils.toHexString(block.getXdagBlock().getData()));
        Log.i(TAG,"New BlockAddress:" + BytesUtils.toHexString(block.getXdagBlock().getData()));
    }

    private void textClear() {
        mEtAddress.setText("");
        mEtAmount.setText("");
        mEtRemark.setText("");
    }

    @OnClick({R.id.send_tv_available})
    void send_tv_available() {
        mEtAmount.setText(mBalance);
    }

    public static SendFragment newInstance() {
        return new SendFragment();
    }


    @Override
    public int getPosition() {
        return 2;
    }

    private void sendTransaction(String transaction){
        mDisposable.add(RpcManager.get().sendXfer(transaction).subscribe(this::add,new WebErrorConsumer()));
    }

    private void add(String address){
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransactionList list = RpcManager.get().getTransactionList();
                list.add(address,"Pending");
                mTvTransaction.setText(list.getNum()+"笔交易正在进行中...");

                while(true){
                    try {
                        Thread.sleep(20000);
                        WebXdag web = Web3XdagFactory.build(new HttpService(Config.POOL_TEST));
                        TransactionState tra = web.xdagGetTransactionByHash(address).send();
                        Log.i("交易状态:",address+"的状态"+tra.getTransactionDTO().state);
                        Log.i("待处理交易数量",String.valueOf(list.getNum()));
                        list.change(address,tra.getTransactionDTO().state);
                        if("Accepted".equals(list.getTransactionList().get(address))) {
                            list.remove(address);
                            if(list.getNum()==0) {
                                System.out.println("设置为空");
                                mTvTransaction.setText("");
                            }
                            else{
                                mTvTransaction.setText(list.getNum()+"笔交易正在进行中...");
                            }
                            break;
                        }
                    } catch (Throwable e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        System.out.println(address);
    }
    private void add1(String address){
        new Thread(new Runnable() {
            @Override
            public void run() {
                TransactionList list = RpcManager.get().getTransactionList();
                list.add(address,"Pending");
                while (true){
                    try{
                        Thread.sleep(20000);
                        mDisposable.add(RpcManager.get().CheckTransactionStatus(address).subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {

                                if(list.getNum()==0){
                                    mTvTransaction.setText("");
                                }
                                else {
                                    mTvTransaction.setText(list.getNum()+"笔交易正在进行中...");
                                }
                            }
                        },new WebErrorConsumer()));
                    }catch (Throwable e){
                        e.printStackTrace();
                    }
                    if("Accepted".equals(list.getTransactionList().get(address))) {
                        list.remove(address);
                        break;
                    }
                }
            }
        }).start();
    }
}
