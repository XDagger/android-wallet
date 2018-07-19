package io.xdag.xdagwallet.api.xdagscan;

import io.reactivex.functions.Function;

/**
 * created by lxm on 2018/7/19.
 */
public class Response2Model<T> implements Function<BaseResponse<T>, T> {

    @Override public T apply(BaseResponse<T> response) throws Exception {
        if (response.isSuccess()) {
            if (response.data != null) {
                return response.data;
            }
        }
        throw new Exception(response.message);
    }
}
