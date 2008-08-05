package simpipe.coolstreaming;

import simpipe.coolstreaming.interfaces.*;


public class Node {
	
	public int port;
	public boolean isSource=false;
	
	public int dummy=400;
	public int startTime=100;
	public int joinTime=200;
	
	public Partnership partners;
    public Membership members;
    public Gossip gossip;
    public Scheduler scheduler;
    
    //main coolstreaming parameters
	
	public final int pSize=4;
    public final int mSize=20;
    public int deputyHops=4;
    public int deleteTime=60000; // time to delete the members from the mCache
    public int bootTime=3000;
    public int continuityIndex;
    public int allIndex; // it is all the segments that I've received (doesnt matter if its deadline came or not when it is received)
    public int bandwidth;
    public final int defaultBandwidth=256;
    
    //Video parameters
    public int windowSize=30;	//30 sec
    public int videoSize=500;  //used to be 120sec 
    public int segmentSize=256; //256 Kb = 32 KB for one sec
    public BitField myBuffer;

    
    
    //exiperiment limit 
    int gossipNumber;
    int exchangeNumber;
    int gossipLimit=4;
    int exchangeLimit=40;//used to be 25
    
   
}
