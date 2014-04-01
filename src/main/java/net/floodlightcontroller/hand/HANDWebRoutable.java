package net.floodlightcontroller.hand;

import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.routing.Router;

/**
 * 
 * @author wallnerryan
 *
 */

public class HANDWebRoutable implements RestletRoutable {

	 /**
     * Create the Reslet router
     */
    @Override
    public Router getRestlet(Context context) {
        Router router = new Router(context);
        
        /**
         * Manipulates the module
         **/
        router.attach("/module/{foo}/json", HANDResource.class);
        
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
        router.attach("/hosts/json", HANDGangliaHostsResource.class);
        
        //metric api service
        //router.attach("/metrics/{host}/{type}/json", HANDMetricHostTypeResource.class);
        //TODO Need to add Class
        
        router.attach("/metrics/{host}/json",  HANDMetricHostResource.class);
        //http://10.11.17.120:8080/wm/hand/metrics/host1/disk/json
        
        /**
         * This may be unrealistic, getting all nodes and all metrics,
         * Calls for super overlaod in large networks :)
         * We can already get hosts from API, lets say a combo
         * of host lookup, then metrics with either all or type should be called.
         */
        //router.attach("/metrics/json", HANDMetricsResource.class);
        //http://10.11.17.120:8080/wm/hand/metrics/json
        
        return router;
    }

    /**
     * Set the base path for HAND
     * "hand" Ganglia's host aware networking decisions module for Floodlight
     */
    @Override
    public String basePath() {
        return "/wm/hand";
    }

}
