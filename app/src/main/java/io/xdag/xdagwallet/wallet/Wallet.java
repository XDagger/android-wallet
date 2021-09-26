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

import android.util.Log;

import io.xdag.common.util.ToastUtil;
import io.xdag.xdagwallet.crypto.Aes;
import io.xdag.xdagwallet.crypto.Bip32ECKeyPair;
import io.xdag.xdagwallet.crypto.ECKeyPair;
import io.xdag.xdagwallet.crypto.Keys;
import io.xdag.xdagwallet.crypto.MnemonicUtils;
import io.xdag.xdagwallet.crypto.SecureRandomUtils;
import io.xdag.xdagwallet.crypto.SimpleDecoder;
import io.xdag.xdagwallet.crypto.SimpleEncoder;
import io.xdag.xdagwallet.util.ByteArrayWrapper;
import io.xdag.xdagwallet.util.SystemUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.crypto.generators.BCrypt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;



public class Wallet {

    private static final int VERSION = 4;
    private static final int SALT_LENGTH = 16;
    private static final int BCRYPT_COST = 12;
    private static final String path = "/data/data/io.xdag.xdagwallet/files/xdag/wallet.dat";
    private Set<PosixFilePermission> POSIX_SECURED_PERMISSIONS = null;

    private final File file;

    private final Map<ByteArrayWrapper, ECKeyPair> accounts = Collections.synchronizedMap(new LinkedHashMap<>());
    private String password;

    // hd wallet key
    private String mnemonicPhrase = "";
    private int nextAccountIndex = 0;
    private final String TAG = "wallet";


    /**
     * Creates a new wallet instance.
     */
    public Wallet() {
        File file1 = new File(path);
        try {
            if(!file1.exists()){
                file1.getParentFile().mkdirs();
                file1.createNewFile();
            }
        }catch (Exception e){
            ToastUtil.showCenter("创建失败");
        }
        this.file = FileUtils.getFile(path);
        POSIX_SECURED_PERMISSIONS = new HashSet<>();
        this.POSIX_SECURED_PERMISSIONS.add(OWNER_READ);
        this.POSIX_SECURED_PERMISSIONS.add(OWNER_WRITE);
    }

    /**
     * Returns whether the wallet file exists and non-empty.
     */
    public boolean exists() {
        return file.length() > 0;
    }

    /**
     * Deletes the wallet file.
     */
    public void delete() throws IOException {
        Files.delete(file.toPath());
    }

    /**
     * Returns the file where the wallet is persisted.
     */
    public File getFile() {
        return file;
    }

    /**
     * Locks the wallet.
     */
    public void lock() {
        password = null;
        accounts.clear();
    }

    public ECKeyPair getDefKey() {
        List<ECKeyPair> accountList = getAccounts();
        if(CollectionUtils.isNotEmpty(accountList)) {
            return accountList.get(0);
        }
        return null;
    }

    /**
     * Unlocks this wallet
     */
    public boolean unlock(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password can not be null");
        }

