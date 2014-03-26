package net.floodlightcontroller.hand;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.storage.IStorageSourceService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;

/**
 * HAND: Host Aware Network Decisions
 * "NetworkAware Network Decisions based on Ganglia metrics and clusters"
 * @author ryan wallner
 * 
 * ***All host that join HAND must be running GMOND*** in order to work.
 **/


public class HAND implements IHANDService, IFloodlightModule {
	
	// services needed
    protected IFloodlightProviderService floodlightProvider;
    protected IStaticFlowEntryPusherService flowPusher;
    protected IStorageSourceService storageSource;
    protected IRestApiService restApi;
    protected static Logger logger;
    protected IDeviceService devices;
    
    protected ArrayList<HANDRule> hostRules;
    protected ArrayList<HANDGangliaHost> gangliaHosts;
    protected ArrayList<String> messages;
    protected PriorityBlockingQueue<HANDRule> ruleQueue; //used to prioritize rules
    
    //Base path for RRDs
    public static String gangliaBasePath;
    
    /**
     * Table for hosts in storage source
     */
    public static final String HOSTS_TABLE_NAME = "hand_hosts";
    public static final String COLUMN_HID = "hostid";
    public static final String COLUMN_CLUSTER = "cluster";
    public static final String COLUMN_NAME = "hostname";
    public static final String COLUMN_IPADD = "ip_address";
    public static final String COLUMN_MACADD = "mac_address";
    public static final String COLUMN_FIRSTHOPS = "first_hop_switches";
    public static final String COLUMN_TIMEADDED = "time_added";
    public static String HostColumnNames[] = {
    	COLUMN_HID,
    	COLUMN_CLUSTER,
    	COLUMN_NAME,
    	COLUMN_IPADD,
    	COLUMN_MACADD,
    	COLUMN_FIRSTHOPS,
    	COLUMN_TIMEADDED
    	};
    
    /**
     * Table for rules in storage source
     */
    public static final String RULES_TABLE_NAME = "hand_rules";
    public static final String COLUMN_RID = "ruleid";
    public static final String COLUMN_RNAME = "rule_name";
    public static final String COLUMN_PRIOR = "priority";
    public static final String COLUMN_POLLTIME = "polling_time";
    public static final String COLUMN_TIMEADD = "time_added";
    public static final String COLUMN_NEXTCHECK = "next_check_time";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_ASSOC = "host_association";
    public static final String COLUMN_METRICS = "metrics";
    public static final String COLUMN_FRULE = "firewall_rule";
    public static final String COLUMN_QOS = "qos_rule";
    public static final String COLUMN_STATICFLOW = "static_flow";
    public static final String COLUMN_CHECKLIMIT = "check_limit";
    public static final String COLUMN_CURRCHECK = "current_check";
    public static final String COLUMN_ACTION = "action";
    public static String RuleColumnNames[] = {
    	COLUMN_RID,
    	COLUMN_RNAME,
    	COLUMN_PRIOR,
    	COLUMN_POLLTIME,
    	COLUMN_TIMEADD,
    	COLUMN_NEXTCHECK,
    	COLUMN_ACTIVE,
    	COLUMN_ASSOC,
    	COLUMN_METRICS,
    	COLUMN_FRULE,
    	COLUMN_QOS,
    	COLUMN_STATICFLOW,
    	COLUMN_CHECKLIMIT,
    	COLUMN_CURRCHECK,
    	COLUMN_ACTION,
    	};
    
    public boolean enabled;
    
    
    @Override
    public String getName(){
    	return "HostAwareNetorkingDecisions";
    }
    
    @Override
    public Boolean isHANDEnabled(){
    	return this.enabled;
    }
    
    @Override
    public void enableHAND(boolean enabled){
    	logger.info("Setting Host Aware Networking Decisions to {}", enabled);
    	this.enabled = enabled;
    	
    	// when enabled, we need to start the timed polling mechanism
    	if (enabled)
    		this.startTimerPollingTask();
    	if (!(enabled)){
    		logger.info("HAND disabled");
    	}
    }
    
