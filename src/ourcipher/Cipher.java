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
    
    public void permute(int[] arr) {
        permuteHelper(arr, 0);
    }

    private void permuteHelper(int[] arr, int index) {
        if (index >= arr.length - 1) { //If we are at the last element - nothing left to permute
            //System.out.println(Arrays.toString(arr));
            //Print the array
            if(pc++ % 1247 == 0)permutations.add(arr.clone());
            return;
        }

        for (int i = index; i < arr.length; i++) { //For each index in the sub array arr[index...end]

            //Swap the elements at indices index and i
            int t = arr[index];
            arr[index] = arr[i];
            arr[i] = t;

            //Recurse on the sub array arr[index+1...end]
            if(permutations.size() < NUMBEROFROUNDS) permuteHelper(arr, index + 1);

            //Swap the elements back
            t = arr[index];
            arr[index] = arr[i];
            arr[i] = t;
        }
    }

    private int mixFunction(int lsbs, long key, int[] permutation) {
        long result = key ^ expand(lsbs);
//        System.out.println(result);
        long[] segments = new long[8];
        for (int i = 0; i < 8; i++) {
            segments[i] = (byte) result;
            result >>= 8;
        }
        long[] permutatedSegments = new long[8];
        result = 0;
        for (int i = 0; i < 8; i++) {
            result |= segments[permutation[i]];
            result <<= 8;
        }
        return (int)((result >> 32) ^ (int)result);
    }

    private long round(long number, long key, int[] permutation) {
        int lsbs = (int) (number);
        int msbs = (int) (number >> 32);
        int out1 = mixFunction(lsbs, key, permutation);
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
        segments[6] = (((segments[4] * segments[1] * 113) - 23) % 256);
        segments[7] = (((segments[5] * segments[2]) * 37 - 71)% 256);
        
        result = 
                (segments[0] ) |
                (segments[4] << 8) |
                (segments[2] << 16) |
                (segments[3] << 24) |
                
                (segments[5] << 32) |
                (segments[2] << 40) |
                (segments[1] << 48) |
                (segments[7] << 56);
        return result;
    }

    private static long swap(long number) {
        return (number << 32) ^ (number >> 32);
    }

    public Cipher(long key) {
        this.keys = this.expandKey(key);
        permute(new int[]{0,1,2,3,4,5,6,7});
    }

    private long[] expandKey (long key){
        long[] keys = new long[NUMBEROFROUNDS];
        for (int i = 0; i < NUMBEROFROUNDS; i++) {
            keys[i] = (key-0x5874612);
            key = ~key;
        }
        return keys;
    }
    private long process(long number, boolean isEncrypt) {
        for (int i = 0; i < NUMBEROFROUNDS; i++) {
            int roundUtilityIndex = (isEncrypt)? i : NUMBEROFROUNDS-i-1;
            number = round(number, keys[roundUtilityIndex], this.permutations.get(roundUtilityIndex));
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
