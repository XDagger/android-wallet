package io.xdag.xdagwallet.config;


import io.xdag.xdagwallet.wallet.Wallet;

public class XdagConfig {
    private Wallet wallet = null;
    private String address = null;
    private static XdagConfig Instance = null;



    public static XdagConfig getInstance(){
        if(Instance==null){
            Instance = new XdagConfig();
        }
        return Instance;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
