import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.core.Scheduler;
import se.peertv.peertvsim.network.Message;
import se.peertv.peertvsim.network.MessageDeliveryEvent;
import junit.framework.TestCase;




public class SchedulerTest extends TestCase{

	class DummyMsg extends Message{
		String name;
				
		public DummyMsg(String name) {
			super();
			this.name = name;
		}
	}
	
	class DummyEvent extends Event{
		public DummyEvent(long time, Object args) {
			super(time, args);
			
		}

		@Override
		public void handle() throws Throwable {
						
		}
		
		public DummyMsg getMsg(){
			return (DummyMsg)args;
		}
	}
	
	
	
	public void testInsertion() throws Exception{
		Scheduler.getInstance().enqueue(new DummyEvent(5, new DummyMsg("A")));
		Scheduler.getInstance().enqueue(new DummyEvent(3, new DummyMsg("B1")));
		Scheduler.getInstance().enqueue(new DummyEvent(8, new DummyMsg("C")));
		Scheduler.getInstance().enqueue(new DummyEvent(1, new DummyMsg("D")));
		Scheduler.getInstance().enqueue(new DummyEvent(3, new DummyMsg("B2")));
		Scheduler.getInstance().enqueue(new DummyEvent(3, new DummyMsg("B3")));
		Scheduler.getInstance().enqueue(new DummyEvent(3, new DummyMsg("B4")));
		Scheduler.getInstance().enqueue(new DummyEvent(3, new DummyMsg("B5")));
		
		
		DummyMsg msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		assertTrue(msg.name.equals("D"));
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		assertTrue(msg.name.equals("B1"));
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		assertTrue(msg.name.equals("B2"));
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		assertTrue(msg.name.equals("B3"));
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		assertTrue(msg.name.equals("B4"));
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		assertTrue(msg.name.equals("B5"));
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		assertTrue(msg.name.equals("E"));
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		assertTrue(msg.name.equals("A"));
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		assertTrue(msg.name.equals("C"));

	}

	public void testInsertion1() throws Exception{
		Scheduler.getInstance().clear();
		Scheduler.getInstance().enqueue(new DummyEvent(5, new DummyMsg("A")));
		Scheduler.getInstance().enqueue(new DummyEvent(3, new DummyMsg("B1")));
		Scheduler.getInstance().enqueue(new DummyEvent(8, new DummyMsg("C")));
		Scheduler.getInstance().enqueue(new DummyEvent(1, new DummyMsg("D")));
		Scheduler.getInstance().enqueue(new DummyEvent(3, new DummyMsg("B2")));
		Scheduler.getInstance().enqueue(new DummyEvent(3, new DummyMsg("B3")));
		Scheduler.getInstance().enqueue(new DummyEvent(3, new DummyMsg("B4")));
		Scheduler.getInstance().enqueue(new DummyEvent(3, new DummyMsg("B5")));
		
		
		DummyMsg msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		System.out.println(msg.name);
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		System.out.println(msg.name);
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		System.out.println(msg.name);
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		System.out.println(msg.name);
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		System.out.println(msg.name);
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		System.out.println(msg.name);
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		System.out.println(msg.name);
		msg = ((DummyEvent)Scheduler.getInstance().dequeue()).getMsg();
		System.out.println(msg.name);
	}


}
