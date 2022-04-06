package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPublicParameter;

public class OutsourceServerDWITHINHandler extends OutsourceServerProtocolHandler{
    public OutsourceServerDWITHINHandler(SHEPublicParameter pb, SHECipher E_mins_1){
        super(pb, E_mins_1);
    }

    public SHECipher phrase1(SHECipher E_m1, SHECipher E_m2, SHECipher E_m3){
        return PrivacyProtocolHelper.DWITHINPhrase1(E_m1, E_m2, E_m3, getE_mins_1(), getR1(), getR2(), getCoin(), getPb());
    }

    public boolean phrase3(boolean x){
        return PrivacyProtocolHelper.DWITHINPhrase3(x, getCoin());
    }

}
