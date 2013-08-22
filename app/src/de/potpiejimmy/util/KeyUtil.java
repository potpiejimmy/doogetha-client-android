package de.potpiejimmy.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.doogetha.client.util.Utils;

public class KeyUtil {
	
	protected final static String ALGORITHM = "RSA";
	
	public static KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
			keyGen.initialize(2048);
			KeyPair generatedKeyPair = keyGen.genKeyPair();
			dumpKeyPair(generatedKeyPair);
			return generatedKeyPair;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 
	private static void dumpKeyPair(KeyPair keyPair) {
		System.out.println("Generated Key Pair");
		
		PublicKey pub = keyPair.getPublic();
		System.out.println("Public Key: " + Utils.bytesToHex(pub.getEncoded()));
		 
		PrivateKey priv = keyPair.getPrivate();
		System.out.println("Private Key: " + Utils.bytesToHex(priv.getEncoded()));
		
		System.out.println("Encoded public  key (X509)  length: " + encodeKey(pub).length());
		System.out.println("Encoded private key (PKCS8) length: " + encodeKey(priv).length());
	}
 
	public static String encodeKey(PrivateKey privateKey) {
		// Encode Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		return Utils.bytesToHex(pkcs8EncodedKeySpec.getEncoded());
	}
 
	public static String encodeKey(PublicKey publicKey) {
		// Encode Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
		return Utils.bytesToHex(x509EncodedKeySpec.getEncoded());
	}
	
	public KeyPair decodeKeyPair(String privateKeyHex, String publicKeyHex) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedPrivateKey = Utils.hexToBytes(privateKeyHex);
		byte[] encodedPublicKey = Utils.hexToBytes(publicKeyHex);
		 
		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		 
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		 
		return new KeyPair(publicKey, privateKey);
	}	
}
