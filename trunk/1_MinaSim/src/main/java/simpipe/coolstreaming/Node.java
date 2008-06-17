package simpipe.coolstreaming;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import se.peertv.peertvsim.core.Timer;

public class Node {
	
	public int port;
	boolean isSource=false;
	
	int dummy=400;
	
	Partnership partners;
    Membership members;
    Gossip gossip;
    Schedule scheduler;
    
    //main coolstreaming parameters
	
	public final int pSize=4;
    public final int mSize=20;
    int deputyHops=4;
    int deleteTime=60000; // time to delete the members from the mCache
    int bootTime=3000;
    int continuityIndex;
    int allIndex; // it is all the segments that I've received (doesnt matter if its deadline came or not when it is received)
    int bandwidth;
    final int defaultBandwidth=256;
    
    //Video parameters
    int windowSize=30;	//30 sec
    int videoSize=500;  //used to be 120sec 
    BitField myBuffer;

    
    
    //exiperiment limit 
    int gossipNumber;
    int exchangeNumber;
    int gossipLimit=4;
    int exchangeLimit=40;//used to be 25
}
