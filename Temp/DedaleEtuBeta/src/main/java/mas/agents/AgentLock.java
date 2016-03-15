package mas.agents;

import java.util.HashMap;
import java.util.HashSet;

import jade.core.AID;

public class AgentLock {
	
	private long lock_move;
	private long mail_check;
	private HashMap<AID,Long> hmlock = new HashMap<AID,Long>();
	private final static long TTL = (long)0;
	private final static long MAILBOX_CHECK = (long)0;
	
	public AgentLock(AID aid){
		//hmlock.put(aid,(long)0);
		lock_move = System.currentTimeMillis()-TTL;
		mail_check = System.currentTimeMillis()-MAILBOX_CHECK;
	}
	
	public void set_mail_check(){
		mail_check = System.currentTimeMillis();
	}
	
	public boolean is_ready_move(){
		//System.out.println(lock_move);
		return ((lock_move+TTL) <= System.currentTimeMillis()) && ((mail_check+MAILBOX_CHECK) <= System.currentTimeMillis());
	}
	
	public void set_lock_move(AID aid){
		//System.out.println("Lock Set");
		this.lock_move = System.currentTimeMillis();
		this.hmlock.remove(aid);
		this.hmlock.put(aid, lock_move);
	}
	
	public HashSet<AID> locked_move(){
		HashSet<AID> result = new HashSet<AID>();
		for(AID aid : hmlock.keySet()){
			if(hmlock.get(aid)+TTL > System.currentTimeMillis())
				result.add(aid);
		}
		return result;
	}
	
	private long getMaxlock(){
		long max = System.currentTimeMillis()-TTL;
		for(long val : hmlock.values()){
			if(max < val)
				max = val;
		}
		return max;
	}
	
	public void unset_lock_move(AID aid){
		this.hmlock.remove(aid);
		this.lock_move = getMaxlock();
	}

}
