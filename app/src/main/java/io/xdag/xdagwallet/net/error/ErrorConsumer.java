package io.xdag.xdagwallet.net.error;

import android.app.Activity;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;

import io.reactivex.functions.Consumer;
import io.xdag.common.tool.MLog;
import io.xdag.xdagwallet.util.AlertUtil;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * created by lxm on 2018/7/19.
 * <p>
 * handle exception
 */
public class ErrorConsumer implements Consumer<Throwable> {

    private Activity activity;
    private static final Gson gson = new Gson();


    public ErrorConsumer() {
    }


    public ErrorConsumer(Activity activity) {
        this.activity = activity;
    }


    @Override
    public void accept(Throwable throwable) {
        MLog.i(throwable.getMessage());
        String message = throwable.getMessage();
        // ignore the time out of https://raw.githubusercontent.com/
        if (message.contains("githubusercontent")) {
            return;
        }
        if (throwable instanceof HttpException) {
            // parse error message
            HttpException httpException = (HttpException) throwable;
            try {
                ResponseBody errorBody = httpException.response().errorBody();
                if (errorBody != null) {
                    ErrorResponse errorResponse = gson.fromJson(errorBody.string(),
                            ErrorResponse.class);
                    if (errorResponse != null && !TextUtils.isEmpty(errorResponse.message)) {
                        message = errorResponse.message;
                    }

                }
            } catch (IOException ignored) {
            }

        }
        AlertUtil.show(activity, message);
    }
}
