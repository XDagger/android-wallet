package io.xdag.xdagwallet.api.response;

import java.util.List;

/**
 * created by lxm on 2018/7/6.
 *
 * desc :
 */
public class XdagScanBlockDetail {

    public String time;
    public String timestamp;
    public String flags;
    public String file_pos;
    public String hash;
    public String difficulty;
    public String balance;
    public String transaction_total;
    public String address_total;
    public List<Transaction> transaction;
    public List<AddressList> address_list;


    public static class Transaction {

        public String direction;
        public String address;
        public String amount;
    }


    public static class AddressList {

        public String direction;
        public String address;
        public String amount;
        public String time;
    }
}
