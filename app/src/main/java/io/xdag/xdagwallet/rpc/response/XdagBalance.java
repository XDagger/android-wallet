package io.xdag.xdagwallet.rpc.response;

import org.web3j.protocol.core.Response;

import java.math.BigInteger;

import io.xdag.xdagwallet.util.BasicUtils;

public class XdagBalance extends Response<String> {

    public String getBalance() {
        System.out.println(getResult());
        //return Double.toString(BasicUtils.amount2xdag(new BigInteger(getResult().substring(2),16).longValue()));
        return getResult();
    }
}
