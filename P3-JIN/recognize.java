/*
 * Recognize-Implemenation of the forward procedure and calculate the observation probability
 * BU CS440 2015 Spring
 * Name:Ding Jin, Fuqing Wang, Xi Zhang
 */

import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;

public class recognize {
    
    //helper method to find the index
    public static double getIndex(String obs, int state, String[] OBsym, ArrayList<ArrayList<Double>> bmat) {
        for (int i = 0; i < OBsym.length; i++) {
            if (obs.equals(OBsym[i])) {   
                return bmat.get(state).get(i);
            }
        }
        return 0.0;
    }
    
    //main
    public static void main(String args[]) throws IOException {
        File hmm = new File(args[0]);
        File obs = new File(args[1]);
        
        //read the hmm file
        Scanner scan = new Scanner(hmm);
        int N = scan.nextInt();
        //int M = scan.nextInt();
        //int T = scan.nextInt();
        scan.nextLine();
        
        scan.nextLine();
        //String[] state = scan.nextLine().split("\\s+");
        String[] OBsym = scan.nextLine().split("\\s+");
        scan.nextLine();
        
        //buidling matrix a
        int rows = 0;
        ArrayList<ArrayList<Double>> amat = new ArrayList<ArrayList<Double>>();
        while(scan.hasNextDouble()) {
            amat.add(new ArrayList<Double>());
            String line = scan.nextLine();
            int count = line.split("\\s+").length;
            Scanner newScan = new Scanner(line);
            for (int i = 0; i < count; i++) {
                amat.get(rows).add(newScan.nextDouble());
            }
            rows++;
        }
        scan.nextLine();
        
        //building matrix b
        rows = 0;
        ArrayList<ArrayList<Double>> bmat = new ArrayList<ArrayList<Double>>();
        while(scan.hasNextDouble()) {
            bmat.add(new ArrayList<Double>());
            String line = scan.nextLine();
            int count = line.split("\\s+").length;
            Scanner newScan = new Scanner(line);
            for (int i = 0; i < count; i++) {
                bmat.get(rows).add(newScan.nextDouble());
            }
            rows++;
        }
        scan.nextLine();
        
        //building array pi
        double[] pi = new double[N];
        for (int i = 0; i < N; i++) {
            pi[i] = scan.nextFloat();
        }
        
        //read the obs file
        scan = new Scanner(obs);
        int numsets = scan.nextInt();
        scan.nextLine();
        int[] numobs = new int[numsets];
        ArrayList<String[]> observ = new ArrayList<String[]>();
        for (int i = 0; i < numsets; i++) {
            numobs[i] = scan.nextInt();
            scan.nextLine();
            observ.add(scan.nextLine().split("\\s+"));
        }
        
        //forward algorithm process
        for (int h = 0; h < numsets; h++) {
            double[][] alphas = new double[N][numobs[h]];
            for (int k = 0; k < alphas.length; k++) {
                alphas[k][0] = pi[k]*getIndex(observ.get(h)[0], k, OBsym, bmat);
            }
            for (int t = 1; t < numobs[h]; t++) {
                for (int j = 0; j < N; j++) {
                    double alphabuf = 0;
                    for (int i = 0; i < N; i++) {
                        alphabuf += alphas[i][t-1]*amat.get(i).get(j);
                    }
                    alphas[j][t] = alphabuf*getIndex(observ.get(h)[t], j, OBsym, bmat);
                }
            }
            double P = alphas[0][numobs[h]-1] + alphas[1][numobs[h]-1] + alphas[2][numobs[h]-1] + alphas[3][numobs[h]-1];
            System.out.println(P);
        }
    }

}
