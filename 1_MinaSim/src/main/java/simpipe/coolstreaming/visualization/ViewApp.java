package simpipe.coolstreaming.visualization;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class ViewApp extends JFrame{
	
	JTabbedPane tab = new JTabbedPane();
	private Visualization visual[];
    
	
	public ViewApp(Visualization visual[]) {
		
		setPanels(visual);
		this.setBounds(100,100,600,550);
		this.add(tab,null);
		for(int i=0;i<visual.length;i++)
		tab.addTab(visual[i].getTitle(),visual[i]);
		
		this.setVisible(true);
	}
	
	void setPanels(Visualization visual[]){
    	this.visual=visual;
    }
}
