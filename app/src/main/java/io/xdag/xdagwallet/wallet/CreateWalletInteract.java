package io.xdag.xdagwallet.wallet;

import android.app.Activity;
import android.util.Log;

import org.web3j.protocol.http.HttpService;

import java.util.Arrays;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.config.XdagConfig;
import io.xdag.xdagwallet.crypto.ECKeyPair;
import io.xdag.xdagwallet.crypto.Keys;
import io.xdag.xdagwallet.rpc.Web3XdagFactory;
import io.xdag.xdagwallet.rpc.WebXdag;
import io.xdag.xdagwallet.rpc.response.XdagSendTransaction;
import io.xdag.xdagwallet.util.BytesUtils;


public class CreateWalletInteract {


    public CreateWalletInteract() {
    }

    public Single<Wallet> create(final String pwd, String confirmPwd,Activity activity) {
        return Single.fromCallable(() -> {
            Wallet xdagWallet = WalletUtils.createNewWallet(pwd);
            WalletUtils.walletStart(xdagWallet);
            String AddressBlock = WalletUtils.createAddress(activity,xdagWallet);
            WebXdag web = Web3XdagFactory.build(new HttpService(Config.POOL_TEST));
            XdagSendTransaction tra = web.xdagSendRawTransaction(AddressBlock).send();
            Log.i("新地址:",tra.getTransactionHash());
            xdagWallet.lock();
            return xdagWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }
    public Single<Wallet> load(final String pwd,Activity activity){
        return Single.fromCallable(()->{
            Wallet xdagWallet  = WalletUtils.loadAndUnlockWallet(pwd);
            WalletUtils.walletStart(xdagWallet);
            WalletUtils.createAddress(activity,xdagWallet);
            xdagWallet.lock();
            return xdagWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }
    public Single<ECKeyPair> send(String password){
        return Single.fromCallable(()->{
            Wallet wallet = XdagConfig.getInstance().getWallet();
            wallet.unlock(password);
            ECKeyPair keyPair = wallet.getAccount(0);
            Log.d("sendFragment","get wallet account 0 " + BytesUtils.toHexString(Keys.toBytesAddress(keyPair)));
            wallet.lock();
            return keyPair;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

//    public Single<ETHWallet> loadWalletByKeystore(final String keystore, final String pwd) {
//        return Single.fromCallable(() -> {
//            ETHWallet ethWallet = ETHWalletUtils.loadWalletByKeystore(keystore, pwd);
//            if (ethWallet != null) {
//                WalletDaoUtils.insertNewWallet(ethWallet);
//            }
//
//            return ethWallet;
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    public Single<ETHWallet> loadWalletByPrivateKey(final String privateKey, final String pwd) {
//        return Single.fromCallable(() -> {
//
//                    ETHWallet ethWallet = ETHWalletUtils.loadWalletByPrivateKey(privateKey, pwd);
//                    if (ethWallet != null) {
//                        WalletDaoUtils.insertNewWallet(ethWallet);
//                    }
//                    return ethWallet;
//                }
//        ).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//
//    }
//
//    public Single<ETHWallet> loadWalletByMnemonic(final String bipPath, final String mnemonic, final String pwd) {
//        return Single.fromCallable(() -> {
//            ETHWallet ethWallet = ETHWalletUtils.importMnemonic(bipPath
//                    , Arrays.asList(mnemonic.split(" ")), pwd);
//            if (ethWallet != null) {
//                WalletDaoUtils.insertNewWallet(ethWallet);
//            }
//            return ethWallet;
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//
//
//    }

}
