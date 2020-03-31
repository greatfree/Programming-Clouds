package edu.greatfree.container.p2p.peer;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 01/12/2019, Bing Li
class ChatPeer
{
	private PeerContainer peer;
	
	public ChatPeer()
	{
	}
	
	private static ChatPeer instance = new ChatPeer();
	
	public static ChatPeer CONTAINER()
	{
		if (instance == null)
		{
			instance = new ChatPeer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		this.peer.stop(timeout);
	}

	public void start(String peerName, int port, ServerTask task, boolean isRegistryNeeded) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new PeerContainer(peerName, port, task, isRegistryNeeded);
		this.peer.start();
	}

	public void notify(String ip, int port, Notification notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip, port, notification);
	}
	
	public ServerMessage read(String ip, int port, Request request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (ServerMessage)this.peer.read(ip, port, request);
	}
}
