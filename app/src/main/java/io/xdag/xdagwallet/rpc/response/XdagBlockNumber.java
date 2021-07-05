package io.xdag.xdagwallet.rpc.response;

import org.web3j.protocol.core.Response;

public class XdagBlockNumber extends Response<String> {
    public String getBlockNumber() {
        return getResult();
    }
}
