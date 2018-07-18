package io.xdag.common.tool;

import android.text.TextUtils;
import android.util.Log;
import java.util.Locale;

public class MLog {

    /**
     * 日志的TAG
     */
    public static String sTag = "XdagWallet";

    /**
     * 日志级别，默认为 V
     */
    private static LogLev sLogLev;


    static {
        sLogLev = LogLev.V;
    }


    private MLog() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("MLog cannot be instantiated !");
    }


    /**
     * 设置日志级别，只有高于设置级别的日志才打印。
     *
     * @param lev {@link LogLev#V,LogLev#D,LogLev#I,LogLev#W,LogLev#E,LogLev#NO_LOG}
     */
    public static void setLogLev(LogLev lev) {
        sLogLev = lev;
    }


    public static void v(Object obj) {
        if (sLogLev.lev <= LogLev.V.lev) {
            printLog(sTag, LogLev.V, getMsg(obj));
        }
    }


    public static void v(String tag, Object obj) {

        if (sLogLev.lev <= LogLev.V.lev) {
            printLog(tag, LogLev.V, getMsg(obj));
        }
    }


    public static void d(Object obj) {
        if (sLogLev.lev <= LogLev.D.lev) {
            printLog(sTag, LogLev.D, getMsg(obj));
        }
    }


    public static void d(String tag, Object obj) {

        if (sLogLev.lev <= LogLev.D.lev) {
            printLog(tag, LogLev.D, getMsg(obj));
        }
    }


    public static void i(Object obj) {
        if (sLogLev.lev <= LogLev.I.lev) {
            printLog(sTag, LogLev.I, getMsg(obj));
        }
    }


    public static void i(String tag, Object obj) {

        if (sLogLev.lev <= LogLev.I.lev) {
            printLog(tag, LogLev.I, getMsg(obj));
        }
    }


    public static void w(Object obj) {
        if (sLogLev.lev <= LogLev.W.lev) {
            printLog(sTag, LogLev.W, getMsg(obj));
        }
    }


    public static void w(String tag, Object obj) {

        if (sLogLev.lev <= LogLev.W.lev) {
            printLog(tag, LogLev.W, getMsg(obj));
        }
    }


    public static void e(Object obj) {
        if (sLogLev.lev <= LogLev.E.lev) {
            printLog(sTag, LogLev.E, getMsg(obj));
        }
    }


    public static void e(String tag, Object obj) {

        if (sLogLev.lev <= LogLev.E.lev) {
            printLog(tag, LogLev.E, getMsg(obj));
        }
    }


    /**
     * 日志打印
     *
     * @param lev 级别
     * @param msg Message
     */
    private static void printLog(String defaultTag, LogLev lev, String msg) {

        // 记录下全局 sTag
        String tmpTAG = sTag;
        sTag = defaultTag;

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        byte index = 4;
        String fileName = stackTrace[index].getFileName();
        String className = stackTrace[index].getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        String tag = "[%s.%s(%s:%d)]";
        tag = String.format(Locale.getDefault(), tag, className, methodName, fileName, lineNumber);
        tag = TextUtils.isEmpty(sTag) ? tag : sTag + ":" + tag;

        switch (lev) {
            case V:
                Log.v(tag, msg);
                break;
            case D:
                Log.d(tag, msg);
                break;
            case I:
                Log.i(tag, msg);
                break;
            case W:
                Log.w(tag, msg);
                break;
            case E:
                Log.e(tag, msg);
                break;
            case NO_LOG:
                break;
            default:
        }

        // 恢复全局 sTag
        sTag = tmpTAG;
    }


    private static String getMsg(Object obj) {
        return obj != null && obj.toString() != null ? obj.toString() : "null";
    }


    private enum LogLev {
        V(1),
        D(2),
        I(3),
        W(4),
        E(5),
        NO_LOG(6);

        int lev = 1;


        LogLev(int i) {
            this.lev = i;
        }
    }
}

