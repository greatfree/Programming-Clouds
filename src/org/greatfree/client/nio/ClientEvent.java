package org.greatfree.client.nio;

import java.nio.channels.SocketChannel;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 * 
 */
// class ClientEvent<Notification extends ServerMessage>
class ClientEvent
{
//	public SyncRemoteEventer<Notification> client;
	public SocketChannel server;
	public byte[] data;
	
//	public ClientEvent(SyncRemoteEventer<Notification> client, SocketChannel server, byte[] data)
	public ClientEvent(SocketChannel server, byte[] data)
	{
//		this.client = client;
		this.server = server;
		this.data = data;
	}
}
