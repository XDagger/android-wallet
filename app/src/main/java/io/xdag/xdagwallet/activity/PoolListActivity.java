package io.xdag.xdagwallet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;

import io.xdag.common.base.ListActivity;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.model.PoolModel;
import io.xdag.xdagwallet.wrapper.XdagHandlerWrapper;
import io.xdag.xdagwallet.wrapper.XdagWrapper;

/**
 * created by ssyijiu  on 2018/7/29
 */
public class PoolListActivity extends ListActivity<PoolModel> {

    @Override
    protected int getItemLayout() {
        return R.layout.item_pool;
    }

    @Override
    protected boolean isRefresh() {
        return false;
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
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setMessage(getString(R.string.switch_pool_to, item.address))
                        .setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Config.setPoolAddress(item.address);
                                mAdapter.setNewData(PoolModel.getPoolList());
                                // TODO: switch pool
                                MainActivity.startForChangePool(getApplicationContext());
                            }
                        })
                        .setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
        });
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.switch_pool;
    }

    public static void start(Activity context) {
        Intent intent = new Intent(context, PoolListActivity.class);
        context.startActivity(intent);
    }
}
