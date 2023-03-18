package org.greatfree.framework.container.p2p.registry;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.greatfree.chat.ChatConfig;
import org.greatfree.cluster.message.IsRootOnlineResponse;
import org.greatfree.framework.container.p2p.message.AllPeerNamesResponse;
import org.greatfree.framework.container.p2p.message.AllRegisteredIPsResponse;
import org.greatfree.framework.container.p2p.message.SearchBrokerRequest;
import org.greatfree.framework.container.p2p.message.SearchBrokerResponse;
import org.greatfree.framework.container.p2p.message.ChatPartnerRequest;
import org.greatfree.framework.container.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.container.p2p.message.ClusterIPRequest;
import org.greatfree.framework.container.p2p.message.IsRootOnlineRequest;
import org.greatfree.framework.container.p2p.message.LeaveClusterNotification;
import org.greatfree.framework.container.p2p.message.MulticastChildrenRequest;
import org.greatfree.framework.container.p2p.message.MulticastChildrenResponse;
import org.greatfree.framework.container.p2p.message.PartnersRequest;
import org.greatfree.framework.container.p2p.message.PartnersResponse;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.framework.container.p2p.message.PeerDisableStateRequest;
import org.greatfree.framework.container.p2p.message.PeerDisableStateResponse;
import org.greatfree.framework.container.p2p.message.PortRequest;
import org.greatfree.framework.container.p2p.message.RegisterPeerRequest;
import org.greatfree.framework.container.p2p.message.UnregisterPeerRequest;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.p2p.message.ChatPartnerResponse;
import org.greatfree.framework.p2p.message.ChatRegistryResponse;
import org.greatfree.framework.p2p.registry.AccountRegistry;
import org.greatfree.framework.p2p.registry.PeerChatAccount;
import org.greatfree.framework.p2p.registry.PeerRegistry;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.PortResponse;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.UnregisterPeerResponse;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.server.PeerAccount;
import org.greatfree.util.IPAddress;
import org.greatfree.util.IPPort;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

// Created: 01/12/2019, Bing Li
class Register
{
//	private final static Logger log = Logger.getLogger("org.greatfree.framework.container.p2p.registry");

	public static void register() throws SocketException
	{
		// Register the ports to avoid potential conflicts. 05/02/2017, Bing Li
		String localIP = Tools.getLocalIP();
		// Register the main port. 05/02/2017, Bing Li
//		PeerRegistry.SYSTEM().register(ChatConfig.CHAT_REGISTRY_SERVER_KEY, ChatConfig.CHAT_REGISTRY_NAME, localIP, RegistryConfig.PEER_REGISTRY_PORT, false, false);
		PeerRegistry.SYSTEM().register(ChatConfig.CHAT_REGISTRY_SERVER_KEY, ChatConfig.CHAT_REGISTRY_NAME, localIP, RegistryConfig.PEER_REGISTRY_PORT, false, false);
		// Register the chat registry port. 05/02/2017, Bing Li
		PeerRegistry.SYSTEM().registeOthers(ChatConfig.CHAT_REGISTRY_SERVER_KEY, ChatConfig.CHAT_REGISTRY_PORT_KEY, localIP, ChatConfig.CHAT_REGISTRY_PORT);
		// Register the registry administration port. 05/02/2017, Bing Li
		PeerRegistry.SYSTEM().registeOthers(ChatConfig.CHAT_REGISTRY_SERVER_KEY, RegistryConfig.REGISTRY_ADMIN_PORT_KEY, localIP, ChatConfig.CHAT_ADMIN_PORT);
	}
	
	public static void unregister()
	{
		// Dispose the system-level registry. 05/01/2017, Bing Li
		PeerRegistry.SYSTEM().dispose();
		// Dispose the account registry. 04/30/2017, Bing Li
		AccountRegistry.APP().dispose();
	}
	
	public static ChatRegistryResponse registerChat(ChatRegistryRequest req)
	{
		AccountRegistry.APP().add(new PeerChatAccount(req.getPeerID(), req.getPeerName(), req.getPeerDescription(), req.getPreference()));
		return new ChatRegistryResponse(true);
	}
	
	public static ChatPartnerResponse checkChatPartner(ChatPartnerRequest req)
	{
		ChatPartnerResponse response;
		if (PeerRegistry.SYSTEM().isExisted(req.getPartnerKey()))
		{
			PeerAccount account = PeerRegistry.SYSTEM().get(req.getPartnerKey());
			if (AccountRegistry.APP().isAccountExisted(req.getPartnerKey()))
			{
				PeerChatAccount chatAccount = AccountRegistry.APP().getAccount(req.getPartnerKey());
				response = new ChatPartnerResponse(account.getPeerKey(), account.getPeerName(), chatAccount.getDescription(), chatAccount.getPreference(), account.getIP(), account.getPeerPort());
			}
			else
			{
				response = new ChatPartnerResponse(account.getPeerKey(), account.getPeerName(), UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, account.getIP(), account.getPeerPort());
			}
		}
		else
		{
			response = new ChatPartnerResponse(UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.ZERO);
		}
		return response;
	}
	
