package io.xdag.xdagwallet.rpc;


import org.web3j.protocol.Web3jService;

public class Web3XdagFactory {

    public static WebXdag build(Web3jService web3jService) {
        return new WebXdag(web3jService);
    }
}
