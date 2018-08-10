package io.xdag.xdagwallet.model;

/**
 * created by ssyijiu  on 2018/8/6
 */
public class ConfigModel {

    public String defaultPool;
    public String transactionHost;


    @Override public String toString() {
        return "ConfigModel{" +
            "defaultPool='" + defaultPool + '\'' +
            ", transactionHost='" + transactionHost + '\'' +
            '}';
    }
}
