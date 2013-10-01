package net.floodlightcontroller.hand;

import java.util.HashMap;

import net.floodlightcontroller.firewall.FirewallRule;
import net.floodlightcontroller.qos.QoSPolicy;

public class HANDRule implements Comparable<HANDRule>{
	
	public long ruleId;
	public String name;
	public int priority = 0;
	
	public int pollingTime;	// 1,5,10,30,60 ... in seconds
	public long timeAdded;	//Date in seconds added
	public long nextCheckTime;
	public boolean active;
	public int checkLimit;	//number of times it can be checked. (ttl?)
	public int currentCheck; //current number checked
	
	public long hostAssoc;	//host ID of host who owns this rule
	
	public HashMap<String, HANDThreshold> metrics;
	
	public FirewallRule fRule;
	public QoSPolicy qos;
	public String staticFlow;
	//TODO LoadBalancer Integration
	
	public ActionType action;
	public String actionString;
	
	public enum ActionType{
		RPRT,	//Report threshold is reported
		AFR, 	//Add Firewall Rule
		DFW, 	//Delete Firewall Rule
		LB, 	//LoadBalance (placeholder for now //TODO )
		PSF, 	//Push Static Flow
		AQOS, 	//Add QoS Rule
		DQOS,	//Delete QoS Rule
		KR,		//Kill Route
	}
	
	public HANDRule(){
		this.ruleId = 0;
		this.name = null;
		this.priority = 0;
		this.pollingTime = 1; //default to 1 second
		this.timeAdded = this.getCurrentTime();
		this.nextCheckTime = 0; //when checked, add polling time to current time
		this.active = false;
		this.metrics = new HashMap<String, HANDThreshold>();
		this.fRule = null;
		this.qos = null;
		this.staticFlow = null;
		this.hostAssoc = 0;
		this.checkLimit = Integer.MAX_VALUE; //essentially unlimited checks.
		this.currentCheck = 0;
		this.action = ActionType.RPRT; //Default to Report
		this.actionString = ""; //This correlates to the ActionType
		
		
	}
	
	/**
	 * Return Rule's polling time.
	 * used in rule time synchronization.
	 */
	public int getPollingTime(){
		return this.pollingTime;
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

	/**
	 * Compare to another Rule
	 * @param rule
	 * @return
	 */
	public boolean isSameAs(HANDRule rule) {
		boolean isSame;
		if (this.name == rule.name
				&& this.timeAdded == rule.timeAdded){
			isSame = true;
		}else if( this.ruleId == rule.ruleId ||
				rule.equals(this)){
			isSame = true;
		}else{
			isSame=false;
			}
		
		return isSame;
	}
	
	@Override
	public int compareTo(HANDRule rule) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 *TODO
	 */
	public boolean checkActionType(String actionString){
		//TODO check string against ActionTypes
		return true;
	}
	
	/**
	 * Hash Code
	 * @return
	 */
	@Override
	public int hashCode(){
		final int primeNum = 2521;
		int result = super.hashCode();
		result = primeNum * result + priority;
		result = primeNum * result + pollingTime;
		result = primeNum * result + (int) nextCheckTime;
	    result = primeNum * result + (active ? 1 : 0);
		result = primeNum * result + checkLimit;
		result = primeNum * result + currentCheck;
		result = primeNum * result + (int) hostAssoc;
		result = primeNum * result + metrics.hashCode();
		result = primeNum * result + fRule.hashCode();
	    result = primeNum * result + qos.hashCode();
		result = primeNum * result + staticFlow.hashCode();
		result = primeNum * result + action.hashCode();
		return result;
		
	}

}
