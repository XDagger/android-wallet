package io.xdag.xdagwallet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import com.chad.library.adapter.base.BaseViewHolder;
import io.xdag.common.base.ListActivity;
import io.xdag.xdagwallet.MainActivity;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;
import io.xdag.xdagwallet.dialog.InputBuilder;
import io.xdag.xdagwallet.dialog.TipBuilder;
import io.xdag.xdagwallet.model.PoolModel;
import io.xdag.xdagwallet.model.PoolListModel;
import io.xdag.xdagwallet.util.AlertUtil;

/**
 * created by ssyijiu  on 2018/7/29
 */
public class PoolListActivity extends ListActivity<PoolModel> {

    private static final String EXTRA_ONLY_CONFIG = "extra_only_config";
    private boolean mOnlyConfig;

    @BindView(R.id.pool_fab_add)
    FloatingActionButton mFabAdd;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_poollist;
    }


    @Override
    protected int getItemLayout() {
        return R.layout.item_pool;
    }


    @Override
    protected boolean isRefresh() {
        return false;
    }


    @Override
    protected void parseIntent(Intent intent) {
        super.parseIntent(intent);
        mOnlyConfig = intent.getBooleanExtra(EXTRA_ONLY_CONFIG, false);
    }


    @Override protected void initToolbar() {
        super.initToolbar();
        mToolbar.inflateMenu(R.menu.toolbar_pool);
        mToolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action_pool) {
                resetPool();
            }
            return false;
        });
    }



    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        super.initView(rootView, savedInstanceState);
        mFabAdd.setOnClickListener(v -> addPool());
    }


    @Override
    protected void initData() {
        super.initData();
        mAdapter.setNewData(PoolListModel.load().getPoolListToAdapter());
    }


    @Override
    protected void convert(BaseViewHolder helper, final PoolModel item) {
        super.convert(helper, item);
        helper.setText(R.id.item_pool_tv, item.address);
        helper.setImageResource(R.id.item_pool_img, item.selectedImage);
        helper.itemView.setOnClickListener(v -> {
            switchPool(item);
        });
        helper.itemView.setOnLongClickListener(v -> {
            deletePool(item);
            return true;
        });
    }


    @Override
    protected int getToolbarTitle() {
        return R.string.more_pool;
    }


    private void switchPool(PoolModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
            .setMessage(getString(R.string.switch_pool_to, item.address))
            .setPositiveButton(R.string.ensure, (dialog, which) -> {
                Config.setPoolAddress(item.address);
                mAdapter.setNewData(PoolListModel.get().getPoolListToAdapter());
                if (!mOnlyConfig) {
                    MainActivity.switchPool(mContext);
                }
            })
            .setNegativeButton(R.string.cancel, null);
        builder.show();
    }


    private void addPool() {
        new InputBuilder(mContext)
            .setInputType(InputType.TYPE_CLASS_TEXT)
            .setPositiveListener((dialog, input) -> {
                if (TextUtils.isEmpty(input)) {
                    AlertUtil.show(mContext, R.string.error_invalid_pool_address);
                    return;
                }

                PoolModel pool = new PoolModel(input);
                if (PoolListModel.get().contains(pool)) {
                    AlertUtil.show(mContext, R.string.error_pool_address_exists);
                    return;
                }
                PoolListModel.get().add(pool);
                mAdapter.setNewData(PoolListModel.get().getPoolListToAdapter());
            })
            .setMessage(R.string.add_pool_address)
            .setCancelable(true)
            .show();
    }


    private void deletePool(PoolModel item) {
        new TipBuilder(mContext)
            .setPositiveListener((dialog, which) -> {
                if(item.isSelected()) {
                    AlertUtil.show(mContext,R.string.error_cannot_delete_selected_pool);
                    return;
                }
                PoolListModel.get().delete(item);
                mAdapter.setNewData(PoolListModel.get().getPoolListToAdapter());
            })
            .setNegativeButton(R.string.cancel,null)
            .setMessage(getString(R.string.delete_pool_address, item.address))
            .show();
    }


    private void resetPool() {
        new TipBuilder(mContext)
                .setPositiveListener((dialog, which) -> {
                    PoolListModel.get().init();
                    mAdapter.setNewData(PoolListModel.get().getPoolListToAdapter());
                })
                .setNegativeButton(R.string.cancel,null)
                .setMessage(getString(R.string.reset_pool_address))
                .setCancelable(true)
                .show();

    }

    @Override protected void onStop() {
        super.onStop();
        PoolListModel.get().save();
    }


    public static void start(Activity context, boolean onlyConfig) {
        Intent intent = new Intent(context, PoolListActivity.class);
        intent.putExtra(EXTRA_ONLY_CONFIG, onlyConfig);
        context.startActivity(intent);
    }
}
