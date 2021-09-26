package io.xdag.xdagwallet.rpc;

import io.xdag.xdagwallet.rpc.response.TransactionState;
import io.xdag.xdagwallet.rpc.response.Web3ClientVersion;
import io.xdag.xdagwallet.rpc.response.XdagBalance;
import io.xdag.xdagwallet.rpc.response.XdagBlockNumber;
import io.xdag.xdagwallet.rpc.response.XdagGetTransactionByHash;
import io.xdag.xdagwallet.rpc.response.XdagSendTransaction;

import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.Request;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;

public class WebXdag extends JsonRpc2_0Web3j {

    public WebXdag(Web3jService web3jService) {
        super(web3jService);
    }

    public WebXdag(Web3jService web3jService, long pollingInterval, ScheduledExecutorService scheduledExecutorService) {
        super(web3jService, pollingInterval, scheduledExecutorService);
    }



    public Request<?, XdagBalance> xdagGetBalance(
            String address) {
        return new Request<String, XdagBalance>(
                "xdag_getBalance",
                Arrays.asList(address),
                web3jService,
                XdagBalance.class);
    }

    public Request<?, XdagBlockNumber> xdag_blockNumber() {
        return new Request<String, XdagBlockNumber>(
                "xdag_blockNumber",
                Collections.<String>emptyList(),
                web3jService,
                XdagBlockNumber.class);
    }

    public Request<?, Web3ClientVersion> web3ClientVersion1() {
        return new Request<String, Web3ClientVersion>(
                "web3_clientVersion", //web3_clientVersion
                Collections.<String>emptyList(),
                web3jService,
                Web3ClientVersion.class);
    }

    public Request<?, XdagSendTransaction> xdagSendRawTransaction(String signedTransactionData){
        return new Request<String,XdagSendTransaction>(
                "xdag_sendRawTransaction",
                Arrays.asList(signedTransactionData),
                web3jService,
                XdagSendTransaction.class);
    }

    public Request<?, TransactionState> xdagGetTransactionByHash(String hash){
        return new Request<String,TransactionState>(
                "xdag_getTransactionByHash",
                Arrays.asList(hash,"true"),
                web3jService,
                TransactionState.class);
    }
}
