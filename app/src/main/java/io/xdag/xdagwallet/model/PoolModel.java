package io.xdag.xdagwallet.model;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

import io.xdag.xdagwallet.R;
import io.xdag.xdagwallet.config.Config;

/**
 * created by ssyijiu  on 2018/7/29
 */
public class PoolModel {
    public String address;
    public int selectedImage;

    public PoolModel(String address) {
        this.address = address;
        this.selectedImage = 0;
    }


    private static List<PoolModel> sPoolModelList = Arrays.asList(
            new PoolModel("xdagscan.com:13654"),
            new PoolModel("xdagmine.com:13654")
    );

    public static List<PoolModel> getPoolList() {
        for (int i = 0; i < sPoolModelList.size(); i++) {
            if (TextUtils.equals(Config.getPoolAddress(), sPoolModelList.get(i).address)) {
                sPoolModelList.get(i).selectedImage = R.drawable.ic_pool_selected;
            } else {
                sPoolModelList.get(i).selectedImage = 0;
            }
        }

        return sPoolModelList;
    }
}
