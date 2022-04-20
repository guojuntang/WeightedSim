package com.github.weightedsim;

import com.github.SymHomEnc.*;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.weightedsim.encryptedrtree.EncryptedLeaf;
import com.github.weightedsim.encryptedrtree.EncryptedRTree;
import com.github.weightedsim.entities.*;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import com.github.weightedsim.privacyprotocol.SLESSEProtocol;
import com.github.weightedsim.util.DataUtil;
import com.github.weightedsim.util.PivotUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Main {
    // This class will simulate the entire process of weightedSim
    private static SHEParameters param;
    private static SHEPrivateKey sk;
    private static SHEPublicParameter pb;
    private static SHEPublicKey pk;
    private static SHECipher E_mins_1;
    private static OutsourceServer s1;
    private static AssistServer s2;
    private static DLESSProtocol less_protocol;
    private static DWITHINProtocol within_protocol;
    private static SLESSEProtocol lesse_protocol;
    private static List<double[]> pivots;
    private static List<double[]> dataset;
    private static QueryUser q1;


    private static void init(int k, String filename){
        param = new SHEParameters(1200, SHEParameters.K1, SHEParameters.K2);
        sk = param.getSHEPrivateKey();
        pb = param.getSHEPublicParameter();
        pk = param.getSHEPublicKey();
        E_mins_1 = SymHomEnc.enc(-1, sk);
        s1 = new OutsourceServer(pb, E_mins_1);
        s2 = new AssistServer(sk);
        less_protocol = new DLESSProtocol(s1, s2);
        within_protocol = new DWITHINProtocol(s1, s2);
        lesse_protocol = new SLESSEProtocol(s1, s2);
        dataset = DataUtil.readCsvData(filename);
        pivots = PivotUtil.choosePivots(dataset, k);
        q1 = new QueryUser(pk, pivots);
    }

    public static RTree<SHECipher[], Point> buildUpRtreeIndexAndEncryptData(){
        int dimension = pivots.size();
        RTree<SHECipher[], Point> tree = RTree.dimensions(dimension).star().create();
        for (double[] record:
             dataset) {
            tree = tree.add(DataUtil.convertAndEncryptVector(record, 100, sk), DataUtil.createPointFromPivots(pivots, record, 100));
        }
        return tree;
    }


    public static void main(String[] args) {
        // Initialization
        init(10, "/test.csv");

        // Build up Rtree Index and encrypt only the data
        RTree<SHECipher[], Point> rTree = buildUpRtreeIndexAndEncryptData();

        // Encrypt the tree information
        EncryptedRTree<SHECipher[]> encryptedRTree = new EncryptedRTree<>(rTree, sk);

        s1.setUpOutsource(encryptedRTree, less_protocol, within_protocol, lesse_protocol);

        double[] w = {0.1, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.05, 0.1, 0.1, 0.05, 0.15, 0.1};

        // Weighted Distance Similarity Query
        // Generate encrypted token
        QueryToken queryToken = new QueryToken(dataset.get(50), w, 20.0);
        EncryptedToken encryptedToken = q1.genEncryptedToken(queryToken);

        // Filtration
        List<EncryptedLeaf<SHECipher>> filtration_result = s1.filtration(encryptedToken);

        // Refinement

        List<RefinementCandidate> s1_refinement_result = s1.refinement(encryptedToken, filtration_result);
        List<RefinementResult> s2_refinement_result = s2.refinement(s1_refinement_result);
        List<BigInteger[]> query_user_refinement = q1.refinement(encryptedToken.getSsk(), s2_refinement_result);

        //print info
        System.out.println("Candidate set size: " + filtration_result.size());
        System.out.println("Result size: " + query_user_refinement.size());

        // print result
        for (BigInteger[] a:
             query_user_refinement) {
            double[] double_vector = DataUtil.bigIntVectorToDoubleVector(a, 100);
            for (int i = 0; i < double_vector.length; i++) {
                System.out.print(double_vector[i]);
                System.out.print(" ,");
            }
            System.out.println();
        }

    }
}
