package io.xdag.xdagwallet.util;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import io.xdag.xdagwallet.BuildConfig;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/7/6.
 * <p>
 * desc : to set toolbar show status
 */
public class ToolbarUtil {

    public static void setToolbar(int pos, Toolbar toolbar) {
        if (toolbar == null) {
            return;
        }

        switch (pos) {
            case 0:
                toolbar.setVisibility(View.GONE);
                break;
            case 1:
                toolbar.setTitle(R.string.receive_xdag);
                toolbar.setVisibility(View.VISIBLE);
                toolbar.getMenu().setGroupVisible(0, false);
                break;
            case 2:
                toolbar.setTitle(R.string.send_xdag);
                toolbar.setVisibility(View.VISIBLE);
                toolbar.getMenu().setGroupVisible(0, true);
                break;
            case 3:
                toolbar.setTitle(R.string.more);
                toolbar.setVisibility(View.VISIBLE);
                toolbar.getMenu().setGroupVisible(0, false);
                break;
            default:
        }
    }
}
