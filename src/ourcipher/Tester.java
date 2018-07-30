/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ourcipher;
import java.util.BitSet;
/**
 *
 * @author fadia
 */
public class Tester {
    private Cipher c;
    public Tester(){
        c = new Cipher(0x5ABCDEF5);
    }
    public void plaintextAvalancheTest(int startNumber, long numberOfTests){
        double sum = 0, min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (int i = startNumber; i < numberOfTests+startNumber; i++) {
            BitSet b = BitSet.valueOf(new long[]{c.encrypt(i-1)});
            long ct = c.encrypt(i);
            BitSet bb = BitSet.valueOf(new long[]{ct});
            
            bb.xor(b);
            int cardinality = bb.cardinality();
            if(cardinality> max) max = cardinality;
            if(cardinality<min) min = cardinality;
            System.out.println("Input: "+i+", Output: "+ct + ", Avalanche Effect: " + cardinality+"/64bit, " +cardinality/0.640+"%.");
            sum+=cardinality;
        }
        sum/=numberOfTests;
        System.out.println("Average avalanche "+ sum+"/64bit, " +sum/0.640+"%.");
        System.out.println("Minimum avalanche "+ min+"/64bit, " +min/0.640+"%.");
        System.out.println("Maximum avalanche "+ max+"/64bit, " +max/0.640+"%.");
    }
    public void timeTest(int numberOfTries){
        double sum = 0;
        long ct = 0;
        for (int i = 0; i < numberOfTries; i++) {
            long startTime = System.currentTimeMillis();
            ct = c.encrypt(78);
            long elapsedTime = System.currentTimeMillis() - startTime;
            sum+=elapsedTime;
        }
        sum/=numberOfTries;
        System.out.println("Average Encryption speed: " + (64/sum)+" bits/ms");
        sum = 0;
        for (int i = 0; i < numberOfTries; i++) {
            long startTime = System.currentTimeMillis();
            c.decrypt(ct);
            long elapsedTime = System.currentTimeMillis() - startTime;
            sum+=elapsedTime;
        }
        sum/=numberOfTries;
        System.out.println("Average Decryption speed: " + (64/sum)+" bits/ms");
    }
}
