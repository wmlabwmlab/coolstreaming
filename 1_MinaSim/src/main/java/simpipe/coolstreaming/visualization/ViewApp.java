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
    private ContinuityIndex continuityIndex;
    private PAverageOverTime pAverageOverTime; 
    
	
	public ViewApp(MCacheOverPeers mCacheOverPeers,PCacheOverPeers pCacheOverPeers,PScoreOverNetwork pScoreOverNetwork,PScoreOverPeers pScoreOverPeers,ContinuityIndex continuityIndex,PAverageOverTime pAverageOverTime) {
		
		setPanels(mCacheOverPeers, pCacheOverPeers, pScoreOverNetwork, pScoreOverPeers,continuityIndex,pAverageOverTime);
		this.setBounds(100,100,600,550);
		this.add(tab,null);
		tab.addTab("Member's cache at each peer",mCacheOverPeers);
		tab.addTab("Partner's cache at each peer",pCacheOverPeers);
		tab.addTab("Partnership's score over network",pScoreOverNetwork);
		tab.addTab("Partnership's score at each peer",pScoreOverPeers);
		tab.addTab("Continuity Index",continuityIndex);
		tab.addTab("Average Scores Over Time",pAverageOverTime);
		this.setVisible(true);
	}
	
	void setPanels(MCacheOverPeers mCacheOverPeers,PCacheOverPeers pCacheOverPeers,PScoreOverNetwork pScoreOverNetwork,PScoreOverPeers pScoreOverPeers,ContinuityIndex continuityIndex,PAverageOverTime pAverageOverTime){
    	this.mCacheOverPeers=mCacheOverPeers;
    	this.pCacheOverPeers=pCacheOverPeers;
    	this.pScoreOverPeers=pScoreOverPeers;
    	this.pScoreOverNetwork=pScoreOverNetwork;
    	this.continuityIndex=continuityIndex;
    }
}
