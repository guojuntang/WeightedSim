package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;

public class AssistServerDWITHINHandler extends AssistServerProtocolHandler{
    public AssistServerDWITHINHandler(SHEPrivateKey sk){
        super(sk);
    }

    public boolean phrase2(SHECipher E_x){
        return PrivacyProtocolHelper.DWITHINPhrase2(E_x, getSk());
    }
}
