package com.algorithms.coverage.drre;

import java.util.ArrayList;
import java.util.Collections;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Reader;
import com.algorithms.coverage.Tag;
import com.algorithms.coverage.WriteMessage;
import com.my.utilities.MyUtil;
import com.simulator.SimSystem;




public class DRREReader  extends Reader {


	private static final boolean D = true;

	// states: 
	protected static final String STAT_IDLE = "STAT_IDLE";
	protected static final String STAT_FIRST_WRITE = "STAT_FIRST_WRITE";
	protected static final String STAT_FIRST_READ = "STAT_FIRST_READ";
	protected static final String STAT_SECOND_WRITE = "STAT_SECOND_WRITE"; 
	protected static final String STAT_SECOND_READ = "STAT_SECOND_READ"; 
	protected static final String STAT_TERMINATE = "STAT_TERMINATE";
	private static final String STAT_PREPARE_TERMINATION = 
				"STAT_PREPARE_TERMINATION";
	
	// messages:
	protected static final String MSG_TIMER_FIRST_READ = "MSG_TIMER_FIRST_READ";
	protected static final String MSG_TIMER_SECOND_WRITE = "MSG_TIMER_SECOND_WRITE";
	protected static final String MSG_TIMER_SECOND_READ = "MSG_TIMER_SECOND_READ";

	protected static final String MSG_APPEND = "MSG_APPEND";
	protected static final String MSG_OVERWRITE = "MSG_OVERWRITE";

	private static final String MSG_TIMER_PREPARE_TERMINATION = 
			"MSG_TIMER_PREPARE_TERMINATION";



	private ArrayList<Integer> secondDegReaders; 

	public DRREReader(SimSystem sim, int id) {
		super(sim, id);
		changeStatus(DRREReader.STAT_IDLE);
		round = 0; 
		secondDegReaders = new ArrayList<Integer>();
	}

	@Override
	protected void initProtocol() {

		startWriteRound(); 
	}

	private void startWriteRound() {
		changeStatus(DRREReader.STAT_FIRST_WRITE); 
		round ++;
		
		
		if (D) { 
			log.printf("*** reader %d starting round %d *** \n", 
					this.id, round);
		}

		for (int i = 0; i < this.neighborsTags.size(); i++ ) { 

			WriteMessage msg = getWriteMessage();

			String msType = getMessageType(round);
			Message m = new Message(this.id, neighborsTags.get(i), 
					msType, 
					msg, 
					Reader.myType, 
					Tag.myType);

			if (D) { 
				log.printf("reader %d sends message %s (%s) to tag %d \n",
						this.id, msg.toString(), 
						msType, neighborsTags.get(i) ); 
			}
			
			sendMessage(m);

		}




		if (round == 1) { 
			scheduleTimer(2* this.msgDelay(), 
					DRREReader.MSG_TIMER_FIRST_READ); 
			changeStatus(DRREReader.STAT_FIRST_READ);
		} else if (round == 2) { 
			scheduleTimer(2* this.msgDelay(), 
					DRREReader.MSG_TIMER_SECOND_READ); 
			changeStatus(DRREReader.STAT_SECOND_READ);

		} else { 
			log.printf("error at reader %d: cannot have round(%d) > 2 ", 
					this.id, round);
			System.exit(0);
		}

	}

	private WriteMessage getWriteMessage() {

		if (round == 1) { 
			return new DRREWriteMessage
						(this.id, -1); 
		} else if (round == 2) { 
			return new DRREWriteMessage(this.id, this.secondDegReaders.size());
		
			
			
		} else { 
			log.printf("error at reader %d " +
					"must be in round 1 or 2 at getWriteMessage", 
					this.id);
			System.exit(0);
			return null;
		}
	}



	private String getMessageType(int round) {
		if (round == 1) { 
			return DRREReader.MSG_APPEND; 
		} else if (round == 2) { 
			return DRREReader.MSG_OVERWRITE; 
		} else {
			log.printf("round is not recognized - at getMessage()  at reader \n", this.id);
			System.exit(0); 
			return null; 
		}
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


		if (status == DRREReader.STAT_IDLE) { 
			handleStatusIdle(message);


		} else if (status == DRREReader.STAT_FIRST_READ) { 
			handleStatusFirstRead(message);

		} else if (status == DRREReader.STAT_SECOND_WRITE) { 
			handleStatusSecondWrite(message);


		} else if (status == DRREReader.STAT_SECOND_READ) {
			handleStatusSecondRead(message);

		} else if (status == DRREReader.STAT_PREPARE_TERMINATION) { 
			handleStatusPrepareTermination(message);
			
		} else { 
			log.printf("error at reader %d: cannot receive message at state %s \n",
					this.id, status);
			System.exit(0);
		}


	}

