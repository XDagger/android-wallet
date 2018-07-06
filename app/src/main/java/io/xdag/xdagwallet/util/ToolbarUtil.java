package io.xdag.xdagwallet.util;

import android.support.v7.widget.Toolbar;
import android.view.View;
import io.xdag.xdagwallet.R;

/**
 * created by lxm on 2018/7/6.
 *
 * desc : to set toolbar show status
 */
public class ToolbarUtil {

    public static void setToolbar(int itemId, Toolbar toolbar) {
        if (toolbar == null) {
            return;
        }

        switch (itemId) {
            case R.id.navigation_home:
                toolbar.setVisibility(View.GONE);
                break;
            case R.id.navigation_receive:
                toolbar.setTitle(R.string.receive_xdag);
                toolbar.setVisibility(View.VISIBLE);
                toolbar.getMenu().setGroupVisible(0, false);
                break;
            case R.id.navigation_send:
                toolbar.setTitle(R.string.send_xdag);
                toolbar.setVisibility(View.VISIBLE);
                toolbar.getMenu().setGroupVisible(0, true);
                break;
            case R.id.navigation_setting:
                toolbar.setTitle(R.string.setting);
                toolbar.setVisibility(View.VISIBLE);
                toolbar.getMenu().setGroupVisible(0, false);
                break;
            default:
        }
    }
}
