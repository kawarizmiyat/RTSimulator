package com.algorithms.coverage.gdesi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.algorithms.coverage.Message;
import com.algorithms.coverage.Reader;
import com.algorithms.coverage.Tag;
import com.algorithms.coverage.WriteMessage;
import com.simulator.SimSystem;

public class GDESIReader extends Reader {

	private static final boolean D = false;

	private static final String STAT_IDLE = "STAT_IDLE";
	private static final String STAT_MAKE_ROUND_DECISION = "STAT_MAKE_ROUND_DECISION";
	private static final String STAT_TERMINATE = "STAT_TERMINATE";
	private static final String STAT_WRITE_ROUND = "STAT_WRITE_ROUND";
	private static final String STAT_READ_ROUND = "STAT_READ_ROUND";

	protected static final String MSG_APPEND = "MSG_APPEND";
	private static final String MSG_TIMER_MAKE_ROUND_DECISION = "MSG_TIMER_MAKE_ROUND_DECISION";
	private static final String MSG_TIMER_READ_ROUND = "MSG_TIMER_READ_ROUND";

	private static final Integer NULL_OWNER = -1 ;

	
	private HashMap<Integer, Integer> wvMap; 
	public ArrayList<Integer> activeTags;
	private ArrayList<Integer> Pv; 
	public int round, count; 
	
	public GDESIReader(SimSystem sim, int id) {
		super(sim, id);
		// TODO Auto-generated constructor stub
		round = 0;
		Pv = new ArrayList<Integer>();
		wvMap = new HashMap<Integer, Integer>();
	}

	@Override
	protected void initProtocol() {

		
		// initiate active tags;
		for (int i = 0; i < neighborsTags.size();i++) { 
			activeTags.add(neighborsTags.get(i));
		}
		
		
		
		// initiate wvTable
		for (int i = 0; i < neighborsTags.size(); i++) { 
			wvMap.put(neighborsTags.get(i), GDESIReader.NULL_OWNER);
		}
		
		goToStatusMakeDecision();
		
	}

	private void goToStatusMakeDecision() {
		Message m  = new Message(this.id, this.id, 
				GDESIReader.MSG_TIMER_MAKE_ROUND_DECISION, null, 'r', 'r');
		handleStatusMakeRoundDecision(m);		
	}

	private void handleStatusMakeRoundDecision(Message message) {
		if (status != GDESIReader.STAT_MAKE_ROUND_DECISION && status != GDESIReader.STAT_IDLE ) { 
			log.printf("error at reader %d, cannot run handleStatusMakeRoundDecision at status %s \n", 
					this.id, status); 
			System.exit(0);
		}
		
		
		if (message.msgType == GDESIReader.MSG_TIMER_MAKE_ROUND_DECISION) { 
			
			if (! isActive()) { 
				if (D) { 
					log.printf("*** reader %d has all its neighbors tags deactivated and will terminate \n",
							this.id);
				}
				changeStatus(GDESIReader.STAT_TERMINATE);
			} else { 
				
				// TODO: this where we should make changes to 
				// LIMITEDGDE. We check if the current round is the last.
				// If this is the case, then we execute RRE. Otherwise, 
				// we do startGDERound. 
				
				// However, this implementation will lead to at least 
				// one GDE round. 
				
				startGDESIRound();
			}
			
		} else { 

			log.printf("error at reader %d: message type (%s) (rid:%d) " +
					"cannot be received " +
					"at %s \n", this.id, message.receiverId, 
					message.msgType, status); 
			System.exit(0);
			
		}
		
	}

	private void startGDESIRound() {
		
		// Change status and increase the number of rounds. 
		changeStatus(GDESIReader.STAT_WRITE_ROUND);
		round ++; 
		
		
		// Pv = activeTags at the beginning of the round. 
		Pv.clear(); 
		for (int i = 0; i < activeTags.size(); i++) { 
			Pv.add(activeTags.get(i)); 
		}
		
		// initialize the value of count at each round.
		count = Pv.size();
		
		
		// for debugging:
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

		
		// Create a message to each active tag, and send it.
		for (int i = 0; i < activeTags.size(); i ++) { 
			WriteMessage msg = getWriteMessage(activeTags.get(i));
			
			Message m = new Message(this.id, activeTags.get(i), 
					GDESIReader.MSG_APPEND, 
					msg, 
					Reader.myType, 
					Tag.myType);
					
			sendMessage(m);
		}
		
		// prepare for STAT_READ_ROUND
		scheduleTimer(2* this.msgDelay(), 
					GDESIReader.MSG_TIMER_READ_ROUND); 
		changeStatus(GDESIReader.STAT_READ_ROUND);

	
	}

