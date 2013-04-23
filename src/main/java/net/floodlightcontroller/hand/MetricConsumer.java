package net.floodlightcontroller.hand;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sourceforge.jrrd.*;

/**
 * 
 * @author wallnerryan
 *
 */

public class MetricConsumer {
	 protected static Logger log = LoggerFactory.getLogger(MetricConsumer.class);
	
	///Default for ganglia
	private String metricPath = "/var/lib/ganglia/rrds/";
	
	//Specific to your RRDTool fetch request
	Date startDate;
	Date endDate;
	long start;
	long end;
	long step;
	
	//Specifies where the host/cluster and specific data metric to fetch
	String cluster;
	String host;
	String rrd;
	
	public void setStartDate(Date sDate){
		this.startDate = sDate;
	}
	
	public void setEndDate(Date eDate){
		this.endDate = eDate;
	}
	
	public void setStart(long s){
		this.start = s;
	}
	
	public void setEnd(long e){
		this.end = e;
	}
	
	public void setStep(long st){
		this.step = st;
	}
	
	public void setCluster(String clusterName){
		this.cluster = clusterName;
	}
	
	public void setHost(String h){
		this.host = h;
	}
	
	public void setRRDName(String db){
		this.rrd = db;
	}
	
	/*
	 * Useful for metrics
	 */
	public long getTime(){
		long seconds = System.currentTimeMillis() / 1000l;
		return seconds;
	}
	
	/**
	 * Fetches cluster name from file system based on (@param String ip)
	 * @param ip
	 * @return
	 */
	public String fetchClusterByIP(String ip){
		
		//TODO
		
		return null;
	}
	
	/**
	 * Fetches cluster name from file system based on (@param String hostName)
	 * @param hostName
	 * @return
	 */
	public String fetchClusterByHostName(String hostName){
		
		//TODO
		
		return null;
	}
	
	/**
	 * Get the bytes inbound to the host
	 * Uses the Start and End times to subtract from current TFE(Time from Epoch)
	 * @param start
	 * @param end
	 * @param step
	 * @param cluster
	 * @param host
	 * @return
	 * @throws IOException
	 * @throws RRDException
	 */
	public DataChunk getNetworkInBytes(long start,long end, long step,
			String cluster, String host) throws IOException, RRDException{
		
		// Initialize the database and chunk to return
		RRDatabase net;
		DataChunk chunk;
		net = new RRDatabase(metricPath+cluster+"/"+host+"bytes_in.rrd");
		chunk = net.getData(ConsolidationFunctionType.AVERAGE,
				(this.getTime() - start),
				(this.getTime() - end), step);
		
		return chunk;
	}
	
	/**
	 * Get the bytes sent out by the host
	 * Uses the Start and End times to subtract from current TFE(Time from Epoch)
	 * @param start
	 * @param end
	 * @param step
	 * @param cluster
	 * @param host
	 * @return
	 * @throws IOException
	 * @throws RRDException
	 */
	public DataChunk getNetworkOutBytes(long start,long end, long step,
			String cluster, String host) throws IOException, RRDException{
		
		// Initialize the database and chunk to return
		RRDatabase net;
		DataChunk chunk;
		net = new RRDatabase(metricPath+cluster+"/"+host+"bytes_out.rrd");
		chunk = net.getData(ConsolidationFunctionType.AVERAGE,
				(this.getTime() - start),
				(this.getTime() - end), step);
		
		return chunk;
	}
	
	/**
	 * Get the memory free of the host
	 * Uses the Start and End times to subtract from current TFE(Time from Epoch)
	 * @param start
	 * @param end
	 * @param step
	 * @param cluster
	 * @param host
	 * @return
	 * @throws IOException
	 * @throws RRDException
	 */
	public DataChunk getMemFree(long start,long end, long step,
			String cluster, String host) throws IOException, RRDException{
		
		// Initialize the database and chunk to return
		RRDatabase net;
		DataChunk chunk;
		net = new RRDatabase(metricPath+cluster+"/"+host+"mem_free.rrd");
		chunk = net.getData(ConsolidationFunctionType.AVERAGE,
				(this.getTime() - start),
				(this.getTime() - end), step);
		
		return chunk;
	}
	
	
	/**
	 * Get the disk space free of the host
	 * Uses the Start and End times to subtract from current TFE(Time from Epoch)
	 * @param start
	 * @param end
	 * @param step
	 * @param cluster
	 * @param host
	 * @return
	 * @throws IOException
	 * @throws RRDException
	 */
	public DataChunk getDiskFree(long start,long end, long step,
			String cluster, String host) throws IOException, RRDException{
		
		// Initialize the database and chunk to return
		RRDatabase net;
		DataChunk chunk;
		net = new RRDatabase(metricPath+cluster+"/"+host+"mem_free.rrd");
		chunk = net.getData(ConsolidationFunctionType.AVERAGE,
				(this.getTime() - start),
				(this.getTime() - end), step);
		
		return chunk;
	}
	
	/**
	 * Gets the average for the values in a DataChunk
	 * @param c
	 * @return
	 */
	public double getAverage(DataChunk c){
		//initialize
		double avg = 0;
		
		@SuppressWarnings("unchecked") //temporary, should cast Date and Double to Map types
									   //to comply with Java generics
		Map<Date, Double> vals = c.toMap(0);
		if(!(vals.isEmpty())){
			for(Map.Entry<Date, Double> entry : vals.entrySet()){
				try{
					avg = avg + entry.getValue();
				}catch(IllegalStateException e){
					log.info(e.toString());
				}
			}
			return (avg / vals.size());
		}
		else{
			log.info("Empty Data Set, Nothing to Average: Returning 0");
			return 0;
		}
	}
	
	public double getLastValue(DataChunk c){
		
		@SuppressWarnings("unchecked") //temporary, should cast Date and Double to Map types
		   							   //to comply with Java generics
		Map<Date, Double> vals = c.toMap(0);
		Collection<Double> nums = vals.values();
		
		@SuppressWarnings({ "rawtypes", "unchecked" }) //temporary, should cast Date and Double to Map types
		   											   //to comply with Java generics
		ArrayList num_list = new ArrayList(nums);
		
		Double last;
		last = (Double) num_list.get((num_list.size() -1));
		return last;
	}
	
	/**
	 * Add data get functions and any other operations
	 * 
	 * 
	 * 
	 */
	


}
