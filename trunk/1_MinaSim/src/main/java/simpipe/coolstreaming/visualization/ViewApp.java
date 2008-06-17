package simpipe.coolstreaming.visualization;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class ViewApp extends JFrame{
	
	JTabbedPane tab = new JTabbedPane();
	private MCacheOverPeers mCacheOverPeers;
    private PCacheOverPeers pCacheOverPeers;
    private PScoreOverNetwork pScoreOverNetwork;
    private PScoreOverPeers pScoreOverPeers;
    private JPanel pScoreDistribution;
    
	
	public ViewApp(MCacheOverPeers mCacheOverPeers,PCacheOverPeers pCacheOverPeers,PScoreOverNetwork pScoreOverNetwork,PScoreOverPeers pScoreOverPeers) {
		
		setPanels(mCacheOverPeers, pCacheOverPeers, pScoreOverNetwork, pScoreOverPeers);
		this.setBounds(100,100,600,550);
		this.add(tab,null);
		tab.addTab("Member's cache at each peer",mCacheOverPeers);
		tab.addTab("Partner's cache at each peer",pCacheOverPeers);
		tab.addTab("Partnership's score over network",pScoreOverNetwork);
		tab.addTab("Partnership's score at each peer",pScoreOverPeers);
		
		this.setVisible(true);
	}
	
	void setPanels(MCacheOverPeers mCacheOverPeers,PCacheOverPeers pCacheOverPeers,PScoreOverNetwork pScoreOverNetwork,PScoreOverPeers pScoreOverPeers){
    	this.mCacheOverPeers=mCacheOverPeers;
    	this.pCacheOverPeers=pCacheOverPeers;
    	this.pScoreOverPeers=pScoreOverPeers;
    	this.pScoreOverNetwork=pScoreOverNetwork;
    }
}
