package net.floodlightcontroller.hand;

import java.util.HashMap;

import net.floodlightcontroller.firewall.FirewallRule;
import net.floodlightcontroller.qos.QoSPolicy;

public class HANDRule implements Comparable<HANDRule>{
	
	public long ruleId;
	public int priority = 0;
	
	public int pollingTime;	// 1,5,10,30,60 ... in seconds
	public long timeAdded;	//Date in seconds added
	public long nextCheckTime;
	public boolean active;
	public int checkLimit;
	public int currentCheck;
	
	public long hostAssoc;	//host ID of host who owns this rule
	
	public HashMap<String, HANDThreshold> metrics;
	
	public FirewallRule fRule;
	public QoSPolicy qos;
	public String staticFlow;
	//TODO LoadBalancer Integration
	
	public ActionType action;
	
	public enum ActionType{
		RPRT,	//Report threshold is reported
		AFW, 	//Add Firewall Rule
		DFW, 	//Delete Firewall Rule
		LB, 	//LoadBalance (placeholder for now //TODO )
		PSF, 	//Push Static Flow
		AQOS, 	//Add QoS Rule
		DQOS,	//Delete QoS Rule
		KR,		//Kill Route
	}
	
	public HANDRule(){
		this.ruleId = 0;
		this.priority = 0;
		this.pollingTime = 0;
		this.timeAdded = this.getCurrentTime();
		this.nextCheckTime = 0;
		this.active = false;
		this.metrics = new HashMap<String, HANDThreshold>();
		this.fRule = null;
		this.qos = null;
		this.staticFlow = null;
		this.hostAssoc = 0;
		this.checkLimit = 0;
		this.currentCheck = 0;
		this.action = ActionType.RPRT; //Default to Report
		
		
	}
	
	
	
	/**
	 * Get current time in seconds since Epoch.
	 * @return
	 */
	public long getCurrentTime(){
		long time = System.currentTimeMillis() / 1000l;
		return time;
				
	}
	
	/**
	 * Generate UID
	 * @return
	 */
	public int genUniqueId() {
		int uid = this.hashCode();
		if (uid <= 0 ){
			uid = Math.abs(uid);
			uid = uid * 1553;
		}
		return uid;
	}

	@Override
	public int compareTo(HANDRule rule) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * Hash Code
	 * @return
	 */
	@Override
	public int hashCode(){
		final int primeNum = 2521;
		int result = super.hashCode();
		
		//TODO
		
		return result;
		
	}

}
