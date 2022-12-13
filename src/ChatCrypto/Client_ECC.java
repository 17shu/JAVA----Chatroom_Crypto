
package ChatCrypto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client_ECC extends Frame implements Runnable,ActionListener {

	private String targetIP ;
	private int Port;
	private Socket socket,socket2;
	private static String username;
	DataInputStream in;
	DataOutputStream out;
	TalkRoom record =new TalkRoom();
	TextField inp= new TextField(); 
	private KeyPair pair;
	private String prikey,pubkey;
	private String targetkey;
	private String choice ="";
	int i ;
	PublicKey target;
	private byte[] sign;
	PublicKey pbk;
	PrivateKey pvk;
	

	Client_ECC() throws NoSuchAlgorithmException, IOException{
		try {
			Scanner scan = new Scanner(System.in);
			System.out.println("If u want to create a room input '1',if there was a room input '2'...........");
			choice = scan.nextLine();
		    i = Integer.parseInt(choice);
			if(i==1) {
				System.out.println("＜＝＝＝＝＝＝ ＣＲＥＡＴＥ A　ＲＯＯＭ　＝＝＝＝＝＝＞");
				System.out.println("Please input the port:");
				Port = Integer.parseInt(scan.nextLine());
				ServerSocket ss = new ServerSocket(Port);
				System.out.println("================Server is Ready=================");
				socket = ss.accept();
				
			}
			else if(i==2) {
				System.out.println("input the ip u want to connect:");
				targetIP = scan.nextLine();
				System.out.println("input the port to connect:");
				Port = Integer.parseInt(scan.nextLine());
				socket = new Socket(InetAddress.getByName(targetIP),Port);
				System.out.println("======================Connect Successfully===================>");
			}
			
			System.out.println("Please input ur username:");
			username = scan.nextLine();
			setTitle(username);
			scan.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		try {
			add(record,BorderLayout.CENTER);
			add(inp,BorderLayout.SOUTH);
			inp.addActionListener(this);
			setVisible(true);
			setSize(500,350);
			addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
			});
			
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());	
			new Thread(this).start();
			System.out.println("thread start----------------->");

		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	static{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		new Client_ECC();

	}
	public void getkey() throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeySpecException {
		pair = cpt.ECC_KeyPair();
		//System.out.println(pair);
		byte[] b;
		pbk =  pair.getPublic();
		b = pbk.getEncoded();
		pubkey = Base64.getEncoder().encodeToString(b);
	    //get string private key
		pvk =  pair.getPrivate();
		//System.out.println(pvk);
		b = pvk.getEncoded();
		prikey = Base64.getEncoder().encodeToString(b);
		
		out.writeUTF(pubkey);
		//System.out.println("Public key:"+pbk);
		targetkey = in.readUTF();
		//System.out.println("Targetkey:"+targetkey);	
		byte[] kb = Base64.getDecoder().decode(targetkey);
		String kk = Base64.getEncoder().encodeToString(kb);
		target = cpt.getPubkey(kk);
		//System.out.println("TK:"+target);
	}
	
	public void run() {
		try {
			//System.out.println("in run................");
			getkey();
			   while(true) {
				   //System.out.println("reading...");
				   String resign = in.readUTF();
				   byte[] bsign = Base64.getDecoder().decode(resign);
				   System.out.println("SIGN:"+resign);
				   String netline = in.readUTF();
				   if(cpt.verify(netline, bsign, target)) {
					   System.out.println("pass verify");
					   netline = new String(cpt.ECC_decrypt(netline, pvk));
					   //System.out.println(netline);
					   record.printalk(netline, Color.DARK_GRAY);	
				   }
			   }
				  		
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()== inp) {
			try {
				String mm =username+">>"+inp.getText();
				record.printalk(mm, Color.darkGray);
				 mm = cpt.ECC_encrypt(mm,pbk );
				 byte[] sign = cpt.sign(mm, pvk);
				 String ssign = Base64.getEncoder().encodeToString(sign);
				out.writeUTF(ssign);
				out.writeUTF(mm);
				//System.out.println();
				
			}
			catch(Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		inp.setText(null);
		
	}

}