    /**
     * @return
     */
    public ArrayList<String> getMessages(){
    	return this.messages;
    }
    
    /**
     * 
     * @return
     */
    public ArrayList<HANDRule> getRules(){
    	return this.hostRules;
    }
    
    /**
     * 
     * @return
     */
    public ArrayList<HANDGangliaHost> getHosts(){
    	return this.gangliaHosts;
    }
    
    /**
     * This will start a scheduled task to run every second.
     * 	-this will run a procedure to check the current time, the
     *  subsequently check the current rules for there @nextCheckTime
     *  and see if they match. If the do, the appropriate action will be
     *  taken to consume the needed metrics for the host's rule and
     *  analyze the rules to see if any actions need be taken.
     *  When the rules is check it will appear in the Messaged queue with
     *  a time stamp.
     *  
     *  //This is inefficient at large scale, but to get things working 
     *  //on a small scale it will be used.
     */
    public void startTimerPollingTask(){
    	long delay = 1000; // delay for 1 sec.
    	long period = 1000; // repeat every sec.
    	final boolean enabled = this.isHANDEnabled();
    	
    	//Create a new timer and add a task to it. Our task is
    	// right inside using the run method of a new TimerTask.
    	final Timer timer = new Timer();
    	timer.scheduleAtFixedRate(new TimerTask(){
    		public void run(){
    			if(!(enabled)){
    				//Cancel 
    				//This is important to check before each second
    				//in case HAND becomes disabled at any point.
    				timer.cancel();
    			}else{
    				//Stamp this with time in seconds since Jan. 1 1970 midnight :)
    				long currentTime = System.currentTimeMillis() / 1000l;
    				logger.info("Checking Rules : "+currentTime);
    				
    				//TODO
    				/**
    				 * Logic here needs carry out the above description 
    				 * 
    				 * 	-Queuing, possibly threading, and concurrency methods need to be thought of here.
    				 * 	-For the above use PriorrityBlockingQueue ir 
    				 *   a PriorityQueue for action on matches
    				 */
    				
    				// Current time should be updated on rules when checked, if 
    				// checking time, is < pollingTime then pass. 
    				//1.call function to check rules bases on priority
    				//2.Rules are sent into priorityqueue bases on .. priority setting
    				//3.Rule is checked against associated host metrics and thresholds in rule
    				//4.If threshold is met, rule.action is carried out.
    				//5. maybe a intoActionFunc(Action action) to carry out rules actions to controller.
    				
    			}
    		}
    		
    	}, delay, period);
    	
    }
    
   
    
