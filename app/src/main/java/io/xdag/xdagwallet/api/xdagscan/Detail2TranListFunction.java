package io.xdag.xdagwallet.api.xdagscan;

import io.reactivex.functions.Function;
import io.xdag.common.Common;
import io.xdag.xdagwallet.R;
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
            throw new Exception(Common.getString(R.string.no_transaction));
        }
        return blockDetailModel.block_as_transaction;
    }
}
