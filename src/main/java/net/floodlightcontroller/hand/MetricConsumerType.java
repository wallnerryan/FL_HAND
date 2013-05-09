package net.floodlightcontroller.hand;

public class MetricConsumerType {
	
	/**
	 * Ganglia Metric Types
	 * @author wallnerryan
	 *
	 */
	public enum MetricType{
		BYTES_IN{
		      public String toString() {
		          return "bytes_in.rrd";
		      }
		  },
		BYTES_OUT{
		      public String toString() {
		          return "bytes_out.rrd";
		      }
		  },
		MEMORY_FREE{
		      public String toString() {
		          return "mem_free.rrd";
		      }
		  },
		DISK_FREE{
		      public String toString() {
		          return "disk_free.rrd";
		      }
		  },
		  
		  //TODO Add More
	}
}
