package net.floodlightcontroller.hand;

import java.util.ArrayList;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IHANDService extends IFloodlightService {
	
	public String getName();
    
    
    public Boolean isHANDEnabled();
    

    public void enableHAND(boolean enabled);
    
    
    public ArrayList<String> getMessages();

}
