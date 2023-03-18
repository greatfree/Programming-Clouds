package org.greatfree.cluster;

import java.io.IOException;

import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.PeerContainerSingleton;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.IPAddress;

/**
 * 
 * 10/31/2021, Bing Li
 * 
 * @author libing
 *
 */
public class StandaloneClusterPeer
{
	private IPAddress rootAddress;
	private String registryIP;
	private int registryPort;

	private StandaloneClusterPeer()
	{
	}

	private static StandaloneClusterPeer instance = new StandaloneClusterPeer();
	
	public static StandaloneClusterPeer CONTAINER()
	{
		if (instance == null)
		{
			instance = new StandaloneClusterPeer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, RemoteIPNotExistedException, IOException
	{
		PeerContainerSingleton.P2P().stop(timeout);
	}
	
	public void start(String peerName, String registryIP, int registryPort, ServerTask task, String rootKey) throws ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, IOException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		PeerContainerSingleton.P2P().start(peerName, registryPort, registryIP, registryPort, task, true);
		this.registryIP = registryIP;
		this.registryPort = registryPort;
		PeerAddressResponse response = (PeerAddressResponse)PeerContainerSingleton.P2P().read(registryIP,  registryPort, new PeerAddressRequest(rootKey));
		this.rootAddress = response.getPeerAddress();
	}
	
	public IPAddress getRootAddress()
	{
		return this.rootAddress;
	}
	
	public IPAddress getIPAddress(String partnerName) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return ((PeerAddressResponse)PeerContainerSingleton.P2P().read(this.registryIP, this.registryPort, new PeerAddressRequest(PeerContainer.getPeerKey(partnerName)))).getPeerAddress();
	}

	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		PeerContainerSingleton.P2P().syncNotify(ip, port, notification);
	}
	
	public void asyncNotify(String ip, int port, ServerMessage notification)
	{
		PeerContainerSingleton.P2P().asyncNotify(ip, port, notification);
	}
	
	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return PeerContainerSingleton.P2P().read(ip, port, request);
	}

	public void syncNotifyRoot(ServerMessage notification) throws IOException, InterruptedException
	{
		PeerContainerSingleton.P2P().syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public void asyncNotifyRoot(ServerMessage notification)
	{
		PeerContainerSingleton.P2P().asyncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public ServerMessage readRoot(ServerMessage request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return PeerContainerSingleton.P2P().read(this.rootAddress.getIP(), this.rootAddress.getPort(), request);
	}

	public void syncNotifyRegistry(ServerMessage notification) throws IOException, InterruptedException
	{
		PeerContainerSingleton.P2P().syncNotify(this.registryIP, this.registryPort, notification);
	}
	
	public void asyncNotifyRegistry(ServerMessage notification)
	{
		PeerContainerSingleton.P2P().asyncNotify(this.registryIP, this.registryPort, notification);
	}
	
	public ServerMessage readRegistry(ServerMessage request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return PeerContainerSingleton.P2P().read(this.registryIP, this.registryPort, request);
	}
}
