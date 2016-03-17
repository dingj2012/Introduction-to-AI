/*
 * Optimize-Implemenation of the Baum-Welch algorithm and report the probabilities
 * BU CS440 2015 Spring
 * Name:Ding Jin, Fuqing Wang, Xi Zhang
 */

import java.util.Scanner;
import java.io.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class optimize {
    //helper method to find the index
    public static double getIndex(String obs, int state,String[] OBsym, ArrayList<ArrayList<Double>> bmat) {
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
        int M = scan.nextInt();
        //int T = scan.nextInt();
        scan.nextLine();
        
        scan.nextLine();
        //String syntax = scan.nextLine();
        String[] OBsym = scan.nextLine().split("\\s+");
        scan.nextLine();
        
        //building matrix a
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
   
        //the Baum-Welch process
        for (int h = 0; h < numsets; h++) {
            double[][] alphas = new double[numobs[h]][N];
            for (int k = 0; k < alphas[0].length; k++) {
                alphas[0][k] = pi[k]*getIndex(observ.get(h)[0], k, OBsym, bmat);
            }
            for (int t = 1; t < numobs[h]; t++) {
                for (int j = 0; j < N; j++) {
                    double alphabuf = 0;
                    for (int i = 0; i < N; i++) {
                        alphabuf += alphas[t-1][i]*amat.get(i).get(j);
                    }
                    alphas[t][j] = alphabuf*getIndex(observ.get(h)[t], j, OBsym, bmat);
                }
            }
            double P = alphas[numobs[h]-1][0] + alphas[numobs[h]-1][1] + alphas[numobs[h]-1][2] + alphas[numobs[h]-1][3];
            System.out.printf("%.6f ", P);
            
            double[][] betas = new double[numobs[h]][N];
            
            for (int init = 0; init < N; init++) {
                betas[numobs[h]-1][init] = 1;
            }
            for (int t = numobs[h]-2; t >= 0; t--) {
                for (int i = 0; i < N; i++) {
                    double betabuf = 0;
                    for (int j = 0; j < N; j++) {
                        betabuf += betas[t+1][j]*amat.get(i).get(j)*getIndex(observ.get(h)[t+1], j, OBsym, bmat);
                    }
                    betas[t][i] = betabuf;
                }
            }
            
            double[][] gammas = new double[numobs[h]][N];
            for (int t = 0; t < numobs[h]; t++) {
                double POlamda = 0;
                for (int x = 0; x < N; x++) {
                    POlamda += alphas[t][x]*betas[t][x];
                }
                for (int i = 0; i < N; i++) {
                    gammas[t][i] = (alphas[t][i]*betas[t][i])/POlamda;
                }     
            }
            
            double[] newpi = new double[N];
            double[][] newamat = new double[N][N];
            double[][] newbmat = new double[N][bmat.get(0).size()];
            double[][][] xis = new double[numobs[h]-1][N][N];
            for (int t = 0; t < numobs[h]-1; t++) {
                double sum = 0;
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        sum += alphas[t][i]*amat.get(i).get(j)*getIndex(observ.get(h)[t+1], j, OBsym, bmat)*betas[t+1][j];
                    }
                }
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        xis[t][i][j] = (alphas[t][i]*amat.get(i).get(j)*getIndex(observ.get(h)[t+1], j, OBsym, bmat)
                                            *betas[t+1][j])/sum;
                    }
                }
            }
            
            for (int i = 0; i < N; i++) {
                newpi[i] = gammas[0][i];
            }
            
            for (int i = 0; i < N; i++) {
                double sumgamma = 0;
                for (int x = 0; x < numobs[h]-1; x++) {
                    sumgamma += gammas[x][i];
                }
                for (int j = 0; j < N; j++) {
                    double sumxi = 0;
                    for (int t = 0; t < numobs[h]-1; t++) {
                        sumxi += xis[t][i][j];
                    }
                    if (sumgamma == 0.0) {
                        newamat[i][j] = amat.get(i).get(j);
                    } else {
                        newamat[i][j] = (sumxi/sumgamma);
                    }
                }
            }
            
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < M; k++) {
                    double sumobk = 0;
                    double sumgamma = 0;
                    for (int t = 0; t < numobs[h]; t++) {
                        if (observ.get(h)[t].equals(OBsym[k])) {
                            sumobk += gammas[t][j];
                        }
                        sumgamma += gammas[t][j];
                    }
                    if (sumgamma == 0.0) {
                        newbmat[j][k] = bmat.get(j).get(k);
                    } else {
                        newbmat[j][k] = (sumobk/sumgamma);
                    }
                }
            }
            
            
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    amat.get(i).add(j, newamat[i][j]);
                }
            }
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < M; k++) {
                    bmat.get(j).add(k, newbmat[j][k]);
                }
            }
            
            pi = newpi;
            
            for (int k = 0; k < N; k++) {
                alphas[0][k] = pi[k]*getIndex(observ.get(h)[0], k, OBsym, bmat);
            }
            for (int t = 1; t < numobs[h]; t++) {
                for (int j = 0; j < N; j++) {
                    double alphabuf = 0;
                    for (int i = 0; i < N; i++) {
                        alphabuf += alphas[t-1][i]*amat.get(i).get(j);
                    }
                    alphas[t][j] = alphabuf*getIndex(observ.get(h)[t], j, OBsym, bmat);
                }
            }
            
            P = alphas[numobs[h]-1][0] + alphas[numobs[h]-1][1] + alphas[numobs[h]-1][2] + alphas[numobs[h]-1][3];
            System.out.printf("%.6f ", P);
            System.out.println();
            BufferedWriter out = null;
            out = new BufferedWriter(new FileWriter(args[2]));
            out.write(String.format("%d %d %d\n", N, M, numobs[h]));
            out.write("SUBJECT AUXILIARY PREDICATE OBJECT\n");
            out.write("students computers do can play develop games movies\n");
            out.write("a:\n");
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    out.write(String.format("%f ", amat.get(i).get(j)));
                }
                out.write("\n");
            }
            out.write("b:\n");
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < M; k++) {
                    out.write(String.format("%f ", bmat.get(j).get(k)));
                }
                out.write("\n");
            }
            out.write("pi:\n");
            for (int i = 0; i < N; i++) {
                out.write(String.format("%f ", pi[i]));
            }
            out.close();
        }
    }
    
}
