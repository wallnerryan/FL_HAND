package net.floodlightcontroller.hand;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class HANDResource extends ServerResource {
	
	@Get("json")
	public Object handleRequest(){
		IHANDService hand = (IHANDService)getContext().getAttributes().
						get(IHANDService.class.getCanonicalName());
		
		//Retrieve the operation demanded in the REST url.
		String foo = (String) getRequestAttributes().get("foo");
		
		//TODO Return whether this is enabled or not. on status
		
		//TODO enable on "enable"
		
		//TODO disable on "disable"
		
		
	}

}
