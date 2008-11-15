package simpipe.coolstreaming;

import java.net.SocketAddress;

import simpipe.coolstreaming.implementations.RandomMembership;
import simpipe.coolstreaming.implementations.RandomPartnership;

public class CentralNode  {
    
    RandomPartnership members;
    ServerProtocol protocol;
    private int pSize = 500;
    boolean isTracker=true;
    
    public CentralNode(SocketAddress serverAddress,int size){
    	pSize=size;
    	protocol = new ServerProtocol(serverAddress,this);
    	members=new RandomPartnership(pSize,Constants.SERVER_PORT,0,0,null);
    }
}