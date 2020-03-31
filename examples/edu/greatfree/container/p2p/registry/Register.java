package edu.greatfree.container.p2p.registry;

import java.net.SocketException;
import java.util.Set;

import org.greatfree.cluster.message.IsRootOnlineResponse;
import org.greatfree.dip.container.p2p.message.ClusterIPRequest;
import org.greatfree.dip.container.p2p.message.IsRootOnlineRequest;
import org.greatfree.dip.container.p2p.message.LeaveClusterNotification;
import org.greatfree.dip.container.p2p.message.PeerAddressRequest;
import org.greatfree.dip.container.p2p.message.PortRequest;
import org.greatfree.dip.container.p2p.message.RegisterPeerRequest;
import org.greatfree.dip.container.p2p.message.UnregisterPeerRequest;
import org.greatfree.dip.multicast.message.PeerAddressResponse;
import org.greatfree.dip.p2p.registry.AccountRegistry;
import org.greatfree.dip.p2p.registry.PeerChatAccount;
import org.greatfree.message.PortResponse;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.UnregisterPeerResponse;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.server.PeerAccount;
import org.greatfree.server.PeerRegistry;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

import edu.greatfree.container.p2p.message.ChatPartnerRequest;
import edu.greatfree.container.p2p.message.ChatRegistryRequest;
import edu.greatfree.cs.multinode.ChatConfig;
import edu.greatfree.p2p.RegistryConfig;
import edu.greatfree.p2p.message.ChatPartnerResponse;
import edu.greatfree.p2p.message.ChatRegistryResponse;

// Created: 01/12/2019, Bing Li
class Register
{
	public static void register() throws SocketException
	{
		// Register the ports to avoid potential conflicts. 05/02/2017, Bing Li
		String localIP = Tools.getLocalIP();
		// Register the main port. 05/02/2017, Bing Li
		PeerRegistry.SYSTEM().register(ChatConfig.CHAT_REGISTRY_SERVER_KEY, ChatConfig.CHAT_REGISTRY_NAME, localIP, RegistryConfig.PEER_REGISTRY_PORT);
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
		AccountRegistry.APPLICATION().dispose();
	}
	
	public static ChatRegistryResponse registerChat(ChatRegistryRequest req)
	{
		AccountRegistry.APPLICATION().add(new PeerChatAccount(req.getPeerID(), req.getPeerName(), req.getPeerDescription(), req.getPreference()));
		return new ChatRegistryResponse(true);
	}
	
	public static ChatPartnerResponse checkChatPartner(ChatPartnerRequest req)
	{
		ChatPartnerResponse response;
		if (PeerRegistry.SYSTEM().isExisted(req.getPartnerKey()))
		{
			PeerAccount account = PeerRegistry.SYSTEM().get(req.getPartnerKey());
			if (AccountRegistry.APPLICATION().isAccountExisted(req.getPartnerKey()))
			{
				PeerChatAccount chatAccount = AccountRegistry.APPLICATION().getAccount(req.getPartnerKey());
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

	public static RegisterPeerResponse registerPeer(RegisterPeerRequest req)
	{
//		System.out.println("Register-registerPeer(): peerKey = " + req.getPeerKey());
		return new RegisterPeerResponse(PeerRegistry.SYSTEM().register(req.getPeerKey(), req.getPeerName(), req.getIP(), req.getPort()));
	}
	
	public static PortResponse retrievePort(PortRequest req)
	{
		return new PortResponse(PeerRegistry.SYSTEM().getIdlePort(req.getPeerKey(), req.getPortKey(), req.getIP(), req.getCurrentPort()));
	}
	
	public static ClusterIPResponse retrieveClusterIP(ClusterIPRequest req)
	{
		Set<String> childrenKeys = Clusters.SYSTEM().getChildKeys(req.getRootKey());
		if (childrenKeys != UtilConfig.NO_KEYS)
		{
			return new ClusterIPResponse(AccountRegistry.APPLICATION().getIPPorts(PeerRegistry.SYSTEM().getIPPorts(childrenKeys)));
		}
		else
		{
			return new ClusterIPResponse();
		}
	}
	
	public static PeerAddressResponse retrievePeerAddress(PeerAddressRequest req)
	{
		return new PeerAddressResponse(PeerRegistry.SYSTEM().getAddress(req.getPeerID()));
	}

	public static IsRootOnlineResponse isRootOnline(IsRootOnlineRequest req)
	{
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

	public static UnregisterPeerResponse unregisterPeer(UnregisterPeerRequest req)
	{
		PeerRegistry.SYSTEM().unregister(req.getPeerKey());
		return new UnregisterPeerResponse(true);
	}
}
