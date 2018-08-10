package io.xdag.xdagwallet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

import com.chad.library.adapter.base.BaseViewHolder;

import io.xdag.common.base.ListActivity;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.model.PoolModel;

/**
 * created by ssyijiu  on 2018/7/29
 */
public class PoolListActivity extends ListActivity<PoolModel> {

    private static final String EXTRA_ONLY_CONFIG = "extra_only_config";
    private boolean mOnlyConfig;


    @Override
    protected int getItemLayout() {
        return R.layout.item_pool;
    }


    @Override
    protected boolean isRefresh() {
        return false;
    }


    @Override protected void parseIntent(Intent intent) {
        super.parseIntent(intent);
        mOnlyConfig = intent.getBooleanExtra(EXTRA_ONLY_CONFIG, false);
    }


    @Override
    protected void initData() {
        super.initData();
        mAdapter.setNewData(PoolModel.getPoolList());
    }


    @Override
    protected void convert(BaseViewHolder helper, final PoolModel item) {
        super.convert(helper, item);
        helper.setText(R.id.item_pool_tv, item.address);
        helper.setImageResource(R.id.item_pool_img, item.selectedImage);
        helper.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setMessage(getString(R.string.switch_pool_to, item.address))
                .setPositiveButton(R.string.ensure, (dialog, which) -> {
                    Config.setPoolAddress(item.address);
                    mAdapter.setNewData(PoolModel.getPoolList());
                    if(!mOnlyConfig) {
                        MainActivity.switchPool(mContext);
                    }
                })
                .setNegativeButton(R.string.cancel, null);
            builder.create().show();
        });
    }


    @Override
    protected int getToolbarTitle() {
        return R.string.more_pool;
    }


    public static void start(Activity context, boolean onlyConfig) {
        Intent intent = new Intent(context, PoolListActivity.class);
        intent.putExtra(EXTRA_ONLY_CONFIG, onlyConfig);
        context.startActivity(intent);
    }
}
