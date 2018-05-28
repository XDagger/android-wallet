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
        mData.add(new TransactionModel("+VP7hTEcNKAE73yH9kI8MXjMjYunz46Z", "5000.000",
                "2018-03-27 04:06:28.024 UTC",
                TransactionModel.Type.INPUT));
        mData.add(new TransactionModel("DvT81lXDyiID+eom0u/9l54APwmm4Vdk", "2000.000",
                "2018-02-27 10:35:13.447 UTC",
                TransactionModel.Type.OUTPUT));
        mData.add(new TransactionModel("6YXzAotTuYppzlKJvbo815yeAeSvnUBo", "0.005",
                "2018-02-05 12:09:38.309 UTC",
                TransactionModel.Type.INPUT));
        mData.add(new TransactionModel("Ole9IDB1L3daUQmC07T+GsIDzw7GmlSQ", "0.010",
                "2018-02-11 06:41:05.392 UTC",
                TransactionModel.Type.INPUT));
        mData.add(new TransactionModel("s6adigDmA3aeLQBzNK4amgFKoLZBk68K", "100.000",
                "2018-02-11 06:41:05.231 UTC",
                TransactionModel.Type.OUTPUT));
        mData.add(new TransactionModel("OJtv4ws8P7t9a5IMY5BnMSZKPOBtHRPu", "1000.234",
                "2018-02-11 06:41:05.191 UTC",
                TransactionModel.Type.INPUT));
        mData.add(new TransactionModel("h1rViGmwyAHoGcE6n3Amm7RZFHbuJ6SA", "100.120",
                "2018-02-11 06:20:48.997 UTC",
                TransactionModel.Type.OUTPUT));
        mData.add(new TransactionModel("fGNAwrv3bpkI+Q7Y2SzLpLQbihKh3ic8", "10.234",
                "2018-02-11 06:05:52.520 UTC3",
                TransactionModel.Type.INPUT));
        mData.add(new TransactionModel("6jvtafHm5y0s/QEq+64Wgs+kcGxrt4ms", "10000.111",
                "2018-02-11 05:58:25.275 UTC",
                TransactionModel.Type.OUTPUT));
        mData.add(new TransactionModel("PD4W7XMtYuGWIko2K/KCzDHt2bO3ctQe", "100.333",
                "2018-02-11 05:56:18.053 UTC",
                TransactionModel.Type.OUTPUT));
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
