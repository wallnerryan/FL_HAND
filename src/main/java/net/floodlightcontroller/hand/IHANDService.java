package net.floodlightcontroller.hand;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IHANDService extends IFloodlightService {
	
	public String getName();
    
    
    public Boolean isHANDEnabled();
    

    public void enableHAND(boolean enabled);

}
