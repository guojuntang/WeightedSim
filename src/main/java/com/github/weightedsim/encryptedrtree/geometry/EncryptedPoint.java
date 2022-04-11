package com.github.weightedsim.encryptedrtree.geometry;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.SymHomEnc.SymHomEnc;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import com.github.weightedsim.privacyprotocol.PrivacyProtocol;

public class EncryptedPoint implements EncryptedGeometry {
    private SHECipher[] x;

    public EncryptedPoint(SHECipher[] x){
        this.x = x;
    }

    @Override
    public SHECipher getMin(int i) {
        return x[i];
    }

    @Override
    public SHECipher getMax(int i) {
        return x[i];
    }

    @Override
    public SHECipher[] getMaxes() {
        return x;
    }

    @Override
    public SHECipher[] getMins() {
        return x;
    }

    public SHECipher[] getX() {
        return x;
    }

    public SHECipher getX(int i) {
        return x[i];
    }

    public EncryptedPoint(double[] x, SHEPrivateKey sk){
        SHECipher[] x_list = new SHECipher[x.length];
        for (int i = 0; i < x.length; i++) {
            x_list[i] = SymHomEnc.enc((int)x[i], sk);
        }
        this.x = x_list;
    }

    public EncryptedPoint(int[] x, SHEPrivateKey sk){
        SHECipher[] x_list = new SHECipher[x.length];
        for (int i = 0; i < x.length; i++) {
            x_list[i] = SymHomEnc.enc(x[i], sk);
        }
        this.x = x_list;
    }

    public boolean intersect(EncryptedRectangle r, PrivacyProtocol protocol){
        DWITHINProtocol dwithinProtocol = (DWITHINProtocol) protocol;
        for (int i = 0; i < x.length; i++) {
            if (!dwithinProtocol.run(r.getMins()[i], x[i], r.getMaxes()[i])){
                return false;
            }
        }
        return true;
    }
}
