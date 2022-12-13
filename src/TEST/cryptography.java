import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class cryptography {
	
	public static SecretKey AES_key(int n) throws NoSuchAlgorithmException {
		KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
		keygenerator.init(n);
		SecretKey key = keygenerator.generateKey();
		return key;
	}
	public static IvParameterSpec AES_IV() {
		byte[] IV = new byte[16];
		new SecureRandom().nextBytes(IV);
		
		return new IvParameterSpec(IV);
	}
	
	public static String AES_encrypto(String s,SecretKey key,IvParameterSpec iv)throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE,key,iv);
		byte[] ciphertext = cipher.doFinal(s.getBytes());
		
		String re = Base64.getEncoder().encodeToString(ciphertext) ;
		return re;
	}
	
	public static String AES_decrypto(String ciphertext,SecretKey key,IvParameterSpec iv)throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE,key,iv);
		byte[] content = Base64.getDecoder().decode(ciphertext.getBytes());
		byte[] plaintext = cipher.doFinal(content);
		return new String(plaintext);
		
	}
	

	
	
}
