package net.floodlightcontroller.hand;

import java.io.IOException;

import net.sourceforge.jrrd.ConsolidationFunctionType;
import net.sourceforge.jrrd.DataChunk;
import net.sourceforge.jrrd.RRDException;
import net.sourceforge.jrrd.RRDatabase;

public class NetworkMetrics extends MetricConsumerService {

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
	
	
}
