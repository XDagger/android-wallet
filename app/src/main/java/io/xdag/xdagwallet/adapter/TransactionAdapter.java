package io.xdag.xdagwallet.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.activity.TranDetailActivity;
import io.xdag.xdagwallet.model.BlockDetailModel;
import io.xdag.xdagwallet.util.CopyUtil;
import java.util.List;

/**
 * created by lxm on 2018/5/25.
 * <p>
 * desc :
 */
public class TransactionAdapter
    extends BaseQuickAdapter<BlockDetailModel.BlockAsAddress, BaseViewHolder> {

    public TransactionAdapter(@Nullable
                                  List<BlockDetailModel.BlockAsAddress> data) {
        super(R.layout.item_transaction, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, final BlockDetailModel.BlockAsAddress item) {
        helper.setText(R.id.item_transaction_tv_address, item.address);
        helper.setText(R.id.item_transaction_tv_amount, item.getAmount());
        helper.setText(R.id.item_transaction_tv_remark, item.remark);
        helper.setTextColor(R.id.item_transaction_tv_amount, item.getAmountColor());
        helper.setImageResource(R.id.item_transaction_img_type, item.getTypeImage());

        TextView tvTime = helper.getView(R.id.item_transaction_tv_time);
        tvTime.setVisibility(item.getTimeVisible());
        tvTime.setText(item.time);

        helper.itemView.setOnClickListener(v -> TranDetailActivity.start(mContext, item.address));

        helper.itemView.setOnLongClickListener(v -> {
            if (mContext instanceof Activity) {
                CopyUtil.copyAddress(((Activity) mContext), item.address);
            } else {
                CopyUtil.copyAddress(item.address);
            }
            return true;
        });
    }
}
