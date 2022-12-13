import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

class clientreader implements Runnable{
	private Socket clientsocket ;
	public clientreader(Socket socket){
		this.clientsocket = socket;
	}
	@Override
	public void run() {
		try {
			Scanner scanner = new Scanner(clientsocket.getInputStream());
			while(scanner.hasNext()) {
				System.out.println(scanner.nextLine());
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
 class clientsender implements Runnable{
	private Socket clientsocket;
	public clientsender(Socket socket){
		this.clientsocket = socket;
	}
	@Override
	public void run() {
		
		try {
			PrintStream printstream = new PrintStream(clientsocket.getOutputStream());
			Scanner scanner = new Scanner(System.in);
			while(true) {
				if(scanner.hasNext()) {
					String msg = scanner.nextLine();
					printstream.println(msg);
				}
				if(scanner.nextLine()=="exit"||scanner.nextLine()=="EXIT") {
					scanner.close();
					printstream.close();
					break;
				}
			}
		}
		catch(IOException e) {
			e.getStackTrace();
		}
		
	}

}

public class MultiClients {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		Socket socket = new Socket("127.0.0.1",6665);
		Thread read = new Thread(new clientreader(socket));
		Thread send = new Thread(new clientsender(socket));
		
		read.start();
		send.start();
		
	}

}
