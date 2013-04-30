package com.algorithms.coverage;

import java.util.ArrayList;

import com.algorithms.coverage.rre.RRETagContent;
import com.simulator.Event;
import com.simulator.EventType;
import com.simulator.SimSystem;

public abstract class SingleRoundReader extends Reader {

	
	private static final boolean D = true;
	
	public static final String MSG_TIMER_WAIT = "msg_timer_wait";
	public static final String MSG_OVERWRITE = "overwrite";
	
	private static final String STAT_IDLE = "idle";
	private static final String STAT_WAIT = "waiting";
	private static final String STAT_TERMINATE = "terminate";
	

	public ArrayList<Integer> ownedTags;
	
	public SingleRoundReader(SimSystem sim, int id) {
		super(sim, id);
		
		ownedTags = new ArrayList<Integer>();
		changeStatus(SingleRoundReader.STAT_IDLE);
	}

	@Override
	public void handleEvent(Event e) {
		
		if (e.time < now) { 
			log.printf("Error at %d (handleEvent): event in past ",
					this.id);
		}
		
		updateTimer(e.time);
		switch (e.action) { 
		
		case EventType.MESSAGE: 
			handleReceivedMessage(e.message);
			break ;
		
		default: 
			System.out.printf("Only messages are allowed in handleEvent");
			System.exit(0);
			break; 
		}
			
		

	}


	@Override
	protected void initProtocol() {
		
		if (D) { 
			log.printf("Reader %d is at initProtocol " +
					" with %d tags \n", this.id, neighborsTags.size());
		}
		
		// for each neighbor tag, 
		// send a message of type: overwrite.
		for (int i = 0; i < neighborsTags.size(); i++) { 
			
			
			// TODO: general use (create an abstract function generate 
			// write message) 
			WriteMessage msg = getWriteMessage();
			
			Message m = new Message(this.id, neighborsTags.get(i), 
					SingleRoundReader.MSG_OVERWRITE, 
					msg, 
					Reader.myType, 
					't');
					
			sendMessage(m);
		}
		
		 scheduleTimer(2* this.msgDelay(), 
				SingleRoundReader.MSG_TIMER_WAIT); 
		 changeStatus(SingleRoundReader.STAT_WAIT);
		//status = RREReader.STAT_WAIT;
			
			
		

	}


	protected abstract WriteMessage getWriteMessage(); 
	// {
	//	RREWriteMessage rreMsg = new RREWriteMessage(this.id, 
	//			this.numNeighborTags);
	//	return rreMsg;
	// }

	@Override
	protected void handleReceivedMessage(Message message) {
		if (this.id != message.receiverId) { 
			log.printf("Error: received message is not destined to" +
					"the correct destination (%d != %d) \n", 
					this.id, message.receiverId);
		}
		
		
		if (status == SingleRoundReader.STAT_IDLE) { 
			handleStatusIdle(message);
		} else if (status == SingleRoundReader.STAT_WAIT) { 
			handleStatusWait(message);
		}
		

	}

	private void handleStatusWait(Message message) {
		
		// for each neighbor tag, 
		// read current content, if ccontent id is equal to 
		// my id, then become owner.
		
		// TODO: how to implement the read operation ?
		
		if (message.msgType == SingleRoundReader.MSG_TIMER_WAIT) { 
		
		for (int i = 0; i < neighborsTags.size(); i++) { 
			TagContent tc = (TagContent) this.sim.readTag(neighborsTags.get(i));
			

			
			
			
			if (tc.id == this.id) { 
				ownTag(i);


			}
		}
		
		
		// TODO: 
		// update timer here. 
		// require a better understanding of how timer work.
		// One way of implementing this is by sending a dummy message 
		// to yourself and then do that. However, this is equivalent
		// to increasing the timer at TIMER_WAIT.

		updateTimer(now + 1);
		changeStatus(SingleRoundReader.STAT_TERMINATE);
		// status = RREReader.STAT_TERMINATE;
	
		
		} else { 
			log.printf("Reader %d expectining timer msg \n");
			System.exit(0);
		}
		
	}



	private void ownTag(int i) {
		
		if (D) { 
			log.printf("Reader %d owns Tag %d \n", 
					this.id, i);
		}
		ownedTags.add(i);
	}

	private void handleStatusIdle(Message message) {
		
		if (message.msgType == SingleRoundReader.MSG_INIT) { 
			initProtocol();
		}  else { 
			log.printf("Error: cannot receive an init message in IDLE");
			System.exit(0);
		}
	}

	@Override
	public boolean isTerminated() {
		return isTerminatedStatus(status);
	}

	@Override
	protected void changeStatus(String str) {
		if (isValidStatus(str)) { 
			status = str; 
			if (isTerminatedStatus(str)) { 
				log.printf("Reader %d terminated at %f \n",
						this.id, this.now);
			}
		}
 	}

	private boolean isTerminatedStatus(String str) {
		return str == SingleRoundReader.STAT_TERMINATE;
	}

	private boolean isValidStatus(String str) {
		
		return (str == SingleRoundReader.STAT_IDLE || 
				str == SingleRoundReader.STAT_WAIT || 
				str == SingleRoundReader.STAT_TERMINATE); 
		
	}
}