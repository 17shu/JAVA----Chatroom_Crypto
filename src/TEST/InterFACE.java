

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
import java.io.IOException;
import java.util.Vector;

public class InterFACE extends Frame implements ActionListener {
		
	TalkRoom record =new TalkRoom();
	TextField input= new TextField(); 
	public InterFACE() throws Exception{
		super("InterFACE");
		
		try {
			add(record,BorderLayout.CENTER);
			add(input,BorderLayout.SOUTH);
			input.addActionListener(this);
			setVisible(true);
			setSize(500,350);
			addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});
			
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	public static void main(String[] args) throws Exception {
		 
		InterFACE t = new InterFACE();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if(e.getSource()==input) {
			record.printalk(input.getText(),Color.BLUE);
			
			}
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		input.setText(null);
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


