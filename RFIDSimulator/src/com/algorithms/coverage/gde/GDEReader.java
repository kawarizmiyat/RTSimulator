package com.algorithms.coverage.gde;

import java.util.ArrayList;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Reader;
import com.algorithms.coverage.Tag;
import com.algorithms.coverage.TagContent;
import com.algorithms.coverage.WriteMessage;
import com.simulator.SimSystem;

public class GDEReader extends Reader {

	private static final boolean D = true;
	
	protected static final String STAT_IDLE = "STAT_IDLE";
	
	protected static final String STAT_FIRST_WRITE = "STAT_FIRST_WRITE";
	protected static final String STAT_FIRST_READ = "STAT_FIRST_READ";
	protected static final String STAT_SECOND_WRITE = "STAT_SECOND_WRITE"; 
	protected static final String STAT_SECOND_READ = "STAT_SECOND_READ"; 
	protected static final String STAT_MAKE_ROUND_DECISION = "STAT_MAKE_ROUND_DECISION";

/*	
 	private static final String STAT_WRITE_1 = "STAT_WRITE_1";
	private static final String STAT_READ_1 = "STAT_READ_1"; 
	private static final String STAT_WRITE_2 = "STAT_WRITE_2"; 
	private static final String STAT_READ_2 = "STAT_READ_2";
	*/
	
	protected static final String STAT_TERMINATE = "STAT_TERMINATE";
	
	protected static final String MSG_TIMER_FIRST_READ = "MSG_TIMER_FIRST_READ";
	protected static final String MSG_TIMER_SECOND_WRITE = "MSG_TIMER_SECOND_WRITE";
	protected static final String MSG_TIMER_SECOND_READ = "MSG_TIMER_SECOND_READ";
	protected static final String MSG_TIMER_MAKE_ROUND_DECISION = "MSG_TIMER_MAKE_ROUND_DECISION";

	protected static final String MSG_OVERWRITE = "MSG_OVERWRITE";
	protected static final String MSG_DEACTIVATE = "MSG_DEACTIVATE";

	

	private boolean localMaxima; 
	public int round; 
	public ArrayList<Integer> activeTags;
	public int maxIterations; 
	
	public GDEReader(SimSystem sim, int id) {
		super(sim, id);
		changeStatus(GDEReader.STAT_IDLE);
		
		activeTags = new ArrayList<Integer>(); 

		round = 0;
		
	}

	@Override
	protected void initProtocol() {
		
		// setActiveTags();
		for (int i = 0; i < neighborsTags.size();i++) { 
			activeTags.add(neighborsTags.get(i));
		}
		
		goToStatusMakeDecision();
		
		//startGDERound();
	}

	private void goToStatusMakeDecision() {
		Message m  = new Message(this.id, this.id, 
				GDEReader.MSG_TIMER_MAKE_ROUND_DECISION, null, 'r', 'r');
		handleStatusMakeRoundDecision(m);
	}

	protected void startGDERound() {
		
		changeStatus(GDEReader.STAT_FIRST_WRITE);
		localMaxima = false;
		round ++;

	
		// TODO:
		// This condition is added in LIMITED-GDE.
		// if (round > maxIterations) { 
		//	changeStatus(GDEReader.STAT_TERMINATE); 
		//	return;
		//}
		
		if (D) { 
			log.printf("*** reader %d starting round %d ***\n",  
					this.id,  round); 
		}	
		
		if (D) { 
			log.printf("The active tags of reader %d: ", this.id); 
			for (int i = 0; i < activeTags.size(); i++) { 
				log.printf("%d ", activeTags.get(i));
			}
			log.printf("\n");
		}
		
		for (int i = 0; i < activeTags.size(); i ++) { 
			WriteMessage msg = getWriteMessage();
			
			Message m = new Message(this.id, activeTags.get(i), 
					GDEReader.MSG_OVERWRITE, 
					msg, 
					Reader.myType, 
					Tag.myType);
					
			sendMessage(m);
		}
		
		 scheduleTimer(2* this.msgDelay(), 
					GDEReader.MSG_TIMER_FIRST_READ); 
			 changeStatus(GDEReader.STAT_FIRST_READ);
		
	}

	// It is extactl RREWriteMessage. so why we create a new method.
	protected WriteMessage getWriteMessage() {
		GDEWriteMessage msg = new GDEWriteMessage(this.id, this.activeTags.size(), this.round);
		return msg;
	}

