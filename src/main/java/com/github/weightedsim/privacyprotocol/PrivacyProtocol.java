package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.weightedsim.entities.AssistServer;
import com.github.weightedsim.entities.OutsourceServer;

abstract public class PrivacyProtocol {
    private OutsourceServer s1;
    private AssistServer s2;

    public PrivacyProtocol(OutsourceServer s1, AssistServer s2){
        this.s1 = s1;
        this.s2 = s2;
    }

    public OutsourceServer getS1() {
        return s1;
    }

    public AssistServer getS2() {
        return s2;
    }
}
