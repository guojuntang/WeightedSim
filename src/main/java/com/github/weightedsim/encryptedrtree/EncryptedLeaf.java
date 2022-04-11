package com.github.weightedsim.encryptedrtree;

import com.github.SymHomEnc.SHEPrivateKey;
import com.github.davidmoten.rtreemulti.Node;
import com.github.davidmoten.rtreemulti.geometry.HasGeometry;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedGeometry;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedRectangle;
import com.github.weightedsim.privacyprotocol.PrivacyProtocol;

/**
 * Encrypted from Entry in multi-rtree
 * @param <T>
 */
public class EncryptedLeaf<T> extends EncryptedNode {
    private T value;

    public EncryptedLeaf(T value, EncryptedRectangle mbr){
        super(mbr);
        this.value = value;
    }


    public EncryptedLeaf(T value, HasGeometry geo, SHEPrivateKey sk){
        super(geo, sk);
        this.value = value;
    }

    public T getValue() {
        return value;
    }


    @Override
    public boolean test(EncryptedRectangle r, PrivacyProtocol protocol) {
        return getMbr().intersect(r, protocol);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
