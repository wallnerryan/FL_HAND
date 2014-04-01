package net.floodlightcontroller.hand;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author wallnerryan
 *
 */

public class HANDMetricHostResource extends ServerResource {
	public static Logger logger = LoggerFactory.getLogger(HANDMetricHostResource.class);
	
	//TODO services /metrics/{host}/json
	/*
	 * Should recursively call and sort data from RRD files and
	 * output to JSON, or some text type. JSON Preferable.
	 */
	
	@Get("json")
	public Object hangleRequest(){
		// "Module Service" as "hand"
		
		
		//Get the {host}
		// e.g "host2"
		String foo = (String) getRequestAttributes().get("host");
		
		//Your return values stared here
		String json = null;
		
		//Based on host "foo"
		
		//1. Does host foo exist?
		
		//2 If it does, create a (MetricConsumer consumer = new MetricConsumer()) object to pull metrics
		
		//3 netInBytes = consumer.getNetworkInBytes( var var .. )
		// .. Mem
		// .. Disk
		
		//Take return values translate them into json (
				
		return ("{\"Metrics for \""+ foo +" : \"" + json + "\"}");
	}
	
	public String metricsToJson(String metricString){
		String returnedJson = null;
		
		//TODO parse metricString into json and return
		
		return returnedJson;
	}
	
}
