package org.greatfree.server.container;

import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.greatfree.client.CSClient;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.container.p2p.message.RegisterPeerRequest;
import org.greatfree.framework.container.p2p.message.UnregisterPeerRequest;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;
import org.greatfree.util.Tools;

// Created: 01/13/2019, Bing Li
class Register<Dispatcher extends ServerDispatcher<ServerMessage>>
{
	private final static Logger log = Logger.getLogger("org.greatfree.server.container");

	//	private final String peerID;
	private String peerID;
	// The name that makes sense to humans for the peer. 05/01/2017, Bing Li
//	private final String peerName;
	private String peerName;
	// The system registry server IP. 05/01/2017, Bing Li
	private final String registryServerIP;
	// The system registry server port. 05/01/2017, Bing Li
	private final int registryServerPort;
	private final boolean isRegistryNeeded;
	// The peer IP. 05/01/2017, Bing Li
	private final String peerIP;
	// The local IP key. The key is used to avoid the local node sends messages to itself. 05/19/2017, Bing Li
	private String localIPKey;
	private AtomicBoolean isRegistered;

	public Register(String peerName, String registryServerIP, int registryServerPort, boolean isRegistryNeeded) throws SocketException
	{
		this.peerName = peerName;
		this.peerID = Tools.getHash(this.peerName);
		this.peerIP = Tools.getLocalIP();
		this.registryServerIP = registryServerIP;
		this.registryServerPort = registryServerPort;
		this.isRegistryNeeded = isRegistryNeeded;
		this.isRegistered = new AtomicBoolean(false);
	}
	
	public String getPeerName()
	{
		return this.peerName;
	}
	
	public synchronized void setPeerName(String pn)
	{
		this.peerName = pn;
		this.peerID = Tools.getHash(pn);
	}
	
	public String getPeerID()
	{
		return this.peerID;
	}
	
	public String getPeerIP()
	{
		return this.peerIP;
	}
	
	public String getLocalIPKey()
	{
		return this.localIPKey;
	}
	
	public String getRegistryIP()
	{
		return this.registryServerIP;
	}
	
	public int getRegistryPort()
	{
		return this.registryServerPort;
	}
	
	public boolean isRegistered()
	{
		return this.isRegistered.get();
	}
	
	public void unregister(CSClient client) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		if (this.isRegistryNeeded)
		{
			try
			{
				client.read(this.registryServerIP, this.registryServerPort, new UnregisterPeerRequest(this.peerID));
			}
			catch (RemoteIPNotExistedException e)
			{
				throw new RemoteIPNotExistedException(this.registryServerIP, this.registryServerPort);
			}
		}
	}
	
	public void register(CSClient client, Peer<Dispatcher> peer) throws ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		// To avoid the possibility of port conflict, which happens when multiple peers run on the same machine, it is necessary to obtain an idle port from the registry server. 05/01/2017, Bing Li
		if (this.isRegistryNeeded)
		{
			try
			{
				RegisterPeerResponse response;
//				if (!peer.isServerDisabled() && !peer.isClientDisabled())
				if (!peer.isServerDisabled())
				{
					if (!peer.isBroker())
					{
						response = (RegisterPeerResponse)client.read(this.registryServerIP, this.registryServerPort, new RegisterPeerRequest(this.peerID, this.peerName, this.peerIP, peer.getPort()));
					}
					else
					{
						log.info("The cluster plays the role as a broker ...");
						response = (RegisterPeerResponse)client.read(this.registryServerIP, this.registryServerPort, new RegisterPeerRequest(this.peerID, this.peerName, peer.isBroker(), this.peerIP, peer.getPort()));
					}
				}
				else 
				{
//					response = (RegisterPeerResponse)client.read(this.registryServerIP, this.registryServerPort, new RegisterPeerRequest(this.peerID, this.peerName, this.peerIP, peer.getPort(), peer.isServerDisabled(), peer.isClientDisabled()));
					response = (RegisterPeerResponse)client.read(this.registryServerIP, this.registryServerPort, new RegisterPeerRequest(this.peerID, this.peerName, this.peerIP, peer.getPort(), peer.isServerDisabled()));
				}

				if (!peer.isServerDisabled())
				{
					if (!response.isDuplicate())
					{
						if (peer.getPort() != response.getPeerPort())
						{
							// Initialize the peer port, which is obtained from the registry server. 05/01/2017, Bing Li
							// Set the port of the parent class, CSServer, to get ready for startup. 05/01/2017, Bing Li
							peer.setPort(response.getPeerPort());
						}
						else
						{
							/*
							 * Since the ServerSocket is initialized, it is not reasonable to initialize it again with the same port. 06/18/2022, Bing Li
							 */
							peer.setPort();
						}
//						System.out.println("\n===================");
						log.info("Peer IP Address: " + peer.getPeerIP() + ":" + peer.getPort());
//						System.out.println("===================\n");
						this.isRegistered.set(true);
					}
					else
					{
						this.isRegistered.set(false);
						throw new DuplicatePeerNameException(this.peerName);
					}
				}
				else
				{
//					System.out.println("\n===================");
					log.info("Peer IP Address: null - Server is disabled!");
//					System.out.println("===================\n");
					this.isRegistered.set(true);
				}
			}
			catch (RemoteIPNotExistedException e)
			{
				throw new RemoteIPNotExistedException(this.registryServerIP, this.registryServerPort);
			}
		}
		else
		{
			/*
			 * Since the ServerSocket is initialized, it is not reasonable to initialize it again with the same port. 06/18/2022, Bing Li
			 */
			peer.setPort();
		}
			
		this.localIPKey = Tools.getKeyOfFreeClient(this.peerIP, peer.getPort());
	}
}
