package io.xdag.xdagwallet.net.rx;

import android.text.TextUtils;
import io.reactivex.functions.Function;
import io.xdag.common.Common;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.model.BlockDetailModel;
import io.xdag.xdagwallet.net.error.NoTransactionException;
import java.util.List;

/**
 * created by lxm on 2018/7/26.
 */
public class Detail2TranListFunction
    implements Function<BlockDetailModel, List<BlockDetailModel.BlockAsAddress>> {

    @Override public List<BlockDetailModel.BlockAsAddress> apply(BlockDetailModel blockDetailModel)
        throws Exception {
        if (blockDetailModel == null
            || blockDetailModel.block_as_transaction == null
            || blockDetailModel.block_as_transaction.isEmpty()) {
            throw new NoTransactionException();
        }

        // error message
        if(!TextUtils.isEmpty(blockDetailModel.error)) {
            String message = Common.getString(R.string.error_server_problem);
            if(!TextUtils.isEmpty(blockDetailModel.message)) {
                message = blockDetailModel.message;
            }
            throw new Exception(message);
        }

        return blockDetailModel.block_as_transaction;
    }
}
