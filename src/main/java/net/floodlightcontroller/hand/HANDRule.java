package net.floodlightcontroller.hand;

import java.util.HashMap;

import net.floodlightcontroller.firewall.FirewallRule;
import net.floodlightcontroller.qos.QoSPolicy;

public class HANDRule implements Comparable<HANDRule>{
	
	public long ruleId;
	public int priority = 0;
	
	public int pollingTime;
	public long timeAdded;
	public long nextCheckTime;
	public boolean active;
	
	public HashMap<String, HANDThreshold> metrics;
	
	public FirewallRule fRule;
	public QoSPolicy qos;
	public String staticFlow;
	//TODO LoadBalancer Integration
	
	public enum actionType{
		AFW, 	//Add Firewall Rule
		DFW, 	//Delete Firewall Rule
		LB, 	//LoadBalance (placeholder for now //TODO )
		PSF, 	//Push Static Flow
		AQOS, 	//Add QoS Rule
		DQOS,	//Delete QoS Rule
		KR,		//Kill Route
	}
	
	public HANDRule(){
		
		//TODO
		
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

}
