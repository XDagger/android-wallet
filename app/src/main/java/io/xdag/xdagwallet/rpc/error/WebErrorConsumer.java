package io.xdag.xdagwallet.rpc.error;

import android.util.Log;

import io.reactivex.functions.Consumer;

public class WebErrorConsumer implements Consumer<Throwable> {
    @Override
    public void accept(Throwable throwable) throws Exception {

        String message = throwable.getMessage();
        Log.e("Error",message);
        Log.e("Error",throwable.toString());
    }

}
