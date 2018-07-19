package io.xdag.xdagwallet.api.xdagscan;

import android.text.TextUtils;
import io.xdag.common.Common;
import io.xdag.xdagwallet.R;
import java.util.List;

/**
 * created by lxm on 2018/7/6.
 *
 * desc :
 */
public class BlockDetailModel {

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


        public int getTypeImage() {
            if (isInput()) {
                return R.drawable.ic_input;
            } else {
                return R.drawable.ic_output;
            }
        }


        public String getAmount() {
            if (isInput()) {
                return String.format("+%s", amount);
            } else {
                return String.format("-%s", amount);
            }
        }


        public int getAmountColor() {
            if (isInput()) {
                return Common.getColor(R.color.colorPrimary);
            } else {
                return Common.getColor(R.color.colorAccent);
            }
        }


        public boolean isInput() {
            return TextUtils.equals(direction, "input");
        }
    }
}
