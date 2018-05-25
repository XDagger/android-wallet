package io.xdag.xdagwallet.model;

import io.xdag.common.Common;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/5/25.
 *
 * desc :
 */
public class TransactionModel {

    public enum Type {
        INPUT, OUTPUT
    }


    public String address;
    public String amount;
    public String time;
    public Type type;


    public TransactionModel(String address, String amount, String time, Type type) {
        this.address = address;
        this.amount = amount;
        this.time = time;
        this.type = type;
    }


    public int getTypeImage() {
        if (Type.INPUT.equals(type)) {
            return R.drawable.ic_input;
        } else {
            return R.drawable.ic_output;
        }
    }


    public String getAmount() {
        if (Type.INPUT.equals(type)) {
            return String.format("+%s", amount);
        } else {
            return String.format("-%s", amount);
        }
    }


    public int getAmountColor() {
        if (Type.INPUT.equals(type)) {
            return Common.getColor(R.color.colorPrimary);
        } else {
            return Common.getColor(R.color.colorAccent);
        }
    }
}
