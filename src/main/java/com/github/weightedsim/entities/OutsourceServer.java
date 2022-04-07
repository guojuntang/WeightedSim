package com.github.weightedsim.entities;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPublicParameter;

public class OutsourceServer {
    private SHEPublicParameter pb;
    private SHECipher E_mins_1;

    public OutsourceServer(SHEPublicParameter pb, SHECipher E_mins_1){
        this.pb = pb;
        this.E_mins_1 = E_mins_1;
    }

    public SHEPublicParameter getPb() {
        return pb;
    }

    public SHECipher getE_mins_1() {
        return E_mins_1;
    }
}
