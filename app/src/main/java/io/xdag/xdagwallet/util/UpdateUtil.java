package io.xdag.xdagwallet.util;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.xdag.common.util.IntentUtil;
import io.xdag.xdagwallet.BuildConfig;
import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.model.VersionModel;

/**
 * created by ssyijiu  on 2018/7/30
 */
public class UpdateUtil {

    public static void update(final VersionModel versionModel, final LinearLayout versionLayout, TextView tvDesc, TextView tvUpdate, TextView tvClose) {
        if (versionModel.versionCode > BuildConfig.VERSION_CODE) {

            final Context context = versionLayout.getContext();
            versionLayout.setVisibility(View.VISIBLE);
            tvDesc.setText(context.getString(R.string.new_version_available, versionModel.versionName));

            tvUpdate.setOnClickListener(v -> IntentUtil.openBrowser(context, versionModel.url));

            tvClose.setOnClickListener(v -> versionLayout.setVisibility(View.GONE));

        } else {
            versionLayout.setVisibility(View.GONE);
        }
    }
}
