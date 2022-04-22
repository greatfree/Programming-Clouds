package org.greatfree.testing.nio;

import java.nio.channels.SocketChannel;

/**
 * 
 * @author Bing Li
 * 
 * 02/01/2022, Bing Li
 *
 */
class NewServerDataEvent
{
	public NewLargeObjectServer server;
	public SocketChannel socket;
	public byte[] data;

	public NewServerDataEvent(NewLargeObjectServer server, SocketChannel client, byte[] data)
	{
		this.server = server;
		this.socket = client;
		this.data = data;
	}
}
