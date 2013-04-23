package net.floodlightcontroller.hand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.util.MACAddress;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Delete;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;

public class HANDGangliaHostsResource extends ServerResource {
	public static Logger logger = LoggerFactory.getLogger(HANDGangliaHostsResource.class);
	
	
	@Get("json")
	public Object handleRequest(){
		IHANDService hand = 
				(IHANDService)getContext().getAttributes().
				get(IHANDService.class.getCanonicalName());
		
		String status = null;
		
		if(hand.isHANDEnabled()){
			return hand.getHosts();
		}
		else{
			status = "Please enable Host Aware Networking Decisions";
			return ("{\"status\" : \"" + status + "\"}");
		}
	}
	
	/**
	 * 
	 * @param hostJson
	 * @return
	 */
	@Post
	public String addHost(String hostJson){
		IHANDService hand = 
					(IHANDService)getContext().getAttributes().
					get(IHANDService.class.getCanonicalName());
			
		HANDGangliaHost host;
		String status = null;
			
		try{
			host = jsonToGangliaHost(hostJson);
		}
		catch(IOException e){
			logger.error("Error parsing Ganglia host to JSON: {}, Error: {}", hostJson, e);
    		e.printStackTrace();
    		return "{\"status\" : \"Error! Could not parse Ganglia host, see log for details.\"}";
		}
		if(hostExists(host, hand.getHosts())){
			status = "Error!, This host already exists!";
			logger.error(status);
		}
		else{
			if(hand.isHANDEnabled()){
				
				/**Required Field Checks**/
				//hostname or ip is required. b/c Ganglia stores RRD's by them.
				if(host.hostName == null && host.ipAddress == 0){
					status = "Must provide either a Hostname or IP Address";
				}
				if(host.cluster == null){
					logger.info("Fetching Cluster Information...");
					if(existsInCluster(host)){
						//Send to HAND Module for addition.
						hand.addGangliaHost(host);
						status = "Success, adding host to HAND.";
					}else{
						status = "Host does not exist in any cluster.";
					}
				}
				/**
				 * MAC, Cluster, and First Hop Switch will be added later b/c
				 * Floodlight will already has this info if not provided.
				 */
			}
		}
		return "{\"status\" : "+ status +"}";
	 
	 }
	 
	/**
	 * 
	 * @param hostJson
	 * @return
	 */
	 @Delete
	 public String deleteHost(String hostJson){
		 
		//TODO
		 /**
		  * 
		  *  !!!!!!!!!!!!!!!!!!!!
		  * 
		  * 
		  */
		 
		return null;
		 
	 }

	private static HANDGangliaHost jsonToGangliaHost(String hostJson) throws IOException{
		HANDGangliaHost host = new HANDGangliaHost();
		
		MappingJsonFactory jsonFactory = new MappingJsonFactory();
		JsonParser parser;
		
		
		try{
			parser = jsonFactory.createJsonParser(hostJson);
		}catch(JsonParseException e){
			throw new IOException (e);
		}
		
		JsonToken token = parser.getCurrentToken();
		if(token != JsonToken.START_OBJECT){
			parser.nextToken();
			if(parser.getCurrentToken() != JsonToken.START_OBJECT){
				logger.error("did not recieve json start token, current token is: {}"
						+ parser.getCurrentToken());
			}
		}
		//Start parsing
		while(parser.nextToken() != JsonToken.END_OBJECT){
			if(parser.getCurrentToken() != JsonToken.FIELD_NAME){
    			throw new IOException("FIELD_NAME expected");
    		}
    		
    		try{
    			String name = parser.getCurrentName();
    			parser.nextToken();
    			
    			//current text in parser
    			String jsonText = parser.getText();
    			
    			logger.info("JSON Parser text is: {}", jsonText); //DEBUG
    			
    			if(jsonText.equals("")){
    				//ignore empty string, return to loop
    				continue;
    			}
    			else if(name == "host_name"){
    				host.hostName = jsonText;
    			}
    			else if(name == "ip_address"){
    				host.ipAddress = IPv4.toIPv4Address(jsonText);
    				
    			}
    			else if(name == "mac_address"){
    				host.macAddress = MACAddress.valueOf(jsonText);
    			}
    			else if(name == "cluster"){
    				host.cluster = jsonText;
    			}
    			else if(name == "first_hop"){
    				host.firstHop = Long.parseLong(jsonText);
    			}
    			
    		}catch(JsonParseException e){
    			logger.debug("Error getting current FIELD_NAME {}", e);
    		}catch(IOException e){
    			logger.debug("Error procession Json {}", e);
    		}
		}
		//Return the HANDGangliaHost Object to the GangliaHostResource
		return host;
	}
	
	/**
	 * 
	 * @param host
	 * @param hostList
	 * @return
	 */
	public static boolean hostExists(HANDGangliaHost host,
			ArrayList<HANDGangliaHost> hostList){
		
		Iterator<HANDGangliaHost> hostIter = hostList.iterator();
		while(hostIter.hasNext()){
			HANDGangliaHost h = hostIter.next();
			if(host.isSameAs(h) || host.macAddress.equals(h.macAddress)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks to make sure a cluster exists for this Host
	 * @param host
	 * @return
	 */
	public static boolean existsInCluster(HANDGangliaHost host){
		
		//TODO
		
		return false;
	}

}
