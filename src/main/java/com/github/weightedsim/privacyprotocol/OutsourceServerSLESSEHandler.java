package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPublicParameter;

public class OutsourceServerSLESSEHandler extends OutsourceServerProtocolHandler{
    public OutsourceServerSLESSEHandler(SHEPublicParameter pb, SHECipher E_mins_1){
        super(pb, E_mins_1);
    }

    public SHECipher phrase1(SHECipher E_m1, SHECipher E_m2){
        return  PrivacyProtocolHelper.SLESSEPhrase1(E_m1, E_m2, getE_mins_1(), getR1(), getR2(), getCoin(), getPb());
    }

    public SHECipher phrase3(SHECipher E_x){
        return  PrivacyProtocolHelper.SLESSEPhrase3(E_x, getE_mins_1(), getCoin(), getPb());
    }

}
