package simpipe.coolstreaming;

import simpipe.coolstreaming.interfaces.*;


public class Node {
	
	boolean isTracker=false;
	int port;
	boolean isSource=false;
	int dummy=400;
	int startTime=100;
	int joinTime=200;
	
	Partnership partners;
    Membership members;
    Gossip gossip;
    Scheduler scheduler;
  
  //main coolstreaming parameters
	
	final int pSize=4;
    final int mSize=20;
    int deputyHops=5;
    int deleteTime=70000; // time to delete the members from the mCache
    int bootTime=3000;
    int continuityIndex;
    int allIndex; // it is all the segments that I've received (doesnt matter if its deadline came or not when it is received)
    int bandwidth;
    final int defaultBandwidth=256;
  
  //Video parameters
    int windowSize=30;	//30 sec
    int videoSize=870;  //14.5 min., used to be 120sec 
    int segmentSize=256; //256 Kb = 32 KB for one sec
    BitField myBuffer;

  
  
  //exiperiment limit 
   int gossipNumber;
   int exchangeNumber;
   int gossipLimit=4;
   int exchangeLimit=40;//used to be 25
  
	
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isSource() {
		return isSource;
	}
	public void setSource(boolean isSource) {
		this.isSource = isSource;
	}
	public int getDummy() {
		return dummy;
	}
	public void setDummy(int dummy) {
		this.dummy = dummy;
	}
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public int getJoinTime() {
		return joinTime;
	}
	public void setJoinTime(int joinTime) {
		this.joinTime = joinTime;
	}
	public Partnership getPartners() {
		return partners;
	}
	public void setPartners(Partnership partners) {
		this.partners = partners;
	}
	public Membership getMembers() {
		return members;
	}
	public void setMembers(Membership members) {
		this.members = members;
	}
	public Gossip getGossip() {
		return gossip;
	}
	public void setGossip(Gossip gossip) {
		this.gossip = gossip;
	}
	public Scheduler getScheduler() {
		return scheduler;
	}
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	public int getDeputyHops() {
		return deputyHops;
	}
	public void setDeputyHops(int deputyHops) {
		this.deputyHops = deputyHops;
	}
	public int getDeleteTime() {
		return deleteTime;
	}
	public void setDeleteTime(int deleteTime) {
		this.deleteTime = deleteTime;
	}
	public int getBootTime() {
		return bootTime;
	}
	public void setBootTime(int bootTime) {
		this.bootTime = bootTime;
	}
	public int getContinuityIndex() {
		return continuityIndex;
	}
	public void setContinuityIndex(int continuityIndex) {
		this.continuityIndex = continuityIndex;
	}
	public int getAllIndex() {
		return allIndex;
	}
	public void setAllIndex(int allIndex) {
		this.allIndex = allIndex;
	}
	public int getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}
	public int getWindowSize() {
		return windowSize;
	}
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}
	public int getVideoSize() {
		return videoSize;
	}
	public void setVideoSize(int videoSize) {
		this.videoSize = videoSize;
	}
	public int getSegmentSize() {
		return segmentSize;
	}
	public void setSegmentSize(int segmentSize) {
		this.segmentSize = segmentSize;
	}
	public BitField getMyBuffer() {
		return myBuffer;
	}
	public void setMyBuffer(BitField myBuffer) {
		this.myBuffer = myBuffer;
	}
	public int getGossipNumber() {
		return gossipNumber;
	}
	public void setGossipNumber(int gossipNumber) {
		this.gossipNumber = gossipNumber;
	}
	public int getExchangeNumber() {
		return exchangeNumber;
	}
	public void setExchangeNumber(int exchangeNumber) {
		this.exchangeNumber = exchangeNumber;
	}
	public int getGossipLimit() {
		return gossipLimit;
	}
	public void setGossipLimit(int gossipLimit) {
		this.gossipLimit = gossipLimit;
	}
	public int getExchangeLimit() {
		return exchangeLimit;
	}
	public void setExchangeLimit(int exchangeLimit) {
		this.exchangeLimit = exchangeLimit;
	}
	public int getPSize() {
		return pSize;
	}
	public int getMSize() {
		return mSize;
	}
	public int getDefaultBandwidth() {
		return defaultBandwidth;
	}
	  
   
}
