package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;

public class AssistServerSLESSEHandler extends AssistServerProtocolHandler{
    public AssistServerSLESSEHandler(SHEPrivateKey sk){
        super(sk);
    }

    public SHECipher phrase2 (SHECipher E_x){
        return PrivacyProtocolHelper.SLESSEPhrase2(E_x, getSk());
    }
}
