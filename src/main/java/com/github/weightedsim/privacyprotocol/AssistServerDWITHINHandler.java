package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.weightedsim.entities.AssistServer;

public class AssistServerDWITHINHandler extends AssistServerProtocolHandler{
    public AssistServerDWITHINHandler(SHEPrivateKey sk){
        super(sk);
    }

    public AssistServerDWITHINHandler(AssistServer s){
        super(s.getSk());
    }

    public boolean phrase2(SHECipher E_x){
        return PrivacyProtocolHelper.DWITHINPhrase2(E_x, getSk());
    }
}
