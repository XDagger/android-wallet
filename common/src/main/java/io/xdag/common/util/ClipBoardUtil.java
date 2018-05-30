package io.xdag.common.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import io.xdag.common.Common;

public class ClipBoardUtil {

    private ClipBoardUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("ClipBoardUtil cannot be instantiated !");
    }


    public static void copyToClipBoard(String text) {
        ClipData clipData = ClipData.newPlainText("common_copy", text);
        ClipboardManager manager =
            (ClipboardManager) Common.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            manager.setPrimaryClip(clipData);
        }
    }
}
