package io.xdag.xdagwallet.model;

/**
 * created by ssyijiu  on 2018/7/29
 */
public class UpdateModel {

    public int versionCode;
    public String versionName;
    public String url;

    @Override
    public String toString() {
        return "UpdateModel{" +
                "versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