    @Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
    	Collection<Class<? extends IFloodlightService>> l = 
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IHANDService.class);
        return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>,
        IFloodlightService> m = 
        new HashMap<Class<? extends IFloodlightService>,
        IFloodlightService>();
        // Implements the QoS service
        m.put(IHANDService.class, this);
        return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		//This module should depend on FloodlightProviderService,
		// IStorageSourceProviderService, IRestApiService, &
		// IStaticFlowEntryPusherService
		Collection<Class<? extends IFloodlightService>> l =
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IStorageSourceService.class);
        l.add(IRestApiService.class);
        l.add(IStaticFlowEntryPusherService.class);
        l.add(IDeviceService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context
                .getServiceImpl(IFloodlightProviderService.class);
        storageSource = context.getServiceImpl(IStorageSourceService.class);
        restApi = context.getServiceImpl(IRestApiService.class);
        devices = context.getServiceImpl(IDeviceService.class);
        hostRules = new ArrayList<HANDRule>();
        gangliaHosts = new ArrayList<HANDGangliaHost>();
        messages = new ArrayList<String>();
        logger = LoggerFactory.getLogger(HAND.class);

        // start disabled
        enabled = false; //true for testing 
        
        //fetch config
        getGangliaRRDLocation();
        logger.info("RRD Location: {}", HAND.gangliaBasePath);
	}


	@Override
	public void startUp(FloodlightModuleContext context) {
		
		//initialize
		 // register REST interface
        restApi.addRestletRoutable(new HANDWebRoutable());

        // storage for hosts
        storageSource.createTable(HOSTS_TABLE_NAME, null);
        storageSource.setTablePrimaryKeyName(HOSTS_TABLE_NAME, COLUMN_HID);
     	synchronized (gangliaHosts) {
            this.gangliaHosts = readHostsFromStorage();
        }
        
        //storage for rules
        storageSource.createTable(RULES_TABLE_NAME, null);
        storageSource.setTablePrimaryKeyName(RULES_TABLE_NAME, COLUMN_RID);
        synchronized (hostRules) {
            this.hostRules = readRulesFromStorage();
        }
        
		
		// One of the things we need to do is start our timing
		// mechanism when the module is started.
		if(this.isHANDEnabled()){
			this.startTimerPollingTask();
		}
		else{
			logger.info("Disabled, waiting to enable to start polling hosts.");
		}

	}
	
	
	/**
	 * Adds a host to the controller in an ordered manner,
	 * hosts are ordered High --> Low by time they were added.
	 * @param host
	 */
	public void addGangliaHost(HANDGangliaHost host){
		if(hostSeenByFloodlight(host)){
			logger.debug("All checks complete, adding Ganglia Host to Controller");
			
			//Generate unique identifier.
			host.hostId = host.genUniqueId();
			
			int pos = 0;
			int h = 0;
			for(h = 0; h < this.gangliaHosts.size(); h ++){
				if(this.gangliaHosts.isEmpty()){
					break; //just add it.
				}
				//Add High to Low. (newer hosts are checked first)
				if(this.gangliaHosts.get(h).timeAdded <= host.timeAdded){
					pos = h;
					break; //want to add here. H -> L
					
				}
			}
			if(pos <= this.gangliaHosts.size()){
				this.gangliaHosts.add(pos, host);
			}
			else{
				this.gangliaHosts.add(host);
			}
			
			/**
			 * Add the host to the storageSource.
			 * COLUMN_HID,
    		 * COLUMN_CLUSTER,
    		 * COLUMN_NAME,
    		 * COLUMN_IPADD,
    		 * COLUMN_MACADD,
    		 * COLUMN_FIRSTHOPS,
    		 * COLUMN_TIMEADDED
			 */
			Map<String, Object> hostEntry = new HashMap<String, Object>();
			hostEntry.put(COLUMN_HID, Long.toString(host.hostId));
			hostEntry.put(COLUMN_CLUSTER, host.cluster);
			hostEntry.put(COLUMN_NAME, host.hostName);
			hostEntry.put(COLUMN_IPADD, Integer.toString(host.ipAddress));
			hostEntry.put(COLUMN_MACADD, Long.toString(host.macAddress));
			hostEntry.put(COLUMN_FIRSTHOPS, host.firstHops.toString());
			hostEntry.put(COLUMN_TIMEADDED, Long.toString(host.timeAdded));
			storageSource.insertRow(HOSTS_TABLE_NAME, hostEntry);
		
		}
		else{
			logger.error("Ganglia host not added. Reason:"+
						 "HAND Could not detect that Floodlight has seen this host before,"+
						 "Please check to make sure host in visible to the controller.");
		}
		
		
	}
	
	 //TODO
    /**
     * Add host (DONE!) 5/1/2013
     * 
     * Remove host //TODO ****************************
     */
	
	//TODO
    /**
     * Add/Remove rules
     * 
     * Rules deal with all other modules in Floodlight.
     * LB 			- Load Balancer 
     * AFR / DFR 	- Add/Remove Firewall Rule
     * PSF			- Push Static Flow
     * AQOS / DQOS  - Add/Remove QoS
     * 
     * ***Rules must be check every time data is polled for a specific host***
     *
     */
	
	/**
	 * Adds a Host Aware Rule to the controller
	 * @param rule
	 */
	public void addHostAwareRule(HANDRule rule){
		
		//TODO
		
	}
    
    //TODO
    /**
     * Needs  a way to carry out the rules and a way to
     * time synchronize HANDRule.getPollingTime() with the system clock
     * Hosts need to be polled every ^^time^^ that is set.
     * 
     * If the metrics add up the carry out the rule.
     */
    
    //TODO
    /**
     * need storage source for *Metrics last polled* if a rule states
     * "If the last polled metrics is above X amount 5 times in a row, carry out rule Y"
     * The last 4 polls need to be stored.
     */
	
	
	
	/**
	 * This method takes in a user defined host and
	 * makes sure that Floodlight has seen the host. If
	 * Floodlight cannot see the host, the host information
	 * is useless to the SDN network and the controller will
	 * not be able to control the flows to / from the host.
	 * 
	 * The method also adds vital information about the host
	 * if Floodlight has seen it in its networks before.
	 * 
	 * @param host
	 * @return boolean
	 */
	
	public boolean hostSeenByFloodlight(HANDGangliaHost host){
		boolean isSeen = false;
		Iterator<? extends IDevice> hosts;
		if(host.macAddress != 0){
			hosts = devices.queryDevices(host.macAddress, null, host.ipAddress,null, null);
		}
		else{
			hosts = devices.queryDevices(null, null, host.ipAddress, null, null);
		}
		
		while(hosts.hasNext()){
			IDevice device = hosts.next();
			Integer[] deviceAddresses = device.getIPv4Addresses();
			for(Integer address : deviceAddresses){
				if(address == host.ipAddress){
					isSeen = true;
					logger.info("******Host confirmed by Floodlight******");
					/**
					 * some of the host information is optional,
					 * if the user doesn't enter it, Floodlight
					 * knows about it.
					 */
					if(host.firstHops.isEmpty()){
						ArrayList<Long> switches = new ArrayList<Long>();
						for(SwitchPort p : device.getAttachmentPoints()){
							switches.add(p.getSwitchDPID());
							logger.info("***Adding first hop***");
						}
						host.firstHops = switches;
					}
					if(host.macAddress == 0){
						host.macAddress = device.getMACAddress();
					}
					break;
				}else{
					logger.error("******Host ["+host.ipAddress+"/"+host.hostName+"] Not seen by Floodlight");
					isSeen = false;
					}
				}
			}
		//return whether Floodlight has seen the host.
		return isSeen;
	}
    

	/**
	 * Reads the policies from the storage and creates an
     * ArrayList of GangliaHost's from them.
	 * @return
	 */
	public ArrayList<HANDGangliaHost> readHostsFromStorage(){
		ArrayList<HANDGangliaHost> list = new ArrayList<HANDGangliaHost>();
		
		
		//TODO read gangliaHosts from storageSource;
		
		return list;
	}
	
	/**
	 * Reads the policies from the storage and creates a sorted 
     * ArrayList of Rule's from them.
	 * @return
	 */
	public ArrayList<HANDRule> readRulesFromStorage(){
		ArrayList<HANDRule> list = new ArrayList<HANDRule>();
		
		
		//TODO read rules from storageSource, sorted based on priority  of rule.
		
		return list;
	}
	
	/**
	 * Only needed by init.
	 * Gets the rrd UNIX file locations for clusters and RRD's
	 */
	private void getGangliaRRDLocation() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream("src/main/resources/floodlightdefault.properties"));
			gangliaBasePath = props.getProperty("net.floodlightcontroller.hand.GangliaBasePath");
			//System.out.println("GANGLIA BASE PATH AS: "+gangliaBasePath); //debug
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
