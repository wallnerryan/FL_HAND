package net.floodlightcontroller.hand;


import net.stamfest.rrd.CommandResult;
import net.stamfest.rrd.RRDp;

public class MetricTests {
	
 public static void main(String[] args) throws Exception{
	 
	RRDp rrd;
	rrd = new RRDp("192.168.56.102", 13900);
	String[] command = {"fetch", "/var/lib/ganglia/rrds/HAND-Cluster/controllerhost/bytes_in.rrd","AVERAGE","-r", "10", "-s", "-1s"};
	CommandResult result = rrd.command(command);
	if (!result.ok)
	    System.out.println(result.error);
	else
	    System.out.println(result.output);
	
	
 }

}
