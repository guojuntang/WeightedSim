package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.weightedsim.entities.AssistServer;

public class AssistServerDLESSHandler extends AssistServerProtocolHandler{
    public AssistServerDLESSHandler(AssistServer s){
        super(s.getSk());
    }

    public AssistServerDLESSHandler(SHEPrivateKey sk){
        super(sk);
    }

    public boolean phrase2(SHECipher E_x){
        return PrivacyProtocolHelper.DLESSPhrase2(E_x, getSk());
    }
}
