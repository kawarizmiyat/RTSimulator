package com.algorithms.coverage.gde;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Reader;
import com.algorithms.coverage.Tag;
import com.algorithms.coverage.TagContent;
import com.algorithms.coverage.WriteMessage;
import com.simulator.SimSystem;



public class LimitedGDEReader extends GDEReader  {

	
	private static final boolean D = true;
	
	private static final String STAT_RRE_WRITE = "STAT_RRE_WRITE";
	private static final String STAT_RRE_READ = "STAT_RRE_READ";
	private static final String STAT_PREPARE_TERMINATION = "STAT_PREPARE_TERMINATION";
	
	private static final String MSG_TIMER_RRE_READ = "MSG_TIMER_RRE_READ";
	private static final String MSG_PREPARE_TERMINATION = "MSG_PREPARE_TERMINATION";

	
	private int maxIterations; 
	
	public LimitedGDEReader(SimSystem sim, int id, int mi) {
		super(sim, id);
		maxIterations = mi;
		
	}
	
	
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
				
				if (round == maxIterations) { 
					startRRERound();
				} else { 
					startGDERound();
				}
			}
			
		} else { 

			log.printf("error at reader %d: message type (%s) (rid:%d) " +
					"cannot be received " +
					"at %s \n", this.id, message.receiverId, 
					message.msgType, status); 
			System.exit(0);
			
		}
		
	}


	private void startRRERound() {

		
		changeStatus(LimitedGDEReader.STAT_RRE_WRITE);
		round ++;
		
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
					LimitedGDEReader.MSG_TIMER_RRE_READ); 
		 changeStatus(LimitedGDEReader.STAT_RRE_READ);
		
		
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
			
		} else if (status == LimitedGDEReader.STAT_RRE_READ) { 
			handleStatusRRERead(message);
			
		} else if (status == LimitedGDEReader.STAT_PREPARE_TERMINATION) { 
			handleStatusPrepareTermination(message);
			
		} else { 
			log.printf("error at reader %d: cannot receive message at state %s \n",
					this.id, status);
			System.exit(0);
		}

		
	}


	private void handleStatusPrepareTermination(Message message) {
		
		if (message.msgType == LimitedGDEReader.MSG_PREPARE_TERMINATION) { 
			
			changeStatus(GDEReader.STAT_TERMINATE);
			
		} else { 
			log.printf("error at reader %d: cannot receive message at state %s \n",
					this.id, status);
			System.exit(0);
		}
		
	}


	private void handleStatusRRERead(Message message) {
		
		if (message.msgType == LimitedGDEReader.MSG_TIMER_RRE_READ) { 
			
			if (D) { 
				log.printf("reader %d reads all its neighbor active tags" +
						" to check if it owns any of them \n", this.id);
			}
			
			// for every active tag, read the content of the tag. 
			// own it if necessary. 
			for (int i = 0; i < activeTags.size(); i++ ) { 
				TagContent tc = this.sim.readTag(activeTags.get(i));
				if (tc.id == this.id) { 
					ownTag(activeTags.get(i));
				}
			}
			
			 scheduleTimer(this.msgDelay(), 
						LimitedGDEReader.MSG_PREPARE_TERMINATION); 
			 changeStatus(LimitedGDEReader.STAT_PREPARE_TERMINATION);
			
		} else { 
			log.printf("error at reader %d: cannot receive message type (%s) in %s", 
					message.msgType, status);
			System.exit(0);
		}
		
	}


	public boolean isValidStatus(String str) {
		
		
		return (str == GDEReader.STAT_IDLE || 
				str == GDEReader.STAT_FIRST_READ ||
				str == GDEReader.STAT_FIRST_WRITE ||
				str == GDEReader.STAT_SECOND_WRITE ||
				str == GDEReader.STAT_SECOND_READ ||
				str == GDEReader.STAT_MAKE_ROUND_DECISION ||
				str == GDEReader.STAT_TERMINATE || 
				str == LimitedGDEReader.STAT_PREPARE_TERMINATION ||
				str == LimitedGDEReader.STAT_RRE_READ || 
				str == LimitedGDEReader.STAT_RRE_WRITE ); 
	}
	
	
}
