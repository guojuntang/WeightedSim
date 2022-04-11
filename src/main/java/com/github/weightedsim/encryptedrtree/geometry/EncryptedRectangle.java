package com.github.weightedsim.encryptedrtree.geometry;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.SymHomEnc.SymHomEnc;
import com.github.weightedsim.encryptedrtree.EncryptedNonLeaf;
import com.github.weightedsim.privacyprotocol.*;


public class EncryptedRectangle implements EncryptedGeometry {
    private SHECipher[] mins;
    private SHECipher[] maxes;

    public EncryptedRectangle(SHECipher[] maxes, SHECipher[] mins){
        if (mins.length != maxes.length)
            throw new RuntimeException("Encrypted Rectangle: length error.");
        this.maxes = maxes;
        this.mins = mins;
    }

    public EncryptedRectangle(double[] maxes, double[] mins, SHEPrivateKey sk){
        if (mins.length != maxes.length)
            throw new RuntimeException("Encrypted Rectangle: length error.");

        SHECipher[] maxes_ciphers = new SHECipher[maxes.length];
        SHECipher[] mins_ciphers = new SHECipher[mins.length];

        for (int i = 0; i < maxes.length; i++) {
            maxes_ciphers[i] = SymHomEnc.enc((int)maxes[i], sk);
            mins_ciphers[i] = SymHomEnc.enc((int)mins[i], sk);
        }
        this.maxes = maxes_ciphers;
        this.mins = mins_ciphers;
    }

    public EncryptedRectangle(int[] maxes, int[] mins, SHEPrivateKey sk){
        if (mins.length != maxes.length)
            throw new RuntimeException("Encrypted Rectangle: length error.");

        SHECipher[] maxes_ciphers = new SHECipher[maxes.length];
        SHECipher[] mins_ciphers = new SHECipher[mins.length];

        for (int i = 0; i < maxes.length; i++) {
            maxes_ciphers[i] = SymHomEnc.enc(maxes[i], sk);
            mins_ciphers[i] = SymHomEnc.enc(mins[i], sk);
        }
        this.maxes = maxes_ciphers;
        this.mins = mins_ciphers;
    }

    @Override
    public SHECipher[] getMaxes() {
        return maxes;
    }

    @Override
    public SHECipher[] getMins() {
        return mins;
    }

    @Override
    public SHECipher getMax(int i) {
        return maxes[i];
    }

    @Override
    public SHECipher getMin(int i) {
        return mins[i];
    }

    public boolean intersect(EncryptedRectangle r, PrivacyProtocol protocol){
        DLESSProtocol dlessProtocol = (DLESSProtocol) protocol;
        for (int i = 0; i < mins.length; i++) {
            // this.maxes < r.mins or r.maxes < this.mins
            if (dlessProtocol.run(maxes[i], r.mins[i]) || dlessProtocol.run(r.maxes[i], mins[i])){
                return false;
            }
        }
        return true;
    }

}
