package io.xdag.xdagwallet.core;

import java.util.ArrayList;
import java.util.List;

import io.xdag.xdagwallet.crypto.ECKeyPair;

import static io.xdag.xdagwallet.core.XdagField.FieldType;

public class BlockBuilder {
    public static Block generateTransactionBlock( ECKeyPair key, long xdagTime, Address from, Address to, long amount, String remark) {
        List<Address> refs = new ArrayList<>();
        refs.add(new Address(from.getHashLow(), FieldType.XDAG_FIELD_IN, amount)); // key1
        refs.add(new Address(to.getHashLow(), FieldType.XDAG_FIELD_OUT, amount));
        List<ECKeyPair> keys = new ArrayList<>();
        keys.add(key);
        if(remark==null||remark.length()==0){
            remark = null;
        }
        return new Block(xdagTime, refs, null, false, keys, remark, 0); // orphan
    }
    public static Block generateAddressBlock(ECKeyPair key, long xdagTime){
        Block block = new Block(xdagTime,null,null,false,null,null,-1);
        block.signOut(key);
        return block;
    }
}