	private WriteMessage getWriteMessage(int tag) {

		GDESIWriteMessage m = new GDESIWriteMessage(this.id, this.Pv, wv(tag), round ); 
		return m; 
	}

	private int wv(int tag) {
		return wvMap.get(tag); 
	}

	private boolean isActive() {
		return activeTags.size() > 0; 
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
		
		if (status == GDESIReader.STAT_IDLE) { 
			handleStatusIdle(message);

			
			
		} else if (status == GDESIReader.STAT_READ_ROUND) { 
			handleStatusReadRound(message);
			

			
		} else if (status == GDESIReader.STAT_MAKE_ROUND_DECISION) { 
			handleStatusMakeRoundDecision(message);
			
		} else { 
			log.printf("error at reader %d: cannot receive message at state %s \n",
					this.id, status);
			System.exit(0);
		}


		
	}

	private void handleStatusReadRound(Message message) {
		
		if (message.msgType == GDESIReader.MSG_TIMER_READ_ROUND) { 
			runGDESIReadRound();
		} else { 
			log.printf("error at reader %d: cannot receive message type (%s) in %s", 
					message.msgType, status);
			System.exit(0);
		}
		
	}



	private void runGDESIReadRound() {
		
		for (int i = 0; i < activeTags.size(); i ++ ) { 
			int t = activeTags.get(i); 
			GDESITagContent tContent = (GDESITagContent) this.sim.readTag(t);
			
			int vSharp = AG(t, tContent); 
			
			if (vSharp != GDESIReader.NULL_OWNER) { 
				
				// update the owner of t according to this.reader
				updateWVMap(t, vSharp);
				
				// deactivate tag t (remove from activeTags)
				removeTag(activeTags, t);
				i -- ;			// note, we decrease i because
				// the iteration is over ativeTags. 
				
				// remove tag t from pv 
				removeTag(this.Pv, t); 
				
				// TODO: define count and initialize it in each 
				// beginning of a round to activeTags.size();
				count --; 
				
				
				// own tag if vSharp is yourself.
				if (this.id == vSharp) { 
					this.ownTag(t);
				}
				
			} else { 
				vSharp = tContent.findMax();	// what does this functino do ?
				updateWVMap(t, vSharp);
				
				if (vSharp != this.id) { 
					// for each tag t' shared between vsharp and v in M(t), 
					// do: 
					// Pv = Pv - t', 
					// count --;
				}
				
			}
			
		}
		
	}



	private void removeTag(ArrayList<Integer> list, Integer id) {
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

	private void updateWVMap(int t, int vSharp) {
		if ( ! wvMap.containsKey(t) ) { 
			log.printf("error at reader %d. trying to update " +
					"a non-existing tag %d", this.id, t);
			System.exit(0);
		} else { 
			
			if (D) { 
				log.printf("reader %d updates the owner of tag %d with %d", 
						this.id, t, vSharp );
			}
			wvMap.put(t, vSharp);
		}
		
		
	
 	}

	
	// TODO: please test me. 
	private int AG(int t, GDESITagContent tContent) {
		
		// iterate over all the entities of tContent. 
		// if they are all the same (and not null) return this value 
		
		int lastWv = GDESIReader.NULL_OWNER;
		
		for (Map.Entry<Integer, GDESITagStruct> entry: tContent.table.entrySet()) { 

			// when to return null ? 
			
			// if there is a null 
			if (entry.getValue().wv == GDESIReader.NULL_OWNER) { 
				return GDESIReader.NULL_OWNER;
			}
			
			// if one of the value is different. 
			if (entry.getValue().wv != lastWv && lastWv != GDESIReader.NULL_OWNER) {
				return GDESIReader.NULL_OWNER;
			}
			
			if (lastWv == GDESIReader.NULL_OWNER && 
					entry.getValue().wv != GDESIReader.NULL_OWNER ) { 
				
				lastWv = entry.getValue().wv;
				
			}
	
		}
		
		// return any wv -- they are all the same ..
		return lastWv;
		
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

		return (str == GDESIReader.STAT_IDLE || 
				str == GDESIReader.STAT_MAKE_ROUND_DECISION || 
				str == GDESIReader.STAT_TERMINATE || 
				str == GDESIReader.STAT_WRITE_ROUND || 
				str == GDESIReader.STAT_READ_ROUND); 
	}

	@Override
	public boolean isTerminatedStatus(String str) {
		// TODO Auto-generated method stub
		return (str == GDESIReader.STAT_TERMINATE);
	}

}
