package com.github.weightedsim.encryptedrtree.geometry;

import com.github.SymHomEnc.SHECipher;
import com.github.weightedsim.privacyprotocol.PrivacyProtocol;

public interface EncryptedGeometry {
    boolean intersect(EncryptedRectangle r, PrivacyProtocol protocol);


    public SHECipher[] getMaxes();

    public SHECipher[] getMins();


    public SHECipher getMax(int i);

    public SHECipher getMin(int i);

}
