package io.xdag.xdagwallet.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.model.TransactionModel;

/**
 * created by lxm on 2018/5/25.
 * <p>
 * desc :
 */
public class TransactionAdapter extends BaseQuickAdapter<TransactionModel, BaseViewHolder> {
    public TransactionAdapter() {
        super(R.layout.item_transaction, null);
    }


    @Override
    protected void convert(BaseViewHolder helper, TransactionModel item) {
        helper.setText(R.id.item_transaction_tv_address, item.address);
        helper.setText(R.id.item_transaction_tv_amount, item.getAmount());
        helper.setTextColor(R.id.item_transaction_tv_amount, item.getAmountColor());
        helper.setText(R.id.item_transaction_tv_time, item.time);
        helper.setImageResource(R.id.item_transaction_tv_type, item.getTypeImage());
    }
}
