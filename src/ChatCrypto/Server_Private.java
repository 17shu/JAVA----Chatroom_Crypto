package ChatCrypto;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Server_Private {

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		System.out.println("which way u want 1:RSA,2:ECC");
		Scanner scan = new Scanner(System.in);
		String s = scan.nextLine();
		int a = Integer.parseInt(s);
		if(a==1) {
			Client_private.main(args);
		}
		else if(a==2) {
			Client_ECC.main(args);
		}
	}

}
