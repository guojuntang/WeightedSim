package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.weightedsim.entities.AssistServer;

public class AssistServerSLESSEHandler extends AssistServerProtocolHandler{
    public AssistServerSLESSEHandler(SHEPrivateKey sk){
        super(sk);
    }

    public AssistServerSLESSEHandler(AssistServer s){
        super(s.getSk());
    }

    public SHECipher phrase2 (SHECipher E_x){
        return PrivacyProtocolHelper.SLESSEPhrase2(E_x, getSk());
    }
}
