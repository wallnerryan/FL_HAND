package net.floodlightcontroller.hand;

/**
 * HAND: Host Aware Network Decisions
 * "network decisions based on Ganglia metrics and clusters"
 * @author ryan wallner
 **/

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openflow.protocol.OFFlowMod;
import org.slf4j.Logger;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.storage.IStorageSourceService;
import net.floodlightcontroller.topology.ITopologyListener;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.topology.NodePortTuple;

public class HAND implements IFloodlightModule, IStaticFlowEntryPusherService,
		ITopologyService {
	
	// services needed
    protected IFloodlightProviderService floodlightProvider;
    protected IStorageSourceService storageSource;
    protected IRestApiService restApi;
    protected static Logger logger;
    
    protected List<HANDRule> hostRules;
    protected List<HANDGangliaHost> gangliaHosts;

	@Override
	public void addListener(ITopologyListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Date getLastUpdateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAttachmentPointPort(long switchid, short port) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAttachmentPointPort(long switchid, short port,
			boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getOpenflowDomainId(long switchId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getOpenflowDomainId(long switchId, boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getL2DomainId(long switchId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getL2DomainId(long switchId, boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean inSameOpenflowDomain(long switch1, long switch2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inSameOpenflowDomain(long switch1, long switch2,
			boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Long> getSwitchesInOpenflowDomain(long switchDPID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getSwitchesInOpenflowDomain(long switchDPID,
			boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean inSameL2Domain(long switch1, long switch2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inSameL2Domain(long switch1, long switch2,
			boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBroadcastDomainPort(long sw, short port) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBroadcastDomainPort(long sw, short port,
			boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAllowed(long sw, short portId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAllowed(long sw, short portId, boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConsistent(long oldSw, short oldPort, long newSw,
			short newPort) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConsistent(long oldSw, short oldPort, long newSw,
			short newPort, boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInSameBroadcastDomain(long s1, short p1, long s2, short p2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInSameBroadcastDomain(long s1, short p1, long s2,
			short p2, boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Short> getPortsWithLinks(long sw) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Short> getPortsWithLinks(long sw, boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Short> getBroadcastPorts(long targetSw, long src, short srcPort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Short> getBroadcastPorts(long targetSw, long src, short srcPort,
			boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isIncomingBroadcastAllowed(long sw, short portId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIncomingBroadcastAllowed(long sw, short portId,
			boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NodePortTuple getOutgoingSwitchPort(long src, short srcPort,
			long dst, short dstPort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodePortTuple getOutgoingSwitchPort(long src, short srcPort,
			long dst, short dstPort, boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodePortTuple getIncomingSwitchPort(long src, short srcPort,
			long dst, short dstPort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodePortTuple getIncomingSwitchPort(long src, short srcPort,
			long dst, short dstPort, boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodePortTuple getAllowedOutgoingBroadcastPort(long src,
			short srcPort, long dst, short dstPort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodePortTuple getAllowedOutgoingBroadcastPort(long src,
			short srcPort, long dst, short dstPort, boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodePortTuple getAllowedIncomingBroadcastPort(long src, short srcPort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodePortTuple getAllowedIncomingBroadcastPort(long src,
			short srcPort, boolean tunnelEnabled) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<NodePortTuple> getBroadcastDomainPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<NodePortTuple> getTunnelPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<NodePortTuple> getBlockedPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LDUpdate> getLastLinkUpdates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Short> getPorts(long sw) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addFlow(String name, OFFlowMod fm, String swDpid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteFlow(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteFlowsForSwitch(long dpid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllFlows() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Map<String, OFFlowMod>> getFlows() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, OFFlowMod> getFlows(String dpid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		// TODO Auto-generated method stub

	}

}
