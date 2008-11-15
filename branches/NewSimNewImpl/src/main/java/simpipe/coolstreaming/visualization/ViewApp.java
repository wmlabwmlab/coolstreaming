package simpipe.coolstreaming.visualization;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import se.peertv.peertvsim.conf.Conf;


public class ViewApp extends JFrame{
	
	JTabbedPane tab = new JTabbedPane();
	private Visualization visual[];
    
	
	public ViewApp(Visualization visual[]) {
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPanels(visual);
		this.setBounds(100,100,600,550);
		JMenuBar bar = new JMenuBar();
		JMenu options = new JMenu("Options"); 
		JMenuItem jump = new JMenuItem("Go to");
		options.add(jump);
		bar.add(options);
		bar.setVisible(true);
		this.setJMenuBar(bar);
		this.add(tab,null);
		for(int i=0;i<visual.length;i++)
		tab.addTab(visual[i].getTitle(),visual[i]);
		this.setVisible(true);
		
		jump.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Component source = (Component) e.getSource();
				goTo(source);
				
			}
		});
		
	}
	
	void goTo(Component c){
		int x=-11;
		Object content=(Object)"Please enter the time to go to";
		String str=JOptionPane.showInputDialog(this,content,"Go to",JOptionPane.QUESTION_MESSAGE);
		int max=((int)Conf.MAX_SIMULATION_TIME/1000)-10;
		try{
			x=Integer.parseInt(str);
		}
		catch (Exception e) {
			return;
		}
		if(x<0){
			return;
		}
		else{
			System.out.println(x);
			for(int i=0;i<visual.length;i++)
				visual[i].view(x-1);
		}
	}
	
	void setPanels(Visualization visual[]){
    	this.visual=visual;
    }
	
	
}
