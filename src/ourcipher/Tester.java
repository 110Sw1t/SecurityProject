/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ourcipher;

import java.util.BitSet;
import java.util.Random;

/**
 *
 * @author fadia
 */
public class Tester {

    private Cipher c;
    private Random r;
    private int key;

    public Tester(int key) {
        this.key = key;
        c = new Cipher(key);
        r = new Random();
    }

    public void plaintextAvalancheTest(int startNumber, long numberOfTests) {
        double total_avg = 0;
        for (int t = 0; t < 10; t++) {
            double sum = 0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;

            long oldPlain = startNumber;
            long oldCipher = c.encrypt(oldPlain);

            for (int i = 0; i < numberOfTests; i++) {
                long newPlain = oldPlain ^ (1 << r.nextInt(32));
                long newCipher = c.encrypt(newPlain);
                long xoredNewOldCipherText = oldCipher ^ newCipher;
                int cardinality = BitSet.valueOf(new long[]{xoredNewOldCipherText}).cardinality();
                if (cardinality > max) {
                    max = cardinality;
                }
                if (cardinality < min) {
                    min = cardinality;
                }
                //System.out.println("Input: "+newPlain+", Output: "+newCipher + ", Avalanche Effect: " + cardinality+"/64bit, " +cardinality/0.640+"%.");
                sum += cardinality;
                oldCipher = newCipher;
                oldPlain = newPlain;
            }
            sum /= numberOfTests;
            total_avg += sum / 0.640 ;
            System.out.println("Key: " + this.key + "\tAverage avalanche " + sum + "/64bit, " + sum / 0.640 + "%.");
            System.out.println("Minimum avalanche " + min + "/64bit, " + min / 0.640 + "%.");
            System.out.println("Maximum avalanche " + max + "/64bit, " + max / 0.640 + "%.");
        }
            System.out.println("\nTotal Average Avalance: " + total_avg/10);
    }

    public void timeTest(int numberOfTries) {
        double sum = 0;
        long ct = 0;
        for (int i = 0; i < numberOfTries; i++) {
            long startTime
                    = System.currentTimeMillis();
            ct = c.encrypt(78);
            long elapsedTime
                    = System.currentTimeMillis() - startTime;
            sum += elapsedTime;
        }
        sum /= numberOfTries;
        //System.out.println("Average Encryption speed: " + (64/sum)+" bits/ms");
        sum = 0;
        for (int i = 0; i < numberOfTries; i++) {
            long startTime
                    = System.currentTimeMillis();
                    c.decrypt(ct);
            long elapsedTime
                    = System.currentTimeMillis() - startTime;
                    sum += elapsedTime;
        }
        sum /= numberOfTries;
        //System.out.println("Average Decryption speed: " + (64/sum)+" bits/ms");
    }
}
