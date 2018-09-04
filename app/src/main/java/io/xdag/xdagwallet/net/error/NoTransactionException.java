package io.xdag.xdagwallet.net.error;

import io.xdag.common.Common;
import io.xdag.xdagwallet.R;

/**
 * created by ssyijiu  on 2018/8/9
 */
public class NoTransactionException extends Exception {
    public NoTransactionException() {
        super(Common.getString(R.string.no_transaction));
    }
}
