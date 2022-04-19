package com.github.weightedsim.entities;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.SymHomEnc.SymHomEnc;

import java.math.BigInteger;
import java.util.List;

public class RefinementResult {
    private BigInteger[] x_pi;
    private BigInteger b_pi;
    private List<byte[]> dataMasks;
    private byte[] resultMasks;

    public byte[] getResultMasks() {
        return resultMasks;
    }

    public BigInteger getB_pi() {
        return b_pi;
    }

    public BigInteger[] getX_pi() {
        return x_pi;
    }

    public List<byte[]> getDataMasks() {
        return dataMasks;
    }

    public RefinementResult(RefinementCandidate candidate, SHEPrivateKey sk){
        SHECipher[] x_vector = candidate.getEncryptedData();
        int size = x_vector.length;
        this.x_pi = new BigInteger[size];
        // decrypt x_pi
        for (int i = 0; i < size; i++) {
            x_pi[i] = SymHomEnc.dec(x_vector[i], sk);
        }
        this.b_pi = SymHomEnc.dec(candidate.getResultBit(), sk);
        this.dataMasks = candidate.getDataMasks();
        this.resultMasks = candidate.getResultMasks();
    }

}
