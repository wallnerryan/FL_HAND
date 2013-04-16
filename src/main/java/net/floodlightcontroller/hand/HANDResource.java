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
		

		if (foo.equalsIgnoreCase("status")){
			return hand.isHANDEnabled();
		}
		

		else if (foo.equalsIgnoreCase("enable")){
			hand.enableHAND(true);
				return "{\"result\" : \"success\", \"details\" : \"HAND is running\"}";
		}
		

		else if (foo.equalsIgnoreCase("disable")){
			hand.enableHAND(false);
			return "{\"result\" : \"success\", \"details\" : \"HAND is stopped\"}";
		}
		
		else if (foo.equalsIgnoreCase("messages")){
			if(hand.getMessages().isEmpty()){
				return "There are currently no administrative messages.";
			}
			else{
				return hand.getMessages();
			}
		}
		
	
		//If {foo} does not match any option
		return "{\"status\" : \"failure\", \"details\" : \"invalid operation\"}";
	}

}
