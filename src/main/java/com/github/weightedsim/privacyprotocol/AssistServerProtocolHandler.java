package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHEPrivateKey;

abstract class AssistServerProtocolHandler {
    private SHEPrivateKey sk;
    public AssistServerProtocolHandler(SHEPrivateKey sk){
        this.sk = sk;
    }

    public SHEPrivateKey getSk() {
        return sk;
    }
}
