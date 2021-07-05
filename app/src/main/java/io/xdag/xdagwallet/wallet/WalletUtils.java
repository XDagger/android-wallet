/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2030 The XdagJ Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.xdag.xdagwallet.wallet;


import android.app.Activity;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import io.xdag.common.util.ToastUtil;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.config.XdagConfig;
import io.xdag.xdagwallet.core.Block;
import io.xdag.xdagwallet.core.BlockBuilder;
import io.xdag.xdagwallet.crypto.Bip32ECKeyPair;
import io.xdag.xdagwallet.crypto.ECKeyPair;
import io.xdag.xdagwallet.crypto.Keys;
import io.xdag.xdagwallet.crypto.MnemonicUtils;
import io.xdag.xdagwallet.crypto.SecureRandomUtils;
import io.xdag.xdagwallet.crypto.SimpleEncoder;
import io.xdag.xdagwallet.rpc.RpcManager;
import io.xdag.xdagwallet.util.BytesUtils;
import io.xdag.xdagwallet.util.XdagTime;

import static io.xdag.xdagwallet.crypto.Bip32ECKeyPair.HARDENED_BIT;
import static io.xdag.xdagwallet.util.BasicUtils.hash2Address;

public class WalletUtils {

    // https://github.com/satoshilabs/slips/blob/master/slip-0044.md
    public static final int XDAG_BIP44_CION_TYPE = 586;

    public static Bip32ECKeyPair generateBip44KeyPair(Bip32ECKeyPair master, int index) {
        // m/44'/586'/0'/0/0
        // xdag coin type 586 at https://github.com/satoshilabs/slips/blob/master/slip-0044.md
        final int[] path = {44 | HARDENED_BIT, XDAG_BIP44_CION_TYPE | HARDENED_BIT, 0 | HARDENED_BIT, 0, index};
        return Bip32ECKeyPair.deriveKeyPair(master, path);
    }

    public static Bip32ECKeyPair importMnemonic(Wallet wallet, String password, String mnemonic, int index) {
        wallet.unlock(password);
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, password);
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        return generateBip44KeyPair(masterKeypair, index);
    }

    public static Wallet createNewWallet(String newPassword) {
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

    public static Wallet loadWallet() {
        return new Wallet();
    }

    public static Wallet loadAndUnlockWallet(String password) {
        Wallet wallet = loadWallet();
        if (!wallet.unlock(password)) {
            System.err.println("Invalid password");
        }

        return wallet;
    }

    public static boolean initializedHdSeed(Wallet wallet, PrintStream printer) {
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
    public static void walletStart(Wallet wallet){
        //wallet = loadWallet().exists() ? loadAndUnlockWallet() : createNewWallet();
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

        XdagConfig.getInstance().setWallet(wallet);

    }

    public static String createAddress(Activity activity,Wallet wallet) {
        String xdagAddress = "";
        String newAddress = "";
        try {
            File file = new File(activity.getFilesDir(),"xdag/address.dat");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                Block AddressBlock = BlockBuilder.generateAddressBlock(wallet.getAccount(0), XdagTime.getCurrentTimestamp());
                newAddress = BytesUtils.toHexString(AddressBlock.getXdagBlock().getData());
                Log.i("Wallet","New BlockAddress:" + newAddress);
                //RpcManager.get().sendXfer(newAddress);
                byte[] adr = AddressBlock.getHash();
                xdagAddress = hash2Address(adr);
                XdagConfig.getInstance().setAddress(xdagAddress);
                Config.setAddress(xdagAddress);
                SimpleEncoder enc = new SimpleEncoder();
                enc.write(adr);
                FileUtils.writeByteArrayToFile(file, enc.toBytes());
            }else{
                byte[] address = FileUtils.readFileToByteArray(file);
                byte[] adr = new byte[32];
                System.arraycopy(address,0,adr,0,32);
                xdagAddress = hash2Address(adr);
                XdagConfig.getInstance().setAddress(xdagAddress);
            }
        } catch (IOException e) {
            ToastUtil.show("创建地址失败");
            e.printStackTrace();
        }
        return newAddress;
    }
    public static void walletDelete(String path){
            File file = new File(path);
            if (file.exists()) {
                deleteFile(file);
            }
    }
    public static void deleteFile(File dir ){

        if (dir == null || !dir.exists() || dir.isFile()) {

            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteFile(file); // 递归
            }
        }
        dir.delete();

    }

}