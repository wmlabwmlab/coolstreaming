package se.peertv.peertvsim.network.tcp.bw;
//package se.peertv.peertvsim.network.bandwidth;
//
//import static org.junit.Assert.assertEquals;
//
//import java.nio.ByteBuffer;
//import java.util.ArrayList;
//import java.util.Formatter;
//import java.util.Map;
//import java.util.TreeMap;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import se.peertv.peertvsim.core.Scheduler;
//import se.peertv.peertvsim.network.Message;
//import se.peertv.peertvsim.network.Network;
//import se.peertv.peertvsim.network.Node;
//import se.peertv.peertvsim.utils.A;
//import se.peertv.peertvsim.utils.UnitConversion;
//import simpipe.protocol.SimpPipeUDPMessage;
//
//public class BandwidthManagerTest {
//	
//	public static void main(String[] args) throws Exception
//	{
//		new  BandwidthManagerTest().runTests();
//	}
//	
//	boolean isolatedTests = false;
//	@Before public void setUp() { isolatedTests = true; }
//	 
//	// manual test so if i want to see the whole output at once
//	public void runTests() throws Exception
//	{	
//		test1();	
//		test2();
//		test3();
//		test4();
//		
//		// test deallocation
//		test5();
//		
//		test6_1();		
//		test6_2();		
//		test6_3();		
//		test6_4();		
//		test6_5();		
//		test6_6();		
//		test6_7();
//	}	
//
//	@Test
//	public void test1() throws Exception
//	{
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();
//		
//		System.out.println("\n\n////////////\tCASE  1\t///////////\n\n");
//		Node node1 = new Node(1,0,100);
//		Node node2 = new Node(2,50,0);
//				
//		Network.getInstance().add(node1);
//		Network.getInstance().add(node2);			
//		
//		byte[] array= new byte[1000];
//		Transfer t = node1.sendBytes(2, new SimpPipeUDPMessage(array),1000);
//
//		assertEquals(node1.getUpBandwidthManager().totalBandwidthInBPS/2, t.bandwidthInBPS, 0.001);		
//		
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();
//	}
//	
//	@Test
//	public void test2() throws Exception
//	{
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();
//		
//		System.out.println("\n\n////////////\tCASE  2\t///////////\n\n");
//		Node node1 = new Node(1,0,100);
//		Node node2 = new Node(2,50,0);
//		Node node3 = new Node(3,50,0);
//		
//		Network.getInstance().add(node1);
//		Network.getInstance().add(node2);		
//		Network.getInstance().add(node3);
//		
//		byte[] array1= new byte[1000];
//		Transfer t1 = node1.sendBytes(2, new SimpPipeUDPMessage(array1),1000);
//		byte[] array2= new byte[1000];
//		Transfer t2 = node1.sendBytes(3, new SimpPipeUDPMessage(array2),1000);		
//		
//		assertEquals(node1.getUpBandwidthManager().totalBandwidthInBPS/2, t1.bandwidthInBPS, 0.001);
//		assertEquals(t1.bandwidthInBPS, t2.bandwidthInBPS, 0.001);
//		
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();
//	}
//	
//	@Test
//	public void test3() throws Exception
//	{
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();
//		
//		System.out.println("\n\n////////////\tCASE  3\t///////////\n\n");
//		Node node1 = new Node(1,0,100);
//		Node node2 = new Node(2,0,70);
//		Node node3 = new Node(3,50,0);
//		
//		Network.getInstance().add(node1);
//		Network.getInstance().add(node2);		
//		Network.getInstance().add(node3);
//		
//		byte[] array1= new byte[1000];
//		Transfer t1 = node1.sendBytes(2, new SimpPipeUDPMessage(array1),1000);
//		byte[] array2= new byte[1000];
//		Transfer t2 = node1.sendBytes(3, new SimpPipeUDPMessage(array2),1000);		
//		
//		assertEquals(node3.getDownBandwidthManager().totalBandwidthInBPS/2, t1.bandwidthInBPS, 0.001);
//		assertEquals(t1.bandwidthInBPS, t2.bandwidthInBPS, 0.001);
//		
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();
//	}
//	
//	@Test
//	public void test4() throws Exception
//	{
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();
//		
//		System.out.println("\n\n////////////\tCASE  4\t///////////\n\n");
//		Node node1 = new Node(1,0,100);
//		Node node2 = new Node(2,0,70);
//		Node node3 = new Node(3,100,0);
//		Node node4 = new Node(4,0,30);
//		
//		
//		Network.getInstance().add(node1);
//		Network.getInstance().add(node2);		
//		Network.getInstance().add(node3);
//		
//		byte[] array1= new byte[1000];
//		Transfer t1 = node1.sendBytes(2, new SimpPipeUDPMessage(array1),1000);
//		byte[] array2= new byte[1000];
//		Transfer t2 = node1.sendBytes(3, new SimpPipeUDPMessage(array2),1000);		
//		byte[] array3= new byte[1000];
//		Transfer t3 = node1.sendBytes(3, new SimpPipeUDPMessage(array3),1000);		
//		
//		assertEquals(node3.getDownBandwidthManager().totalBandwidthInBPS*(3.5/10.0), t1.bandwidthInBPS, 0.001);
//		assertEquals(t1.bandwidthInBPS, t2.bandwidthInBPS, 0.001);
//		assertEquals(node3.getDownBandwidthManager().totalBandwidthInBPS, t1.bandwidthInBPS + t2.bandwidthInBPS + t3.bandwidthInBPS, 0.001);
//		
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();		
//	}
//	
//	@Test
//	public void test5() throws Exception {
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();
//		
//		System.out.println("\n\n////////////\tCASE  5\t///////////\n\n");
//		Node node1 = new Node(1,0,100);
//		Node node2 = new Node(2,100,0);
//		Node node3 = new Node(3,100,0);
//		
//		Network.getInstance().add(node1);
//		Network.getInstance().add(node2);
//		Network.getInstance().add(node3);	
//		
//		byte[] array1= new byte[1000];
//		Transfer t1 = node1.sendBytes(2, new SimpPipeUDPMessage(array1),1000);
//		byte[] array2= new byte[1000];
//		Transfer t2 = node1.sendBytes(3, new SimpPipeUDPMessage(array2),1000);
//		
//		Transfer t = null;
//		Transfer otherT = null;
//		if(t1.deliveryEvent.time < t2.deliveryEvent.time)
//		{
//			t = t1;
//			otherT = t2;
//		}
//		else 
//		{
//			t = t2;
//			otherT = t1;
//		}
//		
//		Scheduler.getInstance().setTime(t.deliveryEvent.time);
//		Network.getInstance().get(t.destBandwidthManager.getNodeId())
//		.getDownBandwidthManager().receive(t);
//
//		assertEquals(node1.getUpBandwidthManager().totalBandwidthInBPS,otherT.bandwidthInBPS, 0.001);		
//		
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();			
//	}
//	
//	// test6 variables
//	static int originalBandwidth = 800;//(int) (Conf.STREAM_RATE_IN_KILOBIT_S);
//	static double factor = 0.25;
//	static StringBuilder output = new StringBuilder();
//	static int i = 1;
//	private void prepare_test6() throws Exception
//	{	
//		output.append("** | Data\t| Rate\t\t\t| Rate\t\t|Channel\t\t| Rate/Channel\t| Time took\t\n");
//		output.append("** | (MB)\t| (Kbps)\t\t| Bytes/s\t|(Kbps/Bps)\t\t| \t\t|          \t\n");
//		output.append("** |------------------------------------------------------------------------------------------------------\n");				
//	}
//	
//	private void test6_common() throws Exception
//	{
//		if(i==1)
//			prepare_test6();
//		System.out.println("\n\n////////////\tCASE  6." + i++ + "\t///////////\n\n");
//		String time = prettyPrint(
//				test6helper(
//						(int)(originalBandwidth * factor),
//						(int)(4 * factor) // start with 1 MB, then double. 4 cuz first factor is 1/4
//					)
//				);
//		long bytesPerSec = (long) (originalBandwidth * factor *1024 / 8);
//		long channelInBPS = (long)UnitConversion.kilobitToByte(originalBandwidth);
//		
//		Formatter f = new Formatter(output);
//		f.format("** | %.2f\t| %.2f\t\t| %8d\t| %d/%d\t\t| %6.2f times\t| %s\n",
//				4 * factor, originalBandwidth * factor, bytesPerSec, originalBandwidth, channelInBPS,
//				factor , time);
//		//System.out.println();			
//		factor *= 2;		
//		if(i-1==7)
//			end_test6();
//	}
//	
//	private void end_test6()
//	{
//		System.out.println(output);
//	}
//	
//	@Test public void test6_1() throws Exception { test6_common(); }
//	@Test public void test6_2() throws Exception { test6_common(); }
//	@Test public void test6_3() throws Exception { test6_common(); }
//	@Test public void test6_4() throws Exception { test6_common(); }
//	@Test public void test6_5() throws Exception { test6_common(); }
//	@Test public void test6_6() throws Exception { test6_common(); }
//	@Test public void test6_7() throws Exception { test6_common(); }
//	
//	/*
//	 * This method takes seconds in print it as in d days, h hours, m minutes, and s seconds
//	 */
//	private String prettyPrint(long millisec) {
//		String ret = "";
//		
//		long seconds = millisec / 1000;
//		
//		ret += (millisec + " = ");
//		
//		int secondsPerDay = 24*60*60;
//		long days = seconds / secondsPerDay;
//		seconds -= days * secondsPerDay;
//		if(days > 0)
//			ret += (days + " days, ");
//		
//		int secondsPerHour = 60*60;
//		long hours = seconds / secondsPerHour;
//		seconds -= hours * secondsPerHour;
//		if(hours > 0)
//			ret += (hours + " hours, ");
//		
//		int secondsPerMinute = 60;
//		long minutes = seconds / secondsPerMinute;
//		seconds -= minutes * secondsPerMinute;
//		if(minutes > 0)
//			ret += (minutes + " minutes, ");		
//		
//		ret += (seconds + " seconds");
//		
//		return ret;
//	}
//	
//	private long test6helper(int kiloBitsPerSec, int megs) throws Exception {
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();
//		
//		Node node1 = new Node(1,0,10000);
//		Node node2 = new Node(2,10000,0); 
//		
//		Network.getInstance().add(node1);
//		Network.getInstance().add(node2);
//
//		long currentTime = 0; // in millisec
//		long bytesLeft = megs * 1024*1024;
//		long bytesPerSec = (long) UnitConversion.kilobitToByte(kiloBitsPerSec); // also bytesPerPacket or bytesPerTransfer
//		
//		long totalBytes = bytesLeft;
//		long bytesSent = 0;
//		long bytesReceived = 0;
//		
//		TreeMap<Long /*receive time*/, ArrayList<Transfer> > schedule = new TreeMap<Long, ArrayList<Transfer> >();
//		
//		while(bytesLeft > 0 || !schedule.isEmpty())
//		{		
//			Scheduler.getInstance().setTime(currentTime);
//			while(!schedule.isEmpty() && currentTime >= schedule.firstKey())
//			{				
//				Scheduler.getInstance().setTime(schedule.firstKey());
//				
//				// the pollFirstEntry also removes it from the tree map 
//				Map.Entry<Long,ArrayList<Transfer> > entry = schedule.pollFirstEntry();				
//				
//				A.ssert(entry.getKey() == Scheduler.getInstance().getNow());
//				
//				for(Transfer t : entry.getValue())
//				{					
//					if(entry.getKey() != t.deliveryEvent.time)
//					{
//						// if one of the transfers that was to be recieved in this time 
//						// have been changed due to the last reception
//						if(schedule.get(t.deliveryEvent.time) == null)
//							schedule.put(t.deliveryEvent.time,new ArrayList<Transfer>());
//						schedule.get(t.deliveryEvent.time).add(t);
//						
//						continue;
//					}
//					
//					t.getDestBandwidthManager().receive(t);
//					bytesReceived += t.originalBytes;
//					
//					// the reception have changed the bandwidth of other transfer, so we need to create an new schedule					
//					schedule = update_schedule(schedule);
//				}
//			}
//			
//			Scheduler.getInstance().setTime(currentTime);
//			
//			if(bytesLeft > 0)
//			{
//				long bytesToSend = 0;
//				if(bytesLeft >= bytesPerSec)
//					bytesToSend = bytesPerSec;
//				else 
//					bytesToSend = bytesLeft;
//				
//				byte[] array1= new byte[1000];
//				Transfer t1 = node1.sendBytes(2, new SimpPipeUDPMessage(array1),1000);
//				// the sending have changed the bandwidth of other transfer, so we need to create an new schedule				
//				schedule = update_schedule(schedule);
//				
//				bytesSent += bytesToSend;
//
//				long timeItTakes = (long)UnitConversion.secToMilliSec(bytesToSend / t1.bandwidthInBPS);			
//				long receptionTime = currentTime + timeItTakes;
//								
//				if(schedule.get(receptionTime) == null)
//					schedule.put(receptionTime,new ArrayList<Transfer>());
//				schedule.get(receptionTime).add(t1);
//				
//				bytesLeft -= bytesToSend;
//			}
//			
//			currentTime += 1000; // millisec			
//		}	
//		
//		assertEquals(totalBytes, bytesSent);
//		assertEquals(totalBytes, bytesReceived);
//
//		Network.getInstance().clear();
//		Scheduler.getInstance().reset();
//		
//		return currentTime-1000;		
//	}
//
//	private TreeMap<Long, ArrayList<Transfer> > update_schedule(
//			TreeMap<Long, ArrayList<Transfer> > schedule) {
//		TreeMap<Long /*receive time*/, ArrayList<Transfer> > new_schedule = new TreeMap<Long, ArrayList<Transfer> >();
//		
//		for(ArrayList<Transfer> modified_entries : schedule.values())
//		{
//			for(Transfer modified_transfer : modified_entries)
//			{
//				if(new_schedule.get(modified_transfer.deliveryEvent.time) == null)
//					new_schedule.put(modified_transfer.deliveryEvent.time, new  ArrayList<Transfer>());
//								
//				new_schedule.get(modified_transfer.deliveryEvent.time).add(modified_transfer);
//			}
//		}
//		return new_schedule;
//	}
//}
