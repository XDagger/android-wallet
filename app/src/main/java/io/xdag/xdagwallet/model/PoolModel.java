package io.xdag.xdagwallet.model;

import java.io.Serializable;

/**
 * created by ssyijiu  on 2018/7/29
 */
public class PoolModel implements Serializable{
    public String address;
    public int selectedImage;


    public PoolModel(String address) {
        this.address = address;
        this.selectedImage = 0;
    }


    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PoolModel poolModel = (PoolModel) o;

        return address != null ? address.equals(poolModel.address) : poolModel.address == null;
    }


    @Override public int hashCode() {
        return address != null ? address.hashCode() : 0;
    }


    public boolean isSelected() {
        return selectedImage != 0;
    }
}
