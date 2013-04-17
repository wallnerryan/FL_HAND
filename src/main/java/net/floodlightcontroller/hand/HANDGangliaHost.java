package net.floodlightcontroller.hand;


public class HANDGangliaHost {
	
	public long hostId;
	
	public String cluster; //Translates to the cluster name used in RRDFetch
	public String hostName; //Translates the host name used for the RRDFetch
	public String ipAddress; //can use IPv4.toIPv4Address(String add)
	public String macAddress; //can use MACAddress.valueOf(String MACadd)
	public long firstHop;	//ID of first hop OF switch.
	
	public HANDGangliaHost(){
		
		this.hostId = 0;
		this.cluster = null;
		this.hostName = null;
		this.ipAddress = null;
		this.macAddress = null;
		this.firstHop = 0;
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
	 * Compare to another Host
	 * @param host
	 * @return
	 */
	public boolean isSameAs(HANDGangliaHost host) {
		boolean isSame;
		if (this.cluster == host.cluster
				&& this.hostName == host.hostName){
			isSame = true;
		}else if( this.ipAddress == host.ipAddress){
			isSame = true;
		}else{
			isSame=false;
			}
		
		return isSame;
	}
	
	/**
	 * Hash Code
	 * @return
	 */
	@Override
	public int hashCode(){
		final int primeNum = 2521;
		int result = super.hashCode();
		result = primeNum * result + cluster.hashCode();
		result = primeNum * result + hostName.hashCode();
		result = primeNum * result + ipAddress.hashCode();
		result = primeNum * result + macAddress.hashCode();
		return result;
		
	}

}
