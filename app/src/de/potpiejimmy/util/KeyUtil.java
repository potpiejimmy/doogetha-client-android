package de.potpiejimmy.util;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.doogetha.client.util.Utils;

public class KeyUtil {
	
	protected final static String KEY_ALGORITHM = "RSA";
	protected final static String SIGNATURE_ALGORITHM = "SHA1withRSA";
	
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyGen.initialize(2048);
		KeyPair generatedKeyPair = keyGen.genKeyPair();
//			dumpKeyPair(generatedKeyPair);
		return generatedKeyPair;
	}
 
//	private static void dumpKeyPair(KeyPair keyPair) {
//		System.out.println("Generated Key Pair");
//		
//		PublicKey pub = keyPair.getPublic();
//		System.out.println("Public Key: " + Utils.bytesToHex(pub.getEncoded()));
//		 
//		PrivateKey priv = keyPair.getPrivate();
//		System.out.println("Private Key: " + Utils.bytesToHex(priv.getEncoded()));
//		
//		System.out.println("Encoded public  key (X509)  length: " + encodeKey(pub).length());
//		System.out.println("Encoded private key (PKCS8) length: " + encodeKey(priv).length());
//	}
 
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
	
	public static PublicKey decodePublicKey(String publicKeyHex) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedPublicKey = Utils.hexToBytes(publicKeyHex);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		return keyFactory.generatePublic(publicKeySpec);
	}
	
	public static PrivateKey decodePrivateKey(String privateKeyHex) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encodedPrivateKey = Utils.hexToBytes(privateKeyHex);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		return keyFactory.generatePrivate(privateKeySpec);
	}	
	
	public static byte[] sign(byte[] data, PrivateKey key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
	{
		Signature s = Signature.getInstance(SIGNATURE_ALGORITHM);
		s.initSign(key);
		s.update(data);
		return s.sign();
	}
}
