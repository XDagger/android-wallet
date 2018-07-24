package io.xdag.xdagwallet.api.xdagscan;

import io.reactivex.functions.Function;
import io.xdag.common.Common;
import io.xdag.xdagwallet.R;
import java.util.List;

/**
 * created by lxm on 2018/7/24.
 */
public class Detail2AddressListFunction
    implements Function<BlockDetailModel, List<BlockDetailModel.BlockAsAddress>> {

    @Override public List<BlockDetailModel.BlockAsAddress> apply(BlockDetailModel blockDetailModel)
        throws Exception {
        if (blockDetailModel == null
            || blockDetailModel.block_as_address == null
            || blockDetailModel.block_as_address.isEmpty()) {
            throw new Exception(Common.getString(R.string.no_transaction));
        }
        return blockDetailModel.block_as_address;
    }
}
