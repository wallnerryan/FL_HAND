package net.floodlightcontroller.hand;

import java.util.ArrayList;
import net.floodlightcontroller.core.module.IFloodlightService;

/**
 * 
 * @author wallnerryan
 *
 */

public interface IHANDService extends IFloodlightService {
	
	/**
	 * 
	 * 
	 */
	public String getName();
    
    /**
     * 
     * 
     */
    public Boolean isHANDEnabled();
    
    /**
     * 
     * 
     */
    public void enableHAND(boolean enabled);
    
    /**
     * 
     * 
     */
    public ArrayList<String> getMessages();
    
    
    /**
     * 
     * 
     */
    public ArrayList<HANDRule> getRules();
    	
    
    /**
     * 
     * 
     */
    public ArrayList<HANDGangliaHost> getHosts();
    
    /**
     * 
     * 
     * 
     */
    public void addGangliaHost(HANDGangliaHost host);
    	
    
    

}
