//package Project2;

/**
 * This program Hex value of given byte/s.
 *
 * @author  Aashish Thakur(at1948@rit.edu)
 * @version 1.0
 */

public class convertVals {


    /**
     * Returns the int of a given byte.
     *
     * @param value         Single byte which is to be processed,
     *
     * @return              Returns the hex result as a string.
     */
    public static int getValue(int value){
        //Build the hex for single byte and then concatenate it.
        return value & 0xFF;
    }


    /**
     * Returns the int of given bytes.
     *
     * @param buffer        Buffer array contains the bytes which are to be processed,
     * @param i             i is the starting index
     * @param j             j represents the final index
     * @param delimiter     In case a delimiter is needed pass that or pass empty string.
     *w
     *
     * @return              Returns the hex result as a string.
     */
    public static String getValue(byte[] buffer, int i, int j, String delimiter){
        StringBuilder result = new StringBuilder();
        //Build the hex for each byte and then concatenate it.
        while (i<=j){
            if (i == j){
                delimiter = "";
            }
            if (delimiter.equals("") && buffer[i]==0){
                i+=1;
                continue;
            }
            result.append(getValue(buffer[i])).append(delimiter);

            i+= 1;
        }
        return String.valueOf(result);
    }

}


