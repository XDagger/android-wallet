package io.xdag.xdagwallet.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.xdag.common.tool.MLog;

/**
 * Created by ssyijiu on 2022/11/3.
 */
public class BackupUtils {

    public static boolean copyInternalFileToExternal(Context context, String srcPath, Uri externalUri) {
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean result;
        try {
            outputStream = contentResolver.openOutputStream(externalUri);
            File srcFile = new File(srcPath);
            if (srcFile.exists()) {
                inputStream = new FileInputStream(srcFile);

                int readCount;
                byte[] buffer = new byte[1024];
                while ((readCount = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readCount);
                    outputStream.flush();
                }
            }
            result = true;
        } catch (Exception e) {
            MLog.e("copy InternalFile To ExternalUri. e = " + e.toString());
            result = false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                MLog.d("input stream and output stream close successful.");
            } catch (Exception e) {
                e.printStackTrace();
                MLog.e("input stream and output stream close fail. e = " + e.toString());
            }
        }
        return result;
    }

    public static boolean copyFieUriToInnerStorage(Context context, Uri srcUri, File destFile) {
        InputStream ins = null;
        FileOutputStream fos = null;
        boolean result;
        try {
            ins = context.getContentResolver().openInputStream(srcUri);
            if (destFile.exists()) {
                destFile.delete();
            }
            fos = new FileOutputStream(destFile);
            byte[] buffer = new byte[4096];
            int redCount;
            while ((redCount = ins.read(buffer)) >= 0) {
                fos.write(buffer, 0, redCount);
            }
            result = true;
        } catch (Exception e) {
            result = false;
            MLog.e(" copy file uri to inner storage e = " + e.toString());
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.getFD().sync();
                    fos.close();
                }
                if (ins != null) {
                    ins.close();
                }
            } catch (Exception e) {
                MLog.e(" close stream e = " + e.toString());
            }
        }
        return result;
    }
}
