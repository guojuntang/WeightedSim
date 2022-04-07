package com.github.weightedsim.entities;

import com.github.SymHomEnc.SHEPrivateKey;

public class AssistServer {
    private SHEPrivateKey sk;

    public AssistServer(SHEPrivateKey sk){
        this.sk = sk;
    }

    public SHEPrivateKey getSk() {
        return sk;
    }
}
