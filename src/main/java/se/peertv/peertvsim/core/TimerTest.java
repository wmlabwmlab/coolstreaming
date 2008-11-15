///**
// * =============================================== 
// *  File     : $Id: TimerTest.java,v 1.5 2007/06/06 09:22:48 sameh Exp $
// *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
// *  Version  :$Revision: 1.5 $
// *  Tag	  : $Name:  $
// *  Last edited by   : $Author: sameh $
// *  Last updated:    $Date: 2007/06/06 09:22:48 $
// *===============================================
// */
//package se.peertv.peertvsim.core;
//
//
//import junit.framework.TestCase;
//import se.peertv.peertvsim.utils.A;
//
///**
// * @author sameh
// *
// */
//public class TimerTest extends TestCase{
//
//	int counter=0;
//
//	public void hello(){
//		counter++;
//	}
//
////	public void testTimer() throws Exception {
////	Scheduler s  = Scheduler.getInstance();	
////	new Timer(5,this,"hello");
////	Event e = s.dequeue();
////	A.ssertTrue(((TimerEvent)e).getTime()==5);
////	}
//
////	public void testRemove() throws Exception {
////	Scheduler s  = Scheduler.getInstance();
////	Timer t  = new Timer(5,this,"hello");
////	t.reset();
////	Event e = s.dequeue();
////	A.ssertTrue(e==null);
////	}
//
//	/**
//	 * @throws Exception
//	 */
//	/**
//	 * @throws Exception
//	 */
////	public void testSet() throws Exception {
////		Scheduler s  = Scheduler.getInstance();
////
////		for (int i = 0; i < 1000; i++) {			
////			new Timer(5,this,"hello");
////		}
////
//////		for (int i = 0; i < 1000; i++) {			
//////		e = s.dequeue();
//////		A.ssertTrue(((TimerEvent)e).getTime()==5);
//////		//A.ssertTrue(((Timer)((TimerEvent)e).args).getCallBackFunction().equals(""+i));
//////		}
////
////		try {
////			Event e;
////			e = s.dequeue();
////			e.handle();
////			while (e !=null){
////				e = s.dequeue();
////				e.handle();
////			}
////		} catch (Throwable e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////		A.ssert(counter==1000);
////	}
//
//	public void testSet() throws Exception {
//		Scheduler s  = Scheduler.getInstance();
//
//		Timer[] t =new Timer[5] ;
//		int c=0;
//		
//		for (int i = 0; i < 10; i++) {			
//			Timer x = new Timer(5,this,"hello");
//			if ((i % 3 == 0) && (c <5)){
//					t[c] = x;
//					c++;
//			}		
//		}
//		
//		for (int i = 0; i < c; i++) {
//			t[i].reset();
//		}
//
//		try {
//			Event e;
//			e = s.dequeue();
//			while (e !=null){
//				e.handle();
//				e = s.dequeue();
//			}
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		A.ssert(counter==10-c);
//	}
//	
////	public void testSet() throws Exception {
////		Scheduler s  = Scheduler.getInstance();
////
////		Timer x = new Timer(5,this,"hello");
////		Timer y = new Timer(5,this,"hello");
////		
////		x.reset();
////	
////		Event e;
////		e = s.dequeue();
////		try{
////			e.handle();
////		} catch (Throwable e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
////		A.ssert(counter==1);
////	}
//	
//	
//}
