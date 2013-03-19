package net.floodlightcontroller.hand;


import java.io.IOException;
import net.stamfest.rrd.CommandResult;
import net.stamfest.rrd.RRDp;

public class MetricConsumer {
	
	private String metricPath = "/var/lib/ganglia/rrds/";
	String host;
    int port;
	boolean connection;
	
	public void setHost(String h){
		this.host = h;
	}
	
	public void setPort(int p){
		this.port = p;
	}
	
	public void setHostandPort(String h, int p){
		this.host = h;
		this.port = p;
	}
	
	public boolean testConnectToHost(){
		try{
			//Open and finish a test connection
			//to see if connection is available
			RRDp rrd = new RRDp(host, port);
			rrd.finish();
			connection = true;
		}catch (IOException e) {
			System.out.println("Error connecting to gmetric host, reason: "+ e);
			connection = false;
		}
		System.out.println("Connection: "+connection);
		
	return connection;
	}

	/**
	 * 
	 * @param h
	 * @throws Exception 
	 */
	public String getHostBytesIn(String cluster, String h) throws Exception{
		//rrds can be saved as IP's or hostnames. try both	
	
		//this.setConnection(host,port);
		RRDp rrd = new RRDp(host,port);
		
		//Defaults to LAST metric
		String[] command = {"fetch",
				metricPath+cluster+"/"+h+"/"+"bytes_in.rrd",
				"AVERAGE",
				"-r","10",
				"-s", "-1s"};
		
		
		//Send the command
		CommandResult result = rrd.command(command);
		if (!result.ok){
			System.out.println("Error getting LAST bytes_in");
			System.out.println(result.error);
			return "failed";
		}else{
			System.out.println(result.output);
			return result.output;
			}
		
	}


}
