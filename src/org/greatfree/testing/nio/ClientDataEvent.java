package org.greatfree.testing.nio;

import java.nio.channels.SocketChannel;

/**
 * 
 * @author Bing Li
 * 
 * 02/01/2022, Bing Li
 *
 */
class ClientDataEvent
{
	public LargeObjectClient client;
	public SocketChannel server;
	public byte[] data;
	
	public ClientDataEvent(LargeObjectClient client, SocketChannel server, byte[] data)
	{
		this.client = client;
		this.server = server;
		this.data = data;
	}
}
