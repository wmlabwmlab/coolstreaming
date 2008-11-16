package se.peertv.peertvsim.examples.Tennis;


import se.peertv.peertvsim.core.Timer;
import se.peertv.peertvsim.network.Node;
import se.peertv.peertvsim.utils.P;

public class ToyPeer extends Node{

	public ToyPeer(int id, long downCapacity, long upCapacity) throws Exception{
		super(id);
		if (id !=10)
			send(10, new TennisBall(5),0);
		
		//Timer t  = new Timer(5, this, "dummyTimeOut");		
	}

	/**
	 * 
	 * Example of how you send an receive message
	 * 
	 */
	public void handle(TennisBall ball) throws Throwable {
        int sender = ball.getSource();
		
        ball = ball.stroke();

        if (ball.getTTL() > 0) {
            // If the ball is still alive, pass it back to peer.
			send(sender, ball,0);        
		} 
        else
        	P.rint("Done:"+id);
	}
	
	
	/**
	 * Example of how you write timers
	 */
	public void dummyTimeOut() throws Exception{
		P.rint("Timer Elapsed");
		Timer t=new Timer();
		t.set(5, this, "dummyTimeOut");
	}
	
	/**
	 *      Assume you have some code in reality that looks like this.
	 *		public void sleepInTheMiddle(){
	 *		    P.rint("Gonna sleep for some time"); 
	 *          Thread.sleep(500);
	 *          P.rint("Now I can continue");
	 *      }    
	 *      
	 */
	
	public void sleepInTheMiddle() throws Exception{
		P.rint("Gonna sleep for some time");
		Timer t=new Timer();
		t.set(500, this,"sleepInTheMiddleRest");
	}

	public void sleepInTheMiddleRest() throws Exception{
		P.rint("Now I can continue");
	}
	
}
