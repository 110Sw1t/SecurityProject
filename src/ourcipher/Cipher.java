/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ourcipher;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author fadia
 */
public class Cipher {

    public static final int NUMBEROFROUNDS = 8;
    private ArrayList<int[]> permutations = new ArrayList<>();
    private int pc = 0;
    private final long[] keys;
    private SBOX sbox;
    private Random r;
    private long number;

    private long generateKey(long k, int round) {

        k = permute(tables.PC1, 64, k);

        int lsbs = (int) (((0xFFFF0000) & (k)) >> 16);
        int msbs = (int) ((0x0000FFFF) & (k));
        int s = (round == 0 || round == 2 || round == 6) ? 1 : 2;

        lsbs = leftCircShift(lsbs, s);
        msbs = leftCircShift(msbs, s);
        //lsbs = sbox.sub(leftCircShift(lsbs, s)%256);
        //msbs = sbox.sub(leftCircShift(msbs, s)%256);
        long nk = (((long) lsbs) << 32) | (msbs & 0xffffffffL);
        //long mask = r.nextLong()*(1 << 8 | 1 << 16 | 1 << 24 | 1 << 32 | 1 << 40 | 1 << 48 | 1 << 56);
        //nk^=mask;
        nk = permute(tables.PC2, 64, nk);
        return nk;
    }

    private static long permute(byte[] table, int srcWidth, long src) {
        long dst = 0;
        for (int i = 0; i < table.length; i++) {
            int srcPos = srcWidth - table[i];
            dst = (dst << 1) | (src >> srcPos & 0x01);
        }
        return dst;
    }

    public int leftCircShift(int bits, int k) {
        return (bits << k) | (bits >> (Integer.SIZE - k));

    }

    private int mixFunction(int lsbs, long key, boolean isEncrypt) {
        long result = key ^ permutatedExpand(lsbs);
//        System.out.println(result);
        long[] segments = new long[8];
        for (int i = 0; i < 8; i++) {
            segments[i] = result & 0xFF;
            result >>= 8;
//            if(segments[i] < 0) segments[i] = -segments[i];
        }
        long seg0 = segments[7] ^ segments[1];
        long seg1 = segments[6] ^ segments[0];
        long seg2 = segments[5] ^ segments[3];
        long seg3 = segments[4] ^ segments[2];

        seg0 = sbox.sub((int) seg0);
        seg1 = sbox.sub((int) seg1);
        seg2 = sbox.sub((int) seg2);
        seg3 = sbox.sub((int) seg3);

        result = (seg2)
                | (seg3 << 8)
                | (seg1 << 16)
                | (seg0 << 24);

        return (int) (result);
    }

    private long round(long number, long key, boolean isEncrypt) {

        int lsbs = (int) (number);
        int msbs = (int) (number >> 32);
        int out1 = mixFunction(lsbs, key, isEncrypt);
        int newLSBS = out1 ^ msbs;
        long newMSBS = (((long) lsbs) << 16);
        return newMSBS ^ newLSBS;
    }

    private static long permutatedExpand(int number) {
        long result = number;

        long[] segments = new long[8];
        for (int i = 0; i < 4; i++) {

            segments[i] = (byte) result;
            result >>= 8;
        }
        segments[4] = (segments[0] ^ segments[2]);
        segments[5] = (segments[1] ^ segments[3]);
        segments[6] = (((segments[4] * segments[1])) % 256);
        segments[7] = (((segments[5] * segments[2])) % 256);

        result
                = (segments[2])
                | (segments[4] << 8)
                | (segments[6] << 16)
                | (segments[0] << 24)
                | (segments[5] << 32)
                | (segments[3] << 40)
                | (segments[1] << 48)
                | (segments[7] << 56);
        return result;
    }

    private static long swap(long number) {
        return (number << 32) ^ (number >> 32);
    }

    public Cipher(long key) {
        r = new Random();
        this.sbox = new SBOX();
        this.keys = this.expandKey(key);

    }

    private long[] expandKey(long key) {
        long[] keys = new long[NUMBEROFROUNDS];
        key = key ^ this.number;
        for (int i = 0; i < NUMBEROFROUNDS; i++) {
            keys[i] = generateKey(key, i);
            key = keys[i];
        }
        return keys;
    }

    private long process(long number, boolean isEncrypt) {
        for (int i = 0; i < NUMBEROFROUNDS; i++) {
            int roundUtilityIndex = (isEncrypt) ? i : NUMBEROFROUNDS - i - 1;
            number = round(number, keys[roundUtilityIndex], isEncrypt);
        }
        return swap(number);

    }

    public long encrypt(long number) {
        this.number = number;
        return process(number, true);
    }

    public long decrypt(long number) {
        return process(number, false);
    }
}
