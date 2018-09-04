package io.xdag.xdagwallet.net.rx;

import android.text.TextUtils;
import io.xdag.common.Common;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.model.BlockDetailModel;
import java.util.List;

import io.reactivex.functions.Function;
import io.xdag.xdagwallet.net.error.NoTransactionException;

/**
 * created by lxm on 2018/7/24.
 */
public class Detail2AddressListFunction
        implements Function<BlockDetailModel, List<BlockDetailModel.BlockAsAddress>> {

    @Override
    public List<BlockDetailModel.BlockAsAddress> apply(BlockDetailModel blockDetailModel)
            throws Exception {

        // no transaction found
        if (blockDetailModel == null
                || blockDetailModel.block_as_address == null
                || blockDetailModel.block_as_address.isEmpty()) {
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

        return blockDetailModel.block_as_address;
    }
}
