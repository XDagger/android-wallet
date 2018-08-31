package io.xdag.xdagwallet.net.rx;

import io.xdag.xdagwallet.model.BlockDetailModel;
import java.util.List;

import io.reactivex.functions.Function;
import io.xdag.xdagwallet.net.NoTransactionException;

/**
 * created by lxm on 2018/7/24.
 */
public class Detail2AddressListFunction
        implements Function<BlockDetailModel, List<BlockDetailModel.BlockAsAddress>> {

    @Override
    public List<BlockDetailModel.BlockAsAddress> apply(BlockDetailModel blockDetailModel)
            throws Exception {
        if (blockDetailModel == null
                || blockDetailModel.block_as_address == null
                || blockDetailModel.block_as_address.isEmpty()) {
            throw new NoTransactionException();
        }
        return blockDetailModel.block_as_address;
    }
}