	private void handleStatusPrepareTermination(Message message) {

		if (message.msgType == DRREReader.MSG_TIMER_PREPARE_TERMINATION) { 
			
			changeStatus(DRREReader.STAT_TERMINATE);
			
			
		} else { 
			log.printf("error at reader %d: message type (%s) (rid:%d) " +
					"cannot be received " +
					"at %s \n", this.id, message.receiverId, 
					message.msgType, status); 
			System.exit(0);			
		}
		
	}

	private void handleStatusSecondRead(Message message) {
		if (message.msgType == DRREReader.MSG_TIMER_SECOND_READ) { 

			for (int i = 0; i < neighborsTags.size(); i++) { 
				
				DRRETagContent tc = 
						(DRRETagContent) this.sim.readTag(neighborsTags.get(i));
			
				if (tc.maxReader == this.id) { 
					ownTag(neighborsTags.get(i));
					
					if (D) { 
						log.printf("reader %d owns tags %d \n", 
								this.id, neighborsTags.get(i));
					}
				}
				
			}
			
			
			scheduleTimer(this.msgDelay(), 
					DRREReader.MSG_TIMER_PREPARE_TERMINATION); 
			changeStatus(DRREReader.STAT_PREPARE_TERMINATION);			
			
		} else { 
			log.printf("error at reader %d: message type (%s) (rid:%d) " +
					"cannot be received " +
					"at %s \n", this.id, message.receiverId, 
					message.msgType, status); 
			System.exit(0);
			
		}
	}

	private void handleStatusSecondWrite(Message message) {
		this.startWriteRound();
	}

	private void handleStatusFirstRead(Message message) {

		if (message.msgType == DRREReader.MSG_TIMER_FIRST_READ) {

			// for every neighbor tag. 
			for (int i = 0; i < neighborsTags.size(); i++) { 
				DRRETagContent tc = (DRRETagContent) 
						this.sim.readTag(neighborsTags.get(i));

				addUnique(secondDegReaders, tc.readers);

				if (D) { 
					log.printf("reader %d added %s \n", this.id, 
							MyUtil.toString(tc.readers) );
				}
				
			}


			if (D) { 
				log.printf("reader %d second deg readers %s \n", 
						this.id, MyUtil.toString(this.secondDegReaders));
			}
			
			
			
			scheduleTimer(this.msgDelay(), 
					DRREReader.MSG_TIMER_SECOND_WRITE); 
			changeStatus(DRREReader.STAT_SECOND_WRITE);


		} else { 

			log.printf("error at reader %d: message type (%s) (rid:%d) " +
					"cannot be received " +
					"at %s \n", this.id, message.receiverId, 
					message.msgType, status); 
			System.exit(0);

		}

	}


	private void addUnique(ArrayList<Integer> dest,
			ArrayList<Integer> source) {

		
//// 		Method: 2:
		
		// System.out.println("From: source " + MyUtil.toString(source));
		// System.out.println("To: dest " + MyUtil.toString(dest));
		
		Collections.sort(dest);
		Collections.sort(source);
		
		ArrayList<Integer> result = new ArrayList<Integer>();
		int j = 0, i = 0; 
		while (j < source.size()) { 
			
			if (i == dest.size()) { 
				// System.out.printf("i : dest.size() \n");
				copy(source, j, result);
				break;
			}

			// System.out.printf("comp: source(j): %d - dest(i): %d \n",
			//		source.get(j), dest.get(i));
			
			if (source.get(j) < dest.get(i)) {
				// for sure, source.get(j) is not in dest. 
				
				// System.out.printf("adding %d to result", source.get(j));
				
				result.add(source.get(j)); 
				j ++; 
			
			} else if (source.get(j) > dest.get(i)) { 
				// No decision can be made.
				i ++; 
			
			} else { 
				// source.get(j) exists in dest.. both indices are advanced.
				i ++; j ++; 
			}
			
		}

		// System.out.println("dest: " + MyUtil.toString(dest));
		// System.out.println("result: " + MyUtil.toString(result));
		
		for (int k = 0; k < result.size(); k++) { 
			dest.add(result.get(k));
		}
		
		Collections.sort(dest);
		// System.out.println("dest: " + MyUtil.toString(dest));
	}






	private static void copy(ArrayList<Integer> s, int j,
			ArrayList<Integer> result) {

		for (int k = j; k < s.size(); k++) 
			result.add(s.get(k));
		
	}


	private void handleStatusIdle(Message message) {

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

		return (str == DRREReader.STAT_TERMINATE || 
				str == DRREReader.STAT_FIRST_READ || 
				str == DRREReader.STAT_FIRST_WRITE || 
				str == DRREReader.STAT_SECOND_READ || 
				str == DRREReader.STAT_SECOND_WRITE || 
				str == DRREReader.STAT_PREPARE_TERMINATION || 
				str == DRREReader.STAT_IDLE);
	}

	@Override
	public boolean isTerminatedStatus(String str) {
		return (str == DRREReader.STAT_TERMINATE);
	}



}