	protected boolean isActive() {
		return (activeTags.size() > 0); 
	}

	@Override
	protected void handleReceivedMessage(Message message) {
		if (this.id != message.receiverId) { 
			log.printf("error at %d: reader %d received message is not destined to" +
					"the correct destination (%d != %d) \n", 
					this.id, this.id, message.receiverId);
		}
		
		
		if (D) { 
			
			if (message.receiverId == message.senderId) { 
				log.printf("reader %d wakes up with timer %s \n", 
						this.id, 
						message.msgType);
			} else if (message.senderId == -1 && message.msgType == Reader.MSG_INIT ) {
				
				log.printf("reader %d starts algorithm \n", this.id);
				
			} else {
			
				log.printf("reader %d received message (t: %s) from (%d) \n", 
					this.id, message.msgType, message.senderId);
			}
		}
		
		if (status == GDEReader.STAT_IDLE) { 
			handleStatusIdle(message);

			
		} else if (status == GDEReader.STAT_FIRST_READ) { 
			handleStatusFirstRead(message);
			
		} else if (status == GDEReader.STAT_SECOND_WRITE) { 
			handleStatusSecondWrite(message);
			

		} else if (status == GDEReader.STAT_SECOND_READ) {
			handleStatusSecondRead(message);
			
		} else if (status == GDEReader.STAT_MAKE_ROUND_DECISION) { 
			handleStatusMakeRoundDecision(message);
			
		} else { 
			log.printf("error at reader %d: cannot receive message at state %s \n",
					this.id, status);
			System.exit(0);
		}

		
	}

	protected void handleStatusSecondRead(Message message) {
		
		if (message.msgType == GDEReader.MSG_TIMER_SECOND_READ) { 
			
			// read all active neighbor tags. 
			// if deactivated, remove them from active tags. 
			// if tag.id == this.id --> own tag. 
			
			if (D) { 
				log.printf("reader %d reads the content of all its active tags to " +
						"decativate or own \n", this.id);
			}
			
			for (int i = 0; i < activeTags.size(); i++) { 
				GDETagContent tc = (GDETagContent) this.sim.readTag(activeTags.get(i));
				
				if (D) { 
				log.printf("active tag %d of reader %d is active: " + tc.active + " \n", 
						activeTags.get(i), this.id);
				}
				
				if (tc.active == false) { 
					
					if (D) { 
						log.printf("reader %d deactivates tag at the reader level %d \n", this.id, activeTags.get(i));
					}
					
					
					if (tc.id == this.id) { 

						ownTag(activeTags.get(i));
					}
					
					removeTag(activeTags, activeTags.get(i));
					i -- ;
					
				}
			
		
			}
			
			 scheduleTimer(this.msgDelay(), 
						GDEReader.MSG_TIMER_MAKE_ROUND_DECISION); 
			 changeStatus(GDEReader.STAT_MAKE_ROUND_DECISION);
			
			
		} else { 
			log.printf("error at reader %d: message type (%s) (rid:%d) " +
					"cannot be received " +
					"at %s \n", this.id, message.receiverId, 
					message.msgType, status); 
			System.exit(0);			
		}
		
	}

	private void removeTag(ArrayList<Integer> list, int id) {

		
		if (list.size() == 0) { 
			log.printf("warning: the list in removeTag is empty ! \n");
			System.exit(0);				
		}
		
		int index = -1; 
		for (int i = 0; i < list.size(); i++ ) { 
			if (list.get(i) == id) { 
				index = i;
				break;
			}
		}
		
		if (index == -1) { 
			log.printf("Error: we cannot find %d in the list ! \n", id);
			System.exit(0);
		}
		
		list.remove(index);

		
	}

	
	// TODO: 
	// We can make few modifications (see below) to make it work for LimitedGDE. 
	// LimitedGDE should be a subclass of GDEReader. 
	// LimitedGDE however should implements this function differently. It will be given the
	// priority in the excution.
	protected void handleStatusMakeRoundDecision(Message message) {
		
		if (status != GDEReader.STAT_MAKE_ROUND_DECISION && status != GDEReader.STAT_IDLE ) { 
			log.printf("error at reader %d, cannot run handleStatusMakeRoundDecision at status %s \n", 
					this.id, status); 
			System.exit(0);
		}
		
		
		if (message.msgType == GDEReader.MSG_TIMER_MAKE_ROUND_DECISION) { 
			
			if (! isActive()) { 
				if (D) { 
					log.printf("*** reader %d has all its neighbors tags deactivated and will terminate \n",
							this.id);
				}
				changeStatus(GDEReader.STAT_TERMINATE);
			} else { 
				
				// TODO: this where we should make changes to 
				// LIMITEDGDE. We check if the current round is the last.
				// If this is the case, then we execute RRE. Otherwise, 
				// we do startGDERound. 
				
				// However, this implementation will lead to at least 
				// one GDE round. 
				
				startGDERound();
			}
			
		} else { 

			log.printf("error at reader %d: message type (%s) (rid:%d) " +
					"cannot be received " +
					"at %s \n", this.id, message.receiverId, 
					message.msgType, status); 
			System.exit(0);
			
		}
		
	}



