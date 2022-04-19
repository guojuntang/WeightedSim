package com.github.weightedsim.entities;

import com.github.SymHomEnc.SHECipher;
import javafx.util.Pair;

import java.math.BigInteger;
import java.util.List;

public class RefinementCandidate {
    private SHECipher[] encryptedData;
    private SHECipher resultBit;
    private List<byte[]> dataMasks;
    private byte[] resultMasks;

    RefinementCandidate(SHECipher[] encryptedData, SHECipher resultBit, List<byte[]> dataMasks, byte[] resultMasks){
        this.dataMasks = dataMasks;
        this.resultMasks = resultMasks;
        this.encryptedData = encryptedData;
        this.resultBit = resultBit;
    }

    RefinementCandidate(Pair<SHECipher[], List<byte[]>> data, Pair<SHECipher, byte[]> bit){
        this.encryptedData = data.getKey();
        this.dataMasks = data.getValue();
        this.resultBit = bit.getKey();
        this.resultMasks = bit.getValue();
    }


    public List<byte[]> getDataMasks() {
        return dataMasks;
    }

    public byte[] getResultMasks() {
        return resultMasks;
    }

    public SHECipher getResultBit() {
        return resultBit;
    }

    public SHECipher[] getEncryptedData() {
        return encryptedData;
    }
}

