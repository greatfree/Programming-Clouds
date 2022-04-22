package org.greatfree.server.nio;

import java.nio.channels.SocketChannel;

/**
 * 
 * @author Bing Li
 * 
 * 02/01/2022, Bing Li
 *
 */
// class ServerEvent<Dispatcher extends ServerDispatcher<ServerMessage>>
class ServerEvent
{
//	public CSServer<Dispatcher> server;
	public SocketChannel client;
	public byte[] message;
	
//	public ServerEvent(CSServer<Dispatcher> server, SocketChannel client, byte[] message)
	public ServerEvent(SocketChannel client, byte[] message)
	{
//		this.server = server;
		this.client = client;
		this.message = message;
	}
}
