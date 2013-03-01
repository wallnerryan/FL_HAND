package net.floodlightcontroller.hand;

import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.routing.Router;

public class HANDWebRoutable implements RestletRoutable {

	 /**
     * Create the Restlet router
     */
    @Override
    public Router getRestlet(Context context) {
        Router router = new Router(context);
        
        /**
         * Manipulates the module
         **/
        router.attach("/module/{op}/json", HANDResource.class);
        
        /**
         * Shows decision rule and host/cluster pairings
         * or tries to add a rules to a device monitored by
         * Ganglia
         */
        router.attach("/rules/json", HANDRulesResource.class);
        
        /**
         * Shows the hosts that are currently polled, or tries to add
         *a host with MAC/IP pairing
         **/
        router.attach("/ganglia/json", HANDGangliaHostsResource.class);
        return router;
    }

    /**
     * Set the base path for HAND
     * "ghand" ganglia host aware networking decisions
     */
    @Override
    public String basePath() {
        return "/wm/ghand";
    }

}
