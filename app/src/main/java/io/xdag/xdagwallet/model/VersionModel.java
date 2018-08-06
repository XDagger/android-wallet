package io.xdag.xdagwallet.model;

/**
 * created by ssyijiu  on 2018/7/29
 */
public class VersionModel {

    public int versionCode;
    public String versionName;
    public String url;


    @Override public String toString() {
        return "VersionModel{" +
            "versionCode=" + versionCode +
            ", versionName='" + versionName + '\'' +
            ", url='" + url + '\'' +
            '}';
    }
}
