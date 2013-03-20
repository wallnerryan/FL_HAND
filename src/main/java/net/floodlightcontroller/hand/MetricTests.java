package net.floodlightcontroller.hand;



import net.sourceforge.jrrd.*;

public class MetricTests {
	
	public long getTime(){
		long seconds = System.currentTimeMillis() / 1000l;
		return seconds;
	}
	
 public static void main(String[] args) throws Exception{
	 
	/*
	 RRDp rrd;
	
	rrd = new RRDp("192.168.56.102", 13900);
	String[] command = {"fetch", "/var/lib/ganglia/rrds/HAND-Cluster/controllerhost/bytes_in.rrd","AVERAGE","-r", "10", "-s", "-1s"};
	CommandResult result = rrd.command(command);
	if (!result.ok)
	    System.out.println(result.error);
	else
	    System.out.println(result.output);
	*/
	
	 MetricTests m = new MetricTests();
	
	 RRDatabase network_in = new RRDatabase("/Users/wallnerryan/Desktop/bytes_out.rrd");
	 System.out.println(network_in.getLastUpdate());
	 try{
		 //go back 14 days
		 DataChunk chunk = network_in.getData(ConsolidationFunctionType.AVERAGE, (m.getTime() - 1145000), m.getTime(), 1);
		 //DataChunk chunk = network_in.getData(ConsolidationFunctionType.AVERAGE);
		 System.out.println("printing datachunk");
		 System.out.println(chunk.toString());
	 }catch (Exception e){
		 System.out.println(e);
	 }
 }

}