	public static PartnersResponse checkPartners(PartnersRequest req)
	{
		PeerAccount account;
		Map<String, IPPort> ips = new HashMap<String, IPPort>();
		for (String entry : req.getPartnerKeys())
		{
			account = PeerRegistry.SYSTEM().get(entry);
			ips.put(entry, new IPPort(account.getIP(), account.getPeerPort()));
		}
		return new PartnersResponse(ips);
	}

	public synchronized static RegisterPeerResponse registerPeer(RegisterPeerRequest req)
	{
//		log.info("account name = " + req.getPeerName() + " is registering ...");
//		System.out.println("Register-registerPeer(): peerKey = " + req.getPeerKey());
		/*
		 * The line is added to simplify the registry of multicasting children. 10/14/2022, Bing Li
		 */
		if (AccountRegistry.APP().isAccountExisted(req.getPeerKey()))
		{
//			log.info("account name = " + req.getPeerName() + " is failed to register ...");
			return new RegisterPeerResponse(true);
		}
//		log.info("account name = " + req.getPeerName() + " is succeeded to register ...");
		AccountRegistry.APP().add(new PeerChatAccount(req.getPeerKey(), req.getPeerName()));
//		return new RegisterPeerResponse(PeerRegistry.SYSTEM().register(req.getPeerKey(), req.getPeerName(), req.getIP(), req.getPort(), req.isServerDisabled(), req.isClientDisabled()));
		return new RegisterPeerResponse(PeerRegistry.SYSTEM().register(req.getPeerKey(), req.getPeerName(), req.getIP(), req.getPort(), req.isServerDisabled(), req.isBroker()));
	}
	
	public static PortResponse retrievePort(PortRequest req)
	{
		return new PortResponse(PeerRegistry.SYSTEM().getIdlePort(req.getPeerKey(), req.getPortKey(), req.getIP(), req.getCurrentPort()));
	}
	
	public static ClusterIPResponse retrieveClusterIP(ClusterIPRequest req)
	{
//		log.info("rootKey = " + req.getRootKey());
		Set<String> childrenKeys = Clusters.SYSTEM().getChildKeys(req.getRootKey());
		if (childrenKeys != UtilConfig.NO_KEYS)
		{
//			log.info("childrenKeys' size = " + childrenKeys.size());
			return new ClusterIPResponse(AccountRegistry.APP().getIPPorts(PeerRegistry.SYSTEM().getIPPorts(childrenKeys)));
		}
		else
		{
//			log.info("no children found!");
			return new ClusterIPResponse();
		}
	}

	public static MulticastChildrenResponse retrieveMulticastChildrenIPs(MulticastChildrenRequest req)
	{
//		log.info("rootKey = " + req.getRootKey());
		return new MulticastChildrenResponse(AccountRegistry.APP().getIPPorts(PeerRegistry.SYSTEM().getIPPorts()));
	}

	public static PeerAddressResponse retrievePeerAddress(PeerAddressRequest req)
	{
		return new PeerAddressResponse(PeerRegistry.SYSTEM().getAddress(req.getPeerID()));
	}
	
	public static PeerDisableStateResponse retrievePeerDisableState(PeerDisableStateRequest req)
	{
//		return new PeerDisableStateResponse(PeerRegistry.SYSTEM().isServerDisabled(req.getPeerID()), PeerRegistry.SYSTEM().isClientDisabled(req.getPeerID()));
		return new PeerDisableStateResponse(PeerRegistry.SYSTEM().isServerDisabled(req.getPeerID()));
	}
	
	public static SearchBrokerResponse retrieveBroker(SearchBrokerRequest req)
	{
		return new SearchBrokerResponse(PeerRegistry.SYSTEM().getBroker());
	}
	
	public static AllRegisteredIPsResponse retrieveAllRegisteredIPs()
	{
		return new AllRegisteredIPsResponse(PeerRegistry.SYSTEM().getIPPorts());
	}
	
	public static AllPeerNamesResponse retrieveAllPeerNames()
	{
		return new AllPeerNamesResponse(PeerRegistry.SYSTEM().getNames());
	}

	public static IsRootOnlineResponse isRootOnline(IsRootOnlineRequest req)
	{
//		log.info("rootID = " + req.getRootID());
		Clusters.SYSTEM().joinCluster(req.getRootID(), req.getChildKey());
		IPAddress rootIP = PeerRegistry.SYSTEM().getAddress(req.getRootID());
		if (rootIP != UtilConfig.NO_IP_ADDRESS)
		{
			return new IsRootOnlineResponse(rootIP, true);
		}
		else
		{
			return new IsRootOnlineResponse(rootIP, false);
		}
	}
	
	public static void leaveCluster(LeaveClusterNotification notification)
	{
		Clusters.SYSTEM().leaveCluster(notification.getRootKey(), notification.getChildKey());
	}

	public synchronized static UnregisterPeerResponse unregisterPeer(UnregisterPeerRequest req)
	{
		AccountRegistry.APP().remove(req.getPeerKey());
		PeerRegistry.SYSTEM().unregister(req.getPeerKey());
		return new UnregisterPeerResponse(true);
	}
}
