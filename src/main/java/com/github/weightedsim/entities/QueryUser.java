package com.github.weightedsim.entities;

import com.github.SymHomEnc.SHEPublicKey;
import com.github.SymHomEnc.SHEPublicParameter;

public class QueryUser {
    private SHEPublicKey pk;
    public QueryUser(SHEPublicKey pk){
        this.pk = pk;
    }
}
