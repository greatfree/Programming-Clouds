package org.greatfree.server.container;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.ServerMessage;

// Created: 09/20/2019, Bing Li
public class PeerContainerSingleton
{
	private PeerContainer peer;
	private AtomicBoolean isStarted;
	private AtomicBoolean isStopped;

	private PeerContainerSingleton()
	{
		this.isStarted = new AtomicBoolean(false);
		this.isStopped = new AtomicBoolean(false);
	}
	
	private static PeerContainerSingleton instance = new PeerContainerSingleton();
	
	public static PeerContainerSingleton P2P()
	{
		if (instance == null)
		{
			instance = new PeerContainerSingleton();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public synchronized void stop(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, RemoteIPNotExistedException, IOException
	{
		if (!this.isStopped.get())
		{
			this.peer.stop(timeout);
			this.isStopped.set(true);
		}
	}

	public synchronized void start(String peerName, int port, ServerTask task, boolean isRegistryNeeded) throws ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, IOException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		if (!this.isStarted.get())
		{
			this.peer = new PeerContainer(peerName, port, task, isRegistryNeeded);
			this.peer.start();
			this.isStarted.set(true);
		}
	}

	public synchronized void start(String peerName, int port, String registryIP, int registryPort, ServerTask task, boolean isRegistryNeeded) throws ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, IOException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		if (!this.isStarted.get())
		{
			this.peer = new PeerContainer(peerName, port, registryIP, registryPort, task, isRegistryNeeded);
			this.peer.start();
			this.isStarted.set(true);
		}
	}

	/*
	 * It is useful. The incompatible servers, such as HTTP and Wind, need to be retrieved through the registry. 10/19/2021, Bing Li
	 * 
	 * It is not useful. If the local peer is registered, it can replace the incompatible servers. The incompatible ones can be controlled by the local peer. 10/19/2021, Bing Li
	 * 
	 * The method is specially designed for registering for other servers rather than the local peer. The servers run in the same process with the local peer. But they have different port. Usually, the servers are not GreatFree-compatible. Instead, they are introduced by other vendors, such as the HTTP server. So those servers cannot register with GreatFree's registry server. They need the support from the local peer. Then, they are controlled by or integrated with GreatFree. 10/19/2021, Bing Li
	 */
	public RegisterPeerResponse register(String registryIP, int registryPort, String name, int port) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return this.peer.register(registryIP, registryPort, name, port);
	}
	
	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip, port, notification);
	}
	
	public void asyncNotify(String ip, int port, ServerMessage notification)
	{
		this.peer.asyncNotify(ip, port, notification);
	}
	
	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return this.peer.read(ip, port, request);
	}
	
	public PeerContainer getPeer()
	{
		return this.peer;
	}
}
