package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;

public class AssistServerDLESSHandler extends AssistServerProtocolHandler{
    public AssistServerDLESSHandler(SHEPrivateKey sk){
        super(sk);
    }

    public boolean phrase2(SHECipher E_x){
        return PrivacyProtocolHelper.DLESSPhrase2(E_x, getSk());
    }
}
