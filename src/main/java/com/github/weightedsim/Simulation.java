package com.github.weightedsim;

import com.github.SymHomEnc.*;
import com.github.davidmoten.rtreemulti.Entry;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.weightedsim.encryptedrtree.EncryptedLeaf;
import com.github.weightedsim.encryptedrtree.EncryptedRTree;
import com.github.weightedsim.entities.*;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import com.github.weightedsim.privacyprotocol.SLESSEProtocol;
import com.github.weightedsim.util.DataMag;
import com.github.weightedsim.util.DataUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation {
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
    private static List<double[]> dataset;
    private static QueryUser q1;
    private static String filename = "/EEGEyeState.csv";
    private static boolean is_star = true;
    private static Random random = new Random();
    private static int cycles = 1000;
    private static final DataMag dataMag = new DataMag(100, 100, 1000);


    private static void init(){
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
        dataset = readCsvData(filename, 14, 14800);
        q1 = new QueryUser(pk);
    }


    /**
     *
     * @param filename
     * @param d data dimensions
     * @param n data number
     * @return
     */
    public static List<double[]> readCsvData(String filename,int d, int n){
        try {
            List<double[]> result = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Simulation.class.getResourceAsStream(filename)));
            String line;
            String[] string_buffer;
            for (int i = 0; i < n; i++) {
                line = bufferedReader.readLine();
                string_buffer = line.split(",");
                if(d > string_buffer.length){
                    throw new Exception("Data dimension is out of range");
                }
                double[] tmp = new double[d];
                for (int j = 0; j < d; j++) {
                    tmp[j] = Double.valueOf(string_buffer[j]);
                }
                result.add(tmp);

            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static RTree<SHECipher[], Point> buildUpRtreeIndexAndEncryptData(){
        int dimension = dataset.get(0).length;
        List<Entry<SHECipher[], Point>> entries = new ArrayList<>();
        for (double[] record:
                dataset) {
            SHECipher[] cipher = DataUtil.convertAndEncryptVector(record, dataMag.getData_mag(), sk);
            entries.add(Entry.entry(cipher, DataUtil.createPointFromData(record, dataMag.getData_mag())));
        }
        RTree<SHECipher[], Point> tree;
        if (is_star) {
            tree = RTree.dimensions(dimension).star().maxChildren(16).<SHECipher[], Point>create().add(entries);
        }else{
            tree = RTree.dimensions(dimension).maxChildren(16).<SHECipher[], Point>create().add(entries);
        }
        return tree;
    }

    public static void main(String[] args) {
        init();
        // Build up Rtree Index and encrypt only the data
        RTree<SHECipher[], Point> rTree = buildUpRtreeIndexAndEncryptData();

        // Encrypt the tree information
        EncryptedRTree<SHECipher[]> encryptedRTree = new EncryptedRTree<>(rTree, sk);

        s1.setUpOutsource(encryptedRTree, less_protocol, within_protocol, lesse_protocol);


        // Weighted Distance Similarity Query
        // Generate encrypted token
        double[] w = DataUtil.genWeighted(dataset.get(0).length);
        int randIndex = random.nextInt(dataset.size() - 1) + 1;

        // Weighted Distance Similarity Query
        // Generate encrypted token
        QueryToken queryToken = new QueryToken(dataset.get(randIndex), w, 5.0);
        EncryptedToken encryptedToken = q1.genEncryptedToken(queryToken, dataMag);

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
