package org.greatfree.framework.container.p2p.self.peer;

import java.io.IOException;

import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 10/03/2019, Bing Li
class SelfPeer
{
	private PeerContainer peer;
	
	public SelfPeer()
	{
	}
	
	private static SelfPeer instance = new SelfPeer();
	
	public static SelfPeer CONTAINER()
	{
		if (instance == null)
		{
			instance = new SelfPeer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, RemoteIPNotExistedException
	{
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.peer.stop(timeout);
	}

	public void start(String peerName, int port, ServerTask task, boolean isRegistryNeeded) throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.peer = new PeerContainer(peerName, port, task, isRegistryNeeded);
		this.peer.start();
	}

	public void notify(String ip, int port, Notification notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip, port, notification);
	}
	
	public ServerMessage read(String ip, int port, Request request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return (ServerMessage)this.peer.read(ip, port, request);
	}

	public void selfSyncNotify(Notification notification) throws IOException, InterruptedException
	{
		this.peer.selfSyncNotify(notification);
	}

	public void selfAsyncNotify(Notification notification) throws IOException, InterruptedException
	{
		this.peer.selfAsyncNotify(notification);
	}
	
	public ServerMessage selfRead(Request request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return (ServerMessage)this.peer.selfRead(request);
	}
}
