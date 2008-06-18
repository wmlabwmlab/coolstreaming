package simpipe.coolstreaming;

import java.net.SocketAddress;

public class CentralNode  {
    
    Membership members;
    ServerProtocol protocol;
    private int mSize = 20;
    int deleteTime=60000; // time to delete the members from the mCache
    public CentralNode(SocketAddress serverAddress){
    	protocol = new ServerProtocol(serverAddress,this);
    	members=new Membership(mSize,Constants.SERVER_PORT,deleteTime);
    }
}