package com.github.weightedsim.encryptedrtree.geometry;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.SymHomEnc.SymHomEnc;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;

public class EncryptedPoint {
    private SHECipher[] x;

    public EncryptedPoint(SHECipher[] x){
        this.x = x;
    }

    public EncryptedPoint(int[] x, SHEPrivateKey sk){
        SHECipher[] x_list = new SHECipher[x.length];
        for (int i = 0; i < x.length; i++) {
            x_list[i] = SymHomEnc.enc(x[i], sk);
        }
        this.x = x_list;
    }

    public boolean intersect(EncryptedRectangle r, DWITHINProtocol protocol){
        for (int i = 0; i < x.length; i++) {
            if (!protocol.run(r.getMins()[i], x[i], r.getMaxes()[i])){
                return false;
            }
        }
        return true;
    }
}
