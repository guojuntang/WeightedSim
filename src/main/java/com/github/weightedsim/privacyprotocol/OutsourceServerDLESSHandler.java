package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPublicParameter;

public class OutsourceServerDLESSHandler extends OutsourceServerProtocolHandler{
    public OutsourceServerDLESSHandler(SHEPublicParameter pb, SHECipher E_mins_1) {
        super(pb, E_mins_1);
    }

    public SHECipher phrase1(SHECipher E_m1, SHECipher E_m2){
        return PrivacyProtocolHelper.DLESSPhrase1(E_m1, E_m2, getE_mins_1(), getR1(), getR2(), getCoin(), getPb());
    }

    public boolean phrase3(boolean x){
        return PrivacyProtocolHelper.DLESSPhrase3(x, getCoin());
    }

}
