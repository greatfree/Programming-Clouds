package org.greatfree.framework.container.p2p.registry;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.SearchBrokerRequest;
import org.greatfree.framework.container.p2p.message.ChatPartnerRequest;
import org.greatfree.framework.container.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.container.p2p.message.ClusterIPRequest;
import org.greatfree.framework.container.p2p.message.IsRootOnlineRequest;
import org.greatfree.framework.container.p2p.message.LeaveClusterNotification;
import org.greatfree.framework.container.p2p.message.MulticastChildrenRequest;
import org.greatfree.framework.container.p2p.message.PartnersRequest;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.framework.container.p2p.message.PeerDisableStateRequest;
import org.greatfree.framework.container.p2p.message.PortRequest;
import org.greatfree.framework.container.p2p.message.RegisterPeerRequest;
import org.greatfree.framework.container.p2p.message.UnregisterPeerRequest;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

// Created: 01/12/2019, Bing Li
final public class RegistryTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.container.p2p.registry");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case SystemMessageType.LEAVE_CLUSTER_NOTIFICATION:
				log.info("LEAVE_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				Register.leaveCluster((LeaveClusterNotification)notification);
				break;
		
			case SystemMessageType.SHUTDOWN_SERVER_NOTIFICATION:
				log.info("SHUTDOWN_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					RegisterServer.CS().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
//		System.out.println("request.getApplicationID() = " + request.getApplicationID());
		switch (request.getApplicationID())
		{
			case SystemMessageType.PEER_CHAT_REGISTRY_REQUEST:
				log.info("PEER_CHAT_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.registerChat((ChatRegistryRequest)request);
				
			case SystemMessageType.PEER_CHAT_PARTNER_REQUEST:
				log.info("PEER_CHAT_PARTNER_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.checkChatPartner((ChatPartnerRequest)request);
				
			case SystemMessageType.PARTNERS_REQUEST:
				log.info("PARTNERS_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.checkPartners((PartnersRequest)request);
				
			case SystemMessageType.REGISTER_PEER_REQUEST:
				log.info("REGISTER_PEER_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.registerPeer((RegisterPeerRequest)request);
				
			case SystemMessageType.PORT_REQUEST:
				log.info("PORT_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.retrievePort((PortRequest)request);
				
			case SystemMessageType.CLUSTER_IP_REQUEST:
				log.info("CLUSTER_IP_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.retrieveClusterIP((ClusterIPRequest)request);
				
			case SystemMessageType.MULTICAST_CHILDREN_REQUEST:
				log.info("MULTICAST_CHILDREN_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.retrieveMulticastChildrenIPs((MulticastChildrenRequest)request);
				
			case SystemMessageType.PEER_ADDRESS_REQUEST:
				log.info("PEER_ADDRESS_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.retrievePeerAddress((PeerAddressRequest)request);
				
			case SystemMessageType.PEER_DISABLE_STATE_REQUEST:
				log.info("PEER_DISABLE_STATE_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.retrievePeerDisableState((PeerDisableStateRequest)request);
				
			case SystemMessageType.SEARCH_BROKER_REQUEST:
				log.info("SEARCH_BROKER_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.retrieveBroker((SearchBrokerRequest)request);
				
			case SystemMessageType.ALL_REGISTERED_IPS_REQUEST:
				log.info("ALL_REGISTERED_IPS_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.retrieveAllRegisteredIPs();
				
			case SystemMessageType.ALL_PEER_NAMES_REQUEST:
				log.info("ALL_PEER_NAMES_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.retrieveAllPeerNames();
				
			case SystemMessageType.IS_ROOT_ONLINE_REQUEST:
				log.info("IS_ROOT_ONLINE_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.isRootOnline((IsRootOnlineRequest)request);
				
			case SystemMessageType.UNREGISTER_PEER_REQUEST:
				log.info("UNREGISTER_PEER_REQUEST received @" + Calendar.getInstance().getTime());
				return Register.unregisterPeer((UnregisterPeerRequest)request);
		}
		return null;
	}
}
