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
public class OurCipher {

    public static void permute(int[] arr){
    permuteHelper(arr, 0);
}

private static void permuteHelper(int[] arr, int index){
    if(index >= arr.length - 1){ //If we are at the last element - nothing left to permute
        //System.out.println(Arrays.toString(arr));
        //Print the array
        System.out.print("[");
        for(int i = 0; i < arr.length - 1; i++){
            System.out.print(arr[i] + ", ");
        }
        if(arr.length > 0) 
            System.out.print(arr[arr.length - 1]);
        System.out.println("]");
        return;
    }

    for(int i = index; i < arr.length; i++){ //For each index in the sub array arr[index...end]

        //Swap the elements at indices index and i
        int t = arr[index];
        arr[index] = arr[i];
        arr[i] = t;

        //Recurse on the sub array arr[index+1...end]
        permuteHelper(arr, index+1);

        //Swap the elements back
        t = arr[index];
        arr[index] = arr[i];
        arr[i] = t;
    }
}
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        Cipher c = new Cipher(0x55555555);
        
//        BitSet b = BitSet.valueOf(new long[]{c.encrypt(0)});
//        BitSet bb = BitSet.valueOf(new long[]{c.encrypt(10)});
//        
//        bb.xor(b);
//        System.out.println(bb.cardinality());
//
//        for (int i = 0; i < 10000; i++) {
//            System.out.println(c.encrypt(i));
//        }

        new Tester().timeTest(2000000);
        new Tester().plaintextAvalancheTest(0, 20000);
    }
    
}
