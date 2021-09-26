package io.xdag.xdagwallet.rpc.response;

import org.web3j.protocol.core.Response;

public class XdagSendTransaction extends Response<String> {
    public String getTransactionHash() {
        return getResult();
    }
}
