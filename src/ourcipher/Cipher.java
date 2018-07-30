/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ourcipher;
import java.util.ArrayList;
/**
 *
 * @author fadia
 */
public class Cipher {

    public static final int NUMBEROFROUNDS = 8;
    private ArrayList<int[]> permutations = new ArrayList<>();
    private int pc = 0;
    private final long[] keys;
    

    private int mixFunction(int lsbs, long key) {
        long result = key ^ expand(lsbs);
//        System.out.println(result);
//        long[] segments = new long[8];
//        for (int i = 0; i < 8; i++) {
//            segments[i] = (byte) result;
//            result >>= 8;
//        }
//        long[] permutatedSegments = new long[8];
//        result = 0;
//        for (int i = 0; i < 8; i++) {
//            result |= segments[permutation[i]];
//            result <<= 8;
//        }
        return (int)((result >> 32) ^ (int)result);
    }

    private long round(long number, long key) {
        int lsbs = (int) (number);
        int msbs = (int) (number >> 32);
        int out1 = mixFunction(lsbs, key);
        int newLSBS = out1 ^ msbs;
        long newMSBS = (((long) lsbs) << 32);
        return newMSBS ^ newLSBS;
    }

    private static long expand(int number) {
        long result = number;
        long[] segments = new long[8];
        for (int i = 0; i < 4; i++) {
            segments[i] = (byte) result;
            result >>= 8;
        }
        segments[4] = (segments[0] ^ segments[2]);
        segments[5] = (segments[1] ^ segments[3]);
        segments[6] = (((segments[4] * segments[1])) % 256);
        segments[7] = (((segments[5] * segments[2]))% 256);
        
        result = 
                (segments[2] ) |
                (segments[4] << 8) |
                (segments[6] << 16) |
                (segments[0] << 24) |
                
                (segments[5] << 32) |
                (segments[3] << 40) |
                (segments[1] << 48) |
                (segments[7] << 56);
        return result;
    }

    private static long swap(long number) {
        return (number << 32) ^ (number >> 32);
    }

    public Cipher(long key) {
        this.keys = this.expandKey(key);
    }

    private long[] expandKey (long key){
        long[] keys = new long[NUMBEROFROUNDS];
        for (int i = 0; i < NUMBEROFROUNDS; i++) {
            keys[i] = ((key) << 2) | i;
        }
        return keys;
    }
    private long process(long number, boolean isEncrypt) {
        for (int i = 0; i < NUMBEROFROUNDS; i++) {
            int roundUtilityIndex = (isEncrypt)? i : NUMBEROFROUNDS-i-1;
            number = round(number, keys[roundUtilityIndex]);
        }
        return swap(number);

    }

    public long encrypt(long number) {
        return process(number, true);
    }

    public long decrypt(long number) {
        return process(number, false);
    }
}
