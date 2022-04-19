package com.github.weightedsim.entities;

import com.github.SymHomEnc.SHEPrivateKey;

import java.util.ArrayList;
import java.util.List;

public class AssistServer {
    private SHEPrivateKey sk;

    public AssistServer(SHEPrivateKey sk){
        this.sk = sk;
    }

    public SHEPrivateKey getSk() {
        return sk;
    }

    // TODO: multithreading
    public List<RefinementResult> refinement(List<RefinementCandidate> candidate){
        List<RefinementResult> result = new ArrayList<>();
        for (RefinementCandidate c:
             candidate) {
            result.add(new RefinementResult(c, sk));
        }
        return result;
    }
}