        try {
            byte[] key;
            byte[] salt;

            if (exists()) {

                SimpleDecoder dec = new SimpleDecoder(FileUtils.readFileToByteArray(file));
                int version = dec.readInt(); // version

                Set<ECKeyPair> newAccounts = null;
                switch (version) {
                    // only version 4
                    case 4 :
                        salt = dec.readBytes();
                        key = BCrypt.generate(password.getBytes(UTF_8), salt, BCRYPT_COST);
                        try {
                            newAccounts = readAccounts(key, dec, true, version);
                            readHdSeed(key, dec);
                        } catch (Exception e) {
                            Log.i(TAG,"Failed to read HD mnemonic phrase");
                            return false;
                        }
                        break;
                    default : throw new RuntimeException("Unknown wallet version.");
                }

                synchronized (accounts) {
                    accounts.clear();
                    for (ECKeyPair account : newAccounts) {
                        ByteArrayWrapper baw = ByteArrayWrapper.of(Keys.toBytesAddress(account));
                        accounts.put(baw, account);
                    }
                }
            }
            this.password = password;
            return true;
        } catch (Exception e) {
            Log.i(TAG,"Failed to open wallet");
        }
        return false;
    }

    /**
     * Reads the account keys.
     */
    protected LinkedHashSet<ECKeyPair> readAccounts(byte[] key, SimpleDecoder dec, boolean vlq, int version) {
        LinkedHashSet<ECKeyPair> keys = new LinkedHashSet<>();
        int total = dec.readInt(); // size

        for (int i = 0; i < total; i++) {
            byte[] iv = dec.readBytes(vlq);
            byte[] privateKey = Aes.decrypt(dec.readBytes(vlq), key, iv);
            keys.add(ECKeyPair.create(privateKey));
        }
        return keys;
    }

    /**
     * Writes the account keys.
     */
    protected void writeAccounts(byte[] key, SimpleEncoder enc) {
        synchronized (accounts) {
            enc.writeInt(accounts.size());
            for (ECKeyPair a : accounts.values()) {
                byte[] iv = SecureRandomUtils.secureRandom().generateSeed(16);

                enc.writeBytes(iv);
                enc.writeBytes(Aes.encrypt(a.getPrivateKey().toByteArray(), key, iv));
            }
        }
    }

    /**
     * Reads the mnemonic phase and next account index.
     */
    protected void readHdSeed(byte[] key, SimpleDecoder dec) {
        byte[] iv = dec.readBytes();
        byte[] hdSeedEncrypted = dec.readBytes();
        byte[] hdSeedRaw = Aes.decrypt(hdSeedEncrypted, key, iv);

        SimpleDecoder d = new SimpleDecoder(hdSeedRaw);
        mnemonicPhrase = d.readString();
        nextAccountIndex = d.readInt();
    }

    /**
     * Writes the mnemonic phase and next account index.
     */
    protected void writeHdSeed(byte[] key, SimpleEncoder enc) {
        SimpleEncoder e = new SimpleEncoder();
        e.writeString(mnemonicPhrase);
        e.writeInt(nextAccountIndex);

        byte[] iv = SecureRandomUtils.secureRandom().generateSeed(16);
        byte[] hdSeedRaw = e.toBytes();
        byte[] hdSeedEncrypted = Aes.encrypt(hdSeedRaw, key, iv);

        enc.writeBytes(iv);
        enc.writeBytes(hdSeedEncrypted);
    }

    /**
     * Returns if this wallet is unlocked.
     */
    public boolean isUnlocked() {
        return !isLocked();
    }

    /**
     * Returns whether the wallet is locked.
     */
    public boolean isLocked() {
        return password == null;
    }

    /**
     * Sets the accounts inside this wallet.
     */
    public void setAccounts(List<ECKeyPair> list) {
        requireUnlocked();
        accounts.clear();
        for (ECKeyPair key : list) {
            addAccount(key);
        }
    }

    /**
     * Returns a copy of the accounts inside this wallet.
     */
    public List<ECKeyPair> getAccounts(){
        requireUnlocked();
        synchronized (accounts) {
            return new ArrayList<>(accounts.values());
        }
    }

    /**
     * Returns account by index.
     */
    public ECKeyPair getAccount(int idx) {
        requireUnlocked();
        synchronized (accounts) {
            return getAccounts().get(idx);
        }
    }

    /**
     * Returns account by address.
     */
    public ECKeyPair getAccount(byte[] address) {
        requireUnlocked();

        synchronized (accounts) {
            return accounts.get(ByteArrayWrapper.of(address));
        }
    }

    /**
     * Flushes this wallet into the disk.
     */
    public boolean flush() {
        requireUnlocked();

        try {
            SimpleEncoder enc = new SimpleEncoder();
            enc.writeInt(VERSION);

            byte[] salt = SecureRandomUtils.secureRandom().generateSeed(SALT_LENGTH);
            enc.writeBytes(salt);

            byte[] key = BCrypt.generate(password.getBytes(UTF_8), salt, BCRYPT_COST);

            writeAccounts(key, enc);
            writeHdSeed(key, enc);

            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                Log.i(TAG,"Failed to create the directory for wallet");
                return false;
            }

            // set posix permissions
            if (SystemUtil.isPosix() && !file.exists()) {
                Files.createFile(file.toPath());
                Files.setPosixFilePermissions(file.toPath(), POSIX_SECURED_PERMISSIONS);
            }

            FileUtils.writeByteArrayToFile(file, enc.toBytes());
            return true;
        } catch (IOException e) {
            Log.i(TAG,"Failed to write wallet to disk", e);
        }
        return false;
    }


    private void requireUnlocked() {
        if (!isUnlocked()) {
            throw new RuntimeException("Wallet is Locked!");
        }
    }

    /**
     * Adds a new account to the wallet.
     */
    public boolean addAccount(ECKeyPair newKey) {
        requireUnlocked();

        synchronized (accounts) {
            ByteArrayWrapper address = ByteArrayWrapper.of(Keys.toBytesAddress(newKey));
            if (accounts.containsKey(address)) {
                return false;
            }

            accounts.put(address, newKey);
            return true;
        }
    }

    /**
     * Add an account with randomly generated key.
     */
    public ECKeyPair addAccountRandom() {
        ECKeyPair key = Keys.createEcKeyPair();
        addAccount(key);
        return key;
    }

    /**
     * Adds a list of accounts to the wallet.
     */
    public int addAccounts(List<ECKeyPair> accounts) {
        requireUnlocked();

        int n = 0;
        for (ECKeyPair acc : accounts) {
            n += addAccount(acc) ? 1 : 0;
        }
        return n;
    }

    /**
     * Deletes an account in the wallet.
     */
    public boolean removeAccount(ECKeyPair key) {
        return removeAccount(Keys.toBytesAddress(key));
    }

    /**
     * Deletes an account in the wallet.
     */
    public boolean removeAccount(byte[] address) {
        requireUnlocked();
        synchronized (accounts) {
            return accounts.remove(ByteArrayWrapper.of(address)) != null;
        }
    }

    /**
     * Changes the password of the wallet.
     */
    public void changePassword(String newPassword) {
        requireUnlocked();

        if (newPassword == null) {
            throw new IllegalArgumentException("Password can not be null");
        }

        this.password = newPassword;
    }

    // ================
    // HD wallet
    // ================

    /**
     * Returns whether the HD seed is initialized.
     *
     * @return true if set, otherwise false
     */
    public boolean isHdWalletInitialized() {
        requireUnlocked();
        return mnemonicPhrase != null && !mnemonicPhrase.isEmpty();
    }

    /**
     * Initialize the HD wallet.
     *
     * @param mnemonicPhrase
     *            the mnemonic word list
     */
    public void initializeHdWallet(String mnemonicPhrase) {
        this.mnemonicPhrase = mnemonicPhrase;
        this.nextAccountIndex = 0;
    }

    /**
     * Returns the HD seed.
     */
    public byte[] getSeed() {
        byte[] initialEntropy = new byte[16];
        SecureRandomUtils.secureRandom().nextBytes(initialEntropy);
        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
        return MnemonicUtils.generateSeed(mnemonic, null);
    }

    /**
     * Derives a key based on the current HD account index, and put it into the
     * wallet.
     */
    public ECKeyPair addAccountWithNextHdKey() {
        requireUnlocked();
        requireHdWalletInitialized();

        synchronized (accounts) {
            byte[] seed = getSeed();
            Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
            Bip32ECKeyPair bip44Keypair = WalletUtils.generateBip44KeyPair(masterKeypair, nextAccountIndex++);
            ByteArrayWrapper address = ByteArrayWrapper.of(Keys.toBytesAddress(bip44Keypair));
            accounts.put(address, bip44Keypair);
            return bip44Keypair;
        }
    }

    private void requireHdWalletInitialized() {
        if (!isHdWalletInitialized()) {
            throw new IllegalArgumentException("HD Seed is not initialized");
        }
    }

    public String getMnemonicPhrase(){
        return this.mnemonicPhrase;
    }

    public String getPassword(){
        return this.password;
    }
}
