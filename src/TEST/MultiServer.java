import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Server implements Runnable{
	private static Map<String,Socket> map= new ConcurrentHashMap<>(); 
	private Socket serversocket; 
	public Server(Socket socket) {
		this.serversocket = socket;
	}
	@Override
	public void run() {
		try {
			System.out.println("into run......");
			Scanner scanner=new Scanner(serversocket.getInputStream());
			String msg = null;
			while(true) {
				if(scanner.hasNext()) {
					msg=scanner.nextLine();
					Pattern pattern = Pattern.compile("\r");
					Matcher matcher = pattern.matcher(msg);
					msg = matcher.replaceAll("");
					if(msg.startsWith("userName:")) {
						System.out.println("Got username");
						String userName=msg.split(":")[1];
						userRegist(userName,serversocket);
						continue;
					}
					else if(msg.startsWith("G:")) {
						firststep(serversocket);
						String str = msg.split("\\:")[1];
						groupChat(serversocket,str);
						continue;
					}
					else if(msg.startsWith("P:")&&msg.contains("-")) {
						firststep(serversocket);
						String username = msg.split("\\:")[1].split("-")[0];
						String str = msg.split("\\:")[1].split("-")[1];
						privateChat(serversocket,username,str);
						continue;
					}
					else if(msg.contains("exit")) {
						firststep(serversocket);
						userEXIT(serversocket);
						continue;
					}
					else {
						PrintStream printstream = new PrintStream(serversocket.getOutputStream());
						printstream.println("Error Format!");
						continue;
					}
					
				}
				
				
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	private void userEXIT(Socket s) {
		String username=null;
		for(String key:map.keySet()) {
			if(map.get(key).equals(s)) {
				username =key;
				break;
			}
		}
		map.remove(username,s);
		System.out.println("User:"+username+"OFFLINE!");
	}
	private void privateChat(Socket socket, String username, String s) throws IOException {
		String curUser=null;
		Set<Map.Entry<String, Socket>> set =map.entrySet();
		for(Map.Entry<String, Socket> entry :set) {
			if(entry.getValue().equals(socket)){
				curUser=entry.getKey();
				break;
			}
		}
		Socket client = map.get(username);
		PrintStream p = new PrintStream(client.getOutputStream());
		p.println(username+"私聊說:"+s);
		
	}
	private void groupChat(Socket socket, String s)throws IOException {
		Set<Map.Entry<String, Socket>> set =map.entrySet();
		String username= null;
		for(Map.Entry<String, Socket> entry :set) {
			if(entry.getValue().equals(socket)){
				username= entry.getKey();
				break;
			}
		}
		
		for(Map.Entry<String, Socket> entry:set) {
			Socket client = entry.getValue();
			PrintStream p = new PrintStream(client.getOutputStream());
			p.println(username+"群聊說:"+s);
		}
		
	}
	private void userRegist(String user, Socket socket) {
		map.put(user,socket);
		System.out.println("[userName:"+user+"][client:"+socket+"]online");
		System.out.println("=====online size:"+map.size()+"=====");			
	}
	private void firststep(Socket socket) throws IOException{
		Set<Map.Entry<String,Socket>> set = map.entrySet();
		for(Map.Entry<String,Socket> entry:set) {
			if(entry.getValue().equals(socket)) {
				if(entry.getKey()==null) {
					PrintStream print = new PrintStream(socket.getOutputStream());
					print.println("go regist");
				}
			}
			
		}
	}
	
}

public class MultiServer {
	
	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(6665);
			ExecutorService es = Executors.newFixedThreadPool(20);
			for(int i=0;i<20;i++) {
				System.out.println("welcome to my chatroom.........");
				Socket socket =ss.accept();
				System.out.println("new friend welcome..........");
				es.execute(new Server(socket));
			}
			es.shutdown();
			ss.close();
		}catch(IOException e) {
			e.printStackTrace();
		}

	}

}
