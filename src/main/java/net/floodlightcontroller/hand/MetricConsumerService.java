package net.floodlightcontroller.hand;


import java.io.File;
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

abstract class MetricConsumerService {
	 protected static Logger logger = LoggerFactory.getLogger(MetricConsumerService.class);
	
	/**
	 * Specified in 
	 * 
	 * (net.floodlightcontroller.hand.GangliaBasePath) 
	 * 
	 * in Ganglia Controller Properties
	 */
	public String metricPath = HAND.gangliaBasePath;
	
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
	 *  Fetches cluster name from file system based on (@param String ip)
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public String fetchClusterByIP(String ip) throws ClusterNotFoundException{
		
		String cluster = null;
		
		String dir = this.metricPath;
				
		File containingPath = new File(dir);
		logger.info("Searching for clusters in: {}", containingPath.getPath()); //debug
		
		for( File child : containingPath.listFiles()){
			//check if directory, clusters are listed as directories in Unix File Systems
			
			if(child.isDirectory() && !(child.toString().contains("unspecified")) 
					&& !(child.toString().contains("__SummaryInfo__")) ){
				String[] strDirs = child.toString().split("/");
				String curDir = strDirs[strDirs.length-1];
				logger.info("Checking Cluster: {}", curDir); //debug
				
				for(File rrd : child.listFiles()){
					String[] strRRDs = rrd.toString().split("/");
					String compareRRD = strRRDs[strRRDs.length-1];
					logger.debug("Comparing : {}", compareRRD);
					
					if(compareRRD.contains(ip)){
						cluster = curDir;
						break;
						} 
						
					}
				}
			}
		
		if(cluster != null){
			return cluster;
		}else{
			throw new ClusterNotFoundException("ClusterNotFoundException");
		}
					
		}
				

	
	/**
	 * Fetches cluster name from file system based on (@param String hostName)
	 * hostName can be HOSTNAME or HOSTNAME+DOMAIN
	 * @param hostName
	 * @return
	 * @throws Exception
	 */
	public String fetchClusterByHostName(String hostName) throws ClusterNotFoundException{
		
		String cluster = null;
		
		String dir = this.metricPath;
				
		File containingPath = new File(dir);
		logger.info("Searching for clusters in: {}", containingPath.getPath()); //debug
		
		for( File child : containingPath.listFiles()){
			
			//check if directory, clusters are listed as directories in Unix File Systems
			if(child.isDirectory() && !(child.toString().contains("unspecified")) 
					&& !(child.toString().contains("__SummaryInfo__")) ){
				String[] strDirs = child.toString().split("/");
				String curDir = strDirs[strDirs.length-1];
				logger.info("Checking Cluster: {}", curDir); //debug
				
				for(File rrd : child.listFiles()){
					String[] strRRDs = rrd.toString().split("/");
					String compareRRD = strRRDs[strRRDs.length-1];
					logger.debug("Comparing : {}", compareRRD);
					//Looks for "hostname" in any "hostname.rrd" file
					
					if(compareRRD.toLowerCase().contains(hostName.toLowerCase())){
						cluster = curDir;
						break;
						} 
						
					}
				}
			}
		
		if(cluster != null){
			return cluster;
		}else{
			throw new ClusterNotFoundException("ClusterNotFoundException");
		}
	}
	
	
	//Can be extended out from abrast class.
	
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
		net = new RRDatabase(metricPath+cluster+"/"+host+MetricConsumerType.MetricType.BYTES_IN);
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
		net = new RRDatabase(metricPath+cluster+"/"+host+MetricConsumerType.MetricType.BYTES_OUT);
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
		net = new RRDatabase(metricPath+cluster+"/"+host+MetricConsumerType.MetricType.MEMORY_FREE);
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
		net = new RRDatabase(metricPath+cluster+"/"+host+MetricConsumerType.MetricType.DISK_FREE);
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
					logger.info(e.toString());
				}
			}
			return (avg / vals.size());
		}
		else{
			logger.info("Empty Data Set, Nothing to Average: Returning 0");
			return 0;
		}
	}
	
	/**
	 * Kind of shotty, need to fix :)
	 * 
	 * Returns the last value in the DataChunk.
	 * Useful for last known measurement.
	 * @param c
	 * @return
	 */
	public double getLastValue(DataChunk c){
		
		/**
		 * Unchecked because no way of know values match DATE of DOUBLE.
		 * Should really verify them all before added them to MAP.
		 */
		
		@SuppressWarnings("unchecked") /**temporary, should cast Date and Double to Map types
		   							      to comply with Java generic**/
		Map<Date, Double> vals = c.toMap(0);
		Collection<Double> nums = vals.values();
		
		@SuppressWarnings({ "rawtypes", "unchecked" }) /**temporary, should cast Date and Double to Map types
		      											  to comply with Java generic**/
		ArrayList num_list = new ArrayList(nums);
		
		Double last;
		last = (Double) num_list.get((num_list.size() -1));
		return last;
	}
	
	/**
	 * Add data get functions and any other operations
	 * The above is pretty incomplete function-list of the
	 * metrics and types that you can get from RRDs
	 * 
	 * TODO
	 * 
	 */
	


}
