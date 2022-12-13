package ChatCrypto;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.NullCipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;




public class cpt {
	private static String SIGN = "SHA256withECDSA";
	
	public static SecretKey AES_key(int n) throws NoSuchAlgorithmException {
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
		keygenerator.init(n);
		SecretKey key = keygenerator.generateKey();
		//System.out.println(key.toString());
		return key;
	}
	public static byte[] AES_IV() {
		 byte[] IV = new byte[16];
		new SecureRandom().nextBytes(IV);
		System.out.println(IV.toString());
		return IV;
	}
	
	public static String AES_encrypto(String s,SecretKey key,IvParameterSpec iv)throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE,key,iv);
		byte[] ciphertext = cipher.doFinal(s.getBytes("UTF-8"));
		
		String re = Base64.getEncoder().encodeToString(ciphertext) ;
		return re;
	}
	
	public static String AES_decrypto(String ciphertext,SecretKey key,IvParameterSpec iv)throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE,key,iv);
		byte[] content = Base64.getDecoder().decode(ciphertext.getBytes("UTF-8"));
		byte[] plaintext = cipher.doFinal(content);
		return new String(plaintext);
		
	}
	
	public static KeyPair RSA_keypair() throws NoSuchAlgorithmException {
		KeyPairGenerator keypair = KeyPairGenerator.getInstance("RSA");
		keypair.initialize(1024);
		KeyPair pair = keypair.generateKeyPair();
		return pair;
	}
	
	public static String RSA_encrypto(String s,String pubkey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
		String eMessage = null;
		//System.out.println("RSA ENCRYPTO--------------------------");
		KeyFactory kf = KeyFactory.getInstance("RSA");
		byte[] decode = Base64.getDecoder().decode(pubkey);
		System.out.println("decode complete");
		X509EncodedKeySpec x509 = new X509EncodedKeySpec(decode);
		RSAPublicKey key = (RSAPublicKey) kf.generatePublic(x509);
		Cipher encode = Cipher.getInstance("RSA");
		encode.init(Cipher.ENCRYPT_MODE, key);
		
		eMessage =Base64.getEncoder().encodeToString(encode.doFinal(s.getBytes(StandardCharsets.UTF_8)));
		//System.out.println("encoder:"+eMessage);
		return eMessage;

	}
	
	public static String RSA_decrypto(String s,String prikey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		//System.out.println("RSA DECRYPTO---------------------------");
		byte[] sb = Base64.getDecoder().decode(s.getBytes("UTF-8"));
		KeyFactory kf = KeyFactory.getInstance("RSA");
		byte[] decode = Base64.getDecoder().decode(prikey);
		//System.out.println("Decode complete2");
		PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(decode);
		RSAPrivateKey key = (RSAPrivateKey) kf.generatePrivate(pkcs8);
		//System.out.println("privatekey in decrypto:"+key);
		Cipher encode = Cipher.getInstance("RSA");
		encode.init(Cipher.DECRYPT_MODE, key);
		String dMessage = new String( encode.doFinal(sb));
		//System.out.println("decoder:"+dMessage);
		return dMessage;
	}
	
	public static KeyPair ECC_KeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator keypair = KeyPairGenerator.getInstance("ECDH",BouncyCastleProvider.PROVIDER_NAME);
		keypair.initialize(256,new SecureRandom());
		KeyPair pair = keypair.generateKeyPair();
		return pair;
	}
	
	public static String ECC_encrypt(String s,PublicKey pbk) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		//System.out.println("Encode in.....");
		byte[] sb = s.getBytes("UTF-8");
		//System.out.println("sb finished.....");
		//byte[] keyb = Base64.getDecoder().decode(pubkey);
		Cipher cipher = new NullCipher();
		cipher.init(Cipher.ENCRYPT_MODE,pbk );
		
		byte[] fine = cipher.doFinal(sb);
		String rs = Base64.getEncoder().encodeToString(fine);
		//System.out.println("encoder:"+rs);
		return rs;
	}
	
	public static byte[] ECC_decrypt(String s,PrivateKey prikey) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		byte[] rs = null;
		//byte[] rr = null;
		try {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			//System.out.println("start decrypt......");
			byte[] sb = Base64.getDecoder().decode(s);
			//byte[] keyb = Base64.getDecoder().decode(pubkey);
			//System.out.println(sb);
			Cipher cipher = new NullCipher();
			//System.out.println("cipher ready");
			cipher.init(Cipher.DECRYPT_MODE,prikey);
			//System.out.println("decoder:"+s);
		
			
			rs = cipher.doFinal(sb);
			
		}
		catch(Exception e) {
			e.getStackTrace();
			System.out.println(e.getMessage());
		}
		System.out.println(rs);
		return rs;
	}
	
	public static byte[] sign(String s,PrivateKey prikey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		Signature sign = Signature.getInstance(SIGN);
		sign.initSign(prikey);
		sign.update(s.getBytes());
		return sign.sign();
	}
	public static boolean verify(String s,byte[] sign,PublicKey pubkey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		Signature si = Signature.getInstance(SIGN);
		si.initVerify(pubkey);
		si.update(s.getBytes());
		return si.verify(sign);
	}
	
	public static PublicKey getPubkey(String key) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		byte[] k = Base64.getDecoder().decode(key);
		KeyFactory kf = KeyFactory.getInstance("ECDSA","BC");
		X509EncodedKeySpec x509 = new X509EncodedKeySpec(k);
		return  kf.generatePublic(x509);
	}
	
	public static PrivateKey getPrikey(String prikey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		byte[] k = Base64.getDecoder().decode(prikey);
		KeyFactory kf = KeyFactory.getInstance("ECDSA","BC");
		PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(k);
		return  kf.generatePrivate(pkcs8);
	}
	
	public static String getScretkey(PublicKey pub,PrivateKey pri) throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException {
		KeyAgreement agree = KeyAgreement.getInstance("ECDH");
		agree.init(pri);
		agree.doPhase(pub, true);
		String key = Base64.getEncoder().encodeToString(agree.generateSecret());
		return key;
	}
	


}



