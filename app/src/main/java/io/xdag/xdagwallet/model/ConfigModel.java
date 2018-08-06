package io.xdag.xdagwallet.model;

/**
 * created by ssyijiu  on 2018/7/29
 */
public class ConfigModel {

    public int versionCode;
    public String versionName;
    public String url;
    public String default_pool;
    public String transfer_url;


    @Override public String toString() {
        return "ConfigModel{" +
            "versionCode=" + versionCode +
            ", versionName='" + versionName + '\'' +
            ", url='" + url + '\'' +
            ", default_pool='" + default_pool + '\'' +
            ", transfer_url='" + transfer_url + '\'' +
            '}';
    }
}
