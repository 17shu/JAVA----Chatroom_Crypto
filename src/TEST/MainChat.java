import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class MainChat {

	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		while(true) {
			System.out.println("Please input a string u want to encrypt:");
		String inputstring = input.nextLine();
		System.out.println("=====start to encrpt=====");
		SecretKey Key = cryptography.AES_key(128);
		IvParameterSpec iv = cryptography.AES_IV();
		String encode = cryptography.AES_encrypto(inputstring, Key, iv);
		System.out.println("Encode: "+ encode);
		System.out.println("=====start to decrpt=====");
		System.out.println("Decode: "+cryptography.AES_decrypto(encode, Key, iv));
		if(inputstring=="-1")break;
		}
		
	}

}
