package org.greatfree.server.container;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.exceptions.RemoteReadException;
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

	public synchronized void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		if (!this.isStopped.get())
		{
			this.peer.stop(timeout);
			this.isStopped.set(true);
		}
	}

	public synchronized void start(String peerName, int port, ServerTask task, boolean isRegistryNeeded) throws IOException, ClassNotFoundException, RemoteReadException
	{
		if (!this.isStarted.get())
		{
			this.peer = new PeerContainer(peerName, port, task, isRegistryNeeded);
			this.peer.start();
			this.isStarted.set(true);
		}
	}
	
	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip, port, notification);
	}
	
	public void asyncNotify(String ip, int port, ServerMessage notification)
	{
		this.peer.asyncNotify(ip, port, notification);
	}
	
	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.peer.read(ip, port, request);
	}
	
	public PeerContainer getPeer()
	{
		return this.peer;
	}
}
