
package ChatCrypto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Server{
	
	private int port;
	private String IP;
	private Hashtable<Socket, DataOutputStream> ht = new Hashtable<Socket, DataOutputStream>();
	public static SecretKey key;
	public static IvParameterSpec IV;
	byte[] CIV;
	String SIV;
	public int size ;
	
	
	public Server()throws IOException, NoSuchAlgorithmException {
		IP =InetAddress.getLocalHost().getHostAddress();
		System.out.println("IP Address:"+IP);
		System.out.println("input ur port:");
		CIV = cpt.AES_IV();
		
		try {
			try (Scanner input = new Scanner(System.in)) {
				port = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			try (ServerSocket ss = new ServerSocket(port)) {
				System.out.println("================Server is Ready=================");
				
				key = cpt.AES_key(128);
				//ivb = cpt.AES_IV();
							
				while(true) {
					Socket socket = ss.accept();
					System.out.println("--ClientIP: "+socket.getInetAddress().getHostAddress()+"\nClient Port:"+socket.getPort());
						
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					sendKey(out);
					sendIV(out);
					ht.put(socket, out);
					Thread sthread = new Thread(new serverthread(socket,ht));
					sthread.start();
				}
			}
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
	}
	public void sendKey(DataOutputStream out) throws IOException {
		try {
			byte[] by = key.getEncoded();
			String skey = Base64.getEncoder().encodeToString(by);
			out.writeUTF(skey);
			System.out.println("SKEY:"+key+"..........................");

		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	public void sendIV(DataOutputStream out) throws IOException {
		System.out.println("CIV SIZE:"+CIV.length+"||\tCIV = "+CIV);
		SIV = CIV.toString();
		out.writeUTF(SIV);
	}
	

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		new Server();
		
	
	}
	
}
class serverthread extends Thread implements Runnable{
	Socket socket;
	Hashtable<Socket, DataOutputStream> ht;
	private String msg;
	
	serverthread(Socket socket,Hashtable<Socket, DataOutputStream> ht){
		this.socket = socket;
		this.ht = ht;
	}
	public void run(){
		
			try {
				//this.wait(2000);
				while(true) {
					DataInputStream in = new DataInputStream(socket.getInputStream());
					System.out.println("runing............");
					while(true) {
						//System.out.println("<--message in-->");
						msg = in.readUTF();
						System.out.println("Message:"+msg);
						
						synchronized(ht) {
							for(Enumeration<DataOutputStream> e = ht.elements();e.hasMoreElements();) {
								DataOutputStream out = new DataOutputStream(socket.getOutputStream());
								try {
									e.nextElement().writeUTF(msg);
									//System.out.println("msg delivered..............");
								}
								catch(Exception ex) {
									System.out.println(ex.getMessage());
								}
							}
						}
					}
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
			finally {
				synchronized(ht) {
					System.out.println("System Remove connection :"+socket);
					ht.remove(socket);
					try {
						socket.close();
					}
					catch(Exception ex) {
						ex.getStackTrace();
					}
				}
			}
		}
	
		
	
}