	// TODO: It is better to change the name of this state ! -- same for Write_2
	protected void handleStatusFirstRead(Message message) {
		
		if (message.msgType == GDEReader.MSG_TIMER_FIRST_READ) { 
			
			// Check if you are an owner of all your active neighbors tags. 
			// if yes, check a global variable called localMaxima to true. 
			// schedule a timer for the next state. 
			// your actions in the next state will depend on the value of localMaxima, 
			
			if (D) { 
				log.printf("reader %d is checking if it is a local maxima \n", this.id);
			}
			
			localMaxima = true;
			for (int i = 0; i < activeTags.size(); i++) { 
				TagContent tc = (TagContent) this.sim.readTag(activeTags.get(i));
				
			
				
				if (tc.id != this.id) { 

					localMaxima = false;
				}
			}

			
			if (D) { 
				log.printf("reader %d : localmaxima: " + localMaxima + " \n", this.id);
			}
			
			 scheduleTimer(this.msgDelay(), 
						GDEReader.MSG_TIMER_SECOND_WRITE); 
			 changeStatus(GDEReader.STAT_SECOND_WRITE);
			
		} else { 
			log.printf("error at reader %d: message type (%s) (rid:%d) " +
					"cannot be received " +
					"at %s \n", this.id, message.receiverId, 
					message.msgType, status); 
			System.exit(0);
		}
		
	}

	protected void handleStatusSecondWrite(Message message) {

		if (message.msgType == GDEReader.MSG_TIMER_SECOND_WRITE) { 
		
			if (localMaxima) { 
				
			
				
				for (int i = 0; i < activeTags.size(); i ++) { 
					// deactive(activeTags.get(i));
					// That is, send a a deactivation message. 
					
					DeactivationMessage msg = new DeactivationMessage(this.id);
					Message m = new Message(this.id, activeTags.get(i), 
							GDEReader.MSG_DEACTIVATE, 
							msg, 
							Reader.myType, 
							Tag.myType);
					
					sendMessage(m);
					
					if (D) { 
						log.printf("reader %d is deactivating tag %d \n", this.id, activeTags.get(i));
					}
					
				}
				
				
			}

			 scheduleTimer(2* this.msgDelay(), 
						GDEReader.MSG_TIMER_SECOND_READ); 
			 changeStatus(GDEReader.STAT_SECOND_READ);
			 
			
		} else { 
			log.printf("error at reader %d: message type (%s) (rid:%d) " +
					"cannot be received " +
					"at %s \n", this.id, message.receiverId, 
					message.msgType, status); 
			System.exit(0);
		}
	}

	protected void handleStatusIdle(Message message) {
		
		if (message.msgType == Reader.MSG_INIT) { 
			
			initProtocol();
		}  else { 
			log.printf("error at reader %d: cannot receive message type (%s) in %s", 
					message.msgType, status);
			System.exit(0);
		}
		
	}

	@Override
	public boolean isValidStatus(String str) {
		

		
		return (str == GDEReader.STAT_IDLE || 
				str == GDEReader.STAT_FIRST_WRITE ||
				str == GDEReader.STAT_FIRST_READ ||
				str == GDEReader.STAT_SECOND_WRITE ||
				str == GDEReader.STAT_SECOND_READ ||
				str == GDEReader.STAT_MAKE_ROUND_DECISION ||
				str == GDEReader.STAT_TERMINATE); 
	}

	@Override
	public boolean isTerminatedStatus(String str) {
		return (status == GDEReader.STAT_TERMINATE);
	}

}
