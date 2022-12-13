package ChatCrypto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Vector;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;

public class Client extends Frame implements Runnable,ActionListener{
	private Socket socket;
	private String addr="134.208.229.56";
	private int port;
	private static String username;
	DataInputStream in;
	DataOutputStream out;
	TalkRoom record =new TalkRoom();
	TextField inp= new TextField(); 
	public static SecretKey key;
	public static IvParameterSpec IV;
	//JButton ;
	
	
	Client() throws UnknownHostException, IOException, InterruptedException{
		
		Scanner input = new Scanner(System.in);
		System.out.println("Please input the port:");
		port = Integer.parseInt(input.nextLine());
		System.out.println("Please input ur username:");
		username = input.nextLine();
		setTitle(username);
		input.close();
		
		
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
			
			socket = new Socket(InetAddress.getByName(addr),port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			Thread thread = new Thread(this);
			thread.start();
			System.out.println("thread start----------------->");

			
			
			
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, NoSuchAlgorithmException, InterruptedException {
		
		new Client();	

		
	}
	

	public void getKey(DataInputStream in) throws IOException {
		System.out.println("<============get Key===============>");
		String skey = in.readUTF();
		System.out.println("read key & IV");
		byte[] by = Base64.getDecoder().decode(skey.getBytes("UTF-8"));
		key = new SecretKeySpec(by,0,by.length,"AES");
		System.out.println("KEY:"+key+"..................");

	}
	public void getIv(DataInputStream in) throws IOException {
		System.out.println("<============get IV===============>");
		try {
			String SIV = in.readUTF();
			System.out.println("I can read SIV in string===>"+SIV);
			byte[] SIVB =SIV.getBytes();
			byte[] CIV = new byte[16];
			for(int i=0;i<16;i++) {
				CIV[i]=0;
				if(i<11) {
					CIV[i]=SIVB[i];
				}
			}
			
			IV = new IvParameterSpec(CIV);
			System.out.println("IV:"+IV+"--------------------->");
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void run() {
		try {
			getKey(in);
			getIv(in);
			   while(true) {
				   String netline = in.readUTF();
				   netline = cpt.AES_decrypto(netline, key, IV);
				   System.out.println(netline);
				   record.printalk(netline, Color.DARK_GRAY);	
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
				mm= cpt.AES_encrypto(mm, key, IV);
				//System.out.println("KEY:"+key.toString());
				out.writeUTF(mm);
				//record.printalk(mm, Color.darkGray);
			}
			catch(Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		inp.setText(null);
		
	}
}
class saveline{
	String s;
	Color c;
	saveline(String s,Color c){
		this.s=s;
		this.c=c;
	}
}
class TalkRoom extends Component{

		Vector pastline = new Vector();
		Vector newline = new Vector();
		
		synchronized void printalk(String s,Color c){
			newline.addElement(new saveline(s,c));
			repaint();
		}
		public void paint(Graphics g) {
			synchronized(this) {
				while(newline.size()>0) {
					pastline.addElement(newline.elementAt(0));
					newline.removeElementAt(0);
				}
				while(pastline.size()>40) {
					pastline.removeElementAt(0);
				}
			}
			FontMetrics fontM = g.getFontMetrics();
			int margin = fontM.getHeight()/2;
			int w =getSize().width;
			int y=getSize().height - fontM.getHeight()-margin;
			
			for(int i=pastline.size()-1;i>=0;i--) {
				saveline showline = (saveline)pastline.elementAt(i);
				g.setColor(showline.c);
				g.setFont(new Font("Arial",Font.BOLD,12));
				g.drawString(showline.s, margin, y+fontM.getAscent());
				y-=fontM.getHeight();
				
			}
			
		}
}

