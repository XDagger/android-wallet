package io.xdag.xdagwallet.rpc;

import java.util.HashMap;
import java.util.Map;

public class TransactionList {
    private Map<String,String > transactionList;

    public TransactionList(){
        this.transactionList = new HashMap<>();
    }
    public synchronized void add(String hash,String state){
        transactionList.put(hash,state);
    }
    public synchronized void remove(String hash){
        transactionList.remove(hash);
    }
    public synchronized void change(String hash,String state){
        transactionList.put(hash,state);
    }
    public synchronized int getNum(){
        return transactionList.size();
    }

    public synchronized Map<String, String> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(Map<String, String> transactionList) {
        this.transactionList = transactionList;
    }
}
