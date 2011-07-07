package de.letsdoo.client.util;

import android.app.Activity;
import de.letsdoo.client.android.Letsdoo;

/**
 * Static helper methods for Letsdoo app
 */
public class Utils {
	
	public static Letsdoo getApp(Activity activity) {
		return (Letsdoo)activity.getApplication();
	}
	
    public static void xor(byte[] result, byte[] xor) {
        if (result == null || xor == null || result.length != xor.length)
            throw new IllegalArgumentException();
        
        for (int i=0; i<result.length; i++)
            result[i] ^= xor[i];
    }
    
    public static String xorHex(String a, String b) {
        byte[] result = hexToBytes(a);
        byte[] xor = hexToBytes(b);
        xor(result, xor);
        return bytesToHex(result);
    }
    
    public static byte[] hexToBytes(String hex) {
        byte[] result = new byte[hex.length()/2];
        for (int i=0; i<result.length; i++)
            result[i] = (byte)Short.parseShort(hex.substring(i*2, i*2+2), 16);
        return result;
    }
    
    public static String bytesToHex(byte[] digest) {
        String digits = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            int bi = b & 0xff;
            sb.append(digits.charAt(bi >> 4));
            sb.append(digits.charAt(bi & 0xf));
        }
        return sb.toString();
    }
}
