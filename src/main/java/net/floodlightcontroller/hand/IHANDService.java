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
	 * @return
	 */
	public String getName();
    
    /**
     * 
     * @return
     */
    public Boolean isHANDEnabled();
    
    /**
     * 
     * @param enabled
     */
    public void enableHAND(boolean enabled);
    
    /**
     * 
     * @return
     */
    public ArrayList<String> getMessages();

}
