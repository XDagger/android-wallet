package io.xdag.common.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * created by lxm on 2018/7/25.
 */
public class IntentUtil {

    public static void openBrowser(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }
}
