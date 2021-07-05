package io.xdag.xdagwallet.rpc.response;

import org.web3j.protocol.core.Response;

public class Web3ClientVersion extends Response<String> {

    public String getWeb3ClientVersion() {
        return getResult();
    }
}
