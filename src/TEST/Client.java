

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client extends Frame implements Runnable{
	private Socket socket;
	private String addr;
	private int port;
	private String username;
	DataInputStream in;
	DataOutputStream out;
	
	Client() throws UnknownHostException, IOException{
		Scanner input = new Scanner(System.in);
		System.out.println("Please input ur server address:");
		addr = input.nextLine();
		System.out.println("Please input the port:");
		port = Integer.parseInt(input.nextLine());
		System.out.println("Please input ur username:");
		username = input.nextLine();
		System.out.println("Say three sentence:");
		out.writeUTF(username+"->"+input.nextLine());
		
		
		try {
			socket = new Socket(InetAddress.getByName(addr),port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			new Thread(this).start();
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Client creatclient = new Client();	
			
	}

	@Override
	public void run() {
		try {
			while(true) {
				String netline = in.readUTF();
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
